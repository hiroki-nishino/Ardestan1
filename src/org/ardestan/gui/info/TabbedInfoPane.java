package org.ardestan.gui.info;

import org.ardestan.gui.DnDTabbedPane;
import org.ardestan.gui.info.console.ConsolePanel;
import org.ardestan.gui.info.serialmonitor.SerialMonitorPanel;


/**
 * @author hnishino
 *
 */
public class TabbedInfoPane extends DnDTabbedPane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ConsolePanel 	consolePanel;
	protected SerialMonitorPanel	serialMonitorPanel;
	
	/**
	 * 
	 */
	public TabbedInfoPane()
	{
		consolePanel 		= new ConsolePanel();
		serialMonitorPanel	= new SerialMonitorPanel();
			
		this.add("Console"			, this.consolePanel);
		this.add("Serial Monitor"	, this.serialMonitorPanel);
	}
	
}
