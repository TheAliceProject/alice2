package edu.cmu.cs.stage3.lang;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;

public class Messages {
	public static String getString(String key) {
		try {	
			String t = (String)ResourceBundle.getBundle("edu.cmu.cs.stage3.lang.messages", AikMin.locale).getObject(key);
			t = t.replace("''", "'");
			return t; //already converted from UTF-8 properties
		} catch (MissingResourceException e) {
			return key.replace("_", " ");
		} catch (Exception ee){
			return "";
		}
	}
	
	public static String getString (String key, Object... parameters) {
		java.text.MessageFormat format = new java.text.MessageFormat("");
		format.applyPattern(getString(key));
		String output = format.format(parameters);
		return output;
		//return (String)ResourceBundle.getBundle("edu.cmu.cs.stage3.lang."+AikMin.locale).getString(key);
	}
	

}
