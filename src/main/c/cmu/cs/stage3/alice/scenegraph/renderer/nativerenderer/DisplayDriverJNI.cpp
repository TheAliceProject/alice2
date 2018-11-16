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

#include "DisplayDriver.hpp"
#include "UtilJNI.hpp"

extern "C" {

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDriver
 * Method:    getDisplayDeviceCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDriver_getDisplayDeviceCount
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDriver_getDisplayDeviceCount
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	DisplayDriver* pDisplayDriver = (DisplayDriver*)JNI_GetNativeInstance( pEnv, jSelf );
	jint count;
	int nResult = pDisplayDriver->GetDisplayDeviceCount( count );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return count;
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDriver
 * Method:    getDisplayDeviceNativeInstanceAt
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDriver_getDisplayDeviceNativeInstanceAt
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDriver_getDisplayDeviceNativeInstanceAt
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint index )
{
	DisplayDriver* pDisplayDriver = (DisplayDriver*)JNI_GetNativeInstance( pEnv, jSelf );
	DisplayDevice* pDisplayDevice;
	int nResult = pDisplayDriver->GetDisplayDeviceAt( index, pDisplayDevice );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return (jint)pDisplayDevice;
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDriver
 * Method:    getFullscreenDisplayModeCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDriver_getFullscreenDisplayModeCount
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDriver_getFullscreenDisplayModeCount
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	DisplayDriver* pDisplayDriver = (DisplayDriver*)JNI_GetNativeInstance( pEnv, jSelf );
	jint count;
	int nResult = pDisplayDriver->GetFullscreenDisplayModeCount( count );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return count;
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDriver
 * Method:    getFullscreenDisplayModeNativeInstanceAt
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDriver_getFullscreenDisplayModeNativeInstanceAt
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDriver_getFullscreenDisplayModeNativeInstanceAt
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint index )
{
	DisplayDriver* pDisplayDriver = (DisplayDriver*)JNI_GetNativeInstance( pEnv, jSelf );
	FullscreenDisplayMode* pFullscreenDisplayMode;
	int nResult = pDisplayDriver->GetFullscreenDisplayModeAt( index, pFullscreenDisplayMode );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return (jint)pFullscreenDisplayMode;
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDriver
 * Method:    getDescription
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDriver_getDescription
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDriver_getDescription
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	DisplayDriver* pDisplayDriver = (DisplayDriver*)JNI_GetNativeInstance( pEnv, jSelf );
	char vcDescription[256];
	int nResult = pDisplayDriver->GetDescription( vcDescription, 256 );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return pEnv->NewStringUTF( vcDescription );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDriver
 * Method:    getName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDriver_getName
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDriver_getName
#endif
	( JNIEnv* pEnv, jobject jSelf )
{
	DisplayDriver* pDisplayDriver = (DisplayDriver*)JNI_GetNativeInstance( pEnv, jSelf );
	char vcName[256];
	int nResult = pDisplayDriver->GetName( vcName, 256 );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return pEnv->NewStringUTF( vcName );
}

} //extern "C"