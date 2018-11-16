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

#ifndef GRAPHICS_INCLUDED
#define GRAPHICS_INCLUDED

#include "platform.hpp"
#include "RenderTarget.hpp"
class TextureMap;

class Graphics {
public:
	Graphics() {
		m_pDDSurface = NULL;
		m_hDC = NULL;
		m_hPen = NULL;
		m_hBrush = NULL;
		m_hFont = NULL;
		m_origin.x = m_origin.y = 0;
		m_rgb = 0;
		m_nHeight = -1;
		m_bIsFlipped = false;
	}
	int Lock( RenderTarget* pRenderTarget ) {
		LPDIRECTDRAWSURFACE7 pDDSurface;
		CHECK_SUCCESS( pRenderTarget->GetDDSurface( pDDSurface ) );
		CHECK_SUCCESS( Lock( pDDSurface ) );
		CHECK_SUCCESS( pRenderTarget->GetHeight( m_nHeight ) );
		m_bIsFlipped = false;
		return S_OK;
	}
	int Lock( RenderTarget* pRenderTarget, TextureMap* pTextureMap ) {
		LPDIRECTDRAWSURFACE7 pDDSurface;
		CHECK_SUCCESS( pRenderTarget->GetDDSurface( pTextureMap, pDDSurface ) );
		CHECK_SUCCESS( Lock( pDDSurface ) );
		CHECK_SUCCESS( pTextureMap->GetHeight( m_nHeight ) );
		m_bIsFlipped = true;
		return S_OK;
	}

	int Release() {
		if( m_hPen ) {
			DeleteObject( m_hPen );
			m_hPen = NULL;
		}
		if( m_hBrush ) {
			DeleteObject( m_hBrush );
			m_hBrush = NULL;
		}
		if( m_hFont ) {
			DeleteObject( m_hFont );
			m_hFont = NULL;
		}
		if( m_pDDSurface ) {
			m_pDDSurface->ReleaseDC( m_hDC );
			m_pDDSurface = NULL;
		}
		return S_OK;
	}

	int Translate( long x, long y ) {
		m_origin.x += x;
		m_origin.y += y;
		return S_OK;
	}
	int GetColor( long& red, long& green, long& blue ) {
		red = GetRValue( m_rgb );
		green = GetGValue( m_rgb );
		blue = GetBValue( m_rgb );
		return S_OK;
	}
	int SetColor( long red, long green, long blue ) {
		DeleteObject( m_hPen );
		DeleteObject( m_hBrush );
		m_rgb = RGB( red, green, blue );
		m_hPen = CreatePen( PS_SOLID, 1, m_rgb );
		SelectObject( m_hDC, m_hPen );
		m_hBrush = CreateSolidBrush( m_rgb );
		SelectObject( m_hDC, m_hBrush );
		SetTextColor( m_hDC, m_rgb );
		return S_OK;
	}
	int SetPaintMode() {
		//todo
		return S_OK;
	}
	int SetXORMode() {
		//todo
		return S_OK;
	}
	int CopyArea( long x, long y, long width, long height, long dx, long dy ) {
		//todo
		return S_OK;
	}
	int DrawLine( long x1, long y1, long x2, long y2 ) {
		x1 += m_origin.x;
		y1 += m_origin.y;
		x2 += m_origin.x;
		y2 += m_origin.y;
		FlipY( y1 );
		FlipY( y2 );
		MoveToEx( m_hDC, x1, y1, NULL );
		LineTo( m_hDC, x2, y2 );
		return S_OK;
	}
	int FillRect( long x, long y, long width, long height ) {
		x += m_origin.x;
		y += m_origin.y;
		RECT sRect;
		sRect.left = x;
		sRect.top = y;
		sRect.right = x+width;
		sRect.bottom = y+height;
		FlipY( sRect.top );
		FlipY( sRect.bottom );
		::FillRect( m_hDC, &sRect, m_hBrush );
		return S_OK;
	}
	int ClearRect( long x, long y, long width, long height ) {
		//todo
		return S_OK;
	}
	int DrawRoundRect( long x, long y, long width, long height, long arcWidth, long arcHeight ) {
		x += m_origin.x;
		y += m_origin.y;
		
		RECT sRect;
		sRect.left = x;
		sRect.top = y;
		sRect.right = x+width;
		sRect.bottom = y+height;
		FlipY( sRect.top );
		FlipY( sRect.bottom );

		HGDIOBJ hBrush = SelectObject( m_hDC, GetStockObject( NULL_BRUSH ) );
		RoundRect( m_hDC, sRect.left, sRect.top, sRect.right, sRect.bottom, arcWidth, arcHeight );
		SelectObject( m_hDC, hBrush );
		return S_OK;
	}
	int FillRoundRect( long x, long y, long width, long height, long arcWidth, long arcHeight ) {
		x += m_origin.x;
		y += m_origin.y;

		RECT sRect;
		sRect.left = x;
		sRect.top = y;
		sRect.right = x+width;
		sRect.bottom = y+height;
		FlipY( sRect.top );
		FlipY( sRect.bottom );
		
		RoundRect( m_hDC, sRect.left, sRect.top, sRect.right, sRect.bottom, arcWidth, arcHeight );

		return S_OK;
	}
	int DrawOval( long x, long y, long width, long height ) {
		x += m_origin.x;
		y += m_origin.y;

		RECT sRect;
		sRect.left = x;
		sRect.top = y;
		sRect.right = x+width;
		sRect.bottom = y+height;
		FlipY( sRect.top );
		FlipY( sRect.bottom );
		
		HGDIOBJ hBrush = SelectObject( m_hDC, GetStockObject( NULL_BRUSH ) );

		Ellipse( m_hDC, sRect.left, sRect.top, sRect.right, sRect.bottom );
		
		SelectObject( m_hDC, hBrush );
		return S_OK;
	}
	int FillOval( long x, long y, long width, long height ) {
		x += m_origin.x;
		y += m_origin.y;

		RECT sRect;
		sRect.left = x;
		sRect.top = y;
		sRect.right = x+width;
		sRect.bottom = y+height;
		FlipY( sRect.top );
		FlipY( sRect.bottom );
		
		Ellipse( m_hDC, sRect.left, sRect.top, sRect.right, sRect.bottom );

		return S_OK;
	}
	int DrawArc( long x, long y, long width, long height, long startAngle, long arcAngle ) {
		//todo
		return S_OK;
	}
	int FillArc( long x, long y, long width, long height, long startAngle, long arcAngle ) {
		//todo
		return S_OK;
	}
	int DrawPolyline( long* vnX, long* vnY, long nPoints ) {
		POINT* vsPoints = new POINT[nPoints];
		for( int i=0; i<nPoints; i++ ) {
			vsPoints[i].x = vnX[i]+m_origin.x;
			vsPoints[i].y = vnY[i]+m_origin.y;
			FlipY( vsPoints[i].y );
		}
		Polyline( m_hDC, vsPoints, nPoints );
		delete vsPoints;
		return S_OK;
	}
	int DrawPolygon( long* vnX, long* vnY, long nPoints ) {
		HGDIOBJ hBrush = SelectObject( m_hDC, GetStockObject( NULL_BRUSH ) );
		POINT* vsPoints = new POINT[nPoints];
		for( int i=0; i<nPoints; i++ ) {
			vsPoints[i].x = vnX[i]+m_origin.x;
			vsPoints[i].y = vnY[i]+m_origin.y;
			FlipY( vsPoints[i].y );
		}
		Polygon( m_hDC, vsPoints, nPoints );
		delete vsPoints;
		SelectObject( m_hDC, hBrush );
		return S_OK;
	}
	int FillPolygon( long* vnX, long* vnY, long nPoints ) {
		POINT* vsPoints = new POINT[nPoints];
		for( int i=0; i<nPoints; i++ ) {
			vsPoints[i].x = vnX[i]+m_origin.x;
			vsPoints[i].y = vnY[i]+m_origin.y;
			FlipY( vsPoints[i].y );
		}
		Polygon( m_hDC, vsPoints, nPoints );
		delete vsPoints;
		return S_OK;
	}
	int DrawString( const char* vcString, long x, long y ) {
		if( vcString ) {
			x += m_origin.x;
			y += m_origin.y;
			FlipY( y );
			if( m_bIsFlipped ) {
				//todo
			}
			int length = MultiByteToWideChar(CP_UTF8, 0, (LPCSTR)vcString, -1, NULL, 0);
			if (length > 0)
			{
				wchar_t* wide = new wchar_t[length];
				MultiByteToWideChar(CP_UTF8, 0, (LPCSTR)vcString, -1, wide, length);

				//size_t convertedChars = 0;
				//char* ansi = new char[length];
				//wcstombs_s(&convertedChars, ansi, length, wide, _TRUNCATE);
//TextOutW(m_hDC, x, y,  &wide, (int)strlen( wide ) );
				TextOutW(m_hDC, x, y,  wide, wcslen( wide ) );
			}
		}
		return S_OK;
	}
	int SetFont( const char* vcFamily, const char* vcName, bool bIsBold, bool bIsItalic, int nSize ) {
		if( m_hFont ) {
			DeleteObject( m_hFont );
			m_hFont = 0;
		}
		if( vcName ) {
			DWORD fnWeight;
			if( bIsBold ) {
				fnWeight = FW_BOLD;
			} else {
				fnWeight = 0;
			}
			m_hFont = CreateFont( nSize, 0, 0, 0, fnWeight, bIsItalic, 0, 0, 0, 0, 0, 0, 0, vcName );
			SelectObject( m_hDC, m_hFont );
		}
		return S_OK;
	}
private:
	void FlipY( long& nY ) {
		if( m_bIsFlipped ) {
			nY = m_nHeight - nY;
		}
	}
	int Lock( LPDIRECTDRAWSURFACE7 pDDSurface ) {
		m_pDDSurface = pDDSurface;
		m_pDDSurface->GetDC( &m_hDC );
		m_hPen = CreatePen( PS_SOLID, 1, 0 );
		m_hBrush = CreateSolidBrush( 0 );
		m_hFont = 0;
		m_origin.x = m_origin.y = 0;

		SelectObject( m_hDC, m_hPen );
		SelectObject( m_hDC, m_hBrush );
		SetBkMode( m_hDC, TRANSPARENT );
		SetTextColor( m_hDC, 0 );
		SetTextAlign( m_hDC, TA_BASELINE );
		return S_OK;
	}
	LPDIRECTDRAWSURFACE7 m_pDDSurface;
	HDC m_hDC;
	HPEN m_hPen;
	HBRUSH m_hBrush;
	HFONT m_hFont;
	COLORREF m_rgb;
	POINT m_origin;
	long m_nHeight;
	bool m_bIsFlipped;
};

#endif   