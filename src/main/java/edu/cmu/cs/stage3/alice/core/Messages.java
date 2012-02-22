package edu.cmu.cs.stage3.alice.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;

public class Messages {
	private static final String BUNDLE_NAME = "edu.cmu.cs.stage3.lang." + AikMin.locale + ".core_" + AikMin.locale; //$NON-NLS-1$"; //$NON-NLS-1$

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
