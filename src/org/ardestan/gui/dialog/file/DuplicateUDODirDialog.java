package org.ardestan.gui.dialog.file;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ardestan.misc.ARNameVerifier;
import org.ardestan.misc.ProjectSetting;


/**
 * @author hnishino
 *
 */
public class DuplicateUDODirDialog extends JDialog implements ActionListener, DocumentListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected JTextField tfObjectName;
	
	protected JButton 	okButton;
	protected JButton 	cancelButton;
	
	protected boolean 	canceled;
	
	protected String  	dupObjectName;
	
	
	/**
	 * 
	 */
	public DuplicateUDODirDialog(String originalObjectName)
	{
		
		File projectDir = ProjectSetting.getSingleton().getProjectDirectory();
				
		int idx = 2;
		String newObjectName = null;
		while(true) {
			String tmp = originalObjectName + "_" + idx; 
			if (new File(projectDir, tmp).exists() == false) {
				newObjectName = tmp;
				break;
			}
			idx++;
		}
		
		tfObjectName = new JTextField();
		tfObjectName.setColumns(50);
		tfObjectName.setText(newObjectName);
		tfObjectName.addActionListener(this);
		tfObjectName.getDocument().addDocumentListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		okButton = new JButton("OK");
		cancelButton = new JButton("cancel");
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		this.checkValidity();
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder("Input a New User-defined Object Name"));

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(tfObjectName, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		this.add(mainPanel);
		
		//reset the states
		canceled = true;
		dupObjectName = null;
		
		this.setTitle("Duplicate");
		this.setModal(true);
		
		this.checkValidity();
	}

	/**
	 * 
	 */
	protected boolean checkValidity()
	{
		okButton.setEnabled(false);
		
		String objectName = tfObjectName.getText().trim();
		if (ARNameVerifier.isValidObjectName(objectName)) {
			okButton.setEnabled(true);
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return
	 */
	public boolean isCanceled()
	{
		return this.canceled;
	}
	
	/**
	 * @return
	 */
	public String getDupObjectName()
	{
		return this.dupObjectName;
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		checkValidity();
	}


	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		checkValidity();
	}


	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		checkValidity();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			this.setVisible(false);		
			return;
		}
		
		if (!checkValidity()) {
			return;
		}
		
		canceled = false;
		this.dupObjectName = tfObjectName.getText().trim();
		this.setVisible(false);		
	}

}
