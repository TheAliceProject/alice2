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

#include <windows.h>
#include <jni.h>

/*
 * Class:     edu_cmu_cs_stage3_alice_authoringtool_util_CDUtil
 * Method:    getCDRootPaths
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_edu_cmu_cs_stage3_alice_authoringtool_util_CDUtil_getCDRootPaths( JNIEnv* pjEnv, jclass jCls ) {
	//GetDriveType
    //GetLogicalDriveStrings
	char vcBuffer[ 1024 ];
	int nLength = GetLogicalDriveStrings( 1024, vcBuffer );
	char* pcCurrentDrive = vcBuffer;
	char* pcCDROMs[26];
	int nCDROMCount = 0;
	jclass jStringCls = (*pjEnv)->FindClass( pjEnv, "java/lang/String" );
	jobjectArray jPaths;
	int i;
	while( pcCurrentDrive[ 0 ] ) {
		if( GetDriveType( pcCurrentDrive ) == DRIVE_CDROM ) {
			pcCDROMs[ nCDROMCount ] = pcCurrentDrive;
			nCDROMCount++;
		}
		pcCurrentDrive += strlen( pcCurrentDrive ) + 1;
	}
	jPaths = (*pjEnv)->NewObjectArray( pjEnv, nCDROMCount, jStringCls, NULL );
	for( i = 0; i<nCDROMCount; i++ ) {
		(*pjEnv)->SetObjectArrayElement( pjEnv, jPaths, i, (*pjEnv)->NewStringUTF( pjEnv, pcCDROMs[ i ] ) );
	}
	return jPaths;
}
