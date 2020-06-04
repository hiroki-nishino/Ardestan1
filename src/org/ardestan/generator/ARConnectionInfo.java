package org.ardestan.generator;

/**
 * @author hnishino
 *
 */
public class ARConnectionInfo {

	protected String src;
	protected int	 outletNo;
	protected String dest;
	protected int	 inletNo;
	
	/**
	 * @param src				the source object name
	 * @param outletNo			the outlet no.
	 * @param dest				the destination object name
	 * @param inletNo			the inlet no.
	 */
	public ARConnectionInfo(String src, int outletNo, String dest, int inletNo)
	{
		this.src = src;
		this.outletNo = outletNo;
		this.dest = dest;
		this.inletNo = inletNo;
	}

	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @return the outletNo
	 */
	public int getOutletNo() {
		return outletNo;
	}


	/**
	 * @return the dest
	 */
	public String getDest() {
		return dest;
	}

	/**
	 * @return the inletNo
	 */
	public int getInletNo() {
		return inletNo;
	}
	
	
}
