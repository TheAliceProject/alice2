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

package edu.cmu.cs.stage3.awt;

import java.awt.event.InputEvent;

// Redoing AWTUtilities so its cross platform compatible.
public class AWTUtilities {
    private static boolean s_successfullyLoadedLibrary;
    static {
        try {
            System.loadLibrary( "jni_awtutilities" );
            s_successfullyLoadedLibrary = true;
		} catch( Throwable t ) {
            s_successfullyLoadedLibrary = false;
        }
    }
  
 	private static native boolean isGetCursorLocationSupportedNative();
 	public static boolean isGetCursorLocationSupported() {
		if( s_successfullyLoadedLibrary ) {
			return isGetCursorLocationSupportedNative(); //return 1;
		}
		return true;
	}

	private static native boolean isSetCursorLocationSupportedNative();
	public static boolean isSetCursorLocationSupported() {
		if( s_successfullyLoadedLibrary ) {
			return isSetCursorLocationSupportedNative(); //return 1;
		}
		return false;
	}

	private static native boolean isIsKeyPressedSupportedNative();
	public static boolean isIsKeyPressedSupported() {
		if( s_successfullyLoadedLibrary ) {
			return isIsKeyPressedSupportedNative(); //return 1;
		}
		return true;
	}

	private static native boolean isGetModifiersSupportedNative();
	public static boolean isGetModifiersSupported() {
		if( s_successfullyLoadedLibrary ) {
			return isGetModifiersSupportedNative(); //return 1;
		}
		return true;
	}
	
	private static native boolean isPumpMessageQueueSupportedNative();
	public static boolean isPumpMessageQueueSupported() {
		if( s_successfullyLoadedLibrary ) {
			return isPumpMessageQueueSupportedNative();
		}
		return false;
	}
		
	private static native void pumpMessageQueueNative();
	public static void pumpMessageQueue() {
		if( s_successfullyLoadedLibrary ) {
			pumpMessageQueueNative();
		} else {
			//pass
		}
	}
    
	private static native void getCursorLocationNative( java.awt.Point p );
	public static java.awt.Point getCursorLocation() {
		if( s_successfullyLoadedLibrary ) {
			java.awt.Point p = new java.awt.Point();
			getCursorLocationNative( p );
			return p;
		}
		java.awt.Point p = new java.awt.Point();
		try {
			p = java.awt.MouseInfo.getPointerInfo().getLocation().getLocation();
		} catch (Exception e) {
		}
		return p;
	}

	private static native void setCursorLocationNative( int x, int y );
	public static void setCursorLocation( int x, int y ) {
		if( s_successfullyLoadedLibrary ) {
			setCursorLocationNative( x, y );
		} else {
			moveMouse(new java.awt.Point(x,y));
		}
	}
	
	private static void moveMouse(java.awt.Point p) {
		java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
		java.awt.GraphicsDevice[] gs = ge.getScreenDevices();

	    // Search the devices for the one that draws the specified point.
	    for (java.awt.GraphicsDevice device: gs) { 
	    	java.awt.GraphicsConfiguration[] configurations =
	            device.getConfigurations();
	        for (java.awt.GraphicsConfiguration config: configurations) {
	        	java.awt.Rectangle bounds = config.getBounds();
	            if(bounds.contains(p)) {
	                // Set point to screen coordinates.
	            	java.awt.Point b = bounds.getLocation(); 
	            	//java.awt.Point s = new java.awt.Point(p.x - b.x, p.y - b.y);
	            	java.awt.Point s = new java.awt.Point(p.x, p.y);
	            	
	                try {
	                	java.awt.Robot r = new java.awt.Robot(device);
	                    r.mouseMove(s.x, s.y);
	                } catch (java.awt.AWTException e) {
	                    e.printStackTrace();
	                }

	                return;
	            }
	        }
	    }
	    // Couldn't move to the point, it may be off screen.
	    return;
	}
	
	public static void setCursorLocation( java.awt.Point p ) {
		setCursorLocation( p.x, p.y );
	}
	
	private static native boolean isCursorShowingNative();
	public static boolean isCursorShowing() { //not called anywhere
		if( s_successfullyLoadedLibrary ) {
			return isCursorShowingNative();
		}
		return true;
	}
	
	private static native void setIsCursorShowingNative( boolean isCursorShowing );
	public static void setIsCursorShowing( boolean isCursorShowing ) {
		if( s_successfullyLoadedLibrary ) {
			setIsCursorShowingNative( isCursorShowing );
		} else {
			//pass
		}
	}

	private static native boolean isIsCursorShowingSupportedNative();
	public static boolean isIsCursorShowingSupported() {
		if( s_successfullyLoadedLibrary ) {
			return isIsCursorShowingSupportedNative(); //return 1;
		}
		return false;
	}
	
	private static native boolean isSetIsCursorShowingSupportedNative();
	public static boolean isSetIsCursorShowingSupported() {
		if( s_successfullyLoadedLibrary ) {
			return isSetIsCursorShowingSupportedNative(); //return 1;
		}
		return false;
	}
	
	private static native boolean isKeyPressedNative( int keyCode );
	public static boolean isKeyPressed( int keyCode ) {
		if( s_successfullyLoadedLibrary ) {
			return isKeyPressedNative( keyCode );
		}
		if (keyCode == java.awt.event.KeyEvent.VK_CONTROL) {
			return (modifier & 8) != 0;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_SHIFT) {
			return (modifier & 1) != 0;
		}
		return false;
	}
	
	private static native int isKeyNative( int keyCode );
	public static int isKey( int keyCode ) {
		if( s_successfullyLoadedLibrary ) {			
			return isKeyNative( keyCode );
		}
		return 0;
	}
	
    public static int modifier;
	private static native int getModifiersNative();
	public static int getModifiers() {
		if( s_successfullyLoadedLibrary ) {
			return getModifiersNative();
		}
		return modifier;	
	}

	public static boolean mouseListenersAreSupported() {
		return isGetModifiersSupported() && isGetCursorLocationSupported();
	}

	public static boolean mouseMotionListenersAreSupported() {
		return isGetModifiersSupported() && isGetCursorLocationSupported();
	}

	private static java.util.Vector s_mouseListeners = new java.util.Vector();
	private static java.util.Vector s_mouseMotionListeners = new java.util.Vector();
	public static void addMouseListener( java.awt.event.MouseListener mouseListener ) {
		s_mouseListeners.addElement( mouseListener );
	}
	public static void removeMouseListener( java.awt.event.MouseListener mouseListener ) {
		s_mouseListeners.removeElement( mouseListener );
	}
	public static void addMouseMotionListener( java.awt.event.MouseMotionListener mouseMotionListener ) {
		s_mouseMotionListeners.addElement( mouseMotionListener );
	}
	public static void removeMouseMotionListener( java.awt.event.MouseMotionListener mouseMotionListener ) {
		s_mouseMotionListeners.removeElement( mouseMotionListener );
	}

	private static java.awt.Component s_source = new java.awt.Label( "edu.cmu.cs.stage3.io.toolkit.Toolkit" );

    private static int s_prevModifiers = 0;
    private static int s_clickCount = 0;
    private static boolean isButton1Pressed( int modifiers ) {
    	return (modifiers & InputEvent.BUTTON1_MASK)==InputEvent.BUTTON1_MASK;
    }
    private static boolean isButton2Pressed( int modifiers ) {
        return (modifiers & InputEvent.BUTTON2_MASK)==InputEvent.BUTTON2_MASK;
    }
    private static boolean isButton3Pressed( int modifiers ) {
        return (modifiers & InputEvent.BUTTON3_MASK)==InputEvent.BUTTON3_MASK;
    }

    private static java.awt.Point s_prevCursorPos = new java.awt.Point();
    private static java.awt.Point s_currCursorPos = new java.awt.Point();
   
    public static void fireMouseAndMouseMotionListenersIfNecessary() {
    	if( mouseListenersAreSupported() ) {
			int id = 0;
			int currModifiers = getModifiers();
			boolean drag = false;
			if( isButton1Pressed( s_prevModifiers ) ) {
				if( isButton1Pressed( currModifiers ) ) {
					drag = true;
				} else {
					id = java.awt.event.MouseEvent.MOUSE_RELEASED;
				}
			} else {
				if( isButton1Pressed( currModifiers ) ) {
					id = java.awt.event.MouseEvent.MOUSE_PRESSED;
					drag = true;
				} else {
				}
			}
			if( isButton2Pressed( s_prevModifiers ) ) {
				if( isButton2Pressed( currModifiers ) ) {
					drag = true;
				} else {
					id = java.awt.event.MouseEvent.MOUSE_RELEASED;
				}
			} else {
				if( isButton2Pressed( currModifiers ) ) {
					id = java.awt.event.MouseEvent.MOUSE_PRESSED;
					drag = true;
				} else {
				}
			}
			if( isButton3Pressed( s_prevModifiers ) ) {
				if( isButton3Pressed( currModifiers ) ) {
					drag = true;
				} else {
					id = java.awt.event.MouseEvent.MOUSE_RELEASED;
				}
			} else {
				if( isButton3Pressed( currModifiers ) ) {
					id = java.awt.event.MouseEvent.MOUSE_PRESSED;
					drag = true;
				} else {
				}
			}
			long when = System.currentTimeMillis();
			boolean isPopupTrigger = false;

			s_currCursorPos = getCursorLocation();

			if( id!=0 ) {
				if( s_mouseListeners.size()>0 ) {
					java.awt.event.MouseEvent mouseEvent = new java.awt.event.MouseEvent( s_source, id, when, currModifiers, s_currCursorPos.x, s_currCursorPos.y, s_clickCount, isPopupTrigger );
					for( int i=0; i<s_mouseListeners.size(); i++ ) {
						java.awt.event.MouseListener mouseListener = (java.awt.event.MouseListener)s_mouseListeners.elementAt( i );
						switch( id ) {
						case java.awt.event.MouseEvent.MOUSE_CLICKED:
							mouseListener.mouseClicked( mouseEvent );
							break;
						case java.awt.event.MouseEvent.MOUSE_ENTERED:
							mouseListener.mouseEntered( mouseEvent );
							break;
						case java.awt.event.MouseEvent.MOUSE_EXITED:
							mouseListener.mouseExited( mouseEvent );
							break;
						case java.awt.event.MouseEvent.MOUSE_PRESSED:
							mouseListener.mousePressed( mouseEvent );
							break;
						case java.awt.event.MouseEvent.MOUSE_RELEASED:
							mouseListener.mouseReleased( mouseEvent );
							break;
						}
					}
				}
			} else {
				if( s_currCursorPos.x == s_prevCursorPos.x && s_currCursorPos.y == s_prevCursorPos.y ) {
					//pass
				} else {
					if( s_mouseMotionListeners.size()>0 ) {
						if( drag ) {
							id = java.awt.event.MouseEvent.MOUSE_DRAGGED;
						} else {
							id = java.awt.event.MouseEvent.MOUSE_MOVED;
						}
						java.awt.event.MouseEvent mouseEvent = new java.awt.event.MouseEvent( s_source, id, when, currModifiers, s_currCursorPos.x, s_currCursorPos.y, s_clickCount, isPopupTrigger );
						for( int i=0; i<s_mouseMotionListeners.size(); i++ ) {
							java.awt.event.MouseMotionListener mouseMotionListener = (java.awt.event.MouseMotionListener)s_mouseMotionListeners.elementAt( i );
							switch( id ) {
							case java.awt.event.MouseEvent.MOUSE_MOVED:
								mouseMotionListener.mouseMoved( mouseEvent );
								break;
							case java.awt.event.MouseEvent.MOUSE_DRAGGED:
								mouseMotionListener.mouseDragged( mouseEvent );
								break;
							}
						}
					}
				}
			}
			s_prevCursorPos.x = s_currCursorPos.x;
			s_prevCursorPos.y = s_currCursorPos.y;
			s_prevModifiers = currModifiers;
    	} 
    }
}
