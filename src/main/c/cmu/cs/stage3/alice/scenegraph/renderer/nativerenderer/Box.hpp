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

#ifndef BOX_INCLUDED
#define BOX_INCLUDED

#include "Shape.hpp"

class Box : public Shape {
public:
	Box() : Shape() {
		OnWidthChange( 1 );
		OnHeightChange( 1 );
		OnDepthChange( 1 );
	}
	int OnWidthChange( double value ) {
		m_fWidth = value;
		CHECK_SUCCESS( MarkDirty() );
		return 0;
	}
	int OnHeightChange( double value ) {
		m_fHeight = value;
		CHECK_SUCCESS( MarkDirty() );
		return 0;
	}
	int OnDepthChange( double value ) {
		m_fDepth = value;
		CHECK_SUCCESS( MarkDirty() );
		return 0;
	}
#if defined DX7_RENDERER
protected:
	int GenerateSimpleShape( LPDIRECT3DDEVICE7 pD3DDevice, LPD3DXSIMPLESHAPE& pD3DXSimpleShape ) {
		CHECK_SUCCESS( D3DXCreateBox( pD3DDevice, m_fWidth, m_fHeight, m_fDepth, D3DX_DEFAULT, &pD3DXSimpleShape ) );
		return S_OK;
	}
#elif defined OPENGL_RENDERER
	int Render( RenderTarget* pRenderTarget, void* pContext ) {
		//todo
		return S_OK;
	}
#endif
private:
	double m_fWidth;
	double m_fHeight;
	double m_fDepth;
};

#endif