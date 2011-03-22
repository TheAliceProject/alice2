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

abstract class ComponentProxy extends ElementProxy {
    private javax.media.j3d.BranchGroup m_j3dBranchGroup = new javax.media.j3d.BranchGroup();
    protected abstract javax.media.j3d.Node getJ3DNode();
    public javax.media.j3d.BranchGroup getJ3DBranchGroup() {
        return m_j3dBranchGroup;
    }
    protected void initJ3D() {
        super.initJ3D();
		m_j3dBranchGroup.setCapability( javax.media.j3d.BranchGroup.ALLOW_DETACH );
        m_j3dBranchGroup.setPickable( true );
        m_j3dBranchGroup.setUserData( this );
        m_j3dBranchGroup.addChild( getJ3DNode() );
    }
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Component.PARENT_PROPERTY ) {
            //pass
		} else {
			super.changed( property, value );
		}
	}
	public boolean isHelper() {
		return false;
	}
}
