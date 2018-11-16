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

#ifndef SPOT_LIGHT_INCLUDED
#define SPOT_LIGHT_INCLUDED

#include "PointLight.hpp"

class SpotLight : public PointLight {
public:
	int OnInnerBeamAngleChange( double value ) {
		m_fInnerBeamAngle = (float)value;
		return S_OK;
	}
	int OnOuterBeamAngleChange( double value ) {
		m_fOuterBeamAngle = (float)value;
		return S_OK;
	}
	int OnFalloffChange( double value ) {
		m_fFalloff = (float)value;
		return S_OK;
	}
#if defined DX7_RENDERER
	int Setup( D3DLIGHT7& sD3DLight ) {
		CHECK_SUCCESS( PointLight::Setup( sD3DLight ) );
	    sD3DLight.dltType = D3DLIGHT_SPOT;
		sD3DLight.dvTheta = m_fInnerBeamAngle;
		sD3DLight.dvPhi = m_fOuterBeamAngle;
		sD3DLight.dvFalloff = m_fFalloff;
		sD3DLight.dvDirection.x = m_sD3DMAbsolute._31;
		sD3DLight.dvDirection.y = m_sD3DMAbsolute._32;
		sD3DLight.dvDirection.z = m_sD3DMAbsolute._33;	
		return S_OK;
	}
#elif defined OPENGL_RENDERER
	int Setup( RenderTarget* pRenderTarget, void* pContext ) {
		CHECK_SUCCESS( PointLight::Setup( pRenderTarget, pContext ) );
		GLfloat vfSpotDirection[3] = { m_vfAbsolute[8], m_vfAbsolute[9], m_vfAbsolute[10] };
		CHECK_GL( glLightfv( m_nID, GL_SPOT_DIRECTION, vfSpotDirection ) );
		CHECK_GL( glLightf( m_nID, GL_SPOT_CUTOFF, (GLfloat)((180*m_fOuterBeamAngle)/M_PI) ) );
		//glLightf( m_nID, GL_SPOT_EXPONENT,  );
		return S_OK;
	}
#endif

private:
	float m_fInnerBeamAngle;
	float m_fOuterBeamAngle;
	float m_fFalloff;
};

#endif