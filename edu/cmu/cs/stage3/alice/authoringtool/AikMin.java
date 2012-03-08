package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.Font;
import javax.swing.UIManager;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
	
public class AikMin {
	public static String locale = "en";
	public static int target = 0;	// Compile with 1 for alice applet
	public static String version ="02/01/2012";
	//.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
	//javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	//Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) )
	//System.getProperty("os.name") != null) && System.getProperty("os.name").startsWith("Windows")  
	//edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource( "images/record.png" )
	//System.getProperty( "file.separator" )
	
	public static String getString(String key) {
		try {
			if (locale.compareToIgnoreCase("en") == 0 ) {
				return key;
			} else {
				return ResourceBundle.getBundle("edu.cmu.cs.stage3.alice.authoringtool.strings_" + locale).getString(key.replace(" ", ""));
			}
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	public static String getProperty(String key) {
		try {
			if (locale.compareToIgnoreCase("en") == 0 ) {
				return key;
			} else {
				return ResourceBundle.getBundle("edu.cmu.cs.stage3.alice.authoringtool.properties_" + locale).getString(key.replace(" ", ""));
			}
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	public static boolean isValidName(String name){
		if( name == null ) {
			return false;
		}
		if( name.length() == 0  ) {
			return false;
		}
		if( name.trim().length() != name.length() ) {
			return false;
		}
//		String [] javaKeywords = { 
//				"abstract", "default", 	"if", 			"private", 	"this",
//				"boolean",  "do",		"implements", 	"protected","throw",
//				"break", 	"double",  	"import",  		"public", 	"throws",
//				"byte", 	"else", 	"instanceof", 	"return", 	"transient",
//				"case",  	"extends", 	"int",  		"short",  	"try",
//				"catch", 	"final",	"interface",  	"static", 	"void",
//				"char",  	"finally", 	"long", 		"strictfp", "volatile",
//				"class", 	"float",	"native",  		"super",  	"while",
//				"const", 	"for",  	"new",  		"switch",
//				"continue", "goto", 	"package", 		"synchronized" };  
//		for (int i=0; i<javaKeywords.length; i++){
//			if (name.equalsIgnoreCase(javaKeywords[i])) return false;
//		}
		CharSequence [] invalidCharacters = {
				"\u0000",	"\u0001", 	"\u0002", 	"\u0003", 	"\u0004", 	"\u0005", 	"\u0006", 	"\u0007", 
				"\u0008",	"\u0009", 	"\\u000A", 	"\u000B", 	"\u000C", 	"\\u000D", 	"\u000E", 	"\u000F",
				"\u0010", 	"\u0011", 	"\u0012", 	"\u0013", 	"\u0014", 	"\u0015", 	"\u0016", 	"\u0017", 
				"\u0018", 	"\u0019", 	"\\u001A", 	"\u001B", 	"\u001C", 	"\\u001D", 	"\u001E", 	"\u001F",
				"\u0021", 	"\\u0022", 	"\u0023", 	"\u0025", 	"\u0026", 	"\u0027", 
				"\u0028", 	"\u0029", 	"\\u002A", 	"\u002B", 	"\u002C", 	"\\u002D", 	"\u002E", 	"\u002F",
				"\u003A", 	"\u003B", 	"\u003C", 	"\u003D", 	"\u003E", 	"\u003F", 	"\u0040",
				"\u005B", 	"\\u005C", 	"\u005D", 	"\u005E", 	"\u0060", 
				"\u007B", 	"\\u007C", 	"\u007D", 	"\u007E", 	"\u007F"
//				"\u00A9", 	"\\u00AA",	"\u00AB", 	"\u00AC", 	"\u00AD", 	"\u00AE", 	"\u00AF",
//				"\u00A0", 	"\u00A1", 	"\\u00A2", 	"\u00A3", 	"\u00A4", 	"\u00A5", 	"\u00A6", 	"\u00A7", 
//				"\u00A8", 	"\u00A9", 	"\\u00AA", 	"\u00AB", 	"\u00AC", 	"\\u00AD", 	"\u00AE", 	"\u00AF",
//				"\u00B0", 	"\u00B1", 	"\\u00B2", 	"\u00B3", 	"\u00B4", 	"\u00B5", 	"\u00B6", 	"\u00B7", 
//				"\u00B8", 	"\u00B9", 	"\\u00BA", 	"\u00BB", 	"\u00BC", 	"\\u00BD", 	"\u00BE", 	"\u00BF",
//				"\u00C0", 	"\u00C1", 	"\\u00C2", 	"\u00C3", 	"\u00C4", 	"\u00C5", 	"\u00C6", 	"\u00C7", 
//				"\u00C8", 	"\u00C9", 	"\\u00CA", 	"\u00CB", 	"\u00CC", 	"\\u00CD", 	"\u00CE", 	"\u00CF",
//				"\u00D0", 	"\u00D1", 	"\\u00D2", 	"\u00D3", 	"\u00D4", 	"\u00D5", 	"\u00D6", 	"\u00D7", 
//				"\u00D8", 	"\u00D9", 	"\\u00DA", 	"\u00DB", 	"\u00DC", 	"\\u00DD", 	"\u00DE", 	"\u00DF",
//				"\u00E0", 	"\u00E1", 	"\\u00E2", 	"\u00E3", 	"\u00E4", 	"\u00E5", 	"\u00E6", 	"\u00E7", 
//				"\u00E8", 	"\u00E9", 	"\\u00EA", 	"\u00EB", 	"\u00EC", 	"\\u00ED", 	"\u00EE", 	"\u00EF",
//				"\u00F0", 	"\u00F1", 	"\\u00F2", 	"\u00F3", 	"\u00F4", 	"\u00F5", 	"\u00F6", 	"\u00F7", 
//				"\u00F8", 	"\u00F9", 	"\\u00FA", 	"\u00FB", 	"\u00FC", 	"\\u00FD", 	"\u00FE", 	"\u00FF"
		};	
		for (int i=0; i<invalidCharacters.length; i++){
			if (name.contains(invalidCharacters[i])) return false; 
		}
		return true;
	}
	
	public static void setFontSize(int fontSize){
		Font fontType = UIManager.getFont("Menu.font");
	    String name = fontType.getName();
	    int style = fontType.getStyle();
		Font font = new Font( name, style, fontSize);
		setUI(font);
	}
	
	private static void setUI(Font font){
		UIManager.put("Button.font",  font);
		UIManager.put("CheckBox.font",  font);
		UIManager.put("CheckBoxMenuItem.acceleratorFont",  font);
		UIManager.put("CheckBoxMenuItem.font",  font);
		UIManager.put("ComboBox.font",  font);
		UIManager.put("DesktopIcon.font",  font);
		UIManager.put("EditorPane.font",  font);
		UIManager.put("FormattedTextField.font",  font);
		UIManager.put("InternalFrame.titleFont",  font);
		UIManager.put("Label.font", font);
		UIManager.put("List.font",  font);
		UIManager.put("Menu.acceleratorFont",  font);
		UIManager.put("Menu.font",  font);
		UIManager.put("MenuBar.font",  font);
		UIManager.put("MenuItem.acceleratorFont",  font);
		UIManager.put("MenuItem.font",  font);
		UIManager.put("OptionPane.buttonFont", font);
		UIManager.put("OptionPane.messageFont",  font);
		UIManager.put("PasswordField.font", font);
		UIManager.put("PopupMenu.font",  font);
		UIManager.put("ProgressBar.font",  font);
		UIManager.put("RadioButton.font",  font);
		UIManager.put("RadioButtonMenuItem.acceleratorFont",  font);
		UIManager.put("RadioButtonMenuItem.font",  font);
		UIManager.put("Spinner.font",  font);
		UIManager.put("TabbedPane.font", font);
		UIManager.put("Table.font", font);
		UIManager.put("TableHeader.font",  font);
		UIManager.put("TextArea.font", font);
		UIManager.put("TextField.font", font);
		UIManager.put("TextPane.font",  font);
		UIManager.put("TitledBorder.font",  font);
		//UIManager.put("ToggleButton.font",  new Font());
		UIManager.put("ToolBar.font",  font);
		UIManager.put("ToolTip.font",  font);
		UIManager.put("Tree.font",  font);
		UIManager.put("Viewport.font", font);
		UIManager.put("JTitledPanel.title.font", font);
	}
}
