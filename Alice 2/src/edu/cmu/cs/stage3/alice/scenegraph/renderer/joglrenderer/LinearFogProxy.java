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

import com.jogamp.opengl.GL2;

class LinearFogProxy extends FogProxy {
    private float m_near;
    private float m_far;
    
	public void setup( RenderContext context ) {
        super.setup( context );
        context.gl2.glFogi( GL2.GL_FOG_MODE, GL2.GL_LINEAR );
        context.gl2.glFogf( GL2.GL_FOG_START, m_near );
        context.gl2.glFogf( GL2.GL_FOG_END, m_far );
    }
	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.LinearFog.NEAR_DISTANCE_PROPERTY ) {
		    m_near = ((Number)value).floatValue();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.LinearFog.FAR_DISTANCE_PROPERTY ) {
		    m_far = ((Number)value).floatValue();
		} else {
			super.changed( property, value );
		}
	}
}
