/*
 *
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;

/**
 * @author Jason Pratt
 */
public class AuthoringTool implements java.awt.datatransfer.ClipboardOwner, edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication {
	// file extensions
	public static final String CHARACTER_EXTENSION = "a2c"; 
	public static final String WORLD_EXTENSION = "a2w"; 

	// python standard out/err
	private static org.python.core.PyFile pyStdOut;
	private static org.python.core.PyFile pyStdErr;

	// core components
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory renderTargetFactory;
	
	private edu.cmu.cs.stage3.alice.core.World world;
	private java.io.File defaultWorld;
	private JAliceFrame jAliceFrame;
	private edu.cmu.cs.stage3.alice.authoringtool.EditorManager editorManager;
		
	private edu.cmu.cs.stage3.alice.authoringtool.util.DefaultScheduler scheduler;
	private edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler oneShotScheduler;
	
	private Runnable worldScheduleRunnable;
	private edu.cmu.cs.stage3.alice.core.clock.DefaultClock worldClock;
	
	private MainUndoRedoStack undoRedoStack;
	private Actions actions;
	private Importing importing;
	public edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent stdOutOutputComponent;
	public edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent stdErrOutputComponent;
	private edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel watcherPanel;

	private javax.swing.JFileChooser importFileChooser;
	private javax.swing.JFileChooser addCharacterFileChooser;
	private javax.swing.JFileChooser browseFileChooser;

	private javax.swing.JFileChooser saveWorldFileChooser;
	private javax.swing.JFileChooser saveCharacterFileDialog; //java.awt.FileDialog

	private edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane worldLoadProgressPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane worldStoreProgressPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane characterLoadProgressPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane characterStoreProgressPane;

	private edu.cmu.cs.stage3.alice.authoringtool.dialog.PreferencesContentPane preferencesContentPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.AboutContentPane aboutContentPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.LicenseContentPane licenseContentPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.WorldInfoContentPane worldInfoContentPane;
	public edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane stdErrContentPane;
	public edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane stdOutContentPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.ExportCodeForPrintingContentPane exportCodeForPrintingContentPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane startUpContentPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.SaveForWebContentPane saveForWebContentPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.NewVariableContentPane newVariableContentPane;

	private edu.cmu.cs.stage3.alice.authoringtool.dialog.RenderContentPane renderContentPane;
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane captureContentPane;
	private javax.swing.JPanel renderPanel;

	// file filters
	private javax.swing.filechooser.FileFilter worldFileFilter;
	private javax.swing.filechooser.FileFilter characterFileFilter;

	// misc
	private java.io.File currentWorldLocation;
	private boolean worldHasBeenModified = false;
	private long lastSaveTime;
	private movieMaker.SoundStorage soundStorage = null;
	private java.io.File worldDirectory; // only needed for saving backup files
	private java.util.HashMap extensionStringsToFileFilterMap;
	private edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig;
	private edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget;
	private edu.cmu.cs.stage3.alice.scripting.ScriptingFactory scriptingFactory;

	private java.awt.event.WindowListener jAliceFrameWindowListener;

	private boolean saveTabsEnabled = false;
	private long worldLoadedTime;
	//private double speedMultiplier = 1.0;

	private edu.cmu.cs.stage3.alice.authoringtool.util.RectangleAnimator rectangleAnimator;

	private boolean stdOutToConsole;
	private boolean stdErrToConsole;
	public int numEncoded=0;	//Madeleine added

	private static ArrayList sound = new ArrayList ();
	
	private edu.cmu.cs.stage3.alice.core.util.WorldListener userDefinedParameterListener = new edu.cmu.cs.stage3.alice.core.util.WorldListener() {
		private Object m_previousPropertyValue = null;
		private edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse[] getCallsTo( final edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse userDefined ) {
			java.util.Vector v = new java.util.Vector();
			this.getWorld().internalSearch( new edu.cmu.cs.stage3.util.Criterion() {
				public boolean accept( Object o ) {
					if( o instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse ) {
						edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse call = (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)o;
						if( call.userDefinedResponse.getUserDefinedResponseValue() == userDefined ) {
							return true;
						}
					}
					return false;
				}
			}, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, v );
			edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse[] calls = new edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse[ v.size() ];
			v.copyInto( calls );
			return calls;
		}
		private edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion[] getCallsTo( final edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion userDefined ) {
			java.util.Vector v = new java.util.Vector();
			this.getWorld().internalSearch( new edu.cmu.cs.stage3.util.Criterion() {
				public boolean accept( Object o ) {
					if( o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion ) {
						edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion call = (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)o;
						if( call.userDefinedQuestion.getUserDefinedQuestionValue() == userDefined ) {
							return true;
						}
					}
					return false;
				}
			}, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, v );
			edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion[] calls = new edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion[ v.size() ];
			v.copyInto( calls );
			return calls;
		}
		protected void handleChildrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e ) {
		}
		protected void handleChildrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e ) {
		}
		
		protected void handlePropertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e ) {
			m_previousPropertyValue = e.getProperty().get();
		}
		protected void handlePropertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e ) {
			edu.cmu.cs.stage3.alice.core.Property property = e.getProperty();
			edu.cmu.cs.stage3.alice.core.Element owner = property.getOwner();
			if( owner instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
				edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)owner;
				if( property.getName().equals( "name" ) ) { 
					edu.cmu.cs.stage3.alice.core.Element parent = variable.getParent();
					if( parent instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ) {
						edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse userDefined = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)parent;
						edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse[] calls = getCallsTo( userDefined );
						for( int i=0; i<calls.length; i++ ) {
							for( int j=0; j<calls[ i ].requiredActualParameters.size(); j++ ) {
								edu.cmu.cs.stage3.alice.core.Variable actualParameterJ = (edu.cmu.cs.stage3.alice.core.Variable)calls[ i ].requiredActualParameters.get( j );
								String nameJ = actualParameterJ.name.getStringValue();
								if( nameJ != null && nameJ.equals( m_previousPropertyValue ) ) {
									actualParameterJ.name.set( e.getValue() );
								}
							}
						}
					} else if( parent instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) {
						edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion userDefined = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)parent;
						edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion[] calls = getCallsTo( userDefined );
						for( int i=0; i<calls.length; i++ ) {
							for( int j=0; j<calls[ i ].requiredActualParameters.size(); j++ ) {
								edu.cmu.cs.stage3.alice.core.Variable actualParameterJ = (edu.cmu.cs.stage3.alice.core.Variable)calls[ i ].requiredActualParameters.get( j );
								String nameJ = actualParameterJ.name.getStringValue();
								if( nameJ != null && nameJ.equals( m_previousPropertyValue ) ) {
									actualParameterJ.name.set( e.getValue() );
								}
							}
						}
					}
				}
			}
		}
		protected void handleObjectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e ) {
		}
		protected void handleObjectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e ) {
			edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = e.getObjectArrayProperty();
			edu.cmu.cs.stage3.alice.core.Element owner = oap.getOwner();
			if( owner instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ) {
				edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse userDefined = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)owner;
				if( oap.getName().equals( "requiredFormalParameters" ) ) { 
					Object item = e.getItem();
					if( item instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
						edu.cmu.cs.stage3.alice.core.Variable formalParameter = (edu.cmu.cs.stage3.alice.core.Variable)item;
						String formalParameterName = formalParameter.name.getStringValue();
						edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse[] calls = getCallsTo( userDefined );
						switch( e.getChangeType() ) {
						case edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED:
							for( int i=0; i<calls.length; i++ ) {
								edu.cmu.cs.stage3.alice.core.Variable actualParameter = new edu.cmu.cs.stage3.alice.core.Variable();
								actualParameter.name.set( formalParameter.name.get() );
								Class cls = formalParameter.valueClass.getClassValue();
								actualParameter.valueClass.set( cls );
								actualParameter.value.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass( cls ) );
								boolean tempListening = AuthoringTool.this.getUndoRedoStack().getIsListening();
								AuthoringTool.this.getUndoRedoStack().setIsListening( false );
								calls[ i ].addChild( actualParameter );
								calls[ i ].requiredActualParameters.add( actualParameter );
								AuthoringTool.this.getUndoRedoStack().setIsListening( tempListening );
							}
							break;
						case edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED:
							for( int i=0; i<calls.length; i++ ) {
								for( int j=0; j<calls[ i ].requiredActualParameters.size(); j++ ) {
									edu.cmu.cs.stage3.alice.core.Variable actualParameterJ = (edu.cmu.cs.stage3.alice.core.Variable)calls[ i ].requiredActualParameters.get( j );
									String nameJ = actualParameterJ.name.getStringValue();
									if( nameJ != null && nameJ.equals( formalParameterName ) ) {
										boolean tempListening = AuthoringTool.this.getUndoRedoStack().getIsListening();
										AuthoringTool.this.getUndoRedoStack().setIsListening( false );
										actualParameterJ.removeFromParent();
										AuthoringTool.this.getUndoRedoStack().setIsListening( tempListening );
									}
								}
							}
							break;
						case edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_SHIFTED:
							for( int i=0; i<calls.length; i++ ) {
								calls[ i ].requiredActualParameters.shift( e.getOldIndex(), e.getNewIndex() );
							}
							break;
						}
					}
				}
			} else if( owner instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) {
				edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion userDefined = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)owner;
				if( oap.getName().equals( "requiredFormalParameters" ) ) { 
					Object item = e.getItem();
					if( item instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
						edu.cmu.cs.stage3.alice.core.Variable formalParameter = (edu.cmu.cs.stage3.alice.core.Variable)item;
						String formalParameterName = formalParameter.name.getStringValue();
						edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion[] calls = getCallsTo( userDefined );
						switch( e.getChangeType() ) {
						case edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED:
							for( int i=0; i<calls.length; i++ ) {
								edu.cmu.cs.stage3.alice.core.Variable actualParameter = new edu.cmu.cs.stage3.alice.core.Variable();
								actualParameter.name.set( formalParameter.name.get() );
								Class cls = formalParameter.valueClass.getClassValue();
								actualParameter.valueClass.set( cls );
								actualParameter.value.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass( cls ) );
								calls[ i ].addChild( actualParameter );
								calls[ i ].requiredActualParameters.add( actualParameter );
							}
							break;
						case edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED:
							for( int i=0; i<calls.length; i++ ) {
								for( int j=0; j<calls[ i ].requiredActualParameters.size(); j++ ) {
									edu.cmu.cs.stage3.alice.core.Variable actualParameterJ = (edu.cmu.cs.stage3.alice.core.Variable)calls[ i ].requiredActualParameters.get( j );
									String nameJ = actualParameterJ.name.getStringValue();
									if( nameJ != null && nameJ.equals( formalParameterName ) ) {
										actualParameterJ.removeFromParent();
									}
								}
							}
							break;
						case edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_SHIFTED:
							for( int i=0; i<calls.length; i++ ) {
								calls[ i ].requiredActualParameters.shift( e.getOldIndex(), e.getNewIndex() );
							}
							break;
						}
					}
				}
			}
		}
		protected boolean isPropertyListeningRequired( edu.cmu.cs.stage3.alice.core.Property property ) {
			return true;
		}
		protected boolean isObjectArrayPropertyListeningRequired( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap ) {
			return true;
		}
	};

	// selected element
	private edu.cmu.cs.stage3.alice.core.Element selectedElement;
	private java.util.HashSet selectionListeners = new java.util.HashSet();

	// AuthoringTool state listening
	private java.util.HashSet stateListeners = new java.util.HashSet();

	// Logging
	public boolean instructorInControl = false;
	public static org.python.core.PyFile getPyStdOut() {
		return pyStdOut;
	}

	public static org.python.core.PyFile getPyStdErr() {
		return pyStdErr;
	}

	private static AuthoringTool hack;
	public static AuthoringTool getHack() {
		return hack;
	}

	// constructor
	public AuthoringTool(java.io.File defaultWorld, java.io.File worldToLoad, boolean stdOutToConsole, boolean stdErrToConsole) {
		AikMin.setUI();
		
		AuthoringTool.hack = this;
		this.defaultWorld = defaultWorld;
/*		if (!(defaultWorld.exists() && defaultWorld.canRead())) {
			this.defaultWorld = null;
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(defaultWorld.getAbsolutePath() + " " + Messages.getString("does_not_exist_or_cannot_be_read__No_starting_world_will_be_available_"), Messages.getString("Warning"), javax.swing.JOptionPane.WARNING_MESSAGE);  
		}
*/
		filterInit();
		configInit();	
		mainInit();
		this.stdOutToConsole = stdOutToConsole;
		this.stdErrToConsole = stdErrToConsole;
		initializeOutput(stdOutToConsole, stdErrToConsole);
		pyInit();
		dialogInit();
		undoRedoInit();
		miscInit();
		importInit();
		//preCacheCommonEditors();
		worldInit(null);
		stencilInit();
		edu.cmu.cs.stage3.scheduler.Scheduler s = new edu.cmu.cs.stage3.scheduler.AbstractScheduler() {
			protected void handleCaughtThowable( Runnable source, Throwable t ) {
				markEachFrameRunnableForRemoval( source );
				showErrorDialog( source.toString(), t );
				
				//todo?
				//addEachFrameRunnable( source );
			}
		};
		s.addEachFrameRunnable( scheduler );
		s.addEachFrameRunnable( oneShotScheduler );

		edu.cmu.cs.stage3.scheduler.SchedulerThread schedulerThread = new edu.cmu.cs.stage3.scheduler.SchedulerThread( s );
		//schedulerThread.setSleepMillis( 1 );
		//schedulerThread.setPriority( Thread.MAX_PRIORITY );
		schedulerThread.start();

		jAliceFrame.setVisible(true);
		if( worldToLoad == null ) {
			if (authoringToolConfig.getValue("showStartUpDialog").equalsIgnoreCase("true")) {  
				showStartUpDialog(edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.TEMPLATE_TAB_ID);//.DO_NOT_CHANGE_TAB_ID);
			}
		} else if (worldToLoad != defaultWorld){
			worldInit(worldToLoad);
		}
	}

	private void filterInit() {
		worldFileFilter = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter(WORLD_EXTENSION, WORLD_EXTENSION.toUpperCase() + " " + Messages.getString("_Alice_World_Files_")); 
		characterFileFilter = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter(CHARACTER_EXTENSION, CHARACTER_EXTENSION.toUpperCase() + " " + Messages.getString("_Alice_Object_Files_")); 
	}

	private void mainInit() {
		editorManager = new edu.cmu.cs.stage3.alice.authoringtool.EditorManager(this);
		scheduler = new edu.cmu.cs.stage3.alice.authoringtool.util.DefaultScheduler();
		undoRedoStack = new MainUndoRedoStack(this);
		oneShotScheduler = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler();
		jAliceFrame = new JAliceFrame(this);
		actions = new Actions(this, jAliceFrame);
		jAliceFrame.actionInit(actions);
		edu.cmu.cs.stage3.swing.DialogManager.initialize(jAliceFrame);
		importing = new Importing();
		stdOutOutputComponent = new edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent();
		stdErrOutputComponent = new edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent();
		watcherPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel();
	}

	private void pyInit() {
		scriptingFactory = new edu.cmu.cs.stage3.alice.scripting.jython.ScriptingFactory();
		scriptingFactory.setStdOut(System.out);
		scriptingFactory.setStdErr(System.err);
	}

	private void worldsDirectoryChanged() {
		String worldsDirPath = authoringToolConfig.getValue("directories.worldsDirectory"); 
		if (worldsDirPath != null) {
			java.io.File worldsDir = new java.io.File(worldsDirPath);
			if (worldsDir != null && worldsDir.exists() && worldsDir.isDirectory()) {
				try {
					saveWorldFileChooser.setCurrentDirectory(worldsDir);
				} catch( IndexOutOfBoundsException ioobe ) {
					// for some reason this can potentially fail in jdk1.4.2_04
				}
			} else {
				//TODO -Ignore : ?
			}
		} else {
			//TODO -Ignore : what to do when the directory is null?
		}
	}
	private void importDirectoryChanged() {
		String importDirPath = authoringToolConfig.getValue("directories.importDirectory"); 
		if (importDirPath != null) {
			java.io.File importDir = new java.io.File(importDirPath);
			if (importDir != null && importDir.exists() && importDir.isDirectory()) {
				try {
					importFileChooser.setCurrentDirectory(importDir);
				} catch( IndexOutOfBoundsException aioobe ) {
					// for some reason this can potentially fail in jdk1.4.2_04
				}
			} else {
				//TODO -Ignore : ?
			}
		} else {
			//TODO -Ignore : what to do when the directory is null?
		}
	}
	private void charactersDirectoryChanged() {
		String charactersDirPath = authoringToolConfig.getValue("directories.charactersDirectory"); 
		if (charactersDirPath != null) {
			java.io.File charactersDir = new java.io.File(charactersDirPath);
			if (charactersDir != null) {
				if (!charactersDir.exists()) {
//				&& charactersDir.isDirectory()) {
					charactersDir.mkdir();
				} 
				try {
					addCharacterFileChooser.setCurrentDirectory(charactersDir);
					saveCharacterFileDialog.setCurrentDirectory(charactersDir);//.getAbsolutePath());
				} catch( IndexOutOfBoundsException aioobe ) {
					// for some reason this can potentially fail in jdk1.4.2_04
				}		
			} 
		} else {
			//TODO -Ignore : what to do when the directory is null?
		}
	}

	private void configInit() {
		authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.addConfigurationListener(new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener() {
			public void changing(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
			}

			public void changed(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
				if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.recentWorlds.maxWorlds")) { 
					AuthoringTool.this.jAliceFrame.updateRecentWorlds();
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.numberOfClipboards")) { 
					AuthoringTool.this.jAliceFrame.updateClipboards();
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.showWorldStats")) { 
					AuthoringTool.this.jAliceFrame.showStatusPanel(ev.getNewValue().equalsIgnoreCase("true")); 
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.worldsDirectory")) { 
					AuthoringTool.this.worldsDirectoryChanged();
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.importDirectory")) { 
					AuthoringTool.this.importDirectoryChanged();
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.charactersDirectory")) { 
					AuthoringTool.this.charactersDirectoryChanged();
				}
			}
		});
	}

	private void dialogInit() {
		javax.swing.LookAndFeel laf = javax.swing.UIManager.getLookAndFeel();
		
		preferencesContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.PreferencesContentPane();
		preferencesContentPane.setAuthoringTool(this);
	
		newVariableContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewVariableContentPane();


		try {
			if ( AikMin.isMAC() ){
				javax.swing.UIManager.setLookAndFeel("apple.laf.AquaLookAndFeel");
				javax.swing.UIManager.getLookAndFeelDefaults().put("Menu.arrowIcon", laf.getDefaults().get("Menu.arrowIcon"));
			}
		} catch (Exception e) {
		}

		captureContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane(this);
		renderContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.RenderContentPane(this);
		
		
		importFileChooser = new javax.swing.JFileChooser();		
		saveWorldFileChooser = new javax.swing.JFileChooser() {
			public void approveSelection() {
				java.io.File desiredFile = getSelectedFile();
				if (currentWorldLocation == null || currentWorldLocation.equals(desiredFile) || !desiredFile.exists()) {
					if (shouldAllowOverwrite(desiredFile)) {
						super.approveSelection();
					} else {
						edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("That_is_protected_Alice_file_and_you_can_not_overwrite_it__Please_choose_another_file_")); 
					}
				} else if (desiredFile.exists()) {
					if (shouldAllowOverwrite(desiredFile)) {
						int n = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("You_are_about_to_save_over_an_existing_file__Are_you_sure_you_want_to_"), Messages.getString("Save_Over_Warning"), javax.swing.JOptionPane.YES_NO_OPTION);  
						if (n == javax.swing.JOptionPane.YES_OPTION) {
							super.approveSelection();
						}
					} else {
						edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("That_is_protected_Alice_file_and_you_can_not_overwrite_it__Please_choose_another_file_")); 
					}

				}
			}
		};
		addCharacterFileChooser = new javax.swing.JFileChooser();
		saveCharacterFileDialog = new javax.swing.JFileChooser();
			
		//browseFileChooser = new javax.swing.JFileChooser();
		//		browseFileChooser.setApproveButtonText( "Set Directory" );
		//		browseFileChooser.setDialogTitle( "Choose Directory..." );
		//		browseFileChooser.setDialogType( javax.swing.JFileChooser.OPEN_DIALOG );
		//		browseFileChooser.setFileSelectionMode( javax.swing.JFileChooser.DIRECTORIES_ONLY );

		/*
		 * Removed - variable not used.
		java.io.FilenameFilter worldFilenameFilter = new java.io.FilenameFilter() {
			public boolean accept(java.io.File dir, String name) {
				return name.toLowerCase().endsWith("." + WORLD_EXTENSION);
			}
		};
		
		java.io.FilenameFilter characterFilenameFilter = new java.io.FilenameFilter() {
			public boolean accept(java.io.File dir, String name) {
				return name.toLowerCase().endsWith("." + CHARACTER_EXTENSION);
			}
		};
*/		
		int fontSize = Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) ); 
		int x = 800 + ( fontSize - 12 ) * 25; //700 + ( fontSize - 12 ) * 25;
		int y = 405 + (fontSize - 12);
				
		saveCharacterFileDialog.setDialogTitle(Messages.getString("Save_Object___")); 
		saveCharacterFileDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		saveCharacterFileDialog.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		saveCharacterFileDialog.setFileFilter(characterFileFilter);
		saveCharacterFileDialog.setPreferredSize(new java.awt.Dimension(x, y));

		worldLoadProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane(Messages.getString("Loading_World___"), Messages.getString("Loading__"));  
		worldStoreProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane(Messages.getString("Saving_World___"), Messages.getString("Saving__"));  
		characterLoadProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane(Messages.getString("Loading_Object___"), Messages.getString("Loading__"));  
		characterStoreProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane(Messages.getString("Saving_Object___"), Messages.getString("Saving__"));  

		renderPanel = new javax.swing.JPanel();

		//aboutContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.AboutContentPane();
		//licenseContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LicenseContentPane();

		renderPanel.setLayout(new java.awt.BorderLayout());

		importFileChooser.setApproveButtonText(Messages.getString("Import")); 
		importFileChooser.setDialogTitle(Messages.getString("Import___")); 
		importFileChooser.setDialogType(javax.swing.JFileChooser.OPEN_DIALOG);
		importFileChooser.setPreferredSize(new java.awt.Dimension(x, y));

		saveWorldFileChooser.setApproveButtonText(Messages.getString("Save_World_As")); 
		saveWorldFileChooser.setDialogTitle(Messages.getString("Save_World_As___")); 
		saveWorldFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		saveWorldFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		saveWorldFileChooser.setFileFilter(worldFileFilter);
		saveWorldFileChooser.setPreferredSize(new java.awt.Dimension(x, y));

		addCharacterFileChooser.setApproveButtonText(Messages.getString("Add_Object")); 
		addCharacterFileChooser.setDialogTitle(Messages.getString("Add_Object___")); 
		addCharacterFileChooser.setDialogType(javax.swing.JFileChooser.OPEN_DIALOG);
		addCharacterFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		addCharacterFileChooser.setFileFilter(characterFileFilter);
		addCharacterFileChooser.setPreferredSize(new java.awt.Dimension(x, y));

		worldsDirectoryChanged();
		importDirectoryChanged();
		charactersDirectoryChanged();

		startUpContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane(this);	

		worldInfoContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.WorldInfoContentPane();		
		
		exportCodeForPrintingContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.ExportCodeForPrintingContentPane(this);
		
		//saveForWebContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.SaveForWebContentPane(this);

		//newVariableContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewVariableContentPane();
	
		stdErrContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane(this, true);
		stdOutContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane(this, false);
	}

	private void undoRedoInit() {
		addAuthoringToolStateListener(undoRedoStack);

		undoRedoStack.addUndoRedoListener(new edu.cmu.cs.stage3.alice.authoringtool.event.UndoRedoListener() {
			public void onChange() {
				int currentIndex = AuthoringTool.this.undoRedoStack.getCurrentUndoableRedoableIndex();
				if (currentIndex == -1) {
					AuthoringTool.this.actions.undoAction.setEnabled(false);
				} else {
					AuthoringTool.this.actions.undoAction.setEnabled(true);
				}

				if (currentIndex == (AuthoringTool.this.undoRedoStack.size() - 1)) {
					AuthoringTool.this.actions.redoAction.setEnabled(false);
				} else {
					AuthoringTool.this.actions.redoAction.setEnabled(true);
				}

				AuthoringTool.this.worldHasBeenModified = (currentIndex != AuthoringTool.this.undoRedoStack.getUnmodifiedIndex()) || AuthoringTool.this.undoRedoStack.isScriptDirty();
				AuthoringTool.this.updateTitle();
			}
		});
	}

	private void miscInit() {
		extensionStringsToFileFilterMap = new java.util.HashMap();
		scheduler.addEachFrameRunnable(oneShotScheduler);

		// try to quit on window close
		jAliceFrameWindowListener = new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				AuthoringTool.this.quit(false);
			}
		};
		jAliceFrame.addWindowListener(jAliceFrameWindowListener);

		worldClock = new edu.cmu.cs.stage3.alice.core.clock.DefaultClock();
		
		worldScheduleRunnable = new Runnable() {
			public void run() {
				try {
					worldClock.schedule();
//					double time = AuthoringToolResources.getCurrentTime() - AuthoringTool.this.timeDifferential;
//					AuthoringTool.this.world.schedule( time );
				} catch( Throwable t ) {
					stopWorldAndShowDialog( t );
				}
			}
		};

		// for running the world
		//		worldScheduleRunnable = new Runnable() {
		//			public void run() {
		//				try {
		//					AuthoringTool.this.world.schedule( AuthoringToolResources.getCurrentTime() - AuthoringTool.this.timeDifferential );
		//				} catch( final edu.cmu.cs.stage3.alice.core.SimulationException e ) {
		//					javax.swing.SwingUtilities.invokeLater( new Runnable() {
		//						public void run() {
		//							AuthoringTool.showRuntimeErrorDialog( e, AuthoringTool.this );
		//						}
		//					} );
		//				} catch( final edu.cmu.cs.stage3.alice.core.ExceptionWrapper e ) {
		//					javax.swing.SwingUtilities.invokeLater( new Runnable() {
		//						public void run() {
		//							Exception wrappedException = e.getWrappedException();
		//							if( wrappedException instanceof edu.cmu.cs.stage3.alice.core.SimulationException ) {
		//								AuthoringTool.showRuntimeErrorDialog( (edu.cmu.cs.stage3.alice.core.SimulationException)wrappedException, AuthoringTool.this );
		//							} else {
		//								AuthoringTool.showErrorDialog( "Error during simulation.", wrappedException );
		//							}
		//						}
		//					} );
		//				} catch( final org.python.core.PyException e ) {
		//					javax.swing.SwingUtilities.invokeLater( new Runnable() {
		//						public void run() {
		//							if( org.python.core.Py.matchException( e, org.python.core.Py.SystemExit ) ) {
		//								//just quit
		//							} else {
		//								AuthoringTool.showErrorDialog( "Jython error during world run.", e, false );
		//							}
		//						}
		//					} );
		//				} catch( final Throwable t ) {
		//					javax.swing.SwingUtilities.invokeLater( new Runnable() {
		//						public void run() {
		////							renderWindowListener.windowClosing( null ); // somewhat hackish
		//							AuthoringTool.showErrorDialog( "Error during simulation.", t );
		//						}
		//					} );
		//				}
		//			}
		//		};

		// track framerate
		javax.swing.Timer fpsTimer = new javax.swing.Timer(500, new java.awt.event.ActionListener() {
			java.text.DecimalFormat formater = new java.text.DecimalFormat("#0.00"); 
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				if (authoringToolConfig.getValue("rendering.showFPS").equalsIgnoreCase("true")) {  
					AuthoringTool.this.renderContentPane.setTitle(Messages.getString("World_Running_____", formater.format(AuthoringTool.this.scheduler.getSimulationFPS()))); 
				}
			}
		});
		fpsTimer.start();

		// prompt to save
		lastSaveTime = System.currentTimeMillis();
		javax.swing.Timer promptToSaveTimer = new javax.swing.Timer(60000, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
					// this check is a little hackish.  the idea is to not throw up the save dialog if a modal dialog is already showing.
				boolean modalShowing = false;
				java.awt.Window[] ownedWindows = AuthoringTool.this.jAliceFrame.getOwnedWindows();
				for (int i = 0; i < ownedWindows.length; i++) {
					//						System.out.println(ownedWindows[i]+", "+ownedWindows[i].isShowing());
					if (ownedWindows[i].isShowing()) {
						modalShowing = true;
						break;
					}
				}

				// skip tutorial worlds
				boolean skipThisWorld = false;
				if (currentWorldLocation != null) {
					if (currentWorldLocation.getAbsolutePath().startsWith(getTutorialDirectory().getAbsolutePath())) {
						skipThisWorld = true;
					}
				}

				// skip unmodified worlds
				if (!AuthoringTool.this.worldHasBeenModified) {
					skipThisWorld = true;
				}

				if ((!modalShowing) && (!skipThisWorld)) {
					long time = System.currentTimeMillis();
					long dt = time - lastSaveTime;
					int interval = Integer.parseInt(authoringToolConfig.getValue("promptToSaveInterval")); 
					long intervalMillis = ((long) interval) * ((long) 60000);
					if (dt > intervalMillis) {
						// edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( "You have not saved in more than" + interval + " minutes.\nIt is recommended that you save early and often to avoid losing work." );
						int result =
							edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(
								Messages.getString("You_have_not_saved_in_more_than_minutes__nIt_is_recommended_that_you_save_early_and_often_to_avoid_losing_work_", String.valueOf(interval)),  
								Messages.getString("Save_"), 
								javax.swing.JOptionPane.YES_NO_OPTION,
								javax.swing.JOptionPane.WARNING_MESSAGE,
								null,
								new Object[] { Messages.getString("Save_right_now"), Messages.getString("Remind_me_later") },  
								Messages.getString("Save_right_now")); 
						if (result == javax.swing.JOptionPane.YES_OPTION) {
							AuthoringTool.this.getActions().saveWorldAction.actionPerformed(null);
						}
						lastSaveTime = System.currentTimeMillis();
					}
				}
			}
		});
		promptToSaveTimer.start();
		addAuthoringToolStateListener(new edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateAdapter() {
			public void worldLoaded(AuthoringToolStateChangedEvent ev) {
				lastSaveTime = System.currentTimeMillis();
			}
			public void worldSaved(AuthoringToolStateChangedEvent ev) {
				lastSaveTime = System.currentTimeMillis();
			}
		});
		
		// global mouse listening
		if (edu.cmu.cs.stage3.awt.AWTUtilities.mouseListenersAreSupported() || edu.cmu.cs.stage3.awt.AWTUtilities.mouseMotionListenersAreSupported()) {
			scheduler.addEachFrameRunnable(new Runnable() {
				public void run() {
					edu.cmu.cs.stage3.awt.AWTUtilities.fireMouseAndMouseMotionListenersIfNecessary();
				}
			});
		}
		if (AikMin.isMAC()) {
		  	java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(new java.awt.event.AWTEventListener(){
		   		public void eventDispatched(AWTEvent event) {
	   				int mod = ((java.awt.event.InputEvent) event).getModifiers();
	   				edu.cmu.cs.stage3.awt.AWTUtilities.modifier = mod;
		   		}
		  	}, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
		  	
		}

		// Global Key Listener
//		if (AikMin.isMAC()) {
//			jnativeLisnener();
//		}
		
		// for animating ui changes
		rectangleAnimator = new edu.cmu.cs.stage3.alice.authoringtool.util.RectangleAnimator(this);

		watcherPanel.setMinimumSize(new java.awt.Dimension(0, 0));

		//tooltips
		javax.swing.ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		javax.swing.ToolTipManager.sharedInstance().setInitialDelay(1000);	
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
	}
/*	private void jnativeLisnener(){
		org.jnativehook.GlobalScreen.addNativeKeyListener(new org.jnativehook.keyboard.NativeKeyListener(){
			@Override
			public void nativeKeyPressed(NativeKeyEvent ev) {
				if ( (ev.getKeyCode() == NativeKeyEvent.VC_SHIFT_L) || (ev.getKeyCode() == NativeKeyEvent.VC_SHIFT_R) ){
					AikMin.shift = true;
				}
				if (AikMin.isMAC()) {
					if ( (ev.getKeyCode() == NativeKeyEvent.VC_ALT_L) || (ev.getKeyCode() == NativeKeyEvent.VC_ALT_L) ){
						AikMin.control = true;
					}
				} else {
					if ( (ev.getKeyCode() == NativeKeyEvent.VC_CONTROL_L) || (ev.getKeyCode() == NativeKeyEvent.VC_CONTROL_L) ){
						AikMin.control = true;
					}
				}
				
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent ev) {
				if ( (ev.getKeyCode() == NativeKeyEvent.VC_SHIFT_L) || (ev.getKeyCode() == NativeKeyEvent.VC_SHIFT_R) ){
					AikMin.shift = false;
				}
				if (AikMin.isMAC()) {
					if ( (ev.getKeyCode() == NativeKeyEvent.VC_ALT_L) || (ev.getKeyCode() == NativeKeyEvent.VC_ALT_L) ){
						AikMin.control = false;
					}
				} else {
					if ( (ev.getKeyCode() == NativeKeyEvent.VC_CONTROL_L) || (ev.getKeyCode() == NativeKeyEvent.VC_CONTROL_L) ){
						AikMin.control = false;
					}
				}
			}

			public void nativeKeyTyped(NativeKeyEvent ev) {}
	  	});
		
	}*/

	private void importInit() {
		java.util.List importers = importing.getImporters();
		edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter imageFiles = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter(Messages.getString("Image_Files")); 
		extensionStringsToFileFilterMap.put("Image Files", imageFiles);//Messages.getString("Image_Files"), imageFiles); 
		edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter soundFiles = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter(Messages.getString("Sound_Files")); 
		extensionStringsToFileFilterMap.put("Sound Files", soundFiles);//Messages.getString("Sound_Files"), soundFiles); 
		java.util.TreeSet extensions = new java.util.TreeSet();
		for (java.util.Iterator iter = importers.iterator(); iter.hasNext();) {
			edu.cmu.cs.stage3.alice.authoringtool.Importer importer = (edu.cmu.cs.stage3.alice.authoringtool.Importer) iter.next();
			java.util.Map map = importer.getExtensionMap();
			for (java.util.Iterator jter = map.keySet().iterator(); jter.hasNext();) {
				String extension = (String) jter.next();
				String description = extension + " (" + map.get(extension) + ")";  
				edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter ext = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter(extension, description);
				extensions.add(ext);
				extensionStringsToFileFilterMap.put(extension, ext);
				if (importer instanceof edu.cmu.cs.stage3.alice.authoringtool.importers.ImageImporter) {
					imageFiles.addExtensionFileFilter(ext);
				} else if (importer instanceof edu.cmu.cs.stage3.alice.authoringtool.importers.MediaImporter) {
					soundFiles.addExtensionFileFilter(ext);
				}
			}
		}
		importFileChooser.addChoosableFileFilter(characterFileFilter);
		importFileChooser.addChoosableFileFilter(imageFiles);
		importFileChooser.addChoosableFileFilter(soundFiles);
		for (java.util.Iterator iter = extensions.iterator(); iter.hasNext();) {
			edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter ext = (edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter) iter.next();
			importFileChooser.addChoosableFileFilter(ext);
		}
		importFileChooser.setFileFilter(importFileChooser.getAcceptAllFileFilter());
	}

//	private void preCacheCommonEditors() {
		//		editorManager.preloadEditor( edu.cmu.cs.stage3.alice.authoringtool.editors.evaluationgroupeditor.EvaluationGroupEditor.class );
		//		editorManager.preloadEditor( edu.cmu.cs.stage3.alice.authoringtool.editors.miscgroupeditor.MiscGroupEditor.class );
		//		editorManager.preloadEditor( edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.class );
		//		editorManager.preloadEditor( edu.cmu.cs.stage3.alice.authoringtool.editors.soundgroupeditor.SoundGroupEditor.class );
		//		editorManager.preloadEditor( edu.cmu.cs.stage3.alice.authoringtool.editors.texturemapgroupeditor.TextureMapGroupEditor.class );
		//		editorManager.preloadEditor( edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor.class );
//	}

	private void worldInit(java.io.File worldToLoad) {
		if (worldToLoad != null) {
			
			String oldWorld = worldToLoad.getAbsolutePath();
			oldWorld = oldWorld.substring(0, oldWorld.lastIndexOf(".")) + ".a2w";
			java.io.File world = new java.io.File ( oldWorld );
			
			if (world.exists()) {
				if (world.canRead()) {
					int retVal = loadWorld(world, false);
					if (retVal == Constants.SUCCEEDED) {
						return;
					}
				} else {
					AuthoringTool.showErrorDialog(Messages.getString("cannot_read_world__", world), null, false); 
				}
			} else {
				AuthoringTool.showErrorDialog(Messages.getString("world_doesn_t_exist__", world), null, false); 
			}
		}

		// if that fails
		loadWorld(defaultWorld, false);
	}

	@SuppressWarnings("resource")
	private void initializeOutput(boolean stdOutToConsole, boolean stdErrToConsole) {
		if (stdOutToConsole) {
			AuthoringTool.pyStdOut = new org.python.core.PyFile(System.out);
		} else {
			java.io.PrintStream stdOutStream = stdOutOutputComponent.getStdOutStream();
			System.setOut(stdOutStream);
			AuthoringTool.pyStdOut = new org.python.core.PyFile(stdOutStream);
		}
		if (stdErrToConsole) {
			AuthoringTool.pyStdErr = new org.python.core.PyFile(System.err);
		} else {
			java.io.PrintStream stdErrStream = stdErrOutputComponent.getStdErrStream();
			System.setErr(stdErrStream);
			AuthoringTool.pyStdErr = new org.python.core.PyFile(stdErrStream);
		}
	}

	public JAliceFrame getJAliceFrame() {
		return jAliceFrame;
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.DefaultScheduler getScheduler() {
		return scheduler;
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler getOneShotScheduler() {
		return oneShotScheduler;
	}

	public MainUndoRedoStack getUndoRedoStack() {
		return undoRedoStack;
	}

	public Actions getActions() {
		return actions;
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.Configuration getConfig() {
		return authoringToolConfig;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory getRenderTargetFactory() {
		if( renderTargetFactory == null ) {
			Class rendererClass = null;
			boolean isSoftwareEmulationForced = false;
			try {
				String[] renderers = authoringToolConfig.getValueList( "rendering.orderedRendererList" ); 
				rendererClass = Class.forName( renderers[ 0 ] );
			} catch( Throwable t ) {
				//todo: inform user of configuration problem?
				//pass
			}
			try {
				String s = authoringToolConfig.getValue( "rendering.forceSoftwareRendering" ); 
				if( s != null ) {
					isSoftwareEmulationForced = s.equals( "true" ); 
				}
			} catch( Throwable t ) {
				//todo: inform user of configuration problem?
				//pass
			}
			String commandLineOption = System.getProperty( "alice.forceSoftwareRendering" ); 
			if( commandLineOption != null && commandLineOption.equalsIgnoreCase( "true" ) ) { 
				isSoftwareEmulationForced = true;
			}
			renderTargetFactory = new edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory( rendererClass );
			renderTargetFactory.setIsSoftwareEmulationForced( isSoftwareEmulationForced );
		}
		return renderTargetFactory;
	}

	public Object getContext() {
		//TODO
		return null;
	}

	public void setContext(Object context) {
		//TODO
	}

	public edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent getStdOutOutputComponent() {
		return stdOutOutputComponent;
	}

	public edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent getStdErrOutputComponent() {
		return stdErrOutputComponent;
	}

	public boolean isStdOutToConsole() {
		return stdOutToConsole;
	}

	public boolean isStdErrToConsole() {
		return stdErrToConsole;
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel getWatcherPanel() {
		return watcherPanel;
	}

	public EditorManager getEditorManager() {
		return editorManager;
	}

	///////////////
	// Selection
	///////////////

	public void setSelectedElement(edu.cmu.cs.stage3.alice.core.Element element) {
		if (element == null) { // is this too much of a hack?
			element = getWorld();
		}
		if (this.selectedElement != element) {
			this.selectedElement = element;
			fireElementSelected(element);
		}
	}

	public edu.cmu.cs.stage3.alice.core.Element getSelectedElement() {
		return selectedElement;
	}

	public void addElementSelectionListener(edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener listener) {
		selectionListeners.add(listener);
	}

	public void removeElementSelectionListener(edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener listener) {
		selectionListeners.remove(listener);
	}

	protected void fireElementSelected(edu.cmu.cs.stage3.alice.core.Element element) {
		for (java.util.Iterator iter = selectionListeners.iterator(); iter.hasNext();) {
			((edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener) iter.next()).elementSelected(element);
		}
	}

	///////////////////////
	// State listening
	///////////////////////

	public void addAuthoringToolStateListener(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener listener) {
		stateListeners.add(listener);
	}

	public void removeAuthoringToolStateListener(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener listener) {
		stateListeners.remove(listener);
	}

	protected void fireStateChanging(int previousState, int currentState) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.stateChanging(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_an_authoring_tool_state_change_"), t); 
			}
		}
	}

	protected void fireWorldLoading(edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldLoading(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_load_"), t); 
			}
		}
	}

	protected void fireWorldUnLoading(edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldUnLoading(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_unload_"), t); 
			}
		}
	}

	protected void fireWorldStarting(int previousState, int currentState, edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldStarting(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_starting_"), t); 
			}
		}
	}

	protected void fireWorldStopping(int previousState, int currentState, edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldStopping(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_stopping_"), t); 
			}
		}
	}

	protected void fireWorldPausing(int previousState, int currentState, edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldPausing(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_pausing_"), t); 
			}
		}
	}

	protected void fireWorldSaving(edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldSaving(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_saving_"), t); 
			}
		}
	}

	protected void fireStateChanged(int previousState, int currentState) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.stateChanged(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_authoring_tool_state_changed_"), t); 
			}
		}
	}

	protected void fireWorldLoaded(edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldLoaded(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_loaded_"), t); 
			}
		}
	}

	protected void fireWorldUnLoaded(edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldUnLoaded(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_unloaded_"), t); 
			}
		}
	}

	protected void fireWorldStarted(int previousState, int currentState, edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldStarted(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_started_"), t); 
			}
		}
	}

	protected void fireWorldStopped(int previousState, int currentState, edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldStopped(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_stopped_"), t); 
			}
		}
	}

	protected void fireWorldPaused(int previousState, int currentState, edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldPaused(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_paused_"), t); 
			}
		}
	}

	protected void fireWorldSaved(edu.cmu.cs.stage3.alice.core.World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter.next();
			try {
				listener.worldSaved(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_saved_"), t); 
			}
		}
	}

	//////////////////////////////
	// Editors
	//////////////////////////////

	public void editObject(Object object) {
		editObject(object, true);
	}

	public void editObject(Object object, boolean switchToNewTab) {
		Class editorClass = null;
		if (object != null) {
			editorClass = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(object.getClass());
		}
		editObject(object, editorClass, switchToNewTab);
	}

	public void editObject(Object object, Class editorClass, boolean switchToNewTab) {
		jAliceFrame.getTabbedEditorComponent().editObject(object, editorClass, switchToNewTab);
		saveTabs();
		if (switchToNewTab && (getJAliceFrame().getGuiMode() != JAliceFrame.SCENE_EDITOR_SMALL_MODE)) {
			getJAliceFrame().setGuiMode(JAliceFrame.SCENE_EDITOR_SMALL_MODE);
		}
	}

	public void editObject(final Object object, javax.swing.JComponent componentToAnimateFrom) {
		if (!isObjectBeingEdited(object)) {
			animateEditOpen(componentToAnimateFrom);
		}
		editObject(object);
		//		edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
		//			public Object construct() {
		//				editObject( object );
		//				return null;
		//			}
		//		};
		//		worker.start();
	}

	public void editObject(final Object object, final boolean switchToNewTab, javax.swing.JComponent componentToAnimateFrom) {
		if (!isObjectBeingEdited(object)) {
			animateEditOpen(componentToAnimateFrom);
		}
		editObject(object, switchToNewTab);
		//		edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
		//			public Object construct() {
		//				editObject( object, switchToNewTab );
		//				return null;
		//			}
		//		};
		//		worker.start();
	}

	public void editObject(final Object object, final Class editorClass, final boolean switchToNewTab, javax.swing.JComponent componentToAnimateFrom) {
		if (!isObjectBeingEdited(object)) {
			animateEditOpen(componentToAnimateFrom);
		}
		editObject(object, editorClass, switchToNewTab);
		//		edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
		//			public Object construct() {
		//				editObject( object, editorClass, switchToNewTab );
		//				return null;
		//			}
		//		};
		//		worker.start();
	}

	protected void animateEditOpen(javax.swing.JComponent componentToAnimateFrom) {
		java.awt.Rectangle sourceBounds = componentToAnimateFrom.getBounds();
		java.awt.Point sourceLocation = sourceBounds.getLocation();
		javax.swing.SwingUtilities.convertPointToScreen(sourceLocation, componentToAnimateFrom);
		sourceBounds.setLocation(sourceLocation);
		java.awt.Rectangle targetBounds = jAliceFrame.getTabbedEditorComponent().getBounds();
		java.awt.Point targetLocation = targetBounds.getLocation();
		javax.swing.SwingUtilities.convertPointToScreen(targetLocation, jAliceFrame.getTabbedEditorComponent());
		targetBounds.setLocation(targetLocation);
		java.awt.Color color = componentToAnimateFrom.getBackground();
		rectangleAnimator.animate(sourceBounds, targetBounds, color);
	}

	public Object getObjectBeingEdited() {
		return jAliceFrame.getTabbedEditorComponent().getObjectBeingEdited();
	}

	public Object[] getObjectsBeingEdited() {
		return jAliceFrame.getTabbedEditorComponent().getObjectsBeingEdited();
	}

	public boolean isObjectBeingEdited(Object object) {
		return jAliceFrame.getTabbedEditorComponent().isObjectBeingEdited(object);
	}

	///////////////////////////
	// General Functionality
	///////////////////////////

	private int showStartUpDialog(int tabID) {
		int retVal = askForSaveIfNecessary();
		if( retVal != Constants.CANCELED ) {
			startUpContentPane.setTabID(tabID);
			if (edu.cmu.cs.stage3.swing.DialogManager.showDialog(startUpContentPane) == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
				java.io.File file = startUpContentPane.getFile();
				if (startUpContentPane.isTutorial()) {
					launchTutorialFile( file );
				} else {
					//loadWorld(file, startUpContentPane.isSaveNeeded());
					loadWorld(file, false);
				}
				resetClipboards();
				watcherPanel.clear();
			}
			return Constants.SUCCEEDED;
		} 
		return retVal;
	}
	
	public void resetClipboards() {
		try {
			final int numClipboards = Integer.parseInt( authoringToolConfig.getValue( "numberOfClipboards" ) ); 
			jAliceFrame.clipboardPanel.removeAll();//.remove(0);	// Aik Min - need work
			for( int i = 0; i < numClipboards; i++ ) {
				jAliceFrame.clipboardPanel.add( new edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard() );
			}
			jAliceFrame.clipboardPanel.revalidate();
			jAliceFrame.clipboardPanel.repaint();
		} catch( NumberFormatException e ) {
			AuthoringTool.showErrorDialog( Messages.getString("illegal_number_of_clipboards__", authoringToolConfig.getValue( "numberOfClipboards" )), null );  
		}
	}
	
	public int newWorld() {
		return showStartUpDialog( edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.TEMPLATE_TAB_ID );
	}
	public int openWorld() {
		return showStartUpDialog( edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.OPEN_TAB_ID );
	}
	public int openExampleWorld() {
		return showStartUpDialog( edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.EXAMPLE_TAB_ID );
	}
	public int openTutorialWorld() {	
		return showStartUpDialog(edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.TUTORIAL_TAB_ID);
	}

	public int saveWorld() {
		if ((currentWorldLocation != null) && shouldAllowOverwrite(currentWorldLocation) && currentWorldLocation.canWrite()) {
			try {
				return saveWorldToFile(currentWorldLocation, false);
			} catch (java.io.IOException e) {
				AuthoringTool.showErrorDialog(Messages.getString("Unable_to_save_world__", currentWorldLocation.getAbsolutePath()), e); 
				return Constants.FAILED;
			}
		} 
		return saveWorldAs();
	}

	public int saveWorldAs() {
		int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showSaveDialog( saveWorldFileChooser );
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			java.io.File file = saveWorldFileChooser.getSelectedFile();
			/*if (!saveWorldFileChooser.getCurrentDirectory().canWrite()){
				AuthoringTool.showErrorDialog("Unable to save world" , "The folder " + saveWorldFileChooser.getCurrentDirectory() + " isn't accessible.");
				file.delete();
				return Constants.FAILED;
			}*/
			if (!file.getName().endsWith("." + WORLD_EXTENSION)) { 
				file = new java.io.File(file.getParent(), file.getName() + "." + WORLD_EXTENSION); 
			}
			try {
				return saveWorldToFile(file, false);
			} catch (java.io.IOException e) {
				AuthoringTool.showErrorDialog(Messages.getString("Unable_to_save_world__", file), e); 
				file.delete();
				return Constants.FAILED;
			}
		} else if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			return Constants.CANCELED;
		} else {
			return Constants.FAILED;
		}
	}

	private void finalCleanUp() {
		try {
			java.awt.Rectangle bounds = jAliceFrame.getBounds();
			authoringToolConfig.setValue("mainWindowBounds", bounds.x + ", " + bounds.y + ", " + bounds.width + ", " + bounds.height);    //$NON-NLS-4$
			edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.storeConfig();
			renderTargetFactory.release();
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_encountered_during_final_cleanup_"), t); 
		}
	}

	public int quit(boolean condition) {
		try {
			int retVal = leaveWorld(true);
			if (retVal == Constants.SUCCEEDED) {
				finalCleanUp();
				java.io.File temp = new java.io.File(authoringToolConfig.getValue("directories.charactersDirectory"));
				if (temp.exists() && !(temp.list().length>0)){
					temp.delete();
				}
				java.io.File aliceHasNotExitedFile = new java.io.File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt"); 
				aliceHasNotExitedFile.delete();
				java.io.BufferedInputStream bis = null;
				java.io.BufferedOutputStream bos = null;
				java.io.File aliceJAR;
				aliceJAR = new java.io.File(JAlice.getAliceHomeDirectory().toString() + File.separator + "lib" + File.separator + "aliceupdate.jar"); 
				if (aliceJAR.exists()) {
		            try {      
						bis = new java.io.BufferedInputStream( new java.io.FileInputStream( aliceJAR ) );
						bos = new java.io.BufferedOutputStream( new java.io.FileOutputStream( new java.io.File(JAlice.getAliceHomeDirectory().toString() + File.separator + "lib" + File.separator + "alice.jar") ) ); 
			            int i;
			            while ((i = bis.read()) != -1) {
			            	bos.write( i );
			            }      
					} catch (Exception e1){
						e1.printStackTrace();
					} finally {
						 if (bis != null)
			             try {
			            	 bis.close();
			            	 aliceJAR.delete();
			             } catch (java.io.IOException ioe) {
			                 ioe.printStackTrace();
			             }
			             if (bos != null)
			             try {
			                 bos.close();
			             } catch (java.io.IOException ioe) {
			                 ioe.printStackTrace();
			             }             
					}
				}
				// Try to auto-restart if Alice require a restart
				if (condition) {
					try {
						// Restart for Windows
						if (AikMin.isWindows()){
							String file = JAlice.getAliceHomeDirectory().getParent().toString()+"\\Alice.exe";
							if (new java.io.File(file).exists()) {
								Runtime.getRuntime().exec( file );
							} else {
								edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Missing Alice.exe in Alice directory. Please restart Alice manually.");
							}
						} 
						// Restart for Mac
						else if (AikMin.isMAC()){
							String decodedPath = java.net.URLDecoder.decode(JAlice.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
							decodedPath=decodedPath.substring(0, decodedPath.lastIndexOf(".app")+4);
							String params[] = {"open", "-n", decodedPath };
							Runtime.getRuntime().exec(params);
						} 
						// Restart for Linux - Ubuntu
						else {
							String file = JAlice.getAliceHomeDirectory().getParent().toString()+"/Required/run-alice";
							if (new java.io.File(file).exists()) {
								try {
									Runtime.getRuntime().exec( file );
								} catch (Exception e){
									e.printStackTrace();
								}
							} else {
								edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Missing Alice executable in Alice directory. Please restart Alice manually.");
							}
						}							
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.exit(0);
				return Constants.SUCCEEDED; // never reached
			} else if (retVal == Constants.CANCELED) {
				return Constants.CANCELED;
			} else if (retVal == Constants.FAILED) {
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("Alice_failed_to_correctly_save_and_or_close_the_current_world___Would_you_still_like_to_quit_")); 
				if (result == javax.swing.JOptionPane.YES_OPTION) {
					finalCleanUp();
					System.exit(1);
				}
			}
		} catch (Throwable t) {
			try {
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("Error_encountered_while_attempting_to_close_Alice___Would_you_like_to_force_the_close_") ); // TODO: give information about the error 
				if (result == javax.swing.JOptionPane.YES_OPTION) {
					finalCleanUp();
					System.exit(1);
				}
			} catch (Throwable t2) {
				finalCleanUp();
				System.exit(1);
			}
		}
		return Constants.FAILED;
	}

	public int askForSaveIfNecessary() {
		if (worldHasBeenModified) {
			if ((currentWorldLocation != null) && currentWorldLocation.getAbsolutePath().startsWith(getTutorialDirectory().getAbsolutePath())) { // skip tutorial worlds
				return Constants.SUCCEEDED;
			} else if ((currentWorldLocation == null) || (!shouldAllowOverwrite(currentWorldLocation))) {
				String question = Messages.getString("The_world_has_not_been_saved___Would_you_like_to_save_it_"); 
				int n = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(question, Messages.getString("Save_World_"), javax.swing.JOptionPane.YES_NO_CANCEL_OPTION); 
				if (n == javax.swing.JOptionPane.YES_OPTION) {
					int retVal = saveWorldAs();
					if (retVal == Constants.CANCELED) {
						return askForSaveIfNecessary();
					} else if (retVal == Constants.FAILED) {
						return Constants.FAILED;
					}
				} else if (n == javax.swing.JOptionPane.NO_OPTION) {
					return Constants.SUCCEEDED;
				} else if (n == javax.swing.JOptionPane.CANCEL_OPTION) {
					return Constants.CANCELED;
				} else if (n == javax.swing.JOptionPane.CLOSED_OPTION) {
					return Constants.CANCELED;
				}
			} else {
				String question = Messages.getString("The_world_has_been_modified___Would_you_like_to_save_it_"); 
				int n = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(question, Messages.getString("Save_World_"), javax.swing.JOptionPane.YES_NO_CANCEL_OPTION); 
				if (n == javax.swing.JOptionPane.YES_OPTION) {
					int retVal = saveWorld();
					if (retVal == Constants.CANCELED) {
						return Constants.CANCELED;
					} else if (retVal == Constants.FAILED) {
						return Constants.FAILED;
					}
				} else if (n == javax.swing.JOptionPane.NO_OPTION) {
					return Constants.SUCCEEDED;
				} else if (n == javax.swing.JOptionPane.CANCEL_OPTION) {
					return Constants.CANCELED;
				} else if (n == javax.swing.JOptionPane.CLOSED_OPTION) {
					return Constants.CANCELED;
				}
			}
			worldHasBeenModified = false;
			undoRedoStack.setUnmodified();
		}
		return Constants.SUCCEEDED;
	}

//	public int askForSaveIfNecessaryForMovie() {
//		if (worldHasBeenModified) {
//			if ((currentWorldLocation != null) && currentWorldLocation.getAbsolutePath().startsWith(getTutorialDirectory().getAbsolutePath())) { // skip tutorial worlds
//				return Constants.SUCCEEDED;
//			} else if ((currentWorldLocation == null) || (!shouldAllowOverwrite(currentWorldLocation))) {
//				String question = "The world has not been saved.  You need to save it before making a movie.";
//				int n = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(question, "Save World?", javax.swing.JOptionPane.OK_CANCEL_OPTION);
//				if (n == javax.swing.JOptionPane.YES_OPTION) {
//					int retVal = saveWorldAs();
//					if (retVal == Constants.CANCELED) {
//						return askForSaveIfNecessaryForMovie();
//					} else if (retVal == Constants.FAILED) {
//						return Constants.FAILED;
//					}
//				} else if (n == javax.swing.JOptionPane.CANCEL_OPTION) {
//					return Constants.CANCELED;
//				} else if (n == javax.swing.JOptionPane.CLOSED_OPTION) {
//					return Constants.CANCELED;
//				}
//			} else {
//				String question = "The world has been modified.  You need to save it before making a movie.";
//				int n = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(question, "Save World?", javax.swing.JOptionPane.OK_CANCEL_OPTION);
//				if (n == javax.swing.JOptionPane.YES_OPTION) {
//					int retVal = saveWorld();
//					if (retVal == Constants.CANCELED) {
//						return Constants.CANCELED;
//					} else if (retVal == Constants.FAILED) {
//						return Constants.FAILED;
//					}
//				} else if (n == javax.swing.JOptionPane.CANCEL_OPTION) {
//					return Constants.CANCELED;
//				} else if (n == javax.swing.JOptionPane.CLOSED_OPTION) {
//					return Constants.CANCELED;
//				}
//			}
//			worldHasBeenModified = false;
//			undoRedoStack.setUnmodified();
//		}
//		return Constants.SUCCEEDED;
//	}

	public int leaveWorld(boolean askForSaveIfNecessary) {
		try {
			if (askForSaveIfNecessary) {
				int retVal = askForSaveIfNecessary();
				if (retVal == Constants.CANCELED) {
					return Constants.CANCELED;
				} else if (retVal == Constants.FAILED) {
					return Constants.FAILED;
				}
			}

			fireWorldUnLoading(world);

			saveTabsEnabled = false;
			undoRedoStack.clear();
			jAliceFrame.setWorld(null);
			userDefinedParameterListener.setWorld( null );
			setCurrentWorldLocation(null);
			editObject(null);
			PopupMenuUtilities.clearRecentlyUsedValues();
			if (world != null) {
				world.release();
				fireWorldUnLoaded(world);
			}

			world = null;

			return Constants.SUCCEEDED;
		} catch (Exception e) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_encountered_while_leaving_current_world_"), e); 
			return Constants.FAILED;
		}
	}

	//private edu.cmu.cs.stage3.progress.ProgressObserver m_backupProgressObserver = null;
	public void waitForBackupToFinishIfNecessary( java.io.File file ) {
	}
	public void backupWorld( final java.io.File src, final int maxBackups ) {
	    //if( !edu.cmu.cs.stage3.io.FileUtilities.isFileCopySupported() ) {
			new Thread() {
				public void run() {
					java.io.File parentDir = src.getParentFile();
					String name = src.getName();
					if( name.endsWith( ".a2w" ) ) { 
						name = name.substring( 0, name.length()-4 );
					}
					java.io.File dstDir = new java.io.File( parentDir, Messages.getString("Backups_of_", name )); 

					StringBuffer sb = new StringBuffer();
					java.util.Calendar calendar = java.util.Calendar.getInstance();
					sb.append( name );
					sb.append( " " );
					sb.append( Messages.getString("backed_up_on_") + " "); 
					switch( calendar.get( java.util.Calendar.MONTH ) ) {
					case java.util.Calendar.JANUARY:
						sb.append( Messages.getString("Jan_")); sb.append( " " );   
						break;
					case java.util.Calendar.FEBRUARY:
						sb.append( Messages.getString("Feb_")); sb.append( " " );  
						break;
					case java.util.Calendar.MARCH:
						sb.append( Messages.getString("Mar_")); sb.append( " " ); 
						break;
					case java.util.Calendar.APRIL:
						sb.append( Messages.getString("Apr_")); sb.append( " " ); 
						break;
					case java.util.Calendar.MAY:
						sb.append( Messages.getString("May_")); sb.append( " " ); 
						break;
					case java.util.Calendar.JUNE:
						sb.append( Messages.getString("Jun_")); sb.append( " " ); 
						break;
					case java.util.Calendar.JULY:
						sb.append( Messages.getString("Jul_")); sb.append( " " ); 
						break;
					case java.util.Calendar.AUGUST:
						sb.append( Messages.getString("Aug_")); sb.append( " " ); 
						break;
					case java.util.Calendar.SEPTEMBER:
						sb.append( Messages.getString("Sep_")); sb.append( " " ); 
						break;
					case java.util.Calendar.OCTOBER:
						sb.append( Messages.getString("Oct_")); sb.append( " " ); 
						break;
					case java.util.Calendar.NOVEMBER:
						sb.append( Messages.getString("Nov_")); sb.append( " " ); 
						break;
					case java.util.Calendar.DECEMBER:
						sb.append( Messages.getString("Dec_")); sb.append( " " ); 
						break;
					}
					sb.append( calendar.get( java.util.Calendar.DAY_OF_MONTH ) );
					sb.append( " " ); 
					sb.append( calendar.get( java.util.Calendar.YEAR ) );
					sb.append( " " );
					sb.append( Messages.getString("at_")); sb.append( " " ); 
					sb.append( calendar.get( java.util.Calendar.HOUR ) );
					sb.append( "h" ); 
					sb.append( calendar.get( java.util.Calendar.MINUTE ) );
					sb.append( "m" ); 
					sb.append( calendar.get( java.util.Calendar.SECOND ) );
					switch( calendar.get( java.util.Calendar.AM_PM ) ) {
					case java.util.Calendar.AM:
						sb.append( "s AM" ); 
						break;
					case java.util.Calendar.PM:
						sb.append( "s PM" ); 
						break;
					}
					sb.append(".a2w");

					java.io.File dst = new java.io.File( dstDir, sb.toString() );
					//m_backupProgressObserver = new edu.cmu.cs.stage3.progress.ProgressObserver();
					if( edu.cmu.cs.stage3.io.FileUtilities.isFileCopySupported() ) {				//Aik Min - Make a version for MAC
						try {
							edu.cmu.cs.stage3.io.FileUtilities.copy( src, dst, true );
							java.io.File[] siblings = dstDir.listFiles( new java.io.FilenameFilter() {
								public boolean accept( java.io.File dir, String name ) {
									return name.endsWith( ".a2w" ); 
								}
							} );
							if( siblings.length > maxBackups ) {
								java.io.File fileToDelete = siblings[ 0 ];
								long fileToDeleteLastModified = fileToDelete.lastModified();
								for( int i=1; i<siblings.length; i++ ) {
									long lastModified = siblings[ i ].lastModified();
									if( lastModified < fileToDeleteLastModified ) {
										fileToDelete = siblings[ i ];
										fileToDeleteLastModified = lastModified;
									}
								}
								fileToDelete.delete();
							}
						} finally {
							//m_backupProgressObserver = null;
						}
					} else {
						//Copy the current file to the BACKUP directory
						if (!dst.exists()) {
							dst.getParentFile().mkdirs();
						}
						try {
							java.io.InputStream in = new java.io.FileInputStream(src);
							java.io.OutputStream out = new java.io.FileOutputStream(dst); 
			 
			    	        byte[] buffer = new byte[1024];
			 
			    	        int length;
			    	        //copy the file content in bytes 
			    	        while ((length = in.read(buffer)) > 0){
			    	    	   out.write(buffer, 0, length);
			    	        }
			 
			    	        in.close();
			    	        out.close();
						} catch (Exception e) 
						{}
						//Delete older file if total BACKUP file > maxBackups
						java.io.File[] siblings = dstDir.listFiles( new java.io.FilenameFilter() {
							public boolean accept( java.io.File dir, String name ) {
								return name.endsWith( ".a2w" ); 
							}
						} );
						if( siblings.length > maxBackups ) {
							java.io.File fileToDelete = siblings[ 0 ];
							long fileToDeleteLastModified = fileToDelete.lastModified();
							for( int i=1; i<siblings.length; i++ ) {
								long lastModified = siblings[ i ].lastModified();
								if( lastModified < fileToDeleteLastModified ) {
									fileToDelete = siblings[ i ];
									fileToDeleteLastModified = lastModified;
								}
							}
							fileToDelete.delete();
						}
					}
				}
			}.start();
	    //}
	}
	

	public int saveWorldToFile(java.io.File file, boolean saveForWeb) throws java.io.IOException {
		if (file.exists() && (!file.canWrite())) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("Cannot_save_world___is_read_only_", file.getAbsolutePath()), Messages.getString("Cannot_Save_World"), javax.swing.JOptionPane.ERROR_MESSAGE);   
			return Constants.FAILED;
		}
		if (saveForWeb && file.exists()) {
			file.delete();
		}
		if (file.exists()) {
			worldStoreProgressPane.setIsCancelEnabled(false);
		} else {
			worldStoreProgressPane.setIsCancelEnabled(true);
			waitForBackupToFinishIfNecessary( file );
		}
		fireWorldSaving(world);

		worldDirectory = null;
		boolean tempListening = AuthoringTool.this.getUndoRedoStack().getIsListening();
		undoRedoStack.setIsListening( false );
		try {
			// save which tabs are open
			saveTabsEnabled = true;
			saveTabs();

			// save count
			countSomething("edu.cmu.cs.stage3.alice.authoringtool.saveCount"); 

			// world open time
			updateWorldOpenTime();

			// store the world
			java.util.Dictionary map = new java.util.Hashtable();
			if (authoringToolConfig.getValue("saveThumbnailWithWorld").equalsIgnoreCase("true")) {  
				try {
					edu.cmu.cs.stage3.alice.core.Camera[] cameras = (edu.cmu.cs.stage3.alice.core.Camera[]) world.getDescendants(edu.cmu.cs.stage3.alice.core.Camera.class);
					if (cameras.length > 0) {
						edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget rt = getRenderTargetFactory().createOffscreenRenderTarget();
						rt.setSize(120, 90);
						rt.addCamera(cameras[0].getSceneGraphCamera());
						rt.clearAndRenderOffscreen();
						java.awt.Image image = rt.getOffscreenImage();
						if (image != null) {
							java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
							edu.cmu.cs.stage3.image.ImageIO.store("png", baos, image); 
							map.put("thumbnail.png", baos.toByteArray()); 
						} 
						rt.release();
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			worldStoreProgressPane.setElement( world );
			worldStoreProgressPane.setFile( file );
			worldStoreProgressPane.setFilnameToByteArrayMap( map );
			int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog( worldStoreProgressPane );
			if( result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION ) {
				if( worldStoreProgressPane.wasSuccessful() ) {
					jAliceFrame.HACK_standDownFromRedAlert();
					worldHasBeenModified = false;
					undoRedoStack.setUnmodified();
					if (!file.equals(defaultWorld) && !isTemplateWorld(file.getAbsolutePath())) {
						setCurrentWorldLocation(file);

						if (file.isDirectory()) {
							worldDirectory = file;
						}
						if( saveForWeb ) {
							//pass
						} else {
							jAliceFrame.updateRecentWorlds( file.getAbsolutePath() );
							int backupCount = 0;
							try {
								backupCount = Integer.parseInt( authoringToolConfig.getValue( "maximumWorldBackupCount" ) ); 
							} catch( Throwable t ) {
								t.printStackTrace();
							}
							if( backupCount > 0 ) {
								backupWorld( file, backupCount );
							}
						}
					} else {
						setCurrentWorldLocation(null);
					}
					fireWorldSaved(world);
					return Constants.SUCCEEDED;
				}
				return Constants.FAILED;
			} 
			file.delete();
			return Constants.CANCELED;
		} catch( Throwable t ) {
			AuthoringTool.showErrorDialog( Messages.getString("Unable_to_store_world_to_file__", file), t ); 
			//file.delete();
			return Constants.FAILED;
		} finally {
			undoRedoStack.setIsListening( tempListening );
		}
	}

	public void saveForWeb() {
		if (edu.cmu.cs.stage3.swing.DialogManager.showDialog(saveForWebContentPane) == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			java.io.File directory = saveForWebContentPane.getExportDirectory();
			if (!directory.exists()) {
				directory.mkdir();
			}
			String fileName = saveForWebContentPane.getExportFileName();
			java.io.File file = new java.io.File(directory, fileName);
			int width = saveForWebContentPane.getExportWidth();
			int height = saveForWebContentPane.getExportHeight();
			String authorName = saveForWebContentPane.getExportAuthorName();

			try {
				String htmlCode = null;
				if (saveForWebContentPane.isCodeToBeExported()) {
					StringBuffer buffer = new StringBuffer();
					exportCodeForPrintingContentPane.initialize(authorName);
					exportCodeForPrintingContentPane.getHTML(buffer, file, false, true, null);
					htmlCode = buffer.toString();
				}
				saveWorldForWeb(file, width, height, authorName, htmlCode);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(Messages.getString("Error_saving_for_the_web_"), t); 
			}
		}
	}

	public int saveWorldForWeb(java.io.File htmlFile, int width, int height, String authorName, String code) throws java.io.IOException {
		String baseName = htmlFile.getName();
		int dotIndex = htmlFile.getName().lastIndexOf("."); 
		if (dotIndex > 0) {
			baseName = htmlFile.getName().substring(0, dotIndex);
		}

		java.io.File worldFile = new java.io.File(htmlFile.getParentFile(), baseName + "." + WORLD_EXTENSION); 

		if (htmlFile.exists() && (!htmlFile.canWrite())) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("Cannot_save_web_page___is_read_only_", htmlFile.getAbsolutePath()), Messages.getString("Cannot_Save"), javax.swing.JOptionPane.ERROR_MESSAGE);   
			return Constants.FAILED;
		}
		if (worldFile.exists() && (!worldFile.canWrite())) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("Cannot_save_world___is_read_only_", worldFile.getAbsolutePath()), Messages.getString("Cannot_Save"), javax.swing.JOptionPane.ERROR_MESSAGE);   
			return Constants.FAILED;
		}
		java.util.HashMap replacements = new java.util.HashMap();
		replacements.put("__worldname__", baseName); 
		replacements.put("__code__", code == null ? " " : code); 
		replacements.put("__authorname__", authorName == null ? " " : "<h2>" + Messages.getString("Created_by_", authorName) + "</h2>\n"); 
		replacements.put("__worldfile__", worldFile.getName()); 
		replacements.put("__width__", Integer.toString(width)); 
		replacements.put("__height__", Integer.toString(height)); 
		//replacements.put("__language__", AikMin.locale); 

		// write web page
		java.io.File templateFile = new java.io.File(JAlice.getAliceHomeDirectory(), "etc/appletTemplate.html"); 
		if (!templateFile.exists()) {
			templateFile.createNewFile();
		}
		java.io.BufferedReader templateReader = new java.io.BufferedReader(new java.io.FileReader(templateFile));
		java.io.PrintWriter webPageWriter = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(htmlFile)));

		String line = templateReader.readLine();
		while (line != null) {
			for (java.util.Iterator iter = replacements.keySet().iterator(); iter.hasNext();) {
				String from = (String) iter.next();
				String to = (String) replacements.get(from);
				while (line.indexOf(from) > 0) {
					line = line.substring(0, line.indexOf(from)) + to + line.substring(line.indexOf(from) + from.length());
				}
			}
			webPageWriter.println(line);
			line = templateReader.readLine();
		}
		templateReader.close();
		webPageWriter.flush();
		webPageWriter.close();

		// write world
		int saveWorldResult = saveWorldToFile(worldFile, true);
		if (saveWorldResult != Constants.SUCCEEDED) {
			return saveWorldResult;
		}

		// write applet
		java.io.File appletSourceFile = new java.io.File(JAlice.getAliceHomeDirectory(), "etc/aliceapplet.jar"); 
		java.io.File appletDestinationFile = new java.io.File(htmlFile.getParentFile(), "aliceapplet.jar"); 
		AuthoringToolResources.copyFile(appletSourceFile, appletDestinationFile);

		return Constants.SUCCEEDED;
	}

	public int loadWorld(String filename, boolean askForSaveIfNecessary) {
		return loadWorld(new java.io.File(filename), askForSaveIfNecessary);
	}

	public int loadWorld(final java.io.File file, boolean askForSaveIfNecessary) {
		int result;
		if (file.getAbsoluteFile().isFile()) {
			result = loadWorld(new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file, askForSaveIfNecessary);
		} else if (file.isDirectory()) {
			result = loadWorld(new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file, askForSaveIfNecessary);
		} else {
			AuthoringTool.showErrorDialog(Messages.getString("The_file_or_directory_is_not_valid__", file), null); 
			result = Constants.FAILED;
		}
		return result;
	}

	public boolean isTemplateWorld(String filename) {
		return filename.startsWith(getTemplateWorldsDirectory().getAbsolutePath());
	}

	public void remove(edu.cmu.cs.stage3.alice.core.Element temp){
		if (temp instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray) {						
			((edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray) temp).removeVertices();
		} else {
			for (int ii=0; ii<temp.getChildCount(); ii++){
				edu.cmu.cs.stage3.alice.core.Element element = temp.getChildAt(ii);
				remove(element);
			}		
		} 			
	}
	
	public void cleanup(){
		// DEBUG - Unload world
		//finalCleanUp();
		if (world != null) {
			edu.cmu.cs.stage3.alice.core.Element temp;
			for (int i=0; i<world.getChildCount(); i++){
				temp = world.getChildAt(i);
				remove(temp);
			}	
		}
		//leaveWorld(false);
		//edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer.clearMap = true;
		
/*		if (world != null){
			edu.cmu.cs.stage3.alice.core.Element temp;
			for (int i=0; i<world.getChildCount(); i++){
				temp = world.getChildAt(i);
				if (temp instanceof edu.cmu.cs.stage3.alice.core.Model) {
					if ( temp.getChildAt(0) != null){
						edu.cmu.cs.stage3.alice.core.Element element = temp.getChildAt(0);
						if (element instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray) {						
							((edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray) element).removeVertices();
						} 
					}						
				} 			
			}	
			edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer.clearMap = true;	// clear m_vertices from IndexedTriangleArrayProxy			
			
			fireWorldUnLoading(world);

			saveTabsEnabled = false;
			undoRedoStack.clear();
			jAliceFrame.setWorld(null);
			userDefinedParameterListener.setWorld( null );
			setCurrentWorldLocation(null);
			editObject(null);
			PopupMenuUtilities.clearRecentlyUsedValues();
			if (world != null) {
				world.release();
				fireWorldUnLoaded(world);
			}

			world = null;
			renderTargetFactory.release();
			
			editorManager = new edu.cmu.cs.stage3.alice.authoringtool.EditorManager(this);
			scheduler = new edu.cmu.cs.stage3.alice.authoringtool.util.DefaultScheduler();
			undoRedoStack = new MainUndoRedoStack(this);
			oneShotScheduler = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler();
			miscInit();	
		}
		*/
	}
	
	public int loadWorld(final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, Object path, boolean askForSaveIfNecessary) {
		worldLoadProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane(Messages.getString("Loading_World___"), Messages.getString("Loading__"));  
		if (askForSaveIfNecessary) {
			int retVal = askForSaveIfNecessary();
			if (retVal == Constants.CANCELED) {
				return Constants.CANCELED;
			} else if (retVal == Constants.FAILED) {
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("Alice_failed_to_correctly_save_the_current_world___Would_you_still_like_to_load_a_new_world_")); 
				if (result != javax.swing.JOptionPane.YES_OPTION) {
					return Constants.CANCELED;
				}
			}
		}
		
		java.awt.Cursor prevCursor = getJAliceFrame().getCursor();
		getJAliceFrame().setCursor( java.awt.Cursor.getPredefinedCursor( java.awt.Cursor.WAIT_CURSOR )); // Aik Min
		
		fireWorldLoading(null);
		worldDirectory = null;
		edu.cmu.cs.stage3.alice.core.World tempWorld = null;
		try {
			loader.open(path);
			try {
				if (path.equals(defaultWorld)) {
					tempWorld = (edu.cmu.cs.stage3.alice.core.World)edu.cmu.cs.stage3.alice.core.Element.load( loader, null, null );		
				} else {
					worldLoadProgressPane.setLoader( loader );
					worldLoadProgressPane.setExternalRoot( null );
					edu.cmu.cs.stage3.swing.DialogManager.showDialog( worldLoadProgressPane );
					tempWorld = (edu.cmu.cs.stage3.alice.core.World)worldLoadProgressPane.getLoadedElement();
					worldLoadProgressPane = null;
				}
			} finally {
				loader.close();
			}
		} catch( Throwable t ) {
			AuthoringTool.showErrorDialog(Messages.getString("Unable_to_load_world__", path), t); 
		}

		if (tempWorld != null) {
			cleanup();
			world = tempWorld;
			worldClock.setWorld( world );
			world.setClock( worldClock );
			world.setScriptingFactory(scriptingFactory);
			worldLoadedTime = System.currentTimeMillis();
			jAliceFrame.setWorld(world);
			userDefinedParameterListener.setWorld( world );
			world.setRenderTargetFactory(getRenderTargetFactory());

			//edu.cmu.cs.stage3.alice.core.Element[] elements = world.search(new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(edu.cmu.cs.stage3.alice.core.RenderTarget.class));
			edu.cmu.cs.stage3.alice.core.Element[] elements = world.getDescendants( edu.cmu.cs.stage3.alice.core.RenderTarget.class );
			if (elements.length > 0) {
				renderPanel.removeAll();
				renderTarget = (edu.cmu.cs.stage3.alice.core.RenderTarget) elements[0];
				renderPanel.add(renderTarget.getAWTComponent(), java.awt.BorderLayout.CENTER);
				renderPanel.revalidate();
				renderPanel.repaint();
			}

			setSelectedElement(world);

			loadTabs();
			if (!world.responses.isEmpty()) {
				editObject(world.responses.get(0), true);
			}

			if ((!path.equals(defaultWorld)) && (path instanceof java.io.File) && !isTemplateWorld(((java.io.File) path).getAbsolutePath())) {
				setCurrentWorldLocation(((java.io.File) path));

				if (((java.io.File) path).isDirectory()) {
					worldDirectory = (java.io.File) path;
				}
				jAliceFrame.updateRecentWorlds(((java.io.File) path).getAbsolutePath());
			} else {
				setCurrentWorldLocation(null);
			}
	
//			undoRedoStack.setUnmodified();	// replaced with undoRedoStack.clear();
			undoRedoStack.clear();
			fireWorldLoaded(world);

			getJAliceFrame().setCursor( prevCursor );
				
/*			try{
					
			} finally {
				tempWorld = null;
				javax.swing.SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						getJAliceFrame().setCursor( prevCursor );
					}
				} );
			}*/
			return Constants.SUCCEEDED;
		} 
		return Constants.FAILED;
	}

	protected boolean shouldAllowOverwrite(java.io.File file) {
		if (file != null) {
			if (file.getAbsolutePath().startsWith(getTutorialDirectory().getAbsolutePath())) {
				return false;
			} else if (file.getAbsolutePath().startsWith(getExampleWorldsDirectory().getAbsolutePath())) {
				return false;
			} else if (isTemplateWorld(file.getAbsolutePath())) {
				return false;
			}
		}
		return true;
	}

	public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter() {
		//		addCharacterFileDialog.setVisible( true );
		//		AuthoringToolResources.centerComponentOnScreen( addCharacterFileDialog );
		//		if( addCharacterFileDialog.getFile() != null ) {
		//			String filename = addCharacterFileDialog.getFile();
		//			java.io.File openFile = new java.io.File( addCharacterFileDialog.getDirectory(), filename );
		//			return loadCharacter( openFile );
		//		} else {
		//			return Constants.CANCELED;
		//		}

		edu.cmu.cs.stage3.alice.core.Element character = null;

		addCharacterFileChooser.rescanCurrentDirectory();
		int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showDialog(addCharacterFileChooser, null);

		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			java.io.File file = addCharacterFileChooser.getSelectedFile();
			character = loadAndAddCharacter(file);
		}

		return character;
	}

	public int add3DText() {
		edu.cmu.cs.stage3.alice.authoringtool.dialog.Add3DTextPanel add3DTextPanel = new edu.cmu.cs.stage3.alice.authoringtool.dialog.Add3DTextPanel();
		//this.setTitle("Add 3D Text");

		if (edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(add3DTextPanel, Messages.getString("Add_3D_Text"), javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE) == javax.swing.JOptionPane.OK_OPTION) { 
			edu.cmu.cs.stage3.alice.core.Text3D text3D = add3DTextPanel.createText3D();
			if (text3D != null) {
				undoRedoStack.startCompound();
				text3D.name.set(AuthoringToolResources.getNameForNewChild(text3D.name.getStringValue(), world));
				world.addChild(text3D);
				world.sandboxes.add(text3D);
				if (getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
					animateAddModel(text3D, world, (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) getCurrentCamera());
				} else {
					text3D.vehicle.set(world);
				}
				undoRedoStack.stopCompound();
				return Constants.SUCCEEDED;
			} 
		} 
		return Constants.CANCELED;
	}
	
	public int exportMovie() {
		// Get Dimensions and Location of Movie
		/*int boundsX=0, boundsY=0, boundsWidth=0, boundsHeight = 0;
		   
		Package authoringToolPackage = Package.getPackage( "edu.cmu.cs.stage3.alice.authoringtool" );
		String dimensions = Configuration.getValue( authoringToolPackage, "rendering.renderWindowBounds" );
		   
		java.util.StringTokenizer st = new java.util.StringTokenizer( dimensions, " \t," );
		if( st.countTokens() == 4 ) {
			boundsX = Integer.parseInt(st.nextToken())+5;
			boundsY = Integer.parseInt(st.nextToken())+65;
			boundsWidth = Integer.parseInt(st.nextToken());
			boundsHeight = Integer.parseInt(st.nextToken());
		}*/
	    if(currentWorldLocation==null)
	    	if(saveWorldAs()!=Constants.SUCCEEDED)
	    		return Constants.CANCELED;
		String directory = currentWorldLocation.getParent();
    	directory = directory.replace('\\', '/');
    	java.io.File dir = new java.io.File(directory +"/frames"); 
    	if (!dir.exists() && !dir.mkdir()){
    		showErrorDialog("Error_creating_temporary_folder_", 
					"Cannot_create_the_frames_folder. You don't have permission. Please save your world in a different location.");
    		return Constants.CANCELED;
    	}
    	
    	soundStorage = new movieMaker.SoundStorage();
    	playWhileEncoding(directory);
		soundStorage = null;
    	if (authoringToolConfig.getValue("rendering.deleteFiles").equalsIgnoreCase("true") == true){  
	    	captureContentPane.removeFiles(directory+"/frames/"); 
	    	dir.delete();
	    	if (dir.exists()){
    			showErrorDialog(Messages.getString("Error_removing_temporary_folder_"), 
    					Messages.getString("Cannot_delete_the_frames_folder___nOne_or_more_files_in_the_folder_is_being_used_") + "\n" +
    					Messages.getString("Try_deleting_the_folder_manually_", dir));  
    		}
		} 		
		return Constants.SUCCEEDED;
	}

	public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(java.net.URL url) {
		return loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipTreeLoader(), url, null);
	}

	public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(java.io.File file) {
		edu.cmu.cs.stage3.alice.core.Element character = null;
		if (file.isFile()) {
			character = loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file, null);
		} else if (file.isDirectory()) {
			character = loadAndAddCharacter(new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file, null);
		} else {
			AuthoringTool.showErrorDialog(Messages.getString("The_file_or_directory_is_not_valid__", file), null); 
		}
		return character;
	}
	
	public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(java.net.URL url, edu.cmu.cs.stage3.math.Matrix44 targetTransformation) {
		return loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipTreeLoader(), url, targetTransformation);
	}

	public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(java.io.File file, edu.cmu.cs.stage3.math.Matrix44 targetTransformation) {
		edu.cmu.cs.stage3.alice.core.Element character = null;
		if (file.isFile()) {
			character = loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file, targetTransformation);
		} else if (file.isDirectory()) {
			character = loadAndAddCharacter(new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file, targetTransformation);
		} else {
			AuthoringTool.showErrorDialog(Messages.getString("The_file_or_directory_is_not_valid__", file), null); 
		}
		return character;
	}

	public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter( edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, Object pathname, edu.cmu.cs.stage3.math.Matrix44 targetTransformation) {
		undoRedoStack.startCompound();

		edu.cmu.cs.stage3.alice.core.Element character = null;
		
		try {
			loader.open(pathname);
			try {
				characterLoadProgressPane.setLoader( loader );
				characterLoadProgressPane.setExternalRoot( world );
				edu.cmu.cs.stage3.swing.DialogManager.showDialog( characterLoadProgressPane );
				character = characterLoadProgressPane.getLoadedElement();
			} finally {
				loader.close();
			}
			if (character != null) {
				addCharacter(character, targetTransformation);
			}
		} catch (java.util.zip.ZipException e) {
			AuthoringTool.showErrorDialog(Messages.getString("File_is_not_a_valid_", CHARACTER_EXTENSION, pathname), e, false);  
		} catch (Exception e) {
			AuthoringTool.showErrorDialog(Messages.getString("Unable_to_load_object__", pathname), e); 
		} finally {
			undoRedoStack.stopCompound();
		}
		return character;
	}

	public void addCharacter(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.math.Matrix44 targetTransformation, edu.cmu.cs.stage3.alice.core.ReferenceFrame asSeenBy) {
		if (element != null) {
			element.name.set(AuthoringToolResources.getNameForNewChild(element.name.getStringValue(), world));

			world.addChild(element);
			world.sandboxes.add(element);
		
			if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				int animateStyle = 0;
				edu.cmu.cs.stage3.alice.core.Transformable model = (edu.cmu.cs.stage3.alice.core.Transformable) element;
				if (targetTransformation != null) {
					try {
						boolean tempListening = AuthoringTool.this.getUndoRedoStack().getIsListening();
						AuthoringTool.this.getUndoRedoStack().setIsListening( false );
						model.vehicle.set(world);
						edu.cmu.cs.stage3.math.Box boundingBox = model.getBoundingBox();
						//javax.vecmath.Vector3d insertionPoint = boundingBox.getCenterOfBottomFace();
						model.setAbsoluteTransformationRightNow(targetTransformation);
						model.moveRightNow( edu.cmu.cs.stage3.math.MathUtilities.negate(boundingBox.getCenterOfBottomFace() ) );
//						model.vehicle.set(null);
						animateStyle = 2;
						AuthoringTool.this.getUndoRedoStack().setIsListening( tempListening );
					} catch (Exception e) {
						animateStyle = 1;
					}
				} else {
					animateStyle = 1;
				}
			
				promptForVisualizationInfo(element);
				if (animateStyle > 0){				
					if (animateStyle == 1 && getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
						animateAddModel(model, world, (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) getCurrentCamera());
					} else if (animateStyle == 2 || !(getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera)){
						animateAddModel(model, world, null);
					}
				}
			}
		} else {
			AuthoringTool.showErrorDialog(Messages.getString("null_Element_encountered"), null); 
		}
	}

	public void addCharacter(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.math.Matrix44 targetTransformation) {
		addCharacter(element, targetTransformation, null);
	}
	
	public void addCharacter(edu.cmu.cs.stage3.alice.core.Element element) {
		addCharacter(element, null, null);
	}

	public void promptForVisualizationInfo(edu.cmu.cs.stage3.alice.core.Element element) {
		if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization) {
			String typeString = Messages.getString("array"); 
			if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization) {
				typeString = Messages.getString("list"); 
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization) {
				typeString = Messages.getString("array"); 
			}
			edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization) element;

			edu.cmu.cs.stage3.alice.authoringtool.util.CollectionEditorPanel collectionEditorPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getCollectionEditorPanel();
			collectionEditorPanel.setCollection(visualization.getItemsCollection());
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(collectionEditorPanel, Messages.getString("Initialize_", typeString), javax.swing.JOptionPane.PLAIN_MESSAGE); 
		}
	}

	private int[] makePixmap(java.awt.Image img) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		int[] pixels = new int[w * h];
		java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			return null;
		}
		if ((pg.getStatus() & java.awt.image.ImageObserver.ABORT) != 0) {
			return null;
		}

		return pixels;
	}
	
	private org.w3c.dom.Document createCharacterXML( edu.cmu.cs.stage3.alice.core.Transformable model ) {
		java.text.DecimalFormat numberFormatter = new java.text.DecimalFormat( "#0.##" );
		javax.xml.parsers.DocumentBuilderFactory factory = null;
		javax.xml.parsers.DocumentBuilder builder = null;
		org.w3c.dom.Document xmlDocument = null;
		try {
			factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			xmlDocument = builder.newDocument();
		} catch (javax.xml.parsers.ParserConfigurationException pce) {
			pce.printStackTrace();
			return null;
		}
		org.w3c.dom.Element xmlModel = null;

		edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
		edu.cmu.cs.stage3.alice.core.util.TextureMapCounter textureMapCounter = new edu.cmu.cs.stage3.alice.core.util.TextureMapCounter();

		model.visit( itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
		model.visit( textureMapCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS );

		xmlModel = xmlDocument.createElement("model");
		org.w3c.dom.Element xmlElement = xmlDocument.createElement("name");
		xmlElement.appendChild( xmlDocument.createTextNode( model.name.getStringValue() ) );
		xmlModel.appendChild(xmlElement);
		if (model.data.get( "modeled by" )!=null && !model.data.get( "modeled by" ).equals("")) {
			xmlElement = xmlDocument.createElement("modeledby");
			xmlElement.appendChild( xmlDocument.createTextNode( model.data.get( "modeled by" ).toString()) );
			xmlModel.appendChild(xmlElement);
		}
		if (model.data.get( "painted by" )!=null && !model.data.get( "painted by" ).equals("")) {
			xmlElement = xmlDocument.createElement("paintedby");
			xmlElement.appendChild( xmlDocument.createTextNode( model.data.get( "painted by" ).toString()) );
			xmlModel.appendChild(xmlElement);
		}
		if (model.data.get( "programmed by" )!=null && !model.data.get( "programmed by" ).equals("")) {
			xmlElement = xmlDocument.createElement("programmedby");
			xmlElement.appendChild( xmlDocument.createTextNode( model.data.get( "programmed by" ).toString()) );
			xmlModel.appendChild(xmlElement);
		}
		xmlElement = xmlDocument.createElement("parts");
		xmlElement.appendChild( xmlDocument.createTextNode(String.valueOf(itaCounter.getIndexedTriangleArrayCount())) );
		xmlModel.appendChild(xmlElement);
		xmlElement = xmlDocument.createElement("physicalsize");
		xmlElement.appendChild( xmlDocument.createTextNode( numberFormatter.format( model.getSize().x ) + "m x " + numberFormatter.format( model.getSize().y ) + "m x " + numberFormatter.format( model.getSize().z ) + "m" ) );
		xmlModel.appendChild(xmlElement);

		org.w3c.dom.Element xmlGroup = xmlDocument.createElement("methods");
		edu.cmu.cs.stage3.alice.core.Element[] listElements = model.responses.getElementArrayValue();
		for (int i=0; i<listElements.length; i++) {
			xmlElement = xmlDocument.createElement("method");
			xmlElement.appendChild(xmlDocument.createTextNode(listElements[i].name.getStringValue()));
			xmlGroup.appendChild(xmlElement);
		}
		xmlModel.appendChild(xmlGroup);

		xmlGroup = xmlDocument.createElement("questions");
		listElements = model.questions.getElementArrayValue();
		for (int i=0; i<listElements.length; i++) {
			xmlElement = xmlDocument.createElement("question");
			xmlElement.appendChild(xmlDocument.createTextNode(listElements[i].name.getStringValue()));
			xmlGroup.appendChild(xmlElement);
		}
		xmlModel.appendChild(xmlGroup);

		xmlGroup = xmlDocument.createElement("sounds");
		listElements = model.sounds.getElementArrayValue();
		for (int i=0; i<listElements.length; i++) {
			xmlElement = xmlDocument.createElement("sound");
			xmlElement.appendChild(xmlDocument.createTextNode(listElements[i].name.getStringValue()));
			xmlGroup.appendChild(xmlElement);
		}
		xmlModel.appendChild(xmlGroup);

		xmlDocument.appendChild( xmlModel );
		xmlDocument.getDocumentElement().normalize();

		return xmlDocument;
	}
	
   	public void saveCharacter(edu.cmu.cs.stage3.alice.core.Element element) {
		String characterFilename = element.name.getStringValue() + "." + CHARACTER_EXTENSION; 
		characterFilename = characterFilename.substring(0, 1).toUpperCase() + characterFilename.substring(1);
		
		saveCharacterFileDialog.setSelectedFile(new java.io.File(characterFilename));
		saveCharacterFileDialog.setVisible(true);
		AuthoringToolResources.centerComponentOnScreen(saveCharacterFileDialog);
		int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showDialog(saveCharacterFileDialog, null);
		
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			final java.io.File file = saveCharacterFileDialog.getSelectedFile();
			saveCharacter(element, file);
		}
	}

	public void saveCharacter(edu.cmu.cs.stage3.alice.core.Element element, java.io.File file) {
		
		edu.cmu.cs.stage3.alice.gallery.batch.BatchSaveWithThumbnails batch = new edu.cmu.cs.stage3.alice.gallery.batch.BatchSaveWithThumbnails();
		edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable) element;
		
		//String elementName = trans.name.getStringValue();
		//String fileName = Character.toUpperCase( elementName.charAt( 0 ) ) + elementName.substring( 1 ) + ".a2c"; 
		java.io.File dst = file; //new java.io.File( dstParent, fileName );
		Object def = trans.vehicle.get();
		trans.vehicle.set( batch.getWorld() );

		java.util.Dictionary map = new java.util.Hashtable();
		batch.m_camera.getAGoodLookAtRightNow( trans );

		edu.cmu.cs.stage3.math.Sphere bs = trans.getBoundingSphere();
		if( (bs != null) && (bs.getCenter() != null) && (bs.getRadius() > 0.0) ) {
			double radius = bs.getRadius();
			double theta = Math.min( batch.m_camera.horizontalViewingAngle.doubleValue(), batch.m_camera.verticalViewingAngle.doubleValue() );
			double farDist = radius/Math.sin( theta/2.0 ) + radius;
			batch.m_camera.farClippingPlaneDistance.set( new Double( farDist ) );
		}

		batch.m_rt.clearAndRenderOffscreen();
		java.awt.Image image = batch.m_rt.getOffscreenImage();

		// Crappy drop shadowing

		java.awt.Image zBufferImage = batch.m_rt.getZBufferImage();

		int clear = 0xFFFFFF00;
		if (zBufferImage != null){
			int width = zBufferImage.getWidth(null);
			int height = zBufferImage.getHeight(null);
			int[] zBuffer = makePixmap(zBufferImage);
			int[] imageBuffer = makePixmap(image);
			double[] shadow = new double[zBuffer.length];
	
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					if (zBuffer[x+y*width]!=clear) {
						for (int i=1; i<=6; i++) {
							double shade = (7 - i) / 10.0;
							if ((x+i+(y+i)*width<zBuffer.length) && zBuffer[x+i+(y+i)*width]==clear && shadow[x+i+(y+i)*width]<shade) {
								shadow[x+i+(y+i)*width]=shade;
							}
						}
					}
				}
			}
	
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					int r = (imageBuffer[x+y*width]>>16)&0xFF;
					int g = (imageBuffer[x+y*width]>>8)&0xFF;
					int b = (imageBuffer[x+y*width])&0xFF;
					r=(int)(r*(1.0-shadow[x+y*width]));
					g=(int)(g*(1.0-shadow[x+y*width]));
					b=(int)(b*(1.0-shadow[x+y*width]));
					imageBuffer[x+y*width] = (0xFF<<24)+(r<<16)+(g<<8)+b;
				}
			}
			image = new java.awt.image.BufferedImage(width,height,java.awt.image.BufferedImage.TYPE_INT_ARGB);
			((java.awt.image.BufferedImage)image).setRGB(0,0,width,height,imageBuffer,0,width);
	
			// end of crappy drop shadow
		}

		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		try {
			edu.cmu.cs.stage3.image.ImageIO.store( "png", baos, image );
			map.put( "thumbnail.png", baos.toByteArray() );
		} catch( java.io.IOException ioe ) {
			ioe.printStackTrace();
		} catch( InterruptedException ie ) {
			ie.printStackTrace();
		}

		org.w3c.dom.Document xmlDocument = createCharacterXML( trans );
		
		baos = new java.io.ByteArrayOutputStream();
		try {
			edu.cmu.cs.stage3.xml.Encoder.write( xmlDocument, baos );
			map.put( "galleryData.xml", baos.toByteArray() );
		} catch( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}

		trans.vehicle.set( def );

		try {
			Thread.sleep( 100 );
		} catch( InterruptedException ie ) {
			ie.printStackTrace();
		}

		if( batch.m_rt instanceof edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget ) {
			((edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget)batch.m_rt).commitAnyPendingChanges();
		}

		try {
			trans.store( dst, null, map );
		} catch( java.io.IOException ioe ) {
			ioe.printStackTrace();
		} catch( Throwable t ) {
			t.printStackTrace();
		}
		
		//characterStoreProgressPane.setElement( element );
		//characterStoreProgressPane.setFile( file );
		//characterStoreProgressPane.setFilnameToByteArrayMap( null );
		//edu.cmu.cs.stage3.swing.DialogManager.showDialog( characterStoreProgressPane );
	}
	
	public edu.cmu.cs.stage3.alice.core.Element importElement() {
		return importElement(null);
	}

	public edu.cmu.cs.stage3.alice.core.Element importElement(Object path) {
		return importElement(path, null);
	}

	public edu.cmu.cs.stage3.alice.core.Element importElement(Object path, edu.cmu.cs.stage3.alice.core.Element parent) {
		return importElement(path, parent, null);
	}

	public edu.cmu.cs.stage3.alice.core.Element importElement(Object path, edu.cmu.cs.stage3.alice.core.Element parent, edu.cmu.cs.stage3.alice.authoringtool.util.PostImportRunnable postImportRunnable) {
		return importElement(path, parent, postImportRunnable, true);
	}

	public edu.cmu.cs.stage3.alice.core.Element importElement(Object path, edu.cmu.cs.stage3.alice.core.Element parent, edu.cmu.cs.stage3.alice.authoringtool.util.PostImportRunnable postImportRunnable, boolean animateOnAdd) {
		edu.cmu.cs.stage3.alice.core.Element element = null;

		if (path == null) {	
			//String s=importFileChooser.getFileFilter().getDescription();
			if (importFileChooser.getFileFilter().getDescription().compareToIgnoreCase(Messages.getString("Image_Files__BMP_JPG_JPEG_PNG_GIF_TIF_TIFF_")) == 0) { 
				importFileChooser.setAccessory(new edu.cmu.cs.stage3.alice.authoringtool.dialog.ImagePreview(importFileChooser));	//Aik Min - image preview for import texture map
				importFileChooser.setCurrentDirectory( new java.io.File(JAlice.getAliceHomeDirectory(), "textureMap") ); 
				java.io.File file = new java.io.File(JAlice.getAliceHomeDirectory(), "textureMap/GrassTexture.png") ; 
				if ( file.exists() ) {
					importFileChooser.setSelectedFile( file ); 
				}
			} else {
				importFileChooser.setAccessory(null);
				importFileChooser.setSelectedFile( new java.io.File("") );  
				edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage() );
				importFileChooser.setCurrentDirectory( new java.io.File(authoringToolConfig.getValue("directories.worldsDirectory")) ); 
			}
			importFileChooser.rescanCurrentDirectory();
			int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showDialog(importFileChooser, null);

			if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
				final java.io.File file = importFileChooser.getSelectedFile();
				if (file.getAbsolutePath().toLowerCase().endsWith(AuthoringTool.CHARACTER_EXTENSION)) { // special case Alice objects
					return loadAndAddCharacter(file);
				} 
				path = file;
			}
		}
		importFileChooser.setSelectedFile( null ); 
		if (path != null) {
			String ext = null, s;
			if (path instanceof String) {
				s = (String) path;
				ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
			} else if (path instanceof java.io.File) {
				s = ((java.io.File) path).getAbsolutePath();
				ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
				path = ((java.io.File) path).getAbsoluteFile();
			} else if (path instanceof java.net.URL) {
				s = ((java.net.URL) path).getPath();
				ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
			} else {
				throw new IllegalArgumentException(Messages.getString("path_must_be_a_String__java_io_File__or_java_net_URL")); 
			}

			if (parent == null) {
				parent = world;
			}

			Importer importerToUse = null;
			for (java.util.Iterator iter = importing.getImporters().iterator(); iter.hasNext();) {
				Importer importer = (Importer) iter.next();

				if (importer.getExtensionMap().get(ext) != null) {
					importerToUse = importer;
					break;
				}
			}

			if (importerToUse != null) {
				AuthoringTool.this.undoRedoStack.startCompound();

				try {
					if (path instanceof String) {
						element = importerToUse.load((String) path);
					} else if (path instanceof java.io.File) {
						element = importerToUse.load((java.io.File) path);
					} else if (path instanceof java.net.URL) {
						element = importerToUse.load((java.net.URL) path);
					}

					if (element != null) {
						if (element.getParent() != parent) {
							String name = element.name.getStringValue();
							element.name.set(AuthoringToolResources.getNameForNewChild(name, parent));
							if ((parent != null)) {
								parent.addChild(element);
								AuthoringToolResources.addElementToAppropriateProperty(element, parent);
							}
							if (animateOnAdd) {
								animateAddModelIfPossible(element, parent);
							} else{
//								makeIDVisible();
							}
							if (postImportRunnable != null) {
								postImportRunnable.setImportedElement(element);
								postImportRunnable.run();
							}
						}
					} else {
						AuthoringTool.showErrorDialog(Messages.getString("Corrupted_file_or_incorrect_file_type_"), null, false); 
					}
				} catch (java.io.IOException e) {
					AuthoringTool.showErrorDialog(Messages.getString("Error_while_importing_object_"), e); 
				}
				AuthoringTool.this.undoRedoStack.stopCompound();
			} else {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("No_importer_found_to_load_given_file_type_"), Messages.getString("Import_error"), javax.swing.JOptionPane.ERROR_MESSAGE);  
			}
		} else {
			return null;
		}

		return element;
	}

	@SuppressWarnings("cast")
	private void animateAddModelIfPossible(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.core.Element parent) {
		if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame) {
				if (AuthoringTool.this.getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
					animateAddModel(
						(edu.cmu.cs.stage3.alice.core.Transformable) element,
						(edu.cmu.cs.stage3.alice.core.ReferenceFrame) parent,
						(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) AuthoringTool.this.getCurrentCamera());
				} else {
					((edu.cmu.cs.stage3.alice.core.Transformable) element).vehicle.set((edu.cmu.cs.stage3.alice.core.ReferenceFrame) parent);
				}
			} else {
				if (AuthoringTool.this.getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
					animateAddModel((edu.cmu.cs.stage3.alice.core.Transformable) element, world, (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) AuthoringTool.this.getCurrentCamera());
				} else {
					((edu.cmu.cs.stage3.alice.core.Transformable) element).vehicle.set(world);
				}
			}
		}
	}

	public void animateAddModel(edu.cmu.cs.stage3.alice.core.Transformable transformable, edu.cmu.cs.stage3.alice.core.ReferenceFrame vehicle, edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera) {
		if (transformable instanceof edu.cmu.cs.stage3.alice.core.Model) {
			edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model) transformable;

			java.util.HashMap opacityMap = new java.util.HashMap();
			java.util.Vector properties = new java.util.Vector();
			if (camera != null){
				properties.add(camera.localTransformation);
				properties.add(camera.farClippingPlaneDistance);
			}
			properties.add(model.vehicle);
			edu.cmu.cs.stage3.alice.core.Element[] descendants = model.getDescendants(edu.cmu.cs.stage3.alice.core.Model.class, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			for (int i = 0; i < descendants.length; i++) {
				opacityMap.put(descendants[i], ((edu.cmu.cs.stage3.alice.core.Model) descendants[i]).opacity.get());
				properties.add(((edu.cmu.cs.stage3.alice.core.Model) descendants[i]).opacity);
			}
			edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = (edu.cmu.cs.stage3.alice.core.Property[]) properties.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
			boolean tempListening = AuthoringTool.this.getUndoRedoStack().getIsListening();
			undoRedoStack.setIsListening(false);
			model.opacity.set(new Double(0.0), edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			//			javax.vecmath.Matrix4d goodLook = AuthoringToolResources.getAGoodLookAtMatrix( model, camera );
			javax.vecmath.Matrix4d goodLook = null;
			if (camera != null){
				model.vehicle.set(vehicle);
				goodLook = camera.calculateGoodLookAt(model);
			}
			model.vehicle.set(null);
			undoRedoStack.setIsListening(tempListening);

			double distanceToBackOfObject = 0.0;
			boolean needToChangeFarClipping = false;
			if (camera != null){
				distanceToBackOfObject = AuthoringToolResources.distanceToBackAfterGetAGoodLookAt(model, camera);
				needToChangeFarClipping = distanceToBackOfObject > camera.farClippingPlaneDistance.doubleValue();
			}

			// getAGoodLook response
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation setupOpacity = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			setupOpacity.element.set(model);
			setupOpacity.propertyName.set("opacity"); 
			setupOpacity.value.set(new Double(0.0));
			setupOpacity.duration.set(new Double(0.0));
			setupOpacity.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation vehicleAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			vehicleAnimation.element.set(model);
			vehicleAnimation.propertyName.set("vehicle"); 
			vehicleAnimation.value.set(vehicle);
			vehicleAnimation.duration.set(new Double(0.0));
			vehicleAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation getAGoodLook = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			if (camera != null){
				getAGoodLook.subject.set(camera);
				getAGoodLook.pointOfView.set(goodLook);
			}
			
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation cameraGoBack = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			if (camera != null){
				cameraGoBack.subject.set(camera);
				cameraGoBack.pointOfView.set(camera.getLocalTransformation());
				cameraGoBack.duration.set(new Double(.5));
			}
			edu.cmu.cs.stage3.alice.core.response.Wait wait = new edu.cmu.cs.stage3.alice.core.response.Wait();
			wait.duration.set(new Double(.7));
			edu.cmu.cs.stage3.alice.core.response.Wait wait2 = new edu.cmu.cs.stage3.alice.core.response.Wait();
			wait2.duration.set(new Double(.2));
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation farClipping = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			if (camera != null){
				farClipping.element.set(camera);
				farClipping.propertyName.set("farClippingPlaneDistance"); 
				farClipping.value.set(new Double(distanceToBackOfObject));
			}
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation farClipping2 = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			if (camera != null){
				farClipping2.element.set(camera);
				farClipping2.propertyName.set("farClippingPlaneDistance"); 
				farClipping2.value.set(camera.farClippingPlaneDistance.get());
			}
			edu.cmu.cs.stage3.alice.core.response.DoTogether opacityDoTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			for (java.util.Iterator iter = opacityMap.keySet().iterator(); iter.hasNext();) {
				edu.cmu.cs.stage3.alice.core.Model m = (edu.cmu.cs.stage3.alice.core.Model) iter.next();
				Object opacity = opacityMap.get(m);
				edu.cmu.cs.stage3.alice.core.response.PropertyAnimation opacityAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
				opacityAnimation.element.set(m);
				opacityAnimation.propertyName.set("opacity"); 
				opacityAnimation.value.set(opacity);
				opacityAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
				opacityDoTogether.componentResponses.add(opacityAnimation);
			}
			edu.cmu.cs.stage3.alice.core.response.DoInOrder waitOpacityDoInOrder = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			waitOpacityDoInOrder.componentResponses.add(wait);
			waitOpacityDoInOrder.componentResponses.add(opacityDoTogether);
			waitOpacityDoInOrder.componentResponses.add(wait2);
			edu.cmu.cs.stage3.alice.core.response.DoTogether cameraOpacityDoTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			if (needToChangeFarClipping) {
				cameraOpacityDoTogether.componentResponses.add(farClipping);
			}
			cameraOpacityDoTogether.componentResponses.add(getAGoodLook);
			cameraOpacityDoTogether.componentResponses.add(waitOpacityDoInOrder);
			edu.cmu.cs.stage3.alice.core.response.DoInOrder response = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			response.componentResponses.add(setupOpacity);
			response.componentResponses.add(vehicleAnimation);
			if (camera != null){
				response.componentResponses.add(cameraOpacityDoTogether);
				response.componentResponses.add(cameraGoBack);
				response.componentResponses.add(farClipping2);
			} else{
				response.componentResponses.add(opacityDoTogether);
			}
			// getAGoodLook undoResponse
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoVehicleAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			undoVehicleAnimation.element.set(model);
			undoVehicleAnimation.propertyName.set("vehicle"); 
			undoVehicleAnimation.value.set(null);
			undoVehicleAnimation.duration.set(new Double(0.0));
			undoVehicleAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoGetAGoodLook = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			if (camera != null){	
				undoGetAGoodLook.subject.set(camera);
				undoGetAGoodLook.pointOfView.set(goodLook);
				undoGetAGoodLook.duration.set(new Double(.5));
			}
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoCameraGoBack = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			if (camera != null){	
				undoCameraGoBack.subject.set(camera);
				undoCameraGoBack.pointOfView.set(camera.getLocalTransformation());
			}
			edu.cmu.cs.stage3.alice.core.response.Wait undoWait = new edu.cmu.cs.stage3.alice.core.response.Wait();
			undoWait.duration.set(new Double(.9));
			edu.cmu.cs.stage3.alice.core.response.Wait undoWait2 = new edu.cmu.cs.stage3.alice.core.response.Wait();
			undoWait2.duration.set(new Double(.2));
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoFarClipping = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			if (camera != null){
				undoFarClipping.element.set(camera);
				undoFarClipping.propertyName.set("farClippingPlaneDistance"); 
				undoFarClipping.value.set(new Double(distanceToBackOfObject));
				undoFarClipping.duration.set(new Double(.2));
			}
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoFarClipping2 = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			if (camera != null){
				undoFarClipping2.element.set(camera);
				undoFarClipping2.propertyName.set("farClippingPlaneDistance"); 
				undoFarClipping2.value.set(camera.farClippingPlaneDistance.get());
				undoFarClipping2.duration.set(new Double(.2));
			}
			edu.cmu.cs.stage3.alice.core.response.DoInOrder undoCameraGoBackWaitDoInOrder = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			undoCameraGoBackWaitDoInOrder.componentResponses.add(undoWait);
			undoCameraGoBackWaitDoInOrder.componentResponses.add(undoCameraGoBack);
			edu.cmu.cs.stage3.alice.core.response.DoTogether undoOpacityDoTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			for (java.util.Iterator iter = opacityMap.keySet().iterator(); iter.hasNext();) {
				edu.cmu.cs.stage3.alice.core.Model m = (edu.cmu.cs.stage3.alice.core.Model) iter.next();
				edu.cmu.cs.stage3.alice.core.response.PropertyAnimation opacityAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
				opacityAnimation.element.set(m);
				opacityAnimation.propertyName.set("opacity"); 
				opacityAnimation.value.set(new Double(0.0));
				opacityAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
				undoOpacityDoTogether.componentResponses.add(opacityAnimation);
			}
			edu.cmu.cs.stage3.alice.core.response.DoInOrder undoOpacityWaitDoInOrder = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			undoOpacityWaitDoInOrder.componentResponses.add(undoWait2);
			undoOpacityWaitDoInOrder.componentResponses.add(undoOpacityDoTogether);
			edu.cmu.cs.stage3.alice.core.response.DoTogether undoCameraOpacityDoTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			undoCameraOpacityDoTogether.componentResponses.add(undoOpacityWaitDoInOrder);
			undoCameraOpacityDoTogether.componentResponses.add(undoCameraGoBackWaitDoInOrder);
			if (needToChangeFarClipping) {
				undoCameraOpacityDoTogether.componentResponses.add(undoFarClipping);
			}
			edu.cmu.cs.stage3.alice.core.response.DoInOrder undoResponse = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			if (camera != null){
				undoResponse.componentResponses.add(undoGetAGoodLook);
				undoResponse.componentResponses.add(undoCameraOpacityDoTogether);
				if (needToChangeFarClipping) {
					undoResponse.componentResponses.add(undoFarClipping2);
				}
			}else{
				undoResponse.componentResponses.add(undoOpacityWaitDoInOrder);
			}
			undoResponse.componentResponses.add(undoVehicleAnimation);
//			displayDurations(undoResponse);
			performOneShot(response, undoResponse, affectedProperties);
		} else {
			transformable.vehicle.set(vehicle);
		}
	}
	
/*	private void displayDurations(edu.cmu.cs.stage3.alice.core.Response r){
		if (r instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse){
			edu.cmu.cs.stage3.alice.core.response.CompositeResponse c = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse)r;
			System.out.println("COMPOSITE("); 
			for (int i=0; i<c.componentResponses.size(); i++){
				displayDurations((edu.cmu.cs.stage3.alice.core.Response)c.componentResponses.get(i));
			}
			System.out.println(")"); 
		}
		else{
			System.out.println(r.duration.doubleValue());
		}
	}*/

	public void getAGoodLookAt(edu.cmu.cs.stage3.alice.core.Transformable transformable, final edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera) {
		edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = { camera.localTransformation };

		//		javax.vecmath.Matrix4d goodLook = AuthoringToolResources.getAGoodLookAtMatrix( transformable, camera );
		javax.vecmath.Matrix4d goodLook = camera.calculateGoodLookAt(transformable);

		edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation getAGoodLook = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
		getAGoodLook.subject.set(camera);
		getAGoodLook.pointOfView.set(goodLook);

		edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
		undoResponse.subject.set(camera);
		undoResponse.pointOfView.set(camera.getLocalTransformation());

		performOneShot(getAGoodLook, undoResponse, affectedProperties);

		final double distanceToBackOfObject = AuthoringToolResources.distanceToBackAfterGetAGoodLookAt(transformable, camera);

		if (distanceToBackOfObject > camera.farClippingPlaneDistance.doubleValue()) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						AuthoringTool.showErrorDialog(Messages.getString("Interrupted_during_clipping_plane_operation_"), e); 
					}
					int result =
						edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(
							Messages.getString("The_camera_s_far_clipping_plane_is_too_close_to_see_all_of_the_object___Would_you_like_to_move_it_back_"), 
							Messages.getString("Alter_camera_s_far_clipping_plane_"), 
							javax.swing.JOptionPane.YES_NO_OPTION,
							javax.swing.JOptionPane.INFORMATION_MESSAGE);
					if (result == javax.swing.JOptionPane.YES_OPTION) {
						edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = { camera.farClippingPlaneDistance };

						edu.cmu.cs.stage3.alice.core.response.PropertyAnimation farClippingAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
						farClippingAnimation.element.set(camera);
						farClippingAnimation.propertyName.set("farClippingPlaneDistance"); 
						farClippingAnimation.value.set(new Double(distanceToBackOfObject));

						edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
						undoResponse.element.set(camera);
						undoResponse.propertyName.set("farClippingPlaneDistance"); 
						undoResponse.value.set(camera.farClippingPlaneDistance.get());

						performOneShot(farClippingAnimation, undoResponse, affectedProperties);
					}
				}
			};
			javax.swing.SwingUtilities.invokeLater(runnable);
		}
	}

	public void saveTabs() {
		if (saveTabsEnabled && (world != null)) {
			Object[] objects = jAliceFrame.getTabbedEditorComponent().getObjectsBeingEdited();
			String tabObjectsString = ""; 
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] instanceof edu.cmu.cs.stage3.alice.core.Element) { //TODO: handle non Elements?
					tabObjectsString += ((edu.cmu.cs.stage3.alice.core.Element) objects[i]).getKey() + ":"; 
				}
			}
			world.data.put("edu.cmu.cs.stage3.alice.authoringtool.tabObjects", tabObjectsString); 
		}
	}

	public void loadTabs() {
		if (authoringToolConfig.getValue("loadSavedTabs").equalsIgnoreCase("true")) {  
			if (world != null) {
				String tabObjectsString = (String) world.data.get("edu.cmu.cs.stage3.alice.authoringtool.tabObjects"); 
				if (tabObjectsString != null) {
					java.util.StringTokenizer st = new java.util.StringTokenizer(tabObjectsString, ":"); 
					getJAliceFrame().setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
					while (st.hasMoreTokens()) {
						String key = st.nextToken();
						key = key.substring(world.getKey().length() + (key.equals(world.getKey()) ? 0 : 1));
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(key);
						if (element != null) {
							editObject(element, false); //TODO: handle different types of editors
						}
					}
					getJAliceFrame().setCursor(java.awt.Cursor.getDefaultCursor());
				}
			}
		}
	}

	public boolean isImportable(String extension) {
		for (java.util.Iterator iter = importing.getImporters().iterator(); iter.hasNext();) {
			edu.cmu.cs.stage3.alice.authoringtool.Importer importer = (edu.cmu.cs.stage3.alice.authoringtool.Importer) iter.next();
			java.util.Map map = importer.getExtensionMap();
			if (map.get(extension.toUpperCase()) != null) {
				return true;
			}
		}

		return false;
	}

	public void setImportFileFilter(String extensionString) {
		javax.swing.filechooser.FileFilter filter = (javax.swing.filechooser.FileFilter) extensionStringsToFileFilterMap.get(extensionString);
		if (filter != null) {
			importFileChooser.setFileFilter(filter);
		}
	}

	public void showWorldInfoDialog() {
		updateWorldOpenTime();
		worldInfoContentPane.setWorld(world);
		edu.cmu.cs.stage3.swing.DialogManager.showDialog(worldInfoContentPane);
	}

	private java.text.DecimalFormat captureFormatter = new java.text.DecimalFormat();

	public void storeCapturedImage(java.awt.Image image) {
		java.io.File dir = new java.io.File(authoringToolConfig.getValue("screenCapture.directory")); 

		int numDigits = Integer.parseInt(authoringToolConfig.getValue("screenCapture.numDigits")); 
		StringBuffer pattern = new StringBuffer(authoringToolConfig.getValue("screenCapture.baseName")); 
		String codec = authoringToolConfig.getValue("screenCapture.codec"); 
		pattern.append("#"); 
		for (int i = 0; i < numDigits; i++) {
			pattern.append("0"); 
		}
		//pattern.append("."); 
		//pattern.append(codec);
		captureFormatter.applyPattern(pattern.toString());
				
		int i = 0;
		java.io.File file = new java.io.File(dir, captureFormatter.format(i) + "." + codec);
		boolean writable;
		if (file.exists()) {
			writable = file.canWrite();
		} else {
			try {
				boolean success = file.createNewFile();
				writable = success;
				if (success) {
					file.delete();
				}
			} catch (Throwable e) {
				writable = false;
			}
		}

		if (!writable) {
			Object[] options = { Messages.getString("Yes__let_me_select_a_directory"), Messages.getString("No__I_don_t_want_to_take_a_picture_anymore") };  
			int dialogVal =
				edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(
					Messages.getString("Alice_can_not_save_the_captured_image__Do_you_want_to_select_a_new_directory_"), 
					Messages.getString("Alice_can_t_save_the_file"), 
					javax.swing.JOptionPane.YES_NO_OPTION,
					javax.swing.JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
			if (dialogVal == javax.swing.JOptionPane.YES_OPTION) {
				java.io.File parent = dir.getParentFile();
				try {
					browseFileChooser.setCurrentDirectory(parent);
				} catch( ArrayIndexOutOfBoundsException aioobe ) {
					// for some reason this can potentially fail in jdk1.4.2_04
				}
				boolean done = false;
				while (!done) {
					int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showOpenDialog(browseFileChooser);
					if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
						java.io.File selectedFile = browseFileChooser.getSelectedFile();
						java.io.File testCaptureFile = new java.io.File(selectedFile, "test.jpg"); 
						if (testCaptureFile.exists()) {
							writable = testCaptureFile.canWrite();
						} else {
							try {
								boolean success = testCaptureFile.createNewFile();
								writable = success;
								if (success) {
									testCaptureFile.delete();
								}
							} catch (Exception e) {
								writable = false;
							}
						}
						if (!writable) {
							edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("The_capture_directory_specified_can_not_be_written_to__Please_choose_another_directory_")); 
						} else {
							done = true;
							dir = selectedFile;
						}
					} else {
						edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
							Messages.getString("You_have_not_selected_a_writable_directory_to_save_pictures_to_") + "\n" + Messages.getString("Alice_will_not_be_able_to_take_pictures_until_you_do_so__You_can_go_to_Preferences__Screen_Grab_to_set_a_directory_"),  
							Messages.getString("No_directory_set"), 
							javax.swing.JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
			} else {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("You_have_not_selected_a_writable_directory_to_save_pictures_to_") + "\n" + Messages.getString("Alice_will_not_be_able_to_take_pictures_until_you_do_so__You_can_go_to_Preferences__Screen_Grab_to_set_a_directory_"),  
					Messages.getString("No_directory_set"), 
					javax.swing.JOptionPane.WARNING_MESSAGE);
				return;
			}
			file = new java.io.File(dir, captureFormatter.format(i) + "." + codec);
		}
		while (file.exists()) {
			i++;
			file = new java.io.File(dir, captureFormatter.format(i) + "." + codec);
		}
		try {
			file.createNewFile();
			java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
			java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(fos);
			java.io.DataOutputStream dos = new java.io.DataOutputStream(bos);
			edu.cmu.cs.stage3.image.ImageIO.store(codec, dos, image);
			dos.flush();
			fos.close();

			if (authoringToolConfig.getValue("screenCapture.informUser").equalsIgnoreCase("true")) {  
				java.awt.image.BufferedImage scaledImage = edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.getImageScaledToLongestDimension(image, 128);
				edu.cmu.cs.stage3.alice.authoringtool.dialog.CapturedImageContentPane capturedImageContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.CapturedImageContentPane();
				capturedImageContentPane.setStoreLocation(file.getCanonicalPath());
				capturedImageContentPane.setImage(scaledImage);
				edu.cmu.cs.stage3.swing.DialogManager.showDialog(capturedImageContentPane);
			}
			//} catch( InterruptedException ie ) {
			//} catch( java.io.IOException ioe ) {
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_while_storing_screen_capture_"), t); 
		}
	}

	public void performPointOfViewOneShot(edu.cmu.cs.stage3.alice.core.Transformable transformable, edu.cmu.cs.stage3.math.Matrix44 newTransformation) {
		edu.cmu.cs.stage3.math.Matrix44 oldTransformation = transformable.localTransformation.getMatrix44Value();

		edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation povAnimation = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
		povAnimation.subject.set(transformable);
		povAnimation.pointOfView.set(newTransformation);

		edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior oneShotBehavior = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior();
		oneShotBehavior.setResponse(povAnimation);
		oneShotBehavior.setAffectedProperties(new edu.cmu.cs.stage3.alice.core.Property[] { transformable.localTransformation });
		oneShotBehavior.start(oneShotScheduler);

		edu.cmu.cs.stage3.alice.authoringtool.util.PointOfViewUndoableRedoable undo = new edu.cmu.cs.stage3.alice.authoringtool.util.PointOfViewUndoableRedoable(transformable, oldTransformation, newTransformation, oneShotScheduler);
		undoRedoStack.push(undo);
	}

	public void performOneShot(edu.cmu.cs.stage3.alice.core.Response response, edu.cmu.cs.stage3.alice.core.Response undoResponse, edu.cmu.cs.stage3.alice.core.Property[] affectedProperties) {
		edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior oneShotBehavior = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior();
		oneShotBehavior.setResponse(response);
		oneShotBehavior.setAffectedProperties(affectedProperties);
		oneShotBehavior.start(oneShotScheduler);

		edu.cmu.cs.stage3.alice.authoringtool.util.OneShotUndoableRedoable undo = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotUndoableRedoable(response, undoResponse, oneShotBehavior, oneShotScheduler);
		undoRedoStack.push(undo);
	}

	public void lostOwnership(java.awt.datatransfer.Clipboard clipboard, java.awt.datatransfer.Transferable contents) {
		//TODO: store a reference to CastMembers until ownership is lost, then put the whole thing in the clipboard
	}

	/*
	public void editCharacter( edu.cmu.cs.stage3.alice.core.Transformable character ) {
		characterEditorDialog.setCharacter( character );
		if( ! characterEditorDialog.isShowing() ) {
			characterEditorDialog.show();
		}
	}
	*/

	public void makeBillboard() {
		setImportFileFilter("Image Files"); 
		importElement(null, null, new edu.cmu.cs.stage3.alice.authoringtool.util.PostImportRunnable() {
			public void run() {
				edu.cmu.cs.stage3.alice.core.TextureMap textureMap = (edu.cmu.cs.stage3.alice.core.TextureMap) getImportedElement();
				if (textureMap != null) {
					textureMap.removeFromParent();
					textureMap.name.set(AuthoringToolResources.getNameForNewChild(textureMap.name.getStringValue(), world));
					edu.cmu.cs.stage3.alice.core.Billboard billboard = AuthoringToolResources.makeBillboard(textureMap, true);
					animateAddModelIfPossible(billboard, world);
					world.addChild( billboard );
					billboard.vehicle.set( world );
					world.sandboxes.add( billboard );
				}
			}
		}, true);
		//		importObject( null, new edu.cmu.cs.stage3.alice.authoringtool.util.BillboardPostWorker( world ) );
	}

	public void setElementScope(edu.cmu.cs.stage3.alice.core.Element element) {
		jAliceFrame.getWorldTreeComponent().setCurrentScope(element);
		if (element != null) {
			if (selectedElement != null) {
				if (!selectedElement.isDescendantOf(element)) {
					setSelectedElement(element);
				}
			} else {
				setSelectedElement(element);
			}
		}
	}

	//TODO: handle this correctly
	public edu.cmu.cs.stage3.alice.core.Camera getCurrentCamera() {
		//edu.cmu.cs.stage3.alice.core.Element[] e = world.search(new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(edu.cmu.cs.stage3.alice.core.Camera.class));
		edu.cmu.cs.stage3.alice.core.Element[] e = world.getDescendants( edu.cmu.cs.stage3.alice.core.Camera.class );
		if (e != null) {
			return (edu.cmu.cs.stage3.alice.core.Camera) e[0];
		} 
		return null;
	}

	public void showAbout() {
		aboutContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.AboutContentPane();
		edu.cmu.cs.stage3.swing.DialogManager.showDialog(aboutContentPane);
	}

	public void showLicense() {
		licenseContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LicenseContentPane();
		edu.cmu.cs.stage3.swing.DialogManager.showDialog(licenseContentPane);
	}
	
	public void showPreferences() {
		int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(preferencesContentPane);
		if (result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			preferencesContentPane.finalizeSelections();
		}
	}
	/*
	public static void showSaveErrorDialog(String message, Throwable t) {
		getHack().jAliceFrame.HACK_goToRedAlert();
		edu.cmu.cs.stage3.alice.authoringtool.dialog.SaveErrorContentPane errorPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.SaveErrorContentPane();
		errorPane.setMessage(message);
		errorPane.setSubmitBugButtonEnabled( true );
		errorPane.setThrowable(t);
		int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(errorPane);
		if (getHack().isStdErrToConsole() && (t != null)) {
			t.printStackTrace(System.err);
		}
	}
*/
	public static void showErrorDialog(String message, Object t) {
			showErrorDialog(message, t, false);
	}

	public static void showErrorDialog(String message, Object t, boolean showSubmitBugButton) {
		edu.cmu.cs.stage3.alice.authoringtool.dialog.ErrorContentPane errorPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.ErrorContentPane();
		errorPane.setMessage(message);
		//errorPane.setSubmitBugButtonEnabled(showSubmitBugButton);
		if (t instanceof Throwable)
			errorPane.setThrowable((Throwable) t);
		else
			errorPane.setDetails((String) t);
		edu.cmu.cs.stage3.swing.DialogManager.showDialog(errorPane);
		if (t instanceof Throwable) {
			if (getHack().isStdErrToConsole() && (t != null)) {
				Throwable tt = (Throwable)t;
				tt.printStackTrace(System.err);
			}
		} 
	}

/*	public static void showErrorDialogWithDetails(String message, String details) {
		showErrorDialogWithDetails(message, details, true);
	}

	public static void showErrorDialogWithDetails(String message, String details, boolean showSubmitBugButton) {
		edu.cmu.cs.stage3.alice.authoringtool.dialog.ErrorContentPane errorPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.ErrorContentPane();
		errorPane.setMessage(message);
		errorPane.setSubmitBugButtonEnabled(showSubmitBugButton);
		int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(errorPane);
		//		if( getHack().isStdErrToConsole() && (t != null) ) {
		//			t.printStackTrace( System.err );
		//		}
	}
*/
	private void stopWorldAndShowDialog( Throwable throwable ) {
		stopWorld();
		if( throwable instanceof edu.cmu.cs.stage3.alice.core.ExceptionWrapper) {
			throwable = ((edu.cmu.cs.stage3.alice.core.ExceptionWrapper)throwable).getWrappedException();
		}
		final Throwable t = throwable;
		javax.swing.SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				if( t instanceof edu.cmu.cs.stage3.alice.core.SimulationException ) {
					showSimulationExceptionDialog( (edu.cmu.cs.stage3.alice.core.SimulationException)t );
				} else {
					showErrorDialog( Messages.getString("Error_during_simulation_"), t ); 
				}
			}
		} );
	}

	public void showSimulationExceptionDialog( edu.cmu.cs.stage3.alice.core.SimulationException simulationException ) {
		edu.cmu.cs.stage3.alice.authoringtool.dialog.SimulationExceptionPanel simulationExceptionPanel = new edu.cmu.cs.stage3.alice.authoringtool.dialog.SimulationExceptionPanel(this);
		simulationExceptionPanel.setSimulationException(simulationException);
		simulationExceptionPanel.setErrorHighlightingEnabled(true);
		edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(simulationExceptionPanel, Messages.getString("Problem_Detected"), javax.swing.JOptionPane.ERROR_MESSAGE, AuthoringToolResources.getAliceSystemIcon() ); 
		simulationExceptionPanel.setErrorHighlightingEnabled(false);
	}

	public javax.swing.JFileChooser getImportFileChooser() {
		return importFileChooser;
	}

	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return world;
	}

	public void showOnScreenHelp() {
		showStencils();
	}

	public void showStdErrDialog() {
		stdErrContentPane.setMode(edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane.HISTORY_MODE);
		stdErrContentPane.showStdErrDialog();
	}
	
	public void showStdOutDialog() {
		stdOutContentPane.setMode(edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane.HISTORY_MODE);
		stdOutContentPane.showStdOutDialog();
	}
	
	public void updateWorldOpenTime() {
		if (world != null) {
			String worldOpenTimeString = (String) world.data.get("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime"); 
			long worldOpenTime = 0;
			if (worldOpenTimeString != null) {
				worldOpenTime = Long.parseLong(worldOpenTimeString);
			}
			worldOpenTime += System.currentTimeMillis() - worldLoadedTime;
			worldLoadedTime = System.currentTimeMillis();
			world.data.put("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime", Long.toString(worldOpenTime)); 
		}
	}

	public void countSomething(String dataKey) {
		String countString = (String) world.data.get(dataKey);
		int count = 0;
		if (countString != null) {
			count = Integer.parseInt(countString);
		}
		count++;
		world.data.put(dataKey, Integer.toString(count));
	}

	public void showPrintDialog() {
		int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(exportCodeForPrintingContentPane);
		if (result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			final java.io.File fileToExportTo = exportCodeForPrintingContentPane.getFileToExportTo();
			edu.cmu.cs.stage3.progress.ProgressPane progressPane = new edu.cmu.cs.stage3.progress.ProgressPane( Messages.getString("Saving_HTML___"), Messages.getString("Saving__") ) {  
				protected void construct() throws edu.cmu.cs.stage3.progress.ProgressCancelException {
					try {
						StringBuffer htmlOutput = new StringBuffer();
						exportCodeForPrintingContentPane.getHTML(htmlOutput, fileToExportTo, true, true, this);
						java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(new java.io.FileOutputStream(fileToExportTo));
						writer.write(htmlOutput.toString());
						writer.flush();
						writer.close();
					} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
						fileToExportTo.delete();
						throw pce;
					} catch( Throwable t ) {
						AuthoringTool.showErrorDialog(Messages.getString("Unable_to_store_world_to_file__", fileToExportTo), t); 
						fileToExportTo.delete();
					}
				}
			};
			edu.cmu.cs.stage3.swing.DialogManager.showDialog( progressPane );
		}
	}

	///////////////////////////////
	// Private methods
	///////////////////////////////

	private void setCurrentWorldLocation(java.io.File file) {
		currentWorldLocation = file;
		updateTitle();
	}

	private void updateTitle() {
		String path = ""; 
		if (currentWorldLocation != null) {
			try {
				path = currentWorldLocation.getCanonicalPath();
			} catch (java.io.IOException e) {
				path = currentWorldLocation.getAbsolutePath();
			}
		}

		String modifiedStatus = ""; 
		if (worldHasBeenModified) {
			modifiedStatus = Messages.getString("____Modified_"); 
		}

		jAliceFrame.setTitle("Alice (" + JAlice.getVersion() + ") " + path + modifiedStatus);  
	}

	public java.awt.Component getEditorForElement(edu.cmu.cs.stage3.alice.core.Element elementToEdit) {
		int index = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementToEdit);
		if (index > -1) {
			return jAliceFrame.tabbedEditorComponent.getEditorAt(index).getJComponent();
		} 
		Class editorClass = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(elementToEdit.getClass());
		Editor editor = editorManager.getEditorInstance(editorClass);
		edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.editObject(editor, elementToEdit);
		return editor.getJComponent();
	}

	///////////////
	// Stencils
	///////////////
	protected java.util.HashMap componentMap = new java.util.HashMap();
	protected edu.cmu.cs.stage3.caitlin.stencilhelp.client.StencilManager stencilManager;
	protected java.util.HashSet classesToStopOn = new java.util.HashSet();
	protected javax.swing.Timer updateTimer;
	protected boolean stencilDragging = false;
	protected java.awt.Component dragStartSource;
	protected java.io.File tutorialOne;
	protected java.io.File tutorialDirectory = new java.io.File("tutorial").getAbsoluteFile(); 
	protected java.util.ArrayList wayPoints = new java.util.ArrayList();

	private void stencilInit() {
		stencilManager = new edu.cmu.cs.stage3.caitlin.stencilhelp.client.StencilManager(this);
		jAliceFrame.setGlassPane(stencilManager.getStencilComponent());
		((javax.swing.JComponent) stencilManager.getStencilComponent()).setOpaque(false); //TODO: remove

		classesToStopOn.add(javax.swing.JMenu.class);
		classesToStopOn.add(javax.swing.AbstractButton.class);
		classesToStopOn.add(javax.swing.JComboBox.class);
		classesToStopOn.add(javax.swing.JList.class);
		classesToStopOn.add(javax.swing.JMenu.class);
		classesToStopOn.add(javax.swing.JSlider.class);
		classesToStopOn.add(javax.swing.text.JTextComponent.class);
		classesToStopOn.add(javax.swing.JTabbedPane.class);
		classesToStopOn.add(javax.swing.JTree.class);
		classesToStopOn.add(javax.swing.JTable.class);
		classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.class);
		classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel.class);
		classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.util.TrashComponent.class);
		classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController.class);
		classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BehaviorGroupsEditor.class);
		classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.class);
		classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.class);
		classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject.class);
		//		classesToStopOn.add( edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator.class );

		componentMap.put("fileMenu", jAliceFrame.fileMenu); 
		componentMap.put("editMenu", jAliceFrame.editMenu); 
		componentMap.put("toolsMenu", jAliceFrame.toolsMenu); 
		componentMap.put("helpMenu", jAliceFrame.helpMenu); 
		componentMap.put("playButton", jAliceFrame.playButton); 
		componentMap.put("addObjectButton", jAliceFrame.addObjectButton); 
		componentMap.put("undoButton", jAliceFrame.undoButton); 
		componentMap.put("redoButton", jAliceFrame.redoButton); 
		componentMap.put("trashComponent", jAliceFrame.trashComponent); 
		componentMap.put("clipboardPanel", jAliceFrame.clipboardPanel); 
		componentMap.put("objectTree", jAliceFrame.worldTreeComponent); 
		componentMap.put("sceneEditor", jAliceFrame.sceneEditor); 
		componentMap.put("details", jAliceFrame.dragFromComponent); 
		componentMap.put("behaviors", jAliceFrame.behaviorGroupsEditor); 
		componentMap.put("editors", jAliceFrame.tabbedEditorComponent); 

		updateTimer = new javax.swing.Timer(100, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				stencilManager.update();
				//				System.out.println( "update: " + System.currentTimeMillis() );
			}
		});
		updateTimer.setRepeats(false);
		java.awt.event.AWTEventListener updateListener = new java.awt.event.AWTEventListener() {
			public void eventDispatched(java.awt.AWTEvent ev) {
				if (ev.getSource() instanceof java.awt.Component) {
					if ((ev.getSource() == jAliceFrame) || jAliceFrame.isAncestorOf((java.awt.Component) ev.getSource())) {
						updateTimer.restart();
					}
				}
			}
		};
		java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(updateListener, /*java.awt.AWTEvent.PAINT_EVENT_MASK |*/
		java.awt.AWTEvent.CONTAINER_EVENT_MASK | java.awt.AWTEvent.HIERARCHY_EVENT_MASK | java.awt.AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK);

		// special case updates
		jAliceFrame.worldTreeComponent.worldTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
			public void treeCollapsed(javax.swing.event.TreeExpansionEvent ev) {
				updateTimer.restart();
			}
			public void treeExpanded(javax.swing.event.TreeExpansionEvent ev) {
				updateTimer.restart();
			}
		});

		tutorialOne = new java.io.File(JAlice.getAliceHomeDirectory(), "tutorial" + java.io.File.separator + "Tutorial1.stl");  
	}

	public void hackStencilUpdate() { // used in specific places in authoringtool code to notify stencils of updates not otherwise caught
		if (updateTimer != null) {
			updateTimer.restart();
		}
	}

	protected java.awt.Dimension oldDimension;
	protected java.awt.Point oldPosition;
	protected int oldLeftRightSplitPaneLocation;
	protected int oldWorldTreeDragFromSplitPaneLocation;
	protected int oldEditorBehaviorSplitPaneLocation;
	protected int oldSmallSceneBehaviorSplitPaneLocation;
	protected java.awt.Dimension oldRenderWindowSize;
	protected java.awt.Point oldRenderWindowPosition;
	protected boolean oldShouldConstrain;
	protected java.awt.Rectangle oldRenderBounds;

	public void showStencils() {
		setLayout();
		jAliceFrame.dragFromComponent.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.ANIMATIONS_TAB);
		jAliceFrame.removeKeyListener(stencilManager);
		jAliceFrame.addKeyListener(stencilManager);
		jAliceFrame.requestFocus();
		stencilManager.showStencils(true);
		authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "true");  
	}

	protected void restoreLayout() {
		jAliceFrame.setResizable(true);
		if (oldShouldConstrain) {
			authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "true");  
		} else {
			authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "false");  
		}
		renderContentPane.saveRenderBounds(oldRenderBounds);
		jAliceFrame.setSize(oldDimension);
		jAliceFrame.setLocation(oldPosition);
		jAliceFrame.validate();//.doLayout();
		jAliceFrame.leftRightSplitPane.setDividerLocation(oldLeftRightSplitPaneLocation);
		jAliceFrame.worldTreeDragFromSplitPane.setDividerLocation(oldWorldTreeDragFromSplitPaneLocation);
		jAliceFrame.editorBehaviorSplitPane.setDividerLocation(oldEditorBehaviorSplitPaneLocation);
		jAliceFrame.validate();//.doLayout();
		jAliceFrame.smallSceneBehaviorSplitPane.setDividerLocation(oldSmallSceneBehaviorSplitPaneLocation);
	}

	protected void setLayout() {
		int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		//int height = screenHeight - 28;

		java.awt.Dimension d = getScreenSize();
		oldDimension = new java.awt.Dimension(d.width, d.height);
		oldPosition = jAliceFrame.getLocation();
		java.awt.Point newPosition = new java.awt.Point(oldPosition.x, oldPosition.y);
		int newWidth = d.width;
		int newHeight = d.height;
		if (d.width > 1032) {
			newWidth = 1032;
		} else if (d.width < 1024 && screenWidth >= 1024) {
			newWidth = 1032;
		}
		if (d.height > 776) {
			newHeight = 740;
		} else if (d.height < 740 && screenWidth >= 768) {
			newHeight = 740;
		}
		if (oldPosition.x + newWidth > (screenWidth + 4)) {
			newPosition.x = (screenWidth - newWidth) / 2;
		}
		if (oldPosition.y + newHeight > (screenHeight + 4)) {
			newPosition.y = (screenHeight - 28 - newHeight) / 2;
		}
		boolean shouldModifyStuff = true;
		if (screenWidth < 1024 || screenHeight < 740) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
				Messages.getString("Your_screen_resolution_is_lower_than_what_we_recommend_for_running_the_tutorial__n") + Messages.getString("Alice_will_still_run_the_tutorial__but_some_of_the_objects_may_not_line_up_well_"),  
				Messages.getString("Low_Resolution_Warning"), 
				javax.swing.JOptionPane.WARNING_MESSAGE,
				null);
			shouldModifyStuff = false;
		}
		oldSmallSceneBehaviorSplitPaneLocation = jAliceFrame.smallSceneBehaviorSplitPane.getDividerLocation();
		oldLeftRightSplitPaneLocation = jAliceFrame.leftRightSplitPane.getDividerLocation();
		oldWorldTreeDragFromSplitPaneLocation = jAliceFrame.worldTreeDragFromSplitPane.getDividerLocation();
		oldEditorBehaviorSplitPaneLocation = jAliceFrame.editorBehaviorSplitPane.getDividerLocation();
		oldRenderWindowSize = renderContentPane.getSize();
		oldRenderWindowPosition = renderContentPane.getLocation();

		oldShouldConstrain = authoringToolConfig.getValue("rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase("true");  
		authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "false");  

		oldRenderBounds = renderContentPane.getRenderBounds();
		//renderContentPane.saveRenderBounds(new java.awt.Rectangle(191, 199, 400, 300));

		if ((newHeight != oldDimension.height || newWidth != oldDimension.width) && shouldModifyStuff) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
				Messages.getString("Alice_is_going_to_adjust_your_screen_size_to_make_the_tutorial_fit_on_the_screen_better__n") + Messages.getString("When_you_exit_the_tutorial_your_original_screen_size_will_be_restored_"),  
				Messages.getString("Different_Resolution_Warning"), 
				javax.swing.JOptionPane.WARNING_MESSAGE,
				null);

		}
		if (shouldModifyStuff) {
			jAliceFrame.smallSceneBehaviorSplitPane.setDividerLocation(220);//(224);
			jAliceFrame.leftRightSplitPane.setDividerLocation(300);//250);
			jAliceFrame.worldTreeDragFromSplitPane.setDividerLocation(237);
			jAliceFrame.editorBehaviorSplitPane.setDividerLocation(204);
			try{
				Thread.sleep(100);
			}catch (Exception e){}
			jAliceFrame.setExtendedState(java.awt.Frame.NORMAL);
			jAliceFrame.setSize(newWidth, newHeight);
			jAliceFrame.setPreferredSize(new Dimension (newWidth, newHeight));
			jAliceFrame.setLocation(newPosition);	
		}

		//java.awt.Dimension targetDimension = new java.awt.Dimension(newWidth, newHeight);
		jAliceFrame.doLayout();
		jAliceFrame.setResizable(false);
	}

	public void hideStencils() {
		restoreLayout();
		stencilManager.showStencils(false);
		authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "false");  
		jAliceFrame.removeKeyListener(stencilManager);
		jAliceFrame.requestFocus();
	}

	public void setGlassPane(java.awt.Component c) {
		jAliceFrame.setGlassPane(c);
	}

	public void setVisible(boolean visible) {
		if (visible) {
			setLayout();
			authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "true");  
			jAliceFrame.removeKeyListener(stencilManager);
			jAliceFrame.addKeyListener(stencilManager);
			jAliceFrame.requestFocus();
		} else {
			restoreLayout();
			authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "false");  
			jAliceFrame.removeKeyListener(stencilManager);
			jAliceFrame.requestFocus();
		}
	}

	//Caitlin's code
	@SuppressWarnings("cast")
	public edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule getStateCapsuleFromString(String capsuleString) {
		edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule sc = new edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule();
		sc.parse(capsuleString);
		return (edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule) sc;
	}

	public edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule getCurrentState() {
		if (wayPoints.size() > 0) {
			edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule currentWayPoint = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule) wayPoints.get(0);
			edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule capsule = currentWayPoint.getStateCapsule();

			//this.doesStateMatch(capsule);
			return capsule;
		}
		return null;
	}

	//	public void newSlide() {}
	//	public void clearSlide() {}

	protected java.awt.Component getValidComponent(java.awt.Component c) {
		while (c != null) {
			// special cases
			if ((c instanceof javax.swing.JButton) && (c.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)) {
				c = c.getParent();
			} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController) {
				c = c.getParent();
			} else if ((c instanceof javax.swing.AbstractButton) && (c.getParent() instanceof javax.swing.JComboBox)) {
				c = c.getParent();
			} else if ((c instanceof javax.swing.JTextField) && (c.getParent() instanceof javax.swing.JComboBox)) {
				c = c.getParent();
			} else if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel) && (c.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator)) {
				return c;
			} else if ((c instanceof javax.swing.JLabel) && Messages.getString("more___").equals(((javax.swing.JLabel) c).getText())) { 
				return c;
			} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor) {
				return ((edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor) c).getRenderPanel();
				//			} else if( (c instanceof javax.swing.JScrollPane) && (c.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) && (c == ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent()).getWorkSpace()) ) {
				//				return c;
				//			} else if( (c instanceof javax.swing.JPanel) && (c.getParent() != null) && (c.getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) && (c == ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getParameterPanel()) ) {
				//				return c;
				//			} else if( (c instanceof javax.swing.JPanel) && (c.getParent() != null) && (c.getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) && (c == ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getVariablePanel()) ) {
				//				return c;
				//			} else if( (c instanceof javax.swing.JPanel) && (c.getParent() != null) && (c.getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) && (c == ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getDoNothingPanel()) ) {
				//				return c;
			}
			// default cases
			for (java.util.Iterator iter = classesToStopOn.iterator(); iter.hasNext();) {
				Class stopClass = (Class) iter.next();
				if (stopClass.isAssignableFrom(c.getClass())) {
					return c;
				}
			}
			// look up one level
			c = c.getParent();
		}
		return null;
	}

	public String getIDForPoint(java.awt.Point p, boolean dropSite) {
		p = javax.swing.SwingUtilities.convertPoint(jAliceFrame.getGlassPane(), p, jAliceFrame.getLayeredPane()); //shouldn't be necessary; just being careful
		java.awt.Component c = jAliceFrame.getRootPane().getLayeredPane().findComponentAt(p);
		c = getValidComponent(c);
		if (c == null) {
			return null;
		}
		java.awt.Point localPoint = javax.swing.SwingUtilities.convertPoint(jAliceFrame.getRootPane().getLayeredPane(), p, c);

		String key = null;
		if ((c instanceof javax.swing.JTree) && jAliceFrame.worldTreeComponent.isAncestorOf(c)) {
			javax.swing.JTree tree = (javax.swing.JTree) c;
			javax.swing.tree.TreePath treePath = tree.getClosestPathForLocation(localPoint.x, localPoint.y);
			java.awt.Rectangle bounds = tree.getPathBounds(treePath);
			key = "objectTree"; 
			if (bounds.contains(localPoint)) {
				String elementKey = ((edu.cmu.cs.stage3.alice.core.Element) treePath.getLastPathComponent()).getKey(world);
				key += "<" + elementKey + ">";  
			}
		} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard) {
			key = "clipboard"; 
			java.awt.Component[] components = c.getParent().getComponents();
			int index = -1;
			for (int i = 0; i < components.length; i++) {
				if (components[i] == c) {
					index = i;
				}
			}
			key += "<" + Integer.toString(index) + ">";  

		} else if (jAliceFrame.sceneEditor.isAncestorOf(c) || (c == jAliceFrame.sceneEditor)) {
			if (jAliceFrame.sceneEditor.getGuiMode() == edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.LARGE_MODE) {
				key = "sceneEditor<large>"; 
			} else {
				key = "sceneEditor<small>"; 
			}
			if (jAliceFrame.sceneEditor.getViewMode() == edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.QUAD_VIEW_MODE) {
				key += ":quadView"; 
			} else {
				key += ":singleView"; 
			}

			if (jAliceFrame.sceneEditor.getGalleryViewer().isAncestorOf(c) || (c == jAliceFrame.sceneEditor.getGalleryViewer())) {
				key += ":galleryViewer<" + jAliceFrame.sceneEditor.getGalleryViewer().getDirectory() + ">";  

				if (c instanceof javax.swing.JButton) {
					key += ":button<" + ((javax.swing.JButton) c).getText() + ">";  
				} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) {
					key += ":galleryObject<" + ((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) c).getUniqueIdentifier() + ">";  
				} else if (c != jAliceFrame.sceneEditor.getGalleryViewer()) {
					key = null;
				}
			} else {
				String id = jAliceFrame.sceneEditor.getIdForComponent(c);
				//				System.out.println( c + " --> " + id );
				if (id != null) {
					key += ":" + id; 
				} else if (c != jAliceFrame.sceneEditor) {
					key = null;
				}
			}
		} else if (jAliceFrame.dragFromComponent.isAncestorOf(c) || (c == jAliceFrame.dragFromComponent)) {
			if (jAliceFrame.dragFromComponent.getElement() == null) {
				key = "details"; 
			} else {
				key = "details<" + jAliceFrame.dragFromComponent.getElement().getKey(world) + ">";  
				if (c instanceof javax.swing.JTabbedPane) {
					int whichTab = ((javax.swing.JTabbedPane) c).getUI().tabForCoordinate((javax.swing.JTabbedPane) c, localPoint.x, localPoint.y);
					if (whichTab > -1) {
						key += ":tab<" + Integer.toString(whichTab) + ">";  
					}
				} else if (c != jAliceFrame.dragFromComponent) {
					key += ":" + jAliceFrame.dragFromComponent.getKeyForComponent(c); 
				}
			}
		} else if (jAliceFrame.behaviorGroupsEditor.isAncestorOf(c)) {
			key = "behaviors"; 
			if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
				try {
					java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) c).getTransferable();
					if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
						edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
						key += ":elementTile<" + e.getKey(world) + ">";  
					} else {
						key = null;
					}
				} catch (Exception e) {
					AuthoringTool.showErrorDialog("Error while examining DnDGroupingPanel.", e); 
					key = null;
				}
			} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
				edu.cmu.cs.stage3.alice.core.Property property = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) c).getProperty();
				edu.cmu.cs.stage3.alice.core.Element element = property.getOwner();
				key += ":elementTile<" + element.getKey(world) + ">";  
				key += ":property<" + property.getName() + ">";  
				//TODO: handle user-defined parameters
			} else if ((c instanceof javax.swing.JLabel) && ((javax.swing.JLabel) c).getText().equals("more...")) { 
				if (c.getParent().getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
					edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController vc = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) c.getParent().getParent().getParent();
					edu.cmu.cs.stage3.alice.core.Element element = vc.getElement();
					key += ":elementTile<" + element.getKey(world) + ">";  
					key += ":more"; 
				} else {
					key = null;
				}
			} else if (c instanceof javax.swing.JButton) {
				if (((javax.swing.JButton) c).getText().equals("create new event")) { // HACK 
					key += ":createNewEventButton"; 
				} else {
					key = null;
				}
			}
		} else if (jAliceFrame.tabbedEditorComponent.isAncestorOf(c)) {
			key = "editors"; 
			if (c instanceof javax.swing.JTabbedPane) {
				int whichTab = ((javax.swing.JTabbedPane) c).getUI().tabForCoordinate((javax.swing.JTabbedPane) c, localPoint.x, localPoint.y);
				if (whichTab > -1) {
					Object o = jAliceFrame.tabbedEditorComponent.getObjectBeingEditedAt(whichTab);
					if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
						key += ":element<" + ((edu.cmu.cs.stage3.alice.core.Element) o).getKey(world) + ">";  
					} else {
						key = null;
					}
				}
			} else {
				Object o = jAliceFrame.tabbedEditorComponent.getObjectBeingEdited();
				if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
					key += ":element<" + ((edu.cmu.cs.stage3.alice.core.Element) o).getKey(world) + ">";  
					//					edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor responseEditor = null;
					//					if( jAliceFrame.tabbedEditorComponent.getCurrentEditor() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor ) {
					//						responseEditor = (edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor)jAliceFrame.tabbedEditorComponent.getCurrentEditor();
					//					}
					//					if( (c instanceof javax.swing.JScrollPane) && (c.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) && (c == ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent()).getWorkSpace()) ) {
					//						key += ":compositeEditorWorkSpace";
					//					} else if( (c instanceof javax.swing.JPanel) && (c.getParent() != null) && (c.getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) && (c == ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getParameterPanel()) ) {
					//						key += ":compositeEditorParameterPanel";
					//					} else if( (c instanceof javax.swing.JPanel) && (c.getParent() != null) && (c.getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) && (c == ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getVariablePanel()) ) {
					//						key += ":compositeEditorVariablePanel";
					//					} else if( (c instanceof javax.swing.JPanel) && (c.getParent() != null) && (c.getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) && (c == ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getDoNothingPanel()) ) {
					//						key += ":compositeEditorDoNothingPanel";
					if (c instanceof javax.swing.JButton) {
						key += ":button<" + ((javax.swing.JButton) c).getText() + ">";  
					} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
						try {
							java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) c).getTransferable();
							if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
								edu.cmu.cs.stage3.alice.core.Element e =
									(edu.cmu.cs.stage3.alice.core.Element) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
								key += ":elementTile<" + e.getKey(world) + ">";  
							} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
								edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep =
									(edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) transferable.getTransferData(
										edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
								key += ":elementPrototypeTile<" + ep.getElementClass().getName() + ">";  
							} else {
								key = null;
							}
							//TODO: handle other DnDGroupingPanels
						} catch (Exception e) {
							AuthoringTool.showErrorDialog(Messages.getString("Error_while_examining_DnDGroupingPanel_"), e); 
							key = null;
						}
					} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
						edu.cmu.cs.stage3.alice.core.Property property = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) c).getProperty();
						edu.cmu.cs.stage3.alice.core.Element element = property.getOwner();
						key += ":elementTile<" + element.getKey(world) + ">";  
						key += ":property<" + property.getName() + ">";  
						//TODO: handle user-defined parameters
					} else if ((c instanceof javax.swing.JLabel) && ((javax.swing.JLabel) c).getText().equals("more...")) { 
						if (c.getParent().getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
							edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController vc =
								(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) c.getParent().getParent().getParent();
							edu.cmu.cs.stage3.alice.core.Element element = vc.getElement();
							key += ":elementTile<" + element.getKey(world) + ">";  
							key += ":more"; 
						} else {
							key = null;
						}
					}
				} else {
					key = null;
				}
			}
		} else if (componentMap.containsValue(c)) {
			for (java.util.Iterator iter = componentMap.keySet().iterator(); iter.hasNext();) {
				String k = (String) iter.next();
				if (c.equals(componentMap.get(k))) {
					key = k;
					break;
				}
			}
		}
		//		System.out.println( key );
		return key;
	}

	private java.awt.Image getComponentImage(java.awt.Component c) {
		java.awt.Rectangle bounds = c.getBounds();
		java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(bounds.width, bounds.height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = image.createGraphics();
		c.paintAll(g);
		return image;
	}

	public java.awt.Image getImageForID(String id) {
		java.awt.Rectangle r = null;
		java.awt.Image image = null;
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":", false); 
		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) { 
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						javax.swing.JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel worldTreeModel = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel) tree.getModel();
						r = tree.getPathBounds(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
						if ((r != null) && (!worldTreeModel.isLeaf(element))) { //HACK to include expand handle
							r.x -= 15;
							r.width += 15;
						}
						if (r != null) {
							r = javax.swing.SwingUtilities.convertRectangle(tree, r, jAliceFrame.getGlassPane());
						}
					}
				} else {
					r = jAliceFrame.worldTreeComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.worldTreeComponent.getParent(), r, jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("clipboard")) { 
				if (spec != null) {
					try {
						int index = Integer.parseInt(spec);
						if (index > -1) {
							java.awt.Component c = jAliceFrame.clipboardPanel.getComponent(index);
							if (c != null) {
								image = getComponentImage(c);
							}
						}
					} catch (Exception e) {
					}
				}
			} else if (prefix.equals("sceneEditor")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView, assume we're in the right mode
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) { 
							jAliceFrame.sceneEditor.getGalleryViewer().setDirectory(spec);
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { 
									java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("galleryObject")) { 
									java.awt.Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								}
							}
						} else {
							java.awt.Component c = jAliceFrame.sceneEditor.getComponentForId(token);
							if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
								image = getComponentImage(c);
							}
						}
					}
				}
			} else if (prefix.equals("details")) { 
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
						if (st.hasMoreTokens()) {
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("viewController")) { 
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources.getPrefix(token);
									spec = AuthoringToolResources.getSpecifier(token);
									java.awt.Component c = jAliceFrame.dragFromComponent.getPropertyViewComponentForKey(token);
									if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
										image = getComponentImage(c);
									}
								}
							} else {
								java.awt.Component c = jAliceFrame.dragFromComponent.getComponentForKey(token);
								if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
									image = getComponentImage(c);
								}
							}
						} else {
							image = getComponentImage(jAliceFrame.dragFromComponent);
						}
					}
				} else {
					image = getComponentImage(jAliceFrame.dragFromComponent);
				}
			} else if (prefix.equals("behaviors")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewEventButton")) { 
						java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new event"); 
						if (c != null) {
							image = getComponentImage(c);
						}
					} else if (prefix.equals("elementTile") && (spec != null)) { 
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) { 
									java.awt.Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("more")) { 
									java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
										if ((moreTile != null) && moreTile.isShowing()) {
											image = getComponentImage(moreTile);
										}
									}
								}
							} else {
								java.awt.Component c = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
								if (c != null) {
									image = getComponentImage(c);
								}
							}
						}
					}
				}
			} else if (prefix.equals("editors")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) { 
						edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)) {
								//										Editor editor = jAliceFrame.tabbedEditorComponent.getEditorAt( jAliceFrame.tabbedEditorComponent.getIndexOfObject( elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { 
									java.awt.Component c = AuthoringToolResources.findButton(container, spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("elementTile") && (spec != null)) { 
									edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources.getPrefix(token);
											spec = AuthoringToolResources.getSpecifier(token);
											if (prefix.equals("property") && (spec != null)) { 
												java.awt.Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
												if (c != null) {
													image = getComponentImage(c);
												}
											} else if (prefix.equals("more")) { 
												java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
												if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
													java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
													if ((moreTile != null) && moreTile.isShowing()) {
														image = getComponentImage(moreTile);
													}
												}
											}
										} else {
											java.awt.Component c = AuthoringToolResources.findElementDnDPanel(container, element);
											if (c != null) {
												image = getComponentImage(c);
											}
										}
									}
								} else if (prefix.equals("elementPrototypeTile") && (spec != null)) { 
									try {
										Class elementClass = Class.forName(spec);
										if (elementClass != null) {
											java.awt.Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
											if (c != null) {
												image = getComponentImage(c);
											}
										}
									} catch (Exception e) {
										AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_", spec), e); 
									}
								}
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
							if ((tabIndex >= 0) && (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
								r = jAliceFrame.tabbedEditorComponent.tabbedPane.getUI().getTabBounds(jAliceFrame.tabbedEditorComponent.tabbedPane, tabIndex);
								r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.tabbedEditorComponent.tabbedPane, r, jAliceFrame.getGlassPane());
							}
						}
					}
				} else {
					r = jAliceFrame.tabbedEditorComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.tabbedEditorComponent.getParent(), r, jAliceFrame.getGlassPane());
				}
			} else if (componentMap.containsKey(prefix)) {
				java.awt.Component c = (java.awt.Component) componentMap.get(prefix);
				if (c != null) {
					image = getComponentImage(c);
				}
			}
		}
		return image;
	}

	public java.awt.Rectangle getBoxForID(String id) throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
		java.awt.Rectangle r = null;
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":", false); 
		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) { 
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						javax.swing.JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel worldTreeModel = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel) tree.getModel();
						r = tree.getPathBounds(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
						if ((r != null) && (!worldTreeModel.isLeaf(element))) { //HACK to include expand handle
							r.x -= 15;
							r.width += 15;
						}
						if (r != null) {
							r = javax.swing.SwingUtilities.convertRectangle(tree, r, jAliceFrame.getGlassPane());
						}
					}
				} else {
					r = jAliceFrame.worldTreeComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.worldTreeComponent.getParent(), r, jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("clipboard")) { 
				if (spec != null) {
					try {
						int index = Integer.parseInt(spec);
						if (index > -1) {
							java.awt.Component c = jAliceFrame.clipboardPanel.getComponent(index);
							if (c != null) {
								r = c.getBounds();
								r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
							}
						}
					} catch (Exception e) {
					}
				}

			} else if (prefix.equals("sceneEditor")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView, assume we're in the right mode
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) { 
							String directory = jAliceFrame.sceneEditor.getGalleryViewer().getDirectory();
							String gallery = directory;
							if ( directory.indexOf("(") >= 0 )
								gallery = directory.substring(directory.lastIndexOf("(")-1, directory.lastIndexOf(")")+1);
							jAliceFrame.sceneEditor.getGalleryViewer().setDirectory(spec);
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) {  
									java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec + gallery);
									if (c == null) {
										c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), Messages.getString(spec.replaceAll(" ", "_")) + gallery);
									}
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
									}
								} else if (prefix.equals("galleryObject")) { 
									java.awt.Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
									}
								}
							} else {
								r = jAliceFrame.sceneEditor.getGalleryViewer().getBounds();
								r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.sceneEditor.getGalleryViewer().getParent(), r, jAliceFrame.getGlassPane());
							}
						} else {
							java.awt.Component c = jAliceFrame.sceneEditor.getComponentForId(token);
							if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
								r = c.getBounds();
								r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
							}
						}
					} else {
						r = jAliceFrame.sceneEditor.getBounds();
						r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.sceneEditor.getParent(), r, jAliceFrame.getGlassPane());
					}
				} else {
					r = jAliceFrame.sceneEditor.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.sceneEditor.getParent(), r, jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("details")) { 
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
						if (st.hasMoreTokens()) {
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("tab")) { 
								int tabIndex = Integer.parseInt(spec);
								r = jAliceFrame.dragFromComponent.tabbedPane.getUI().getTabBounds(jAliceFrame.dragFromComponent.tabbedPane, tabIndex);
								r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.dragFromComponent.tabbedPane, r, jAliceFrame.getGlassPane());
								return r;
							} else if (prefix.equals("viewController")) { 
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources.getPrefix(token);
									spec = AuthoringToolResources.getSpecifier(token);
									java.awt.Component c = jAliceFrame.dragFromComponent.getPropertyViewComponentForKey(token);
									if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
									}
								}
							} else {
								java.awt.Component c = jAliceFrame.dragFromComponent.getComponentForKey(token);
								if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
									r = c.getBounds();
									r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
								}
							}
						} else {
							r = jAliceFrame.dragFromComponent.getBounds();
							r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.dragFromComponent.getParent(), r, jAliceFrame.getGlassPane());
						}
					}
				} else {
					r = jAliceFrame.dragFromComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.dragFromComponent.getParent(), r, jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("behaviors")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewEventButton")) { 
						java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new event"); 
						if (c != null) {
							r = c.getBounds();
							r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
						}
					} else if (prefix.equals("elementTile") && (spec != null)) { 
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) { 
									java.awt.Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
									}
								} else if (prefix.equals("more")) { 
									java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
										if ((moreTile != null) && moreTile.isShowing()) {
											r = moreTile.getBounds();
											r = javax.swing.SwingUtilities.convertRectangle(moreTile.getParent(), r, jAliceFrame.getGlassPane());
										}
									}
								}
							} else {
								java.awt.Component c = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
								if (c != null) {
									r = c.getBounds();
									r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
								}
							}
						}
					}
				} else {
					r = jAliceFrame.behaviorGroupsEditor.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.behaviorGroupsEditor.getParent(), r, jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("editors")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) { 
						edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)) {
								//								Editor editor = jAliceFrame.tabbedEditorComponent.getEditorAt( jAliceFrame.tabbedEditorComponent.getIndexOfObject( elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { 
									java.awt.Component c = AuthoringToolResources.findButton(container, spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
									}
								} else if (prefix.equals("elementTile") && (spec != null)) { 
									edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources.getPrefix(token);
											spec = AuthoringToolResources.getSpecifier(token);
											if (prefix.equals("property") && (spec != null)) { 
												java.awt.Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
												if (c != null) {
													r = c.getBounds();
													r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
												}
											} else if (prefix.equals("more")) { 
												java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
												if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
													java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
													if ((moreTile != null) && moreTile.isShowing()) {
														r = moreTile.getBounds();
														r = javax.swing.SwingUtilities.convertRectangle(moreTile.getParent(), r, jAliceFrame.getGlassPane());
													}
												}
											}
										} else {
											java.awt.Component c = AuthoringToolResources.findElementDnDPanel(container, element);
											if (c != null) {
												r = c.getBounds();
												r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
											}
										}
									}
								} else if (prefix.equals("elementPrototypeTile") && (spec != null)) { 
									try {
										Class elementClass = Class.forName(spec);
										if (elementClass != null) {
											java.awt.Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
											if (c != null) {
												r = c.getBounds();
												r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
											}
										}
									} catch (Exception e) {
										AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_", spec), e); 
									}
								}
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
							if ((tabIndex >= 0) && (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
								r = jAliceFrame.tabbedEditorComponent.tabbedPane.getUI().getTabBounds(jAliceFrame.tabbedEditorComponent.tabbedPane, tabIndex);
								r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.tabbedEditorComponent.tabbedPane, r, jAliceFrame.getGlassPane());
							}
						}
					}
				} else {
					r = jAliceFrame.tabbedEditorComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.tabbedEditorComponent.getParent(), r, jAliceFrame.getGlassPane());
				}
			} else if (componentMap.containsKey(prefix)) {
				java.awt.Component c = (java.awt.Component) componentMap.get(prefix);
				if (c != null) {
					r = c.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
				}
			}
		}

		if (r == null) {
			throw new edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException(id);
		}
		return r;
	}

	public boolean isComponentVisible(javax.swing.JComponent c) {
		if (c == null) {
			return false;
		}
		java.awt.Rectangle visibleR = c.getVisibleRect();
		java.awt.Rectangle ourRect = c.getBounds();
		return (ourRect.width == visibleR.width && ourRect.height == visibleR.height);
		//		java.awt.Component parent = c.getParent();
		//		while (parent != null && !(parent instanceof javax.swing.JScrollPane)){
		//			parent = parent.getParent();
		//		}
		//		if (parent == null){
		//			return c.isVisible();
		//		} else{
		//			javax.swing.JScrollPane parentScrollPane = (javax.swing.JScrollPane)parent;
		//			return false;
		//		}
	}
	
	public boolean isIDVisible(String id) throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":", false); 

		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) { 
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						javax.swing.JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel worldTreeModel = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel) tree.getModel();
						java.awt.Rectangle r = tree.getPathBounds(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
						if ((r != null) && tree.getVisibleRect().contains(r)) {
							return true;
						}
						return false;
					}
				}
			} else if (prefix.equals("clipboard")) { 
				if (spec != null) {
					try {
						int index = Integer.parseInt(spec);
						if (index > -1) {
							java.awt.Component c = jAliceFrame.clipboardPanel.getComponent(index);
							if (c != null && isComponentVisible((javax.swing.JComponent) c)) {
								return true;
							}
						}
					} catch (Exception e) {
						return false;
					}
				} 
				return false;
			} else if (prefix.equals("sceneEditor")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) { 
							String directory = jAliceFrame.sceneEditor.getGalleryViewer().getDirectory();
							String gallery = directory;
							if ( directory.indexOf("(") >= 0 ) {
								gallery = directory.substring(directory.lastIndexOf("(")-1, directory.lastIndexOf(")")+1);
								spec += gallery;
							}
							if (directory.equals(spec)) {
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources.getPrefix(token);
									spec = AuthoringToolResources.getSpecifier(token);
									if (prefix.equals("button")) { 
										java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec + gallery);
										if (c == null) {
											c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), Messages.getString(spec.replaceAll(" ", "_")) + gallery);
										}
										if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
											return true;
										}
										return false;
									} else if (prefix.equals("galleryObject")) { 
										java.awt.Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
										if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
											return true;
										}
										return false;
									}
								} else {
									if (jAliceFrame.sceneEditor.getGalleryViewer().isShowing()) {
										return true;
									}
									return false;
								}
							} else {
								return false;
							}
						} else {
							java.awt.Component c = jAliceFrame.sceneEditor.getComponentForId(token);
							if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
								return true;
							}
							return false;
						}
					}
				}
			} else if (prefix.equals("details")) { 
				if (spec == null) {
					spec = ""; 
				}
				edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
				if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("tab")) { 
							return true;
						}
						java.awt.Component c = jAliceFrame.dragFromComponent.getComponentForKey(token);
						//System.out.println(c+", "+isComponentVisible((javax.swing.JComponent)c)+", "+c.isVisible());
						if (c != null){
							java.awt.Rectangle boundss = c.getBounds();
							boundss = javax.swing.SwingUtilities.convertRectangle(c.getParent(), boundss, jAliceFrame);
						}
						//System.out.println(boundss);
						//System.out.println(jAliceFrame.getBounds());
						if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
							java.awt.Rectangle bounds = c.getBounds();
							javax.swing.SwingUtilities.convertRectangle(c.getParent(), bounds, jAliceFrame.dragFromComponent);
							if (jAliceFrame.dragFromComponent.getVisibleRect().contains(bounds)) {
								//System.out.println("returning true");
								return true;
							}
							return false;
						}
						return false;
					}
					return false;
				}
				//TODO is this safe?
				return false;
			} else if (prefix.equals("behaviors")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewBehaviorButton")) { 
						java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new behavior"); 
						if ((c != null) && c.isShowing()) {
							return true;
						}
						return false;
					} else if (prefix.equals("elementTile") && (spec != null)) { 
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) { 
									java.awt.Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
									if ((c != null) && c.isShowing()) {
										java.awt.Rectangle bounds = c.getBounds();
										bounds = javax.swing.SwingUtilities.convertRectangle(c.getParent(), bounds, jAliceFrame.behaviorGroupsEditor.getScrollPane());
										if (jAliceFrame.behaviorGroupsEditor.getScrollPaneVisibleRect().contains(bounds)) {
											return true;
										}
										return false;
									}
									return false;
								} else if (prefix.equals("more")) { 
									java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
										if ((moreTile != null) && moreTile.isShowing()) {
											java.awt.Rectangle bounds = moreTile.getBounds();
											javax.swing.SwingUtilities.convertRectangle(moreTile.getParent(), bounds, jAliceFrame.behaviorGroupsEditor.getScrollPane());
											if (jAliceFrame.behaviorGroupsEditor.getScrollPaneVisibleRect().contains(bounds)) {
												return true;
											}
											return false;
										}
										return false;
									}
								}
							} else {
								java.awt.Component c = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
								boolean visibleRectNotEmpty = true;
								if (c instanceof javax.swing.JComponent) {
									visibleRectNotEmpty = !((javax.swing.JComponent) c).getVisibleRect().isEmpty();
								}
								if ((c != null) && c.isShowing() && visibleRectNotEmpty) {
									java.awt.Rectangle bounds = c.getBounds();
									javax.swing.SwingUtilities.convertRectangle(c.getParent(), bounds, jAliceFrame.behaviorGroupsEditor.getScrollPane());
									if (jAliceFrame.behaviorGroupsEditor.getScrollPaneVisibleRect().contains(bounds)) {
										return true;
									}
									return false;
								}
								return false;
							}
						}
					}
				}
			} else if (prefix.equals("editors")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) { 
						edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)) {
								//								Editor editor = jAliceFrame.tabbedEditorComponent.getEditorAt( jAliceFrame.tabbedEditorComponent.getIndexOfObject( elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { 
									java.awt.Component c = AuthoringToolResources.findButton(container, spec);
									if ((c != null) && c.isShowing()) {
										return true;
									}
									return false;
								} else if (prefix.equals("elementTile") && (spec != null)) { 
									edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources.getPrefix(token);
											spec = AuthoringToolResources.getSpecifier(token);
											if (prefix.equals("property") && (spec != null)) { 
												java.awt.Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
												if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
													return true;
												}
												return false;
											} else if (prefix.equals("more")) { 
												java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
												if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
													java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
													if ((moreTile != null) && moreTile.isShowing()) {
														return true;
													}
													return false;
												}
											}
										} else {
											java.awt.Component c = AuthoringToolResources.findElementDnDPanel(container, element);
											if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
												return true;
											}
											return false;
										}
									}
								} else if (prefix.equals("elementPrototypeTile") && (spec != null)) { 
									try {
										Class elementClass = Class.forName(spec);
										if (elementClass != null) {
											java.awt.Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
											if ((c != null) && c.isShowing()) {
												return true;
											}
											return false;
										}
									} catch (Exception e) {
										AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_", spec), e); 
									}
								}
							} else {
								return false;
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
							if ((tabIndex >= 0) && (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
								return true;
							}
							return false;
						}
					}
				}
			} else if (componentMap.containsKey(prefix)) {
				java.awt.Component c = (java.awt.Component) componentMap.get(prefix);
				if ((c != null) && c.isShowing()) {
					return true;
				}
				return false;
			}
		}

		return true;
	}

	public void makeIDVisible(String id) throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":", false); 

		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);

			if (prefix.equals("objectTree")) { 
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						javax.swing.JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel worldTreeModel = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel) tree.getModel();
						tree.scrollPathToVisible(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
					}
				}
			} else if (prefix.equals("clipboard")) { 
				//Do nothing

			} else if (prefix.equals("sceneEditor")) { 
				if (spec.equals("large")) { 
					jAliceFrame.sceneEditor.setGuiMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.LARGE_MODE);
					//	System.out.println("just made scene editor large");
				} else if (spec.equals("small")) { 
					jAliceFrame.sceneEditor.setGuiMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.SMALL_MODE);
					//	System.out.println("just made scene editor small");
				}
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView
					if (token.equals("singleView")) { 
						jAliceFrame.sceneEditor.setViewMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.SINGLE_VIEW_MODE);
						//	System.out.println("just set single view");
					} else if (token.equals("quadView")) { 
						jAliceFrame.sceneEditor.setViewMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.QUAD_VIEW_MODE);
						//	System.out.println("just made set quad view");
					}

					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) { 
							//jAliceFrame.sceneEditor.getGalleryViewer().setDirectory( spec );
							//		System.out.println("trying to show gallery viewer");
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { 
									java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
									}
								} else if (prefix.equals("galleryObject")) { 
									java.awt.Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c.getParent()).scrollRectToVisible(c.getBounds());
									}
								}
							} else {
								//		System.out.println("no more tokens");
							}
						} else {
							java.awt.Component c = jAliceFrame.sceneEditor.getComponentForId(token);
							if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
								((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
							}
						}
					}
				}
			} else if (prefix.equals("details")) { 
				if (spec == null) {
					spec = ""; 
				}
				edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
				if (!jAliceFrame.dragFromComponent.getElement().equals(element)) {
					jAliceFrame.dragFromComponent.setElement(element);
				}
				if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);

						if (!prefix.equals("tab")) { 
							boolean isViewController = false;
							if (prefix.equals("viewController")) { 
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources.getPrefix(token);
									spec = AuthoringToolResources.getSpecifier(token);
									isViewController = true;
								}
							}
							if (prefix.equals("property") || prefix.equals("variable") || prefix.equals("textureMap") || prefix.equals("sound") || prefix.equals("other")) {    //$NON-NLS-4$ //$NON-NLS-5$
								jAliceFrame.dragFromComponent.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.PROPERTIES_TAB);
							}
							if (prefix.equals("userDefinedResponse") || prefix.equals("responsePrototype")) {  
								jAliceFrame.dragFromComponent.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.ANIMATIONS_TAB);
							}
							if (prefix.equals("userDefinedQuestion") || prefix.equals("questionPrototype")) {  
								jAliceFrame.dragFromComponent.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.QUESTIONS_TAB);
							}
							java.awt.Component c = null;
							if (isViewController) {
								c = jAliceFrame.dragFromComponent.getPropertyViewComponentForKey(token);
							} else {
								c = jAliceFrame.dragFromComponent.getComponentForKey(token);
							}
							if (c != null && c.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel) {
								edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel) c.getParent();
								ep.setExpanded(true);
							}
							if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
								((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
							}
						}
					}
				}
			} else if (prefix.equals("behaviors")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("elementTile") && (spec != null)) { 
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) { 
									java.awt.Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
									if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
									}
								} else if (prefix.equals("more")) { 
									java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
										if ((moreTile != null) && moreTile.isShowing() && (moreTile instanceof javax.swing.JComponent)) {
											((javax.swing.JComponent) moreTile).scrollRectToVisible(moreTile.getBounds());
										}
									}
								}
							} else {
								java.awt.Component c = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
								if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
									//									((javax.swing.JComponent)c).scrollRectToVisible( c.getBounds() );
									 ((javax.swing.JComponent) c).scrollRectToVisible(new java.awt.Rectangle(0, 0, c.getWidth(), c.getHeight()));
								}
							}
						}
					}
				}
			} else if (prefix.equals("editors")) { 
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) { 
						edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
						if (elementBeingEdited == null) {
							throw new edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException(spec);
						}
						if (st.hasMoreTokens()) {
							if (jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != elementBeingEdited) {
								editObject(elementBeingEdited);
							}

							// Editor editor = jAliceFrame.tabbedEditorComponent.getEditorAt( jAliceFrame.tabbedEditorComponent.getIndexOfObject( elementBeingEdited ) );
							java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("button")) { 
								java.awt.Component c = AuthoringToolResources.findButton(container, spec);
								if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
									((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
								}
							} else if (prefix.equals("elementTile") && (spec != null)) { 
								edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
								if (element != null) {
									if (st.hasMoreTokens()) {
										token = st.nextToken();
										prefix = AuthoringToolResources.getPrefix(token);
										spec = AuthoringToolResources.getSpecifier(token);
										if (prefix.equals("property") && (spec != null)) { 
											java.awt.Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
											if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
												((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
											}
										} else if (prefix.equals("more")) { 
											java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
											if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
												java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
												if ((moreTile != null) && moreTile.isShowing() && (moreTile instanceof javax.swing.JComponent)) {
													((javax.swing.JComponent) moreTile).scrollRectToVisible(moreTile.getBounds());
												}
											}
										}
									} else {
										java.awt.Component c = AuthoringToolResources.findElementDnDPanel(container, element);
										if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
											((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
										}
									}
								}
							} else if (prefix.equals("elementPrototypeTile") && (spec != null)) { 
								try {
									Class elementClass = Class.forName(spec);
									if (elementClass != null) {
										java.awt.Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
										if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
											((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
										}
									}
								} catch (Exception e) {
									AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_", spec), e); 
								}
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
							if ((tabIndex < 0) || (tabIndex >= jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
								editObject(elementBeingEdited);
							}
						}
					}
				}
			} else if (componentMap.containsKey(prefix)) {
				java.awt.Component c = (java.awt.Component) componentMap.get(prefix);
				if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
					((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
				}
			}
		}
	}

	synchronized public void makeWayPoint() {
		if (wayPoints.size() > 0) {
			edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule currentWayPoint = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule) wayPoints.get(0);
			currentWayPoint.stopListening();
		}

		edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule wayPoint = new edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule(this, world);
		wayPoints.add(0, wayPoint);
	}

	synchronized public void goToPreviousWayPoint() {
		if (wayPoints.size() > 0) {
			edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule currentWayPoint = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule) wayPoints.get(0);
			currentWayPoint.restoreWorld();
			currentWayPoint.dispose();
			wayPoints.remove(0);
		}

		if (wayPoints.size() > 0) {
			edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule previousWayPoint = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule) wayPoints.get(0);
			previousWayPoint.restoreWorld();
			previousWayPoint.startListening();
		}
	}

	synchronized public void clearWayPoints() {
		for (java.util.Iterator iter = wayPoints.iterator(); iter.hasNext();) {
			edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule wayPoint = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule) iter.next();
			wayPoint.dispose();
		}
		wayPoints.clear();
	}

	public boolean doesStateMatch(edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule capsule) {
		if (capsule instanceof edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule) {
			edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule stencilStateCapsule = (edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule) capsule;

			String[] existantElements = stencilStateCapsule.getExistantElements();
			String[] nonExistantElements = stencilStateCapsule.getNonExistantElements();
			java.util.Set propertyValueKeys = stencilStateCapsule.getPropertyValueKeySet();
			java.util.Set elementPositions = stencilStateCapsule.getElementPositionKeySet();

			// check for all the elements that need to exist
			for (int i = 0; i < existantElements.length; i++) {
				if (world.getDescendantKeyed(existantElements[i]) == null) {
					return false;
				}
			}

			//						  System.out.println("elements exist ok");

			// make sure all the elements that shouldn't exist don't
			for (int i = 0; i < nonExistantElements.length; i++) {
				if (world.getDescendantKeyed(nonExistantElements[i]) != null) {
					return false;
				}
			}

			//						  System.out.println("elements don't exist ok");

			// check that elements are in the right positions
			for (java.util.Iterator iter = elementPositions.iterator(); iter.hasNext();) {
				String elementKey = (String) iter.next();
				int position = stencilStateCapsule.getElementPosition(elementKey);

				edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(elementKey);
				if (element != null) {
					int actualPosition = element.getParent().getIndexOfChild(element);
					if (element instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
						edu.cmu.cs.stage3.alice.core.Property resp = element.getParent().getPropertyNamed("responses"); 
						if (resp instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
							actualPosition = ((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) resp).indexOf(element);
							//									System.out.println("index in responses: " + actualPosition);
						}
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
						edu.cmu.cs.stage3.alice.core.Property resp = element.getParent().getPropertyNamed("componentResponses"); 
						if (resp instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
							actualPosition = ((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) resp).indexOf(element);
							//									System.out.println("index in componentResponses: " + actualPosition);
						}
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
						edu.cmu.cs.stage3.alice.core.Property resp = element.getParent().getPropertyNamed("behaviors"); 
						if (resp instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
							actualPosition = ((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) resp).indexOf(element);
							// System.out.println("index in responses: " + actualPosition);
						}
					}

					// element isn't in the right place wrt to its parent
					if (position != actualPosition) {
						// System.out.println("actual position: " + actualPosition + " correct position: " + position);
						return false;
					}
				} else {
					// element doesn't exist and it should
					return false;
				}
			}
			// System.out.println("elements in correct positions ok");

			for (java.util.Iterator iter = propertyValueKeys.iterator(); iter.hasNext();) {
				String propertyKey = (String) iter.next();
				String valueRepr = stencilStateCapsule.getPropertyValue(propertyKey);
				int dotIndex = propertyKey.lastIndexOf("."); 
				String elementKey = propertyKey.substring(0, dotIndex);
				String propertyName = propertyKey.substring(dotIndex + 1);

				edu.cmu.cs.stage3.alice.core.Element propertyOwner = world.getDescendantKeyed(elementKey);
				if (propertyOwner != null) {

					// getting "properties" of a call to a user defined response
					if (propertyOwner instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
						edu.cmu.cs.stage3.alice.core.Property requiredParams = propertyOwner.getPropertyNamed("requiredActualParameters"); 

						Object udobj = requiredParams.getValue();
						if (udobj instanceof edu.cmu.cs.stage3.alice.core.Variable[]) {
							edu.cmu.cs.stage3.alice.core.Variable vars[] = (edu.cmu.cs.stage3.alice.core.Variable[]) udobj;
							if (vars != null) {
								for (int i = 0; i < vars.length; i++) {
									if (vars[i].getKey(world).equals(propertyKey)) {
										String actualValueRepr = AuthoringToolResources.getReprForValue(vars[i].getValue(), true);
										if (!actualValueRepr.equals(valueRepr)) {
											return false;
										}
									}
								}
							}
						}
					} else {
						Object value = propertyOwner.getPropertyNamed(propertyName).get();
						String actualValueRepr = AuthoringToolResources.getReprForValue(value, true);
						if (actualValueRepr != null) {
							if (!actualValueRepr.equals(valueRepr)) {
								return false;
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			}
			// System.out.println("property value checks ok");
			// if we've arrived here, it means that all the new conditions have been met. check to make sure user hasn't done anything else that's strange.
			edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule currentWayPoint = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule) wayPoints.get(0);

			if ((currentWayPoint.otherPropertyChangesMade(propertyValueKeys)) || (currentWayPoint.otherElementsInsertedOrDeleted(existantElements, nonExistantElements)) || (currentWayPoint.otherElementsShifted(elementPositions))) {
				return false;
			}
			return true;
		}
		return true;
	}

	public void handleMouseEvent(java.awt.event.MouseEvent ev) {
		java.awt.Point p = ev.getPoint();
		p = javax.swing.SwingUtilities.convertPoint((java.awt.Component) ev.getSource(), p, jAliceFrame.getLayeredPane());
		java.awt.Component newSource = jAliceFrame.getLayeredPane().findComponentAt(p);

		if ((newSource instanceof javax.swing.JLabel) || (newSource instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPrototypeDnDPanel.Tile)) {
			newSource = newSource.getParent(); // is this the right way to handle this?
		}

		switch (ev.getID()) {
			case java.awt.event.MouseEvent.MOUSE_DRAGGED :
				if (stencilDragging) {
					newSource = dragStartSource;
				}
				break;
			case java.awt.event.MouseEvent.MOUSE_PRESSED :
				stencilDragging = true;
				dragStartSource = newSource;
				break;
			case java.awt.event.MouseEvent.MOUSE_RELEASED :
				if (stencilDragging) {
					newSource = dragStartSource;
					dragStartSource = null;
				}
				stencilDragging = false;
				break;
			case java.awt.event.MouseEvent.MOUSE_CLICKED :
			case java.awt.event.MouseEvent.MOUSE_ENTERED :
			case java.awt.event.MouseEvent.MOUSE_EXITED :
			case java.awt.event.MouseEvent.MOUSE_MOVED :
			default :
				break;
		}
		if (newSource != null) {
			p = javax.swing.SwingUtilities.convertPoint(jAliceFrame.getLayeredPane(), p, newSource);
			java.awt.event.MouseEvent newEv = new java.awt.event.MouseEvent(newSource, ev.getID(), ev.getWhen(), ev.getModifiers(), p.x, p.y, ev.getClickCount(), ev.isPopupTrigger());
			ev.consume();
			newSource.dispatchEvent(newEv);
			//			}
		}
	}

	public void deFocus() {
		jAliceFrame.getContentPane().requestFocus();
	}

	public void performTask(String task) {
		String prefix = AuthoringToolResources.getPrefix(task);
		String spec = AuthoringToolResources.getSpecifier(task);

		if (prefix.equals("loadWorld")) { 
			//boolean askForSave = !((currentWorldLocation != null) && currentWorldLocation.getAbsolutePath().startsWith(tutorialDirectory.getAbsolutePath()));
			boolean askForSave = false;
			loadWorld(spec, askForSave);
		}
	}

	public java.awt.Dimension getScreenSize() {
		return jAliceFrame.getSize();
	}

	public void launchTutorial() {
		launchTutorialFile(null);
	}
	public void launchTutorialFile(java.io.File tutorialFile) {
		if (Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) ) > 12) { 
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("Alice_is_not_able_to_load_the_tutorial_"),  
					Messages.getString("Font_size_too_large"), 
					javax.swing.JOptionPane.WARNING_MESSAGE);
		} else {
			showStencils();
			stencilManager.loadStencilTutorial(tutorialFile == null ? tutorialOne : tutorialFile.getAbsoluteFile());
		}
	}
	public java.io.File getTutorialDirectory() {
		return tutorialDirectory;
	}
	public java.io.File getExampleWorldsDirectory() {
		return new java.io.File(authoringToolConfig.getValue("directories.examplesDirectory")).getAbsoluteFile(); 
	}
	public java.io.File getTemplateWorldsDirectory() {
		return new java.io.File(authoringToolConfig.getValue("directories.templatesDirectory")).getAbsoluteFile(); 
	}

	public javax.swing.filechooser.FileFilter getWorldFileFilter() {
		return worldFileFilter;
	}

	public javax.swing.filechooser.FileFilter getCharacterFileFilter() {
		return characterFileFilter;
	}

	//Dialog Handling

	public edu.cmu.cs.stage3.alice.core.Variable showNewVariableDialog(String title, edu.cmu.cs.stage3.alice.core.Element context) {
		newVariableContentPane.reset(context);
		newVariableContentPane.setTitle(title);
		newVariableContentPane.setListsOnly(false);
		newVariableContentPane.setShowValue(true);
		return showNewVariableDialog(newVariableContentPane, context);
	}

	public edu.cmu.cs.stage3.alice.core.Variable showNewVariableDialog(String title, edu.cmu.cs.stage3.alice.core.Element context, boolean listsOnly, boolean showValue) {
		newVariableContentPane.reset(context);
		newVariableContentPane.setListsOnly(listsOnly);
		newVariableContentPane.setShowValue(showValue);
		newVariableContentPane.setTitle(title);
		return showNewVariableDialog(newVariableContentPane, context);
	}

	protected edu.cmu.cs.stage3.alice.core.Variable showNewVariableDialog(edu.cmu.cs.stage3.alice.authoringtool.dialog.NewVariableContentPane newVariablePaneToShow, edu.cmu.cs.stage3.alice.core.Element context) {
		int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(newVariablePaneToShow);
		switch (result) {
			case edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION :
				return newVariablePaneToShow.getVariable();
			case edu.cmu.cs.stage3.swing.ContentPane.CANCEL_OPTION :
				return null;
			default :
				return null;
		}
	}

	public edu.cmu.cs.stage3.alice.core.Sound promptUserForRecordedSound(edu.cmu.cs.stage3.alice.core.Sandbox parent) {
    	if(currentWorldLocation == null) {
    		if (saveWorldAs()!=Constants.SUCCEEDED)
    			return null;
    	}
		String directory = currentWorldLocation.getParent();
		directory = directory.replace('\\', '/');
		java.io.File dir = new java.io.File(directory + "/sound"); 
		dir.mkdir();
	
		final edu.cmu.cs.stage3.alice.authoringtool.dialog.SoundRecorder soundRecorder = new edu.cmu.cs.stage3.alice.authoringtool.dialog.SoundRecorder(dir);
		soundRecorder.setParentToCheckForNameValidity(parent);

		int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(soundRecorder);
		edu.cmu.cs.stage3.alice.core.Sound sound = null;

		switch (result) {
			case edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION :
				sound = soundRecorder.getSound();
				if (sound != null) {
					getUndoRedoStack().startCompound();
					try {
						parent.addChild(sound);
						parent.sounds.add(sound);
					} finally {
						getUndoRedoStack().stopCompound();
					}
				}
				break;
		}
		return sound;
	}
	
	public java.io.File getCurrentWorldLocation() {
		return currentWorldLocation;
	}
	
	public String getCurrentRendererText() {
		return renderTargetFactory.getClass().toString();
	}

	// Logging
	public void logInstructorIntervention(){
		//get comment if starting something new
		String comment = ""; 
		if (instructorInControl == false) {
			comment = javax.swing.JOptionPane.showInputDialog(Messages.getString("What_are_you_demonstrating_")); 
			if (comment != null) instructorInControl = true;
		} else {
			instructorInControl = false;
		}
		//log it
		if (undoRedoStack != null){
			if (instructorInControl){
				undoRedoStack.logInstructorIntervention(Messages.getString("start"), comment); 
			} else {
				if (comment != null) undoRedoStack.logInstructorIntervention(Messages.getString("end"), comment); 
			}	
		}
	}




	private boolean worldRun() {
		undoRedoStack.setIsListening(false);

		fireStateChanging(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.RUNTIME_STATE);
		fireWorldStarting(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.RUNTIME_STATE, world);

		//		if( authoringToolConfig.getValue( "reloadWorldScriptOnRun" ).equalsIgnoreCase( "true" ) ) {
		//			try {
		//				if( world.script.isAssociatedWithFile() ) {
		//					world.script.loadFromAssociatedFile();
		//				}
		//			} catch( java.io.IOException e ) {
		//				AuthoringTool.showErrorDialog( "Error while loading script from associated file.", e );
		//			}
		//		}

		// play count
		countSomething("edu.cmu.cs.stage3.alice.authoringtool.playCount"); 

		world.preserve();
		try {
//			scheduler.addDoOnceRunnable(new Runnable() {
//				public void run() {
//					double t = AuthoringToolResources.getCurrentTime();
//					world.start(t);
//					//System.out.println( "world.start( " + t + " )" );
//				}
//			});

			worldClock.start();
			scheduler.addEachFrameRunnable(worldScheduleRunnable);
			actions.pauseWorldAction.setEnabled(true);
			actions.resumeWorldAction.setEnabled(false);
			fireStateChanged(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.RUNTIME_STATE);
			fireWorldStarted(AuthoringToolStateChangedEvent.AUTHORING_STATE, AuthoringToolStateChangedEvent.RUNTIME_STATE, world);
		} catch (org.python.core.PyException e) {
			world.restore();
			AuthoringTool.showErrorDialog(Messages.getString("Error_during_world_start_"), null); 
			if (org.python.core.Py.matchException(e, org.python.core.Py.SystemExit)) {
				//TODO
			} else {
				org.python.core.Py.printException(e, null, pyStdErr);
			}
			return false;
		} catch (edu.cmu.cs.stage3.alice.core.SimulationException e) {
			world.restore();
			showSimulationExceptionDialog(e);
			return false;
		} catch (edu.cmu.cs.stage3.alice.core.ExceptionWrapper e) {
			world.restore();
			Exception wrappedException = e.getWrappedException();
			if (wrappedException instanceof edu.cmu.cs.stage3.alice.core.SimulationException) {
				showSimulationExceptionDialog((edu.cmu.cs.stage3.alice.core.SimulationException) wrappedException);
			} else {
				AuthoringTool.showErrorDialog(Messages.getString("Error_during_world_start_"), wrappedException); 
			}
			return false;
		} catch (Throwable t) {
			world.restore();
			showErrorDialog(Messages.getString("Error_during_world_start_"), t); 
			return false;
		}
		return true;
	}

	public void worldStopRunning() {
		fireStateChanging(AuthoringToolStateChangedEvent.RUNTIME_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE);
		fireWorldStopping(AuthoringToolStateChangedEvent.RUNTIME_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		//		Thread.dumpStack();
		scheduler.removeEachFrameRunnable(worldScheduleRunnable);
		try {
			//world.stop(AuthoringToolResources.getCurrentTime());
			worldClock.stop();
		} catch (org.python.core.PyException e) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_during_world_stop_"), null); 
			if (org.python.core.Py.matchException(e, org.python.core.Py.SystemExit)) {
				//TODO
			} else {
				org.python.core.Py.printException(e, null, pyStdErr);
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_during_world_stop_"), t); 
		}
		world.restore();
		undoRedoStack.setIsListening(true);
		fireStateChanged(AuthoringToolStateChangedEvent.RUNTIME_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE);
		fireWorldStopped(AuthoringToolStateChangedEvent.RUNTIME_STATE, AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
	}


	public double getAspectRatio() {
		double aspectRatio = 0.0;
		if (getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
			edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera cam = (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) getCurrentCamera();
			Number hAngle = cam.horizontalViewingAngle.getNumberValue();
			Number vAngle = cam.verticalViewingAngle.getNumberValue();
			if ((hAngle != null) && (vAngle != null)) {
				aspectRatio = hAngle.doubleValue() / vAngle.doubleValue();
			}
		}
		return aspectRatio;
	}

	private void checkForUnreferencedCurrentMethod() {
		Object object = getObjectBeingEdited();
		if (object instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
			if (!AuthoringToolResources.isMethodHookedUp((edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) object, world) && !authoringToolConfig.getValue("doNotShowUnhookedMethodWarning").equalsIgnoreCase("true")) {  
				String objectRepr = AuthoringToolResources.getReprForValue(object, true);
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("The_current_method____is_not_called_by_any_events_or_by_any_other_methods_which_might_be_called_by_any_events_", objectRepr),  
					Messages.getString("Warning__Current_method_will_not_be_called_"), 
					javax.swing.JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void play() {
		jAliceFrame.playButton.setEnabled(false);

		checkForUnreferencedCurrentMethod();

		if (worldRun()) {
			double aspectRatio = getAspectRatio();
			stdErrContentPane.stopReactingToPrint();
			stdOutContentPane.stopReactingToPrint();
			renderContentPane.setAspectRatio(aspectRatio);
			renderContentPane.getRenderPanel().add(renderPanel, java.awt.BorderLayout.CENTER);

			edu.cmu.cs.stage3.swing.DialogManager.showDialog(renderContentPane);
			stdErrContentPane.startReactingToPrint();
			stdOutContentPane.startReactingToPrint();
		}
		jAliceFrame.playButton.setEnabled(true);
	}

	public javax.swing.JPanel playWhileEncoding(String directory) {
		jAliceFrame.playButton.setEnabled(false);

		checkForUnreferencedCurrentMethod();

		if (worldRun()) {
			double aspectRatio = getAspectRatio();
			stdErrContentPane.stopReactingToPrint();
			stdOutContentPane.stopReactingToPrint();
			captureContentPane.setExportDirectory(directory);
			captureContentPane.captureInit();
			captureContentPane.setAspectRatio(aspectRatio);
			captureContentPane.getRenderPanel().add(renderPanel, java.awt.BorderLayout.CENTER);
			jAliceFrame.sceneEditor.makeDirty();
			edu.cmu.cs.stage3.swing.DialogManager.setResize(false);
			edu.cmu.cs.stage3.swing.DialogManager.showDialog(captureContentPane);
			stdErrContentPane.startReactingToPrint();
			stdOutContentPane.startReactingToPrint();
		}
		jAliceFrame.playButton.setEnabled(true);
		edu.cmu.cs.stage3.swing.DialogManager.setResize(true);
		return captureContentPane;
	}
		
	public void pause() {
		new Thread(new PauseSound(true)).start();
		worldClock.pause();
		actions.pauseWorldAction.setEnabled(false);
		actions.resumeWorldAction.setEnabled(true);
		renderTarget.getAWTComponent().requestFocus();
	}

	public edu.cmu.cs.stage3.alice.core.clock.DefaultClock getWorldClock() {
		return worldClock;	
	}
	
	public void resume() {
		new Thread(new PauseSound(false)).start();
		worldClock.resume();
		actions.pauseWorldAction.setEnabled(true);
		actions.resumeWorldAction.setEnabled(false);
		renderTarget.getAWTComponent().requestFocus();
	}

	public void restartWorld() {
		try {
			worldClock.stop();
			sound.clear();
			world.restore();
			actions.pauseWorldAction.setEnabled(true);
			actions.resumeWorldAction.setEnabled(false);
			//renderContentPane.setSpeedSliderValue(0);
			Thread.sleep(100);	// Need this pause so the world starts from the beginning. 
			worldClock.start();
		} catch (org.python.core.PyException e) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_while_restarting_world_"), null); 
			if (org.python.core.Py.matchException(e, org.python.core.Py.SystemExit)) {
				//TODO
			} else {
				org.python.core.Py.printException(e, null, pyStdErr);
			}
		} catch (edu.cmu.cs.stage3.alice.core.SimulationException e) {
			showSimulationExceptionDialog(e);
		} catch (edu.cmu.cs.stage3.alice.core.ExceptionWrapper e) {
			Exception wrappedException = e.getWrappedException();
			if (wrappedException instanceof edu.cmu.cs.stage3.alice.core.SimulationException) {
				showSimulationExceptionDialog((edu.cmu.cs.stage3.alice.core.SimulationException) wrappedException);
			} else {
				AuthoringTool.showErrorDialog(Messages.getString("Error_while_restarting_world_"), wrappedException); 
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_while_restarting_world_"), t); 
		}
		renderTarget.getAWTComponent().requestFocus();
	}

	public void stopWorld() {
		sound.clear();
		renderContentPane.stopWorld();
	}

	public void setWorldSpeed(double newSpeed) {
		worldClock.setSpeed( newSpeed );
	}

	public void takePicture() {
		try {
			storeCapturedImage(renderTarget.getOffscreenImage());
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("Error_capturing_image_"), t); 
		}
		renderTarget.getAWTComponent().requestFocus();
	}
	
	public movieMaker.SoundStorage getSoundStorage() {
		return soundStorage;
	}
	
	public void setSoundStorage(movieMaker.SoundStorage myS) {
		soundStorage = myS;
	}

	public static void pauseSound(edu.cmu.cs.stage3.media.Player m_player) {
		sound.add(m_player);
		m_player.startFromBeginning();
	}
	
	private class PauseSound implements Runnable {
		boolean pause = false;
		public PauseSound(boolean pause) {
			this.pause = pause;	
		}

		public void run() {
			if (pause) {
				for (int i=0; i<sound.size(); i++)
					(( edu.cmu.cs.stage3.media.Player ) sound.get(i)).stop();
			} else {
				for (int i=0; i<sound.size(); i++)
					(( edu.cmu.cs.stage3.media.Player ) sound.get(i)).start();
			}
		}

	}
	
	// Tutorial Editor
	public void openTutorialEditor() {
	    if (currentWorldLocation != null)
	    	stencilManager.setTutorialWorld(currentWorldLocation.getAbsolutePath());
	    stencilManager.setInstructorMode(true);
	    showStencils();
	}
	public void stencilManagerReFocus() {
	    jAliceFrame.removeKeyListener(this.stencilManager);
	    jAliceFrame.addKeyListener(this.stencilManager);
	    jAliceFrame.requestFocus();
	}

	// Aik Min
//    private static boolean pulsing = true;   
//    private static JLabel statusLabel = new JLabel(Messages.getString("Ready")); 
//    private static javax.swing.JFrame statusFrame;
    int numUpdate = 0;
/*    private boolean checkForUpdate(){
    	java.net.URL url;
    	java.net.URLConnection urlc = null;
		try {
			url = new java.net.URL(AuthoringToolResources.getMainUpdateURL().toString()+"alice.jar");
			urlc = url.openConnection();
			java.io.File old = new java.io.File(JAlice.getAliceHomeDirectory().toString() + System.getProperty( "file.separator" ) + "lib" + System.getProperty( "file.separator" ) + "alice.jar");
    		long date = urlc.getLastModified();
    		long oldDate = old.lastModified();
    		if (date > oldDate){
    			return true;
    		}
    	} catch ( Exception e){
			javax.swing.JOptionPane.showMessageDialog(null, 
					Messages.getString("Update_failed"), Messages.getString("Cannot_access_required_file_"), javax.swing.JOptionPane.ERROR_MESSAGE );  
    	}	
    	return false;
    }*/
    
	public void updateAlice() {
		javax.swing.JPanel updateDialog;
		final Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		
		final javax.swing.JDialog dlg = new javax.swing.JDialog(new JFrame(),Messages.getString("Checking_for_update"));
   		dlg.setPreferredSize(new Dimension(330,250));
   		dlg.setSize(new Dimension(330,250));
   		dlg.setLocation( dim.getSize().width/2-150, dim.getSize().height/2-50 );
        dlg.setAlwaysOnTop(true);
        dlg.setResizable(false);
        dlg.setVisible(true);
        
   		updateDialog = new javax.swing.JPanel();
   		updateDialog.setLayout(new BorderLayout());

		Box left = new Box(javax.swing.BoxLayout.PAGE_AXIS); // Box.createVerticalBox();
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
               
        final JCheckBox UpdateJAR = new JCheckBox("");
        //boolean updateAvailable = checkForUpdate();
        //dlg.setTitle(Messages.getString("Updating"));
        //if (updateAvailable) {
         	UpdateJAR.setText(Messages.getString("Update_Software"));
        	UpdateJAR.setSelected(true);
        // } else {
        // 	UpdateJAR.setText(Messages.getString("Update_Software__No_new_updates_"));
        // 	UpdateJAR.setSelected(false);
        // 	UpdateJAR.setEnabled(false);
        // }
        left.add(UpdateJAR);

        final JCheckBox CoreGallery = new JCheckBox(Messages.getString("Download_Core_Gallery"));
        left.add(CoreGallery);

        final JCheckBox EnglishGallery = new JCheckBox(Messages.getString("Download_English_Gallery"));
        left.add(EnglishGallery);

        final JCheckBox SpanishGallery = new JCheckBox(Messages.getString("Download_Spanish_Gallery"));
        left.add(SpanishGallery);
       
        updateDialog.add(left, BorderLayout.LINE_START);
        
        Box bottom = Box.createVerticalBox();
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        JButton ConfirmUpdate = new JButton(Messages.getString("Update"));
        ConfirmUpdate.setAlignmentX((float)0.5);
        ConfirmUpdate.addActionListener(new java.awt.event.ActionListener(){
        	public void actionPerformed(java.awt.event.ActionEvent e) {
        		dlg.dispose();
    			if ( UpdateJAR.isSelected() ) {
    				numUpdate++;
   					new Thread(new StartUpdating( "AliceUpdate.zip", 
    						JAlice.getAliceHomeDirectory().getParent().toString(), false )).start();
    			}
       			if ( CoreGallery.isSelected() ) {
    				numUpdate++;
    				new Thread(new StartUpdating( "CoreGallery.zip", 
    						JAlice.getAliceHomeDirectory().toString() + System.getProperty( "file.separator" ) + "gallery", true )).start();
    			}
    			if ( EnglishGallery.isSelected() ) {
    				numUpdate++;
    				new Thread(new StartUpdating( "EnglishGallery.zip", 
    						JAlice.getAliceHomeDirectory().toString() + System.getProperty( "file.separator" ) + "gallery", true )).start();
    			}
    			if ( SpanishGallery.isSelected() ) {
    				numUpdate++;
    				new Thread(new StartUpdating( "SpanishGallery.zip",
    						JAlice.getAliceHomeDirectory().toString() + System.getProperty( "file.separator" ) + "gallery", true )).start();
    			}
        		if (numUpdate == 0){
        			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("No_update_selected_"));
        		}		        
        	}
        });
        bottom.add(ConfirmUpdate);

        updateDialog.add(bottom, BorderLayout.PAGE_END);
		if (!AikMin.isLTR()){	// ***** For property tiles in the properties tab of the details pane *****
			updateDialog.applyComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}

        dlg.add(updateDialog);
		dlg.pack();
        dlg.repaint();
    }

  	
	public class StartUpdating implements Runnable {
		private boolean isGallery;
		private String file;
		private String dest;
    	private ProgressMonitor monitor = new ProgressMonitor(null, Messages.getString("Updating"), Messages.getString("Getting_Started___"), 0, 0); 
    	
		public StartUpdating(String filename, String location, boolean dir) {
	        file = filename;
	        dest = location;
	        isGallery = dir;
		
		}
		
		public void run() {
			java.io.BufferedInputStream bis = null;
			java.io.BufferedOutputStream bos = null;
			java.net.URLConnection urlc = null;
			try {
				java.net.URL url = new File("/Volumes/DATA/Dropbox (Alice Project)/Distribution/Alice 2.5/WEB/AliceUpdate.zip").toURI().toURL();//new java.net.URL(AuthoringToolResources.getMainUpdateURL().toString()+file);
				urlc = url.openConnection();
				monitor.setMaximum(urlc.getContentLength());
					
				java.io.File aliceHome = new java.io.File( dest );
				bis = new java.io.BufferedInputStream( urlc.getInputStream() );
				bos = new java.io.BufferedOutputStream( new java.io.FileOutputStream( aliceHome + System.getProperty( "file.separator" ) + file ) );	

				if (isGallery){
					if (!aliceHome.exists()){
						aliceHome.mkdir();
					}
					monitor.setNote(Messages.getString("Downloading_gallery"));
				} else {
					monitor.setNote(Messages.getString("Downloading_AliceUpdate_zip_file"));
				}
				
				int i, progress = 0;
				while ((i = bis.read()) != -1) {				
					bos.write( i );
					if (monitor.isCanceled()) {
						break;
					} 
					monitor.setProgress(progress++);						
				}

				monitor.setProgress(monitor.getMaximum());
				bis.close();
				bos.close();

				if ( !monitor.isCanceled()) {
					Enumeration entries;
					ZipFile zipFile;

					zipFile = new ZipFile(aliceHome + System.getProperty( "file.separator" ) + file);
					entries = zipFile.entries();
					
					monitor.setNote("Copying models to Alice gallery");
					monitor.setProgress(0); progress = 0;
					monitor.setMaximum(zipFile.size());

					while (entries.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) entries.nextElement();
						File file = new File (aliceHome + System.getProperty( "file.separator" ) + entry.getName());
						if (entry.isDirectory()) {
							file.mkdir();
							continue;
						} 
						file.getParentFile().mkdirs();
						InputStream in = zipFile.getInputStream(entry);
						OutputStream out = new BufferedOutputStream(new FileOutputStream(aliceHome + System.getProperty( "file.separator" ) + entry.getName()));
						byte[] buffer = new byte[1024];
						int len;
						while ((len = in.read(buffer)) >= 0)
							out.write(buffer, 0, len);
						in.close();
						out.flush();
						out.close();				
						monitor.setProgress(progress++);
					}
					monitor.setProgress(monitor.getMaximum());
					zipFile.close();
	        	}
	        } catch ( java.io.FileNotFoundException e){
	        	AuthoringTool.showErrorDialog("Error encountered during update", e); 
			} catch ( java.util.zip.ZipException ze ){
				AuthoringTool.showErrorDialog("Error encountered during update", ze); 
			} catch (Exception e1) {
				javax.swing.JOptionPane.showMessageDialog(null, " "+Messages.getString("Update_failed__Please_check_your_internet_connection__"), Messages.getString("Update_failed"), javax.swing.JOptionPane.ERROR_MESSAGE );  
	        } finally {
	        	if (bis != null)
	            try {
	            	bis.close();
				} catch (java.io.IOException ioe) {
					ioe.printStackTrace();
				}
				if (bos != null)
				try {
				    bos.close();
				} catch (java.io.IOException ioe) {
				    ioe.printStackTrace();
				}          
				numUpdate--;
	        	java.io.File temp;
	        	temp = new java.io.File( dest + System.getProperty( "file.separator" ) + file);
	        	if ( temp.exists() )
	        		temp.delete();        		
	        	if (numUpdate == 0) {
					Object[] options = {Messages.getString("Restart"), Messages.getString("Cancel")};
					int result = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(" "+Messages.getString("You_must_restart_Alice_for_the_updates_to_take_effect__"), Messages.getString("Update_completed"), JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0] );  
					if (result == ContentPane.OK_OPTION){
						monitor.close();
						quit(true);
					}
	        	}
	        	monitor.close();
				
	        }
		}
	}

	

}
