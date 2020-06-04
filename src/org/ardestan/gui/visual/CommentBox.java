package org.ardestan.gui.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;


/**
 * @author hiroki
 *
 */
public class CommentBox 
{
	public static final Color	SelectedFontColor	= new Color(32	,  70, 240);
	public static final Color 	SelectedLineColor	= SelectedFontColor;

	protected String 	comment;
	protected String	fontName;
	protected double 	fontSize;
	protected Color		color;

	protected String[]	lines;
	
	protected int		x;
	protected int		y;
	protected int		width;
	protected int		height;
	
	protected int		scaledWidth;
	protected int		scaledHeight;
	protected int		scaledX;
	protected int		scaledY;
	
	protected double	scaleTheLastDrawCall;
	/**
	 * 
	 */
	public CommentBox()
	{
		this.setComment("");
		this.fontName 	= GUIFont.DefaultFontName;
		this.fontSize	= GUIFont.DefaultFontSize; 
		this.color 		= Color.BLACK;
		
		return;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public CommentBox clone()
	{
		CommentBox c = new CommentBox();
		
		c.setComment(this.comment, this.fontName, this.fontSize, this.color);
		
		c.setX		(this.x);
		c.setY		(this.y);
		c.setWidth	(this.width);
		c.setHeight	(this.height);
				
		return c;
		
	}
	
	/**
	 * @param comment
	 * @param fontSize
	 */
	public void setComment(String comment, String fontName, double fontSize, Color color)
	{
		this.setComment(comment);
		this.fontName	= fontName;
		this.fontSize 	= fontSize;
		this.color		= color;
		return;
	}
	
	/**
	 * @param fontName
	 */
	public void setFontName(String fontName)
	{
		this.fontName = fontName;
		this.requestUpdateDrawParameters();
	}

	/**
	 * 
	 */
	public String getFontName()
	{
		return this.fontName;
	}
	
	/**
	 * @return
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 */
	public void setComment(String comment) {
		
		boolean blankText = true;
		
		if (comment.length() != 0) {
			for (int i = 0; i < comment.length(); i++) {
				char c = comment.charAt(i);
				if (!Character.isWhitespace(c)) {
					blankText = false;
					break;
				}
			}
		}
		
		if (blankText) {
			this.comment = "Enter your text here.";
		}
		else {
			this.comment = comment;
		}
		
		this.lines = this.comment.split("\n");
		this.requestUpdateDrawParameters();
		return;
	}

	/**
	 * @return
	 */
	public double getFontSize() {
		return fontSize;
	}

	/**
	 * @param fontSize
	 */
	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
		this.requestUpdateDrawParameters();
	}
	


	/**
	 * @return
	 */
	public Color getColor() {
		return color;
	}


	/**
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}


	/**
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 */
	public void setX(int x) {
		this.requestUpdateDrawParameters();
		this.x = x;
	}

	/**
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 */
	public void setY(int y) {
		this.requestUpdateDrawParameters();
		this.y = y;
	}

	/**
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 */
	public void setWidth(int width) {
		this.requestUpdateDrawParameters();
		this.width = width;
	}

	/**
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 */
	public void setHeight(int height) {
		this.requestUpdateDrawParameters();
		this.height = height;
	}
	
	
	/**
	 * 
	 */
	public void requestUpdateDrawParameters()
	{
		this.scaleTheLastDrawCall = 0.0;
	}
	
	
	/**
	 * @return
	 */
	public int getScaledWidth() {
		return scaledWidth;
	}


	/**
	 * @return
	 */
	public int getScaledHeight() {
		return scaledHeight;
	}


	/**
	 * @return
	 */
	public int getScaledX() {
		return scaledX;
	}


	/**
	 * @return
	 */
	public int getScaledY() {
		return scaledY;
	}


	/**
	 * @param g
	 * @param scale
	 */
	public void updateDrawParameters(Graphics2D g, double scale)
	{
		if (this.scaleTheLastDrawCall == scale) {
			return;
		}
		
		this.scaledX = (int)Math.ceil(this.getX() * scale);
		this.scaledY = (int)Math.ceil(this.getY() * scale);
		
		double scaledFontSize = this.fontSize * scale;
		
		//g.setFont(GUIFont.getSingleton().getObjectBoxFont(scaledFontSize, fontStyle));		
		g.setFont(this.getFont(scaledFontSize));		
		
		FontMetrics metrics = g.getFontMetrics();
		
				
		int lh = metrics.getHeight() + metrics.getLeading();
		scaledHeight = lh * lines.length;

		scaledWidth = 0;
		for (String l : lines) {
			int lw = metrics.stringWidth(l);
			if (lw > scaledWidth) {
				scaledWidth = lw;
			}
		}		
		
		scaledWidth += metrics.getLeading() * 2;
		
		this.width 	= (int)Math.floor(scaledWidth / scale);
		this.height = (int)Math.floor(scaledHeight/ scale);

		this.scaleTheLastDrawCall = scale;
		
		return;
	}
	
	/**
	 * @return
	 */
	public Font getFont(double fontSize)
	{
		int ceil = (int)Math.ceil(fontSize);
		Font f = new Font(this.fontName, Font.PLAIN, ceil);
		if (fontSize != ceil) {
			f = f.deriveFont((float)fontSize);
		}
		
		return f;
	}
	/**
	 * @param g
	 * @param editManager
	 * @param offsetX
	 * @param offsetY
	 * @param scale
	 */
	public void draw(Graphics2D g, VisualProgramEditManager editManager, int offsetX, int offsetY, double scale)
	{
		//we draw nothing if the scale is 0.0.
		if (scale == 0.0) {
			return;
		}
		
		boolean selected = editManager.isSelected(this);
		
		double scaledFontSize = this.fontSize * scale;
		
		//g.setFont(GUIFont.getSingleton().getObjectBoxFont(scaledFontSize, fontStyle));		
		g.setFont(this.getFont(scaledFontSize));		
		
		FontMetrics metrics = g.getFontMetrics();
		
		int canvasX 		= (int)Math.ceil(this.x * scale - offsetX);
		int canvasY 		= (int)Math.ceil(this.y * scale - offsetY);

		int posX = canvasX + metrics.getLeading();
		int posY = canvasY + metrics.getAscent() + metrics.getLeading();

		if (selected == false) {
			g.setColor(this.color);
		}
		else {
			g.setColor(SelectedFontColor);
		}		
				
		for (String l : lines) {
			g.drawString(l, posX, posY);	
			posY += metrics.getHeight();
		}		
				
		if (selected == false) {
			return;
		}
		
		g.setColor(SelectedLineColor);
		BasicStroke os = (BasicStroke)g.getStroke();
		
		BasicStroke ns = new BasicStroke(
				os.getLineWidth(),
				os.getEndCap(),
				os.getLineJoin(),
				os.getMiterLimit(),
				new float[]{5.0f, 2.0f},
				0.0f);
		
		g.setStroke(ns);
		g.drawRect(canvasX, canvasY, scaledWidth, scaledHeight);
		g.setStroke(os);
		
		return;
	}
	

	
	/**
	 * @param canvasX
	 * @param canvasY
	 * @return
	 */
	public boolean isInside(int px, int py)
	{
		int l = this.getX();
		int u = this.getY();
		int r = l + this.getWidth();
		int b = u + this.getHeight();
		
		if (l <= px && u <= py && px <= r && py <= b) {
			return true;
		}
		
		return false;		
	}
}
