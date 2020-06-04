package org.ardestan.gui.visual;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hnishino
 *
 */
public class GUIFont 
{
	public static final String 	DefaultFontName = "Monospaced";
	public static final double 	DefaultFontSize = 14;

	protected static GUIFont singleton = null;
	
	protected Map<Double, Font> objectBoxFonts = null;
	
	/**
	 * @return
	 */
	public static synchronized GUIFont getSingleton()
	{
		if (singleton == null) {
			singleton = new GUIFont();
		}
		
		return singleton;
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
	 * @param fontSize
	 * @return
	 */
	public Font getObjectBoxFont(double fontSize)
	{
		if (objectBoxFonts.containsKey(fontSize)) {
			return objectBoxFonts.get(fontSize);
		}
		
		int ceil = (int)Math.ceil(fontSize);
		Font f = new Font("Monospaced",Font.PLAIN, ceil);
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
