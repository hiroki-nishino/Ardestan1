package org.ardestan.gui.visual;

import java.util.Set;
import java.util.Vector;

/**
 * @author hiroki
 *
 */
public class VisualProgramState {
	public double zoom;
	public Vector<ObjectBox> 		objectBoxes;
	public Vector<ObjectBox>		selectedObjectBoxes;
	public Vector<CommentBox>		commentBoxes;
	public Vector<CommentBox>		selectedCommentBoxes;
	
	public Set<ObjectBoxConnection>	connections;
	public ObjectBox				objectBoxSelectedForEdit;
	public Clipboard				clipboard;
	public int						sequentialCopyTimes;
}
