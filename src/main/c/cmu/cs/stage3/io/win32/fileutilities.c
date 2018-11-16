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

#define _WIN32_WINNT 0x0400 
#include <windows.h>

struct CallbackContext {
	/*bool*/int(*fcnCallback)(void*,int,int);
	void* pContext;
};

static DWORD CALLBACK CopyProgressRoutine( LARGE_INTEGER TotalFileSize, LARGE_INTEGER TotalBytesTransferred, LARGE_INTEGER StreamSize, LARGE_INTEGER StreamBytesTransferred, DWORD dwStreamNumber, DWORD dwCallbackReason, HANDLE hSourceFile, HANDLE hDestinationFile, LPVOID lpData ) {
	struct CallbackContext* psCallbackContext = (struct CallbackContext*)lpData;
	DWORD dwResult;
	if( psCallbackContext && psCallbackContext->fcnCallback ) {
		if( psCallbackContext->fcnCallback( psCallbackContext->pContext, TotalBytesTransferred.LowPart, TotalFileSize.LowPart ) ) {
			dwResult = PROGRESS_CONTINUE;
		} else {
			dwResult = PROGRESS_CANCEL;
		}
	} else {
		dwResult = PROGRESS_QUIET;
	}
	return dwResult;
}

/*bool*/int FileUtilities_copy( const char* vcSrcPath, const char* vcDstPath, /*bool*/int bOverwriteIfNecessary, /*bool*/int(*fcnCallback)(void*,int,int), void* pContext ) {
	BOOL bFailIfExists = !bOverwriteIfNecessary;
	BOOL bCancel = FALSE;
	DWORD dwCopyFlags; 
	struct CallbackContext sCallbackContect;
	sCallbackContect.fcnCallback = fcnCallback;
	sCallbackContect.pContext = pContext;
	if( bOverwriteIfNecessary ) {
		dwCopyFlags = 0;
	} else {
		dwCopyFlags = COPY_FILE_FAIL_IF_EXISTS;
	}
	return CopyFileEx( vcSrcPath, vcDstPath, CopyProgressRoutine, &sCallbackContect, &bCancel, dwCopyFlags );
}
