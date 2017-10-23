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

class LinearFogProxy extends FogProxy {
    private javax.media.j3d.LinearFog m_j3dLinearFog = new javax.media.j3d.LinearFog();
    protected void initJ3D() {
        super.initJ3D();
        m_j3dLinearFog.setCapability( javax.media.j3d.LinearFog.ALLOW_DISTANCE_WRITE );
    }
    protected javax.media.j3d.Fog getJ3DFog() {
        return m_j3dLinearFog;
    }
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.LinearFog.NEAR_DISTANCE_PROPERTY ) {
			m_j3dLinearFog.setFrontDistance( ((Double)value).doubleValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.LinearFog.FAR_DISTANCE_PROPERTY ) {
			m_j3dLinearFog.setBackDistance( ((Double)value).doubleValue() );
		} else {
			super.changed( property, value );
		}
	}
}
