package org.ardestan.arclass;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import org.ardestan.generator.ARExternalObjectUpdateListener;
import org.ardestan.gui.Message;
import org.ardestan.gui.ProjectDirectoryListener;
import org.ardestan.json.JsonClassInfoLoader;
import org.ardestan.misc.ARFileConst;
import org.ardestan.misc.ProjectSetting;


/**
 * @author hiroki
 *
 */
public class ARClassDatabase implements Runnable {

	
	protected static ARClassDatabase singleton = null;
	
	Map<String, ARClassInfo> nameToClassInfo = null;
	
	LinkedList<ARExternalObjectUpdateListener> 	externalObjectUpdateListeners 	= null;
	LinkedList<ProjectDirectoryListener> 		projectDirectoryListeners 		= null;
	
	Thread watchDogThread = null;
	
	boolean watchDogThreadRunning = false;
	
	
	/**
	 * @param jsonfile
	 * @throws IOException
	 */
	private ARClassDatabase()
	{
		externalObjectUpdateListeners = new LinkedList<ARExternalObjectUpdateListener>();
		projectDirectoryListeners = new LinkedList<ProjectDirectoryListener>();
				
	}
	
	/**
	 * @param listener
	 */
	public void addARExternalObjectUpdateListener(ARExternalObjectUpdateListener listener)
	{
		synchronized(externalObjectUpdateListeners){
			externalObjectUpdateListeners.addLast(listener);
		}
	}
	
	/**
	 * @param listener
	 */
	public void addProjectDirectoryListeners(ProjectDirectoryListener listener)
	{
		synchronized(projectDirectoryListeners) {
			projectDirectoryListeners.add(listener);
		}
	}
	
	public void removeProjectDirectoryListeners(ProjectDirectoryListener listener)
	{
		synchronized(projectDirectoryListeners) {
			projectDirectoryListeners.remove(listener);
		}
	}
	
	/**
	 * @param listener
	 */
	public void removeARExternalObjectUpdateListener(ARExternalObjectUpdateListener listener)
	{
		synchronized(externalObjectUpdateListeners){
			externalObjectUpdateListeners.remove(listener);
		}
	}
	
	/**
	 * 
	 */
	public void invokeExternalObjectUpdateEvent() {
		synchronized(externalObjectUpdateListeners){
			for (ARExternalObjectUpdateListener l : externalObjectUpdateListeners) {
				l.subpatchUpdated();
			}
		}
	}
	
	/**
	 * 
	 */
	public void invokeDirectoryUpdateEvent() {
		synchronized(projectDirectoryListeners){
			for (ProjectDirectoryListener l : projectDirectoryListeners) {
				l.projectDirectoryChanged();
			}
		}
	}
	
	/**
	 * 
	 */
	public void removeAllSubpatches()
	{
		synchronized(this)
		{
			
			Iterator<String> i = nameToClassInfo.keySet().iterator();
			while(i.hasNext()) {
				String name = i.next();
				ARClassInfo info = nameToClassInfo.get(name);
				if (info.isSubpatch()) {
					i.remove();
				}
			}
		}
		
	}
	
	/**
	 * 
	 */
	public void stopWatchThread()
	{
		if (watchDogThread == null) {
			return;
		}
		
		watchDogThread.interrupt();
		try {
			watchDogThread.join();
		}
		catch(InterruptedException e) {
			
		}
		
		watchDogThread = null;
		return;
		
	}
	/**
	 * 
	 */
	public void startWatchThread()
	{
		if (watchDogThread != null) {
			return;
		}
		watchDogThread = new Thread(this);
		watchDogThread.start();
		
	}
	
	/**
	 * 
	 */
	public void run()
	{
		watchDogThreadRunning = true;
		while(true) {
			this.checkSubpatchUpdate();
			this.checkUserDefinedObjectUpdate();
			try {
				Thread.sleep(1500);
			}
			catch(InterruptedException e) {
				break;
			}
			catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		watchDogThreadRunning = false;
		watchDogThread.notifyAll();
	}
	
	/**
	 * @return
	 */
	public boolean isWatchDogThreadRunning()
	{
		return watchDogThreadRunning;
	}
	
	
	/**
	 * 
	 */
	private void checkUserDefinedObjectUpdate()
	{
		File path = ProjectSetting.getSingleton().getProjectDirectory();
		if (path == null || !path.exists()) {
			return;
		}
		
		boolean updated = false;
		
		Vector<ARClassInfoUserDefined> userDefinedObjects = new Vector<ARClassInfoUserDefined>();
		
		File[] files = path.listFiles();
		for (File f: files) {
			File defFile = getUserDefinedObjectDefFile(f);
			if (defFile != null) {
				ARClassInfoUserDefined klazz = new ARClassInfoUserDefined(defFile);
				userDefinedObjects.add(klazz);
			}
		}
		
		//load the new user defined objects
		boolean foundNew = false;
		for (ARClassInfoUserDefined udo: userDefinedObjects) {
			try {
				udo.loadUserObjectDefinitionFile();
				String udObjName = udo.getARClassName();
				if (udObjName == null) {
					continue;
				}
				else if (this.nameToClassInfo.containsKey(udObjName)) {
					continue;
				}
				
				//TODO: check if the user-defined object directory is valid.
				this.nameToClassInfo.put(udObjName, udo);
				Message.println("loaded the user-defined object:" + udo.getARClassName());
				foundNew = true;
			}
			catch(IOException e) {
				Message.println("failed to load the user-defined object:" + udo.getARClassName());
				Message.println(e);
			}
		}
		
		//remove the deleted files and update the 
		Vector<String> objectsToRemove = new Vector<String>();
		Vector<ARClassInfoUserDefined> renamedUserDefinedObjects = new Vector<ARClassInfoUserDefined>();
		
		synchronized(this)
		{
			Iterator<String> it = nameToClassInfo.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				
				ARClassInfo info = nameToClassInfo.get(name);
				if (!info.isUserDefinedObject()) {
					continue;
				}
				
				ARClassInfoUserDefined udo = (ARClassInfoUserDefined)info;
				
				if (!udo.fileExists()) {
					updated = true;
					Message.println("The user defined object:" + name + " was unloaded. The file is gone.");
					it.remove();
					continue;
				}
				
				boolean fileUpdate =  udo.isFileUpdated();
				if (fileUpdate) {
					updated = true;
					try {	
						String oldKlazzName = udo.getARClassName();
						udo.loadUserObjectDefinitionFile();
						String newKlazzName = udo.getARClassName();
						if (!oldKlazzName.equals(newKlazzName)) {
							Message.println("the name of the user object:" + oldKlazzName + " has changed to:" + newKlazzName);
							objectsToRemove.add(oldKlazzName);
							renamedUserDefinedObjects.add(udo);
						}
					}
					catch(IOException e) {
						objectsToRemove.add(name);
						Message.println("failed to load the user object definition file:" + udo.getARClassName());
						Message.println(e);
					}
				}
			}
		}
		for (String name: objectsToRemove) {
			nameToClassInfo.remove(name);
		}
		
		for (ARClassInfoUserDefined udo: renamedUserDefinedObjects) {
			this.nameToClassInfo.put(udo.getARClassName(), udo);
		}
		
		if (updated || foundNew) {
			invokeDirectoryUpdateEvent();
			invokeExternalObjectUpdateEvent();
		}
		return;
	}
	
	/**
	 * @param filename
	 * @return
	 */
	public boolean isUserDefinedObjectDefFile(String filename)
	{
		return filename.endsWith(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT);
	}
	
	/**
	 * @param f
	 * @return
	 */
	private File getUserDefinedObjectDefFile(File f)
	{
		if (f.isDirectory() == false) {
			return null;
		}
		
		File[] files = f.listFiles();
		for (File file: files) {
			if (isUserDefinedObjectDefFile(file.getName())) {
				return file;
			}
		}
		return null;
	}
	/**
	 * 
	 */
	private void checkSubpatchUpdate()
	{
		File path = ProjectSetting.getSingleton().getProjectDirectory();
		if (path == null || !path.exists()) {
			return;
		}
		
		boolean updated = false;

	
		String extension = ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT;
		File[] files = path.listFiles();
		
		Vector<ARClassInfoSubpatch> loadedSubPatches = new Vector<ARClassInfoSubpatch>();
		
		//find new subpatches.
		for (File f: files) {
			//we don't check the directory here.
			if (f.isDirectory()) {
				continue;
			}
			String filename = f.getAbsolutePath();
			if (!filename.endsWith(extension)) {
				continue;
			}
	
			
			ARClassInfoSubpatch subpatch = new ARClassInfoSubpatch(new File(filename));
			
			String subpatchName = subpatch.getARClassName();
			if (subpatchName == null) {
				continue;
			}
			else if (this.nameToClassInfo.containsKey(subpatchName)) {
				continue;
			}
			
			this.nameToClassInfo.put(subpatchName, subpatch);
			loadedSubPatches.add(subpatch);
			updated = true;

		}
		
		//load the new subpatches.
		for (ARClassInfoSubpatch subpatch: loadedSubPatches) {
			try {
				subpatch.updateSubpatchInfo();	
				Message.println("loaded the subpatch file:" + subpatch.getARClassName());
			}
			catch(IOException e) {
				Message.println("failed to load the subpatch file:" + subpatch.getARClassName());
				Message.println(e);
			}
		}		
		
		//remove the deleted subpatches and reload the updated subpaches.
		Vector<String> updateFailed = new Vector<String>();
		synchronized(this)
		{
			Iterator<String> it = nameToClassInfo.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				
				ARClassInfo info = nameToClassInfo.get(name);
				if (!info.isSubpatch()) {
					continue;
				}
				
				ARClassInfoSubpatch sp = (ARClassInfoSubpatch)info;
				
				if (!sp.fileExists()) {
					updated = true;
					Message.println("The subpatch:" + name + " was unloaded. The file is gone.");
					it.remove();
					continue;
				}
				
				boolean fileUpdate =  sp.isFileUpdated();
				if (fileUpdate) {
					updated = true;
					try {	
						boolean ret = sp.updateSubpatchInfo();
						if (ret == true) {
							sp.updateLastModifiedTimestamp();
						}
					}
					catch(IOException e) {
						updateFailed.add(name);
						e.printStackTrace();
					}
				}
			}
		}
		
		
		for (String name: updateFailed) {
			nameToClassInfo.remove(name);
		}
		
		if (updated) {
			invokeDirectoryUpdateEvent();
			invokeExternalObjectUpdateEvent();
		}
		return;
	}
	
	
	/**
	 * @param resourceName
	 * @throws IOException
	 */
	public static synchronized void initClassDatabaseFromResource() throws IOException
	{
		initClassDatabaseFromResource(ARFileConst.DEFAULT_CLASSINFO_FILE_RESOURCE_NAME);
		return;
		
//		singleton = new ARClassDatabase();	
//		
//		Map<String, ARClassInfo> info = JsonClassInfoLoader.loadNativeClassInfoFromResource(ARFileConst.DEFAULT_CLASSINFO_FILE_RESOURCE_NAME);
//		singleton.setNameToClassInfoMap(info);
//
//		//add the comment box class
//		singleton.addARClassInfo(new ARClassInfoCommentBox());
	}
	
	/**
	 * @param resourceName
	 * @throws IOException
	 */
	public static synchronized void initClassDatabaseFromResource(String resourceName) throws IOException
	{
		singleton = new ARClassDatabase();	
		
		Map<String, ARClassInfo> info = JsonClassInfoLoader.loadNativeClassInfoFromResource(resourceName);
		singleton.setNameToClassInfoMap(info);
		
	}
	
	/**
	 * @return
	 */
	public static synchronized ARClassDatabase getSingleton()
	{		
		return singleton;
	}
	
	/**
	 * @param className
	 * @return
	 */
	/**
	 * @param className
	 * @return
	 */
	public  ARClassInfo getARClassInfo(String className)
	{
		synchronized(this){
			if (nameToClassInfo.containsKey(className)) {
				return nameToClassInfo.get(className);
			}
		}
		return null;
	}
	
	/**
	 * 
	 */
	private void setNameToClassInfoMap(Map<String, ARClassInfo> newNameToClassInfo)
	{
		synchronized(this){
			this.nameToClassInfo = newNameToClassInfo;
		}
		return;
	}
	
	
	/**
	 * 
	 */
	public void loadSubpatchesInTheProjectPath()
	{
		File path = ProjectSetting.getSingleton().getProjectDirectory();
		if (path == null || !path.exists()) {
			return;
		}
	
		Message.println("loading subpatches in the project folder: " + path.getAbsolutePath() +"...");

		boolean loaded = false;
		
		String extension = ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT;
		File[] files = path.listFiles();
		
		Vector<ARClassInfoSubpatch> loadedSubPatches = new Vector<ARClassInfoSubpatch>();
		
		for (File f: files) {
			String filename = f.getAbsolutePath();
			if (!filename.endsWith(extension)) {
				continue;
			}
			
			ARClassInfoSubpatch subpatch = new ARClassInfoSubpatch(new File(filename));
			
			String subpatchName = subpatch.getARClassName();
			if (subpatchName == null) {
				continue;
			}
			else if (subpatchName.equals("")) {
				Message.println("neglected the ard file:" + filename + "(invalid filename)");
				continue;
			}
			else if (this.nameToClassInfo.containsKey(subpatchName) && 
					 this.nameToClassInfo.get(subpatchName).isSubpatch() == false) {
				Message.println("neglected the ard file:" + filename + "(can't override a native class)");
				continue;
			}
			this.nameToClassInfo.put(subpatchName, subpatch);
			loadedSubPatches.add(subpatch);
			loaded = true;

		}
		
		for (ARClassInfoSubpatch subpatch: loadedSubPatches) {
			try {
				subpatch.updateSubpatchInfo();	
				Message.println("loaded the subpatch file:" + subpatch.getARClassName());
			}
			catch(IOException e) {
				Message.println("failed to load the subpatch file:" + subpatch.getARClassName());
				Message.println("(IOException)");
			}
		}		
		if (loaded) {
			invokeExternalObjectUpdateEvent();
			invokeDirectoryUpdateEvent();
		}
	}
	
	/**
	 * 
	 */
	public void removeNonExistingSubPatches()
	{
		synchronized(this)
		{
			Vector<String> nonExistingSubpatches = new Vector<String>();
			
			//check if each subpatch still exists.
			for(String n: nameToClassInfo.keySet()) {
				ARClassInfo i = nameToClassInfo.get(n);
				if (!i.isSubpatch()) {
					continue;
				}
				
				ARClassInfoSubpatch s = (ARClassInfoSubpatch)i;
				if (s.fileExists()) {
					continue;
				}
				nonExistingSubpatches.add(n);
			}
			
			//remove the information of non existing subpatches.
			for (String n: nonExistingSubpatches) {
				nameToClassInfo.remove(n);
			}
		}
		
		return;
	}
	
	/**
	 * @param info
	 */
	public void addARClassInfo(ARClassInfo klazzInfo)
	{
		this.nameToClassInfo.put(klazzInfo.getARClassName(), klazzInfo);
	}
	
	/**
	 * @return
	 */
	public Map<String, ARClassInfo> getCopyOfNameToClassInfoMap()
	{
		synchronized(this){
			return new HashMap<String, ARClassInfo>(this.nameToClassInfo);
		}
	}
}
