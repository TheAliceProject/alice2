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

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;

/**
 * @author Jason Pratt
 */
public class AuthoringTool implements java.awt.datatransfer.ClipboardOwner, edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication {
	// file extensions
	public static final String CHARACTER_EXTENSION = "a2c"; //$NON-NLS-1$
	public static final String WORLD_EXTENSION = "a2w"; //$NON-NLS-1$

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
	private edu.cmu.cs.stage3.alice.authoringtool.dialog.WorldInfoContentPane worldInfoContentPane;
	public edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane stdErrOutContentPane;
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
				if( property.getName().equals( "name" ) ) { //$NON-NLS-1$
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
				if( oap.getName().equals( "requiredFormalParameters" ) ) { //$NON-NLS-1$
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
				if( oap.getName().equals( "requiredFormalParameters" ) ) { //$NON-NLS-1$
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
		String font = "SansSerif"; // "Tahoma"; //$NON-NLS-1$
		try {
			javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); //$NON-NLS-1$

			//			javax.swing.UIManager.put( "Button.focus", new java.awt.Color( 255, 255, 255, 0 ) ); // don't show focus  // makes printing slow, unfortunately

			class CustomButtonBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
				protected java.awt.Insets insets = new java.awt.Insets(3, 3, 3, 3);
				protected javax.swing.border.Border line = javax.swing.BorderFactory.createLineBorder(java.awt.Color.black, 1);
				protected javax.swing.border.Border spacer = javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 4);
				protected javax.swing.border.Border raisedBevel = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED);
				protected javax.swing.border.Border loweredBevel = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED);
				protected javax.swing.border.Border raisedBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(line, raisedBevel), spacer);
				protected javax.swing.border.Border loweredBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(line, loweredBevel), spacer);
				//				protected javax.swing.border.Border raisedBorder = javax.swing.BorderFactory.createCompoundBorder( raisedBevel, spacer );
				//				protected javax.swing.border.Border loweredBorder = javax.swing.BorderFactory.createCompoundBorder( loweredBevel, spacer );

				public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int w, int h) {
					javax.swing.JButton button = (javax.swing.JButton) c;
					javax.swing.ButtonModel model = button.getModel();

					if (model.isEnabled()) {
						if (model.isPressed() && model.isArmed()) {
							loweredBorder.paintBorder(button, g, x, y, w, h);
						} else {
							raisedBorder.paintBorder(button, g, x, y, w, h);
						}
					} else {
						raisedBorder.paintBorder(button, g, x, y, w, h);
					}
				}

				public java.awt.Insets getBorderInsets(java.awt.Component c) {
					return insets;
				}
			}
			javax.swing.UIManager.put("Button.border", new javax.swing.plaf.BorderUIResource.CompoundBorderUIResource(new CustomButtonBorder(), new javax.swing.plaf.basic.BasicBorders.MarginBorder())); //$NON-NLS-1$

			//necessary for 1.4
	
			javax.swing.UIManager.put("Label.font", new java.awt.Font(font, java.awt.Font.BOLD, 12)); //$NON-NLS-1$
			javax.swing.UIManager.put("Label.foreground", edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("mainFontColor")); //$NON-NLS-1$ //$NON-NLS-2$
			javax.swing.UIManager.put("TabbedPane.selected", new java.awt.Color(255, 255, 255, 0)); //$NON-NLS-1$
			javax.swing.UIManager.put("TabbedPane.tabInsets", new java.awt.Insets(1, 4, 1, 3)); //$NON-NLS-1$

			if ((System.getProperty("os.name") != null) && System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				javax.swing.UIManager.put("FileChooserUI", "com.sun.java.swing.plaf.windows.WindowsFileChooserUI"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (Exception e) {
			showErrorDialog(Messages.getString("AuthoringTool.18"), e); //$NON-NLS-1$
		}

		AuthoringTool.hack = this;
		this.defaultWorld = defaultWorld;
		if (!(defaultWorld.exists() && defaultWorld.canRead())) {
			this.defaultWorld = null;
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(defaultWorld.getAbsolutePath() + Messages.getString("AuthoringTool.19"), Messages.getString("AuthoringTool.20"), javax.swing.JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		}

		filterInit();
		configInit();	
		try{
			AikMin.setFontSize(Integer.parseInt( authoringToolConfig.getValue( "fontSize" )));		 //$NON-NLS-1$
			if (authoringToolConfig.getValue( "enableHighContrastMode" ).equalsIgnoreCase("true")){ //$NON-NLS-1$ //$NON-NLS-2$
				javax.swing.UIManager.put("Label.foreground", java.awt.Color.black); //$NON-NLS-1$
			}
		} catch (Exception e){}
		mainInit();
		this.stdOutToConsole = stdOutToConsole;
		this.stdErrToConsole = stdErrToConsole;
		initializeOutput(stdOutToConsole, stdErrToConsole);
		pyInit();
		//AikMin.setFontSize(12);		
		dialogInit();
		//AikMin.setFontSize(Integer.parseInt( authoringToolConfig.getValue( "fontSize" )));		
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
		worldInit(worldToLoad);
		if( worldToLoad == null ) {
			if (authoringToolConfig.getValue("showStartUpDialog").equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
				showStartUpDialog(edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.DO_NOT_CHANGE_TAB_ID);
			}
		}
	}

	private void filterInit() {
		worldFileFilter = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter(WORLD_EXTENSION, WORLD_EXTENSION.toUpperCase() + Messages.getString("AuthoringTool.27")); //$NON-NLS-1$
		characterFileFilter = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter(CHARACTER_EXTENSION, CHARACTER_EXTENSION.toUpperCase() + Messages.getString("AuthoringTool.28")); //$NON-NLS-1$
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
		String worldsDirPath = authoringToolConfig.getValue("directories.worldsDirectory"); //$NON-NLS-1$
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
		String importDirPath = authoringToolConfig.getValue("directories.importDirectory"); //$NON-NLS-1$
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
		String charactersDirPath = authoringToolConfig.getValue("directories.charactersDirectory"); //$NON-NLS-1$
		if (charactersDirPath != null) {
			java.io.File charactersDir = new java.io.File(charactersDirPath);
			if (charactersDir != null && charactersDir.exists() && charactersDir.isDirectory()) {
				try {
					addCharacterFileChooser.setCurrentDirectory(charactersDir);
					saveCharacterFileDialog.setCurrentDirectory(charactersDir);//.getAbsolutePath());
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

	private void configInit() {
		authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.addConfigurationListener(new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener() {
			public void changing(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
			}

			public void changed(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
				if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.recentWorlds.maxWorlds")) { //$NON-NLS-1$
					AuthoringTool.this.jAliceFrame.updateRecentWorlds();
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.numberOfClipboards")) { //$NON-NLS-1$
					AuthoringTool.this.jAliceFrame.updateClipboards();
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.showWorldStats")) { //$NON-NLS-1$
					AuthoringTool.this.jAliceFrame.showStatusPanel(ev.getNewValue().equalsIgnoreCase("true")); //$NON-NLS-1$
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.worldsDirectory")) { //$NON-NLS-1$
					AuthoringTool.this.worldsDirectoryChanged();
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.importDirectory")) { //$NON-NLS-1$
					AuthoringTool.this.importDirectoryChanged();
				} else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.charactersDirectory")) { //$NON-NLS-1$
					AuthoringTool.this.charactersDirectoryChanged();
				}
			}
		});
	}

	private void dialogInit() {
		importFileChooser = new javax.swing.JFileChooser();
		saveWorldFileChooser = new javax.swing.JFileChooser() {
			public void approveSelection() {
				java.io.File desiredFile = getSelectedFile();
				if (currentWorldLocation == null || currentWorldLocation.equals(desiredFile) || !desiredFile.exists()) {
					if (shouldAllowOverwrite(desiredFile)) {
						super.approveSelection();
					} else {
						edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("AuthoringTool.39")); //$NON-NLS-1$
					}
				} else if (desiredFile.exists()) {
					if (shouldAllowOverwrite(desiredFile)) {
						int n = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("AuthoringTool.40"), Messages.getString("AuthoringTool.41"), javax.swing.JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
						if (n == javax.swing.JOptionPane.YES_OPTION) {
							super.approveSelection();
						}
					} else {
						edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("AuthoringTool.42")); //$NON-NLS-1$
					}

				}
			}
		};
		addCharacterFileChooser = new javax.swing.JFileChooser();
		saveCharacterFileDialog = new javax.swing.JFileChooser();
		
		browseFileChooser = new javax.swing.JFileChooser();
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
		int fontSize = Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) ); //$NON-NLS-1$
		int x = 700 + ( fontSize - 12 ) * 25;
		int y = 405 + (fontSize - 12);
				
		saveCharacterFileDialog.setDialogTitle(Messages.getString("AuthoringTool.44")); //$NON-NLS-1$
		saveCharacterFileDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		saveCharacterFileDialog.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		saveCharacterFileDialog.setFileFilter(characterFileFilter);
		saveCharacterFileDialog.setPreferredSize(new java.awt.Dimension(x, y));

		worldLoadProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane(Messages.getString("AuthoringTool.45"), Messages.getString("AuthoringTool.46")); //$NON-NLS-1$ //$NON-NLS-2$
		worldStoreProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane(Messages.getString("AuthoringTool.47"), Messages.getString("AuthoringTool.48")); //$NON-NLS-1$ //$NON-NLS-2$
		characterLoadProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane(Messages.getString("AuthoringTool.49"), Messages.getString("AuthoringTool.50")); //$NON-NLS-1$ //$NON-NLS-2$
		characterStoreProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane(Messages.getString("AuthoringTool.51"), Messages.getString("AuthoringTool.52")); //$NON-NLS-1$ //$NON-NLS-2$

		preferencesContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.PreferencesContentPane();
		preferencesContentPane.setAuthoringTool(this);
		captureContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane(this);
		renderContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.RenderContentPane(this);
		renderPanel = new javax.swing.JPanel();

		aboutContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.AboutContentPane();

		renderPanel.setLayout(new java.awt.BorderLayout());

		importFileChooser.setApproveButtonText(Messages.getString("AuthoringTool.53")); //$NON-NLS-1$
		importFileChooser.setDialogTitle(Messages.getString("AuthoringTool.54")); //$NON-NLS-1$
		importFileChooser.setDialogType(javax.swing.JFileChooser.OPEN_DIALOG);
		importFileChooser.setPreferredSize(new java.awt.Dimension(x, y));

		saveWorldFileChooser.setApproveButtonText(Messages.getString("AuthoringTool.55")); //$NON-NLS-1$
		saveWorldFileChooser.setDialogTitle(Messages.getString("AuthoringTool.56")); //$NON-NLS-1$
		saveWorldFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		saveWorldFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		saveWorldFileChooser.setFileFilter(worldFileFilter);
		saveWorldFileChooser.setPreferredSize(new java.awt.Dimension(x, y));

		addCharacterFileChooser.setApproveButtonText(Messages.getString("AuthoringTool.57")); //$NON-NLS-1$
		addCharacterFileChooser.setDialogTitle(Messages.getString("AuthoringTool.58")); //$NON-NLS-1$
		addCharacterFileChooser.setDialogType(javax.swing.JFileChooser.OPEN_DIALOG);
		addCharacterFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		addCharacterFileChooser.setFileFilter(characterFileFilter);
		addCharacterFileChooser.setPreferredSize(new java.awt.Dimension(x, y));


		worldsDirectoryChanged();
		importDirectoryChanged();
		charactersDirectoryChanged();

		worldInfoContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.WorldInfoContentPane();

		stdErrOutContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane(this);
		
		exportCodeForPrintingContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.ExportCodeForPrintingContentPane(this);
		
		saveForWebContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.SaveForWebContentPane(this);

		newVariableContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewVariableContentPane();
		
		startUpContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane(this);

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
				AuthoringTool.this.quit();
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
			java.text.DecimalFormat formater = new java.text.DecimalFormat("#0.00"); //$NON-NLS-1$
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				String fps = formater.format(AuthoringTool.this.scheduler.getSimulationFPS()) + Messages.getString("AuthoringTool.60"); //$NON-NLS-1$
				if (authoringToolConfig.getValue("rendering.showFPS").equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
					AuthoringTool.this.renderContentPane.setTitle(Messages.getString("AuthoringTool.63") + fps); //$NON-NLS-1$
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
					int interval = Integer.parseInt(authoringToolConfig.getValue("promptToSaveInterval")); //$NON-NLS-1$
					long intervalMillis = ((long) interval) * ((long) 60000);
					if (dt > intervalMillis) {
						//							edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( "You have not saved in more than" + interval + " minutes.\nIt is recommended that you save early and often to avoid losing work." );
						int result =
							edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(
								Messages.getString("AuthoringTool.65") + interval + Messages.getString("AuthoringTool.66"), //$NON-NLS-1$ //$NON-NLS-2$
								Messages.getString("AuthoringTool.67"), //$NON-NLS-1$
								javax.swing.JOptionPane.YES_NO_OPTION,
								javax.swing.JOptionPane.WARNING_MESSAGE,
								null,
								new Object[] { Messages.getString("AuthoringTool.68"), Messages.getString("AuthoringTool.69") }, //$NON-NLS-1$ //$NON-NLS-2$
								Messages.getString("AuthoringTool.70")); //$NON-NLS-1$
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

		// for animating ui changes
		rectangleAnimator = new edu.cmu.cs.stage3.alice.authoringtool.util.RectangleAnimator(this);

		watcherPanel.setMinimumSize(new java.awt.Dimension(0, 0));

		//tooltips
		javax.swing.ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		//sound format TEST
		//		System.out.println( AuthoringToolResources.formatTime( .123 ) );
		//		System.out.println( AuthoringToolResources.formatTime( 45.123 ) );
		//		System.out.println( AuthoringToolResources.formatTime( 54.0 ) );
		//		System.out.println( AuthoringToolResources.formatTime( 63.123 ) );
		//		System.out.println( AuthoringToolResources.formatTime( 927.123 ) );
		//		System.out.println( AuthoringToolResources.formatTime( 32729.123 ) );
	}

	private void importInit() {
		java.util.List importers = importing.getImporters();
		edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter imageFiles = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter(Messages.getString("AuthoringTool.71")); //$NON-NLS-1$
		extensionStringsToFileFilterMap.put(Messages.getString("AuthoringTool.72"), imageFiles); //$NON-NLS-1$
		edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter soundFiles = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter(Messages.getString("AuthoringTool.73")); //$NON-NLS-1$
		extensionStringsToFileFilterMap.put(Messages.getString("AuthoringTool.74"), soundFiles); //$NON-NLS-1$
		java.util.TreeSet extensions = new java.util.TreeSet();
		for (java.util.Iterator iter = importers.iterator(); iter.hasNext();) {
			edu.cmu.cs.stage3.alice.authoringtool.Importer importer = (edu.cmu.cs.stage3.alice.authoringtool.Importer) iter.next();
			java.util.Map map = importer.getExtensionMap();
			for (java.util.Iterator jter = map.keySet().iterator(); jter.hasNext();) {
				String extension = (String) jter.next();
				String description = extension + " (" + map.get(extension) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
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
			if (worldToLoad.exists()) {
				if (worldToLoad.canRead()) {
					int retVal = loadWorld(worldToLoad, false);
					if (retVal == Constants.SUCCEEDED) {
						return;
					}
				} else {
					AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.77") + worldToLoad, null, false); //$NON-NLS-1$
				}
			} else {
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.78") + worldToLoad, null, false); //$NON-NLS-1$
			}
		}

		// if that fails
		loadWorld(defaultWorld, false);
	}

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
				String[] renderers = authoringToolConfig.getValueList( "rendering.orderedRendererList" ); //$NON-NLS-1$
				rendererClass = Class.forName( renderers[ 0 ] );
			} catch( Throwable t ) {
				//todo: inform user of configuration problem?
				//pass
			}
			try {
				String s = authoringToolConfig.getValue( "rendering.forceSoftwareRendering" ); //$NON-NLS-1$
				if( s != null ) {
					isSoftwareEmulationForced = s.equals( "true" ); //$NON-NLS-1$
				}
			} catch( Throwable t ) {
				//todo: inform user of configuration problem?
				//pass
			}
			String commandLineOption = System.getProperty( "alice.forceSoftwareRendering" ); //$NON-NLS-1$
			if( commandLineOption != null && commandLineOption.equalsIgnoreCase( "true" ) ) { //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.84"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.85"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.86"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.87"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.88"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.89"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.90"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.91"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.92"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.93"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.94"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.95"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.96"), t); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.97"), t); //$NON-NLS-1$
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
			}
			resetClipboards();
			return Constants.SUCCEEDED;
		} else {
			return retVal;
		}
	}
	
	public void resetClipboards() {
		try {
			int numClipboards = Integer.parseInt( authoringToolConfig.getValue( "numberOfClipboards" ) ); //$NON-NLS-1$
			for( int i = 0; i < numClipboards; i++ ) {
				jAliceFrame.clipboardPanel.remove( 0 );
			}
			for( int i = 0; i < numClipboards; i++ ) {
				jAliceFrame.clipboardPanel.add( new edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard() );
			}
			jAliceFrame.clipboardPanel.revalidate();
			jAliceFrame.clipboardPanel.repaint();
		} catch( NumberFormatException e ) {
			AuthoringTool.showErrorDialog( Messages.getString("AuthoringTool.99") + authoringToolConfig.getValue( "numberOfClipboards" ), null ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	public int newWorld() {
		watcherPanel.clear();
		return showStartUpDialog( edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.TEMPLATE_TAB_ID );
	}
	public int openWorld() {
		watcherPanel.clear();
		return showStartUpDialog( edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.OPEN_TAB_ID );
	}
	public int openExampleWorld() {
		watcherPanel.clear();
		return showStartUpDialog( edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.EXAMPLE_TAB_ID );
	}
	public int openTutorialWorld() {	
		watcherPanel.clear();
		return showStartUpDialog(edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.TUTORIAL_TAB_ID);
	}

	public int saveWorld() {
		if ((currentWorldLocation != null) && shouldAllowOverwrite(currentWorldLocation) && currentWorldLocation.canWrite()) {
			try {
				return saveWorldToFile(currentWorldLocation, false);
			} catch (java.io.IOException e) {
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.101") + currentWorldLocation.getAbsolutePath(), e); //$NON-NLS-1$
				return Constants.FAILED;
			}
		} else {
			return saveWorldAs();
		}
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
			if (!file.getName().endsWith("." + WORLD_EXTENSION)) { //$NON-NLS-1$
				file = new java.io.File(file.getParent(), file.getName() + "." + WORLD_EXTENSION); //$NON-NLS-1$
			}
			try {
				return saveWorldToFile(file, false);
			} catch (java.io.IOException e) {
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.104") + file, e); //$NON-NLS-1$
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
			authoringToolConfig.setValue("mainWindowBounds", bounds.x + ", " + bounds.y + ", " + bounds.width + ", " + bounds.height); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.storeConfig();
			renderTargetFactory.release();
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.109"), t); //$NON-NLS-1$
		}
	}

	public int quit() {
		try {
			int retVal = leaveWorld(true);
			if (retVal == Constants.SUCCEEDED) {
				finalCleanUp();
				java.io.File aliceHasNotExitedFile = new java.io.File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt"); //$NON-NLS-1$
				aliceHasNotExitedFile.delete();
				java.io.BufferedInputStream bis = null;
				java.io.BufferedOutputStream bos = null;
				java.io.File aliceJAR = new java.io.File(JAlice.getAliceHomeDirectory().toString() + "\\lib\\aliceupdate.jar"); //$NON-NLS-1$
	            try {      
					bis = new java.io.BufferedInputStream( new java.io.FileInputStream( aliceJAR ) );
		            bos = new java.io.BufferedOutputStream( new java.io.FileOutputStream( new java.io.File(JAlice.getAliceHomeDirectory().toString() + "\\lib\\alice.jar") ) ); //$NON-NLS-1$
		            int i;
		            while ((i = bis.read()) != -1) {
		            	bos.write( i );
		            }      
				} catch (Exception e1){
					
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
				System.exit(0);
				return Constants.SUCCEEDED; // never reached
			} else if (retVal == Constants.CANCELED) {
				return Constants.CANCELED;
			} else if (retVal == Constants.FAILED) {
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("AuthoringTool.113")); //$NON-NLS-1$
				if (result == javax.swing.JOptionPane.YES_OPTION) {
					finalCleanUp();
					System.exit(1);
				}
			}
		} catch (Throwable t) {
			try {
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("AuthoringTool.114") ); // TODO: give information about the error //$NON-NLS-1$
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
				String question = Messages.getString("AuthoringTool.115"); //$NON-NLS-1$
				int n = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(question, Messages.getString("AuthoringTool.116"), javax.swing.JOptionPane.YES_NO_CANCEL_OPTION); //$NON-NLS-1$
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
				String question = Messages.getString("AuthoringTool.117"); //$NON-NLS-1$
				int n = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(question, Messages.getString("AuthoringTool.118"), javax.swing.JOptionPane.YES_NO_CANCEL_OPTION); //$NON-NLS-1$
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
			if (world != null) {
				world.release();
				fireWorldUnLoaded(world);
			}

			world = null;

			return Constants.SUCCEEDED;
		} catch (Exception e) {
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.119"), e); //$NON-NLS-1$
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
					if( name.endsWith( ".a2w" ) ) { //$NON-NLS-1$
						name = name.substring( 0, name.length()-4 );
					}
					java.io.File dstDir = new java.io.File( parentDir, Messages.getString("AuthoringTool.121") + name ); //$NON-NLS-1$

					StringBuffer sb = new StringBuffer();
					java.util.Calendar calendar = java.util.Calendar.getInstance();
					sb.append( name );
					sb.append( Messages.getString("AuthoringTool.122") ); //$NON-NLS-1$
					switch( calendar.get( java.util.Calendar.MONTH ) ) {
					case java.util.Calendar.JANUARY:
						sb.append( Messages.getString("AuthoringTool.123") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.FEBRUARY:
						sb.append( Messages.getString("AuthoringTool.124") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.MARCH:
						sb.append( Messages.getString("AuthoringTool.125") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.APRIL:
						sb.append( Messages.getString("AuthoringTool.126") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.MAY:
						sb.append( Messages.getString("AuthoringTool.127") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.JUNE:
						sb.append( Messages.getString("AuthoringTool.128") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.JULY:
						sb.append( Messages.getString("AuthoringTool.129") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.AUGUST:
						sb.append( Messages.getString("AuthoringTool.130") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.SEPTEMBER:
						sb.append( Messages.getString("AuthoringTool.131") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.OCTOBER:
						sb.append( Messages.getString("AuthoringTool.132") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.NOVEMBER:
						sb.append( Messages.getString("AuthoringTool.133") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.DECEMBER:
						sb.append( Messages.getString("AuthoringTool.134") ); //$NON-NLS-1$
						break;
					}
					sb.append( calendar.get( java.util.Calendar.DAY_OF_MONTH ) );
					sb.append( " " ); //$NON-NLS-1$
					sb.append( calendar.get( java.util.Calendar.YEAR ) );
					sb.append( Messages.getString("AuthoringTool.136") ); //$NON-NLS-1$
					sb.append( calendar.get( java.util.Calendar.HOUR ) );
					sb.append( Messages.getString("AuthoringTool.137") ); //$NON-NLS-1$
					sb.append( calendar.get( java.util.Calendar.MINUTE ) );
					sb.append( Messages.getString("AuthoringTool.138") ); //$NON-NLS-1$
					sb.append( calendar.get( java.util.Calendar.SECOND ) );
					switch( calendar.get( java.util.Calendar.AM_PM ) ) {
					case java.util.Calendar.AM:
						sb.append( Messages.getString("AuthoringTool.139") ); //$NON-NLS-1$
						break;
					case java.util.Calendar.PM:
						sb.append( Messages.getString("AuthoringTool.140") ); //$NON-NLS-1$
						break;
					}

					java.io.File dst = new java.io.File( dstDir, sb.toString() );
					//m_backupProgressObserver = new edu.cmu.cs.stage3.progress.ProgressObserver();
					if( edu.cmu.cs.stage3.io.FileUtilities.isFileCopySupported() ) {				//Aik Min - Make a version for MAC
						try {
							edu.cmu.cs.stage3.io.FileUtilities.copy( src, dst, true );
							java.io.File[] siblings = dstDir.listFiles( new java.io.FilenameFilter() {
								public boolean accept( java.io.File dir, String name ) {
									return name.endsWith( ".a2w" ); //$NON-NLS-1$
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
								return name.endsWith( ".a2w" ); //$NON-NLS-1$
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
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("AuthoringTool.143") + file.getAbsolutePath() + Messages.getString("AuthoringTool.144"), Messages.getString("AuthoringTool.145"), javax.swing.JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			countSomething("edu.cmu.cs.stage3.alice.authoringtool.saveCount"); //$NON-NLS-1$

			// world open time
			updateWorldOpenTime();

			// store the world
			java.util.Dictionary map = new java.util.Hashtable();
			if (authoringToolConfig.getValue("saveThumbnailWithWorld").equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
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
							edu.cmu.cs.stage3.image.ImageIO.store("png", baos, image); //$NON-NLS-1$
							map.put("thumbnail.png", baos.toByteArray()); //$NON-NLS-1$
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
								backupCount = Integer.parseInt( authoringToolConfig.getValue( "maximumWorldBackupCount" ) ); //$NON-NLS-1$
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
				} else {
					return Constants.FAILED;
				}
			} else {
				file.delete();
				return Constants.CANCELED;
			}
		} catch( Throwable t ) {
			AuthoringTool.showErrorDialog( Messages.getString("AuthoringTool.152") + file, t ); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.153"), t); //$NON-NLS-1$
			}
		}
	}

	public int saveWorldForWeb(java.io.File htmlFile, int width, int height, String authorName, String code) throws java.io.IOException {
		String baseName = htmlFile.getName();
		int dotIndex = htmlFile.getName().lastIndexOf("."); //$NON-NLS-1$
		if (dotIndex > 0) {
			baseName = htmlFile.getName().substring(0, dotIndex);
		}

		java.io.File worldFile = new java.io.File(htmlFile.getParentFile(), baseName + "." + WORLD_EXTENSION); //$NON-NLS-1$

		if (htmlFile.exists() && (!htmlFile.canWrite())) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("AuthoringTool.156") + htmlFile.getAbsolutePath() + Messages.getString("AuthoringTool.157"), Messages.getString("AuthoringTool.158"), javax.swing.JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return Constants.FAILED;
		}
		if (worldFile.exists() && (!worldFile.canWrite())) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("AuthoringTool.159") + worldFile.getAbsolutePath() + Messages.getString("AuthoringTool.160"), Messages.getString("AuthoringTool.161"), javax.swing.JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return Constants.FAILED;
		}
		if (authorName != null) {
			authorName = Messages.getString("AuthoringTool.162") + authorName + "</h2>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			authorName = " "; //$NON-NLS-1$
		}
		if (code == null) {
			code = " "; //$NON-NLS-1$
		}
		java.util.HashMap replacements = new java.util.HashMap();
		replacements.put("__worldname__", baseName); //$NON-NLS-1$
		replacements.put("__code__", code); //$NON-NLS-1$
		replacements.put("__authorname__", authorName); //$NON-NLS-1$
		replacements.put("__worldfile__", worldFile.getName()); //$NON-NLS-1$
		replacements.put("__width__", Integer.toString(width)); //$NON-NLS-1$
		replacements.put("__height__", Integer.toString(height)); //$NON-NLS-1$

		// write web page
		java.io.File templateFile = new java.io.File(JAlice.getAliceHomeDirectory(), "etc/appletTemplate.html"); //$NON-NLS-1$
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
		java.io.File appletSourceFile = new java.io.File(JAlice.getAliceHomeDirectory(), "etc/aliceapplet.jar"); //$NON-NLS-1$
		java.io.File appletDestinationFile = new java.io.File(htmlFile.getParentFile(), "aliceapplet.jar"); //$NON-NLS-1$
		AuthoringToolResources.copyFile(appletSourceFile, appletDestinationFile);

		return Constants.SUCCEEDED;
	}

	public int loadWorld(String filename, boolean askForSaveIfNecessary) {
		return loadWorld(new java.io.File(filename), askForSaveIfNecessary);
	}

	public int loadWorld(final java.io.File file, boolean askForSaveIfNecessary) {
		int result;
		if (file.isFile()) {
			result = loadWorld(new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file, askForSaveIfNecessary);
		} else if (file.isDirectory()) {
			result = loadWorld(new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file, askForSaveIfNecessary);
		} else {
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.175") + file, null); //$NON-NLS-1$
			result = Constants.FAILED;
		}
		return result;
	}

	public boolean isTemplateWorld(String filename) {
		return filename.startsWith(getTemplateWorldsDirectory().getAbsolutePath());
	}

	public int loadWorld(final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, Object path, boolean askForSaveIfNecessary) {
		if (askForSaveIfNecessary) {
			int retVal = askForSaveIfNecessary();
			if (retVal == Constants.CANCELED) {
				return Constants.CANCELED;
			} else if (retVal == Constants.FAILED) {
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("AuthoringTool.176")); //$NON-NLS-1$
				if (result != javax.swing.JOptionPane.YES_OPTION) {
					return Constants.CANCELED;
				}
			}
		}

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
				}
			} finally {
				loader.close();
			}
		} catch( Throwable t ) {
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.177") + path, t); //$NON-NLS-1$
		}
//
//		try {
//			loader.open(path);
//		} catch (java.io.IOException ioe) {
//			AuthoringTool.showErrorDialog("Unable to open world: " + path, ioe);
//		}
//
//		try {
//			tempWorld = (edu.cmu.cs.stage3.alice.core.World) edu.cmu.cs.stage3.alice.core.Element.load(loader, null, progressPane);
//		} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
//			edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = upre.getPropertyReferences();
//			System.err.println("Unable to load world: " + path + ".  Couldn't resolve the following references:");
//			for (int i = 0; i < propertyReferences.length; i++) {
//				System.err.println("\t" + propertyReferences[i]);
//			}
//			tempWorld = (edu.cmu.cs.stage3.alice.core.World) upre.getElement();
//		} catch (edu.cmu.cs.stage3.progress.ProgressCancelException pce) {
//			return Constants.CANCELED;
//		} catch (Throwable t) {
//			AuthoringTool.showErrorDialog("Unable to load world: " + path, t);
//		} finally {
//			try {
//				loader.close();
//			} catch (java.io.IOException ioe) {
//				AuthoringTool.showErrorDialog("Unable to close world: " + path, ioe);
//			}
//		}

		if (tempWorld != null) {
			final java.awt.Cursor prevCursor = getJAliceFrame().getCursor();
			getJAliceFrame().setCursor( java.awt.Cursor.getPredefinedCursor( java.awt.Cursor.WAIT_CURSOR )); // Aik Min
			try {
				leaveWorld(false);
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
	
				undoRedoStack.setUnmodified();
				fireWorldLoaded(world);
			} finally {
				javax.swing.SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						getJAliceFrame().setCursor( prevCursor );
					}
				} );
			}
			return Constants.SUCCEEDED;
		} else {
			return Constants.FAILED;
		}
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

		if (edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(add3DTextPanel, Messages.getString("AuthoringTool.178"), javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE) == javax.swing.JOptionPane.OK_OPTION) { //$NON-NLS-1$
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
			} else {
				return Constants.CANCELED;
			}
		} else {
			return Constants.CANCELED;
		}

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
	    //if(currentWorldLocation==null)
	    	if(saveWorldAs()!=Constants.SUCCEEDED)
	    		return Constants.CANCELED;
		String directory = currentWorldLocation.getParent();
    	directory = directory.replace('\\', '/');
    	java.io.File dir = new java.io.File(directory +"/frames"); //$NON-NLS-1$
    	dir.mkdir();
    	
    	soundStorage = new movieMaker.SoundStorage();
    	playWhileEncoding(directory);
		soundStorage = null;
    	if (authoringToolConfig.getValue("rendering.deleteFiles").equalsIgnoreCase("true") == true){ //$NON-NLS-1$ //$NON-NLS-2$
	    	captureContentPane.removeFiles(directory+"/frames/"); //$NON-NLS-1$
	    	dir.delete();
	    	if (dir.exists()){
    			showErrorDialog(Messages.getString("AuthoringTool.183"), //$NON-NLS-1$
    					Messages.getString("AuthoringTool.184") + //$NON-NLS-1$
    					Messages.getString("AuthoringTool.185") + dir + Messages.getString("AuthoringTool.186")); //$NON-NLS-1$ //$NON-NLS-2$
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
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.187") + file, null); //$NON-NLS-1$
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
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.188") + file, null); //$NON-NLS-1$
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
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.189") + CHARACTER_EXTENSION + ": " + pathname, e, false); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.191") + pathname, e); //$NON-NLS-1$
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
						javax.vecmath.Vector3d insertionPoint = boundingBox.getCenterOfBottomFace();
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
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.192"), null); //$NON-NLS-1$
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
			String typeString = Messages.getString("AuthoringTool.0"); //$NON-NLS-1$
			if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization) {
				typeString = Messages.getString("AuthoringTool.194"); //$NON-NLS-1$
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization) {
				typeString = Messages.getString("AuthoringTool.195"); //$NON-NLS-1$
			}
			edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization) element;

			edu.cmu.cs.stage3.alice.authoringtool.util.CollectionEditorPanel collectionEditorPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getCollectionEditorPanel();
			collectionEditorPanel.setCollection(visualization.getItemsCollection());
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(collectionEditorPanel, Messages.getString("AuthoringTool.196") + typeString, javax.swing.JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
		}
	}

	public void saveCharacter(edu.cmu.cs.stage3.alice.core.Element element) {
		String characterFilename = element.name.getStringValue() + "." + CHARACTER_EXTENSION; //$NON-NLS-1$
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
		characterStoreProgressPane.setElement( element );
		characterStoreProgressPane.setFile( file );
		characterStoreProgressPane.setFilnameToByteArrayMap( null );
		edu.cmu.cs.stage3.swing.DialogManager.showDialog( characterStoreProgressPane );
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
			if (importFileChooser.getFileFilter().getDescription().compareToIgnoreCase(Messages.getString("AuthoringTool.198")) == 0) { //$NON-NLS-1$
				importFileChooser.setAccessory(new edu.cmu.cs.stage3.alice.authoringtool.dialog.ImagePreview(importFileChooser));	//Aik Min - image preview for import texture map
				importFileChooser.setCurrentDirectory( new java.io.File(JAlice.getAliceHomeDirectory(), "textureMap") ); //$NON-NLS-1$
				java.io.File file = new java.io.File(JAlice.getAliceHomeDirectory(), "textureMap/GrassTexture.png") ; //$NON-NLS-1$
				if ( file.exists() ) {
					importFileChooser.setSelectedFile( file ); 
				}
			} else {
				importFileChooser.setAccessory(null);
				importFileChooser.setSelectedFile( new java.io.File("") );  //$NON-NLS-1$
				edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage() );
				importFileChooser.setCurrentDirectory( new java.io.File(authoringToolConfig.getValue("directories.worldsDirectory")) ); //$NON-NLS-1$
			}
			importFileChooser.rescanCurrentDirectory();
			int returnVal = edu.cmu.cs.stage3.swing.DialogManager.showDialog(importFileChooser, null);

			if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
				final java.io.File file = importFileChooser.getSelectedFile();
				if (file.getAbsolutePath().toLowerCase().endsWith(AuthoringTool.CHARACTER_EXTENSION)) { // special case Alice objects
					return loadAndAddCharacter(file);
				} else {
					path = file;
				}
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
				throw new IllegalArgumentException(Messages.getString("AuthoringTool.203")); //$NON-NLS-1$
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
						AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.204"), null, false); //$NON-NLS-1$
					}
				} catch (java.io.IOException e) {
					AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.205"), e); //$NON-NLS-1$
				}
				AuthoringTool.this.undoRedoStack.stopCompound();
			} else {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("AuthoringTool.206"), Messages.getString("AuthoringTool.207"), javax.swing.JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			return null;
		}

		return element;
	}

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
			boolean shouldAnimateCamera = (camera != null);
			edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model) transformable;

			java.util.HashMap opacityMap = new java.util.HashMap();
			java.util.Vector properties = new java.util.Vector();
			if (shouldAnimateCamera){
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
			if (shouldAnimateCamera){
				model.vehicle.set(vehicle);
				goodLook = camera.calculateGoodLookAt(model);
			}
			model.vehicle.set(null);
			undoRedoStack.setIsListening(tempListening);

			double distanceToBackOfObject = 0.0;
			boolean needToChangeFarClipping = false;
			if (shouldAnimateCamera){
				distanceToBackOfObject = AuthoringToolResources.distanceToBackAfterGetAGoodLookAt(model, camera);
				needToChangeFarClipping = distanceToBackOfObject > camera.farClippingPlaneDistance.doubleValue();
			}

			// getAGoodLook response
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation setupOpacity = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			setupOpacity.element.set(model);
			setupOpacity.propertyName.set("opacity"); //$NON-NLS-1$
			setupOpacity.value.set(new Double(0.0));
			setupOpacity.duration.set(new Double(0.0));
			setupOpacity.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation vehicleAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			vehicleAnimation.element.set(model);
			vehicleAnimation.propertyName.set("vehicle"); //$NON-NLS-1$
			vehicleAnimation.value.set(vehicle);
			vehicleAnimation.duration.set(new Double(0.0));
			vehicleAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation getAGoodLook = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			if (shouldAnimateCamera){
				getAGoodLook.subject.set(camera);
				getAGoodLook.pointOfView.set(goodLook);
			}
			
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation cameraGoBack = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			if (shouldAnimateCamera){
				cameraGoBack.subject.set(camera);
				cameraGoBack.pointOfView.set(camera.getLocalTransformation());
				cameraGoBack.duration.set(new Double(.5));
			}
			edu.cmu.cs.stage3.alice.core.response.Wait wait = new edu.cmu.cs.stage3.alice.core.response.Wait();
			wait.duration.set(new Double(.7));
			edu.cmu.cs.stage3.alice.core.response.Wait wait2 = new edu.cmu.cs.stage3.alice.core.response.Wait();
			wait2.duration.set(new Double(.2));
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation farClipping = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			if (shouldAnimateCamera){
				farClipping.element.set(camera);
				farClipping.propertyName.set("farClippingPlaneDistance"); //$NON-NLS-1$
				farClipping.value.set(new Double(distanceToBackOfObject));
			}
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation farClipping2 = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			if (shouldAnimateCamera){
				farClipping2.element.set(camera);
				farClipping2.propertyName.set("farClippingPlaneDistance"); //$NON-NLS-1$
				farClipping2.value.set(camera.farClippingPlaneDistance.get());
			}
			edu.cmu.cs.stage3.alice.core.response.DoTogether opacityDoTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			for (java.util.Iterator iter = opacityMap.keySet().iterator(); iter.hasNext();) {
				edu.cmu.cs.stage3.alice.core.Model m = (edu.cmu.cs.stage3.alice.core.Model) iter.next();
				Object opacity = opacityMap.get(m);
				edu.cmu.cs.stage3.alice.core.response.PropertyAnimation opacityAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
				opacityAnimation.element.set(m);
				opacityAnimation.propertyName.set("opacity"); //$NON-NLS-1$
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
			if (shouldAnimateCamera){
				response.componentResponses.add(cameraOpacityDoTogether);
				response.componentResponses.add(cameraGoBack);
				response.componentResponses.add(farClipping2);
			} else{
				response.componentResponses.add(opacityDoTogether);
			}
			// getAGoodLook undoResponse
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoVehicleAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			undoVehicleAnimation.element.set(model);
			undoVehicleAnimation.propertyName.set("vehicle"); //$NON-NLS-1$
			undoVehicleAnimation.value.set(null);
			undoVehicleAnimation.duration.set(new Double(0.0));
			undoVehicleAnimation.howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoGetAGoodLook = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			if (shouldAnimateCamera){	
				undoGetAGoodLook.subject.set(camera);
				undoGetAGoodLook.pointOfView.set(goodLook);
				undoGetAGoodLook.duration.set(new Double(.5));
			}
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoCameraGoBack = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			if (shouldAnimateCamera){	
				undoCameraGoBack.subject.set(camera);
				undoCameraGoBack.pointOfView.set(camera.getLocalTransformation());
			}
			edu.cmu.cs.stage3.alice.core.response.Wait undoWait = new edu.cmu.cs.stage3.alice.core.response.Wait();
			undoWait.duration.set(new Double(.9));
			edu.cmu.cs.stage3.alice.core.response.Wait undoWait2 = new edu.cmu.cs.stage3.alice.core.response.Wait();
			undoWait2.duration.set(new Double(.2));
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoFarClipping = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			if (shouldAnimateCamera){
				undoFarClipping.element.set(camera);
				undoFarClipping.propertyName.set("farClippingPlaneDistance"); //$NON-NLS-1$
				undoFarClipping.value.set(new Double(distanceToBackOfObject));
				undoFarClipping.duration.set(new Double(.2));
			}
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoFarClipping2 = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			if (shouldAnimateCamera){
				undoFarClipping2.element.set(camera);
				undoFarClipping2.propertyName.set("farClippingPlaneDistance"); //$NON-NLS-1$
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
				opacityAnimation.propertyName.set("opacity"); //$NON-NLS-1$
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
			if (shouldAnimateCamera){
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
	
	private void displayDurations(edu.cmu.cs.stage3.alice.core.Response r){
		if (r instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse){
			edu.cmu.cs.stage3.alice.core.response.CompositeResponse c = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse)r;
			System.out.println("COMPOSITE("); //$NON-NLS-1$
			for (int i=0; i<c.componentResponses.size(); i++){
				displayDurations((edu.cmu.cs.stage3.alice.core.Response)c.componentResponses.get(i));
			}
			System.out.println(")"); //$NON-NLS-1$
		}
		else{
			System.out.println(r.duration.doubleValue());
		}
	}

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
						AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.219"), e); //$NON-NLS-1$
					}
					int result =
						edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(
							Messages.getString("AuthoringTool.220"), //$NON-NLS-1$
							Messages.getString("AuthoringTool.221"), //$NON-NLS-1$
							javax.swing.JOptionPane.YES_NO_OPTION,
							javax.swing.JOptionPane.INFORMATION_MESSAGE);
					if (result == javax.swing.JOptionPane.YES_OPTION) {
						edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = { camera.farClippingPlaneDistance };

						edu.cmu.cs.stage3.alice.core.response.PropertyAnimation farClippingAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
						farClippingAnimation.element.set(camera);
						farClippingAnimation.propertyName.set("farClippingPlaneDistance"); //$NON-NLS-1$
						farClippingAnimation.value.set(new Double(distanceToBackOfObject));

						edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
						undoResponse.element.set(camera);
						undoResponse.propertyName.set("farClippingPlaneDistance"); //$NON-NLS-1$
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
			String tabObjectsString = ""; //$NON-NLS-1$
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] instanceof edu.cmu.cs.stage3.alice.core.Element) { //TODO: handle non Elements?
					tabObjectsString += ((edu.cmu.cs.stage3.alice.core.Element) objects[i]).getKey() + ":"; //$NON-NLS-1$
				}
			}
			world.data.put("edu.cmu.cs.stage3.alice.authoringtool.tabObjects", tabObjectsString); //$NON-NLS-1$
		}
	}

	public void loadTabs() {
		if (authoringToolConfig.getValue("loadSavedTabs").equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
			if (world != null) {
				String tabObjectsString = (String) world.data.get("edu.cmu.cs.stage3.alice.authoringtool.tabObjects"); //$NON-NLS-1$
				if (tabObjectsString != null) {
					java.util.StringTokenizer st = new java.util.StringTokenizer(tabObjectsString, ":"); //$NON-NLS-1$
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
		java.io.File dir = new java.io.File(authoringToolConfig.getValue("screenCapture.directory")); //$NON-NLS-1$

		int numDigits = Integer.parseInt(authoringToolConfig.getValue("screenCapture.numDigits")); //$NON-NLS-1$
		StringBuffer pattern = new StringBuffer(authoringToolConfig.getValue("screenCapture.baseName")); //$NON-NLS-1$
		String codec = authoringToolConfig.getValue("screenCapture.codec"); //$NON-NLS-1$
		pattern.append("#"); //$NON-NLS-1$
		for (int i = 0; i < numDigits; i++) {
			pattern.append("0"); //$NON-NLS-1$
		}
		pattern.append("."); //$NON-NLS-1$
		pattern.append(codec);
		captureFormatter.applyPattern(pattern.toString());

		int i = 0;
		java.io.File file = new java.io.File(dir, captureFormatter.format(i));
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
			Object[] options = { Messages.getString("AuthoringTool.238"), Messages.getString("AuthoringTool.239") }; //$NON-NLS-1$ //$NON-NLS-2$
			int dialogVal =
				edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(
					Messages.getString("AuthoringTool.240"), //$NON-NLS-1$
					Messages.getString("AuthoringTool.241"), //$NON-NLS-1$
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
						java.io.File testCaptureFile = new java.io.File(selectedFile, "test.jpg"); //$NON-NLS-1$
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
							edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("AuthoringTool.243")); //$NON-NLS-1$
						} else {
							done = true;
							dir = selectedFile;
						}
					} else {
						edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
							Messages.getString("AuthoringTool.244") + Messages.getString("AuthoringTool.245"), //$NON-NLS-1$ //$NON-NLS-2$
							Messages.getString("AuthoringTool.246"), //$NON-NLS-1$
							javax.swing.JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
			} else {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("AuthoringTool.247") + Messages.getString("AuthoringTool.248"), //$NON-NLS-1$ //$NON-NLS-2$
					Messages.getString("AuthoringTool.249"), //$NON-NLS-1$
					javax.swing.JOptionPane.WARNING_MESSAGE);
				return;
			}
			file = new java.io.File(dir, captureFormatter.format(i));
		}
		while (file.exists()) {
			i++;
			file = new java.io.File(dir, captureFormatter.format(i));
		}
		try {
			file.createNewFile();
			java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
			java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(fos);
			java.io.DataOutputStream dos = new java.io.DataOutputStream(bos);
			edu.cmu.cs.stage3.image.ImageIO.store(codec, dos, image);
			dos.flush();
			fos.close();

			if (authoringToolConfig.getValue("screenCapture.informUser").equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
				java.awt.image.BufferedImage scaledImage = edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.getImageScaledToLongestDimension(image, 128);
				edu.cmu.cs.stage3.alice.authoringtool.dialog.CapturedImageContentPane capturedImageContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.CapturedImageContentPane();
				capturedImageContentPane.setStoreLocation(file.getCanonicalPath());
				capturedImageContentPane.setImage(scaledImage);
				edu.cmu.cs.stage3.swing.DialogManager.showDialog(capturedImageContentPane);
			}
			//} catch( InterruptedException ie ) {
			//} catch( java.io.IOException ioe ) {
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.252"), t); //$NON-NLS-1$
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
		setImportFileFilter("Image Files"); //$NON-NLS-1$
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
		} else {
			return null;
		}
	}

	public void showAbout() {
		edu.cmu.cs.stage3.swing.DialogManager.showDialog(aboutContentPane);
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
					showErrorDialog( Messages.getString("AuthoringTool.254"), t ); //$NON-NLS-1$
				}
			}
		} );
	}

	public void showSimulationExceptionDialog( edu.cmu.cs.stage3.alice.core.SimulationException simulationException ) {
		edu.cmu.cs.stage3.alice.authoringtool.dialog.SimulationExceptionPanel simulationExceptionPanel = new edu.cmu.cs.stage3.alice.authoringtool.dialog.SimulationExceptionPanel(this);
		simulationExceptionPanel.setSimulationException(simulationException);
		simulationExceptionPanel.setErrorHighlightingEnabled(true);
		edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(simulationExceptionPanel, Messages.getString("AuthoringTool.255"), javax.swing.JOptionPane.ERROR_MESSAGE, AuthoringToolResources.getAliceSystemIcon() ); //$NON-NLS-1$
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

	public void showStdErrOutDialog() {
		stdErrOutContentPane.setMode(edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane.HISTORY_MODE);
		stdErrOutContentPane.showStdErrOutDialog();
	}

	public void updateWorldOpenTime() {
		if (world != null) {
			String worldOpenTimeString = (String) world.data.get("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime"); //$NON-NLS-1$
			long worldOpenTime = 0;
			if (worldOpenTimeString != null) {
				worldOpenTime = Long.parseLong(worldOpenTimeString);
			}
			worldOpenTime += System.currentTimeMillis() - worldLoadedTime;
			worldLoadedTime = System.currentTimeMillis();
			world.data.put("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime", Long.toString(worldOpenTime)); //$NON-NLS-1$
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
			edu.cmu.cs.stage3.progress.ProgressPane progressPane = new edu.cmu.cs.stage3.progress.ProgressPane( Messages.getString("AuthoringTool.258"), Messages.getString("AuthoringTool.259") ) { //$NON-NLS-1$ //$NON-NLS-2$
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
						AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.260") + fileToExportTo, t); //$NON-NLS-1$
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
		String path = ""; //$NON-NLS-1$
		if (currentWorldLocation != null) {
			try {
				path = currentWorldLocation.getCanonicalPath();
			} catch (java.io.IOException e) {
				path = currentWorldLocation.getAbsolutePath();
			}
		}

		String modifiedStatus = ""; //$NON-NLS-1$
		if (worldHasBeenModified) {
			modifiedStatus = Messages.getString("AuthoringTool.263"); //$NON-NLS-1$
		}

		jAliceFrame.setTitle("Alice (" + JAlice.getVersion() + ") " + path + modifiedStatus); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public java.awt.Component getEditorForElement(edu.cmu.cs.stage3.alice.core.Element elementToEdit) {
		int index = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementToEdit);
		if (index > -1) {
			return jAliceFrame.tabbedEditorComponent.getEditorAt(index).getJComponent();
		} else {
			Class editorClass = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(elementToEdit.getClass());
			Editor editor = editorManager.getEditorInstance(editorClass);
			edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.editObject(editor, elementToEdit);
			return editor.getJComponent();
		}
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
	protected java.io.File tutorialDirectory = new java.io.File(JAlice.getAliceHomeDirectory(), "tutorial").getAbsoluteFile(); //$NON-NLS-1$
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

		componentMap.put("fileMenu", jAliceFrame.fileMenu); //$NON-NLS-1$
		componentMap.put("editMenu", jAliceFrame.editMenu); //$NON-NLS-1$
		componentMap.put("toolsMenu", jAliceFrame.toolsMenu); //$NON-NLS-1$
		componentMap.put("helpMenu", jAliceFrame.helpMenu); //$NON-NLS-1$
		componentMap.put("playButton", jAliceFrame.playButton); //$NON-NLS-1$
		componentMap.put("addObjectButton", jAliceFrame.addObjectButton); //$NON-NLS-1$
		componentMap.put("undoButton", jAliceFrame.undoButton); //$NON-NLS-1$
		componentMap.put("redoButton", jAliceFrame.redoButton); //$NON-NLS-1$
		componentMap.put("trashComponent", jAliceFrame.trashComponent); //$NON-NLS-1$
		componentMap.put("clipboardPanel", jAliceFrame.clipboardPanel); //$NON-NLS-1$
		componentMap.put("objectTree", jAliceFrame.worldTreeComponent); //$NON-NLS-1$
		componentMap.put("sceneEditor", jAliceFrame.sceneEditor); //$NON-NLS-1$
		componentMap.put("details", jAliceFrame.dragFromComponent); //$NON-NLS-1$
		componentMap.put("behaviors", jAliceFrame.behaviorGroupsEditor); //$NON-NLS-1$
		componentMap.put("editors", jAliceFrame.tabbedEditorComponent); //$NON-NLS-1$

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

		tutorialOne = new java.io.File(JAlice.getAliceHomeDirectory(), "tutorial" + java.io.File.separator + "Tutorial1.stl"); //$NON-NLS-1$ //$NON-NLS-2$
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
		authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "true"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void restoreLayout() {
		jAliceFrame.setResizable(true);
		jAliceFrame.leftRightSplitPane.setDividerLocation(oldLeftRightSplitPaneLocation);
		jAliceFrame.worldTreeDragFromSplitPane.setDividerLocation(oldWorldTreeDragFromSplitPaneLocation);
		jAliceFrame.editorBehaviorSplitPane.setDividerLocation(oldEditorBehaviorSplitPaneLocation);
		jAliceFrame.smallSceneBehaviorSplitPane.setDividerLocation(oldSmallSceneBehaviorSplitPaneLocation);
		jAliceFrame.doLayout();
		if (oldShouldConstrain) {
			authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		renderContentPane.saveRenderBounds(oldRenderBounds);
		jAliceFrame.setSize(oldDimension);
		jAliceFrame.setLocation(oldPosition);

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
				Messages.getString("AuthoringTool.290") + Messages.getString("AuthoringTool.291"), //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("AuthoringTool.292"), //$NON-NLS-1$
				javax.swing.JOptionPane.WARNING_MESSAGE,
				null);
			shouldModifyStuff = false;
		}
		oldLeftRightSplitPaneLocation = jAliceFrame.leftRightSplitPane.getDividerLocation();
		oldWorldTreeDragFromSplitPaneLocation = jAliceFrame.worldTreeDragFromSplitPane.getDividerLocation();
		oldEditorBehaviorSplitPaneLocation = jAliceFrame.editorBehaviorSplitPane.getDividerLocation();
		oldSmallSceneBehaviorSplitPaneLocation = jAliceFrame.smallSceneBehaviorSplitPane.getDividerLocation();
		oldRenderWindowSize = renderContentPane.getSize();
		oldRenderWindowPosition = renderContentPane.getLocation();

		oldShouldConstrain = authoringToolConfig.getValue("rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase("true"); //$NON-NLS-1$ //$NON-NLS-2$
		authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "false"); //$NON-NLS-1$ //$NON-NLS-2$

		oldRenderBounds = renderContentPane.getRenderBounds();
		renderContentPane.saveRenderBounds(new java.awt.Rectangle(191, 199, 400, 300));

		if ((newHeight != oldDimension.height || newWidth != oldDimension.width) && shouldModifyStuff) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
				Messages.getString("AuthoringTool.297") + Messages.getString("AuthoringTool.298"), //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("AuthoringTool.299"), //$NON-NLS-1$
				javax.swing.JOptionPane.WARNING_MESSAGE,
				null);

		}
		if (shouldModifyStuff) {
			jAliceFrame.leftRightSplitPane.setDividerLocation(250);
			jAliceFrame.worldTreeDragFromSplitPane.setDividerLocation(237);
			jAliceFrame.editorBehaviorSplitPane.setDividerLocation(204);
			jAliceFrame.smallSceneBehaviorSplitPane.setDividerLocation(224);
			try{
				Thread.sleep(100);
			}catch (Exception e){}
			jAliceFrame.setSize(newWidth, newHeight);
			jAliceFrame.setLocation(newPosition);	
		}

		//java.awt.Dimension targetDimension = new java.awt.Dimension(newWidth, newHeight);
		jAliceFrame.doLayout();
		jAliceFrame.setResizable(false);
	}

	public void hideStencils() {
		restoreLayout();
		stencilManager.showStencils(false);
		authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		jAliceFrame.removeKeyListener(stencilManager);
		jAliceFrame.requestFocus();
	}

	public void setGlassPane(java.awt.Component c) {
		jAliceFrame.setGlassPane(c);
	}

	public void setVisible(boolean visible) {
		if (visible) {
			setLayout();
			authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			jAliceFrame.removeKeyListener(stencilManager);
			jAliceFrame.addKeyListener(stencilManager);
			jAliceFrame.requestFocus();
		} else {
			restoreLayout();
			authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			jAliceFrame.removeKeyListener(stencilManager);
			jAliceFrame.requestFocus();
		}
	}

	//Caitlin's code

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
			} else if ((c instanceof javax.swing.JLabel) && Messages.getString("AuthoringTool.306").equals(((javax.swing.JLabel) c).getText())) { //$NON-NLS-1$
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
			key = "objectTree"; //$NON-NLS-1$
			if (bounds.contains(localPoint)) {
				String elementKey = ((edu.cmu.cs.stage3.alice.core.Element) treePath.getLastPathComponent()).getKey(world);
				key += "<" + elementKey + ">"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard) {
			key = "clipboard"; //$NON-NLS-1$
			java.awt.Component[] components = c.getParent().getComponents();
			int index = -1;
			for (int i = 0; i < components.length; i++) {
				if (components[i] == c) {
					index = i;
				}
			}
			key += "<" + Integer.toString(index) + ">"; //$NON-NLS-1$ //$NON-NLS-2$

		} else if (jAliceFrame.sceneEditor.isAncestorOf(c) || (c == jAliceFrame.sceneEditor)) {
			if (jAliceFrame.sceneEditor.getGuiMode() == edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.LARGE_MODE) {
				key = "sceneEditor<large>"; //$NON-NLS-1$
			} else {
				key = "sceneEditor<small>"; //$NON-NLS-1$
			}
			if (jAliceFrame.sceneEditor.getViewMode() == edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.QUAD_VIEW_MODE) {
				key += ":quadView"; //$NON-NLS-1$
			} else {
				key += ":singleView"; //$NON-NLS-1$
			}

			if (jAliceFrame.sceneEditor.getGalleryViewer().isAncestorOf(c) || (c == jAliceFrame.sceneEditor.getGalleryViewer())) {
				key += ":galleryViewer<" + jAliceFrame.sceneEditor.getGalleryViewer().getDirectory() + ">"; //$NON-NLS-1$ //$NON-NLS-2$

				if (c instanceof javax.swing.JButton) {
					key += ":button<" + ((javax.swing.JButton) c).getText() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
				} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) {
					key += ":galleryObject<" + ((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) c).getUniqueIdentifier() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
				} else if (c != jAliceFrame.sceneEditor.getGalleryViewer()) {
					key = null;
				}
			} else {
				String id = jAliceFrame.sceneEditor.getIdForComponent(c);
				//				System.out.println( c + " --> " + id );
				if (id != null) {
					key += ":" + id; //$NON-NLS-1$
				} else if (c != jAliceFrame.sceneEditor) {
					key = null;
				}
			}
		} else if (jAliceFrame.dragFromComponent.isAncestorOf(c) || (c == jAliceFrame.dragFromComponent)) {
			if (jAliceFrame.dragFromComponent.getElement() == null) {
				key = "details"; //$NON-NLS-1$
			} else {
				key = "details<" + jAliceFrame.dragFromComponent.getElement().getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
				if (c instanceof javax.swing.JTabbedPane) {
					int whichTab = ((javax.swing.JTabbedPane) c).getUI().tabForCoordinate((javax.swing.JTabbedPane) c, localPoint.x, localPoint.y);
					if (whichTab > -1) {
						key += ":tab<" + Integer.toString(whichTab) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
					}
				} else if (c != jAliceFrame.dragFromComponent) {
					key += ":" + jAliceFrame.dragFromComponent.getKeyForComponent(c); //$NON-NLS-1$
				}
			}
		} else if (jAliceFrame.behaviorGroupsEditor.isAncestorOf(c)) {
			key = "behaviors"; //$NON-NLS-1$
			if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
				try {
					java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) c).getTransferable();
					if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
						edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
						key += ":elementTile<" + e.getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						key = null;
					}
				} catch (Exception e) {
					AuthoringTool.showErrorDialog("Error while examining DnDGroupingPanel.", e); //$NON-NLS-1$
					key = null;
				}
			} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
				edu.cmu.cs.stage3.alice.core.Property property = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) c).getProperty();
				edu.cmu.cs.stage3.alice.core.Element element = property.getOwner();
				key += ":elementTile<" + element.getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
				key += ":property<" + property.getName() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
				//TODO: handle user-defined parameters
			} else if ((c instanceof javax.swing.JLabel) && ((javax.swing.JLabel) c).getText().equals("more...")) { //$NON-NLS-1$
				if (c.getParent().getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
					edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController vc = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) c.getParent().getParent().getParent();
					edu.cmu.cs.stage3.alice.core.Element element = vc.getElement();
					key += ":elementTile<" + element.getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
					key += ":more"; //$NON-NLS-1$
				} else {
					key = null;
				}
			} else if (c instanceof javax.swing.JButton) {
				if (((javax.swing.JButton) c).getText().equals("create new event")) { // HACK //$NON-NLS-1$
					key += ":createNewEventButton"; //$NON-NLS-1$
				} else {
					key = null;
				}
			}
		} else if (jAliceFrame.tabbedEditorComponent.isAncestorOf(c)) {
			key = "editors"; //$NON-NLS-1$
			if (c instanceof javax.swing.JTabbedPane) {
				int whichTab = ((javax.swing.JTabbedPane) c).getUI().tabForCoordinate((javax.swing.JTabbedPane) c, localPoint.x, localPoint.y);
				if (whichTab > -1) {
					Object o = jAliceFrame.tabbedEditorComponent.getObjectBeingEditedAt(whichTab);
					if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
						key += ":element<" + ((edu.cmu.cs.stage3.alice.core.Element) o).getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						key = null;
					}
				}
			} else {
				Object o = jAliceFrame.tabbedEditorComponent.getObjectBeingEdited();
				if (o instanceof edu.cmu.cs.stage3.alice.core.Element) {
					key += ":element<" + ((edu.cmu.cs.stage3.alice.core.Element) o).getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
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
						key += ":button<" + ((javax.swing.JButton) c).getText() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
					} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
						try {
							java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) c).getTransferable();
							if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
								edu.cmu.cs.stage3.alice.core.Element e =
									(edu.cmu.cs.stage3.alice.core.Element) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
								key += ":elementTile<" + e.getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
							} else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
								edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep =
									(edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) transferable.getTransferData(
										edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
								key += ":elementPrototypeTile<" + ep.getElementClass().getName() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								key = null;
							}
							//TODO: handle other DnDGroupingPanels
						} catch (Exception e) {
							AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.1"), e); //$NON-NLS-1$
							key = null;
						}
					} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
						edu.cmu.cs.stage3.alice.core.Property property = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) c).getProperty();
						edu.cmu.cs.stage3.alice.core.Element element = property.getOwner();
						key += ":elementTile<" + element.getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
						key += ":property<" + property.getName() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
						//TODO: handle user-defined parameters
					} else if ((c instanceof javax.swing.JLabel) && ((javax.swing.JLabel) c).getText().equals("more...")) { //$NON-NLS-1$
						if (c.getParent().getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
							edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController vc =
								(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) c.getParent().getParent().getParent();
							edu.cmu.cs.stage3.alice.core.Element element = vc.getElement();
							key += ":elementTile<" + element.getKey(world) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
							key += ":more"; //$NON-NLS-1$
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
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":", false); //$NON-NLS-1$
		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) { //$NON-NLS-1$
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
			} else if (prefix.equals("clipboard")) { //$NON-NLS-1$
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
			} else if (prefix.equals("sceneEditor")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView, assume we're in the right mode
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) { //$NON-NLS-1$
							jAliceFrame.sceneEditor.getGalleryViewer().setDirectory(spec);
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("galleryObject")) { //$NON-NLS-1$
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
			} else if (prefix.equals("details")) { //$NON-NLS-1$
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
						if (st.hasMoreTokens()) {
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("viewController")) { //$NON-NLS-1$
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
			} else if (prefix.equals("behaviors")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewEventButton")) { //$NON-NLS-1$
						java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new event"); //$NON-NLS-1$
						if (c != null) {
							image = getComponentImage(c);
						}
					} else if (prefix.equals("elementTile") && (spec != null)) { //$NON-NLS-1$
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("more")) { //$NON-NLS-1$
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
			} else if (prefix.equals("editors")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) { //$NON-NLS-1$
						edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)) {
								//										Editor editor = jAliceFrame.tabbedEditorComponent.getEditorAt( jAliceFrame.tabbedEditorComponent.getIndexOfObject( elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findButton(container, spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("elementTile") && (spec != null)) { //$NON-NLS-1$
									edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources.getPrefix(token);
											spec = AuthoringToolResources.getSpecifier(token);
											if (prefix.equals("property") && (spec != null)) { //$NON-NLS-1$
												java.awt.Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
												if (c != null) {
													image = getComponentImage(c);
												}
											} else if (prefix.equals("more")) { //$NON-NLS-1$
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
								} else if (prefix.equals("elementPrototypeTile") && (spec != null)) { //$NON-NLS-1$
									try {
										Class elementClass = Class.forName(spec);
										if (elementClass != null) {
											java.awt.Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
											if (c != null) {
												image = getComponentImage(c);
											}
										}
									} catch (Exception e) {
										AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.386") + spec, e); //$NON-NLS-1$
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
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":", false); //$NON-NLS-1$
		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) { //$NON-NLS-1$
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
			} else if (prefix.equals("clipboard")) { //$NON-NLS-1$
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

			} else if (prefix.equals("sceneEditor")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView, assume we're in the right mode
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) { //$NON-NLS-1$
							jAliceFrame.sceneEditor.getGalleryViewer().setDirectory(spec);
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
									}
								} else if (prefix.equals("galleryObject")) { //$NON-NLS-1$
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
			} else if (prefix.equals("details")) { //$NON-NLS-1$
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
						if (st.hasMoreTokens()) {
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("tab")) { //$NON-NLS-1$
								int tabIndex = Integer.parseInt(spec);
								r = jAliceFrame.dragFromComponent.tabbedPane.getUI().getTabBounds(jAliceFrame.dragFromComponent.tabbedPane, tabIndex);
								r = javax.swing.SwingUtilities.convertRectangle(jAliceFrame.dragFromComponent.tabbedPane, r, jAliceFrame.getGlassPane());
								return r;
							} else if (prefix.equals("viewController")) { //$NON-NLS-1$
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
			} else if (prefix.equals("behaviors")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewEventButton")) { //$NON-NLS-1$
						java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new event"); //$NON-NLS-1$
						if (c != null) {
							r = c.getBounds();
							r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
						}
					} else if (prefix.equals("elementTile") && (spec != null)) { //$NON-NLS-1$
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
									}
								} else if (prefix.equals("more")) { //$NON-NLS-1$
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
			} else if (prefix.equals("editors")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) { //$NON-NLS-1$
						edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)) {
								//								Editor editor = jAliceFrame.tabbedEditorComponent.getEditorAt( jAliceFrame.tabbedEditorComponent.getIndexOfObject( elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findButton(container, spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
									}
								} else if (prefix.equals("elementTile") && (spec != null)) { //$NON-NLS-1$
									edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources.getPrefix(token);
											spec = AuthoringToolResources.getSpecifier(token);
											if (prefix.equals("property") && (spec != null)) { //$NON-NLS-1$
												java.awt.Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
												if (c != null) {
													r = c.getBounds();
													r = javax.swing.SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
												}
											} else if (prefix.equals("more")) { //$NON-NLS-1$
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
								} else if (prefix.equals("elementPrototypeTile") && (spec != null)) { //$NON-NLS-1$
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
										AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.410") + spec, e); //$NON-NLS-1$
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
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":", false); //$NON-NLS-1$

		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) { //$NON-NLS-1$
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						javax.swing.JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel worldTreeModel = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel) tree.getModel();
						java.awt.Rectangle r = tree.getPathBounds(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
						if ((r != null) && tree.getVisibleRect().contains(r)) {
							return true;
						} else {
							return false;
						}
					}
				}
			} else if (prefix.equals("clipboard")) { //$NON-NLS-1$
				if (spec != null) {
					try {
						int index = Integer.parseInt(spec);
						if (index > -1) {
							java.awt.Component c = jAliceFrame.clipboardPanel.getComponent(index);
							if (c != null && isComponentVisible((javax.swing.JComponent) c)) {
								return true;
							} else {
								return false;
							}
						} else {
							return false;
						}
					} catch (Exception e) {
						return false;
					}
				} else {
					return false;
				}

			} else if (prefix.equals("sceneEditor")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) { //$NON-NLS-1$
							if (jAliceFrame.sceneEditor.getGalleryViewer().getDirectory().equals(spec)) {
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources.getPrefix(token);
									spec = AuthoringToolResources.getSpecifier(token);
									if (prefix.equals("button")) { //$NON-NLS-1$
										java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
										if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
											return true;
										} else {
											return false;
										}
									} else if (prefix.equals("galleryObject")) { //$NON-NLS-1$
										java.awt.Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
										if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
											return true;
										} else {
											return false;
										}
									}
								} else {
									if (jAliceFrame.sceneEditor.getGalleryViewer().isShowing()) {
										return true;
									} else {
										return false;
									}
								}
							} else {
								return false;
							}
						} else {
							java.awt.Component c = jAliceFrame.sceneEditor.getComponentForId(token);
							if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
								return true;
							} else {
								return false;
							}
						}
					}
				}
			} else if (prefix.equals("details")) { //$NON-NLS-1$
				if (spec == null) {
					spec = ""; //$NON-NLS-1$
				}
				edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
				if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("tab")) { //$NON-NLS-1$
							return true;
						} else {
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
								} else {
									return false;
								}
							} else {
								return false;
							}
						}
					} else {
						return false;
					}
				} else {
					//TODO is this safe?
					return false;
				}
			} else if (prefix.equals("behaviors")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewBehaviorButton")) { //$NON-NLS-1$
						java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new behavior"); //$NON-NLS-1$
						if ((c != null) && c.isShowing()) {
							return true;
						} else {
							return false;
						}
					} else if (prefix.equals("elementTile") && (spec != null)) { //$NON-NLS-1$
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
									if ((c != null) && c.isShowing()) {
										java.awt.Rectangle bounds = c.getBounds();
										bounds = javax.swing.SwingUtilities.convertRectangle(c.getParent(), bounds, jAliceFrame.behaviorGroupsEditor.getScrollPane());
										if (jAliceFrame.behaviorGroupsEditor.getScrollPaneVisibleRect().contains(bounds)) {
											return true;
										} else {
											return false;
										}
										//										return true;
										//									} else {
										//										return false;
									} else {
										return false;
									}
								} else if (prefix.equals("more")) { //$NON-NLS-1$
									java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
										if ((moreTile != null) && moreTile.isShowing()) {
											java.awt.Rectangle bounds = moreTile.getBounds();
											javax.swing.SwingUtilities.convertRectangle(moreTile.getParent(), bounds, jAliceFrame.behaviorGroupsEditor.getScrollPane());
											if (jAliceFrame.behaviorGroupsEditor.getScrollPaneVisibleRect().contains(bounds)) {
												return true;
											} else {
												return false;
											}
											//											return true;
											//										} else {
											//											return false;
										} else {
											return false;
										}
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
									} else {
										return false;
									}
									//									return true;
								} else {
									return false;
								}
							}
						}
					}
				}
			} else if (prefix.equals("editors")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) { //$NON-NLS-1$
						edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)) {
								//								Editor editor = jAliceFrame.tabbedEditorComponent.getEditorAt( jAliceFrame.tabbedEditorComponent.getIndexOfObject( elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findButton(container, spec);
									if ((c != null) && c.isShowing()) {
										return true;
									} else {
										return false;
									}
								} else if (prefix.equals("elementTile") && (spec != null)) { //$NON-NLS-1$
									edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources.getPrefix(token);
											spec = AuthoringToolResources.getSpecifier(token);
											if (prefix.equals("property") && (spec != null)) { //$NON-NLS-1$
												java.awt.Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
												if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
													return true;
												} else {
													return false;
												}
											} else if (prefix.equals("more")) { //$NON-NLS-1$
												java.awt.Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
												if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
													java.awt.Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel).getMoreTile();
													if ((moreTile != null) && moreTile.isShowing()) {
														return true;
													} else {
														return false;
													}
												}
											}
										} else {
											java.awt.Component c = AuthoringToolResources.findElementDnDPanel(container, element);
											if ((c != null) && isComponentVisible((javax.swing.JComponent) c)) {
												return true;
											} else {
												return false;
											}
										}
									}
								} else if (prefix.equals("elementPrototypeTile") && (spec != null)) { //$NON-NLS-1$
									try {
										Class elementClass = Class.forName(spec);
										if (elementClass != null) {
											java.awt.Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
											if ((c != null) && c.isShowing()) {
												return true;
											} else {
												return false;
											}
										}
									} catch (Exception e) {
										AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.434") + spec, e); //$NON-NLS-1$
									}
								}
							} else {
								return false;
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
							if ((tabIndex >= 0) && (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
								return true;
							} else {
								return false;
							}
						}
					}
				}
			} else if (componentMap.containsKey(prefix)) {
				java.awt.Component c = (java.awt.Component) componentMap.get(prefix);
				if ((c != null) && c.isShowing()) {
					return true;
				} else {
					return false;
				}
			}
		}

		return true;
	}

	public void makeIDVisible(String id) throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":", false); //$NON-NLS-1$

		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);

			if (prefix.equals("objectTree")) { //$NON-NLS-1$
				if (spec != null) {
					edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						javax.swing.JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel worldTreeModel = (edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel) tree.getModel();
						tree.scrollPathToVisible(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
					}
				}
			} else if (prefix.equals("clipboard")) { //$NON-NLS-1$
				//Do nothing

			} else if (prefix.equals("sceneEditor")) { //$NON-NLS-1$
				if (spec.equals("large")) { //$NON-NLS-1$
					jAliceFrame.sceneEditor.setGuiMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.LARGE_MODE);
					//	System.out.println("just made scene editor large");
				} else if (spec.equals("small")) { //$NON-NLS-1$
					jAliceFrame.sceneEditor.setGuiMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.SMALL_MODE);
					//	System.out.println("just made scene editor small");
				}
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView
					if (token.equals("singleView")) { //$NON-NLS-1$
						jAliceFrame.sceneEditor.setViewMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.SINGLE_VIEW_MODE);
						//	System.out.println("just set single view");
					} else if (token.equals("quadView")) { //$NON-NLS-1$
						jAliceFrame.sceneEditor.setViewMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.QUAD_VIEW_MODE);
						//	System.out.println("just made set quad view");
					}

					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) { //$NON-NLS-1$
							//jAliceFrame.sceneEditor.getGalleryViewer().setDirectory( spec );
							//		System.out.println("trying to show gallery viewer");
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("button")) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
									}
								} else if (prefix.equals("galleryObject")) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
									if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
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
			} else if (prefix.equals("details")) { //$NON-NLS-1$
				if (spec == null) {
					spec = ""; //$NON-NLS-1$
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

						if (!prefix.equals("tab")) { //$NON-NLS-1$
							boolean isViewController = false;
							if (prefix.equals("viewController")) { //$NON-NLS-1$
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources.getPrefix(token);
									spec = AuthoringToolResources.getSpecifier(token);
									isViewController = true;
								}
							}
							if (prefix.equals("property") || prefix.equals("variable") || prefix.equals("textureMap") || prefix.equals("sound") || prefix.equals("other")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
								jAliceFrame.dragFromComponent.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.PROPERTIES_TAB);
							}
							if (prefix.equals("userDefinedResponse") || prefix.equals("responsePrototype")) { //$NON-NLS-1$ //$NON-NLS-2$
								jAliceFrame.dragFromComponent.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.ANIMATIONS_TAB);
							}
							if (prefix.equals("userDefinedQuestion") || prefix.equals("questionPrototype")) { //$NON-NLS-1$ //$NON-NLS-2$
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
			} else if (prefix.equals("behaviors")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("elementTile") && (spec != null)) { //$NON-NLS-1$
						edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources.getPrefix(token);
								spec = AuthoringToolResources.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) { //$NON-NLS-1$
									java.awt.Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
									if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
									}
								} else if (prefix.equals("more")) { //$NON-NLS-1$
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
			} else if (prefix.equals("editors")) { //$NON-NLS-1$
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) { //$NON-NLS-1$
						edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
						if (elementBeingEdited == null) {
							throw new edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException(spec);
						}
						if (st.hasMoreTokens()) {
							if (jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != elementBeingEdited) {
								editObject(elementBeingEdited);
							}

							//							Editor editor = jAliceFrame.tabbedEditorComponent.getEditorAt( jAliceFrame.tabbedEditorComponent.getIndexOfObject( elementBeingEdited ) );
							java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("button")) { //$NON-NLS-1$
								java.awt.Component c = AuthoringToolResources.findButton(container, spec);
								if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
									((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
								}
							} else if (prefix.equals("elementTile") && (spec != null)) { //$NON-NLS-1$
								edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
								if (element != null) {
									if (st.hasMoreTokens()) {
										token = st.nextToken();
										prefix = AuthoringToolResources.getPrefix(token);
										spec = AuthoringToolResources.getSpecifier(token);
										if (prefix.equals("property") && (spec != null)) { //$NON-NLS-1$
											java.awt.Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
											if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
												((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
											}
										} else if (prefix.equals("more")) { //$NON-NLS-1$
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
							} else if (prefix.equals("elementPrototypeTile") && (spec != null)) { //$NON-NLS-1$
								try {
									Class elementClass = Class.forName(spec);
									if (elementClass != null) {
										java.awt.Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
										if ((c != null) && c.isShowing() && (c instanceof javax.swing.JComponent)) {
											((javax.swing.JComponent) c).scrollRectToVisible(c.getBounds());
										}
									}
								} catch (Exception e) {
									AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.470") + spec, e); //$NON-NLS-1$
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
						edu.cmu.cs.stage3.alice.core.Property resp = element.getParent().getPropertyNamed("responses"); //$NON-NLS-1$
						if (resp instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
							actualPosition = ((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) resp).indexOf(element);
							//									System.out.println("index in responses: " + actualPosition);
						}
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
						edu.cmu.cs.stage3.alice.core.Property resp = element.getParent().getPropertyNamed("componentResponses"); //$NON-NLS-1$
						if (resp instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
							actualPosition = ((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) resp).indexOf(element);
							//									System.out.println("index in componentResponses: " + actualPosition);
						}
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
						edu.cmu.cs.stage3.alice.core.Property resp = element.getParent().getPropertyNamed("behaviors"); //$NON-NLS-1$
						if (resp instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) {
							actualPosition = ((edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty) resp).indexOf(element);
							//									System.out.println("index in responses: " + actualPosition);
						}
					}

					// element isn't in the right place wrt to its parent
					if (position != actualPosition) {
						//								  System.out.println("actual position: " + actualPosition + " correct position: " + position);
						return false;
					}
				} else {
					// element doesn't exist and it should
					return false;
				}
			}

			//						  System.out.println("elements in correct positions ok");

			for (java.util.Iterator iter = propertyValueKeys.iterator(); iter.hasNext();) {
				String propertyKey = (String) iter.next();
				String valueRepr = stencilStateCapsule.getPropertyValue(propertyKey);
				int dotIndex = propertyKey.lastIndexOf("."); //$NON-NLS-1$
				String elementKey = propertyKey.substring(0, dotIndex);
				String propertyName = propertyKey.substring(dotIndex + 1);

				edu.cmu.cs.stage3.alice.core.Element propertyOwner = world.getDescendantKeyed(elementKey);
				if (propertyOwner != null) {

					// getting "properties" of a call to a user defined response
					if (propertyOwner instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
						edu.cmu.cs.stage3.alice.core.Property requiredParams = propertyOwner.getPropertyNamed("requiredActualParameters"); //$NON-NLS-1$

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

			//						  System.out.println("property value checks ok");

			// if we've arrived here, it means that all the new conditions have been met. check to make sure user hasn't done
			// anything else that's strange.

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

		if (prefix.equals("loadWorld")) { //$NON-NLS-1$
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
		if (Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) ) > 12) { //$NON-NLS-1$
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("AuthoringTool.478") + Messages.getString("AuthoringTool.479"), //$NON-NLS-1$ //$NON-NLS-2$
					Messages.getString("AuthoringTool.480"), //$NON-NLS-1$
					javax.swing.JOptionPane.WARNING_MESSAGE);
		} else {
		if (tutorialFile == null) {
			tutorialFile = tutorialOne;
		}
		tutorialFile = tutorialFile.getAbsoluteFile(); //BIG HACK
		showStencils();
		stencilManager.loadStencilTutorial(tutorialFile);
		}
	}
	public java.io.File getTutorialDirectory() {
		return tutorialDirectory;
	}
	public java.io.File getExampleWorldsDirectory() {
		return new java.io.File(authoringToolConfig.getValue("directories.examplesDirectory")).getAbsoluteFile(); //$NON-NLS-1$
	}
	public java.io.File getTemplateWorldsDirectory() {
		return new java.io.File(authoringToolConfig.getValue("directories.templatesDirectory")).getAbsoluteFile(); //$NON-NLS-1$
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
		java.io.File dir = new java.io.File(directory + "/sound"); //$NON-NLS-1$
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
		return renderTargetFactory.toString();
	}

	// Logging
	public void logInstructorIntervention(){
		//get comment if starting something new
		String comment = ""; //$NON-NLS-1$
		if (instructorInControl == false) {
			comment = javax.swing.JOptionPane.showInputDialog(Messages.getString("AuthoringTool.485")); //$NON-NLS-1$
			if (comment != null) instructorInControl = true;
		} else {
			instructorInControl = false;
		}
		//log it
		if (undoRedoStack != null){
			if (instructorInControl){
				undoRedoStack.logInstructorIntervention(Messages.getString("AuthoringTool.486"), comment); //$NON-NLS-1$
			} else {
				if (comment != null) undoRedoStack.logInstructorIntervention(Messages.getString("AuthoringTool.487"), comment); //$NON-NLS-1$
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
		countSomething("edu.cmu.cs.stage3.alice.authoringtool.playCount"); //$NON-NLS-1$

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
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.489"), null); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.490"), wrappedException); //$NON-NLS-1$
			}
			return false;
		} catch (Throwable t) {
			world.restore();
			showErrorDialog(Messages.getString("AuthoringTool.491"), t); //$NON-NLS-1$
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
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.492"), null); //$NON-NLS-1$
			if (org.python.core.Py.matchException(e, org.python.core.Py.SystemExit)) {
				//TODO
			} else {
				org.python.core.Py.printException(e, null, pyStdErr);
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.493"), t); //$NON-NLS-1$
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
			if (!AuthoringToolResources.isMethodHookedUp((edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) object, world) && !authoringToolConfig.getValue(Messages.getString("AuthoringTool.494")).equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
				String objectRepr = AuthoringToolResources.getReprForValue(object, true);
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					Messages.getString("AuthoringTool.496") + objectRepr + Messages.getString("AuthoringTool.497"), //$NON-NLS-1$ //$NON-NLS-2$
					Messages.getString("AuthoringTool.498"), //$NON-NLS-1$
					javax.swing.JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void play() {
		jAliceFrame.playButton.setEnabled(false);

		checkForUnreferencedCurrentMethod();

		if (worldRun()) {
			double aspectRatio = getAspectRatio();
			stdErrOutContentPane.stopReactingToPrint();
			renderContentPane.setAspectRatio(aspectRatio);
			renderContentPane.getRenderPanel().add(renderPanel, java.awt.BorderLayout.CENTER);
			
			edu.cmu.cs.stage3.swing.DialogManager.showDialog(renderContentPane);
			stdErrOutContentPane.startReactingToPrint();
		}
		jAliceFrame.playButton.setEnabled(true);
	}

	public static boolean isresizable = true;

	public javax.swing.JPanel playWhileEncoding(String directory) {
		jAliceFrame.playButton.setEnabled(false);

		checkForUnreferencedCurrentMethod();

		if (worldRun()) {
			double aspectRatio = getAspectRatio();
			stdErrOutContentPane.stopReactingToPrint();
			captureContentPane.setExportDirectory(directory);
			captureContentPane.captureInit();
			captureContentPane.setAspectRatio(aspectRatio);
			captureContentPane.getRenderPanel().add(renderPanel, java.awt.BorderLayout.CENTER);
			jAliceFrame.sceneEditor.makeDirty();
			isresizable = false;
			edu.cmu.cs.stage3.swing.DialogManager.showDialog(captureContentPane);
			stdErrOutContentPane.startReactingToPrint();
		}
		jAliceFrame.playButton.setEnabled(true);
		isresizable = true;
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
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.499"), null); //$NON-NLS-1$
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
				AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.500"), wrappedException); //$NON-NLS-1$
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.501"), t); //$NON-NLS-1$
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
			AuthoringTool.showErrorDialog(Messages.getString("AuthoringTool.502"), t); //$NON-NLS-1$
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
	
	// Aik Min
    private static boolean pulsing = true;   
    private static JLabel statusLabel = new JLabel(Messages.getString("AuthoringTool.503")); //$NON-NLS-1$
    private static JFrame statusFrame;
    
	public void updateAlice() {
		createStatusFrame();
		pulsing = true;
        Thread pulse = new Thread (new StatusPulsing(statusLabel, Messages.getString("AuthoringTool.504"))); //$NON-NLS-1$
        statusFrame.setVisible(true);
        pulse.start();
        new Thread(new StartUpdating() ).start();
 	}
	
	public static void createStatusFrame() {
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		
   		statusFrame = new JFrame(Messages.getString("AuthoringTool.505")); //$NON-NLS-1$
		statusFrame.setPreferredSize(new Dimension(250,100));
		statusFrame.setLocation( dim.getSize().width/2-150, dim.getSize().height/2-50 );
		statusFrame.getContentPane().add( statusLabel );
		statusFrame.setVisible(false);
		statusFrame.setAlwaysOnTop(true);
		statusFrame.pack();
	}
  	
	public class StartUpdating implements Runnable {
			
		public void run() {
			StringBuffer sb = new StringBuffer( "http://alicedownloads.org/alice.jar" ); //$NON-NLS-1$
			java.io.BufferedInputStream bis = null;
			java.io.BufferedOutputStream bos = null;
	        try {
				java.net.URL url = new java.net.URL( sb.toString() );
				java.net.URLConnection urlc = url.openConnection();
				
				java.io.File aliceHome = new java.io.File(JAlice.getAliceHomeDirectory().toString() + "\\lib\\aliceupdate.jar"); //$NON-NLS-1$
				    	
				bis = new java.io.BufferedInputStream( urlc.getInputStream() );
				bos = new java.io.BufferedOutputStream( new java.io.FileOutputStream( aliceHome ) );
				
				int i;
				while ((i = bis.read()) != -1) {
					bos.write( i );
				}
				pulsing = false;
				statusFrame.dispose();
				javax.swing.JOptionPane.showMessageDialog(null, Messages.getString("AuthoringTool.508"), Messages.getString("AuthoringTool.509"), javax.swing.JOptionPane.INFORMATION_MESSAGE ); //$NON-NLS-1$ //$NON-NLS-2$
	        } catch (Exception e1) {
	        	pulsing = false;
	        	statusFrame.dispose();
	        	javax.swing.JOptionPane.showMessageDialog(null, Messages.getString("AuthoringTool.510"), Messages.getString("AuthoringTool.511"), javax.swing.JOptionPane.ERROR_MESSAGE ); //$NON-NLS-1$ //$NON-NLS-2$
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
				
	        }
		}
	}
  	
	public class StatusPulsing implements Runnable {
		private JLabel label;

		private String text;
		
		public StatusPulsing(JLabel l, String s) {
			label = l;
			text = s;
		}

		public void run() {

			long time = 300;
			while (AuthoringTool.pulsing) {
				label.setText(text);
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
				}
				label.setText(text + "."); //$NON-NLS-1$
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
				}
				label.setText(text + ". ."); //$NON-NLS-1$
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
				}
				label.setText(text + ". . ."); //$NON-NLS-1$
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
				}
			}
		}

	}
	
}