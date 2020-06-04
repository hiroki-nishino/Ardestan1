package org.ardestan.arduinocli;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.ardestan.gui.Message;
import org.ardestan.gui.dialog.board.CoreInstallationListener;
import org.ardestan.gui.dialog.board.CoreSearchResultListener;
import org.ardestan.gui.dialog.library.LibraryInstallationListener;
import org.ardestan.gui.dialog.library.LibraryManagerSearchItem;
import org.ardestan.gui.dialog.library.LibrarySearchResultListener;
import org.ardestan.gui.info.serialmonitor.SerialMonitor;
import org.ardestan.gui.info.serialmonitor.SerialMonitorException;
import org.ardestan.json.JsonCoreSearchResultItem;
import org.ardestan.misc.ARFileConst;

/**
 * @author hiroki
 *
 */
public class ArduinoCLIBackground extends Thread
{

	private static final int UPDATE_CORE_INDEX 	= 0;
	private static final int BUILD				= 1;
	private static final int BUILD_UPLOAD		= 2;
	private static final int SEARCH_CORE		= 3;
	private static final int INSTALL_BOARD		= 4;
	private static final int UNINSTALL_BOARD 	= 5;
	private static final int UPGRADE_BOARD		= 6;
	private static final int FIRST_EXEC			= 7;
	private static final int SEARCH_LIBRARY		= 8;
	private static final int INSTALL_LIBRARY	= 9;
	private static final int UNINSTAL_LIBRARY	= 10;
	private static final int UPGRADE_LIBRARIES	= 11;
	private static final int UPDATE_LIBRARY_INDEX = 12;
	
	private static ArduinoCLIBackground instance;

	
	/**
	 * @return 
	 * 
	 */
	public static synchronized ArduinoCLIBackground getSingleton()
	{
		if (instance == null) {
			instance = new ArduinoCLIBackground();
		}
		return instance;
	}
	
	/**
	 * @author hiroki
	 *
	 */
	private class Cmd {
		public int 		command;
		public String	portName;
		public File 	projectPath;
		public String	boardName;
		public String	fqbn;
		public String	keyword;

		public CoreSearchResultListener 	coreSearchResultListener;
		public CoreInstallationListener 	coreInstallationListener;
		public LibrarySearchResultListener	librarySearchResultListener;
		public LibraryInstallationListener	libraryInstallationListener;
	};
	
	
	private LinkedList<Cmd> queue;

	/**
	 * 
	 */
	private ArduinoCLIBackground()
	{
		queue = new LinkedList<Cmd>();
		this.start();
	}
	
	
	/**
	 * 
	 */
	public void run()
	{
		while(true) {
			try {
				synchronized(queue) {
					queue.wait(500);
				}
				while(!queue.isEmpty()) {
					this.processNextCommand();
				}
			}
			catch(InterruptedException e) {
				Message.println(e);
			}
		}
	}
	

	/**
	 * 
	 */
	protected void processNextCommand()
	{
		if (queue.isEmpty()) {
			return;
		}
		
		Cmd c = queue.getFirst();
		switch(c.command) {
		case UPDATE_CORE_INDEX:
			this._updateCoreIndex(c.projectPath);
			break;
			
		case BUILD:
			this._build(c.projectPath, c.boardName, c.fqbn);
			break;
			
		case BUILD_UPLOAD:
			this._buildAndUpload(c.projectPath, c.portName, c.boardName, c.fqbn);
			break;
			
		case SEARCH_CORE:
			this._searchCore(c.projectPath, c.coreSearchResultListener, c.keyword);
			break;
			
		case INSTALL_BOARD:
			this._installBoard(c.projectPath, c.fqbn, c.coreInstallationListener);
			break;
			
		case UNINSTALL_BOARD:
			this._uninstallBoard(c.projectPath, c.fqbn, c.coreInstallationListener);
			break;
			
		case UPGRADE_BOARD:
			this._upgradeBoard(c.projectPath, c.fqbn, c.coreInstallationListener);
			break;
			
		case FIRST_EXEC:
			this._firstExec(c.projectPath);
			break;
			
		case SEARCH_LIBRARY:
			this._searchLibrary(c.projectPath, c.librarySearchResultListener, c.keyword);
			break;
			
		case INSTALL_LIBRARY:
			this._installLibrary(c.projectPath, c.libraryInstallationListener, c.keyword);
			break;
			
		case UNINSTAL_LIBRARY:
			this._uninstallLibrary(c.projectPath, c.keyword, c.libraryInstallationListener);
			break;
			
		case UPGRADE_LIBRARIES:
			this._upgradeLibraries(c.projectPath);
			break;
		
		case UPDATE_LIBRARY_INDEX:
			this._updateLibraryIndex(c.projectPath);
			break;
		}
		
		queue.removeFirst();
		
		return;
	}

	/**
	 * @param projectPath
	 * @param listener
	 * @param id
	 * @return
	 */
	public boolean installLibrary(File projectPath, LibraryInstallationListener listener, String keyword)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = INSTALL_LIBRARY;
			c.keyword = keyword;
			c.libraryInstallationListener = listener;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	/**
	 * @param projectPath
	 * @param listener
	 * @param keyword
	 */
	public void _installLibrary(File projectPath, LibraryInstallationListener listener, String keyword)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.print("installing the library with the keyword: " + keyword + "...");
			int ret = cli.installLibrary(projectPath, keyword);
			if (ret != 0) {
				Message.println("error!");
				listener.failedInstallation();
				return;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
			
			listener.finishedInstallation();
		}
		catch(IOException e) {
			Message.println(e);
			listener.failedInstallation();
			return;
		}
		return;
	}
	/**
	 * @param projectPath
	 * @param listener
	 * @param keyword
	 * @return
	 */
	public boolean searchLibrary(File projectPath, LibrarySearchResultListener listener, String keyword)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = SEARCH_LIBRARY;
			c.keyword = keyword;
			c.librarySearchResultListener = listener;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	/**
	 * @param projectPath
	 * @param listener
	 * @param keyword
	 */
	public void _searchLibrary(File projectPath, LibrarySearchResultListener listener, String keyword)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.print("searching for libraries with the keyword: " + keyword + "...");
			LibraryManagerSearchItem[] searchResult = cli.searchLibrary(projectPath, keyword);
			if (searchResult == null) {
				listener.notifyResult(null);
				return;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
			
			listener.notifyResult(searchResult);
		}
		catch(IOException e) {
			Message.println(e);
			return;
		}
		return;
	}
	/**
	 * @param projectPath
	 * @param listener
	 * @param keyword
	 * @return
	 */
	public boolean searchCore(File projectPath, CoreSearchResultListener listener, String keyword)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = SEARCH_CORE;
			c.keyword = keyword;
			c.coreSearchResultListener = listener;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;

	}
	
	
	/**
	 * @param projectPath
	 * @param listener
	 * @param keyword
	 * @return
	 */
	public void _searchCore(File projectPath, CoreSearchResultListener listener, String keyword)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.print("searching for cores with the keyword: " + keyword + "...");
			JsonCoreSearchResultItem[] searchResult = cli.searchCore(projectPath, keyword);
			if (searchResult == null) {
				listener.notifyResult(null);
				Message.println("error!");
				return;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
			
			listener.notifyResult(searchResult);
		}
		catch(IOException e) {
			Message.println(e);
			return;
		}
		
		return;
	}
	

	/**
	 * @param projectPath
	 * @param fqbn
	 * @param listener
	 * @return
	 */
	public boolean installBoard(File projectPath, String fqbn, CoreInstallationListener listener)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = INSTALL_BOARD;
			c.fqbn = fqbn;
			c.coreInstallationListener = listener;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	
	/**
	 * @param projectPath
	 * @param fqbn
	 * @param listener
	 * @return
	 */
	public boolean upgradeBoard(File projectPath, String fqbn, CoreInstallationListener listener)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = UPGRADE_BOARD;
			c.fqbn = fqbn;
			c.coreInstallationListener = listener;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	/**
	 * @param projectPath
	 * @param listener
	 * @return
	 */
	public boolean upgradeLibrary(File projectPath)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = UPGRADE_LIBRARIES;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}

	
	/**
	 * @param projectPath
	 * @param fqbn
	 * @param listener
	 * @return
	 */
	public boolean uninstallBoard(File projectPath, String fqbn, CoreInstallationListener listener)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = UNINSTALL_BOARD;
			c.fqbn = fqbn;
			c.coreInstallationListener = listener;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	/**
	 * @param projectPath
	 * @param fqbn
	 * @param listener
	 * @return
	 */
	public boolean uninstallLibrary(File projectPath, String name, LibraryInstallationListener listener)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = UNINSTAL_LIBRARY;
			c.keyword = name;
			c.libraryInstallationListener = listener;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	/**
	 * @param projectPath
	 */
	public boolean updateCoreIndex(File projectPath)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = UPDATE_CORE_INDEX;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	/**
	 * @param projectPath
	 */
	public boolean updateLibraryIndex(File projectPath)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = UPDATE_LIBRARY_INDEX;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	/**
	 * @param projectPath
	 */
	public boolean firstExec(File projectPath)
	{
		synchronized	(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath = projectPath;
			c.command = FIRST_EXEC;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	/**
	 * @param projectPath
	 * @return
	 */
	public int _firstExec(File projectPath)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.println("This is the first execution of Ardestan. Creating an index for Arduino-CLI...");
			
			//copying the CLI command.
			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			//updating index
			Message.println("updating index...");
			int ret = cli.updateCoreIndex(projectPath);
			if (ret != 0) {
				Message.println("error!");
				return ret;
			}
			Message.println("done.");
			
			//installing the default avr platforms.
			Message.println("installing the arduino:avr platforms...");
			ret = cli.installCore(projectPath, ARFileConst.ID_ARDUINO_AVR_PLATFORMS);
			if (ret != 0) {
				Message.println("error!");
				return ret;
			}
			
			Message.println("done.");

			//deleting the CLI command.
			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
		}
		catch(IOException e) {
			Message.println(e);
			return -1;
		}
		
		return 0;
	}

	/**
	 * @param projectPath
	 * @param fqbn
	 * @param listener
	 * @return
	 */
	protected int _installBoard(File projectPath, String fqbn, CoreInstallationListener listener)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.println("installing a new board...");
			int ret = cli.installCore(projectPath, fqbn);
			if (ret != 0) {
				Message.println("error!");
				listener.failedInstallation();

				return ret;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
			listener.finishedInstallation();
		}
		catch(IOException e) {
			Message.println(e);
			listener.failedInstallation();
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * @param projectPath
	 * @param fqbn
	 * @param listener
	 * @return
	 */
	protected int _uninstallBoard(File projectPath, String fqbn, CoreInstallationListener listener)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.println("uninstalling the board (FQBN:" + fqbn + ")...");
			int ret = cli.uninstallCore(projectPath, fqbn);
			if (ret != 0) {
				Message.println("error!");
				listener.failedUninstallation();
				return ret;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
			listener.finishedUninstallation();
		}
		catch(IOException e) {
			Message.println(e);
			listener.failedUninstallation();
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * @param projectPath
	 * @param fqbn
	 * @param listener
	 * @return
	 */
	protected int _uninstallLibrary(File projectPath, String name, LibraryInstallationListener listener)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.println("uninstalling the library (name:" + name + ")...");
			int ret = cli.uninstallLibrary(projectPath, name);
			if (ret != 0) {
				Message.println("error!");
				listener.failedUninstallation();
				return ret;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
			listener.finishedUninstallation();
		}
		catch(IOException e) {
			Message.println(e);
			listener.failedUninstallation();
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * @param projectPath
	 * @param fqbn
	 * @param listener
	 * @return
	 */
	protected int _upgradeBoard(File projectPath, String fqbn, CoreInstallationListener listener)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.println("upgrading a board (FQBN:" + fqbn + ") ...");
			int ret = cli.upgradeCore(projectPath, fqbn);
			if (ret != 0) {
				Message.println("error!");
				listener.failedUpgrading();
				return ret;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
			listener.finishedUpgrading();
		}
		catch(IOException e) {
			Message.println(e);
			listener.failedUpgrading();
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * @param projectPath
	 * @param listener
	 * @return
	 */
	protected int _upgradeLibraries(File projectPath)
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.println("upgrading installed libraries ...");
			int ret = cli.upgradeLibrary(projectPath);
			if (ret != 0) {
				Message.println("error!");
				return ret;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
		}
		catch(IOException e) {
			Message.println(e);
			return -1;
		}
		
		return 0;
	}
	/**
	 * @param projectPath
	 */
	protected int _updateCoreIndex(File projectPath) 
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.println("updating the core index...");
			int ret = cli.updateCoreIndex(projectPath);
			if (ret != 0) {
				Message.println("error!");
				return ret;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
		}
		catch(IOException e) {
			Message.println(e);
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * @param projectPath
	 * @return
	 */
	protected int _updateLibraryIndex(File projectPath) 
	{
		try {
			ArduinoCLI cli = ArduinoCLI.getSingleton();

			Message.print("copying the arduino command line interface...");
			cli.copyCommandTo(projectPath);
			Message.println("done.");

			Message.println("updating the library index...");
			int ret = cli.updateLibraryIndex(projectPath);
			if (ret != 0) {
				Message.println("error!");
				return ret;
			}
			Message.println("done.");

			Message.print("deleting the arduino command line interface...");
			cli.deleteCommand(projectPath);
			Message.println("done.");
		}
		catch(IOException e) {
			Message.println(e);
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * @param projectPath
	 */
	public boolean build(File projectPath, String boardName, String fqbn)
	{
		synchronized(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath 	= projectPath;
			c.command 		= BUILD;
			c.boardName		= boardName;
			c.fqbn 			= fqbn;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	
	/**
	 * @param projectPath
	 * @param portName
	 * @param boardName
	 * @param fqbn
	 * @return
	 */
	public boolean buildAndUpload(File projectPath, String portName, String boardName, String fqbn)
	{
		synchronized(queue) {
			if (!queue.isEmpty()) {
				return false;
			}
			Cmd c = new Cmd();
			c.projectPath 	= projectPath;
			c.command 		= BUILD_UPLOAD;
			c.portName		= portName;
			c.boardName		= boardName;
			c.fqbn 			= fqbn;
			queue.addLast(c);
			queue.notifyAll();
		}
		return true;
	}
	
	
	/**
	 * @param projectPath
	 * @param boardName
	 * @param fqbn
	 */
	private void _build(File projectPath, String boardName, String fqbn)
	{
		ArduinoCLI cli = ArduinoCLI.getSingleton();
		
		try {
			Message.println("building the arduino project... (board:" + boardName + ", fqbn:" + fqbn + ")");
			cli.copyCommandTo(projectPath);
			cli.buildProject(projectPath, fqbn);
			cli.deleteCommand(projectPath);
			Message.println("done.");
		}
		catch(IOException e) {
			Message.println(e);
		}
	}
	
	/**
	 * @param projectPath
	 * @param portName
	 * @param boardName
	 * @param fqbn
	 */
	private void _buildAndUpload(File projectPath, String portName, String boardName, String fqbn)
	{
		ArduinoCLI cli = ArduinoCLI.getSingleton();
		
		//close the port if the serial monitor is active.
		SerialMonitor monitor = SerialMonitor.getInstance();
		boolean portOpen = monitor.isOpen();
		if (portOpen) {
			monitor.close();
		}
		
		try {
			Message.println("building the arduino project... (board:" + boardName + ", fqbn:" + fqbn + ")");
			cli.copyCommandTo(projectPath);
			cli.buildProject(projectPath, fqbn);
			Message.println("done.");
			
			Message.println("uploading the arduino project... (port:" + portName + ")");
			cli.upload(projectPath, portName, fqbn);
			Message.println("done.");
			
			cli.deleteCommand(projectPath);
		}
		catch(IOException e) {
			Message.println(e);
		}

		//open the port again if the serial monitor was acitve.
		if (portOpen) {
			try {
				monitor.open();
			}
			catch(SerialMonitorException e)
			{
				Message.println(e);
			}
		}
		
		return;
	}
}
