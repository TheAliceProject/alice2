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

class SpotLightProxy extends PointLightProxy {
    private javax.media.j3d.SpotLight m_j3dSpotLight;
    protected javax.media.j3d.PointLight createJ3DPointLight() {
        m_j3dSpotLight = new javax.media.j3d.SpotLight();
        return m_j3dSpotLight;
    }
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.SpotLight.INNER_BEAM_ANGLE_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.SpotLight.OUTER_BEAM_ANGLE_PROPERTY ) {
			m_j3dSpotLight.setSpreadAngle( ((Double)value).floatValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.SpotLight.FALLOFF_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}
