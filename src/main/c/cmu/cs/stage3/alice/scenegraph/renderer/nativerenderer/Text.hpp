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

#ifndef TEXT_INCLUDED
#define TEXT_INCLUDED

#include "Geometry.hpp"
class Text : public Geometry {
public:
	int OnTextChange( const char* value ) {
		//todo
		return S_OK;
	}
	int OnFontChange( const char* name, int style, int size ) {
		//todo
		return S_OK;
	}
	int OnExtrusionChange( double x, double y, double z ) {
		//todo
		return S_OK;
	}
	int Render( RenderTarget* pRenderTarget, void* pContext ) {
		//todo
		return S_OK;
	}
	int Pick( void* pContext, bool isSubElementRequired, bool bIsFrontFacingAppearance ) {
		//todo
		return S_OK;
	}
#if defined DX7_RENDERER
	int ReverseLighting() {
		//todo
		return S_OK;
	}
#elif defined OPENGL_RENDERER
#endif
};

#endif