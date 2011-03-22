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

class PointLightProxy extends LightProxy {
    private javax.media.j3d.PointLight m_j3dPointLight = createJ3DPointLight();
    protected javax.media.j3d.PointLight createJ3DPointLight() {
        return new javax.media.j3d.PointLight();
    }
    protected javax.media.j3d.Light getJ3DLight() {
        return m_j3dPointLight;
    }
    private void updateAttenuation() {
        edu.cmu.cs.stage3.alice.scenegraph.PointLight sgPointLight = (edu.cmu.cs.stage3.alice.scenegraph.PointLight)getSceneGraphElement();
        float constant = (float)sgPointLight.getConstantAttenuation();
        float linear = (float)sgPointLight.getLinearAttenuation();
        float quadratic = (float)sgPointLight.getQuadraticAttenuation();
        m_j3dPointLight.setAttenuation( constant, linear, quadratic );
    }
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.PointLight.CONSTANT_ATTENUATION_PROPERTY ) {
            updateAttenuation();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.PointLight.LINEAR_ATTENUATION_PROPERTY ) {
            updateAttenuation();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.PointLight.QUADRATIC_ATTENUATION_PROPERTY ) {
            updateAttenuation();
		} else {
			super.changed( property, value );
		}
	}
}
