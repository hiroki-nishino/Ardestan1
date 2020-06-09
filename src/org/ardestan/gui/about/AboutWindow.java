package org.ardestan.gui.about;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.ardestan.arduinocli.ArduinoCLI;

public class AboutWindow extends JDialog implements ActionListener 
{
	private static final long serialVersionUID = 1L;

	protected JButton closeButton;
	
	/**
	 * 
	 */
	public AboutWindow()
	{
		
		JTextArea info = new JTextArea();
		info.setEditable(false);
		
		String text  = "\n Ardestan IDE for Arduino: " + ArduinoCLI.getOSString() + " \n";
		text 		+= "\n";
		text 		+= " Version 0.1 (alpha release: Taipei)\n";
		text		+= " Copyright:  (c) 2020, Hiroki Nishino. All right reserved.\n";
		text 		+= "\n";
		text 		+= " Ardestan IDE utilzes the open-source libraies listed below.\n";
		text 		+= " Gson                     (Apache 2.0 license)  : Copyright (C) 2008 Google Inc. All rights reserved.\n";
		text 		+= " jSerialComm              (Apache 2.0 license)  : Copyright (C) 2012-2020 Fazecast, Inc. All rights reserved.\n";
		text 		+= " RSyntaxTextArea          (BSD 3-Clause license): Copyright (c) 2019, Robert Futrell. All rights reserved.\n";
		text 		+= " DnDTabbedPane            (CC BY-SA 4.0)        : Copyright (c) 2020, Atsuhiro Terai. All rights reserved.\n";
		text		+= " Apache Commons Lang 3.10 (Apache License 2.0)  : Copyright (C) 2001-2020 The Apache Software Foundation.\n"
				     + "                                                                All rights reserved.";
		
		info.setText(text);
		info.setFont(new Font("Monospaced",Font.PLAIN, 14));
		info.setBackground(this.getBackground());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		closeButton = new JButton("close");
		closeButton.addActionListener(this);
		
		buttonPanel.add(closeButton);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(info, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		this.add(mainPanel);
		
		
		this.setTitle("About Ardestan IDE");
		this.setAlwaysOnTop(true);
		this.setModal(true);
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);		
	}
		
}
