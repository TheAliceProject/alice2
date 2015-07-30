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

abstract class CameraProxy extends ComponentProxy {
	private javax.media.j3d.ViewPlatform m_j3dViewPlatform = new javax.media.j3d.ViewPlatform();
	private javax.media.j3d.View m_j3dView = new javax.media.j3d.View();

    protected javax.media.j3d.Node getJ3DNode() {
        return m_j3dViewPlatform;
    }
    protected javax.media.j3d.View getJ3DView() {
        return m_j3dView;
    }
	protected abstract int getProjectionPolicy();
	protected void updateProjection( javax.media.j3d.Transform3D m ) {
		m_j3dView.setLeftProjection( m );
	}

    protected void initJ3D() {
        super.initJ3D();

        m_j3dViewPlatform.setCapability( javax.media.j3d.ViewPlatform.ALLOW_POLICY_WRITE );
        m_j3dViewPlatform.setUserData( this );

        m_j3dView.setPhysicalBody( new javax.media.j3d.PhysicalBody() );
        m_j3dView.setPhysicalEnvironment( new javax.media.j3d.PhysicalEnvironment() );
        m_j3dView.setDepthBufferFreezeTransparent( false );
		m_j3dView.setProjectionPolicy( getProjectionPolicy() );
		m_j3dView.setCompatibilityModeEnable( true );
        m_j3dView.attachViewPlatform( m_j3dViewPlatform );
    }
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Camera.NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY ) {
			m_j3dView.setFrontClipDistance( ((Double)value).doubleValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Camera.FAR_CLIPPING_PLANE_DISTANCE_PROPERTY ) {
			m_j3dView.setBackClipDistance( ((Double)value).doubleValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Camera.BACKGROUND_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}
