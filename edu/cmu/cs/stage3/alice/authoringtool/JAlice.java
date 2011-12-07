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

import java.util.Locale;

/**
 * @author Jason Pratt
 */
public class JAlice {
	static{
		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( JAlice.class.getPackage() );
		if( authoringtoolConfig.getValue( "language" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "language", "en - English" ); //$NON-NLS-1$ //$NON-NLS-2$
		} 
		String locale = authoringtoolConfig.getValue( "language" );
		locale = locale.substring(0, locale.indexOf(" ")); //$NON-NLS-1$
		Locale.setDefault(new Locale( locale )); //$NON-NLS-1$
	}
	// version information
	private static String version = Messages.getString("JAlice.0"); //$NON-NLS-1$
	private static String backgroundColor =  new edu.cmu.cs.stage3.alice.scenegraph.Color( 127.0/255.0, 138.0/255.0, 209.0/255.0 ).toString();
	private static String directory = null;
	static {
		try {
			java.io.File versionFile = new java.io.File( getAliceHomeDirectory(), "etc/version.txt" ).getAbsoluteFile(); //$NON-NLS-1$
			if( versionFile.exists() ) {
				if( versionFile.canRead() ) {
					java.io.BufferedReader br = new java.io.BufferedReader( new java.io.FileReader( versionFile ) );
					String versionString = br.readLine();
					String colorString = br.readLine();
					directory = br.readLine();
					br.close();
					if( colorString != null ) {
						colorString = colorString.trim();
						if( colorString.length() > 0 ) {
							try{
								if (colorString.startsWith("0x") == false){ //$NON-NLS-1$
									String [] color = colorString.split(","); //$NON-NLS-1$
									double red = Integer.decode(color[0]).doubleValue() / 255.0;
									double green = Integer.decode(color[1]).doubleValue() / 255.0;
									double blue = Integer.decode(color[2]).doubleValue() / 255.0;
									backgroundColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(red, green, blue).toString();
								}else{
									java.awt.Color newColor = java.awt.Color.decode(colorString);
									backgroundColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(newColor).toString();
								}
							} catch (Throwable colorT){colorT.printStackTrace();}							
						} 
					}
					if( versionString != null ) {
						versionString = versionString.trim();
						if( versionString.length() > 0 ) {
							version = versionString;
						} else {
							version = Messages.getString("JAlice.4"); //$NON-NLS-1$
						}
					} else {
						version = Messages.getString("JAlice.5"); //$NON-NLS-1$
					}
				} else {
					version = Messages.getString("JAlice.6"); //$NON-NLS-1$
				}
			} else {
				version = Messages.getString("JAlice.7"); //$NON-NLS-1$
			}
		} catch( Throwable t ) {
			t.printStackTrace();
			version = Messages.getString("JAlice.8"); //$NON-NLS-1$
		}
	}

	public static String getVersion() {
		return version;
	}

	static java.io.File aliceHomeDirectory = null;
	static java.io.File aliceUserDirectory = null;

	static edu.cmu.cs.stage3.alice.authoringtool.util.SplashScreen splashScreen;
	static java.io.File defaultWorld;
	static java.io.File worldToLoad = null;
	static boolean stdOutToConsole = false;
	static boolean stdErrToConsole = false;
	static String defaultRendererClassname = null;
	static AuthoringTool authoringTool;

	static boolean mainHasFinished = false;

	//////////////////////
	// main
	//////////////////////

	public static void main( String[] args ) {		
		try {
		    String[] mp3args = new String[ 0 ];
		    //System.out.println( "attempting to register mp3 capability... " );
		    com.sun.media.codec.audio.mp3.JavaDecoder.main( mp3args );
		} catch( Throwable t ) {
		    //System.out.println( "FAILED." );
		    t.printStackTrace( System.out );
		}

		try {
			boolean useJavaBasedSplashScreen = true;
			String useSplashScreenString = System.getProperty( "alice.useJavaBasedSplashScreen" ); //$NON-NLS-1$
			if( (useSplashScreenString != null) && (! useSplashScreenString.equalsIgnoreCase( "true" )) ) { //$NON-NLS-1$
				useJavaBasedSplashScreen = false;
			}
			parseCommandLineArgs( args );
			if( useJavaBasedSplashScreen ) {
				splashScreen = initSplashScreen();
				splashScreen.showSplash();
			}
			defaultWorld = new java.io.File( getAliceHomeDirectory(), "etc/default.a2w" ).getAbsoluteFile(); //$NON-NLS-1$
			Class.forName( "edu.cmu.cs.stage3.alice.authoringtool.util.Configuration" ); //$NON-NLS-1$
			configInit();
			try{
				java.io.File aliceHasNotExitedFile = new java.io.File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt"); //$NON-NLS-1$
				if (aliceHasNotExitedFile.exists()){
					aliceHasNotExitedFile.delete();
				}
				aliceHasNotExitedFile.createNewFile();
				java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(new java.io.FileOutputStream(aliceHasNotExitedFile));
				writer.write(Messages.getString("JAlice.14")); //$NON-NLS-1$
				writer.flush();
				writer.close();
			}catch (Exception e){}
			Class.forName( "edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources" ); //$NON-NLS-1$
			Class.forName( "edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities" ); //$NON-NLS-1$
			authoringTool = new AuthoringTool( defaultWorld, worldToLoad, stdOutToConsole, stdErrToConsole );
			if( useJavaBasedSplashScreen ) {
				splashScreen.hideSplash();
			}
		} catch( Throwable t ) {
			t.printStackTrace();
			//System.exit( 1 );
		}

		mainHasFinished = true;
	}

	private static edu.cmu.cs.stage3.alice.authoringtool.util.SplashScreen initSplashScreen() {
		java.io.File splashFile = new java.io.File( getAliceHomeDirectory(), "etc/AliceSplash.jpg" ).getAbsoluteFile(); //$NON-NLS-1$
		java.awt.Image splashImage = java.awt.Toolkit.getDefaultToolkit().getImage( splashFile.getAbsolutePath() );
		return new edu.cmu.cs.stage3.alice.authoringtool.util.SplashScreen( splashImage );
	}

	private static void configInit() {
		final edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( JAlice.class.getPackage() );
//		System.out.println(backgroundColor);
//		System.out.println(new edu.cmu.cs.stage3.alice.scenegraph.Color( 127.0/255.0, 138.0/255.0, 209.0/255.0 ).toString());
		authoringtoolConfig.setValue( "backgroundColor", backgroundColor ); //$NON-NLS-1$
		if( authoringtoolConfig.getValue( "recentWorlds.maxWorlds" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "recentWorlds.maxWorlds", Integer.toString( 8 ) ); //$NON-NLS-1$
		}
		if( authoringtoolConfig.getValueList( "recentWorlds.worlds" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValueList( "recentWorlds.worlds", new String[] {} ); //$NON-NLS-1$
		}
		
		if( authoringtoolConfig.getValue( "enableHighContrastMode" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "enableHighContrastMode", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "enableLoggingMode" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "enableLoggingMode", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if( authoringtoolConfig.getValue( "disableTooltipMode" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "disableTooltipMode", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if( authoringtoolConfig.getValue( "fontSize" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "fontSize", Integer.toString( 12 ) ); //$NON-NLS-1$
		}
		
		if( authoringtoolConfig.getValue( "showObjectLoadFeedback" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "showObjectLoadFeedback", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if( authoringtoolConfig.getValue( "maximumWorldBackupCount" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "maximumWorldBackupCount", Integer.toString( 5 ) ); //$NON-NLS-1$
		}

		if( authoringtoolConfig.getValue( "maxRecentlyUsedValues" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "maxRecentlyUsedValues", Integer.toString( 5 ) ); //$NON-NLS-1$
		}

		if( authoringtoolConfig.getValue( "numberOfClipboards" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "numberOfClipboards", Integer.toString( 1 ) ); //$NON-NLS-1$
		}

		if( authoringtoolConfig.getValue( "showWorldStats" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "showWorldStats", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "enableScripting" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "enableScripting", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "promptToSaveInterval" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "promptToSaveInterval", Integer.toString( 15 ) ); //$NON-NLS-1$
		}

		if (authoringtoolConfig.getValue("doNotShowUnhookedMethodWarning") == null){ //$NON-NLS-1$
			authoringtoolConfig.setValue("doNotShowUnhookedMethodWarning", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		}

//		if( authoringtoolConfig.getValue( "backgroundColor" ) == null ) {
//			authoringtoolConfig.setValue( "backgroundColor", new edu.cmu.cs.stage3.alice.scenegraph.Color( 127.0/255.0, 138.0/255.0, 209.0/255.0 ).toString() );
//		}

		if( authoringtoolConfig.getValue( "clearStdOutOnRun" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "clearStdOutOnRun", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "resourceFile" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "resourceFile", "Alice Style.py" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	
		if( authoringtoolConfig.getValue( "watcherPanelEnabled" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "watcherPanelEnabled", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "showStartUpDialog" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "showStartUpDialog", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if( authoringtoolConfig.getValue( "showWebWarningDialog" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "showWebWarningDialog", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if( authoringtoolConfig.getValue( "showStartUpDialog_OpenTab" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "showStartUpDialog_OpenTab", Integer.toString(edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.TUTORIAL_TAB_ID) ); //$NON-NLS-1$
		}
		
		if( authoringtoolConfig.getValue( "loadSavedTabs" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "loadSavedTabs", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "saveThumbnailWithWorld" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "saveThumbnailWithWorld", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

//		if( authoringtoolConfig.getValue( "useJavaSyntax" ) == null ) {
//			authoringtoolConfig.setValue( "useJavaSyntax", "false" );
//		} else if( authoringtoolConfig.getValue( "useJavaSyntax" ).equalsIgnoreCase( "true" ) ) {
//			AuthoringToolResources.setSyntaxMode( "java" );
//		}
//		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.addConfigurationListener(
//			new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener() {
//				public void changing( edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev ) {
//					if( ev.getKeyName().equals( "edu.cmu.cs.stage3.alice.authoringtool.useJavaSyntax" ) ) {
//						if( ev.getNewValue().equalsIgnoreCase( "true" ) ) {
//							AuthoringToolResources.setSyntaxMode( "java" );
//						} else {
//							AuthoringToolResources.setSyntaxMode( "default" );
//						}
//					}
//				}
//				public void changed( edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev ) {}
//			}
//		);

		if( authoringtoolConfig.getValue( "mainWindowBounds" ) == null ) { //$NON-NLS-1$
			int screenWidth = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int screenHeight = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int x = 0;
			int y = 0;
			int height = screenHeight - 30;
			authoringtoolConfig.setValue( "mainWindowBounds", (x+80) + ", " + y + ", " + (screenWidth-80) + ", " + height ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}

		if( authoringtoolConfig.getValueList( "rendering.orderedRendererList" ) == null ) { //$NON-NLS-1$
			Class[] rendererClasses =  edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory.getPotentialRendererClasses();
			String[] list = new String[rendererClasses.length];
			for( int i = 0; i < rendererClasses.length; i++ ) {
				list[i] = rendererClasses[ i ].getName();
			}
			authoringtoolConfig.setValueList( "rendering.orderedRendererList", list ); //$NON-NLS-1$
		}

		if( authoringtoolConfig.getValue( "rendering.showFPS" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.showFPS", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "rendering.forceSoftwareRendering" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.forceSoftwareRendering", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "rendering.deleteFiles" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.deleteFiles", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if( authoringtoolConfig.getValue( "rendering.renderWindowMatchesSceneEditor" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.renderWindowMatchesSceneEditor", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if( authoringtoolConfig.getValue( "rendering.ensureRenderDialogIsOnScreen" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.ensureRenderDialogIsOnScreen", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "rendering.renderWindowBounds" ) == null ) { //$NON-NLS-1$
			int screenWidth = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int screenHeight = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int width = 480; //(int)(screenWidth*.5);
			int height = 360; //(int)Math.round( ((double)width)/(screenWidth/screenHeight) );
			int x = (screenWidth - width)/2;
			int y = (screenHeight - height)/2;

			authoringtoolConfig.setValue( "rendering.renderWindowBounds", x + ", " + y + ", " + width + ", " + height ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}

		if( authoringtoolConfig.getValue( "rendering.runtimeScratchPadEnabled" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.runtimeScratchPadEnabled", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "rendering.runtimeScratchPadHeight" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.runtimeScratchPadHeight", "300" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "rendering.useBorderlessWindow" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.useBorderlessWindow", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "rendering.constrainRenderDialogAspectRatio" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "rendering.constrainRenderDialogAspectRatio", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

//		if( authoringtoolConfig.getValue( "printing.scaleFactor" ) == null ) {
//			authoringtoolConfig.setValue( "printing.scaleFactor", "1.0" );
//		}
//
//		if( authoringtoolConfig.getValue( "printing.fillBackground" ) == null ) {
//			authoringtoolConfig.setValue( "printing.fillBackground", "true" );
//		}

		if( authoringtoolConfig.getValue( "gui.pickUpTiles" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "gui.pickUpTiles", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "gui.useAlphaTiles" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "gui.useAlphaTiles", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "useSingleFileLoadStore" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "useSingleFileLoadStore", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "directories.worldsDirectory" ) == null ) { //$NON-NLS-1$
			//TODO: be more cross-platform aware
			String dir = System.getProperty( "user.home" ) + System.getProperty( "file.separator" ) + "Desktop"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			authoringtoolConfig.setValue( "directories.worldsDirectory", dir ); //$NON-NLS-1$
		}

//		if( authoringtoolConfig.getValue( "directories.galleryDirectory" ) == null ) {
//			if( new java.io.File( "gallery" ).getAbsoluteFile().exists() ) {
//				authoringtoolConfig.setValue( "directories.galleryDirectory", "gallery" );
//			} else { // this is kind of silly
//				String dir = System.getProperty( "user.home" ) + System.getProperty( "file.separator" ) + "Desktop";
//				authoringtoolConfig.setValue( "directories.galleryDirectory", dir );
//			}
//		}

		if( authoringtoolConfig.getValue( "directories.importDirectory" ) == null ) { //$NON-NLS-1$
			//TODO: be more cross-platform aware
			String dir = System.getProperty( "user.home" ) + System.getProperty( "file.separator" ) + "Desktop"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			authoringtoolConfig.setValue( "directories.importDirectory", dir ); //$NON-NLS-1$
		}

		if( authoringtoolConfig.getValue( "directories.examplesDirectory" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "directories.examplesDirectory", "exampleWorlds" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "directories.charactersDirectory" ) == null ) { //$NON-NLS-1$
			String dir = System.getProperty( "user.home" ) + System.getProperty( "file.separator" ) + "Desktop"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			java.io.File captureDir = new java.io.File(dir);
			if (captureDir.canWrite()){
				authoringtoolConfig.setValue( "directories.charactersDirectory", dir ); //$NON-NLS-1$
			}
			else{
				authoringtoolConfig.setValue( "directories.charactersDirectory", null ); //$NON-NLS-1$
			}
		}
		
		if( authoringtoolConfig.getValue( "directories.templatesDirectory" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "directories.templatesDirectory", "templateWorlds" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
				
		if( authoringtoolConfig.getValue( "directories.textbookExamplesDirectory" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "directories.textbookExamplesDirectory", "textbookExampleWorlds" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

//		if( authoringtoolConfig.getValue( "reloadWorldScriptOnRun" ) == null ) {
//			authoringtoolConfig.setValue( "reloadWorldScriptOnRun", "false" );
//		}

		if( authoringtoolConfig.getValue( "screenCapture.directory" ) == null ) { //$NON-NLS-1$
			String dir = System.getProperty( "user.home" ) + System.getProperty( "file.separator" ) + "Desktop"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			authoringtoolConfig.setValue( "screenCapture.directory", dir ); //$NON-NLS-1$
		}
		if( authoringtoolConfig.getValue( "screenCapture.baseName" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "screenCapture.baseName", "capture" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if( authoringtoolConfig.getValue( "screenCapture.numDigits" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "screenCapture.numDigits", "2" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if( authoringtoolConfig.getValue( "screenCapture.codec" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "screenCapture.codec", "jpeg" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if( authoringtoolConfig.getValue( "screenCapture.codec" ).equalsIgnoreCase("gif") ) { //$NON-NLS-1$ //$NON-NLS-2$
			authoringtoolConfig.setValue( "screenCapture.codec", "jpeg" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if( authoringtoolConfig.getValue( "screenCapture.informUser" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "screenCapture.informUser", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "saveInfiniteBackups" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "saveInfiniteBackups", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if( authoringtoolConfig.getValue( "doProfiling" ) == null ) { //$NON-NLS-1$
			authoringtoolConfig.setValue( "doProfiling", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private static void parseCommandLineArgs( String[] args ) {
		int c;
		//String arg;
		gnu.getopt.LongOpt[] options = {
			new gnu.getopt.LongOpt("stdOutToConsole", gnu.getopt.LongOpt.NO_ARGUMENT, null, 'o'), //$NON-NLS-1$
			new gnu.getopt.LongOpt("stdErrToConsole", gnu.getopt.LongOpt.NO_ARGUMENT, null, 'e'), //$NON-NLS-1$
			new gnu.getopt.LongOpt("defaultRenderer", gnu.getopt.LongOpt.REQUIRED_ARGUMENT, null, 'r'), //$NON-NLS-1$
			//new gnu.getopt.LongOpt("customStartupClass", gnu.getopt.LongOpt.REQUIRED_ARGUMENT, null, 'c'),
			new gnu.getopt.LongOpt("help", gnu.getopt.LongOpt.NO_ARGUMENT, null, 'h'), //$NON-NLS-1$
		};

		String helpMessage = "" + //$NON-NLS-1$
"\nUsage: JAlice <options> <world>\n" + //$NON-NLS-1$
"\n" + //$NON-NLS-1$
"options:\n" + //$NON-NLS-1$
"    --stdOutToConsole|-o:\n" + //$NON-NLS-1$
"        directs System.stdOut to the console instead of the output text area.\n" + //$NON-NLS-1$
"    --stdErrToConsole|-e:\n" + //$NON-NLS-1$
"        directs System.stdOut to the console instead of the output text area.\n" + //$NON-NLS-1$
"    --defaultRenderer|-r <classname>:\n" + //$NON-NLS-1$
"        the Renderer specified by <classname> will be used as the default Renderer\n" + //$NON-NLS-1$
//"    --customStartupClass|-c <classname>:\n" +
//"        calls <classname>.customSetup( String [] args, <JAlice instance>,\n" +
//"                  <world instance> )\n" +
//"        during system initialization\n" +
"    --help|-h:\n" + //$NON-NLS-1$
"        prints this help message\n" + //$NON-NLS-1$
"\n" + //$NON-NLS-1$
"world:\n" + //$NON-NLS-1$
"    a pathname to a world on disk to be loaded at startup.\n"; //$NON-NLS-1$

		// for the options string:
		// --a lone character has no options
		// --a character preceded by a colon has a required argument
		// --a character preceded by two colons has a non-required argument
		// --if the whole string starts with a colon, then ':' is returned for valid options that do not have their required argument
		gnu.getopt.Getopt g = new gnu.getopt.Getopt( "JAlice", args, ":oeh", options ); //$NON-NLS-1$ //$NON-NLS-2$
		while( (c = g.getopt()) != -1 ) {
			switch( c ) {
				case 'o': //stdOut to console...
					stdOutToConsole = true;
					break;
				case 'e': //stdErr to console...
					stdErrToConsole = true;
					break;
				case 'r': //default Renderer Class...
					defaultRendererClassname = g.getOptarg();
					break;
				/*
				case 'c': //custom Startup class
					arg = g.getOptarg();
					try	{
						Class cls = Class.forName( arg );
						Object [] argValues = { args, f, f.world };
						Class [] argClasses = new Class[argValues.length];
						for( int i=0; i<argClasses.length; i++ ) {
							argClasses[i] = argValues[i].getClass();
						}
						java.lang.reflect.Method method = cls.getMethod( "customSetup", argClasses );
						method.invoke( null, argValues );
					} catch( Exception e ) {
						e.printStackTrace();
					}
					break;
				*/
				case 'h': //help
				case '?':
					System.err.println( helpMessage );
					System.exit( 0 );
					break;
				default:
					System.err.println( "ignoring " + c + " on the command line." ); //$NON-NLS-1$ //$NON-NLS-2$
					break;
			}
		}

		int i = g.getOptind();
		if( (i >= 0) && (i < args.length) ) {
			if ((System.getProperty("os.name") != null) && System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				char ch = ':';
				String file = args[i].toString(); 
				file = file.substring(file.lastIndexOf(ch)-1, file.length()-1);
				worldToLoad = new java.io.File( file ).getAbsoluteFile();
			} else 
				worldToLoad = new java.io.File( args[i] ).getAbsoluteFile();
		}
	}

	public static boolean isMainFinished() {
		return mainHasFinished;
	}

	public static void setAliceHomeDirectory( java.io.File file ) {
		aliceHomeDirectory = file;
	}

	public static java.io.File getAliceHomeDirectory() {
		if( aliceHomeDirectory == null ) {
			if( System.getProperty( "alice.home" ) != null ) { //$NON-NLS-1$
				setAliceHomeDirectory( new java.io.File( System.getProperty( "alice.home" ) ).getAbsoluteFile() ); //$NON-NLS-1$
			} else {
				setAliceHomeDirectory( new java.io.File( System.getProperty( "user.dir" ) ).getAbsoluteFile() ); //$NON-NLS-1$
			}
		}

		return aliceHomeDirectory;
	}

	public static void setAliceUserDirectory( java.io.File file ) {
		if (file != null) {
			if( file.exists() ) {
				aliceUserDirectory = file;
			} else if( file.mkdir() ) {
				aliceUserDirectory = file;
			} 
		}
	}

	public static java.io.File getAliceUserDirectory() {
		if( aliceUserDirectory == null) {
			java.io.File dirFromProperties = null;
			if( System.getProperty( "alice.userDir" ) != null ) { //$NON-NLS-1$
				dirFromProperties = new java.io.File( System.getProperty( "alice.userDir" ) ).getAbsoluteFile(); //$NON-NLS-1$
			}
			java.io.File userHome = new java.io.File( System.getProperty( "user.home" ) ).getAbsoluteFile(); //$NON-NLS-1$
			java.io.File aliceHome = getAliceHomeDirectory();
			java.io.File aliceUser = null;
			if( directory != null) {
				aliceUser = new java.io.File( directory, ".alice2" ); //$NON-NLS-1$
			} else if (dirFromProperties != null ) {
				aliceUser = dirFromProperties;
			} else if( userHome.exists() && userHome.canRead() && userHome.canWrite() ) {
				aliceUser = new java.io.File( userHome, ".alice2" ); //$NON-NLS-1$
			} else if( (aliceHome != null) && aliceHome.exists() && aliceHome.canRead() && aliceHome.canWrite() ) {
				aliceUser = new java.io.File( aliceHome, ".alice2" ); //$NON-NLS-1$
			}
			setAliceUserDirectory( aliceUser );

		} 
		return aliceUserDirectory;
	}
}

