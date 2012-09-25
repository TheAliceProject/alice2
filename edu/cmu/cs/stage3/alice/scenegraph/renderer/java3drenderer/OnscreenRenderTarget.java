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

public class OnscreenRenderTarget extends RenderTarget implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget {
	private javax.media.j3d.Canvas3D m_renderCanvas = new javax.media.j3d.Canvas3D( com.sun.j3d.utils.universe.SimpleUniverse.getPreferredConfiguration() );
	private java.util.Vector m_pendingCameras = new java.util.Vector();
	private java.util.Hashtable m_sceneToPickCanvasMap = new java.util.Hashtable();

	public OnscreenRenderTarget( Renderer renderer ) {
		super( renderer );
		m_renderCanvas.setDoubleBufferEnable( true );
		m_renderCanvas.setStereoEnable( false );
	}
	public java.awt.Graphics getOffscreenGraphics() {
		return getAWTComponent().getGraphics();
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

	private com.sun.j3d.utils.picking.PickCanvas lookupPickCanvas( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
		com.sun.j3d.utils.picking.PickCanvas pickCanvas = null;
		ContainerProxy root = (ContainerProxy)getProxyFor( sgCamera.getRoot() );
		if( root instanceof SceneProxy ) {
			SceneProxy scene = (SceneProxy)root;
			pickCanvas = (com.sun.j3d.utils.picking.PickCanvas)m_sceneToPickCanvasMap.get( scene );
			if( pickCanvas == null ) {
				pickCanvas = new com.sun.j3d.utils.picking.PickCanvas( m_renderCanvas, scene.getJ3DLocale() );
				pickCanvas.setMode( com.sun.j3d.utils.picking.PickTool.GEOMETRY_INTERSECT_INFO ); 
				//pickCanvas.setTolerance( 4.0f );
				m_sceneToPickCanvasMap.put( scene, pickCanvas );
			}
		}
		return pickCanvas;
	}
	public java.awt.Component getAWTComponent() {
		return m_renderCanvas;
	}
	public void commitAnyPendingChanges() {
		super.commitAnyPendingChanges();
		if( m_pendingCameras != null ) {
			if( m_pendingCameras.size() > 0 ) {
				java.util.Vector pendingCameras = new java.util.Vector();
				for( int i=0; i<m_pendingCameras.size(); i++ ) {
					edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = (edu.cmu.cs.stage3.alice.scenegraph.Camera)m_pendingCameras.elementAt( i );
					ContainerProxy root = (ContainerProxy)getProxyFor( sgCamera.getRoot() );
					if( root instanceof SceneProxy ) {
						SceneProxy scene = (SceneProxy)root;
						CameraProxy camera = (CameraProxy)getProxyFor( sgCamera );
						camera.getJ3DView().addCanvas3D( m_renderCanvas );
						m_renderCanvas.repaint();
					} else {
						pendingCameras.addElement( sgCamera );
					}
				}
				m_pendingCameras = pendingCameras;
			}
		}
	}

	public void addCamera( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
		super.addCamera( sgCamera );
		CameraProxy camera = (CameraProxy)getProxyFor( sgCamera );
		camera.getJ3DView().addCanvas3D( m_renderCanvas );
		//m_pendingCameras.addElement( sgCamera );
	}
	public void removeCamera( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
		if( m_pendingCameras.contains( sgCamera ) ) {
			//pass
		} else {
			CameraProxy camera = (CameraProxy)getProxyFor( sgCamera );
			camera.getJ3DView().removeCanvas3D( m_renderCanvas );
			m_pendingCameras.remove( sgCamera );
		}
		super.removeCamera( sgCamera );
	}
    
	public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
		edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = getCameras();
		return new PickInfo( sgCameras[ 0 ], lookupPickCanvas( sgCameras[ 0 ] ), x, y, isSubElementRequired, isOnlyFrontMostRequired );
	}
    
}
