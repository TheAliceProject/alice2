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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Font;
import edu.cmu.cs.stage3.lang.Messages;

/**
 * @author Jason Pratt
 */
public class SplashScreen extends java.awt.Frame {
	protected java.awt.Image image;
	protected java.awt.Dimension size;
	protected java.awt.Frame splashWindow;

	public SplashScreen( java.awt.Image image ) {       
		this.image = image;

		java.awt.MediaTracker tracker = new java.awt.MediaTracker( this );
		tracker.addImage( image, 0 );
		try {
			tracker.waitForID( 0 );
		} catch( InterruptedException e ) {}

		size = new java.awt.Dimension( image.getWidth( this ), image.getHeight( this ) );
		if( (size.width < 1) || (size.height < 1) ) {
			size = new java.awt.Dimension( 256, 256 );
		}
		
		splashWindow = new java.awt.Frame() {
			public void paint( java.awt.Graphics g ) {
				super.paint(g);
				g.drawImage( SplashScreen.this.image, 0, 0, this );
//				g.setColor( java.awt.Color.yellow );
				g.setColor( java.awt.Color.white );
				//Font font = g.getFont();			
				g.setFont(new Font("Dialog", Font.BOLD, 12));
				String versionString = Messages.getString("version__", edu.cmu.cs.stage3.alice.authoringtool.JAlice.getVersion()); 
				int stringWidth = g.getFontMetrics().stringWidth( versionString );
				g.drawString( versionString, size.width - 10 - stringWidth, size.height - 6 ); //TODO: this makes the Splash Screen unnecessarily specialized.  the functionality should be abstracted out.			
				g.drawString(Messages.getString("Loading___", ""), 10, size.height - 6 );
			}
		};
		splashWindow.setSize( size );
		splashWindow.setUndecorated(true);
	}

	public void showSplash() {
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - size.width)/2;
		int y = (screenSize.height - size.height)/2;
		splashWindow.setLocation( x, y );
		//this.setLocation( x, y  );
		splashWindow.setVisible( true );
	}

	public void hideSplash() {
		//splashWindow.setVisible( false );
		splashWindow.dispose();
	}
}
