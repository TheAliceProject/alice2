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

import com.sun.opengl.util.GLUT;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;

public abstract class RenderTarget extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderTarget {
	protected RenderTarget( Renderer renderer ) {
		super( renderer );
	}
	private RenderContext m_renderContextForGetOffscreenGraphics = null;
	protected void performClearAndRenderOffscreen( RenderContext context ) {
	    commitAnyPendingChanges();
	    
	    //todo: 
	    //note: clear hasn't really happened
	    onClear();
	    
	    context.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); 
		context.gl.glEnable(GL.GL_BLEND);
		context.gl.glEnable(GL.GL_ALPHA_TEST);
		context.gl.glAlphaFunc(GL.GL_GREATER, 0);
		    
	    edu.cmu.cs.stage3.alice.scenegraph.Camera[] cameras = getCameras();
	    for( int i=0; i<cameras.length; i++ ) {
	        CameraProxy cameraProxyI = (CameraProxy)getProxyFor( cameras[ i ] );
	        cameraProxyI.performClearAndRenderOffscreen( context ); 
	    }
	    try {
	        m_renderContextForGetOffscreenGraphics = context;
	        onRender();
	    } finally {
	        m_renderContextForGetOffscreenGraphics = null;
	    }
	    context.gl.glFlush();
	}
	
    private java.nio.IntBuffer m_pickBuffer;
    private java.nio.IntBuffer m_viewportBuffer;
    
    public PickInfo performPick( PickContext context, PickParameters pickParameters ) {
        int x = pickParameters.getX();
        int y = pickParameters.getY();
	    edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = getCameraAtPixel( x, y );
//	    System.err.println( sgCamera );
	    if( sgCamera != null ) {

        	final int CAPACITY = 256;
        	if( m_pickBuffer == null ) {
            	m_pickBuffer = java.nio.ByteBuffer.allocateDirect( CAPACITY*4 ).order(java.nio.ByteOrder.nativeOrder()).asIntBuffer();
        	} else {
        	    m_pickBuffer.rewind();
        	}
        	context.gl.glSelectBuffer( CAPACITY, m_pickBuffer );

        	context.gl.glRenderMode( GL.GL_SELECT );
        	context.gl.glInitNames();

        	int width = context.getWidth();
        	int height = context.getHeight();
        	//todo: use actual viewport
		    CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
        	java.awt.Rectangle viewport = cameraProxy.getActualViewport( width, height );
        	int[] vp = { viewport.x, viewport.y, viewport.width, viewport.height };
		    context.gl.glViewport( viewport.x, viewport.y, viewport.width, viewport.height );

		    context.gl.glMatrixMode( GL.GL_PROJECTION );
		    context.gl.glLoadIdentity();
        	
		    if( m_viewportBuffer == null ) {
        		m_viewportBuffer = java.nio.IntBuffer.allocate( 4 );
        	} else {
        		m_viewportBuffer.rewind();
        	}
        	m_viewportBuffer.put( viewport.x );
        	m_viewportBuffer.put( viewport.y );
        	m_viewportBuffer.put( viewport.width );
        	m_viewportBuffer.put( viewport.height );
    		m_viewportBuffer.rewind();
		    context.gl.glMatrixMode( GL.GL_PROJECTION );
		    context.gl.glLoadIdentity();
		    context.glu.gluPickMatrix( x, height-y, 1, 1, m_viewportBuffer );

	        cameraProxy.performPick( context, pickParameters );
		    
		    context.gl.glFlush();
		}
	    return new PickInfo( context, m_pickBuffer, sgCamera );
    }
	
	public void commitAnyPendingChanges() {
	    ((Renderer)getRenderer()).commitAnyPendingChanges();
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
	    java.awt.Dimension size = getSize();
	    SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy = (SymmetricPerspectiveCameraProxy)getProxyFor( sgSymmetricPerspectiveCamera );
	    return symmetricPerspectiveCameraProxy.getActualHorizontalViewingAngle( size.width, size.height );
	}
	public double getActualVerticalViewingAngle( edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera ) {
	    java.awt.Dimension size = getSize();
	    SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy = (SymmetricPerspectiveCameraProxy)getProxyFor( sgSymmetricPerspectiveCamera );
	    return symmetricPerspectiveCameraProxy.getActualVerticalViewingAngle( size.width, size.height );
	}
	public java.awt.Rectangle getActualViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
	    java.awt.Dimension size = getSize();
	    CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
		return cameraProxy.getActualViewport( size.width, size.height );
	}
	public java.awt.Rectangle getViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
	    CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
	    return cameraProxy.getViewport();
	}
	public void setViewport( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, java.awt.Rectangle viewport ) {
	    CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
	    cameraProxy.setViewport( viewport );
	}
	public boolean isLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
	    CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
	    return cameraProxy.isLetterboxedAsOpposedToDistorted();
    }
	public void setIsLetterboxedAsOpposedToDistorted( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, boolean isLetterboxedAsOpposedToDistorted ) {
	    CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
	    cameraProxy.setIsLetterboxedAsOpposedToDistorted( isLetterboxedAsOpposedToDistorted );
    }
	javax.media.opengl.GLPbuffer m_glPBuffer = null;
	public void createGLBuffer(int width, int height){
		GLDrawableFactory fac = GLDrawableFactory.getFactory();
		if ( fac.canCreateGLPbuffer() ) {
			GLCapabilities glCap = new GLCapabilities();
			glCap.setDoubleBuffered(false);
			glCap.setRedBits( 8 );
			glCap.setBlueBits( 8 );
			glCap.setGreenBits( 8 );
			glCap.setAlphaBits( 8 );
			m_glPBuffer = fac.createGLPbuffer(glCap, null, width, height, null);
		}
	}
	public void clearAndRenderOffscreen() {
		RenderContext m_renderContext = new RenderContext( this );
		if (m_glPBuffer != null){
			javax.media.opengl.GLContext context =  m_glPBuffer.createContext(null); 
			context.makeCurrent();
			context.getGL().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  
			context.getGL().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			java.awt.Dimension d = getSize();
			int width = d.width, height = d.height;
			m_renderContext.m_height =  height; // m_glPBuffer.getHeight();
			m_renderContext.m_width = width; // m_glPBuffer.getWidth();
			m_glPBuffer.addGLEventListener( m_renderContext );	
		    m_glPBuffer.display();
		    
		}
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

		java.awt.Dimension d = getSize();
		int width = d.width, height = d.height;
		if ( m_glPBuffer == null || m_glPBuffer.getWidth() != width || m_glPBuffer.getHeight() != height){
			createGLBuffer(width, height);
			clearAndRenderOffscreen();
		}
		javax.media.opengl.GLContext context =  m_glPBuffer.createContext(null);
		context.makeCurrent();
		java.awt.image.BufferedImage image =
                new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		image = com.sun.opengl.util.Screenshot.readToBufferedImage(width, height, true);
		context.release();
		context.destroy();
		return image;
	}	
	public java.awt.Graphics getOffscreenGraphics() {
		return new Graphics( m_renderContextForGetOffscreenGraphics );
	}
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
	    //todo
	}
	public void setSilhouetteThickness( double silhouetteThickness ) {
        //todo
    }
	public double getSilhouetteThickness() {
        //todo
        return 0;
    }
	private double[] getActualNearPlane( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera, int width, int height ) {
	    CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
	    double[] ret = new double[ 4 ];
	    return cameraProxy.getActualNearPlane( ret, width, height );
	}
	private edu.cmu.cs.stage3.alice.scenegraph.Camera getCameraAtPixel( int x, int y ) { 
	    edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = getCameras();
	    for( int i=(sgCameras.length-1); i>=0; i-- ) {
	        edu.cmu.cs.stage3.alice.scenegraph.Camera sgCameraI = sgCameras[ i ];
	        java.awt.Rectangle viewportI = getActualViewport( sgCameraI );
	        if( viewportI.contains( x, y ) ) {
	            return sgCameraI;
	        }
	    }
	    return null;
	}
	private static boolean isNaN( double[] array ) {
	    for( int i=0; i<array.length; i++ ) {
	        if( Double.isNaN( array[ i ] ) ) {
	            return true;
	        }
	    }
	    return false;
	}
//	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
//	    edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = getCameraAtPixel( x, y );
//	    if( sgCamera != null ) {
//	        java.awt.Rectangle actualViewport = getActualViewport( sgCamera );
//	        x -= actualViewport.getX();
//	        y -= actualViewport.getY();
//	        edu.cmu.cs.stage3.math.Ray ray = getRayAtPixel( sgCamera, x, y );
//			double nearClippingPlaneDistance = sgCamera.getNearClippingPlaneDistance();
//			double farClippingPlaneDistance = sgCamera.getFarClippingPlaneDistance();
//			javax.vecmath.Vector3d position = ray.getPoint( nearClippingPlaneDistance );
//			double[] nearPlane = getActualNearPlane( sgCamera, actualViewport.width, actualViewport.height );
//			if( isNaN( nearPlane ) ) {
//			    return null;
//			} else {
//				double planeWidth = nearPlane[ 2 ] - nearPlane[ 0 ];
//				double planeHeight = nearPlane[ 3 ] - nearPlane[ 1 ];
//				double pixelHalfWidth = ( planeWidth / actualViewport.getWidth() ) / 2;
//				double pixelHalfHeight = ( planeHeight / actualViewport.getHeight() ) / 2;
//				double planeMinX = position.x - pixelHalfWidth;
//				double planeMinY = position.y - pixelHalfHeight;
//				double planeMaxX = position.x + pixelHalfWidth;
//				double planeMaxY = position.y + pixelHalfHeight;
//			    return getRenderer().pick( sgCamera, ray.getDirection(), planeMinX, planeMinY, planeMaxX, planeMaxY, nearClippingPlaneDistance, farClippingPlaneDistance, isSubElementRequired, isOnlyFrontMostRequired );
//			}
//	    } else {
//	        return null;
//	    }
//	}
	private GLUT glut_ = new GLUT();

	private int displayList;
	
	public class GLEListener implements GLEventListener {
		/**
		 * Executes the drawing.
		 * @param drawable GLAutoDrawable object.
		 */
		public void display ( GLAutoDrawable drawable ) {

			GL gl = drawable.getGL();										// get the OpenGL 2 graphics context
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);	// clear color and depth buffers
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();											// reset the model-view matrix
			
			gl.glTranslatef( (float)(Math.random() - 0.5), (float)(Math.random() - 0.5), 0);
			double d = 0.9+0.2*Math.random();
			gl.glScaled(d, d, d);
			float[] c = new float[] { (float)Math.random(),  (float)Math.random(),  (float)Math.random(), 1 };
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK,GL.GL_DIFFUSE,c,0);
			gl.glCallList(displayList);//use the display list to do the drawing
		}

		public void displayChanged ( GLAutoDrawable drawable, 
				boolean modeChanged, boolean deviceChanged ) {
		}

		/**
		 * Initializes the GLJPanel for drawing.
		 * @param drawable GLAutoDrawable object.
		 */
		public void init ( GLAutoDrawable drawable ) {

			GL gl = drawable.getGL();

			/* set up depth-buffering */
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LEQUAL);

			/* set up lights */
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();

			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_LIGHT0);

			float ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
			float diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			float specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			float position[] = { 0.0f, 10.0f, -15.0f, 1.0f };

			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, specular, 0);
			
			gl.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
			
			/*make a new display list and put a sphere in it*/
			displayList = gl.glGenLists(1);
			gl.glNewList(displayList, GL.GL_COMPILE);
			glut_.glutSolidSphere(0.3,16,8);
			gl.glEndList();
		}

		public void reshape ( GLAutoDrawable drawable, 
				int x, int y, int width, int height ) {

			GL gl = drawable.getGL();
			/* define the viewport transformation */
			gl.glViewport(x,y,width,height);
		}
	}
	
	
}
