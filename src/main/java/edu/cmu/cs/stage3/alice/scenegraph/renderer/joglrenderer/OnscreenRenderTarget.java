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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.nativewindow.ScalableSurface;

public class OnscreenRenderTarget extends RenderTarget implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget {
    private GLJPanel m_glCanvas;
    //private javax.media.opengl.GLPbuffer m_glPBuffer;
    private RenderContext m_renderContext;
    private PickContext m_pickContext;
	public OnscreenRenderTarget( Renderer renderer ) {
		super( renderer );		
	}
	
	public void markDirty() {
	    getAWTComponent().repaint();
	}
	
	public java.awt.Dimension getSize( java.awt.Dimension rv ) {
        java.awt.Component awtComponent = getAWTComponent();
		if( awtComponent != null ) {
			awtComponent.getSize( rv );
		} else {
			rv.width = 0;
            rv.height = 0;
		}
        return rv;
    }

	public java.awt.Component getAWTComponent() {
	    if( m_glCanvas == null ) {
	    	GLProfile profile = GLProfile.get(GLProfile.GL2);
			GLCapabilities glCaps = new GLCapabilities( profile );
			/*glCaps.setHardwareAccelerated( true );
	        glCaps.setRedBits( 8 );
	        glCaps.setBlueBits( 8 );
	        glCaps.setGreenBits( 8 );
	        glCaps.setAlphaBits( 8 );*/
	        //m_glCanvas = javax.media.opengl.GLDrawableFactory.getFactory().createGLCanvas( glCaps );
	        m_glCanvas = new GLJPanel( glCaps );
	       	m_renderContext = new RenderContext( this );
	        m_glCanvas.addGLEventListener( m_renderContext );   
            m_pickContext = new PickContext( this );
	        m_glCanvas.addGLEventListener( m_pickContext );
	        // disable HiDPI
			m_glCanvas.setSurfaceScale (new float[] { ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE } );
		
			/*m_glCanvas.setIgnoreRepaint(true);
			m_glCanvas.setAutoSwapBufferMode(false);*/			
	    }
		return m_glCanvas;
	}    
	
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
	    if( m_pickContext != null ) {
	        return m_pickContext.pick( m_glCanvas, x, y, isSubElementRequired, isOnlyFrontMostRequired );
	    }
		return null;
	    
	}	
}
