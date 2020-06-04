package org.ardestan.gui.dialog.preference;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class PreferencesDialog extends JDialog implements ActionListener 
{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected JTextArea	editor;
	
	protected JButton okButton;
	protected JButton cancelButton;
	
	protected boolean canceled;
	
	
	protected String urls;

	/**
	 * 
	 */
	public PreferencesDialog(String base64EncodedURLs)
	{
		//build UI		
		JPanel editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());
		editorPanel.setBorder(BorderFactory.createTitledBorder("Enter Additional URLs (one for each row)."));
				
		editor = new JTextArea();
		editorPanel.add(editor, BorderLayout.CENTER);
		
		Vector<String> urls = this.decodeBase64URLs(base64EncodedURLs);
		StringBuffer b = new StringBuffer();
		for (String u: urls) {
			u = u.trim();
			if (u.length() == 0) {
				continue;
			}
			b.append(u);
			b.append("\n");
		}
		editor.setText(b.toString());
		
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
		mainPanel.add(editorPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		this.add(mainPanel);
		
		//reset the states
		canceled = true;
		
		this.setTitle("Preferences");
		this.setModal(true);
	}
	
	/**
	 * @return
	 */
	public String getBase64EncodedURLs()
	{
		return urls;
	}
	
	/**
	 * @return
	 */
	public boolean isCanceled()
	{
		return this.canceled;
	}
	
	/**
	 * @param base64EncodedURLs
	 * @return
	 */
	public Vector<String> decodeBase64URLs(String base64EncodedURLs)
	{
		Vector<String> decodedURLs = new Vector<String>();
		
		String[] split = base64EncodedURLs.split(",");
		for (String s: split) {
			s = s.trim();
			if (s.length() == 0) {
				continue;
			}
			String decoded = new String(Base64.getUrlDecoder().decode(s));
			decodedURLs.add(decoded);
		}
		
		return decodedURLs;
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
		
		String t = this.editor.getText();
		String[] split = t.split("\n");
		StringBuffer buf = new StringBuffer();
		for (String s: split) {
			s = s.trim();
			if (s.length() == 0) {
				continue;
			}
			String encoded = Base64.getUrlEncoder().encodeToString(s.getBytes());
			buf.append(encoded + ",");
		}
		
		this.urls = buf.toString();
		canceled = false;
		this.setVisible(false);
	}

	

}
