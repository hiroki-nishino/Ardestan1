package org.ardestan.gui.dialog.board;

import org.ardestan.json.JsonCoreSearchResultItem;

/**
 * @author hnishino
 *
 */
public interface CoreSearchResultListener {
	public void notifyResult(JsonCoreSearchResultItem[] result);
}
