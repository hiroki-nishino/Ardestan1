package org.ardestan.gui.dialog.board;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import org.ardestan.json.JsonCoreListItem;
import org.ardestan.misc.ProjectSetting;

/**
 * @author hiroki
 *
 */
public class BoardUninstallerUpgraderDialogPanel extends JPanel implements ActionListener, CoreInstallationListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JPanel			pnlInstalledBoards	;
	protected JComboBox<String>	cbInstalledBoards	;	
	
	protected JPanel		pnlButtons;
	protected JButton		btnUninstall;
	protected JButton		btnUpgrade;
	protected JButton		btnClose;
		
	protected JDialog 		parent;
	
	protected JsonCoreListItem[] installedBoards;
	
	protected BoardManagerMessageWindow messageWindow;

	
	/**
	 * 
	 */
	public BoardUninstallerUpgraderDialogPanel(JDialog parent)
	{
		//keep to close
		this.parent = parent;

		//create the installed boards panel
		pnlInstalledBoards = new JPanel();
		pnlInstalledBoards.setBorder(BorderFactory.createTitledBorder("Installed Boards"));
		pnlInstalledBoards.setLayout(new FlowLayout());
		
		this.refreshComboBox();
		
		
		//create the close button panel
		btnUpgrade	= new JButton("Upgrade");
		btnUninstall = new JButton("Uninstall");
		btnClose 	= new JButton("Close");

		pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlButtons.add(btnUpgrade);
		pnlButtons.add(btnUninstall);
		pnlButtons.add(btnClose);
		
		this.add(pnlButtons, BorderLayout.SOUTH);

		//create the entire panel
		this.setLayout(new BorderLayout());
		
		this.add(pnlInstalledBoards, BorderLayout.NORTH);
		this.add(pnlButtons, BorderLayout.SOUTH);
		
		
		//setting up listeners
		btnUpgrade.addActionListener(this);
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
			installedBoards = cli.getInstalledBoards(dir);
			cli.deleteCommand(dir);
		}
		catch(IOException e) {
			Message.println(e);
			return;
		}	
		
		this.pnlInstalledBoards.removeAll();
		
		if (installedBoards == null) {
			JLabel l = new JLabel("No board installed.");
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add(l,  BorderLayout.CENTER);
			pnlInstalledBoards.add(p, BorderLayout.CENTER);
			return;
		}

		Vector<String> boardNames = new Vector<String>();
		for (JsonCoreListItem item: installedBoards) {
			String id = item.ID;
			if (id.contains("@")) {
				String[] split = id.split("@");
				id = split[0];
			}	
			boardNames.add(item.Name + " (" + id + ", version=" + item.Installed + ")");
		}
		
	
		cbInstalledBoards = new JComboBox<String>(boardNames);
//		cbInstalledBoards.setPreferredSize(new Dimension(pnlInstalledBoards.getWidth() - 20, cbInstalledBoards.getPreferredSize().height));
		pnlInstalledBoards.add(cbInstalledBoards);

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
			this.uninstallBoard();
		}
		else if (e.getSource() == btnUpgrade) {
			this.upgradeBoard();
		}
		return;
	}
	
	/**
	 * 
	 */
	public void uninstallBoard()
	{
		int idx = cbInstalledBoards.getSelectedIndex();
		if (idx < 0) {
			JOptionPane.showMessageDialog(this.parent, "No board is selected for uninstallation.");
			return;
		}
		parent.setEnabled(false);
		
		messageWindow = new BoardManagerMessageWindow("Uninstalling ...");

		int w = 200;
		int h = 25;
		int x = parent.getX() + parent.getWidth() / 2 - w / 2;
		int y = parent.getY() + parent.getHeight() / 2 - h / 2;
		messageWindow.setBounds(x,  y, w, h);
		messageWindow.setVisible(true);
		
		String id = installedBoards[idx].ID;
		if (id.contains("@")) {
			String[] split = id.split("@");
			id = split[0];
		}		
		
		File path = new File(System.getProperty("java.io.tmpdir"));

		boolean ret = ArduinoCLIBackground.getSingleton().uninstallBoard(path, id, this);
		if (ret == false) {
			messageWindow.setVisible(false);
			parent.setEnabled(true);
		}
		return;
	}
	
	/**
	 * 
	 */
	public void upgradeBoard()
	{
		int idx = cbInstalledBoards.getSelectedIndex();
		
		if (idx < 0) {
			JOptionPane.showMessageDialog(this.parent, "No board is selected for uninstallation.");
			return;
		}
		
		parent.setEnabled(false);		
		messageWindow = new BoardManagerMessageWindow("Upgrading ...");

		int w = 200;
		int h = 25;
		int x = parent.getX() + parent.getWidth() / 2 - w / 2;
		int y = parent.getY() + parent.getHeight() / 2 - h / 2;
		messageWindow.setBounds(x,  y, w, h);
		messageWindow.setVisible(true);
		
		String id = installedBoards[idx].ID;
		String ver = "";
		if (id.contains("@")) {
			String[] split = id.split("@");
			id = split[0];
			ver = split[1];
		}		
		if (installedBoards[idx].Latest.equals(ver)){
			JOptionPane.showMessageDialog(this.parent, "The latest version is already installed.\nNo need to upgrade the core.");
			messageWindow.setVisible(false);
			parent.setEnabled(true);
			return;
		}
				
		File path = new File(System.getProperty("java.io.tmpdir"));
		boolean ret = ArduinoCLIBackground.getSingleton().upgradeBoard(path, id, this);
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
		
		JOptionPane.showMessageDialog(this.parent, "The board was successfully uninstalled.");
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
		
		JOptionPane.showMessageDialog(this.parent, "The board was successfully upgraded.");
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
