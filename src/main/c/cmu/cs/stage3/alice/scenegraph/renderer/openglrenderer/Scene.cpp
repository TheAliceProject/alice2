#include "Scene.hpp"
#include "Affector.hpp"
#include "Visual.hpp"
#include "Camera.hpp"

int Scene::Render( RenderTarget* pRenderTarget, void* pContext, Camera* pCamera ) {
	unsigned lcv;
	CHECK_GL( glMatrixMode( GL_MODELVIEW ) );
	for( lcv=0; lcv<m_affectors.size(); lcv++ ) {
		m_affectors[lcv]->Setup( pRenderTarget, pContext );
	}
	CHECK_GL( glDisable( GL_BLEND ) );
	for( lcv=0; lcv<m_opaqueVisuals.size(); lcv++ ) {
		CHECK_SUCCESS( m_opaqueVisuals[lcv]->Render( pRenderTarget, pContext ) );
	}
	CHECK_GL( glEnable( GL_BLEND ) );
	CHECK_GL( glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA ) );
	if( m_semiTransparentVisuals.size() ) {
		for( lcv=0; lcv<m_semiTransparentVisuals.size(); lcv++ ) {
			CHECK_SUCCESS( m_semiTransparentVisuals[lcv]->Render( pRenderTarget, pContext ) );
		}
	}
	return S_OK;
}

int Scene::Pick( void* pContext, bool isSubElementRequired, bool isOnlyFrontMostRequired ) {
	CHECK_GL( glInitNames() );
	CHECK_GL( glPushName( (GLuint)-1 ) );
	unsigned nID = 0;
	CHECK_GL( glDisable( GL_LIGHTING ) );
	CHECK_GL( glDisable( GL_BLEND ) );
	CHECK_GL( glMatrixMode( GL_MODELVIEW ) );
	unsigned lcv;
	for( lcv=0; lcv<m_opaqueVisuals.size(); lcv++ ) {
		CHECK_GL( glLoadName( nID++ ) );
		CHECK_SUCCESS( m_opaqueVisuals[lcv]->Pick( pContext, isSubElementRequired ) );
	}
	for( lcv=0; lcv<m_semiTransparentVisuals.size(); lcv++ ) {
		CHECK_GL( glLoadName( nID++ ) );
		CHECK_SUCCESS( m_semiTransparentVisuals[lcv]->Pick( pContext, isSubElementRequired ) );
	}
	return S_OK;
}
