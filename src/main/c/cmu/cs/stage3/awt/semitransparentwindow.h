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

#ifndef SEMITRANSPARENT_WINDOW_H
#define SEMITRANSPARENT_WINDOW_H

int SemitransparentWindow_IsSupported( int* pbIsSupported );

int SemitransparentWindow_Create( void** ppData );
int SemitransparentWindow_Destroy( void* pData );
int SemitransparentWindow_Show( void* pData );
int SemitransparentWindow_Hide( void* pData );
int SemitransparentWindow_SetLocationOnScreen( void* pData, long nX, long nY );
int SemitransparentWindow_SetImage( void* pData, long* vnPixels, long nWidth, long nHeight );
int SemitransparentWindow_SetOpacity( void* pData, double fOpacity );


#endif