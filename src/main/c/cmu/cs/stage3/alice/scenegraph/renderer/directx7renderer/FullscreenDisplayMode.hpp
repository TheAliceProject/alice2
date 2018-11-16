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

#ifndef FULLSCREEN_DISPLAY_MODE_INCLUDED
#define FULLSCREEN_DISPLAY_MODE_INCLUDED

#include "platform.hpp"
class DisplayDriver;

class FullscreenDisplayMode {
public:
	FullscreenDisplayMode( DisplayDriver* pDisplayDriver, LPDDSURFACEDESC2 pDDSD ) {
		m_pDisplayDriver = pDisplayDriver;
		memcpy( &m_ddsd, pDDSD, sizeof( DDSURFACEDESC2 ) );
	}
	DisplayDriver* GetDisplayDriver() {
		return m_pDisplayDriver;
	}
	int GetWidth( long& nWidth ) {
		nWidth = m_ddsd.dwWidth;
		return S_OK;
	}
	int GetHeight( long& nHeight ) {
		nHeight = m_ddsd.dwHeight;
		return S_OK;
	}
	int GetDepth( long& nDepth ) {
		nDepth = m_ddsd.ddpfPixelFormat.dwRGBBitCount;
		return S_OK;
	}
	int GetRefreshRate( long& nRefreshRate ) {
		nRefreshRate = m_ddsd.dwRefreshRate;
		return S_OK;
	}
private:
	DisplayDriver* m_pDisplayDriver;
	DDSURFACEDESC2 m_ddsd;
};

#endif
