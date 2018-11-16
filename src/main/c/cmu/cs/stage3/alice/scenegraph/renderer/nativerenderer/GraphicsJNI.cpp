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

#include "Graphics.hpp"
#include "RenderTarget.hpp"
#include "UtilJNI.hpp"

class TextureMap;

extern "C" {

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics
 * Method:    createNativeInstance
 * Signature: (Ledu/cmu/cs/stage3/alice/scenegraph/renderer/nativerenderer/RenderTargetAdapter;)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_createNativeInstance__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_RenderTargetAdapter_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_createNativeInstance__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_RenderTargetAdapter_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jRenderTarget ) 
{
	Graphics* pGraphics = new Graphics();
	if( pGraphics ) {
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", (jint)pGraphics );
		RenderTarget* pRenderTarget = (RenderTarget*)JNI_GetNativeInstance( pEnv, jRenderTarget );
		int nResult = pGraphics->Lock( pRenderTarget );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics
 * Method:    createNativeInstance
 * Signature: (Ledu/cmu/cs/stage3/alice/scenegraph/renderer/nativerenderer/RenderTargetAdapter;Ledu/cmu/cs/stage3/alice/scenegraph/renderer/nativerenderer/TextureMapProxy;)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_createNativeInstance__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_RenderTargetAdapter_2Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_createNativeInstance__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_RenderTargetAdapter_2Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jRenderTarget, jobject jTextureMap ) 
{
	Graphics* pGraphics = new Graphics();
	if( pGraphics ) {
		JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", (jint)pGraphics );
		RenderTarget* pRenderTarget = (RenderTarget*)JNI_GetNativeInstance( pEnv, jRenderTarget );
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pGraphics->Lock( pRenderTarget, pTextureMap );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics
 * Method:    releaseNativeInstance
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_releaseNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->Release();
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    setFont
 * Signature: (Ljava/lang/String;Ljava/lang/String;ZZI)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_setFont
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_setFont
#endif
  ( JNIEnv* pEnv, jobject jSelf, jstring jFamily, jstring jName, jboolean isBold, jboolean isItalic, jint size ) 
{
	const char* vcFamily = NULL;
	if( jFamily ) {
		vcFamily = pEnv->GetStringUTFChars( jFamily, NULL );
	}
	const char* vcName = NULL;
	if( jName ) {
		vcName = pEnv->GetStringUTFChars( jName, NULL );
	}
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->SetFont( vcFamily, vcName, isBold==JNI_TRUE, isItalic==JNI_TRUE, size );
	if( jFamily ) {
		pEnv->ReleaseStringUTFChars( jFamily, vcFamily );
	}
	if( jName ) {
		pEnv->ReleaseStringUTFChars( jName, vcName );
	}
}


/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    translate
 * Signature: (II)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_translate
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_translate
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->Translate( x, y );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    getColor
 * Signature: ()Ljava/awt/Color;
 */
JNIEXPORT jobject JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_getColor
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_getColor
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	jint red;
	jint green;
	jint blue;
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->GetColor( red, green, blue );
	return JNI_NewObject( pEnv, "java/awt/Color", "(III)V", red, green, blue );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    setColor
 * Signature: (Ljava/awt/Color;)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_setColor
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_setColor
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jColor ) 
{
	jint red = JNI_GetIntFromMethodNamed( pEnv, jColor, "getRed" );
	jint green = JNI_GetIntFromMethodNamed( pEnv, jColor, "getGreen" );
	jint blue = JNI_GetIntFromMethodNamed( pEnv, jColor, "getBlue" );
	jint alpha = 255;
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->SetColor( red, green, blue );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    setPaintMode
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_setPaintMode
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_setPaintMode
#endif
  ( JNIEnv* pEnv, jobject jSelf ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->SetPaintMode();
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    setXORMode
 * Signature: (Ljava/awt/Color;)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_setXORMode
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_setXORMode
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jColor ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->SetXORMode();
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    copyArea
 * Signature: (IIIIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_copyArea
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_copyArea
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height, jint dx, jint dy ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->CopyArea( x, y, width, height, dx, dy );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawLine
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawLine
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawLine
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x1, jint y1, jint x2, jint y2 ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->DrawLine( x1, y1, x2, y2 );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    fillRect
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_fillRect
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_fillRect
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->FillRect( x, y, width, height );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    clearRect
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_clearRect
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_clearRect
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->ClearRect( x, y, width, height );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawRoundRect
 * Signature: (IIIIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawRoundRect
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawRoundRect
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height, jint arcWidth, jint arcHeight ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->DrawRoundRect( x, y, width, height, arcWidth, arcHeight );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    fillRoundRect
 * Signature: (IIIIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_fillRoundRect
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_fillRoundRect
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height, jint arcWidth, jint arcHeight ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->FillRoundRect( x, y, width, height, arcWidth, arcHeight );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawOval
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawOval
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawOval
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->DrawOval( x, y, width, height );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    fillOval
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_fillOval
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_fillOval
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->FillOval( x, y, width, height );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawArc
 * Signature: (IIIIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawArc
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawArc
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height, jint startAngle, jint arcAngle ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->DrawArc( x, y, width, height, startAngle, arcAngle );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    fillArc
 * Signature: (IIIIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_fillArc
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_fillArc
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint x, jint y, jint width, jint height, jint startAngle, jint arcAngle ) 
{
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->FillArc( x, y, width, height, startAngle, arcAngle );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawPolyline
 * Signature: ([I[II)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawPolyline
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawPolyline
#endif
  ( JNIEnv* pEnv, jobject jSelf, jintArray xPoints, jintArray yPoints, jint nPoints ) 
{
	jint* vnX = NULL;
	jint* vnY = NULL;
	if( xPoints ) {
		vnX = pEnv->GetIntArrayElements( xPoints, NULL );
	}
	if( yPoints ) {
		vnY = pEnv->GetIntArrayElements( yPoints, NULL );
	}
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->DrawPolyline( vnX, vnY, nPoints );
	if( xPoints ) {
		pEnv->ReleaseIntArrayElements( xPoints, vnX, JNI_ABORT );
	}
	if( yPoints ) {
		pEnv->ReleaseIntArrayElements( yPoints, vnY, JNI_ABORT );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawPolygon
 * Signature: ([I[II)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawPolygon
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawPolygon
#endif
  ( JNIEnv* pEnv, jobject jSelf, jintArray xPoints, jintArray yPoints, jint nPoints ) 
{
	jint* vnX = NULL;
	jint* vnY = NULL;
	if( xPoints ) {
		vnX = pEnv->GetIntArrayElements( xPoints, NULL );
	}
	if( yPoints ) {
		vnY = pEnv->GetIntArrayElements( yPoints, NULL );
	}
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->DrawPolygon( vnX, vnY, nPoints );
	if( xPoints ) {
		pEnv->ReleaseIntArrayElements( xPoints, vnX, JNI_ABORT );
	}
	if( yPoints ) {
		pEnv->ReleaseIntArrayElements( yPoints, vnY, JNI_ABORT );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    fillPolygon
 * Signature: ([I[II)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_fillPolygon
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_fillPolygon
#endif
  ( JNIEnv* pEnv, jobject jSelf, jintArray xPoints, jintArray yPoints, jint nPoints ) 
{
	jint* vnX = NULL;
	jint* vnY = NULL;
	if( xPoints ) {
		vnX = pEnv->GetIntArrayElements( xPoints, NULL );
	}
	if( yPoints ) {
		vnY = pEnv->GetIntArrayElements( yPoints, NULL );
	}
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->FillPolygon( vnX, vnY, nPoints );
	if( xPoints ) {
		pEnv->ReleaseIntArrayElements( xPoints, vnX, JNI_ABORT );
	}
	if( yPoints ) {
		pEnv->ReleaseIntArrayElements( yPoints, vnY, JNI_ABORT );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawString
 * Signature: (Ljava/lang/String;II)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawString
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawString
#endif
  ( JNIEnv* pEnv, jobject jSelf, jstring jString, jint x, jint y ) 
{
	const char* vcString = NULL;
	if( jString ) {
		vcString = pEnv->GetStringUTFChars( jString, NULL );
	}
	((Graphics*)JNI_GetNativeInstance( pEnv, jSelf ))->DrawString( vcString, x, y );
	if( jString ) {
		pEnv->ReleaseStringUTFChars( jString, vcString );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawChars
 * Signature: ([CIIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawChars
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawChars
#endif
  ( JNIEnv* pEnv, jobject jSelf, jcharArray, jint, jint, jint, jint ) 
{
	//todo
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Graphics
 * Method:    drawBytes
 * Signature: ([BIIII)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Graphics_drawBytes
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Graphics_drawBytes
#endif
  ( JNIEnv* pEnv, jobject jSelf, jbyteArray, jint, jint, jint, jint  ) 
{
	//todo
}

} //extern "C"