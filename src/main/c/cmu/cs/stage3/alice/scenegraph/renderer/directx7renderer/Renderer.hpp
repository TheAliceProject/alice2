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

#ifndef RENDERER_INCLUDED
#define RENDERER_INCLUDED

#include "DisplayDriver.hpp"
class _Component;
class _Visual;
class RenderTarget;

extern int HACK_g_nVisualID;

class Renderer {
public:
	Renderer() {
		m_pDDClipper = NULL;
		m_nExclusiveCount = 0;
		m_pDDSurfacePickColor = NULL;
		m_pDDSurfacePickZ = NULL;
		m_pD3DDevicePick = NULL;
		m_bIsSoftwareEmulationForced = false;
	}
	int Enumerate();

	bool OnDisplayDriverEnum( GUID* pGUID, const char* pDriverDescription, const char* pDriverName, HMONITOR hMonitor ) {
		DisplayDriver* pDisplayDriver = new DisplayDriver( this, pGUID, pDriverDescription, pDriverName, hMonitor );
		m_vDisplayDrivers.push_back( pDisplayDriver );
		return true;
	}
	int GetDisplayDriverCount( long& displayDriverCount ) {
		displayDriverCount = (long)m_vDisplayDrivers.size();
		return S_OK;
	}
	int GetDisplayDriverAt( long index, DisplayDriver*& displayDriverID ) {
		displayDriverID = m_vDisplayDrivers[index];
		return S_OK;
	}

	int GetDefaultDisplayDriver( DisplayDriver*& pDisplayDriver ) {
		if( m_vDisplayDrivers.size() ) {
			pDisplayDriver = m_vDisplayDrivers[0];
			return S_OK;
		} else {
			pDisplayDriver = NULL;
			return -1;
		}
	}

	int GetDDClipper( LPDIRECTDRAWCLIPPER& pDDClipper) {
		if( m_pDDClipper==NULL ) {
			CHECK_SUCCESS( DirectDrawCreateClipper( 0, &m_pDDClipper, NULL ) );
		}
		pDDClipper = m_pDDClipper;
		return S_OK;
	}
	
	int IncrementExclusiveCount( bool& bIsFirstExclusive ) {
		m_nExclusiveCount++;
		bIsFirstExclusive = m_nExclusiveCount==1;
		return S_OK;
	}
	int DecrementExclusiveCount() {
		m_nExclusiveCount--;
		return S_OK;
	}

	int Release() {
		SAFE_RELEASE( m_pDDClipper );
		SAFE_RELEASE( m_pD3DDevicePick );
		SAFE_RELEASE( m_pDDSurfacePickZ );
		SAFE_RELEASE( m_pDDSurfacePickColor );
		return S_OK;
	}

	int PerformPick( _Component* pComponent, const D3DMATRIX& sD3DMProjection, const D3DMATRIX& sD3DMView, bool bIsSubElementRequired, bool bIsOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ );
	int Pick( _Component* pComponent, double fVectorX, double fVectorY, double fVectorZ, double fMinX, double fMinY, double fMaxX, double fMaxY, double fNear, double fFar, bool bIsSubElementRequired, bool bIsOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ );

	int GetPickDevice( LPDIRECT3DDEVICE7& pD3DDevice ) {
		CHECK_SUCCESS( CommitPickIfNecessary() );
		pD3DDevice = m_pD3DDevicePick;
		return S_OK;
	}
	int PickLookup( int& nValue ) {
		CHECK_TRUTH( m_pDDSurfacePickColor );
		DDSURFACEDESC2 ddsd;
		memset( &ddsd, 0, sizeof( DDSURFACEDESC2 ) );
		ddsd.dwSize = sizeof( ddsd );
		CHECK_SUCCESS( m_pDDSurfacePickColor->Lock( NULL, &ddsd, DDLOCK_READONLY|DDLOCK_SURFACEMEMORYPTR|DDLOCK_WAIT, NULL ) );
		if( ddsd.lpSurface ) {
			nValue = (*((int*)ddsd.lpSurface));
		} else {
			nValue = -1;
		}
	    CHECK_SUCCESS( m_pDDSurfacePickColor->Unlock( NULL ) );
		return S_OK;
	}

	bool IsSoftwareEmulationForced() {
		return m_bIsSoftwareEmulationForced;
	}
	void SetIsSoftwareEmulationForced( bool bIsSoftwareEmulationForced ) {
		m_bIsSoftwareEmulationForced = bIsSoftwareEmulationForced;
	}
private:
	int CommitPickIfNecessary();

	LPDIRECTDRAWCLIPPER m_pDDClipper;
	LPDIRECTDRAWSURFACE7 m_pDDSurfacePickColor;
	LPDIRECTDRAWSURFACE7 m_pDDSurfacePickZ;
	LPDIRECT3DDEVICE7 m_pD3DDevicePick;
	std::vector< DisplayDriver* > m_vDisplayDrivers;
	int m_nExclusiveCount;
	bool m_bIsSoftwareEmulationForced;
};

#endif
