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

#include "Scene.hpp"
#include "Affector.hpp"
#include "Visual.hpp"
#include "Camera.hpp"

struct CompareVisualCachedDistance {
	bool operator()( _Visual* pA, _Visual* pB ){
		//todo: remove inconsistency?
		if( pA->GetCachedDistance() > pB->GetCachedDistance() )
			return true;
		return false;
	}
};

/*
bool HACK_IsInArray( _Visual* pVisual, _Visual** vpArray, int nLength ) {
	for( int i=0; i<nLength; i++ ) {
		if( vpArray[ i ] == pVisual ) {
			return true;
		}
	}
	return false;
}

int Scene::HACK_Render( RenderTarget* pRenderTarget, void* pContext, Camera* pCamera, _Visual** vpVisuals, int nVisualCount ) {
	LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
	unsigned lcv;
	for( lcv=0; lcv<m_affectors.size(); lcv++ ) {
		m_affectors[lcv]->Setup( pRenderTarget, pContext );
	}
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ALPHABLENDENABLE, FALSE ) );
	CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 0, D3DTSS_ALPHAOP, D3DTOP_SELECTARG2 ) );
	for( lcv=0; lcv<m_opaqueVisuals.size(); lcv++ ) {
		if( HACK_IsInArray( m_opaqueVisuals[lcv], vpVisuals, nVisualCount ) ) {
			CHECK_SUCCESS( m_opaqueVisuals[lcv]->Render( pRenderTarget, pContext ) );
		}
	}
	if( m_semiTransparentVisuals.size() ) {
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ALPHABLENDENABLE, TRUE ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_SRCBLEND, D3DBLEND_SRCALPHA ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_DESTBLEND, D3DBLEND_INVSRCALPHA ) );
		CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 0, D3DTSS_ALPHAOP, D3DTOP_MODULATE ) );

		D3DVECTOR sD3DCameraDirection;
		float fDotProductCameraLocationByCameraDirection;
		CHECK_SUCCESS( pCamera->GetDirection( sD3DCameraDirection ) );
		CHECK_SUCCESS( pCamera->GetDotProductLocationByDirection( fDotProductCameraLocationByCameraDirection ) );

		for( lcv=0; lcv<m_semiTransparentVisuals.size(); lcv++ ) {
			CHECK_SUCCESS( m_semiTransparentVisuals[lcv]->CacheDistance( sD3DCameraDirection, fDotProductCameraLocationByCameraDirection ) );
		}
		std::sort( m_semiTransparentVisuals.begin(), m_semiTransparentVisuals.end(), CompareVisualCachedDistance() );

		Renderer* pRenderer;
		pRenderTarget->GetRenderer( pRenderer );
		bool bIsZBufferWriteForSemiTransparentObjectsEnabled = true;
		if( bIsZBufferWriteForSemiTransparentObjectsEnabled ) {
			//pass
		} else {
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ZWRITEENABLE, FALSE ) );
		}

		for( lcv=0; lcv<m_semiTransparentVisuals.size(); lcv++ ) {
			if( HACK_IsInArray( m_semiTransparentVisuals[lcv], vpVisuals, nVisualCount ) ) {
				CHECK_SUCCESS( m_semiTransparentVisuals[lcv]->Render( pRenderTarget, pContext ) );
			}
		}

		if( bIsZBufferWriteForSemiTransparentObjectsEnabled ) {
			//pass
		} else {
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ZWRITEENABLE, TRUE ) );
		}
	}
	return S_OK;
}
*/

int Scene::Render( RenderTarget* pRenderTarget, void* pContext, Camera* pCamera ) {
	LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
	unsigned lcv;
	
	for( lcv=0; lcv<m_affectors.size(); lcv++ ) {
		m_affectors[lcv]->Setup( pRenderTarget, pContext );
	}
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ALPHABLENDENABLE, FALSE ) );
	CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 0, D3DTSS_ALPHAOP, D3DTOP_SELECTARG2 ) );
	for( lcv=0; lcv<m_opaqueVisuals.size(); lcv++ ) {
		CHECK_SUCCESS( m_opaqueVisuals[lcv]->Render( pRenderTarget, pContext ) );
	}
	if( m_semiTransparentVisuals.size() ) {
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ALPHABLENDENABLE, TRUE ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_SRCBLEND, D3DBLEND_SRCALPHA ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_DESTBLEND, D3DBLEND_INVSRCALPHA ) );
		CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 0, D3DTSS_ALPHAOP, D3DTOP_MODULATE ) );

		D3DVECTOR sD3DCameraDirection;
		float fDotProductCameraLocationByCameraDirection;
		CHECK_SUCCESS( pCamera->GetDirection( sD3DCameraDirection ) );
		CHECK_SUCCESS( pCamera->GetDotProductLocationByDirection( fDotProductCameraLocationByCameraDirection ) );

		for( lcv=0; lcv<m_semiTransparentVisuals.size(); lcv++ ) {
			CHECK_SUCCESS( m_semiTransparentVisuals[lcv]->CacheDistance( sD3DCameraDirection, fDotProductCameraLocationByCameraDirection ) );
		}
		std::sort( m_semiTransparentVisuals.begin(), m_semiTransparentVisuals.end(), CompareVisualCachedDistance() );

		Renderer* pRenderer;
		pRenderTarget->GetRenderer( pRenderer );
		bool bIsZBufferWriteForSemiTransparentObjectsEnabled = true;
		if( bIsZBufferWriteForSemiTransparentObjectsEnabled ) {
			//pass
		} else {
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ZWRITEENABLE, FALSE ) );
		}

		for( lcv=0; lcv<m_semiTransparentVisuals.size(); lcv++ ) {
			CHECK_SUCCESS( m_semiTransparentVisuals[lcv]->Render( pRenderTarget, pContext ) );
		}

		if( bIsZBufferWriteForSemiTransparentObjectsEnabled ) {
			//pass
		} else {
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ZWRITEENABLE, TRUE ) );
		}
	}
	return S_OK;
}

int Scene::Pick( void* pContext, bool isSubElementRequired, bool isOnlyFrontMostRequired ) {
	//todo: set up clipping planes
	HACK_g_nVisualID = 0;
	unsigned lcv;
	for( lcv=0; lcv<m_opaqueVisuals.size(); lcv++ ) {
		//CHECK_SUCCESS( pRenderer->GetNextVisualID()
		CHECK_SUCCESS( m_opaqueVisuals[lcv]->Pick( pContext, isSubElementRequired ) );
		HACK_g_nVisualID++;
	}
	for( lcv=0; lcv<m_semiTransparentVisuals.size(); lcv++ ) {
		CHECK_SUCCESS( m_semiTransparentVisuals[lcv]->Pick( pContext, isSubElementRequired ) );
		HACK_g_nVisualID++;
	}
	return S_OK;
}

