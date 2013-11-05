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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public class PickInfo implements  edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo {
	private edu.cmu.cs.stage3.alice.scenegraph.Component m_source = null;
	private javax.vecmath.Matrix4d m_projection;
	private edu.cmu.cs.stage3.alice.scenegraph.Visual[] m_visuals;
	private boolean[] m_isFrontFacings;
	private edu.cmu.cs.stage3.alice.scenegraph.Geometry[] m_geometries;
	private int[] m_subElements;
	private double[] m_zs;

	public PickInfo( edu.cmu.cs.stage3.alice.scenegraph.Component component, javax.vecmath.Matrix4d projection, edu.cmu.cs.stage3.alice.scenegraph.Visual[] visuals, boolean[] isFrontFacings, edu.cmu.cs.stage3.alice.scenegraph.Geometry[] geometries, int[] subElements, double[] zs ) {
		m_source = component;
		m_projection = projection;
		m_visuals = visuals;
		m_isFrontFacings = isFrontFacings;
		m_geometries = geometries;
		m_subElements = subElements;
		m_zs = zs;
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


	/** @deprecated */
	private RenderTarget m_renderTarget = null;
	/** @deprecated */
	private edu.cmu.cs.stage3.alice.scenegraph.Camera m_camera = null;
	/** @deprecated */
	private int m_x = -1;
	/** @deprecated */
	private int m_y = -1;
	//todo: deprecate
	public PickInfo( RenderTarget renderTarget, edu.cmu.cs.stage3.alice.scenegraph.Camera camera, int x, int y, edu.cmu.cs.stage3.alice.scenegraph.Visual[] visuals, boolean[] isFrontFacings, edu.cmu.cs.stage3.alice.scenegraph.Geometry[] geometries, int[] subElements ) {
		m_renderTarget = renderTarget;
		m_camera = camera;
		m_source = camera;
		m_x = x;
		m_y = y;
		m_visuals = visuals;
		m_isFrontFacings = isFrontFacings;
		m_geometries = geometries;
		m_subElements = subElements;
	}

	public javax.vecmath.Vector3d getLocalPositionAt( int index ) {
		if( m_source != null && m_zs != null ) {
			javax.vecmath.Matrix4d componentInverseAbsolute = m_source.getInverseAbsoluteTransformation();
			javax.vecmath.Matrix4d visualAbsolute = getVisualAt( index ).getAbsoluteTransformation();
			javax.vecmath.Vector4d xyzw = new javax.vecmath.Vector4d( 0, 0, m_