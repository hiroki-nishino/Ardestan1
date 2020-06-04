package org.ardestan.gui.info.serialmonitor;

/**
 * @author hnishino
 *
 */
public class PortOpenFailedException extends SerialMonitorException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * @param cause
	 */
	public PortOpenFailedException(String cause) {
		super(cause);
	}

}
