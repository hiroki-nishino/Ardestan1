/**

 */
package org.ardestan.gui.explorer;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.ardestan.arclass.ARClassDatabase;
import org.ardestan.arclass.ARClassInfo;
import org.ardestan.arclass.ARClassInfoNative;
import org.ardestan.gui.ArdestanIDE;
import org.ardestan.gui.MainWindow;
import org.ardestan.gui.MenuItemText;
import org.ardestan.gui.Message;
import org.ardestan.gui.ProjectDirectoryListener;
import org.ardestan.gui.dialog.file.DuplicateFileDialog;
import org.ardestan.gui.dialog.file.DuplicateUDODirDialog;
import org.ardestan.gui.dialog.file.RenameFileDialog;
import org.ardestan.json.JsonClassInfoLoader;
import org.ardestan.misc.ARFileConst;
import org.ardestan.misc.ARNameVerifier;
import org.ardestan.misc.ProjectSetting;

/**
 * @author hiroki
 *
 */
@SuppressWarnings("serial")
public class FileTreePanel extends JScrollPane implements ProjectDirectoryListener, MouseListener, ActionListener
{
	protected JTree tree = null;
	protected DefaultMutableTreeNode root = null;
	
	protected boolean	changePaneWidthMode;

	protected JPopupMenu 	popupMenu;
	protected JMenuItem	 	deleteFileItem;
	protected JMenuItem  	renameFileItem;
	protected JMenuItem		duplicateFileItem;
	
	/**
	 * 
	 */
	public FileTreePanel()
	{
		
		ProjectSetting.getSingleton().addProjectDirecotryListeners(this);
		ARClassDatabase.getSingleton().addProjectDirectoryListeners(this);
		
		popupMenu	= new JPopupMenu();
		
		deleteFileItem 		= new JMenuItem(MenuItemText.DELETE_FILE);
		renameFileItem 		= new JMenuItem(MenuItemText.RENAME_FILE);
		duplicateFileItem	= new JMenuItem(MenuItemText.DUPLICATE_FILE);

		popupMenu.add(renameFileItem);
		popupMenu.add(duplicateFileItem);
		popupMenu.add(deleteFileItem);
		
		renameFileItem		.addActionListener(this);
		duplicateFileItem	.addActionListener(this);
		deleteFileItem		.addActionListener(this);
		
		return;
	}
	

	
	/**
	 * 
	 */
	public void updateTree()
	{	
		ProjectSetting setting = ProjectSetting.getSingleton();
		
		File projectFile = setting.getProjectFile();
		if (projectFile == null) {
			String[] treedata = {"no active project."};
			tree = new JTree(treedata);
			this.getViewport().setView(tree);
			return;
		}
		
		File projectDirectory = setting.getProjectDirectory();
		File[] filelist = projectDirectory.listFiles();
		
		Vector<String> ardFilenames = new Vector<String>();
		Vector<File> audDirectories = new Vector<File>();
		for (File f: filelist) {
			
			//if it is just a file, check if it is '.ard' file or '.arh" file
			if (f.isFile()) {
				String filename = f.getName();
				if (filename.endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT) ||
					filename.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)) {
					ardFilenames.add(filename);
				}
				continue;
			}
			
			//here, we're sure that it is a directory.
			//if it is the user-defined object directory, add to the list.
			if (this.isUserDefinedObjectDirectory(f)) {
				audDirectories.add(f);
			}
			
		}
		Collections.sort(audDirectories);
		Collections.sort(ardFilenames);

		//build the tree.
		String projectFilename = setting.getProjectFile().getName();
		root = new DefaultMutableTreeNode(projectFilename);
		
		for (File f: audDirectories) {
			DefaultMutableTreeNode node = buildUserDefinedObjectNode(f);
			if (f != null) {
				root.add(node);
			}
		}
		
		//add ard files to the tree.
		for (String filename: ardFilenames) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(filename);
			if (filename.endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT)){
				root.add(child);
			}
		}
		
		//add arh files to the tree.
		for (String filename: ardFilenames) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(filename);
			if (filename.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)){
				root.add(child);
			}
		}
		
		DefaultTreeModel model = new DefaultTreeModel(root);
		tree = new JTree(model);
		tree.addMouseListener(this);
		
		this.getViewport().setView(tree);
		
		return;
	}
	
	/**
	 * @param dir
	 * @return
	 */
	public DefaultMutableTreeNode buildUserDefinedObjectNode(File dir)
	{
		if (isUserDefinedObjectDirectory(dir) == false) {
			return null;
		}
		
		boolean audFileFound = false;
		
		DefaultMutableTreeNode userDefineObjectDir = new DefaultMutableTreeNode(dir.getName());
		File[] children = dir.listFiles();
		
		for (File f: children) {
			String filename = f.getName();
			if (this.isUserDefinedObjectDefFile(filename)) {
				//found multiple aud files.
				if (audFileFound == true) {
					return null;
				}
				audFileFound = true;
			}
		}
		
		//no aud file found.
		if (audFileFound == false) {
			return null;
		}
		
		addUserDefinedObjectFilesToTree(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT, userDefineObjectDir, children);
		addUserDefinedObjectFilesToTree(ARFileConst.ARDESTAN_HEADER_FILE_EXTENSION_WITH_DOT, userDefineObjectDir, children);
		addUserDefinedObjectFilesToTree(ARFileConst.ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT, userDefineObjectDir, children);
		addUserDefinedObjectFilesToTree(ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT, userDefineObjectDir, children);
		addUserDefinedObjectFilesToTree(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT, userDefineObjectDir, children);

		return userDefineObjectDir;
	}
	
	/**
	 * @param extension
	 * @param parent
	 * @param children
	 */
	public void addUserDefinedObjectFilesToTree(String extension,  DefaultMutableTreeNode parent, File[] children)
	{
		for (File f: children) {
			String filename = f.getName();
			if (filename.endsWith(extension)) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(filename);
				parent.add(node);
			}
		}
		return;
	}
	
	@Override
	public void projectDirectoryChanged() {
		this.updateTree();
	}



	@Override
	public void mouseClicked(MouseEvent e) 
	{	
		//we process only double-clicking.
		if (e.getClickCount() < 2) {
			return;
		}
		
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null) {
			return;
		}
		ProjectSetting setting = ProjectSetting.getSingleton();
		for (int i = 0; i < paths.length; i++) {
			if (isUserDefinedObjectFile(paths[i])){
				File f = this.getUserDefinedObjectFile(paths[i]);
				if (f != null) {
					MainWindow window = ArdestanIDE.getMainWindow();
					if (ARNameVerifier.isValidArhFilename(f.getName())) {
						window.loadArdestanHelpFromFile(f);
					}
					else {
						window.loadUserDefinedObjectSourceCode(f);
					}
				}
				continue;
			}
			
			String filename = paths[i].getLastPathComponent().toString();
			if (filename.endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT) 	||
					filename.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)		){
				File f = new File(setting.getProjectDirectory(), filename);
				MainWindow window = ArdestanIDE.getMainWindow();
				window.loadArdestanSourceCode(f);
			}
		}
		return;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
				
		if (!e.isPopupTrigger()) {
			return;
		}
		
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		if(selRow != -1) {
			tree.addSelectionRow(selRow);
			popupMenu.show(e.getComponent(), e.getX(), e.getY()); 
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		this.mousePressed(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		//delete the selected file(s).
		if (e.getSource() == deleteFileItem) {
			this.deleteFile();
			return;
		}
		
		//rename the selected file.
		if (e.getSource() == renameFileItem) {
			this.renameFile();
			return;
		}
		
		//duplicate the selected file(s)
		if (e.getSource() == duplicateFileItem) {
			this.dupilicateFile();
			return;
		}
	}	
	
	
	/**
	 * 
	 */
	public void dupilicateFile()
	{
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null || paths.length == 0) {
			return;
		}
		
		if (paths.length != 1) {
			JOptionPane.showMessageDialog(this.getRootPane(), "You can only duplicate one file or one directory at a time.");			
			return;			
		}
		
		//check if the object is a user define file.
		if (isUserDefinedObjectFile(paths[0])){
			JOptionPane.showMessageDialog(this.getRootPane(), "You can only duplicate the whole directory of a user-defined object\n"
					+ "(it is not allowed to duplicate a single user-defined object file).");
			return;
		}
		
		File projectDir = ProjectSetting.getSingleton().getProjectDirectory();
		
		String filename = paths[0].getLastPathComponent().toString();
		File f = new File(projectDir, filename);
		
		//duplicate an user-define-object directory.
		if (f.isDirectory()) {
			if (isUserDefinedObjectDirectory(f)) {
				this.duplicateUserDefinedObjectDirectory(f);
				this.updateTree();
				return;
			}
			JOptionPane.showMessageDialog(this.getRootPane(), "This is not a user-defined object directory.");
			return;
		}
		
		//duplicate a single visual program.
		if (f.getName().endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT) ||
				f.getName().endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)) {
			this.duplicateArdestanVisualProgramFile(filename);
			this.updateTree();
			return;
		}
		
		//the code won't reach here.
		JOptionPane.showMessageDialog(this.getRootPane(), "This doesn't seem to be valid an ardestan visual program file.");
				
		return;
	}
	
	/**
	 * @param f
	 */
	public void duplicateUserDefinedObjectDirectory(File dir)
	{
		File oldAudFile = null;
		for (File f: dir.listFiles()) {
			if (f.getName().endsWith(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT)) {
				if (oldAudFile != null) {
					JOptionPane.showMessageDialog(this.getRootPane(), "Found a multiple aod files in the user-defined object directory. Duplication failed.");
					return;
				}
				oldAudFile = f;
			}
		}
		
		ARClassInfo oldAudInfo = null;
		
		try {
			oldAudInfo = JsonClassInfoLoader.loadSingleClassInfoFromFile(oldAudFile);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(this.getRootPane(), "Duplication failed due to IOException (see the Ardestan console).");
			Message.println(e);
			return;
		}
		
		
		String originalObjectName = oldAudInfo.getARClassName();
		DuplicateUDODirDialog duplicateUDODirDialog = new DuplicateUDODirDialog(originalObjectName);
		duplicateUDODirDialog.setAlwaysOnTop(true);
		
		int w = 400;
		int h = 110;
		
		JRootPane rp = this.getRootPane();
		Point loc = rp.getLocationOnScreen();
		
		int x = loc.x + rp.getWidth() / 2 - w / 2;
		int y = loc.y + rp.getHeight() / 2 - h / 2;
		
		duplicateUDODirDialog.setLocation(x, y);
		duplicateUDODirDialog.setSize(w, h);
		duplicateUDODirDialog.setModal(true);
		duplicateUDODirDialog.setVisible(true);
		
		boolean ret = duplicateUDODirDialog.isCanceled();
		if (ret == true) {
			return;
		}
		
		//------------------------------------------------------------------------------
		//now start copying.
		
		//create the directory first.
		File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
		String dupDirName = duplicateUDODirDialog.getDupObjectName();
				
		File dupDir = new File(projectDirectory, dupDirName);
		if (dupDir.exists()) {
			JOptionPane.showMessageDialog(this.getRootPane(), "The directory: " + dupDirName + " already exists. Use a differet name.");
			return;
		}
		
		
		ret = dupDir.mkdir();
		if (ret == false) {
			JOptionPane.showMessageDialog(this.getRootPane(), "The directory: " + dupDirName + " couldn't be created.");
			return;
		}
		
		
		String newClassName = dupDirName;
		
		ARClassInfoNative newAudInfo = new ARClassInfoNative();
		
		newAudInfo.setFields(	newClassName, 
								oldAudInfo.getBehaviorType(), 
								oldAudInfo.alwaysIncludeInBuild(),
								oldAudInfo.isOnlyLeftMostInletHot(),
								oldAudInfo.getMinArgNum(), 
								oldAudInfo.getMaxArgNum(),
								oldAudInfo.getArgTypes(),
								oldAudInfo.getRequiredSymbolIDs(),
								ARClassInfo.getDefaultHeaderFilename(newClassName), 
								ARClassInfo.getDefaultCppFilename(newClassName), 
								ARClassInfo.getDefaultInitFuncName(newClassName), 
								ARClassInfo.getDefaultTriggerFuncName(newClassName), 
								ARClassInfo.getDefaultStructName(newClassName),
								oldAudInfo.getNumOfInletsString(),
								oldAudInfo.getNumOfOutletsString());
		
		try {
			ARClassInfo.createAudFile(dupDir, newAudInfo);
			ARClassInfo.copyCppAndHeaderFiles(dir, dupDir, oldAudInfo, newAudInfo);
			ARClassInfo.copyHelpFile(dir, dupDir, oldAudInfo.getARClassName(), newAudInfo.getARClassName());
		}
		catch(IOException e) {
			Message.println(e);
			JOptionPane.showMessageDialog(this.getRootPane(), "Duplication failed.");
			return;

		}
		
		Message.println("Duplicated the user-defined object directory: " + dir.getName() + " as " + newClassName);

		return;
		
	}
	
	
	/**
	 * @param filename
	 */
	public void duplicateArdestanVisualProgramFile(String filename)
	{
		//duplicate the ardestan file.
		DuplicateFileDialog duplicateFileDialog = new DuplicateFileDialog(filename);
		duplicateFileDialog.setAlwaysOnTop(true);
		
		File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
		File src = new File(projectDirectory, filename);
		if (src.isDirectory()) {
			return;			
		}
		
		int w = 400;
		int h = 110;
		
		JRootPane rp = this.getRootPane();
		Point loc = rp.getLocationOnScreen();
		
		int x = loc.x + rp.getWidth() / 2 - w / 2;
		int y = loc.y + rp.getHeight() / 2 - h / 2;
		
		duplicateFileDialog.setLocation(x, y);
		duplicateFileDialog.setSize(w, h);
		duplicateFileDialog.setModal(true);
		duplicateFileDialog.setVisible(true);
		
		boolean ret = duplicateFileDialog.isCanceled();
		if (ret == true) {
			return;
		}
		
		//duplicate the file
		String newFilename = duplicateFileDialog.getDupFilename();
		if (ARNameVerifier.isValidObjectName(newFilename)) {
			if (ARNameVerifier.isValidArdFilename(filename)) {
				newFilename = newFilename + ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT;			
			}
			else if (ARNameVerifier.isValidArhFilename(filename)){
				newFilename = newFilename + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT;							
			}
		}
		
		File dst = new File(projectDirectory, newFilename);
		if (dst.exists()) {
			JOptionPane.showMessageDialog(this.getRootPane(), "The file: " + newFilename + " already exists. use a differet name.");
			return;
		}
		
		Path srcPath = src.toPath();
		Path dstPath = dst.toPath();
		
		try {
			Files.copy(srcPath, dstPath);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(this.getRootPane(), "Duplication failed due to IOException (see the Ardestan Console).");
			Message.println(e);
			return;
		}
		Message.println("Duplicated the file: " + filename + " as " + newFilename);
		
		return;
	}
	
	/**
	 * 
	 */
	public void renameFile()
	{
		TreePath[] paths = tree.getSelectionPaths();

		if (paths.length != 1) {
			JOptionPane.showMessageDialog(this.getRootPane(), "Only one file can be renamed at a time.");
			return;
		}
		
		
		//if it is not a '.ard' file or a '.arh' file, show a message dialog and return.
		//first, check if it is a file
		String filename = paths[0].getLastPathComponent().toString();

		File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
		File src = new File(projectDirectory, filename);
		if (src.isDirectory()) {
			JOptionPane.showMessageDialog(this.getRootPane(), "Only a '.ard' file or '.arh' file can be renamed here (This is a directory).\n" 
					+ "To rename a user-define object file, click its'.aud' file and use the User-defined Object Update Wizard.");
			return;			
		}
		
		//if the file is a user-defined-object file, we don't rename it here.
		if (isUserDefinedObjectFile(paths[0])) {
			JOptionPane.showMessageDialog(this.getRootPane(), "To rename a user-define object file, click its'.aud' file and use the User-defined Object Update Wizard.");	
			return;
		}
		
		//check if it is a patch file.
		if (!filename.endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT) && 
			!filename.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)			){
			JOptionPane.showMessageDialog(this.getRootPane(), "Only a '.ard' file or '.arh' file can be renamed here.\n"
					+ "To rename a user-define object file, click its'.aud' file and use the User-defined Object Update Wizard.");
			return;			
		}
		
	
		//rename the ardestan file.
		RenameFileDialog renameDialog = new RenameFileDialog(filename);
		renameDialog.setAlwaysOnTop(true);
		
		int w = 400;
		int h = 110;
		
		JRootPane rp = this.getRootPane();
		Point loc = rp.getLocationOnScreen();
		
		int x = loc.x + rp.getWidth() / 2 - w / 2;
		int y = loc.y + rp.getHeight() / 2 - h / 2;
		
		renameDialog.setLocation(x, y);
		renameDialog.setSize(w, h);
		renameDialog.setModal(true);
		renameDialog.setVisible(true);
		
		boolean ret = renameDialog.isCanceled();
		if (ret == true) {
			return;
		}
		
		//rename the file
		String newFilename = renameDialog.getNewFilename();
		//if it is just an object name.
		if (ARNameVerifier.isValidObjectName(newFilename)) {
			if (ARNameVerifier.isValidArdFilename(filename)) {
				newFilename = newFilename + ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT;			
			}
			else if (ARNameVerifier.isValidArhFilename(filename)){
				newFilename = newFilename + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT;							
			}
		}

		File dst = new File(projectDirectory, newFilename);
		
		if (dst.exists()) {
			JOptionPane.showMessageDialog(this.getRootPane(), "The file: " + newFilename + " already exists. use a differet name.");
			return;
		}
		
		Path srcPath = src.toPath();
		Path dstPath = dst.toPath();
		
		try {
			Files.move(srcPath, dstPath);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(this.getRootPane(), "Renaming failed.");
			Message.println(e);
		}
		Message.println("Renamed the file: " + filename + " to " + newFilename);
		
		
		MainWindow window = ArdestanIDE.getMainWindow();
		window.renamePane(filename, newFilename);
		this.updateTree();
		
		return;
	}
	
	/**
	 * 
	 */
	public void deleteFile()
	{
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null) {
			return;
		}
		
		for (int i = 0; i < paths.length; i++) {
			if (isUserDefinedObjectFile(paths[i])) {
				JOptionPane.showMessageDialog(this.getRootPane(), "You can only delete the whole directory of a user-defined object.");			
				return;
			}
		}
		
		for (int i = 0; i < paths.length; i++) {
			if (isUserDefinedObjectFile(paths[i])) {
				JOptionPane.showMessageDialog(this.getRootPane(), "If you want to delete a user-defined object file, delete the entire directory.\n"
						+ "User-defined object files can only be deleted altoegther.");	
				return;
			}
		}
	
		
		int ret = JOptionPane.showConfirmDialog(this.getRootPane(), "Are you sure that you want to delete the selected file(s)?\nYou can't undo this action.", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
		if (ret != JOptionPane.OK_OPTION){
			return;
		}
		
		
		for (int i = 0; i < paths.length; i++) {
						
			String filename = paths[i].getLastPathComponent().toString();
		
			if (filename.endsWith(ARFileConst.ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT) 	||
				filename.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)			){
				ProjectSetting setting = ProjectSetting.getSingleton();
				MainWindow window = ArdestanIDE.getMainWindow();
				window.removePane(filename);
				new File(setting.getProjectDirectory(), filename).delete();
			}
			else if (isUserDefinedObjectDirectoryNode(paths[i])){
				this.deleteUserDefinedObjectDirectory(paths[i]);
			}
		}
		
		this.updateTree();
		return;
	}
	
	
	/**
	 * @param f
	 * @return
	 */
	public boolean isUserDefinedObjectDirectory(File directory)
	{
		if (directory.isDirectory() == false) {
			return false;
		}
		
		//we allow only one '.auo' file to exist in the directory.
		boolean aodFound = false;
		boolean cppFound = false;
		boolean hppFound = false;
		
		File[] filelist = directory.listFiles();
		for (File f: filelist) {
			if (f.getName().endsWith(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT)) {
				if (aodFound == false) {
					aodFound = true;
				}
				else {
					return false;
				}
			}
			else if (f.getName().endsWith(ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT)) {
				if (cppFound == false) {
					cppFound = true;
				}
				else {
					return false;
				}
			}
			else if (f.getName().endsWith(ARFileConst.ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT)) {
				if (hppFound == false) {
					hppFound = true;
				}
				else {
					return false;
				}
			}
		}
		
		//all the files must be found.
		return aodFound && cppFound && hppFound;
	}
	
	/**
	 * @param path
	 * @return
	 */
	public boolean isUserDefinedObjectDirectoryNode(TreePath path)
	{
		boolean b = path.getParentPath() != null && root == path.getParentPath().getLastPathComponent();
		if (b == false) {
			return false;
		}
		
		File d = new File(ProjectSetting.getSingleton().getProjectDirectory(), path.getLastPathComponent().toString());
		
		return isUserDefinedObjectDirectory(d);
	}
	
	
	/**
	 * @param filename
	 * @return
	 */
	public boolean isUserDefinedObjectDefFile(String filename)
	{
		return filename.endsWith(ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT);
	}
	
	/**
	 * @param filename
	 * @return
	 */
	public boolean isUserDefinedObjectFile(String filename)
	{
		if (filename.endsWith(ARFileConst.ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT)) {
			return true;
		}
		if (filename.endsWith(ARFileConst.ARDESTAN_HEADER_FILE_EXTENSION_WITH_DOT)) {
			return true;
		}
		if (filename.endsWith(ARFileConst.ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT)) {
			return true;
		}
		if (filename.endsWith(ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT)) {
			return true;
		}
		if (isUserDefinedObjectDefFile(filename)) {
			return true;
		}

		return false;
	}
	
	/**
	 * @param path
	 * @return
	 */
	public boolean isUserDefinedObjectFile(TreePath path)
	{
		//check if this is a direct child directory of the project root.
		if (path.getParentPath() == null) {
			return false;
		}
		if (root == path.getParentPath().getLastPathComponent()) {
			return false;
		}
		
		String filename = path.getLastPathComponent().toString();

		return isUserDefinedObjectFile(filename);
	}
	
	/**
	 * @param path
	 */
	public void deleteUserDefinedObjectDirectory(TreePath path)
	{
		if (!isUserDefinedObjectDirectoryNode(path)) {
			return;
		}
		
		
		ProjectSetting setting = ProjectSetting.getSingleton();
		String dirName = path.getLastPathComponent().toString();
		
		try {
			ARClassInfo oldAudInfo = null;
			File udoDir  = new File(setting.getProjectDirectory(), dirName);
			File audFile = new File(udoDir, dirName + ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT);
			oldAudInfo = JsonClassInfoLoader.loadSingleClassInfoFromFile(audFile);
			
			MainWindow window = ArdestanIDE.getMainWindow();
			window.removePane(oldAudInfo.getCppFilenameWithoutPath());
			window.removePane(oldAudInfo.getHeaderFilenameWithoutPath());
			
			File helpFile = new File(udoDir, dirName + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT);
			window.removePane(helpFile);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(this.getRootPane(), "Deletion failed due to IOException (see the Ardestan console).");
			Message.println(e);
			return;
		}
		

		Path rootPath = Paths.get(setting.getProjectDirectory() + File.separator + dirName);
		try {
			Files.walk(rootPath).sorted(Comparator.reverseOrder())
								.map(Path::toFile)
								.forEach(File::delete);
		}
		catch(IOException e) {
			Message.print(e);
		}
		
		return;
	}
	
	/**
	 * @param path
	 * @return
	 */
	public File getUserDefinedObjectFile(TreePath path)
	{
		//check if this is a direct child directory of the project root.
		if (isUserDefinedObjectFile(path) == false) {
			return null;
		}
		ProjectSetting setting = ProjectSetting.getSingleton();
		String filename = setting.getProjectDirectory().getAbsolutePath() + File.separator 
						+ path.getParentPath().getLastPathComponent().toString() + File.separator
						+ path.getLastPathComponent().toString();	
		return new File(filename);
	}
	
	/**
	 * @param path
	 */
	public void deleteUserDefinedObjectFile(TreePath path)
	{
		//check if this is a direct child directory of the project root.
		if (isUserDefinedObjectFile(path) == false) {
			return;
		}
		File f = getUserDefinedObjectFile(path);
		if (f != null && f.isDirectory() == false) {
			f.delete();
		}
		return;
	}
}
