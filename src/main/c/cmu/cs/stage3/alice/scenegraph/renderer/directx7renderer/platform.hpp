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

#ifndef PLATFORM_INCLUDED
#define PLATFORM_INCLUDED

#pragma once
#pragma warning( disable:4786 )

#define DX7_RENDERER

#include "error.hpp"

#define WIN32_LEAN_AND_MEAN
#include <windows.h>

#define IsNaN _isnan

#include "common.hpp"

#define D3D_OVERLOADS
#include <ddraw.h>
#include <d3d.h>
#include <d3dx.h>

typedef unsigned LightID;
typedef unsigned ClippingPlaneID;
typedef unsigned _VisualID;

#define SAFE_RELEASE(p)      { if(p) { (p)->Release(); (p)=NULL; } }

HINSTANCE GetInstance();

extern D3DMATRIX g_sD3DMIdentity;

inline DWORD FtoDW( FLOAT f ) { return *((DWORD*)&f); }
inline D3DCOLORVALUE MakeColor( float r, float g, float b ) {
	D3DCOLORVALUE out;
	out.r = r;
	out.g = g;
	out.b = b;
	return out;
}

// For comparing guids (duh)
struct CompareGuid{
	bool operator()( const struct _GUID a, const struct _GUID b ){
		int res = memcmp( &a, &b, sizeof( _GUID ) );
		if( res > 0 )
			return true;
		return false;
	}
};

inline int UpdateViewMatrix( D3DMATRIX& sD3DMView, const D3DVECTOR& vRight, const D3DVECTOR& vUp, const D3DVECTOR& vForward, const D3DVECTOR& vFrom ) {
	// Start building the matrix. The first three rows contains the basis
	// vectors used to rotate the sD3DMView to point at the lookat point
	sD3DMView._11 = vRight.x;    sD3DMView._12 = vUp.x;    sD3DMView._13 = vForward.x;
	sD3DMView._21 = vRight.y;    sD3DMView._22 = vUp.y;    sD3DMView._23 = vForward.y;
	sD3DMView._31 = vRight.z;    sD3DMView._32 = vUp.z;    sD3DMView._33 = vForward.z;

	// Do the translation values (rotations are still about the eyepoint)
	sD3DMView._41 = - DotProduct( vFrom, vRight );
	sD3DMView._42 = - DotProduct( vFrom, vUp );
	sD3DMView._43 = - DotProduct( vFrom, vForward );

	sD3DMView._14 = D3DVALUE( 0 );
	sD3DMView._24 = D3DVALUE( 0 );
	sD3DMView._34 = D3DVALUE( 0 );
	sD3DMView._44 = D3DVALUE( 1 );

	return S_OK;
}

inline int UpdateViewMatrix( D3DMATRIX& sD3DMView, const D3DMATRIX& sD3DMAbsolute ) {
	D3DVECTOR vRight( sD3DMAbsolute._11, sD3DMAbsolute._12, sD3DMAbsolute._13 );
	D3DVECTOR vUp( sD3DMAbsolute._21, sD3DMAbsolute._22, sD3DMAbsolute._23 );
	D3DVECTOR vForward( sD3DMAbsolute._31, sD3DMAbsolute._32, sD3DMAbsolute._33 );
	D3DVECTOR vFrom( sD3DMAbsolute._41, sD3DMAbsolute._42, sD3DMAbsolute._43 );
	return UpdateViewMatrix( sD3DMView, vRight, vUp, vForward, vFrom );
}

inline void D3DUtil_SetIdentityMatrix( D3DMATRIX& sD3DM ) {
	            sD3DM._21 = sD3DM._31 = sD3DM._41 =
	sD3DM._12             = sD3DM._32 = sD3DM._42 =
	sD3DM._13 = sD3DM._23             = sD3DM._43 =
	sD3DM._14 = sD3DM._24 = sD3DM._34             = D3DVALUE( 0 );

	sD3DM._11 = sD3DM._22 = sD3DM._33 = sD3DM._44 = D3DVALUE( 1 );
}
#endif