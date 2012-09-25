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

abstract class GeometryProxy extends ElementProxy {
    private java.util.Vector m_visuals = new java.util.Vector();
    protected abstract javax.media.j3d.Geometry getJ3DGeometry();
	public void addVisual( VisualProxy visual ) {
		m_visuals.addElement( visual );
	}
	public void removeVisual( VisualProxy visual ) {
		m_visuals.removeElement( visual );
	}
    protected void updateVisuals() {
        for( int i=0; i<m_visuals.size(); i++ ) {
            VisualProxy visual = (VisualProxy)m_visuals.elementAt( i );
            visual.updateJ3DGeometry();
        }
    }
}
