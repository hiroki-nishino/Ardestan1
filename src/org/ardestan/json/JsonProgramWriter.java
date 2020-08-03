package org.ardestan.json;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.ardestan.arclass.ARClassInfo;
import org.ardestan.generator.ARConnectionInfo;
import org.ardestan.generator.ArgType;
import org.ardestan.gui.visual.CommentBox;
import org.ardestan.gui.visual.GUIFont;
import org.ardestan.gui.visual.ObjectBox;
import org.ardestan.gui.visual.ObjectBoxConnection;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

/**
 * @author hnishino
 *
 */
public class JsonProgramWriter 
{
	
	protected	Vector<JsonARInstanceInfo> 		instanceInfo	;
	protected	Vector<ARConnectionInfo>		connections		;
	protected	Vector<JsonCommentBox>			comments		;
	protected	Map<String, Integer> 			instanceSeqIDs	;

	/**
	 * @param objectBoxes
	 * @param boxConnections
	 */
	public JsonProgramWriter()
	{
	}
	
	/**
	 * 
	 */
	public void save(File file, Vector<ObjectBox> objectBoxes, Vector<ObjectBoxConnection> boxConnections, Vector<CommentBox> commentBoxes) throws IOException
	{
		//------------------------------------------------------
		//first generate the vectors for ARInstanceInfo and ConnectionInfo.
		instanceSeqIDs = new HashMap<String, Integer>();
		instanceInfo = new Vector<JsonARInstanceInfo>();
		
		HashMap<ObjectBox, JsonARInstanceInfo> boxToJsonInstanceInfo = new HashMap<ObjectBox, JsonARInstanceInfo>();
		for (ObjectBox b: objectBoxes) {
			ARClassInfo classInfo = b.getARClassInfo();

			String className = null;
			String instanceName = null;
			
			if (classInfo != null) {
				className = classInfo.getARClassName();
				instanceName = this.generateInstanceName(className);
			}
			else {
				instanceName = this.generateInstanceName("invalid_object");				
			}
			
			
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
			i.args			= args;
			
			i.numOfInlets	= b.getNumOfInlets();
			i.numOfOutlets	= b.getNumOfOutlets();
			
			i.valid			= b.isValid();
			i.boxText		= b.getBoxText();
			
			instanceInfo.add(i);
			
			boxToJsonInstanceInfo.put(b, i);
		}
		
		connections = new Vector<ARConnectionInfo>();
		for (ObjectBoxConnection c: boxConnections) {
			JsonARInstanceInfo src = boxToJsonInstanceInfo.get(c.src);
			JsonARInstanceInfo dst = boxToJsonInstanceInfo.get(c.dst);
			
			ARConnectionInfo i = new ARConnectionInfo(src.instanceName, c.outletNo, dst.instanceName, c.inletNo);
			connections.add(i);
		}
		
		comments = new Vector<JsonCommentBox>();
		GUIFont gf = GUIFont.getSingleton();
		for (CommentBox b: commentBoxes) {
			
			JsonCommentBox jcb = new JsonCommentBox();
			
			jcb.comment		= b.getComment();
			jcb.fontName	= gf.convertCommentFontNameIfNecessaryWhenSaving(b.getFontName());
			jcb.fontSize	= b.getFontSize();
			
			jcb.x			= b.getX();
			jcb.y			= b.getY();
			
			Color c = b.getColor();
			jcb.r			= c.getRed();
			jcb.g			= c.getGreen();
			jcb.b			= c.getBlue();
			
			comments.add(jcb);
		}
		
		this.generateProgram(file);
	}
	
	/**
	 * @param programFilename
	 */
	protected void generateProgram(File file) throws IOException
	{
		JsonARProgram program = new JsonARProgram();
		program.instances 	= this.instanceInfo;
		program.connections = this.connections;
		program.comments	= this.comments;
		
		FileWriter fsw = new FileWriter(file);
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
			int seqID = instanceSeqIDs.get(className)  + 1;
			instanceSeqIDs.put(className, seqID);
			return className + "_" + seqID;
		}
		
		instanceSeqIDs.put(className, 0);
		return className + "_" + 0;
	}	

}
