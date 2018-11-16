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

#ifndef VISUAL_INCLUDED
#define VISUAL_INCLUDED

#include "Component.hpp"
#include "Affector.hpp"
#include "Appearance.hpp"
#include "Geometry.hpp"
#include "Scene.hpp"

class RenderTarget;

#include "VisualState.hpp"

class _Visual : public _Component {
public:
	_Visual() : _Component() {
		m_pGeometry = NULL;
		m_pFrontAppearance = NULL;
		m_pBackAppearance = NULL;
		m_bIsShowing = true;
		m_vpDisabledAffectors = NULL;
		m_nDisabledAffectorCount = 0;

		m_bNeedsToScale = false;
#if defined DX7_RENDERER
		m_bNeedsToComputeScaledAbsolute = true;

		m_fCachedDistance = 0.0f;
		m_sD3DLocation.x = m_sD3DLocation.y = m_sD3DLocation.z = 0.0f;

		D3DUtil_SetIdentityMatrix( m_sD3DMScale );
#elif defined OPENGL_RENDERER
		memcpy( m_vfScale, g_vfIdentity, sizeof( GLdouble[16] ) );
#endif
	}

#if defined DX7_RENDERER
	int OnAbsoluteTransformationChange( double rc00, double rc01, double rc02, double rc03, double rc10, double rc11, double rc12, double rc13, double rc20, double rc21, double rc22, double rc23, double rc30, double rc31, double rc32, double rc33 ) {
		m_bNeedsToComputeScaledAbsolute = true;
		m_sD3DLocation.x = (D3DVALUE)rc30;
		m_sD3DLocation.y = (D3DVALUE)rc31;
		m_sD3DLocation.z = (D3DVALUE)rc32;
		return _Component::OnAbsoluteTransformationChange( rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 );
	}
#endif

	int OnFrontFacingAppearanceChange( Appearance* value ) {
		m_prevState = GetState();
		if( m_pFrontAppearance ) {
			CHECK_SUCCESS( m_pFrontAppearance->RemoveVisual( this ) );
		}
		m_pFrontAppearance = value;
		if( m_pFrontAppearance ) {
			CHECK_SUCCESS( m_pFrontAppearance->AddVisual( this ) );
		}
		CHECK_SUCCESS( CheckForVisualStateChange() );
		return S_OK;
	}
	int OnBackFacingAppearanceChange( Appearance* value ) {
		m_prevState = GetState();
		if( m_pBackAppearance ) {
			CHECK_SUCCESS( m_pBackAppearance->RemoveVisual( this ) );
		}
		m_pBackAppearance = value;
		if( m_pBackAppearance ) {
			CHECK_SUCCESS( m_pBackAppearance->AddVisual( this ) );
		}
		CHECK_SUCCESS( CheckForVisualStateChange() );
		return S_OK;
	}
	int OnGeometryChange( Geometry* value ) {
		m_prevState = GetState();
		m_pGeometry = value;
		CHECK_SUCCESS( CheckForVisualStateChange() );
		return S_OK;
	}

	int OnDisabledAffectorsChange( Affector** vpDisabledAffectors, long nDisabledAffectorCount ) {
		m_vpDisabledAffectors = vpDisabledAffectors;
		m_nDisabledAffectorCount = nDisabledAffectorCount;
		return S_OK;
	}

	int OnScaleChange( double rc00, double rc01, double rc02, double rc10, double rc11, double rc12, double rc20, double rc21, double rc22 ) {
#if defined DX7_RENDERER
		
		m_sD3DMScale._11 = (float)rc00;
		m_sD3DMScale._12 = (float)rc01;
		m_sD3DMScale._13 = (float)rc02;
		m_sD3DMScale._21 = (float)rc10;
		m_sD3DMScale._22 = (float)rc11;
		m_sD3DMScale._23 = (float)rc12;
		m_sD3DMScale._31 = (float)rc20;
		m_sD3DMScale._32 = (float)rc21;
		m_sD3DMScale._33 = (float)rc22;
		m_bNeedsToScale = !( 0 == memcmp( &g_sD3DMIdentity, &m_sD3DMScale, sizeof(D3DMATRIX) ) );
		m_bNeedsToComputeScaledAbsolute = true;

#elif defined OPENGL_RENDERER
		
		m_vfScale[ 0 ] = rc00;
		m_vfScale[ 1 ] = rc01;
		m_vfScale[ 2 ] = rc02;

		m_vfScale[ 4 ] = rc10;
		m_vfScale[ 5 ] = rc11;
		m_vfScale[ 6 ] = rc12;

		m_vfScale[ 8 ] = rc20;
		m_vfScale[ 9 ] = rc21;
		m_vfScale[ 10 ] = rc22;

		m_bNeedsToScale = !( 0 == memcmp( g_vfIdentity, m_vfScale, sizeof( GLdouble[16] ) ) );

#endif
		return S_OK;
	}
	int OnIsShowingChange( bool value ) {
		m_prevState = GetState();
		m_bIsShowing = value;
		CHECK_SUCCESS( CheckForVisualStateChange() );
		return S_OK;
	}
	int AddToScene( Scene* pScene ) {
		CHECK_SUCCESS( _Component::AddToScene( pScene ) );
		m_scenes.push_back( pScene );
		m_prevState = GetState();
		return pScene->AddVisual( this, GetState() );
	}
	int RemoveFromScene( Scene* pScene ) {
		CHECK_SUCCESS( _Component::RemoveFromScene( pScene ) );
		std::vector<Scene*>::iterator iTarget;
		iTarget = std::find( m_scenes.begin(), m_scenes.end(), pScene );
		CHECK_TRUTH( m_scenes.end() != iTarget );
		m_scenes.erase( iTarget );
		return pScene->RemoveVisual( this, GetState() );
	}

	int Render( RenderTarget* pRenderTarget, void* pContext ) {
		bool bIsVisible;
		CHECK_SUCCESS( ComputeVisibility( pContext, bIsVisible ) ); 
		if( bIsVisible ) {
#if defined DX7_RENDERER
			LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
			if( m_bNeedsToScale ) {
				CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_NORMALIZENORMALS, TRUE ) );
			} else {
				CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_NORMALIZENORMALS, FALSE ) );
			}
			if( m_nDisabledAffectorCount ) {
				for( int i=0; i<m_nDisabledAffectorCount; i++ ) {
					CHECK_SUCCESS( m_vpDisabledAffectors[i]->Disable( pRenderTarget, pContext ) );
				}
			}
			if( m_pBackAppearance ) {
				CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CULLMODE, D3DCULL_CW  ) );
				if( m_pBackAppearance->IsRequiringOfLighting() ) {
					CHECK_SUCCESS( m_pGeometry->ReverseLighting() );
				}
				CHECK_SUCCESS( m_pBackAppearance->Render( pRenderTarget, pContext ) );
				CHECK_SUCCESS( m_pGeometry->Render( pRenderTarget, pContext ) );
				if( m_pBackAppearance->IsRequiringOfLighting() ) {
					CHECK_SUCCESS( m_pGeometry->ReverseLighting() );
				}
				CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CULLMODE, D3DCULL_CCW  ) );
			}
			if( m_pFrontAppearance ) {
				CHECK_SUCCESS( m_pFrontAppearance->Render( pRenderTarget, pContext ) );
				CHECK_SUCCESS( m_pGeometry->Render( pRenderTarget, pContext ) );
			}
			if( m_nDisabledAffectorCount ) {
				for( int i=0; i<m_nDisabledAffectorCount; i++ ) {
					CHECK_SUCCESS( m_vpDisabledAffectors[i]->Enable( pRenderTarget, pContext ) );
				}
			}
#elif defined OPENGL_RENDERER
			CHECK_GL( glPushMatrix() );

			//todo: remove?
			//GLint nDepth;
			//glGetIntegerv( GL_MODELVIEW_STACK_DEPTH, &nDepth );
			//assert( nDepth==2 );
			
			CHECK_GL( glMultMatrixd( m_vfAbsolute ) );
			if( m_bNeedsToScale ) {
				CHECK_GL( glMultMatrixd( m_vfScale ) );
				CHECK_GL( glEnable( GL_NORMALIZE ) );
			} else {
				CHECK_GL( glDisable( GL_NORMALIZE ) );
			}
			if( m_pFrontAppearance ) {
				if( m_pBackAppearance ) {
					CHECK_GL( glDisable( GL_CULL_FACE ) );
					//if( m_pFrontAppearance == m_pBackAppearance ) {
					//	CHECK_SUCCESS( m_pFrontAppearance->Render( pRenderTarget, GL_FRONT_AND_BACK ) );
					//} else {
						CHECK_SUCCESS( m_pFrontAppearance->SetPipelineState( pRenderTarget, GL_FRONT ) );
						CHECK_SUCCESS( m_pBackAppearance->SetPipelineState( pRenderTarget, GL_BACK ) );
					//}
				} else {
					CHECK_GL( glEnable( GL_CULL_FACE ) );
					CHECK_GL( glCullFace( GL_BACK ) );
					CHECK_SUCCESS( m_pFrontAppearance->SetPipelineState( pRenderTarget, GL_FRONT ) );
				}
			} else {
				if( m_pBackAppearance ) {
					CHECK_GL( glEnable( GL_CULL_FACE ) );
					CHECK_GL( glCullFace( GL_FRONT ) );
					CHECK_SUCCESS( m_pBackAppearance->SetPipelineState( pRenderTarget, GL_BACK ) );
				} else {
					assert( false );
				}
			}
			if( m_nDisabledAffectorCount ) {
				for( int i=0; i<m_nDisabledAffectorCount; i++ ) {
					CHECK_SUCCESS( m_vpDisabledAffectors[i]->Disable( pRenderTarget, pContext ) );
				}
			}
			CHECK_SUCCESS( m_pGeometry->Render( pRenderTarget, pContext ) );
			if( m_nDisabledAffectorCount ) {
				for( int i=0; i<m_nDisabledAffectorCount; i++ ) {
					CHECK_SUCCESS( m_vpDisabledAffectors[i]->Enable( pRenderTarget, pContext ) );
				}
			}
			CHECK_GL( glPopMatrix() );
#endif
		}
		return S_OK;
	}
	int Pick( void* pContext, bool isSubElementRequired ) {
		bool bIsVisible;
		CHECK_SUCCESS( ComputeVisibility( pContext, bIsVisible ) ); 
		if( bIsVisible ) {
#if defined DX7_RENDERER
			if( m_pBackAppearance ) {
				CHECK_SUCCESS( m_pGeometry->Pick( pContext, isSubElementRequired, false ) );
			}
			if( m_pFrontAppearance ) {
				CHECK_SUCCESS( m_pGeometry->Pick( pContext, isSubElementRequired, true ) );
			}
#elif defined OPENGL_RENDERER
			CHECK_GL( glPushMatrix() );
			CHECK_GL( glMultMatrixd( m_vfAbsolute ) );
			if( m_bNeedsToScale ) {
				CHECK_GL( glMultMatrixd( m_vfScale ) );
			}
			if( m_pBackAppearance ) {
				CHECK_SUCCESS( m_pGeometry->Pick( pContext, isSubElementRequired, false ) );
			}
			if( m_pFrontAppearance ) {
				CHECK_SUCCESS( m_pGeometry->Pick( pContext, isSubElementRequired, true ) );
			}
			CHECK_GL( glPopMatrix() );
#endif
		}
		return S_OK;
	}
	int CheckForVisualStateChange() {
		enum VisualState currState = GetState();
		if( m_prevState!=currState ) {
			for( unsigned i=0; i<m_scenes.size(); i++ ) {
				CHECK_SUCCESS( m_scenes[i]->OnVisualStateChange( this, m_prevState, currState ) );
			}
			m_prevState = currState;
		}
		return S_OK;
	}

#if defined DX7_RENDERER
	int CacheDistance( const D3DVECTOR& sD3DCameraDirection, float fDotProductCameraLocationByCameraDirection ) {
		m_fCachedDistance = DotProduct( m_sD3DLocation, sD3DCameraDirection ) - fDotProductCameraLocationByCameraDirection;
		return S_OK;
	}
	float GetCachedDistance() {
		return m_fCachedDistance;
	}
#endif

private:
	int ComputeVisibility( void* pContext, bool& bIsVisible ) {
		if( m_bIsShowing && m_pGeometry && ( m_pFrontAppearance || m_pBackAppearance ) ) {
#if defined DX7_RENDERER
			if( m_bNeedsToComputeScaledAbsolute ) {
				D3DXMatrixMultiply( (D3DXMATRIX*)&m_sD3DMScaledAbsolute, (D3DXMATRIX*)&m_sD3DMScale, (D3DXMATRIX*)&m_sD3DMAbsolute );
				m_bNeedsToComputeScaledAbsolute = false;
			}

			LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;

			if( m_bNeedsToScale ) {
				CHECK_SUCCESS( pD3DDevice->SetTransform( D3DTRANSFORMSTATE_WORLD, &m_sD3DMScaledAbsolute ) );
			} else {
				CHECK_SUCCESS( pD3DDevice->SetTransform( D3DTRANSFORMSTATE_WORLD, &m_sD3DMAbsolute ) );
			}
			CHECK_SUCCESS( m_pGeometry->ComputeVisibility( pContext, bIsVisible ) );
#elif defined OPENGL_RENDERER
			//todo
			bIsVisible = true;
#endif
		} else {
			bIsVisible = false;
		}
		return S_OK;
	}
	enum VisualState GetState() {
		if( m_bIsShowing && m_pGeometry && m_pFrontAppearance ) {
			if( m_pFrontAppearance->IsOpaque() ) {
				return VISUAL_OPAQUE;
			} else {
				return VISUAL_SEMI_TRANSPARENT;
			}
		} else {
			return VISUAL_HIDDEN;
		}
	}

	enum VisualState m_prevState;

	Geometry* m_pGeometry;
	Appearance* m_pFrontAppearance;
	Appearance* m_pBackAppearance;
	bool m_bIsShowing;
	Affector** m_vpDisabledAffectors;
	long m_nDisabledAffectorCount;

	std::vector<Scene*> m_scenes;

	bool m_bNeedsToScale;
#if defined DX7_RENDERER
	float m_fCachedDistance;
	D3DVECTOR m_sD3DLocation;
	bool m_bNeedsToComputeScaledAbsolute;
	D3DMATRIX m_sD3DMScale;
	D3DMATRIX m_sD3DMScaledAbsolute;
#elif defined OPENGL_RENDERER
	GLdouble m_vfScale[16];
#endif
};

#endif