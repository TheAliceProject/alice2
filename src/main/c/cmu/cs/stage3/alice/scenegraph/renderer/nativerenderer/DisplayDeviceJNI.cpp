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

#include "DisplayDevice.hpp"
#include "UtilJNI.hpp"

extern "C" {

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDevice
 * Method:    getDescription
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDevice_getDescription
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDevice_getDescription
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	DisplayDevice* pDisplayDevice = (DisplayDevice*)JNI_GetNativeInstance( pEnv, jSelf );
	char vcDescription[256];
	int nResult = pDisplayDevice->GetDescription( vcDescription, 256 );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return pEnv->NewStringUTF( vcDescription );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDevice
 * Method:    getName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDevice_getName
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDevice_getName
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	DisplayDevice* pDisplayDevice = (DisplayDevice*)JNI_GetNativeInstance( pEnv, jSelf );
	char vcName[256];
	int nResult = pDisplayDevice->GetName( vcName, 256 );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return pEnv->NewStringUTF( vcName );
}


/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_DisplayDevice
 * Method:    isHardwareAccelerated
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_DisplayDevice_isHardwareAccelerated
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_DisplayDevice_isHardwareAccelerated
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	DisplayDevice* pDisplayDevice = (DisplayDevice*)JNI_GetNativeInstance( pEnv, jSelf );
	//todo?
	bool bIsHardwareAccelerated = pDisplayDevice->IsHardwareAccelerated();
	if( bIsHardwareAccelerated ) {
		return JNI_TRUE;
	} else {
		return JNI_FALSE;
	}
}

} //extern "C"