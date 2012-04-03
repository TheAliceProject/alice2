/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

public class StoreElementProgressPane extends edu.cmu.cs.stage3.progress.ProgressPane {
	private edu.cmu.cs.stage3.alice.core.Element m_element;
	private java.io.File m_file;
	private java.util.Dictionary m_filnameToByteArrayMap;
	private boolean m_wasSuccessful = false;
	public StoreElementProgressPane( String title, String preDescription ) {
		super( title, preDescription );
	}
	
	public boolean wasSuccessful() {
		return m_wasSuccessful;
	}
	
	protected void construct() throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		m_wasSuccessful = false;
		try {
			m_element.store( m_file, this, m_filnameToByteArrayMap );
			m_wasSuccessful = true;
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw pce;
		} catch( Throwable t ) {
			StringBuffer sb = new StringBuffer();
			sb.append( Messages.getString("StoreElementProgressPane.0") ); 
			sb.append( Messages.getString("StoreElementProgressPane.1") ); 
			if( t instanceof java.io.IOException ) {
				sb.append( Messages.getString("StoreElementProgressPane.2") );			 
				sb.append( Messages.getString("StoreElementProgressPane.3") ); 
				sb.append( Messages.getString("StoreElementProgressPane.4") ); 
			} else {
				sb.append( Messages.getString("StoreElementProgressPane.5") ); 
			}
			sb.append( Messages.getString("StoreElementProgressPane.6") ); 

			sb.append( Messages.getString("StoreElementProgressPane.7") ); 
			sb.append( Messages.getString("StoreElementProgressPane.8") ); 
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( sb.toString(), t );
		}
	}
	public void setElement( edu.cmu.cs.stage3.alice.core.Element element ) {
		m_element = element;
	}
	public void setFile( java.io.File file ) {
		m_file = file;
	}
	public void setFilnameToByteArrayMap( java.util.Dictionary filnameToByteArrayMap ) {
		m_filnameToByteArrayMap = filnameToByteArrayMap;
	}
}
