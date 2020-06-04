/**
 * 
 */
package org.ardestan.gui.dialog.udowiz;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ardestan.arclass.ARClassInfo;
import org.ardestan.arclass.ARClassInfoNative;
import org.ardestan.generator.ARConnectionInfo;
import org.ardestan.gui.visual.CommentBox;
import org.ardestan.gui.visual.GUIFont;
import org.ardestan.gui.visual.ObjectBox;
import org.ardestan.gui.visual.ObjectBoxConnection;
import org.ardestan.json.JsonARInstanceInfo;
import org.ardestan.json.JsonARProgram;
import org.ardestan.json.JsonCommentBox;
import org.ardestan.json.JsonProgramWriter;
import org.ardestan.misc.ARFileConst;
import org.ardestan.misc.ARNameVerifier;
import org.ardestan.misc.ProjectSetting;

/**
 * @author hiroki
 *
 */
public class UserDefinedObjectInfoPanel extends JPanel implements DocumentListener, ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int NUM_OF_ARG_TYPE_SELECTORS = 8;
	
	protected enum BehaviorType {
		NORMAL,
		REQUIRE_LOADBANG,
		REQUIRE_POLLING,
		REQUIRE_SCHEDULLING;
		
		/**
		 * @return
		 */
		public int toIndex()
		{
			switch(this)
			{
				case NORMAL:
					return 0;
					
				case REQUIRE_LOADBANG:
					return 1;

				case REQUIRE_POLLING:
					return 2;
					
				case REQUIRE_SCHEDULLING:
					return 3;
			}
			
			return -1;
			
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		public String toString() 
		{
			switch(this)
			{
				case NORMAL:
					return "normal";
					
				case REQUIRE_LOADBANG:
					return "require loadBang";

				case REQUIRE_POLLING:
					return "require polling";
					
				case REQUIRE_SCHEDULLING:
					return "require scheduling";
			}
			
			return null;
		}
	};
	protected static String[] BEHAVIOR_STRING = {
												BehaviorType.NORMAL.toString(), 
												BehaviorType.REQUIRE_LOADBANG.toString(), 
												BehaviorType.REQUIRE_POLLING.toString(),
												BehaviorType.REQUIRE_SCHEDULLING.toString()
												};
	
	
	protected static String[] NUM_INLETS_STRINGS 	= {	"0", "1", "2", "3", "4", "5", "first_arg (max:5)", "num_args (max:5)"};
	
	protected static String[] NUM_INLETS_STRINGS_CLASSINFO = { "0", "1", "2", "3", "4", "5",
			ARClassInfo.NUM_OF_INLETS_DEPENDS_ON_FIRST_ARG,
			ARClassInfo.NUM_OF_INLETS_DEPENDS_ON_NUM_OF_ARGS,
			ARClassInfo.NUM_OF_INLETS_DEPENDS_ON_NUM_OF_ARGS_PLUS_ONE,
	};
	
	protected static String[] NUM_OUTLETS_STRINGS 	= {"0", "1", "2", "3", "4", "5", "6", "7", "8", "first_arg (max:8)",  "num_args (max:8)"};

	protected static String[] NUM_OUTLETS_STRINGS_CLASSINFO = {"0", "1", "2", "3", "4", "5", "6", "7", "8", 
			ARClassInfo.NUM_OF_OUTLETS_DEPENDS_ON_FIRST_ARG,
			ARClassInfo.NUM_OF_OUTLETS_DEPENDS_ON_NUM_OF_ARGS,
			ARClassInfo.NUM_OF_INLETS_DEPENDS_ON_NUM_OF_ARGS_PLUS_ONE
	};

	String[] MIN_ARG_NUM_STRINGS = {"0", "1", "2", "3", "4", "5", "6", "7", "8"};
	String[] MAX_ARG_NUM_STRINGS = {"0", "1", "2", "3", "4", "5", "6", "7", "8"};

	protected JTextField	tfClassName;
	protected JTextField	tfHeaderFilename;
	protected JTextField	tfCppFilename;
	protected JTextField	tfInitFuncName;
	protected JTextField	tfTriggerFuncName;
	protected JTextField	tfStructName;
	protected JTextField	tfSymbols;

	protected JCheckBox		ckAlwaysIncludeBuild;
	protected JCheckBox		ckOnlyLeftMostInletIsHot;
	
	protected JLabel		lbSymbols;

	protected JComboBox<String>	cbBehaviorType;

	protected JComboBox<String>	cbNumOfInlets;
	protected JComboBox<String>	cbNumOfOutlets;

	protected JComboBox<String>	cbMinArgNum;
	protected JComboBox<String>	cbMaxArgNum;
	
	protected JButton		btnOkay;
	protected JButton		btnCancel;

	protected Vector<Vector<JCheckBox>> vctCheckBoxes;
	
	
	/**
	 * @return
	 */
	public ARClassInfoNative getARClassInfoNative()
	{
		if (this.checkValidity() == false) {
			return null;
		}
		
		ARClassInfoNative info = new ARClassInfoNative();
		
		String className = tfClassName.getText().trim();
		info.setFields(	className, 
						this.getBehaviorType(), 
						ckAlwaysIncludeBuild.isSelected(), 
						ckOnlyLeftMostInletIsHot.isSelected(), 
						cbMinArgNum.getSelectedIndex(), 
						cbMaxArgNum.getSelectedIndex(), 
						getArgTypes(), 
						getSymbols(), 
						ARClassInfo.getDefaultHeaderFilename(className), 
						ARClassInfo.getDefaultCppFilename(className), 
						ARClassInfo.getDefaultInitFuncName(className), 
						ARClassInfo.getDefaultTriggerFuncName(className), 
						ARClassInfo.getDefaultStructName(className),
						NUM_INLETS_STRINGS_CLASSINFO[cbNumOfInlets.getSelectedIndex()], 
						NUM_OUTLETS_STRINGS_CLASSINFO[cbNumOfOutlets.getSelectedIndex()]);
		
		
		return info;
	}
	/**
	 * @param info
	 */
	public void setClassInfo(ARClassInfo info)
	{
		tfClassName			.setText(info.getARClassName());
		tfHeaderFilename	.setText(info.getHeaderFilenameWithoutPath());
		tfCppFilename		.setText(info.getCppFilenameWithoutPath());
		tfInitFuncName		.setText(info.getFuncNameInit());
		tfTriggerFuncName	.setText(info.getFuncNameTrigger());
		tfStructName		.setText(info.getStructName());
		
		ckAlwaysIncludeBuild.setSelected(info.alwaysIncludeInBuild());
		ckOnlyLeftMostInletIsHot.setSelected(info.isOnlyLeftMostInletHot());
		
		this.setSymbolNames(info);
		this.setBehaviorType(info);
		this.setNumOfInlets(info);
		this.setNumOfOutlets(info);
	
		this.setMinArgNum(info);
		this.setMaxArgNum(info);
	
		this.setArgTypes(info);
		
		this.checkValidity();
	}
	
	/**
	 * @param info
	 */
	protected void setMinArgNum(ARClassInfo info)
	{
		int minArgNum = info.getMinArgNum();
		if (minArgNum < 0 || minArgNum >= MIN_ARG_NUM_STRINGS.length) {
			minArgNum = 0;
		}
		cbMinArgNum.setSelectedIndex(minArgNum);
	}
	
	/**
	 * @param info
	 */
	protected void setMaxArgNum(ARClassInfo info)
	{
		int minArgNum = info.getMinArgNum();
		int maxArgNum = info.getMaxArgNum();
		if (maxArgNum < minArgNum || maxArgNum >= MAX_ARG_NUM_STRINGS.length) {
			maxArgNum = minArgNum;
		}
		cbMaxArgNum.setSelectedIndex(maxArgNum);
	}
	
	/**
	 * @return
	 */
	public String[] getArgTypes()
	{
		int maxArgNum = cbMaxArgNum.getSelectedIndex();
		if (maxArgNum == 0) {
			return null;
		}
		String[] argTypes = new String[maxArgNum];

		for (int i = 0; i < maxArgNum; i++) {
			Vector<JCheckBox> checkBoxes = vctCheckBoxes.get(i);
			StringBuffer buf = new StringBuffer();
			if (checkBoxes.get(0).isSelected()) {
				buf.append("i");
			}
			if (checkBoxes.get(1).isSelected()) {
				buf.append("f");
			}
			if (checkBoxes.get(2).isSelected()) {
				buf.append("s");
			}
			if (checkBoxes.get(3).isSelected()) {
				buf.append("S");
			}
			argTypes[i] = buf.toString();
		}
		return argTypes;
	}
	/**
	 * @param info
	 */
	protected void setArgTypes(ARClassInfo info)
	{
		String[] argTypes = info.getArgTypes();
		if (argTypes == null) {
			return;
		}
		
		int numArgTypes = argTypes.length;
		if (numArgTypes >= NUM_OF_ARG_TYPE_SELECTORS) {
			numArgTypes = NUM_OF_ARG_TYPE_SELECTORS;
		}
		
		if (numArgTypes >= info.getMaxArgNum()) {
			numArgTypes = info.getMaxArgNum();
		}
		
		for (int i = 0; i < numArgTypes; i++) {
			String argType = argTypes[i];
			Vector<JCheckBox> checkBoxes = vctCheckBoxes.get(i);
			if (argType.contains(ARClassInfo.TYPE_CHAR_INT + "")) {
				checkBoxes.get(0).setSelected(true);
			}
			if (argType.contains(ARClassInfo.TYPE_CHAR_FLOAT + "")) {
				checkBoxes.get(1).setSelected(true);
			}
			if (argType.contains(ARClassInfo.TYPE_CHAR_SYMBOL + "")) {
				checkBoxes.get(2).setSelected(true);
			}
			if (argType.contains(ARClassInfo.TYPE_CHAR_STRING + "")) {
				checkBoxes.get(3).setSelected(true);
			}
		}

		checkArgTypes();
		
		return;
	}
	
	
	/**
	 * @param info
	 */
	protected void setNumOfInlets(ARClassInfo info)
	{
		String numOfInlets = info.getNumOfInletsString().trim();
		for (int i = 0; i < NUM_INLETS_STRINGS_CLASSINFO.length; i++) {
			if (numOfInlets.equals(NUM_INLETS_STRINGS_CLASSINFO[i])) {
				cbNumOfInlets.setSelectedIndex(i);
				return;
			}
		}
		
		cbNumOfInlets.setSelectedIndex(0);
		return ;
	}
	
	/**
	 * @param info
	 */
	protected void setNumOfOutlets(ARClassInfo info)
	{
		String numOfInlets = info.getNumOfInletsString().trim();
		for (int i = 0; i < NUM_OUTLETS_STRINGS_CLASSINFO.length; i++) {
			if (numOfInlets.equals(NUM_OUTLETS_STRINGS_CLASSINFO[i])) {
				cbNumOfOutlets.setSelectedIndex(i);
				return;
			}
		}
		
		cbNumOfOutlets.setSelectedIndex(0);
		return ;
	}

	
	/**
	 * @return
	 */
	public String getBehaviorType()
	{
		if (cbBehaviorType.getSelectedIndex() == BehaviorType.REQUIRE_POLLING.toIndex()) {
			return ARClassInfo.REQUIRE_POLLING;
		}
		
		if (cbBehaviorType.getSelectedIndex() == BehaviorType.REQUIRE_LOADBANG.toIndex()) {
			return ARClassInfo.BANG_ON_LOAD;
		}
		
		if (cbBehaviorType.getSelectedIndex() == BehaviorType.REQUIRE_SCHEDULLING.toIndex()) {
			return ARClassInfo.REQUIRE_SCHEDULING;
		}
		
		return ARClassInfo.NORMAL_OBJECTS;
	
	}
	/**
	 * @param info
	 */
	protected void setBehaviorType(ARClassInfo info)
	{
		if (info.requirePolling()) {
			cbBehaviorType.setSelectedIndex(BehaviorType.REQUIRE_POLLING.toIndex());
			return;
		}
		
		if (info.requireLoadBang()) {
			cbBehaviorType.setSelectedIndex(BehaviorType.REQUIRE_LOADBANG.toIndex());			
			return;
		}
		
		if (info.requireScheduling()) {
			cbBehaviorType.setSelectedIndex(BehaviorType.REQUIRE_SCHEDULLING.toIndex());			
			return;			
		}
		
		cbBehaviorType.setSelectedIndex(BehaviorType.NORMAL.toIndex());
		return;		
	}
	

	/**
	 * @param info
	 */
	protected void setSymbolNames(ARClassInfo info)
	{
		String[] symbols = info.getRequiredSymbolIDs();
		StringBuffer sb = new StringBuffer();
		if (symbols != null) {
			boolean first = true;
			for (String s: symbols) {
				s = s.trim();
				if (first) {
					first = false;
				}
				else {
					sb.append(", ");
				}
				sb.append(s);
			}
		}
		
		tfSymbols.setText(sb.toString());
		return;
	}

	/**
	 * 
	 */
	public UserDefinedObjectInfoPanel(ActionListener actionListener)
	{
		//------------------------------------------------------------------------------
		//								object info area
		//------------------------------------------------------------------------------		
		JPanel objectInfoArea = new JPanel();
		objectInfoArea.setBorder(BorderFactory.createTitledBorder("Object Info"));

		GroupLayout layout = new GroupLayout(objectInfoArea);
		objectInfoArea.setLayout(layout);

		layout.setAutoCreateGaps(true);;
		layout.setAutoCreateContainerGaps(true);

		JLabel lbClassName  = new JLabel("class name ");
		tfClassName			= new JTextField();
		tfClassName.enableInputMethods(false);

		JLabel lbHeaderFilename	= new JLabel("header filename ");
		tfHeaderFilename		= new JTextField();
		tfHeaderFilename.setEditable(false);
		tfHeaderFilename.setForeground(Color.DARK_GRAY);
		tfHeaderFilename.setBackground(Color.LIGHT_GRAY);
		
		JLabel lbCppFilename	= new JLabel("cpp filename    ");
		tfCppFilename			= new JTextField();
		tfCppFilename.setEditable(false);
		tfCppFilename.setForeground(Color.DARK_GRAY);
		tfCppFilename.setBackground(Color.LIGHT_GRAY);

		JLabel lbInitFuncName	= new JLabel("init func name  ");	
		tfInitFuncName			= new JTextField();
		tfInitFuncName.setEditable(false);
		tfInitFuncName.setForeground(Color.DARK_GRAY);
		tfInitFuncName.setBackground(Color.LIGHT_GRAY);

		JLabel lbTriggerFuncName	= new JLabel("trigger func name  ");	
		tfTriggerFuncName			= new JTextField();
		tfTriggerFuncName.setEditable(false);
		tfTriggerFuncName.setForeground(Color.DARK_GRAY);
		tfTriggerFuncName.setBackground(Color.LIGHT_GRAY);

		JLabel lbStructName		= new JLabel("struct name  ");	
		tfStructName			= new JTextField();
		tfStructName.setEditable(false);
		tfStructName.setForeground(Color.DARK_GRAY);
		tfStructName.setBackground(Color.LIGHT_GRAY);

		JLabel lbAlwaysIncludeInBuild	= new JLabel("always include in build");
		ckAlwaysIncludeBuild			= new JCheckBox(" (check if true)");
	
		JLabel lbOnlyLeftMostInletIsHot	= new JLabel("only the inlet #0 triggers");
		ckOnlyLeftMostInletIsHot		= new JCheckBox(" (check if true)");
		ckOnlyLeftMostInletIsHot.setSelected(true);

		JLabel lbBehaviorType	= new JLabel("behavior type");
		cbBehaviorType			= new JComboBox<String>(BEHAVIOR_STRING);
		
		JLabel lbInlets				= new JLabel("inlet(s)");
		cbNumOfInlets				= new JComboBox<String>(NUM_INLETS_STRINGS);

		JLabel lbOutlets			= new JLabel("outlet(s)");
		cbNumOfOutlets				= new JComboBox<String>(NUM_OUTLETS_STRINGS);

		JLabel lbMinArgNum			= new JLabel("min arg num");
		cbMinArgNum					= new JComboBox<String>(MIN_ARG_NUM_STRINGS);

		JLabel lbMaxArgNum			= new JLabel("max arg num");
		cbMaxArgNum					= new JComboBox<String>(MAX_ARG_NUM_STRINGS);

		
		
		SequentialGroup sg = layout.createSequentialGroup();
		ParallelGroup left = layout.createParallelGroup();
		left.addComponent(lbClassName);
		left.addComponent(lbHeaderFilename);
		left.addComponent(lbCppFilename);
		left.addComponent(lbInitFuncName);
		left.addComponent(lbTriggerFuncName);
		left.addComponent(lbStructName);
		left.addComponent(lbAlwaysIncludeInBuild);
		left.addComponent(lbOnlyLeftMostInletIsHot);
		left.addComponent(lbBehaviorType);
		left.addComponent(lbInlets);		
		left.addComponent(lbOutlets);
		left.addComponent(lbMinArgNum);
		left.addComponent(lbMaxArgNum);
		

		ParallelGroup right = layout.createParallelGroup();
		right.addComponent(tfClassName);
		right.addComponent(tfHeaderFilename);
		right.addComponent(tfCppFilename);
		right.addComponent(tfInitFuncName);
		right.addComponent(tfTriggerFuncName);
		right.addComponent(tfStructName);
		right.addComponent(ckAlwaysIncludeBuild);
		right.addComponent(ckOnlyLeftMostInletIsHot);
		right.addComponent(cbBehaviorType);
		right.addComponent(cbNumOfInlets);
		right.addComponent(cbNumOfOutlets);
		right.addComponent(cbMinArgNum);
		right.addComponent(cbMaxArgNum);

		
		sg.addGroup(left);
		sg.addGroup(right);

		layout.setHorizontalGroup(sg);

		SequentialGroup sg2 = layout.createSequentialGroup();
		ParallelGroup row1 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row1.addComponent(lbClassName);
		row1.addComponent(tfClassName);

		ParallelGroup row2 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row2.addComponent(lbHeaderFilename);
		row2.addComponent(tfHeaderFilename);

		ParallelGroup row3 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row3.addComponent(lbCppFilename);
		row3.addComponent(tfCppFilename);

		ParallelGroup row4 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row4.addComponent(lbInitFuncName);
		row4.addComponent(tfInitFuncName);

		ParallelGroup row5 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row5.addComponent(lbTriggerFuncName);
		row5.addComponent(tfTriggerFuncName);

		ParallelGroup row6 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row6.addComponent(lbStructName);
		row6.addComponent(tfStructName);

		ParallelGroup row7 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row7.addComponent(lbAlwaysIncludeInBuild);
		row7.addComponent(ckAlwaysIncludeBuild);

		ParallelGroup row8 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row8.addComponent(lbOnlyLeftMostInletIsHot);
		row8.addComponent(ckOnlyLeftMostInletIsHot);

		ParallelGroup row9 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row9.addComponent(lbBehaviorType);
		row9.addComponent(cbBehaviorType);

		ParallelGroup row10 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row10.addComponent(lbInlets);
		row10.addComponent(cbNumOfInlets);

		ParallelGroup row11 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row11.addComponent(lbOutlets);
		row11.addComponent(cbNumOfOutlets);

		ParallelGroup row12 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row12.addComponent(lbMinArgNum);
		row12.addComponent(cbMinArgNum);

		ParallelGroup row13 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		row13.addComponent(lbMaxArgNum);
		row13.addComponent(cbMaxArgNum);

		sg2.addGroup(row1);
		sg2.addGroup(row2);
		sg2.addGroup(row3);
		sg2.addGroup(row4);
		sg2.addGroup(row5);
		sg2.addGroup(row6);
		sg2.addGroup(row7);
		sg2.addGroup(row8);
		sg2.addGroup(row9);
		sg2.addGroup(row10);
		sg2.addGroup(row11);
		sg2.addGroup(row12);
		sg2.addGroup(row13);

		layout.setVerticalGroup(sg2);		

		//------------------------------------------------------------------------------
		//								arg types
		//------------------------------------------------------------------------------		
		JPanel argTypesArea = new JPanel();
		argTypesArea.setBorder(BorderFactory.createTitledBorder("Argument Types"));

		layout = new GroupLayout(argTypesArea);
		argTypesArea.setLayout(layout);

		layout.setAutoCreateGaps(true);;
		layout.setAutoCreateContainerGaps(true);

		
		Vector<JLabel>	vctArgTypeLabels = new Vector<JLabel>();
		for (int i = 0; i < NUM_OF_ARG_TYPE_SELECTORS; i++) {
			JLabel l = new JLabel("argument #" + i);
			vctArgTypeLabels.add(l);
		}
		
		Vector<JPanel> vctArgTypePanels = new Vector<JPanel>();
		vctCheckBoxes = new Vector<Vector<JCheckBox>>();
		for (int i = 0; i < NUM_OF_ARG_TYPE_SELECTORS; i++) {
			JCheckBox cbInt 	= new JCheckBox("int");
			JCheckBox cbFloat 	= new JCheckBox("float");
			JCheckBox cbSymbol 	= new JCheckBox("symbol");
			JCheckBox cbString 	= new JCheckBox("string");
			Vector<JCheckBox> v = new Vector<JCheckBox>();
			v.add(cbInt);
			v.add(cbFloat);
			v.add(cbSymbol);
			v.add(cbString);
			
			cbInt	.addActionListener(this);
			cbFloat	.addActionListener(this);
			cbSymbol.addActionListener(this);
			cbString.addActionListener(this);
			
			vctCheckBoxes.add(v);
			
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout());
			
			p.add(cbInt);
			p.add(cbFloat);
			p.add(cbSymbol);
			p.add(cbString);
			
			vctArgTypePanels.add(p);
		}

		left = layout.createParallelGroup();
		right = layout.createParallelGroup();
		
		for (JLabel l: vctArgTypeLabels) {
			left.addComponent(l);
		}

		for (JPanel p: vctArgTypePanels) {
			right.addComponent(p);
		}
		
		sg = layout.createSequentialGroup();
		sg.addGroup(left);
		sg.addGroup(right);
		
		layout.setHorizontalGroup(sg);

		
		sg2 = layout.createSequentialGroup();
		for (int i = 0; i < vctArgTypeLabels.size(); i++) {
			JLabel l = vctArgTypeLabels.get(i);
			JPanel p = vctArgTypePanels.get(i);
			ParallelGroup r = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
			r.addComponent(l);
			r.addComponent(p);
			sg2.addGroup(r);
		}

		layout.setVerticalGroup(sg2);		

		//------------------------------------------------------------------------------
		//								symbols
		//------------------------------------------------------------------------------		
		lbSymbols = new JLabel("symbols (comma-separated string values)");
		tfSymbols = new JTextField();
		tfSymbols.setFont(GUIFont.getSingleton().getObjectBoxFont());
		tfSymbols.setColumns(45);
		tfSymbols.enableInputMethods(false);

		JPanel symbolArea = new JPanel();
		symbolArea.setBorder(BorderFactory.createTitledBorder("symbols"));
		symbolArea.setLayout(new GridLayout(2, 1));
//		symbolArea.add(lbSymbols, BorderLayout.NORTH);	
//		symbolArea.add(tfSymbols, BorderLayout.CENTER);
		symbolArea.add(lbSymbols);	
		symbolArea.add(tfSymbols);
		
		
		//------------------------------------------------------------------------------
		//								button area
		//------------------------------------------------------------------------------		
		JPanel buttonArea = new JPanel();
		buttonArea.setLayout(new FlowLayout(FlowLayout.RIGHT));

		btnOkay 	= new JButton("OK");
		btnCancel	= new JButton("Cancel");
		buttonArea.add(btnOkay);
		buttonArea.add(btnCancel);
		
		//------------------------------------------------------------------------------
		//							create the panel
		//------------------------------------------------------------------------------		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		
		JPanel rightPanelInside = new JPanel();
		rightPanelInside.setLayout(new BorderLayout());
		rightPanelInside.add(argTypesArea	, BorderLayout.NORTH);
		rightPanelInside.add(symbolArea		, BorderLayout.SOUTH);
		
		rightPanel.add(rightPanelInside	, BorderLayout.NORTH);
		rightPanel.add(buttonArea		, BorderLayout.SOUTH);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(objectInfoArea,BorderLayout.NORTH);
		
		panel.add(leftPanel		, BorderLayout.WEST);
		panel.add(rightPanel	, BorderLayout.CENTER);		

		JScrollPane scrollPane = new  JScrollPane(
				panel,								
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,	
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		
		btnOkay		.addActionListener(actionListener);
		btnCancel	.addActionListener(actionListener);
		
		tfClassName .getDocument().addDocumentListener(this);
		tfSymbols	.getDocument().addDocumentListener(this);
		cbMinArgNum	.addActionListener(this);
		cbMaxArgNum	.addActionListener(this);
		
		this.btnOkay.setEnabled(false);
		
		
		return;
	}


	/**
	 * @param tf
	 * @return
	 */
	public boolean checkCustomObjectClassName()
	{
		String name = tfClassName.getText().trim();

		return ARNameVerifier.isValidObjectName(name);
	}
	

	/**
	 * 
	 */
	public String[] getSymbols()
	{
		String text = tfSymbols.getText();
		String[] symbols = text.split(",");
		if (symbols == null || symbols.length == 0) {
			return null;
		}
		if (symbols.length == 1 && symbols[0].equals("")) {
			return null;
		}
		for (int i = 0; i < symbols.length; i++) {
			symbols[i] = symbols[i].trim();
		}
		return symbols;
	}
	/**
	 * @return
	 */
	public boolean checkSymbols()
	{
		String text = tfSymbols.getText().trim();
		
		String[] symbols = text.split(",");
		if (symbols == null || symbols.length == 0) {
			return true;
		}
		
		if (symbols.length == 1 && symbols[0].equals("")) {
			return true;
		}
		
		for (String s: symbols) {
			s = s.trim();
			if (!ARNameVerifier.isValidSymbol(s)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return
	 */
	public boolean checkArgNum()
	{
		if (cbMaxArgNum.getSelectedIndex() < cbMinArgNum.getSelectedIndex()) {
			cbMaxArgNum.setForeground(Color.RED);
			cbMinArgNum.setForeground(Color.RED);
			return false;
		}
		
		cbMaxArgNum.setForeground(Color.BLACK);
		cbMinArgNum.setForeground(Color.BLACK);
		
		return true;
	}
	/**
	 * @return
	 */
	public boolean checkArgTypes()
	{
		boolean invalid = false;
		for (int i = 0; i < cbMaxArgNum.getSelectedIndex(); i++) {
			Vector<JCheckBox> checkBoxes = vctCheckBoxes.get(i);
			boolean checked = false;
			for (JCheckBox cb: checkBoxes) {
				if (cb.isSelected()) {
					checked = true;
				}
				cb.setEnabled(true);
			}
			Color cbColor = Color.BLACK;
			if (checked == false) {
				cbColor = Color.red;
				invalid = true;
			}
			for (JCheckBox cb: checkBoxes) {
				cb.setForeground(cbColor);
			}
		}
		
		for (int i = cbMaxArgNum.getSelectedIndex(); i < vctCheckBoxes.size(); i++) {
			Vector<JCheckBox> checkBoxes = vctCheckBoxes.get(i);
			for (JCheckBox cb: checkBoxes) {		
				cb.setForeground(Color.BLACK);
				cb.setEnabled(false);
			}
		}
		
		return !invalid;
	}
	

		
	/**
	 * 
	 */
	public boolean checkValidity()
	{
		boolean validTextFields = this.checkTextFields();
		boolean validArgTypes	= this.checkArgTypes();
		boolean validArgNum		= this.checkArgNum();
		
		boolean valid = validTextFields & validArgTypes & validArgNum;
		this.btnOkay.setEnabled(valid);
		
		return valid;
	}

	

	/**
	 * 
	 */
	public boolean checkTextFields()
	{
		this.tfSymbols.setForeground(Color.BLACK);
		this.tfClassName.setForeground(Color.BLACK);

		boolean validSymbols = this.checkSymbols();
		if (validSymbols == false) {
			this.tfSymbols.setForeground(Color.RED);
		}
		
		boolean validClassName = this.checkCustomObjectClassName();
		
		if (validClassName == false) {
			this.tfClassName.setForeground(Color.RED);
		}
		
		if (validSymbols == false || validClassName == false) {
			return false;
		}

		
		String className = tfClassName.getText().trim();
		
		String headerFilename = ARClassInfo.getDefaultHeaderFilename(className);
		tfHeaderFilename.setText(headerFilename);

		String cppFilename = ARClassInfo.getDefaultCppFilename(className);
		tfCppFilename.setText(cppFilename);

		String initFuncName = ARClassInfo.getDefaultInitFuncName(className);
		tfInitFuncName.setText(initFuncName);
		
		String triggerFuncName	= ARClassInfo.getDefaultTriggerFuncName(className);
		tfTriggerFuncName.setText(triggerFuncName);
		
		String structName = ARClassInfo.getDefaultStructName(className);
		tfStructName.setText(structName);
		
		
		return true;
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.checkValidity();
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		this.checkValidity();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		this.checkValidity();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		this.checkValidity();
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean isOkayButton(Object o)
	{
		return o == this.btnOkay;
	}


	/**
	 * @param o
	 * @return
	 */
	public boolean isCancelButton(Object o)
	{
		return o == this.btnCancel;
	}
	
	
	
	/**
	 * @param info
	 */
	public void createNewUserDefinedObjectDirectory(ARClassInfo info) throws IOException
	{
		File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
		if (projectDirectory == null) {
			JOptionPane.showMessageDialog(this.getRootPane(), "No project is active yet. Open a project first, please.", "No active project", JOptionPane.WARNING_MESSAGE);
			return;
		}

		File userDefinedObjectDirectory = new File(projectDirectory, info.getARClassName());
		
		
		if (userDefinedObjectDirectory.exists()) {
			int response = JOptionPane.showConfirmDialog(this.getRootPane(), 
					"The directory with the same name already exists.\n Do you want to overwrite existing files?", 
					"The directory already exists", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (response != JOptionPane.OK_OPTION) {
				return;
			}
		}
		else {
			userDefinedObjectDirectory.mkdir();
		}

		ARClassInfo.createAudFile	(userDefinedObjectDirectory, info);
		this.createHeaderStabFile	(userDefinedObjectDirectory, info);
		this.createCppStabFile		(userDefinedObjectDirectory, info);
		this.createHelpStabFile		(userDefinedObjectDirectory, info);
		return;
	}
	
	/**
	 * @param parent
	 * @param info
	 * @throws IOException
	 */
	public void updateUserDefinedObjectDefinitionFile(File audFile, ARClassInfoNative oldInfo, ARClassInfoNative newInfo) throws IOException
	{
		File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
		if (projectDirectory == null) {
			JOptionPane.showMessageDialog(this.getRootPane(), "No project is active yet. Open a project first, please.", "No active project", JOptionPane.WARNING_MESSAGE);
			return;
		}

		File userDefinedObjectDirectory = audFile.getParentFile();
		
		if (!projectDirectory.equals(userDefinedObjectDirectory.getParentFile())) {
			JOptionPane.showMessageDialog(this.getRootPane(), "The aud file doesn't exist in the current project directory.", "File error", JOptionPane.WARNING_MESSAGE);
			return;			
		}
		
		if (!oldInfo.getARClassName().equals(newInfo.getARClassName())) {
			int response = JOptionPane.showConfirmDialog(this.getRootPane(), 
					"The object name has been changed.\n Do you really want replace rename all the files?", 
					"The object name has been changed", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.OK_OPTION) {
				this.updateUserDefinedObjectDefinitionFileWithDifferentName(audFile, oldInfo, newInfo);
			}
			return;
		}
		
		//no change to the object name, just overwrite the current aud file.
		ARClassInfo.createAudFile(userDefinedObjectDirectory, newInfo);
		
		return;
	}
	
	
	/**
	 * @param parent
	 * @param audFile
	 * @param oldInfo
	 * @param newInfo
	 * @throws IOException
	 */
	private void updateUserDefinedObjectDefinitionFileWithDifferentName(File audFile, ARClassInfoNative oldInfo, ARClassInfoNative newInfo) throws IOException
	{		
		File projectDirectory = ProjectSetting.getSingleton().getProjectDirectory();
		
		File oldUserDefinedObjectDirectory = audFile.getParentFile();
		
		if (!projectDirectory.equals(oldUserDefinedObjectDirectory.getParentFile())) {
			JOptionPane.showMessageDialog(this.getRootPane(), "The aud file doesn't exist in the current project directory.", "File error", JOptionPane.WARNING_MESSAGE);
			return;			
		}

		File newUserDefinedObjectDirectory = new File(projectDirectory, newInfo.getARClassName());
		if (newUserDefinedObjectDirectory.exists()) {
			JOptionPane.showMessageDialog(this.getRootPane(), "The directory:" + newInfo.getARClassName() + " already exists in the current project directory.", "File error", JOptionPane.WARNING_MESSAGE);
			return;			
		}

		//create a new directory and files.
		newUserDefinedObjectDirectory.mkdir();
		ARClassInfo.createAudFile(newUserDefinedObjectDirectory, newInfo);
		ARClassInfo.copyCppAndHeaderFiles(oldUserDefinedObjectDirectory, newUserDefinedObjectDirectory, oldInfo, newInfo);
		ARClassInfo.copyHelpFile(oldUserDefinedObjectDirectory, newUserDefinedObjectDirectory, oldInfo.getARClassName(), newInfo.getARClassName());
		
		//delete old ones.
		File tmp = new File(oldUserDefinedObjectDirectory, oldInfo.getARClassName() + ARFileConst.ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT);
		if (tmp.exists()) {
			tmp.delete();
		}
		tmp = new File(oldUserDefinedObjectDirectory, oldInfo.getCppFilenameWithoutPath());
		if (tmp.exists()) {
			tmp.delete();
		}
		tmp = new File(oldUserDefinedObjectDirectory, oldInfo.getHeaderFilenameWithoutPath());
		if (tmp.exists()) {
			tmp.delete();
		}
		
		tmp = new File(oldUserDefinedObjectDirectory, oldInfo.getARClassName() + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT);
		if (tmp.exists()){
			tmp.delete();
		}
		
		if (oldUserDefinedObjectDirectory.exists()) {
			oldUserDefinedObjectDirectory.delete();
		}
		
		
		return;
	}
	
	
	

	
	

	
	

	

	/**
	 * @param directory
	 * @param info
	 */
	public void createHeaderStabFile(File directory, ARClassInfo info) throws IOException
	{
		File newHeaderFile = new File(directory, info.getHeaderFilenameWithoutPath());
		FileWriter fw = new FileWriter(newHeaderFile);
		PrintWriter pw = new PrintWriter(fw);
		
		pw.println("//");
		pw.println("// " + info.getHeaderFilenameWithoutPath());
		pw.println("//");
		pw.println("// generated by the Ardestan programming language : " + new Date().toString());
		pw.println("//");
		pw.println();
		
		String defString = "__arobj_" + info.getARClassName() + "__h__";
		pw.println("#ifndef " + defString);
		pw.println("#define " + defString);
		
		pw.println("//----------------------------------------------------------------------------");
		pw.println("// type");
		pw.println("//----------------------------------------------------------------------------");
		pw.println("");
		pw.println("typedef struct {");
		pw.println("    //write your code here.");
		pw.println("} " + info.getStructName() + ";");
		pw.println("");
		pw.println("//----------------------------------------------------------------------------");
		pw.println("// declaration");
		pw.println("//----------------------------------------------------------------------------");
		pw.println("");
		pw.println("void " + info.getFuncNameInit() + "(");
		pw.println("    ARObject*        self      ,");
		pw.println("    void*            __fields__,");
		pw.println("    uint_fast8_t     argc      ,");
		pw.println("    ARMessageType*   argt      ,");
		pw.println("    ARValue*         argv");
		pw.println(");");
		pw.println("");
		pw.println("void " + info.getFuncNameTrigger() + "(");
		pw.println("    ARObject*        self      ,");
		pw.println("    int32_t          inlet_no  ,");
		pw.println("    void*            __fields__");
		pw.println(");");
		
		pw.println("#endif");
		pw.close();
		fw.close();
		
		return;
	}
	
	/**
	 * @param directory
	 * @param info
	 * @throws IOException
	 */
	public void createHelpStabFile(File directory, ARClassInfo info) throws IOException
	{
		File newHelpFile = new File(directory,  info.getARClassName() + ARFileConst.ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT);
		
		JsonARProgram program = new JsonARProgram();
		program.instances 	= new Vector<JsonARInstanceInfo>();
		program.connections = new Vector<ARConnectionInfo>();
		program.comments 	= new Vector<JsonCommentBox>();
		


		CommentBox cbTitle = new CommentBox();
		cbTitle.setColor(Color.BLACK);
		cbTitle.setX(1);
		cbTitle.setY(1);
		cbTitle.setFontName("Monospaced");
		cbTitle.setFontSize(30);
		cbTitle.setComment(info.getARClassName());


		CommentBox cbAbstract = new CommentBox();
		cbAbstract.setColor(Color.BLACK);
		cbAbstract.setX(3);
		cbAbstract.setY(40);
		cbAbstract.setFontName("Monospaced");
		cbAbstract.setFontSize(12);

		cbAbstract.setComment("This is a stab help file for the user-defined object: " + info.getARClassName());

		
		Vector<CommentBox> commentBoxes = new Vector<CommentBox>();
		commentBoxes.add(cbTitle);		
		commentBoxes.add(cbAbstract);		
		 
		JsonProgramWriter writer = new JsonProgramWriter();
		writer.save(newHelpFile, new Vector<ObjectBox>(), new Vector<ObjectBoxConnection>(), commentBoxes);
	
		return;
	}
	/**
	 * @param directory
	 * @param info
	 * @throws IOException
	 */
	public void createCppStabFile(File directory, ARClassInfo info) throws IOException
	{
		File newCppFilename = new File(directory, info.getCppFilenameWithoutPath());
		FileWriter fw = new FileWriter(newCppFilename);
		PrintWriter pw = new PrintWriter(fw);
		
		pw.println("//");
		pw.println("// " + info.getCppFilenameWithoutPath());
		pw.println("//");
		pw.println("// generated by the Ardestan programming language : " + new Date().toString());
		pw.println("//");
		pw.println();
		pw.println("#include <stdio.h>");
		pw.println("#include <stdint.h>");
		pw.println("#include \"ArdestanIDs.h\"");
		pw.println("#include \"Ardestan.h\"");
		pw.println();
		pw.println("#include \""+ info.getHeaderFilenameWithoutPath() + "\"");
		pw.println();
		pw.println("//----------------------------------------------------------------------------");
		pw.println("// implementation");
		pw.println("//----------------------------------------------------------------------------");
		pw.println("");
		pw.println("void " + info.getFuncNameInit() + "(");
		pw.println("    ARObject*        self      ,");
		pw.println("    void*            __fields__,");
		pw.println("    uint_fast8_t     argc      ,");
		pw.println("    ARMessageType*   argt      ,");
		pw.println("    ARValue*         argv");
		pw.println(")");
		pw.println("{");
		pw.println("    " + info.getStructName() + "* fields = (" + info.getStructName() + "*)__fields__;");
		pw.println();
		pw.println("    //write your code here.");
		pw.println("}");
		pw.println("");
		pw.println("void " + info.getFuncNameTrigger() + "(");
		pw.println("    ARObject*        self      ,");
		pw.println("    int32_t          inlet_no  ,");
		pw.println("    void*            __fields__");
		pw.println(")");
		pw.println("{");
		pw.println("    " + info.getStructName() + "* fields = (" + info.getStructName() + "*)__fields__;");
		pw.println();
		pw.println("    //write your code here.");
		pw.println("}");

		pw.close();
		fw.close();
	}

}
