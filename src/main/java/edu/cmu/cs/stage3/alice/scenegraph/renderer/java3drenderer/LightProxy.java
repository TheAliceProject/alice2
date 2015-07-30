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

abstract class LightProxy extends AffectorProxy {
    protected abstract javax.media.j3d.Light getJ3DLight();
    protected javax.media.j3d.Node getJ3DNode() {
        return getJ3DLight();
    }
    protected void initJ3D() {
        super.initJ3D();
        javax.media.j3d.Light j3dLight = getJ3DLight();
        j3dLight.setCapability( javax.media.j3d.Light.ALLOW_COLOR_WRITE );
        j3dLight.setCapability( javax.media.j3d.Light.ALLOW_INFLUENCING_BOUNDS_WRITE );
        j3dLight.setCapability( javax.media.j3d.Light.ALLOW_SCOPE_WRITE );
        j3dLight.setCapability( javax.media.j3d.Light.ALLOW_STATE_WRITE );
        j3dLight.setUserData( this );
        j3dLight.setInfluencingBounds( new javax.media.j3d.BoundingSphere( new javax.vecmath.Point3d(0.0,0.0,0.0), 256.0 ) );
    }

	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Light.COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
            getJ3DLight().setColor( color.createVecmathColor3f() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Light.BRIGHTNESS_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Light.RANGE_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}
