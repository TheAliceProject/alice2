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

#include "Camera.hpp"
#include "Renderer.hpp"
#include "RenderTarget.hpp"
#include "Background.hpp"
#include "Scene.hpp"

int Camera::Clear( RenderTarget* pRenderTarget, void* pContext, const Viewport& iOuterViewport, const Viewport& iActualViewport ) {
	Background* pBackground;
	if( m_pBackground ) {
		pBackground = m_pBackground;
	} else {
		if( m_pScene ) {
			CHECK_SUCCESS( m_pScene->GetBackground( pBackground ) );
		} else {
			pBackground = NULL;
		}
	}
	if( pBackground ) {
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		if( iOuterViewport != iActualViewport ) {
			D3DVIEWPORT7 vp = { iOuterViewport.nX, iOuterViewport.nY, iOuterViewport.nWidth, iOuterViewport.nHeight, 0.f, 1.f };
			CHECK_SUCCESS( pD3DDevice->SetViewport( &vp ) );
			//todo: only clear difference
			CHECK_SUCCESS( pBackground->ClearColorBufferToBlack( pRenderTarget, pContext, iOuterViewport ) );
		}
		D3DVIEWPORT7 vp = { iActualViewport.nX, iActualViewport.nY, iActualViewport.nWidth, iActualViewport.nHeight, 0.f, 1.f };
		CHECK_SUCCESS( pD3DDevice->SetViewport( &vp ) );
		CHECK_SUCCESS( pBackground->Clear( pRenderTarget, pContext, iActualViewport, true, true, false ) );
	}
	return S_OK;
}

int Camera::Setup( RenderTarget* pRenderTarget, void* pContext, const Viewport& iViewport ) {
	//MessageBox( NULL, "Testing", "Checkpoint", MB_OK );
	LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;

	D3DVIEWPORT7 vp = { iViewport.nX, iViewport.nY, iViewport.nWidth, iViewport.nHeight, 0.f, 1.f };
	CHECK_SUCCESS( pD3DDevice->SetViewport( &vp ) );

	CHECK_SUCCESS( UpdateIfNecessary( iViewport ) );
	
	CHECK_SUCCESS( pD3DDevice->SetTransform( D3DTRANSFORMSTATE_PROJECTION, &m_sD3DMProjection ) );
	CHECK_SUCCESS( pD3DDevice->SetTransform( D3DTRANSFORMSTATE_VIEW, &m_sD3DMView ) );
	return S_OK;
}

int Camera::Pick( Renderer* pRenderer, long nX, long nY, const Viewport& iViewport, bool isSubElementRequired, bool isOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ ) {
	CHECK_SUCCESS( UpdateIfNecessary( iViewport ) );
	D3DMATRIX sD3DMPickProjection;
	CHECK_SUCCESS( GetPickProjection( nX, nY, iViewport, sD3DMPickProjection ) );
	CHECK_SUCCESS( pRenderer->PerformPick( this, sD3DMPickProjection, m_sD3DMView, isSubElementRequired, isOnlyFrontMostRequired, pVisual, bIsFrontFacingAppearance, nSubElement, fZ ) );
	return S_OK;
}
