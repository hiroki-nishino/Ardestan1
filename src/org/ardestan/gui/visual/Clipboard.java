package org.ardestan.gui.visual;

import java.util.HashMap;
import java.util.Vector;

class Clipboard {

	private static Clipboard singleton;

	private VisualProgramEditManager		srcEditManager	;
	
	private Vector<ObjectBox>				objectBoxes		;
	private Vector<ObjectBoxConnection>		connections		;
	private Vector<CommentBox>				commentBoxes	;

	int originalMouseX;
	int originalMouseY;
	
	int currentPasteMouseX;
	int currentPasteMouseY;
	
	/**
	 * @return
	 */
	public static Clipboard getInstance() {
		if (singleton == null) {
			singleton = new Clipboard();
		}
		
		return singleton;
	}
	/**
	 * 
	 */
	private Clipboard() {
	}
	
	/**
	 * 
	 */
	public void clear() {
		srcEditManager 	= null;
		objectBoxes 	= null;
		connections 	= null;
	}
	
	/**
	 * @param srcEditManager
	 * @param mx
	 * @param my
	 * @param objectBoxes
	 * @param connections
	 */
	public void copyToClipboard(VisualProgramEditManager srcEditManager, int mx, int my,
							Vector<ObjectBox> objectBoxes, Vector<ObjectBoxConnection> connections, Vector<CommentBox> commentBoxes)
	{
		this.srcEditManager = srcEditManager;
		this.objectBoxes = objectBoxes;
		this.connections = connections;
		this.commentBoxes = commentBoxes;
		
		this.originalMouseX = mx;
		this.originalMouseY = my;
	}

	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return objectBoxes == null;
	}
	

	/**
	 * @param editManager
	 * @param sequentialCopyTimes
	 */
	public void pasteRequested(VisualProgramEditManager editManager, int mx, int my, int sequentialCopyTimes)
	{
		if (objectBoxes == null) {
			return;
		}
		
		HashMap<ObjectBox, ObjectBox> 	cloneMap 			= new HashMap<ObjectBox, ObjectBox>();
		Vector<ObjectBox> 				clonedObjects 		= new Vector<ObjectBox>();
		Vector<ObjectBoxConnection> 	clonedConnections 	= new Vector<ObjectBoxConnection>();
		Vector<CommentBox>				clonedCommentBoxes	= new Vector<CommentBox>();
		
		//check if any object goes out of the left/upper boundary of the canvas,
		//then, try to shift the location so that all the  within 
		int offsetX = 0;
		int offsetY = 0;
		
		if (srcEditManager != editManager){
			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			for (ObjectBox ob: this.objectBoxes) {
				int x = ob.getX() - originalMouseX + mx;
				int y = ob.getY() - originalMouseY + my;

				minX = minX < x ? minX : x;
				minY = minY < y ? minY : y;
			}
			
			if (minX < 0){
				offsetX = -minX;
			}
			if (minY < 0) {
				offsetY = -minY;
			}
		}
		
		for (ObjectBox ob: this.objectBoxes) {
			ObjectBox clone = ob.clone();

			int x, y;
			
			if (srcEditManager == editManager) {
				x = ob.getX() + ObjectBox.CopyDiffX * sequentialCopyTimes;
				y = ob.getY() + ObjectBox.CopyDiffY * sequentialCopyTimes;
			}
			else {
				if (sequentialCopyTimes == 0) {
					currentPasteMouseX = mx;
					currentPasteMouseY = my;
				}
				x = ob.getX() - originalMouseX + currentPasteMouseX + offsetX + ObjectBox.CopyDiffX * sequentialCopyTimes;
				y = ob.getY() - originalMouseY + currentPasteMouseY + offsetY + ObjectBox.CopyDiffY * sequentialCopyTimes;
			}
			clone.setX(x);
			clone.setY(y);

			clonedObjects.add(clone);
			
			cloneMap.put(ob, clone);
		}
		
		for (ObjectBoxConnection c: this.connections) {
			ObjectBoxConnection nc = new ObjectBoxConnection();
			nc.src 		= cloneMap.get(c.src);
			nc.outletNo = c.outletNo;
			nc.dst		= cloneMap.get(c.dst);
			nc.inletNo	= c.inletNo;
			clonedConnections.add(nc);
		}
		
		
		for (CommentBox cb: this.commentBoxes) {
			CommentBox clone = cb.clone();

			int x, y;
			
			if (srcEditManager == editManager) {
				x = cb.getX() + ObjectBox.CopyDiffX * sequentialCopyTimes;
				y = cb.getY() + ObjectBox.CopyDiffY * sequentialCopyTimes;
			}
			else {
				if (sequentialCopyTimes == 0) {
					currentPasteMouseX = mx;
					currentPasteMouseY = my;
				}
				x = cb.getX() - originalMouseX + currentPasteMouseX + offsetX + ObjectBox.CopyDiffX * sequentialCopyTimes;
				y = cb.getY() - originalMouseY + currentPasteMouseY + offsetY + ObjectBox.CopyDiffY * sequentialCopyTimes;
			}
			clone.setX(x);
			clone.setY(y);

			clonedCommentBoxes.add(clone);
		}
		
		editManager.paste(clonedObjects, clonedConnections, clonedCommentBoxes);
		
	}
}