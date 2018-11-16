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

#ifndef DIRECTIONAL_LIGHT_INCLUDED
#define DIRECTIONAL_LIGHT_INCLUDED

#include "Light.hpp"
class DirectionalLight : public Light {
public:
#if defined DX7_RENDERER
	int Setup( D3DLIGHT7& sD3DLight ) {
		Light::Setup( sD3DLight );
	    sD3DLight.dltType = D3DLIGHT_DIRECTIONAL;
		sD3DLight.dvDirection.x = m_sD3DMAbsolute._31;
		sD3DLight.dvDirection.y = m_sD3DMAbsolute._32;
		sD3DLight.dvDirection.z = m_sD3DMAbsolute._33;	
		return S_OK;
	}
#elif defined OPENGL_RENDERER
	int Setup( RenderTarget* pRenderTarget, void* pContext ) {
		CHECK_SUCCESS( Light::Setup( pRenderTarget, pContext ) );
		CHECK_GL( glLightfv( m_nID, GL_DIFFUSE, m_vfColor ) );
		GLfloat vfDirection[4] = { m_vfAbsolute[8], m_vfAbsolute[9], m_vfAbsolute[10], 0 };
		CHECK_GL( glLightfv( m_nID, GL_POSITION, vfDirection ) );
		return S_OK;
	}
#endif
};

#endif