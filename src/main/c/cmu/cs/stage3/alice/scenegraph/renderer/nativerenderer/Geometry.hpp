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

#ifndef GEOMETRY_INCLUDED
#define GEOMETRY_INCLUDED

#include "Element.hpp"
#include "Renderer.hpp"
#include "RenderTarget.hpp"

class Geometry : public Element {
public:
	Geometry() : Element() {
		OnBoundChange( 0,0,0, 0 );
	}
#if defined DX7_RENDERER
	virtual int ReverseLighting() = 0;
#elif defined OPENGL_RENDERER
#endif
	virtual int Render( RenderTarget* pRenderTarget, void* pContext ) = 0;
	virtual int Pick( void* pContext, bool isSubElementRequired, bool bIsFrontFacingAppearance ) = 0;

	int OnBoundChange( double fX, double fY, double fZ, double fRadius ) {
#if defined DX7_RENDERER
		m_sD3DBoundingSphereCenter.x = (D3DVALUE)fX;
		m_sD3DBoundingSphereCenter.y = (D3DVALUE)fY;
		m_sD3DBoundingSphereCenter.z = (D3DVALUE)fZ;
		m_fBoundingSphereRadius = (float)fRadius;
#elif defined OPENGL_RENDERER
#endif
		return S_OK;
	}
	int ComputeVisibility( void* pContext, bool& bIsVisible ) {
#if defined DX7_RENDERER
		if( IsNaN( m_fBoundingSphereRadius ) || IsNaN( m_sD3DBoundingSphereCenter.x ) ) {
			//todo
			bIsVisible = true;
		} else {
			LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
			DWORD dwResult;
			CHECK_SUCCESS( pD3DDevice->ComputeSphereVisibility( &m_sD3DBoundingSphereCenter, &m_fBoundingSphereRadius, 1, 0, &dwResult ) );
			bIsVisible = !( dwResult & D3DSTATUS_CLIPINTERSECTIONALL );
		}
#elif defined OPENGL_RENDERER
		bIsVisible = true;
#endif
		return S_OK;
	}
private:
#if defined DX7_RENDERER
	D3DVECTOR m_sD3DBoundingSphereCenter;
	float m_fBoundingSphereRadius;
#elif defined OPENGL_RENDERER
#endif
};

#endif