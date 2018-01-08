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

package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.lang.Messages;

/**
 * @author Jason Pratt
 */
public class Actions {
	public javax.swing.AbstractAction newWorldAction;
	public javax.swing.AbstractAction openWorldAction;
	public javax.swing.AbstractAction openExampleWorldAction;
	public javax.swing.AbstractAction saveWorldAction;
	public javax.swing.AbstractAction saveWorld;
	public javax.swing.AbstractAction saveWorldAsAction;
	public javax.swing.AbstractAction saveForWebAction;
	public javax.swing.AbstractAction importObjectAction;
	public javax.swing.AbstractAction quitAction;
	public javax.swing.AbstractAction cutAction;
	public javax.swing.AbstractAction copyAction;
	public javax.swing.AbstractAction pasteAction;
	public javax.swing.AbstractAction undoAction;
	public javax.swing.AbstractAction redoAction;
	public javax.swing.AbstractAction aboutAction;
	public javax.swing.AbstractAction playAction;
	public javax.swing.AbstractAction addCharacterAction;
	public javax.swing.AbstractAction add3DTextAction;
	public javax.swing.AbstractAction exportMovieAction;
	public javax.swing.AbstractAction trashAction;
	public javax.swing.AbstractAction helpAction;
	public javax.swing.AbstractAction onScreenHelpAction;
	public javax.swing.AbstractAction preferencesAction;
	public javax.swing.AbstractAction makeBillboardAction;
	public javax.swing.AbstractAction showWorldInfoAction;
	public javax.swing.AbstractAction launchTutorialAction;
	public javax.swing.AbstractAction launchTutorialFileAction;
	public javax.swing.AbstractAction launchTutorialEditor;			// Aik Min
	public javax.swing.AbstractAction launchSoftwareUpdate;			// Aik Min
	public javax.swing.AbstractAction showStdOutDialogAction;
	public javax.swing.AbstractAction showStdErrDialogAction;
	public javax.swing.AbstractAction showPrintDialogAction;
	public javax.swing.AbstractAction pauseWorldAction;
	public javax.swing.AbstractAction resumeWorldAction;
	public javax.swing.AbstractAction restartWorldAction;
	public javax.swing.AbstractAction stopWorldAction;
	public javax.swing.AbstractAction takePictureAction;
	public javax.swing.AbstractAction restartStopWorldAction;
	public javax.swing.AbstractAction logInstructorIntervention;	// Logging
	public javax.swing.AbstractAction licenseAction;
	
	protected AuthoringTool authoringTool;
	protected JAliceFrame jAliceFrame;
	protected java.util.LinkedList applicationActions = new java.util.LinkedList();
	public java.util.LinkedList renderActions = new java.util.LinkedList();

	public Actions( AuthoringTool authoringTool, JAliceFrame jAliceFrame ) {
		this.authoringTool = authoringTool;
		this.jAliceFrame = jAliceFrame;
		actionInit();
		keyInit();
		undoAction.setEnabled( false );
		redoAction.setEnabled( false );
	}

	private void actionInit() {
		newWorldAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.newWorld();
			}
		};

		openWorldAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.openWorld();
			}
		};

		openExampleWorldAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.openExampleWorld();
			}
		};

		saveWorldAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.saveWorld();
			}
		};

		saveWorld = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.saveWorld();
			}
		};
		
		saveWorldAsAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.saveWorldAs();
			}
		};

		saveForWebAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.saveForWeb();
			}
		};

		importObjectAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.getImportFileChooser().setFileFilter( authoringTool.getImportFileChooser().getAcceptAllFileFilter() );
				authoringTool.importElement();
			}
		};

		quitAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.quit(false);
			}
		};

		cutAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				//TODO - Ignore
			}
		};

		copyAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				//TODO - Ignore
			}
		};

		pasteAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				//TODO - Ignore
			}
		};

		undoAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.getUndoRedoStack().undo();
			}
		};

		redoAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.getUndoRedoStack().redo();
			}
		};

		aboutAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showAbout();
			}
		};

		playAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.play();
			}
		};

		addCharacterAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.loadAndAddCharacter();
			}
		};

		add3DTextAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.add3DText();
			}
		};

		exportMovieAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.exportMovie();
			}
		};
		
		trashAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				//TODO - Ignore
			}
		};

		helpAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
			}
		};

		onScreenHelpAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showOnScreenHelp();
			}
		};

		preferencesAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showPreferences();
			}
		};

		makeBillboardAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.makeBillboard();
			}
		};

		showWorldInfoAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showWorldInfoDialog();
			}
		};

		launchTutorialAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.launchTutorial();
			}
		};

		launchTutorialFileAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.openTutorialWorld();
			}
		};
		
		launchTutorialEditor = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.openTutorialEditor();
			}
		};
		
		launchSoftwareUpdate = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.updateAlice();
			}
		};

		showStdOutDialogAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showStdOutDialog();
			}
		};

		showStdErrDialogAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showStdErrDialog();
			}
		};

		showPrintDialogAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showPrintDialog();
			}
		};

		pauseWorldAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.pause();
			}
		};

		resumeWorldAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.resume();
			}
		};

		restartWorldAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.restartWorld();
			}
		};

		restartStopWorldAction = new javax.swing.AbstractAction(){
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.restartWorld();
				authoringTool.pause();
			}
		
		};
		
		stopWorldAction = new javax.swing.AbstractAction() {
			public void actionPerformed( final java.awt.event.ActionEvent e ) {
				//authoringTool.stopWorld();
			}
		};

		takePictureAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				takePictureAction.setEnabled(false);
				authoringTool.takePicture();
				takePictureAction.setEnabled(true);
			}
		};	
		
		//logging instructor intervention
		logInstructorIntervention = new javax.swing.AbstractAction() {
			 public void actionPerformed( java.awt.event.ActionEvent e ) {
				 authoringTool.logInstructorIntervention();
			 }		
		};	

		licenseAction = new javax.swing.AbstractAction() {
			 public void actionPerformed( java.awt.event.ActionEvent e ) {
				 authoringTool.showLicense();
				 //authoringTool.openTutorialEditor(); 
			 }		
		};
		
		newWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK) );
		newWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "newWorld" ); 
		newWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'N' ) );
		newWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("New_World") ); 
		newWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Create_a_new_world") ); 
		newWorldAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "new" ) ); 
		applicationActions.add( newWorldAction );

		openWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK) );
		openWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "openWorld" ); 
		openWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'O' ) );
		openWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Open_World___") ); 
		openWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Open_an_existing_world") ); 
		openWorldAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "open" ) ); 
		applicationActions.add( openWorldAction );

		saveWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "saveWorld" ); 
		saveWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'S' ) );
		saveWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Save_World") ); 
		saveWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Save_the_current_world") ); 
		saveWorldAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) ); 
		applicationActions.add( saveWorldAction );

		saveWorld.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0) );
		saveWorld.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "saveWorld" ); 
		saveWorld.putValue( javax.swing.Action.NAME, Messages.getString("Save_World") );	 
		applicationActions.add( saveWorld );
		
		//saveWorldAsAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveWorldAsAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "saveWorldAs" ); 
		saveWorldAsAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'v' ) );
		saveWorldAsAction.putValue( javax.swing.Action.NAME, Messages.getString("Save_World_As___") ); 
		saveWorldAsAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Save_the_current_world") ); 
		//saveWorldAsAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) );
		applicationActions.add( saveWorldAsAction );

		//saveForWebAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveForWebAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "saveForWeb" ); 
		saveForWebAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'w' ) );
		saveForWebAction.putValue( javax.swing.Action.NAME, Messages.getString("Export_As_A_Web_Page___") ); 
		saveForWebAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Export_as_a_web_page") ); 
		//saveForWebAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) );
		applicationActions.add( saveForWebAction );

		//importObjectAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		importObjectAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "importObject" ); 
		importObjectAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'I' ) );
		importObjectAction.putValue( javax.swing.Action.NAME, Messages.getString("Import___") ); 
		importObjectAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Import") ); 
		importObjectAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "import" ) ); 
		applicationActions.add( importObjectAction );

		//quitAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		quitAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "quit" ); 
		quitAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'x' ) );
		quitAction.putValue( javax.swing.Action.NAME, Messages.getString("Exit") ); 
		quitAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Exit_Alice") ); 
		//quitAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( quitAction );

		cutAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.Event.CTRL_MASK) );
		cutAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "cut" ); 
		cutAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 't' ) );
		cutAction.putValue( javax.swing.Action.NAME, Messages.getString("Cut") ); 
		cutAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Cut") ); 
		cutAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "cut" ) ); 
		applicationActions.add( cutAction );

		copyAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK) );
		copyAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "copy" ); 
		copyAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'C' ) );
		copyAction.putValue( javax.swing.Action.NAME, Messages.getString("Copy") ); 
		copyAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Copy") ); 
		copyAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "copy" ) ); 
		applicationActions.add( copyAction );

		pasteAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK) );
		pasteAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "paste" ); 
		pasteAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		pasteAction.putValue( javax.swing.Action.NAME, Messages.getString("Paste") ); 
		pasteAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Paste") ); 
		pasteAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "paste" ) ); 
		applicationActions.add( pasteAction );

		undoAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.Event.CTRL_MASK) );
		undoAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "undo" ); 
		undoAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'U' ) );
		undoAction.putValue( javax.swing.Action.NAME, Messages.getString("Undo") ); 
		undoAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Undo_the_Last_Action") ); 
		undoAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "undo" ) ); 
		applicationActions.add( undoAction );

		redoAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.Event.CTRL_MASK) );
		redoAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "redo" ); 
		redoAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'R' ) );
		redoAction.putValue( javax.swing.Action.NAME, Messages.getString("Redo") ); 
		redoAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Redo") ); 
		redoAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "redo" ) ); 
		applicationActions.add( redoAction );

		//aboutAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		aboutAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "about" ); 
		aboutAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'A' ) );
		aboutAction.putValue( javax.swing.Action.NAME, Messages.getString("About_Alice") ); 
		aboutAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("About_Alice") ); 
		aboutAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "about" ) ); 
		applicationActions.add( aboutAction );

		//onScreenHelpAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		onScreenHelpAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "onScreenHelp" ); 
		onScreenHelpAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'O' ) );
		onScreenHelpAction.putValue( javax.swing.Action.NAME, Messages.getString("On_Screen_Help__experimental_") ); 
		onScreenHelpAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Experimental_Tutorial_Editor") ); 
		//onScreenHelpAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "onScreenHelp" ) );
		applicationActions.add( onScreenHelpAction );

		String justify = "left";
		if (!AikMin.isLTR())	// ***** Right justify tooltip text for Arabic  *****
			justify = "right";
		
		playAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0) );// java.awt.Event.CTRL_MASK) );
		playAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "play" ); 
		//playAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		playAction.putValue( javax.swing.Action.NAME, Messages.getString("Play") ); 
		playAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "<html><body><div align="+justify+">" + Messages.getString("Play_the_world_") + "<p><p>" + Messages.getString("Opens_the_play_window_and_p_starts_the_world_running_") + "</div></body></html>"); 
		playAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "play" ) ); 
		applicationActions.add( playAction );

		//addCharacterAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		addCharacterAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "addObject" ); 
		//addCharacterAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'A' ) );
		addCharacterAction.putValue( javax.swing.Action.NAME, Messages.getString("Add_Object___") ); 
		addCharacterAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Add_a_previously_stored_Object") ); 
		//addCharacterAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( addCharacterAction );

		//add3DTextAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		add3DTextAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "add3DText" ); 
		//add3DTextAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'A' ) );
		add3DTextAction.putValue( javax.swing.Action.NAME, Messages.getString("Add_3D_Text___") ); 
		add3DTextAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Add_Text_extruded_into_3D") ); 
		//add3DTextAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( add3DTextAction );
		
		exportMovieAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0) );
	    exportMovieAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "exportVideo" ); 
		exportMovieAction.putValue( javax.swing.Action.NAME, Messages.getString("Export_Video___") ); 
		exportMovieAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Export_the_current_world_as_a_video") ); 
	  	applicationActions.add( exportMovieAction );

		//trashAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		trashAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "trash" ); 
		//trashAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		//trashAction.putValue( javax.swing.Action.NAME, "Trash" );
		trashAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "<html><body><div align="+justify+">" + Messages.getString("Trash") + "<p><p>" + Messages.getString("Drag_and_drop_tiles_here_to_delete_them_") + "</div></body></html>"); 
		//trashAction.putValue( javax.swing.Action.SMALL_ICON,   );
		applicationActions.add( trashAction );

		//openExampleWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		openExampleWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "openExampleWorld" ); 
		//openExampleWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		openExampleWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Example_Worlds") ); 
		openExampleWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Open_an_Example_World") ); 
		//openExampleWorldAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( openExampleWorldAction );

		//helpAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		helpAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "help" ); 
		helpAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'H' ) );
		helpAction.putValue( javax.swing.Action.NAME, Messages.getString("Help_Topics") ); 
		helpAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Alice_Documentation") ); 
		//helpAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( helpAction );

		preferencesAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F8, 0 ) );
		preferencesAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "preferences" ); 
		preferencesAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		preferencesAction.putValue( javax.swing.Action.NAME, Messages.getString("Preferences") ); 
		preferencesAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Set_Preferences") ); 
		//preferencesAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( preferencesAction );

		makeBillboardAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_B, java.awt.Event.CTRL_MASK ) );
		makeBillboardAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "makeBillboard" ); 
		makeBillboardAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'B' ) );
		makeBillboardAction.putValue( javax.swing.Action.NAME, Messages.getString("Make_Billboard___") ); 
		makeBillboardAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Make_a_billboard_object_from_an_image") ); 
		//makeBillboardAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( makeBillboardAction );

		//showWorldInfoAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		showWorldInfoAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "showWorldInfo" ); 
		showWorldInfoAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'I' ) );
		showWorldInfoAction.putValue( javax.swing.Action.NAME, Messages.getString("World_Statistics") ); 
		showWorldInfoAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Show_information_about_the_current_world") ); 
		//showWorldInfoAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( showWorldInfoAction );

		//launchTutorialAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		launchTutorialAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "launchTutorial" ); 
		launchTutorialAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		launchTutorialAction.putValue( javax.swing.Action.NAME, Messages.getString("Teach_Me") ); 
		launchTutorialAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Launch_the_Tutorial") ); 
		//launchTutorialAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( launchTutorialAction );

		launchTutorialFileAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		launchTutorialFileAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "launchTutorialFile" ); 
		launchTutorialFileAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		launchTutorialFileAction.putValue( javax.swing.Action.NAME, Messages.getString("Tutorial") ); 
		launchTutorialFileAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Open_a_tutorial") ); 
		//launchTutorialFileAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( launchTutorialFileAction );

		//launchTutorialEditor.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_T, java.awt.Event.CTRL_MASK ) );
		launchTutorialEditor.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "launchTutorialEditor" ); 
		//launchTutorialEditor.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		launchTutorialEditor.putValue( javax.swing.Action.NAME, "Tutorial Editor" ); 
		launchTutorialEditor.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Create or edit a tutorial" ); 
		//launchTutorialFileAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( launchTutorialEditor );
		
    	//launchSoftwareUpdate.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		launchSoftwareUpdate.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "launchSoftwareUpdate" );
		//launchSoftwareUpdate.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'U' ) );
		launchSoftwareUpdate.putValue( javax.swing.Action.NAME, Messages.getString("Update_Software") );
		launchSoftwareUpdate.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Update_Software") );
		//launchSoftwareUpdate.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( launchSoftwareUpdate );	
    
		//showStdOutDialogAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		showStdOutDialogAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "showStdOutDialog" ); 
		showStdOutDialogAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'O' ) );
		showStdOutDialogAction.putValue( javax.swing.Action.NAME, Messages.getString("Text_Output") ); 
		showStdOutDialogAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Show_text_output_window") ); 
		//showStdOutDialogAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( showStdOutDialogAction );

		//showStdErrDialogAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		showStdErrDialogAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "showStdErrDialog" ); 
		showStdErrDialogAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'E' ) );
		showStdErrDialogAction.putValue( javax.swing.Action.NAME, Messages.getString("Error_Console") ); 
		showStdErrDialogAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Show_error_console_window") ); 
		//showStdErrDialogAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( showStdErrDialogAction );

		showPrintDialogAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK ) );
		showPrintDialogAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "showPrintDialog" ); 
		//showPrintDialogAction.putValue( javax.swing.Action.DISPLAYED_MNEMONIC_INDEX_KEY, 16);
		showPrintDialogAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		showPrintDialogAction.putValue( javax.swing.Action.NAME, Messages.getString("Export_Code_For_Printing___") ); 
		showPrintDialogAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Export_user_defined_methods_and_questions_for_printing") ); 
		//showPrintDialogAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( showPrintDialogAction );

		pauseWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_PAUSE, 0 ) );
		pauseWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "pauseWorld" ); 
//		pauseWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		pauseWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Pause") ); 
		pauseWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Pause_the_running_of_the_world__Pause_Break_") ); 
		//pauseWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( pauseWorldAction );

		resumeWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_PAGE_UP, 0 ) );
		resumeWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "resumeWorld" ); 
//		resumeWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'R' ) );
		resumeWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Play") ); 
		resumeWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Resume_the_running_of_the_world__Page_Up_") ); 
		//resumeWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( resumeWorldAction );

		restartWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_BACK_SPACE, 0 ) );
		restartWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "restartWorld" ); 
//		restartWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		restartWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Restart") ); 
		restartWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Restart_the_world__Backspace_") ); 
		//restartWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( restartWorldAction );

		restartStopWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_BACK_SPACE, 0 ) );
		restartStopWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "restartWorld" ); 
//		restartWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		restartStopWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Restart") ); 
		restartStopWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Restart_the_world__Backspace_") ); 
		//restartWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( restartStopWorldAction );

		
		stopWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_ESCAPE, 0 ) );
		stopWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "stopWorld" ); 
//		stopWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'S' ) );
		stopWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Stop") ); 
		stopWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Stop_the_running_of_the_world__Esc_") ); 
		//stopWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( stopWorldAction );

		takePictureAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_G, java.awt.Event.CTRL_MASK ) );
		takePictureAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "takePicture" ); 
//		takePictureAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'c' ) );
		takePictureAction.putValue( javax.swing.Action.NAME, Messages.getString("Take_Picture") ); 
		takePictureAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Take_a_screenshot_of_the_current_scene__Ctrl_G_") ); 
		//takePictureAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( takePictureAction );
		
//		logging instructor intervention...
		logInstructorIntervention.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "logInstructor" ); 
		logInstructorIntervention.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'L' ) );
		logInstructorIntervention.putValue( javax.swing.Action.NAME, Messages.getString("Log_Instructor_Actions") ); 
		logInstructorIntervention.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Add_record_of_instructor_actions_to_log") ); 
		logInstructorIntervention.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "log" )); 
		applicationActions.add( logInstructorIntervention );
		
		//LicenseAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_G, java.awt.event.KeyEvent.CTRL_MASK ) );
		licenseAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "license" );
		licenseAction.putValue( javax.swing.Action.NAME, Messages.getString("View_License") ); 
		licenseAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("View_License") ); 
		applicationActions.add( licenseAction );
	}

	private void keyInit() {
		javax.swing.KeyStroke keyStroke;
		String commandKey;

		for( java.util.Iterator iter = applicationActions.iterator(); iter.hasNext(); ) {
			javax.swing.Action action = (javax.swing.Action)iter.next();

			try {
				keyStroke = (javax.swing.KeyStroke)action.getValue( javax.swing.Action.ACCELERATOR_KEY );
				commandKey = (String)action.getValue( javax.swing.Action.ACTION_COMMAND_KEY );
			} catch( ClassCastException e ) {
				continue;
			}

			if( (keyStroke != null) && (commandKey != null) ) {
				jAliceFrame.registerKeyboardAction( action, commandKey, keyStroke, javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW );
				// below is the new way of doing things, but it doesn't seem to work...
				//applicationPanel.getInputMap().put( keyStroke, commandKey );
				//applicationPanel.getActionMap().put( commandKey, action );
			}
		}
	}
}
