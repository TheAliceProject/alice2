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

#ifndef EXPONENTIAL_FOG_INCLUDED
#define EXPONENTIAL_FOG_INCLUDED

#include "Fog.hpp"
class ExponentialFog : public Fog {
public:
	ExponentialFog() : Fog() {
		OnDensityChange( 0 );
	}
	int OnDensityChange( double value ) {
#if defined DX7_RENDERER
		m_dwDensity = FtoDW((float)value);
#elif defined OPENGL_RENDERER
#endif
		return S_OK;
	}
	int Setup( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGTABLEMODE,   D3DFOG_EXP ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGDENSITY, m_dwDensity ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGVERTEXMODE,  D3DFOG_NONE ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_RANGEFOGENABLE, FALSE ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glFogi( GL_FOG_MODE, GL_EXP ) );
#endif
		return Fog::Setup( pRenderTarget, pContext );
	}
private:
#if defined DX7_RENDERER
	DWORD m_dwDensity;
#elif defined OPENGL_RENDERER
#endif
};

#endif