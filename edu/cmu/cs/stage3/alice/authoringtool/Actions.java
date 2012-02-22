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
		
		launchSoftwareUpdate = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.updateAlice();
			}
		};

		showStdOutDialogAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showStdErrOutDialog();
			}
		};

		showStdErrDialogAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				authoringTool.showStdErrOutDialog();
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
		

		newWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK) );
		newWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "newWorld" ); //$NON-NLS-1$
		newWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'N' ) );
		newWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.1") ); //$NON-NLS-1$
		newWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.2") ); //$NON-NLS-1$
		newWorldAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "new" ) ); //$NON-NLS-1$
		applicationActions.add( newWorldAction );

		openWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK) );
		openWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "openWorld" ); //$NON-NLS-1$
		openWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'O' ) );
		openWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.5") ); //$NON-NLS-1$
		openWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.6") ); //$NON-NLS-1$
		openWorldAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "open" ) ); //$NON-NLS-1$
		applicationActions.add( openWorldAction );

		saveWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "saveWorld" ); //$NON-NLS-1$
		saveWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'S' ) );
		saveWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.9") ); //$NON-NLS-1$
		saveWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.10") ); //$NON-NLS-1$
		saveWorldAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) ); //$NON-NLS-1$
		applicationActions.add( saveWorldAction );

		saveWorld.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0) );
		saveWorld.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "saveWorld" ); //$NON-NLS-1$
		saveWorld.putValue( javax.swing.Action.NAME, Messages.getString("Actions.13") );	 //$NON-NLS-1$
		applicationActions.add( saveWorld );
		
		//saveWorldAsAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveWorldAsAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "saveWorldAs" ); //$NON-NLS-1$
		saveWorldAsAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'v' ) );
		saveWorldAsAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.15") ); //$NON-NLS-1$
		saveWorldAsAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.16") ); //$NON-NLS-1$
		//saveWorldAsAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) );
		applicationActions.add( saveWorldAsAction );

		//saveForWebAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveForWebAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "saveForWeb" ); //$NON-NLS-1$
		saveForWebAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'w' ) );
		saveForWebAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.18") ); //$NON-NLS-1$
		saveForWebAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.19") ); //$NON-NLS-1$
		//saveForWebAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) );
		applicationActions.add( saveForWebAction );

		//importObjectAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		importObjectAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "importObject" ); //$NON-NLS-1$
		importObjectAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'I' ) );
		importObjectAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.21") ); //$NON-NLS-1$
		importObjectAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.22") ); //$NON-NLS-1$
		importObjectAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "import" ) ); //$NON-NLS-1$
		applicationActions.add( importObjectAction );

		//quitAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		quitAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "quit" ); //$NON-NLS-1$
		quitAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'x' ) );
		quitAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.25") ); //$NON-NLS-1$
		quitAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.26") ); //$NON-NLS-1$
		//quitAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( quitAction );

		cutAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.Event.CTRL_MASK) );
		cutAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "cut" ); //$NON-NLS-1$
		cutAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 't' ) );
		cutAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.28") ); //$NON-NLS-1$
		cutAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.29") ); //$NON-NLS-1$
		cutAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "cut" ) ); //$NON-NLS-1$
		applicationActions.add( cutAction );

		copyAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK) );
		copyAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "copy" ); //$NON-NLS-1$
		copyAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'C' ) );
		copyAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.32") ); //$NON-NLS-1$
		copyAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.33") ); //$NON-NLS-1$
		copyAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "copy" ) ); //$NON-NLS-1$
		applicationActions.add( copyAction );

		pasteAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK) );
		pasteAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "paste" ); //$NON-NLS-1$
		pasteAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		pasteAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.36") ); //$NON-NLS-1$
		pasteAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.37") ); //$NON-NLS-1$
		pasteAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "paste" ) ); //$NON-NLS-1$
		applicationActions.add( pasteAction );

		undoAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.Event.CTRL_MASK) );
		undoAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "undo" ); //$NON-NLS-1$
		undoAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'U' ) );
		undoAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.40") ); //$NON-NLS-1$
		undoAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.41") ); //$NON-NLS-1$
		undoAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "undo" ) ); //$NON-NLS-1$
		applicationActions.add( undoAction );

		redoAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.Event.CTRL_MASK) );
		redoAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "redo" ); //$NON-NLS-1$
		redoAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'R' ) );
		redoAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.44") ); //$NON-NLS-1$
		redoAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.45") ); //$NON-NLS-1$
		redoAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "redo" ) ); //$NON-NLS-1$
		applicationActions.add( redoAction );

		//aboutAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		aboutAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "about" ); //$NON-NLS-1$
		aboutAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'A' ) );
		aboutAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.48") ); //$NON-NLS-1$
		aboutAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.49") ); //$NON-NLS-1$
		aboutAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "about" ) ); //$NON-NLS-1$
		applicationActions.add( aboutAction );

		//onScreenHelpAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		onScreenHelpAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "onScreenHelp" ); //$NON-NLS-1$
		onScreenHelpAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'O' ) );
		onScreenHelpAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.52") ); //$NON-NLS-1$
		onScreenHelpAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.53") ); //$NON-NLS-1$
		//onScreenHelpAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "onScreenHelp" ) );
		applicationActions.add( onScreenHelpAction );

		playAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0) );// java.awt.Event.CTRL_MASK) );
		playAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "play" ); //$NON-NLS-1$
		//playAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		playAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.55") ); //$NON-NLS-1$
		playAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.56") ); //$NON-NLS-1$
		playAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "play" ) ); //$NON-NLS-1$
		applicationActions.add( playAction );

		//addCharacterAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		addCharacterAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "addObject" ); //$NON-NLS-1$
		//addCharacterAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'A' ) );
		addCharacterAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.59") ); //$NON-NLS-1$
		addCharacterAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.60") ); //$NON-NLS-1$
		//addCharacterAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( addCharacterAction );

		//add3DTextAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		add3DTextAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "add3DText" ); //$NON-NLS-1$
		//add3DTextAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'A' ) );
		add3DTextAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.0") ); //$NON-NLS-1$
		add3DTextAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.63") ); //$NON-NLS-1$
		//add3DTextAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( add3DTextAction );
		
		exportMovieAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0) );
	    exportMovieAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "exportVideo" ); //$NON-NLS-1$
		exportMovieAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.65") ); //$NON-NLS-1$
		exportMovieAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.66") ); //$NON-NLS-1$
	  	applicationActions.add( exportMovieAction );

		//trashAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		trashAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "trash" ); //$NON-NLS-1$
		//trashAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		//trashAction.putValue( javax.swing.Action.NAME, "Trash" );
		trashAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.68") ); //$NON-NLS-1$
		//trashAction.putValue( javax.swing.Action.SMALL_ICON,   );
		applicationActions.add( trashAction );

		//openExampleWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		openExampleWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "openExampleWorld" ); //$NON-NLS-1$
		//openExampleWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		openExampleWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.70") ); //$NON-NLS-1$
		openExampleWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.71") ); //$NON-NLS-1$
		//openExampleWorldAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( openExampleWorldAction );

		//helpAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		helpAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "help" ); //$NON-NLS-1$
		helpAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'H' ) );
		helpAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.73") ); //$NON-NLS-1$
		helpAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.74") ); //$NON-NLS-1$
		//helpAction.putValue( javax.swing.Action.SMALL_ICON,  );
		applicationActions.add( helpAction );

		preferencesAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F8, 0 ) );
		preferencesAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "preferences" ); //$NON-NLS-1$
		preferencesAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		preferencesAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.76") ); //$NON-NLS-1$
		preferencesAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.77") ); //$NON-NLS-1$
		//preferencesAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( preferencesAction );

		makeBillboardAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_B, java.awt.Event.CTRL_MASK ) );
		makeBillboardAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "makeBillboard" ); //$NON-NLS-1$
		makeBillboardAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'B' ) );
		makeBillboardAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.79") ); //$NON-NLS-1$
		makeBillboardAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.80") ); //$NON-NLS-1$
		//makeBillboardAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( makeBillboardAction );

		//showWorldInfoAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		showWorldInfoAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "showWorldInfo" ); //$NON-NLS-1$
		showWorldInfoAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'I' ) );
		showWorldInfoAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.82") ); //$NON-NLS-1$
		showWorldInfoAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.83") ); //$NON-NLS-1$
		//showWorldInfoAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( showWorldInfoAction );

		//launchTutorialAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		launchTutorialAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "launchTutorial" ); //$NON-NLS-1$
		launchTutorialAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		launchTutorialAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.85") ); //$NON-NLS-1$
		launchTutorialAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.86") ); //$NON-NLS-1$
		//launchTutorialAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( launchTutorialAction );

		launchTutorialFileAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		launchTutorialFileAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "launchTutorialFile" ); //$NON-NLS-1$
		launchTutorialFileAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		launchTutorialFileAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.88") ); //$NON-NLS-1$
		launchTutorialFileAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.89") ); //$NON-NLS-1$
		//launchTutorialFileAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( launchTutorialFileAction );

    	//launchSoftwareUpdate.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		launchSoftwareUpdate.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "launchSoftwareUpdate" );
		//launchSoftwareUpdate.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'U' ) );
		launchSoftwareUpdate.putValue( javax.swing.Action.NAME, "Update Software" );
		launchSoftwareUpdate.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Update Alice 2.2" );
		//launchSoftwareUpdate.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( launchSoftwareUpdate );	
    
		//showStdOutDialogAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		showStdOutDialogAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "showStdOutDialog" ); //$NON-NLS-1$
		showStdOutDialogAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'O' ) );
		showStdOutDialogAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.91") ); //$NON-NLS-1$
		showStdOutDialogAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.92") ); //$NON-NLS-1$
		//showStdOutDialogAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( showStdOutDialogAction );

		//showStdErrDialogAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0 ) );
		showStdErrDialogAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "showStdErrDialog" ); //$NON-NLS-1$
		showStdErrDialogAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'E' ) );
		showStdErrDialogAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.94") ); //$NON-NLS-1$
		showStdErrDialogAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.95") ); //$NON-NLS-1$
		//showStdErrDialogAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( showStdErrDialogAction );

		showPrintDialogAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK ) );
		showPrintDialogAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "showPrintDialog" ); //$NON-NLS-1$
		//showPrintDialogAction.putValue( javax.swing.Action.DISPLAYED_MNEMONIC_INDEX_KEY, 16);
		showPrintDialogAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		showPrintDialogAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.97") ); //$NON-NLS-1$
		showPrintDialogAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.98") ); //$NON-NLS-1$
		//showPrintDialogAction.putValue( javax.swing.Action.SMALL_ICON, );
		applicationActions.add( showPrintDialogAction );

		pauseWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_PAUSE, 0 ) );
		pauseWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "pauseWorld" ); //$NON-NLS-1$
//		pauseWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'P' ) );
		pauseWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.100") ); //$NON-NLS-1$
		pauseWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.101") ); //$NON-NLS-1$
		//pauseWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( pauseWorldAction );

		resumeWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_PAGE_UP, 0 ) );
		resumeWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "resumeWorld" ); //$NON-NLS-1$
//		resumeWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'R' ) );
		resumeWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.103") ); //$NON-NLS-1$
		resumeWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.104") ); //$NON-NLS-1$
		//resumeWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( resumeWorldAction );

		restartWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_BACK_SPACE, 0 ) );
		restartWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "restartWorld" ); //$NON-NLS-1$
//		restartWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		restartWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.106") ); //$NON-NLS-1$
		restartWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.107") ); //$NON-NLS-1$
		//restartWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( restartWorldAction );

		restartStopWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_BACK_SPACE, 0 ) );
		restartStopWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "restartWorld" ); //$NON-NLS-1$
//		restartWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'T' ) );
		restartStopWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.109") ); //$NON-NLS-1$
		restartStopWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.110") ); //$NON-NLS-1$
		//restartWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( restartStopWorldAction );

		
		stopWorldAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_ESCAPE, 0 ) );
		stopWorldAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "stopWorld" ); //$NON-NLS-1$
//		stopWorldAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'S' ) );
		stopWorldAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.112") ); //$NON-NLS-1$
		stopWorldAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.113") ); //$NON-NLS-1$
		//stopWorldAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( stopWorldAction );

		takePictureAction.putValue( javax.swing.Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_G, java.awt.event.KeyEvent.CTRL_MASK ) );
		takePictureAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "takePicture" ); //$NON-NLS-1$
//		takePictureAction.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'c' ) );
		takePictureAction.putValue( javax.swing.Action.NAME, Messages.getString("Actions.115") ); //$NON-NLS-1$
		takePictureAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.116") ); //$NON-NLS-1$
		//takePictureAction.putValue( javax.swing.Action.SMALL_ICON, );
		renderActions.add( takePictureAction );
		
//		logging instructor intervention...
		  logInstructorIntervention.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "logInstructor" ); //$NON-NLS-1$
		  logInstructorIntervention.putValue( javax.swing.Action.MNEMONIC_KEY, new Integer( 'L' ) );
		  logInstructorIntervention.putValue( javax.swing.Action.NAME, Messages.getString("Actions.118") ); //$NON-NLS-1$
		  logInstructorIntervention.putValue( javax.swing.Action.SHORT_DESCRIPTION, Messages.getString("Actions.119") ); //$NON-NLS-1$
		  logInstructorIntervention.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "log" )); //$NON-NLS-1$
		  applicationActions.add( logInstructorIntervention );
		
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