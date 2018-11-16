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

#include "ProjectionCamera.hpp"
#include "UtilJNI.hpp"

extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ProjectionCameraProxy_onProjectionChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ProjectionCameraProxy_onProjectionChange
#endif
( JNIEnv* jEnv, jobject jSelf, jobject jProjection ) {
	if( jSelf ) {
		ProjectionCamera* pSelf = (ProjectionCamera*)JNI_GetNativeInstance( jEnv, jSelf );
		if( pSelf ) {
			double rc00 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m00" );
			double rc01 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m01" );
			double rc02 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m02" );
			double rc03 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m03" );
			double rc10 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m10" );
			double rc11 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m11" );
			double rc12 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m12" );
			double rc13 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m13" );
			double rc20 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m20" );
			double rc21 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m21" );
			double rc22 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m22" );
			double rc23 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m23" );
			double rc30 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m30" );
			double rc31 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m31" );
			double rc32 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m32" );
			double rc33 = JNI_GetDoubleFieldNamed( jEnv, jProjection, "m33" );
			int nResult = pSelf->OnProjectionChange( rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 );
			if( nResult<0 ) {
				JNI_ThrowNewException( jEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( jEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}

} //extern "C"