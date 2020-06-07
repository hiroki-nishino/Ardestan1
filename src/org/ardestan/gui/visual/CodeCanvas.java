package org.ardestan.gui.visual;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;

import org.ardestan.arduinocli.ArduinoCLIBackground;
import org.ardestan.gui.ArdestanIDE;
import org.ardestan.gui.MainWindow;
import org.ardestan.gui.MenuItemText;
import org.ardestan.gui.Message;
import org.ardestan.gui.dialog.commentbox.CommentBoxDialog;
import org.ardestan.misc.ProjectSetting;

/**
 * @author hnishino
 *
 */
public class CodeCanvas extends JPanel implements MouseListener, MouseMotionListener, ActionListener, MouseWheelListener, KeyListener, FocusListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	protected MouseMode 	mouseMode	;
	protected ShiftKeyMode 	shiftKeyMode;

	protected JPopupMenu 	popupMenu;
	protected JMenuItem	 	helpItem;

	protected String		helpObjectName;

	protected VisualProgramEditorPanel editor;
	

	/**
	 * @param editor
	 */
	public CodeCanvas(VisualProgramEditorPanel editor) 
	{
		this.editor = editor;
		this.mouseMode = MouseMode.NONE;
		this.shiftKeyMode = ShiftKeyMode.NOT_PRESSED;

		
		this.setFocusable(true);

		setBackground(Color.WHITE);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);	
		this.addKeyListener(this);
		
		this.helpItem	= new JMenuItem(MenuItemText.HELP);
		this.helpItem.addActionListener(this);

		this.popupMenu 	= new JPopupMenu();
		this.popupMenu.add(helpItem);
		
		return;
	}

	

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftKeyMode = ShiftKeyMode.NOT_PRESSED;
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			editor.unselectAllObjects();
			this.repaint();
		}

		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftKeyMode = ShiftKeyMode.PRESSED;
		}

		if (e.getSource() == this && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			editor.pushPast();
			editor.deleteSelectedObjects();
			this.repaint();
		}

		//we move the object only when the canvas has a focus.
		if (this.hasFocus() == false) {
			return;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			editor.pushPast();
			editor.createDeepCopy();
			editor.moveSelectedObjectsRelative(0, -1);
			this.repaint();
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			editor.pushPast();
			editor.createDeepCopy();
			editor.moveSelectedObjectsRelative(0, 1);
			this.repaint();			
		}

		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			editor.pushPast();
			editor.createDeepCopy();
			editor.moveSelectedObjectsRelative(-1, 0);
			this.repaint();			
		}
		
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			editor.pushPast();
			editor.createDeepCopy();
			editor.moveSelectedObjectsRelative(1, 0);
			this.repaint();			
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) 
	{

		Graphics2D g2d = (Graphics2D)g;

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

		g2d.setColor(Color.BLACK);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		editor.draw(g2d, this);			

	}

	//mouse events.
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		//we handle only double click here.
		if (e.getClickCount() < 2) {
			return;
		}
		
		int mx = e.getX();
		int my = e.getY();

		CommentBox cb = editor.getCommentBoxAt(mx,  my);
		if (cb != null) {
			editor.pushPast();
			editor.createDeepCopy();
			
			CommentBoxDialog dialog = new CommentBoxDialog(cb);
			

			JRootPane pane = this.getRootPane();
			int width	= 800;
			int height 	= 300;
			int x = pane.getX() + pane.getWidth() / 2 - width / 2;
			int y = pane.getY() + pane.getHeight() / 2 - height / 2;
			
			dialog.setLocation(x, y);
			dialog.setSize(width, height);
			
			dialog.setVisible(true);
			
			boolean edited = dialog.isCanceled();
			if (edited == true) {
				editor.popPast();
				
			}
			else {
				editor.clearFuture();
				CommentBox ncb = editor.getCommentBoxAt(mx,  my);
				ncb.setComment(dialog.getComment());
				ncb.setColor(dialog.getColor());
				ncb.setFontName(dialog.getFontName());
				ncb.setFontSize(dialog.getFontSize());
				editor.unselect(ncb);
				this.repaint();
			}
		}
		
		return;
	}

	/**
	 * 
	 */
	/**
	 * @param mx
	 * @param my
	 */
	public void showPopupMenu(int mx, int my)
	{
		ObjectBox ob = editor.getObjectBoxAt(mx, my);
		helpObjectName =  null;
		if (ob != null) {
			String boxText = ob.getBoxText();
			String[] args = boxText.split(" ");
			if (args.length > 0) {
				helpObjectName = args[0];
			}
			popupMenu.show(this, mx, my);
		}
		return;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) 
	{
		int mx = e.getX();
		int my = e.getY();


		if (e.isPopupTrigger()) {
			showPopupMenu(mx, my);
			return;
		}
		

		this.grabFocus();
		
		if (editor.getObjectBoxSelectedForEdit() != null) {
			objectBoxEditFinished();
//			this.mouseMode = MouseMode.NONE;
//			return;
		}

	
		//if the mouse mode is not none, then just return.
		if (this.mouseMode != MouseMode.NONE) {
			return;
		}

		//first let's check if we can find an object outlet there.
		ObjectBoxOutletInfo oi = editor.getOutletAt(mx, my);
		if (oi != null) {
			this.mouseMode = MouseMode.CONNECTING;
			editor.pushPast();
			editor.startConnecting(oi);
			editor.connecting(mx, my);
			return;
		}


		//get the object at the current cursor position.
		ObjectBox ob = editor.getObjectBoxAt(mx, my);

		//if there is any object, select the object.
		if (ob != null) {
			editor.pushPast();

			//select the object
			if (editor.isSelected(ob)) {
				if (this.shiftKeyMode == ShiftKeyMode.PRESSED) {
					editor.unselect(ob);
					this.repaint();
					return;
				}
			}
			else {
				if (this.shiftKeyMode == ShiftKeyMode.PRESSED) {
					editor.select(ob);
				}
				else {
					editor.selectOneAndUnselectOthers(ob);
				}
			}
			//start dragging
			this.mouseMode = MouseMode.DRAGGING_OBJECTS;		
			editor.pushPast();
			editor.createDeepCopy();
			editor.startDraggingSelectedObjects(mx, my);
			this.repaint();
			return;
		}
		
		//get the comment box at the current cursor position;
		CommentBox cb = editor.getCommentBoxAt(mx,  my);
		
		//if there is any comment box found, select the comment box.
		if (cb != null) {
			editor.pushPast();

			//select the object
			if (editor.isSelected(cb)) {
				if (this.shiftKeyMode == ShiftKeyMode.PRESSED) {
					editor.unselect(cb);
					this.repaint();
					return;
				}
			}
			else {
				if (this.shiftKeyMode == ShiftKeyMode.PRESSED) {
					editor.select(cb);
				}
				else {
					editor.selectOneAndUnselectOthers(cb);
				}
			}
			//start dragging
			this.mouseMode = MouseMode.DRAGGING_OBJECTS;		
			editor.pushPast();
			editor.createDeepCopy();
			editor.startDraggingSelectedObjects(mx, my);
			this.repaint();
			return;		
		}

	
		//no object found, start multiple object selections
		editor.pushPast();
		if (this.shiftKeyMode == ShiftKeyMode.NOT_PRESSED) {
			if (editor.isAnyObjectSelected()) {
				editor.unselectAllObjects();
				editor.pushPast();
			}
			else {
				editor.unselectAllObjects();
			}
			
		}
		this.mouseMode = MouseMode.SELECTING_MULTIPLE_OBJECTS;
		editor.startSelectingMultipleObjects(e.getX(), e.getY());		

		return;
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{	
		int mx = e.getX();
		int my = e.getY();
		
		if (e.isPopupTrigger()) {
			showPopupMenu(mx, my);
			return;
		}
		
		if (this.mouseMode == MouseMode.NONE) {
			return;
		}

		boolean effectiveChangeMade = false;
		switch(this.mouseMode)
		{
		case DRAGGING_OBJECTS:
			effectiveChangeMade = editor.finishDraggingSelectedObjects(mx, my);
			break;

		case SELECTING_MULTIPLE_OBJECTS:	
			effectiveChangeMade = editor.finishSelectingMultipleObjects();			
			break;

		case CONNECTING:
			effectiveChangeMade = editor.finishConnecting(mx, my);
			break;

		default:
			break;
		}
		
		//if any effective change made, clear the future.
		if (effectiveChangeMade) {
			editor.clearFuture();
		}
		//if not just pop the past.	
		else {
			editor.popPast();	
		}


		ObjectBox ob = editor.getObjectBoxAt(mx, my);
		if (ob != null) {
			if (editor.getObjectBoxSelectedForEdit() != ob) {
				editor.startEditingObjectBox(ob);
			}
			ob.grabFocus();
			ob.selectAll();
		}

		this.mouseMode = MouseMode.NONE;
		this.repaint();

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.requestFocus();
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		int mx = e.getX();
		int my = e.getY();

		switch (this.mouseMode)
		{
		case DRAGGING_OBJECTS:
			editor.dragSelectedObjects(mx, my);
			break;

		case SELECTING_MULTIPLE_OBJECTS:
			editor.selectingMultipleObjects(mx, my);
			break;

		case CONNECTING:
			editor.connecting(mx, my);
			break;

		default:
			break;
		}

		this.repaint();
	}

	
	@Override
	public void mouseMoved(MouseEvent e) 
	{			
		int mx = e.getX();
		int my = e.getY();

		ObjectBox b = editor.getObjectBoxAt(mx, my);

		switch(this.mouseMode) {
		case NONE:
			if (b != null &&
				this.getCursor() != Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)) {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));			
			}
			else if (editor.mouseMoved(mx, my) && 
					this.getCursor() != Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)){
				this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}
			else if (this.getCursor() != Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)){
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
			break;

		default:	
		}
		
		if (b != null && b.isValid() == false) {
			editor.setObjectBoxUnderMouseCursor(b);
		}
		else {
			editor.setObjectBoxUnderMouseCursor(null);
		}
		this.repaint();
	}

	
	/**
	 * 
	 */
	public void objectBoxEditFinished()
	{
		ObjectBox 	selectedObjectBox = editor.getObjectBoxSelectedForEdit();
		
		if (selectedObjectBox == null) {
			return;
		}
		
		//no effective change.
		if (!selectedObjectBox.isTextUpdated()) {
			editor.finishEditingObjectBox();
			this.repaint();
			return;
		}
		
		
		editor.pushPast();
		editor.createDeepCopy();
		selectedObjectBox.resetText();
		
		editor.finishEditingObjectBox();
		editor.clearFuture();

		this.repaint();
		return;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object obj = e.getSource();

		if (obj instanceof ObjectBoxTextField){
			objectBoxEditFinished();
			editor.setObjectBoxUnderMouseCursor(null);
			this.repaint();			
			return;
		}
		
		if (obj instanceof JMenuItem == false) {
			return;
		}
		
		if (obj == helpItem) {
			Message.println("help requested:" + this.helpObjectName);
			MainWindow window = ArdestanIDE.getMainWindow();
			window.loadHelpFile(this.helpObjectName);
			this.repaint();			
			return;
		}

		JMenuItem item = (JMenuItem)obj;
		String menuItemText = item.getText();
		
		if (menuItemText.equals(MenuItemText.SAVE_FILE)) {
			try {
				editor.save();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.SAVE_FILE_AS)) {
			try {
				editor.saveAs(null);
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.UNDO)) {
			editor.undo();
			
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.REDO)) {
			editor.redo();
			
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.CUT)) {
			editor.pushPast();
			editor.createDeepCopy();
			editor.clearFuture();
			editor.cutSelectedObjects();
			
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.SELECT_ALL)) {
			editor.pushPast();
			editor.selectAll();
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.COPY)) {
			editor.copySelectedObjectToClipboard();
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.PASTE)) {
			editor.pushPast();
			editor.clearFuture();
			editor.pasteFromClipboard();
			
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.ZOOM_IN)) {
			ZoomRequest zr = new ZoomRequest();
			zr.req = ZoomRequestState.ZOOM_IN;
			zr.canvasX = this.getWidth() / 2;
			zr.canvasY = this.getHeight() / 2;
			editor.zoomRequested(zr);
			
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.ZOOM_OUT)) {
			ZoomRequest zr = new ZoomRequest();
			zr.req = ZoomRequestState.ZOOM_OUT;
			zr.canvasX = this.getWidth() / 2;
			zr.canvasY = this.getHeight() / 2;
			editor.zoomRequested(zr);
			
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.OBJECT_BOX)) {
			if (this.mouseMode == MouseMode.NONE) {
				Point p = this.getMousePosition();
				
				int boxX = 0;
				int boxY = 0;
				if (p != null) {
					boxX = p.x;
					boxY = p.y;
				}
				
				editor.pushPast();
				editor.clearFuture();
				
				ObjectBox ob = editor.createObjectBox(boxX, boxY);

				editor.startEditingObjectBox(ob);
				ob.grabFocus();
 
			}
			
			this.repaint();			
			return;
		}
		
		if (menuItemText.equals(MenuItemText.COMMENT_BOX)) {
			if (this.mouseMode == MouseMode.NONE) {
				Point p = this.getMousePosition();
				
				int boxX = 0;
				int boxY = 0;
				if (p != null) {
					boxX = p.x;
					boxY = p.y;
				}
				
				editor.pushPast();
				editor.clearFuture();
				
				editor.createCommentBox(boxX, boxY);
 
			}
			this.repaint();			
			return;

		}
		
		if (menuItemText.equals(MenuItemText.BUILD)) {
			ProjectSetting setting = ProjectSetting.getSingleton();
			String fqbn = setting.getFqbn();
			if (fqbn == null) {
				JOptionPane.showMessageDialog(this, "No Arduino board is selected.\nPlease select one from the menu (tools -> board) first.");
				return;

			}
			if (editor.getPatchName() == null) {
				JOptionPane.showMessageDialog(this, "this patch is not saved yet. Please save the patch first.");
				return;
			}
			
			try {
				//generate the arduino project.
				Message.print("building the patch: " + editor.getPatchName() + ".........");
				boolean ret = editor.build();
				if (ret == false) {
					Message.println("canceled.");
					return;
				}
				Message.println("done.");
				Message.println("generated the arduino source files inside the arduino project folder.");
				
				//build the arduino project.
				Message.println();
				
				File projectPath = setting.getProjectDirectory();
				
				ret = ArduinoCLIBackground.getSingleton().build(projectPath, setting.getBoardName(), fqbn);
				if (!ret) {
					Message.println("the previous command is still being processed.");
				}

			}
			catch (IOException ex)
			{
				Message.println(ex);
			}
			
			this.repaint();			
			return;
		}
		
		
		if (menuItemText.equals(MenuItemText.BUILD_AND_UPLOAD)) {
			ProjectSetting setting = ProjectSetting.getSingleton();
			String fqbn = setting.getFqbn();
			if (fqbn == null) {
				JOptionPane.showMessageDialog(this, "No Arduino board is selected.\nPlease, select one from the menu (tools -> port).");
				return;
			}
			
			String port = setting.getPortName();
			if (port == null) {
				JOptionPane.showMessageDialog(this, "No port is selected.\nPlease, select one from the menu (tools -> port).");
				return;
			}
			
			if (editor.getPatchName() == null) {
				JOptionPane.showMessageDialog(this, "This patch is not saved yet. please save the patch first.");
				return;
			}
			
			try {
				//generate the arduino project.
				Message.print("building the patch: " + editor.getPatchName() + ".........");
				boolean ret  = editor.build();
				if (ret == false) {
					Message.println("canceled.");
					return;
				}
				Message.println("done.");
				Message.println("generated the arduino source files inside the arduino project folder.");
				
				//build the arduino project.
				Message.println();
				
				File projectPath = setting.getProjectDirectory();
								
				ret = ArduinoCLIBackground.getSingleton().buildAndUpload(projectPath, setting.getPortName(), setting.getBoardName(), fqbn);
				if (!ret) {
					Message.println("The previous command is still being processed.");
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			
			this.repaint();			
			return;
		}
	
		return;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		if (e.getModifiers() == 1) {
			editor.scrollHorizontallyRelative(e.getUnitsToScroll() * 3);
		}
		else if (e.getModifiers() == 0) {
			editor.scrollVerticallyRelative(e.getUnitsToScroll() * 3);
		}
	}
	
	

	@Override
	public void focusGained(FocusEvent e) {

		if (e.getSource() instanceof ObjectBoxTextField){
			ObjectBoxTextField tf = (ObjectBoxTextField)e.getSource();
			tf.selectAll();
			tf.updateTextBeforeEdit();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof ObjectBoxTextField){
			ObjectBoxTextField tf = (ObjectBoxTextField)e.getSource();
			this.objectBoxEditFinished();
			tf.select(0,0);		
			editor.setObjectBoxUnderMouseCursor(null);
			this.repaint();
		}
	}


}