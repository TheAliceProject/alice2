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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.RenderTargetProperty;

public abstract class Camera extends Model /*Transformable*/ {
	public final BooleanProperty isLetterboxedAsOpposedToDistorted = new BooleanProperty( this, "isLetterboxedAsOpposedToDistorted", Boolean.TRUE ); 
	public final NumberProperty nearClippingPlaneDistance = new NumberProperty( this, "nearClippingPlaneDistance", new Double( 0.1 ) ); 
	public final NumberProperty farClippingPlaneDistance = new NumberProperty( this, "farClippingPlaneDistance", new Double( 100.0 ) ); 
	public final RenderTargetProperty renderTarget = new RenderTargetProperty( this, "renderTarget", null ); 
	public final BooleanProperty isViewVolumeShowing = new BooleanProperty( this, "isViewVolumeShowing", Boolean.FALSE ); 
	private edu.cmu.cs.stage3.alice.scenegraph.Camera m_sgCamera;
	private edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator m_viewVolumeDecorator;
	protected Camera( edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera ) {
		super();
		m_sgCamera = sgCamera;
		m_sgCamera.setParent( getSceneGraphTransformable() );
		m_sgCamera.setBonus( this );
		nearClippingPlaneDistance.set( new Double( m_sgCamera.getNearClippingPlaneDistance() ) );
		farClippingPlaneDistance.set( new Double( m_sgCamera.getFarClippingPlaneDistance() ) );
		m_viewVolumeDecorator = createViewVolumeDecorator();
	}
    
	protected void internalRelease( int pass ) {
        switch( pass ) {
        case 1:
            m_sgCamera.setParent( null );
            m_viewVolumeDecorator.internalRelease( 1 );
            break;
        case 2:
            m_sgCamera.release();
            m_sgCamera = null;
            m_viewVolumeDecorator.internalRelease( 2 );
            m_viewVolumeDecorator = null;
            break;
        }
        super.internalRelease( pass );
    }

	public edu.cmu.cs.stage3.alice.scenegraph.Camera getSceneGraphCamera() {
		return m_sgCamera;
	}
	protected abstract edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator createViewVolumeDecorator();
	
	protected void nameValueChanged( String value ) {
		super.nameValueChanged( value );
		String s = null;
		if( value!=null ) {
			s = value+".m_sgCamera"; 
		}
		m_sgCamera.setName( s );
	}
	private void nearClippingPlaneDistanceValueChanged( Number value ) {
		double d = Double.NaN;
		if( value!=null ) {
			d = value.doubleValue();
		}
		m_sgCamera.setNearClippingPlaneDistance( d );
	}
	private void farClippingPlaneDistanceValueChanged( Number value ) {
		double d = Double.NaN;
		if( value!=null ) {
			d = value.doubleValue();
		}
		m_sgCamera.setFarClippingPlaneDistance( d );
	}
	private void renderTargetValueChanging( RenderTarget renderTargetValueToBe ) {
		RenderTarget renderTargetValue = (RenderTarget)renderTarget.getValue();
		if( renderTargetValue!=null ) {
			renderTargetValue.removeCamera( this );
		}
	}
	private void renderTargetValueChanged( RenderTarget renderTargetValue 