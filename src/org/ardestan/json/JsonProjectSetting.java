package org.ardestan.json;

/**
 * @author hnishino
 *
 */
public class JsonProjectSetting 
{
	protected String	fqbn 		= null;
	protected String	portName	= null;
	protected String	boardName	= null;
	
	protected boolean	usePROGMEMforConnections 	= false;
	protected boolean	usePROGMEMforOutlets		= false;
	
	/**
	 * @return the usePROGMEMforOutlets
	 */
	public boolean getUsePROGMEMforOutlets() {
		return usePROGMEMforOutlets;
	}

	/**
	 * @param usePROGMEMforOutlets the usePROGMEMforOutlets to set
	 */
	public void setUsePROGMEMforOutlets(boolean usePROGMEMforOutlets) {
		this.usePROGMEMforOutlets = usePROGMEMforOutlets;
	}

	/**
	 * @return
	 */
	public boolean getUsePROGMEMforConnections() {
		return usePROGMEMforConnections;
	}

	/**
	 * @param usePROGMEMforConnections
	 */
	public void setUsePROGMEMforConnections(boolean usePROGMEMforConnections) {
		this.usePROGMEMforConnections = usePROGMEMforConnections;
	}

	
	/**
	 * @return
	 */
	public String getFqbn() {
		return fqbn;
	}
	
	/**
	 * @param fqbn
	 */
	public void setFqbn(String fqbn) {
		this.fqbn = fqbn;
	}
	
	/**
	 * @return
	 */
	public String getPortName() {
		return portName;
	}
	
	/**
	 * @param portname
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}

	/**
	 * @return
	 */
	public String getBoardName() {
		return boardName;
	}

	/**
	 * @param boardname
	 */
	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}


	
	
}
