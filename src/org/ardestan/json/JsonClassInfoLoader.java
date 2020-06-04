package org.ardestan.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import org.ardestan.arclass.ARClassInfo;
import org.ardestan.arclass.ARClassInfoNative;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author hnishino
 *
 */
public class JsonClassInfoLoader {

	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	protected static String buildString(String filename) throws IOException
	{
		//read the text from the file.
		File file = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String s = buildString(br);
		br.close();

		return s;
	
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
	 * @return
	 * @throws IOException
	 */
	public static Map<String, ARClassInfo> loadNativeClassInfoFromResource(String resourceName) throws IOException
	{
		Map<String, ARClassInfo> nameToClassInfo = new TreeMap<String, ARClassInfo>();
		
		InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
		
		InputStreamReader 	isr = new InputStreamReader(is);
		BufferedReader		br = new BufferedReader(isr);
		
		String input = buildString(br);
		br.close();
		

		Gson gson = new Gson();
		JsonArray jsonArray = gson.fromJson(input, JsonArray.class);
		
		for (int i = 0; i < jsonArray.size(); i++){
			JsonObject o = jsonArray.get(i).getAsJsonObject();
			ARClassInfo info = gson.fromJson(o, ARClassInfoNative.class);
			nameToClassInfo.put(info.getARClassName(), info);
		}
	
		return nameToClassInfo;
		
	}
	
	/**
	 * @param jsonFilename
	 * @throws IOException
	 */
	public static Map<String, ARClassInfo> loadNativeClassInfo(String jsonFilename) throws IOException
	{
		Map<String, ARClassInfo> nameToClassInfo = new TreeMap<String, ARClassInfo>();
	
		//parse the json file.
		String input = buildString(jsonFilename);

		Gson gson = new Gson();
		JsonArray jsonArray = gson.fromJson(input, JsonArray.class);
		
		for (int i = 0; i < jsonArray.size(); i++){
			JsonObject o = jsonArray.get(i).getAsJsonObject();
			ARClassInfo info = gson.fromJson(o, ARClassInfoNative.class);
			nameToClassInfo.put(info.getARClassName(), info);
		}
	
		return nameToClassInfo;
	}
	
	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ARClassInfoNative loadSingleClassInfoFromFile(File file) throws IOException
	{
		//parse the json file.
		String input = buildString(file.getAbsolutePath());

		Gson gson = new Gson();
		ARClassInfoNative klazz = gson.fromJson(input, ARClassInfoNative.class);

		return klazz;
	}
}
