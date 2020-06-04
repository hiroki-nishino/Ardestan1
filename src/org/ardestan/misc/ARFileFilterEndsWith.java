package org.ardestan.misc;
import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author hiroki
 *
 */
public class ARFileFilterEndsWith extends FileFilter
{
	protected String endWith 	= null;
	protected String desc		= null;
	
	/**
	 * @param endWith
	 * @param desc
	 */
	public ARFileFilterEndsWith(String endWith, String desc) 
	{
		this.endWith 	= endWith;
		this.desc		= desc;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) 
	{
		if (f.isDirectory()) {
			return true;
		}
		
		boolean ret = f.getName().endsWith(endWith);
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription()
	{
		return desc;
	}
}