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
public class DuplicateFileDialog extends JDialog implements DocumentListener, ActionListener 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected JTextField tfFilename;
	
	protected JButton 	okButton;
	protected JButton 	cancelButton;
	
	protected boolean 	canceled;
	
	protected String  	dupFilename;
	
	
	/**
	 * 
	 */
	public DuplicateFileDialog(String originalFilename)
	{
		
		File projectDir = ProjectSetting.getSingleton().getProjectDirectory();
		
		String beforeExt = originalFilename.substring(0, originalFilename.lastIndexOf('.') );
		String ext		 = originalFilename.substring(originalFilename.lastIndexOf('.'));
		
		int idx = 2;
		String newFilenameCandidate = null;
		while(true) {
			String tmp = beforeExt + "_" + idx +  ext; 
			if (new File(projectDir, tmp).exists() == false) {
				newFilenameCandidate = tmp;
				break;
			}
			idx++;
		}
		
		tfFilename = new JTextField();
		tfFilename.setColumns(50);
		tfFilename.setText(newFilenameCandidate);
		tfFilename.getDocument().addDocumentListener(this);
		tfFilename.addActionListener(this);

		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		okButton = new JButton("OK");
		cancelButton = new JButton("cancel");
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder("Input a New Filename"));

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(tfFilename, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		this.add(mainPanel);
		
		//reset the states
		canceled = true;
		dupFilename = null;
		
		this.setTitle("Duplicate");
		this.setModal(true);
		
		this.checkValidity();
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
	public String getDupFilename()
	{
		return this.dupFilename;
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


	/**
	 * 
	 */
	protected boolean checkValidity()
	{
		okButton.setEnabled(false);
		
		String filename = tfFilename.getText().trim();
		if (ARNameVerifier.isValidObjectName(filename) || ARNameVerifier.isValidArdFilename(filename) || 
			ARNameVerifier.isValidArhFilename(filename)) {
			okButton.setEnabled(true);
			return true;
		}
		
		return false;
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
		
		if (checkValidity() == false) {
			return;
		}
		
				
		canceled = false;
		this.dupFilename = tfFilename.getText().trim();
		this.setVisible(false);		
	}

}
