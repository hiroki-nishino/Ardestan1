/**
 * 
 */
package org.ardestan.gui.info.serialmonitor;


import java.util.LinkedList;

import org.ardestan.gui.Message;
import org.ardestan.misc.ProjectSetting;

import com.fazecast.jSerialComm.SerialPort;

/**
 * @author hnishino
 *
 */
/**
 * @author hiroki
 *
 */
public class SerialMonitor 
{
	//----------------------------------------------------------------------
	//singleton pattern
	private static SerialMonitor singleton = null;
	/**
	 * @return
	 */
	public static synchronized SerialMonitor getInstance() {
		if (singleton == null) {
			singleton = new SerialMonitor();
		}
		return singleton;
	}
	
	//----------------------------------------------------------------------
	
	protected	SerialPort 			port = null;
	protected	int					baudRate = 9600;
	protected	int					pollingSleepTime = 10;
	
	protected	SerialReadThread 	readThread = null;
	
	protected	LinkedList<SerialMonitorListener> listeners = null;
	
	
	/**
	 * 
	 */
	private SerialMonitor()
	{
		listeners = new LinkedList<SerialMonitorListener>();
		return;
	}
	
	/**
	 * @param l
	 */
	public void addSerialMonitorListener(SerialMonitorListener l)
	{
		listeners.add(l);
	}
	
	/**
	 * @param l
	 */
	public void removeSerialMonitorListener(SerialMonitorListener l)
	{
		listeners.remove(l);
	}
	
	/**
	 * @param baudRate
	 */
	public void setBaudRate(int baudRate)
	{
		this.baudRate = baudRate;
	}
	
	/**
	 * 
	 */
	public void notifyOpen()
	{
		for (SerialMonitorListener l: listeners) {
			l.notifyOpen();
		}
		return;
	}
	
	/**
	 * 
	 */
	public void notifyClose()
	{
		for (SerialMonitorListener l: listeners) {
			l.notifyClose();
		}
		return;
	}
	
	/**
	 * @return
	 */
	public boolean isOpen()
	{
		if (port == null) {
			return false;
		}
		
		return port.isOpen();
	}
	
	/**
	 * @return
	 */
	public int getBaudRate()
	{
		return this.baudRate;
	}
	/**
	 * 
	 */
	public void open() throws PortOpenFailedException
	{
		//stop the read thread, if it is active.
		if (readThread != null || (port != null && port.isOpen())) {
			throw new PortOpenFailedException("There is an active serial port. Close it first.");
		}
		
		
		//is the port already specified?
		ProjectSetting setting = ProjectSetting.getSingleton();
		String settingPortname = setting != null ? setting.getPortName() : "null";
		
		if (settingPortname == null) {
			throw new PortOpenFailedException("No serial port is selected yet.");
		}
		
		//get the list of available ports and find the right SerialPort object to access.
		SerialPort[] comPorts = SerialPort.getCommPorts();
		if (comPorts == null || comPorts.length == 0) {
			throw new PortOpenFailedException("No serial port is available.");
		}
		
		for (SerialPort p: comPorts) {
			if (settingPortname.contains(p.getSystemPortName())) {
				port = p;
			}
		}
		if (port == null) {
			throw new PortOpenFailedException("The serial port specified by the project was not found (" + settingPortname + ").");
		}
		
		//now let's open the port
		Message.println("setting the baud rate to:" + this.baudRate + "...");
		port.setBaudRate(this.baudRate);
		Message.println("done.");
		
		Message.print("opening the serial port:" + port.getSystemPortName() + " (" + port.getDescriptivePortName() + ") ...");
		boolean ret = port.openPort();
		if (ret == false) {
			Message.print("failed.");
			String portName = port.getSystemPortName();
			port = null;
			throw new PortOpenFailedException("Failed to open the serial port:" + portName + " (" + settingPortname + ").");			
		}
		
		Message.println("done.");
		//start a new reader thread.
		this.readThread = new SerialReadThread();
		readThread.start();
		
		this.notifyOpen();
		return;
	}
	
	/**
	 * 
	 */
	public void close()
	{
		//stop the read thread, if it is active.
		if (readThread != null) {
			Message.print("stopping the current serial read thread...");
			readThread.stopPolling();
			Message.println("done.");
			readThread = null;
		}
		
		//close the current port if it is open.
		if (port != null && port.isOpen()) {
			Message.print("closing the serial port: : " + port.getSystemPortName() + " (" + port.getDescriptivePortName() + ")...");
			
			boolean ret = port.closePort();
			if (!ret) {
				Message.println("failed.");
			}
			else {
				Message.println("done.");
			}
			port = null;
		}
		
		this.notifyClose();
		return;
	}
	
	/**
	 * 
	 */
	public void dataReceived(byte[] data)
	{
		for (SerialMonitorListener l: listeners) {
			l.dataReceived(data);
		}		
		return;
	}
	
	/**
	 * @param s
	 */
	public void send(String s)
	{
		if (!this.isOpen()) {
			return;
		}
		
		byte[] b = s.getBytes();
		port.writeBytes(b, b.length);
		
		byte[] n = {0x00};
		port.writeBytes(n, n.length);
	
		return;
	}
	
	
	//----------------------------------------------------------------------
	/**
	 * @author hnishino
	 *
	 */
	class SerialReadThread extends Thread
	{
		private boolean loop;
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run()
		{
			this.loop = true;
			while(loop) {
				//if there is no available data incoming, sleep for a while.
				if (port.bytesAvailable() == 0) {
					try {
						Thread.sleep(pollingSleepTime);
					}
					catch(InterruptedException e) {
						Message.println("An error occured within the SerialReadThread's main loop.");
						Message.println(e);
					}
					continue;
				}
				
				//the connection is closed.
				if (port.bytesAvailable() < 0) {
					return;
				}
				//read the incoming data and pass it to the parent instance.
				byte[] data = new byte[port.bytesAvailable()];
				port.readBytes(data, data.length);
				dataReceived(data);
			}
			return;
		}
		
		/**
		 * 
		 */
		public void stopPolling() 
		{
			this.loop = false;
			try {
				this.join();
			}
			catch(InterruptedException e) {
				Message.println("An error occured when stopping the SerialReadThread's main loop.");
				Message.println(e);
			}
			return;
		}
	}
}
