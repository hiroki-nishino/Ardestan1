package org.ardestan.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.ardestan.misc.ARFileConst;


/**
 * @author hiroki
 *
 */
@SuppressWarnings("serial")
public class TabbedCodeEditorPane extends DnDTabbedPane implements ActionListener
{
	public static String DEFAULT_NEW_TAB_TITLE = "untitled";
	
	protected Map<String, JLabel>						titleToLabel;
	protected Map<JPanel, ActionListener>		 		panelToActionListener;
	protected Map<JPanel, EditorPanelInterface> 		panelToEditorPanel;
	
	protected Map<JButton, EditorPanelInterface>		closeButtonToEditorPanel;
	
	
	public static Icon closeIcon = null;
	
	/**
	 * 
	 */
	public TabbedCodeEditorPane()
	{
		this.titleToLabel		= new HashMap<String, JLabel>();
		this.panelToActionListener 	= new HashMap<JPanel, ActionListener>();
		this.panelToEditorPanel 	= new HashMap<JPanel, EditorPanelInterface>();
		
		this.closeButtonToEditorPanel = new HashMap<JButton, EditorPanelInterface>();
		this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		if (closeIcon == null) {
			URL url = ClassLoader.getSystemResource(ARFileConst.DEFAULT_CLOSE_ICON);
			ImageIcon icon = new ImageIcon(url);
			Image img = icon.getImage();
			img = img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
			closeIcon = new ImageIcon(img);
		}
		
	}
	
	
	

	/**
	 * @param candidate
	 * @return
	 */
	public boolean tabTitleExists(String candidate)
	{
		for (int i = 0; i < this.getTabCount(); i++) {
			String filename = this.getTitleAt(i);
			if (candidate.equals(filename)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	public String getUntitledTabTitle()
	{
		//check 'untitled'
		String base = DEFAULT_NEW_TAB_TITLE;
		if (!tabTitleExists(base)) {
			return base;
		}
		
		//now try, "untitled1", "untitled2", "untitled3" ...
		int cnt = 1;
		String candidate = null;
		while(true) {
			candidate = base + cnt;
			if (!tabTitleExists(candidate)) {
				break;
			}
			cnt++;
		}
		

		return candidate;
	}
	
	/**
	 * @param file
	 * @return
	 */
	public EditorPanelInterface getEditorPanel(File file)
	{
		for (EditorPanelInterface p: panelToEditorPanel.values()) {
			File f = p.getFile();
			if (f != null && f.getAbsolutePath().equals(file.getAbsolutePath())) {
				return p;
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * @param title
	 * @param panel
	 */
	public void addPane(String title, EditorPanelInterface tab)
	{
		JPanel p = tab.getJPanel();
		this.add(title, p);
		this.setTabTextAndCloseButton(tab, this.getTabCount() - 1, title);
		
		this.panelToActionListener.put(p,  tab);
		this.panelToEditorPanel.put(p,  tab);
		this.setSelectedComponent(p);
		return;	
	}
	
	
	
	/**
	 * 
	 */
	private void setTabTextAndCloseButton(EditorPanelInterface tab, int index, String title) {
		this.setTabTextAndCloseButton(tab, index, title, true);
	}
	
	
	
	/**
	 * @param tab
	 * @param index
	 * @param title
	 * @param closeButton
	 */
	private void setTabTextAndCloseButton(EditorPanelInterface tab, int index, String title, boolean showCloseButton) {
		//add the close button.
		JPanel tabArea = new JPanel(new BorderLayout());
		tabArea.setOpaque(false);
		
		JLabel label = new JLabel(title);
		this.titleToLabel.put(title, label);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
		tabArea.add(label, BorderLayout.WEST);
		
		

		if (showCloseButton) {
			JButton closeButton = new JButton(closeIcon);		
			closeButton.setBorder(BorderFactory.createEmptyBorder());
			closeButton.addActionListener(this);
			tabArea.add(closeButton, BorderLayout.EAST);
			this.closeButtonToEditorPanel.put(closeButton, tab);
		}
		
		this.setTabComponentAt(index, tabArea);	
	}
	
	/**
	 * @param before
	 * @param after
	 */
	public void replacePane(String title, EditorPanelInterface before, EditorPanelInterface after)
	{
		int index = this.indexOfComponent(before.getJPanel());
	
		this.setComponentAt(index, after.getJPanel());
		this.setTabTextAndCloseButton(after, index, title);
	
		
		this.panelToActionListener	.remove(before.getJPanel());
		this.panelToEditorPanel		.remove(before.getJPanel());

		this.panelToActionListener	.put(after.getJPanel(), after);
		this.panelToEditorPanel		.put(after.getJPanel(), after);
		
		return;
	}
	
	


	/**
	 * 
	 */
	public void closeAllPanes()
	{
		Iterator<JPanel> i = new HashSet<JPanel>(panelToActionListener.keySet()).iterator();
		while(i.hasNext()) {
			JPanel p = i.next();
			this.removePane(p);
		}

		return;
	}
	
	/**
	 * 
	 */
	public void removeSelectedPane() 
	{
		JPanel p = (JPanel)this.getSelectedComponent();
		
		Iterator<JButton> it = this.closeButtonToEditorPanel.keySet().iterator();
		while(it.hasNext()) {
			JButton b = it.next();
			if (this.closeButtonToEditorPanel.get(b).getJPanel() == p) {
				this.removePane(p);
				return;
			}
		}
		
		return;
	}
	
	/**
	 * @param original
	 * @param renamed
	 */
	public void renamed(String original, String renamed)
	{
		
		Iterator<JPanel> i = this.panelToEditorPanel.keySet().iterator();
		while(i.hasNext()) {
			JPanel p = i.next();
			EditorPanelInterface ep = this.panelToEditorPanel.get(p);
			File f = ep.getFile();
			if (f.getName().equals(original)) {
				File nf = new File(f.getParentFile(), renamed);
				ep.setFile(nf);
				
				JLabel l = this.titleToLabel.get(original);
				l.setText(renamed);
				this.titleToLabel.remove(original);
				this.titleToLabel.put(renamed, l);
				break;
			}
		}
		
		return;
		
	}
	
	/**
	 * @param filename
	 */
	public void removePane(String filename)
	{
		Iterator<JPanel> i = this.panelToEditorPanel.keySet().iterator();
		while(i.hasNext()) {
			JPanel p = i.next();
			EditorPanelInterface ep = this.panelToEditorPanel.get(p);
			if (ep.getFile().getName().equals(filename)) {
				this.removePane(p);
				break;
			}
		}
		return;
	}
	
	/**
	 * @param file
	 */
	public void removePane(File file)
	{
		Iterator<JPanel> i = this.panelToEditorPanel.keySet().iterator();
		while(i.hasNext()) {
			JPanel p = i.next();
			EditorPanelInterface ep = this.panelToEditorPanel.get(p);
			if (ep.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
				this.removePane(p);
				break;
			}
		}
		return;
	}

	
	/**
	 * @param index
	 */
	public void removePane(JPanel p)
	{
		this.panelToActionListener	.remove(p);
		this.panelToEditorPanel		.remove(p);
		
		Iterator<JButton> it = this.closeButtonToEditorPanel.keySet().iterator();
		while(it.hasNext()) {
			JButton b = it.next();
			if (this.closeButtonToEditorPanel.get(b).getJPanel() == p) {
				it.remove();
				break;
			}
		}

		this.remove(p);
		
		return;
	}
	
	
	/**
	 * @param p
	 */
	public void updateLabel(JPanel p)
	{
		Iterator<JButton> i = this.closeButtonToEditorPanel.keySet().iterator();
		while(i.hasNext()) {
			JButton b = i.next();
			if (this.closeButtonToEditorPanel.get(b).getJPanel() == p) {
				i.remove();
				break;
			}
		}
		
		File f = this.panelToEditorPanel.get(p).getFile();		
		String title = f.getName();

		//add the close button.
		JPanel tabArea = new JPanel(new BorderLayout());
		tabArea.setOpaque(false);
		
		JLabel label = new JLabel(title);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
		tabArea.add(label, BorderLayout.WEST);

		JButton closeButton = new JButton(closeIcon);		
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		closeButton.addActionListener(this);
		tabArea.add(closeButton, BorderLayout.EAST);
		
		int index = this.getSelectedIndex();
		this.setTabComponentAt(index, tabArea);	
		
		this.setTitleAt(index, title);
		
		this.closeButtonToEditorPanel.put(closeButton, this.panelToEditorPanel.get(p));
	
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() instanceof JButton) {
			EditorPanelInterface tab = this.closeButtonToEditorPanel.get(e.getSource());
			if (tab != null) {
				this.removePane(tab.getJPanel());
			}
			return;
		}
		
		if (e.getSource() instanceof JMenuItem == false) {	
			return;
		}

		String s = ((JMenuItem)e.getSource()).getText();
		if (s.equals(MenuItemText.CLOSE_FILE)) {
			this.removeSelectedPane();
			return;
		}

		
		JPanel p = (JPanel)this.getSelectedComponent();
		if (p == null) {
			if (s.equals(MenuItemText.BUILD) || s.equals(MenuItemText.BUILD_AND_UPLOAD)) {
				JOptionPane.showMessageDialog(this, "Please, open a visual program to build.");
			}
			else {
				JOptionPane.showMessageDialog(this, "No program is currently being edited.");				
			}
			return;
		}
		
		panelToActionListener.get(p).actionPerformed(e);
		if (s.equals(MenuItemText.SAVE_FILE) || s.equals(MenuItemText.SAVE_FILE_AS)) {
			updateLabel(p);
		}


	}

}
