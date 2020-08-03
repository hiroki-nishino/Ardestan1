package org.ardestan.gui.visual;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.ardestan.arduinocli.ArduinoCLI;

/**
 * @author hnishino
 *
 */
public class GUIFont 
{
	private static boolean fontConversionRequired = false;
	
	private static final String 	DefaultFontName_MAC = "Courier";
	private static final String 	DefaultFontName_WIN = "Courier NEW";
	private static final String		DefaultFontName_LINUX = Font.MONOSPACED;
	
	private static final double 	DefaultFontSize = 14;

	protected static GUIFont singleton = null;
	
	private static String DefaultFontName = DefaultFontName_MAC;
	protected Map<Double, Font> objectBoxFonts = null;
	
	/**
	 * @return
	 */
	public static synchronized GUIFont getSingleton()
	{
		if (singleton == null) {
			singleton = new GUIFont();
	
			switch(ArduinoCLI.getOS()) {
			case ArduinoCLI.MAC_OS:
				DefaultFontName = DefaultFontName_MAC;
				break;
			case ArduinoCLI.WIN_32:
			case ArduinoCLI.WIN_64:
				DefaultFontName = DefaultFontName_WIN;
				break;
				
			case ArduinoCLI.LINUX_32:
			case ArduinoCLI.LINUX_64:
			case ArduinoCLI.LINUX_32_ARM:
			case ArduinoCLI.LINUX_64_ARM:
			default:
				DefaultFontName = DefaultFontName_LINUX;
				break;					
			}
		}
		
		return singleton;
	}
	
	
		
	/**
	 * @param fontName
	 * @return
	 */
	public String convertCommentFontNameIfNecessaryWhenLoading(String fontName)
	{
		switch(ArduinoCLI.getOS()) {
		case ArduinoCLI.MAC_OS:
			if (fontName.equals(DefaultFontName_LINUX) || fontName.equals(DefaultFontName_WIN)) {
				fontName = DefaultFontName;				
			}
			break;
		case ArduinoCLI.WIN_32:
		case ArduinoCLI.WIN_64:
			if (fontName.equals(DefaultFontName_LINUX) || fontName.equals(DefaultFontName_MAC)) {
				fontName = DefaultFontName;				
			}
			break;
			
		case ArduinoCLI.LINUX_32:
		case ArduinoCLI.LINUX_64:
		case ArduinoCLI.LINUX_32_ARM:
		case ArduinoCLI.LINUX_64_ARM:
		default:
			if (fontName.equals(DefaultFontName_WIN) || fontName.equals(DefaultFontName_MAC)) {
				fontName = DefaultFontName;				
			}
			break;					
		}
		
		
		return fontName;
	}
	
	/**
	 * @param fontName
	 * @return
	 */
	public String convertCommentFontNameIfNecessaryWhenSaving(String fontName)
	{
		if (fontName.equals(DefaultFontName_LINUX) || fontName.equals(DefaultFontName_WIN)) {
			return DefaultFontName_MAC;
		}
		
		return fontName;
	}
	/**
	 * 
	 */
	protected GUIFont()
	{
		this.objectBoxFonts = new HashMap<Double, Font>();
	}
	
	/**
	 * 
	 */
	public Font getObjectBoxFont()
	{
		return this.getObjectBoxFont(DefaultFontSize);
	}
	
	/**
	 * @return
	 */
	public String getDefaultFontName()
	{
		return DefaultFontName;
	}
	
	/**
	 * @return
	 */
	public double getDefaultFontSize()
	{
		return DefaultFontSize;
	}
	
	/**
	 * @param fontSize
	 * @return
	 */
	public Font getObjectBoxFont(double fontSize)
	{
		if (objectBoxFonts.containsKey(fontSize)) {
			return objectBoxFonts.get(fontSize);
		}
		
		int ceil = (int)Math.ceil(fontSize);
		Font f = new Font(DefaultFontName,Font.PLAIN, ceil);
		if (fontSize != ceil) {
			f = f.deriveFont((float)fontSize);
		}
		objectBoxFonts.put(fontSize, f);
		
		return f;
	}
	
	/**
	 * @param fontSize
	 * @return
	 */
	public Font getObjectBoxFont(double fontSize, int style)
	{
		int ceil = (int)Math.ceil(fontSize);
		Font f = new Font(DefaultFontName, style, ceil);
		if (fontSize != ceil) {
			f = f.deriveFont((float)fontSize);
		}
		objectBoxFonts.put(fontSize, f);
		
		return f;
	}
	
	
	
}
