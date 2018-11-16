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
#include "awtutilities.h"

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isGetCursorLocationSupportedNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isGetCursorLocationSupportedNative( JNIEnv* pjEnv, jclass jCls ) {
	return AWTUtilities_IsGetCursorLocationSupported();
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isSetCursorLocationSupportedNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isSetCursorLocationSupportedNative( JNIEnv* pjEnv, jclass jCls ) {
	return AWTUtilities_IsSetCursorLocationSupported();
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isIsKeyPressedSupportedNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isIsKeyPressedSupportedNative( JNIEnv* pjEnv, jclass jCls ) {
	return AWTUtilities_IsIsKeyPressedSupported();
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isGetModifiersSupportedNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isGetModifiersSupportedNative( JNIEnv* pjEnv, jclass jCls ) {
	return AWTUtilities_IsGetModifiersSupported();
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isPumpMessageQueueSupportedNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isPumpMessageQueueSupportedNative( JNIEnv* pjEnv, jclass jCls ) {
	return AWTUtilities_IsPumpMessageQueueSupported();
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    pumpMessageQueueNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_pumpMessageQueueNative( JNIEnv* pjEnv, jclass jCls ) {
	AWTUtilities_PumpMessageQueue();
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    getCursorLocationNative
 * Signature: (Ljava/awt/Point;)V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_getCursorLocationNative( JNIEnv* pjEnv, jclass jCls, jobject jPoint ) {
	if( jPoint ) {
		long x;
		long y;
		AWTUtilities_GetCursorLocation( &x, &y );
		JNI_SetIntFieldNamed( pjEnv, jPoint, "x", x );
		JNI_SetIntFieldNamed( pjEnv, jPoint, "y", y );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    setCursorLocationNative
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_setCursorLocationNative( JNIEnv* pjEnv, jclass jCls, jint jX, jint jY ) {
	AWTUtilities_SetCursorLocation( jX, jY );
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isCursorShowingNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isCursorShowingNative( JNIEnv* pjEnv, jclass jCls ) {
	if( AWTUtilities_IsCursorShowing() ) {
		return JNI_TRUE;
	} else {
		return JNI_FALSE;
	}
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    setIsCursorShowingNative
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_setIsCursorShowingNative( JNIEnv* pjEnv, jclass jCls, jboolean jIsCursorShowing ) {
	AWTUtilities_SetIsCursorShowing( jIsCursorShowing==JNI_TRUE );
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isIsCursorShowingSupportedNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isIsCursorShowingSupportedNative( JNIEnv* pjEnv, jclass jCls ) {
	if( AWTUtilities_IsIsCursorShowingSupported() ) {
		return JNI_TRUE;
	} else {
		return JNI_FALSE;
	}
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isSetIsCursorShowingSupportedNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isSetIsCursorShowingSupportedNative( JNIEnv* pjEnv, jclass jCls ) {
	if( AWTUtilities_IsSetIsCursorShowingSupported() ) {
		return JNI_TRUE;
	} else {
		return JNI_FALSE;
	}
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    isKeyPressedNative
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_isKeyPressedNative( JNIEnv* pjEnv, jclass jCls, jint jKeyCode) {
	int nVK = jKeyCode;
	//switch( jKeyCode ) {
	//todo: handle special cases
	//}
	return AWTUtilities_IsKeyPressed( nVK );
}

/*
 * Class:     edu_cmu_cs_stage3_awt_AWTUtilities
 * Method:    getModifiersNative
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_edu_cmu_cs_stage3_awt_AWTUtilities_getModifiersNative( JNIEnv* pjEnv, jclass jCls ) {
	return AWTUtilities_GetModifiers();
}
