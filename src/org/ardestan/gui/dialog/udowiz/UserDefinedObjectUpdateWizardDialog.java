package org.ardestan.gui.dialog.udowiz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;

import org.ardestan.arclass.ARClassInfoNative;
import org.ardestan.gui.ArdestanIDE;
import org.ardestan.gui.MainWindow;
import org.ardestan.gui.Message;
import org.ardestan.json.JsonClassInfoLoader;

/**
 * @author hnishino
 *
 */
public class UserDefinedObjectUpdateWizardDialog extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected UserDefinedObjectInfoPanel 	panel;
	
	protected boolean						canceled;
	protected File							oldAudFile;
	protected ARClassInfoNative 			oldInfo;
	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public UserDefinedObjectUpdateWizardDialog(File oldAudFile, int x, int y, int w, int h) throws IOException
	{ 
		this.oldAudFile = oldAudFile;
		this.oldInfo = JsonClassInfoLoader.loadSingleClassInfoFromFile(oldAudFile);
				
		this.setLocation(x, y);
		this.setSize(w, h);
		
		this.setModal(true);
		this.setTitle("User-defined Object Wizard");
	
		
		panel = new UserDefinedObjectInfoPanel(this);
		
		panel.setClassInfo(this.oldInfo);
		
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
			ARClassInfoNative newInfo = panel.getARClassInfoNative();

			try {
				panel.updateUserDefinedObjectDefinitionFile(oldAudFile, oldInfo, newInfo);
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			this.setVisible(false);
			
			MainWindow window = ArdestanIDE.getMainWindow();
			
			window.removePane(oldInfo.getHeaderFilenameWithoutPath());
			window.removePane(oldInfo.getCppFilenameWithoutPath());
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
