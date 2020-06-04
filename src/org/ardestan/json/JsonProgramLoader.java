package org.ardestan.json;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.ardestan.arclass.ARClassDatabase;
import org.ardestan.arclass.ARClassInfo;
import org.ardestan.generator.ARConnectionInfo;
import org.ardestan.generator.ARInstanceInfo;
import org.ardestan.generator.ARSubpatchInstance;
import org.ardestan.gui.visual.CommentBox;
import org.ardestan.gui.visual.ObjectBox;
import org.ardestan.gui.visual.ObjectBoxConnection;

import com.google.gson.Gson;

/**
 * @author hiroki
 *
 */
public class JsonProgramLoader 
{
	protected Vector<ARInstanceInfo>	instances;
	protected Vector<ARConnectionInfo>	connections;
	protected Vector<CommentBox>		commentBoxes;
	
	 
	/**
	 * @param classDatabaseFilename
	 * @param programFilename
	 */
	public void loadFromFile(String programFilename) throws IOException
	{
		Map<String, ARClassInfo> nameToClassInfo = ARClassDatabase.getSingleton().getCopyOfNameToClassInfoMap();
		this.loadProgramFromFile(nameToClassInfo, programFilename);
	}
	
	
	/**
	 * @param resourceName
	 * @throws IOException
	 */
	public void loadFromResource(String resourceName) throws IOException
	{
		Map<String, ARClassInfo> nameToClassInfo = ARClassDatabase.getSingleton().getCopyOfNameToClassInfoMap();
		this.loadProgramFromResource(nameToClassInfo, resourceName);
	}

	
	/**
	 * @return the instances
	 */
	public Vector<ARInstanceInfo> getInstances() {
		return instances;
	}


	/**
	 * @return the connections
	 */
	public Vector<ARConnectionInfo> getConnections() {
		return connections;
	}
	
	/**
	 * @return
	 */
	public Vector<CommentBox> getCommentBoxes(){
		return commentBoxes;
	}

	/**
	 * @return
	 */
	public ARSubpatchInstance createARSubpatchInstance()
	{
		Vector<ObjectBox> 				objectBoxes = new Vector<ObjectBox>();
		Vector<ObjectBoxConnection>		objectBoxConnections = new Vector<ObjectBoxConnection>();
		
		Map<String, ObjectBox> instanceNameToObjectBox = new HashMap<String, ObjectBox>();

		
		Vector<String> args = new Vector<String>();
		
		for (ARInstanceInfo i: instances) {
			ObjectBox b = new ObjectBox();
		
			if (i.getARClassInfo() != null) {
				b.setARClassInfo(i.getARClassInfo());

				args.clear();
				for (String arg: i.getArgValues()) {
					args.add(arg);
				}

				b.setArguments(args);
			}			
			b.setX(i.getX());
			b.setY(i.getY());
			b.setWidth(i.getW());	
			b.setHeight(i.getH());
			
			b.setNumOfInletsAndOutletsWhenLoading(i.getNumOfInlets(), i.getNumOfOutlets());
	
			if (i.isValid()) {
				b.validate();
			}
			else {
				b.invalidate();
			}
			
			b.setBoxText(i.getBoxText());

			objectBoxes.add(b);			

			instanceNameToObjectBox.put(i.getInstanceName(), b);
		}
		
		for (ARConnectionInfo i: connections) {
			ObjectBoxConnection c  = new ObjectBoxConnection();
			c.src = instanceNameToObjectBox.get(i.getSrc());
			c.outletNo = i.getOutletNo();
			c.dst = instanceNameToObjectBox.get(i.getDest());
			c.inletNo  = i.getInletNo();
			objectBoxConnections.add(c);
		}
		
		return new ARSubpatchInstance(objectBoxes, objectBoxConnections, commentBoxes);
	}
	

//	/**
//	 * @param filename
//	 * @return
//	 * @throws IOException
//	 */
//	protected String buildString(String filename) throws IOException
//	{
//		//read the text from the file.
//		File file = new File(filename);
//		BufferedReader br = new BufferedReader(new FileReader(file));
//		
//		StringBuilder builder = new StringBuilder();
//		while(true){
//			String s = br.readLine();
//			if (s == null){
//				break;
//			}
//			builder.append(s);
//		}
//		
//		br.close();
//		
//		return builder.toString();
//	}

	
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
		
		String s = buildString(br);
		br.close();

		return s;
	
	}
	
	/**
	 * @param bfr
	 * @return
	 * @throws IOException
	 */
	protected String buildString(BufferedReader br) throws IOException
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
	public void loadProgramFromResource(Map<String, ARClassInfo> nameToClassInfo, String resourceName) throws IOException
	{		
		InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
		
		BufferedReader br = null;
		try {
			InputStreamReader 	isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			String input = buildString(br);	

			Gson gson = new Gson();

			JsonARProgram program = gson.fromJson(input, JsonARProgram.class);
			this.parseJsonARProgram(nameToClassInfo, program);
		}
		finally {
			br.close();
		}
		return;
		
	}
	
	/**
	 * @param nameToClassInfo
	 * @param program
	 */
	protected void parseJsonARProgram(Map<String, ARClassInfo> nameToClassInfo, JsonARProgram program)
	{
		this.connections = program.connections != null ? program.connections : new Vector<ARConnectionInfo>();

		//build the instance information.
		instances = new Vector<ARInstanceInfo>();
		
		if (program.instances != null) {
			for(JsonARInstanceInfo i: program.instances) {

				ARClassInfo classInfo = null;
				if (i.className == null || nameToClassInfo.containsKey(i.className) == false) {
					ARInstanceInfo instanceInfo = new ARInstanceInfo(i.instanceName, null, null, null, i.x, i.y, i.w, i.h, i.numOfInlets, i.numOfOutlets, false, i.boxText);
					this.instances.addElement(instanceInfo);
					continue;
				}

				classInfo = nameToClassInfo.get(i.className);

				int length = i.args.size();
				String[] argTypes  = new String[length];
				String[] argValues = new String[length];
				for (int j = 0; j < i.args.size(); j++) {
					argTypes	[j] = i.args.get(j).type;
					argValues	[j] = i.args.get(j).value;
				}

				ARInstanceInfo instanceInfo = new ARInstanceInfo(i.instanceName, classInfo, argTypes, argValues, i.x, i.y, i.w, i.h, i.numOfInlets, i.numOfOutlets, i.valid, i.boxText);
				this.instances.addElement(instanceInfo);
			}
		}
		
		//load comment boxes
		commentBoxes = new Vector<CommentBox>();
		if (program.comments != null) {
			for (JsonCommentBox jcb: program.comments) {
				CommentBox cb = new CommentBox();
				cb.setComment(jcb.comment, jcb.fontName, jcb.fontSize, new Color(jcb.r, jcb.g, jcb.b));
				cb.setX(jcb.x);
				cb.setY(jcb.y);
				commentBoxes.add(cb);
			}
		}

		return;
	}
	/**
	 * @param jsonFilename
	 * @return
	 * @throws IOException
	 */
	protected void loadProgramFromFile(Map<String, ARClassInfo> nameToClassInfo, String jsonFilename) throws IOException
	{
		String input = buildString(jsonFilename);

		Gson gson = new Gson();

		JsonARProgram program = gson.fromJson(input, JsonARProgram.class);
		
		this.parseJsonARProgram(nameToClassInfo, program);
		
		return;
	}
	
	
}
