package org.ardestan.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hiroki
 *
 */
public class InstanceNameGenerator
{

	protected	Map<String, Integer> 			instanceSeqIDs	;

	
	/**
	 * 
	 */
	public InstanceNameGenerator()
	{
		this.reset();
	}
	
	public void reset()
	{
		instanceSeqIDs = new HashMap<String, Integer>();
	}
	
	/**
	 * @param className
	 * @return
	 */
	public String generateInstanceName(String className)
	{
		if (instanceSeqIDs.containsKey(className)) {
			int seqID = instanceSeqIDs.get(className) + 1;
			instanceSeqIDs.put(className, seqID);
			return className + "_" + seqID;
		}
		
		instanceSeqIDs.put(className, 0);
		return className + "_" + 0;
	}
}
