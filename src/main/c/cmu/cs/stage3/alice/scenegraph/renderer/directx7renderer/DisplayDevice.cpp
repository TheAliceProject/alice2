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

#include "DisplayDevice.hpp"
#include "DisplayDriver.hpp"
#include "TextureMap.hpp"

#include <map>

std::map< LPDIRECT3DDEVICE7, DisplayDevice* > g_map;

static HRESULT CALLBACK TexturePixelFormatCallback( LPDDPIXELFORMAT lpDDPixFmt, LPVOID lpContext ) {
	DisplayDevice* pDisplayDevice = (DisplayDevice*)lpContext;
	return pDisplayDevice->OnTexturePixelFormatEnum( lpDDPixFmt );
} 

static HRESULT CALLBACK ZBufferPixelFormatCallback( LPDDPIXELFORMAT lpDDPixFmt, LPVOID lpContext ) {
	DisplayDevice* pDisplayDevice = (DisplayDevice*)lpContext;
	return pDisplayDevice->OnZBufferPixelFormatEnum( lpDDPixFmt );
}

int DisplayDevice::EnumerateTexturePixelFormats() {
	m_vTexturePixelFormats.clear();
	CHECK_SUCCESS( m_pD3DDevice->EnumTextureFormats( TexturePixelFormatCallback, this ) );
	return S_OK;
}

int DisplayDevice::EnumerateZBufferPixelFormats() {
	m_vTexturePixelFormats.clear();
	LPDIRECT3D7 pD3D;
	CHECK_SUCCESS( m_pDisplayDriver->GetD3D( pD3D ) );
	CHECK_SUCCESS( pD3D->EnumZBufferFormats( m_sD3DDeviceDesc.deviceGUID, ZBufferPixelFormatCallback, this ) );
	return S_OK;
}

int DisplayDevice::GetD3DDevice( LPDIRECTDRAWSURFACE7 pDDSurface, LPDIRECT3DDEVICE7& pD3DDevice ) {
	if( m_pD3DDevice==NULL ) {
		LPDIRECT3D7 pD3D;
		CHECK_SUCCESS( m_pDisplayDriver->GetD3D( pD3D ) );
		CHECK_SUCCESS( pD3D->CreateDevice( m_sD3DDeviceDesc.deviceGUID, pDDSurface, &m_pD3DDevice ) );
		g_map[ m_pD3DDevice ] = this;
		CHECK_SUCCESS( EnumerateTexturePixelFormats() );
	}
	CHECK_NOT_NULL( m_pD3DDevice );
	pD3DDevice = m_pD3DDevice;
	return S_OK;
}

DisplayDevice* DisplayDevice::Map( LPDIRECT3DDEVICE7 pD3DDevice ) {
	return g_map[ pD3DDevice ];
}


int DisplayDevice::Release() {
	m_vTexturePixelFormats.clear();
	if( m_pD3DDevice ) {
		CHECK_SUCCESS( TextureMap::ReleaseAllSurfaces( m_pD3DDevice ) );
		g_map[ m_pD3DDevice ] = NULL;
	}
	SAFE_RELEASE( m_pD3DDevice );
	return S_OK;
}
