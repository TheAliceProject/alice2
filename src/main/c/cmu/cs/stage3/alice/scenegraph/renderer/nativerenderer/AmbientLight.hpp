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

#ifndef AMBIENT_LIGHT_INCLUDED
#define AMBIENT_LIGHT_INCLUDED

#include "Light.hpp"

class AmbientLight : public Light {
public:
	int Enable( RenderTarget* pRenderTarget, void* pContext ) {
		//override and do not call super
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
	    CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_AMBIENT, D3DRGB( m_vfColor[0]*m_fBrightness, m_vfColor[1]*m_fBrightness, m_vfColor[2]*m_fBrightness ) ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glLightModelfv( GL_LIGHT_MODEL_AMBIENT, m_vfColor ) );
#endif
		return S_OK;
	}
	int Disable( RenderTarget* pRenderTarget, void* pContext ) {
		//override and do not call super
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
	    CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_AMBIENT, 0 ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glLightModelfv( GL_LIGHT_MODEL_AMBIENT, g_vfBlack ) );
#endif
		return S_OK;
	}
	int Setup( RenderTarget* pRenderTarget, void* pContext ) {
		//override and do not call super
		Enable( pRenderTarget, pContext );
		return S_OK;
	}
};

#endif