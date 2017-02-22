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

package edu.cmu.cs.stage3.alice.player;

import java.net.URISyntaxException;
import java.net.URLDecoder;

public class DefaultPlayer extends AbstractPlayer {
	
	public static Class rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer.class;
	public static DefaultPlayer player = new DefaultPlayer(rendererClass);
	
	public DefaultPlayer(Class rendererClass) {
		super( rendererClass );
	}
	private java.util.Vector m_frames = new java.util.Vector();
	
	protected boolean isPreserveAndRestoreRequired() {
		return true;
	}
	
	protected int getDesiredFrameWidth() {
		return 800;
	}
	protected int getDesiredFrameHeight() {
		return 600;
	}
	
	private javax.swing.JButton m_pauseButton = new javax.swing.JButton( "pause" );
	private javax.swing.JButton m_resumeButton = new javax.swing.JButton( "resume" );
	private javax.swing.JButton m_startButton = new javax.swing.JButton( "restart" );
	private javax.swing.JButton m_stopButton = new javax.swing.JButton( "stop" );
	private javax.swing.JSlider speedSlider;
	private javax.swing.JLabel speedLabel;
	
	public void updateSpeed(double newSpeed) {
		player.setSpeed(newSpeed);
		String speedText = java.text.NumberFormat.getInstance().format(newSpeed);
		if (newSpeed < 1) {
			if (newSpeed == .5) {
				speedText = "1/2";
			} else if (newSpeed == .25) {
				speedText = "1/4";
			} else if (newSpeed == .2) {
				speedText = "1/5";
			} else if (newSpeed > .3 && newSpeed < .34) {
				speedText = "1/3";
			} else if (newSpeed > .16 && newSpeed < .168) {
				speedText = "1/6";
			} else if (newSpeed > .14 && newSpeed < .143) {
				speedText = "1/7";
			}
		}
		speedLabel.setText(" Speed: " + speedText + "x");
		speedLabel.repaint();
	}
	protected void handleRenderTarget(edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget) {
		javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
		buttonPanel.setLayout( new java.awt.FlowLayout() );
		
		m_pauseButton.setEnabled( true );
		m_resumeButton.setEnabled( false );
		m_startButton.setEnabled( true );
		m_stopButton.setEnabled( true );
	
		speedLabel = new javax.swing.JLabel("  Speed: 1x    ");
		speedLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
		speedLabel.setSize(new java.awt.Dimension(100, 12));
/*		speedLabel.setPreferredSize(new java.awt.Dimension(80, 12));
		speedLabel.setMinimumSize(new java.awt.Dimension(20, 12));
		speedLabel.setMaximumSize(new java.awt.Dimension(80, 12));*/
		speedSlider = new javax.swing.JSlider(0, 9, 0);

/*		speedSlider.setUI(new javax.swing.plaf.metal.MetalSliderUI() {
			public void paintTrack(java.awt.Graphics g) {
				super.paintTrack(g);
			}
		});*/
/*		speedSlider.setPreferredSize(new java.awt.Dimension(100, 16));
		speedSlider.setMinimumSize(new java.awt.Dimension(40, 16));
		speedSlider.setMaximumSize(new java.awt.Dimension(100, 16));*/
		speedSlider.setSnapToTicks(true);
		speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent ce) {
				javax.swing.JSlider s = (javax.swing.JSlider) ce.getSource();
					int value = s.getValue();
					if (value >= 0) {
						updateSpeed(value + 1.0);
					} else if (value < 0) {
						updateSpeed(1.0 / (1 + (value * (-1))));
					}
			}
		});
		
		buttonPanel.add( speedLabel );
		buttonPanel.add( speedSlider );
		buttonPanel.add( m_pauseButton );
		buttonPanel.add( m_resumeButton );
		buttonPanel.add( m_startButton );
		buttonPanel.add( m_stopButton );
		
		java.awt.Frame frame = new java.awt.Frame("Alice .a2w player");
		
		frame.setSize(getDesiredFrameWidth(), getDesiredFrameHeight());
		frame.setLayout(new java.awt.BorderLayout());
		frame.add( buttonPanel, java.awt.BorderLayout.NORTH );
		frame.add( renderTarget.getAWTComponent(), java.awt.BorderLayout.CENTER );
		frame.setVisible( true );
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			
			public void windowClosing(java.awt.event.WindowEvent ev) {
				m_frames.removeElement(ev.getSource());
				if( m_frames.size() == 0 ) {
					System.exit(0);
				}
			}
		});
		
		m_pauseButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				m_pauseButton.setEnabled( false );
				m_resumeButton.setEnabled( true );
				player.pauseWorld();
			}
		} );

		m_resumeButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				m_pauseButton.setEnabled( true );
				m_resumeButton.setEnabled( false );
				player.resumeWorld();
			}
		} );

		m_stopButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				m_startButton.setText( "start" );
				m_pauseButton.setEnabled( false );
				m_stopButton.setEnabled( false );
				m_resumeButton.setEnabled( false );
				player.stopWorld();
			}
		} );

		m_startButton.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				player.stopWorldIfNecessary();
				m_startButton.setText( "restart" );
				m_pauseButton.setEnabled( true );
				m_stopButton.setEnabled( true );
				m_resumeButton.setEnabled( false );
				player.startWorld();
			}
		} );
		
		m_frames.add(frame);
		edu.cmu.cs.stage3.swing.DialogManager.initialize( frame );
	}
	
	private static java.io.File getFileFromArgs(String[] args, int startFrom) {
		java.io.File file = null;
		String path = "";
		int i = startFrom;
		while (i < args.length) {
			path += args[i];
			i++;
			file = new java.io.File(path);
			if( file.exists() ) {
				break;
			}
			path += " ";
			file = null;
		}
		return file;
	}

	public static void main(String[] args) throws URISyntaxException {
/*		System.setProperty("java.library.path", "lib");
		 
		java.lang.reflect.Field fieldSysPath;
		try {
			fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
*/
		java.io.File file = null;
		if( args.length > 0 ) {
			int startFrom = 0; // 1;
			/*
			if (args[0].equals("-directx")) {
				rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer.class;
			//} else if( args[ 0 ].equals("-opengl" ) ) {
			//	renderer = new edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer();
			//} else if( args[ 0 ].equals("-java3d" ) ) {
			//	rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer.class;
			} else if( args[ 0 ].equals("-jogl" ) ) {
			    rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer.class;
			} else if( args[ 0 ].equals("-null" ) ) {
				rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer.class;
			} else {
				System.err.println(args[0]);
				startFrom = 0;
			}*/
			file = getFileFromArgs(args, startFrom);
		}

		if( file == null ) {
			String path = player.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodePath = "";
			try {
				decodePath = URLDecoder.decode(path, "UTF-8");				
			} catch (Exception e) {}	
			file = new java.io.File( decodePath );
			java.io.FilenameFilter a2wFilter = new java.io.FilenameFilter() {
				public boolean accept(java.io.File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".a2w")) {
						return true;
					}
					return false;
				}
			};
			java.io.File[] files = file.listFiles(a2wFilter);
	
			if ( files.length != 1 ) {
				java.awt.Frame frame = new java.awt.Frame();
				java.awt.FileDialog fd = new java.awt.FileDialog(frame);
				fd.setVisible( true );
				//String filename = fd.getFile();
				if (fd.getDirectory() != null && fd.getFile() != null) {
					file = new java.io.File(fd.getDirectory() + fd.getFile());
				} else {
					frame.dispose();
					System.exit(0);
				}
			} else {
				file = files[0];
			}
		}

		try {
			player.loadWorld( file, new edu.cmu.cs.stage3.progress.ProgressObserver() {
				private int i = 0;
				private int n = 80;
				private int m_total = edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL;
				public void progressBegin(int total) {
					progressUpdateTotal(total);
				}
				public void progressUpdateTotal(int total) {
					m_total = total;
				}
				public void progressUpdate(int current, String description) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
					if (m_total == edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL) {
						System.out.print("?");
					} else {
						if (i < (int) ((current / (double) m_total) * n)) {
							System.out.print(".");
							i++;
						}
					}
				}
				public void progressEnd() {
				}
			} );
			player.startWorld();
			
		} catch( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}
	}
}
