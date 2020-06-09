package org.ardestan.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.ardestan.arduinocli.ArduinoCLI;
import org.ardestan.json.JsonDialogSizes;
import org.ardestan.json.JsonSize;
import org.ardestan.misc.ARFileConst;

import com.google.gson.Gson;

/**
 * @author hiroki
 *
 */
public class DialogSizes 
{
	JsonDialogSizes sizes = null;
		
	protected static DialogSizes singleton = null;	 
	
	
	/**
	 * 
	 */
	public static void init(int os) throws IOException
	{
		String resourceName = null;
		switch(os) {
		case ArduinoCLI.MAC_OS:
			resourceName = ARFileConst.DIALOG_SIZE_RESOURCE_NAME_MAC;
			break;
			
		case ArduinoCLI.LINUX_32:
		case ArduinoCLI.LINUX_64:
		case ArduinoCLI.LINUX_32_ARM:
		case ArduinoCLI.LINUX_64_ARM:
			resourceName = ARFileConst.DIALOG_SIZE_RESOURCE_NAME_LINUX;			
			break;

		default:
		case ArduinoCLI.WIN_32:       
			resourceName = ARFileConst.DIALOG_SIZE_RESOURCE_NAME_WIN32;
			break;
		case ArduinoCLI.WIN_64:
			resourceName = ARFileConst.DIALOG_SIZE_RESOURCE_NAME_WIN64;
			break;
		}
		
		DialogSizes instance = new DialogSizes();
		instance.load(resourceName);
		
		singleton = instance;
		return;
	}
	
	/**
	 * @return
	 */
	public static DialogSizes getSingleton()
	{
		return singleton;
	}
	
	/**
	 * @return
	 */
	public JsonSize getAboutSize()
	{
		return this.sizes.about;
	}
	
	/**
	 * @return
	 */
	public JsonSize getProjectSetting()
	{
		return this.sizes.projectSetting;
	}
	
	/**
	 * @return
	 */
	public JsonSize getUDOWizard()
	{
		return this.sizes.udoWizard;
	}
	/**
	 * @return
	 */
	public JsonSize getPreferenceSize()
	{
		return this.sizes.preference;
	}
	
	/**
	 * @return
	 */
	public JsonSize getBoardManagerSize()
	{
		return this.sizes.boardManager;
	}
	
	/**
	 * @return
	 */
	public JsonSize getLibraryManagerSize()
	{
		return this.sizes.libraryManager;
	}
	
	/**
	 * @return
	 */
	public JsonSize getLibraryVersionSize()
	{
		return this.sizes.libraryVersion;
	}
	
	/**
	 * @return
	 */
	public JsonSize getConnectedBoardSize()
	{
		return this.sizes.connectedBoard;
	}
	/**
	 * @return
	 */
	public JsonSize getPortSize()
	{
		return this.sizes.port;
	}
	
	/**
	 * @return
	 */
	public JsonSize getBoardSize()
	{
		return this.sizes.board;
	}
	
	/**
	 * @return
	 */
	public JsonSize getMessageWindow()
	{
		return this.sizes.messageWindow;
	}
	
	/**
	 * @param bfr
	 * @return
	 * @throws IOException
	 */
	protected static String buildString(BufferedReader br) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		while(true){
			String s = br.readLine();
			if (s == null){
				break;
			}
			builder.append(s);
		}
		
		return builder.toString();
	}
	
	
	/**
	 * @param resourceName
	 * @throws IOException
	 */
	protected void load(String resourceName) throws IOException
	{
		InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
		
		InputStreamReader 	isr = new InputStreamReader(is);
		BufferedReader		br = new BufferedReader(isr);
		
		String input = buildString(br);
		br.close();
		
		Gson gson = new Gson();
		this.sizes = gson.fromJson(input, JsonDialogSizes.class);
		
		return;
	}
}
