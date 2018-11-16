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

#include "FullscreenDisplayMode.hpp"
#include "UtilJNI.hpp"

extern "C" {

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_FullscreenDisplayMode
 * Method:    getWidth
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_FullscreenDisplayMode_getWidth
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_FullscreenDisplayMode_getWidth
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	FullscreenDisplayMode* pFullscreenDisplayMode = (FullscreenDisplayMode*)JNI_GetNativeInstance( pEnv, jSelf );
	jint width;
	int nResult = pFullscreenDisplayMode->GetWidth( width );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return width;
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_FullscreenDisplayMode
 * Method:    getHeight
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_FullscreenDisplayMode_getHeight
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_FullscreenDisplayMode_getHeight
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	FullscreenDisplayMode* pFullscreenDisplayMode = (FullscreenDisplayMode*)JNI_GetNativeInstance( pEnv, jSelf );
	jint height;
	int nResult = pFullscreenDisplayMode->GetHeight( height );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return height;
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_FullscreenDisplayMode
 * Method:    getDepth
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_FullscreenDisplayMode_getDepth
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_FullscreenDisplayMode_getDepth
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	FullscreenDisplayMode* pFullscreenDisplayMode = (FullscreenDisplayMode*)JNI_GetNativeInstance( pEnv, jSelf );
	jint depth;
	int nResult = pFullscreenDisplayMode->GetDepth( depth );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return depth;
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_FullscreenDisplayMode
 * Method:    getRefreshRate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_FullscreenDisplayMode_getRefreshRate
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_FullscreenDisplayMode_getRefreshRate
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	FullscreenDisplayMode* pFullscreenDisplayMode = (FullscreenDisplayMode*)JNI_GetNativeInstance( pEnv, jSelf );
	jint refreshRate;
	int nResult = pFullscreenDisplayMode->GetRefreshRate( refreshRate );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return refreshRate;
}

} //extern "C"