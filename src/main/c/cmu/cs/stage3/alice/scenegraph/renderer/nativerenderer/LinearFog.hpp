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

#ifndef LINEAR_FOG_INCLUDED
#define LINEAR_FOG_INCLUDED

#include "Fog.hpp"
class LinearFog : public Fog {
public:
	LinearFog() : Fog() {
		OnNearDistanceChange( 0 );
		OnFarDistanceChange( 1 );
	}
	int OnNearDistanceChange( double value ) {
#if defined DX7_RENDERER
		m_dwStart = FtoDW((float)value);
#elif defined OPENGL_RENDERER
#endif
		return 0;
	}
	int OnFarDistanceChange( double value ) {
#if defined DX7_RENDERER
		m_dwEnd = FtoDW((float)value);
#elif defined OPENGL_RENDERER
#endif
		return 0;
	}
	int Setup( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		//todo
		if( false ) {
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGTABLEMODE, D3DFOG_LINEAR ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGTABLESTART, m_dwStart ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGTABLEEND, m_dwEnd ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGVERTEXMODE,  D3DFOG_NONE ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_RANGEFOGENABLE, FALSE ) );
		} else {
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGTABLEMODE, D3DFOG_NONE ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGVERTEXMODE,  D3DFOG_LINEAR ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGSTART, m_dwStart ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGEND, m_dwEnd ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_RANGEFOGENABLE, FALSE ) );
		}
#elif defined OPENGL_RENDERER
		CHECK_GL( glFogi( GL_FOG_MODE, GL_LINEAR ) );
#endif
		return Fog::Setup( pRenderTarget, pContext );
	}
private:
#if defined DX7_RENDERER
	DWORD m_dwStart;
	DWORD m_dwEnd;
#elif defined OPENGL_RENDERER
#endif
};

#endif