package org.ardestan.arclass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.ardestan.generator.ARSubpatchInstance;
import org.ardestan.gui.visual.CommentBox;
import org.ardestan.gui.visual.ObjectBox;
import org.ardestan.json.JsonClassInfoWriter;
import org.ardestan.json.JsonProgramLoader;
import org.ardestan.json.JsonProgramWriter;
import org.ardestan.misc.ARFileConst;


/**
 * @author hnishino
 *
 */
public abstract class ARClassInfo
{
	
	public static final String BANG_ON_LOAD 		= "requireLoadBang";
	public static final String REQUIRE_POLLING	 	= "requirePolling";
	public static final String REQUIRE_SCHEDULING	= "requireScheduling";
	public static final String NORMAL_OBJECTS		= "normal";
	
	public static final String NUM_OF_INLETS_DEPENDS_ON_FIRST_ARG 	= "first_arg";
	public static final String NUM_OF_INLETS_DEPENDS_ON_NUM_OF_ARGS	= "num_args";
	public static final String NUM_OF_INLETS_DEPENDS_ON_FIRST_ARG_PLUS_ONE	 = "first_arg_plus_one";
	public static final String NUM_OF_INLETS_DEPENDS_ON_NUM_OF_ARGS_PLUS_ONE = "num_args_plus_one";

	public static final String NUM_OF_OUTLETS_DEPENDS_ON_FIRST_ARG 		= NUM_OF_INLETS_DEPENDS_ON_FIRST_ARG;
	public static final String NUM_OF_OUTLETS_DEPENDS_ON_NUM_OF_ARGS	= NUM_OF_INLETS_DEPENDS_ON_NUM_OF_ARGS;
	public static final String NUM_OF_OUTLETS_DEPENDS_ON_FIRST_ARG_PLUS_ONE	 = NUM_OF_INLETS_DEPENDS_ON_FIRST_ARG_PLUS_ONE;
	public static final String NUM_OF_OUTLETS_DEPENDS_ON_NUM_OF_ARGS_PLUS_ONE = NUM_OF_INLETS_DEPENDS_ON_NUM_OF_ARGS_PLUS_ONE;

	public static final char TYPE_CHAR_INT 		= 'i';
	public static final char TYPE_CHAR_FLOAT 	= 'f';
	public static final char TYPE_CHAR_SYMBOL	= 's';
	public static final char TYPE_CHAR_STRING	= 'S';
	
	public static final int MIN_INLET_NO = 0;
	public static final int MAX_INLET_NO = 4;
	public static final int MAX_NUM_OF_INLETS = MAX_INLET_NO + 1;
	
	public static final int MIN_OUTLET_NO = 0;
	public static final int MAX_OUTLET_NO = 7;
	public static final int MAX_NUM_OF_OUTLETS = MAX_OUTLET_NO + 1;

	public static final int MIN_MIN_ARG_NUM = 0;
	public static final int MAX_MAX_ARG_NUM = 8;
	
	protected String className;
	protected String behaviorType;
	protected boolean alwaysIncludeInBuild;
	
	protected int minArgNum;
	protected int maxArgNum;
	
	protected boolean 	onlyLeftMostInletIsHot;
	protected String[]	argTypes;
	
	protected String[]	requiredSymbolIDs;
		
	/**
	 * @return
	 */
	public abstract String getNumOfInletsString();
	
	/**
	 * @return
	 */
	public abstract String getNumOfOutletsString();
	
	
	/**
	 * @return
	 */						
	public abstract String[] getRequiredSymbolIDs();
	
	/**
	 * @return
	 */
	public abstract String[] getArgTypes();
	
	/**
	 * @return
	 */
	public abstract int getMinArgNum();
	
	/**
	 * @return
	 */
	public abstract int getMaxArgNum();
	
	/**
	 * @return
	 */
	public boolean validateClassInfo()
	{
		return true;
	}

	
	/**
	 * @param arguments
	 * @return
	 */
	public abstract boolean checkArguments(Vector<String> arguments);
	
	/**
	 * @return the className
	 */
	public String getARClassName() {
		return className;
	}
	
	/**
	 * @return the numOfOutlets
	 */
	public abstract int getNumOfOutlets(Vector<String> arguments);
	
	/**
	 * @param arguments
	 * @return
	 */
	public abstract int getNumOfInlets(Vector<String> arguments);
	

	/**
	 * @return
	 */
	public abstract boolean isSubpatch();
	
	/**
	 * @return
	 */
	public abstract boolean isUserDefinedObject();
	
	/**
	 * @return the cppFilename
	 */
	public abstract String getCppFilename();

	/**
	 * @return the funcNameInit
	 */
	public abstract String getFuncNameInit();

	/**
	 * @return the funcNameTrigger
	 */
	public abstract String getFuncNameTrigger();

	/**
	 * @return the structName
	 */
	public abstract String getStructName();
	
	/**
	 * @return the headerFilename
	 */
	public abstract String getHeaderFilename();

	
	/**
	 * @return
	 */
	public abstract String getCppFilenameWithoutPath();
	
	/**
	 * @return
	 */
	public abstract String getHeaderFilenameWithoutPath();
	
	/**
	 * @param arguments
	 * @return
	 */
	public abstract String getErrorStrings(Vector<String> arguments);

	
	/**
	 * @return
	 */
	public String getBehaviorType()
	{
		return this.behaviorType;
	}
	
	/**
	 * @return
	 */
	public boolean requirePolling()
	{
		return REQUIRE_POLLING.equals(this.behaviorType);
	}
	
	
	/**
	 * @return
	 */
	public boolean requireLoadBang()
	{
		return BANG_ON_LOAD.equals(this.behaviorType);
	}
	
	/**
	 * @return
	 */
	public boolean requireScheduling()
	{
		return REQUIRE_SCHEDULING.equals(this.behaviorType);
	}
	
	/**
	 * @return
	 */
	public boolean isNormalObject()
	{
		return NORMAL_OBJECTS.equals(this.behaviorType);
	}
	
	/**
	 * @return
	 */
	public boolean isOnlyLeftMostInletHot()
	{
		return this.onlyLeftMostInletIsHot;
	}
	
	/**
	 * @return
	 */
	public boolean alwaysIncludeInBuild()
	{
		return this.alwaysIncludeInBuild;
	}

	
	/**
	 * @param className
	 * @return
	 */
	public static String getDefaultHeaderFilename(String className)
	{
		return ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_IMPLEMENTAION_PREFIX + className + ARFileConst.ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT;
	}
	
	/**
	 * @param className
	 * @return
	 */
	public static String getDefaultCppFilename(String className)
	{
		return  ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_IMPLEMENTAION_PREFIX + className + ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT;
	}
	
	/**
	 * @param className
	 */
	public static String getDefaultInitFuncName(String className)
	{
		return ARFileConst.DEFAULT_INIT_FUNC_PREFIX + className;
	}
	
	/**
	 * @param className
	 * @return
	 */
	public static String getDefaultTriggerFuncName(String className)
	{
		return ARFileConst.DEFAULT_TRIGGER_FUNC_PREFIX + className;
	}
	/**
	 * @param className
	 * @return
	 */
	public static String getDefaultStructName(String className)
	{
		return ARFileConst.AROBJ_STRUCTNAME_PREFIX + createCamelCaseStructName(className);
	}
	

	/**
	 * @param className
	 * @return
	 */
	public static String createCamelCaseStructName(String className)
	{
		StringBuffer structName = new StringBuffer();
		
		className = className.trim();
		
		structName.append(Character.toUpperCase(className.charAt(0)));
		
		for (int i = 1; i < className.length(); i++) {
			if (className.charAt(i) != '_') {
				structName.append(className.charAt(i));
				continue;
			}
			i++;
			if (i < className.length()) {
				structName.append(Character.toUpperCase(className.charAt(i)));
			}
		}
		
		return structName.toString();
	}
	
	/**
	 * @param directory
	 * @param info
	 * @throws IOException
	 */
	public static void createAudFile(File directory, ARClassInfo info) throws IOException
	{
		File newCustomObjectDefinitionFile = new File (directory, info.getARClassName()+ ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT);
		JsonClassInfoWriter.save(newCustomObjectDefinitionFile, info);
		
		return;
	}
	
	
	/**
	 * @param oldUserDefinedObjectDirectory
	 * @param newUserDefinedObjectDirectory
	 * @param oldObjectName
	 * @param newObjectName
	 * @throws IOException
	 */
	public static void copyHelpFile(File oldUserDefinedObjectDirectory, File newUserDefinedObjectDirectory, String oldObjectName, String newObjectName) throws IOException
	{
		File helpFile = new File(oldUserDefinedObjectDirectory, oldObjectName + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT);
		if (!helpFile.exists()) {
			return;
		}
		
		JsonProgramLoader loader = new JsonProgramLoader();
		loader.loadFromFile(helpFile.getAbsolutePath());

		
		ARSubpatchInstance instance = loader.createARSubpatchInstance();

		Vector<ObjectBox> objectBoxes = instance.getObjectBoxes();
		for (ObjectBox b: objectBoxes) {
			String tmp = b.getBoxText();
			String[] args = tmp.split(" ");
			if (args.length == 0 || args[0].equals(oldObjectName) == false) {
				continue;
			}
			args[0] = newObjectName;
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < args.length; i++) {
				buf.append(args[i]);
				buf.append(" ");
			}
			b.setBoxText(buf.toString());
		}
		
		Vector<CommentBox> commentBoxes = instance.getCommentBoxes();
		if (commentBoxes == null) {
			commentBoxes = new Vector<CommentBox>();
		}
		for (CommentBox b: commentBoxes) {
			String cmt = b.getComment();
			b.setComment(cmt.replaceAll(oldObjectName, newObjectName));
		}
		
		File newHelpFile = new File(newUserDefinedObjectDirectory, newObjectName + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT);
		JsonProgramWriter writer = new JsonProgramWriter();
		writer.save(newHelpFile, objectBoxes, instance.getObjectBoxConnections(), commentBoxes);
		
		return;
	}
	
	/**
	 * @param oldUserDefinedObjectDirectory
	 * @param newUserDefinedObjectDirectory
	 * @param oldInfo
	 * @param newInfo
	 * @throws IOException
	 */
	public static void copyCppAndHeaderFiles(File oldUserDefinedObjectDirectory, File newUserDefinedObjectDirectory, ARClassInfo oldInfo, ARClassInfo newInfo) throws IOException
	{
		//cpp
		File f = new File(oldUserDefinedObjectDirectory, oldInfo.getCppFilenameWithoutPath());
		if (f.exists()) {
			String oldCppFileString = buildString(f);
			String newCppFileString = replaceInfo(oldCppFileString, oldInfo, newInfo);
			saveString(new File(newUserDefinedObjectDirectory, newInfo.getCppFilenameWithoutPath()), newCppFileString);
		}
		
		//header
		f = new File(oldUserDefinedObjectDirectory, oldInfo.getHeaderFilenameWithoutPath());
		if (f.exists()) {
			String oldHeaderFileString = buildString(f);
			String newHeaderFileString = replaceInfo(oldHeaderFileString, oldInfo, newInfo);
			saveString(new File(newUserDefinedObjectDirectory, newInfo.getHeaderFilenameWithoutPath()), newHeaderFileString);
		}
		return;
	}
	
	
	/**
	 * @return
	 */
	public String getDefName()
	{
		return getDefName(this.className);
	}
	
	/**
	 * @param className
	 * @return
	 */
	public static String getDefName(String className)
	{
		return "__arobj_" + className + "_h__";
	}

	
	/**
	 * @param original
	 * @param newInfo
	 * @param oldInfo
	 * @return
	 */
	public static String replaceInfo(String oldString, ARClassInfo oldInfo, ARClassInfo newInfo)
	{
		String tmp = oldString.replaceAll(oldInfo.getFuncNameInit(), newInfo.getFuncNameInit());
		tmp = tmp.replaceAll(oldInfo.getFuncNameTrigger(), newInfo.getFuncNameTrigger());
		tmp = tmp.replaceAll(oldInfo.getStructName(), newInfo.getStructName());

		tmp = tmp.replaceAll(oldInfo.getCppFilenameWithoutPath(), newInfo.getCppFilenameWithoutPath());
		tmp = tmp.replaceAll(oldInfo.getHeaderFilenameWithoutPath(), newInfo.getHeaderFilenameWithoutPath());
		

		tmp = tmp.replaceAll(oldInfo.getDefName(), newInfo.getDefName());

		return tmp;
	}
	
	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	protected static String buildString(File file) throws IOException
	{
		//read the text from the file.
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
			int c = br.read();
			if (c == -1) {
				break;
			}
			builder.append((char)c);
		}
		
		
		return builder.toString();
	}
	
	/**
	 * @param file
	 * @param s
	 * @throws IOException
	 */
	protected static void saveString(File file, String s) throws IOException
	{
		FileWriter fw = new FileWriter(file);
		fw.write(s);
		fw.close();
		return;
	}
}
