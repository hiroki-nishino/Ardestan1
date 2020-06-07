package org.ardestan.gui.dialog.port;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.ardestan.json.JsonBoard;
import org.ardestan.json.JsonPort;

public class SelectPortDialog extends JDialog implements ActionListener 
{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected JComboBox<String> cbSerialPorts;

	protected String selectedPortName;
	protected String selectedFQBN;
	protected String selectedBoardName;
	
	protected JButton okButton;
	protected JButton cancelButton;
	
	protected boolean canceled;
	
	
	protected Vector<PortInfo> vctPortInfo;
	/**
	 * 
	 */
	public SelectPortDialog(JsonPort[] ports, String currentPortName, String currentFQBN)
	{
		//translate the port information for the combobox.
		Vector<String> portInfoStrings = new Vector<String>();

		int idx = 0;
		int selectedIndex = 0;
		vctPortInfo = new Vector<PortInfo>();
		for (JsonPort p: ports) {
			if (p.address.equals(currentPortName)) {
				selectedIndex = idx;
			}
			if (p.boards == null) {
				PortInfo i = new PortInfo();
				i.portName = p.address;
				vctPortInfo.add(i);
				
				portInfoStrings.add(i.portName);
				idx++;
				continue;
			}
			
			for (JsonBoard b: p.boards) {
				PortInfo i = new PortInfo();
				i.portName  = p.address;
				i.boardName = b.name;
				i.fqbn		= b.FQBN;
				vctPortInfo.add(i);				
				portInfoStrings.add(i.portName + " (" + i.boardName + ", FQBN=" + b.FQBN + ")");
				if (p.address.equals(currentPortName) && i.fqbn.equals(currentFQBN)) {
					selectedIndex = idx;
				}
				idx++;				
			}
		}
		
		//build UI
		//create the installed boards panel
		JPanel portPanel = new JPanel();
		portPanel.setBorder(BorderFactory.createTitledBorder("Port(s)"));
		portPanel.setLayout(new GridBagLayout());

		cbSerialPorts = new JComboBox<String>(portInfoStrings);
		cbSerialPorts.setSelectedIndex(selectedIndex);
		
		portPanel.add(cbSerialPorts);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		okButton = new JButton("OK");
		cancelButton = new JButton("cancel");
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(portPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		this.add(mainPanel);
		
		//reset the states
		canceled = true;
		selectedPortName = null;
		
		this.setTitle("Select the Arduino port");
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
	public String getSelectedPortName()
	{
		return this.selectedPortName;
	}
	
	/**
	 * @return
	 */
	public String getSelectedFQBN()
	{
		return this.selectedFQBN;
	}
	
	/**
	 * @return
	 */
	public String getSelectedBoardName()
	{
		return this.selectedBoardName;
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
		PortInfo info = vctPortInfo.get(cbSerialPorts.getSelectedIndex());
		this.selectedPortName 	= info.portName;
		this.selectedFQBN		= info.fqbn;
		this.selectedBoardName	= info.boardName;
		this.setVisible(false);		
	}
	
	
	/**
	 * @author hnishino
	 *
	 */
	protected class PortInfo
	{
		public String portName 	= null;
		public String boardName	= null;
		public String fqbn		= null;
	}
	

}
