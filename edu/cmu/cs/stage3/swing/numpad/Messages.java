package edu.cmu.cs.stage3.swing.numpad;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;

public class Messages {
	private static final String BUNDLE_NAME = "edu.cmu.cs.stage3.lang." + AikMin.locale + ".others_" + AikMin.locale; 

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
