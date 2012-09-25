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

class BackgroundProxy extends ElementProxy {
    private javax.media.j3d.Background m_j3dBackground = new javax.media.j3d.Background();
    protected void initJ3D() {
        super.initJ3D();
        m_j3dBackground = new javax.media.j3d.Background();
        m_j3dBackground.setCapability( javax.media.j3d.Background.ALLOW_COLOR_WRITE );
        m_j3dBackground.setCapability( javax.media.j3d.Background.ALLOW_IMAGE_WRITE );
        m_j3dBackground.setApplicationBounds( new javax.media.j3d.BoundingSphere( new javax.vecmath.Point3d( 0.0, 0.0, 0.0 ), Double.POSITIVE_INFINITY ) );
        m_j3dBackground.setPickable( false );
        m_j3dBackground.setUserData( this );
    }

    public javax.media.j3d.Background getJ3DBackground() {
        return m_j3dBackground;
    }

	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Background.COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
            m_j3dBackground.setColor( color.createVecmathColor3f() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Background.TEXTURE_MAP_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Background.TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}
