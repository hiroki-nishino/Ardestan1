package org.ardestan.gui.info.serialmonitor;

/**
 * @author hnishino
 *
 */
public interface SerialMonitorListener 
{
	public void notifyOpen	();
	public void notifyClose	();
	public void dataReceived(byte[] data);
	
}
