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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;

/**
 * @author Jason Pratt, Aik Min Choong
 */

public class PreferencesContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	protected java.util.HashMap checkBoxToConfigKeyMap = new java.util.HashMap();
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	private Package authoringToolPackage = Package.getPackage("edu.cmu.cs.stage3.alice.authoringtool");
	protected javax.swing.JFileChooser browseFileChooser = new javax.swing.JFileChooser();
	protected java.util.HashMap rendererStringMap = new java.util.HashMap();
	protected boolean restartRequired = false;
	protected boolean reloadRequired = false;
	protected boolean shouldListenToRenderBoundsChanges = true;
	protected boolean changedCaptureDirectory = false;
	protected java.awt.Frame owner;
	private java.util.Vector m_okActionListeners = new java.util.Vector();
	private final String FOREVER_INTERVAL_STRING = Messages.getString("Forever");
	private final String INFINITE_BACKUPS_STRING = Messages.getString("Infinite");

	private static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
			.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());

	public PreferencesContentPane() {
		super();
		this.setPreferredSize(new Dimension(600, 500));
		this.setMinimumSize(new Dimension(600, 500));
		jbInit();
		actionInit();
		checkBoxMapInit();
		miscInit();
		updateGUI();
		scaleFont(this);
	}

	private void actionInit() {
		okayAction.putValue(javax.swing.Action.NAME, Messages.getString("OK"));
		okayAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Accept_preference_changes"));
		cancelAction.putValue(javax.swing.Action.NAME, Messages.getString("Cancel"));
		cancelAction.putValue(javax.swing.Action.SHORT_DESCRIPTION,	Messages.getString("Close_dialog_without_accepting_changes"));
		okayButton.setAction(okayAction);
		cancelButton.setAction(cancelAction);
	}

	private void checkBoxMapInit() {
		useBorderlessWindowCheckBox.setText(Messages.getString("use_a_borderless_render_window"));
		watcherPanelEnabledCheckBox.setText(Messages.getString("show_variable_watcher_when_world_runs"));
		runtimeScratchPadEnabledCheckBox.setText(Messages.getString("show_scratch_pad_when_world_runs"));
		infiniteBackupsCheckBox.setText(Messages.getString("save_infinite_number_of_backup_scripts"));
		doProfilingCheckBox.setText(Messages.getString("profile_world"));
		enableScriptingCheckBox.setToolTipText("");
		enableScriptingCheckBox.setActionCommand("enable jython scripting");
		enableScriptingCheckBox.setText(" "+Messages.getString("enable_jython_scripting"));
		saveAsSingleFileCheckBox.setText(Messages.getString("always_save_worlds_as_single_files"));
		checkBoxToConfigKeyMap.put(showStartUpDialogCheckBox, "showStartUpDialog");
		checkBoxToConfigKeyMap.put(enableHighContrastCheckBox, "enableHighContrastMode");
		checkBoxToConfigKeyMap.put(enableLoggingCheckBox, "enableLoggingMode");
		checkBoxToConfigKeyMap.put(disableTooltipCheckBox, "disableTooltipMode");
		checkBoxToConfigKeyMap.put(showBuilderCheckBox, "showBuilderMode");
		checkBoxToConfigKeyMap.put(showWebWarningCheckBox, "showWebWarningDialog");
		checkBoxToConfigKeyMap.put(loadSavedTabsCheckBox, "loadSavedTabs");
		// checkBoxToConfigKeyMap.put( reloadWorldScriptCheckBox,
		// "reloadWorldScriptOnRun" );
		checkBoxToConfigKeyMap.put(saveThumbnailWithWorldCheckBox, "saveThumbnailWithWorld");
		checkBoxToConfigKeyMap.put(forceSoftwareRenderingCheckBox, "rendering.forceSoftwareRendering");
		checkBoxToConfigKeyMap.put(showFPSCheckBox, "rendering.showFPS");
		checkBoxToConfigKeyMap.put(deleteFiles, "rendering.deleteFiles"); // Aik Min added this.
		checkBoxToConfigKeyMap.put(useBorderlessWindowCheckBox,	"rendering.useBorderlessWindow");
		// checkBoxToConfigKeyMap.put( renderWindowMatchesSceneEditorCheckBox,
		// "rendering.renderWindowMatchesSceneEditor" );
		checkBoxToConfigKeyMap.put(constrainRenderDialogAspectCheckBox, "rendering.constrainRenderDialogAspectRatio");
		checkBoxToConfigKeyMap.put(ensureRenderDialogIsOnScreenCheckBox, "rendering.ensureRenderDialogIsOnScreen");
		checkBoxToConfigKeyMap.put(createNormalsCheckBox, "importers.aseImporter.createNormalsIfNoneExist");
		checkBoxToConfigKeyMap.put(createUVsCheckBox, "importers.aseImporter.createUVsIfNoneExist");
		checkBoxToConfigKeyMap.put(useSpecularCheckBox, "importers.aseImporter.useSpecular");
		checkBoxToConfigKeyMap.put(groupMultipleRootObjectsCheckBox, "importers.aseImporter.groupMultipleRootObjects");
		checkBoxToConfigKeyMap.put(colorToWhiteWhenTexturedCheckBox, "importers.aseImporter.colorToWhiteWhenTextured");
		checkBoxToConfigKeyMap.put(watcherPanelEnabledCheckBox, "watcherPanelEnabled");
		checkBoxToConfigKeyMap.put(runtimeScratchPadEnabledCheckBox, "rendering.runtimeScratchPadEnabled");
		checkBoxToConfigKeyMap.put(infiniteBackupsCheckBox, "saveInfiniteBackups");
		checkBoxToConfigKeyMap.put(doProfilingCheckBox, "doProfiling");
		// checkBoxToConfigKeyMap.put( scriptTypeInEnabledCheckBox,
		// "editors.sceneeditor.showScriptComboWidget" );
		checkBoxToConfigKeyMap.put(showWorldStatsCheckBox, "showWorldStats");
		checkBoxToConfigKeyMap.put(enableScriptingCheckBox, "enableScripting");
		checkBoxToConfigKeyMap.put(pickUpTilesCheckBox, "gui.pickUpTiles");
		checkBoxToConfigKeyMap.put(useAlphaTilesCheckBox, "gui.useAlphaTiles");
		// checkBoxToConfigKeyMap.put( useJavaSyntaxCheckBox, "useJavaSyntax" );
		checkBoxToConfigKeyMap.put(saveAsSingleFileCheckBox, "useSingleFileLoadStore");
		checkBoxToConfigKeyMap.put(clearStdOutOnRunCheckBox, "clearStdOutOnRun");
		checkBoxToConfigKeyMap.put(screenCaptureInformUserCheckBox, "screenCapture.informUser");
		// checkBoxToConfigKeyMap.put( printingFillBackgroundCheckBox,
		// "printing.fillBackground" );
	}

	private void miscInit() {
		browseFileChooser.setApproveButtonText(Messages.getString("Set_Directory"));
		browseFileChooser.setDialogTitle(Messages.getString("Choose_Directory___"));
		browseFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		browseFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		Configuration.addConfigurationListener(new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener() {
					public void changing(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
					}

					public void changed(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
						if (ev.getKeyName().endsWith("rendering.orderedRendererList") ||
								ev.getKeyName().endsWith("rendering.forceSoftwareRendering") ||
								ev.getKeyName().endsWith("resourceFile") ||
								ev.getKeyName().endsWith("language") ||
								ev.getKeyName().endsWith("enableScripting") ||
								ev.getKeyName().endsWith("enableLoggingMode")) {
							restartRequired = true;
						}
					}
				});
	}

	public final javax.swing.AbstractAction okayAction = new javax.swing.AbstractAction() {
		public void actionPerformed(java.awt.event.ActionEvent ev) {
			if (PreferencesContentPane.this.validateInput()) {
				fireOKActionListeners();
			}
		}
	};

	public final javax.swing.AbstractAction cancelAction = new javax.swing.AbstractAction() {
		public void actionPerformed(java.awt.event.ActionEvent ev) {
		}
	};

	public final javax.swing.event.DocumentListener captureDirectoryChangeListener = new javax.swing.event.DocumentListener() {
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			changedCaptureDirectory = true;
		}

		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			changedCaptureDirectory = true;
		}

		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			changedCaptureDirectory = true;
		}
	};

	public final javax.swing.event.DocumentListener renderDialogBoundsChecker = new javax.swing.event.DocumentListener() {
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderBounds();
			}
		}

		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderBounds();
			}
		}

		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderBounds();
			}
		}
	};

	public final javax.swing.event.DocumentListener renderDialogWidthChecker = new javax.swing.event.DocumentListener() {
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderWidth();
			}
		}

		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderWidth();
			}
		}

		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderWidth();
			}
		}
	};

	public final javax.swing.event.DocumentListener renderDialogHeightChecker = new javax.swing.event.DocumentListener() {
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderHeight();
			}
		}

		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderHeight();
			}
		}

		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges) {
				checkAndUpdateRenderHeight();
			}
		}
	};

	private void scaleFont(Component currentComponent) {
		currentComponent.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));

		if (currentComponent instanceof Container) {
			for (int i = 0; i < ((Container) currentComponent)
					.getComponentCount(); i++) {
				scaleFont(((Container) currentComponent).getComponent(i));
			}
		}
	}

	public void setAuthoringTool(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
	}

	public String getTitle() {
		return Messages.getString("Preferences");
	}

	public void preDialogShow(javax.swing.JDialog dialog) {
		super.preDialogShow(dialog);
		updateGUI();
		changedCaptureDirectory = false;
	}

	public void postDialogShow(javax.swing.JDialog dialog) {
		super.postDialogShow(dialog);
	}

	public void addOKActionListener(java.awt.event.ActionListener l) {
		m_okActionListeners.addElement(l);
	}

	public void removeOKActionListener(java.awt.event.ActionListener l) {
		m_okActionListeners.removeElement(l);
	}

	public void addCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.addActionListener(l);
	}

	public void removeCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.removeActionListener(l);
	}

	private void fireOKActionListeners() {
		java.awt.event.ActionEvent e = new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, Messages.getString("OK"));
		for (int i = 0; i < m_okActionListeners.size(); i++) {
			java.awt.event.ActionListener l = (java.awt.event.ActionListener) m_okActionListeners.elementAt(i);
			l.actionPerformed(e);
		}
	}

	public void finalizeSelections() {
		setInput();
		if (restartRequired) {
			Object[] options = { Messages.getString("Restart"), Messages.getString("Cancel") };
			int result = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(
					Messages.getString("You_will_have_to_restart_Alice_in_order_for_these_settings_to_take_effect_"),
					Messages.getString("Restart_Required"),
					JOptionPane.OK_CANCEL_OPTION,
					javax.swing.JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			restartRequired = false;
			if (result == ContentPane.OK_OPTION) {
				authoringTool.quit(true);
			}
		} else if (reloadRequired) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("You_will_have_to_reload_the_current_world_in_order_for_these_settings_to_take_effect_"),
					Messages.getString("Reload_Required"),
					JOptionPane.INFORMATION_MESSAGE);
			reloadRequired = false;
		}
		if (configTabbedPane != null && generalPanel != null) {
			configTabbedPane.setSelectedComponent(generalPanel);
		}
	}

	protected boolean isValidRenderBounds(int x, int y, int w, int h) {
		if ((x < 0) || (y < 0) || (w <= 0) || (h <= 0)) {
			return false;
		}
		return true;
	}

	protected void checkAndUpdateRenderWidth() {
		int w = 0, h = 0;
		boolean isOK = true;
		try {
			w = java.lang.Integer.parseInt(boundsWidthTextField.getText());
			if (w > 0) {
				boundsWidthTextField.setForeground(java.awt.Color.black);
			} else {
				boundsWidthTextField.setForeground(java.awt.Color.red);
				isOK = false;
			}
		} catch (java.lang.NumberFormatException e) {
			boundsWidthTextField.setForeground(java.awt.Color.red);
			isOK = false;
		}
		try {
			h = java.lang.Integer.parseInt(boundsHeightTextField.getText());
			if (h <= 0) {
				isOK = false;
			}
		} catch (java.lang.NumberFormatException e) {
			isOK = false;
		}
		if (constrainRenderDialogAspectCheckBox.isSelected() && isOK && authoringTool != null) {
			double currentAspectRatio = authoringTool.getAspectRatio();
			h = (int) Math.round(w / currentAspectRatio);
			if (h <= 0) {
				h = 1;
			}
			shouldListenToRenderBoundsChanges = false;
			boundsHeightTextField.setText(Integer.toString(h));
			shouldListenToRenderBoundsChanges = true;
		}
		okayButton.setEnabled(isOK);
	}

	protected void checkAndUpdateRenderHeight() {
		int w = 0, h = 0;
		boolean isOK = true;
		try {
			h = java.lang.Integer.parseInt(boundsHeightTextField.getText());
			if (h > 0) {
				boundsHeightTextField.setForeground(java.awt.Color.black);
			} else {
				boundsHeightTextField.setForeground(java.awt.Color.red);
				isOK = false;
			}
		} catch (java.lang.NumberFormatException e) {
			boundsHeightTextField.setForeground(java.awt.Color.red);
			isOK = false;
		}
		try {
			w = java.lang.Integer.parseInt(boundsWidthTextField.getText());
			if (w <= 0) {
				isOK = false;
			}
		} catch (java.lang.NumberFormatException e) {
			isOK = false;
		}
		if (constrainRenderDialogAspectCheckBox.isSelected() && isOK && authoringTool != null) {
			double currentAspectRatio = authoringTool.getAspectRatio();
			w = (int) Math.round(h * currentAspectRatio);
			if (w <= 0) {
				w = 1;
			}
			shouldListenToRenderBoundsChanges = false;
			boundsWidthTextField.setText(Integer.toString(w));
			shouldListenToRenderBoundsChanges = true;
		}
		okayButton.setEnabled(isOK);
	}

	protected void checkAndUpdateRenderBounds() {
		int x = 0, y = 0, w = 0, h = 0;
		boolean isOK = true;
		try {
			x = java.lang.Integer.parseInt(boundsXTextField.getText());
			if (x >= 0) {
				boundsXTextField.setForeground(java.awt.Color.black);
			} else {
				boundsXTextField.setForeground(java.awt.Color.red);
				isOK = false;
			}
		} catch (java.lang.NumberFormatException e) {
			boundsXTextField.setForeground(java.awt.Color.red);
			isOK = false;
		}
		try {
			y = java.lang.Integer.parseInt(boundsYTextField.getText());
			if (y >= 0) {
				boundsYTextField.setForeground(java.awt.Color.black);
			} else {
				boundsYTextField.setForeground(java.awt.Color.red);
				isOK = false;
			}
		} catch (java.lang.NumberFormatException e) {
			boundsYTextField.setForeground(java.awt.Color.red);
			isOK = false;
		}
		try {
			w = java.lang.Integer.parseInt(boundsWidthTextField.getText());
			if (w > 0) {
				boundsWidthTextField.setForeground(java.awt.Color.black);
			} else {
				boundsWidthTextField.setForeground(java.awt.Color.red);
				isOK = false;
			}
		} catch (java.lang.NumberFormatException e) {
			boundsWidthTextField.setForeground(java.awt.Color.red);
			isOK = false;
		}
		try {
			h = java.lang.Integer.parseInt(boundsHeightTextField.getText());
			if (h > 0) {
				boundsHeightTextField.setForeground(java.awt.Color.black);
			} else {
				boundsHeightTextField.setForeground(java.awt.Color.red);
				isOK = false;
			}
		} catch (java.lang.NumberFormatException e) {
			boundsHeightTextField.setForeground(java.awt.Color.red);
			isOK = false;
		}
		if (constrainRenderDialogAspectCheckBox.isSelected() && isOK && authoringTool != null) {
			double currentAspectRatio = authoringTool.getAspectRatio();
			if (currentAspectRatio > 1.0) {
				w = (int) Math.round(h * currentAspectRatio);
				if (w <= 0) {
					w = 1;
				}
				shouldListenToRenderBoundsChanges = false;
				boundsWidthTextField.setText(Integer.toString(w));
				shouldListenToRenderBoundsChanges = true;
			} else {
				h = (int) Math.round(w / currentAspectRatio);
				if (h <= 0) {
					h = 1;
				}
				shouldListenToRenderBoundsChanges = false;
				boundsHeightTextField.setText(Integer.toString(h));
				shouldListenToRenderBoundsChanges = true;
			}
		}
		okayButton.setEnabled(isOK);
	}

	protected boolean validateInput() {
		try {
			int i = java.lang.Integer.parseInt(maxRecentWorldsTextField.getText());
			if ((i < 0) || (i > 30)) {
				throw new java.lang.NumberFormatException();
			}
		} catch (java.lang.NumberFormatException e) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("the_maximum_number_of_recent_worlds_must_be_between_0_and_30"),
					Messages.getString("Invalid_Clipboard_Number"),
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		try {
			int i = java.lang.Integer.parseInt(numClipboardsTextField.getText());
			if ((i < 0) || (i > 30)) {
				throw new java.lang.NumberFormatException();
			}
		} catch (java.lang.NumberFormatException e) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("the_number_of_clipboards_must_be_between_0_and_30"),
					Messages.getString("Invalid_Clipboard_Number"),
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		try {
			int x = java.lang.Integer.parseInt(boundsXTextField.getText());
			int y = java.lang.Integer.parseInt(boundsYTextField.getText());
			int w = java.lang.Integer.parseInt(boundsWidthTextField.getText());
			int h = java.lang.Integer.parseInt(boundsHeightTextField.getText());
			if (!isValidRenderBounds(x, y, w, h)) {
				throw new java.lang.NumberFormatException();
			}
		} catch (java.lang.NumberFormatException e) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
							Messages.getString("all_of_the_render_window_bounds_values_must_be_integers_greater_than_0"),
							Messages.getString("Bad_Render_Bounds"),
							javax.swing.JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		// bad directories are just given warnings
		java.io.File worldDirectoryFile = new java.io.File(worldDirectoryTextField.getText());
		if ((!worldDirectoryFile.exists()) || (!worldDirectoryFile.isDirectory()) || (!worldDirectoryFile.canRead())) {
			int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(
							Messages.getString("is_not_valid___The_worlds_directory_must_be_a_directory_that_exists_and_can_be_read___Would_you_like_to_fix_this_now_", worldDirectoryFile.getAbsolutePath()),
							Messages.getString("Bad_Directory"),
							javax.swing.JOptionPane.YES_NO_OPTION,
							javax.swing.JOptionPane.WARNING_MESSAGE);
			if (result != javax.swing.JOptionPane.NO_OPTION) {
				return false;
			}
			// return false;
		}

		java.io.File importDirectoryFile = new java.io.File(importDirectoryTextField.getText());
		if ((!importDirectoryFile.exists())	|| (!importDirectoryFile.isDirectory()) || (!importDirectoryFile.canRead())) {
			int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(
							Messages.getString("is_not_valid___The_import_directory_must_be_a_directory_that_exists_and_can_be_read___Would_you_like_to_fix_this_now_", importDirectoryFile.getAbsolutePath()),
							Messages.getString("Bad_Directory"),
							javax.swing.JOptionPane.YES_NO_OPTION,
							javax.swing.JOptionPane.WARNING_MESSAGE);
			if (result != javax.swing.JOptionPane.NO_OPTION) {
				return false;
			}
			// return false;
		}

		// java.io.File galleryDirectoryFile = new java.io.File(
		// galleryDirectoryTextField.getText() );
		// if( (!galleryDirectoryFile.exists()) ||
		// (!galleryDirectoryFile.isDirectory()) ||
		// (!galleryDirectoryFile.canRead()) ) {
		// edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( this,
		// "The gallery directory must be a directory that exists and can be read."
		// );
		// return false;
		// }

		// java.io.File characterDirectoryFile = new java.io.File(
		// characterDirectoryTextField.getText() );
		// if( (!characterDirectoryFile.exists()) ||
		// (!characterDirectoryFile.isDirectory()) ||
		// (!characterDirectoryFile.canRead()) ) {
		// edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( this,
		// "The character directory must be a directory that exists and can be read."
		// );
		// return false;
		// }
		if (changedCaptureDirectory) {
			java.io.File captureDirectoryFile = new java.io.File(captureDirectoryTextField.getText());
			int directoryCheck = edu.cmu.cs.stage3.io.FileUtilities.isWritableDirectory(captureDirectoryFile);
			if (directoryCheck == edu.cmu.cs.stage3.io.FileUtilities.DIRECTORY_IS_NOT_WRITABLE) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
								Messages.getString("The_capture_directory_specified_can_not_be_written_to__Please_choose_another_directory_"),
								Messages.getString("Bad_Directory"),
								javax.swing.JOptionPane.INFORMATION_MESSAGE);
				return false;
			} else if (directoryCheck == edu.cmu.cs.stage3.io.FileUtilities.DIRECTORY_DOES_NOT_EXIST
					|| directoryCheck == edu.cmu.cs.stage3.io.FileUtilities.BAD_DIRECTORY_INPUT) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
								Messages.getString("The_capture_directory_must_be_a_directory_that_exists_"),
								Messages.getString("Bad_Directory"),
								javax.swing.JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}

		if (baseNameTextField.getText().trim().equals("")) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("The_capture_base_name_must_not_be_empty_"),
					Messages.getString("Bad_Base_Name"),
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		char[] badChars = { '\\', '/', ':', '*', '?', '"', '<', '>', '|' }; 
		String baseName = baseNameTextField.getText().trim();
		for (int i = 0; i < badChars.length; i++) {
			if (baseName.indexOf(badChars[i]) != -1) {
				StringBuffer message = new StringBuffer(Messages.getString("Filenames_may_not_contain_the_following_characters_"));
				for (int j = 0; j < badChars.length; j++) {
					message.append(" ");
					message.append(badChars[j]);
				}
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(message.toString(), Messages.getString("Bad_Filename"), javax.swing.JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}

		String saveIntervalString = (String) saveIntervalComboBox.getSelectedItem();
		if (!saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)) {
			try {
				int i = Integer.parseInt(saveIntervalString);
				if (i < 0) {
					throw new java.lang.NumberFormatException();
				}
			} catch (Throwable t) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
								Messages.getString("You_must_enter_a_valid_number_for_the_time_to_wait_before_prompting_to_save_"),
								Messages.getString("Bad_Prompt_To_Save_Interval"),
								javax.swing.JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}

		String backupCountString = (String) backupCountComboBox.getSelectedItem();
		if (!backupCountString.equalsIgnoreCase(INFINITE_BACKUPS_STRING)) {
			try {
				int i = Integer.parseInt(backupCountString);
				if (i < 0) {
					throw new java.lang.NumberFormatException();
				}
			} catch (Throwable t) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
								Messages.getString("You_must_enter_a_valid_number_for_the_number_of_backups_you_want_Alice_to_save_"),
								Messages.getString("Bad_Backup_Count_Value"),
								javax.swing.JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}
		
		String fontSizeString = (String) fontSizeComboBox.getSelectedItem();
		try {
			int i = Integer.parseInt(fontSizeString);
			if (i < 0) {
				throw new java.lang.NumberFormatException();
			}
		} catch (Throwable t) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
							Messages.getString("You_must_enter_a_valid_number_for_the_font_size_"),
							Messages.getString("Invalid_Font_Size"),
							javax.swing.JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		String decimalPlacesString = (String) decimalPlacesComboBox.getSelectedItem();
		try {
			int i = Integer.parseInt(decimalPlacesString);
			if (i < 0) {
				throw new java.lang.NumberFormatException();
			}
		} catch (Throwable t) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
							Messages.getString("You_must_enter_a_valid_number_for_the_decimal_places_"),
							Messages.getString("Invalid_Number"),
							javax.swing.JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

	@SuppressWarnings("cast")
	protected void setInput() {
		boolean oldContrast = Configuration.getValue(authoringToolPackage, "enableHighContrastMode").equalsIgnoreCase("true");
		for (java.util.Iterator iter = checkBoxToConfigKeyMap.keySet().iterator(); iter.hasNext();) {
			javax.swing.JCheckBox checkBox = (javax.swing.JCheckBox) iter.next();
			String currentValue = Configuration.getValue(authoringToolPackage,	(String) checkBoxToConfigKeyMap.get(checkBox));
			if (currentValue == null) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool	.showErrorDialog(Messages.getString("Warning__no_value_found_for_preference__", checkBoxToConfigKeyMap.get(checkBox)), null);
				currentValue = "false";
				Configuration.setValue(authoringToolPackage, (String) checkBoxToConfigKeyMap.get(checkBox),	currentValue);
			}
			if (currentValue.equalsIgnoreCase("true") != checkBox.isSelected()) {
				Configuration.setValue(authoringToolPackage, (String) checkBoxToConfigKeyMap.get(checkBox),	checkBox.isSelected() ? "true" : "false");
			}
		}
		// Aik Min - Logging and Tooltip
		if (Configuration.getValue(authoringToolPackage, "disableTooltipMode").equalsIgnoreCase("true")) {
			javax.swing.ToolTipManager.sharedInstance().setEnabled(false);
		} else {
			javax.swing.ToolTipManager.sharedInstance().setEnabled(true);
		}
		
		// Aik Min - Show/hide he/she builder
		if (Configuration.getValue(authoringToolPackage, "showBuilderMode").equalsIgnoreCase("true")) {
			GalleryViewer.showBuilder = true;
		} else {
			GalleryViewer.showBuilder = false;
		}
		
		// Aik Min - Decimal places
		if (!Configuration.getValue(authoringToolPackage, "decimalPlaces").equals((String) decimalPlacesComboBox.getSelectedItem())) {
			Configuration.setValue(authoringToolPackage, "decimalPlaces", (String) decimalPlacesComboBox.getSelectedItem());
			AikMin.decimal = Integer.valueOf((String) decimalPlacesComboBox.getSelectedItem());		
		}
		
		if (!Configuration.getValue(authoringToolPackage, "recentWorlds.maxWorlds").equals(maxRecentWorldsTextField.getText())) {
			Configuration.setValue(authoringToolPackage, "recentWorlds.maxWorlds", maxRecentWorldsTextField.getText());
		}
		if (!Configuration.getValue(authoringToolPackage, "numberOfClipboards").equals(numClipboardsTextField.getText())) {
			Configuration.setValue(authoringToolPackage, "numberOfClipboards", numClipboardsTextField.getText());
		}
		String boundsString = boundsXTextField.getText() + ", " + boundsYTextField.getText() + ", "	+ boundsWidthTextField.getText() + ", "	+ boundsHeightTextField.getText();
		if (!Configuration.getValue(authoringToolPackage, "rendering.renderWindowBounds").equals(boundsString)) {
			Configuration.setValue(authoringToolPackage, "rendering.renderWindowBounds", boundsString);
		}
		if (!Configuration.getValue(authoringToolPackage, "directories.worldsDirectory").equals(worldDirectoryTextField.getText())) {
			Configuration.setValue(authoringToolPackage, "directories.worldsDirectory", worldDirectoryTextField.getText());
		}
		if (!Configuration.getValue(authoringToolPackage, "directories.importDirectory").equals(worldDirectoryTextField.getText())) {
			Configuration.setValue(authoringToolPackage, "directories.importDirectory", worldDirectoryTextField.getText());
		}
		// if( ! Configuration.getValue( authoringToolPackage,
		// "directories.galleryDirectory" ).equals(
		// galleryDirectoryTextField.getText() ) ) {
		// Configuration.setValue( authoringToolPackage,
		// "directories.galleryDirectory", galleryDirectoryTextField.getText()
		// );
		// }
		if( ! Configuration.getValue( authoringToolPackage, "directories.charactersDirectory" ).equals(worldDirectoryTextField.getText() + System.getProperty( "file.separator" ) + "CustomGallery") ) {
			Configuration.setValue( authoringToolPackage, "directories.charactersDirectory", worldDirectoryTextField.getText() + System.getProperty( "file.separator" ) + "CustomGallery");
		}
		if (!Configuration.getValue(authoringToolPackage, "screenCapture.directory").equals(captureDirectoryTextField.getText())) {
			Configuration.setValue(authoringToolPackage, "screenCapture.directory",	captureDirectoryTextField.getText());
		}
		if (!Configuration.getValue(authoringToolPackage, "screenCapture.baseName").equals(baseNameTextField.getText())) {
			Configuration.setValue(authoringToolPackage, "screenCapture.baseName", baseNameTextField.getText());
		}
		if (!Configuration.getValue(authoringToolPackage, "screenCapture.numDigits").equals((String) numDigitsComboBox.getSelectedItem())) {
			Configuration.setValue(authoringToolPackage, "screenCapture.numDigits",	(String) numDigitsComboBox.getSelectedItem());
		}
		if (!Configuration.getValue(authoringToolPackage, "screenCapture.codec").equals((String) codecComboBox.getSelectedItem())) {
			Configuration.setValue(authoringToolPackage, "screenCapture.codec",(String) codecComboBox.getSelectedItem());
		}
		if (!Configuration.getValue(authoringToolPackage, "resourceFile").equals((String) resourceFileComboBox.getSelectedItem())) {
			Configuration.setValue(authoringToolPackage, "resourceFile", (String) resourceFileComboBox.getSelectedItem());
		}
		if (!Configuration.getValue(authoringToolPackage, "language").equals((String) languageComboBox.getSelectedItem())) {
			Configuration.setValue(authoringToolPackage, "language", (String) languageComboBox.getSelectedItem());
			Configuration.setValue( authoringToolPackage, "directories.templatesDirectory", "templateWorlds" + System.getProperty( "file.separator" ) + (String) languageComboBox.getSelectedItem() );  
		}
		// if( ! Configuration.getValue( authoringToolPackage,
		// "printing.scaleFactor" ).equals(
		// printingScaleComboBox.getSelectedItem().toString() ) ) {
		// Configuration.setValue( authoringToolPackage, "printing.scaleFactor",
		// printingScaleComboBox.getSelectedItem().toString() );
		// }

		String saveIntervalString = (String) saveIntervalComboBox.getSelectedItem();
		if (saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)) {
			Configuration.setValue(authoringToolPackage, "promptToSaveInterval", Integer.toString(Integer.MAX_VALUE));
		} else {
			Configuration.setValue(authoringToolPackage, "promptToSaveInterval", saveIntervalString);
		}

		String backupCountString = (String) backupCountComboBox.getSelectedItem();
		if (saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)) {
			Configuration.setValue(authoringToolPackage, "maximumWorldBackupCount", Integer.toString(Integer.MAX_VALUE));
		} else {
			Configuration.setValue(authoringToolPackage, "maximumWorldBackupCount", backupCountString);
		}

		int oldFontSize = ((java.awt.Font) javax.swing.UIManager.get("Label.font")).getSize();
		String fontSizeString = (String) fontSizeComboBox.getSelectedItem();
		Configuration.setValue(authoringToolPackage, "fontSize", fontSizeString);
		int newFontSize = Integer.valueOf(fontSizeString).intValue();
		if (oldContrast != enableHighContrastCheckBox.isSelected() || oldFontSize != newFontSize) {
			restartRequired = true;
		}
		// java.awt.Color backgroundColor =
		// backgroundColorButton.getBackground();
		// String backgroundColorString = new
		// edu.cmu.cs.stage3.alice.scenegraph.Color( backgroundColor
		// ).toString();
		// if( ! Configuration.getValue( authoringToolPackage, "backgroundColor"
		// ).equals( backgroundColorString ) ) {
		// Configuration.setValue( authoringToolPackage, "backgroundColor",
		// backgroundColorString );
		// }

		// TODO: currently the rendererList updates its data immediately...

		try {
			Configuration.storeConfig();
		} catch (java.io.IOException e) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(Messages.getString("Error_storing_preferences_"), e);
		}
	}

	protected void updateGUI() {
		for (java.util.Iterator iter = checkBoxToConfigKeyMap.keySet().iterator(); iter.hasNext();) {
			javax.swing.JCheckBox checkBox = (javax.swing.JCheckBox) iter.next();
			boolean value;
			try {
				value = Configuration.getValue(authoringToolPackage, (String) checkBoxToConfigKeyMap.get(checkBox)).equalsIgnoreCase("true");
			} catch (Exception e) {
				value = false;
			}
			checkBox.setSelected(value);
		}

		setSaveIntervalValues();
		initSaveIntervalComboBox();
		setBackupCountValues();
		initBackupCountComboBox();
		setFontSizeValues();
		initFontSizeComboBox();
		setDecimalPlacesValues();
		initDecimalPlacesComboBox();

		maxRecentWorldsTextField.setText(Configuration.getValue(authoringToolPackage, "recentWorlds.maxWorlds"));
		numClipboardsTextField.setText(Configuration.getValue(authoringToolPackage, "numberOfClipboards"));

		// String backgroundColorString = Configuration.getValue(authoringToolPackage, "backgroundColor" );
		// backgroundColorButton.setBackground(edu.cmu.cs.stage3.alice.scenegraph.Color.valueOf( backgroundColorString ).createAWTColor() );

		String boundsString = Configuration.getValue(authoringToolPackage,"rendering.renderWindowBounds");
		java.util.StringTokenizer st = new java.util.StringTokenizer(boundsString, " \t,");
		if (st.countTokens() == 4) {
			boundsXTextField.setText(st.nextToken());
			boundsYTextField.setText(st.nextToken());
			boundsWidthTextField.setText(st.nextToken());
			boundsHeightTextField.setText(st.nextToken());
		}

		String worldDirectory = Configuration.getValue(authoringToolPackage, "directories.worldsDirectory");
		worldDirectoryTextField.setText(worldDirectory);
		String importDirectory = Configuration.getValue(authoringToolPackage, "directories.importDirectory");
		importDirectoryTextField.setText(importDirectory);
		// String galleryDirectory = Configuration.getValue(authoringToolPackage, "directories.galleryDirectory" );
		// galleryDirectoryTextField.setText( galleryDirectory );
		// String characterDirectory = Configuration.getValue(authoringToolPackage, "directories.charactersDirectory" );
		// characterDirectoryTextField.setText( characterDirectory );
		String captureDirectory = Configuration.getValue(authoringToolPackage, "screenCapture.directory");
		captureDirectoryTextField.setText(captureDirectory);

		baseNameTextField.setText(Configuration.getValue(authoringToolPackage, "screenCapture.baseName"));
		numDigitsComboBox.setSelectedItem(Configuration.getValue(authoringToolPackage, "screenCapture.numDigits"));
		codecComboBox.setSelectedItem(Configuration.getValue(authoringToolPackage, "screenCapture.codec"));

		// printingScaleComboBox.setSelectedItem( Double.valueOf(
		// Configuration.getValue( authoringToolPackage, "printing.scaleFactor"
		// ) ) );

		// TODO: currently the rendererList updates its data immediately...
		/*
		 * ((javax.swing.DefaultListModel)rendererList.getModel()).clear();
		 * String[] rendererStrings = Configuration.getValueList(
		 * authoringToolPackage, "rendering.orderedRendererList" ); for( int i =
		 * 0; i < rendererStrings.length; i++ ) { String s = rendererStrings[i];
		 * String repr = AuthoringToolResources.getReprForValue( s );
		 * rendererStringMap.put( repr, s );
		 * ((javax.swing.DefaultListModel)rendererList.getModel()).addElement(
		 * repr ); } if( selectedRenderer != null ) {
		 * rendererList.setSelectedValue( selectedRenderer, true ); } else {
		 * rendererList.setSelectedIndex( 0 ); }
		 */
	}

	public void setVisible(boolean b) {
		if (b) {
			updateGUI();
		}
		super.setVisible(b);
	}

	protected class ConfigListModel implements javax.swing.ListModel, edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener {
		protected Package configPackage;
		protected String configKey;
		protected java.util.Set listenerSet = new java.util.HashSet();

		public ConfigListModel(Package configPackage, String configKey) {
			this.configPackage = configPackage;
			this.configKey = configKey;
			Configuration.addConfigurationListener(this);
		}

		public void addListDataListener(javax.swing.event.ListDataListener listener) {
			listenerSet.add(listener);
		}

		public void removeListDataListener(javax.swing.event.ListDataListener listener) {
			listenerSet.remove(listener);
		}

		public int getSize() {
			return Configuration.getValueList(configPackage, configKey).length;
		}

		public Object getElementAt(int index) {
			String item = Configuration.getValueList(configPackage, configKey)[index];
			return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(item);
		}

		public void moveIndexHigher(int index) {
			String[] valueList = Configuration.getValueList(configPackage, configKey);
			if ((index > 0) && (index < valueList.length)) {
				String[] newValueList = new String[valueList.length];
				System.arraycopy(valueList, 0, newValueList, 0, valueList.length);
				String temp = newValueList[index];
				newValueList[index] = newValueList[index - 1];
				newValueList[index - 1] = temp;
				Configuration.setValueList(configPackage, configKey, newValueList);
			}
		}

		public void moveIndexLower(int index) {
			String[] valueList = Configuration.getValueList(configPackage, configKey);
			if ((index >= 0) && (index < (valueList.length - 1))) {
				String[] newValueList = new String[valueList.length];
				System.arraycopy(valueList, 0, newValueList, 0,	valueList.length);
				String temp = newValueList[index];
				newValueList[index] = newValueList[index + 1];
				newValueList[index + 1] = temp;
				Configuration.setValueList(configPackage, configKey, newValueList);
			}
		}

		public void changing(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
		}

		public void changed(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
			if (ev.getKeyName().endsWith("rendering.orderedRendererList")) {
				int upperRange = 0;
				if (ev.getOldValueList() != null) {
					upperRange = Math.max(upperRange, ev.getOldValueList().length);
				}
				if (ev.getNewValueList() != null) {
					upperRange = Math.max(upperRange, ev.getNewValueList().length);
				}
				javax.swing.event.ListDataEvent listDataEvent = new javax.swing.event.ListDataEvent(
						this, javax.swing.event.ListDataEvent.CONTENTS_CHANGED, 0, upperRange);
				for (java.util.Iterator iter = listenerSet.iterator(); iter.hasNext();) {
					((javax.swing.event.ListDataListener) iter.next()).contentsChanged(listDataEvent);
				}
			}
		}
	}

	private int getValueForString(String numString) {
		if (numString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)	|| numString.equalsIgnoreCase(INFINITE_BACKUPS_STRING)) {
			return Integer.MAX_VALUE;
		}
		try {
			int toReturn = Integer.parseInt(numString);
			return toReturn;
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	private void setSaveIntervalValues() {
		saveIntervalOptions.removeAllElements();
		saveIntervalOptions.add("15");
		saveIntervalOptions.add("30");
		saveIntervalOptions.add("45");
		saveIntervalOptions.add("60");
		saveIntervalOptions.add(FOREVER_INTERVAL_STRING);
		String intervalString = Configuration.getValue(authoringToolPackage, "promptToSaveInterval");
		int interval = -1;
		try {
			interval = Integer.parseInt(intervalString);
		} catch (Throwable t) {
		}
		addComboBoxValueValue(interval, saveIntervalOptions);
	}

	private void addComboBoxValueValue(int toAdd, java.util.Vector toAddTo) {
		if (toAdd > 0) {
			boolean isThere = false;
			int location = toAddTo.size() - 1;
			for (int i = 0; i < toAddTo.size(); i++) {
				int currentValue = getValueForString((String) toAddTo.get(i));
				if (toAdd == currentValue) {
					isThere = true;
				} else if (toAdd < currentValue && location > i) {
					location = i;
				}
			}
			if (!isThere) {
				Integer currentValue = new Integer(toAdd);
				toAddTo.insertElementAt(currentValue.toString(), location);
			}
		}
	}

	private void initSaveIntervalComboBox() {
		saveIntervalComboBox.removeAllItems();
		String intervalString = Configuration.getValue(authoringToolPackage, "promptToSaveInterval");
		for (int i = 0; i < saveIntervalOptions.size(); i++) {
			saveIntervalComboBox.addItem(saveIntervalOptions.get(i));
			if (intervalString.equalsIgnoreCase(saveIntervalOptions.get(i).toString())) {
				saveIntervalComboBox.setSelectedIndex(i);
			} else if (intervalString.equalsIgnoreCase(Integer.toString(Integer.MAX_VALUE))	&& ((String) saveIntervalOptions.get(i)).equalsIgnoreCase(FOREVER_INTERVAL_STRING)) {
				saveIntervalComboBox.setSelectedIndex(i);
			}
		}
	}

	private void setBackupCountValues() {
		backupCountOptions.removeAllElements();
		backupCountOptions.add("0");
		backupCountOptions.add("1");
		backupCountOptions.add("2");
		backupCountOptions.add("3");
		backupCountOptions.add("4");
		backupCountOptions.add("5");
		backupCountOptions.add("10");
		backupCountOptions.add(INFINITE_BACKUPS_STRING);
		String intervalString = Configuration.getValue(authoringToolPackage, "maximumWorldBackupCount");
		int interval = -1;
		try {
			interval = Integer.parseInt(intervalString);
		} catch (Throwable t) {
		}
		addComboBoxValueValue(interval, backupCountOptions);
	}

	private void initBackupCountComboBox() {
		backupCountComboBox.removeAllItems();
		String intervalString = Configuration.getValue(authoringToolPackage, "maximumWorldBackupCount");
		for (int i = 0; i < backupCountOptions.size(); i++) {
			backupCountComboBox.addItem(backupCountOptions.get(i));
			if (intervalString.equalsIgnoreCase(backupCountOptions.get(i).toString())) {
				backupCountComboBox.setSelectedIndex(i);
			} else if (intervalString.equalsIgnoreCase(Integer.toString(Integer.MAX_VALUE))	&& ((String) backupCountOptions.get(i)).equalsIgnoreCase(INFINITE_BACKUPS_STRING)) {
				backupCountComboBox.setSelectedIndex(i);
			}
		}
	}

	private void setFontSizeValues() {
		fontSizeOptions.removeAllElements();
		int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
		java.util.List size = new java.util.ArrayList();
		size.add(Integer.valueOf(8));
		size.add(Integer.valueOf(10));
		size.add(Integer.valueOf(12));
		size.add(Integer.valueOf(14));
		size.add(Integer.valueOf(16));
		size.add(Integer.valueOf(20));
		if (fontSize != 8 && fontSize != 10 && fontSize != 12 && fontSize != 14 && fontSize != 16 && fontSize != 20) {
			size.add(Integer.valueOf(fontSize));
		}
		java.util.Collections.sort(size);
		for (int i = 0; i < size.size(); i++) {
			fontSizeOptions.add(String.valueOf(size.get(i)));
		}
	}

	private void initFontSizeComboBox() {
		fontSizeComboBox.removeAllItems();
		String fontSizeString = Configuration.getValue(authoringToolPackage, "fontSize");
		for (int i = 0; i < fontSizeOptions.size(); i++) {
			fontSizeComboBox.addItem(fontSizeOptions.get(i));
			if (fontSizeString.equalsIgnoreCase(fontSizeOptions.get(i).toString())) {
				fontSizeComboBox.setSelectedIndex(i);
			}
		}
	}

	private void setDecimalPlacesValues() {
		decimalPlacesOptions.removeAllElements();
		int decimalPlaces = Integer.parseInt(authoringToolConfig.getValue("decimalPlaces"));
		java.util.List size = new java.util.ArrayList();
		size.add(Integer.valueOf(1));
		size.add(Integer.valueOf(2));
		size.add(Integer.valueOf(3));
		size.add(Integer.valueOf(4));
		size.add(Integer.valueOf(5));
		size.add(Integer.valueOf(6));
		if (decimalPlaces < 1 && decimalPlaces > 6 ) {
			size.add(Integer.valueOf(decimalPlaces));
		}
		java.util.Collections.sort(size);
		for (int i = 0; i < size.size(); i++) {
			decimalPlacesOptions.add(String.valueOf(size.get(i)));
		}
	}

	private void initDecimalPlacesComboBox() {
		decimalPlacesComboBox.removeAllItems();
		String decimalPlacesString = Configuration.getValue(authoringToolPackage, "decimalPlaces");
		for (int i = 0; i < decimalPlacesOptions.size(); i++) {
			decimalPlacesComboBox.addItem(decimalPlacesOptions.get(i));
			if (decimalPlacesString.equalsIgnoreCase(decimalPlacesOptions.get(i).toString())) {
				decimalPlacesComboBox.setSelectedIndex(i);
			}
		}
	}
	// ///////////////
	// Callbacks
	// ///////////////

	// void renderWindowMatchesSceneEditorCheckBox_actionPerformed(ActionEvent
	// e) {
	// boundsXTextField.setEnabled( !
	// renderWindowMatchesSceneEditorCheckBox.isSelected() );
	// boundsYTextField.setEnabled( !
	// renderWindowMatchesSceneEditorCheckBox.isSelected() );
	// boundsWidthTextField.setEnabled( !
	// renderWindowMatchesSceneEditorCheckBox.isSelected() );
	// boundsHeightTextField.setEnabled( !
	// renderWindowMatchesSceneEditorCheckBox.isSelected() );
	// }

	void worldDirectoryBrowseButton_actionPerformed(ActionEvent ev) {
		java.io.File parent = new java.io.File(Configuration.getValue(authoringToolPackage, "directories.worldsDirectory"));// .getParentFile();
		browseFileChooser.setCurrentDirectory(parent);
		int returnVal = browseFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			java.io.File file = browseFileChooser.getSelectedFile();
			worldDirectoryTextField.setText(file.getAbsolutePath());
		}
	}

	void importDirectoryBrowseButton_actionPerformed(ActionEvent ev) {
		java.io.File parent = new java.io.File(Configuration.getValue(authoringToolPackage, "directories.importDirectory")).getParentFile();
		browseFileChooser.setCurrentDirectory(parent);
		int returnVal = browseFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			java.io.File file = browseFileChooser.getSelectedFile();
			importDirectoryTextField.setText(file.getAbsolutePath());
		}
	}

	void browseButton_actionPerformed(ActionEvent e) {
		boolean done = false;
		String finalFilePath = captureDirectoryTextField.getText();
		while (!done) {
			java.io.File parent = new java.io.File(finalFilePath);
			if (!parent.exists()) {
				parent = new java.io.File(Configuration.getValue(authoringToolPackage, "screenCapture.directory"));
			}
			browseFileChooser.setCurrentDirectory(parent);
			int returnVal = browseFileChooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				java.io.File captureDirectoryFile = browseFileChooser.getSelectedFile();
				int directoryCheck = edu.cmu.cs.stage3.io.FileUtilities.isWritableDirectory(captureDirectoryFile);
				if (directoryCheck == edu.cmu.cs.stage3.io.FileUtilities.DIRECTORY_IS_NOT_WRITABLE) {
					done = false;
					edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
									Messages.getString("The_capture_directory_specified_can_not_be_written_to__Please_choose_another_directory_"),
									Messages.getString("Bad_Directory"),
									javax.swing.JOptionPane.INFORMATION_MESSAGE);
				} else if (directoryCheck == edu.cmu.cs.stage3.io.FileUtilities.DIRECTORY_DOES_NOT_EXIST
						|| directoryCheck == edu.cmu.cs.stage3.io.FileUtilities.BAD_DIRECTORY_INPUT) {
					done = false;
					edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
									Messages.getString("The_capture_directory_must_be_a_directory_that_exists_"),
									Messages.getString("Bad_Directory"),
									javax.swing.JOptionPane.INFORMATION_MESSAGE);
				} else {
					finalFilePath = captureDirectoryFile.getAbsolutePath();
					done = true;
				}
			} else {
				finalFilePath = parent.getAbsolutePath();
				done = true;
			}
		}
		captureDirectoryTextField.setText(finalFilePath);
	}

	// void galleryDirectoryBrowseButton_actionPerformed( ActionEvent ev ) {
	// java.io.File parent = new java.io.File( Configuration.getValue(
	// authoringToolPackage, "directories.galleryDirectory" ) ).getParentFile();
	// browseFileChooser.setCurrentDirectory( parent );
	// int returnVal = browseFileChooser.showOpenDialog( this );
	//
	// if( returnVal == JFileChooser.APPROVE_OPTION ) {
	// java.io.File file = browseFileChooser.getSelectedFile();
	// galleryDirectoryTextField.setText( file.getAbsolutePath() );
	// }
	// }

	// void backgroundColorButton_actionPerformed(ActionEvent e) {
	// java.awt.Color color = javax.swing.JColorChooser.showDialog( this,
	// "Background Color", backgroundColorButton.getBackground() );
	// if( color != null ) {
	// backgroundColorButton.setBackground( color );
	// }
	// }

	JPanel generalPanel = new JPanel();
	JPanel renderingPanel = new JPanel();
	JPanel screenGrabPanel = new JPanel();
	JPanel seldomUsedPanel = new JPanel();
	JPanel directoriesPanel = new JPanel();
	JPanel aseImporterPanel = new JPanel();

	JButton okayButton = new JButton();
	JButton cancelButton = new JButton();

	JCheckBox useBorderlessWindowCheckBox = new JCheckBox();
	JCheckBox infiniteBackupsCheckBox = new JCheckBox();
	JTextField importDirectoryTextField = new JTextField();
	java.util.Vector saveIntervalOptions = new java.util.Vector();
	java.util.Vector backupCountOptions = new java.util.Vector();
	java.util.Vector fontSizeOptions = new java.util.Vector();
	java.util.Vector decimalPlacesOptions = new java.util.Vector();
	JLabel importDirectoryLabel = new JLabel();
	JButton importDirectoryBrowseButton = new JButton();
	JCheckBox enableScriptingCheckBox = new JCheckBox();
	JCheckBox doProfilingCheckBox = new JCheckBox();
	JCheckBox runtimeScratchPadEnabledCheckBox = new JCheckBox();
	JCheckBox saveAsSingleFileCheckBox = new JCheckBox();
	JCheckBox watcherPanelEnabledCheckBox = new JCheckBox();
	JComboBox saveIntervalComboBox = new JComboBox();

	JTabbedPane configTabbedPane = new JTabbedPane();

	private void jbInit() {

		this.setLayout(new BorderLayout());
		okayButton.setText(Messages.getString("Okay"));
		cancelButton.setText(Messages.getString("Cancel"));

		Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setBorder(emptyBorder);

		configTabbedPane.setBackground(new Color(204, 204, 204));
		this.add(configTabbedPane, BorderLayout.CENTER);

		GeneralTabInit();
		RenderingTabInit();
		ScreenGrabTabInit();
		SeldomUsedTabInit();
		ASEImportTabInit();

		generalPanel.setBorder(emptyBorder);
		renderingPanel.setBorder(emptyBorder);
		screenGrabPanel.setBorder(emptyBorder);
		seldomUsedPanel.setBorder(emptyBorder);

		configTabbedPane.add(generalPanel, Messages.getString("General"));
		configTabbedPane.add(renderingPanel, Messages.getString("Rendering"));
		// configTabbedPane.add(directoriesPanel, "Directories");
		configTabbedPane.add(screenGrabPanel, Messages.getString("Screen_Grab"));
		// TODO: config for bvw
		// configTabbedPane.add(aseImporterPanel, "ASE Importer");
		configTabbedPane.add(seldomUsedPanel, Messages.getString("Seldom_Used"));

		Box buttonBox;
		buttonBox = Box.createHorizontalBox();
		Component component1;
		component1 = Box.createGlue();
		Component component2;
		component2 = Box.createHorizontalStrut(8);
		Component component3;
		component3 = Box.createGlue();

		buttonBox.add(component1, null);
		buttonBox.add(okayButton, null);
		buttonBox.add(component2, null);
		buttonBox.add(cancelButton, null);
		buttonBox.add(component3, null);
		buttonPanel.add(buttonBox, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

	}

	private JTextField maxRecentWorldsTextField = new JTextField();
	private JLabel maxRecentWorldsLabel = new JLabel();
	private JComboBox resourceFileComboBox = new JComboBox();
	private JComboBox languageComboBox = new JComboBox();
	private JComboBox fontSizeComboBox = new JComboBox();
	private JComboBox decimalPlacesComboBox = new JComboBox();
	private JTextField worldDirectoryTextField = new JTextField();

	private void GeneralTabInit() {
		JPanel maxRecentWorldsPanel = new JPanel();
		JPanel resourcesPanel = new JPanel();
		JPanel languagePanel = new JPanel();
		JPanel inputDirectoriesPanel = new JPanel();
		JPanel fontSizePanel = new JPanel();
		JPanel decimalPlacesPanel = new JPanel();

		maxRecentWorldsTextField.setColumns(3);
		maxRecentWorldsTextField.setMinimumSize(new Dimension(50, 22));
		maxRecentWorldsTextField.setMargin(new Insets(1, 1, 1, 1));
		maxRecentWorldsLabel.setText(Messages.getString("maximum_number_of_worlds_kept_in_the_recent_worlds_menu"));

		maxRecentWorldsPanel.setLayout(new GridBagLayout());
		maxRecentWorldsPanel.add(maxRecentWorldsTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		maxRecentWorldsPanel.add(maxRecentWorldsLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		JLabel resourcesLabel = new JLabel();
		resourcesPanel.setLayout(new GridBagLayout());
		resourcesPanel.add(resourcesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		resourcesPanel.add(resourceFileComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		resourcesLabel.setText(Messages.getString("display_my_program_"));

		java.io.File resourceDirectory = new java.io.File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceHomeDirectory(), "resources" + System.getProperty("file.separator") + AikMin.locale.getDisplayLanguage()).getAbsoluteFile();
		java.io.File[] resourceFiles = resourceDirectory.listFiles(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.resourceFileFilter);
		for (int i = 0; i < resourceFiles.length; i++) {
			resourceFileComboBox.addItem(resourceFiles[i].getName());
		}

		resourceFileComboBox.setRenderer(new javax.swing.ListCellRenderer() {
			public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index,	boolean isSelected, boolean cellHasFocus) {
				javax.swing.JLabel toReturn = new javax.swing.JLabel(Messages.getString("No_Name"));
				toReturn.setOpaque(true);
				if (!AikMin.isLTR())	// ***** For making Combobox items right justify *****
					toReturn.setHorizontalAlignment(SwingConstants.RIGHT);
				// toReturn.setVerticalAlignment(javax.swing.JLabel.CENTER);

				String name = value.toString();
				if (name.equals("Alice Style.py")) {
					name = Messages.getString("Alice_Style");
				} else if (name.equals("Java Style.py")) {
					name = Messages.getString("Java_Style_in_Color");
				} else if (name.equals("Java Text Style.py")) {
					name = Messages.getString("Java_Style_in_Black___White");
				} else {
					int dotIndex = name.lastIndexOf(".");
					if (dotIndex > -1) {
						name = name.substring(0, dotIndex);
					}
				}
				toReturn.setText(name);
				if (isSelected) {
					toReturn.setBackground(list.getSelectionBackground());
					toReturn.setForeground(list.getSelectionForeground());
				} else {
					toReturn.setBackground(list.getBackground());
					toReturn.setForeground(list.getForeground());
				}

				return toReturn;
			}
		});
		resourceFileComboBox.setSelectedItem(Configuration.getValue(authoringToolPackage, "resourceFile"));
		
		// Aik Min - Language
		JLabel languageLabel = new JLabel();
		languagePanel.setLayout(new GridBagLayout());
		languagePanel.add(languageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,	new Insets(5, 5, 5, 5), 0, 0));
		languagePanel.add(languageComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		languageLabel.setText(Messages.getString("display_language_"));
		for (int i=0; i<AikMin.listOfLanguages.length; i++) {
			languageComboBox.addItem(AikMin.listOfLanguages[i]);	
		}
		languageComboBox.setSelectedItem(Configuration.getValue(authoringToolPackage, "language"));
		if (!AikMin.isLTR()) { 	// ***** For making Combobox items right justify *****
			languageComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
	            @Override
	            public void paint(java.awt.Graphics g) {
	                setHorizontalAlignment(SwingConstants.RIGHT);
	                super.paint(g);
	            }
	        });
		}
		JLabel worldDirectoryLabel = new JLabel();
		JButton worldDirectoryBrowseButton = new JButton();
		worldDirectoryTextField.setColumns(15);
		inputDirectoriesPanel.setLayout(new GridBagLayout());
		inputDirectoriesPanel.add(worldDirectoryLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		inputDirectoriesPanel.add(worldDirectoryTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		inputDirectoriesPanel.add(worldDirectoryBrowseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		// inputDirectoriesPanel.add(importDirectoryLabel, new
		// GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4,
		// 4, 4), 0, 0));
		// inputDirectoriesPanel.add(importDirectoryTextField, new
		// GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
		// ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4,
		// 4, 4), 0, 0));
		// inputDirectoriesPanel.add(importDirectoryBrowseButton, new
		// GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 4,
		// 4, 4), 0, 0));
		// inputDirectoriesPanel.add(characterDirectoryLabel, new
		// GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 4,
		// 4, 4), 0, 0));
		// inputDirectoriesPanel.add(characterDirectoryTextField, new
		// GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
		// ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4,
		// 4, 4), 0, 0));
		// inputDirectoriesPanel.add(characterDirectoryBrowseButton, new
		// GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 4,
		// 4, 4), 0, 0));

		worldDirectoryLabel.setText(Messages.getString("save_and_load_from_"));
		worldDirectoryBrowseButton.setText(Messages.getString("Browse___"));
		worldDirectoryBrowseButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						worldDirectoryBrowseButton_actionPerformed(e);
					}
				});

		JLabel fontSizeLabel = new JLabel();

		fontSizePanel.setLayout(new GridBagLayout());
		fontSizePanel.add(fontSizeComboBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		fontSizePanel.add(fontSizeLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,	new Insets(5, 5, 5, 5), 0, 0));
		if (!AikMin.isLTR()) {		// ***** For making Combobox items right justify *****
			fontSizeComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
	            @Override
	            public void paint(java.awt.Graphics g) {
	                setHorizontalAlignment(SwingConstants.RIGHT);
	                super.paint(g);
	            }
	        });
		}
		fontSizeComboBox.setEditable(true);
		fontSizeComboBox.setPreferredSize(new java.awt.Dimension(55, 25));
		fontSizeComboBox.setMaximumRowCount(9);

		fontSizeLabel.setText(" " + Messages.getString("general_font_size__default_value_is_12_"));

		JLabel decimalPlacesLabel = new JLabel();
		
		decimalPlacesPanel.setLayout(new GridBagLayout());
		decimalPlacesPanel.add(decimalPlacesComboBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		decimalPlacesPanel.add(decimalPlacesLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,	new Insets(5, 5, 5, 5), 0, 0));
		if (!AikMin.isLTR()) {		// ***** For making Combobox items right justify *****
			decimalPlacesComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
	            @Override
	            public void paint(java.awt.Graphics g) {
	                setHorizontalAlignment(SwingConstants.RIGHT);
	                super.paint(g);
	            }
	        });
		}
		decimalPlacesComboBox.setEditable(true);
		decimalPlacesComboBox.setPreferredSize(new java.awt.Dimension(55, 25));
		decimalPlacesComboBox.setMaximumRowCount(9);

		decimalPlacesLabel.setText(" " + Messages.getString("decimal_places__default_value_is_2_"));
		
		Component component = Box.createGlue();
		// generalPanel.setBorder(emptyBorder);
		generalPanel.setLayout(new GridBagLayout());
		generalPanel.add(maxRecentWorldsPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,	new Insets(0, 0, 0, 0), 0, 0));
		// generalPanel.add(backgroundColorPanel, new GridBagConstraints(0, 2,
		// 1, 1, 1.0, 0.0
		// ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new
		// Insets(4, 0, 4, 0), 0, 0));
		generalPanel.add(resourcesPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		generalPanel.add(languagePanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		generalPanel.add(inputDirectoriesPanel, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		generalPanel.add(fontSizePanel, new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		generalPanel.add(decimalPlacesPanel, new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		generalPanel.add(component, new GridBagConstraints(0, 8, 1, 1, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		// generalPanel.add(watcherPanelEnabledCheckBox, new
		// GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new
		// Insets(0, 0, 0, 0), 0, 0));
		// generalPanel.add(scriptTypeInEnabledCheckBox, new
		// GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new
		// Insets(0, 0, 0, 0), 0, 0));
	}

	private JCheckBox forceSoftwareRenderingCheckBox = new JCheckBox();
	private JCheckBox showFPSCheckBox = new JCheckBox();
	private JCheckBox deleteFiles = new JCheckBox(); // Aik Min added this.
	private JTextField boundsXTextField = new JTextField();
	private JTextField boundsYTextField = new JTextField();
	private JTextField boundsWidthTextField = new JTextField();
	private JTextField boundsHeightTextField = new JTextField();
	private JCheckBox constrainRenderDialogAspectCheckBox = new JCheckBox();
	private JCheckBox ensureRenderDialogIsOnScreenCheckBox = new JCheckBox();
	private JList rendererList = new JList();

	private void RenderingTabInit() {
		JPanel renderWindowBoundsPanel = new JPanel();
		JLabel rendererListLabel = new JLabel();
		JButton rendererMoveUpButton = new JButton();
		JButton rendererMoveDownButton = new JButton();

		forceSoftwareRenderingCheckBox.setText(" " + Messages.getString("force_software_rendering__slower_and_safer_"));
		showFPSCheckBox.setText(" " + Messages.getString("show_frames_per_second"));
		deleteFiles.setText(" " + Messages.getString("delete_frames_folder_after_exporting_video")); // Aik Min added this.
		// renderWindowBoundsPanel.setAlignmentX((float) 0.0);
		// renderWindowBoundsPanel.setMaximumSize(new Dimension(32767, 125));
		// renderWindowBoundsPanel.setPreferredSize(new Dimension(300, 125));
		// renderWindowBoundsPanel.setLayout(null);

		JLabel renderWindowBoundsLabel = new JLabel();
		JLabel boundsWidthLabel = new JLabel();
		JLabel boundsHeightLabel = new JLabel();
		JLabel boundsXLabel = new JLabel();
		JLabel boundsYLabel = new JLabel();

		renderWindowBoundsLabel.setText(Messages.getString("render_window_position_and_size_"));

		//boundsXLabel.setHorizontalAlignment(SwingConstants.CENTER);
		boundsXLabel.setText(Messages.getString("horizontal_position_"));
		boundsXTextField.setColumns(5);
		boundsXTextField.setMinimumSize(new Dimension(61, 22));
		boundsXTextField.setMargin(new Insets(1, 1, 1, 1));
		boundsXTextField.getDocument().addDocumentListener(renderDialogBoundsChecker);

		//boundsYLabel.setHorizontalAlignment(SwingConstants.CENTER);
		boundsYLabel.setText(Messages.getString("vertical_position_"));
		boundsYTextField.setColumns(5);
		boundsYTextField.setMinimumSize(new Dimension(61, 22));
		boundsYTextField.setMargin(new Insets(1, 1, 1, 1));
		boundsYTextField.getDocument().addDocumentListener(renderDialogBoundsChecker);

		//boundsWidthLabel.setHorizontalAlignment(SwingConstants.CENTER);
		boundsWidthLabel.setText(Messages.getString("width_"));
		boundsWidthTextField.setColumns(5);
		boundsWidthTextField.setMinimumSize(new Dimension(61, 22));
		boundsWidthTextField.setMargin(new Insets(1, 1, 1, 1));
		boundsWidthTextField.getDocument().addDocumentListener(renderDialogWidthChecker);

		boundsHeightLabel.setText(Messages.getString("height_"));
		boundsHeightTextField.setColumns(5);
		boundsHeightTextField.setMinimumSize(new Dimension(61, 22));
		boundsHeightTextField.setMargin(new Insets(1, 1, 1, 1));
		boundsHeightTextField.getDocument().addDocumentListener(renderDialogHeightChecker);

		renderWindowBoundsPanel.setLayout(new GridBagLayout());
		renderWindowBoundsPanel.add(renderWindowBoundsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,	new Insets(3, 5, 3, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsXLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsXTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsYLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsYTextField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsWidthLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsWidthTextField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsHeightLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsHeightTextField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));

		constrainRenderDialogAspectCheckBox.setText(" " + Messages.getString("constrain_render_window_s_aspect_ratio"));
		constrainRenderDialogAspectCheckBox.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent ae) {
						checkAndUpdateRenderBounds();
					}
				});

		ensureRenderDialogIsOnScreenCheckBox.setText(" " + Messages.getString("make_sure_the_render_window_is_always_on_the_screen"));

		rendererListLabel.setText(Messages.getString("renderer_order__top_item_will_be_tried_first__bottom_item_will_be_tried_last__"));

		rendererList.setModel(new ConfigListModel(authoringToolPackage,	"rendering.orderedRendererList"));
		rendererList.setSelectedIndex(0);
		rendererList.setBorder(BorderFactory.createLineBorder(Color.black));
		rendererList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		rendererMoveUpButton.setText(Messages.getString("move_up"));
		rendererMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Object selectedItem = rendererList.getSelectedValue();
						((ConfigListModel) rendererList.getModel()).moveIndexHigher(rendererList.getSelectedIndex());
						rendererList.setSelectedValue(selectedItem, false);
					}
				});
		rendererMoveDownButton.setText(Messages.getString("move_down"));
		rendererMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Object selectedItem = rendererList.getSelectedValue();
						((ConfigListModel) rendererList.getModel()).moveIndexLower(rendererList.getSelectedIndex());
						rendererList.setSelectedValue(selectedItem, false);
					}
				});

		Component component = Box.createGlue();
		renderingPanel.setLayout(new GridBagLayout());
		// renderingPanel.setBorder(emptyBorder);

		renderingPanel.add(forceSoftwareRenderingCheckBox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		renderingPanel.add(showFPSCheckBox, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		// Aik Min added this.
		renderingPanel.add(deleteFiles, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		renderingPanel.add(renderWindowBoundsPanel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		renderingPanel.add(constrainRenderDialogAspectCheckBox, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		renderingPanel.add(ensureRenderDialogIsOnScreenCheckBox, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		renderingPanel.add(rendererListLabel, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
		renderingPanel.add(rendererList, new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		renderingPanel.add(rendererMoveUpButton, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		renderingPanel.add(rendererMoveDownButton, new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		renderingPanel.add(component, new GridBagConstraints(0, 9, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	JTextField captureDirectoryTextField = new JTextField();
	private JTextField baseNameTextField = new JTextField();
	private JComboBox numDigitsComboBox = new JComboBox();
	private JComboBox codecComboBox = new JComboBox();
	private JCheckBox screenCaptureInformUserCheckBox = new JCheckBox();

	private void ScreenGrabTabInit() {
		JLabel captureDirectory = new JLabel();
		captureDirectory.setText(Messages.getString("directory_to_capture_to_"));
		captureDirectoryTextField.getDocument().addDocumentListener(
				captureDirectoryChangeListener);
		captureDirectoryTextField.setColumns(15);

		JButton browseButton = new JButton();
		browseButton.setText(Messages.getString("Browse___"));
		browseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseButton_actionPerformed(e);
			}
		});

		JLabel baseNameLabel = new JLabel();
		baseNameLabel.setText(Messages.getString("base_filename_"));

		baseNameTextField.setMinimumSize(new Dimension(100, 28));
		baseNameTextField.setPreferredSize(new Dimension(100, 28));

		JLabel numDigitsLabel = new JLabel();
		numDigitsLabel.setText(Messages.getString("number_of_digits_to_append_"));
		numDigitsComboBox.setPreferredSize(new java.awt.Dimension(60, 25));
		numDigitsComboBox.addItem("1");
		numDigitsComboBox.addItem("2");
		numDigitsComboBox.addItem("3");
		numDigitsComboBox.addItem("4");
		numDigitsComboBox.addItem("5");
		numDigitsComboBox.addItem("6");
		if (!AikMin.isLTR()) {		// ***** For making Combobox items right justify *****
			numDigitsComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
	            @Override
	            public void paint(java.awt.Graphics g) {
	                setHorizontalAlignment(SwingConstants.RIGHT);
	                super.paint(g);
	            }
	        });
		}
		
		JLabel codecLabel = new JLabel();
		codecLabel.setText(Messages.getString("image_format_"));
		codecComboBox.setPreferredSize(new java.awt.Dimension(60, 25));
		codecComboBox.addItem("jpeg");
		codecComboBox.addItem("png");
		if (!AikMin.isLTR()) {		// ***** For making Combobox items right justify *****
			codecComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
	            @Override
	            public void paint(java.awt.Graphics g) {
	                setHorizontalAlignment(SwingConstants.RIGHT);
	                super.paint(g);
	            }
	        });
		}
		screenCaptureInformUserCheckBox.setText(" " + Messages.getString("show_information_dialog_when_capture_is_made"));

		JLabel usageLabel = new JLabel();
		usageLabel.setText(Messages.getString("Note__use_Ctrl_G_to_grab_a_frame_while_the_world_is_running_"));

		screenGrabPanel.setLayout(new GridBagLayout());

		Component component = Box.createGlue();
		screenGrabPanel.add(captureDirectory, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(captureDirectoryTextField, new GridBagConstraints( 1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(browseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(baseNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(baseNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(numDigitsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(numDigitsComboBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(codecLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(codecComboBox, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		screenGrabPanel.add(screenCaptureInformUserCheckBox, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		screenGrabPanel.add(component, new GridBagConstraints(0, 5, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		screenGrabPanel.add(usageLabel, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	}

	private JCheckBox showStartUpDialogCheckBox = new JCheckBox();
	private JCheckBox showWebWarningCheckBox = new JCheckBox();
	private JCheckBox loadSavedTabsCheckBox = new JCheckBox();
	private JCheckBox pickUpTilesCheckBox = new JCheckBox();
	private JCheckBox useAlphaTilesCheckBox = new JCheckBox();
	private JCheckBox saveThumbnailWithWorldCheckBox = new JCheckBox();
	private JCheckBox showWorldStatsCheckBox = new JCheckBox();
	private JCheckBox clearStdOutOnRunCheckBox = new JCheckBox();
	private JCheckBox enableHighContrastCheckBox = new JCheckBox();
	private JCheckBox enableLoggingCheckBox = new JCheckBox();
	private JCheckBox disableTooltipCheckBox = new JCheckBox();
	private JTextField numClipboardsTextField = new JTextField();
	private JComboBox backupCountComboBox = new JComboBox();
	private JCheckBox showBuilderCheckBox = new JCheckBox();

	private void SeldomUsedTabInit() {
		JPanel saveIntervalPanel = new JPanel();
		JPanel backupCountPanel = new JPanel();

		showStartUpDialogCheckBox.setText(" " + Messages.getString("show_startup_dialog_when_Alice_launches"));
		showWebWarningCheckBox.setText(" " + Messages.getString("show_warning_when_browsing_the_web_gallery"));
		loadSavedTabsCheckBox.setText(" " + Messages.getString("open_tabs_that_were_previously_open_on_world_load"));
		pickUpTilesCheckBox.setText(" " + Messages.getString("pick_up_tiles_while_dragging_and_dropping__reduces_performance_"));
		useAlphaTilesCheckBox.setText(" " + Messages.getString("use_alpha_blending_in_picked_up_tiles__really_reduces_performance_"));
		saveThumbnailWithWorldCheckBox.setText(" " + Messages.getString("save_thumbnail_with_world"));
		showWorldStatsCheckBox.setText(" " + Messages.getString("show_world_statistics"));
		clearStdOutOnRunCheckBox.setText(" " + Messages.getString("clear_text_output_on_play"));
		enableHighContrastCheckBox.setText(" " + Messages.getString("enable_high_contrast_mode_for_projectors"));
		enableLoggingCheckBox.setText(" " + Messages.getString("enable_logging"));
		disableTooltipCheckBox.setText(" " + Messages.getString("disable_tooltip"));
		showBuilderCheckBox.setText(" " + Messages.getString("show_he_she_builder_in_gallery"));
		
		JLabel numClipboardsLabel = new JLabel();
		numClipboardsTextField.setColumns(3);
		numClipboardsTextField.setMinimumSize(new Dimension(50, 22));
		numClipboardsTextField.setMargin(new Insets(1, 1, 1, 1));
		numClipboardsLabel.setText(Messages.getString("number_of_clipboards"));

		JPanel numClipboardsPanel = new JPanel();
		numClipboardsPanel.setLayout(new GridBagLayout());
		numClipboardsPanel.add(numClipboardsTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		numClipboardsPanel.add(numClipboardsLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		JLabel saveIntervalLabelEnd = new JLabel();
		saveIntervalComboBox.setEditable(true);
		saveIntervalComboBox.setPreferredSize(new java.awt.Dimension(80, 25));
		saveIntervalLabelEnd.setText(" " + Messages.getString("number_of_minutes_to_wait_before_displaying_save_reminder"));
		if (!AikMin.isLTR()) {		// ***** For making Combobox items right justify *****
			saveIntervalComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
	            @Override
	            public void paint(java.awt.Graphics g) {
	                setHorizontalAlignment(SwingConstants.RIGHT);
	                super.paint(g);
	            }
	        });
		}

		saveIntervalPanel.setOpaque(false);
		saveIntervalPanel.setBorder(null);
		saveIntervalPanel.add(saveIntervalComboBox);
		saveIntervalPanel.add(saveIntervalLabelEnd);

		JLabel backupCountLabel = new JLabel();
		backupCountComboBox.setEditable(true);
		backupCountComboBox.setPreferredSize(new java.awt.Dimension(80, 25));
		backupCountLabel.setText(" " + Messages.getString("number_of_backups_of_each_world_to_save"));
		backupCountComboBox.setMaximumRowCount(9);
		if (!AikMin.isLTR()) {		// ***** For making Combobox items right justify *****
			backupCountComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
	            @Override
	            public void paint(java.awt.Graphics g) {
	                setHorizontalAlignment(SwingConstants.RIGHT);
	                super.paint(g);
	            }
	        });
		}

		backupCountPanel.setOpaque(false);
		backupCountPanel.setBorder(null);
		backupCountPanel.add(backupCountComboBox);
		backupCountPanel.add(backupCountLabel);

		// seldomUsedPanel.setBorder(emptyBorder);
		seldomUsedPanel.setLayout(new GridBagLayout());

		seldomUsedPanel.add(showStartUpDialogCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(showWebWarningCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(loadSavedTabsCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(pickUpTilesCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(useAlphaTilesCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(saveThumbnailWithWorldCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(showWorldStatsCheckBox, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		// seldomUsedPanel.add(saveAsSingleFileCheckBox, new
		// GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0,
		// 0, 0), 0, 0));
		seldomUsedPanel.add(clearStdOutOnRunCheckBox, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(enableHighContrastCheckBox, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(enableLoggingCheckBox, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(enableScriptingCheckBox, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));	
		seldomUsedPanel.add(disableTooltipCheckBox, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(showBuilderCheckBox, new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		seldomUsedPanel.add(numClipboardsPanel, new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(saveIntervalPanel, new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		seldomUsedPanel.add(backupCountPanel, new GridBagConstraints(0, 15, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		// seldomUsedPanel.add(runtimeScratchPadEnabledCheckBox, new
		// GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new
		// Insets(0, 0, 0, 0), 0, 0));
		// seldomUsedPanel.add(reloadWorldScriptCheckBox, new
		// GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new
		// Insets(0, 0, 0, 0), 0, 0));
		// seldomUsedPanel.add(infiniteBackupsCheckBox, new
		// GridBagConstraints(0, 12, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new
		// Insets(0, 0, 0, 0), 0, 0));
		// seldomUsedPanel.add(printingFillBackgroundCheckBox, new
		// GridBagConstraints(0, 13, 1, 1, 0.0, 0.0
		// ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0,
		// 0, 0), 0, 0));
		// seldomUsedPanel.add(printingScalePanel, new GridBagConstraints(0, 14,
		// 1, 1, 1.0, 0.0
		// ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0,
		// 0, 0), 0, 0));
		seldomUsedPanel.add(javax.swing.Box.createVerticalGlue(), new GridBagConstraints(0, 16, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		if (Configuration.getValue(authoringToolPackage, "disableTooltipMode").equalsIgnoreCase("true")) {
			javax.swing.ToolTipManager.sharedInstance().setEnabled(false);
		} else {
			javax.swing.ToolTipManager.sharedInstance().setEnabled(true);
		}
	}

	JCheckBox createNormalsCheckBox = new JCheckBox();
	JCheckBox createUVsCheckBox = new JCheckBox();
	JCheckBox useSpecularCheckBox = new JCheckBox();
	JCheckBox groupMultipleRootObjectsCheckBox = new JCheckBox();
	JCheckBox colorToWhiteWhenTexturedCheckBox = new JCheckBox();

	private void ASEImportTabInit() {
		Border emptyBorder;
		emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

		createNormalsCheckBox.setText(Messages.getString("create_normals_if_none_exist"));
		createUVsCheckBox.setText(Messages.getString("create_uv_coordinates_if_none_exist"));
		useSpecularCheckBox.setText(Messages.getString("use_specular_information_if_given_in_ASE_file"));
		groupMultipleRootObjectsCheckBox.setText(Messages.getString("group_multiple_root_objects"));
		colorToWhiteWhenTexturedCheckBox.setText(Messages.getString("set_ambient_and_diffuse_color_to_white_if_object_is_textured"));

		Box aseImporterBox;
		aseImporterBox = Box.createVerticalBox();
		aseImporterBox.add(createNormalsCheckBox, null);
		aseImporterBox.add(createUVsCheckBox, null);
		aseImporterBox.add(useSpecularCheckBox, null);
		aseImporterBox.add(groupMultipleRootObjectsCheckBox, null);
		aseImporterBox.add(colorToWhiteWhenTexturedCheckBox, null);

		aseImporterPanel.setLayout(new BorderLayout());
		aseImporterPanel.setBorder(emptyBorder);
		aseImporterPanel.add(aseImporterBox, BorderLayout.CENTER);
	}

}
