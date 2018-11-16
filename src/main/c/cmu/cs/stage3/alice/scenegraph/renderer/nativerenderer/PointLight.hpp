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

#ifndef POINT_LIGHT_INCLUDED
#define POINT_LIGHT_INCLUDED

#include "Light.hpp"
class PointLight : public Light {
public:
	PointLight() : Light() {
		OnConstantAttenuationChange( 1 );
		OnLinearAttenuationChange( 0 );
		OnQuadraticAttenuationChange( 0 );
	}
	int OnConstantAttenuationChange( double value ) {
		m_fConstantAttenuation = (float)value;
		return S_OK;
	}
	int OnLinearAttenuationChange( double value ) {
		m_fLinearAttenuation = (float)value;
		return S_OK;
	}
	int OnQuadraticAttenuationChange( double value ) {
		m_fQuadraticAttenuation = (float)value;
		return S_OK;
	}

#if defined DX7_RENDERER
	virtual int Setup( D3DLIGHT7& sD3DLight ) {
		Light::Setup( sD3DLight );
	    sD3DLight.dltType = D3DLIGHT_POINT;
		sD3DLight.dvAttenuation0 = m_fConstantAttenuation; 
		sD3DLight.dvAttenuation1 = m_fLinearAttenuation; 
		sD3DLight.dvAttenuation2 = m_fQuadraticAttenuation; 
		sD3DLight.dvPosition.x = m_sD3DMAbsolute._41;
		sD3DLight.dvPosition.y = m_sD3DMAbsolute._42;
		sD3DLight.dvPosition.z = m_sD3DMAbsolute._43;	
		return S_OK;
	}
#elif defined OPENGL_RENDERER
	virtual int Setup( RenderTarget* pRenderTarget, void* pContext ) {
		CHECK_SUCCESS( Light::Setup( pRenderTarget, pContext ) );
		CHECK_GL( glLightfv( m_nID, GL_DIFFUSE, m_vfColor ) );
		//glLightfv( m_nID, GL_SPECULAR, m_vfColor );
		CHECK_GL( glLightf( m_nID, GL_CONSTANT_ATTENUATION, m_fConstantAttenuation ) );
		CHECK_GL( glLightf( m_nID, GL_LINEAR_ATTENUATION, m_fLinearAttenuation ) );
		CHECK_GL( glLightf( m_nID, GL_QUADRATIC_ATTENUATION, m_fQuadraticAttenuation ) );
		GLfloat vfPosition[] = { m_vfAbsolute[12], m_vfAbsolute[13], m_vfAbsolute[14], 1 };
		CHECK_GL( glLightfv( m_nID, GL_POSITION, vfPosition ) );
		return S_OK;
	}
#endif

private:
	float m_fConstantAttenuation;
	float m_fLinearAttenuation;
	float m_fQuadraticAttenuation;
};

#endif