package edu.cmu.cs.stage3.lang;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;

public class Messages {
	static {				
		//if (AikMin.locale.compareToIgnoreCase("") == 0) {
			edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( JAlice.class.getPackage() );
			if( authoringtoolConfig.getValue( "language" ) == null ) { 
				authoringtoolConfig.setValue( "language", "English" );  
			} 
			//Locale mexico = new Locale("es","MX");
			//Locale spain = new Locale("es","ES");
			if (authoringtoolConfig.getValue( "language" ).compareToIgnoreCase("spanish")==0){
				AikMin.locale = "es";
				Locale.setDefault(new Locale("es","MX"));
			} else {
				AikMin.locale = "en";
				Locale.setDefault(new Locale("en","US"));
			}
		//}
	}
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
