package org.ardestan.gui.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.ardestan.generator.ARPatchExpansionFailedException;
import org.ardestan.generator.ARSubpatchInstance;
import org.ardestan.generator.ArdestanSourceCodeGenerator;
import org.ardestan.gui.Message;
import org.ardestan.json.JsonProgramLoader;
import org.ardestan.json.JsonProgramWriter;



/**
 * @author hiroki
 *
 */
public class VisualProgramEditManager 
{
	protected 	double zoom;
	
	protected 	LinkedList<VisualProgramState> past;
	protected	LinkedList<VisualProgramState> future;

	protected 	Vector<ObjectBox> 	objectBoxes;
	protected	Vector<CommentBox>	commentBoxes;
		
	protected	Vector<ObjectBox>			selectedObjectBoxes;
	protected	Vector<CommentBox>			selectedCommentBoxes;
	
	protected	Set<ObjectBoxConnection> 	connections;
	protected 	ObjectBoxConnection			selectedConnection	;


	protected	Set<ObjectBox>	temporarilySelectedObjectBoxes;
	protected	Set<CommentBox>	temporarilySelectedCommentBoxes;
	
	protected	Point			regionStartPosition;
	protected	Point			regionCurrentPosition;
	protected	boolean			selectingMultipleObjects;
	protected	boolean			connectingObjects;

	protected	int				dragStartX;
	protected	int				dragStartY;
	protected	int				dragPrevX;
	protected	int				dragPrevY;
	
	ObjectBoxOutletInfo 		outletToConnect		; 
	protected	int				connectionCurrentX	;
	protected	int				connectionCurrentY	;

	protected 	BasicStroke defaultStroke 	 = new BasicStroke(1.0f);
	protected 	BasicStroke connectionStroke = new BasicStroke(1.2f);	
	
	protected	ObjectBox	objectBoxSelectedForEdit;
	
	protected	VisualProgramEditorPanel editor;
	
	
	protected 	Clipboard	clipboard;
	
	protected	int			sequentialCopyTimes;

	
	protected	boolean		changeMade = false;
	
	protected	boolean		loadedFromResource = false;
	
	/**
	 * 
	 * 
	 */
	public VisualProgramEditManager(VisualProgramEditorPanel editor)
	{
		this.editor = editor;
		
		this.selectedObjectBoxes = new Vector<ObjectBox>();
		this.selectedCommentBoxes = new Vector<CommentBox>();
		
		this.connections 	= new HashSet<ObjectBoxConnection>();

		
		this.past 	= new LinkedList<VisualProgramState>();
		this.future	= new LinkedList<VisualProgramState>();

		this.zoom = 1.0;
		this.objectBoxes = new Vector<ObjectBox>();
		this.commentBoxes  = new Vector<CommentBox>();

		this.temporarilySelectedObjectBoxes  = new HashSet<ObjectBox>();
		this.temporarilySelectedCommentBoxes = new HashSet<CommentBox>();
		
		this.regionStartPosition 	= new Point();
		this.regionCurrentPosition 	= new Point();
		
		this.clipboard = Clipboard.getInstance();

		
		return;
	}
	

	
	/**
	 * @return
	 */
	public boolean isChangeMade()
	{
		return changeMade;
	}
	
	/**
	 * @param newObjectBox
	 */
	public void addObjectBox(ObjectBox newObjectBox)
	{
		this.objectBoxes.add(newObjectBox);
		
		this.editor.addObjectBoxToProgramArea(newObjectBox);
	}
	
	/**
	 * @param newCommentBox
	 */
	public void addCommentBox(CommentBox newCommentBox)
	{
		this.commentBoxes.add(newCommentBox);
	}

	/**
	 * @param g
	 * @param offsetX
	 * @param offsetY
	 */
	public void draw(Graphics2D g, int offsetX, int offsetY)
	{
		//reset to the default stroke.
	    g.setStroke(defaultStroke);
	    
	    //draw comment boxes
	    for (CommentBox b: commentBoxes) {
	    	b.draw(g, this, offsetX, offsetY, zoom);
	    }
	    
	    
	    //draw object boxes
		for (ObjectBox b: objectBoxes) {
			b.draw(g, this, offsetX, offsetY, zoom);
		}
		
		
		g.setColor(Color.BLACK);
		
		
		if (this.selectingMultipleObjects) {
			int x = this.regionCurrentPosition.x < this.regionStartPosition.x ? this.regionCurrentPosition.x : this.regionStartPosition.x;
			int y = this.regionCurrentPosition.y < this.regionStartPosition.y ? this.regionCurrentPosition.y : this.regionStartPosition.y;
			int w = Math.abs(this.regionCurrentPosition.x - this.regionStartPosition.x);
			int h = Math.abs(this.regionCurrentPosition.y - this.regionStartPosition.y);
			
			x = (int)Math.floor(x * this.zoom - offsetX);
			y = (int)Math.floor(y * this.zoom - offsetY);
			w = (int)Math.floor(w * this.zoom);
			h = (int)Math.floor(h * this.zoom);

			g.drawRect(x, y, w, h);
		}
		
		//set to the connection stroke.
	    g.setStroke(connectionStroke);
		if (this.connectingObjects) {
			int x1 = (int)Math.floor(this.outletToConnect.center.x * zoom - offsetX);
			int y1 = (int)Math.floor(this.outletToConnect.center.y * zoom - offsetY);
			int x2 = (int)Math.floor(this.connectionCurrentX * zoom - offsetX);
			int y2 = (int)Math.floor(this.connectionCurrentY * zoom - offsetY);
		
			g.drawLine(x1, y1, x2, y2);
		}
		
		
		//draw connections
		for (ObjectBoxConnection c: this.connections) {
			Point opos = c.src.getOutletPostion(c.outletNo);
			Point ipos = c.dst.getInletPosition(c.inletNo);

			if ((this.selectedObjectBoxes.contains(c.src) || this.temporarilySelectedObjectBoxes.contains(c.src)) && 
				(this.selectedObjectBoxes.contains(c.dst) || this.temporarilySelectedObjectBoxes.contains(c.dst)) ){
				g.setColor(Color.BLUE);				
			}
			
			else if (selectedConnection == c){
				g.setColor(Color.BLUE);				
			}
			else {
				g.setColor(Color.BLACK);
			}

			g.drawLine(	(int)Math.ceil(opos.x * this.zoom - offsetX), (int)Math.ceil(opos.y * this.zoom) - offsetY, 
						(int)(ipos.x * this.zoom - offsetX), (int)(ipos.y * this.zoom) - offsetY);
		}
		
		return;
	}
	
	/**
	 * @param px
	 * @param py
	 * @return
	 */
	public ObjectBoxOutletInfo getOutletAt(int px, int py)
	{
		for (int i = objectBoxes.size() - 1; i >= 0; i--) {
			ObjectBoxOutletInfo oi = objectBoxes.get(i).getOutletAt(px - 5, py - 5, 10, 10);
			if (oi != null) {
				return oi;
			}
		}
		return null;
	}
	
	/**
	 * @param px
	 * @param py
	 * @return
	 */
	public boolean selectConnectionAt(int px, int py, int w, int h)
	{
		for (ObjectBoxConnection c: connections) {
		
			Point opos = c.src.getOutletPostion(c.outletNo);
			Point ipos = c.dst.getInletPosition(c.inletNo);
			
			if (doesIntersect(px    , py    , px + w, py    , opos.x, opos.y, ipos.x, ipos.y) ||
				doesIntersect(px    , py    , px    , py + h, opos.x, opos.y, ipos.x, ipos.y) ||
				doesIntersect(px + w, py    , px + w, py + h, opos.x, opos.y, ipos.x, ipos.y) ||
				doesIntersect(px    , py + h, px + w, py + h, opos.x, opos.y, ipos.x, ipos.y) ){
				selectedConnection = c;
				return true;
			}
		}
		selectedConnection = null;
		return false;
	}
	
	/**
	 * @param ax
	 * @param ay
	 * @param bx
	 * @param by
	 * @param cx
	 * @param cy
	 * @param dx
	 * @param dy
	 * @return
	 */
	public boolean doesIntersect(int ax, int ay, int bx, int by, int cx, int cy, int dx, int dy)
	{
		double d = (bx - ax) * (dy - cy) - (by - ay) * (dx - cx);
        double u = ((cx - ax) * (dy - cy) - (cy - ay) * (dx - cx)) / d;
        double v = ((cx - ax) * (by - ay) - (cy - ay) * (bx - ax)) / d;
        
        if ( u < 0 || u > 1 || v < 0 || v > 1) {
            return false;
        }
        return true;
	}
	
	/**
	 * @param px
	 * @param py
	 * @return
	 */
	public ObjectBoxInletInfo getInletAt(int px, int py)
	{
		for (int i = objectBoxes.size() - 1; i >= 0; i--) {
			ObjectBoxInletInfo ii = objectBoxes.get(i).getInletAt(px - 5, py - 5, 10, 10);
			if (ii != null) {
				return ii;
			}
		}
		return null;
	}
	/**
	 * @param outletInfo
	 */
	public void startConnecting(ObjectBoxOutletInfo outletInfo)
	{
		//this should be always found.
		this.outletToConnect = outletInfo;
		this.connectingObjects = true;
	}
	
	/**
	 * @param px
	 * @param py
	 */
	public void connecting(int px, int py)
	{
		this.connectionCurrentX = px;
		this.connectionCurrentY = py;
	}
	
	
	/**
	 * 
	 */
	public void removeInvalidConnections()
	{
		Iterator<ObjectBoxConnection> i = this.connections.iterator();
		while(i.hasNext()) {
			ObjectBoxConnection c = i.next();
			if (c.outletNo >= c.src.getNumOfOutlets() || c.inletNo >= c.dst.getNumOfInlets()) {
				i.remove();
			}
		}
	}
	
	/**
	 * @param px
	 * @param py
	 */
	public boolean finishConnecting(int px, int py)
	{
		this.connectingObjects = false;

		ObjectBoxInletInfo ii = this.getInletAt(px, py);
		if(ii == null) {
			return false;
		}
		
		//create a connection.
		
		ObjectBoxConnection nc = new ObjectBoxConnection();
		nc.src = this.outletToConnect.objectBox;
		nc.outletNo = this.outletToConnect.outletNo;
		nc.dst = ii.objectBox;
		nc.inletNo = ii.inletNo;
		
		
		//if the same connection exists, just neglect
		for (ObjectBoxConnection c: this.connections) {
			if (c.src == nc.src && c.outletNo == nc.outletNo && c.dst == nc.dst && c.inletNo == nc.inletNo) {
				return false;
			}
		}
		
		this.connections.add(nc);
		return true;
	}

	/**
	 * @param px1
	 * @param py1
	 * @param w1
	 * @param h1
	 * @param px2
	 * @param py2
	 * @param w2
	 * @param h2
	 * @return
	 */
	protected boolean isCollided(int px1, int py1, int w1, int h1,
								 int px2, int py2, int w2, int h2)
	{
		int cx1 = px1 + w1 / 2;
		int cy1 = py1 + h1 / 2;
		int cx2 = px2 + w2 / 2;
		int cy2 = py2 + h2 / 2;
		
		int distX = Math.abs(cx1 - cx2);
		int distY = Math.abs(cy1 - cy2);
		
		if (distX < (w1 + w2) / 2 && distY < (h1 + h2) / 2) {
			return true;
		}
		
		return false;
	}
	/**
	 * @param b
	 * @return
	 */
	public boolean isSelected(ObjectBox b)
	{
		return this.selectedObjectBoxes.contains(b) || this.temporarilySelectedObjectBoxes.contains(b);
	}
	
	/**
	 * @param b
	 * @return
	 */
	public boolean isSelected(CommentBox b)
	{
		return this.selectedCommentBoxes.contains(b) || this.temporarilySelectedCommentBoxes.contains(b);
	}
	
	/**
	 * @param b
	 * @return
	 */
	public boolean isSelectedForEdit(ObjectBox b) {
		return this.objectBoxSelectedForEdit == b;
	}
	
	/**
	 * @return
	 */
	public boolean isAnyObjectSelected()
	{
		if (this.selectedObjectBoxes.isEmpty() && this.selectedCommentBoxes.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 */
	public void save(File file) throws IOException
	{
		JsonProgramWriter pw = new JsonProgramWriter();
		
		Vector<ObjectBox> 			objs = new Vector<ObjectBox>(this.objectBoxes);
		Vector<ObjectBoxConnection> cons = new Vector<ObjectBoxConnection>(this.connections);
		Vector<CommentBox>			cmts = new Vector<CommentBox>(this.commentBoxes);
		
		pw.save(file, objs, cons, cmts);
		changeMade = false;
	}
	
	/**
	 * 
	 */
	public void generateArdestanSourceCodeForBuild(File file) throws IOException
	{
		ArdestanSourceCodeGenerator gen = new ArdestanSourceCodeGenerator();
		
		//create a copy as subpatch and expand it (to eliminate subpatches inside).
		Vector<ObjectBox> 			objs = new Vector<ObjectBox>(this.objectBoxes);
		Vector<ObjectBoxConnection> cons = new Vector<ObjectBoxConnection>(this.connections);
		Vector<CommentBox>			cmnts= new Vector<CommentBox>(this.commentBoxes);
		
		ARSubpatchInstance patch = new ARSubpatchInstance(objs, cons, cmnts);
		try {
			patch.expandSubpatches();
		}
		catch(ARPatchExpansionFailedException e) {
			e.printStackTrace();
			return;
		}
		gen.buildModel(patch.getObjectBoxes(), patch.getObjectBoxConnections());
		gen.generateProgram(file.getAbsolutePath());
	}
	
	/**
	 * @return
	 */
	public boolean isLoadedFromResource()
	{
		return this.loadedFromResource;
	}
	/**
	 * @param jsonFilename
	 * @throws IOException
	 */
	public void loadFromFile(String jsonFilename) throws IOException
	{
		JsonProgramLoader loader = new JsonProgramLoader();
		loader.loadFromFile(jsonFilename);
		this.setupThisPatch(loader);
		this.loadedFromResource = false;
		
		return;
	}
	
	/**
	 * @param resourceName
	 * @throws IOException
	 */
	public void loadFromResource(String resourceName) throws IOException
	{
		JsonProgramLoader loader = new JsonProgramLoader();
		loader.loadFromResource(resourceName);
		this.setupThisPatch(loader);
		
		this.loadedFromResource = true;

		return;
	}
	
	/**
	 * @param loader
	 */
	protected void setupThisPatch(JsonProgramLoader loader)
	{
		this.objectBoxes.clear();
		this.connections.clear();
		this.commentBoxes.clear();

		this.past.clear();
		this.future.clear();

		this.selectedObjectBoxes.clear();
		this.selectedCommentBoxes.clear();

		this.selectedConnection = null;
		this.temporarilySelectedObjectBoxes.clear();
		this.temporarilySelectedCommentBoxes.clear();

		this.objectBoxSelectedForEdit = null; 

		ARSubpatchInstance instance = loader.createARSubpatchInstance();

		for (ObjectBox box: instance.getObjectBoxes()) {
			this.objectBoxes.add(box);
			this.editor.addObjectBoxToProgramArea(box);
		}

		for (ObjectBoxConnection c: instance.getObjectBoxConnections()) {
			this.connections.add(c);
		}

		this.commentBoxes.addAll(loader.getCommentBoxes());

		return;
	}
	
	/**
	 * @return
	 */
	public ObjectBox getObjectBoxSelectedForEdit() {
		return this.objectBoxSelectedForEdit;
	}
	/**
	 * @param b
	 * @return
	 */
	public void startEditingObjectBox(ObjectBox b) 
	{	
		
		if (this.objectBoxSelectedForEdit != null) {
			this.objectBoxSelectedForEdit.setEditMode(false);
		}
		
		
		this.objectBoxSelectedForEdit = b;
		
		if (this.objectBoxSelectedForEdit == null) {
			return;
		}

		this.objectBoxSelectedForEdit.textField.updateTextBeforeEdit();
		this.objectBoxSelectedForEdit.setEditMode(true);


		return;
	}
	
	/**
	 * @return
	 */
	public boolean isEditingObjectBox()
	{
		return this.objectBoxSelectedForEdit != null;
	}

	/**
	 * 
	 */
	public boolean finishEditingObjectBox() 
	{

		if (this.objectBoxSelectedForEdit == null) {
			throw new RuntimeException("bug!!!");
		}

		boolean ret = this.objectBoxSelectedForEdit.textField.isTextUpdated();
		
		this.objectBoxSelectedForEdit.setEditMode(false);	
		this.objectBoxSelectedForEdit.boxTextUpdated();
		this.objectBoxSelectedForEdit = null;
		
		this.removeInvalidConnections();
		
		return ret;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public ObjectBox getObjectBoxAt(int px, int py)
	{
		ObjectBox found = null;
		for (int i = objectBoxes.size() - 1; i >= 0; i--) {
			if (objectBoxes.get(i).isInside(px, py)) {
				found = objectBoxes.get(i);
				break;
			}
		}
		return found;
	}
	
	
	/**
	 * @param px
	 * @param py
	 * @return
	 */
	public CommentBox getCommentBoxAt(int px, int py)
	{
		CommentBox found = null;
		
		for (int i = commentBoxes.size() - 1; i >= 0; i--) {
			if (commentBoxes.get(i).isInside(px, py)) {
				found = commentBoxes.get(i);
				break;
			}
		}
		
		return found;
	}
	/**
	 * 
	 */
	public void zoomIn() {
		this.zoom *= Math.sqrt(2);
	}
	
	/**
	 * 
	 */
	public void zoomOut() {
		this.zoom /= Math.sqrt(2);
	}
	
	
	/**
	 * 
	 */
	public void printObjects()
	{
		Message.println("----------------");
		Message.println("object box");
		for (ObjectBox b: this.objectBoxes) {
			Message.println(b.toString());
		}
		Message.println();
		Message.println("connections");
		for (ObjectBoxConnection c: this.connections) {
			Message.println(c.src + ":" + c.outletNo + "->" + c.dst + ":" + c.inletNo);
			if (c.src == null) {
				Message.println("???");
			}
		}
		
		return;
	}
	

	/**
	 * @param b
	 */
	public void select(ObjectBox b)
	{
		this.selectedObjectBoxes.add(b);
	}
	
	/**
	 * @param c
	 */
	public void select(CommentBox c)
	{
		this.selectedCommentBoxes.add(c);
	}
	
	/**
	 * @param b
	 */
	public void unselect(ObjectBox b)
	{
		this.selectedObjectBoxes.remove(b);
		return;
	}
	
	
	/**
	 * @param c
	 */
	public void unselect(CommentBox b)
	{
		this.selectedCommentBoxes.remove(b);
	}
	/**
	 * 
	 */
	public void unselectAllObjects()
	{
		this.selectedObjectBoxes.clear();
		this.selectedCommentBoxes.clear();
		return;
	}
	
	/**
	 * @param b
	 */
	public void selectOneAndUnselectOthers(ObjectBox b)
	{
		this.unselectAllObjects();
		this.selectedObjectBoxes.add(b);
		return;
	}
	
	/**
	 * @param b
	 */
	public void selectOneAndUnselectOthers(CommentBox b)
	{
		this.unselectAllObjects();
		this.selectedCommentBoxes.add(b);
	}
	/**
	 * @return
	 */
	public double getZoom() {
		return this.zoom;
	}
	
	
	/**
	 * 
	 */
	public void copySelectedObjectToClipboard(int mx, int my)
	{
		sequentialCopyTimes = 1;
		
		this.clipboard.clear();

		if (this.selectedObjectBoxes.size() == 0 && this.selectedCommentBoxes.size() == 0) {
			return;
		}
	
		HashMap<ObjectBox, ObjectBox> 	cloneMap = new HashMap<ObjectBox, ObjectBox>();
		
		Vector<ObjectBox>	clonedObjectBox 	= new Vector<ObjectBox>();
		Vector<ObjectBoxConnection>	clonedConnections 	= new Vector<ObjectBoxConnection>();
		for (ObjectBox ob: this.selectedObjectBoxes) {
			ObjectBox clone = ob.clone();
			cloneMap.put(ob, clone);		
			clonedObjectBox.add(clone);
		}
		
		for (ObjectBoxConnection c: this.connections) {
			//if both src and dst are contained in the selection, then copy the connection between them too.
			if (this.selectedObjectBoxes.contains(c.src) && this.selectedObjectBoxes.contains(c.dst)) {
				ObjectBoxConnection nc = new ObjectBoxConnection();
				nc.src 		= cloneMap.get(c.src);
				nc.outletNo = c.outletNo;
				nc.dst		= cloneMap.get(c.dst);
				nc.inletNo	= c.inletNo;
				clonedConnections.add(nc);
			}
		}
		
		Vector<CommentBox> clonedCommentBoxes = new Vector<CommentBox>(this.selectedCommentBoxes);

		this.clipboard.copyToClipboard(this, mx, my, clonedObjectBox, clonedConnections, clonedCommentBoxes);
	}
	
	/**
	 * 
	 */
	public void pasteFromClipboard(int mx, int my)
	{
		if (this.clipboard.isEmpty()) {
			return;
		}
		
		this.clipboard.pasteRequested(this, mx, my, this.sequentialCopyTimes);
		
		this.sequentialCopyTimes++;
	}
	
	/**
	 * @param boxes
	 */
	public void paste(Vector<ObjectBox> boxes, Vector<ObjectBoxConnection> connections, Vector<CommentBox> commentBoxes)
	{
		for (ObjectBox b: boxes) {
			this.editor.addObjectBoxToProgramArea(b);
		}
		
		
		this.objectBoxes.addAll(boxes);		
		this.connections.addAll(connections);
		this.commentBoxes.addAll(commentBoxes);
		
		this.unselectAllObjects();
		this.selectedObjectBoxes.addAll(boxes);
		this.selectedCommentBoxes.addAll(commentBoxes);
		
		return;
	}
	
	
	/**
	 * 
	 */
	public void cutSelectedObjects(int mx, int my)
	{		
		this.copySelectedObjectToClipboard(mx, my);
		this.removeSelectedObjects();
		
		this.sequentialCopyTimes = 0;
		return;
	}
	
	/**
	 * 
	 */
	public void removeSelectedObjects()
	{
		Vector<ObjectBox> objectsToKeep = new Vector<ObjectBox>();
		Set<ObjectBoxConnection> connectionsToKeep = new HashSet<ObjectBoxConnection>();
		
		for (ObjectBox o: this.objectBoxes) {
			if (this.selectedObjectBoxes.contains(o)) {
				this.editor.removeObjectBoxFromProgramArea(o);
				continue;
			}
			objectsToKeep.add(o);
		}
		
		for (ObjectBoxConnection c: this.connections) {
			if (c == selectedConnection) {
				continue;
			}
			if (this.selectedObjectBoxes.contains(c.src)|| this.selectedObjectBoxes.contains(c.dst)) {
				continue;
			}
			connectionsToKeep.add(c);
		}
		
		Vector<CommentBox> commentBoxToKeep = new Vector<CommentBox>();
		for (CommentBox c: this.commentBoxes) {
			if (this.selectedCommentBoxes.contains(c)) {
				continue;
			}
			commentBoxToKeep.add(c);
		}

		this.objectBoxes = objectsToKeep;
		this.connections = connectionsToKeep;
		this.commentBoxes = commentBoxToKeep;

		return;
	}
	/**
	 * @param px
	 * @param py
	 */
	public void deleteSelectedObjects()
	{
		this.removeSelectedObjects();
		this.selectedObjectBoxes.clear();
		this.selectedCommentBoxes.clear();

		return;
	}
	
	/**
	 * 
	 */
	
	public void startDraggingSelectedObjects(int px, int py)
	{		
		this.dragPrevX = this.dragStartX = px;
		this.dragPrevY = this.dragStartY = py;
			
		return;
		
	}
	
	/**
	 * @param diffX
	 * @param diffY
	 */
	public void dragSelectedObjects(int px, int py)
	{
		int diffX = px - this.dragPrevX;
		int diffY = py - this.dragPrevY;
		
		this.dragPrevX = px;
		this.dragPrevY = py;
				

		for (ObjectBox b: this.selectedObjectBoxes) {
			int newX = b.getX() + diffX;
			int newY = b.getY() + diffY;
			b.setX(newX);
			b.setY(newY);
		}		
	
		for (CommentBox b: this.selectedCommentBoxes) {
			int newX = b.getX() + diffX;
			int newY = b.getY() + diffY;
			b.setX(newX);
			b.setY(newY);
		}
	}
	
	/**
	 * @param dx
	 * @param dy
	 */
	public void moveSelectedObjectsRelative(int dx, int dy)
	{
		for (ObjectBox b: this.selectedObjectBoxes) {
			int newX = b.getX() + dx;
			int newY = b.getY() + dy;
			b.setX(newX);
			b.setY(newY);
		}		
	
		for (CommentBox b: this.selectedCommentBoxes) {
			int newX = b.getX() + dx;
			int newY = b.getY() + dy;
			b.setX(newX);
			b.setY(newY);
		}
	
	}
	
	/**
	 * @param px
	 * @param py
	 */
	public boolean finishDraggingSelectedObjects(int px, int py)
	{
		//if there was no actual dragging, then we don't have to keep the undo information
		if (this.dragStartX == px && this.dragStartY == py) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 */
	public void startSelectingMultipleObjectBoxes(int px, int py)
	{
		this.regionStartPosition.x = px;
		this.regionStartPosition.y = py;
		
		this.regionCurrentPosition.x = px;
		this.regionCurrentPosition.y = py;

		this.selectingMultipleObjects = true;
		
		return;
	}
	
	/**
	 * 
	 */
	public void selectAll()
	{
		this.selectedObjectBoxes.clear();
		this.selectedObjectBoxes.addAll(this.objectBoxes);
		
		this.selectedCommentBoxes.clear();
		this.selectedCommentBoxes.addAll(this.commentBoxes);
		
		return;
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void selectingMultipleObjectBoxes(int px, int py)
	{
		this.regionCurrentPosition.x = px;
		this.regionCurrentPosition.y = py;
		
		int x1 = this.regionCurrentPosition.x < this.regionStartPosition.x ? this.regionCurrentPosition.x : this.regionStartPosition.x;
		int y1 = this.regionCurrentPosition.y < this.regionStartPosition.y ? this.regionCurrentPosition.y : this.regionStartPosition.y;
		
		int w1 = Math.abs(this.regionCurrentPosition.x - this.regionStartPosition.x);
		int h1 = Math.abs(this.regionCurrentPosition.y - this.regionStartPosition.y);
		
		
		this.temporarilySelectedObjectBoxes.clear();
		for (ObjectBox ob: this.objectBoxes) {
			int x2 = ob.getX();
			int y2 = ob.getY();
			int w2 = ob.getWidth();
			int h2 = ob.getHeight();
			
			
			boolean inside = this.isCollided(x1, y1, w1, h1, x2, y2, w2, h2);
			if (inside) {
				this.temporarilySelectedObjectBoxes.add(ob);
			}
		}
		
		this.temporarilySelectedCommentBoxes.clear();
		for (CommentBox cb: this.commentBoxes) {
			int x2 = cb.getX();
			int y2 = cb.getY();
			int w2 = cb.getWidth();
			int h2 = cb.getHeight();
			
			boolean inside = this.isCollided(x1, y1, w1, h1, x2, y2, w2, h2);
			if (inside) {
				this.temporarilySelectedCommentBoxes.add(cb);
			}
		}
	}
	
	/**
	 */
	public boolean finishSelectingMultipleObjectBoxes()
	{
		this.selectingMultipleObjects = false;
		if (this.temporarilySelectedObjectBoxes.size() == 0 && this.temporarilySelectedCommentBoxes.size() == 0) {
			return false;
		}
		sequentialCopyTimes = 1;
		
		this.selectedObjectBoxes	.addAll(this.temporarilySelectedObjectBoxes);
		this.selectedCommentBoxes	.addAll(this.temporarilySelectedCommentBoxes);
		
		this.temporarilySelectedObjectBoxes.clear();
		this.temporarilySelectedCommentBoxes.clear();
		
		return true;
	}
	
	/**
	 * @param g
	 * @param canvas
	 * @return
	 */
	public Point updateDrawingParameters(Graphics2D g)
	{
		int programRight  = Integer.MIN_VALUE;
		int programBottom = Integer.MIN_VALUE;
				
		for (ObjectBox o: objectBoxes) {
			o.updateDrawParameters(g, this.zoom);

			int l = o.getScaledX();
			int u = o.getScaledY();
			int r = l + o.getScaledWidth();
			int b = u + o.getScaledHeight();
			
			
			programRight  = programRight  < r ? r : programRight;
			programBottom = programBottom < b ? b : programBottom;
		}
		
		for (CommentBox c: commentBoxes) {
			c.updateDrawParameters(g, this.zoom);

			int l = c.getScaledX();
			int u = c.getScaledY();
			int r = l + c.getScaledWidth();
			int b = u + c.getScaledHeight();
			
			programRight  = programRight  < r ? r : programRight;
			programBottom = programBottom < b ? b : programBottom;
			
		}
		
		Point r = new Point(programRight, programBottom);
		
		return r;
	}
	

	


	/**
	 * 
	 */
	public void removeAllObjectBoxFromProgramArea()
	{
		for (ObjectBox ob: this.objectBoxes) {
			editor.removeObjectBoxFromProgramArea(ob);
		}
	}
	
	/**
	 * 
	 */
	public void addAllOjectBoxToProgamArea()
	{
		for (ObjectBox ob: this.objectBoxes) {
			editor.addObjectBoxToProgramArea(ob);
		}
		return;
	}
	
	
	
	/**
	 * 
	 */
	protected void pushPast()
	{	
		changeMade = true;

		VisualProgramState s = new VisualProgramState();
		s.zoom = this.zoom;
		
		s.objectBoxes 			= new Vector<ObjectBox>(this.objectBoxes);		
		s.selectedObjectBoxes	= new Vector<ObjectBox>(this.selectedObjectBoxes);
		s.connections 			= new HashSet<ObjectBoxConnection>(this.connections);
		
		s.commentBoxes			= new Vector<CommentBox>(this.commentBoxes);
		s.selectedCommentBoxes	= new Vector<CommentBox>(this.selectedCommentBoxes);
				
		s.objectBoxSelectedForEdit = this.objectBoxSelectedForEdit;
		s.sequentialCopyTimes = this.sequentialCopyTimes;

		past.addFirst(s);
		
		
		return;
	}
	
	/**
	 * 
	 */
	protected void pushFuture()
	{
		VisualProgramState s = new VisualProgramState();
		s.zoom = this.zoom;
				
		s.objectBoxes 			= new Vector<ObjectBox>(this.objectBoxes);
		s.selectedObjectBoxes	= new Vector<ObjectBox>(this.selectedObjectBoxes);
		s.connections			= new HashSet<ObjectBoxConnection>(this.connections);
		
		s.commentBoxes			= new Vector<CommentBox>(this.commentBoxes);
		s.selectedCommentBoxes	= new Vector<CommentBox>(this.selectedCommentBoxes);
		
		s.objectBoxSelectedForEdit = this.objectBoxSelectedForEdit;
		s.sequentialCopyTimes = this.sequentialCopyTimes;
	
		future.addFirst(s);
	}
	
	
	/**
	 * 
	 */
	public void clearFuture()
	{
		this.future.clear();
	}
	
	
	/**
	 * @return
	 */
	public int getPastSize()
	{
		return past.size();
	}
	
	/**
	 * @return
	 */
	public int getFutureSize()
	{
		return future.size();
	}
	
	/**
	 * 
	 */
	protected void popPast()
	{
		if (past.size() == 0) {
			return;
		}
		
		changeMade = true;

		this.removeAllObjectBoxFromProgramArea();
		
		VisualProgramState s = past.pop();
		this.zoom = s.zoom;

		this.objectBoxes 			= s.objectBoxes;
		this.selectedObjectBoxes	= s.selectedObjectBoxes;
		this.connections 			= s.connections;
		
		this.commentBoxes			= s.commentBoxes;
		this.selectedCommentBoxes	= s.selectedCommentBoxes;

		this.objectBoxSelectedForEdit = s.objectBoxSelectedForEdit;
		this.sequentialCopyTimes = s.sequentialCopyTimes;
		
		this.addAllOjectBoxToProgamArea();
		
		if (this.objectBoxSelectedForEdit != null) {
			this.objectBoxSelectedForEdit.grabFocus();
		}
	}
	
	/**
	 * 
	 */
	protected void popFuture()
	{
		if (future.size() == 0) {
			return;
		}
		
		this.removeAllObjectBoxFromProgramArea();
		
		VisualProgramState s = future.pop();
		this.zoom = s.zoom;
		
		this.objectBoxes 			= s.objectBoxes;
		this.selectedObjectBoxes	= s.selectedObjectBoxes;
		this.connections 			= s.connections;
		
		this.commentBoxes			= s.commentBoxes;
		this.selectedCommentBoxes	= s.selectedCommentBoxes;
		
		this.objectBoxSelectedForEdit = s.objectBoxSelectedForEdit;
		this.sequentialCopyTimes = s.sequentialCopyTimes;
		
		this.addAllOjectBoxToProgamArea();
		
		if (this.objectBoxSelectedForEdit != null) {
			this.objectBoxSelectedForEdit.grabFocus();
		}
		
	}
	
	/**
	 * 
	 */
	public void createDeepCopy()
	{
		//printObjects();
		
		//first remove all the objects from canvas.
		this.removeAllObjectBoxFromProgramArea();
		
		//then create deep copies
		Vector<ObjectBox> 			deepCopiedObjectBoxes 			= new Vector<ObjectBox>();
		Vector<ObjectBox> 			deepCopiedSelectedObjectBoxes 	= new Vector<ObjectBox>();

		Set<ObjectBoxConnection>	deepCopiedConnections 			= new HashSet<ObjectBoxConnection>();

		Vector<CommentBox>			deepCopiedCommentBoxes			= new Vector<CommentBox>();
		Vector<CommentBox>			deepCopiedSelectedCommentBoxes	= new Vector<CommentBox>();
		
		Map<ObjectBox, ObjectBox> cloneMap = new HashMap<ObjectBox, ObjectBox>();
		
		for (ObjectBox b: this.objectBoxes) {
			//make a clone and map between their references.
			ObjectBox clone = b.clone();
			cloneMap.put(b, clone);
			deepCopiedObjectBoxes.add(clone);
			
			if (this.selectedObjectBoxes.contains(b)) {
				deepCopiedSelectedObjectBoxes.add(clone);
			}
		}
		
		for (ObjectBoxConnection c: this.connections) {
			ObjectBox src = cloneMap.get(c.src);
			ObjectBox dst = cloneMap.get(c.dst);
			ObjectBoxConnection nc = new ObjectBoxConnection();
			nc.src = src;
			nc.outletNo = c.outletNo;
			nc.dst = dst;
			nc.inletNo = c.inletNo;
			
			deepCopiedConnections.add(nc);
			if (c == selectedConnection) {
				this.selectedConnection = nc;
			}
		}
		
		for (CommentBox b: this.commentBoxes) {
			CommentBox clone = b.clone();
			
			deepCopiedCommentBoxes.add(clone);
			if (this.selectedCommentBoxes.contains(b)) {
				deepCopiedSelectedCommentBoxes.add(clone);
			}
		}

		
		this.objectBoxes 			= deepCopiedObjectBoxes;
		this.selectedObjectBoxes	= deepCopiedSelectedObjectBoxes;
		this.connections			= deepCopiedConnections;
		
		this.commentBoxes			= deepCopiedCommentBoxes;
		this.selectedCommentBoxes	= deepCopiedSelectedCommentBoxes;
		
		this.objectBoxSelectedForEdit = cloneMap.get(this.objectBoxSelectedForEdit);

		if (this.outletToConnect != null) {
			this.outletToConnect.objectBox = cloneMap.get(this.outletToConnect.objectBox);
		}
	
		//add objects to the program area
		this.addAllOjectBoxToProgamArea();
	}
	
	
	/* (non-Javadoc)
	 * @see com.rhythmgeek.ardestan.generator.ARSubpatchUpdateListener#subpatchUpdated()
	 */
	public void subpatchUpdated()
	{
		for (ObjectBox b: objectBoxes) {
			b.boxTextUpdated();
			b.updateNumOfInletsAndOutlets();
		}

		this.removeInvalidConnections();
	}


	
}
