package org.ardestan.gui.dialog.library;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * @author hiroki
 *
 */
public class LibraryManagerVersionDialog extends JDialog implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final static String LATEST = "Latest";
	
	protected JButton btnInstall;
	protected JButton btnCancel;

	protected JPanel  pnlVersion;
	protected JComboBox<String> cbVersions;
	
	protected String selectedVersion = null;
	/**
	 * 
	 */
	public LibraryManagerVersionDialog(String libraryName, Vector<String> versions) 
	{
		
		pnlVersion = new JPanel();
		pnlVersion.setLayout(new BorderLayout());
		pnlVersion.setBorder(BorderFactory.createTitledBorder("Select the version"));

		Vector<String> cbStrings = new Vector<String>();
		cbStrings.add(LATEST);
		for (int i = versions.size() - 1; i >= 0; i--) {
			cbStrings.add(versions.get(i));
		}

		cbVersions = new JComboBox<String>(cbStrings);
		pnlVersion.add(new JLabel(libraryName), BorderLayout.WEST);
		
		pnlVersion.add(cbVersions, BorderLayout.CENTER);
		cbVersions.setSelectedIndex(0);
		
		btnInstall = new JButton("Install");
		btnCancel  =  new JButton("Cancel");
		
		btnInstall.addActionListener(this);
		btnCancel .addActionListener(this);
		
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlButtons.add(btnInstall);
		pnlButtons.add(btnCancel);
		
		this.setLayout(new BorderLayout());
		this.add(pnlVersion, BorderLayout.NORTH);
		this.add(pnlButtons, BorderLayout.SOUTH);
		
		this.setAlwaysOnTop(true);
		this.setModal(true);		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == btnInstall) {
			this.selectedVersion = (String)cbVersions.getSelectedItem();
		}
		
		this.setVisible(false);
	}
	
	/**
	 * @return
	 */
	public String getSelectedVersion()
	{
		return this.selectedVersion;
	}
	
	
	
}
