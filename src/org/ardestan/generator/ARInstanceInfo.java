package org.ardestan.generator;

import java.util.Vector;

import org.ardestan.arclass.ARClassInfo;

/**
 * @author hnishino
 *
 */
public class ARInstanceInfo 
{
	protected String instanceName;
		
	protected String[] argTypes;
	protected String[] argValues;
	
	protected ARClassInfo classInfo;
	
	protected int x;
	protected int y;
	
	protected int w;
	protected int h;
	
	protected int numOfInlets;
	protected int numOfOutlets;
	
	protected boolean	valid;
	protected String 	boxText;
	
	protected Vector<String> arguments;
	

	/**
	 * @param instanceName
	 * @param classInfo
	 * @param argTypes
	 * @param argValues
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param numOfInlets
	 * @param numOfOutlets
	 * @param valid
	 * @param boxText
	 */
	public ARInstanceInfo(String instanceName, ARClassInfo classInfo, String[] argTypes, String[] argValues, int x, int y, int w, int h, int numOfInlets, int numOfOutlets, boolean valid, String boxText)
	{
		this.instanceName 	= instanceName;
		this.classInfo 		= classInfo;
				
		this.argTypes 		= argTypes;
		this.argValues 		= argValues;
		
		this.x = x;
		this.y = y;
		
		this.w = w;
		this.h = h;
		
		this.numOfInlets = numOfInlets;
		this.numOfOutlets = numOfOutlets;
		
		this.valid = valid;
		this.boxText = boxText;
		
		if (argTypes != null && argValues != null && argTypes.length != argValues.length){
			throw new RuntimeException("the length of argTypes doesn't match the length of argValues.");
		}
		
		this.arguments = new Vector<String>();
		if (argValues != null) {
			for (String arg: argValues) {
				this.arguments.add(arg);
			}
		}
	}

	
	
	/**
	 * @return
	 */
	public boolean isSubpatch()
	{
		return this.classInfo.isSubpatch();
	}

	/**
	 * @return the instanceName
	 */
	public String getInstanceName() {
		return instanceName;
	}

	
	/**
	 * @return
	 */
	public ARClassInfo getARClassInfo() {
		return this.classInfo;
	}
	
	/**
	 * @return
	 */
	public String getClassName(){
		return classInfo.getARClassName();
	}
	
	/**
	 * @return
	 */
	public String getStructName(){
		return classInfo.getStructName();
	}
	
	/**
	 * @return
	 */
	public String getHeaderFilename(){
		return classInfo.getHeaderFilename();
	}
	
	/**
	 * @return
	 */
	public String getCppFilename()
	{
		return classInfo.getCppFilename();
	}
	
	/**
	 * @return
	 */
	public int getNumOfInlets(){
		if (this.valid) {
			return this.getARClassInfo().getNumOfInlets(arguments);
		}
		return this.numOfInlets;
	}
	
	/**
	 * @return
	 */
	public int getNumOfOutlets() {
		if (this.valid) {
			return this.getARClassInfo().getNumOfOutlets(arguments);
		}
		return this.numOfOutlets;
	}
	
	/**
	 * @return
	 */
	public String getFuncNameInit(){
		return classInfo.getFuncNameInit();
	}

	/**
	 * @return
	 */
	public String getFuncNameTrigger(){
		return classInfo.getFuncNameTrigger();
	}
	
	/**
	 * @return the argTypes
	 */
	public String[] getArgTypes() {
		return argTypes;
	}


	/**
	 * @return the argValues
	 */
	public String[] getArgValues() {
		return argValues;
	}
	
	/**
	 * @return
	 */
	public int getArgc(){
		return (argTypes != null ? argTypes.length : 0);
	}


	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}



	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}



	/**
	 * @return the w
	 */
	public int getW() {
		return w;
	}


	/**
	 * @return the h
	 */
	public int getH() {
		return h;
	}


	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}


	/**
	 * @return the boxText
	 */
	public String getBoxText() {
		return boxText;
	}



	/**
	 * @return
	 */
	public ARClassInfo getClassInfo() {
		return classInfo;
	}



	/**
	 * @param classInfo
	 */
	public void setClassInfo(ARClassInfo classInfo) {
		this.classInfo = classInfo;
	}



	/**
	 * @return
	 */
	public Vector<String> getArguments() {
		return arguments;
	}



	/**
	 * @param arguments
	 */
	public void setArguments(Vector<String> arguments) {
		this.arguments = arguments;
	}



	/**
	 * @param argTypes
	 */
	public void setArgTypes(String[] argTypes) {
		this.argTypes = argTypes;
	}



	/**
	 * @param argValues
	 */
	public void setArgValues(String[] argValues) {
		this.argValues = argValues;
	}



	/**
	 * @param boxText
	 */
	public void setBoxText(String boxText) {
		this.boxText = boxText;
	}

	
}
