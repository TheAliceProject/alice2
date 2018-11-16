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

#include "RenderCanvas.hpp"
#include "UtilJNI.hpp"

extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderCanvas_createNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderCanvas_createNativeInstance
#endif
  ( JNIEnv* pjEnv, jobject jSelf, jobject jRenderTarget )
{
	if( jSelf ) {
		if( jRenderTarget ) {
			RenderTarget* pRenderTarget = (RenderTarget*)JNI_GetNativeInstance( pjEnv, jRenderTarget );
			RenderCanvas* pSelf = new RenderCanvas( pRenderTarget );
			JNI_SetNativeInstance( pjEnv, jSelf, pSelf );
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderCanvas_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderCanvas_releaseNativeInstance
#endif
  ( JNIEnv* pjEnv, jobject jSelf )
{
	RenderCanvas* pRenderCanvas = (RenderCanvas*)JNI_GetNativeInstance( pjEnv, jSelf );
	JNI_SetNativeInstance( pjEnv, jSelf, NULL );
	delete pRenderCanvas;
}

} //extern "C"