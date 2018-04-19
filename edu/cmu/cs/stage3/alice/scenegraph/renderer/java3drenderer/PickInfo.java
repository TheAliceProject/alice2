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

public class PickInfo implements  edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo {
	private edu.cmu.cs.stage3.alice.scenegraph.Component m_source = null;
    private javax.vecmath.Matrix4d m_projection;
	private edu.cmu.cs.stage3.alice.scenegraph.Visual[] m_visuals;
    private boolean[] m_isFrontFacings;
	private edu.cmu.cs.stage3.alice.scenegraph.Geometry[] m_geometries;
	private int[] m_subElements;
    private double[] m_zs;

	//todo: remove source parameter
	public PickInfo( edu.cmu.cs.stage3.alice.scenegraph.Camera source, com.sun.j3d.utils.picking.PickCanvas pickCanvas, int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
		com.sun.j3d.utils.picking.PickResult[] pickResults = null;
		pickCanvas.setShapeLocation( x, y );
		if( isOnlyFrontMostRequired ) {
			com.sun.j3d.utils.picking.PickResult pickResult = pickCanvas.pickClosest();
			if( pickResult != null ) {
				pickResults = new com.sun.j3d.utils.picking.PickResult[ 1 ];
				pickResults[ 0 ] = pickResult;
			}
		} else {
			pickResults = pickCanvas.pickAllSorted();
		}
		if( pickResults == null ) {
			pickResults = new com.sun.j3d.utils.picking.PickResult[ 0 ];
		}
		m_source = source;
		m_projection = null;
		m_visuals = new edu.cmu.cs.stage3.alice.scenegraph.Visual[ pickResults.length ];
		m_isFrontFacings = new boolean[ pickResults.length ];
		m_geometries = new edu.cmu.cs.stage3.alice.scenegraph.Geometry[ pickResults.length ];
		m_subElements = new int[ pickResults.length ];
		m_zs = new double[ pickResults.length ];
		for( int i=0; i<pickResults.length; i++ ) {
			m_visuals[ i ] = null;
			if( pickResults[ i ] != null ) {
				javax.media.j3d.SceneGraphPath path = pickResults[ i ].getSceneGraphPath();
				VisualProxy vp = (VisualProxy)path.getObject().getUserData();
				if( vp != null ) {
					m_visuals[ i ] = (edu.cmu.cs.stage3.alice.scenegraph.Visual)vp.getSceneGraphElement();
				}
			}
		}
	}

    public edu.cmu.cs.stage3.alice.scenegraph.Component getSource() {
        return m_source;
    }
	public edu.cmu.cs.stage3.alice.scenegraph.Visual[] getVisuals() {
		return m_visuals;
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Geometry[] getGeometries() {
		return m_geometries;
	}
	public boolean[] isFrontFacings() {
		return m_isFrontFacings;
	}
	public int[] getSubElements() {
		return m_subElements;
	}
	public double[] getZs() {
		return m_zs;
	}

	public int getCount() {
		if( m_visuals!=null ) {
			return m_visuals.length;
		} else {
			return 0;
		}
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Visual getVisualAt( int index ) {
		return m_visuals[index];
	}
	public boolean isFrontFacingAt( int index ) {
		return m_isFrontFacings[ index ];
	}
	public edu.cmu.cs.stage3.alice.scenegraph.Geometry getGeometryAt( int index ) {
		return m_geometries[index];
	}
	public int getSubElementAt( int index ) {
		return m_subElements[index];
	}
	public double getZAt( int index ) {
		return m_zs[index];
	}
	public javax.vecmath.Vector3d getLocalPositionAt( int index ) {
		return null;
	}
}
