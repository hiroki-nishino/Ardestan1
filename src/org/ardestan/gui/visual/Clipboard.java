package org.ardestan.gui.visual;

import java.util.HashMap;
import java.util.Vector;

class Clipboard {

	private static Clipboard singleton;

	private VisualProgramEditManager		srcEditManager	;
	
	private Vector<ObjectBox>				objectBoxes		;
	private Vector<ObjectBoxConnection>		connections		;
	private Vector<CommentBox>				commentBoxes	;

	
	private int sequentialCopyTimes = 0;
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
		commentBoxes	= null;
	}
	
	/**
	 * @param srcEditManager
	 * @param mx
	 * @param my
	 * @param objectBoxes
	 * @param connections
	 */
	public void copyToClipboard(VisualProgramEditManager srcEditManager,
								Vector<ObjectBox> objectBoxes, Vector<ObjectBoxConnection> connections, Vector<CommentBox> commentBoxes)
	{
		this.srcEditManager = srcEditManager;
		this.objectBoxes = objectBoxes;
		this.connections = connections;
		this.commentBoxes = commentBoxes;
	
		this.sequentialCopyTimes = 1;
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
	public void pasteRequested(VisualProgramEditManager editManager)
	{
		if (objectBoxes == null) {
			return;
		}
		
		//if we are copying to another editor, reset the copy 
		if (srcEditManager != editManager){
			sequentialCopyTimes = 0;
			srcEditManager = editManager;
		}
		
		HashMap<ObjectBox, ObjectBox> 	cloneMap 			= new HashMap<ObjectBox, ObjectBox>();
		Vector<ObjectBox> 				clonedObjects 		= new Vector<ObjectBox>();
		Vector<ObjectBoxConnection> 	clonedConnections 	= new Vector<ObjectBoxConnection>();
		Vector<CommentBox>				clonedCommentBoxes	= new Vector<CommentBox>();
		
		
	
		
		for (ObjectBox ob: this.objectBoxes) {
			ObjectBox clone = ob.clone();

			int x, y;
			
			x = ob.getX() + ObjectBox.CopyDiffX * sequentialCopyTimes;
			y = ob.getY() + ObjectBox.CopyDiffY * sequentialCopyTimes;
		
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
			
			x = cb.getX() + ObjectBox.CopyDiffX * sequentialCopyTimes;
			y = cb.getY() + ObjectBox.CopyDiffY * sequentialCopyTimes;
			clone.setX(x);
			clone.setY(y);

			clonedCommentBoxes.add(clone);
		}

		editManager.paste(clonedObjects, clonedConnections, clonedCommentBoxes);
	
		sequentialCopyTimes++;
	}
}