package org.ardestan.gui.dialog.commentbox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.ardestan.gui.visual.CommentBox;
import org.ardestan.gui.visual.GUIFont;

public class CommentBoxDialog extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	protected CommentBox oldCommentBox;
	
	protected String	comment	;
	protected String	fontName;
	protected double	fontSize;
	protected Color		color	;
	
	JComboBox<String>	cbFontNames;
	
	JButton 			btnOk;
	JButton 			btnCancel;
	JComboBox<String>	cbColors;
	JTextField			tfFontSize;
	
	JComboBox<Integer>	cbRed;
	JComboBox<Integer>	cbGreen;
	JComboBox<Integer>	cbBlue;
	
	protected JScrollPane 	scrollPane	;
	protected JEditorPane 	commentEditor	;	
	
	boolean canceled;
	
	protected ColorInfo[] 	colors;
	
	/**
	 * @param b
	 */
	/**
	 * @return
	 */
	public CommentBoxDialog(CommentBox b)
	{
		this.oldCommentBox = b.clone();
		this.canceled = true;
		
		this.comment 	= b.getComment();
		this.fontSize	= b.getFontSize();
		this.color		= b.getColor();
		
		this.btnOk 		= new JButton("ok");
		this.btnCancel	= new JButton("cancel");
		
		this.btnOk.addActionListener(this);
		this.btnCancel.addActionListener(this);
		
		this.tfFontSize = new JTextField(5);
		this.tfFontSize.setHorizontalAlignment(JTextField.RIGHT);
		this.tfFontSize.addActionListener(this);
		this.tfFontSize.setText(this.fontSize + "");
		
		colors = new ColorInfo[] {	
				new ColorInfo("user defined", Color.BLACK),
				new ColorInfo("black"		, Color.BLACK),
				new ColorInfo("blue"		, Color.BLUE),
				new ColorInfo("cyan"		, Color.CYAN),
				new ColorInfo("dark gray"	, Color.DARK_GRAY),
				new ColorInfo("gray"		, Color.GRAY),
				new ColorInfo("green"		, Color.GREEN),
				new ColorInfo("light gray"	, Color.LIGHT_GRAY),
				new ColorInfo("magent"		, Color.MAGENTA),
				new ColorInfo("orange"		, Color.ORANGE),
				new ColorInfo("pink"		, Color.PINK),
				new ColorInfo("white"		, Color.WHITE),
				new ColorInfo("yellow"		, Color.YELLOW)
		};
		
		Vector<String> colorName = new Vector<String>();
		for (ColorInfo i: colors) {
			colorName.add(i.getColorName());
		}
		
		Integer[] values = new Integer[256];
		for (int i = 0; i < values.length; i++) {
			values[i] = i;
		}
		
		cbRed	= new JComboBox<Integer>(values);
		cbGreen = new JComboBox<Integer>(values);
		cbBlue	= new JComboBox<Integer>(values);
		
		cbRed	.setSelectedIndex(b.getColor().getRed()		);
		cbGreen	.setSelectedIndex(b.getColor().getGreen()	);
		cbBlue	.setSelectedIndex(b.getColor().getBlue()	);
		
		cbRed	.addActionListener(this);
		cbGreen	.addActionListener(this);
		cbBlue	.addActionListener(this);
		
		cbColors = new JComboBox<String>(colorName);
		cbColors.addActionListener(this);
		
		Font [] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		Vector<String> fontNames = new Vector<String>();
		for(Font f: fonts) {
			fontNames.add(f.getName());
		}
		cbFontNames = new JComboBox<String>(fontNames);
		for (int i = 0; i < fontNames.size(); i++) {
			if (fontNames.get(i).equals(b.getFontName())){
				cbFontNames.setSelectedIndex(i);
				break;
			}
		}
		
		JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		r1.add(new JLabel("font name"));
		r1.add(cbFontNames);
		r1.add(new JLabel("size:"));
		r1.add(tfFontSize);

		JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		r2.add(new JLabel("color:"));
		r2.add(cbColors);
		
		r2.add(new JLabel("red:"));
		r2.add(cbRed);
		r2.add(new JLabel("green:"));
		r2.add(cbGreen);
		r2.add(new JLabel("blue:"));
		r2.add(cbBlue);
		
		JPanel r3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		r3.add(btnOk);
		r3.add(btnCancel);

		JPanel p = new JPanel();
		GridLayout gl = new GridLayout(3, 1, 0, 0);
		p.setLayout(gl);
		p.add(r1);
		p.add(r2);
		p.add(r3);

		this.commentEditor = new JEditorPane();
		this.commentEditor.setFont(GUIFont.getSingleton().getObjectBoxFont());
		
		this.scrollPane =  new JScrollPane(	this.commentEditor, 
											JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		
		this.setLayout(new BorderLayout());
		this.add(this.scrollPane, BorderLayout.CENTER);
		this.add(p, BorderLayout.SOUTH);
		
		this.setTitle("Comment Box");
		this.setModal(true);
		
		
		this.commentEditor.setText(this.comment);
		
		this.updateColorNameComboBox();		

		


		return;
	}
	
	/**
	 * 
	 */
	public void setRGBComboBoxes(int r, int g, int b)
	{
		cbRed	.setSelectedIndex(r);
		cbGreen	.setSelectedIndex(g);
		cbBlue	.setSelectedIndex(b);
	
		return;
	}
	
	/**
	 * @return
	 */
	public String getFontName()
	{
		return this.fontName;
	}
	/**
	 * 
	 */
	public void updateColorNameComboBox()
	{
		Color c = new Color(cbRed.getSelectedIndex(), cbGreen.getSelectedIndex(), cbBlue.getSelectedIndex());
		cbColors.setSelectedIndex(0);
		for (int i = 1; i < colors.length; i++){
			if (colors[i].getColor().equals(c)) {
				cbColors.setSelectedIndex(i);
				break;
			}
		}
	}
	
	/**
	 * @return
	 */
	public boolean isCanceled()
	{
		return canceled;
	}
	
	/**
	 * @return
	 */
	public String getComment()
	{
		return this.comment;
	}
	
	/**
	 * @return
	 */
	public Color getColor()
	{
		return this.color;
	}
	
	/**
	 * @return
	 */
	public double getFontSize()
	{
		return this.fontSize;
	}
	
	/**
	 * @return
	 */
	public boolean isChanged()
	{
		
		if (oldCommentBox.getComment().equals(comment) == false) {
			return false;
		}
		
		if (oldCommentBox.getFontSize() != fontSize) {
			return false;
		}
		
		if (oldCommentBox.getColor().equals(color) == false) {
			return false;
		}
		
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object src = e.getSource();
		
		if (src == cbColors) {
			int i = cbColors.getSelectedIndex();
			if (i != 0) {
				Color c = colors[i].color;
				setRGBComboBoxes(c.getRed(), c.getGreen(), c.getBlue());
			}
			return;
		}
		
		if (src == cbRed || src == cbGreen || src == cbBlue) {
			updateColorNameComboBox();
			return;
		}
		
		if (src == btnCancel) {
			this.setVisible(false);
			return;
		}

		String fs = tfFontSize.getText();
		double fontSize = 0.0;
		try {
			fontSize = Double.parseDouble(fs);
			Font font = GUIFont.getSingleton().getObjectBoxFont(fontSize);
			tfFontSize.setText(font.getSize() + "");
			if (fontSize != font.getSize()) {
				JOptionPane.showMessageDialog(this, "The font size is adjusted to the nearest available size.", "Warning",JOptionPane.OK_OPTION);				
			}
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showConfirmDialog(this, "an invalid font size is given.", "Warning",JOptionPane.OK_OPTION);
			tfFontSize.setText(this.oldCommentBox.getFontSize() + "");
			return;
		}
		
		if (src == btnOk) {
			this.canceled = false;
			this.comment 	= this.commentEditor.getText();
			this.fontName	= (String)this.cbFontNames.getSelectedItem();
			this.fontSize 	= (fontSize <= 0.0 ? GUIFont.DefaultFontSize : fontSize);
			this.color		= new Color(cbRed.getSelectedIndex(), cbGreen.getSelectedIndex(), cbBlue.getSelectedIndex());
			this.setVisible(false);
			return;
		}
	}
	
	/**
	 * @author hiroki
	 *
	 */
	class ColorInfo
	{
		protected String 	colorName;
		protected Color		color;

		/**
		 * @param colorName
		 * @param color
		 */
		public ColorInfo(String colorName, Color color)
		{
			this.colorName 	= colorName;
			this.color		= color;
		}

		/**
		 * @return
		 */
		public String getColorName() {
			return colorName;
		}

		/**
		 * @return
		 */
		public Color getColor() {
			return color;
		}
	}


	

}
