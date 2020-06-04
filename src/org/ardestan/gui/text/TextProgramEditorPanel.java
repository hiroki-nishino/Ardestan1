package org.ardestan.gui.text;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ardestan.gui.EditorPanelInterface;
import org.ardestan.gui.MenuItemText;
import org.ardestan.misc.ARFileConst;
import org.ardestan.misc.ProjectSetting;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * @author hiroki
 *
 */

public class TextProgramEditorPanel implements EditorPanelInterface
{
	RSyntaxTextArea textArea	;
	RTextScrollPane scrollPane	;
	JPanel			panel		;
	File			file		;
	
	/**
	 * 
	 */
	public TextProgramEditorPanel()
	{
		
		textArea = new RSyntaxTextArea();
		
		scrollPane = new RTextScrollPane(textArea);
		scrollPane.setLineNumbersEnabled(true);
		
		panel = new JPanel();
		panel.add(scrollPane);
		
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		
		file = null;
	}

	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.gui.EditorPanelInterface#getJPanel()
	 */
	public JPanel getJPanel() {
		return panel;
	}
	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.EditorPanelInterface#getFile()
	 */
	public File getFile() {
		return file;
	}
	

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.EditorPanelInterface#setFile(java.io.File)
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	protected String buildString(File file) throws IOException
	{
		//read the text from the file.
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String s = buildString(br);
		br.close();

		return s;
	
	}
	
	/**
	 * @param bfr
	 * @return
	 * @throws IOException
	 */
	protected String buildString(BufferedReader br) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		
		while(true){
			int c = br.read();
			if (c == -1) {
				break;
			}
			builder.append((char)c);
		}
		
		
		return builder.toString();
	}

	/**
	 * @param sourceFile
	 */
	public void loadProgram(File sourceFile) throws IOException
	{	
		String filename = sourceFile.getName();
		
		if (filename.endsWith(ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT) 	|| 
			filename.endsWith(ARFileConst.ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT) 	|| 
			filename.endsWith(ARFileConst.ARDESTAN_HEADER_FILE_EXTENSION_WITH_DOT)	){ 
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
			textArea.setCodeFoldingEnabled(true);
		}
		
		
		String text = this.buildString(sourceFile);
		this.textArea.setText(text);
		this.file = sourceFile;
		this.textArea.setTabSize(4);
		textArea.discardAllEdits();
		
		return;
	}
	
	
	/**
	 * 
	 */
	public void save() throws IOException
	{
		String text = textArea.getText();
		
		FileWriter fw = new FileWriter(this.file);
		fw.write(text);
		fw.close();
		
		return;
	}
	
	/**
	 * @param filename
	 * @throws IOException
	 */
	public void saveAs(String filename) throws IOException
	{
		ProjectSetting ps = ProjectSetting.getSingleton();

		JFileChooser fc = new JFileChooser(ps.getProjectDirectory());
		fc.setSelectedFile(new File(filename));
		
		int ret = fc.showSaveDialog(panel.getRootPane());
		
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
				
		File f = fc.getSelectedFile();
		
		if (this.file.getName().endsWith(ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT)) {
			if (f.getName().endsWith(ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT)) {
				f = new File(this.file.getAbsolutePath() + ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT);
			}
		}
		else if (this.file.getName().endsWith(ARFileConst.ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT)) {
			if (f.getName().endsWith(ARFileConst.ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT)) {
				f = new File(this.file.getAbsolutePath() + ARFileConst.ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT);
			}
		}
		else if (this.file.getName().endsWith(ARFileConst.ARDESTAN_HEADER_FILE_EXTENSION_WITH_DOT)) {
			if (f.getName().endsWith(ARFileConst.ARDESTAN_HEADER_FILE_EXTENSION_WITH_DOT)) {
				f = new File(this.file.getAbsolutePath() + ARFileConst.ARDESTAN_HEADER_FILE_EXTENSION_WITH_DOT);
			}
		}
		else if (this.file.getName().endsWith(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT)) {
			if (f.getName().endsWith(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT)) {
				f = new File(this.file.getAbsolutePath() + ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT);
			}
		}
		else {
			throw new IOException("a bug!the code shouldn't reach here!");
		}

		if (f.exists()) {
			int r = JOptionPane.showConfirmDialog(panel.getRootPane(), "The file already exists, overwrite?", "Existing File", JOptionPane.YES_NO_OPTION);
			if (r != JOptionPane.YES_OPTION) {
				return;
			}
		}
		
		this.file = f;

		this.save();

		return;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object obj = e.getSource();
		
		if (obj instanceof JMenuItem == false) {
			return;
		}

		JMenuItem item = (JMenuItem)obj;
		String menuItemText = item.getText();
		
		if (menuItemText.equals(MenuItemText.SAVE_FILE)) {
			try {
				this.save();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			return;
		}
		else if (menuItemText.equals(MenuItemText.SAVE_FILE_AS)){
			JOptionPane.showMessageDialog(panel.getRootPane(), 
					"Use the User-defined Object Wizard to change the file name.\n Click '.aud' file to open thw wizard.");
		}
		else if (menuItemText.equals(MenuItemText.REDO)) {
			textArea.redoLastAction();
		}
		
	}

	
}
