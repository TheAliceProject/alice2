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

#if defined _MACOSX
#include <JavaVM/jni.h>
#else
#include "jni.h"
#endif

void JNI_AddElement( JNIEnv* pEnv, jobject jSelf, jobject jElement );

jint JNI_GetIntFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName );
void JNI_SetIntFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName, jint value );
jdouble JNI_GetDoubleFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName );
jobject JNI_GetObjectFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName, const char* signature );
void JNI_SetStringFieldNamed( JNIEnv* pEnv, jobject jSelf, const char* fieldName, const char *bytes );

jobject JNI_GetObjectFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName, const char* signature, ... );
jint JNI_GetIntFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName, const char* signature, ...  );
jint JNI_GetIntFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName );
jdouble JNI_GetDoubleFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName, const char* signature, ...  );
jdouble JNI_GetDoubleFromMethodNamed( JNIEnv* pEnv, jobject jSelf, const char* methodName );

void* JNI_GetNativeInstance( JNIEnv* pEnv, jobject jSelf );
void JNI_SetNativeInstance( JNIEnv* pEnv, jobject jSelf, void* nativeInstance );
void JNI_ThrowNewException( JNIEnv* pEnv, int nResult );

jobject JNI_NewObject( JNIEnv* pEnv, const char* className );
jobject JNI_NewObject( JNIEnv* pEnv, const char* className, const char* signature, ... );
void JNI_CallVoidMethod( JNIEnv* pEnv, jobject jSelf, const char* methodName );


#define JNIERR_CLASS_NOT_SUPPORTED -16
#define JNIERR_TYPE_ID_NOT_FOUND -17
#define JNIERR_NULL_NATIVE_INSTANCE -18