package org.ardestan.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.ardestan.arclass.ARClassInfo;
import org.ardestan.gui.visual.ObjectBox;
import org.ardestan.gui.visual.ObjectBoxConnection;
import org.ardestan.json.JsonARInstanceInfo;
import org.ardestan.json.JsonARInstanceInfoArg;
import org.ardestan.json.JsonARProgram;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

/**
 * @author hnishino
 *
 */
public class ArdestanSourceCodeGenerator 
{
	
	protected	Vector<JsonARInstanceInfo> 		instanceInfo	;
	protected	Vector<ARConnectionInfo>		connections		;
	protected	Map<String, Integer> 			instanceSeqIDs	;

	/**
	 * @param objectBoxes
	 * @param boxConnections
	 */
	public ArdestanSourceCodeGenerator()
	{
	}
	

	
	/**
	 * 
	 */
	public void buildModel(Vector<ObjectBox> objectBoxes, Vector<ObjectBoxConnection> boxConnections)
	{
		//------------------------------------------------------
		//obtain the valid connections and live object boxes.
		Vector<ObjectBoxConnection> validConnections = new Vector<ObjectBoxConnection>();
	
		Set<ObjectBox> liveObjectBoxes = new HashSet<ObjectBox>();
		
		for (ObjectBox b: objectBoxes) {
			if (b.isValid() && b.getARClassInfo().alwaysIncludeInBuild()) {
				liveObjectBoxes.add(b);
			}
			
		}

		for (ObjectBoxConnection c: boxConnections) {
			if (c.src.isValid() && c.dst.isValid()) {
				validConnections.add(c);
				liveObjectBoxes.add(c.src);
				liveObjectBoxes.add(c.dst);
			}
		}
		
		//------------------------------------------------------
		//first generate the vectors for ARInstanceInfo and ConnectionInfo.
		instanceSeqIDs = new HashMap<String, Integer>();
		instanceInfo = new Vector<JsonARInstanceInfo>();
		
		HashMap<ObjectBox, JsonARInstanceInfo> boxToJsonInstanceInfo = new HashMap<ObjectBox, JsonARInstanceInfo>();
		for (ObjectBox b: liveObjectBoxes) {
			ARClassInfo classInfo = b.getARClassInfo();
			String className = classInfo.getARClassName();
			String instanceName = this.generateInstanceName(className);
			
			Vector<String> argStrings = b.getArguments();
			int argNums = argStrings.size();
			
			Vector<JsonARInstanceInfoArg> args = new Vector<JsonARInstanceInfoArg>();
			for (int i = 0; i < argNums; i++) {
				JsonARInstanceInfoArg arg = new JsonARInstanceInfoArg();
				arg.value 	= argStrings.get(i);
				arg.type	= ArgType.getArgumentTypeString(arg.value);
				
				args.add(arg);
			}
			
			JsonARInstanceInfo i = new JsonARInstanceInfo();
			i.instanceName 	= instanceName;
			i.className		= className;
			i.x				= b.getX();
			i.y				= b.getY();
			i.w				= b.getWidth();
			i.h				= b.getHeight();
			i.numOfInlets	= b.getNumOfInlets();
			i.numOfOutlets	= b.getNumOfOutlets();
			i.args			= args;
			i.valid			= b.isValid();
			i.boxText		= b.getBoxText();
			
			
			instanceInfo.add(i);
			
			boxToJsonInstanceInfo.put(b, i);
		}
		
		connections = new Vector<ARConnectionInfo>();
		for (ObjectBoxConnection c: validConnections) {
			JsonARInstanceInfo src = boxToJsonInstanceInfo.get(c.src);
			JsonARInstanceInfo dst = boxToJsonInstanceInfo.get(c.dst);
			
			ARConnectionInfo i = new ARConnectionInfo(src.instanceName, c.outletNo, dst.instanceName, c.inletNo);
			connections.add(i);
		}
		
	}
	
	/**
	 * @param programFilename
	 */
	public void generateProgram(String programFilename) throws IOException
	{
		JsonARProgram program = new JsonARProgram();
		program.instances = this.instanceInfo;
		program.connections = this.connections;
		
		FileWriter fsw = new FileWriter(programFilename);
		JsonWriter jsw = new JsonWriter(fsw);
		jsw.setIndent("\t");
		
		Gson gson = new Gson();
		gson.toJson(program, program.getClass(), jsw);
		jsw.flush();
		jsw.close();		
	}
	
	/**
	 * @param className
	 * @return
	 */
	protected String generateInstanceName(String className)
	{
		if (instanceSeqIDs.containsKey(className)) {
			int seqID = instanceSeqIDs.get(className) + 1;
			instanceSeqIDs.put(className, seqID);
			return className + "_" + seqID;
		}
		
		instanceSeqIDs.put(className, 0);
		return className + "_" + 0;
	}
	


}
