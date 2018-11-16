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

#ifndef COMPONENT_INCLUDED
#define COMPONENT_INCLUDED

#include "Element.hpp"
class Container;
class Scene;

class _Component : public Element {
public:
	_Component() : Element() {
		OnAbsoluteTransformationChange( 1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1 );
		m_pScene = NULL;
	}
	virtual int OnAbsoluteTransformationChange( double rc00, double rc01, double rc02, double rc03, double rc10, double rc11, double rc12, double rc13, double rc20, double rc21, double rc22, double rc23, double rc30, double rc31, double rc32, double rc33 ) {
#if defined DX7_RENDERER
		m_sD3DMAbsolute._11 = (float)rc00;
		m_sD3DMAbsolute._12 = (float)rc01;
		m_sD3DMAbsolute._13 = (float)rc02;
		m_sD3DMAbsolute._14 = (float)rc03;

		m_sD3DMAbsolute._21 = (float)rc10;
		m_sD3DMAbsolute._22 = (float)rc11;
		m_sD3DMAbsolute._23 = (float)rc12;
		m_sD3DMAbsolute._24 = (float)rc13;

		m_sD3DMAbsolute._31 = (float)rc20;
		m_sD3DMAbsolute._32 = (float)rc21;
		m_sD3DMAbsolute._33 = (float)rc22;
		m_sD3DMAbsolute._34 = (float)rc23;

		m_sD3DMAbsolute._41 = (float)rc30;
		m_sD3DMAbsolute._42 = (float)rc31;
		m_sD3DMAbsolute._43 = (float)rc32;
		m_sD3DMAbsolute._44 = (float)rc33;
#elif defined OPENGL_RENDERER
		m_vfAbsolute[0] = rc00;
		m_vfAbsolute[1] = rc01;
		m_vfAbsolute[2] = -rc02;
		m_vfAbsolute[3] = rc03;

		m_vfAbsolute[4] = rc10;
		m_vfAbsolute[5] = rc11;
		m_vfAbsolute[6] = -rc12;
		m_vfAbsolute[7] = rc13;

		m_vfAbsolute[8] = -rc20;
		m_vfAbsolute[9] = -rc21;
		m_vfAbsolute[10] = rc22;
		m_vfAbsolute[11] = -rc23;

		m_vfAbsolute[12] = rc30;
		m_vfAbsolute[13] = rc31;
		m_vfAbsolute[14] = -rc32;
		m_vfAbsolute[15] = rc33;
#endif
		return S_OK;
	}
	virtual int AddToScene( Scene* pScene ) {
		m_pScene = pScene;
		return S_OK;
	}
	virtual int RemoveFromScene( Scene* pScene ) {
		m_pScene = NULL;
		return S_OK;
	}
	int GetScene( Scene*& pScene ) {
		pScene = m_pScene;
		return S_OK;
	}
#if defined DX7_RENDERER
	int GetAbsoluteTransformation( D3DMATRIX& sD3DMAbsolute ) {
		memcpy( &sD3DMAbsolute, &m_sD3DMAbsolute, sizeof( D3DMATRIX ) );
		return S_OK;
	}
#endif
protected:
#if defined DX7_RENDERER
	D3DMATRIX m_sD3DMAbsolute;
#elif defined OPENGL_RENDERER
	double m_vfAbsolute[16];
#endif
	Scene* m_pScene;
};

#endif