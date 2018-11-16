#include "Camera.hpp"
#include "RenderTarget.hpp"
#include "Background.hpp"
#include "Scene.hpp"

int Camera::Clear( RenderTarget* pRenderTarget, void* pContext, const Viewport& iOuterViewport, const Viewport& iActualViewport ) {
	Background* pBackground;
	if( m_pBackground ) {
		pBackground = m_pBackground;
	} else {
		if( m_pScene ) {
			CHECK_SUCCESS( m_pScene->GetBackground( pBackground ) );
		} else {
			pBackground = NULL;
		}
	}
	if( pBackground ) {
		if( iOuterViewport != iActualViewport ) {
			//todo: only clear difference
			/*
			if( iOuterViewport.nWidth > iActualViewport.nWidth ) {
				if( iOuterViewport.nHeight > iActualViewport.nHeight ) {
				} else {
				}
			} else {
				if( iOuterViewport.nHeight > iActualViewport.nHeight ) {
					Viewport iTopBar( iOuterViewport.nX, iOuterViewport.nY, iOuterViewport.nWidth, iActualViewport.nHeight );
				} else {
					//todo?
				}
			}
			*/
			CHECK_SUCCESS( pBackground->ClearColorBufferToBlack( pRenderTarget, pContext, iOuterViewport ) );
		}
		CHECK_SUCCESS( pBackground->Clear( pRenderTarget, pContext, iActualViewport, true, true, false ) );
	}
	return S_OK;
}

int Camera::Setup( RenderTarget* pRenderTarget, void* pContext, const Viewport& iViewport ) {
	CHECK_SUCCESS( UpdateIfNecessary( iViewport ) );
	CHECK_GL( glViewport( iViewport.nX, iViewport.nY, iViewport.nWidth, iViewport.nHeight ) );
	
	CHECK_GL( glMatrixMode( GL_PROJECTION ) );
	CHECK_GL( glLoadMatrixd( m_vfProjection ) );

	CHECK_GL( glMatrixMode( GL_MODELVIEW ) );
	CHECK_GL( glLoadMatrixd( m_vfView ) );
	
	//todo: remove?
	//GLint nDepth;
	//glGetIntegerv( GL_MODELVIEW_STACK_DEPTH, &nDepth );
	//assert( nDepth==1 );
	return S_OK;
}

int Camera::Pick( Renderer* pRenderer, long nX, long nY, const Viewport& iViewport, bool isSubElementRequired, bool isOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ ) {
	CHECK_SUCCESS( UpdateIfNecessary( iViewport ) );
	CHECK_SUCCESS( pRenderer->PerformPick( this, m_vfProjection, m_vfView, nX, nY, iViewport, isSubElementRequired, isOnlyFrontMostRequired, pVisual, bIsFrontFacingAppearance, nSubElement, fZ ) );
	return S_OK;
}
