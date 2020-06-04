package org.ardestan.gui.dialog.project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;


public class CompileOptionDialog extends JDialog implements ActionListener 
{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected JCheckBox cbConnections;
	protected JCheckBox cbOutlets;

	protected boolean usePROGMEMforConnections;
	protected boolean usePROGMEMforOutlets;
	
	protected JButton okButton;
	protected JButton cancelButton;
	
	protected boolean canceled;
	
	
	/**
	 * 
	 */
	public CompileOptionDialog(boolean usePROGMEMforOutlets, boolean usePROGMEMforConnections)
	{

		//build UI		
		JPanel checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		checkboxPanel.setBorder(BorderFactory.createTitledBorder("PROGMEM"));
		
		cbOutlets		= new JCheckBox("Use PROGMEM for outlets");
		cbConnections 	= new JCheckBox("Use PROGMEM for connections");

		checkboxPanel.add(cbOutlets);
		checkboxPanel.add(cbConnections);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		cbOutlets.setSelected(usePROGMEMforOutlets);
		cbConnections.setSelected(usePROGMEMforConnections);
		
		okButton = new JButton("OK");
		cancelButton = new JButton("cancel");
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(checkboxPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		this.add(mainPanel);
		
		//reset the states
		canceled = true;
		
		this.setTitle("Compile Options");
		this.setModal(true);
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
	public boolean getUsePROGMEMforConnections()
	{
		return this.usePROGMEMforConnections;
	}
	
	/**
	 * @return
	 */
	public boolean getUsePROGMEMforOutlets()
	{
		return this.usePROGMEMforOutlets;
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
		
		canceled = false;
		this.usePROGMEMforConnections 	= cbConnections.isSelected();
		this.usePROGMEMforOutlets		= cbOutlets.isSelected();
		this.setVisible(false);		
	}

	

}
