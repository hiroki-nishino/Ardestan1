package org.ardestan.gui.explorer;



import org.ardestan.gui.DnDTabbedPane;
import org.ardestan.gui.ProjectDirectoryListener;

/**
 * @author hnishino
 *
 */
public class TabbedExplorerPane extends DnDTabbedPane implements ProjectDirectoryListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int DISTANT_TO_EDGE_FOR_RESIZE = 10;

	protected FileTreePanel	fileTreePanel;
		
	/**
	 * 
	 */
	public TabbedExplorerPane()
	{
		fileTreePanel = new FileTreePanel();
		
		fileTreePanel.updateTree();
		this.add("Project", fileTreePanel);

	}
	


	@Override
	public void projectDirectoryChanged() {
		fileTreePanel.updateTree();
	}
	

	
}
