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

#include "TextureMap.hpp"
#include "Appearance.hpp"
#include "DisplayDriver.hpp"
#include "DisplayDevice.hpp"

#include <vector>
#include <algorithm>

typedef std::vector< TextureMap* > TextureMapVector;
typedef TextureMapVector::iterator TextureMapVectorIterator;

static TextureMapVector g_instances;

#define RGB_FORMAT 1
#define ALPHA_FORMAT 2
#define RGBA_FORMAT RGB_FORMAT|ALPHA_FORMAT
#define LUMINANCE_FORMAT 4
#define LUMINANCE_PLUS_ALPHA_FORMAT LUMINANCE_FORMAT|ALPHA_FORMAT

bool TextureMap::IsOpaque() {
	return ( m_nFormat & ALPHA_FORMAT ) == 0;
}

int TextureMap::CheckForVisualStateChange() {
	for( size_t i=0; i<m_appearances.size(); i++ ) {
		CHECK_SUCCESS( m_appearances[i]->CheckForVisualStateChange() );
	}
	return S_OK;
}

void TextureMap::AddToInstances() {
	g_instances.push_back( this );
}
void TextureMap::RemoveFromInstances() {
	TextureMapVectorIterator iter = std::find( g_instances.begin(), g_instances.end(), this );
	if( iter != g_instances.end() ) {
		g_instances.erase( iter );
	}
}

int TextureMap::OnImageChange( long* vnPixels, int nWidth, int nHeight ) {
	if( vnPixels ) {
		CHECK_TRUTH( nWidth>0 );
		CHECK_TRUTH( nHeight>0 );
		if( nWidth==m_nWidth && nHeight==m_nHeight ) {
			//todo
			//update current surfaces
		} else {
			//purge old surfaces
			if( m_vnPixels ) {
				delete [] m_vnPixels;
			}
			m_nWidth = nWidth;
			m_nHeight = nHeight;
			m_vnPixels = new long[ m_nWidth * m_nHeight ];
		}
		for( int nRow=0; nRow<nHeight; nRow++ ) {
			memcpy( m_vnPixels + nRow*nWidth, vnPixels + (nHeight-1-nRow)*nWidth, nWidth*4 );
		}
	} else {
		CHECK_TRUTH( nWidth==0 );
		CHECK_TRUTH( nHeight==0 );
		if( m_vnPixels ) {
			delete [] m_vnPixels;
		}
		m_nWidth = 0;
		m_nHeight = 0;
		m_vnPixels = NULL;

	}
	CHECK_SUCCESS( ReleaseAllSurfaces() );
	return S_OK;
}
int TextureMap::OnFormatChange( int nFormat ) {
	if( nFormat != m_nFormat ) {
		CHECK_SUCCESS( ReleaseAllSurfaces() );
		m_nFormat = nFormat;
		CHECK_SUCCESS( CheckForVisualStateChange() );
		
	}
	return S_OK;
}

void ConvertPixels( DDSURFACEDESC2 sDDSDA, DWORD* pPixelsB, bool bFromAToB ) {
	DWORD dwRMask = sDDSDA.ddpfPixelFormat.dwRBitMask;
    DWORD dwGMask = sDDSDA.ddpfPixelFormat.dwGBitMask;
    DWORD dwBMask = sDDSDA.ddpfPixelFormat.dwBBitMask;
    DWORD dwAMask = sDDSDA.ddpfPixelFormat.dwRGBAlphaBitMask;
	
    DWORD dwRShiftL = 8, dwRShiftR = 0;
    DWORD dwGShiftL = 8, dwGShiftR = 0;
    DWORD dwBShiftL = 8, dwBShiftR = 0;
    DWORD dwAShiftL = 8, dwAShiftR = 0;
	
    DWORD dwMask;
    for( dwMask=dwRMask; dwMask && !(dwMask&0x1); dwMask>>=1 ) dwRShiftR++;
    for( ; dwMask; dwMask>>=1 ) dwRShiftL--;
	
    for( dwMask=dwGMask; dwMask && !(dwMask&0x1); dwMask>>=1 ) dwGShiftR++;
    for( ; dwMask; dwMask>>=1 ) dwGShiftL--;
	
    for( dwMask=dwBMask; dwMask && !(dwMask&0x1); dwMask>>=1 ) dwBShiftR++;
    for( ; dwMask; dwMask>>=1 ) dwBShiftL--;
	
    for( dwMask=dwAMask; dwMask && !(dwMask&0x1); dwMask>>=1 ) dwAShiftR++;
    for( ; dwMask; dwMask>>=1 ) dwAShiftL--;
	
	BYTE* pPixelsA = (BYTE*)(sDDSDA.lpSurface);
    for( DWORD y=0; y<sDDSDA.dwHeight; y++ )
    {
	    if( bFromAToB ) {
			DWORD* pDstData32 = (DWORD*)pPixelsA;
			WORD*  pDstData16 = (WORD*)pPixelsA;
			for( DWORD x=0; x<sDDSDA.dwWidth; x++ )
			{
				DWORD dwPixel = *pPixelsB++;
				BYTE a = (BYTE)((dwPixel>>24)&0x000000ff);
				BYTE r = (BYTE)((dwPixel>>16)&0x000000ff);
				BYTE g = (BYTE)((dwPixel>> 8)&0x000000ff);
				BYTE b = (BYTE)((dwPixel>> 0)&0x000000ff);
				
				DWORD da = ((a>>(dwAShiftL))<<dwAShiftR)&dwAMask;
				DWORD dr = ((r>>(dwRShiftL))<<dwRShiftR)&dwRMask;
				DWORD dg = ((g>>(dwGShiftL))<<dwGShiftR)&dwGMask;
				DWORD db = ((b>>(dwBShiftL))<<dwBShiftR)&dwBMask;
				
				if( 32 == sDDSDA.ddpfPixelFormat.dwRGBBitCount ) {
					pDstData32[x] = (DWORD)(da|dr|dg|db);
				} else {
					pDstData16[x] = (WORD)(da|dr|dg|db);
				}
			}
		} else {
			BYTE* pPixelsATemp = pPixelsA;
			for( DWORD x=0; x<sDDSDA.dwWidth; x++ )
			{
				BYTE b0 = 0;
				BYTE b1 = 0;
				BYTE b2 = 0;
				BYTE b3 = 0;

				if( sDDSDA.ddpfPixelFormat.dwRGBBitCount >= 8 ) {
					b0 = *pPixelsATemp++;
				}
				if( sDDSDA.ddpfPixelFormat.dwRGBBitCount >= 16 ) {
					b1 = *pPixelsATemp++;
				}
				if( sDDSDA.ddpfPixelFormat.dwRGBBitCount >= 24 ) {
					b2 = *pPixelsATemp++;
				}
				if( sDDSDA.ddpfPixelFormat.dwRGBBitCount >= 32 ) {
					b3 = *pPixelsATemp++;
				}

				DWORD dwPixel = b0 | (b1<<8) | (b2<<16) | (b3<<24);

				DWORD da = ( ( dwPixel&dwAMask ) >> dwAShiftR ) << dwAShiftL;
				DWORD dr = ( ( dwPixel&dwRMask ) >> dwRShiftR ) << dwRShiftL;
				DWORD dg = ( ( dwPixel&dwGMask ) >> dwGShiftR ) << dwGShiftL;
				DWORD db = ( ( dwPixel&dwBMask ) >> dwBShiftR ) << dwBShiftL;

				*pPixelsB++ = (da<<24)|(dr<<16)|(dg<<8)|db;
			}
        }
        pPixelsA += sDDSDA.lPitch;
    }
}
int TextureMap::PixelConversion( LPDIRECTDRAWSURFACE7 pDest ){
	LPDIRECTDRAW7 pDD;
	CHECK_SUCCESS( pDest->GetDDInterface( (void**)&pDD ) );

	//todo... if a CHECK_SUCCESS fails we should release pDD

	DDSURFACEDESC2 ddsd;
	memset( &ddsd, 0, sizeof( DDSURFACEDESC2 ) );
	ddsd.dwSize = sizeof( DDSURFACEDESC2 );
	
    // Setup the new surface desc
    pDest->GetSurfaceDesc( &ddsd );
    ddsd.dwFlags         = DDSD_CAPS|DDSD_HEIGHT|DDSD_WIDTH|DDSD_PIXELFORMAT|DDSD_TEXTURESTAGE;
    ddsd.ddsCaps.dwCaps  = DDSCAPS_TEXTURE|DDSCAPS_SYSTEMMEMORY;
    ddsd.ddsCaps.dwCaps2 = 0L;
    ddsd.dwWidth         = m_nWidth;
    ddsd.dwHeight        = m_nHeight;
	
    // Create a new surface for the texture
    LPDIRECTDRAWSURFACE7 pddsTempSurface;
    CHECK_SUCCESS( pDD->CreateSurface( &ddsd, &pddsTempSurface, NULL ) );
	
    while( pddsTempSurface->Lock( NULL, &ddsd, 0, 0 ) == DDERR_WASSTILLDRAWING );
	::ConvertPixels( ddsd, (DWORD*)m_vnPixels, true );


    pddsTempSurface->Unlock(0);
	
    // Copy the temp surface to the real texture surface
    CHECK_SUCCESS( pDest->Blt( NULL, pddsTempSurface, NULL, DDBLT_WAIT, NULL ) );
	
    // Done with the temp objects
    CHECK_SUCCESS( pddsTempSurface->Release() );

	CHECK_SUCCESS( pDD->Release() );
    return S_OK;
}

int TextureMap::GetSurface( LPDIRECT3DDEVICE7 pD3DDevice, LPDIRECTDRAWSURFACE7& pDDSurfaceTexture ) {

	//pDDSurfaceTexture = NULL;
	//return S_OK;

	CHECK_NOT_NULL( m_vnPixels ); //todo
	pDDSurfaceTexture = m_map[ pD3DDevice ];
	if( pDDSurfaceTexture==NULL ) {
		DisplayDevice* pDisplayDevice = DisplayDevice::Map( pD3DDevice );
		CHECK_NOT_NULL( pDisplayDevice );
		DisplayDriver* pDisplayDriver = pDisplayDevice->GetDisplayDriver();
		LPDIRECTDRAW7 pDD;
		CHECK_SUCCESS( pDisplayDriver->GetDD( pDD ) );		
		
		// Get the device caps so we can check if the device has any constraints
		// when using textures
		D3DDEVICEDESC7 ddDesc;
		CHECK_SUCCESS( pD3DDevice->GetCaps( &ddDesc ) );
		
		// Setup the new surface desc for the texture. Note how we are using the
		// texture manage attribute, so Direct3D does alot of dirty work for us
		DDSURFACEDESC2 ddsd;
		memset( &ddsd, 0, sizeof( DDSURFACEDESC2 ) );
		ddsd.dwSize = sizeof( DDSURFACEDESC2 );

		ddsd.dwFlags = DDSD_CAPS | DDSD_WIDTH | DDSD_HEIGHT | DDSD_PIXELFORMAT;
		ddsd.ddsCaps.dwCaps = DDSCAPS_TEXTURE;
		ddsd.dwWidth = m_nWidth;
		ddsd.dwHeight = m_nHeight;
		
		// Turn on texture management for hardware devices
		if( pDisplayDevice->IsHardwareAccelerated() ) {
			ddsd.ddsCaps.dwCaps2 = DDSCAPS2_TEXTUREMANAGE;
		} else {
			ddsd.ddsCaps.dwCaps |= DDSCAPS_SYSTEMMEMORY;
		}
		
		// Adjust width and height, if the driver requires it
		if( ddDesc.dpcTriCaps.dwTextureCaps & D3DPTEXTURECAPS_POW2 )
		{
			for( ddsd.dwWidth=1;  m_nWidth>ddsd.dwWidth;   ddsd.dwWidth<<=1 );
			for( ddsd.dwHeight=1; m_nHeight>ddsd.dwHeight; ddsd.dwHeight<<=1 );
		}
		if( ddDesc.dpcTriCaps.dwTextureCaps & D3DPTEXTURECAPS_SQUAREONLY )
		{
			if( ddsd.dwWidth > ddsd.dwHeight ) ddsd.dwHeight = ddsd.dwWidth;
			else                               ddsd.dwWidth  = ddsd.dwHeight;
		}

		// Get a texture format;
		const size_t BIT_DEPTH_COUNT = 3;
		int vnBitDepth[ BIT_DEPTH_COUNT ] = { 32, 16, 24 };
		for( int i=0; i<BIT_DEPTH_COUNT; i++ ) {
			if( pDisplayDevice->GetTexturePixelFormat( vnBitDepth[ i ], !IsOpaque(), ddsd.ddpfPixelFormat ) == S_OK ) {
				break;
			} else {
				if( i == BIT_DEPTH_COUNT ) {
					return -1;
				}
			}
		}

		// Create a new surface for the texture
		CHECK_SUCCESS( pDD->CreateSurface( &ddsd, &pDDSurfaceTexture, NULL ) );

		CHECK_SUCCESS( pDisplayDriver->DisplayVideoMemoryDiagnosticInformation() );

		CHECK_SUCCESS( PixelConversion( pDDSurfaceTexture ) );
		m_map[ pD3DDevice ] = pDDSurfaceTexture;
	} 
	if( pDDSurfaceTexture->IsLost() ) {
		CHECK_SUCCESS( pDDSurfaceTexture->Restore() );
	}
	return S_OK;
}

int TextureMap::ReleaseAllSurfaces() {
	DeviceToSurfaceMapIterator iter = m_map.begin();
	while( iter != m_map.end() ) {
		LPDIRECTDRAWSURFACE7 pDDSurfaceTexture = iter->second;
		SAFE_RELEASE( pDDSurfaceTexture );
		iter++;
	}
	m_map.clear();
	return S_OK;
}
	
int TextureMap::ReleaseSurface( LPDIRECT3DDEVICE7 pD3DDevice ) {
	DisplayDevice* pDisplayDevice = DisplayDevice::Map( pD3DDevice );
	CHECK_NOT_NULL( pDisplayDevice );
	DisplayDriver* pDisplayDriver = pDisplayDevice->GetDisplayDriver();

	DeviceToSurfaceMapIterator iter = m_map.find( pD3DDevice );
	if( iter != m_map.end() ) {
		LPDIRECTDRAWSURFACE7 pDDSurfaceTexture = iter->second;
		SAFE_RELEASE( pDDSurfaceTexture );
		m_map.erase( pD3DDevice );	
	}
	CHECK_SUCCESS( pDisplayDriver->DisplayVideoMemoryDiagnosticInformation() );
	return S_OK;
}

int TextureMap::ReleaseAllSurfaces( LPDIRECT3DDEVICE7 pD3DDevice ) {
	for( int i=0; i<g_instances.size(); i++ ) {
		CHECK_SUCCESS( g_instances[i]->ReleaseSurface( pD3DDevice ) );
	}
	return S_OK;
}

extern int GetPixels( DisplayDevice* pDisplayDevice, LPDIRECTDRAWSURFACE7 pDDSurfaceOriginal, bool bBltSupported, bool bFlipY, long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long* vnPixels );

int TextureMap::GetPixels( DisplayDevice* pDisplayDevice, LPDIRECTDRAWSURFACE7 pDDBackBuffer, long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long nRedBitMask, long nGreenBitMask, long nBlueBitMask, long nAlphaBitMask, long* vnPixels ) {
	LPDIRECT3DDEVICE7 pD3DDevice;
	CHECK_SUCCESS( pDisplayDevice->GetD3DDevice( pDDBackBuffer, pD3DDevice ) );

	LPDIRECTDRAWSURFACE7 pDDSurface;
	CHECK_SUCCESS( GetSurface( pD3DDevice, pDDSurface ) );

	DDSURFACEDESC2 ddsd;
	memset( &ddsd, 0, sizeof( DDSURFACEDESC2 ) );
	ddsd.dwSize = sizeof( DDSURFACEDESC2 );
	CHECK_SUCCESS( pDDSurface->GetSurfaceDesc( &ddsd ) );

	CHECK_NOT_NULL( vnPixels );
	CHECK_TRUTH( nPitch == ddsd.lPitch );
	CHECK_TRUTH( nBitCount == ddsd.ddpfPixelFormat.dwRGBBitCount );
	CHECK_TRUTH( nRedBitMask == ddsd.ddpfPixelFormat.dwRBitMask );
	CHECK_TRUTH( nGreenBitMask == ddsd.ddpfPixelFormat.dwGBitMask );
	CHECK_TRUTH( nBlueBitMask == ddsd.ddpfPixelFormat.dwBBitMask );
	CHECK_TRUTH( nAlphaBitMask == ddsd.ddpfPixelFormat.dwRGBAlphaBitMask );
	CHECK_SUCCESS( ::GetPixels( pDisplayDevice, pDDSurface, true, true, nX, nY, nWidth, nHeight, nPitch, nBitCount, vnPixels ) );
	return S_OK;
}
