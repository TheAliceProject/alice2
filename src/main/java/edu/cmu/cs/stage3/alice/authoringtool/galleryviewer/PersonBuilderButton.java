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

package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.lang.Messages;

public class PersonBuilderButton extends GenericBuilderButton {
	private edu.cmu.cs.stage3.caitlin.personbuilder.PersonBuilder m_personBuilder = null;
	protected String getToolTipString() {
		return Messages.getString("Click_to_create_your_own_custom_character"); 
	}

	public void respondToMouse() {
		if (mainViewer != null && PersonBuilderButton.this.isEnabled()) {
			if (m_personBuilder == null) {
				edu.cmu.cs.stage3.progress.ProgressPane progressPane = new edu.cmu.cs.stage3.progress.ProgressPane( Messages.getString("Progress"), Messages.getString("Loading_Character_Builder___") ) {  
					
					protected void construct() throws edu.cmu.cs.stage3.progress.ProgressCancelException {
						m_personBuilder = new edu.cmu.cs.stage3.caitlin.personbuilder.PersonBuilder( data.name, this );
					}
				};
				edu.cmu.cs.stage3.swing.DialogManager.showDialog( progressPane );
			} else {
				m_personBuilder.reset();
			}
			if( m_personBuilder != null ) {
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog( m_personBuilder, Messages.getString("Person_Builder"), javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE ); 
				if( result == javax.swing.JOptionPane.OK_OPTION ) {
					 mainViewer.addObject( m_personBuilder.getModel() );
				}
			}
		}
	}
}
