package edu.cmu.cs.stage3.alice.authoringtool.dialog;
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

/*
 * @(#)CapturePlayback.java	1.11	99/12/03
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;

public class SoundRecorder extends edu.cmu.cs.stage3.swing.ContentPane{
	private static byte[] s_wavHeader = new byte[ 44 ];
	static {
		System.arraycopy( "RIFF????WAVEfmt ".getBytes(), 0, s_wavHeader, 0, 16 ); //$NON-NLS-1$
		s_wavHeader[16] = 0x10;
		s_wavHeader[17] = 0;
		s_wavHeader[18] = 0;
		s_wavHeader[19] = 0;
		s_wavHeader[20] = 1;
		s_wavHeader[21] = 0;
		s_wavHeader[22] = 2;
		s_wavHeader[23] = 0;
		s_wavHeader[24] = (byte) 0x44; //(44100 & 0xFF);
		s_wavHeader[25] = (byte) 0xAC; //(44100 & 0xFF00)>>8;
		s_wavHeader[26] = 0;
		s_wavHeader[27] = 0;
		s_wavHeader[28] = (byte) 0x10; //((44100*4) & 0xFF);
		s_wavHeader[29] = (byte) 0xB1; //((44100*4) & 0xFF00)>>8;
		s_wavHeader[30] = (byte) 0x2; //((44100*4) & 0xFF0000)>>16;
		s_wavHeader[31] = 0;
		s_wavHeader[32] = 4;
		s_wavHeader[33] = 0;
		s_wavHeader[34] = 16;
		s_wavHeader[35] = 0;
		System.arraycopy( "data????".getBytes(), 0, s_wavHeader, 36, 8 ); //$NON-NLS-1$
	}

	Capture capture = new Capture();
	Playback playback = new Playback();
	AudioInputStream audioInputStream;
	
	final int bufSize = 16384;
	
	private static final int IDLE = 0;
	private static final int RECORDING = 1;
	private static final int PAUSE = 2;
	private static final int RESUME = 3;
	private static final int PLAYING = 4;

	private int m_state = IDLE;

	private edu.cmu.cs.stage3.alice.core.Element m_parentToCheckForNameValidity;

	private edu.cmu.cs.stage3.alice.core.Sound m_sound;
	private edu.cmu.cs.stage3.media.DataSource m_dataSource;
	
	private javax.swing.JTextField m_nameTextField;
	private javax.swing.JLabel m_durationLabel;
	private javax.swing.JButton m_recordButton;
	private javax.swing.JButton m_pauseButton;
	private javax.swing.JButton m_playButton;
	private javax.swing.JButton m_okButton;
	private javax.swing.JButton m_cancelButton;

    private javax.swing.Timer m_durationUpdateTimer = new javax.swing.Timer( 100, new java.awt.event.ActionListener(){
        public void actionPerformed( java.awt.event.ActionEvent e ) {
        	SoundRecorder.this.onDurationUpdate();
        }
    } );
    
	private long m_durationT0;
	private java.io.File soundDirectory;

    public SoundRecorder(java.io.File currentWorldLocation) {
    	soundDirectory = currentWorldLocation;
		m_durationLabel = new javax.swing.JLabel();
		onDurationUpdate();

		m_recordButton = new javax.swing.JButton( Messages.getString("SoundRecorder.2") ); //$NON-NLS-1$
		m_recordButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onRecord();
			}
		});

		m_pauseButton = new javax.swing.JButton( Messages.getString("SoundRecorder.3") ); //$NON-NLS-1$
		m_pauseButton.setEnabled(false);
		m_pauseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onPause();
			}
		});
		
		m_playButton = new javax.swing.JButton( Messages.getString("SoundRecorder.4") ); //$NON-NLS-1$
		m_playButton.setEnabled(false);
		m_playButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onPlay();
			}
		});

		m_nameTextField = new javax.swing.JTextField();
		m_nameTextField.setText( Messages.getString("SoundRecorder.5") ); //$NON-NLS-1$
		m_nameTextField.getDocument().addDocumentListener(
			new javax.swing.event.DocumentListener() {
				public void changedUpdate( javax.swing.event.DocumentEvent e ) { SoundRecorder.this.checkNameForValidity(); }
				public void insertUpdate( javax.swing.event.DocumentEvent e ) { SoundRecorder.this.checkNameForValidity(); }
				public void removeUpdate( javax.swing.event.DocumentEvent e ) { SoundRecorder.this.checkNameForValidity(); }
			}
		);

		m_okButton = new javax.swing.JButton( Messages.getString("SoundRecorder.6") ); //$NON-NLS-1$
		m_okButton.setPreferredSize(new Dimension(80, 26));
		m_okButton.setEnabled( false );
		m_okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onOK();
			}
		});

		m_cancelButton = new javax.swing.JButton( Messages.getString("SoundRecorder.7") ); //$NON-NLS-1$
		m_cancelButton.setPreferredSize(new Dimension(80, 26));
		m_cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onCancel();
			}
		});

		checkNameForValidity();

		java.awt.GridBagConstraints gbc;

		javax.swing.JPanel namePanel = new javax.swing.JPanel();
		namePanel.setLayout( new java.awt.GridBagLayout() );
		gbc = new java.awt.GridBagConstraints();
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		namePanel.add( new javax.swing.JLabel( Messages.getString("SoundRecorder.8") ), gbc ); //$NON-NLS-1$
		gbc.weightx = 1.0;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		namePanel.add( m_nameTextField, gbc );
		

		javax.swing.JPanel controlPanel = new javax.swing.JPanel();
		controlPanel.setLayout( new java.awt.GridBagLayout() );
		gbc = new java.awt.GridBagConstraints();
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		controlPanel.add( m_recordButton, gbc );
		controlPanel.add( m_pauseButton, gbc );
		controlPanel.add( m_playButton, gbc );


		javax.swing.JPanel okCancelPanel = new javax.swing.JPanel();
		okCancelPanel.setLayout( new java.awt.GridBagLayout() );
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		okCancelPanel.add( new javax.swing.JLabel(), gbc );
		gbc.weightx = 0.0;
		okCancelPanel.add( m_okButton, gbc );
		okCancelPanel.add( m_cancelButton, gbc );

		final int opad = 16;
		setLayout( new java.awt.GridBagLayout() );

		gbc = new java.awt.GridBagConstraints();
		gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets.top = opad;
		gbc.insets.left = opad;
		gbc.insets.right = opad;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;

		add( namePanel, gbc );

		gbc.insets.top = 0;

		javax.swing.JLabel spacer1 = new javax.swing.JLabel();
		spacer1.setPreferredSize( new java.awt.Dimension( 480, 16 ) );
		gbc.weighty = 1.0;
		add( spacer1, gbc );
		gbc.weighty = 0.0;

		gbc.anchor = java.awt.GridBagConstraints.WEST;
		add( m_durationLabel, gbc );
		add( controlPanel, gbc );

		javax.swing.JLabel spacer2 = new javax.swing.JLabel();
		spacer2.setPreferredSize( new java.awt.Dimension( 480, 16 ) );
		gbc.weighty = 1.0;
		add( spacer2, gbc );
		gbc.weighty = 0.0;

		gbc.insets.bottom = opad;

		gbc.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		add( okCancelPanel, gbc );
    }
    
	
	public void handleDispose() {
		onCancel();
		super.handleDispose();
	}
	
	public void addOKActionListener( java.awt.event.ActionListener l ) {
		m_okButton.addActionListener( l );
	}
	
	public void removeOKActionListener( java.awt.event.ActionListener l ) {
		m_okButton.removeActionListener( l );
	}
	
	public void addCancelActionListener( java.awt.event.ActionListener l ) {
		m_cancelButton.addActionListener( l );
	}
	
	public void removeCancelActionListener( java.awt.event.ActionListener l ) {
		m_cancelButton.removeActionListener( l );
	}

	public edu.cmu.cs.stage3.alice.core.Sound getSound() {
		return m_sound;
	}
	public void setSound( edu.cmu.cs.stage3.alice.core.Sound sound ) {
		m_sound = sound;
	}
	
	
	public edu.cmu.cs.stage3.alice.core.Element getParentToCheckForNameValidity() {
		return m_parentToCheckForNameValidity;
	}
	
	public void setParentToCheckForNameValidity( edu.cmu.cs.stage3.alice.core.Element parentToCheckForNameValidity ) {
		m_parentToCheckForNameValidity = parentToCheckForNameValidity;
		m_nameTextField.setText( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( Messages.getString("SoundRecorder.9"), m_parentToCheckForNameValidity ) ); //$NON-NLS-1$
	}
	
	private void checkNameForValidity() {
		java.awt.Color color = java.awt.Color.black;
		if( edu.cmu.cs.stage3.alice.core.Element.isPotentialNameValid( m_nameTextField.getText() ) ) {
			color = java.awt.Color.black;
		} else {
			color = java.awt.Color.red;
		}
		
		if( m_parentToCheckForNameValidity != null ) {
			if( m_parentToCheckForNameValidity.getChildNamedIgnoreCase( m_nameTextField.getText() ) != null ) {
				color = java.awt.Color.red;
			}
		}

		m_nameTextField.setForeground( color );
		updateOKButtonEnabled();
	}

	private void updateOKButtonEnabled() {
		m_okButton.setEnabled( audioInputStream != null && m_nameTextField.getForeground().equals( java.awt.Color.black ) );
	}

	private String formatTime( double seconds ) {
		if( Double.isNaN( seconds ) ) {
			return "?:??"; //$NON-NLS-1$
		} else {
			java.text.DecimalFormat decFormatter = new java.text.DecimalFormat( ".000" ); //$NON-NLS-1$
			java.text.DecimalFormat secMinFormatter1 = new java.text.DecimalFormat( "00" ); //$NON-NLS-1$
			java.text.DecimalFormat secMinFormatter2 = new java.text.DecimalFormat( "#0" ); //$NON-NLS-1$

			double secondsFloored = (int)Math.floor( seconds );
			double decimal = seconds - secondsFloored;
			double secs = secondsFloored % 60.0;
			double minutes = ((secondsFloored - secs)/60.0) % 60.0;
			double hours = (secondsFloored - 60.0*minutes - secs)/(60.0*60.0);

			String timeString = secMinFormatter1.format( secs ) + decFormatter.format( decimal );
			if( hours > 0.0 ) {
				timeString = secMinFormatter1.format( minutes ) + ":" + timeString; //$NON-NLS-1$
				timeString = secMinFormatter2.format( hours ) + ":" + timeString; //$NON-NLS-1$
			} else {
				timeString = secMinFormatter2.format( minutes ) + ":" + timeString; //$NON-NLS-1$
			}

			return timeString;
		}
	}
	
	double pauseTime = 0;
	long dt = 0;	
	double t = 0, totalPauseTime = 0;
	
	private void onDurationUpdate() {	
		switch( m_state ) {
		case RECORDING:
		case PLAYING:
			dt = System.currentTimeMillis() - m_durationT0;
			t = dt*0.001 - totalPauseTime;
			break;
		case PAUSE:
			pauseTime = System.currentTimeMillis() - m_durationT0 - dt;
			break;
		case RESUME:
			totalPauseTime += pauseTime * 0.001;
			 if (capture.thread != null) {
				 m_state = RECORDING;
			 } else {
				 if (playback.thread != null) { 
					 m_state = PLAYING;
				 }
			 }
		break;		
		}
		m_durationLabel.setText( Messages.getString("SoundRecorder.17") + formatTime( t ) ); //$NON-NLS-1$
	}

	private void onStop() {
		pauseTime = 0; totalPauseTime = 0;
		m_state = IDLE;
		m_playButton.setText( Messages.getString("SoundRecorder.18") ); //$NON-NLS-1$
		m_playButton.setEnabled( true );
		m_recordButton.setText( Messages.getString("SoundRecorder.19") ); //$NON-NLS-1$
		m_recordButton.setEnabled( true );
		m_pauseButton.setEnabled(false);
		m_durationUpdateTimer.stop();
		checkNameForValidity();
	}
	
    private void onRecord() {
   	 	if (m_recordButton.getText().startsWith(Messages.getString("SoundRecorder.20"))) {	 		 //$NON-NLS-1$
			capture.start();
			m_playButton.setEnabled(false);
			m_pauseButton.setEnabled(true);
			m_recordButton.setText(Messages.getString("SoundRecorder.21")); //$NON-NLS-1$
			m_state = RECORDING;
			m_durationT0 = System.currentTimeMillis();
			m_durationUpdateTimer.start();		
         } else {
             capture.stop();
         }
    }
    
    private void onPause() {
		if (m_pauseButton.getText().startsWith(Messages.getString("SoundRecorder.22"))) { //$NON-NLS-1$
		    if (capture.thread != null) {
		        capture.line.stop();
		        m_state = PAUSE;
		    } else {
		        if (playback.thread != null) {
		            playback.line.stop();
		            m_state = PAUSE;
		        }
		    }
		    m_pauseButton.setText(Messages.getString("SoundRecorder.23")); //$NON-NLS-1$
		} else {
		    if (capture.thread != null) {
		        capture.line.start();
		        m_state = RESUME;
		    } else {
		        if (playback.thread != null) {
		            playback.line.start();
		            m_state = RESUME;
		        }
		    }
		    m_pauseButton.setText(Messages.getString("SoundRecorder.24")); //$NON-NLS-1$
		}
    }
    
    private void onPlay() {
    	if (m_playButton.getText().startsWith(Messages.getString("SoundRecorder.25"))) { //$NON-NLS-1$
            playback.start();
            m_recordButton.setEnabled(false);
            m_pauseButton.setEnabled(true);
            m_playButton.setText(Messages.getString("SoundRecorder.26")); //$NON-NLS-1$
            m_state = PLAYING;
            m_durationT0 = System.currentTimeMillis();
			m_durationUpdateTimer.start();	
        } else {
            playback.stop();
       }
    }

	private void onCancel() {
		onStop();
		setSound( null );
    }

	private void onOK() {
		onStop();
		edu.cmu.cs.stage3.alice.core.Sound sound = new edu.cmu.cs.stage3.alice.core.Sound();
		sound.name.set( m_nameTextField.getText() );
		sound.dataSource.set( m_dataSource );
		setSound( sound );
    }

    /**
     * Write data to the OutputChannel.
     */
    public class Playback implements Runnable {

        SourceDataLine line;
        Thread thread;

        public void start() {
            thread = new Thread(this);
            thread.setName(Messages.getString("SoundRecorder.27")); //$NON-NLS-1$
            thread.start();
        }

        public void stop() {
            thread = null;
           
        }
        
        private void shutDown(String message) {
            if (message != null) {
            	AuthoringTool.showErrorDialog( message, null );
            }
            if (thread != null) {
                thread = null;
            } 
            onStop();
        }

        public void run() {
            // make sure we have something to play
            if (audioInputStream == null) {
                shutDown(Messages.getString("SoundRecorder.28")); //$NON-NLS-1$
                return;
            }
            // reset to the beginning of the stream
            try {
                audioInputStream.reset();
            } catch (Exception e) {
                shutDown(Messages.getString("SoundRecorder.29") + e); //$NON-NLS-1$
                return;
            }

            // get an AudioInputStream of the desired format for playback
            AudioFormat format = new AudioFormat (AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
            AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
                        
            if (playbackInputStream == null) {
                shutDown(Messages.getString("SoundRecorder.30") + audioInputStream + Messages.getString("SoundRecorder.31") + format); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            // define the required attributes for our line, 
            // and make sure a compatible line is supported.
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, 
                format);
            if (!AudioSystem.isLineSupported(info)) {
                shutDown(Messages.getString("SoundRecorder.32") + info + Messages.getString("SoundRecorder.33")); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            // get and open the source data line for playback.
            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format, bufSize);
            } catch (LineUnavailableException ex) { 
                shutDown(Messages.getString("SoundRecorder.34") + ex); //$NON-NLS-1$
                return;
            }

            // play back the captured audio data
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead = 0;

            // start the source data line
            line.start();

            while (thread != null) {
                try {
                    if ((numBytesRead = playbackInputStream.read(data)) == -1) {
                        break;
                    }
                    int numBytesRemaining = numBytesRead;
                    while (numBytesRemaining > 0 ) {
                        numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                    }
                } catch (Exception e) {
                    shutDown(Messages.getString("SoundRecorder.35") + e); //$NON-NLS-1$
                    break;
                }
            }
            // we reached the end of the stream.  let the data play out, then
            // stop and close the line.
            if (thread != null) {
                line.drain();
            }
            line.stop();
            line.close();
            line = null;
            shutDown(null);
        }
    } // End class Playback
    
    /** 
     * Reads data from the input channel and writes to the output stream
     */
	   class Capture implements Runnable {

	        TargetDataLine line;
	        Thread thread;

	        public void start() {
	            thread = new Thread(this);
	            thread.setName(Messages.getString("SoundRecorder.36")); //$NON-NLS-1$
	            thread.start();
	        }

	        public void stop() {
	            thread = null;
	        }
	        
	        private void shutDown(String message) {
	            if (message != null) {
	            	AuthoringTool.showErrorDialog( message, null );
	            }
	            if (thread != null) {
	                thread = null;
	            } 
	            onStop();
	        }

	        public void run() {

	            audioInputStream = null;
	            
	            // define the required attributes for our line, 
	            // and make sure a compatible line is supported.
	            AudioFormat format = new AudioFormat (AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
	            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	                        
	            if (!AudioSystem.isLineSupported(info)) {
	                shutDown(Messages.getString("SoundRecorder.37") + info + Messages.getString("SoundRecorder.38")); //$NON-NLS-1$ //$NON-NLS-2$
	                return;
	            }

	            // get and open the target data line for capture.
	            try {
	                line = (TargetDataLine) AudioSystem.getLine(info);
	                line.open(format, line.getBufferSize());
	            } catch (LineUnavailableException ex) { 
	                shutDown(Messages.getString("SoundRecorder.39") + ex); //$NON-NLS-1$
	                return;
	            } catch (SecurityException ex) { 
	                shutDown(ex.toString());	       
	                return;
	            } catch (Exception ex) { 
	                shutDown(ex.toString());
	                return;
	            }

	            // play back the captured audio data
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            int frameSizeInBytes = format.getFrameSize();
	            int bufferLengthInFrames = line.getBufferSize() / 8;
	            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
	            byte[] data = new byte[bufferLengthInBytes];
	            int numBytesRead;
	            
	            line.start();

	            while (thread != null) {
	                if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
	                    break;
	                }
	                out.write(data, 0, numBytesRead);
	            }

	            // we reached the end of the stream.  stop and close the line.
	            line.stop();
	            line.close();
	            line = null;

	            // stop and close the output stream
	            try {
	                out.flush();
	                out.close();
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }

	            // load bytes into the audio input stream for playback
	            byte audioBytes[] = out.toByteArray();
	            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
	            audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

		        try {
		            audioInputStream.reset();
		        } catch (Exception e) { 		     
		            return;
		        }
		        
		        File file = new File(soundDirectory + "/" + m_nameTextField.getText() + ".wav");		//TODO: make it work for MAC //$NON-NLS-1$ //$NON-NLS-2$
		        
		        try {
		            if (AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file) == -1) {
		                throw new IOException(Messages.getString("SoundRecorder.42")); //$NON-NLS-1$
		            }
		        } catch (Exception ex) {  }
		
            	try {
					m_dataSource = edu.cmu.cs.stage3.media.Manager.createDataSource( file );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	m_dataSource.waitForRealizedPlayerCount( 1, 0 );
            	
	    		shutDown(null);
	        }
	    } // End class Capture

}