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
#include "DisplayDriver.hpp"

static HWND g_hExclusivePopupWnd = NULL;

static HRESULT WINAPI DisplayModeCallback( LPDDSURFACEDESC2 lpDDSurfaceDesc, LPVOID lpContext) {
	DisplayDriver* pDisplayDriver = (DisplayDriver*)lpContext;
	return pDisplayDriver->OnFullscreenDisplayModeEnum( lpDDSurfaceDesc );
}

static HRESULT WINAPI DisplayDeviceCallback( LPSTR lpDeviceDescription, LPSTR lpDeviceName, LPD3DDEVICEDESC7 lpDeviceDesc, LPVOID lpContext ) {
	DisplayDriver* pDisplayDriver = (DisplayDriver*)lpContext;
	return pDisplayDriver->OnDisplayDeviceEnum( lpDeviceDescription, lpDeviceName, lpDeviceDesc );
}

int DisplayDriver::Enumerate() {
	LPDIRECTDRAW7 pDD;
	LPDIRECT3D7 pD3D;

	CHECK_SUCCESS( DirectDrawCreateEx( m_pGUID, (void**)&pDD, IID_IDirectDraw7, NULL ) );

	// Enumerate the modes for this adapter
	CHECK_SUCCESS( pDD->EnumDisplayModes( DDEDM_REFRESHRATES, NULL, this, DisplayModeCallback ) );

	// Query for D3D
	CHECK_SUCCESS( pDD->QueryInterface( IID_IDirect3D7, (VOID**)&pD3D ) );
	CHECK_SUCCESS( pD3D->EnumDevices( DisplayDeviceCallback, this ) );

	DDCAPS ddcapsHardware;
	ddcapsHardware.dwSize = sizeof(DDCAPS);
	DDCAPS ddcapsSoftware;
	ddcapsSoftware.dwSize = sizeof(DDCAPS);
	CHECK_SUCCESS( pDD->GetCaps( &ddcapsHardware, &ddcapsSoftware ) );

	m_bSupportsOverlapped = ( ddcapsHardware.dwCaps2 & DDCAPS2_CANRENDERWINDOWED )!=0;
	//todo? why do they all report supporting overlapped?

	SAFE_RELEASE( pD3D );
	SAFE_RELEASE( pDD );
	return 0;
}

int DisplayDriver::CommitIfNecessary() {
	if( m_pDD==NULL ) {
		CHECK_SUCCESS( DirectDrawCreateEx( m_pGUID, (void**)&m_pDD, IID_IDirectDraw7, NULL ) );
		CHECK_SUCCESS( m_pDD->SetCooperativeLevel( 0, DDSCL_NORMAL ) );
	}
	if( m_pD3D==NULL ) {
		CHECK_SUCCESS( m_pDD->QueryInterface( IID_IDirect3D7, (VOID**)&m_pD3D ) );
	}
	return S_OK;
}

int DisplayDriver::CommitPrimaryIfNecessary() {
	CHECK_SUCCESS( CommitIfNecessary() );
	if( m_pDDSurfacePrimary==NULL ) {
		if( m_bHasExclusiveRenderTarget ) {
			DDSURFACEDESC2 sDDSDPrimary;
			memset( &sDDSDPrimary, 0, sizeof( DDSURFACEDESC2 ) );
			sDDSDPrimary.dwSize = sizeof( DDSURFACEDESC2 );
			sDDSDPrimary.dwFlags = DDSD_CAPS | DDSD_BACKBUFFERCOUNT;
			sDDSDPrimary.ddsCaps.dwCaps = DDSCAPS_PRIMARYSURFACE | DDSCAPS_FLIP | DDSCAPS_COMPLEX | DDSCAPS_3DDEVICE;
			sDDSDPrimary.dwBackBufferCount = 1;
			CHECK_SUCCESS( m_pDD->CreateSurface( &sDDSDPrimary, &m_pDDSurfacePrimary, NULL ) );
		} else {
			LPDIRECTDRAWCLIPPER pDDClipper;
			CHECK_SUCCESS( m_pRenderer->GetDDClipper( pDDClipper) );

			DDSURFACEDESC2 sDDSDPrimary;
			memset( &sDDSDPrimary, 0, sizeof( DDSURFACEDESC2 ) );
			sDDSDPrimary.dwSize = sizeof( DDSURFACEDESC2 );
			sDDSDPrimary.dwFlags = DDSD_CAPS;
			sDDSDPrimary.ddsCaps.dwCaps = DDSCAPS_PRIMARYSURFACE;

			CHECK_SUCCESS( m_pDD->CreateSurface( &sDDSDPrimary, &m_pDDSurfacePrimary, NULL ) );
			CHECK_SUCCESS( m_pDDSurfacePrimary->SetClipper( pDDClipper ) );
		}
	}
	return S_OK;
}

LRESULT DisplayDriver::WindowProc( HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam ) {
	return DefWindowProc( hWnd, uMsg, wParam, lParam );
}

LRESULT CALLBACK DisplayDriverWindowProc( HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam ) {
	switch( uMsg ){
    case WM_NCHITTEST:
		// Prevent the user from selecting the menu in fullscreen mode
		return HTCLIENT;
		break;
	case WM_KEYDOWN:
		// Check for Escape
		if( wParam == VK_ESCAPE ) 
			PostMessage( hWnd, WM_CLOSE, 0, 0 );
		break;
	case WM_SYSKEYDOWN:
		// Check for Alt-Enter
		if( wParam == VK_RETURN )
			PostMessage( hWnd, WM_CLOSE, 0, 0 );
		break;
	case WM_ACTIVATE:
		//cerr << "active=" << LOWORD(wParam) << "  minimized=" << (BOOL)HIWORD(wParam) << endl;
		break;
    case WM_SYSCOMMAND:
        // Prevent moving/sizing and power loss in fullscreen mode
        switch( wParam )
        {
            case SC_MOVE:
            case SC_SIZE:
            case SC_MAXIMIZE:
            case SC_MONITORPOWER:
                return 1;
                break;
        }
        break;
    case WM_CLOSE:
        DestroyWindow( hWnd );
        return 0;
    case WM_DESTROY:
		//g_hExclusivePopupWnd = NULL;
		//todo
		return 0;
	}
	return DefWindowProc( hWnd, uMsg, wParam, lParam );
}

int DisplayDriver::DisplayVideoMemoryDiagnosticInformation() {
	/*
	DDSCAPS2      ddsCaps2; 
	DWORD         dwTotal; 
	DWORD         dwFree;
	ZeroMemory(&ddsCaps2, sizeof(ddsCaps2));
	//ddsCaps2.dwCaps = DDSCAPS_TEXTURE; 
	ddsCaps2.dwCaps = DDSCAPS_VIDEOMEMORY;
	CHECK_SUCCESS( m_pDD->GetAvailableVidMem( &ddsCaps2, &dwTotal, &dwFree ) ); 
	if( m_pDDSurfacePrimary ) {
		char vcText[ 1024 ];
		sprintf( vcText, "Available Video Memory: %d bytes out of %d bytes", dwFree, dwTotal );

		HDC hDC;
		m_pDDSurfacePrimary->GetDC( &hDC );

		int nX = 20;
		int nY = 40;
		int nWidth = 640;
		int nHeight = 20;

		RECT sRect;
		sRect.left = nX;
		sRect.top = nY;
		sRect.right = nX + nWidth * ( dwFree / (double)dwTotal );
		sRect.bottom = nY + nHeight;

		::FillRect( hDC, &sRect, (HBRUSH)GetStockObject( BLACK_BRUSH ) );

		sRect.left = sRect.right;
		sRect.right = nX + nWidth;

		::FillRect( hDC, &sRect, (HBRUSH)GetStockObject( WHITE_BRUSH ) );

		::TextOut( hDC, nX, sRect.bottom + 10, vcText, strlen( vcText ) );

		m_pDDSurfacePrimary->ReleaseDC( hDC );

	} 
	*/
	return S_OK;
}

int DisplayDriver::AcquireExclusive( FullscreenDisplayMode* pFullscreenDisplayMode, LPDIRECTDRAWSURFACE7& pDDSurfaceBackBuffer ) {
	CHECK_TRUTH( !m_bHasExclusiveRenderTarget );
	if( g_hExclusivePopupWnd==NULL ) {
		WNDCLASSEX wcex;
		wcex.cbSize = sizeof(WNDCLASSEX); 
		wcex.style			= CS_HREDRAW | CS_VREDRAW;
		wcex.lpfnWndProc	= (WNDPROC)DisplayDriverWindowProc;
		wcex.cbClsExtra		= 0;
		wcex.cbWndExtra		= 0;
		wcex.hInstance		= GetInstance();
		wcex.hIcon			= NULL;
		wcex.hCursor		= LoadCursor(NULL, IDC_ARROW);
		wcex.hbrBackground	= (HBRUSH)(COLOR_WINDOW+1);
		wcex.lpszMenuName	= NULL;
		wcex.lpszClassName	= "DX Popup";
		wcex.hIconSm		= NULL;
		RegisterClassEx(&wcex);
		g_hExclusivePopupWnd = CreateWindow( wcex.lpszClassName, "DX Popup", WS_POPUP, 0, 0, 100, 100, NULL, NULL, GetInstance(), NULL );
		ShowWindow( g_hExclusivePopupWnd, SW_SHOW);
		UpdateWindow( g_hExclusivePopupWnd );
	}
	bool bIsFirstExclusive;
	CHECK_SUCCESS( m_pRenderer->IncrementExclusiveCount( bIsFirstExclusive ) );
	SAFE_RELEASE( m_pDDSurfacePrimary );
	CHECK_SUCCESS( m_pD3D->EvictManagedTextures() );

	if( bIsFirstExclusive ) {
		DWORD dwFlags = DDSCL_SETFOCUSWINDOW;
        CHECK_SUCCESS( m_pDD->SetCooperativeLevel( g_hExclusivePopupWnd, dwFlags ) );
        dwFlags = DDSCL_ALLOWREBOOT | DDSCL_EXCLUSIVE | DDSCL_FULLSCREEN | DDSCL_NOWINDOWCHANGES;
        CHECK_SUCCESS( m_pDD->SetCooperativeLevel( g_hExclusivePopupWnd, dwFlags ) );
	} else {
		DWORD dwFlags = DDSCL_SETFOCUSWINDOW | DDSCL_CREATEDEVICEWINDOW | DDSCL_NOWINDOWCHANGES | DDSCL_ALLOWREBOOT | DDSCL_EXCLUSIVE | DDSCL_FULLSCREEN;;
        CHECK_SUCCESS( m_pDD->SetCooperativeLevel( g_hExclusivePopupWnd, dwFlags ) );
	}
	long nWidth;
	long nHeight;
	long nDepth;
	long nRefreshRate;
	if( pFullscreenDisplayMode ) {
		CHECK_SUCCESS( pFullscreenDisplayMode->GetWidth( nWidth ) );
		CHECK_SUCCESS( pFullscreenDisplayMode->GetHeight( nHeight ) );
		CHECK_SUCCESS( pFullscreenDisplayMode->GetDepth( nDepth ) );
		CHECK_SUCCESS( pFullscreenDisplayMode->GetRefreshRate( nRefreshRate ) );
		CHECK_SUCCESS( m_pDD->SetDisplayMode( nWidth, nHeight, nDepth, nRefreshRate, 0L ) );
	} else {
		DDSURFACEDESC2 sDDSD;
		memset( &sDDSD, 0, sizeof( DDSURFACEDESC2 ) );
		sDDSD.dwSize = sizeof( DDSURFACEDESC2 );
		CHECK_SUCCESS( m_pDD->GetDisplayMode( &sDDSD ) );
		nWidth = sDDSD.dwWidth;
		nHeight = sDDSD.dwHeight;
		nDepth = sDDSD.ddpfPixelFormat.dwRGBBitCount;
		CHECK_SUCCESS( m_pDD->GetMonitorFrequency( (DWORD*)&nRefreshRate ) );
	}
	m_bHasExclusiveRenderTarget = true;

	CHECK_SUCCESS( CommitPrimaryIfNecessary() );

	// Get Back Buffer
	DDSCAPS2 ddscaps = { DDSCAPS_BACKBUFFER, 0, 0, 0 };
	CHECK_SUCCESS( m_pDDSurfacePrimary->GetAttachedSurface( &ddscaps, &pDDSurfaceBackBuffer ) );

	return S_OK;
}
int DisplayDriver::ReleaseExclusive() {
	SAFE_RELEASE( m_pDDSurfacePrimary );
	CHECK_SUCCESS( m_pDD->SetCooperativeLevel( g_hExclusivePopupWnd, DDSCL_NORMAL ) );
	CHECK_SUCCESS( m_pDD->RestoreDisplayMode() );
	CHECK_SUCCESS( m_pRenderer->DecrementExclusiveCount() );
	CHECK_SUCCESS( m_pD3D->EvictManagedTextures() );
	m_bHasExclusiveRenderTarget = false;
	//todo: should the release of the first exclusive driver find another driver to set special cooperative level?
	return S_OK;
}
int DisplayDriver::GetDefaultDisplayDevice( long nBitDepth, DisplayDevice*& pDisplayDevice ) {
	if( m_pRenderer->IsSoftwareEmulationForced() ) {
		pDisplayDevice = m_pRGBDisplayDevice;
	} else {
		if( m_pTnLDisplayDevice && m_pTnLDisplayDevice->SupportsBitDepth( nBitDepth ) ) {
			pDisplayDevice = m_pTnLDisplayDevice;
		} else if( m_pHALDisplayDevice && m_pHALDisplayDevice->SupportsBitDepth( nBitDepth ) ) {
			pDisplayDevice = m_pHALDisplayDevice;
		} else if( m_pRGBDisplayDevice && m_pRGBDisplayDevice->SupportsBitDepth( nBitDepth ) ) {
			pDisplayDevice = m_pRGBDisplayDevice;
		} else { 
			CHECK_TRUTH( false );
		}
	}
	return S_OK;
}
