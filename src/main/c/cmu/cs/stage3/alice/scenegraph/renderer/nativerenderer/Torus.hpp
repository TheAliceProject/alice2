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

#ifndef TORUS_INCLUDED
#define TORUS_INCLUDED

#include "Shape.hpp"

#define SLICE_COUNT 16
#define STACK_COUNT 16

class Torus : public Shape {
public:
	Torus() : Shape() {
		OnInnerRadiusChange( 1 );
		OnOuterRadiusChange( 2 );
	}
	int OnInnerRadiusChange( double value ) {
		m_fInnerRadius = value;
		CHECK_SUCCESS( MarkDirty() );
		return 0;
	}
	int OnOuterRadiusChange( double value ) {
		m_fOuterRadius = value;
		CHECK_SUCCESS( MarkDirty() );
		return 0;
	}
#if defined DX7_RENDERER
protected:
	int GenerateSimpleShape( LPDIRECT3DDEVICE7 pD3DDevice, LPD3DXSIMPLESHAPE& pD3DXSimpleShape ) {
		CHECK_SUCCESS( D3DXCreateTorus( pD3DDevice, m_fInnerRadius, m_fOuterRadius, SLICE_COUNT, STACK_COUNT, D3DX_DEFAULT, &pD3DXSimpleShape ) );
		return S_OK;
	}
#elif defined OPENGL_RENDERER
	int Render( RenderTarget* pRenderTarget, void* pContext ) {
		int i, j;
		GLfloat theta, phi, theta1;
		GLfloat cosTheta, sinTheta;
		GLfloat cosTheta1, sinTheta1;
		GLfloat ringDelta, sideDelta;

		ringDelta = (float)( 2.0 * M_PI / SLICE_COUNT );
		sideDelta = (float)( 2.0 * M_PI / STACK_COUNT );

		theta = 0.0;
		cosTheta = 1.0;
		sinTheta = 0.0;
		for (i = SLICE_COUNT - 1; i >= 0; i--) {
			theta1 = theta + ringDelta;
			cosTheta1 = cos(theta1);
			sinTheta1 = sin(theta1);
			glBegin(GL_QUAD_STRIP);
			phi = 0.0;
			for (j = STACK_COUNT; j >= 0; j--) {
				GLfloat cosPhi, sinPhi, dist;

				phi += sideDelta;
				cosPhi = cos(phi);
				sinPhi = sin(phi);
				dist = m_fOuterRadius + m_fInnerRadius * cosPhi;

				glNormal3f(cosTheta1 * cosPhi, -sinTheta1 * cosPhi, sinPhi );
				glVertex3f(cosTheta1 * dist, -sinTheta1 * dist, m_fInnerRadius * sinPhi );
				glNormal3f(cosTheta * cosPhi, -sinTheta * cosPhi, sinPhi );
				glVertex3f(cosTheta * dist, -sinTheta * dist,  m_fInnerRadius * sinPhi );
			}
			CHECK_GL( glEnd() );
			theta = theta1;
			cosTheta = cosTheta1;
			sinTheta = sinTheta1;
		}
		return S_OK;
	}
#endif
private:
	double m_fInnerRadius;
	double m_fOuterRadius;
};

#endif