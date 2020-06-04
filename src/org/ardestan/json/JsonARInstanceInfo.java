package org.ardestan.json;

import java.util.Vector;

public class JsonARInstanceInfo 
{
	public String 	instanceName;
	public String 	className;
	public int		x;
	public int		y;
	public int		w;
	public int		h;
	public int 		numOfInlets;
	public int		numOfOutlets;

	public Vector<JsonARInstanceInfoArg> args;
	
	public boolean	valid;
	public String	boxText;
}
