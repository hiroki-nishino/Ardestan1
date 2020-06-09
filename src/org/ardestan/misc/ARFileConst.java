package org.ardestan.misc;

public interface ARFileConst {

	
	public static final String ARDUINO_INO_FILE_EXTENSION_WITH_DOT 		= ".ino";
	
	public static final String ARDESTAN_PROJECT_FILE_EXTENSION_WITHOUT_DOT = "arp";
	public static final String ARDESTAN_PROJECT_FILE_EXTENSION_WITH_DOT = "." + ARDESTAN_PROJECT_FILE_EXTENSION_WITHOUT_DOT;
	
	public static final String ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITHOUT_DOT 	= "ard";
	public static final String ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITH_DOT 	= "." + ARDESTAN_VISUAL_PROGRAM_FILE_EXTENSION_WITHOUT_DOT;
	
	
	
	
	public static final String ARDESTAN_USER_DEFINED_OBJECT_DEF_FILE_EXTENSTION_WITH_DOT = ".aud";
	
	public static final String ARDESTAN_USER_DEFINED_OBJECT_IMPLEMENTAION_PREFIX_WITHOUT_UNDERSCORE = "arobj";
	public static final String ARDESTAN_USER_DEFINED_OBJECT_IMPLEMENTAION_PREFIX = ARDESTAN_USER_DEFINED_OBJECT_IMPLEMENTAION_PREFIX_WITHOUT_UNDERSCORE + "_";
	public static final String ARDESTAN_CPP_FILE_EXTENSION_WITH_DOT 	= ".cpp";
	public static final String ARDESTAN_HEADER_FILE_EXTENSION_WITH_DOT 	= ".h";
	public static final String ARDESTAN_HPP_FILE_EXTENSION_WITH_DOT 	= ".hpp";
	public static final String ARDESTAN_HELP_FILE_EXTENSION_WITH_DOT 	= ".arh";
	
	public static final String IDE_PROPERTIES_FILE_NAME	= ".ardestan_ide.properties";
	public static final String IDE_PROPERTIES_RECENT_PROJECT_FILENAME_PREFIX = "recent_project_file_";

	public static final String DEFAULT_CLASSINFO_FILE_RESOURCE_NAME 		= "resources/classinfo/DefaultClassDatabase.json";
	
	public static final String DEFAULT_FRAMEWORK_HEADER_FILE_RESOURCE_NAME 	= "resources/arduino/ardestan/Ardestan.h";
	public static final String DEFAULT_FRAMEWORK_CPP_FILE_RESOURCE_NAME 	= "resources/arduino/ardestan/Ardestan.cpp";
	
	public static final String DEFAULT_OBJECT_HEADER_RESOURCE_PATH 			= "resources/arduino/object/header";
	public static final String DEFAULT_OBJECT_CPP_RESOURCE_PATH 			= "resources/arduino/object/cpp";
	public static final String DEFAULT_OBJECT_HELP_REOUCES_PATH				= "resources/help/";
	

	public static final String DEFAULT_INIT_FUNC_PREFIX 	= "init_func_";
	public static final String DEFAULT_TRIGGER_FUNC_PREFIX 	= "trigger_func_";
	public static final String AROBJ_STRUCTNAME_PREFIX		= "ARObj";
	
	public static final String ARDUINO_CLI_RESOURCE_NAME_MAC 		= "resources/arduino/cli/mac/arduino-cli";
	public static final String ARDUINO_CLI_RESOURCE_NAME_WIN64 		= "resources/arduino/cli/win64/arduino-cli.exe";
	public static final String ARDUINO_CLI_RESOURCE_NAME_WIN32 		= "resources/arduino/cli/win32/arduino-cli.exe";
	public static final String ARDUINO_CLI_RESOURCE_NAME_LINUX64	= "resources/arduino/cli/linux64/arduino-cli";
	public static final String ARDUINO_CLI_RESOURCE_NAME_LINUX32	= "resources/arduino/cli/linux32/arduino-cli";
	public static final String ARDUINO_CLI_RESOURCE_NAME_LINUX64ARM	= "resources/arduino/cli/linux64arm/arduino-cli";
	public static final String ARDUINO_CLI_RESOURCE_NAME_LINUX32ARM = "resources/arduino/cli/linux32arm/arduino-cli";
	public static final String ARDUINO_CLI_COMMAND_NAME_UNIX		= "arduino-cli";
	public static final String ARDUINO_CLI_COMMAND_NAME_WIN			= "arduino-cli.exe";
	
	
	public static final String DIALOG_SIZE_RESOURCE_NAME_MAC	= "resources/dialog/mac/dialog_setting.json";
	public static final String DIALOG_SIZE_RESOURCE_NAME_WIN64	= "resources/dialog/win64/dialog_setting.json";
	public static final String DIALOG_SIZE_RESOURCE_NAME_WIN32	= "resources/dialog/win32/dialog_setting.json";
	public static final String DIALOG_SIZE_RESOURCE_NAME_LINUX  = "resources/dialog/linux/dialog_setting.json";

	
	public static final String ID_ARDUINO_AVR_PLATFORMS			= "arduino:avr";
	public static final String FIRST_EXEC 	= "first_exec";
	public static final String BASE64_ENCODED_ADDITIONAL_URLS = "base64_encoded_additional_urls";
	public static final String BOARD_NAME 	= "board_name";
	public static final String PORT_NAME  	= "port_name";
	public static final String FQBN			= "fqbn";
	
	public static final String DEFAULT_CLOSE_ICON = "resources/image/close.png";
	public static final String DEFAULT_DOCK_ICON  = "resources/image/ziku.png";
	
	public static final String ListAllObjectsHelpObjectName = "list all objects";
}
