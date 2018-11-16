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

#ifndef ERROR_INCLUDED
#define ERROR_INCLUDED

const char* ERROR_GetText( int nError );
void ERROR_SetFileAndLine( const char* vcFile, int nLine );

#define GENERIC_FAILURE -1
#define TRUTH_FAILURE -2

#define CHECK_TRUTH(V) if(!(V)){ ERROR_SetFileAndLine( __FILE__, __LINE__ ); return TRUTH_FAILURE; }
#define CHECK_NOT_NULL CHECK_TRUTH
#define CHECK_SUCCESS(RESULT) if((RESULT)<0){ ERROR_SetFileAndLine( __FILE__, __LINE__ ); return (RESULT); }
#define CHECK_SUCCESS_WITH_CLEANUP(RESULT,CODE) if((RESULT)<0){ ERROR_SetFileAndLine( __FILE__, __LINE__ ); CODE(); return (RESULT); }

#define ASSERT_TRUE(PTR) assert((PTR))
#define ASSERT_SUCCESS(RESULT) assert((RESULT)>=0)


#ifdef OPENGL_RENDERER

extern GLenum g_nError;
#define GL_FAILURE -3

#define CHECK_GL(FCN) FCN;g_nError=glGetError();if(g_nError!=GL_NO_ERROR){ ERROR_SetFileAndLine( __FILE__, __LINE__ ); return GL_FAILURE; };

#endif 

#endif