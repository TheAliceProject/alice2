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

#ifndef LIGHT_INCLUDED
#define LIGHT_INCLUDED

#include "Affector.hpp"

class Light : public Affector {
public:
	Light() : Affector() {
		OnColorChange( 0,0,0,1 );
	}
	virtual int OnColorChange( double r, double g, double b, double a ) {
		m_vfColor[ 0 ] = (float)r;
		m_vfColor[ 1 ] = (float)g;
		m_vfColor[ 2 ] = (float)b;
		m_vfColor[ 3 ] = (float)a;
		return S_OK;
	}
	int OnBrightnessChange( double value ) {
		m_fBrightness = (float)value;
		return S_OK;
	}
	int OnRangeChange( double value ) {
		m_fRange = (float)value;
		return S_OK;
	}
	virtual int Enable( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->LightEnable( m_nID, TRUE ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glEnable( m_nID ) );
#endif
		return S_OK;
	}
	virtual int Disable( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( pD3DDevice->LightEnable( m_nID, FALSE ) );
#elif defined OPENGL_RENDERER
		CHECK_GL( glDisable( m_nID ) );
#endif
		return S_OK;
	}
#if defined DX7_RENDERER
	virtual int Setup( D3DLIGHT7& sD3DLight ) {
	    ZeroMemory( &sD3DLight, sizeof(D3DLIGHT7) );
		sD3DLight.dcvAmbient.r = 0.0f;
		sD3DLight.dcvAmbient.g = 0.0f;
		sD3DLight.dcvAmbient.b = 0.0f;
		sD3DLight.dcvAmbient.a = 1.0f;
		sD3DLight.dcvDiffuse.r = m_vfColor[ 0 ]*m_fBrightness;
		sD3DLight.dcvDiffuse.g = m_vfColor[ 1 ]*m_fBrightness;
		sD3DLight.dcvDiffuse.b = m_vfColor[ 2 ]*m_fBrightness;
		sD3DLight.dcvDiffuse.a = m_vfColor[ 3 ]*m_fBrightness;
		sD3DLight.dcvSpecular.r = 1.0f;
		sD3DLight.dcvSpecular.g = 1.0f;
		sD3DLight.dcvSpecular.b = 1.0f;
		sD3DLight.dcvSpecular.a = 1.0f;
		//sD3DLight.dvRange = m_fRange;
		sD3DLight.dvRange = D3DLIGHT_RANGE_MAX;
		return S_OK;
	}
#endif
	virtual int Setup( RenderTarget* pRenderTarget, void* pContext ) {
		CHECK_SUCCESS( pRenderTarget->GetNextLightID( m_nID ) );
		Enable( pRenderTarget, pContext );
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		D3DLIGHT7 sD3DLight;
		CHECK_SUCCESS( Setup( sD3DLight ) );
		CHECK_SUCCESS( pD3DDevice->SetLight( m_nID, &sD3DLight ) );
#endif
		return S_OK;
	}
protected:
	LightID m_nID;
	float m_vfColor[4];
	float m_fBrightness;
	float m_fRange;
};

#endif