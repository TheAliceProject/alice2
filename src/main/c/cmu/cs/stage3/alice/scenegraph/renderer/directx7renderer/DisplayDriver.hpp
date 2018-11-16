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

#ifndef DISPLAY_DRIVER_INCLUDED
#define DISPLAY_DRIVER_INCLUDED

#include <vector>

#include "DisplayDevice.hpp"
#include "FullscreenDisplayMode.hpp"
class Renderer;

class DisplayDriver {
public:
	DisplayDriver( Renderer* pRenderer, GUID* pGUID, const char* pDriverDescription, const char* pDriverName, HMONITOR hMonitor ) {
		m_pRenderer = pRenderer;
		m_pGUID = pGUID;
		strncpy( m_pDescription, pDriverDescription, 256 );
		strncpy( m_pName, pDriverName, 256 );
		m_hMonitor = hMonitor;
		m_pDD = NULL;
		m_pD3D = NULL;
		m_pDDSurfacePrimary = NULL;
		m_bHasExclusiveRenderTarget = false;
		m_pRGBDisplayDevice = NULL;
		m_pHALDisplayDevice = NULL;
		m_pTnLDisplayDevice = NULL;
		Enumerate();
	}

	GUID* GetGUID() {
		return m_pGUID;
	}
	int GetDescription( char* vcDescription, long n ) {
		strncpy( vcDescription, m_pDescription, n );
		return S_OK;
	}
	int GetName( char* vcName, long n ) {
		strncpy( vcName, m_pName, n );
		return S_OK;
	}

	HMONITOR GetMonitor() {
		return m_hMonitor;
	}
	int GetFullscreenDisplayModeCount( long& nFullscreenDisplayModeCount ) {
		nFullscreenDisplayModeCount = (long)m_vFullscreenDisplayModes.size();
		return S_OK;
	}
	int GetFullscreenDisplayModeAt( long nIndex, FullscreenDisplayMode*& pFullscreenDisplayMode ) {
		pFullscreenDisplayMode = m_vFullscreenDisplayModes[ nIndex ];
		return S_OK;
	}
	int GetDisplayDeviceCount( long& nDisplayDeviceCount ) {
		nDisplayDeviceCount = (long)m_vDisplayDevices.size();
		return S_OK;
	}
	int GetDisplayDeviceAt( long nIndex, DisplayDevice*& pDisplayDevice ) {
		pDisplayDevice = m_vDisplayDevices[ nIndex ];
		return S_OK;
	}
	bool SupportsOverlapped() {
		return m_bSupportsOverlapped;
	}

	int GetDisplayDepth( long& nDisplayDepth ) {
		DDSURFACEDESC2 sDDSD;
		memset( &sDDSD, 0, sizeof( DDSURFACEDESC2 ) );
		sDDSD.dwSize = sizeof( DDSURFACEDESC2 );
		CHECK_SUCCESS( m_pDD->GetDisplayMode( &sDDSD ) );
		nDisplayDepth = sDDSD.ddpfPixelFormat.dwRGBBitCount;
		return S_OK;
	}

	int OnFullscreenDisplayModeEnum( LPDDSURFACEDESC2 pDDSurfaceDesc ) {
		if( pDDSurfaceDesc->ddpfPixelFormat.dwRGBBitCount>8 ) {
			m_vFullscreenDisplayModes.push_back( new FullscreenDisplayMode( this, pDDSurfaceDesc ) );
		} else {
			//skip modes which require palettes
		}
		return DDENUMRET_OK;
	}
	int OnDisplayDeviceEnum( const char* pDeviceDescription, const char* pDeviceName, LPD3DDEVICEDESC7 pDeviceDesc ) {
		DisplayDevice* pDisplayDevice = new DisplayDevice( this, pDeviceDescription, pDeviceName, pDeviceDesc );
		m_vDisplayDevices.push_back( pDisplayDevice );
		if( memcmp( &pDeviceDesc->deviceGUID, &IID_IDirect3DTnLHalDevice, sizeof( GUID ) ) == 0 ) {
			m_pTnLDisplayDevice = pDisplayDevice;
		} else if( memcmp( &pDeviceDesc->deviceGUID, &IID_IDirect3DHALDevice, sizeof( GUID ) ) == 0 ) {
			m_pHALDisplayDevice = pDisplayDevice;
		} else if( memcmp( &pDeviceDesc->deviceGUID, &IID_IDirect3DRGBDevice, sizeof( GUID ) ) == 0 ) {
			m_pRGBDisplayDevice = pDisplayDevice;
		}
		return DDENUMRET_OK;
	}

	int GetDefaultDisplayDevice( long nBitDepth, DisplayDevice*& pDisplayDevice );
	int GetRGBDisplayDevice( DisplayDevice*& pDisplayDevice ) {
		pDisplayDevice = m_pRGBDisplayDevice;
		return S_OK;
	}
	int GetHALDisplayDevice( DisplayDevice*& pDisplayDevice ) {
		pDisplayDevice = m_pHALDisplayDevice;
		return S_OK;
	}
	int GetTnLDisplayDevice( DisplayDevice*& pDisplayDevice ) {
		pDisplayDevice = m_pTnLDisplayDevice;
		return S_OK;
	}

	bool IsReadyToWaitForVerticalBlank() {
		return m_pDD!=NULL;
	}
	int WaitForVerticalBlank() {
		CHECK_NOT_NULL( m_pDD );
		CHECK_SUCCESS( m_pDD->WaitForVerticalBlank( DDWAITVB_BLOCKBEGIN, NULL ) );
		return S_OK;
	}
	int GetDD( LPDIRECTDRAW7& pDD ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		pDD = m_pDD;
		return S_OK;
	}
	int GetD3D( LPDIRECT3D7& pD3D ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		pD3D = m_pD3D;
		return S_OK;
	}

	int GetDDSurfacePrimary( LPDIRECTDRAWSURFACE7& pDDSurfacePrimary ) {
		CHECK_SUCCESS( CommitPrimaryIfNecessary() );
		pDDSurfacePrimary = m_pDDSurfacePrimary;
		return S_OK;
	}

	int Release() {
		SAFE_RELEASE( m_pDDSurfacePrimary );
		SAFE_RELEASE( m_pD3D );
		SAFE_RELEASE( m_pDD );
	}

	LRESULT WindowProc( HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam );

	int DisplayVideoMemoryDiagnosticInformation();
	int AcquireExclusive( FullscreenDisplayMode* pFullscreenDisplayMode, LPDIRECTDRAWSURFACE7& pDDSurfaceBackBuffer );
	int ReleaseExclusive();
private:
	int CommitIfNecessary();
	int CommitPrimaryIfNecessary();

	Renderer* m_pRenderer;
	GUID* m_pGUID;
	char m_pDescription[256];
	char m_pName[256];
	HMONITOR m_hMonitor;
	bool m_bSupportsOverlapped;
	std::vector< FullscreenDisplayMode* > m_vFullscreenDisplayModes;
	std::vector< DisplayDevice* > m_vDisplayDevices;
	int Enumerate();

	bool m_bHasExclusiveRenderTarget;
	LPDIRECTDRAW7 m_pDD;
	LPDIRECT3D7 m_pD3D;
	LPDIRECTDRAWSURFACE7 m_pDDSurfacePrimary;
	DisplayDevice* m_pRGBDisplayDevice;
	DisplayDevice* m_pHALDisplayDevice;
	DisplayDevice* m_pTnLDisplayDevice;

};

#endif
