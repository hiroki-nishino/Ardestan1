package org.ardestan.arclass;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.ardestan.json.JsonClassInfoLoader;

/**
 * @author hnishino
 *
 */
public class ARClassInfoUserDefined extends ARClassInfo
{

	ARClassInfoNative	nativeKlass;
	
	protected File		ucoFile;

	protected long		lastModified;
	
	
	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getRequiredSymbolIDs()
	 */
	public String[] getRequiredSymbolIDs()
	{
		return nativeKlass.requiredSymbolIDs;
	}
	
	/**
	 * @return
	 */
	public String[] getArgTypes()
	{
		return nativeKlass.getArgTypes();
	}
	/**
	 * @return
	 */
	public int getMinArgNum()
	{
		return nativeKlass.getMinArgNum();
	}
	
	/**
	 * @return
	 */
	public int getMaxArgNum()
	{
		return nativeKlass.getMaxArgNum();
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getNumOfInletsString()
	 */
	public String getNumOfInletsString() {
		return nativeKlass.getNumOfInletsString();
	}
	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.arclass.ARClassInfo#getNumOfOutletsString()
	 */
	public String getNumOfOutletsString()
	{
		return nativeKlass.getNumOfOutletsString();
	}
	
	/**
	 * @param file
	 */
	public ARClassInfoUserDefined(File file) 
	{
		ucoFile = file;
		
		lastModified = ucoFile.lastModified();
		
		
		nativeKlass = null;
		
	
		return;
	}
	
	/**
	 * 
	 */
	public void loadUserObjectDefinitionFile() throws IOException
	{
		nativeKlass = JsonClassInfoLoader.loadSingleClassInfoFromFile(ucoFile);
		
		return;
	}
	
	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public boolean fileExists()
	{
		return ucoFile.exists();
	}


	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#checkArguments(java.util.Vector)
	 */
	@Override
	public boolean checkArguments(Vector<String> arguments) 
	{
		if (nativeKlass == null) {
			return false;
		}
		return nativeKlass.checkArguments(arguments);
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getARClassName()
	 */
	@Override
	public String getARClassName() 
	{
		if (nativeKlass == null) {
			return null;
		}
		return nativeKlass.getARClassName();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getNumOfOutlets(java.util.Vector)
	 */
	@Override
	public int getNumOfOutlets(Vector<String> arguments) 
	{
		if (nativeKlass == null) {
			return 0;
		}

		return nativeKlass.getNumOfOutlets(arguments);
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getNumOfInlets(java.util.Vector)
	 */
	@Override
	public int getNumOfInlets(Vector<String> arguments) 
	{
		if (nativeKlass == null) {
			return 0;
		}
		return nativeKlass.getNumOfInlets(arguments);
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#isSubpatch()
	 */
	@Override
	public boolean isSubpatch() 
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getHeaderFilename()
	 */
	@Override
	public String getHeaderFilename() 
	{
		if (nativeKlass == null) {
			return null;
		}

		return ucoFile.getParentFile().getAbsolutePath() + File.separator + nativeKlass.getHeaderFilename();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getCppFilename()
	 */
	@Override
	public String getCppFilename() 
	{
		if (nativeKlass == null) {
			return null;
		}
		return ucoFile.getParentFile().getAbsolutePath() + File.separator + nativeKlass.getCppFilename();
	}

	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getHeaderFilenameWithoutPath()
	 */
	public String getHeaderFilenameWithoutPath() {
		return nativeKlass.getHeaderFilename();
	}
	
	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARClassInfo#getCppFilenameWithoutPath()
	 */
	public String getCppFilenameWithoutPath()
	{
		return nativeKlass.getCppFilename();
	}
	
	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getFuncNameInit()
	 */
	@Override
	public String getFuncNameInit() {
		if (nativeKlass == null) {
			return null;
		}

		return nativeKlass.getFuncNameInit();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getFuncNameTrigger()
	 */
	@Override
	public String getFuncNameTrigger() 
	{
		if (nativeKlass == null) {
			return null;
		}

		return nativeKlass.getFuncNameTrigger();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getStructName()
	 */
	@Override
	public String getStructName() 
	{
		if (nativeKlass == null) {
			return null;
		}

		return nativeKlass.getStructName();
	}


	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#getErrorStrings(java.util.Vector)
	 */
	@Override
	public String getErrorStrings(Vector<String> arguments) 
	{
		if (nativeKlass == null) {
			return null;
		}
		return nativeKlass.getErrorStrings(arguments);
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#requirePolling()
	 */
	@Override
	public boolean requirePolling() 
	{
		if (nativeKlass == null) {
			return false;
		}
		return nativeKlass.requirePolling();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#requireLoadBang()
	 */
	@Override
	public boolean requireLoadBang() 
	{
		if (nativeKlass == null) {
			return false;
		}

		return nativeKlass.requireLoadBang();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#requireScheduling()
	 */
	@Override
	public boolean requireScheduling() 
	{
		if (nativeKlass == null) {
			return false;
		}

		return nativeKlass.requireScheduling();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#isNormalObject()
	 */
	@Override
	public boolean isNormalObject() 
	{
		if (nativeKlass == null) {
			return true;
		}

		return nativeKlass.isNormalObject();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#isOnlyLeftMostInletHot()
	 */
	@Override
	public boolean isOnlyLeftMostInletHot() 
	{
		if (nativeKlass == null) {
			return true;
		}

		return nativeKlass.isOnlyLeftMostInletHot();
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#alwaysIncludeInBuild()
	 */
	@Override
	public boolean alwaysIncludeInBuild() 
	{
		if (nativeKlass == null) {
			return false;
		}

		return nativeKlass.alwaysIncludeInBuild();
	}
	
	
	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARClassInfo#isUserDefinedObject()
	 */
	public boolean isUserDefinedObject() {
		return true;
	}
	
	/**
	 * @return
	 */
	public boolean isFileUpdated()
	{
		
		boolean ret = false;
		long latedUpdateTimestamp = ucoFile.lastModified();
		
		if (latedUpdateTimestamp != lastModified) {
			ret = true;
		}
		
		this.lastModified = latedUpdateTimestamp;
		return ret;
	}
}
