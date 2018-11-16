#ifndef PLATFORM_INCLUDED
#define PLATFORM_INCLUDED

#define OPENGL_RENDERER

#include "common.hpp"

#if defined _MACOSX

	#include <OpenGL/gl.h>
	#include <OpenGL/glu.h>

	#define IsNaN isnan

	#define CHANGE_IMAGE_DATA

#else

	#if defined _WINDOWS

		#define WIN32_LEAN_AND_MEAN
		#include <windows.h>
		#define IsNaN _isnan

	#else

		#define IsNaN isnan

	#endif


	#include <GL/gl.h>
	#include <GL/glu.h>

#endif

#ifndef S_OK
#define S_OK 0
#endif

#include "error.hpp"

typedef GLenum LightID;
typedef GLenum ClippingPlaneID;
typedef unsigned VisualID;

extern GLdouble g_vfIdentity[16];
extern GLfloat g_vfBlack[4];
extern GLUquadricObj* g_pGLUQuadric;


#endif