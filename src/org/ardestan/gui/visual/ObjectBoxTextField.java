package org.ardestan.gui.visual;

import javax.swing.JTextField;

/**
 * @author hiroki
 *
 */
public class ObjectBoxTextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ObjectBox 	objectBox = null;
	private String		textBeforeEdit;
	
	
	public ObjectBoxTextField(){
		this.enableInputMethods(false);
	}
	/**
	 * @param objectBox
	 */
	public void setObjectBox(ObjectBox objectBox)
	{
		this.objectBox = objectBox;
	}
	
	/**
	 * @return
	 */
	public ObjectBox getObjectBox()
	{
		return this.objectBox;
	}
	
	
	/**
	 * 
	 */
	public void updateTextBeforeEdit()
	{
		textBeforeEdit = this.getText();
	}
	
	/**
	 * @return
	 */
	public String getTextBeforeEdit()
	{
		return this.textBeforeEdit;
	}
	
	/**
	 * @return
	 */
	public boolean isTextUpdated()
	{
		return !this.getText().equals(this.textBeforeEdit);
	}
	
	/**
	 * 
	 */
	public void resetText()
	{
		this.setText(this.textBeforeEdit);
		return;
	}
}
