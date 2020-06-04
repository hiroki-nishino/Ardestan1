package org.ardestan.generator;


/**
 * @author hiroki
 *
 */
public class ARPatchExpansionFailedException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String patchName;
	
	
	public ARPatchExpansionFailedException(String message, String patchName) {
		super(message);
		this.patchName 		= patchName;
	}

	/**
	 * @return
	 */
	public String getPatchName() {
		return patchName;
	}

}
