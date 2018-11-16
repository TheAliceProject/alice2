//DO NOT EDIT.  this code is generated

#include "AmbientLight.hpp"
#include "UtilJNI.hpp"


extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_createNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_createNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	AmbientLight* pElement = new AmbientLight();
	if( pElement ) {
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", (jint)pElement );
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeTypeID", AMBIENT_LIGHT_TYPE_ID );
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_releaseNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	AmbientLight* pElement = (AmbientLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_onNameChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_onNameChange
#endif
( JNIEnv* pEnv, jobject jSelf, jstring value ) {
	AmbientLight* pSelf = (AmbientLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_addToScene
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_addToScene
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jScene ) 
{
	if( jSelf ) {
		AmbientLight* pSelf = (AmbientLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_removeFromScene
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_removeFromScene
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jScene ) 
{
	if( jSelf ) {
		AmbientLight* pSelf = (AmbientLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_onAbsoluteTransformationChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_onAbsoluteTransformationChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jAbsoluteTransformation ) 
{
	if( jSelf ) {
		AmbientLight* pSelf = (AmbientLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_onColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_onColorChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble r, jdouble g, jdouble b, jdouble a ) {
	AmbientLight* pSelf = (AmbientLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_onBrightnessChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_onBrightnessChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	AmbientLight* pSelf = (AmbientLight*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AmbientLightProxy_onRangeChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AmbientLightProxy_onRangeChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	AmbientLight* pSelf = (AmbientLight*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnRangeChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


} //extern "C"
