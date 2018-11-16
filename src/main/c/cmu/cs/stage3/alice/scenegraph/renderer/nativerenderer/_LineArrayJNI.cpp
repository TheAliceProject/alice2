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

#include "LineArray.hpp"
#include "UtilJNI.hpp"


extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_createNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_createNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	LineArray* pElement = new LineArray();
	if( pElement ) {
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", (jint)pElement );
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeTypeID", LINE_ARRAY_TYPE_ID );
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_releaseNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	LineArray* pElement = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onBoundChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onBoundChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jdouble x, jdouble y, jdouble z, jdouble radius )
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	int nResult = pSelf->OnBoundChange( x, y, z, radius );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
}



JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onNameChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onNameChange
#endif
( JNIEnv* pEnv, jobject jSelf, jstring value ) {
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
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
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVerticesBeginChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVerticesBeginChange
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVerticesBeginChange();
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVerticesEndChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVerticesEndChange
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVerticesEndChange();
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVerticesFormatAndLengthChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVerticesFormatAndLengthChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint format, jint length ) 
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVerticesFormatAndLengthChange( format, length );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVerticesVertexPositionChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVerticesVertexPositionChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint index, jdouble x, jdouble y, jdouble z ) 
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVerticesVertexPositionChange( index, x, y, z );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVerticesVertexNormalChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVerticesVertexNormalChange
#endif
	( JNIEnv* pEnv, jobject jSelf, jint index, jdouble i, jdouble j, jdouble k ) 
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVerticesVertexNormalChange( index, i, j, k );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVerticesVertexDiffuseColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVerticesVertexDiffuseColorChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint index, jfloat red, jfloat green, jfloat blue, jfloat alpha ) 
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVerticesVertexDiffuseColorChange( index, red, green, blue, alpha );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVerticesVertexSpecularHighlightColorChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVerticesVertexSpecularHighlightColorChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint index, jfloat red, jfloat green, jfloat blue, jfloat alpha ) 
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVerticesVertexSpecularHighlightColorChange( index, red, green, blue, alpha );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVerticesVertexTextureCoordinate0Change
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVerticesVertexTextureCoordinate0Change
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint index, jfloat u, jfloat v ) 
{
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVerticesVertexTextureCoordinate0Change( index, u, v );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}




JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVertexLowerBoundChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVertexLowerBoundChange
#endif
( JNIEnv* pEnv, jobject jSelf, jint value ) {
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVertexLowerBoundChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_LineArrayProxy_onVertexUpperBoundChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_LineArrayProxy_onVertexUpperBoundChange
#endif
( JNIEnv* pEnv, jobject jSelf, jint value ) {
	LineArray* pSelf = (LineArray*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->OnVertexUpperBoundChange( value );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


} //extern "C"
