package edu.cmu.cs.stage3.lang;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;

public class Messages {
	public static String getString(String key) {
		try {			
			String temp;
			temp = (String)ResourceBundle.getBundle("edu.cmu.cs.stage3.lang."+AikMin.locale).getObject(key);
			int i = 0;
			while (	key.charAt(i) == '_'){
				i++;
			}
			if ( i != 0 && key.charAt(i+1) != temp.charAt(i+1) ){
				temp = " "+temp;
			}
			return temp;
		} catch (MissingResourceException e) {
			e.printStackTrace();
			return '!' + key + '!';
		}
	}
}
