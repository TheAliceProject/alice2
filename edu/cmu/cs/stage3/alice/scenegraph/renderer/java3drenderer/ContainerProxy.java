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

abstract class ContainerProxy extends ComponentProxy {
    protected abstract javax.media.j3d.Group getJ3DGroup();
    protected javax.media.j3d.Node getJ3DNode() {
        return getJ3DGroup();
    }
	private edu.cmu.cs.stage3.alice.scenegraph.Container getSceneGraphContainer() {
		return (edu.cmu.cs.stage3.alice.scenegraph.Container)getSceneGraphElement();
	}
	public void onChildAdded( ComponentProxy componentProxy ) {
		if( componentProxy.isHelper() ) {
			//pass
		} else {
			getJ3DGroup().addChild( componentProxy.getJ3DBranchGroup() );
		}
    }
	public void onChildRemoved( ComponentProxy componentProxy ) {
		if( componentProxy.isHelper() ) {
			//pass
		} else {
			getJ3DGroup().removeChild( componentProxy.getJ3DBranchGroup() );
		}
    }
	public void initialize( edu.cmu.cs.stage3.alice.scenegraph.Element sgElement, edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer renderer ) {
		super.initialize( sgElement, renderer );
		edu.cmu.cs.stage3.alice.scenegraph.Component[] sgChildren = getSceneGraphContainer().getChildren();
		for( int i=0; i<sgChildren.length; i++ ) {
			onChildAdded( (ComponentProxy)getProxyFor( sgChildren[i] ) );
		}
	}
}
