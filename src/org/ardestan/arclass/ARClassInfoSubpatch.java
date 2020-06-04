package org.ardestan.arclass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.ardestan.generator.ARSubpatchInstance;
import org.ardestan.generator.ArgType;
import org.ardestan.json.JsonARInstanceInfo;
import org.ardestan.json.JsonARProgram;
import org.ardestan.json.JsonProgramLoader;

import com.google.gson.Gson;

/**
 * @author hnishino
 *
 */
public class ARClassInfoSubpatch extends ARClassInfo 
{
	protected File		subpatchFile;
	protected long		lastModified;

	
	protected int numOfInlets = 0;
	protected int numOfOutlets = 0;
	
	
	protected HashSet<String> containedSubpatchNames;

	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getRequiredSymbolIDs()
	 */
	public String[] getRequiredSymbolIDs()
	{
		return null;
	}
	
	/**
	 * @return
	 */
	public String[] getArgTypes()
	{
		return null;
	}
	
	/**
	 * @return
	 */
	public int getMinArgNum()
	{
		return MIN_MIN_ARG_NUM;
	}
	
	/**
	 * @return
	 */
	public int getMaxArgNum()
	{
		return MAX_MAX_ARG_NUM;
	}
	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getNumOfInletsString()
	 */
	public String getNumOfInletsString()
	{
		return "" + numOfInlets;
	}
	
	/**
	 * @return
	 */
	public String getNumOfOutletsString()
	{
		return "" + numOfOutlets;
	}
	
	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public boolean fileExists()
	{
		return subpatchFile.exists();
	}
	
	/**
	 * @param file
	 */
	public ARClassInfoSubpatch(File file) 
	{
		subpatchFile = file;
		
		lastModified = subpatchFile.lastModified();
		
		String filename = file.getName();
		int point = filename.lastIndexOf(".");
	
		if (point != -1) {
	    	className = filename.substring(0, point);
	    } 	
		else {
			className = filename;
		}
			
		containedSubpatchNames = new HashSet<String>();
		
	}
	
	/**
	 * 
	 */
	public ARSubpatchInstance instantiate() throws IOException
	{
		JsonProgramLoader loader = new JsonProgramLoader();
		loader.loadFromFile(subpatchFile.getAbsolutePath());
		return loader.createARSubpatchInstance();
	}
	
	/**
	 * 
	 */
	public Set<String> getContainedSubpatchNames()
	{
		return new HashSet<String>(containedSubpatchNames);
	}
	
	/**
	 * @return
	 */
	public boolean isFileUpdated()
	{
		
		boolean ret = false;
		long lastdUpdateTimestamp = subpatchFile.lastModified();
		
		if (lastdUpdateTimestamp != lastModified) {
			ret = true;
		}
		
		return ret;
	}

	
	/**
	 * 
	 */
	public void updateLastModifiedTimestamp()
	{
		this.lastModified = subpatchFile.lastModified();
	}
	
	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	protected String buildString() throws IOException
	{
		//read the text from the file.
		BufferedReader br = new BufferedReader(new FileReader(subpatchFile));
		
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
	 * @throws IOException
	 */
	public boolean updateSubpatchInfo() throws IOException
	{
		String input = buildString();

		Gson gson = new Gson();

		JsonARProgram program = gson.fromJson(input, JsonARProgram.class);
		if (program == null) {
			return false;
		}
		
		updateNumOfInletsAndOutlets(program);
		updateContainedSupatches(program);
		return true;
	}
	
	/**
	 * @param program
	 */
	public void updateContainedSupatches(JsonARProgram program) 
	{

		ARClassDatabase db = ARClassDatabase.getSingleton();
		
		containedSubpatchNames.clear();
		for (JsonARInstanceInfo i : program.instances) {
			//neglect an invalid object.
			if (!i.valid) {
				continue;
			}
			ARClassInfo classInfo = db.getARClassInfo(i.className);
			if (classInfo == null) {
				continue;
			}
			if (classInfo.isSubpatch()) {
				containedSubpatchNames.add(i.className);
			}
		}
	}
	
	/**
	 * @throws IOException
	 */
	public void updateNumOfInletsAndOutlets(JsonARProgram program) 
	{
		
		int maxInletNo  = -1;
		int maxOutletNo = -1;
		
		
		//check if the object is inlet or outlet one-by-one 
		for (JsonARInstanceInfo i : program.instances) {
			
			String className = i.className;
			if (className == null) {
				continue;
			}
			className = className.trim();

			//is it an inlet
			if (ArgType.INLET_OBJECT_CLASS_NAME.equals(className)) {
				//does it have an integer argument as expected?
				if (i.args.size() == 0 || !ArgType.Str_INT.equals(i.args.get(0).type)) {
					continue;
				}
				
				//update the max inlet no. of this patch.
				int inletNo = Integer.parseInt(i.args.get(0).value);
				maxInletNo = maxInletNo < inletNo ? inletNo : maxInletNo;
			}
			else if (ArgType.OUTLET_OBJECT_CLASS_NAME.equals(className)){
				//does it have an integer argument as expected?
				if (i.args.size() == 0 || !ArgType.Str_INT.equals(i.args.get(0).type)) {
					continue;
				}
				
				//update the max outlet no. of this patch.
				int outletNo = Integer.parseInt(i.args.get(0).value);
				maxOutletNo = maxOutletNo < outletNo ? outletNo : maxOutletNo;
			}
		}
		
		//now we have the number of inlets and that of outlets.	
		numOfInlets = maxInletNo + 1;
		numOfOutlets = maxOutletNo + 1;	
		
		return;
	}
	
	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getErrorStrings(java.util.Vector)
	 */
	public String getErrorStrings(Vector<String> arguments)
	{
		return null;
	}


	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#isSubpatch()
	 */
	public boolean isSubpatch() {
		return true;
	}

	@Override
	public int getNumOfOutlets(Vector<String> arguments) {
		return numOfOutlets;
	}

	@Override
	public int getNumOfInlets(Vector<String> arguments) {
		return numOfInlets;
	}

	@Override
	public String getCppFilename() {
		return null;
	}
	

	@Override
	public String getCppFilenameWithoutPath() {
		return null;
	}

	@Override
	public String getFuncNameInit() {
		return null;
	}

	@Override
	public String getFuncNameTrigger() {
		return null;
	}

	@Override
	public String getStructName() {
		return null;
	}

	@Override
	public String getHeaderFilename() {
		return null;
	}
	
	
	@Override
	public String getHeaderFilenameWithoutPath() {
		return null;
	}
	
	@Override
	public boolean checkArguments(Vector<String> arguments) {
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#isUserDefinedObject()
	 */
	public boolean isUserDefinedObject() {
		return false;
	}
}
