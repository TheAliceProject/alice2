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

#ifndef SYMMETRIC_PERSPECTIVE_CAMERA_INCLUDED
#define SYMMETRIC_PERSPECTIVE_CAMERA_INCLUDED

#include "Camera.hpp"

class SymmetricPerspectiveCamera : public Camera {
public:
	int OnVerticalViewingAngleChange( double value ) {
		m_fVerticalViewingAngle = value;
		CHECK_SUCCESS( MarkProjectionDirty() );
		return S_OK;
	}
	int OnHorizontalViewingAngleChange( double value ) {
		m_fHorizontalViewingAngle = value;
		CHECK_SUCCESS( MarkProjectionDirty() );
		return S_OK;
	}
	int LetterboxViewportIfNecessary( Viewport& iViewport ) {
		if( !IsNaN( m_fHorizontalViewingAngle ) ) { 
			if( !IsNaN( m_fVerticalViewingAngle )) {
				double fAspect = m_fHorizontalViewingAngle / m_fVerticalViewingAngle;
				if( fAspect > (iViewport.nWidth / (double)iViewport.nHeight) ) {
					int nViewportHeight = (int)( iViewport.nWidth / fAspect );
					int dy = iViewport.nHeight - nViewportHeight;
					iViewport.nY += dy/2;
					iViewport.nHeight = nViewportHeight;
				} else {
					int nViewportWidth = (int)( iViewport.nHeight * fAspect );
					int dx = iViewport.nWidth - nViewportWidth;
					iViewport.nX += dx/2;
					iViewport.nWidth = nViewportWidth;
				}
			}
		}
		return S_OK;
	}
protected:
	int UpdateProjection( long nWidth, long nHeight ) {
		double fHorizontal;
		double fVertical;
		CHECK_SUCCESS( GetActualViewingAngles( nWidth, nHeight, fHorizontal, fVertical ) );
#if defined DX7_RENDERER
		double fX = m_fNearClippingPlaneDistance * tan( fHorizontal*0.5 );
		double fY = m_fNearClippingPlaneDistance * tan( fVertical*0.5 );

		double fPlaneMinX = -fX;
		double fPlaneMinY = -fY;
		double fPlaneMaxX = fX;
		double fPlaneMaxY = fY;
		D3DXMatrixPerspectiveOffCenterLH( (D3DXMATRIX*)&m_sD3DMProjection, (float)fPlaneMinX, (float)fPlaneMaxX, (float)fPlaneMinY, (float)fPlaneMaxY, (float)m_fNearClippingPlaneDistance, (float)m_fFarClippingPlaneDistance ); 
		
		//D3DXMatrixPerspectiveFovLH( (D3DXMATRIX*)&m_sD3DMatrixProjection, (float)fVertical, (float)(fVertical/fHorizontal), (float)m_fNearClippingPlaneDistance, (float)m_fFarClippingPlaneDistance );
#else if defined OPENGL_RENEDER
		CHECK_GL( glMatrixMode( GL_PROJECTION ) );
   		CHECK_GL( glLoadIdentity() );
   		CHECK_GL( gluPerspective( 180*(fVertical/M_PI), fHorizontal/fVertical, m_fNearClippingPlaneDistance, m_fFarClippingPlaneDistance ) );
		CHECK_GL( glViewport( 0, 0, nWidth, nHeight ) );
		CHECK_GL( glGetDoublev( GL_PROJECTION_MATRIX , m_vfProjection ) );
#endif
		return S_OK;
		
	}
#if defined DX7_RENDERER
	int GetPickProjection( long nX, long nY, const Viewport& iViewport, D3DMATRIX& sPickProjection ) {
		double fHorizontal;
		double fVertical;
		CHECK_SUCCESS( GetActualViewingAngles( iViewport.nWidth, iViewport.nHeight, fHorizontal, fVertical ) );

		double fX = m_fNearClippingPlaneDistance * tan( fHorizontal*0.5 );
		double fY = m_fNearClippingPlaneDistance * tan( fVertical*0.5 );

		double fPlaneMinX = -fX;
		double fPlaneMinY = -fY;
		double fPlaneMaxX = fX;
		double fPlaneMaxY = fY;
		CHECK_SUCCESS( UpdatePickPlane( nX, nY, iViewport, fPlaneMinX, fPlaneMinY, fPlaneMaxX, fPlaneMaxY ) );
		D3DXMatrixPerspectiveOffCenterLH( (D3DXMATRIX*)&sPickProjection, (float)fPlaneMinX, (float)fPlaneMaxX, (float)fPlaneMinY, (float)fPlaneMaxY, (float)m_fNearClippingPlaneDistance, (float)m_fFarClippingPlaneDistance ); 
		return S_OK;
	}
#endif
private:
	int GetActualViewingAngles( long nWidth, long nHeight, double& fHorizontal, double& fVertical ) {
		double fAspect = nWidth/(double)nHeight;
		if( !IsNaN( m_fHorizontalViewingAngle ) ) { 
			fHorizontal = m_fHorizontalViewingAngle;
			if( !IsNaN( m_fVerticalViewingAngle )) {
				fVertical = m_fVerticalViewingAngle;
			} else {
				fVertical = fHorizontal/fAspect;
			}
		} else {
			if( !IsNaN( m_fVerticalViewingAngle )) {
				fVertical = m_fVerticalViewingAngle;
			} else {
				//todo: use screen dimensions
				fVertical = 0.5;
			}
			fHorizontal = fVertical*fAspect;
		}
		return S_OK;
	}
	double m_fVerticalViewingAngle;
	double m_fHorizontalViewingAngle;
};

#endif