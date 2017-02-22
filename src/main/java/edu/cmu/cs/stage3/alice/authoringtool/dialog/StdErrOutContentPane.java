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

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;

/**
 * @author Jason Pratt, David Culyba
 */

public class StdErrOutContentPane extends edu.cmu.cs.stage3.alice.authoringtool.dialog.AliceAlertContentPane {
	public final static int HISTORY_MODE = 2;
	
	protected boolean errorDialog = false;

	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected OutputComponent errOutputComponent;
	protected OutputComponent stdOutputComponent;

	protected String lastError;
	protected String lastOutput;
	protected String titleString;

	protected boolean isShowing = false;
	protected boolean errorContentAdded = false;
	protected boolean textContentAdded = false;
	protected boolean shouldListenToErrors = true;
	protected boolean shouldListenToPrint = true;

	protected class ErrOutputDocumentListener implements javax.swing.event.DocumentListener {
		public void insertUpdate(final javax.swing.event.DocumentEvent ev) {
			try {
				lastError = ev.getDocument().getText(ev.getOffset(), ev.getLength());
				if (lastError.trim().startsWith("Unable to handle format") == true) {
					lastError = Messages.getString("_n_nYour_sound_file_cannot_be_played_in_Alice__n")
							+ Messages.getString("Please_find_an_audio_editor_to_convert_the_file_to_one_with_a_PCM_encoding__n")
							+ Messages.getString("See_the_tutorial_on_converting_sound_files_at_our_Alice_website__n")
							+ Messages.getString("Right_click_to_clear_the_messages_here__n_n")
							+ lastError.trim();
				} 
				detailTextPane.getDocument().insertString(
						detailTextPane.getDocument().getLength(), lastError,
						detailTextPane.stdErrStyle);
				
				if (lastError.startsWith("java.lang.ClassCastException") == true){
					lastError = "OOPS!! Looks like we have a slight layout problem. Not a big deal, press OK to continue. \n\n";		
					detailTextPane.getDocument().insertString( 0, lastError, detailTextPane.stdErrStyle);
				} 
			} catch (Exception e) {
			}
			errorContentAdded = true;
			update();
		}

		public void removeUpdate(javax.swing.event.DocumentEvent ev) {
			update();
		}

		public void changedUpdate(javax.swing.event.DocumentEvent ev) {
			update();
		}

		private void update() {
			if (shouldListenToErrors) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!isShowing) {
							isShowing = true;
							edu.cmu.cs.stage3.swing.DialogManager.showDialog(StdErrOutContentPane.this);
						}
					}
				});
			}

		}
	}

	protected class StdOutputDocumentListener implements javax.swing.event.DocumentListener {
		public void insertUpdate(final javax.swing.event.DocumentEvent ev) {
			try {
				lastOutput = ev.getDocument().getText(ev.getOffset(), ev.getLength());
				detailTextPane.getDocument().insertString(detailTextPane.getDocument().getLength(), lastOutput,	detailTextPane.stdOutStyle);
			} catch (Exception e) {
			}
			textContentAdded = true;
			update();
		}

		public void removeUpdate(javax.swing.event.DocumentEvent ev) {
			update();
		}

		public void changedUpdate(javax.swing.event.DocumentEvent ev) {
			update();
		}

		private void update() {
			if (shouldListenToPrint) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!isShowing) {
							isShowing = true;
							edu.cmu.cs.stage3.swing.DialogManager.showDialog(StdErrOutContentPane.this);
						}
					}
				});
			}

		}
	}

	public StdErrOutContentPane(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, boolean errorDialog) {
		super();
		if (errorDialog){
			titleString = Messages.getString("Alice___Error_Console");
			this.authoringTool = authoringTool;
			this.errOutputComponent = authoringTool.getStdErrOutputComponent();
			writeGenericAliceHeaderToTextPane();
			this.errOutputComponent.getTextPane().getDocument()
				.addDocumentListener(new ErrOutputDocumentListener());
		} else {
			titleString = Messages.getString("Alice___Text_Output");
			this.authoringTool = authoringTool;
			this.stdOutputComponent = authoringTool.getStdOutOutputComponent();
			//writeGenericAliceHeaderToTextPane();
			this.stdOutputComponent.getTextPane().getDocument()
				.addDocumentListener(new StdOutputDocumentListener());
		}
	}

	protected void writeGenericAliceHeaderToTextPane() {
		detailTextPane.setText("");
		detailStream.println(Messages.getString("Alice_version__", edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion()));
		String[] systemProperties = { "os.name", "os.version", "os.arch", "java.vm.name", "java.vm.version", "user.dir" };
		for (int i = 0; i < systemProperties.length; i++) {
			detailStream.println(systemProperties[i] + ": " + System.getProperty(systemProperties[i]));
		}
		detailStream.println();
	}

	public void preDialogShow(javax.swing.JDialog parentDialog) {
		super.preDialogShow(parentDialog);
	}

	public void stopReactingToPrint() {
		shouldListenToPrint = false;
	}

	public void startReactingToPrint() {
		//stdOutputComponent.stdErrStream.flush();
		if (stdOutputComponent != null){ 
			stdOutputComponent.stdOutStream.flush();
		}
		shouldListenToPrint = true;
	}

	public void stopReactingToError() {
		shouldListenToErrors = false;
	}

	public void startReactingToError() {
		if (errOutputComponent != null){ 
			errOutputComponent.stdErrStream.flush();
		}
		//errOutputComponent.stdOutStream.flush();
		shouldListenToErrors = true;
	}

	public void postDialogShow(javax.swing.JDialog parentDialog) {
		isShowing = false;
		setMode(LESS_DETAIL_MODE);
		super.postDialogShow(parentDialog);
	}

	public int showStdErrDialog() {
		if (!isShowing) {
			isShowing = true;
			errorDialog = true;
			return edu.cmu.cs.stage3.swing.DialogManager.showDialog(this);
		}
		return -1;
	}
	
	public int showStdOutDialog() {
		if (!isShowing) {
			isShowing = true;
			errorDialog = false;
			return edu.cmu.cs.stage3.swing.DialogManager.showDialog(this);
		}
		return -1;
	}
	
	public String getTitle() {
		return titleString;
	}

	protected void setHistoryDetail() {
		// detailPanel.add(detailScrollPane, BorderLayout.CENTER);
		this.add(detailScrollPane, BorderLayout.CENTER);
		buttonPanel.removeAll();
		buttonConstraints.gridx = 0;
		buttonPanel.add(cancelButton, buttonConstraints);
		// buttonConstraints.gridx++;
		// buttonPanel.add(submitBugButton, buttonConstraints);
		buttonConstraints.gridx++;
		buttonPanel.add(copyButton, buttonConstraints);
		buttonConstraints.gridx++;
		buttonPanel.add(cancelButton, buttonConstraints);
		buttonConstraints.gridx++;
		glueConstraints.gridx = buttonConstraints.gridx;
		buttonPanel.add(buttonGlue, glueConstraints);

		if (errorContentAdded) {
			messageLabel.setText(Messages.getString("Something_bad_has_occurred_"));
		} else if (textContentAdded) {
			messageLabel.setText(Messages.getString("Nothing_bad_has_occurred_"));
		} else {
			messageLabel.setText(Messages.getString("Nothing_bad_has_occurred_"));
		}
	}

	protected void setLessDetail() {
		super.setLessDetail();
		messageLabel.setText(Messages.getString("An_unknown_error_has_occurred_"));
	}

	protected void setMoreDetail() {
		super.setMoreDetail();
		messageLabel.setText(Messages.getString("An_unknown_error_has_occurred_"));
	}

	protected void handleModeSwitch(int mode) {
		if (mode == LESS_DETAIL_MODE) {
			setLessDetail();
		} else if (mode == MORE_DETAIL_MODE) {
			setMoreDetail();
		} else if (mode == HISTORY_MODE) {
			setHistoryDetail();
		} else {
			throw new IllegalArgumentException(Messages.getString("Illegal_mode__", mode));
		}
		packDialog();
	}

}
