package org.ardestan.arclass;

import java.util.Vector;

import org.ardestan.generator.ArgType;


/**
 * @author hnishino
 *
 */
public class ARClassInfoNative extends ARClassInfo
{

	protected String	headerFilename;
	protected String	cppFilename;
	protected String	funcNameInit;
	
	protected String	funcNameTrigger;
	protected String	structName;
	
	protected String	numOfInlets;
	protected String	numOfOutlets;
	
	
	/**
	 * @param className
	 * @return
	 */
	public void setFields(	String 		className,
							String 		behaviorType,
							boolean 	alwaysIncludeInBuild,
							boolean 	onlyLeftMostInletIsHot,
							int			minArgNum,
							int			maxArgNum,
							String[] 	argTypes,
							String[] 	requiredSymbolIDs,
							String		headerFilename,
							String		cppFilename,
							String		funcNameInit,
							String		funcNameTrigger,
							String 		structName,
							String		numOfInlets,
							String		numOfOutlets)
							
	{	
		this.className 				= className;
		this.behaviorType			= behaviorType;
		this.alwaysIncludeInBuild 	= alwaysIncludeInBuild;
		this.onlyLeftMostInletIsHot = onlyLeftMostInletIsHot;
		this.minArgNum				= minArgNum;
		this.maxArgNum				= maxArgNum;
		this.argTypes				= argTypes;
		this.requiredSymbolIDs		= requiredSymbolIDs;
		this.headerFilename			= headerFilename;
		this.cppFilename			= cppFilename;
		this.funcNameInit			= funcNameInit;
		this.funcNameTrigger		= funcNameTrigger;
		this.structName				= structName;
		this.numOfInlets			= numOfInlets;
		this.numOfOutlets			= numOfOutlets;
				
		return;
	}
	


	
	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getRequiredSymbolIDs()
	 */
	public String[] getRequiredSymbolIDs()
	{
		return requiredSymbolIDs;
	}
	
	/**
	 * @return
	 */
	public String[] getArgTypes()
	{
		return argTypes;
	}
	
	/**
	 * @return
	 */
	public int getMinArgNum()
	{
		return this.minArgNum;
	}
	
	/**
	 * @return
	 */
	public int getMaxArgNum()
	{
		return this.maxArgNum;
	}
	
	/**
	 * @return
	 */
	public String getNumOfInletsString()
	{
		return numOfInlets.trim();
	}
	
	/**
	 * @return
	 */
	public String getNumOfOutletsString()
	{
		return numOfOutlets.trim();
	}
	
	/**
	 * @return the headerFilename
	 */
	public String getHeaderFilename() {
		return headerFilename;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getHeaderFilenameWithoutPath()
	 */
	public String getHeaderFilenameWithoutPath() {
		return headerFilename;
	}
	
	
	/**
	 * @return the cppFilename
	 */
	public String getCppFilename() {
		return cppFilename;
	}
	
	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getCppFilenameWithoutPath()
	 */
	public String getCppFilenameWithoutPath()
	{
		return cppFilename;
	}
	

	/**
	 * @return the funcNameInit
	 */
	public String getFuncNameInit() {
		return funcNameInit;
	}

	/**
	 * @return the funcNameTrigger
	 */
	public String getFuncNameTrigger() {
		return funcNameTrigger;
	}

	/**
	 * @return the structName
	 */
	public String getStructName() {
		return structName;
	}


	/**
	 * @return the numOfOutlets
	 */
	public int getNumOfOutlets(Vector<String> arguments) {
	
		if (NUM_OF_OUTLETS_DEPENDS_ON_FIRST_ARG.equals(numOfOutlets.trim())) 
		{
			if (arguments.size() == 0) {
				return 0;
			}

			try {
				int n = Integer.parseInt(arguments.get(0));
				if (n > MAX_NUM_OF_OUTLETS) {
					n = MAX_NUM_OF_OUTLETS;
				}
				return n;
			}
			catch (NumberFormatException e) {
				return 0;			
			}	
		}
		
		if (NUM_OF_OUTLETS_DEPENDS_ON_NUM_OF_ARGS.equals(numOfOutlets.trim())){
			int n = arguments.size();
			if (n > MAX_NUM_OF_OUTLETS) {
				n = MAX_NUM_OF_OUTLETS;
			}
			return n;
		}


		try {
			int n = Integer.parseInt(numOfOutlets);
			return n;
		}
		catch (NumberFormatException e) {
			return 0;			
		}
	}
	
	/**
	 * @param arguments
	 * @return
	 */
	public int getNumOfInlets(Vector<String> arguments) {
		
		if (NUM_OF_INLETS_DEPENDS_ON_FIRST_ARG.equals(numOfInlets.trim())) {
			try {
				if (arguments.size() == 0) {
					return 0;
				}
				int n = Integer.parseInt(arguments.get(0));
				if (n > MAX_NUM_OF_INLETS) {
					n = MAX_NUM_OF_INLETS;
				}

				return n;
			}
			catch (NumberFormatException e) {
				return 0;			
			}	
		}
		
		if (NUM_OF_INLETS_DEPENDS_ON_NUM_OF_ARGS.equals(numOfInlets.trim())){
			int n = arguments.size();
			if (n > MAX_NUM_OF_INLETS) {
				n = MAX_NUM_OF_INLETS;
			}
			return arguments.size();
		}
		
				

		try {
			int n = Integer.parseInt(numOfInlets);
			return n;
		}
		catch (NumberFormatException e) {
			return 0;			
		}
	}
	

	/**
	 * @param arguments
	 * @return
	 */
	public String getErrorStrings(Vector<String> arguments)
	{
		
		int numArgs = arguments.size();
		

		//check the number of arguments
		if (numArgs < minArgNum || maxArgNum < numArgs) {
			if (minArgNum == maxArgNum) {
				return "the number of the arguments must be " + minArgNum + ".";
							
			}
			else {
				return "the number of the arguments must be between " + minArgNum + " and " + maxArgNum + ".";
			}
		}

		if (argTypes != null) {
			for (int i = 0; i < arguments.size() && i < argTypes.length; i++) {
				boolean acceptable = false;
				
				int argType = ArgType.getArgumentType(arguments.get(i));
				switch(argType) {
				case ArgType.INT:
					acceptable = argTypes[i].contains("i") || argTypes[i].contains("f");
					break;
				case ArgType.FLOAT:
					acceptable = argTypes[i].contains("f");
					break;
				case ArgType.SYM_ID:
					acceptable = true;
					if (arguments.get(i).charAt(0) != '$') {
						acceptable = argTypes[i].contains("s");
					}
					break;
				case ArgType.STRING:
					acceptable = argTypes[i].contains("S");
					break;
					
				case ArgType.PARAMETER:
					//postpone the type-check until the compile time.
					acceptable = true;
					break;
					
				case ArgType.INVALID_UNTERMINATED_STRING_LITERAL:
					return "The type of the argument #" + i + " is not valid. An unterminated string literal is found.";

				default:
					return "The type of the argument #" + i + " is not valid. Use int, float, symbol, or string.";
				}
				if (acceptable == false) {
					return ArgType.getArgumentTypeString(arguments.get(i)) + " is not valid for the argument #" + i + ".";
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @param arguments
	 * @return
	 */
	public boolean checkArguments(Vector<String> arguments)
	{
		int numArgs = arguments.size();
				
		//check the number of arguments
		if (numArgs < minArgNum) {
			return false;
		}

		if (maxArgNum < numArgs) {
			return false;
		}

		if (argTypes != null) {
			for (int i = 0; i < arguments.size() && i < argTypes.length; i++) {
				boolean acceptable = false;
				
				int argType = ArgType.getArgumentType(arguments.get(i));
				switch(argType) {
				case ArgType.INT:
					acceptable = argTypes[i].contains("i") || argTypes[i].contains("f");
					break;
				case ArgType.FLOAT:
					acceptable = argTypes[i].contains("f");
					break;
				case ArgType.SYM_ID:
					acceptable = true;
					if (arguments.get(i).charAt(0) != '$') {
						acceptable = argTypes[i].contains("s");
					}
					break;
				case ArgType.STRING:
					acceptable = argTypes[i].contains("S");
					break;
				case ArgType.PARAMETER:
					acceptable = true;
					break;
				default:
					//acceptable is false;
					break;
				}
				if (acceptable == false) {
					return false;
				}
			}
		}
		
		return true;
	}

	/**
	 * @return
	 */
	public boolean isSubpatch() {
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#isUserDefinedObject()
	 */
	public boolean isUserDefinedObject() {
		return false;
	}
	

	
}
