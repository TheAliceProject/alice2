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

import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.gl2.GLUT;

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
	    
	    context.gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); 
		context.gl2.glEnable(GL2.GL_BLEND);
		context.gl2.glEnable(GL2.GL_ALPHA_TEST);
		context.gl2.glAlphaFunc(GL2.GL_GREATER, 0);
		    
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
	    context.gl2.glFlush();
	}
	
    private java.nio.IntBuffer m_pickBuffer;
    private java.nio.IntBuffer m_viewportBuffer;
    
    private float[] scale = new float[2];
    
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
        	context.gl2.glSelectBuffer( CAPACITY, m_pickBuffer );

        	context.gl2.glRenderMode( GL2.GL_SELECT );
        	context.gl2.glInitNames();

        	int width = context.getWidth();
        	int height = context.getHeight();
        	//todo: use actual viewport
		    CameraProxy cameraProxy = (CameraProxy)getProxyFor( sgCamera );
        	java.awt.Rectangle viewport = cameraProxy.getActualViewport( width, height );
        	int[] vp = { viewport.x, viewport.y, viewport.width, viewport.height };
		    context.gl2.glViewport( viewport.x, viewport.y, viewport.width, viewport.height );

		    context.gl2.glMatrixMode( GL2.GL_PROJECTION );
		    context.gl2.glLoadIdentity();
        	
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
		    context.gl2.glMatrixMode( GL2.GL_PROJECTION );
		    context.gl2.glLoadIdentity();
		    context.glu.gluPickMatrix( x, height-y, 1, 1, m_viewportBuffer );

	        cameraProxy.performPick( context, pickParameters );
		    
		    context.gl2.glFlush();
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
	GLAutoDrawable drawable = null;
	public void createDrawable(int width, int height){
		//DEBUG GLProfile glp = GLProfile.getDefault();
		GLProfile glp = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(glp);
        //caps.setDoubleBuffered(true); 
        //caps.setHardwareAccelerated(true);
        //caps.setOnscreen(false);
        //caps.setAlphaBits(1);
        GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);

        drawable = factory.createOffscreenAutoDrawable(null,caps,null,width,height);
        drawable.setRealized(true);
	}
	
	public void clearAndRenderOffscreen() {
		java.awt.Dimension d = getSize();
		int width = d.width, height = d.height;
		if (drawable == null){
			createDrawable(width, height);
		} 

		RenderContext m_renderContext = new RenderContext( this );
		m_renderContext.m_height =  height;
		m_renderContext.m_width = width;

		drawable.addGLEventListener( m_renderContext );	
		drawable.display();

	    drawable.getContext().makeCurrent(); 	
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
		if ( drawable == null || drawable.getSurfaceWidth() != width || drawable.getSurfaceHeight() != height){
			createDrawable(width, height);
			clearAndRenderOffscreen();
		}
		
		BufferedImage image = new AWTGLReadBufferUtil(drawable.getGLProfile(), true).readPixelsToBufferedImage(drawable.getGL().getGL2(), 0, 0, width, height, true); 
		
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

			GL2 gl2 = drawable.getGL().getGL2();								// get the OpenGL 2 graphics context
			gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);		// clear color and depth buffers
			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			gl2.glLoadIdentity();												// reset the model-view matrix
			
			gl2.glTranslatef( (float)(Math.random() - 0.5), (float)(Math.random() - 0.5), 0);
			double d = 0.9+0.2*Math.random();
			gl2.glScaled(d, d, d);
			float[] c = new float[] { (float)Math.random(),  (float)Math.random(),  (float)Math.random(), 1 };
			gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK,GL2.GL_DIFFUSE,c,0);
			gl2.glCallList(displayList);										//use the display list to do the drawing	
		}

		public void displayChanged ( GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged ) { }

		/**
		 * Initializes the GLJPanel for drawing.
		 * @param drawable GLAutoDrawable object.
		 */
		public void init ( GLAutoDrawable drawable ) {

			GL2 gl2 = drawable.getGL().getGL2();

			/* set up depth-buffering */
			gl2.glClearDepth(1.0);
			gl2.glDepthFunc(GL2.GL_LEQUAL);
			gl2.glEnable(GL2.GL_DEPTH_TEST);
			

			/* set up lights */
			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			gl2.glLoadIdentity();

			gl2.glEnable(GL2.GL_LIGHTING);
			gl2.glEnable(GL2.GL_LIGHT0);

			float ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
			float diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			float specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			float position[] = { 0.0f, 10.0f, -15.0f, 1.0f };

			gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
			gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
			gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
			gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);
			
			gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			
			/*make a new display list and put a sphere in it*/
			displayList = gl2.glGenLists(1);
			gl2.glNewList(displayList, GL2.GL_COMPILE);
			glut_.glutSolidSphere(0.3,16,8);
			gl2.glEndList();
		}

		public void reshape ( GLAutoDrawable drawable, int x, int y, int width, int height ) {
			GL2 gl2 = drawable.getGL().getGL2();
			gl2.glViewport(x, y, width, height);
		}

		public void dispose ( GLAutoDrawable arg0 ) {	}
	}
	
	
}
