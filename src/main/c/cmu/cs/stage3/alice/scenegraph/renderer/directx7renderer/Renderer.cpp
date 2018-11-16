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

#include "Renderer.hpp"
#include "Component.hpp"
#include "Visual.hpp"
#include "Scene.hpp"

D3DMATRIX g_sD3DMIdentity;
static HINSTANCE g_hInstance = NULL;
int HACK_g_nVisualID;

static BOOL WINAPI DisplayDriverCallback( GUID FAR *pGUID, LPSTR lpDriverDescription, LPSTR lpDriverName, LPVOID lpContext, HMONITOR hMonitor ) {
	Renderer* pRenderer = (Renderer*)lpContext;
	return pRenderer->OnDisplayDriverEnum( pGUID, lpDriverDescription, lpDriverName, hMonitor );
}

int Renderer::Enumerate() {
	CHECK_SUCCESS( DirectDrawEnumerateEx( DisplayDriverCallback, this,
		DDENUM_NONDISPLAYDEVICES | DDENUM_ATTACHEDSECONDARYDEVICES | DDENUM_DETACHEDSECONDARYDEVICES ) );
	return S_OK;
}

int Renderer::CommitPickIfNecessary() {
	if( m_pDDSurfacePickColor ) {
		//pass
	} else {
		DisplayDriver* pDefaultDisplayDriver;
		CHECK_SUCCESS( GetDefaultDisplayDriver( pDefaultDisplayDriver ) );
		CHECK_NOT_NULL( pDefaultDisplayDriver );

		LPDIRECTDRAW7 pDD;
		CHECK_SUCCESS( pDefaultDisplayDriver->GetDD( pDD ) );
		CHECK_NOT_NULL( pDD );
		LPDIRECT3D7 pD3D;
		CHECK_SUCCESS( pDefaultDisplayDriver->GetD3D( pD3D ) );
		CHECK_NOT_NULL( pD3D );

		DisplayDevice* pRGBDisplayDevice;
		CHECK_SUCCESS( pDefaultDisplayDriver->GetRGBDisplayDevice( pRGBDisplayDevice ) );
		CHECK_NOT_NULL( pRGBDisplayDevice );

		DDSURFACEDESC2 sDDSDPickColor;
		memset( &sDDSDPickColor, 0, sizeof( DDSURFACEDESC2 ) );
		sDDSDPickColor.dwSize = sizeof( DDSURFACEDESC2 );
		sDDSDPickColor.dwFlags = DDSD_CAPS | DDSD_WIDTH | DDSD_HEIGHT | DDSD_PIXELFORMAT;
		sDDSDPickColor.ddsCaps.dwCaps = DDSCAPS_OFFSCREENPLAIN | DDSCAPS_3DDEVICE;
		sDDSDPickColor.dwWidth = 1; 
		sDDSDPickColor.dwHeight = 1;
		sDDSDPickColor.ddpfPixelFormat.dwSize = sizeof( DDPIXELFORMAT );
		sDDSDPickColor.ddpfPixelFormat.dwFlags = DDPF_ALPHAPIXELS | DDPF_RGB;
		sDDSDPickColor.ddpfPixelFormat.dwRGBBitCount = 32;
		sDDSDPickColor.ddpfPixelFormat.dwRBitMask = 0x00FF0000;
		sDDSDPickColor.ddpfPixelFormat.dwGBitMask = 0x0000FF00;
		sDDSDPickColor.ddpfPixelFormat.dwBBitMask = 0x000000FF;
		sDDSDPickColor.ddpfPixelFormat.dwRGBAlphaBitMask = 0xFF000000;

		CHECK_SUCCESS( pDD->CreateSurface( &sDDSDPickColor, &m_pDDSurfacePickColor, NULL ) );
		CHECK_NOT_NULL( m_pDDSurfacePickColor );

		DDSURFACEDESC2 sDDSDPickZ;
		memset( &sDDSDPickZ, 0, sizeof( DDSURFACEDESC2 ) );
		sDDSDPickZ.dwSize = sizeof( DDSURFACEDESC2 );
		sDDSDPickZ.dwFlags = DDSD_CAPS | DDSD_WIDTH | DDSD_HEIGHT | DDSD_PIXELFORMAT;
		sDDSDPickZ.ddsCaps.dwCaps = DDSCAPS_ZBUFFER;
		sDDSDPickZ.dwWidth = 1;
		sDDSDPickZ.dwHeight = 1;
		if( pRGBDisplayDevice->GetZBufferPixelFormat( 32, sDDSDPickZ.ddpfPixelFormat ) < 0 ) {
			CHECK_SUCCESS( pRGBDisplayDevice->GetZBufferPixelFormat( 16, sDDSDPickZ.ddpfPixelFormat ) );
		}

		sDDSDPickZ.ddsCaps.dwCaps |= DDSCAPS_SYSTEMMEMORY;

		// Create ZBuffer
		CHECK_SUCCESS( pDD->CreateSurface( &sDDSDPickZ, &m_pDDSurfacePickZ, NULL ) );
		CHECK_NOT_NULL( m_pDDSurfacePickZ );

		// Attach ZBuffer
		CHECK_SUCCESS( m_pDDSurfacePickColor->AddAttachedSurface( m_pDDSurfacePickZ ) );

		CHECK_SUCCESS( pD3D->CreateDevice( IID_IDirect3DRGBDevice, m_pDDSurfacePickColor, &m_pD3DDevicePick ) );
		CHECK_NOT_NULL( m_pD3DDevicePick );

		CHECK_SUCCESS( m_pD3DDevicePick->SetRenderTarget( m_pDDSurfacePickColor, 0 ) );

		D3DVIEWPORT7 vp = { 0, 0, 1, 1, 0.f, 1.f };
		CHECK_SUCCESS( m_pD3DDevicePick->SetViewport( &vp ) );
	}
	CHECK_NOT_NULL( m_pDDSurfacePickColor );
	if( m_pDDSurfacePickColor->IsLost() ) {
		CHECK_SUCCESS( m_pDDSurfacePickColor->Restore() );
	}
	CHECK_NOT_NULL( m_pDDSurfacePickZ );
	if( m_pDDSurfacePickZ->IsLost() ) {
		CHECK_SUCCESS( m_pDDSurfacePickZ->Restore() );
	}
	return S_OK;
}

int Renderer::PerformPick( _Component* pComponent, const D3DMATRIX& sD3DMProjection, const D3DMATRIX& sD3DMView, bool bIsSubElementRequired, bool bIsOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ ) {
	LPDIRECT3DDEVICE7 pD3DPickDevice;

	CHECK_SUCCESS( GetPickDevice( pD3DPickDevice ) );
	CHECK_SUCCESS( pD3DPickDevice->Clear( 0, NULL, D3DCLEAR_TARGET | D3DCLEAR_ZBUFFER, -1, 1.f, 0 ) ); 

	CHECK_SUCCESS( pD3DPickDevice->SetRenderState( D3DRENDERSTATE_ZENABLE, TRUE ) );
	CHECK_SUCCESS( pD3DPickDevice->SetRenderState( D3DRENDERSTATE_ALPHABLENDENABLE, FALSE ) );
	CHECK_SUCCESS( pD3DPickDevice->SetRenderState( D3DRENDERSTATE_LIGHTING, FALSE ) );
	CHECK_SUCCESS( pD3DPickDevice->SetRenderState( D3DRENDERSTATE_NORMALIZENORMALS, FALSE ) );
	//CHECK_SUCCESS( pD3DPickDevice->SetRenderState( D3DRENDERSTATE_NORMALIZENORMALS, TRUE ) );

	D3DVIEWPORT7 vp = { 0, 0, 1, 1, 0.f, 1.f };
	CHECK_SUCCESS( pD3DPickDevice->SetViewport( &vp ) );

	CHECK_SUCCESS( pD3DPickDevice->SetTransform( D3DTRANSFORMSTATE_PROJECTION, (D3DMATRIX*)&sD3DMProjection ) );

	CHECK_SUCCESS( pD3DPickDevice->SetTransform( D3DTRANSFORMSTATE_VIEW, (D3DMATRIX*)&sD3DMView ) );

	Scene* pScene;
	CHECK_SUCCESS( pComponent->GetScene( pScene ) );

	int nValue;
	if( pScene ) {
		CHECK_SUCCESS( pD3DPickDevice->BeginScene() );
		CHECK_SUCCESS_WITH_CLEANUP( pScene->Pick( pD3DPickDevice, bIsSubElementRequired, bIsOnlyFrontMostRequired ), pD3DPickDevice->EndScene );
		CHECK_SUCCESS( pD3DPickDevice->EndScene() );
		CHECK_SUCCESS( PickLookup( nValue ) );
	} else {
		fprintf( stderr, "NO SCENE\n" );
		fflush( stderr );
		nValue = 0xFFFFFFFF;
	}
	_VisualID nVisualID = nValue & 0x7FFF;
	bIsFrontFacingAppearance = ( nValue & ( 1<<15 ) ) != 0;
	nSubElement = (nValue >> 16);
	if( nVisualID==0x7FFF ) {
		pVisual = NULL;
	} else {
		CHECK_SUCCESS( pScene->GetVisual( nVisualID, pVisual ) );
	}
	return S_OK;
}

int Renderer::Pick( _Component* pComponent, double fVectorX, double fVectorY, double fVectorZ, double fPlaneMinX, double fPlaneMinY, double fPlaneMaxX, double fPlaneMaxY, double fNearClippingPlaneDistance, double fFarClippingPlaneDistance, bool bIsSubElementRequired, bool bIsOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ ) {
	D3DMATRIX sD3DMAbsolute;
	D3DMATRIX sD3DMView;
	D3DMATRIX sD3DMProjection;
	//fprintf( stderr, "%lf %lf %lf %lf\n", fPlaneMinX, fPlaneMinY, fPlaneMaxX, fPlaneMaxY );
	CHECK_SUCCESS( pComponent->GetAbsoluteTransformation( sD3DMAbsolute ) );
	CHECK_SUCCESS( ::UpdateViewMatrix( sD3DMView, sD3DMAbsolute ) );
	D3DXMatrixOrthoOffCenterLH( (D3DXMATRIX*)&sD3DMProjection, (float)fPlaneMinX, (float)fPlaneMaxX, (float)fPlaneMinY, (float)fPlaneMaxY, (float)fNearClippingPlaneDistance, (float)fFarClippingPlaneDistance ); 
	CHECK_SUCCESS( PerformPick( pComponent, sD3DMProjection, sD3DMView, bIsSubElementRequired, bIsOnlyFrontMostRequired, pVisual, bIsFrontFacingAppearance, nSubElement, fZ ) );
	return S_OK;
}


HINSTANCE GetInstance() {
	return g_hInstance;
}

BOOL APIENTRY DllMain( HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved ) {
	D3DUtil_SetIdentityMatrix( g_sD3DMIdentity );
    switch (ul_reason_for_call) {
	case DLL_PROCESS_ATTACH:
		g_hInstance = (HINSTANCE)hModule;
		break;
	case DLL_THREAD_ATTACH:
		break;
	case DLL_THREAD_DETACH:
		break;
	case DLL_PROCESS_DETACH:
		break;
    }
    return TRUE;
}

