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

import com.jogamp.opengl.GL2;

abstract class CameraProxy extends ComponentProxy {
    private BackgroundProxy m_backgroundProxy = null;
    private float m_near;
    private float m_far;

    private java.awt.Rectangle m_viewport = null;
    private boolean m_isLetterboxedAsOpposedToDistorted = true;

    protected abstract java.awt.Rectangle getActualLetterboxedViewport( int width, int height );
	public java.awt.Rectangle getActualViewport( int width, int height ) {
	    if( m_viewport != null ) {
	        return m_viewport;
	    } 

        if( m_isLetterboxedAsOpposedToDistorted ) {
            return getActualLetterboxedViewport( width, height );
        } 
        return new java.awt.Rectangle( 0, 0, width, height );
	}
	public java.awt.Rectangle getViewport() {
	    return m_viewport;
	}
	public void setViewport(java.awt.Rectangle viewport ) {
	    m_viewport = new java.awt.Rectangle( viewport );
	}
	public boolean isLetterboxedAsOpposedToDistorted() {
        return m_isLetterboxedAsOpposedToDistorted;
    }
	public void setIsLetterboxedAsOpposedToDistorted( boolean isLetterboxedAsOpposedToDistorted ) {
	    m_isLetterboxedAsOpposedToDistorted = isLetterboxedAsOpposedToDistorted;
    }

	protected abstract double[] getActualNearPlane( double[] ret, int width, int height, double near );
	public double[] getActualNearPlane( double[] ret, int width, int height ) {
	    return getActualNearPlane( ret, width, height, m_near );
	}
    
	public void setup( RenderContext context ) {
        //pass
    }
    protected abstract void projection( Context context, int width, int height, float near, float far );
	public void performClearAndRenderOffscreen( RenderContext context ) {
	    SceneProxy sceneProxy = getSceneProxy();
	    if( sceneProxy != null ) {
	        java.awt.Rectangle actualViewport = getActualViewport( context.getWidth(), context.getHeight() );
	        BackgroundProxy backgroundProxy;
	        if( m_backgroundProxy != null ) {
	            backgroundProxy = m_backgroundProxy;
	        } else {
	            backgroundProxy = sceneProxy.getBackgroundProxy();
	        }
            context.clear( backgroundProxy, actualViewport );

            context.gl2.glMatrixMode( GL2.GL_PROJECTION );
		    context.gl2.glLoadIdentity();
		    projection( context, actualViewport.width, actualViewport.height, m_near, m_far );
		    context.gl2.glMatrixMode( GL2.GL_MODELVIEW );
		    context.gl2.glLoadIdentity();
		    context.gl2.glLoadMatrixd( getInverseAbsoluteTransformationAsBuffer() );

		    sceneProxy.setup( context );
		    
		    //first color in opaque objects
		    context.setRenderOpaque();
		    sceneProxy.render( context );    
	    
		    context.gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); 
		    context.gl2.glEnable(GL2.GL_BLEND);
		    context.gl2.glEnable(GL2.GL_ALPHA_TEST);
		    context.gl2.glAlphaFunc(GL2.GL_GREATER, 0);
		    //context.gl.glClearColor(0, 0, 0, 0);

		    //next render transparent
		    context.setRenderTransparent();
			sceneProxy.render( context );

			context.gl2.glDisable( GL2.GL_BLEND );
	    }
	}
	public void performPick( PickContext context, PickParameters pickParameters ) {
	    SceneProxy sceneProxy = getSceneProxy();
	    if( sceneProxy != null ) {
        	int width = context.getWidth();
        	int height = context.getHeight();
		    projection( context, width, height, m_near, m_far );
		    context.gl2.glMatrixMode( GL2.GL_MODELVIEW );
		    context.gl2.glLoadIdentity();
		    context.gl2.glLoadMatrixd( getInverseAbsoluteTransformationAsBuffer() );
	        sceneProxy.pick( context, pickParameters );
	    }
	}
	
	public void render( RenderContext context ) {
	}
	
	public void pick( PickContext context, PickParameters pickParameters ) {
	}
	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Camera.NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY ) {
			m_near = ((Number)value).floatValue();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Camera.FAR_CLIPPING_PLANE_DISTANCE_PROPERTY ) {
			m_far = ((Number)value).floatValue();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Camera.BACKGROUND_PROPERTY ) {
		    m_backgroundProxy = (BackgroundProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Background)value );
		} else {
			super.changed( property, value );
		}
	}
}
