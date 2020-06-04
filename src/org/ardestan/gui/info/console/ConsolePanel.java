package org.ardestan.gui.info.console;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ardestan.gui.Message;
import org.ardestan.gui.visual.GUIFont;

public class ConsolePanel extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JButton		btnClear	;
	protected JScrollPane 	scrollPane	;
	protected JEditorPane 	messageArea	;

	
	/**
	 * 
	 */
	public ConsolePanel()
	{
		this.setLayout(new BorderLayout());
		
		this.messageArea = new JEditorPane();
		this.messageArea.setFont(GUIFont.getSingleton().getObjectBoxFont());
		
		this.scrollPane =  new JScrollPane(	this.messageArea, 
											JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		
		this.btnClear = new JButton("clear");
		this.btnClear.addActionListener(this);
		
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		p.add(this.btnClear);
		
		this.add(p		   , BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		
		
		Message.initialize(messageArea);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		//clear the text
		this.messageArea.setText("");
	}
	
	
}
