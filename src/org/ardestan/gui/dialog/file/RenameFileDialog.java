package org.ardestan.gui.dialog.file;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ardestan.misc.ARNameVerifier;

/**
 * @author hnishino
 *
 */
public class RenameFileDialog extends JDialog implements ActionListener, DocumentListener 
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected JTextField tfFilename;
	
	protected JButton 	okButton;
	protected JButton 	cancelButton;
	
	protected boolean 	canceled;
	
	protected String  	newFilename;
	
	/**
	 * 
	 */
	public RenameFileDialog(String originalFilename)
	{
		
		tfFilename = new JTextField();
		tfFilename.setColumns(50);
		tfFilename.setText(originalFilename);
		tfFilename.addActionListener(this);
		tfFilename.getDocument().addDocumentListener(this);
		
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
		newFilename = null;
		
		this.setTitle("Rename");
		this.setModal(true);
		
		this.checkValidity();
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
	public String getNewFilename()
	{
		return this.newFilename;
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
		this.newFilename = tfFilename.getText();
		this.setVisible(false);		
	}

}
