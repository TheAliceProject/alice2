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

#ifndef DISPLAY_DEVICE_INCLUDED
#define DISPLAY_DEVICE_INCLUDED

#include "platform.hpp"
#include <algorithm>
#include <vector>

class DisplayDriver;
class DisplayDevice;


class TexturePixelFormatSearcher {
public:
	TexturePixelFormatSearcher( int nDepth, bool bAlpha ) {
		m_nDepth = nDepth;
		m_bAlpha = bAlpha;
	}
	
	bool operator()( DDPIXELFORMAT& pf ){
		// Skip any funky modes
		if( pf.dwFlags & (DDPF_LUMINANCE|DDPF_BUMPLUMINANCE|DDPF_BUMPDUDV) )
			return false;
		
		// Skip any FourCC formats
		if( pf.dwFourCC != 0 )
			return false;


		int nDepth = pf.dwRGBBitCount;
		bool bAlpha = ( pf.dwFlags&DDPF_ALPHAPIXELS ) != 0;

		return nDepth==m_nDepth && bAlpha==m_bAlpha;
	}
private:
	int m_nDepth;
	bool m_bAlpha;
};

class ZBufferPixelFormatSearcher {
public:
	ZBufferPixelFormatSearcher( int nDepth ) {
		m_nDepth = nDepth;
	}
	bool operator()( DDPIXELFORMAT& pf ) {
		if( pf.dwFlags == DDPF_ZBUFFER ) {
			int nDepth = pf.dwZBufferBitDepth;
			return nDepth==m_nDepth;
		} else {
			return false;
		}
	}
private:
	int m_nDepth;
};

class DisplayDevice {
public:
	DisplayDevice( DisplayDriver* pDisplayDriver, const char* pDeviceDescription, const char* pDeviceName, LPD3DDEVICEDESC7 pDeviceDesc ) {
		m_pDisplayDriver = pDisplayDriver;
		strncpy( m_pDescription, pDeviceDescription, 256 );
		strncpy( m_pName, pDeviceName, 256 );
		m_sD3DDeviceDesc = *pDeviceDesc;
		m_pD3DDevice = NULL;
		//todo CHECK_SUCCESS
		EnumerateZBufferPixelFormats();
	}
	DisplayDriver* GetDisplayDriver() {
		return m_pDisplayDriver;
	}
	int GetDescription( char* vcDescription, long n ) {
		strncpy( vcDescription, m_pDescription, n );
		return S_OK;
	}
	int GetName( char* vcName, long n ) {
		strncpy( vcName, m_pName, n );
		return S_OK;
	}

	bool SupportsBitDepth( long nBitDepth ) {
		switch( nBitDepth ) {
		case 32:
			return ( m_sD3DDeviceDesc.dwDeviceRenderBitDepth & DDBD_32 ) != 0;
		case 24:
			return ( m_sD3DDeviceDesc.dwDeviceRenderBitDepth & DDBD_24 ) != 0;
		case 16:
			return ( m_sD3DDeviceDesc.dwDeviceRenderBitDepth & DDBD_16 ) != 0;
		case 8:
			return ( m_sD3DDeviceDesc.dwDeviceRenderBitDepth & DDBD_8 ) != 0;
		default:
			return false;
		}
	}
	LPD3DDEVICEDESC7 GetD3DDeviceDesc() {
		return &m_sD3DDeviceDesc;
	}
	
	bool IsHardwareAccelerated() {
		return ( m_sD3DDeviceDesc.deviceGUID == IID_IDirect3DHALDevice ) || ( m_sD3DDeviceDesc.deviceGUID == IID_IDirect3DTnLHalDevice );
	}

	int GetZBufferPixelFormat( int nDepth, DDPIXELFORMAT& ddpf ) {
		std::vector<DDPIXELFORMAT>::iterator iter = std::find_if( 
			m_vZBufferPixelFormats.begin(),
			m_vZBufferPixelFormats.end(),
			ZBufferPixelFormatSearcher( nDepth ) );
		if( iter == m_vZBufferPixelFormats.end() ) {
			return -1;
		} else {
			memcpy( &ddpf, &(*iter), sizeof( DDPIXELFORMAT ) );
		}
		return S_OK;
	}
	int GetTexturePixelFormat( int nDepth, bool bAlpha, DDPIXELFORMAT& ddpf ) {
		std::vector<DDPIXELFORMAT>::iterator iter = std::find_if( 
			m_vTexturePixelFormats.begin(),
			m_vTexturePixelFormats.end(),
			TexturePixelFormatSearcher( nDepth, bAlpha ) );
		if( iter == m_vTexturePixelFormats.end() ) {
			return -1;
		} else {
			memcpy( &ddpf, &(*iter), sizeof( DDPIXELFORMAT ) );
		}
		return S_OK;
	}

	int OnTexturePixelFormatEnum( LPDDPIXELFORMAT lpDDPixFmt ) {
		m_vTexturePixelFormats.push_back( *lpDDPixFmt );
		return DDENUMRET_OK;
	}
	int OnZBufferPixelFormatEnum( LPDDPIXELFORMAT lpDDPixFmt ) {
		if( lpDDPixFmt->dwFlags==DDPF_ZBUFFER ) {
			m_vZBufferPixelFormats.push_back( *lpDDPixFmt );
		}
		return DDENUMRET_OK;
	}
	static DisplayDevice* Map( LPDIRECT3DDEVICE7 pD3DDevice );

	int GetD3DDevice( LPDIRECTDRAWSURFACE7 pDDSurface, LPDIRECT3DDEVICE7& pD3DDevice );
	int Release();

private:
	int EnumerateZBufferPixelFormats();
	int EnumerateTexturePixelFormats();

	DisplayDriver* m_pDisplayDriver;
	char m_pDescription[256];
	char m_pName[256];
	D3DDEVICEDESC7 m_sD3DDeviceDesc;

	LPDIRECT3DDEVICE7 m_pD3DDevice;
	std::vector< DDPIXELFORMAT > m_vZBufferPixelFormats;
	std::vector< DDPIXELFORMAT > m_vTexturePixelFormats;
};

#endif