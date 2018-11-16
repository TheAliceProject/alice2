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

#ifndef FOG_INCLUDED
#define FOG_INCLUDED

#include "Affector.hpp"
class Fog : public Affector {
public:
	Fog() : Affector() {
		OnColorChange( 1, 1, 1, 1 );
	}
	int OnColorChange( double r, double g, double b, double a ) {
#if defined DX7_RENDERER
		m_dwColor = D3DRGB( r, g, b );
#elif defined OPENGL_RENDERER
		m_vfColor[ 0 ] = (GLfloat)r;
		m_vfColor[ 1 ] = (GLfloat)g;
		m_vfColor[ 2 ] = (GLfloat)b;
		m_vfColor[ 3 ] = (GLfloat)a;
#endif
		return S_OK;
	}
	int Enable( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGENABLE, TRUE ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glEnable( GL_FOG ) );
#endif
		return S_OK;
	}
	int Disable( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGENABLE, FALSE ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glDisable( GL_FOG ) );
#endif
		return S_OK;
	}
	virtual int Setup( RenderTarget* pRenderTarget, void* pContext ) {
		Enable( pRenderTarget, pContext );
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGCOLOR,  m_dwColor ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glFogfv( GL_FOG_COLOR, m_vfColor ) );
#endif
		return S_OK;
	}
private:
#if defined DX7_RENDERER
	DWORD m_dwColor;
#elif defined OPENGL_RENDERER
	GLfloat m_vfColor[ 4 ];
#endif
};

#endif
	
