package org.ardestan.gui;


import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JPanel;

/**
 * @author hnishino
 *
 */
public interface EditorPanelInterface extends ActionListener
{

	JPanel 	getJPanel();
	File	getFile();
	void	setFile(File f);
}
