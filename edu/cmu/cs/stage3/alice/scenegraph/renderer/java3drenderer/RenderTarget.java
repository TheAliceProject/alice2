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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer;

public abstract class RenderTarget extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderTarget {
	RenderTarget( Renderer renderer ) {
		super( renderer );
	}
	public void commitAnyPendingChanges() {
	}

	public javax.vecmath.Matrix4d getProjectionMatrix( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
        //todo
		return sgCamera.getProjection();
	}
	public double[] getActualPlane( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgOrthographicCamera ) {
        //todo
		return sgOrthographicCamera.getPlane();
	}
	public double[] getActualPlane( edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera sgPerspectiveCamera ) {
        //todo
		return sgPerspectiveCamera.getPlane();
	}
	public double getActualHorizontalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera ) {
        //todo
		return sgSymmetricPerspectiveCamera.getHorizontalViewingAngle();
	}
	public double getActualVerticalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera ) {
        //todo
		return sgSymmetricPerspectiveCamera.getVerticalViewingAngle();
	}
	public java.awt.Rectangle getActualViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
        //todo
		return null;
	}
	public java.awt.Rectangle getViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
        //todo
		return null;
	}
	public void setViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, java.awt.Rectangle viewport ) {
        //todo
	}
	public boolean isLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
        //todo
        return true;
    }
	public void setIsLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, boolean isLetterboxedAsOpposedToDistorted ) {
        //todo
    }

	public void clearAndRenderOffscreen() {
	}
	public boolean rendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera ) {
        //todo
        return false;
    }
	public void setRendersOnEdgeTrianglesAsLines( edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthographicCamera, boolean rendersOnEdgeTrianglesAsLines ) {
        //todo
        //if( rendersOnEdgeTrianglesAsLines ) {
        //    throw new RuntimeException( "not supported" );
        //}
    }

	public java.awt.Image getOffscreenImage() {
        return null;
	}
	public abstract java.awt.Graphics getOffscreenGraphics();
	public java.awt.Graphics getGraphics( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap ) {
        return null;
	}
    public java.awt.Image getZBufferImage() {
        return null;
    }
	public java.awt.Image getImage( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap ) {
		return null;
	}

	public void copyOffscreenImageToTextureMap( edu.cmu.cs.stage3.alice.scenegraph.TextureMap textureMap ) {
	}

	public void setSilhouetteThickness( double silhouetteThickness ) {
        //todo
    }
	public double getSilhouetteThickness() {
        //todo
        return 0;
    }
}