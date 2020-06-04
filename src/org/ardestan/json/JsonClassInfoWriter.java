package org.ardestan.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ardestan.arclass.ARClassInfo;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

/**
 * @author hiroki
 *
 */
public class JsonClassInfoWriter 
{

	/**
	 * @param file
	 * @param info
	 * @throws IOException
	 */
	public static void save(File file, ARClassInfo info) throws IOException
	{
		FileWriter fsw = new FileWriter(file);
		JsonWriter jsw = new JsonWriter(fsw);
		jsw.setIndent("\t");
		
		Gson gson = new Gson();
		gson.toJson(info, info.getClass(), jsw);
		jsw.flush();
		jsw.close();		

	}
}
