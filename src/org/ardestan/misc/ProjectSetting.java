package org.ardestan.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import org.ardestan.gui.Message;
import org.ardestan.gui.ProjectDirectoryListener;
import org.ardestan.json.JsonProjectSetting;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

/**
 * @author hnishino
 *
 */
/**
 * @author hnishino
 *
 */
/**
 * @author hnishino
 *
 */
/**
 * @author hiroki
 *
 */
/**
 * @author hiroki
 *
 */
public class ProjectSetting 
{	
	private File	projectFile					;
	private File	projectDirectory			;
	private File	arduinoProjectDirectory		;
	
			
	static private ProjectSetting singleton;
	
	private LinkedList<String> recentProjectFilenames;
	
	private int windowWidth;
	private int windowHeight;
	
	protected LinkedList<ProjectDirectoryListener> projectDirectoryListeners = null;
	
	protected JsonProjectSetting projectSetting = null; 
	
	private boolean firstExec = true;
	private String  base64EncodedAdditionalURLs = "";
	
	/**
	 * 
	 */
	public static synchronized ProjectSetting getSingleton()
	{
		if (singleton == null) {
			singleton = new ProjectSetting();
		}
		return singleton;
	}
	
	
	/**
	 * 
	 */
	public void addProjectDirecotryListeners(ProjectDirectoryListener listener)
	{
		projectDirectoryListeners.add(listener);
	}
	

	
	/**
	 * 
	 */
	private void invokeProjectDirectoryUpdateEvent()
	{
		for (ProjectDirectoryListener listener: projectDirectoryListeners) {
			listener.projectDirectoryChanged();
		}
	}
	/**
	 * 
	 */
	private ProjectSetting() 
	{        	
		projectDirectoryListeners = new LinkedList<ProjectDirectoryListener>();
		recentProjectFilenames = new LinkedList<String>();
		
		projectSetting = new JsonProjectSetting();

		firstExec = true;
		return;
	}
	
	
	
	/**
	 * @param exec
	 * @return
	 */
	public void setFirstExec(boolean exec)
	{
		this.firstExec = exec;
	}
	
	/**
	 * 
	 */
	public boolean isFirstExec()
	{
		return this.firstExec;
	}
	
	/**
	 * @param portName
	 */
	public void setPortName(String portName)
	{
		projectSetting.setPortName(portName);
		return;
	}
	
	/**
	 * @return
	 */
	public String getPortName()
	{

		return projectSetting.getPortName();
	}
	
	/**
	 * @param boardName
	 */
	public void setBoardName(String boardName)
	{
		projectSetting.setBoardName(boardName);
		return;
	}

	/**
	 * @return
	 */
	public String getBoardName()
	{
		return projectSetting.getBoardName();
	}
	
	/**
	 * @param fqbn
	 */
	public void setFqbn(String fqbn)
	{
		
		projectSetting.setFqbn(fqbn);
		return;
	}
	
	/**
	 * @return
	 */
	public String getFqbn()
	{

		return projectSetting.getFqbn();
	}
	
	/**
	 * @return
	 */
	public boolean getUsePROGMEMforConnections()
	{
		return projectSetting.getUsePROGMEMforConnections();
	}
	
	/**
	 * @param usePROGMEMforConnections
	 */
	public void setUsePROGMEMforConnections(boolean usePROGMEMforConnections)
	{
		projectSetting.setUsePROGMEMforConnections(usePROGMEMforConnections);
	}
	
	
	/**
	 * @return
	 */
	public String getBase64EncodedAdditionalURLs()
	{
		return this.base64EncodedAdditionalURLs;
	}
	
	/**
	 * @param base64EncodedAdditionalURLs
	 */
	public void setBase64EncodedAdditionalURLs(String base64EncodedAdditionalURLs)
	{
		this.base64EncodedAdditionalURLs = base64EncodedAdditionalURLs;
	}
	
	/**
	 * @return
	 */
	public Vector<String> getBase64DecodedAdditionalURLs()
	{
		Vector<String> decodedURLs = new Vector<String>();
		
		String[] split = this.base64EncodedAdditionalURLs.split(",");
		for (String s: split) {
			s = s.trim();
			if (s.length() == 0) {
				continue;
			}
			String decoded = new String(Base64.getUrlDecoder().decode(s));
			decodedURLs.add(decoded);
		}
		
		return decodedURLs;		
	}
	
	
	/**
	 * 
	 */
	public boolean getUsePROGMEMforOutlets()
	{
		return projectSetting.getUsePROGMEMforOutlets();
	}
	
	/**
	 * @param usePROGMEMforOutlets
	 */
	public void setUsePROGMEMforOutlets(boolean usePROGMEMforOutlets)
	{
		projectSetting.setUsePROGMEMforOutlets(usePROGMEMforOutlets);
	}
	/**
	 * 
	 */
	public int getWindowWidth()
	{
		return this.windowWidth;
	}
	
	/**
	 * @return
	 */
	public int getWindowHeight()
	{
		return this.windowHeight;
	}
	
	/**
	 * @throws IOException
	 */
	public void savePreferences() throws IOException
	{
		//
		File userHome = new File(System.getProperty("user.home"));
		File propFile = new File(userHome, ARFileConst.IDE_PROPERTIES_FILE_NAME);
				
		Properties prop = new Properties();

		Iterator<String> it = recentProjectFilenames.iterator();
		int no = 1;
		while(it.hasNext()) {			
			String propName = ARFileConst.IDE_PROPERTIES_RECENT_PROJECT_FILENAME_PREFIX + no;			
			prop.setProperty(propName, it.next());
			no++;
		}
		
		if (this.firstExec == false){
			prop.setProperty(ARFileConst.FIRST_EXEC, "false");
		}
		
		if (this.base64EncodedAdditionalURLs != null) {
			prop.setProperty(ARFileConst.BASE64_ENCODED_ADDITIONAL_URLS, base64EncodedAdditionalURLs);
		}
				
		prop.store(new FileOutputStream(propFile), "Ardestan IDE Properties");	
		
		return;
	}
	
	/**
	 * @throws IOException
	 */
	public void loadPreferences() throws IOException
	{
		//recent project directories;
		recentProjectFilenames = new LinkedList<>();
		
		File userHome = new File(System.getProperty("user.home"));
		File propFile = new File(userHome, ARFileConst.IDE_PROPERTIES_FILE_NAME);
		
		if (!propFile.exists()) {
			this.firstExec = true;
			return;
		}

		try {
			InputStream istream = new FileInputStream(propFile);
		
			Properties prop = new Properties();
			prop.load(istream);
			
			for (int i = 1; i <= 5; i++) {
				String propName = ARFileConst.IDE_PROPERTIES_RECENT_PROJECT_FILENAME_PREFIX + i;
				String v = prop.getProperty(propName);
				if (v == null) {
					break;
				}
				recentProjectFilenames.addLast(v);
			}
			
			String v = prop.getProperty(ARFileConst.FIRST_EXEC);
			if (v != null) {
				this.firstExec = false;
			}
			
			v = prop.getProperty(ARFileConst.BASE64_ENCODED_ADDITIONAL_URLS);
			if (v != null) {
				this.base64EncodedAdditionalURLs = v;
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * @return
	 */
	public String getMostRecentProjectFile()
	{
		if (recentProjectFilenames.isEmpty()) {
			return null;
		}
		return recentProjectFilenames.getFirst();
	}
		
	/**
	
	/**
	 * @return the projectPath
	 */
	public File getProjectDirectory() {
		return projectDirectory;
	}


	/**
	 * @param newProjectDirector
	 */
	private void addRecentProjectFiles(String projectFilename)
	{
		//check if the same directory already exists in the recent project list.
		Iterator<String> i = recentProjectFilenames.iterator();
		while(i.hasNext()) {
			String next = i.next();
			if (next.equals(projectFilename)) {
				i.remove();
				break;
			}
		}
		
		recentProjectFilenames.addFirst(projectFilename);
		
		if (recentProjectFilenames.size() > 5) {
			recentProjectFilenames.removeLast();
		}
		
		try {
			this.savePreferences();
		}
		catch(IOException e) {
			Message.println(e);
		}
		
		return;
	}

	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	protected String buildString(String filename) throws IOException
	{
		//read the text from the file.
		File file = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		StringBuilder builder = new StringBuilder();
		while(true){
			String s = br.readLine();
			if (s == null){
				break;
			}
			builder.append(s);
		}
		
		br.close();
		
		return builder.toString();
	}
	/**
	 * @param projectFile
	 * @throws IOException
	 */
	public void loadProjectFile(File projectFile) throws IOException
	{
		try {
			if (this.projectFile != null && this.projectFile.exists()) {
				this.saveCurrentProject();
			}
			this.projectFile = projectFile;
			this.addRecentProjectFiles(projectFile.getAbsolutePath());
			
						
			String jsonString = this.buildString(projectFile.getAbsolutePath());
			
			Gson gson = new Gson();
			this.projectSetting = gson.fromJson(jsonString, JsonProjectSetting.class);
			if (this.projectSetting == null) {
				this.projectSetting = new JsonProjectSetting();
			}
			this.setProjectDirectory(projectFile.getParentFile());
			
			this.invokeProjectDirectoryUpdateEvent();
		}
		catch (IOException e) {
			Message.print(e);
		}
		
		return;
	}
	
	/**
	 * @throws IOException
	 */
	public void saveCurrentProject() throws IOException
	{
		File project = this.getProjectFile();
		
		if (project == null) {
			return;
		}
		
		FileWriter fsw = new FileWriter(project);
		JsonWriter jsw = new JsonWriter(fsw);
		jsw.setIndent("\t");
		
		Gson gson = new Gson();
		gson.toJson(projectSetting, projectSetting.getClass(), jsw);
		jsw.flush();
		jsw.close();	
		
		return;
	}
	
	/**
	 * @return
	 */
	public File getProjectFile()
	{
		return this.projectFile;
	}
	
	
	/**
	 * @param projectDirectory the projectPath to set
	 */
	private void setProjectDirectory(File projectDirectory) throws IOException
	{
		this.projectDirectory = projectDirectory;
		String projectName = this.projectDirectory.getName();
		
		this.arduinoProjectDirectory = new File(this.projectDirectory.getAbsolutePath() + File.separator + projectName);
		
		return;
	}


	/**
	 * @return the projectDirectory
	 */
	public File getArduinoProjectDirectory() {
		return arduinoProjectDirectory;
	}
}
