package org.ardestan.gui.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JLayeredPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.ardestan.arclass.ARClassDatabase;
import org.ardestan.arclass.ARClassInfo;

/**
 * @author hiroki
 *
 */
public class ObjectBox implements UndoableEditListener
{
	public static final Color 	BackgroundColor  	= new Color(246	, 248, 248);
	public static final Color	SelectedFontColor	= new Color(32	,  70, 240);
	public static final Color 	SelectedLineColor	= SelectedFontColor;
	public static final Color	InvalidLineColor	= new Color(233, 63, 51);

	public static final int		DefaultTextFieldLayerNo 	= 10;
	public static final	int 	SelectedTextFiledLayerNo 	= 30;
	public static final int		DefaultHeight				= 16;
	public static final int		DefaultInletWidth 			= 8; 
	public static final int		DefaultInletHeight			= 2;
	public static final int		DefaultInletGap   			= 4; 
	
	public static final int		DefaultOutletWidth 			= 8; 
	public static final int		DefaultOutletHeight			= 2;
	public static final int		DefaultOutletGap   			= 4; 

	
	public static final int		CopyDiffX = DefaultInletWidth + DefaultInletGap;
	public static final int		CopyDiffY = DefaultHeight / 2;
	
	protected int				x;
	protected int				y;
	protected int				width;
	protected int				height;
	
	protected int				scaledX;
	protected int				scaledY;
	protected int				scaledWidth;
	protected int				scaledHeight;
	protected String			boxText;
	protected double			scaledFontSize;

	protected int 				scaledInletHeight;
	protected int				scaledInletWidth;
	protected int				scaledInletGap;
	
	protected int				scaledOutletWidth;
	protected int				scaledOutletHeight;
	protected int 				scaledOutletGap;

	protected int				numOfOutlets;
	protected int				numOfInlets;
	
	protected ARClassInfo		arclassInfo;
	protected Vector<String>	arguments;
	
	protected boolean			editMode;
	
	protected boolean			valid;
	
	protected double			scaleTheLastDrawCall;

	protected int				textHorizontalMargin;
	
	protected ObjectBoxTextField 		textField;
	
	
	/**
	 * 
	 */
	public ObjectBox() 
	{
		this.scaleTheLastDrawCall = 0;
		this.boxText = "";
		this.textField = new ObjectBoxTextField();	
		this.textField.setBackground(BackgroundColor);
		this.arguments = new Vector<String>();
		this.textField.getDocument().addUndoableEditListener(this);
		
		this.textField.enableInputMethods(false);
		this.textField.setObjectBox(this);
	
	}
	
	/**
	 * @param b
	 */
	public void setCloneParameters(ObjectBox c)
	{
		c.x = this.x;
		c.y = this.y;
		
		c.width 	= this.width;
		c.height 	= this.height;
		
		c.scaledX = this.scaledX;
		c.scaledY = this.scaledY;
		c.scaledWidth 	= this.scaledWidth;
		c.scaledHeight 	= this.scaledHeight;
		c.boxText = this.boxText;
		c.scaledFontSize = this.scaledFontSize;
		
		c.scaledInletHeight = this.scaledInletHeight;
		c.scaledInletWidth	= this.scaledInletWidth;
		c.scaledInletGap	= this.scaledInletGap;

		c.scaledOutletWidth	= this.scaledOutletWidth;
		c.scaledOutletHeight= this.scaledOutletHeight;
		c.scaledOutletGap	= this.scaledOutletGap;

		c.arclassInfo = this.arclassInfo;
		
		c.arguments = new Vector<String>(this.arguments);
		c.scaleTheLastDrawCall = this.scaleTheLastDrawCall;
		
		c.numOfInlets 	= this.numOfInlets;
		c.numOfOutlets 	= this.numOfOutlets;
		c.editMode = this.editMode;	
		c.valid = this.valid;
		

		c.setBoxText(boxText);
		c.textHorizontalMargin = this.textHorizontalMargin;
		
		return;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public ObjectBox clone()
	{
		ObjectBox c = new ObjectBox();
		this.setCloneParameters(c);
		return c;
	}
	
	
	/**
	 * 
	 */
	public void grabFocus()
	{
		this.textField.grabFocus();
		return;
	}
	
	/**
	 * @param pane
	 */
	public void addedToProgramArea(JLayeredPane programArea)
	{
		programArea.add(this.textField);
		programArea.setLayer(this.textField, 10);

		return;
	}
	
	/**
	 * @param programArea
	 */
	public void removedFromProgramArea(JLayeredPane programArea)
	{
		programArea.remove(this.textField);

		return;
	}
	
	/**
	 * @param canvas
	 */
	public void addListenerCanvas(CodeCanvas canvas)
	{
		this.textField.addMouseWheelListener(canvas);
		this.textField.addKeyListener(canvas);
		this.textField.addFocusListener(canvas);
		this.textField.addActionListener(canvas);	

		return;
	}
	
	/**
	 * @param canvas
	 */
	public void removeListenerCanvas(CodeCanvas canvas)
	{
		this.textField.removeMouseWheelListener(canvas);
		this.textField.removeKeyListener(canvas);
		this.textField.removeFocusListener(canvas);
		this.textField.removeActionListener(canvas);
		
		return;
	}
	/**
	 * 
	 */
	public void selectAll()
	{
		this.textField.selectAll();
		return;
	}

	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * @return
	 */
	public boolean isValid() {
		return this.valid;
	}
	
	/**
	 * 
	 */
	public void validate() {
		this.valid = true;
	}
	
	/**
	 * 
	 */
	public void invalidate(){
		this.valid = false;
	}
	
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		if (x < 0) {
			x = 0;
		}
		this.x = x;
		
		//invalidate the current canvas parameters.
		this.requestUpdateDrawParameters();
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * @param edit
	 */
	public void setEditMode(boolean edit)
	{
		this.editMode = edit;
		if (this.editMode == true) {
			this.textField.setText(this.boxText);
			this.textField.setEditable(true);
		}
		else {
			this.parseBoxText(this.textField.getText());
			this.textField.setEditable(false);
		}
	}
	

	/**
	 * @param y the y to set
	 */
	
	public void setY(int y) {
		if (y < 0) {
			y = 0;
		}
		this.y = y;
		
		//invalidate the current canvas parameters.
		this.requestUpdateDrawParameters();
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
		
		//invalidate the current canvas parameters.
		this.requestUpdateDrawParameters();
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
		
		//invalidate the current canvas parameters.
		this.requestUpdateDrawParameters();
	}
	
	/**
	 * @return the arguments
	 */
	public Vector<String> getArguments() {
		return arguments;
	}
	
	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(Vector<String> arguments) {
		this.arguments = arguments;

		//invalidate the current canvas parameters.
		this.requestUpdateDrawParameters();
	}
	
	/**
	 * @return the klazz
	 */
	public ARClassInfo getARClassInfo() {
		return arclassInfo;
	}
	
	/**
	 * @param klazz the klazz to set
	 */
	public void setARClassInfo(ARClassInfo klazz) 
	{
		
		this.arclassInfo = klazz;
		this.updateNumOfInletsAndOutlets();
		
		this.requestUpdateDrawParameters();
	}
	
	/**
	 * @param numOfInlets
	 * @param numOfOutlets
	 */
	public void setNumOfInletsAndOutletsWhenLoading(int numOfInlets, int numOfOutlets)
	{
		this.numOfInlets = numOfInlets;
		this.numOfOutlets = numOfOutlets;
	}
	
	/**
	 * 
	 */
	public void updateNumOfInletsAndOutlets()
	{
		if (this.arclassInfo == null) {
			this.invalidate();			
		}
		else if (this.isValid()) {

			int numOfInlets		= arclassInfo.getNumOfInlets(arguments);
			int numOfOutlets 	= arclassInfo.getNumOfOutlets(arguments);
			
			if (numOfInlets < 0 || numOfInlets > ARClassInfo.MAX_NUM_OF_INLETS) {
				this.invalidate();
			}
			if (numOfOutlets < 0 || numOfOutlets > ARClassInfo.MAX_NUM_OF_OUTLETS) {
				this.invalidate();
			}
			
			if (this.isValid()){
				this.numOfInlets = numOfInlets;
				this.numOfOutlets = numOfOutlets;
			}
		}

		//invalidate the current canvas parameters.
		this.requestUpdateDrawParameters();
	}
	
	/**
	 * 
	 */
	public void boxTextUpdated()
	{
		this.parseBoxText(boxText);
	}
	
	
	/**
	 * @param boxText
	 */
	public void setBoxText(String boxText)
	{
		this.boxText = boxText;
		this.textField.setText(this.boxText);
		this.parseBoxText(boxText);
	}
	
	/**
	 * @return
	 */
	public boolean isTextUpdated()
	{
		return this.textField.isTextUpdated();
	}
	
	/**
	 * 
	 */
	public void resetText()
	{
		this.textField.resetText();
		return;
	}
	
	/**
	 * @return
	 */
	public String getBoxText()
	{
		return this.boxText;
	}
	
	
	
	/**
	 * @param boxText
	 */
	private void parseBoxText(String boxText)
	{
		
		LinkedList<Character> chars = new LinkedList<Character>();
		for (int i = 0; i < boxText.length(); i++) {
			chars.addLast(boxText.charAt(i));
		}
		
		//get the class name
		this.skipSpace(chars);
		if (chars.isEmpty()) {
			this.invalidate();
			this.requestUpdateDrawParameters();
			return;
		}
		
		String klazzName = this.parseNextToken(chars);
		this.skipSpace(chars);
		
		Vector<String> arguments = new Vector<String>();
		while(!chars.isEmpty()) {
			String s = this.parseNextToken(chars);
			arguments.add(s);
			this.skipSpace(chars);
		}
		
		ARClassInfo klazzInfo = ARClassDatabase.getSingleton().getARClassInfo(klazzName);
		if (klazzInfo == null || !klazzInfo.checkArguments(arguments)) {
			this.invalidate();
		}
		else {
			this.validate();
			this.setArguments(arguments);
			this.setARClassInfo(klazzInfo);
		}
		
		this.requestUpdateDrawParameters();
		return;
	}
	
	
	/**
	 * @return
	 */
	public String getErrorString()
	{
		
		if (this.isValid()) {
			return null;
		}

		
		
		LinkedList<Character> chars = new LinkedList<Character>();
		for (int i = 0; i < boxText.length(); i++) {
			chars.addLast(boxText.charAt(i));
		}
		
		//get the class name
		this.skipSpace(chars);
		if (chars.isEmpty()) {
			return "no object name is given.";
		}
		
		String klazzName = this.parseNextToken(chars);
		this.skipSpace(chars);
		
		Vector<String> arguments = new Vector<String>();
		while(!chars.isEmpty()) {
			String s = this.parseNextToken(chars);
			arguments.add(s);
			this.skipSpace(chars);
		}
		
		ARClassInfo klazzInfo = ARClassDatabase.getSingleton().getARClassInfo(klazzName);
		if (klazzInfo == null) {
			return "no such object exists: " + klazzName + ".";
		}
		
		return klazzInfo.getErrorStrings(arguments);
	
	}
	
	/**
	 * @param chars
	 * @return
	 */
	public String parseNextToken(LinkedList<Character> chars)
	{
		char c = chars.peekFirst();

		//is this a string token?
		if (c == '\"') {
			return parseString(chars);
		}
		
		//if not just parse as usual.
		StringBuffer buf = new StringBuffer();
		while(!chars.isEmpty()) {
			c = chars.peekFirst();
			if (Character.isWhitespace(c)) {
				break;
			}
			buf.append(c);
			chars.removeFirst();
		}
		
		return buf.toString();
	}
	
	/**
	 * @param chars
	 * @return
	 */
	public String parseString(LinkedList<Character> chars)
	{
		StringBuffer buf = new StringBuffer();
		
		//get the first ' " ' .
		buf.append(chars.removeFirst());

		if (chars.size() < 1) {
			return buf.toString();
		}

		
		while(chars.peekFirst() != '"') {
			
			char c = chars.removeFirst();
			//handle the escape sequence '\"' and '\\' (no other escape sequence is allowed)
			//in this version.
			if (c != '\\') {
				buf.append(c);
				if (chars.size() == 0) {
					break;
				}
				continue;
			}
			
			c = chars.removeFirst();
			if (c == '"') {
				buf.append("\"");
			}
			else if (c == '\\') {
				buf.append("\\");
			}
			else {
				buf.append(c);
			}

		}
		
		//get the last ' " ' .
		if (chars.size() != 0 && chars.peekFirst() == '"') {
			buf.append(chars.removeFirst());
		}
		
		return buf.toString();
	}
	
	
	/**
	 * @param chars
	 */
	public void skipSpace(LinkedList<Character> chars)
	{
		//skip the white space.
		
		while (!chars.isEmpty() && Character.isWhitespace(chars.peekFirst())){
			chars.removeFirst();
			if (chars.isEmpty()) {
				break;
			}
		}
		return;
	}
	
	/**
	 * 
	 */
	public int getNumOfInlets()
	{
		return this.numOfInlets;
	}
	
	/**
	 * @return
	 */
	public int getNumOfOutlets()
	{
		return this.numOfOutlets;
	}
	
	/**
	 * @return the scaledX
	 */
	public int getScaledX() {
		return scaledX;
	}

	/**
	 * @return the scaledY
	 */
	public int getScaledY() {
		return scaledY;
	}

	/**
	 * @return the scaledWidth
	 */
	public int getScaledWidth() {
		return scaledWidth;
	}

	/**
	 * @return the scaledHeight
	 */
	public int getScaledHeight() {
		return scaledHeight;
	}

	/**
	 * @return the scaledInletHeight
	 */
	public int getScaledInletHeight() {
		return scaledInletHeight;
	}

	/**
	 * @return the scaledInletWidth
	 */
	public int getScaledInletWidth() {
		return scaledInletWidth;
	}

	/**
	 * @return the scaledInletGap
	 */
	public int getScaledInletGap() {
		return scaledInletGap;
	}

	/**
	 * @return the scaledOutletWidth
	 */
	public int getScaledOutletWidth() {
		return scaledOutletWidth;
	}

	/**
	 * @return the scaledOutletHeight
	 */
	public int getScaledOutletHeight() {
		return scaledOutletHeight;
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
	
	/**
	 * @param px
	 * @param py
	 * @param pw
	 * @param ph
	 * @return
	 */
	public ObjectBoxOutletInfo getOutletAt(int px, int py, int pw, int ph)
	{
		if (!this.isValid()) {
			return null;
		}
		
		int boxX = px - this.getX();
		int boxY = py - this.getY();
		
		int outletX = 0;
		int outletY = this.getHeight() - DefaultOutletHeight;

		int outletWidth	 = DefaultOutletWidth;
		int outletHeight = DefaultOutletHeight;
		
		int numOfOulets = this.getNumOfOutlets();
		int outletGap	 = (int)Math.ceil(((double)this.getWidth() - numOfOulets * outletWidth) / (numOfOulets - 1) );
						
		for (int i = 0; i < numOfOulets; i++) {
			boolean collided = isCollided(	boxX, boxY, pw, ph,
											outletX, outletY, outletWidth, outletHeight);
			if (collided == true) {
				ObjectBoxOutletInfo outletInfo = new ObjectBoxOutletInfo();
				outletInfo.objectBox 	= this;
				outletInfo.outletNo 	= i;
				outletInfo.center		= this.getOutletPostion(i);
				return outletInfo;
			}
			
			outletX += outletWidth + outletGap;
		}
		
		return null;
	}
	
	

	
	/**
	 * @param outletNo
	 * @return
	 */
	public Point getOutletPostion(int outletNo)
	{
		int outletWidth	 = DefaultOutletWidth;
		
		
		int numOfOutlets  = this.getNumOfOutlets();
		double outletGap  = 0;
		if (numOfOutlets >= 2){
			outletGap = ((double)this.getWidth() - numOfOutlets * outletWidth) / (numOfOutlets - 1);
		}
		
		int ox = (int)Math.ceil((outletWidth + outletGap) * outletNo + outletWidth / 2.0 + this.getX());
		int oy = this.getHeight() + this.getY();

		return new Point(ox, oy);
	}
	
	/**
	 * @param px
	 * @param py
	 * @param pw
	 * @param ph
	 * @return
	 */
	public ObjectBoxInletInfo getInletAt(int px, int py, int pw, int ph)
	{
		if (!this.isValid()) {
			return null;
		}
		int boxX = px - this.getX();
		int boxY = py - this.getY();
		
		int inletX = 0;
		int inletY = 0;

		int inletWidth	= DefaultInletWidth;
		int inletHeight	= DefaultInletHeight;
		
		int numOfInlets = this.getNumOfInlets();
		int inletGap	= (int)Math.ceil(((double)this.getWidth() - numOfInlets * inletWidth) / (numOfInlets - 1) );
						
		for (int i = 0; i < numOfInlets; i++) {
			boolean collided = isCollided(	boxX, boxY, pw, ph,
											inletX, inletY, inletWidth, inletHeight);
			if (collided == true) {
				ObjectBoxInletInfo inletInfo = new ObjectBoxInletInfo();
				inletInfo.objectBox 	= this;
				inletInfo.inletNo 		= i;
				inletInfo.center		= this.getInletPosition(i);
				return inletInfo;
			}
			
			inletX += inletWidth + inletGap;
		}
		
		return null;
	}
	
	/**
	 * @param inletNo
	 * @return
	 */
	public Point getInletPosition(int inletNo)
	{
		int inletWidth	= DefaultInletWidth;
		
		
		int numOfInlets	= this.getNumOfInlets();
		
		double inletGap  = 0;
		if (numOfInlets >= 2){
			inletGap = ((double)this.getWidth() - numOfInlets * inletWidth) / (numOfInlets - 1);
		}

		int ix = (int)Math.ceil((inletWidth + inletGap) * inletNo + inletWidth / 2.0 + this.getX());
		int iy = this.getY();

		return new Point(ix, iy);
	}
	
	
	
	/**
	 * @param cx1
	 * @param cy1
	 * @param w1
	 * @param h1
	 * @param cx2
	 * @param cy2
	 * @param w2
	 * @param h2
	 * @return
	 */
	protected boolean isCollided(int px1, int py1, int w1, int h1,
								 int px2, int py2, int w2, int h2)
	{
		int cx1 = px1 + w1 / 2;
		int cy1 = py1 + h1 / 2;
		int cx2 = px2 + w2 / 2;
		int cy2 = py2 + h2 / 2;
		
		int distX = Math.abs(cx1 - cx2);
		int distY = Math.abs(cy1 - cy2);

		if (distX < (w1 + w2) / 2 && distY < (h1 + h2) / 2) {
			return true;
		}

		return false;
	}
	

	/**
	 * @return the scaledOutletGap
	 */
	public int getScaledOutletGap() {
		return scaledOutletGap;
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
		
		this.scaledWidth  = (int)Math.ceil(this.getWidth() * scale);
		this.scaledHeight = (int)Math.ceil(this.getHeight() * scale);
		
		//draw the text (class + arguments) inside the box.
		if (this.isValid() && this.editMode == false) {
			StringBuffer buf = new StringBuffer(this.getARClassInfo().getARClassName());
			if (arguments != null) {
				for (String arg: arguments) {
					buf.append(" ");
					buf.append(arg);
				}
			}
			this.boxText = buf.toString();
		}
		else {
			this.boxText = this.textField.getText();
		}

		//set up the font size
		this.scaledFontSize = GUIFont.getSingleton().getDefaultFontSize() * (scaledHeight / (double)DefaultHeight);
		
		Font f = GUIFont.getSingleton().getObjectBoxFont(scaledFontSize);
		g.setFont(f);
		
		//update the width.
		FontMetrics fm = g.getFontMetrics();
		Rectangle rect = fm.getStringBounds(boxText, g).getBounds();

		Rectangle hmargin = fm.getStringBounds("__", g).getBounds();
		this.textHorizontalMargin = hmargin.width;
				
		
		//adjust the size of the box to draw
		scaledWidth = rect.width + textHorizontalMargin * 2;
		double tmp = scaledWidth / scale;
		this.setWidth((int)Math.ceil(tmp));
	
		
		//inlets
		int numOfInlets = this.getNumOfInlets();
		
		this.scaledInletWidth  	= (int)Math.floor(DefaultInletWidth * scale);
		this.scaledInletHeight 	= (int)Math.floor(DefaultInletHeight * scale);
		this.scaledInletGap		= (int)Math.floor(DefaultInletGap * scale);

		if (numOfInlets > 0) {
			int widthRequiredByInlets = scaledInletWidth * numOfInlets + scaledInletGap * (numOfInlets - 1);
			if (scaledWidth < widthRequiredByInlets) {
				scaledWidth = widthRequiredByInlets;
				tmp = scaledWidth / scale;
				this.setWidth((int)Math.ceil(tmp));
			}

			//adjust the inlet gap.
			if (numOfInlets > 1) {
				scaledInletGap = (int)Math.ceil( ((double)scaledWidth - numOfInlets * scaledInletWidth) / (numOfInlets - 1) );
			}
		}
		//outlets
		int numOfOutlets = this.getNumOfOutlets();

		this.scaledOutletWidth  = (int)Math.floor(DefaultOutletWidth * scale);;
		this.scaledOutletHeight = (int)Math.floor(DefaultOutletHeight * scale);
		this.scaledOutletGap	= (int)Math.floor(DefaultOutletGap * scale);;

		if (numOfOutlets > 0) {

			int widthRequiredByOutlets = scaledOutletWidth * numOfOutlets + scaledOutletGap * (numOfOutlets - 1);
			if (scaledWidth < widthRequiredByOutlets) {
				scaledWidth = widthRequiredByOutlets;
				tmp = scaledWidth / scale;
				this.setWidth((int)Math.ceil(tmp));
			}		

			if (numOfOutlets > 1) {
				//adjust the outlet gap.
				scaledOutletGap = (int)Math.ceil( ((double)scaledWidth - numOfOutlets * scaledOutletWidth) / (numOfOutlets - 1) );
			}
		}
		
		
		this.scaledHeight += 2;
	
		this.scaleTheLastDrawCall = scale;
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
		
		int canvasX = this.scaledX - offsetX;
		int canvasY = this.scaledY - offsetY;
		
		//clear the background of the objectBox
		g.setColor(BackgroundColor);
		g.fillRect(canvasX, canvasY, scaledWidth, scaledHeight);

		
		if (editManager.isSelected(this)) {
			g.setColor(SelectedFontColor);
		}
		else {
			g.setColor(Color.BLACK);
		}
	
		Font f = GUIFont.getSingleton().getObjectBoxFont(scaledFontSize);
		g.setFont(f);
		
		if (editManager.isSelectedForEdit(this)) {
			this.textField.setLocation(canvasX + textHorizontalMargin + 1 , canvasY + scaledInletHeight + 2);
			this.textField.setFont(f);
			this.textField.setBorder(null);
			this.textField.setSize(this.getScaledWidth() - textHorizontalMargin * 2 + 2, (int)Math.ceil(scaledHeight * 0.75) - 2);
			this.textField.setVisible(true);
		}
		else {
			g.drawString(boxText, canvasX + textHorizontalMargin + 1, (int)Math.ceil(canvasY + (scaledHeight * 0.75)) - 1);
			this.textField.setVisible(false);
		}
		
		if (!isValid()) {
			if (!editManager.isSelectedForEdit(this)) {
				g.setColor(InvalidLineColor);
			}
			else {
				g.setColor(SelectedLineColor);
			}
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
		
		
		//draw the inlets
		int numOfInlets = this.getNumOfInlets();
		int inletX = canvasX;
		
		if (numOfInlets > 0) {
			g.drawRect(canvasX, canvasY, scaledInletWidth, scaledInletHeight);	
			for (int i = 1; i < numOfInlets - 1; i++) {
				inletX += scaledInletWidth + scaledInletGap;
				g.drawRect(inletX, canvasY, scaledInletWidth, scaledInletHeight);
			
			}
		
			if (numOfInlets > 1) {
				g.drawRect(canvasX + scaledWidth - scaledInletWidth, canvasY, scaledInletWidth, scaledInletHeight);	
			}
		}
		
		
		//draw the outlets
		int numOfOutlets = this.getNumOfOutlets();
		int outletX = canvasX;
		
		if (numOfOutlets > 0) {
			g.drawRect(canvasX, canvasY + scaledHeight - scaledOutletHeight, scaledOutletWidth, scaledOutletHeight);
			for (int i = 1; i < numOfOutlets - 1; i++) {
				outletX += scaledOutletWidth + scaledOutletGap;
				g.drawRect(outletX, canvasY + scaledHeight - scaledOutletHeight, scaledOutletWidth, scaledOutletHeight);
			}

			if (numOfOutlets > 1) {
				g.drawRect(canvasX + scaledWidth - scaledOutletWidth, canvasY + scaledHeight - scaledOutletHeight, scaledOutletWidth, scaledOutletHeight);
			}
		}
		//the draw the entire rectangle
		g.drawRect(canvasX, canvasY, scaledWidth, scaledHeight);

		
		return;
	}


	
	/**
	 * 
	 */
	public void requestUpdateDrawParameters()
	{
		this.scaleTheLastDrawCall = 0.0;
	}



	@Override
	public void undoableEditHappened(UndoableEditEvent e) 
	{	
		Component c = (Component)this.textField.getParent();
		if (c != null) {
			c.repaint();
		}

		this.requestUpdateDrawParameters();
	}

	
}
