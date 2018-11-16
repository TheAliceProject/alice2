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

#include "UtilJNI.hpp"
#include "error.hpp"
#include <assert.h>

jobject JNI_NewObject( JNIEnv* pEnv, const char* className, const char* signature, ... ) {
	va_list args;
	jclass cls = pEnv->FindClass( className );
	assert( cls );
	jmethodID mid = pEnv->GetMethodID( cls, "<init>", signature );
	assert( mid );
	va_start(args, signature);
	jobject ret = pEnv->NewObjectV( cls, mid, args );
	va_end(args);
	return ret;
}

jobject JNI_NewObject( JNIEnv* pEnv, const char* className ) {
	return JNI_NewObject( pEnv, className, "()V" );
}

void JNI_CallVoidMethod( JNIEnv* pEnv, jobject jSelf, const char* methodName ) {
	jclass cls = pEnv->GetObjectClass( jSelf );
	assert( cls );
	jmethodID mid = pEnv->GetMethodID( cls, methodName, "()V" );
	assert( mid );
	pEnv->CallVoidMethod( jSelf, mid );
}

void JNI_AddElement( JNIEnv* pEnv, jobject jSelf, jobject jElement ) {
	jclass cls = pEnv->GetObjectClass( jSelf );
	assert( cls );
	jmethodID mid = pEnv->GetMethodID( cls, "addElement", "(Ljava/lang/Object;)V" );
	assert( mid );
	pEnv->CallVoidMethod( jSelf, mid, jElement );
}

jint JNI_GetIntFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName ) {
	jclass cls = pEnv->GetObjectClass(jSelf);
	assert( cls );
	jfieldID fid = pEnv->GetFieldID( cls, fieldName, "I" );
	assert( fid );
	return pEnv->GetIntField( jSelf, fid );
}
void JNI_SetIntFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName, jint value ) {
	jclass cls = pEnv->GetObjectClass(jSelf);
	assert( cls );
	jfieldID fid = pEnv->GetFieldID( cls, fieldName, "I" );
	assert( fid );
	pEnv->SetIntField( jSelf, fid, value );
}
jdouble JNI_GetDoubleFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName ) {
	jclass cls = pEnv->GetObjectClass(jSelf);
	assert( cls );
	jfieldID fid = pEnv->GetFieldID( cls, fieldName, "D" );
	assert( fid );
	return pEnv->GetDoubleField( jSelf, fid );
}
jobject JNI_GetObjectFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName, const char* signature ) {
	jclass cls = pEnv->GetObjectClass(jSelf);
	assert( cls );
	jfieldID fid = pEnv->GetFieldID( cls, fieldName, signature );
	assert( fid );
	return pEnv->GetObjectField( jSelf, fid );
}
void JNI_SetStringFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName, const char *bytes ) {
	jclass cls = pEnv->GetObjectClass(jSelf);
	assert( cls );
	jfieldID fid = pEnv->GetFieldID( cls, fieldName, "Ljava/lang/String;" );
	assert( fid );
	jstring value = pEnv->NewStringUTF( bytes );
	assert( value );
	pEnv->SetObjectField( jSelf, fid, value );
}

jobject JNI_GetObjectFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName, const char* signature, ... ) {
	va_list args;
	jclass cls = pEnv->GetObjectClass( jSelf );
	assert( cls );
	jmethodID mid = pEnv->GetMethodID( cls, methodName, signature );
	assert( mid );
	va_start(args, signature);
	jobject ret = pEnv->CallObjectMethodV( jSelf, mid, args );
	va_end(args);
	return ret;
}
jint JNI_GetIntFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName, const char* signature, ...  ) {
	va_list args;
	jclass cls = pEnv->GetObjectClass( jSelf );
	assert( cls );
	jmethodID mid = pEnv->GetMethodID( cls, methodName, signature );
	assert( mid );
	va_start(args, signature);
	jint ret = pEnv->CallIntMethodV( jSelf, mid, args );
	va_end(args);
	return ret;
}
jint JNI_GetIntFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName ) {
	return JNI_GetIntFromMethodNamed( pEnv, jSelf, methodName, "()I" );
}
jdouble JNI_GetDoubleFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName, const char* signature, ...  ) {
	va_list args;
	jclass cls = pEnv->GetObjectClass( jSelf );
	assert( cls );
	jmethodID mid = pEnv->GetMethodID( cls, methodName, signature );
	assert( mid );
	va_start(args, signature);
	jdouble ret = pEnv->CallDoubleMethodV( jSelf, mid, args );
	va_end(args);
	return ret;
}
jdouble JNI_GetDoubleFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName ) {
	return JNI_GetDoubleFromMethodNamed( pEnv, jSelf, methodName, "()D" );
}

void* JNI_GetNativeInstance( JNIEnv* pEnv, jobject jSelf ) {
	if( jSelf ) {
		return (void*)JNI_GetIntFieldNamed( pEnv, jSelf, "m_nativeInstance" );
	} else {
		return NULL;
	}
}

void JNI_SetNativeInstance( JNIEnv* pEnv, jobject jSelf, void* nativeInstance ) {
	JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeInstance", (jint)nativeInstance );
}

void JNI_ThrowNewException( JNIEnv* pEnv, int nResult ) {
	jclass cls = pEnv->FindClass( "java/lang/RuntimeException" );
	pEnv->ThrowNew( cls, ERROR_GetText( nResult ) );
}

