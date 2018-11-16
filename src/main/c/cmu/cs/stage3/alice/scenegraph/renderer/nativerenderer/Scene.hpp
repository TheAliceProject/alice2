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

#ifndef SCENE_INCLUDED
#define SCENE_INCLUDED

#include "ReferenceFrame.hpp"
#include <vector>
#include <algorithm>

class Background;
class _Visual;
class Affector;
class Camera;
class OrthographicCamera;
class RenderTarget;
class Renderer;

#include "VisualState.hpp"

class Scene : public ReferenceFrame {
public:
	int GetBackground( Background*& pBackground ) {
		pBackground = m_pBackground;
		return S_OK;
	}
	int OnBackgroundChange( Background* value ) {
		m_pBackground = value;
		return S_OK;
	}

	int OnVisualStateChange( _Visual* pVisual, enum VisualState from, enum VisualState to ) {
		CHECK_SUCCESS( RemoveVisual( pVisual, from ) );
		CHECK_SUCCESS( AddVisual( pVisual, to ) );
		return S_OK;
	}

	int AddVisual( _Visual* pVisual, enum VisualState state ) {
		switch( state ) {
		case VISUAL_OPAQUE:
			m_opaqueVisuals.push_back( pVisual );
			break;
		case VISUAL_SEMI_TRANSPARENT:
			m_semiTransparentVisuals.push_back( pVisual );
			break;
		case VISUAL_HIDDEN:
			m_hiddenVisuals.push_back( pVisual );
			break;
		default:
			return -1;
		}
		return S_OK;
	}
	int RemoveVisual( _Visual* pVisual, enum VisualState state ) {
		std::vector<_Visual*>::iterator iBegin;
		std::vector<_Visual*>::iterator iEnd;
		switch( state ) {
		case VISUAL_OPAQUE:
			iBegin = m_opaqueVisuals.begin();
			iEnd = m_opaqueVisuals.end();
			break;
		case VISUAL_SEMI_TRANSPARENT:
			iBegin = m_semiTransparentVisuals.begin();
			iEnd = m_semiTransparentVisuals.end();
			break;
		case VISUAL_HIDDEN:
			iBegin = m_hiddenVisuals.begin();
			iEnd = m_hiddenVisuals.end();
			break;
		default:
			return -1;
		}
		std::vector<_Visual*>::iterator iTarget = std::find( iBegin, iEnd, pVisual );
		CHECK_TRUTH( iEnd != iTarget );
		switch( state ) {
		case VISUAL_OPAQUE:
			m_opaqueVisuals.erase( iTarget );
			break;
		case VISUAL_SEMI_TRANSPARENT:
			m_semiTransparentVisuals.erase( iTarget );
			break;
		case VISUAL_HIDDEN:
			m_hiddenVisuals.erase( iTarget );
			break;
		default:
			return -1;
		}
		return S_OK;
	}



	int AddAffector( Affector* pAffector ) {
		m_affectors.push_back( pAffector );
		return S_OK;
	}
	int RemoveAffector( Affector* pAffector ) {
		std::vector<Affector*>::iterator iTarget;
		iTarget = std::find( m_affectors.begin(), m_affectors.end(), pAffector );
		CHECK_TRUTH( m_affectors.end() != iTarget );
		m_affectors.erase( iTarget );
		return S_OK;
	}

	int Render( RenderTarget* pRenderTarget, void* pContext, Camera* pCamera );
	int Pick( void* pContext, bool isSubElementRequired, bool isOnlyFrontMostRequired );

	int GetVisual( int nVisualID, _Visual*& pVisual ) {
		int nOpaqueCount = (int)m_opaqueVisuals.size();
		int nSemiTransparentCount = (int)m_semiTransparentVisuals.size();
		CHECK_TRUTH( nVisualID>=0 );
		CHECK_TRUTH( nVisualID<(nOpaqueCount+nSemiTransparentCount) );
		if( nVisualID<nOpaqueCount ) {
			pVisual = m_opaqueVisuals[nVisualID];
		} else { 
			pVisual = m_semiTransparentVisuals[nVisualID-nOpaqueCount];
		}
		return S_OK;
	}

private:
	Background* m_pBackground;
	std::vector<Affector*> m_affectors;
	std::vector<_Visual*> m_opaqueVisuals;
	std::vector<_Visual*> m_semiTransparentVisuals;
	std::vector<_Visual*> m_hiddenVisuals;
};

#endif