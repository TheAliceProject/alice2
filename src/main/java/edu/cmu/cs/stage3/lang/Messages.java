package edu.cmu.cs.stage3.lang;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;

public class Messages {
	static {				
		if (AikMin.locale.compareToIgnoreCase("") == 0) {		 	
			edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( JAlice.class.getPackage() );
			if( authoringtoolConfig.getValue( "language" ) == null ) { 
				authoringtoolConfig.setValue( "language", "English" );
				AikMin.locale = "English";				
			} else {
				AikMin.locale = authoringtoolConfig.getValue( "language" );
			}
			//Locale mexico = new Locale("es","MX");
			//Locale spain = new Locale("es","ES");
		}
	}
	
	public static String getString(String key) {
		try {	
			String t = (String)ResourceBundle.getBundle("edu.cmu.cs.stage3.lang."+AikMin.locale).getObject(key);
			if (AikMin.locale == "Arabic"){
				return t = new String ( t.getBytes("ISO-8859-1"), "UTF-8" );
			}
			return t;
			//return (String)ResourceBundle.getBundle("edu.cmu.cs.stage3.lang."+AikMin.locale).getObject(key);
		} catch (MissingResourceException e) {
			return key.replace("_", " ");
		} catch (Exception ee){
			return "";
		}
	}
	
	public static String getString (String key, Object... parameters) {
		String value = getString(key);
		value = java.text.MessageFormat.format(value, parameters);
		return value;
		//return (String)ResourceBundle.getBundle("edu.cmu.cs.stage3.lang."+AikMin.locale).getString(key);
	}
	
}
