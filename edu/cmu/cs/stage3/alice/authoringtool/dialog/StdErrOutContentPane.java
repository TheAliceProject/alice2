/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * yes
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

import java.awt.*;

/**
 * @author Jason Pratt, David Culyba
 */

public class StdErrOutContentPane extends edu.cmu.cs.stage3.alice.authoringtool.dialog.AliceAlertContentPane {
	public final static int HISTORY_MODE = 2;
	
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected OutputComponent errOutputComponent;
	protected OutputComponent stdOutputComponent;
	
	protected String lastError;
	protected String titleString;
	
	protected boolean isShowing = false;
	protected boolean errorContentAdded = false;
	protected boolean textContentAdded = false;
	protected boolean shouldListenToErrors = true;
	protected boolean shouldListenToPrint = true;
	
	protected class ErrOutputDocumentListener implements javax.swing.event.DocumentListener{
		public void insertUpdate( final javax.swing.event.DocumentEvent ev ) {
			try{	
				lastError = ev.getDocument().getText(ev.getOffset(), ev.getLength());
				if (lastError.startsWith(Messages.getString("StdErrOutContentPane.0")) == true){ //$NON-NLS-1$
					lastError = Messages.getString("StdErrOutContentPane.1")+ //$NON-NLS-1$
						Messages.getString("StdErrOutContentPane.2")+ //$NON-NLS-1$
						Messages.getString("StdErrOutContentPane.3") + //$NON-NLS-1$
						Messages.getString("StdErrOutContentPane.4") + lastError; //$NON-NLS-1$
				}
				detailTextPane.getDocument().insertString(detailTextPane.getDocument().getLength(), lastError, detailTextPane.stdErrStyle);
			}catch (Exception e){}
			errorContentAdded = true;
			update();
		}
		public void removeUpdate( javax.swing.event.DocumentEvent ev ) { update(); }
		public void changedUpdate( javax.swing.event.DocumentEvent ev ) { update(); }

		private void update() {
			if (shouldListenToErrors){
				javax.swing.SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						if( !isShowing) {
							isShowing = true;
							int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog( StdErrOutContentPane.this );
						} 
					}
				});
			}

		}
	}
	
	protected class StdOutputDocumentListener implements javax.swing.event.DocumentListener{
		public void insertUpdate( final javax.swing.event.DocumentEvent ev ) {
			try{
				lastError = ev.getDocument().getText(ev.getOffset(), ev.getLength());
				detailTextPane.getDocument().insertString(detailTextPane.getDocument().getLength(), lastError, detailTextPane.stdOutStyle);
			}catch (Exception e){}
			textContentAdded = true;
			update(); 
		}
		public void removeUpdate( javax.swing.event.DocumentEvent ev ) { update(); }
		public void changedUpdate( javax.swing.event.DocumentEvent ev ) { update(); }

		private void update() {
			if (shouldListenToPrint){
				javax.swing.SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						if( !isShowing ) {
							isShowing = true;
							int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog( StdErrOutContentPane.this );
						} 
					}
				});
			}

		}
	}

	public StdErrOutContentPane( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
		super();
		titleString =  Messages.getString("StdErrOutContentPane.5"); //$NON-NLS-1$
		this.authoringTool = authoringTool;
		this.errOutputComponent = authoringTool.getStdErrOutputComponent();
		this.stdOutputComponent = authoringTool.getStdOutOutputComponent();
		writeGenericAliceHeaderToTextPane();
		this.errOutputComponent.getTextPane().getDocument().addDocumentListener(new ErrOutputDocumentListener());
		this.stdOutputComponent.getTextPane().getDocument().addDocumentListener(new StdOutputDocumentListener());
	}
	
	protected void writeGenericAliceHeaderToTextPane() {
		detailTextPane.setText( "" ); //$NON-NLS-1$
		detailStream.println( Messages.getString("StdErrOutContentPane.7") + edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion() ); //$NON-NLS-1$
		//String[] systemProperties = { "os.name", "os.version", "os.arch", "java.vm.name", "java.vm.version", "user.dir" };
		//for( int i = 0; i < systemProperties.length; i++ ) {
		//	detailStream.println( systemProperties[i] + ": " + System.getProperty( systemProperties[i] ) );
		//}
		detailStream.println();
	}

	
	
	public void preDialogShow(javax.swing.JDialog parentDialog) {
		super.preDialogShow(parentDialog);
	}

	public void stopReactingToPrint(){
		shouldListenToPrint = false;
	}
	
	public void startReactingToPrint(){
		stdOutputComponent.stdErrStream.flush();
		stdOutputComponent.stdOutStream.flush();
		shouldListenToPrint = true;
	}
	
	public void stopReactingToError(){
		shouldListenToErrors = false;
	}

	public void startReactingToError(){
		errOutputComponent.stdErrStream.flush();
		errOutputComponent.stdOutStream.flush();
		shouldListenToErrors = true;
	}
	
	
	public void postDialogShow(javax.swing.JDialog parentDialog) {
		isShowing = false;
		setMode(LESS_DETAIL_MODE);
		super.postDialogShow(parentDialog);
	}	
	
	public int showStdErrOutDialog(){
		if( !isShowing ) {
			isShowing = true;
			return edu.cmu.cs.stage3.swing.DialogManager.showDialog( this );
		} else{
			return -1;
		}
	}
	
	
	public String getTitle() {
		return titleString;
	}

	protected void setHistoryDetail(){
//		detailPanel.add(detailScrollPane, BorderLayout.CENTER);
		this.add(detailScrollPane, BorderLayout.CENTER);
		buttonPanel.removeAll();
		buttonConstraints.gridx = 0;
		buttonPanel.add(cancelButton, buttonConstraints );
		//buttonConstraints.gridx++;
		//buttonPanel.add(submitBugButton, buttonConstraints);
		buttonConstraints.gridx++;			
		buttonPanel.add(copyButton, buttonConstraints);
		buttonConstraints.gridx++;
		buttonPanel.add(cancelButton, buttonConstraints );
		buttonConstraints.gridx++;
		glueConstraints.gridx = buttonConstraints.gridx;
		buttonPanel.add(buttonGlue, glueConstraints);
		
		
		if (errorContentAdded){
			messageLabel.setText(Messages.getString("StdErrOutContentPane.8")); //$NON-NLS-1$
		} else if (textContentAdded){
			messageLabel.setText(Messages.getString("StdErrOutContentPane.9")); //$NON-NLS-1$
		} else{
			messageLabel.setText(Messages.getString("StdErrOutContentPane.10")); //$NON-NLS-1$
		}
	}

	
	protected void setLessDetail(){
		super.setLessDetail();
		messageLabel.setText(Messages.getString("StdErrOutContentPane.11")); //$NON-NLS-1$
	}
	
	
	protected void setMoreDetail(){
		super.setMoreDetail();
		messageLabel.setText(Messages.getString("StdErrOutContentPane.12")); //$NON-NLS-1$
	}
	

	
	protected void handleModeSwitch(int mode){
		if( mode == LESS_DETAIL_MODE ) {
			setLessDetail();
		} else if( mode == MORE_DETAIL_MODE ) {
			setMoreDetail();
		} else if (mode == HISTORY_MODE){
			setHistoryDetail();
		}  else {
			throw new IllegalArgumentException( Messages.getString("StdErrOutContentPane.13") + mode ); //$NON-NLS-1$
		}
		packDialog();
	}
	
	
	
}