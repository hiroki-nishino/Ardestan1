package org.ardestan.generator;

/**
 * @author hiroki
 *
 */
public class ArgType {

	
	public static String INLET_OBJECT_CLASS_NAME = "inlet";
	public static String OUTLET_OBJECT_CLASS_NAME = "outlet";
	
	public static String BINARY_OPERATOR_OBJECT_CLASS_NAME = "bop";

	
	public static final String	Str_INVALID = "INVALID";
	public static final String	Str_UNTERMINATED_STRING_LITERAL = "UNTERMINATED_STRING_LITERAL";
	
	public static final String 	Str_INT 		= "INT";
	public static final String 	Str_FLOAT 		= "FLOAT";
	public static final String 	Str_SYM_ID 		= "SYM_ID";
	public static final String 	Str_STRING 		= "STRING";
	public static final String	Str_Parameter 	= "Parameter";
	
	public static final int 	INVALID_UNTERMINATED_STRING_LITERAL = - 2;
	public static final int 	INVALID		= -1;
	public static final	int		INT			= 0;
	public static final int		FLOAT		= 1;
	public static final int		SYM_ID		= 2;
	public static final int		STRING		= 3;
	public static final int		PARAMETER	= 4;
	
	
	/**
	 * @param argumentValue
	 * @return
	 */
	public static int getArgumentType(String argumentValue)
	{
		//arguments are already parsed and validated. 
		//so we just do something simple here.
		if(argumentValue.startsWith("\"")) {
			if (isStringArgument(argumentValue)) {
				return ArgType.STRING; 				
			}
			return ArgType.INVALID_UNTERMINATED_STRING_LITERAL;
		}
		
		if (isIntArgument(argumentValue)) {
			return ArgType.INT;
		}
		
		if (isFloatArgument(argumentValue)) {
			return ArgType.FLOAT;
		}
		
		if (isSymbolArgument(argumentValue)) {
			return ArgType.SYM_ID;
		}
		
		if (isParameterArgument(argumentValue)) {
			return ArgType.PARAMETER;
		}
		
		return ArgType.INVALID;
		
//		//check if any characters other than '0-9' and '.' is included.
//		boolean dotIncluded = false;
//		
//		for (int i = 0; i < argumentValue.length(); i++) {
//			Character c = argumentValue.charAt(i);
//			if (c.equals('-') && i != 0) {
//				return ArgType.INVALID;
//			}
//			if (!Character.isDigit(c)) {
//				if (c == '.') {
//					dotIncluded = true;
//					continue;
//				}
//				else {
//					return ArgType.SYM_ID;
//				}
//			}
//		}
//		
//		return dotIncluded ? ArgType.FLOAT : ArgType.INT;	
	}
	
	/**
	 * @param argumentValue
	 * @return
	 */
	public static boolean isStringArgument(String argumentValue)
	{
		if (argumentValue.charAt(0) != '\"') {
			return false;
		}
		
		if (argumentValue.length() < 2) {
			return false;
		}
		
		for (int i = 1; i < argumentValue.length() - 1; i++) {
			char c = argumentValue.charAt(i);
			if (c != '\\'){
					continue;
			}
			
			i++;
			if (i == argumentValue.length() - 1) {
				return false;
			}
			if (c != '\\' || c != '\"') {
				return false;
			}
		}
		
		if (argumentValue.charAt(argumentValue.length() - 1) != '\"') {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param argumentValue
	 * @return
	 */
	public static boolean isParameterArgument(String argumentValue)
	{
		if (argumentValue.charAt(0) != '$') {
			return false;
		}
		
		if (argumentValue.length() < 2) {
			return false;
		}
		
		for (int i = 1; i < argumentValue.length(); i++) {
			char c = argumentValue.charAt(i);
			if (Character.isDigit(c) == false) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @param argumentValue
	 * @return
	 */
	public static boolean isIntArgument(String argumentValue)
	{
		int i = 0;
		if (argumentValue.charAt(0) == '-') {
			i++;
		}
		
		boolean numFound = false;

		for (	; i <argumentValue.length(); i++) {
			char c = argumentValue.charAt(i);
			if (Character.isDigit(c) == false) {
				return false;
			}
			numFound = true;
		}
		
		return numFound;
	}
	
	
	/**
	 * @param argumentValue
	 * @return
	 */
	public static boolean isFloatArgument(String argumentValue)
	{
		
		int i = 0;
		if (argumentValue.charAt(0) == '-') {
			i++;
		}
		
		
		boolean dotFound = false;
		boolean numFound = false;
		
		for (	; i <argumentValue.length(); i++) {
			char c = argumentValue.charAt(i);
			if ( c == '.') {
				if (dotFound == true) {
					return false;
				}
				dotFound = true;
				continue;
			}
			if (Character.isDigit(c) == false) {
				return false;
			}
			numFound = true;
		}
		
		return numFound;
	}
	
	/**
	 * @param argumentValue
	 * @return
	 */
	public static boolean isSymbolArgument(String argumentValue) 
	{
		for (int i = 0; i < argumentValue.length(); i++) {
			char c = argumentValue.charAt(i);
			if (Character.isAlphabetic(c) == false &&
				Character.isDigit(c) == false &&
				c != '_') {
				return false;
			}
		}
		
		return true;
	}
	/**
	 * @param argumentValue
	 * @return
	 */
	public static String getArgumentTypeString(String argumentValue)
	{
		//arguments are already parsed and validated. 
		//so we just do something simple here.
		if(argumentValue.startsWith("\"")) {
			return ArgType.Str_STRING; 
		}
		
		if (isIntArgument(argumentValue)) {
			return ArgType.Str_INT;
		}
		
		if (isFloatArgument(argumentValue)) {
			return ArgType.Str_FLOAT;
		}
		
		if (isSymbolArgument(argumentValue)) {
			return ArgType.Str_SYM_ID;
		}
		
		if (isParameterArgument(argumentValue)){
			return ArgType.Str_Parameter;
		}
		
		return ArgType.Str_INVALID;
	}
	
	/**
	 * @param s
	 */
	public static String convertToSymID(String s)
	{
		s = s.trim();
		if ("==".equalsIgnoreCase(s)) {
			return "ID_EQ";
		}
		else if ("!=".equalsIgnoreCase(s)) {
			return "ID_NEQ";
		}
		else if ("<".equalsIgnoreCase(s)) {
			return "ID_LT";
		}
		else if ("<=".equalsIgnoreCase(s)) {
			return "ID_LTEQ";
		}
		else if (">".equalsIgnoreCase(s)) {
			return "ID_GT";
		}
		else if (">=".equalsIgnoreCase(s)) {
			return "ID_GTEQ";
		}
		else if ("&&".equalsIgnoreCase(s)) {
			return "ID_LAND";
		}
		else if ("||".equalsIgnoreCase(s)) {
			return "ID_LOR";
		}
		
		return "ID_" + s.toUpperCase();	
	}

	/**
	 * @param index
	 * @param argType
	 * @param argValue
	 * @return
	 */
	public static String convertToARValueAssignment(int index, String argType, String argValue)
	{
		String ret = "argValues[" + index + "].";
		switch(stringToEnum(argType))
		{
		case INT:
			ret += "i = " + argValue;
			break;
		case FLOAT:
			ret += "f = " + argValue;
			break;
		case SYM_ID:
			ret += "sym_id = " + convertToSymID(argValue);
			break;
		case STRING:
			ret += "str = p_arstr";
			break;
		}
		ret += ";";
		return ret;
	}
	/**
	 * @param s
	 * @return
	 */
	public static final int	stringToEnum(String s)
	{
		if (s.equals(Str_INT)){
			return INT;
		}
		if (s.equals(Str_FLOAT)){
			return FLOAT;
		}
		if (s.equals(Str_SYM_ID)){
			return SYM_ID;
		}
		//anything else will be treated as string.
		return STRING;
	}
}
