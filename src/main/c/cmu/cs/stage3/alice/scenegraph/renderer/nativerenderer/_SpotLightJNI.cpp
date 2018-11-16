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

#include "SpotLight.hpp"
#include "UtilJNI.hpp"


extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_createNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_createNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	SpotLight* pElement = new SpotLight();
	if( pElement ) {
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", (jint)pElement );
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeTypeID", SPOT_LIGHT_TYPE_ID );
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_releaseNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	SpotLight* pElement = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onNameChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onNameChange
#endif
( JNIEnv* pEnv, jobject jSelf, jstring value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_addToScene
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_addToScene
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jScene ) 
{
	if( jSelf ) {
		SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_removeFromScene
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_removeFromScene
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jScene ) 
{
	if( jSelf ) {
		SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onAbsoluteTransformationChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onAbsoluteTransformationChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jAbsoluteTransformation ) 
{
	if( jSelf ) {
		SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onColorChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble r, jdouble g, jdouble b, jdouble a ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onBrightnessChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onBrightnessChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnBrightnessChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onRangeChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onRangeChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnRangeChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onConstantAttenuationChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onConstantAttenuationChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnConstantAttenuationChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onLinearAttenuationChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onLinearAttenuationChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnLinearAttenuationChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onQuadraticAttenuationChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onQuadraticAttenuationChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnQuadraticAttenuationChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onInnerBeamAngleChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onInnerBeamAngleChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnInnerBeamAngleChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onOuterBeamAngleChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onOuterBeamAngleChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnOuterBeamAngleChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_SpotLightProxy_onFalloffChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_SpotLightProxy_onFalloffChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	SpotLight* pSelf = (SpotLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnFalloffChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


} //extern "C"
