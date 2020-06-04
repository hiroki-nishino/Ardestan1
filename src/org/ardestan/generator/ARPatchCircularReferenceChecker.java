package org.ardestan.generator;

import java.util.LinkedList;
import java.util.Set;

import org.ardestan.arclass.ARClassDatabase;
import org.ardestan.arclass.ARClassInfoSubpatch;

/**
 * @author hnishino
 *
 */
public class ARPatchCircularReferenceChecker 
{	
	
	
	/**
	 * @param rootPatch
	 * @param checkingHelpFile
	 * @throws ARPatchCircularReferenceException
	 */
	public static void check(ARClassInfoSubpatch rootPatch, boolean checkingHelpFile) throws ARPatchCircularReferenceException
	{
		String rootPatchName = rootPatch.getARClassName();
		
		LinkedList<String> path = new LinkedList<String>();
		//we don't want to include the name of the root patch when it is a help file.
		if (checkingHelpFile == false) {
			path.add(rootPatchName);
		}
		check(rootPatch, path);
		
		return;
	}
	
	/**
	 * @param patch
	 * @param path
	 */
	public static void check(ARClassInfoSubpatch patch, LinkedList<String> path) throws ARPatchCircularReferenceException
	{
		Set<String> subpatchNames = patch.getContainedSubpatchNames();
		
		//check if any of current subpatch causes a circular reference.
		//(we don't want to traverse path when we already found a circular references).
		for (String subpatchName: subpatchNames) {
			if (path.contains(subpatchName)) {
				throw new ARPatchCircularReferenceException(patch.getARClassName(), subpatchName, path);
			}
		}
		
		//traverse the subpatch paths.
		for (String subpatchName: subpatchNames) {
			path.addLast(subpatchName);
			ARClassInfoSubpatch subpatch = (ARClassInfoSubpatch)ARClassDatabase.getSingleton().getARClassInfo(subpatchName);
			check(subpatch, path);
			path.removeLast();
		}
		
		return;
	}
}
