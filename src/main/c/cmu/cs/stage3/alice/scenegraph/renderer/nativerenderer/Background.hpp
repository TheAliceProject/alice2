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

#ifndef BACKGROUND_INCLUDED
#define BACKGROUND_INCLUDED

#include "Element.hpp"
#include "RenderTarget.hpp"
#include "Viewport.hpp"

class TextureMap;

class Background : public Element {
public:
	Background() : Element() {
		OnColorChange( 0,0,0,1 );
		OnTextureMapChange( NULL );
	}

	int OnColorChange( double r, double g, double b, double a ) {
#if defined DX7_RENDERER
		m_dwColor = D3DRGB( r, g, b );
#elif defined OPENGL_RENDERER
		m_fRed = (GLfloat)r;
		m_fGreen = (GLfloat)g;
		m_fBlue = (GLfloat)b;
		m_fAlpha = (GLfloat)a;
#endif
		return S_OK;
	}
	
	int OnTextureMapChange( TextureMap* value ) {
		m_pTextureMap = NULL;
		return S_OK;
	}
	
	int OnTextureMapSourceRectangleChange( int x, int y, int width, int height ) {
		return S_OK;
	}
	
	int ClearColorBufferToBlack( RenderTarget* pRenderTarget, void* pContext, const Viewport& iViewport ) {
#if defined DX7_RENDERER
		DWORD flags = D3DCLEAR_TARGET;
		RECT rc;
		rc.left = iViewport.nX;
		rc.top = iViewport.nY;
		rc.right = iViewport.nX + iViewport.nWidth;
		rc.bottom = iViewport.nY + iViewport.nHeight;
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->Clear( 1, (LPD3DRECT)&rc, flags, 0, 1.f, 0 ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glClearColor( 0, 0, 0, 0 ) );
		CHECK_GL( glScissor( iViewport.nX, iViewport.nY, iViewport.nWidth, iViewport.nHeight ) );
		CHECK_GL( glEnable( GL_SCISSOR_TEST ) );
		CHECK_GL( glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ) );
		CHECK_GL( glDisable( GL_SCISSOR_TEST ) );
#endif
		return S_OK;
	}
	int Clear( RenderTarget* pRenderTarget, void* pContext, const Viewport& iViewport, bool bClearColor, bool bClearZ, bool bClearStencil ) {
#if defined DX7_RENDERER
		DWORD flags = 
			(bClearColor?D3DCLEAR_TARGET:0) |
			(bClearZ?D3DCLEAR_ZBUFFER:0) |
			(bClearStencil?D3DCLEAR_STENCIL:0);
		
		RECT rc;
		rc.left = iViewport.nX;
		rc.top = iViewport.nY;
		rc.right = iViewport.nX + iViewport.nWidth;
		rc.bottom = iViewport.nY + iViewport.nHeight;

		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->Clear( 1, (LPD3DRECT)&rc, flags, m_dwColor, 1.f, 0 ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glClearColor( m_fRed, m_fGreen, m_fBlue, m_fAlpha ) );
		CHECK_GL( glScissor( iViewport.nX, iViewport.nY, iViewport.nWidth, iViewport.nHeight ) );
		CHECK_GL( glEnable( GL_SCISSOR_TEST ) );
		CHECK_GL( glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ) );
		CHECK_GL( glDisable( GL_SCISSOR_TEST ) );
#endif
		return S_OK;
	}

private:
	TextureMap* m_pTextureMap;
#if defined DX7_RENDERER
	DWORD m_dwColor;
#elif defined OPENGL_RENDERER
	GLfloat m_fRed;
	GLfloat m_fGreen;
	GLfloat m_fBlue;
	GLfloat m_fAlpha;
#endif
};

#endif