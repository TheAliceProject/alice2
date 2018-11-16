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

#ifndef SHAPE_INCLUDED
#define SHAPE_INCLUDED

#include <map>
#include "Geometry.hpp"

class Shape : public Geometry {
public:
#if defined DX7_RENDERER
	int Render( RenderTarget* pRenderTarget, void* pContext ) {
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		LPD3DXSIMPLESHAPE pD3DXSimpleShape;
		pD3DXSimpleShape = m_map[ pD3DDevice ];
		if( pD3DXSimpleShape == NULL ) {
			CHECK_SUCCESS( GenerateSimpleShape( pD3DDevice, pD3DXSimpleShape ) );
			if( pD3DXSimpleShape ) {
				m_map[ pD3DDevice ] = pD3DXSimpleShape;
			}
		}
		if( pD3DXSimpleShape ) {
			pD3DXSimpleShape->Draw();
		}
		return S_OK;
	}
	int Pick( void* pContext, bool isSubElementRequired, bool bIsFrontFacingAppearance ) {
		//todo
		return S_OK;
	}
	int ReverseLighting() {
		//todo
		return S_OK;
	}

protected:
	int MarkDirty() {
		m_map.clear();
		return S_OK;
	}
	virtual int GenerateSimpleShape( LPDIRECT3DDEVICE7 pD3DDevice, LPD3DXSIMPLESHAPE& pD3DXSimpleShape ) = 0;
private:
	std::map< LPDIRECT3DDEVICE7, LPD3DXSIMPLESHAPE > m_map;
#elif defined OPENGL_RENDERER
protected:
	int Pick( void* pContext, bool isSubElementRequired, bool bIsFrontFacingAppearance ) {
		//todo
		return S_OK;
	}
	int MarkDirty() {
		return S_OK;
	}

	GLUquadricObj* GetGLUQuadric() {
		if( g_pGLUQuadric == NULL ) {
			g_pGLUQuadric = gluNewQuadric();
		}
		return g_pGLUQuadric;
	}
#endif

};

#endif