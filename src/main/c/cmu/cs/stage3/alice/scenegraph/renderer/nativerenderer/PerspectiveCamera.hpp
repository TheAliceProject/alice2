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

#ifndef PERSPECTIVE_CAMERA_INCLUDED
#define PERSPECTIVE_CAMERA_INCLUDED

#include "Camera.hpp"
class PerspectiveCamera : public Camera {
public:
	int OnPlaneChange( double minX, double minY, double maxX, double maxY ) {
		m_fPlaneMinX = minX;
		m_fPlaneMinY = minY;
		m_fPlaneMaxX = maxX;
		m_fPlaneMaxY = maxY;
		CHECK_SUCCESS( MarkProjectionDirty() );
		return S_OK;
	}
	int LetterboxViewportIfNecessary( Viewport& iViewport ) {
		//todo
		return S_OK;
	}

protected:
	int UpdateProjection( long nWidth, long nHeight ) {
		double fPlaneMinX;
		double fPlaneMinY;
		double fPlaneMaxX;
		double fPlaneMaxY;
		CHECK_SUCCESS( GetActualPlane( nWidth, nHeight, fPlaneMinX, fPlaneMinY, fPlaneMaxX, fPlaneMaxY ) );
#if defined DX7_RENDERER
		D3DXMatrixPerspectiveOffCenterLH( (D3DXMATRIX*)&m_sD3DMProjection, (float)fPlaneMinX, (float)fPlaneMaxX, (float)fPlaneMinY, (float)fPlaneMaxY, (float)m_fNearClippingPlaneDistance, (float)m_fFarClippingPlaneDistance ); 
#else if defined OPENGL_RENEDER
		CHECK_GL( glMatrixMode( GL_PROJECTION ) );
   		CHECK_GL( glLoadIdentity() );
   		CHECK_GL( glFrustum( fPlaneMinX, fPlaneMaxX, fPlaneMinY, fPlaneMaxY, m_fNearClippingPlaneDistance, m_fFarClippingPlaneDistance ) );
		CHECK_GL( glViewport( 0, 0, nWidth, nHeight ) );
		CHECK_GL( glGetDoublev( GL_PROJECTION_MATRIX , m_vfProjection ) );
#endif
		return S_OK;
	}
#if defined DX7_RENDERER
	int GetPickProjection( long nX, long nY, const Viewport& iViewport, D3DMATRIX& sPickProjection ) {
		double fPlaneMinX;
		double fPlaneMinY;
		double fPlaneMaxX;
		double fPlaneMaxY;
		CHECK_SUCCESS( GetActualPlane( iViewport.nWidth, iViewport.nHeight, fPlaneMinX, fPlaneMinY, fPlaneMaxX, fPlaneMaxY ) );
		CHECK_SUCCESS( UpdatePickPlane( nX, nY, iViewport, fPlaneMinX, fPlaneMinY, fPlaneMaxX, fPlaneMaxY ) );
		D3DXMatrixPerspectiveOffCenterLH( (D3DXMATRIX*)&sPickProjection, (float)fPlaneMinX, (float)fPlaneMaxX, (float)fPlaneMinY, (float)fPlaneMaxY, (float)m_fNearClippingPlaneDistance, (float)m_fFarClippingPlaneDistance ); 
		return S_OK;
	}
#endif

private:
	int GetActualPlane( long nWidth, long nHeight, double& fPlaneMinX, double& fPlaneMinY, double& fPlaneMaxX, double& fPlaneMaxY ) {
		fPlaneMinX = m_fPlaneMinX;
		fPlaneMinY = m_fPlaneMinY;
		fPlaneMaxX = m_fPlaneMaxX;
		fPlaneMaxY = m_fPlaneMaxY;
		if( IsNaN( m_fPlaneMinX ) && IsNaN( m_fPlaneMaxX ) ) {
			double ratio = nWidth / (double)nHeight;
			fPlaneMinX = ratio * fPlaneMinY;
			fPlaneMaxX = ratio * fPlaneMaxY;
		} else if( IsNaN( m_fPlaneMinY ) && IsNaN( m_fPlaneMaxY ) ) {
			double ratio = nHeight / (double)nWidth;
			fPlaneMinY = ratio * fPlaneMinX;
			fPlaneMaxY = ratio * fPlaneMaxX;
		} else {
			//todo: use screen dimensions
		}
		return S_OK;
	}
	double m_fPlaneMinX;
	double m_fPlaneMinY;
	double m_fPlaneMaxX;
	double m_fPlaneMaxY;
};

#endif