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

//DO NOT EDIT.  this code is generated

#include "ExponentialFog.hpp"
#include "UtilJNI.hpp"


extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ExponentialFogProxy_createNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ExponentialFogProxy_createNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	ExponentialFog* pElement = new ExponentialFog();
	if( pElement ) {
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", (jint)pElement );
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeTypeID", EXPONENTIAL_FOG_TYPE_ID );
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ExponentialFogProxy_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ExponentialFogProxy_releaseNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	ExponentialFog* pElement = (ExponentialFog*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pElement ) {
		int nResult = pElement->Release();
		delete pElement;
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", 0 );
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeTypeID", 0 );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}



JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ExponentialFogProxy_onNameChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ExponentialFogProxy_onNameChange
#endif
( JNIEnv* pEnv, jobject jSelf, jstring value ) {
	ExponentialFog* pSelf = (ExponentialFog*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		const char* vcText = pEnv->GetStringUTFChars( value, NULL );
		int nResult = pSelf->OnNameChange( vcText );
		pEnv->ReleaseStringUTFChars( value, vcText );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ExponentialFogProxy_addToScene
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ExponentialFogProxy_addToScene
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jScene ) 
{
	if( jSelf ) {
		ExponentialFog* pSelf = (ExponentialFog*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			Scene* pScene = (Scene*)JNI_GetNativeInstance( pEnv, jScene );
			int nResult = pSelf->AddToScene( pScene );
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ExponentialFogProxy_removeFromScene
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ExponentialFogProxy_removeFromScene
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jScene ) 
{
	if( jSelf ) {
		ExponentialFog* pSelf = (ExponentialFog*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			Scene* pScene = (Scene*)JNI_GetNativeInstance( pEnv, jScene );
			int nResult = pSelf->RemoveFromScene( pScene );
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ExponentialFogProxy_onAbsoluteTransformationChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ExponentialFogProxy_onAbsoluteTransformationChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jAbsoluteTransformation ) 
{
	if( jSelf ) {
		ExponentialFog* pSelf = (ExponentialFog*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			double rc00 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m00" );
			double rc01 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m01" );
			double rc02 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m02" );
			double rc03 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m03" );
			double rc10 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m10" );
			double rc11 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m11" );
			double rc12 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m12" );
			double rc13 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m13" );
			double rc20 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m20" );
			double rc21 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m21" );
			double rc22 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m22" );
			double rc23 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m23" );
			double rc30 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m30" );
			double rc31 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m31" );
			double rc32 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m32" );
			double rc33 = JNI_GetDoubleFieldNamed( pEnv, jAbsoluteTransformation, "m33" );
			int nResult = pSelf->OnAbsoluteTransformationChange( rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 );
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}



JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ExponentialFogProxy_onColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ExponentialFogProxy_onColorChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble r, jdouble g, jdouble b, jdouble a ) {
	ExponentialFog* pSelf = (ExponentialFog*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnColorChange( r, g, b, a );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_ExponentialFogProxy_onDensityChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_ExponentialFogProxy_onDensityChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	ExponentialFog* pSelf = (ExponentialFog*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnDensityChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


} //extern "C"