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

#ifndef TEXTURE_MAP_INCLUDED
#define TEXTURE_MAP_INCLUDED

#include "Element.hpp"
class DisplayDevice;
class Appearance;


#include <algorithm>
#include <vector>
#include <map>

typedef std::map< LPDIRECT3DDEVICE7, LPDIRECTDRAWSURFACE7 > DeviceToSurfaceMap;
typedef DeviceToSurfaceMap::iterator DeviceToSurfaceMapIterator;

class TextureMap : public Element {
public:
	TextureMap() {
		Element::Element();
		m_nWidth = 0;
		m_nHeight = 0;
		m_vnPixels = NULL;
		AddToInstances();
	}
	int OnImageChange( long* vnPixels, int nWidth, int nHeight );
	int OnFormatChange( int value );

	bool IsOpaque();
	int GetSurface( LPDIRECT3DDEVICE7 pD3DDevice, LPDIRECTDRAWSURFACE7& pDDSurface );

	int AddAppearance( Appearance* pAppearance ) {
		m_appearances.push_back( pAppearance );
		return S_OK;
	}
	int RemoveAppearance( Appearance* pAppearance ) {
		std::vector<Appearance*>::iterator iTarget;
		iTarget = std::find( m_appearances.begin(), m_appearances.end(), pAppearance );
		CHECK_TRUTH( m_appearances.end() != iTarget );
		m_appearances.erase( iTarget );
		return S_OK;
	}

	int GetWidth( long& nWidth ) {
		nWidth = m_nWidth;
		return S_OK;
	}
	int GetHeight( long& nHeight ) {
		nHeight = m_nHeight;
		return S_OK;
	}


	int GetWidth( LPDIRECT3DDEVICE7 pD3DDevice, long& nWidth ) {
		DDSURFACEDESC2 ddsd;
		CHECK_SUCCESS( GetSurfaceDesc( pD3DDevice, ddsd ) );
		nWidth = ddsd.dwWidth;
		return S_OK;
	}
	int GetHeight( LPDIRECT3DDEVICE7 pD3DDevice, long& nHeight ) {
		DDSURFACEDESC2 ddsd;
		CHECK_SUCCESS( GetSurfaceDesc( pD3DDevice, ddsd ) );
		nHeight = ddsd.dwHeight;
		return S_OK;
	}
	int GetPitch( LPDIRECT3DDEVICE7 pD3DDevice, long& nPitch ) {
		DDSURFACEDESC2 ddsd;
		CHECK_SUCCESS( GetSurfaceDesc( pD3DDevice, ddsd ) );
		nPitch = ddsd.lPitch;
		return S_OK;
	}
	int GetBitCount( LPDIRECT3DDEVICE7 pD3DDevice, long& nBitCount ) {
		DDSURFACEDESC2 ddsd;
		CHECK_SUCCESS( GetSurfaceDesc( pD3DDevice, ddsd ) );
		nBitCount = ddsd.ddpfPixelFormat.dwRGBBitCount;
		return S_OK;
	}
	int GetRedBitMask( LPDIRECT3DDEVICE7 pD3DDevice, long& nRedBitMask ) {
		DDSURFACEDESC2 ddsd;
		CHECK_SUCCESS( GetSurfaceDesc( pD3DDevice, ddsd ) );
		nRedBitMask = ddsd.ddpfPixelFormat.dwRBitMask;
		return S_OK;
	}
	int GetGreenBitMask( LPDIRECT3DDEVICE7 pD3DDevice, long& nGreenBitMask ) {
		DDSURFACEDESC2 ddsd;
		CHECK_SUCCESS( GetSurfaceDesc( pD3DDevice, ddsd ) );
		nGreenBitMask = ddsd.ddpfPixelFormat.dwGBitMask;
		return S_OK;
	}
	int GetBlueBitMask( LPDIRECT3DDEVICE7 pD3DDevice, long& nBlueBitMask ) {
		DDSURFACEDESC2 ddsd;
		CHECK_SUCCESS( GetSurfaceDesc( pD3DDevice, ddsd ) );
		nBlueBitMask = ddsd.ddpfPixelFormat.dwBBitMask;
		return S_OK;
	}
	int GetAlphaBitMask( LPDIRECT3DDEVICE7 pD3DDevice, long& nAlphaBitMask ) {
		DDSURFACEDESC2 ddsd;
		CHECK_SUCCESS( GetSurfaceDesc( pD3DDevice, ddsd ) );
		nAlphaBitMask = ddsd.ddpfPixelFormat.dwRGBAlphaBitMask;
		return S_OK;
	}

	int GetPixels( DisplayDevice* pDisplayDevice, LPDIRECTDRAWSURFACE7 pDDBackBuffer, long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long nRedBitMask, long nGreenBitMask, long nBlueBitMask, long nAlphaBitMask, long* vnPixels );

	static int ReleaseAllSurfaces( LPDIRECT3DDEVICE7 pD3DDevice );
	int CheckForVisualStateChange();
protected:
	virtual void InternalRelease() {
		Element::InternalRelease();
		ReleaseAllSurfaces();
		RemoveFromInstances();
	}
private:
	int GetSurfaceDesc( LPDIRECT3DDEVICE7 pD3DDevice, DDSURFACEDESC2& ddsd ) {
		LPDIRECTDRAWSURFACE7 pDDSurface;
		CHECK_SUCCESS( GetSurface( pD3DDevice, pDDSurface ) );

		memset( &ddsd, 0, sizeof( DDSURFACEDESC2 ) );
		ddsd.dwSize = sizeof( DDSURFACEDESC2 );
	    CHECK_SUCCESS( pDDSurface->GetSurfaceDesc( &ddsd ) );
		return S_OK;
	}

	void AddToInstances();
	void RemoveFromInstances();
	int ReleaseSurface( LPDIRECT3DDEVICE7 pD3DDevice );
	int ReleaseAllSurfaces();

	int PixelConversion( LPDIRECTDRAWSURFACE7 pDest );

	DeviceToSurfaceMap m_map;

	int m_nWidth;
	int m_nHeight;
	long* m_vnPixels;
	int m_nFormat;

	std::vector<Appearance*> m_appearances;
};

#endif