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

package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.lang.Messages;

/**
 * @author Dennis Cosgrove
 */
public class Visual extends Component {
	public static final Property FRONT_FACING_APPEARANCE_PROPERTY = new Property( Visual.class, "FRONT_FACING_APPEARANCE" ); 
	public static final Property BACK_FACING_APPEARANCE_PROPERTY = new Property( Visual.class, "BACK_FACING_APPEARANCE" ); 
	public static final Property GEOMETRY_PROPERTY = new Property( Visual.class, "GEOMETRY" ); 
	public static final Property SCALE_PROPERTY = new Property( Visual.class, "SCALE" ); 
	public static final Property IS_SHOWING_PROPERTY = new Property( Visual.class, "IS_SHOWING" ); 
	public static final Property DISABLED_AFFECTORS_PROPERTY = new Property( Visual.class, "DISABLED_AFFECTORS" ); 

	private Appearance m_frontFacingAppearance = null;
	private Appearance m_backFacingAppearance = null;
	private Geometry m_geometry = null;
	private javax.vecmath.Matrix3d m_scale = null;
	private boolean m_isShowing = true;
	private Affector[] m_disabledAffectors = null;

	public Visual() {
		m_scale = new javax.vecmath.Matrix3d();
		m_scale.setIdentity();
	}
	
	protected void releasePass1() {
		if( m_frontFacingAppearance != null ) {
			warnln( Messages.getString("WARNING__released_visual_") + this + Messages.getString("_still_has_front_facing_appearance_") + m_frontFacingAppearance + "." );   
			setFrontFacingAppearance( null );
		}
		if( m_backFacingAppearance != null ) {
			warnln( Messages.getString("WARNING__released_visual_") + this + Messages.getString("_still_has_back_facing_appearance_") + m_frontFacingAppearance + "." );   
			setBackFacingAppearance( null );
		}
		if( m_geometry != null ) {
			warnln( Messages.getString("WARNING__released_visual_") + this + Messages.getString("_still_has_geometry_") + m_geometry + "." );   
			setGeometry( null );
		}
		if( m_disabledAffectors != null && m_disabledAffectors.length > 0 ) {
			warnln( Messages.getString("WARNING__released_visual_") + this + Messages.getString("_still_has_disabled_affectors__") );  
			for( int i=0; i<m_disabledAffectors.length; i++ ) {
				warnln( "\t" + m_disabledAffectors[ i ] ); 
			}
			setDisabledAffectors( null );
		}
		super.releasePass1();
	}
	public Geometry getGeometry() {
		return m_geometry;
	}
	public void setGeometry( Geometry geometry ) {
		if( notequal( m_geometry, geometry ) ) {
			m_geometry = geometry;
			onPropertyChange( GEOMETRY_PROPERTY );
		}
	}

	public Appearance getFrontFacingAppearance() {
		return m_frontFacingAppearance;
	}
	public void setFrontFacingAppearance( Appearance frontFacingAppearance ) {
		if( notequal( m_frontFacingAppearance, frontFacingAppearance ) ) {
			m_frontFacingAppearance = frontFacingAppearance;
			onPropertyChange( FRONT_FACING_APPEARANCE_PROPERTY );
		}
	}

	public Appearance getBackFacingAppearance() {
		return m_backFacingAppearance;
	}
	public void setBackFacingAppearance( Appearance backFacingAppearance ) {
		if( notequal( m_backFacingAppearance, backFacingAppearance ) ) {
			m_backFacingAppearance = backFacingAppearance;
