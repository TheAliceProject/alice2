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

#include <jni.h>
#include "jni_util.h"
#include "semitransparentwindow.h"

/*
 * Class:     edu_cmu_cs_stage3_awt_SemitransparentWindow
 * Method:    isSupportedNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_SemitransparentWindow_isSupportedNative( JNIEnv* pjEnv, jclass jCls ) {
	int bIsSupported;
	SemitransparentWindow_IsSupported( &bIsSupported );
	return bIsSupported;
}

/*
 * Class:     edu_cmu_cs_stage3_awt_SemitransparentWindow
 * Method:    create
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_SemitransparentWindow_createNative( JNIEnv* pjEnv, jobject jSelf ) {
	void* pData;
	SemitransparentWindow_Create( &pData );
	JNI_SetIntFieldNamed( pjEnv, jSelf, "m_nativeData", (jint)pData );
}

/*
 * Class:     edu_cmu_cs_stage3_awt_SemitransparentWindow
 * Method:    destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_SemitransparentWindow_destroyNative( JNIEnv* pjEnv, jobject jSelf ) {
	SemitransparentWindow_Destroy( (void*)JNI_GetIntFieldNamed( pjEnv, jSelf, "m_nativeData" ) );
	JNI_SetIntFieldNamed( pjEnv, jSelf, "m_nativeData", (jint)0 );
}

/*
 * Class:     edu_cmu_cs_stage3_awt_SemitransparentWindow
 * Method:    show
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_SemitransparentWindow_showNative( JNIEnv* pjEnv, jobject jSelf ) {
	SemitransparentWindow_Show( (void*)JNI_GetIntFieldNamed( pjEnv, jSelf, "m_nativeData" ) );
}

/*
 * Class:     edu_cmu_cs_stage3_awt_SemitransparentWindow
 * Method:    hide
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_SemitransparentWindow_hideNative( JNIEnv* pjEnv, jobject jSelf ) {
	SemitransparentWindow_Hide( (void*)JNI_GetIntFieldNamed( pjEnv, jSelf, "m_nativeData" ) );
}

/*
 * Class:     edu_cmu_cs_stage3_awt_SemitransparentWindow
 * Method:    setLocationOnScreen
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_SemitransparentWindow_setLocationOnScreenNative( JNIEnv* pjEnv, jobject jSelf, jint jX, jint jY ) {
	SemitransparentWindow_SetLocationOnScreen( (void*)JNI_GetIntFieldNamed( pjEnv, jSelf, "m_nativeData" ), jX, jY );
}

/*
 * Class:     edu_cmu_cs_stage3_awt_SemitransparentWindow
 * Method:    setImage
 * Signature: (II[I)V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_SemitransparentWindow_setImageNative( JNIEnv* pjEnv, jobject jSelf, jint jWidth, jint jHeight, jintArray jPixels ) {
	int nLength;
	jint* vnValue;
	if( jPixels ) {
		nLength = (*pjEnv)->GetArrayLength( pjEnv, jPixels );
		vnValue = (*pjEnv)->GetIntArrayElements( pjEnv, jPixels, NULL );
	} else {
		nLength = 0;
		vnValue = NULL;
	}
	SemitransparentWindow_SetImage( (void*)JNI_GetIntFieldNamed( pjEnv, jSelf, "m_nativeData" ), vnValue, jWidth, jHeight );
	if( jPixels ) {
		(*pjEnv)->ReleaseIntArrayElements( pjEnv, jPixels, vnValue, JNI_ABORT );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_awt_SemitransparentWindow
 * Method:    setOpacity
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_SemitransparentWindow_setOpacityNative( JNIEnv* pjEnv, jobject jSelf, jdouble jOpacity ) {
	SemitransparentWindow_SetOpacity( (void*)JNI_GetIntFieldNamed( pjEnv, jSelf, "m_nativeData" ), jOpacity );
}


