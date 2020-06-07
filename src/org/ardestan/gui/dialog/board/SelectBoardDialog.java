package org.ardestan.gui.dialog.board;

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

public class SelectBoardDialog extends JDialog implements ActionListener 
{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected JComboBox<String> cbBoardNames;

	protected String selectedBoardName;
	protected int	 selectedIndex;
	
	protected JButton okButton;
	protected JButton cancelButton;
	
	protected boolean canceled;
	
	/**
	 * 
	 */
	public SelectBoardDialog(Vector<String> boardNames, int selected)
	{
		//build UI
		
		//create the installed boards panel
		JPanel boardPanel = new JPanel();
		boardPanel.setBorder(BorderFactory.createTitledBorder("Board(s)"));
		boardPanel.setLayout(new GridBagLayout());

		cbBoardNames = new JComboBox<String>(boardNames);
		cbBoardNames.setSelectedIndex(selected);
		
		boardPanel.add(cbBoardNames);
		
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
		mainPanel.add(boardPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		this.add(mainPanel);
		
		//reset the states
		canceled = true;
		selectedBoardName = null;
		
		this.setTitle("Select the Arduino device");
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
	public int getSelectedIndex()
	{
		return this.selectedIndex;
	}
	
	/**
	 * @return
	 */
	public String getSelectedBoardName()
	{
		return this.selectedBoardName;
	}
	
	/**
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			this.setVisible(false);		
			return;
		}
		
		canceled = false;
		this.selectedIndex = cbBoardNames.getSelectedIndex();
		this.selectedBoardName = (String)cbBoardNames.getSelectedItem();
		this.setVisible(false);		
	}

}
