package org.ardestan.arduinocli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Vector;

import org.ardestan.gui.Message;
import org.ardestan.gui.dialog.library.LibraryManagerSearchItem;
import org.ardestan.json.JsonBoardList;
import org.ardestan.json.JsonCoreListItem;
import org.ardestan.json.JsonCoreSearchResultItem;
import org.ardestan.json.JsonLibraryListItem;
import org.ardestan.json.JsonPort;
import org.ardestan.misc.ARFileConst;
import org.ardestan.misc.ProjectSetting;

import com.google.gson.Gson;

/**
 * @author hnishino
 *
 */
public class ArduinoCLI {
	
	public static final int MAC_OS = 0;
	public static final int WIN_64 = 1;
	public static final int WIN_32 = 2;
	public static final int LINUX_64 = 3;
	public static final int LINUX_32 = 4;
	public static final int LINUX_64_ARM = 5;
	public static final int LINUX_32_ARM = 6;
	
	protected static ArduinoCLI singleton = null;
	protected static int os;
	
	private static String command = ARFileConst.ARDUINO_CLI_COMMAND_NAME_UNIX;
	
	public String commandResourceName = null;
	
	
	/**
	 * @param os
	 * @return
	 */
	public static ArduinoCLI init(int os)
	{
		ArduinoCLI.os = os;
		singleton =  new ArduinoCLI();
		switch(os) {
		case MAC_OS:
			singleton.commandResourceName = ARFileConst.ARDUINO_CLI_RESOURCE_NAME_MAC;
			break;
			
		case WIN_64:
			//we need to change the command name from the default.
			command = ARFileConst.ARDUINO_CLI_COMMAND_NAME_WIN;
			singleton.commandResourceName = ARFileConst.ARDUINO_CLI_RESOURCE_NAME_WIN64;
			break;
			
		case WIN_32:
			//we need to change the command name from the default.
			command = ARFileConst.ARDUINO_CLI_COMMAND_NAME_WIN;
			singleton.commandResourceName = ARFileConst.ARDUINO_CLI_RESOURCE_NAME_WIN32;
			break;
			
		case LINUX_64:
			singleton.commandResourceName = ARFileConst.ARDUINO_CLI_RESOURCE_NAME_LINUX64;
			break;
			
		case LINUX_32:
			singleton.commandResourceName = ARFileConst.ARDUINO_CLI_RESOURCE_NAME_LINUX32;
			break;
			
		case LINUX_64_ARM:
			singleton.commandResourceName = ARFileConst.ARDUINO_CLI_RESOURCE_NAME_LINUX64ARM;
			break;
			
		case LINUX_32_ARM:
			singleton.commandResourceName = ARFileConst.ARDUINO_CLI_RESOURCE_NAME_LINUX32ARM;
			break;
			
		}
		
		return singleton;
	}
	
	/**
	 * @return
	 */
	public static int getOS()
	{
		return ArduinoCLI.os;
	}
	
	/**
	 * @return
	 */
	public static String getOSString()
	{
		String osString = null;
		switch(os) {
		case MAC_OS:
			osString = "macOS (64)";
			break;
			
		case WIN_64:
			osString = "Windows (64)";
			break;
			
		case WIN_32:
			osString = "Windows (32)";
			break;
			
		case LINUX_64:
			osString = "Linux (64)";
			break;
			
		case LINUX_32:
			osString = "Linux (32)";
			break;
			
		case LINUX_64_ARM:
			osString = "Linux (Arm 64)";
			break;
			
		case LINUX_32_ARM:
			osString = "Linux (Arm 32)";
			break;
		}
		
		return osString;
	}
	
	
	/**
	 * @return
	 */
	public static ArduinoCLI getSingleton()
	{
		return singleton;
	}
	
	/**
	 * @param path
	 * @throws IOException
	 */
	public void copyCommandTo(File projectPath) throws IOException
	{
		//the destination path
		File dst = new File(projectPath, command);
		
		if (dst.exists()) {
			Files.delete(dst.toPath());
		}

		//the resource name
		String commandResourceName = this.getCommandResourceName();
		
		//copy from the resource to the directory.
		this.copyFileFromResourceToDiretory(commandResourceName, dst.getAbsolutePath());
		
		dst.setExecutable(true);

	}
	
	/**
	 * @param projectPath
	 * @throws IOException
	 */
	public void deleteCommand(File projectPath) throws IOException
	{
		//the file path
		File f = new File(projectPath, command);
		f.delete();

		return;
	}
	
	/**
	 * @param resourceName
	 * @param ProjectPath
	 * @throws IOException
	 */
	public void copyFileFromResourceToDiretory(String resourceName, String destinationFilename) throws IOException
	{
		InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);

		FileSystem fs = FileSystems.getDefault();
		Path dst = fs.getPath(destinationFilename);
		
		if (Files.exists(dst)) {
			Files.delete(dst);
		}

		Files.copy(is, dst);
		
		return;
	}
	

	/**
	 * @param arguments
	 * @throws IOException
	 */
	protected int executeCLI(File projectPath, LinkedList<String> arguments) throws IOException
	{
		File commandPath = new File(projectPath, ArduinoCLI.command);
		arguments.addFirst(commandPath.getAbsolutePath());

		LinkedList<String> execCommand = arguments;

		ProcessBuilder builder = new ProcessBuilder(execCommand);
		builder.redirectErrorStream(true);

		Process process = builder.start();

		InputStream stream = process.getInputStream();


		while (true) {
			int c = stream.read();
			if (c == -1) {
				stream.close();
				break;
			}
			String s = "" + (char)c;
			Message.print(s);
		}

		try {
			process.waitFor();
		}
		catch (InterruptedException e) {
			throw new IOException(e);
		}

		return process.exitValue();
	}
	
	/**
	 * @param projectPath
	 * @return
	 * @throws IOException
	 */
	public JsonPort[] getPorts(File projectPath) throws IOException
	{
		JsonPort[] ports = this.getBoardList(projectPath);		
		return ports;
	}
	

	
	/**
	 * @param projectPath
	 * @param arguments
	 * @return
	 * @throws IOException
	 */
	protected String  executeCLiGetStdOutString(File projectPath, LinkedList<String> arguments) throws IOException
	{
		File commandPath = new File(projectPath, ArduinoCLI.command);
		arguments.addFirst(commandPath.getAbsolutePath());
		
		LinkedList<String> execCommand = arguments;
		
		ProcessBuilder builder = new ProcessBuilder(execCommand);
		builder.redirectErrorStream(true);

		Process process = builder.start();
		
		InputStream isr  = process.getInputStream();
	
		StringBuffer buf = new StringBuffer();
		
         while (true) {
             int c1 = isr.read();
             if (c1 == -1) {
            	 break;
             }
        	 buf.append((char)c1);
         }
                  
         isr.close();
         
     	try {
			process.waitFor();
		}
		catch (InterruptedException e) {
			throw new IOException(e);
		}

         
         System.out.println(process.exitValue());
         
         return buf.toString();
	}
	


	/**
	 * @return
	 */
	public String getCommandResourceName(){
		return commandResourceName;
	}

	

	/**
	 * @param projectPath
	 * @return
	 * @throws IOException
	 */
	public int updateCoreIndex(File projectPath) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("core");
		args.addLast("update-index");
		args.addLast("--format");
		args.addLast("json");
		
		this.addAdditionalURLsToArgs(args);

		return this.executeCLI(projectPath, args);		
	}
	
	/**
	 * @param projectPath
	 * @return
	 * @throws IOException
	 */
	public int updateLibraryIndex(File projectPath) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("lib");
		args.addLast("update-index");
		
		args.addLast("--format");
		args.addLast("json");

		this.addAdditionalURLsToArgs(args);
		
		return this.executeCLI(projectPath, args);		
	}
	
	

	/**
	 * @param projectPath
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public int installCore(File projectPath, String id) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("core");
		args.addLast("install");
		args.addLast(id);
		
		this.addAdditionalURLsToArgs(args);

		return this.executeCLI(projectPath, args);		
	}
	
	/**
	 * @param projectPath
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public int installLibrary(File projectPath, String id) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("lib");
		args.addLast("install");
		args.addLast(id);
		
		this.addAdditionalURLsToArgs(args);

		return this.executeCLI(projectPath, args);		
	}
	

	/**
	 * @param projectPath
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public int uninstallCore(File projectPath, String id) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("core");
		args.addLast("uninstall");
		args.addLast(id);
		
		this.addAdditionalURLsToArgs(args);

		return this.executeCLI(projectPath, args);		
	}
	
	
	/**
	 * @param projectPath
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public int uninstallLibrary(File projectPath, String name) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("lib");
		args.addLast("uninstall");
		args.addLast(name);

		this.addAdditionalURLsToArgs(args);

		
		return this.executeCLI(projectPath, args);		
	}
	

	/**
	 * @param projectPath
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public int upgradeCore(File projectPath, String id) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("core");
		args.addLast("upgrade");
		args.addLast(id);
		
		this.addAdditionalURLsToArgs(args);

		return this.executeCLI(projectPath, args);		
	}
	

	/**
	 * @param projectPath
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public int upgradeLibrary(File projectPath) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("lib");
		args.addLast("upgrade");
		
		this.addAdditionalURLsToArgs(args);
		
		return this.executeCLI(projectPath, args);		
	}
	
	/**
	 * @param projectPath
	 * @return
	 * @throws IOException
	 */
	public JsonPort[] getBoardList(File projectPath) throws IOException
	{		
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("board");
		args.addLast("list");

		args.addLast("--format");
		args.addLast("json");
		
		this.addAdditionalURLsToArgs(args);

		String result = this.executeCLiGetStdOutString(projectPath, args);

		Gson gson = new Gson();
		JsonPort[] ports = gson.fromJson(result, JsonPort[].class);
		return ports;
	}
	
	
	

	/**
	 * @param projectPath
	 * @return
	 * @throws IOException
	 */
	public JsonCoreListItem[] getInstalledBoards(File projectPath) throws IOException
	{		
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("core");
		args.addLast("list");
		args.addLast("--format");
		args.addLast("json");
		
		this.addAdditionalURLsToArgs(args);

		String result = this.executeCLiGetStdOutString(projectPath, args);

		Gson gson = new Gson();
		JsonCoreListItem[] list = gson.fromJson(result, JsonCoreListItem[].class);
		return list;
	}
	
	/**
	 * @param projectPath
	 * @return
	 * @throws IOException
	 */
	public JsonLibraryListItem[] getInstalledLibraries(File projectPath) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("lib");
		args.addLast("list");
		args.addLast("--format");
		args.addLast("json");
		this.addAdditionalURLsToArgs(args);

		
		String result = this.executeCLiGetStdOutString(projectPath, args);

		Gson gson = new Gson();
		JsonLibraryListItem[] list = gson.fromJson(result, JsonLibraryListItem[].class);
		return list;
	}
	

	/**
	 * @param projectPath
	 * @return
	 * @throws IOException
	 */
	public JsonBoardList listAllKnownBoards(File projectPath) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("board");
		args.addLast("listall");

		args.addLast("--format");
		args.addLast("json");

		this.addAdditionalURLsToArgs(args);

		String result = this.executeCLiGetStdOutString(projectPath, args);

		Gson gson = new Gson();
		JsonBoardList list = gson.fromJson(result, JsonBoardList.class);
		return list;
	}
	

	/**
	 * @param projectPath
	 * @param fqbn
	 * @throws IOException
	 */
	public void buildProject(File projectPath, String fqbn) throws IOException
	{
		File arduinoProjectDir = new File(projectPath, projectPath.getName());
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("compile");
		args.addLast("--verbose");
		args.addLast("--fqbn");
		args.addLast(fqbn);
		args.addLast(arduinoProjectDir.getAbsolutePath());
				
		this.addAdditionalURLsToArgs(args);
		this.executeCLI(projectPath, args);

		return;
	}
	

	/**
	 * @param projectPath
	 * @param port
	 * @param fqbn
	 * @throws IOException
	 */
	public void upload(File projectPath, String port, String fqbn) throws IOException
	{

		File arduinoProjectDir = new File(projectPath, projectPath.getName());

		LinkedList<String> args = new LinkedList<String>();
		args.addLast("upload");
		args.addLast("-p");
		args.addLast(port);
		args.addLast("--fqbn");
		args.addLast(fqbn);
		args.addLast(arduinoProjectDir.getAbsolutePath());
				
		this.addAdditionalURLsToArgs(args);

		this.executeCLI(projectPath, args);

		return;
	}
	
	
	
	/**
	 * @param projectPath
	 * @param keyword
	 * @return
	 * @throws IOException
	 */
	public LibraryManagerSearchItem[] searchLibrary(File projectPath, String keyword) throws IOException
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("lib");
		args.addLast("search");
		args.addLast(keyword);
		
		args.addLast("--format");
		args.addLast("text");
		this.addAdditionalURLsToArgs(args);

		String result = this.executeCLiGetStdOutString(projectPath, args);

		return this.parseLibrarySearchOutputString(result);
	}

	
	/**
	 * @param output
	 * @return
	 */
	protected LibraryManagerSearchItem[] parseLibrarySearchOutputString(String output)
	{
		if (output.startsWith("No libraries")) {
			return null;
		}
		Vector<LibraryManagerSearchItem> items = new Vector<LibraryManagerSearchItem>();
		
		final String splitter = "Name:";
		String[] itemString = output.split(splitter);
		for (int i = 0; i < itemString.length; i++) {
			if (itemString[i].trim().equals("")){
				continue;
			}
			itemString[i] = splitter + itemString[i];
			LibraryManagerSearchItem item = this.parseLibrarySearchEachItem(itemString[i]);
			items.add(item);
		}
		
		if (items.size() == 0) {
			return null;
		}
		return items.toArray(new LibraryManagerSearchItem[items.size()]);
	}

	/**
	 * @param itemString
	 * @return
	 */
	protected LibraryManagerSearchItem parseLibrarySearchEachItem(String itemString)
	{
		LibraryManagerSearchItem item = new LibraryManagerSearchItem();
		item.fullInfoString = itemString;
		
		String[] lines = itemString.split("\n");
		
		for (int i = 0; i < lines.length; i++) {
			String line  = lines[i].trim();
			if (line.startsWith("Name")) {
				item.name = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));
			}
			else if (line.startsWith("Author")) {
				item.author = line.substring(line.indexOf(':') + 1).trim();
			}
			else if (line.startsWith("Versions")) {
				String tmp = line.substring(line.indexOf('[') + 1, line.lastIndexOf(']'));
				String[] tmp2 = tmp.split(",");
				item.versions = new Vector<String>();
				for (String t: tmp2) {
					item.versions.add(t.trim());
				}
			}
		}
		return item;
	}

	/**
	 * @param args
	 */
	public void addAdditionalURLsToArgs(LinkedList<String> args)
	{
		ProjectSetting setting = ProjectSetting.getSingleton();
		
		Vector<String> urls = setting.getBase64DecodedAdditionalURLs();
		if (urls.size() == 0){
			return;
		}
		
		for (String url: urls) {
			args.addLast("--additional-urls");
			args.addLast(url);
		}
		return;
	}
	
	/**
	 * @param projectPath
	 * @param keyword
	 * @return
	 * @throws IOException
	 */
	public JsonCoreSearchResultItem[] searchCore(File projectPath, String keyword) throws IOException 
	{
		LinkedList<String> args = new LinkedList<String>();
		args.addLast("core");
		args.addLast("search");
		args.addLast(keyword);
		args.addLast("--format");
		args.addLast("json");
		
		this.addAdditionalURLsToArgs(args);
		
		String result = this.executeCLiGetStdOutString(projectPath, args);

		Gson gson = new Gson();
		JsonCoreSearchResultItem[] ret = gson.fromJson(result, JsonCoreSearchResultItem[].class);
		return ret;
	}


	
}
