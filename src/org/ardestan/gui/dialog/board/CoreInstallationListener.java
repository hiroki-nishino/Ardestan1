package org.ardestan.gui.dialog.board;


/**
 * @author hnishino
 *
 */
public interface CoreInstallationListener {
	public void finishedInstallation();
	public void failedInstallation();
	
	public void finishedUninstallation();
	public void failedUninstallation();
	
	public void finishedUpgrading();
	public void failedUpgrading();
}
