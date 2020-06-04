package org.ardestan.generator;

import java.util.LinkedList;
import java.util.List;

/**
 * @author hiroki
 *
 */
public class ARPatchCircularReferenceException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String patchName;
	private String subpatchName;
	
	private LinkedList<String> circularReferencePath;
	
	public ARPatchCircularReferenceException(String patchName, String subpatchName, List<String> circularReferencePath) {
		this.patchName 		= patchName;
		this.subpatchName 	= subpatchName;
		this.circularReferencePath = new LinkedList<String>(circularReferencePath);
	}

	/**
	 * @return
	 */
	public String getPatchName() {
		return patchName;
	}

	/**
	 * @return
	 */
	public String getSubpatchName() {
		return subpatchName;
	}

	/**
	 * @return
	 */
	public LinkedList<String> getCircularReferencePath() {
		return circularReferencePath;
	}
	
	
}
