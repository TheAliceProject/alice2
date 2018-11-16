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

#define _WIN32_WINNT 0x0500
#include <windows.h>

#include <stdio.h>

// thanks to Rui Godinho Lopes <ruiglopes@yahoo.com> for helpful example code.
// dennisc

static WNDCLASSEX g_wcex;
static FARPROC g_fUpdateLayeredWindow;

struct Data {
	HWND m_hWnd;
	double m_fOpacity;
	BITMAP* m_psBitmap;
};

void PreMultiplyRGBChannels( BYTE* pBitmapBits, int nWidth, int nHeight ) {
	// pre-multiply rgb channels with alpha channel
	int y;
	BYTE *pPixel = pBitmapBits;
	for( y=0; y<nHeight; ++y ) {
		int x;
		for( x=0; x<nWidth; ++x ) {
			pPixel[0] = pPixel[0]*pPixel[3]/255;
			pPixel[1] = pPixel[1]*pPixel[3]/255;
			pPixel[2] = pPixel[2]*pPixel[3]/255;
			pPixel+= 4;
		}
	}
}

int SemitransparentWindow_IsSupported( int* pbIsSupported ) {
	OSVERSIONINFO version;
	version.dwOSVersionInfoSize = sizeof( OSVERSIONINFO );
	GetVersionEx( &version );
	if( version.dwPlatformId == VER_PLATFORM_WIN32_NT ) {
		if( version.dwMajorVersion >= 5 ) {
			HMODULE hModule = GetModuleHandle( "user32.dll" );
			if( hModule ) {
				g_fUpdateLayeredWindow = GetProcAddress( hModule, "UpdateLayeredWindow" );
				if( g_fUpdateLayeredWindow ) {
					HDC hDC = GetDC( NULL );
					*pbIsSupported = GetDeviceCaps( hDC, BITSPIXEL )==32;
					ReleaseDC( NULL, hDC );
				}
			}
		} else {
			*pbIsSupported = FALSE;
		}
	} else {
		*pbIsSupported = FALSE;
	}
	return 0;
}

int SemitransparentWindow_Create( void** ppData ) {
	struct Data* psData = malloc( sizeof( struct Data ) );
	psData->m_hWnd = CreateWindowEx( WS_EX_LAYERED | WS_EX_TRANSPARENT, g_wcex.lpszClassName, "title", WS_CLIPCHILDREN|WS_POPUP, 0, 0, 1, 1, NULL, NULL, g_wcex.hInstance, NULL);
	UpdateWindow( psData->m_hWnd );
	SetWindowPos( psData->m_hWnd, HWND_TOPMOST, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE );
	psData->m_fOpacity = 1.0;
	psData->m_psBitmap = NULL;
	*ppData = psData;
	return 0;
}

int SemitransparentWindow_Destroy( void* pData ) {
	struct Data* psData = pData;
	DestroyWindow( psData->m_hWnd );
	if( psData->m_psBitmap ) {
		free( psData->m_psBitmap );
	}
	free( psData );
	return 0;
}
int SemitransparentWindow_Show( void* pData ) {
	struct Data* psData = pData;
	ShowWindow( psData->m_hWnd, SW_SHOW );
	return 0;
}
int SemitransparentWindow_Hide( void* pData ) {
	struct Data* psData = pData;
	ShowWindow( psData->m_hWnd, SW_HIDE );
	return 0;
}

static int UpdateIfNecessary( struct Data* psData ) {
	if( psData->m_psBitmap ) {
		BLENDFUNCTION sBlendPixelFunction = { AC_SRC_OVER, 0, (unsigned char)(psData->m_fOpacity*255), AC_SRC_ALPHA };
		HDC hScreenDC = GetDC( NULL );
		HDC hMemoryDC = CreateCompatibleDC( hScreenDC );
		POINT origin = { 0, 0 };
		SIZE size = { psData->m_psBitmap->bmWidth, psData->m_psBitmap->bmHeight };

		HBITMAP hBitmap = CreateBitmapIndirect( psData->m_psBitmap );
		HGDIOBJ hPrevBitmap = SelectObject( hMemoryDC, hBitmap );

		//BOOL bResult = UpdateLayeredWindow( psData->m_hWnd, hScreenDC, NULL, &size, hMemoryDC, &origin, 0, &sBlendPixelFunction, ULW_ALPHA );
		if( g_fUpdateLayeredWindow ) {
			BOOL bResult = g_fUpdateLayeredWindow( psData->m_hWnd, hScreenDC, NULL, &size, hMemoryDC, &origin, 0, &sBlendPixelFunction, ULW_ALPHA );
			if( !bResult ) {
				DWORD dwError = GetLastError();
				//todo
			}
		}
	
		SelectObject( hMemoryDC, hPrevBitmap );

		DeleteObject( hBitmap );
		DeleteDC( hMemoryDC );
		ReleaseDC( NULL, hScreenDC );
	}
	return 0;
}


int SemitransparentWindow_SetImage( void* pData, long* vnPixels, long nWidth, long nHeight ) {
	struct Data* psData = pData;
	size_t size = nWidth*nHeight*4;
	PreMultiplyRGBChannels( (BYTE*)vnPixels, nWidth, nHeight );
	if( psData->m_psBitmap == NULL ) {
		psData->m_psBitmap = malloc( sizeof( BITMAP ) );
		psData->m_psBitmap->bmBits = NULL;
	} else {
		free( psData->m_psBitmap->bmBits );
	}
	psData->m_psBitmap->bmType = 0;
	psData->m_psBitmap->bmWidth = nWidth;
	psData->m_psBitmap->bmHeight = nHeight;
	psData->m_psBitmap->bmWidthBytes = nWidth*4;
	psData->m_psBitmap->bmPlanes = 1;
	psData->m_psBitmap->bmBitsPixel = 32;
	psData->m_psBitmap->bmBits = malloc( size );
	memcpy( psData->m_psBitmap->bmBits, vnPixels, size );
	UpdateIfNecessary( psData );
	return 0;
}

int SemitransparentWindow_SetLocationOnScreen( void* pData, long nX, long nY ) {
	struct Data* psData = pData;
	SetWindowPos( psData->m_hWnd, NULL, nX, nY, 0, 0, SWP_ASYNCWINDOWPOS|SWP_NOZORDER|SWP_NOSIZE );
	return 0;
}

int SemitransparentWindow_SetOpacity( void* pData, double fOpacity ) {
	struct Data* psData = pData;
	psData->m_fOpacity = fOpacity;
	UpdateIfNecessary( psData );
	return 0;
}

int SemitransparentWindow_Register( HANDLE hModule ) {
	g_wcex.cbSize = sizeof(WNDCLASSEX); 

	g_wcex.style			= CS_HREDRAW | CS_VREDRAW;
	g_wcex.lpfnWndProc	= DefWindowProc;
	g_wcex.cbClsExtra		= 0;
	g_wcex.cbWndExtra		= 0;
	g_wcex.hInstance		= (HINSTANCE)hModule;
	g_wcex.hIcon			= NULL;
	g_wcex.hCursor		= NULL;
	g_wcex.hbrBackground	= (HBRUSH)GetStockObject( BLACK_BRUSH );
	g_wcex.lpszMenuName	= NULL;;
	g_wcex.lpszClassName	= "SemitransparentWindow";
	g_wcex.hIconSm		= NULL;

	RegisterClassEx(&g_wcex);
	return 0;
}

int SemitransparentWindow_Unregister( HANDLE hModule ) {
	UnregisterClass( g_wcex.lpszClassName, (HINSTANCE)hModule );
	return 0;
}

BOOL APIENTRY DllMain( HANDLE hModule, DWORD ul_reason_for_call, LPVOID lpReserved ) {
    switch (ul_reason_for_call) {
	case DLL_PROCESS_ATTACH:
		SemitransparentWindow_Register( hModule );
		break;
	case DLL_THREAD_ATTACH:
		break;
	case DLL_THREAD_DETACH:
		break;
	case DLL_PROCESS_DETACH:
		SemitransparentWindow_Unregister( hModule );
		break;
    }
    return TRUE;
}