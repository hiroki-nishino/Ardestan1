package org.ardestan.gui.dialog.udowiz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JDialog;

import org.ardestan.arclass.ARClassInfoNative;
import org.ardestan.gui.Message;

/**
 * @author hnishino
 *
 */
public class UserDefinedObjectCreationWizardDialog extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	UserDefinedObjectInfoPanel panel;
	
	protected boolean		canceled;
	
	
	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public UserDefinedObjectCreationWizardDialog(int x, int y, int w, int h)
	{ 
		this.setLocation(x, y);
		this.setSize(w, h);
		
		this.setModal(true);
		this.setTitle("User-defined Object Wizard");
	
		
		panel = new UserDefinedObjectInfoPanel(this);
		
		this.getContentPane().add(panel);
		
		
		this.canceled = false;
		this.setModal(true);		
	}

	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (panel.isOkayButton(e.getSource())) {
			this.canceled = false;
			ARClassInfoNative info = panel.getARClassInfoNative();

			try {
				panel.createNewUserDefinedObjectDirectory(info);
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			this.setVisible(false);
		}
		else if (panel.isCancelButton(e.getSource())) {
			this.canceled = true;
			this.setVisible(false);
		}	
	
		return;
	}

	/**
	 * @return
	 */
	public boolean isCanceled() {
		return this.canceled;
	}

}
