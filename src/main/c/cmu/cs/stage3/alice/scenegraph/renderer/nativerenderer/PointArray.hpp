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

#ifndef POINT_ARRAY_INCLUDED
#define POINT_ARRAY_INCLUDED

#include "VertexGeometry.hpp"
class PointArray : public VertexGeometry {
public:
	int Render( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		CHECK_SUCCESS( RenderPrimative( pRenderTarget, pContext, D3DPT_POINTLIST ) );
#elif defined OPENGL_RENDERER
		CHECK_SUCCESS( RenderPrimative( pRenderTarget, pContext, GL_POINTS ) );
#endif
		return S_OK;
	}
	int Pick( void* pContext, bool isSubElementRequired, bool bIsFrontFacingAppearance ) {
		//todo
		return S_OK;
	}
};

#endif