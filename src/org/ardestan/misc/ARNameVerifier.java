/**
 * 
 */
package org.ardestan.misc;

/**
 * @author hnishino
 *
 */
public class ARNameVerifier 
{
	
	/**
	 * @param filename
	 * @return
	 */
	public static boolean isValidArhFilename(String filename)
	{
		if (!filename.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)) {
			return false;
		}
		
		//make it sure that 
		boolean dotFound = false;
		for (int i = 0; i < filename.length(); i++) {
			char c = filename.charAt(i);
			if (c == '.') {
				if (dotFound) {
					return false;
				}
				dotFound = true;
			}
		}
		
		//check if the object name is valid.
		String beforeExt = filename.substring(0, filename.lastIndexOf('.'));
		
		return isValidObjectName(beforeExt);
	}

	/**
	 * @param filename
	 * @return
	 */
	public static boolean isValidArdFilename(String filename)
	{
		if (!filename.endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT)) {
			return false;
		}
		
		//make it sure that 
		boolean dotFound = false;
		for (int i = 0; i < filename.length(); i++) {
			char c = filename.charAt(i);
			if (c == '.') {
				if (dotFound) {
					return false;
				}
				dotFound = true;
			}
		}
		
		//check if the object name is valid.
		String beforeExt = filename.substring(0, filename.lastIndexOf('.'));
		
		return isValidObjectName(beforeExt);
	}
	
	/**
	 * @param tf
	 * @return
	 */
	public static boolean isValidObjectName(String name)
	{
		if (name.toLowerCase().contains(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_IMPLEMENTAION_PREFIX_WITHOUT_UNDERSCORE)) {
			return false;
		}

		if (name.length() == 0) {
			return false;
		}
		
		char c = name.charAt(0);
		if (!Character.isLowerCase(c)) {
			return false;
		}

		for (int i = 1; i < name.length(); i++) {
			c = name.charAt(i);
			if (!Character.isLowerCase(c) && !Character.isDigit(c) && c != '_') {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param sym
	 * @return
	 */
	public static boolean isValidSymbol(String sym)
	{
		if (sym.length() == 0) {
			return false;
		}
		char c0 = sym.charAt(0);
		if (!Character.isLowerCase(c0) && c0 != '_') {
			return false;
		}
		
		for (int i = 1; i  < sym.length(); i++) {
			char c = sym.charAt(i);
			if (!Character.isLowerCase(c) && !Character.isDigit(c) && c != '_') {
				return false;
			}
		}
		
		return true;
	}
	
}
