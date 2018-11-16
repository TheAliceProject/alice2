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
#include "fileutilities.h"

struct CopyContext {
	JNIEnv* pjEnv;
	jobject jProgressObserver;
	jmethodID jUpdateTotalID;
	jmethodID jUpdateID;
	int nTotal;
};

/*bool*/int CopyCallback( void* pContext, int nProgress, int nTotal ) {
	struct CopyContext* psCopyContext = (struct CopyContext*)pContext;
	if( psCopyContext->nTotal != nTotal ) {
		if( psCopyContext->jProgressObserver && psCopyContext->jUpdateTotalID ) {
			(*psCopyContext->pjEnv)->CallVoidMethod( psCopyContext->pjEnv, psCopyContext->jProgressObserver, psCopyContext->jUpdateTotalID, nTotal );
			if( (*psCopyContext->pjEnv)->ExceptionOccurred( psCopyContext->pjEnv ) ) {
				(*psCopyContext->pjEnv)->ExceptionDescribe( psCopyContext->pjEnv );
				(*psCopyContext->pjEnv)->ExceptionClear( psCopyContext->pjEnv );
			}
		}
		psCopyContext->nTotal = nTotal;
	}
	if( psCopyContext->jProgressObserver && psCopyContext->jUpdateID ) {
		(*psCopyContext->pjEnv)->CallVoidMethod( psCopyContext->pjEnv, psCopyContext->jProgressObserver, psCopyContext->jUpdateID, nProgress, NULL );
		if( (*psCopyContext->pjEnv)->ExceptionOccurred( psCopyContext->pjEnv ) ) {
			return /*false*/0;
		}
	}
	return /*true*/1;
}

/*
 * Class:     edu_cmu_cs_stage3_io_FileUtilities
 * Method:    copy
 * Signature: (Ljava/lang/String;Ljava/lang/String;ZLedu/cmu/cs/stage3/progress/ProgressObserver;)Z
 */
JNIEXPORT jboolean JNICALL Java_edu_cmu_cs_stage3_io_FileUtilities_copy( JNIEnv* pjEnv, jclass jCls, jstring jSrcPath, jstring jDstPath, jboolean jOverwriteIfNecessary, jobject jProgressObserver ) {
	jboolean jResult;
	const char* vcSrcPath = (*pjEnv)->GetStringUTFChars( pjEnv, jSrcPath, NULL );
	const char* vcDstPath = (*pjEnv)->GetStringUTFChars( pjEnv, jDstPath, NULL );
	struct CopyContext sCopyContext;
	sCopyContext.pjEnv = pjEnv;
	sCopyContext.jProgressObserver = jProgressObserver;
	sCopyContext.nTotal = -1;
	if( jProgressObserver ) {
		jclass jProgressObserverClass = (*pjEnv)->GetObjectClass( pjEnv, jProgressObserver );
		sCopyContext.jUpdateTotalID = (*pjEnv)->GetMethodID( pjEnv, jProgressObserverClass, "progressUpdateTotal", "(I)V" );
		if( (*pjEnv)->ExceptionOccurred( pjEnv ) ) {
			(*pjEnv)->ExceptionDescribe( pjEnv );
			(*pjEnv)->ExceptionClear( pjEnv );
		}
		sCopyContext.jUpdateID = (*pjEnv)->GetMethodID( pjEnv, jProgressObserverClass, "progressUpdate", "(ILjava/lang/String;)V" );
		if( (*pjEnv)->ExceptionOccurred( pjEnv ) ) {
			(*pjEnv)->ExceptionDescribe( pjEnv );
			(*pjEnv)->ExceptionClear( pjEnv );
		}
	} else {
		sCopyContext.jUpdateTotalID = NULL;
		sCopyContext.jUpdateID = NULL;
	}

	if( FileUtilities_copy( vcSrcPath, vcDstPath, jOverwriteIfNecessary==JNI_TRUE, CopyCallback, &sCopyContext ) ) {
		jResult = JNI_TRUE;
	} else {
		jResult = JNI_FALSE;
	}

	(*pjEnv)->ReleaseStringUTFChars( pjEnv, jSrcPath, vcSrcPath );
	(*pjEnv)->ReleaseStringUTFChars( pjEnv, jDstPath, vcDstPath );

	return jResult;
}
