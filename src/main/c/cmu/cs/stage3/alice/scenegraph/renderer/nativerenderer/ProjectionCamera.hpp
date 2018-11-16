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

#ifndef PROJECTION_CAMERA_INCLUDED
#define PROJECTION_CAMERA_INCLUDED

#include "Camera.hpp"
class ProjectionCamera : public Camera {
public:
	int OnProjectionChange( double rc00, double rc01, double rc02, double rc03, double rc10, double rc11, double rc12, double rc13, double rc20, double rc21, double rc22, double rc23, double rc30, double rc31, double rc32, double rc33 ) {
#if defined DX7_RENDERER
		m_sD3DMProjection._11 = (float)rc00;
		m_sD3DMProjection._12 = (float)rc01;
		m_sD3DMProjection._13 = (float)rc02;
		m_sD3DMProjection._14 = (float)rc03;

		m_sD3DMProjection._21 = (float)rc10;
		m_sD3DMProjection._22 = (float)rc11;
		m_sD3DMProjection._23 = (float)rc12;
		m_sD3DMProjection._24 = (float)rc13;

		m_sD3DMProjection._31 = (float)rc20;
		m_sD3DMProjection._32 = (float)rc21;
		m_sD3DMProjection._33 = (float)rc22;
		m_sD3DMProjection._34 = (float)rc23;

		m_sD3DMProjection._41 = (float)rc30;
		m_sD3DMProjection._42 = (float)rc31;
		m_sD3DMProjection._43 = (float)rc32;
		m_sD3DMProjection._44 = (float)rc33;

#elif defined OPENGL_RENDERER

		m_vfProjection[0] = rc00;
		m_vfProjection[1] = rc01;
		m_vfProjection[2] = -rc02;
		m_vfProjection[3] = rc03;

		m_vfProjection[4] = rc10;
		m_vfProjection[5] = rc11;
		m_vfProjection[6] = -rc12;
		m_vfProjection[7] = rc13;

		m_vfProjection[8] = -rc20;
		m_vfProjection[9] = -rc21;
		m_vfProjection[10] = rc22;
		m_vfProjection[11] = -rc23;

		m_vfProjection[12] = rc30;
		m_vfProjection[13] = rc31;
		m_vfProjection[14] = -rc32;
		m_vfProjection[15] = rc33;
#endif
		return S_OK;
	}
	int LetterboxViewportIfNecessary( Viewport& iViewport ) {
		//todo
		return S_OK;
	}

protected:
	int UpdateProjection( long nWidth, long nHeight ) {
		return S_OK;
	}
#if defined DX7_RENDERER
	int GetPickProjection( long nX, long nY, const Viewport& iViewport, D3DMATRIX& sPickProjection ) {
		//todo
		return S_OK;
	}
#endif
};

#endif