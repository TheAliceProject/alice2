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

package edu.cmu.cs.stage3.alice.authoringtool.editors.scripteditor;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * @author Jason Pratt
 */
public class ScriptEditor extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.Editor {
	public String editorName = Messages.getString("Script_Editor"); 

	protected edu.cmu.cs.stage3.alice.core.property.ScriptProperty scriptProperty;
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ScriptEditorPane scriptEditorPane = new edu.cmu.cs.stage3.alice.authoringtool.util.ScriptEditorPane();
	protected javax.swing.event.CaretListener caretListener = new javax.swing.event.CaretListener() {
		public void caretUpdate( javax.swing.event.CaretEvent e ) {
			ScriptEditor.this.updateLineNumber();
		}
	};
	protected javax.swing.event.DocumentListener documentListener = new javax.swing.event.DocumentListener() {
		//TODO: more efficient updating; this is going to be really costly when the script is large...
		public void changedUpdate( javax.swing.event.DocumentEvent e ) { scriptProperty.set( scriptEditorPane.getText() ); }
		public void insertUpdate( javax.swing.event.DocumentEvent e ) { scriptProperty.set( scriptEditorPane.getText() ); }
		public void removeUpdate( javax.swing.event.DocumentEvent e ) { scriptProperty.set( scriptEditorPane.getText() ); }
	};

	public ScriptEditor() {
		jbInit();
		guiInit();
	}

	private void guiInit() {
		scriptScrollPane.setViewportView( scriptEditorPane );
		scriptEditorPane.addCaretListener( caretListener );
		scriptEditorPane.performAllAction.setEnabled( false );
		scriptEditorPane.performSelectedAction.setEnabled( false );
	}

	public javax.swing.JComponent getJComponent() {
		return this;
	}

	public Object getObject() {
		return scriptProperty;
	}

	public void setObject( edu.cmu.cs.stage3.alice.core.property.ScriptProperty scriptProperty ) {
		scriptEditorPane.getDocument().removeDocumentListener( documentListener );
		this.scriptProperty = scriptProperty;

		if( this.scriptProperty != null ) {
			if( scriptProperty.getStringValue() == null ) {
				scriptProperty.set( "" ); 
			}
			scriptEditorPane.setText( scriptProperty.getStringValue() );

			scriptEditorPane.getDocument().addDocumentListener( documentListener );

			scriptEditorPane.resetUndoManager();
			scriptEditorPane.setSandbox( scriptProperty.getOwner().getSandbox() );
		} else {
			scriptEditorPane.resetUndoManager();
			scriptEditorPane.setSandbox( null );
		}
	}

	public void setAuthoringTool( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
	}

	public void updateLineNumber() {
		//TODO: better formatting
		this.lineNumberLabel.setText( Messages.getString("__line_number__") + 