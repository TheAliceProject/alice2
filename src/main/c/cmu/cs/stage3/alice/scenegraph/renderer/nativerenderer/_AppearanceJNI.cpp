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

#include "Appearance.hpp"
#include "UtilJNI.hpp"


extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_createNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_createNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	Appearance* pElement = new Appearance();
	if( pElement ) {
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", (jint)pElement );
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeTypeID", APPEARANCE_TYPE_ID );
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_releaseNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	Appearance* pElement = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onNameChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onNameChange
#endif
( JNIEnv* pEnv, jobject jSelf, jstring value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onAmbientColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onAmbientColorChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble r, jdouble g, jdouble b, jdouble a ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnAmbientColorChange( r, g, b, a );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onDiffuseColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onDiffuseColorChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble r, jdouble g, jdouble b, jdouble a ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnDiffuseColorChange( r, g, b, a );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onFillingStyleChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onFillingStyleChange
#endif
( JNIEnv* pEnv, jobject jSelf, jint value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnFillingStyleChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onShadingStyleChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onShadingStyleChange
#endif
( JNIEnv* pEnv, jobject jSelf, jint value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnShadingStyleChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onOpacityChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onOpacityChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnOpacityChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onSpecularHighlightColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onSpecularHighlightColorChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble r, jdouble g, jdouble b, jdouble a ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnSpecularHighlightColorChange( r, g, b, a );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onSpecularHighlightExponentChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onSpecularHighlightExponentChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnSpecularHighlightExponentChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onEmissiveColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onEmissiveColorChange
#endif
( JNIEnv* pEnv, jobject jSelf, jdouble r, jdouble g, jdouble b, jdouble a ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnEmissiveColorChange( r, g, b, a );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onDiffuseColorMapChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onDiffuseColorMapChange
#endif
( JNIEnv* pEnv, jobject jSelf, jobject value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		TextureMap* pValue = (TextureMap*)JNI_GetNativeInstance( pEnv, value );
		int nResult = pSelf->OnDiffuseColorMapChange( pValue );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onOpacityMapChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onOpacityMapChange
#endif
( JNIEnv* pEnv, jobject jSelf, jobject value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		TextureMap* pValue = (TextureMap*)JNI_GetNativeInstance( pEnv, value );
		int nResult = pSelf->OnOpacityMapChange( pValue );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onEmissiveColorMapChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onEmissiveColorMapChange
#endif
( JNIEnv* pEnv, jobject jSelf, jobject value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		TextureMap* pValue = (TextureMap*)JNI_GetNativeInstance( pEnv, value );
		int nResult = pSelf->OnEmissiveColorMapChange( pValue );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onSpecularHighlightColorMapChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onSpecularHighlightColorMapChange
#endif
( JNIEnv* pEnv, jobject jSelf, jobject value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		TextureMap* pValue = (TextureMap*)JNI_GetNativeInstance( pEnv, value );
		int nResult = pSelf->OnSpecularHighlightColorMapChange( pValue );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onBumpMapChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onBumpMapChange
#endif
( JNIEnv* pEnv, jobject jSelf, jobject value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		TextureMap* pValue = (TextureMap*)JNI_GetNativeInstance( pEnv, value );
		int nResult = pSelf->OnBumpMapChange( pValue );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_AppearanceProxy_onDetailMapChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_AppearanceProxy_onDetailMapChange
#endif
( JNIEnv* pEnv, jobject jSelf, jobject value ) {
	Appearance* pSelf = (Appearance*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		TextureMap* pValue = (TextureMap*)JNI_GetNativeInstance( pEnv, value );
		int nResult = pSelf->OnDetailMapChange( pValue );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


} //extern "C"
