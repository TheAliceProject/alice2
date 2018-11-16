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

#ifndef APPEARANCE_INCLUDED
#define APPEARANCE_INCLUDED

#include <vector>
#include <algorithm>

#include "Element.hpp"
#include "RenderTarget.hpp"
#include "TextureMap.hpp"
class _Visual;

enum {
	FILLING_STYLE_POINTS = 1,
	FILLING_STYLE_WIREFRAME = 2,
	FILLING_STYLE_SOLID = 4
};
enum {
	SHADING_STYLE_NONE = 0,
	SHADING_STYLE_FLAT = 1,
	SHADING_STYLE_SMOOTH = 2,
};

class Appearance : public Element {
public:
	Appearance() {
		Element::Element();
		ZeroMemory( &m_sD3DMaterial, sizeof(D3DMATERIAL7) );
		m_sD3DMaterial.dcvDiffuse.r = m_sD3DMaterial.dcvAmbient.r = 0;
		m_sD3DMaterial.dcvDiffuse.g = m_sD3DMaterial.dcvAmbient.g = 0;
		m_sD3DMaterial.dcvDiffuse.b = m_sD3DMaterial.dcvAmbient.b = 0;
		m_sD3DMaterial.dcvDiffuse.a = m_sD3DMaterial.dcvAmbient.a = 1;
		OnFillingStyleChange( FILLING_STYLE_SOLID );
		OnShadingStyleChange( SHADING_STYLE_SMOOTH );
		m_pDiffuseColorMap = NULL;
		m_pOpacityMap = NULL;
	}
	bool IsRequiringOfLighting() {
		return m_bLightingEnabled!=0;
	}
	int AddVisual( _Visual* pVisual ) {
		m_visuals.push_back( pVisual );
		return S_OK;
	}
	int RemoveVisual( _Visual* pVisual ) {
		std::vector<_Visual*>::iterator iTarget;
		iTarget = std::find( m_visuals.begin(), m_visuals.end(), pVisual );
		CHECK_TRUTH( m_visuals.end() != iTarget );
		m_visuals.erase( iTarget );
		return S_OK;
	}

	int OnAmbientColorChange( double r, double g, double b, double a ) {
		m_sD3DMaterial.ambient.r = (float)r;
		m_sD3DMaterial.ambient.g = (float)g;
		m_sD3DMaterial.ambient.b = (float)b;
		m_sD3DMaterial.ambient.a = (float)a;
		return S_OK;
	}
	int OnDiffuseColorChange( double r, double g, double b, double a ) {
		m_sD3DMaterial.diffuse.r = (float)r;
		m_sD3DMaterial.diffuse.g = (float)g;
		m_sD3DMaterial.diffuse.b = (float)b;
		//todo
		//m_sD3DMaterial.diffuse.a = (float)a;
		return S_OK;
	}
	int OnFillingStyleChange( int value ) {
		switch( value ) {
		case FILLING_STYLE_SOLID:
			m_dwFillMode  = D3DFILL_SOLID;
			break;
		case FILLING_STYLE_WIREFRAME:
			m_dwFillMode  = D3DFILL_WIREFRAME;
			break;
		case FILLING_STYLE_POINTS:
			m_dwFillMode  = D3DFILL_POINT;
			break;
		default:
			return -1;
		}
		return S_OK;
	}
	int OnShadingStyleChange( int value ) {
		switch( value ) {
		case SHADING_STYLE_SMOOTH:
			m_bLightingEnabled = true;
			m_dwShadeMode = D3DSHADE_GOURAUD;
			break;
		case SHADING_STYLE_FLAT:
			m_bLightingEnabled = true;
			m_dwShadeMode = D3DSHADE_FLAT;
			break;
		case SHADING_STYLE_NONE:
			m_bLightingEnabled = false;
			m_dwShadeMode = -1;
			break;
		default:
			return -1;
		}
		return S_OK;
	}
	int OnOpacityChange( double value ) {
		float prev = m_sD3DMaterial.diffuse.a;
		m_sD3DMaterial.diffuse.a = (float)value;
		if( prev < 1.0f ) {
			if( value >= 1.0f ) {
				CHECK_SUCCESS( CheckForVisualStateChange() );
			}
		} else {
			if( value < 1.0f ) {
				CHECK_SUCCESS( CheckForVisualStateChange() );
			}
		}
		return S_OK;
	}
	int OnSpecularHighlightColorChange( double r, double g, double b, double a ) {
		m_sD3DMaterial.specular.r = (float)r;
		m_sD3DMaterial.specular.g = (float)g;
		m_sD3DMaterial.specular.b = (float)b;
		m_sD3DMaterial.specular.a = (float)a;
		return S_OK;
	}
	int OnSpecularHighlightExponentChange( double value ) {
		m_sD3DMaterial.power = (float)value;
		return S_OK;
	}
	int OnEmissiveColorChange( double r, double g, double b, double a ) {
		m_sD3DMaterial.emissive.r = (float)r;
		m_sD3DMaterial.emissive.g = (float)g;
		m_sD3DMaterial.emissive.b = (float)b;
		m_sD3DMaterial.emissive.a = (float)a;
		return S_OK;
	}
	int OnDiffuseColorMapChange( TextureMap* value ) {
		if( m_pDiffuseColorMap ) {
			CHECK_SUCCESS( m_pDiffuseColorMap->RemoveAppearance( this ) );
		}
		m_pDiffuseColorMap = value;
		if( m_pDiffuseColorMap ) {
			CHECK_SUCCESS( m_pDiffuseColorMap->AddAppearance( this ) );
		}
		CHECK_SUCCESS( CheckForVisualStateChange() );
		return S_OK;
	}
	int OnOpacityMapChange( TextureMap* value ) {
		TextureMap* prev = m_pOpacityMap;
		m_pOpacityMap = value;
		if( prev ) {
			if( !value ) {
				CHECK_SUCCESS( CheckForVisualStateChange() );
			}
		} else {
			if( value ) {
				CHECK_SUCCESS( CheckForVisualStateChange() );
			}
		}
		return S_OK;
	}
	int OnEmissiveColorMapChange( TextureMap* value ) {
		return S_OK;
	}
	int OnSpecularHighlightColorMapChange( TextureMap* value ) {
		return S_OK;
	}
	int OnBumpMapChange( TextureMap* value ) {
		return S_OK;
	}
	int OnDetailMapChange( TextureMap* value ) {
		return S_OK;
	}
	int Render( RenderTarget* pRenderTarget, void* pContext ) {
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FILLMODE, m_dwFillMode ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_LIGHTING, m_bLightingEnabled ) );
		if( m_bLightingEnabled ) {
			CHECK_SUCCESS( pD3DDevice->SetMaterial( &m_sD3DMaterial ) );
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_SHADEMODE, m_dwShadeMode ) );
		} else {
		}
		LPDIRECTDRAWSURFACE7 pDDSurface;
		if( m_pDiffuseColorMap ) {
			CHECK_SUCCESS( m_pDiffuseColorMap->GetSurface( pD3DDevice, pDDSurface ) );
		} else {
			pDDSurface = NULL;
		}
		if( m_pOpacityMap || ( m_pDiffuseColorMap && !m_pDiffuseColorMap->IsOpaque() ) ) {
			CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 0, D3DTSS_ALPHAOP, D3DTOP_MODULATE ) );
		} else {
			CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 0, D3DTSS_ALPHAOP, D3DTOP_SELECTARG2 ) );
		}
		CHECK_SUCCESS( pD3DDevice->SetTexture( 0, pDDSurface ) );
		return S_OK;
	}

	bool IsOpaque() {
		if( m_pDiffuseColorMap ) {
			if( !m_pDiffuseColorMap->IsOpaque() ) {
				return false;
			}
		}
		if( m_pOpacityMap ) {
			return false;
		}
		return m_sD3DMaterial.diffuse.a>=1.0f;
	}
	int CheckForVisualStateChange();
private:
	D3DMATERIAL7 m_sD3DMaterial;
	DWORD m_dwShadeMode;
	DWORD m_dwFillMode;
	DWORD m_bLightingEnabled;
	TextureMap* m_pDiffuseColorMap;
	TextureMap* m_pOpacityMap;
	std::vector<_Visual*> m_visuals;
};

#endif