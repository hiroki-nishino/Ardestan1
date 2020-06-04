package org.ardestan.gui.dialog.library;


/**
 * @author hnishino
 *
 */
public interface LibraryInstallationListener {
	public void finishedInstallation();
	public void failedInstallation();
	
	public void finishedUninstallation();
	public void failedUninstallation();
	
	public void finishedUpgrading();
	public void failedUpgrading();
}
