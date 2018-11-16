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

#ifndef CAMERA_INCLUDED
#define CAMERA_INCLUDED

#include "Component.hpp"
#include "Viewport.hpp"

class Background;
class Scene;
class RenderTarget;
class Renderer;
class _Visual;

class Camera : public _Component {
public:
	Camera() : _Component() {
		OnNearClippingPlaneDistanceChange( 0.1 );
		OnFarClippingPlaneDistanceChange( 100.0 );
		m_nWidth = 0;
		m_nHeight = 0;
		m_pBackground = NULL;
		MarkProjectionDirty();
		MarkViewDirty();
	}
	virtual int LetterboxViewportIfNecessary( Viewport& iViewport ) = 0;
	//override
	int OnAbsoluteTransformationChange( double rc00, double rc01, double rc02, double rc03, double rc10, double rc11, double rc12, double rc13, double rc20, double rc21, double rc22, double rc23, double rc30, double rc31, double rc32, double rc33 ) {
		CHECK_SUCCESS( _Component::OnAbsoluteTransformationChange( rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 ) );
		CHECK_SUCCESS( MarkViewDirty() );
		return S_OK;
	}
	int OnNearClippingPlaneDistanceChange( double value ) {
		m_fNearClippingPlaneDistance = value;
		CHECK_SUCCESS( MarkProjectionDirty() );
		return S_OK;
	}
	int OnFarClippingPlaneDistanceChange( double value ) {
		m_fFarClippingPlaneDistance = value;
		CHECK_SUCCESS( MarkProjectionDirty() );
		return S_OK;
	}
	int OnBackgroundChange( Background* value ) {
		m_pBackground = value;
		return S_OK;
	}

	int GetProjectionMatrix( double& rc00, double& rc01, double& rc02, double& rc03, double& rc10, double& rc11, double& rc12, double& rc13, double& rc20, double& rc21, double& rc22, double& rc23, double& rc30, double& rc31, double& rc32, double& rc33 ) {
#if defined DX7_RENDERER
		rc00 = m_sD3DMProjection._11;
		rc01 = m_sD3DMProjection._12;
		rc02 = m_sD3DMProjection._13;
		rc03 = m_sD3DMProjection._14;

		rc10 = m_sD3DMProjection._21;
		rc11 = m_sD3DMProjection._22;
		rc12 = m_sD3DMProjection._23;
		rc13 = m_sD3DMProjection._24;

		rc20 = m_sD3DMProjection._31;
		rc21 = m_sD3DMProjection._32;
		rc22 = m_sD3DMProjection._33;
		rc23 = m_sD3DMProjection._34;

		rc30 = m_sD3DMProjection._41;
		rc31 = m_sD3DMProjection._42;
		rc32 = m_sD3DMProjection._43;
		rc33 = m_sD3DMProjection._44;
#elif defined OPENGL_RENDERER
		rc00 = m_vfProjection[ 0 ];
		rc01 = m_vfProjection[ 1 ];
		rc02 = m_vfProjection[ 2 ];
		rc03 = m_vfProjection[ 3 ];

		rc10 = m_vfProjection[ 4 ];
		rc11 = m_vfProjection[ 5 ];
		rc12 = m_vfProjection[ 6 ];
		rc13 = m_vfProjection[ 7 ];

		rc20 = m_vfProjection[ 8 ];
		rc21 = m_vfProjection[ 9 ];
		rc22 = m_vfProjection[ 10 ];
		rc23 = m_vfProjection[ 11 ];

		rc30 = m_vfProjection[ 12 ];
		rc31 = m_vfProjection[ 13 ];
		rc32 = m_vfProjection[ 14 ];
		rc33 = m_vfProjection[ 15 ];
#endif
		return S_OK;
	}

	int Clear( RenderTarget* pRenderTarget, void* pContext, const Viewport& iOuterViewport, const Viewport& iActualViewport );
	int Setup( RenderTarget* pRenderTarget, void* pContext, const Viewport& iActualViewport );
	int Pick( Renderer* pRenderer, long nX, long nY, const Viewport& iViewport, bool isSubElementRequired, bool isOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ );

#if defined DX7_RENDERER
public:
	int GetDirection( D3DVECTOR& sD3DDirection ) {
		sD3DDirection = m_sD3DDirection;
		return S_OK;
	}
	int GetDotProductLocationByDirection( float& fDotProductLocationByDirection ) {
		fDotProductLocationByDirection = m_fDotProductLocationByDirection;
		return S_OK;
	}
#endif

protected:
	virtual int UpdateProjection( long nWidth, long nHeight ) = 0;
	int MarkProjectionDirty() {
		m_bIsProjectionDirty = true;
		return S_OK;
	}
	int MarkViewDirty() {
		m_bIsViewDirty = true;
		return S_OK;
	}

private:
	int UpdateIfNecessary( const Viewport& iViewport ) {
		if( m_nWidth!=iViewport.nWidth || m_nHeight!=iViewport.nHeight ) {
			MarkProjectionDirty();
			m_nWidth = iViewport.nWidth;
			m_nHeight = iViewport.nHeight;
		}
		if( m_bIsProjectionDirty ) {
			CHECK_SUCCESS( UpdateProjection( m_nWidth, m_nHeight ) );
			m_bIsProjectionDirty = false;
		}
		if( m_bIsViewDirty ) {
			CHECK_SUCCESS( UpdateView() );
			m_bIsViewDirty = false;
		}
		return S_OK;
	}
	int UpdateView() {
#if defined DX7_RENDERER
		D3DVECTOR vRight( m_sD3DMAbsolute._11, m_sD3DMAbsolute._12, m_sD3DMAbsolute._13 );
		D3DVECTOR vUp( m_sD3DMAbsolute._21, m_sD3DMAbsolute._22, m_sD3DMAbsolute._23 );
		D3DVECTOR vForward( m_sD3DMAbsolute._31, m_sD3DMAbsolute._32, m_sD3DMAbsolute._33 );
		D3DVECTOR vFrom( m_sD3DMAbsolute._41, m_sD3DMAbsolute._42, m_sD3DMAbsolute._43 );

		CHECK_SUCCESS( ::UpdateViewMatrix( m_sD3DMView, vRight, vUp, vForward, vFrom ) );

		m_sD3DDirection = vForward;
		m_fDotProductLocationByDirection = DotProduct( vFrom, m_sD3DDirection );

#elif defined OPENGL_RENDERER
		CHECK_GL( glMatrixMode( GL_MODELVIEW ) );
		CHECK_GL( glLoadIdentity() );

		GLdouble eyex = m_vfAbsolute[ 12 ];
		GLdouble eyey = m_vfAbsolute[ 13 ];
		GLdouble eyez = m_vfAbsolute[ 14 ];
		GLdouble centerx = eyex - m_vfAbsolute[ 8 ];
		GLdouble centery = eyey - m_vfAbsolute[ 9 ];
		GLdouble centerz = eyez - m_vfAbsolute[ 10 ];
		GLdouble upx = m_vfAbsolute[ 4 ];
		GLdouble upy = m_vfAbsolute[ 5 ];
		GLdouble upz = m_vfAbsolute[ 6 ];

		CHECK_GL( gluLookAt( eyex, eyey, eyez, centerx, centery, centerz, upx, upy, upz ) );
		CHECK_GL( glGetDoublev( GL_MODELVIEW_MATRIX , m_vfView ) );
#endif
		m_bIsViewDirty = false;
		return S_OK;
	}

protected:
	double m_fNearClippingPlaneDistance;
	double m_fFarClippingPlaneDistance;
#if defined DX7_RENDERER
	virtual int GetPickProjection( long nX, long nY, const Viewport& iViewport, D3DMATRIX& sPickProjection ) = 0;
	int UpdatePickPlane( long nX, long nY, const Viewport& iViewport, double& fPlaneMinX, double& fPlaneMinY, double& fPlaneMaxX, double& fPlaneMaxY ) {
		double fPlaneWidth = ( fPlaneMaxX-fPlaneMinX ) / iViewport.nWidth;
		double fPlaneHeight = ( fPlaneMaxY-fPlaneMinY ) / iViewport.nHeight;
		fPlaneMinX = fPlaneMinX + (nX-iViewport.nX)*fPlaneWidth;
		fPlaneMaxX = fPlaneMinX + fPlaneWidth;
		fPlaneMinY = fPlaneMinY + (nY-iViewport.nY)*fPlaneHeight;
		fPlaneMaxY = fPlaneMinY + fPlaneHeight;
		return S_OK;
	}

	D3DMATRIX m_sD3DMProjection;
	D3DMATRIX m_sD3DMView;
	D3DVECTOR m_sD3DDirection;
	float m_fDotProductLocationByDirection;
#elif defined OPENGL_RENDERER
	double m_vfProjection[16];
	double m_vfView[16];
#endif
private:
	Background* m_pBackground;
	bool m_bIsProjectionDirty;
	bool m_bIsViewDirty;

	int m_nWidth;
	int m_nHeight;
};

#endif