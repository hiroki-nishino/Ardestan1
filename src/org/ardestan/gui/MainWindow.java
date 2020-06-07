package org.ardestan.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.ardestan.arclass.ARClassDatabase;
import org.ardestan.arduinocli.ArduinoCLI;
import org.ardestan.arduinocli.ArduinoCLIBackground;
import org.ardestan.gui.about.AboutWindow;
import org.ardestan.gui.dialog.board.BoardManagerDialog;
import org.ardestan.gui.dialog.board.SelectBoardDialog;
import org.ardestan.gui.dialog.library.LibraryManagerDialog;
import org.ardestan.gui.dialog.port.SelectPortDialog;
import org.ardestan.gui.dialog.preference.PreferencesDialog;
import org.ardestan.gui.dialog.project.CompileOptionDialog;
import org.ardestan.gui.dialog.udowiz.UserDefinedObjectCreationWizardDialog;
import org.ardestan.gui.dialog.udowiz.UserDefinedObjectUpdateWizardDialog;
import org.ardestan.gui.explorer.TabbedExplorerPane;
import org.ardestan.gui.info.TabbedInfoPane;
import org.ardestan.gui.text.TextProgramEditorPanel;
import org.ardestan.gui.visual.VisualProgramEditorPanel;
import org.ardestan.json.JsonBoard;
import org.ardestan.json.JsonBoardList;
import org.ardestan.json.JsonPort;
import org.ardestan.json.JsonSize;
import org.ardestan.mac.MacAppHelperListener;
import org.ardestan.misc.ARFileConst;
import org.ardestan.misc.ProjectSetting;

/**
 * @author hiroki
 *
 */
/**
 * @author hnishino
 *
 */
public class MainWindow implements ActionListener, WindowListener
{	
	public static int DEFAULT_PROJECT_EXPLORER_WIDTH = 200;
	public static int DEFAULT_MESSAGE_AREA_HEIGHT = 250;
	
	protected JFrame 				frame;
	
	protected JSplitPane			mainPanel;
	protected TabbedCodeEditorPane	editorPane;
	protected TabbedExplorerPane	explorerPane;
	
	protected TabbedInfoPane 		infoPane;
	
	protected JMenuItem	newFileItem;
	protected JMenuItem	newProjectItem;
	protected JMenuItem	openProjectItem;
	protected JMenuItem	projectSettingItem;
	protected JMenuItem	saveFileItem;
	protected JMenuItem	saveFileAsItem;
	protected JMenuItem	closeFileItem;
	protected JMenuItem closeAllItem;
	protected JMenuItem listAllObjects;
	
	protected JMenuItem undoItem;
	protected JMenuItem redoItem;
	protected JMenuItem	zoomInItem;
	protected JMenuItem	zoomOutItem;
	
	protected JMenuItem	cutItem;
	protected JMenuItem	copyItem;
	protected JMenuItem paseteItem;
	protected JMenuItem	selectAllItem;
	protected JMenuItem newObjectBoxItem;
	protected JMenuItem	newCommentBoxItem;
	protected JMenuItem	newUserDefinedObjectItem;
	
	protected JMenuItem	buildItem;
	protected JMenuItem	buildAndUploadItem;

	protected JMenuItem	selectConnectedBoardItem;
	protected JMenuItem	portItem;
	protected JMenuItem	boardItem;
	protected JMenuItem	updateCoreIndexItem;
	protected JMenuItem	boardManagerItem;
	protected JMenuItem	libraryManagerItem;
	protected JMenuItem	upgradeLibrariesItem;
	protected JMenuItem	updateLibraryIndexItem;
	
	protected JMenuItem	preferenceItem;
	protected JMenuItem aboutItem;
	protected JMenuItem quitItem;
	
	/**
	 * @param width
	 * @param height
	 */
	public MainWindow()
	{		
		frame = new JFrame();
		
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new BorderLayout());
		buildMenu();
		
		mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPanel.setDividerSize(3);
		
		JSplitPane rightSide = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		editorPane = new TabbedCodeEditorPane();
		rightSide.setTopComponent(editorPane);
		
		infoPane = new TabbedInfoPane();
		rightSide.setBottomComponent(infoPane);
		
		
		mainPanel.setRightComponent(rightSide);

		explorerPane = new TabbedExplorerPane();
		mainPanel.setLeftComponent(explorerPane);

		frame.add(mainPanel, BorderLayout.CENTER);
		frame.requestFocus();
		
		//----------------------------------------------
        //get the image icon
		URL url = ClassLoader.getSystemResource(ARFileConst.DEFAULT_DOCK_ICON);
		ImageIcon icon = new ImageIcon(url);
		Image img = icon.getImage();
		
		frame.setIconImage(img);

		//----------------------------------------------
		//handle mac menu
		if (ArduinoCLI.getOS() == ArduinoCLI.MAC_OS) {
			this.setupMacMenu(img);
		}
		//if this is windows.
		else {
			this.frame.setIconImage(img);
		}
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height)
	{
		frame.setSize(width, height);
		explorerPane.setMinimumSize(new Dimension(DEFAULT_PROJECT_EXPLORER_WIDTH, height));
		editorPane.setMinimumSize(new Dimension(width - DEFAULT_PROJECT_EXPLORER_WIDTH, height - DEFAULT_MESSAGE_AREA_HEIGHT));
		infoPane.setPreferredSize(new Dimension(width - DEFAULT_PROJECT_EXPLORER_WIDTH, DEFAULT_MESSAGE_AREA_HEIGHT));
		
		return;
	}
	
	/**
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		frame.setVisible(visible);
		return;
	}
	
	/**
	 * @return
	 */
	public JFrame getFrame()
	{
		return this.frame;
	}
	
	/**
	 * @return
	 */
	public int getWidth()
	{
		return frame.getWidth();
	}
	
	/**
	 * @return
	 */
	public int getHeight()
	{
		return frame.getHeight();
	}
	
	/**
	 * @param width
	 * @param height
	 * @return
	 */
	public void setSize(int width, int height)
	{
		frame.setSize(width, height);
	}
	
	/**
	 * 
	 */
	public void handleQuitMenuItem() {
		int ret = JOptionPane.showConfirmDialog(getFrame(), "Do you want to exit the Ardestan IDE?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (ret == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
		return;
	}
	
	/**
	 * 
	 */
	public boolean setupMacMenu(Image icon)
	{
		MacAppHelperListener listener = new MacAppHelperListener() 
		{			
			@Override
			public void handleQuit() {
				handleQuitMenuItem();
			}
			
			@Override
			public void handlePreferences() {
				try {
					showPreferenceDialog();
				}
				catch(IOException e) {
					Message.println(e);
				}
			}
			
			@Override
			public void handleAbout() {
				showAboutWindow();
			}
		};

		String macAppHelperClassName = "org.ardestan.mac.MacAppHelperBelowJDK9";
		
		if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
			macAppHelperClassName = "org.ardestan.mac.MacAppHelperJDK9orLater";
		}

		try {
			Class<?> 	c = Class.forName(macAppHelperClassName);
			Method 		m = c.getMethod("addMacAppHelperListener", new Class[] { MacAppHelperListener.class });
			m.invoke(null, new Object[] {listener}) ;
			
			m = c.getMethod("setMacDockIconImage", new Class[] { Image.class });
			m.invoke(null, new Object[] {icon}) ;
		}
		catch (ClassNotFoundException e) {
			System.out.println(e);
			return false;
		}
		catch (NoSuchMethodException e) {
			System.out.println(e);
			return false;
		}
		catch (InvocationTargetException e) {
			System.out.println(e);
			return false;
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
			return false;	
		}
		return true;
	}
	/**
	 * 
	 */
	public void buildMenu()
	{
		JMenuBar menubar = new JMenuBar();
		
		JMenu menuFile		= new JMenu("File");
		JMenu menuEdit		= new JMenu("Edit"); 
		JMenu menuPut 		= new JMenu("Put");
		JMenu menuProject 	= new JMenu("Project");
		JMenu menuTools		= new JMenu("Tools");
		JMenu menuHelp		= new JMenu("Help");

		menubar.add(menuFile);
		menubar.add(menuEdit);
		menubar.add(menuPut);
		menubar.add(menuProject);
		menubar.add(menuTools);
		menubar.add(menuHelp);

		
		//----------------------------------------------
		Toolkit tk = Toolkit.getDefaultToolkit();
        @SuppressWarnings("deprecation")
		int shortCutKeyMask = tk.getMenuShortcutKeyMask();

		//----------------------------------------------
		//file
		newFileItem = new JMenuItem(MenuItemText.NEW_PATCH);
		newFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortCutKeyMask));
		menuFile.add(newFileItem);
		
		menuFile.addSeparator();
		
		saveFileItem = new JMenuItem(MenuItemText.SAVE_FILE);
		saveFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortCutKeyMask));
		menuFile.add(saveFileItem);
		
		saveFileAsItem = new JMenuItem(MenuItemText.SAVE_FILE_AS);
		saveFileAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortCutKeyMask | InputEvent.SHIFT_DOWN_MASK));
		menuFile.add(saveFileAsItem);
		
		
		closeFileItem = new JMenuItem(MenuItemText.CLOSE_FILE);
		closeFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, shortCutKeyMask));
		menuFile.add(closeFileItem);
		
		closeAllItem = new JMenuItem(MenuItemText.CLOSE_ALL);
		closeAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, shortCutKeyMask | InputEvent.SHIFT_DOWN_MASK));
		menuFile.add(closeAllItem);
		
		menuFile.addSeparator();
		
		newUserDefinedObjectItem = new JMenuItem(MenuItemText.NEW_USER_DEFINED_OBJECT);
		menuFile.add(newUserDefinedObjectItem);
		

		//for windows/linux
		if (ArduinoCLI.getOS() != ArduinoCLI.MAC_OS) {
			menuFile.addSeparator();
			preferenceItem = new JMenuItem(MenuItemText.PREFERENCE);
			preferenceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, shortCutKeyMask));
			menuFile.add(preferenceItem);
			
			menuFile.addSeparator();
			quitItem = new JMenuItem(MenuItemText.QUIT);
			quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortCutKeyMask));
			menuFile.add(quitItem);
		}
		
		//----------------------------------------------
		//edit
		
		//select all
		selectAllItem = new JMenuItem(MenuItemText.SELECT_ALL);
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, shortCutKeyMask));
		menuEdit.add(selectAllItem);

		menuEdit.addSeparator();

		//undo/redo 
		undoItem = new JMenuItem(MenuItemText.UNDO);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortCutKeyMask));
		menuEdit.add(undoItem);

		redoItem = new JMenuItem(MenuItemText.REDO);
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortCutKeyMask | InputEvent.SHIFT_DOWN_MASK));
		menuEdit.add(redoItem);
		
		//cut/copy/paste
		menuEdit.addSeparator();

		cutItem = new JMenuItem(MenuItemText.CUT);
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortCutKeyMask));		
		menuEdit.add(cutItem);

		copyItem = new JMenuItem(MenuItemText.COPY);
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortCutKeyMask));
		menuEdit.add(copyItem);

		paseteItem = new JMenuItem(MenuItemText.PASTE);
		paseteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, shortCutKeyMask));
		menuEdit.add(paseteItem);

		//zoom 
		menuEdit.addSeparator();
		
		zoomInItem = new JMenuItem(MenuItemText.ZOOM_IN);
		zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, shortCutKeyMask));
		menuEdit.add(zoomInItem);

		zoomOutItem = new JMenuItem(MenuItemText.ZOOM_OUT);
		zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, shortCutKeyMask));
		menuEdit.add(zoomOutItem);
		
		//----------------------------------------------
		//put 
		newObjectBoxItem = new JMenuItem(MenuItemText.OBJECT_BOX);
		newObjectBoxItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, shortCutKeyMask));		
		menuPut.add(newObjectBoxItem);
		
		newCommentBoxItem = new JMenuItem(MenuItemText.COMMENT_BOX);
		newCommentBoxItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, shortCutKeyMask));		
		menuPut.add(newCommentBoxItem);
				
		//----------------------------------------------
		//build 
						
		buildItem = new JMenuItem(MenuItemText.BUILD);
		buildItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, shortCutKeyMask));
		menuProject.add(buildItem);
		
		buildAndUploadItem = new JMenuItem(MenuItemText.BUILD_AND_UPLOAD); 
		buildAndUploadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, shortCutKeyMask));
		menuProject.add(buildAndUploadItem);
		
		menuProject.addSeparator();

		newProjectItem = new JMenuItem(MenuItemText.NEW_PROJECT);
		newProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortCutKeyMask | InputEvent.SHIFT_DOWN_MASK));
		menuProject.add(newProjectItem);
		
		openProjectItem = new JMenuItem(MenuItemText.OPEN_PROJECT);
		openProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortCutKeyMask));
		menuProject.add(openProjectItem);

		projectSettingItem = new JMenuItem(MenuItemText.PROJECT_SETTING);
		menuProject.add(projectSettingItem);		
		
		//----------------------------------------------
		//tools 
		boardManagerItem = new JMenuItem(MenuItemText.BOARD_MANAGER);
		menuTools.add(boardManagerItem);

		updateCoreIndexItem = new JMenuItem(MenuItemText.UPDATE_CORE_INDEX);
		menuTools.add(updateCoreIndexItem);

		libraryManagerItem = new JMenuItem(MenuItemText.LIBRARY_MANAGER);
		menuTools.add(libraryManagerItem);
		
		upgradeLibrariesItem = new JMenuItem(MenuItemText.UPGRADE_LIBRARIES);
		menuTools.add(upgradeLibrariesItem);

		updateLibraryIndexItem = new JMenuItem(MenuItemText.UPDATE_LIBRARY_INDEX);
		menuTools.add(updateLibraryIndexItem);
		
		selectConnectedBoardItem = new JMenuItem(MenuItemText.SELECT_CONNECTED_BOARD);
		menuTools.add(selectConnectedBoardItem);
				
		portItem = new JMenuItem(MenuItemText.PORT);
		menuTools.add(portItem);
		
		boardItem = new JMenuItem(MenuItemText.BOARD);
		menuTools.add(boardItem);
		
		menuTools.insertSeparator(2);
		menuTools.insertSeparator(6);


		//----------------------------------------------
		//help 

		//for windows/linux
		if (ArduinoCLI.getOS() != ArduinoCLI.MAC_OS) {
			aboutItem = new JMenuItem(MenuItemText.ABOUT);
			menuHelp.add(aboutItem);
		}

		listAllObjects = new JMenuItem(MenuItemText.LIST_ALL_OBJECTS);
		menuHelp.add(listAllObjects);

		
		
		//------------------------------------------------------------------------------
		//set up listeners.
		frame.setJMenuBar(menubar);

		newProjectItem.addActionListener(this);
		newFileItem.addActionListener(this);
		saveFileItem.addActionListener(this);
		saveFileAsItem.addActionListener(this);
		openProjectItem.addActionListener(this);
		projectSettingItem.addActionListener(this);
		closeFileItem.addActionListener(this);
		closeAllItem.addActionListener(this);
		newUserDefinedObjectItem.addActionListener(this);
		listAllObjects.addActionListener(this);
		
		//for windows/linux
		if (ArduinoCLI.getOS() != ArduinoCLI.MAC_OS) {
			preferenceItem.addActionListener(this);
			quitItem.addActionListener(this);
			aboutItem.addActionListener(this);
		}

		undoItem.addActionListener(this);
		redoItem.addActionListener(this);

		cutItem.addActionListener(this);
		copyItem.addActionListener(this);
		paseteItem.addActionListener(this);
		
		selectAllItem.addActionListener(this);
		
		zoomInItem.addActionListener(this);
		zoomOutItem.addActionListener(this);
		
		newObjectBoxItem.addActionListener(this);
		newCommentBoxItem.addActionListener(this);
		
		buildItem.addActionListener(this);
		buildAndUploadItem.addActionListener(this);
		
		boardManagerItem.addActionListener(this);
		libraryManagerItem.addActionListener(this);
		updateCoreIndexItem.addActionListener(this);
		upgradeLibrariesItem.addActionListener(this);
		updateLibraryIndexItem.addActionListener(this);
		portItem.addActionListener(this);
		boardItem.addActionListener(this);
		selectConnectedBoardItem.addActionListener(this);
	}
	
	
	/**
	 * @throws IOException
	 */
	public void showPreferenceDialog() throws IOException
	{
		ProjectSetting setting = ProjectSetting.getSingleton();
		

		JsonSize size = DialogSizes.getSingleton().getPreferenceSize();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;
		PreferencesDialog d = new PreferencesDialog(setting.getBase64EncodedAdditionalURLs());
		d.setBounds(x, y, w, h);
		d.setVisible(true);
	
		
		if (d.isCanceled()) {
			return;
		}
		
		setting.setBase64EncodedAdditionalURLs(d.getBase64EncodedURLs());
		
		setting.savePreferences();
		
		
		JOptionPane.showMessageDialog(this.getFrame(), "The list of the additional URLs for the board manager was changed. The core index will be updated.\n"
													 + "If failed, connect to the Internet and select the menu: Tools -> Update Core Index.");

		ArduinoCLIBackground cli = ArduinoCLIBackground.getSingleton();		
		cli.updateCoreIndex(setting.getProjectDirectory());
		
		
		return;
	}
	
	/**
	 * @throws IOException
	 */
	public void showAboutWindow() 
	{
		JsonSize size = DialogSizes.getSingleton().getAboutSize();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;

		AboutWindow d = new AboutWindow();
		d.setBounds(x, y, w, h);
		d.setVisible(true);
		return;		
	}


	
	/**
	 * 
	 */
	public void showProjectSettingDialog() throws IOException
	{
		ProjectSetting setting = ProjectSetting.getSingleton();
		
		JsonSize size = DialogSizes.getSingleton().getProjectSetting();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;
		CompileOptionDialog d = new CompileOptionDialog(setting.getUsePROGMEMforOutlets(), setting.getUsePROGMEMforConnections());
		d.setBounds(x, y, w, h);
		d.setVisible(true);
	
		
		if (d.isCanceled()) {
			return;
		}
		
		setting.setUsePROGMEMforOutlets(d.getUsePROGMEMforOutlets());
		setting.setUsePROGMEMforConnections(d.getUsePROGMEMforConnections());
		
		setting.saveCurrentProject();
		return;
	}
	/**
	 * 
	 */
	public void showUserDefinedObjectCreationWizardDialog()
	{
		JsonSize size = DialogSizes.getSingleton().getUDOWizard();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;
		new UserDefinedObjectCreationWizardDialog(x, y, w, h).setVisible(true);

		return;
	}

	/**
	 * @param oldAudFile
	 */
	public void showUserDefinedObjectUpdateWizardDialog(File oldAudFile) throws IOException
	{
		JsonSize size = DialogSizes.getSingleton().getUDOWizard();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;
		
		new UserDefinedObjectUpdateWizardDialog(oldAudFile, x, y, w, h).setVisible(true);

		return;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{

		String s = ((JMenuItem)e.getSource()).getText();
		
		
		//create a new user-defined object
		if (s.equals(MenuItemText.NEW_USER_DEFINED_OBJECT)) {
			this.showUserDefinedObjectCreationWizardDialog();
			return;
		}
		
		//new patch
		if (s.equals(MenuItemText.NEW_PATCH)) {
			newFile();
			return;
		}
		
		//open project
		if (s.equals(MenuItemText.OPEN_PROJECT)) {
			try {
				openProject();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;
		}		
		
		//new project
		if (s.equals(MenuItemText.NEW_PROJECT)){
			try {
				newProject();
			}
			catch(IOException ex) {
				Message.print(ex);				
			}
			return;
		}
		
		//project setting
		if (s.equals(MenuItemText.PROJECT_SETTING)) {
			try {
				this.showProjectSettingDialog();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;
		}
		
		//preference
		if (s.equals(MenuItemText.PREFERENCE)) {
			try {
				this.showPreferenceDialog();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;
		}
		
		//about
		if (s.equals(MenuItemText.ABOUT)) {
			this.showAboutWindow();
			return;
		}
		
		//quit
		if (s.equals(MenuItemText.QUIT)) {
			this.handleQuitMenuItem();
			return;
		}
		
		//close all windows
		if (s.equals(MenuItemText.CLOSE_ALL)) {
			this.editorPane.closeAllPanes();
			return;
		}
		
		//list all the built-in objects
		if (s.equals(MenuItemText.LIST_ALL_OBJECTS)) {
			this.loadHelpFile(ARFileConst.ListAllObjectsHelpObjectName);
		}
		
		//board manager
		if (s.equals(MenuItemText.BOARD_MANAGER)) {
			try {
				showBoardInstallerDialog();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;
		}
		
		//library manager
		if (s.equals(MenuItemText.LIBRARY_MANAGER)) {
			try {
				showLibraryInstallerDialog();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;
		}
		
		//update core index
		if (s.equals(MenuItemText.UPDATE_CORE_INDEX)) {
			try {
				updateCoreIndex();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;
		}
		
		//select connected board
		if (s.equals(MenuItemText.SELECT_CONNECTED_BOARD)) {
			try {
				selectConnectedBoard();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;
		}
		
		//select port
		if (s.equals(MenuItemText.PORT)) {
			try {
				selectPort();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;
		}
		
		//select board
		if (s.equals(MenuItemText.BOARD)) {
			try {
				selectBoard();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;			
		}
		
		//upgrade librarlies
		if (s.equals(MenuItemText.UPGRADE_LIBRARIES)) {
			try {
				upgradeLibrary();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;	
		}
		
		//update library index
		if (s.equals(MenuItemText.UPDATE_LIBRARY_INDEX)) {
			try {
				updateLibraryIndex();
			}
			catch(IOException ex) {
				Message.print(ex);
			}
			return;	
		}
		
		
		//otherwise, delegate to the editor pane.
		editorPane.actionPerformed(e);
		return;
	}
	

	
	/**
	 * 
	 */
	public void upgradeLibrary() throws IOException
	{
				
		File path = new File(System.getProperty("java.io.tmpdir"));
		boolean ret = ArduinoCLIBackground.getSingleton().upgradeLibrary(path);
	
		if (!ret) {
			Message.println("the previous command is still being processed.");
		}
		return;
	}
	/**
	 * @throws IOException
	 */
	protected void selectBoard() throws IOException
	{
	
		File path = new File(System.getProperty("java.io.tmpdir"));
		
		
		ArduinoCLI cli = ArduinoCLI.getSingleton();
		cli.copyCommandTo(path);
		
		JsonBoardList list = cli.listAllKnownBoards(path);
		cli.deleteCommand(path);
		
		if (list == null || list.boards == null || list.boards.length == 0) {			
			JOptionPane.showMessageDialog(this.frame, "No board installed. Install a board first.");
			return;
		}

		int idx = 0;
		int selectedIndex = 0;
		String currentFQBN = ProjectSetting.getSingleton().getFqbn();
		
		Vector<String> boardNames 	= new Vector<String>();
		Vector<String> FQBNs		= new Vector<String>();
		
		for (JsonBoard b: list.boards) {
			boardNames.add(b.name);
			FQBNs.add(b.FQBN);
			if (b.FQBN.equals(currentFQBN)) {
				selectedIndex = idx;
			}
			idx++;
		}
		
		
		SelectBoardDialog dialog = new SelectBoardDialog(boardNames, selectedIndex);
		
		JsonSize size = DialogSizes.getSingleton().getBoardSize();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;
		
		dialog.setBounds(x, y, w, h);
		dialog.setVisible(true);
		if (dialog.isCanceled()) {
			return;
		}
		

		int index = dialog.getSelectedIndex();
		String boardName 	= boardNames.get(index);
		String fqbn 		= FQBNs.get(index);

		
		ProjectSetting setting = ProjectSetting.getSingleton();
		setting.setBoardName(boardName);
		setting.setFqbn(fqbn);
		
		setting.saveCurrentProject();
		this.refreshTitle();
		
		Message.println("Board:" + boardName + " (FQBN: " + fqbn + ") was selected.");
		return;
	}
	
	/**
	 * @throws IOException
	 */
	protected void selectConnectedBoard() throws IOException
	{
		ProjectSetting setting = ProjectSetting.getSingleton();
		
		File path = setting.getProjectDirectory();
		if (path == null) {
			JOptionPane.showMessageDialog(this.frame, "No project is open. Open a project file or create a new one first.");
			return;
		}		
		
		ArduinoCLI cli = ArduinoCLI.getSingleton();
		cli.copyCommandTo(path);
		JsonPort[] list = cli.getBoardList(path);
		cli.deleteCommand(path);
		
		if (list == null || list.length == 0) {			
			JOptionPane.showMessageDialog(this.frame, "No board found. Please check if the Arduino device is correctly connected.");
			return;
		}

		Vector<String> displayNames	= new Vector<String>();
		Vector<String> portNames	= new Vector<String>();
		Vector<String> boardNames 	= new Vector<String>();
		Vector<String> FQBNs		= new Vector<String>();
		for (JsonPort p: list) {
			if (p.boards == null) {
				continue;
			}
			for (JsonBoard b: p.boards) {
				displayNames.add(b.name + "@" + p.address + " : " + p.protocol_label + " (fqbn:" + b.FQBN + ")" );
				portNames.add(p.address);
				boardNames.add(b.name);
				FQBNs.add(b.FQBN);
			}
		}
		
		if (boardNames.size() == 0) {			
			JOptionPane.showMessageDialog(this.frame, "No board found. Please check if the Arduino device is correctly connected.");
			return;
		}
		
		SelectBoardDialog dialog = new SelectBoardDialog(displayNames, 0);
		
		JsonSize size = DialogSizes.getSingleton().getConnectedBoardSize();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;
		
		dialog.setBounds(x, y, w, h);
		dialog.setVisible(true);
		if (dialog.isCanceled()) {
			return;
		}
		
		int index = dialog.getSelectedIndex();
		String portName		= portNames.get(index);
		String boardName 	= boardNames.get(index);
		String fqbn			= FQBNs.get(index);
		
		setting.setPortName(portName);
		setting.setBoardName(boardName);
		setting.setFqbn(fqbn);
		
		setting.saveCurrentProject();
		this.refreshTitle();
		
		Message.println("Board:" + boardName + " (FQBN: " + fqbn + ") @ " + portName + " was selected.");
		return;
	}
	
	/**
	 * @throws IOException
	 */
	protected void showBoardInstallerDialog() throws IOException
	{		
		BoardManagerDialog dialog = new BoardManagerDialog();
		
		JsonSize size = DialogSizes.getSingleton().getBoardManagerSize();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;

		dialog.setBounds(x, y, w, h);
		dialog.setVisible(true);
		
		return;
	}
	
	/**
	 * @throws IOException
	 */
	protected void showLibraryInstallerDialog() throws IOException
	{		
		LibraryManagerDialog dialog = new LibraryManagerDialog ();
		
		JsonSize size = DialogSizes.getSingleton().getLibraryManagerSize();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;

		dialog.setBounds(x, y, w, h);
		dialog.setVisible(true);
		
		return;
	}
	/**
	 * @throws IOException
	 */
	protected void selectPort() throws IOException
	{
		ProjectSetting setting = ProjectSetting.getSingleton();
		
		File path = setting.getProjectDirectory();
		if (path == null) {
			JOptionPane.showMessageDialog(this.frame, "No project is open. Open a project file or create a new one first.");
			return;
		}
 
		ArduinoCLI cli = ArduinoCLI.getSingleton();
		
		cli.copyCommandTo(path);
		JsonPort[] ports = cli.getPorts(path);
		cli.deleteCommand(path);


		SelectPortDialog dialog = new SelectPortDialog(ports, setting.getPortName(), setting.getFqbn());
		
		JsonSize size = DialogSizes.getSingleton().getPortSize();
		int w = size.width;
		int h = size.height;
		int x = this.frame.getRootPane().getX() + this.frame.getRootPane().getWidth() / 2 - w / 2;
		int y = this.frame.getRootPane().getY() + this.frame.getRootPane().getHeight() / 2 - h / 2;
		
		dialog.setBounds(x, y, w, h);
		dialog.setVisible(true);
		if (dialog.isCanceled()) {
			return;
		}
		
		String portName = dialog.getSelectedPortName();
		setting.setPortName(portName);
		String boardName = null;
		String fqbn = null;
		if (setting.getBoardName() == null) {
			boardName = dialog.getSelectedBoardName();
			setting.setBoardName(boardName);
			fqbn = dialog.getSelectedFQBN();
			setting.setFqbn(fqbn);
		}
		
		setting.saveCurrentProject();
		this.refreshTitle();
		
		Message.println("port:" + portName + " (" + boardName + ", FQBN=" + fqbn + ") was selected.");
		return;
	}
	
	/**
	 * @throws IOException
	 */
	protected void updateCoreIndex() throws IOException
	{
		File path = new File(System.getProperty("java.io.tmpdir"));
		
		//update 
		boolean ret = ArduinoCLIBackground.getSingleton().updateCoreIndex(path);
		if (!ret) {
			Message.println("the previous command is still being processed.");
		}
	}
	
	/**
	 * @throws IOException
	 */
	protected void updateLibraryIndex() throws IOException
	{
		File path = new File(System.getProperty("java.io.tmpdir"));
		
		//update 
		boolean ret = ArduinoCLIBackground.getSingleton().updateLibraryIndex(path);
		if (!ret) {
			Message.println("the previous command is still being processed.");
		}
	}
	
	/**
	 * @throws IOException
	 */
	protected void newProject() throws IOException
	{
		JFileChooser chooser = new JFileChooser(); 
		chooser.setDialogTitle("create a new project");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showSaveDialog(this.frame) != JFileChooser.APPROVE_OPTION) { 
			return;
		}
		
		File dir = chooser.getSelectedFile();
		if (dir.exists()) {
			JOptionPane.showMessageDialog(this.frame, "The directory already exists. Please choose a different location.");
			return;
		}
		
		dir.mkdir();
		File projectFile = new File(dir, dir.getName() + ARFileConst.ARDESTAN_PROJECT_FILE_EXTENSION_WITH_DOT);
		
		projectFile.createNewFile();
		
		this.loadProject(projectFile);
		
		return;
	}
	

	/**
	 * 
	 */
	protected void newFile()
	{
		//have we loaded the project already?
		File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
		if (projectDirectory == null) {
			JOptionPane.showMessageDialog(this.frame, "No project is open. Open a project file or create a new one first.");
			return;
		}
		
		VisualProgramEditorPanel tab = new VisualProgramEditorPanel();	
		String filename = editorPane.getUntitledTabTitle();

		editorPane.addPane(filename, tab);
	}
	
	
	/**
	 * 
	 */
	public void refreshTitle()
	{
		ProjectSetting setting = ProjectSetting.getSingleton();
		File projectFile = setting.getProjectFile();

		String title = "no project selected yet";
		if (projectFile != null) {
			title = projectFile.getName();
		}
		
		String boardName = setting.getBoardName();
		if (boardName == null) {
			title += ": no board selected";
		}
		else {
			title += ": " + boardName;
		}
		
		String portName =setting.getPortName();
		if (portName == null) {
			title += "@no port selected";
		}
		else {
			title += "@" + portName;
		}
		
		this.frame.setTitle(title);
		return;
	}
	
	/**
	 * @param projectFile
	 */
	public void loadProject(File projectFile) throws IOException
	{
		ARClassDatabase.getSingleton().removeAllSubpatches();
		
		this.editorPane.closeAllPanes();
		
		ProjectSetting setting = ProjectSetting.getSingleton();
		setting.loadProjectFile(projectFile);
		
		ARClassDatabase.getSingleton().loadSubpatchesInTheProjectPath();
		
		this.refreshTitle();
		
		Message.println("loaded the ardestan project: " + projectFile.getName()); 
		Message.println("( " + projectFile.getAbsolutePath() + ")");
		return;
	}
	

	/**
	 * 
	 */
	protected void openProject() throws IOException
	{
		//which file to open?
		ProjectSetting ps = ProjectSetting.getSingleton();
		
		File d = ps.getProjectDirectory();
		if (d == null) {
			d = new File(System.getProperty("user.home"));
		}
		else if (d.getParentFile() != null) {
			d = d.getParentFile();
		}

		JFileChooser fc = new JFileChooser(d);
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Ardestan Project File", ARFileConst.ARDESTAN_PROJECT_FILE_EXTENSION_WITHOUT_DOT));
		
		int ret = fc.showOpenDialog(this.frame);
		
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
				
		File f = fc.getSelectedFile();
		
		//if it is not a file, just return.
		if (!f.isFile()) {
			return;
		}
		
		//check if we are opening a patch file or a project file.
		//if it is a project file
		if (f.getName().endsWith(ARFileConst.ARDESTAN_PROJECT_FILE_EXTENSION_WITH_DOT)) {
			this.loadProject(f);
			return;
		}
	
//		//if it is an 'ard' file, it is a visual program.
//		if (f.getName().endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT)) {
//			//have we loaded the project already?
//			File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
//			if (projectDirectory == null) {
//				JOptionPane.showMessageDialog(this.frame, "No project is open. Open a project file or create a new one first.");
//				return;
//			}
//		
//			this.loadArdestanSourceCode(f);
//			return;
//		}
//		
//		//if it is a cpp/hpp/h/auo file, open with a text editor
//		if (f.getName().endsWith(ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT)) {
//			File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
//			if (projectDirectory == null) {
//				JOptionPane.showMessageDialog(this.frame, "No project is open. Open a project file or create a new one first.");
//				return;
//			}
//			this.loadUserDefinedObjectSourceCode(f);
//			return;
//		}
		
		JOptionPane.showMessageDialog(this.frame, "The file is not loadable for the Ardestan IDE.");

		return;
	}
	
	

	/**
	 * @param orignail
	 * @param renamed
	 */
	public void renamePane(String original, String renamed)
	{
		this.editorPane.renamed(original, renamed);
	}
	
	
	/**
	 * @param file
	 */
	public void removePane(File file) {
		this.editorPane.removePane(file);
	}
	
	/**
	 * @param filename
	 */
	public void removePane(String filename)
	{
		this.editorPane.removePane(filename);
	}
	
	/**
	 * @param sourceFile
	 */
	public void loadUserDefinedObjectSourceCode(File sourceFile)
	{
		//if the file is not in this project, just return.
		ProjectSetting setting = ProjectSetting.getSingleton();
		
		File parentDirectory = sourceFile.getParentFile();
		if (parentDirectory == null) {
			JOptionPane.showMessageDialog(this.frame, "Can't load the selected file. The file doesn't belong to the current project.\nPlease place the user-defined object directory in the project directory first.");
			return;			
		}
		
		File grandparentDirectory = parentDirectory.getParentFile();
		if (!setting.getProjectDirectory().equals(grandparentDirectory)) {
			JOptionPane.showMessageDialog(this.frame, "Can't load the selected file. The file doesn't belong to the current project.\nPlease place the user-defined object directory in the project directory first.");
			return;			
		}
		
		String filename = sourceFile.getName();
		if (filename.endsWith(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT)) {
			try {
				this.showUserDefinedObjectUpdateWizardDialog(sourceFile);
			} catch (IOException e) {
				Message.println(e);
			}
			return;
		}
		
		TextProgramEditorPanel tab = new TextProgramEditorPanel();

		try {
			tab.loadProgram(sourceFile);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return;
		}

		sourceFile = tab.getFile();
		EditorPanelInterface p = editorPane.getEditorPanel(sourceFile);
		if (p == null) {
			editorPane.addPane(sourceFile.getName(), tab);
			return;
		}
		else {
			editorPane.replacePane(sourceFile.getName(), p, tab);
			editorPane.setSelectedComponent(tab.getJPanel());
		}
		return;
	}
	
	
	/**
	 * @param objectName
	 * @return
	 */
	public String convertToHelpObjectName(String objectName)
	{
		if (objectName.equals("+")	|| objectName.equals("-")	||
			objectName.equals("*")	|| objectName.equals("/")	||
			objectName.equals("%")	){
			return "arithmetic operators";
		}	
		
		if (objectName.equals("==") || objectName.equals("!=")	||
			objectName.equals("<")	|| objectName.equals("<=")	||
			objectName.equals(">")	|| objectName.equals(">=")	){
			return "relational operators";
		}	

		if (objectName.equals("&&")	|| objectName.equals("||")	){
			return "logical operators";
		}	

		return objectName;
	}
	
	
	
	/**
	 * @param objectName
	 */
	public void loadHelpFile(String objectName)
	{
		objectName = convertToHelpObjectName(objectName);
		
		//look for the help file in the current project directory.
		File udoHelpFile = getUserDefineObjectHelpFile(objectName);
		
		//found the help file in a user-defined object directory
		if (udoHelpFile != null) {
			this.loadArdestanHelpFromFile(udoHelpFile);
			return;
		}
		
		//then, try finding from the resources.
		String resourceName = getHelpFileFromResource(objectName);
		if (resourceName == null) {
			JOptionPane.showMessageDialog(this.frame, "no help file for the object: " + objectName + " can be found.", 
					"", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		this.loadLoadArdestanHelpFromResource(resourceName, objectName);
		return;
	}
	
	/**
	 * @param objectName
	 * @return
	 */
	public String getHelpFileFromResource(String objectName)
	{
		String resourceName = ARFileConst.DEFAULT_OBJECT_HELP_REOUCES_PATH + objectName + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT;

		InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
		if (is == null) {
			return null;
		}
		try {
			is.close();
		}
		catch (IOException e) {
			//do nothing here.
		}
		
		return resourceName;
	}
	/**
	 * @param objectName
	 * @return
	 */
	public File getUserDefineObjectHelpFile(String objectName)
	{
		//if the file is not in this project, just return.
		ProjectSetting setting = ProjectSetting.getSingleton();
		
		//check the project directory first.
		File helpFile = new File(setting.getProjectDirectory(), objectName + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT);
		if (helpFile.exists()) {
			return helpFile;
		}
		
		//check if the user-defined object directory exists.
		File udoDir = new File(setting.getProjectDirectory(), objectName);
		if (!udoDir.exists()) {
			return null;
		}
		
		//check if there exists an Ardestan help file in the directory.
		helpFile = new File(udoDir, objectName + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT);
		if (!helpFile.exists()) {
			return null;
		}
		return helpFile;
	}
	
	/**
	 * @param objectName
	 */
	public void loadLoadArdestanHelpFromResource(String resourceName, String objectName)
	{
		//when loading a patch file. 
		VisualProgramEditorPanel tab = new VisualProgramEditorPanel();	

		String helpFilename = objectName + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT;		
		try {
			tab.loadProgramFromResource(resourceName, helpFilename);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return;
		}

		EditorPanelInterface p = editorPane.getEditorPanel(tab.getFile());
		if (p == null) {
			editorPane.addPane(helpFilename, tab);
			return;
		}
		else {
			editorPane.replacePane(helpFilename, p, tab);
			editorPane.setSelectedComponent(tab.getJPanel());
		}
		return;

	}
	
	/**
	 * @param helpFile
	 */
	public void loadArdestanHelpFromFile(File helpFile)
	{
		//when loading a patch file. 
		VisualProgramEditorPanel tab = new VisualProgramEditorPanel();	

		try {
			tab.loadProgramFromFile(helpFile);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return;
		}

		EditorPanelInterface p = editorPane.getEditorPanel(tab.getFile());
		if (p == null) {
			editorPane.addPane(helpFile.getName(), tab);
			return;
		}
		else {
			editorPane.replacePane(helpFile.getName(), p, tab);
			editorPane.setSelectedComponent(tab.getJPanel());
		}
		return;

	}
	

	/**
	 * @param sourceFile
	 */
	public void loadArdestanSourceCode(File sourceFile)
	{
		//if the file is not in this project, just return.
		ProjectSetting setting = ProjectSetting.getSingleton();
		
		boolean isProjectPatchFile = setting.getProjectDirectory().equals(sourceFile.getParentFile());
	
		if (!isProjectPatchFile){
			JOptionPane.showMessageDialog(this.frame, "Can't load the selected file. The file doesn't belong to the current project.\nPlease move the file to the project directory first.");
			return;
		}
		
		//when loading a patch file. 
		VisualProgramEditorPanel tab = new VisualProgramEditorPanel();	

		try {
			tab.loadProgramFromFile(sourceFile);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return;
		}

		sourceFile = tab.getFile();
		EditorPanelInterface p = editorPane.getEditorPanel(sourceFile);
		if (p == null) {
			editorPane.addPane(sourceFile.getName(), tab);
			return;
		}
		else {
			editorPane.replacePane(sourceFile.getName(), p, tab);
			editorPane.setSelectedComponent(tab.getJPanel());
		}
		return;
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.handleQuitMenuItem();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
	

}

