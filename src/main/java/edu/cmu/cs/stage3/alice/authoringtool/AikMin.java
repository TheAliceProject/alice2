package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.Font;
import java.util.Locale;

import javax.swing.UIManager;

import edu.cmu.cs.stage3.lang.Messages;
	
public class AikMin {
	public static Locale locale = new Locale ("en"); //es
	public static int decimal = 2;
	public static String defaultLanguage = locale.getDisplayLanguage();
	public static String[] listOfLanguages = {"Arabic", "English", "German", "Portuguese","Spanish"}; 
	public static int target = 0;	// Compile with 1 to delete preferences file or create etc/firstRun.txt
	
	//.applyComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
	//.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
	//javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	//Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) )
	//System.getProperty("os.name") != null) && System.getProperty("os.name").startsWith("Windows")  
	//edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource( "images/record.png" )
	//System.getProperty( "file.separator" )
	//new String(item.getBytes("utf-8"), "utf-8"); Windows-1256 ISO-8859-1 
	/*javax.swing.Timer focusTimer = new javax.swing.Timer(100,
			new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					renderCanvas.requestFocus();
				}
			});
	focusTimer.setRepeats(false);
	focusTimer.start();*/
	
	public static Locale getLocale(){
		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( JAlice.class.getPackage() );
		if( authoringtoolConfig.getValue( "language" ) == null ) { 
			authoringtoolConfig.setValue( "language", AikMin.defaultLanguage );
		} else {
			String aliceLanguage = authoringtoolConfig.getValue( "language" );
			if (aliceLanguage.equalsIgnoreCase("English")) {
				locale = new Locale ("en");
			} else if (aliceLanguage.equalsIgnoreCase("Portuguese")) {
				locale = new Locale ("pt");
			} else if (aliceLanguage.equalsIgnoreCase("Spanish")) {
				locale = new Locale ("es");
			} else if (aliceLanguage.equalsIgnoreCase("German")) {
				locale = new Locale ("de");
			} else if (aliceLanguage.equalsIgnoreCase("Arabic")) {
				locale = new Locale ("ar");
			}
		}		
		return locale;
	}
	
	public static boolean isLTR(){
		return !locale.getDisplayName().equalsIgnoreCase("Arabic"); 
		//return true;
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().startsWith("win"); 
	}
	
	public static boolean isMAC() {
		return System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().startsWith("mac"); 
	}
	
	public static boolean isUnix() {
		return System.getProperty("os.name") != null && (
				System.getProperty("os.name").toLowerCase().indexOf("nix") >= 0 ||
				System.getProperty("os.name").toLowerCase().indexOf("nux") >= 0 ||
				System.getProperty("os.name").toLowerCase().indexOf("aix") >= 0 ); 
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
//				"\u0021", 	"\\u0022", 	"\u0023", 	"\u0025", 	"\u0026", 	"\u0027",	"\u0028",	"\u0029", 	
//				"\\u002A", 	"\u002B", 	"\u002C", 	"\\u002D", 	"\u002E", 	"\u002F",
//				"\u003A", 	"\u003B", 	"\u003C", 	"\u003D", 	"\u003E", 	"\u003F", 	"\u0040",
//				"\u005B", 	"\\u005C", 	"\u005D", 	"\u005E", 	"\u0060", 
//				"\u007B", 	"\\u007C", 	"\u007D", 	"\u007E", 	"\u007F"
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
				"\t", "\n", "\\", "/", ":", "*", "?", "\"", "<", ">", "|", "."
		};	
		for (int i=0; i<invalidCharacters.length; i++){
			if (name.contains(invalidCharacters[i])) {
				return false; 
			}
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
	
	public static void setUI(){
		String font = "SansSerif"; // "Tahoma"; 
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());//"javax.swing.plaf.metal.MetalLookAndFeel"); 
			
			//	javax.swing.UIManager.put( "Button.focus", new java.awt.Color( 255, 255, 255, 0 ) ); // don't show focus  // makes printing slow, unfortunately

			class CustomButtonBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
				protected java.awt.Insets insets = new java.awt.Insets(3, 3, 3, 3);
				protected javax.swing.border.Border line = javax.swing.BorderFactory.createLineBorder(java.awt.Color.black, 1);
				protected javax.swing.border.Border spacer = javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 4);
				protected javax.swing.border.Border raisedBevel = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED);
				protected javax.swing.border.Border loweredBevel = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED);
				protected javax.swing.border.Border raisedBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(this.line, this.raisedBevel), this.spacer);
				protected javax.swing.border.Border loweredBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(this.line, this.loweredBevel), this.spacer);
				//				protected javax.swing.border.Border raisedBorder = javax.swing.BorderFactory.createCompoundBorder( raisedBevel, spacer );
				//				protected javax.swing.border.Border loweredBorder = javax.swing.BorderFactory.createCompoundBorder( loweredBevel, spacer );

				public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int w, int h) {
					javax.swing.JButton button = (javax.swing.JButton) c;
					javax.swing.ButtonModel model = button.getModel();

					if (model.isEnabled()) {
						if (model.isPressed() && model.isArmed()) {
							this.loweredBorder.paintBorder(button, g, x, y, w, h);
						} else {
							this.raisedBorder.paintBorder(button, g, x, y, w, h);
						}
					} else {
						this.raisedBorder.paintBorder(button, g, x, y, w, h);
					}
				}

				public java.awt.Insets getBorderInsets(java.awt.Component c) {
					return this.insets;
				}
			}
			javax.swing.UIManager.put("Button.border", new javax.swing.plaf.BorderUIResource.CompoundBorderUIResource(new CustomButtonBorder(), new javax.swing.plaf.basic.BasicBorders.MarginBorder())); 
			
			edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( JAlice.class.getPackage() );
			setFontSize(Integer.parseInt( authoringToolConfig.getValue( "fontSize" )));		 
			if (authoringToolConfig.getValue( "enableHighContrastMode" ).equalsIgnoreCase("true")){  
				javax.swing.UIManager.put("Label.foreground", java.awt.Color.black); 
			} else {
				javax.swing.UIManager.put("Label.foreground", edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("mainFontColor"));  				
			}
			javax.swing.UIManager.put("Label.font", new java.awt.Font(font, java.awt.Font.BOLD, 12)); 
			
			// Customize tab panel
			javax.swing.UIManager.put("TabbedPane.tabInsets", new java.awt.Insets(1, 4, 1, 3)); 
			javax.swing.UIManager.put("TabbedPane.contentBorderInsets", new java.awt.Insets(2, 1, 1, 1)); 
			javax.swing.UIManager.put("TabbedPane.borderHightlightColor", java.awt.Color.black); 
			javax.swing.UIManager.put("TabbedPane.focus", new java.awt.Color(255, 255, 255, 0)); 
			javax.swing.UIManager.put("TabbedPane.contentAreaColor", new java.awt.Color(255, 255, 255, 0)); 
			javax.swing.UIManager.put("TabbedPane.light", java.awt.Color.white); 
			javax.swing.UIManager.put("TabbedPane.selected", java.awt.Color.white); 
			javax.swing.UIManager.put("TabbedPane.darkShadow", java.awt.Color.black); 
			javax.swing.UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.TRUE); 
			javax.swing.UIManager.put("TabbedPane.selectHighlight", javax.swing.UIManager.get("TabbedPane.selected")); 
			
			//Customize OptionPane buttons
			UIManager.put("OptionPane.cancelButtonText", Messages.getString("Cancel"));
		    UIManager.put("OptionPane.okButtonText", Messages.getString("OK"));
		    UIManager.put("OptionPane.yesButtonText", Messages.getString("Yes"));
		    UIManager.put("OptionPane.noButtonText", Messages.getString("No"));
		    
			//Customize buttons
			javax.swing.UIManager.put("Button.select", new java.awt.Color(255, 255, 255, 0)); 
			javax.swing.UIManager.put("Button.focus", new java.awt.Color(255, 255, 255, 0)); 
			
			//Customize slider
			//javax.swing.UIManager.put("SliderUI", "javax.swing.plaf.metal.MetalSliderUI"); 
			
			javax.swing.UIManager.put("ComboBoxUI", "javax.swing.plaf.metal.MetalComboBoxUI");  
			if ( AikMin.isWindows() ) {   
				javax.swing.UIManager.put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI"); // "com.sun.java.swing.plaf.windows.WindowsFileChooserUI");  
			} 
			if ( AikMin.isMAC() ) { 
				//javax.swing.UIManager.put("ScrollBarUI", "apple.laf.AquaScrollBarUI"); 
				//javax.swing.UIManager.put("SliderUI", "apple.laf.AquaSliderUI"); 
			}
		} catch (Exception e) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_configuring_Look_and_Feel_"), e); 
		}

	}
}
