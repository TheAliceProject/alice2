#ifndef RENDER_CANVAS_INCLUDED
#define RENDER_CANVAS_INCLUDED

#include "RenderTarget.hpp"

#include <gl/glaux.h>

class RenderCanvas {
public:
	RenderCanvas( RenderTarget* pRenderTarget ) {
		m_hWnd = NULL;
		m_hDC = NULL;
		m_hGLRC = NULL;
		m_pRenderTarget = pRenderTarget;
		m_pRenderTarget->SetRenderCanvas( this );
	}
	int Release() {
		if( m_hGLRC ) {
			wglDeleteContext( m_hGLRC );
			m_hGLRC = NULL;
		}
		if( m_hWnd && m_hDC ) {
			ReleaseDC( m_hWnd, m_hDC );
			m_hDC = NULL;
		}
		return S_OK;
	}

	void OnAcquireDrawingSurface( HWND hWnd, bool& bIsValid ) {
		if( m_hWnd != hWnd ) {
			m_hWnd = hWnd;
			Release();
		}
		bIsValid = m_hWnd && IsWindow( m_hWnd ) && ::IsWindowVisible( m_hWnd );
	}
	void OnReleaseDrawingSurface() {
	}


	int OnFullscreenDisplayModeChange( FullscreenDisplayMode* pFullscreenDisplayMode ) {
		//todo
		return S_OK;
	}
	int OnOverlappedDisplayModeChange() {
		//todo
		return S_OK;
	}
	int SwapBuffers() {
		CHECK_SUCCESS( CommitIfNecessary() );
		if( m_hDC && m_hGLRC ) {
			if( wglMakeCurrent( m_hDC, m_hGLRC ) ) {
				::SwapBuffers( m_hDC );
				wglMakeCurrent( NULL, NULL );
			} else {
				fprintf( stderr, "cannot SwapBuffers %d\n", GetLastError() );
			}
		}
		return S_OK;
	}
	int Prologue() {
		CHECK_SUCCESS( CommitIfNecessary() );
		if( m_hDC && m_hGLRC ) {
			if( wglMakeCurrent( m_hDC, m_hGLRC ) ) {
				//pass
			} else {
				fprintf( stderr, "cannot SwapBuffers %d\n", GetLastError() );
			}
		}
		return S_OK;
	}
	int Epilogue() {
		wglMakeCurrent( NULL, NULL );
		return S_OK;
	}
	int CommitIfNecessary() {
		if( m_hGLRC ) {
			//pass
		} else {
			//
			// Attach the window dc to OpenGL.
			//
			m_hDC = ::GetDC( m_hWnd ) ;
			CHECK_TRUTH( m_hDC );
			//
			// Fill in the Pixel Format Descriptor
			//
			PIXELFORMATDESCRIPTOR pfd ;
			memset(&pfd,0, sizeof(PIXELFORMATDESCRIPTOR)) ;

			pfd.nSize = sizeof(PIXELFORMATDESCRIPTOR);   
			pfd.nVersion = 1 ;                           // Version number
			pfd.dwFlags =  PFD_DOUBLEBUFFER |            // Use double buffer
						   PFD_SUPPORT_OPENGL |          // Use OpenGL
						   PFD_DRAW_TO_WINDOW ;          // Pixel format is for a window.
			pfd.iPixelType = PFD_TYPE_RGBA ;
			pfd.cColorBits = 24;                         // 8-bit color
			pfd.cDepthBits = 32 ;					   	 // 32-bit depth buffer
			pfd.iLayerType = PFD_MAIN_PLANE ;            // Layer type

			int nPixelFormat = ChoosePixelFormat( m_hDC, &pfd );
			CHECK_TRUTH( nPixelFormat );

			BOOL bResult = SetPixelFormat( m_hDC, nPixelFormat, &pfd );
			CHECK_TRUTH( bResult );
			
			m_hGLRC = wglCreateContext( m_hDC );
			
			CHECK_TRUTH( m_hGLRC );

			CHECK_SUCCESS( m_pRenderTarget->ClearDisplayLists() );
			CHECK_SUCCESS( m_pRenderTarget->ClearTextureObjects() );
		}
		return S_OK;
	}
private:
	HWND m_hWnd;
	HDC m_hDC;
	HGLRC m_hGLRC;
	RenderTarget* m_pRenderTarget;
};

#endif
