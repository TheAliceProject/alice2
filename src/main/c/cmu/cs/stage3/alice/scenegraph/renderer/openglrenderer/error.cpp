#include <stdio.h>
#include <platform.hpp>

static const char* g_vcFile = NULL;
static int g_nLine = NULL;
GLenum g_nError = GL_NO_ERROR;

void ERROR_SetFileAndLine( const char* vcFile, int nLine ) {
	g_vcFile = vcFile;
	g_nLine = nLine;
}

const char* ERROR_GetText( int nResult ) {
	if( g_vcFile!=NULL ) {
		fprintf( stderr, "__FILE__ %s\n", g_vcFile );
		fprintf( stderr, "__LINE__ %d\n", g_nLine );
		fflush( stderr );
	}
	switch( nResult ) {
	case GENERIC_FAILURE:
		return "generic error";
	case TRUTH_FAILURE:
		return "truth failure";
	case GL_FAILURE:
		fprintf( stderr, "__GL__ %s\n", gluErrorString( g_nError ) );
		fflush( stderr );
		return "gl error";
	default:
		return "unknown error";
	}
}

