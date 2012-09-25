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

class TransformableProxy extends ReferenceFrameProxy {
    private javax.media.j3d.TransformGroup m_j3dTransformGroup = new javax.media.j3d.TransformGroup();
    private javax.media.j3d.Transform3D m_j3dTransform3D = new javax.media.j3d.Transform3D();
    protected javax.media.j3d.Group getJ3DGroup() {
        return m_j3dTransformGroup;
    }
    protected void initJ3D() {
        super.initJ3D();
        m_j3dTransformGroup.setTransform( m_j3dTransform3D );

        m_j3dTransformGroup.setCapability( javax.media.j3d.Group.ALLOW_CHILDREN_EXTEND );
        m_j3dTransformGroup.setCapability( javax.media.j3d.Group.ALLOW_CHILDREN_WRITE );
        m_j3dTransformGroup.setCapability( javax.media.j3d.TransformGroup.ALLOW_TRANSFORM_WRITE );

        m_j3dTransformGroup.setPickable( true );
        m_j3dTransformGroup.setUserData( this );
    }
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Transformable.LOCAL_TRANSFORMATION_PROPERTY ) {
            javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
            m.set( (javax.vecmath.Matrix4d)value );

            //adjust from rows to columns
            m.transpose();

            //switch from left-handed to right-handed
                                            m.m02 = -m.m02;
                                            m.m12 = -m.m12;
            m.m20 = -m.m20; m.m21 = -m.m21;                 m.m23 = -m.m23;
                                            m.m32 = -m.m32;

            m_j3dTransform3D.set( m );
            m_j3dTransformGroup.setTransform( m_j3dTransform3D );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Transformable.IS_FIRST_CLASS_PROPERTY ) {
            //pass
		} else {
			super.changed( property, value );
		}
	}
	public boolean isHelper() {
		return ((edu.cmu.cs.stage3.alice.scenegraph.Transformable)getSceneGraphElement()).isHelper();
	}
}
