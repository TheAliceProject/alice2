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

#ifndef RENDER_CANVAS_INCLUDED
#define RENDER_CANVAS_INCLUDED

#include "RenderTarget.hpp"

class RenderCanvas {
public:
	RenderCanvas( RenderTarget* pRenderTarget ) {
		m_hWnd = NULL;
		m_pFullscreenDisplayMode = NULL;
		m_bIsExclusive = false;
		m_pRenderTarget = pRenderTarget;
		m_pRenderTarget->SetRenderCanvas( this );
	}

	int Release() {
		DisplayDriver* pDisplayDriver;
		CHECK_SUCCESS( m_pRenderTarget->GetDisplayDriver( pDisplayDriver ) );
		if( m_bIsExclusive && pDisplayDriver && m_hWnd ) {
			CHECK_SUCCESS( pDisplayDriver->ReleaseExclusive() );
		}
		return S_OK;
	}

	int OnFullscreenDisplayModeChange( FullscreenDisplayMode* pFullscreenDisplayMode ) {
		boolean bIsChange;
		if( m_bIsExclusive ) {
			bIsChange = ( m_pFullscreenDisplayMode != pFullscreenDisplayMode );
		} else {
			bIsChange = true;
		}
		if( bIsChange ) {
			CHECK_SUCCESS( Release() );
			m_bIsExclusive = true;
			m_pFullscreenDisplayMode = pFullscreenDisplayMode;
			//if( m_pFullscreenDisplayMode ) {
			//	long nWidth;
			//	long nHeight;
			//	pFullscreenDisplayMode->GetWidth( nWidth );
			//	pFullscreenDisplayMode->GetHeight( nHeight );
			//	printf( "OnFullscreenDisplayModeChange %d %d\n", nWidth, nHeight );
			//} else {
			//	printf( "OnFullscreenDisplayModeChange null\n" );
			//}
		}
		return S_OK;
	}
	int OnOverlappedDisplayModeChange() {
		if( m_bIsExclusive ) {
			CHECK_SUCCESS( Release() );
			m_bIsExclusive = false;
		}
		return S_OK;
	}

	void OnAcquireDrawingSurface( HWND hWnd, bool& bIsValid ) {
		if( m_hWnd != hWnd ) {
			m_hWnd = hWnd;
			if( !m_bIsExclusive ) {
				Release();
			}
		}
		bIsValid = m_hWnd && IsWindow( m_hWnd ) && ::IsWindowVisible( m_hWnd );
	}
	void OnReleaseDrawingSurface() {
	}

	int SwapBuffers() {
		Renderer* pRenderer;
		CHECK_SUCCESS( m_pRenderTarget->GetRenderer( pRenderer ) );
		DisplayDriver* pDisplayDriver;
		CHECK_SUCCESS( m_pRenderTarget->GetDisplayDriver( pDisplayDriver ) );

		if( m_bIsExclusive ) {
			CHECK_SUCCESS( m_pRenderTarget->CommitIfNecessary() );
			LPDIRECTDRAWSURFACE7 pDDSurfacePrimary;
			CHECK_SUCCESS( pDisplayDriver->GetDDSurfacePrimary( pDDSurfacePrimary ) );
			CHECK_SUCCESS( pDDSurfacePrimary->Flip( NULL, DDFLIP_WAIT ) );
		} else {
			CHECK_SUCCESS( m_pRenderTarget->CommitIfNecessary() );

			RECT screenRect;
			GetClientRect( m_hWnd, &screenRect );
			ClientToScreen( m_hWnd, (POINT*)&screenRect.left );
			ClientToScreen( m_hWnd, (POINT*)&screenRect.right );

			RECT buffRect;
			long nWidth;
			long nHeight;
			m_pRenderTarget->GetWidth( nWidth );
			m_pRenderTarget->GetHeight( nHeight );
			
			SetRect( &buffRect, 0, 0, nWidth, nHeight );

			LPDIRECTDRAWCLIPPER pDDClipper;
			CHECK_SUCCESS( pRenderer->GetDDClipper( pDDClipper ) );
			CHECK_SUCCESS( pDDClipper->SetHWnd( 0, m_hWnd ) );
			
			LPDIRECTDRAWSURFACE7 pDDSurfacePrimary;
			CHECK_SUCCESS( pDisplayDriver->GetDDSurfacePrimary( pDDSurfacePrimary ) );

			LPDIRECTDRAWSURFACE7 pDDBackBuffer;
			CHECK_SUCCESS( m_pRenderTarget->GetBackBuffer( pDDBackBuffer ) );

			if( pDDSurfacePrimary->IsLost() ) {
				CHECK_SUCCESS( pDDSurfacePrimary->Restore() );
			}
			if( pDDBackBuffer->IsLost() ) {
				CHECK_SUCCESS( pDDBackBuffer->Restore() );
			}
			CHECK_SUCCESS( pDDSurfacePrimary->Blt( &screenRect, pDDBackBuffer, &buffRect, DDBLT_WAIT, NULL ) );
		}
		return S_OK;
	}
	bool IsExclusive() {
		return m_bIsExclusive;
	}
	FullscreenDisplayMode* GetFullscreenDisplayMode() {
		return m_pFullscreenDisplayMode;
	}

	HWND GetHWnd() {
		return m_hWnd;
	}
private:
	RenderTarget* m_pRenderTarget;
	HWND m_hWnd;
	FullscreenDisplayMode* m_pFullscreenDisplayMode;
	bool m_bIsExclusive;
	bool m_bIsShowing;
	//todo: use GetSystemMetrics( SM_CXSCREEN ); ?
	//float m_fMetersPerPixelX;
};

#endif
