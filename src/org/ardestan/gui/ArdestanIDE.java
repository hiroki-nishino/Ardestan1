package org.ardestan.gui;


import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;

import org.apache.commons.lang3.SystemUtils;
import org.ardestan.arclass.ARClassDatabase;
import org.ardestan.arduinocli.ArduinoCLI;
import org.ardestan.arduinocli.ArduinoCLIBackground;
import org.ardestan.misc.ProjectSetting;


/**
 * @author hiroki
 *
 */
public class ArdestanIDE  
{

	private static MainWindow window = null;
	
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{		
		//we use the Nimbus Look and Feel.
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

		System.out.println(System.getProperty("sun.arch.data.model"));
		System.out.println(SystemUtils.OS_ARCH);


		String model 	= System.getProperty("sun.arch.data.model");
		String arch		= SystemUtils.OS_ARCH;

		boolean isARM	= arch.contains("arm");
		
		if (SystemUtils.IS_OS_MAC) {
			if (model.equals("32")){
				System.out.println("Unsupported Mac OS X. aborted.");
				System.exit(-1);
			}
			System.out.println("MAC OS X (64 bit) detected.");
			
			ArduinoCLI.init(ArduinoCLI.MAC_OS);

			//ajdust key bindings for Nimbus.
			setupOSXKeyStrokes((InputMap) UIManager.get("EditorPane.focusInputMap"));
			setupOSXKeyStrokes((InputMap) UIManager.get("FormattedTextField.focusInputMap"));
			setupOSXKeyStrokes((InputMap) UIManager.get("PasswordField.focusInputMap"));
			setupOSXKeyStrokes((InputMap) UIManager.get("TextField.focusInputMap"));
			setupOSXKeyStrokes((InputMap) UIManager.get("TextPane.focusInputMap"));
			setupOSXKeyStrokes((InputMap) UIManager.get("TextArea.focusInputMap"));
			setupOSXKeyStrokes((InputMap) UIManager.get("Table.ancestorInputMap"));
			setupOSXKeyStrokes((InputMap) UIManager.get("Tree.focusInputMap"));
			
			UIManager.put("TabbedPaneUI", "javax.swing.plaf.basic.BasicTabbedPaneUI");				
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.application.name", "Ardestan IDE");
		}
		else if (SystemUtils.IS_OS_WINDOWS) {			
			if (model.equals("64")){
				System.out.println("Windows (64 bit) detected.");
				ArduinoCLI.init(ArduinoCLI.WIN_64);
			}
			else if (model.equals("32")){
				System.out.println("Windows (32 bit) detected.");
				ArduinoCLI.init(ArduinoCLI.WIN_32);				
			}
			else {
				System.out.println("Unsupported Windows. aborted.");
				System.exit(-1);
			}
		}
		else if (SystemUtils.IS_OS_LINUX) {
			if (model.equals("64")){
				if (isARM) {
					System.out.println("Linux (64 bit ARM) detected.");
					ArduinoCLI.init(ArduinoCLI.LINUX_64_ARM);					
				}
				else {
					System.out.println("Linux (64 bit) detected.");
					ArduinoCLI.init(ArduinoCLI.LINUX_64);
				}
			}
			else if (model.equals("32")){
				if (isARM) {
					System.out.println("Linux (32 bit ARM) detected.");
					ArduinoCLI.init(ArduinoCLI.LINUX_32_ARM);	
				}
				else {
					System.out.println("Linux (32 bit) detected.");
					ArduinoCLI.init(ArduinoCLI.LINUX_32);	
				}
			}
			else {
				System.out.println("Unsupported Linux. aborted.");
				System.exit(-1);
			}
		}

		DialogSizes.init(ArduinoCLI.getOS());

		ARClassDatabase.initClassDatabaseFromResource();	
		ARClassDatabase.getSingleton().startWatchThread();
		
		
		ProjectSetting setting = ProjectSetting.getSingleton();
		setting.loadPreferences();
		
		
		window = new MainWindow();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				window.resize(1024, 768);
				window.setVisible(true);
				
				//if this is the first execution, install required supports.
				boolean firstExec = ProjectSetting.getSingleton().isFirstExec();
				if (firstExec) {
					ArduinoCLIBackground cli = ArduinoCLIBackground.getSingleton();
					while(true) {
						File path = new File(System.getProperty("java.io.tmpdir"));
						boolean ret = cli.firstExec(path);
						if (ret == false) {
							JOptionPane.showMessageDialog(window.getFrame(), "Failed to peform the 'update-index' command. "
																			+"Make sure you are connected to the Internet");
						}
						else {
							setting.setFirstExec(false);
							try {
								setting.savePreferences();
								
							}
							catch(IOException e) {
								Message.print(e);
							}
							break;
						}
					}
				}
				
				//load project if necessary.
				String projectFile = setting.getMostRecentProjectFile();

				if (projectFile != null) {
					try {
						window.loadProject(new File(projectFile));
					}
					catch(IOException e) {
						Message.print(e);
					}
				}

			}
		});
		return;
	}
	
	
	/**
	 * @param inputMap
	 */
	private static void setupOSXKeyStrokes(InputMap inputMap) {
		  inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
		  inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
		  inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
		  inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
		  inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), "copy");
		  inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), "selectAll");
	}
	
	/**
	 * @return
	 */
	public static MainWindow getMainWindow() {
		return window;
	}
	
}
