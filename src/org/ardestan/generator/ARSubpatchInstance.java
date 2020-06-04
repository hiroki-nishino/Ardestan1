package org.ardestan.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.ardestan.arclass.ARClassInfo;
import org.ardestan.arclass.ARClassInfoSubpatch;
import org.ardestan.gui.visual.CommentBox;
import org.ardestan.gui.visual.ObjectBox;
import org.ardestan.gui.visual.ObjectBoxConnection;

/**
 * @author hiroki
 *
 */
public class ARSubpatchInstance 
{
	protected Vector<String>				arguments;
	protected Vector<ObjectBox> 			objectBoxes;
	protected Vector<ObjectBoxConnection>	objectBoxConnections;
	protected Vector<CommentBox>			commentBoxes;
	
	protected Map<Integer, Vector<ObjectBox>> inlets;
	protected Map<Integer, Vector<ObjectBox>> outlets;
	
	/**
	 * @param objectBoxes
	 * @param objectBoxConnections
	 */
	public ARSubpatchInstance(Vector<ObjectBox> objectBoxes, Vector<ObjectBoxConnection> objectBoxConnections, Vector<CommentBox> commentBoxes)
	{
		this.arguments				= new Vector<String>();
		this.objectBoxes 			= objectBoxes;
		this.objectBoxConnections 	= objectBoxConnections;
		this.commentBoxes			= commentBoxes;
		
		inlets = new HashMap<Integer, Vector<ObjectBox>>();
		outlets= new HashMap<Integer, Vector<ObjectBox>>();
		
		for (ObjectBox b: objectBoxes) {
			ARClassInfo i = b.getARClassInfo();
			if (i == null) {
				continue;
			}
			else if (i.getARClassName().equals(ArgType.INLET_OBJECT_CLASS_NAME)) {
				this.addInlet(b);
			}
			else if (i.getARClassName().equals(ArgType.OUTLET_OBJECT_CLASS_NAME)) {
				this.addOutlet(b);
			}
		}
		
		return;
	}
	
	/**
	 * @param arguments
	 */
	public void setArguments(Vector<String> arguments)
	{
		this.arguments = arguments;
	}
	
	
	/**
	 * @return
	 */
	public Vector<ObjectBox> getObjectBoxes()
	{
		return this.objectBoxes;
	}
	
	/**
	 * @return
	 */
	public Vector<ObjectBoxConnection> getObjectBoxConnections()
	{
		return this.objectBoxConnections;
	}
	
	/**
	 * @return
	 */
	public Vector<CommentBox> getCommentBoxes(){
		return this.commentBoxes;
	}
	
	/**
	 * @param value
	 * @return
	 */
	public int getPatchArgIndex(String value)
	{
		if (!value.startsWith("$")) {
			return -1;
		}
		
		String number = value.substring(1);
		try {
			return Integer.parseInt(number);
		}
		catch(NumberFormatException e) {
			return -1;
		}
	}
	
	/**
	 * 
	 */
	public void applyArgumentsToObjects()
	{
		for (ObjectBox box: objectBoxes) {
			Vector<String> args = box.getArguments();
			
			for (int i = 0; i < args.size(); i++) {
				String arg = args.get(i);
				
				int patchArgIndex = getPatchArgIndex(arg);
				
				//if this is not the '$0', '$1', '$2', ...., just neglect.
				if (patchArgIndex < 0) {
					continue;
				}
				//if the patch doesn't have enough arguments, just set 0.
				if (patchArgIndex >= this.arguments.size()) {
					args.set(i, "0");
					continue;
				}
				args.set(i, this.arguments.get(patchArgIndex));
			}
		}
	}
	
	/**
	 * 
	 */
	public void expandSubpatches() throws ARPatchExpansionFailedException
	{
		//first instantiate all the subpatches in this patch. remove the old subpatch object at the same time.
		Map<ObjectBox, ARSubpatchInstance>	subpatchToInstance = new HashMap<ObjectBox, ARSubpatchInstance>();
		
		Iterator<ObjectBox> i1 = this.objectBoxes.iterator();
		while(i1.hasNext()) {
			ObjectBox b = i1.next();
			//neglect invalid objects
			if (b.isValid() == false) {
				continue;
			}
			ARClassInfo i = b.getARClassInfo();
			if (!i.isSubpatch()) {
				continue;
			}
			
			//instantiate subpatches.
			ARClassInfoSubpatch s = (ARClassInfoSubpatch)i;
			try {
				ARSubpatchInstance instance = s.instantiate();
				instance.setArguments(b.getArguments());
				instance.applyArgumentsToObjects();
				instance.expandSubpatches();
				subpatchToInstance.put(b, instance);
			} catch (IOException e) {
				e.printStackTrace();
				String subpatchName = s.getARClassName();
				throw new ARPatchExpansionFailedException("The instantiation of the subpatch: " + subpatchName + " failed due to the file I/O error.", subpatchName);
			}
			
			//remove the object.
			i1.remove();
		}
		
		//there was no subpatch inside to expand.
		if (subpatchToInstance.size() == 0) {
			return;
		}
		
		
		//replace the connections to/from subpatches.
		Iterator<ObjectBoxConnection> i2 = this.objectBoxConnections.iterator();
		
		Vector<ObjectBoxConnection> newConnections = new Vector<ObjectBoxConnection>();
		while(i2.hasNext()) {
			ObjectBoxConnection c = i2.next();
		
			//the source is a subpatch. we need to replace the reference.
			if (subpatchToInstance.containsKey(c.src)){
				ARSubpatchInstance srcInstance = subpatchToInstance.get(c.src);
				Vector<ObjectBox> outlets = srcInstance.getOutlets(c.outletNo);
				
				//if the destination is a subpatch.
				if (!subpatchToInstance.containsKey(c.dst)) {
					//if not, simply create new connection and add.
					for (ObjectBox outlet: outlets) {
						ObjectBoxConnection nc = new ObjectBoxConnection();
						nc.src = outlet;
						nc.outletNo = 0;
						nc.dst = c.dst;
						nc.inletNo = c.inletNo;
						newConnections.add(nc);
					}
				}
				//if the destination is a sub patch.
				else {
					ARSubpatchInstance dstInstance = subpatchToInstance.get(c.dst);
					Vector<ObjectBox> inlets = dstInstance.getInlets(c.inletNo);
					for (ObjectBox outlet: outlets) {
						for (ObjectBox inlet: inlets) {
							ObjectBoxConnection nc = new ObjectBoxConnection();
							nc.src = outlet;
							nc.outletNo = 0;
							nc.dst = inlet;
							nc.inletNo = 0;
							newConnections.add(nc);
						}
					}
				}
				
				//remove this connection.
				i2.remove();
			}
			//if the source is not a subpatch but the destination is a subpatch.
			else if (subpatchToInstance.containsKey(c.dst)) {
				ARSubpatchInstance dstInstance = subpatchToInstance.get(c.dst);
				Vector<ObjectBox> inlets = dstInstance.getInlets(c.inletNo);
				for (ObjectBox inlet: inlets) {
					ObjectBoxConnection nc = new ObjectBoxConnection();
					nc.src = c.src;
					nc.outletNo = c.outletNo;
					nc.dst = inlet;
					nc.inletNo = 0;
					newConnections.add(nc);
				}
				//remove this connection.
				i2.remove();
			}
		}

		//now add new connections we just built.
		this.objectBoxConnections.addAll(newConnections);
		
		//then add all the objects and the connections in the subpatches.
		for (ARSubpatchInstance subpatch: subpatchToInstance.values()) {
			this.objectBoxes.addAll(subpatch.getObjectBoxes());
			this.objectBoxConnections.addAll(subpatch.getObjectBoxConnections());
		}
		
		return;
	}
	
	
	
	
	/**
	 * @param no
	 * @return
	 */
	public Vector<ObjectBox> getInlets(int no)
	{
		if (!inlets.containsKey(no)) {
			return null;
		}
		
		return inlets.get(no);
	}
	
	/**
	 * @param no
	 * @return
	 */
	public Vector<ObjectBox> getOutlets(int no)
	{
		if (!outlets.containsKey(no)) {
			return null;
		}
		
		return outlets.get(no);
	}
	/**
	 * @param box
	 */
	public void addInlet(ObjectBox box)
	{
		Vector<String> args = box.getArguments();
		if (args.size() < 1) {
			return;
		}
		
		String arg = args.get(0);
		
		if (ArgType.getArgumentType(arg) != ArgType.INT) {
			return;
		}
		
		int inletNo = Integer.parseInt(arg);
		
		if (!inlets.containsKey(inletNo)) {
			Vector<ObjectBox> boxes = new Vector<ObjectBox>();
			inlets.put(inletNo, boxes);
		}
		
		Vector<ObjectBox> boxes = inlets.get(inletNo);
		boxes.add(box);
		
		return;
	}
	
	/**
	 * @param box
	 */
	public void addOutlet(ObjectBox box)
	{
		Vector<String> args = box.getArguments();
		if (args.size() < 1) {
			return;
		}
		
		String arg = args.get(0);
		
		if (ArgType.getArgumentType(arg) != ArgType.INT) {
			return;
		}
		
		int outletNo = Integer.parseInt(arg);
		
		if (!outlets.containsKey(outletNo)) {
			Vector<ObjectBox> boxes = new Vector<ObjectBox>();
			outlets.put(outletNo, boxes);
		}
		
		Vector<ObjectBox> boxes = outlets.get(outletNo);
		boxes.add(box);
		
		return;	
	}
	
}
