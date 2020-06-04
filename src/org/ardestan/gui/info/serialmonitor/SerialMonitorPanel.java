package org.ardestan.gui.info.serialmonitor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import org.ardestan.gui.Message;
import org.ardestan.gui.visual.GUIFont;

public class SerialMonitorPanel extends JPanel implements ActionListener, SerialMonitorListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JScrollPane	messageAreaScrollPane;
	protected JEditorPane 	messageArea		;
	protected JButton		btnClear		;

	protected JTextField	tfOutputString		;
	protected JCheckBox		ckKeepOutputString	;
	protected JCheckBox		ckAddLineFeed	;
	protected JButton		btnSend				;
	
	protected JCheckBox			ckActive	;
	protected JCheckBox			ckAutoScroll;
	protected JComboBox<String>	cbBaudRates	;
	
	/**
	 * 
	 */
	public SerialMonitorPanel()
	{
		this.setLayout(new BorderLayout());
		
		//the incoming message area
		this.messageArea = new JEditorPane();
		this.messageArea.setFont(GUIFont.getSingleton().getObjectBoxFont());
		this.messageAreaScrollPane = new JScrollPane(messageArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//the string output area
		JPanel pnlSerialOutput = new JPanel();
		pnlSerialOutput.setLayout(new BorderLayout());
		tfOutputString = new JTextField();
		
		ckAddLineFeed   = new JCheckBox("add '\\n'");
		//ckAddLineFeed.setSelected(true);
		
		ckKeepOutputString = new JCheckBox("keep output data");
		btnSend = new JButton("send");
		
		JPanel pnlSerialOutputEast = new JPanel();
		pnlSerialOutputEast.setLayout(new BorderLayout());
		pnlSerialOutputEast.add(ckKeepOutputString	, BorderLayout.EAST);
		pnlSerialOutputEast.add(ckAddLineFeed		, BorderLayout.CENTER);
		pnlSerialOutputEast.add(btnSend				, BorderLayout.WEST);
		
		pnlSerialOutput.add(tfOutputString		, BorderLayout.CENTER);
		pnlSerialOutput.add(pnlSerialOutputEast	, BorderLayout.EAST);

		//the control area		
		JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ckActive 		= new JCheckBox("active");
		ckAutoScroll	= new JCheckBox("auto scroll");
		
		String[] baudrates = new String[] {"300", "1200", "2400", "4800", "9600", "19200", "38400", "57600", "74880", "115200", 
											"230400", "250000", "500000", "1000000", "2000000"};
		cbBaudRates = new JComboBox<String>(baudrates);
		cbBaudRates.setSelectedIndex(4);
		
		pnlStatus.add(ckActive);
		pnlStatus.add(ckAutoScroll);

		btnClear = new JButton("clear");

		JPanel pnlBaudrateAndClearButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlBaudrateAndClearButton.add(new JLabel("baud rate:"));
		pnlBaudrateAndClearButton.add(cbBaudRates);
		pnlBaudrateAndClearButton.add(btnClear);

		JPanel pnlControl = new JPanel(new BorderLayout());
		pnlControl.add(pnlStatus	, BorderLayout.WEST);
		pnlControl.add(pnlBaudrateAndClearButton	, BorderLayout.EAST);
		
		this.add(pnlControl				, BorderLayout.NORTH);
		this.add(messageAreaScrollPane	, BorderLayout.CENTER);
		this.add(pnlSerialOutput		, BorderLayout.SOUTH);
		
		//add event listeners
		ckActive.addActionListener(this);
		btnClear.addActionListener(this);
		cbBaudRates.addActionListener(this);
		btnSend.addActionListener(this);
		tfOutputString.addActionListener(this);
		tfOutputString.enableInputMethods(false);
		
		//start listening the serial monitor.
		SerialMonitor monitor = SerialMonitor.getInstance();
		monitor.addSerialMonitorListener(this);
		
		//enable the autoscroll by default.
		ckAutoScroll.setSelected(true);
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		//the 'active' check box
		if (e.getSource() == ckActive) {
			SerialMonitor monitor = SerialMonitor.getInstance();
			if (ckActive.isSelected()) {
				try {
					monitor.open();
				}
				catch(PortOpenFailedException ex) {
					JOptionPane.showMessageDialog(this.getRootPane(), 
							ex.getMessage(), ex.getClass().getSimpleName(), JOptionPane.WARNING_MESSAGE);
					ckActive.setSelected(false);
				}
				return;
			}
			monitor.close();
			return;
		}
		
		//the clear button
		if (e.getSource() == btnClear) {
			this.clear();
			return;
		}
		
		//the baud dates
		if (e.getSource() == cbBaudRates) {
			this.changeBaudRates();
			return;
		}
		
		//send
		if (e.getSource() == btnSend || e.getSource() == tfOutputString) {
			this.send();
			return;
		}
	}

	/**
	 * 
	 */
	public void send()
	{
		String s = tfOutputString.getText();
		
		SerialMonitor monitor = SerialMonitor.getInstance();
		if (ckAddLineFeed.isSelected()) {
			s = s + "\n";
		}
		
		monitor.send(s);
		
		if (ckKeepOutputString.isSelected() == false) {
			tfOutputString.setText("");
		}
		return;
	}
	
	/**
	 * 
	 */
	public void changeBaudRates()
	{
		String tmp = (String)cbBaudRates.getSelectedItem();
		int baudRate = Integer.parseInt(tmp);
		
		SerialMonitor monitor = SerialMonitor.getInstance();
		
		//if the current baud rate is the same as the new one,
		//there is no need to do anything.
		if (monitor.getBaudRate() == baudRate) {
			return;
		}
		
		//if the port is not open, just change the baud rate.
		if (monitor.isOpen() == false) {
			monitor.setBaudRate(baudRate);
			return;
		}
		
		//if the port is open, first close the port.
		//Then change the rate and open again.
		monitor.close();
		monitor.setBaudRate(baudRate);
		try {
			monitor.open();
		}
		catch(SerialMonitorException e) {
			Message.println(e);
		}
		return;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.info.serialmonitor.SerialMonitorListener#notifyOpen()
	 */
	@Override
	public void notifyOpen() {
		if (ckActive.isSelected() == false) {
			ckActive.setSelected(true);
		}
		return;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.info.serialmonitor.SerialMonitorListener#notifyClose()
	 */
	@Override
	public void notifyClose() {
		if (ckActive.isSelected() != false) {
			ckActive.setSelected(false);
		}
	}



	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.info.serialmonitor.SerialMonitorListener#dataReceived(byte[])
	 */
	@Override
	public void dataReceived(byte[] data) 
	{
		String s = new String(data);
		
		try {
			messageArea.getDocument().insertString(messageArea.getDocument().getLength(), s, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		if (ckAutoScroll.isSelected()) {
			messageArea.setCaretPosition(messageArea.getDocument().getLength());
		}
	}

	
	/**
	 * 
	 */
	public void clear()
	{
		messageArea.setText("");
	}

}
