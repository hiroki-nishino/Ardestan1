package org.ardestan.gui.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ardestan.arclass.ARClassDatabase;
import org.ardestan.arclass.ARClassInfo;
import org.ardestan.arclass.ARClassInfoNative;
import org.ardestan.arclass.ARClassInfoSubpatch;
import org.ardestan.generator.ARExternalObjectUpdateListener;
import org.ardestan.generator.ARPatchCircularReferenceChecker;
import org.ardestan.generator.ARPatchCircularReferenceException;
import org.ardestan.generator.ArduinoSourceCodeGenerator;
import org.ardestan.gui.ArdestanIDE;
import org.ardestan.gui.EditorPanelInterface;
import org.ardestan.gui.MainWindow;
import org.ardestan.gui.Message;
import org.ardestan.misc.ARFileConst;
import org.ardestan.misc.ARNameVerifier;
import org.ardestan.misc.ProjectSetting;


/**
 * @author hiroki
 *
 */
public class VisualProgramEditorPanel implements EditorPanelInterface, ARExternalObjectUpdateListener, ComponentListener, AdjustmentListener
{
	public static int marginWidth  = 2;
	public static int marginHeight = 6;

	protected Map<String, ARClassInfo> 	nameToClassInfo	;
	
	protected JPanel		 			panel			;
	protected CodeCanvas				canvas			;
		
	protected JScrollBar				scrollBarVertical;
	protected JScrollBar				scrollBarHorizontal;
	
	protected VisualProgramEditManager	editManager;
	
	protected ZoomRequest				zoomRequest;
	
	protected JLayeredPane				programArea;
	
	

	protected File	 					file;
	
	protected ObjectBox					objectBoxUnderMouseCursor;
	
	protected Color						errorMessageColor;
	
	
	/**
	 * @param width
	 * @param height
	 */
	public VisualProgramEditorPanel()
	{
		this.zoomRequest = new ZoomRequest();
		this.zoomRequest.req = ZoomRequestState.NO_REQUEST;
			
		this.buildUI();
		
		ARClassDatabase.getSingleton().addARExternalObjectUpdateListener(this);
		
		errorMessageColor = new Color(176, 0, 0);
	}
	
	
	
	/**
	 * @return
	 */
	public int getHorizontalScrollBarValue()
	{
		return scrollBarHorizontal.getValue();
	}
	
	
	/**
	 * @return
	 */
	public int getVerticalScrollBarValue()
	{
		return scrollBarVertical.getValue();
		
	}
	
	
	/**
	 * @param box
	 */
	public void addObjectBoxToProgramArea(ObjectBox box)
	{
		box.addedToProgramArea(programArea);

		box.addListenerCanvas(canvas);

		return;
	}
	
	

	/**
	 * @param box
	 */
	public void removeObjectBoxFromProgramArea(ObjectBox box)
	{
		box.removedFromProgramArea(programArea);
		box.removeListenerCanvas(canvas);
		return;
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.EditorPanelInterface#getJPanel()
	 */
	public JPanel getJPanel()
	{
		return this.panel;
	}


	/**
	 * 
	 */
	protected void buildUI()
	{
		this.panel 	= new JPanel();
		
		canvas = new CodeCanvas(this);
		
		this.editManager = new VisualProgramEditManager(this);

		programArea = new JLayeredPane();
		programArea.add(canvas);
		programArea.setLayer(canvas, 0);
		programArea.setFocusable(true);
		
		programArea.addComponentListener(this);
			
			

		//----------------------------------------------
		//create GUI.
		
		panel.setLayout(new BorderLayout());
		
		panel.add(programArea, BorderLayout.CENTER);
	
		
		scrollBarVertical = new JScrollBar(JScrollBar.VERTICAL);
		scrollBarVertical.setMaximum(1000);
		scrollBarVertical.setMinimum(0);
		scrollBarVertical.setValue(0);
		
		scrollBarVertical.addAdjustmentListener(this);
		
		scrollBarHorizontal = new JScrollBar(JScrollBar.HORIZONTAL);
		scrollBarHorizontal.setMaximum(1000);
		scrollBarHorizontal.setMinimum(0);
		scrollBarHorizontal.setValue(0);

		
		scrollBarHorizontal.addAdjustmentListener(this);
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		JPanel filler = new JPanel();
		southPanel.add(filler, BorderLayout.EAST);
		southPanel.add(scrollBarHorizontal, BorderLayout.CENTER);
	

		panel.add(this.scrollBarVertical, BorderLayout.EAST);
		//frame.getContentPane().add(this.scrollBarHorizontal, BorderLayout.SOUTH);
		panel.add(southPanel, BorderLayout.SOUTH);
		Dimension d = new Dimension(scrollBarVertical.getPreferredSize().width, scrollBarHorizontal.getPreferredSize().height);
		filler.setPreferredSize(d);
		
				
		return;	
	}
	
	
	
	/**
	 * 
	 */
	public void requestFocusInWindow()
	{
		canvas.requestFocusInWindow();
	}
	
	/**
	 * @param b
	 */
	public void startEditingObjectBox(ObjectBox b)
	{
		this.editManager.startEditingObjectBox(b);
	}
	
	/**
	 * @return
	 */
	public boolean finishEditingObjectBox()
	{
		return this.editManager.finishEditingObjectBox();
	}
	

	/**
	 * @param g
	 * @param canvas
	 */
	protected void updateDrawingParameters(Graphics2D g, CodeCanvas canvas)
	{
		Point pZoomCenter = null;
		
		if (zoomRequest.req != ZoomRequestState.NO_REQUEST) {
			pZoomCenter = translateToProgramCoordinate(zoomRequest.canvasX, zoomRequest.canvasY);
			
			if (zoomRequest.req == ZoomRequestState.ZOOM_IN) {
				editManager.zoomIn();
			}
			else if (zoomRequest.req == ZoomRequestState.ZOOM_OUT) {
				editManager.zoomOut();
			}
		}
		zoomRequest.req = ZoomRequestState.NO_REQUEST;			
			
		Point programSize = this.editManager.updateDrawingParameters(g);
		
		int programRight  = programSize.x + marginWidth;
		int programBottom = programSize.y + marginHeight;

		programRight  = programRight < canvas.getWidth() ? canvas.getWidth() : programRight;
		programBottom = programBottom < canvas.getHeight() ? canvas.getHeight() : programBottom;
		
		this.scrollBarHorizontal.setMinimum(0);
		this.scrollBarHorizontal.setMaximum(programRight);
		this.scrollBarVertical	.setMinimum(0);
		this.scrollBarVertical	.setMaximum(programBottom);


		if (pZoomCenter != null) {
			int newOffsetX = (int)Math.ceil(pZoomCenter.x * editManager.getZoom() - zoomRequest.canvasX);
			int newOffsetY = (int)Math.ceil(pZoomCenter.y * editManager.getZoom() - zoomRequest.canvasY);

			//temporarily set the visible amount to 1. 
			this.scrollBarHorizontal.setVisibleAmount(1);
			this.scrollBarVertical  .setVisibleAmount(1);
			
			this.scrollBarHorizontal.setValue(newOffsetX);
			this.scrollBarVertical  .setValue(newOffsetY);
		}
		this.scrollBarHorizontal.setVisibleAmount(canvas.getWidth());
		this.scrollBarVertical  .setVisibleAmount(canvas.getHeight());
		
		

		double barLengthHorizontalRatio =  canvas.getWidth() / (double)this.scrollBarHorizontal.getMaximum();
		if (barLengthHorizontalRatio >= 1.0) {
			scrollBarHorizontal.setValue(0);
			scrollBarHorizontal.setVisibleAmount(Integer.MAX_VALUE);
		}
		
		double barLengthVerticalRatio =  canvas.getHeight() / (double)this.scrollBarVertical.getMaximum();
		if (barLengthVerticalRatio >= 1.0) {
			scrollBarVertical.setValue(0);
			scrollBarVertical.setVisibleAmount(Integer.MAX_VALUE);
		}
		
		return;
	}
	
	

	/**
	 * @param g
	 * @param canvas
	 */
	public void draw(Graphics2D g, CodeCanvas canvas)
	{	
		this.updateDrawingParameters(g, canvas);
		
		editManager.draw(g, scrollBarHorizontal.getValue(), scrollBarVertical.getValue());
		
		if (this.objectBoxUnderMouseCursor != null && this.objectBoxUnderMouseCursor.isValid() == false) {
			drawObjectCreationErrors(g);
		}
	}
	

	/**
	 * @param graphics
	 */
	public void drawObjectCreationErrors(Graphics2D graphics)
	{
		String error = this.objectBoxUnderMouseCursor.getErrorString();
		if (error == null) {
			return;
		}
		
		Font oldFont = graphics.getFont();
		Color oldColor = graphics.getColor();
		
		graphics.setFont(GUIFont.getSingleton().getObjectBoxFont());


		FontMetrics metrics = graphics.getFontMetrics();

		int xmargin = 2;

		int w = metrics.stringWidth(error) + xmargin;
		int h = metrics.getAscent() + metrics.getDescent() + metrics.getLeading() * 2;

		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, w, h);


		graphics.setColor(this.errorMessageColor);
		
		graphics.drawString(error, xmargin, metrics.getAscent() + metrics.getDescent() + metrics.getLeading());
				
		graphics.setFont(oldFont);
		graphics.setColor(oldColor);
		
		return;
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 * @return
	 */
	public Point translateToProgramCoordinate(int canvasX, int canvasY)
	{
		double zoom = editManager.getZoom();
		double px = (canvasX + scrollBarHorizontal	.getValue()) / zoom;
		double py = (canvasY + scrollBarVertical	.getValue()) / zoom;
		
		return new Point((int)px, (int)py);
	}
	

	/**
	 * @param canvasX
	 * @param canvasY
	 * @return
	 */
	public ObjectBox getObjectBoxAt(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		return this.editManager.getObjectBoxAt(p.x, p.y);
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 * @return
	 */
	public CommentBox getCommentBoxAt(int canvasX, int canvasY) {
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		return this.editManager.getCommentBoxAt(p.x, p.y);
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 * @return
	 */
	public boolean mouseMoved(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		return editManager.selectConnectionAt(p.x - 5, p.y - 5, 10, 10);
	}

	/**
	 * 
	 */
	public void deleteSelectedObjects()
	{
		editManager.deleteSelectedObjects();
		return;
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 * @return
	 */
	public ObjectBoxOutletInfo getOutletAt(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		return editManager.getOutletAt(p.x, p.y);
	}

	/**
	 * @param oi
	 */
	public void startConnecting(ObjectBoxOutletInfo oi)
	{
		editManager.startConnecting(oi);
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 */
	public void connecting(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		editManager.connecting(p.x, p.y);
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 * @return
	 */
	public boolean finishConnecting(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		return editManager.finishConnecting(p.x, p.y);
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 */
	public void startDraggingSelectedObjects(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		editManager.startDraggingSelectedObjects(p.x, p.y);
	}
	
	
	/**
	 * @param canvasX
	 * @param canvasY
	 */
	public void dragSelectedObjects(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		editManager.dragSelectedObjects(p.x, p.y);
		return;
	}
		
	/**
	 * @param canvasX
	 * @param canvasY
	 * @return
	 */
	public boolean finishDraggingSelectedObjects(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		return editManager.finishDraggingSelectedObjects(p.x, p.y);
	}

	/**
	 * @param dx
	 * @param dy
	 */
	public void moveSelectedObjectsRelative(int dx, int dy)
	{
		this.editManager.moveSelectedObjectsRelative(dx, dy);
	}

	/**
	 * @param canvasX
	 * @param canvasY
	 */
	public void startSelectingMultipleObjects(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		editManager.startSelectingMultipleObjectBoxes(p.x, p.y);
		return;
	}
	
	/**
	 * @param canvasX
	 * @param canvasY
	 */
	public void selectingMultipleObjects(int canvasX, int canvasY)
	{
		Point p = translateToProgramCoordinate(canvasX, canvasY);
		editManager.selectingMultipleObjectBoxes(p.x, p.y);
		return;
	}
	
	/**
	 * @return
	 */
	public boolean finishSelectingMultipleObjects()
	{
		return editManager.finishSelectingMultipleObjectBoxes();
	}
	
	/**
	 * 
	 */
	public void unselectAllObjects()
	{
		editManager.unselectAllObjects();
		return;
	}
	
	
	/**
	 * @param b
	 */
	public void selectOneAndUnselectOthers(ObjectBox b)
	{
		editManager.selectOneAndUnselectOthers(b);
		return;
	}
	
	/**
	 * @param b
	 */
	public void selectOneAndUnselectOthers(CommentBox b)
	{
		editManager.selectOneAndUnselectOthers(b);
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean isSelected(ObjectBox o)
	{
		return editManager.isSelected(o);
	}
	
	/**
	 * @param b
	 * @return
	 */
	public boolean isSelected(CommentBox b)
	{
		return editManager.isSelected(b);
	}
	

	/**
	 * @param o
	 */
	public void select(ObjectBox o)
	{
		editManager.select(o);
	}

	/**
	 * @param b
	 */
	public void select(CommentBox b)
	{
		editManager.select(b);
	}

	/**
	 * @param o
	 */
	public void unselect(ObjectBox o)
	{
		editManager.unselect(o);
	}
	
	/**
	 * @param b
	 */
	public void unselect(CommentBox b)
	{
		editManager.unselect(b);
	}
	
	/**
	 * @return
	 */
	public ObjectBox getObjectBoxSelectedForEdit()
	{
		return editManager.getObjectBoxSelectedForEdit();
	}
		
	/**
	 * @param req
	 */
	public void zoomRequested(ZoomRequest req)
	{
		this.zoomRequest = req;
	}
	
	/**
	 * @param hscroll
	 */
	public void scrollHorizontallyRelative(int hscroll)
	{
		scrollBarHorizontal.setValue(scrollBarHorizontal.getValue() + hscroll);
	}
	
	/**
	 * @param vscroll
	 */
	public void scrollVerticallyRelative(int vscroll)
	{
		scrollBarVertical.setValue(scrollBarVertical.getValue() + vscroll);
	}
	
	/**
	 * 
	 */
	public void copySelectedObjectToClipboard()
	{
		Point p = canvas.getMousePosition();
		if (p == null) {
			p = new Point();
		}
		editManager.copySelectedObjectToClipboard(p.x, p.y);
	}
	
	/**
	 * 
	 */
	public void pasteFromClipboard()
	{
		Point p = canvas.getMousePosition();
		if (p == null) {
			p = new Point();
		}
		editManager.pasteFromClipboard(p.x, p.y);
	}
	
	/**
	 * 
	 */
	public void cutSelectedObjects()
	{
		Point p = canvas.getMousePosition();
		if (p == null) {
			p = new Point();
		}
		
		editManager.cutSelectedObjects(p.x, p.y);
	}
	

	/**
	 * 
	 */
	public void selectAll()
	{
		editManager.selectAll();
	}
	
	
	/**
	 * 
	 */
	private void cleanArduinoDirectory()
	{
		ProjectSetting setting = ProjectSetting.getSingleton();
		File arduinoProjectDirectoryPath = setting.getArduinoProjectDirectory();
		
		if (!arduinoProjectDirectoryPath.exists()) {
			arduinoProjectDirectoryPath.mkdirs();
		}

		File[] list = arduinoProjectDirectoryPath.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].isDirectory()) {
				continue;
			}
			list[i].delete();
		}
	}
	

	/**
	 * @return
	 * @throws IOException
	 */
	public boolean build() throws IOException
	{
		//----------------------------------------------
		//pre check
		if (this.getFile() == null) {
			JOptionPane.showMessageDialog(this.panel.getRootPane(), "The patch is not saved yet. Save the patch first to compile.");
			return false;
		}
		else if (editManager.isChangeMade()) {
			JOptionPane.showMessageDialog(this.panel.getRootPane(), "The patch is modified but not saved yet. Save the patch to compile.");
			return false;
			
		}
		
		ProjectSetting setting = ProjectSetting.getSingleton();
		this.cleanArduinoDirectory();
		
		File projectPath = setting.getProjectDirectory();
		String patchName = this.getFile().getName();
		
		//if this is a help file, we need to treat it a little different.
		ARClassInfoSubpatch subpatchInfo = null;
		boolean isHelpFile = false;
		if (patchName.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)) {
			isHelpFile = true;
			
			if (editManager.isLoadedFromResource() == true) {
				File tmpFile = File.createTempFile( "__ardestam__", ".tmp", projectPath);
				editManager.save(tmpFile);
				subpatchInfo = new ARClassInfoSubpatch(tmpFile);				
				subpatchInfo.updateSubpatchInfo();
				tmpFile.delete();
			}
			else {
				subpatchInfo = new ARClassInfoSubpatch(this.file);
				subpatchInfo.updateSubpatchInfo();
			}
		}
		else if (patchName.endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT)){
			patchName = patchName.substring(0, patchName.lastIndexOf("."));
			ARClassInfo i = ARClassDatabase.getSingleton().getARClassInfo(patchName);
			if (i instanceof ARClassInfoSubpatch == false) {
				if (i instanceof ARClassInfoNative) {
					Message.println("Error: there is a built-in Ardestan object with the same name as this patch name. please rename the patch first.");
				}
				else {
					Message.println("Error: this is not a patch file. The program should not reach here. There is a need for a bug fix.");					
				}
				return false;
			}
			subpatchInfo = (ARClassInfoSubpatch)i;
		}
		
		if (subpatchInfo == null || subpatchInfo instanceof ARClassInfoSubpatch == false){
			Message.println("Error: this is not a patch file. The program should not reach here. There is a need for a bug fix.");
			return false;
		}
		
		try {
			ARPatchCircularReferenceChecker.check(subpatchInfo, isHelpFile);
		}
		catch(ARPatchCircularReferenceException e)
		{
			Message.println("");
			Message.print("    a circular reference is found: ");
			Iterator<String> it = e.getCircularReferencePath().iterator();
			while (it.hasNext()) {
				Message.print(it.next() +  "->");
			}
			
			Message.println(e.getSubpatchName() + ".");

			JOptionPane.showMessageDialog(this.panel.getRootPane(), "The patch:" + e.getPatchName() + " causes a circular reference to :" + e.getSubpatchName());
			return false;
		}
		catch(Exception e)
		{
			Message.print(e);
			Message.println("compiled failed. Does any native object exist with the same as the current patch?");
			return false;
		}
	
		//----------------------------------------------
		//generate source code.
		File srcFile = File.createTempFile( "__ardestam__", ".tmp", projectPath);
		try {
			editManager.generateArdestanSourceCodeForBuild(srcFile);
			File arduinoProjectDirectoryPath = setting.getArduinoProjectDirectory();
			ArduinoSourceCodeGenerator.generate(srcFile.getAbsolutePath(), arduinoProjectDirectoryPath.getAbsolutePath(), 
												setting.getUsePROGMEMforOutlets(), setting.getUsePROGMEMforConnections());
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			srcFile.delete();
		}
		return true;
	}
	
	
	
	/**
	 * @param box
	 */
	public void setObjectBoxUnderMouseCursor(ObjectBox box) {
		this.objectBoxUnderMouseCursor = box;
	}


	/**
	 * @return
	 */
	public String getPatchName()
	{
		if (this.file == null) {
			return null;
		}
		return this.file.getName();
	}
	
	/**
	 * @throws IOException
	 */
	public void save() throws IOException
	{		
		this.saveProgram();
		return;
	}
	
	/**
	 * @return
	 */
	public String getUnusedUntitledPatchName()
	{
		//no file specified before.
		ProjectSetting ps = ProjectSetting.getSingleton();
		String filename = "untitled" + ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT;
		if (new File(ps.getProjectDirectory(), filename).exists()) {
			int seq = 1;
			boolean ret;
			do {
				filename = "untitled" + seq + ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT;
				ret = new File(ps.getProjectDirectory(), filename).exists();
				seq++;
			} while(ret);
		}
		return filename;
	}
	
	
	
	/**
	 * @param filename
	 * @throws IOException
	 */
	public void saveAs(String filename) throws IOException
	{
		if (filename == null) {
			if (this.getFile() == null) {
				filename = getUnusedUntitledPatchName();
			}
			else {
				filename = this.getFile().getName();
			}
		}
		
		ProjectSetting ps = ProjectSetting.getSingleton();

		JFileChooser fc = new JFileChooser(ps.getProjectDirectory());
		fc.setSelectedFile(new File(filename));		
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Ardestan Patch File", ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITHOUT_DOT));
		
		int ret = fc.showSaveDialog(this.panel.getRootPane());
		
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
				
		File f = fc.getSelectedFile();
		
		String newFilename = f.getName();
		if (ARNameVerifier.isValidObjectName(newFilename) == false && ARNameVerifier.isValidArdFilename(newFilename) == false
				&& ARNameVerifier.isValidArhFilename(newFilename) == false) {
			JOptionPane.showMessageDialog(this.panel.getRootPane(), "Invalid filename for an Ardestan patch.");
			return;
		}
		
		if (!newFilename.endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT) && !newFilename.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)) {
			f = new File(f.getAbsolutePath() + ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT);
		}

		if (f.exists()) {
			int r = JOptionPane.showConfirmDialog(this.panel.getRootPane(), "The file already exists, overwrite?", "Existing File", JOptionPane.YES_NO_OPTION);
			if (r != JOptionPane.YES_OPTION) {
				return;
			}
			MainWindow w = ArdestanIDE.getMainWindow();
			w.removePane(f);
		}
		
		this.file = f;

		this.saveProgram();

		return;
	}
	
	/**
	 * @throws IOException
	 */
	protected void saveProgram() throws IOException
	{
		if (file == null) {
			this.saveAs(null);
			return;
		}
		
		boolean isHelpFile = file.getName().endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT);
		if (isHelpFile) {
			int r = JOptionPane.showConfirmDialog(this.panel.getRootPane(), "This is a help file. Do you really want to create/overwrite?", "Existing File", JOptionPane.YES_NO_OPTION);
			if (r != JOptionPane.YES_OPTION) {
				return;
			}
		}
		editManager.save(file);
		
		ARClassDatabase db = ARClassDatabase.getSingleton();
		db.removeNonExistingSubPatches();
		db.loadSubpatchesInTheProjectPath();
	}
	
	/**
	 * @return
	 */
	public boolean isAnyObjectSelected()
	{
		return editManager.isAnyObjectSelected();
	}
	
	/**
	 * @throws IOException
	 */
	public void loadProgramFromFile(File file) throws IOException
	{
		
		editManager.loadFromFile(file.getAbsolutePath());
		this.file = file;
		return;
	}
	
	
	/**
	 * @param resourceName
	 * @throws IOException
	 */
	public void loadProgramFromResource(String resourceName, String tmpFilename) throws IOException
	{
		ProjectSetting setting = ProjectSetting.getSingleton();
		editManager.loadFromResource(resourceName);		
		this.file = new File(setting.getProjectDirectory(), tmpFilename);
		return;
	}

	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.EditorPanelInterface#getFile()
	 */
	public File getFile()
	{
		return this.file;
	}
	
	
	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.gui.EditorPanelInterface#setFile(java.io.File)
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	

	/**
	 * @param mx
	 * @param my
	 * @return
	 */
	public ObjectBox createObjectBox(int mx, int my)
	{
		Point p = translateToProgramCoordinate(mx, my);

		ObjectBox nb = new ObjectBox();
		nb.setX(p.x);
		nb.setY(p.y);
		nb.setWidth(2);
		nb.setHeight(16);	
		nb.setArguments(new Vector<String>());
		editManager.addObjectBox(nb);
				
		return nb;
	}
	

	/**
	 * @param mx
	 * @param my
	 * @return
	 */
	public CommentBox createCommentBox(int mx, int my)
	{
		Point p = translateToProgramCoordinate(mx, my);

		CommentBox cb = new CommentBox();
		cb.setX(p.x);
		cb.setY(p.y);
		
		editManager.addCommentBox(cb);
				
		return cb;
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		canvas.actionPerformed(e);
	}

	/**
	 * @return
	 */
	public boolean isEditingObjectBox()
	{
		return editManager.isEditingObjectBox();
	}

	/**
	 * 
	 */
	public void undo()
	{
		if (editManager.getPastSize() == 0) {
			Message.println("nothing to undo!");
			return;
		}
		
		editManager.pushFuture();
		editManager.popPast();
	}

	/**
	 * 
	 */
	public void redo()
	{
		if (editManager.getFutureSize() == 0) {
			Message.println("nothing to redo!");
			return;
		}
				
		editManager.pushPast();
		editManager.popFuture();
	}

	/**
	 * 
	 */
	public void pushPast() {
		editManager.pushPast();
	}

	/**
	 * 
	 */
	public void popPast() {
		editManager.popPast();
	}

	/**
	 * 
	 */
	public void pushFuture() {
		editManager.pushFuture();
	}

	/**
	 * 
	 */
	public void popFuture() {
		editManager.popFuture();
	}

	/**
	 * 
	 */
	public void createDeepCopy() {
		editManager.createDeepCopy();
	}
	
	/**
	 * 
	 */
	public void clearFuture()
	{
		editManager.clearFuture();
	}

	/* (non-Javadoc)
	 * @see com.hnishino.ardestan.generator.ARExternalObjectUpdateListener#subpatchUpdated()
	 */
	public void subpatchUpdated() {
		editManager.subpatchUpdated();
		panel.repaint();
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent e) {
		canvas.setSize(programArea.getSize());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {
	}



	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent e) {
	}



	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
	}



	/* (non-Javadoc)
	 * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
	 */
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		canvas.repaint();
	}
	
	
}
