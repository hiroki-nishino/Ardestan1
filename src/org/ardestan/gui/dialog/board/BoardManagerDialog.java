package org.ardestan.gui.dialog.board;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;


/**
 * @author hiroki
 *
 */
public class BoardManagerDialog extends JDialog 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected BoardInstallerDialogPanel 			pnlInstaller;
	protected BoardUninstallerUpgraderDialogPanel	pnlUninstallerUpgrader;
	
	protected JTabbedPane pane;
	/**
	 * 
	 */
	public BoardManagerDialog() 
	{
		this.setTitle("Board Manager");
		
		pnlInstaller = new BoardInstallerDialogPanel(this);
		pnlUninstallerUpgrader = new BoardUninstallerUpgraderDialogPanel(this);
		
		pane = new JTabbedPane();
		
		pane.add("Install", pnlInstaller);
		pane.add("Upgrade/Uninstall", pnlUninstallerUpgrader);
		
		this.setLayout(new BorderLayout());
		this.add(pane, BorderLayout.CENTER);
		this.setModal(true);		
		
		this.requestUninstallerUpdate();
	}
	
	/**
	 * 
	 */
	public void requestUninstallerUpdate()
	{
		pnlUninstallerUpgrader.refreshComboBox();
		this.revalidate();
	}
}
