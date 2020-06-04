package org.ardestan.gui.dialog.library;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ardestan.arduinocli.ArduinoCLI;
import org.ardestan.arduinocli.ArduinoCLIBackground;
import org.ardestan.gui.Message;
import org.ardestan.json.JsonLibraryListItem;
import org.ardestan.misc.ProjectSetting;

/**
 * @author hiroki
 *
 */
public class LibraryUninstallerUpgraderDialogPanel extends JPanel implements ActionListener, LibraryInstallationListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JPanel			pnlInstalledLibraries	;
	protected JComboBox<String>	cbInstalledLibraries	;	
	
	protected JPanel		pnlButtons;
	protected JButton		btnUninstall;
	protected JButton		btnClose;
		
	protected JDialog 		parent;
	
	protected JsonLibraryListItem[] installedLibraries;
	
	protected LibraryManagerMessageWindow messageWindow;

	
	/**
	 * 
	 */
	public LibraryUninstallerUpgraderDialogPanel(JDialog parent)
	{
		//keep to close
		this.parent = parent;

		//create the installed boards panel
		pnlInstalledLibraries = new JPanel();
		pnlInstalledLibraries.setBorder(BorderFactory.createTitledBorder("Installed Libraries"));
		pnlInstalledLibraries.setLayout(new FlowLayout());

		this.refreshComboBox();
		
		
		//create the close button panel
		btnUninstall = new JButton("Uninstall");
		btnClose 	= new JButton("Close");

		pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlButtons.add(btnUninstall);
		pnlButtons.add(btnClose);
		
		this.add(pnlButtons, BorderLayout.SOUTH);

		//create the entire panel
		this.setLayout(new BorderLayout());
		this.add(pnlInstalledLibraries, BorderLayout.NORTH);
		this.add(pnlButtons, BorderLayout.SOUTH);
		
		
		//setting up listeners
		btnUninstall.addActionListener(this);
		btnClose.addActionListener(this);
		
		return;
	}

	
	/**
	 * 
	 */
	public void refreshComboBox()
	{
		
		try {
			File dir = ProjectSetting.getSingleton().getProjectDirectory();
			ArduinoCLI cli = ArduinoCLI.getSingleton();
			cli.copyCommandTo(dir);
			installedLibraries = cli.getInstalledLibraries(dir);
			cli.deleteCommand(dir);
		}
		catch(IOException e) {
			Message.println(e);
			return;
		}	
		
		this.pnlInstalledLibraries.removeAll();
		
		if (installedLibraries == null) {
			JLabel l = new JLabel("No libraries installed.");
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add(l,  BorderLayout.CENTER);
			pnlInstalledLibraries.add(p, BorderLayout.CENTER);
			return;
		}

		Vector<String> libraryNames = new Vector<String>();
		for (JsonLibraryListItem item: installedLibraries) {
			String name = item.library.name;
			if (item.library.version != null) {
				name += " (version=" + item.library.version + ")";
			}	
			libraryNames.add(name);
		}
		
	
		cbInstalledLibraries = new JComboBox<String>(libraryNames);
		pnlInstalledLibraries.add(cbInstalledLibraries);

		this.revalidate();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
	
		if (e.getSource() == btnClose) {
			parent.setVisible(false);
			return;
		}
		
		if (e.getSource() == btnUninstall) {
			this.uninstallLibrary();
		}
	
		return;
	}
	
	/**
	 * 
	 */
	public void uninstallLibrary()
	{
		int idx = cbInstalledLibraries.getSelectedIndex();
		if (idx < 0) {
			JOptionPane.showMessageDialog(this.parent, "No library is selected for uninstallation.");
			return;
		}
		parent.setEnabled(false);
		
		messageWindow = new LibraryManagerMessageWindow("Uninstalling ...");

		int w = 200;
		int h = 25;
		int x = parent.getX() + parent.getWidth() / 2 - w / 2;
		int y = parent.getY() + parent.getHeight() / 2 - h / 2;
		messageWindow.setBounds(x,  y, w, h);
		messageWindow.setVisible(true);
		
		String id = installedLibraries[idx].library.name;
		
		File path = new File(System.getProperty("java.io.tmpdir"));

		boolean ret = ArduinoCLIBackground.getSingleton().uninstallLibrary(path, id, this);
		if (ret == false) {
			messageWindow.setVisible(false);
			parent.setEnabled(true);
		}
		return;
	}
	




	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#finishedUninstallation()
	 */
	public void finishedUninstallation() 
	{
		messageWindow.setVisible(false);
		
		JOptionPane.showMessageDialog(this.parent, "The library was successfully uninstalled.");
		this.refreshComboBox();
		
		parent.setEnabled(true);
		return;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#failedUninstallation()
	 */
	public void failedUninstallation() 
	{
		messageWindow.setVisible(false);
		JOptionPane.showMessageDialog(this.parent, "An error occured during the uninstallation.");
		parent.setEnabled(true);
		return;
	}


	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#finishedInstallation()
	 */
	@Override
	public void finishedInstallation() {
	}


	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#failedInstallation()
	 */
	@Override
	public void failedInstallation() {
	}


	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#finishedUpgrading()
	 */
	@Override
	public void finishedUpgrading() {
		messageWindow.setVisible(false);
		
		JOptionPane.showMessageDialog(this.parent, "The library was successfully upgraded.");
		this.refreshComboBox();
		
		parent.setEnabled(true);
		return;
	}


	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#failedUpgrading()
	 */
	@Override
	public void failedUpgrading() {
		messageWindow.setVisible(false);
		JOptionPane.showMessageDialog(this.parent, "An error occured during upgrading.");
		parent.setEnabled(true);

	}
	
	
}
