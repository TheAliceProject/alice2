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

#ifndef CLIPPING_PLANE_INCLUDED
#define CLIPPING_PLANE_INCLUDED

#include "Affector.hpp"

class ClippingPlane : public Affector {
public:
	int Enable( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		CHECK_SUCCESS( pRenderTarget->EnableClippingPlane( (LPDIRECT3DDEVICE7)pContext, m_nID ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glEnable( m_nID ) );
#endif
		return S_OK;
	}
	int Disable( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		CHECK_SUCCESS( pRenderTarget->DisableClippingPlane( (LPDIRECT3DDEVICE7)pContext, m_nID ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glDisable( m_nID ) );
#endif
		return S_OK;
	}
	int Setup( RenderTarget* pRenderTarget, void* pContext ) {
		CHECK_SUCCESS( pRenderTarget->GetNextClippingPlaneID( m_nID ) );
		CHECK_SUCCESS( Enable( pRenderTarget, pContext ) );
#if defined DX7_RENDERER
		D3DVALUE clip[4];
		clip[0] = m_sD3DMAbsolute._31;
		clip[1] = m_sD3DMAbsolute._32;
		clip[2] = m_sD3DMAbsolute._33;
		clip[3] = -( m_sD3DMAbsolute._31*m_sD3DMAbsolute._41 + m_sD3DMAbsolute._32*m_sD3DMAbsolute._42 + m_sD3DMAbsolute._33*m_sD3DMAbsolute._43 );
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->SetClipPlane( m_nID, clip ) );
#elif defined OPENGL_RENDERER
		double vfEquation[4];
		vfEquation[0] = m_vfAbsolute[ 8 ];
		vfEquation[1] = m_vfAbsolute[ 9 ];
		vfEquation[2] = m_vfAbsolute[ 10 ];
		vfEquation[3] = -( m_vfAbsolute[ 8 ]*m_vfAbsolute[ 12 ] + m_vfAbsolute[ 9 ]*m_vfAbsolute[ 13 ] + m_vfAbsolute[ 10 ]*m_vfAbsolute[ 14 ] );
		CHECK_GL( glClipPlane( m_nID, vfEquation ) );
#endif
		return S_OK;
	}
private:
	ClippingPlaneID m_nID;
};

#endif