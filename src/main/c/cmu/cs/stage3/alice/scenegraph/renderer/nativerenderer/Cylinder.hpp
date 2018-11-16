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

#ifndef CYLINDER_INCLUDED
#define CYLINDER_INCLUDED

#include "Shape.hpp"

#define SLICE_COUNT 16
#define STACK_COUNT 16

class Cylinder : public Shape {
public:
	Cylinder() : Shape() {
		OnBaseRadiusChange( 1 );
		OnTopRadiusChange( 1 );
		OnHeightChange( 1 );
	}
	int OnBaseRadiusChange( double value ) {
		m_fBaseRadius = value;
		CHECK_SUCCESS( MarkDirty() );
		return 0;
	}
	int OnTopRadiusChange( double value ) {
		m_fTopRadius = value;
		CHECK_SUCCESS( MarkDirty() );
		return 0;
	}
	int OnHeightChange( double value ) {
		m_fHeight = value;
		CHECK_SUCCESS( MarkDirty() );
		return 0;
	}
#if defined DX7_RENDERER
protected:
	int GenerateSimpleShape( LPDIRECT3DDEVICE7 pD3DDevice, LPD3DXSIMPLESHAPE& pD3DXSimpleShape ) {
		CHECK_SUCCESS( D3DXCreateCylinder( pD3DDevice, m_fBaseRadius, m_fTopRadius, m_fHeight, SLICE_COUNT, STACK_COUNT, D3DX_DEFAULT, &pD3DXSimpleShape ) );
		return S_OK;
	}
#elif defined OPENGL_RENDERER
	int Render( RenderTarget* pRenderTarget, void* pContext ) {
		CHECK_GL( gluCylinder( GetGLUQuadric(), m_fBaseRadius, m_fTopRadius, m_fHeight, SLICE_COUNT, STACK_COUNT ) );
		return S_OK;
	}
#endif
private:
	double m_fBaseRadius;
	double m_fTopRadius;
	double m_fHeight;
};

#endif