package org.ardestan.gui.dialog.library;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ardestan.arduinocli.ArduinoCLIBackground;
import org.ardestan.gui.DialogSizes;
import org.ardestan.json.JsonSize;

/**
 * @author hiroki
 *
 */
public class LibraryInstallerDialogPanel extends JPanel implements ActionListener, LibrarySearchResultListener, LibraryInstallationListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JPanel		pnlSearchKeyword;
	protected JTextField 	tfSearchKeyword	;
	protected JButton		btnSearch		;
	
	protected JPanel		pnlResultArea	;
	
	protected JPanel		pnlButtons;
	protected JButton		btnInstall;
	protected JButton		btnClose;
		
	protected LibraryManagerDialog 		parent;
	
	protected LibraryManagerSearchItem[] lastResult;
	
	protected LibraryManagerMessageWindow 	messageWindow;
	
	JComboBox<String> cbBoards;
	/**
	 * 
	 */
	public LibraryInstallerDialogPanel(LibraryManagerDialog parent)
	{
		//keep to close
		this.parent = parent;
		
		//create the search keyword　panel
		pnlSearchKeyword = new JPanel();
		pnlSearchKeyword.setLayout(new BorderLayout());
		pnlSearchKeyword.setBorder(BorderFactory.createTitledBorder("Search Librarlies"));

		tfSearchKeyword = new JTextField();
		btnSearch		= new JButton("Search");
		
		pnlSearchKeyword.add(tfSearchKeyword, BorderLayout.CENTER);
		pnlSearchKeyword.add(btnSearch, BorderLayout.EAST);
		
		//create the result panel
		pnlResultArea = new JPanel();
		pnlResultArea.setBorder(BorderFactory.createTitledBorder("Libraries Found"));
		pnlResultArea.setLayout(new FlowLayout());
		
		//create the close button panel
		btnInstall 	= new JButton("Install");
		btnClose 	= new JButton("Close");

		pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlButtons.add(btnInstall);
		pnlButtons.add(btnClose);
		
		this.add(pnlButtons, BorderLayout.SOUTH);

		//create the entire panel
		this.setLayout(new BorderLayout());
		this.add(pnlSearchKeyword, BorderLayout.NORTH);
		this.add(pnlResultArea, BorderLayout.CENTER);
		this.add(pnlButtons, BorderLayout.SOUTH);
		
		
		//setting up listeners
		tfSearchKeyword.addActionListener(this);
		btnSearch.addActionListener(this);
		btnInstall.addActionListener(this);
		btnClose.addActionListener(this);
		
		lastResult = null;
		return;
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
	
		if (e.getSource() == btnSearch || e.getSource() == tfSearchKeyword) {
			this.startSearch(tfSearchKeyword.getText().trim());
			return;
		}
		
		if (e.getSource() == btnInstall) {
			this.installLibrary();
		}
		return;
	}
	
	/**
	 * 
	 */
	public void installLibrary()
	{
		if (lastResult == null) {
			JOptionPane.showMessageDialog(this.parent, "No search result found. Please, perform a new search.");
			return;			
		}

		int idx = cbBoards.getSelectedIndex();
		
		LibraryManagerVersionDialog versionDialog = new LibraryManagerVersionDialog(lastResult[idx].name, lastResult[idx].versions);
		JsonSize size = DialogSizes.getSingleton().getLibraryVersionSize();
		int w = size.width;
		int h = size.height;
		int x = parent.getX() + parent.getWidth() / 2 - w / 2;
		int y = parent.getY() + parent.getHeight() / 2 - h / 2;
		versionDialog.setBounds(x,  y, w, h);
		versionDialog.setVisible(true);
		
		String version = versionDialog.getSelectedVersion();
		if (version == null) {
			return;
		}
		
		String libraryName = lastResult[idx].name;
		if (!version.equals(LibraryManagerVersionDialog.LATEST)) {
			libraryName = libraryName + "@" + version;
		}
		
		messageWindow = new LibraryManagerMessageWindow("Installing ...");
	
		size = DialogSizes.getSingleton().getMessageWindow();
		w = size.width;
		h = size.height;
		x = parent.getX() + parent.getWidth() / 2 - w / 2;
		y = parent.getY() + parent.getHeight() / 2 - h / 2;
		messageWindow.setBounds(x,  y, w, h);
		messageWindow.setVisible(true);
		
		File path = new File(System.getProperty("java.io.tmpdir"));
		ArduinoCLIBackground.getSingleton().installLibrary(path, this, libraryName);
		this.parent.setEnabled(false);

		return;
	}
	
	/**
	 * @param keyword
	 */
	public void startSearch(String keyword)
	{
		this.parent.setEnabled(false);

		messageWindow = new LibraryManagerMessageWindow("Searching ...");

		JsonSize size = DialogSizes.getSingleton().getMessageWindow();
		int w = size.width;
		int h = size.height;
		int x = parent.getX() + parent.getWidth() / 2 - w / 2;
		int y = parent.getY() + parent.getHeight() / 2 - h / 2;
		messageWindow.setBounds(x,  y, w, h);
		messageWindow.setVisible(true);
		
		File path = new File(System.getProperty("java.io.tmpdir"));
		boolean ret = ArduinoCLIBackground.getSingleton().searchLibrary(path, this, keyword);
		if (ret == false) {
			messageWindow.setVisible(false);
			parent.setEnabled(true);
		}
		return;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreSearchResultListener#notifyResult(com.hnishino.ardestan.json.JsonCoreSearchResultItem[])
	 */
	@Override
	public void notifyResult(LibraryManagerSearchItem[] result) 
	{
		pnlResultArea.removeAll();
		if (result == null || result.length == 0) {
			pnlResultArea.add(new JLabel("no search result."));
			lastResult = null;
			cbBoards = null;
			this.revalidate();
			messageWindow.setVisible(false);
			parent.setEnabled(true);

			return;
		}
		
		lastResult = result;
		
		Vector<String> items = new Vector<String>();
		for (LibraryManagerSearchItem item: result) {
			String t  = item.name + " (" + item.versions.size() + " version(s) found)";
			items.add(t);
		}
		
		cbBoards = new JComboBox<String>(items);
		
		pnlResultArea.add(cbBoards);
		this.revalidate();
		
		messageWindow.setVisible(false);
		parent.setEnabled(true);
		
		return;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#finished()
	 */
	@Override
	public void finishedInstallation() 
	{
		messageWindow.setVisible(false);
		JOptionPane.showMessageDialog(this.parent, "The library was successfully installed.");
		parent.setEnabled(true);
		parent.requestUninstallerUpdate();
		return;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#failed()
	 */
	public void failedInstallation() 
	{
		messageWindow.setVisible(false);
		JOptionPane.showMessageDialog(this.parent, "An error occured during the installation.");
		parent.setEnabled(true);
		return;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#finishedUninstallation()
	 */
	@Override
	public void finishedUninstallation() {
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#failedUninstallation()
	 */
	@Override
	public void failedUninstallation() {
	}


	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#finishedUpgrading()
	 */
	public void finishedUpgrading() {
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.dialog.CoreInstallationListener#failedUpgrading()
	 */
	public void failedUpgrading() {
	}

	
}
