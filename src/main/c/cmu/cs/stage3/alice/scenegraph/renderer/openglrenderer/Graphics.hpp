#ifndef GRAPHICS_INCLUDED
#define GRAPHICS_INCLUDED

#include "RenderTarget.hpp"
class TextureMap;

class Graphics {
public:
	Graphics() {
	}
	int Lock( RenderTarget* pRenderTarget ) {
		m_pRenderTarget = pRenderTarget;
		CHECK_SUCCESS( m_pRenderTarget->OverlayBegin() );
		return S_OK;
	}
	int Lock( RenderTarget* pRenderTarget, TextureMap* pTextureMap ) {
		m_pRenderTarget = pRenderTarget;
		CHECK_SUCCESS( m_pRenderTarget->OverlayBegin( pTextureMap ) );
		return S_OK;
	}
	int Release() {
		if( m_pRenderTarget ) {
			CHECK_SUCCESS( m_pRenderTarget->OverlayEnd() );
			m_pRenderTarget = NULL;
		}
		return S_OK;
	}

	int Translate( long x, long y ) {
		CHECK_GL( glTranslatef( x, y, 0 ) );
		return S_OK;
	}
	int GetColor( long& red, long& green, long& blue ) {
		GLint vnColor[4];
		CHECK_GL( glGetIntegerv( GL_CURRENT_COLOR, vnColor ) );
		red = vnColor[ 0 ];
		green = vnColor[ 1 ];
		blue = vnColor[ 2 ];
		return S_OK;
	}
	int SetColor( long red, long green, long blue ) {
		CHECK_GL( glColor3ub( red, green, blue ) );
		return S_OK;
	}
	int SetPaintMode() {
		return S_OK;
	}
	int SetXORMode() {
		return S_OK;
	}
	int CopyArea( long x, long y, long width, long height, long dx, long dy ) {
		return S_OK;
	}
	int DrawLine( long x1, long y1, long x2, long y2 ) {
		glBegin( GL_LINES );
		glVertex2i( x1, y1 );
		glVertex2i( x2, y2 );
		CHECK_GL( glEnd() );
		return S_OK;
	}
	int FillRect( long x, long y, long width, long height ) {
		glBegin( GL_QUAD_STRIP );
		glVertex2i( x, y );
		glVertex2i( x+width, y );
		glVertex2i( x+width, y+height );
		glVertex2i( x, y+height );
		CHECK_GL( glEnd() );
		return S_OK;
	}
	int ClearRect( long x, long y, long width, long height ) {
		CHECK_SUCCESS( FillRect( x, y, width, height ) );
		return S_OK;
	}
	int DrawRoundRect( long x, long y, long width, long height, long arcWidth, long arcHeight ) {
		return S_OK;
	}
	int FillRoundRect( long x, long y, long width, long height, long arcWidth, long arcHeight ) {
		return S_OK;
	}
	int DrawOval( long x, long y, long width, long height ) {
		return S_OK;
	}
	int FillOval( long x, long y, long width, long height ) {
		return S_OK;
	}
	int DrawArc( long x, long y, long width, long height, long startAngle, long arcAngle ) {
		return S_OK;
	}
	int FillArc( long x, long y, long width, long height, long startAngle, long arcAngle ) {
		return S_OK;
	}
	int DrawPolyline( long* vnX, long* vnY, long nPoints ) {
		glBegin( GL_LINE_STRIP );
		for( int i=0; i<nPoints; i++ ) {
			glVertex2i( vnX[ i ], vnX[ i ] );
		}
		CHECK_GL( glEnd() );
		return S_OK;
	}
	int DrawPolygon( long* vnX, long* vnY, long nPoints ) {
		glBegin( GL_LINE_LOOP );
		for( int i=0; i<nPoints; i++ ) {
			glVertex2i( vnX[ i ], vnX[ i ] );
		}
		CHECK_GL( glEnd() );
		return S_OK;
	}
	int FillPolygon( long* vnX, long* vnY, long nPoints ) {
		glBegin( GL_POLYGON );
		for( int i=0; i<nPoints; i++ ) {
			glVertex2i( vnX[ i ], vnX[ i ] );
		}
		CHECK_GL( glEnd() );
		return S_OK;
	}
	int DrawString( const char* vcString, long x, long y ) {
		return S_OK;
	}
	int SetFont( const char* vcFamily, const char* vcName, bool bIsBold, bool bIsItalic, int nSize ) {
		return S_OK;
	}
private:
	RenderTarget* m_pRenderTarget;
};

#endif   