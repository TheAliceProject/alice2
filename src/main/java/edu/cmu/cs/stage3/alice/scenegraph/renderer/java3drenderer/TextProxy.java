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

class TextProxy extends GeometryProxy {
    private javax.media.j3d.Text3D m_j3dText = new javax.media.j3d.Text3D();
    protected void initJ3D() {
        super.initJ3D();
        m_j3dText.setCapability( javax.media.j3d.Text3D.ALLOW_STRING_WRITE );
        m_j3dText.setCapability( javax.media.j3d.Text3D.ALLOW_FONT3D_WRITE );
    }
    protected javax.media.j3d.Geometry getJ3DGeometry() {
        return m_j3dText;
    }

	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Text.TEXT_PROPERTY ) {
            if( value == null ) {
                value = "";
            }
            m_j3dText.setString( (String)value );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Text.FONT_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Text.EXTRUSION_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}
