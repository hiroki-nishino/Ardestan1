package org.ardestan.gui.dialog.library;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;


/**
 * @author hiroki
 *
 */
public class LibraryManagerDialog extends JDialog 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected LibraryInstallerDialogPanel 			pnlInstaller;
	protected LibraryUninstallerUpgraderDialogPanel	pnlUninstallerUpgrader;
	
	protected JTabbedPane pane;
	/**
	 * 
	 */
	public LibraryManagerDialog() 
	{
		this.setTitle("Library Manager");
		
		pnlInstaller = new LibraryInstallerDialogPanel(this);
		pnlUninstallerUpgrader = new LibraryUninstallerUpgraderDialogPanel(this);
		
		pane = new JTabbedPane();
		
		pane.add("Install", pnlInstaller);
		pane.add("Uninstall", pnlUninstallerUpgrader);
		
		this.setLayout(new BorderLayout());
		this.add(pane, BorderLayout.CENTER);
		this.setModal(true);		
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
