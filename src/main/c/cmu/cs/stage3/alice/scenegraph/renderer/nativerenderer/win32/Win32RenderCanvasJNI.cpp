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

#include "RenderCanvas.hpp"
#include "UtilJNI.hpp"
#include "jawt_md.h"

typedef jboolean (JNICALL * GetAWTFunction)(JNIEnv*,JAWT*);
static GetAWTFunction g_fGetAWT = NULL;

class DrawingSurfaceInfo {
public:
	DrawingSurfaceInfo() {
	}
	int Initialize( JNIEnv* jEnv, jobject jComponent ) {
		if( g_fGetAWT == NULL ) {
			const char* vcModuleName = "jawt.dll";
			HMODULE hModule = GetModuleHandle( vcModuleName );
			if( hModule ) {
				FARPROC fp = GetProcAddress( hModule, "_JAWT_GetAWT@8" );
				if( fp ) {
					g_fGetAWT = (GetAWTFunction)fp;
				} else {
					fprintf( stderr, "WARNING: GetProcAddress( hModule, \"_JAWT_GetAWT@8\" )\n" );
					return -1;
				}
			} else {
				fprintf( stderr, "WARNING: GetModuleHandle( \"jawt.dll\" )\n" );
				return -1;
			}
		}

		m_awt.version = JAWT_VERSION_1_3;
		if( g_fGetAWT(jEnv, &m_awt) != JNI_TRUE ) {
			fprintf( stderr, "WARNING: g_fGetAWT(jEnv, &m_awt)\n" );
			return -1;
		}
		m_ds = m_awt.GetDrawingSurface( jEnv, jComponent );
		if( m_ds == NULL ) {
			fprintf( stderr, "WARNING: m_awt.GetDrawingSurface( jEnv, jComponent )\n" );
			return -1;
		}

		if( (m_ds->Lock(m_ds) & JAWT_LOCK_ERROR) != 0 ) {
			fprintf( stderr, "WARNING: m_ds->Lock(m_ds)\n" );
			FreeDrawingSurface();
			return -1;
		}

		m_dsi = m_ds->GetDrawingSurfaceInfo(m_ds);
		if( m_dsi == NULL ) {
			fprintf( stderr, "WARNING:  m_ds->GetDrawingSurfaceInfo(m_ds)\n" );
			FreeDrawingSurfaceAndUnlock();
			return -1;
		}
		return 0;
	}
	int Release() {
		if( m_dsi ) {
			m_ds->FreeDrawingSurfaceInfo( m_dsi );
			m_dsi = NULL;
		}
		return FreeDrawingSurfaceAndUnlock();
	}

	HWND GetHWnd() {
		JAWT_Win32DrawingSurfaceInfo* dsi_win = (JAWT_Win32DrawingSurfaceInfo*)m_dsi->platformInfo;
		if( dsi_win != NULL ) {
			return dsi_win->hwnd;
		} else {
			return NULL;
		}
	}
private:
	int FreeDrawingSurface() {
		if( m_ds ) {
#ifndef _DEBUG
			m_awt.FreeDrawingSurface( m_ds );
#endif
			m_ds = NULL;
		} else {
			//todo?
		}
		return S_OK;
	}
	int FreeDrawingSurfaceAndUnlock() {
		if( m_ds ) {
			m_ds->Unlock(m_ds);
			return FreeDrawingSurface();
		} else {
			return S_OK; //todo?
		}
	}

	JAWT m_awt;
	JAWT_DrawingSurface* m_ds;
	JAWT_DrawingSurfaceInfo* m_dsi;
};

static DrawingSurfaceInfo* JNI_GetDrawingSurfaceInfo( JNIEnv* pEnv, jobject jSelf ) {
	if( jSelf ) {
		return (DrawingSurfaceInfo*)JNI_GetIntFieldNamed( pEnv, jSelf, "m_nativeDrawingSurfaceInfo" );
	} else {
		return NULL;
	}
}

static void JNI_SetDrawingSurfaceInfo( JNIEnv* pEnv, jobject jSelf, DrawingSurfaceInfo* pDrawingSurfaceInfo ) {
	JNI_SetIntFieldNamed( pEnv, jSelf, "m_nativeDrawingSurfaceInfo", (jint)pDrawingSurfaceInfo );
}


extern "C" {

JNIEXPORT jboolean JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderCanvas_acquireDrawingSurface
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderCanvas_acquireDrawingSurface
#endif
  ( JNIEnv* jEnv, jobject jSelf )
{
	if( jSelf==NULL ) {
		JNI_ThrowNewException( jEnv, -1 );
		return JNI_FALSE;
	}

	bool bIsValid = false;
	DrawingSurfaceInfo* pDrawingSurfaceInfo = JNI_GetDrawingSurfaceInfo( jEnv, jSelf );
	if( pDrawingSurfaceInfo ) {
		jclass cls = jEnv->FindClass( "java/lang/RuntimeException" );
		jEnv->ThrowNew( cls, "drawing surface not released." );
	} else {
		pDrawingSurfaceInfo = new DrawingSurfaceInfo();
	}
	int nResult = pDrawingSurfaceInfo->Initialize( jEnv, jSelf );
	if( nResult < 0 ) {
		JNI_ThrowNewException( jEnv, nResult );
	} else {
		RenderCanvas* pRenderCanvas = (RenderCanvas*)JNI_GetNativeInstance( jEnv, jSelf );
		pRenderCanvas->OnAcquireDrawingSurface( pDrawingSurfaceInfo->GetHWnd(), bIsValid );
	}

	if( bIsValid ) {
		JNI_SetDrawingSurfaceInfo( jEnv, jSelf, pDrawingSurfaceInfo );
		return JNI_TRUE;
	} else {
		pDrawingSurfaceInfo->Release();
		delete pDrawingSurfaceInfo;
		return JNI_FALSE;
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderCanvas_releaseDrawingSurface
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderCanvas_releaseDrawingSurface
#endif
  ( JNIEnv* jEnv, jobject jSelf )
{
	if( jSelf ) {
		DrawingSurfaceInfo* pDrawingSurfaceInfo = JNI_GetDrawingSurfaceInfo( jEnv, jSelf );
		if( pDrawingSurfaceInfo ) {
			RenderCanvas* pRenderCanvas = (RenderCanvas*)JNI_GetNativeInstance( jEnv, jSelf );
			JNI_SetDrawingSurfaceInfo( jEnv, jSelf, NULL );
			int nResult = pDrawingSurfaceInfo->Release();
			delete pDrawingSurfaceInfo;
			if( nResult<0 ) {
				JNI_ThrowNewException( jEnv, nResult );
			}
		}
	} else {
		JNI_ThrowNewException( jEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderCanvas_swapBuffers
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderCanvas_swapBuffers
#endif
  ( JNIEnv* jEnv, jobject jSelf )
{
	RenderCanvas* pRenderCanvas = (RenderCanvas*)JNI_GetNativeInstance( jEnv, jSelf );
	int nResult = pRenderCanvas->SwapBuffers();
	if( nResult<0 ) {
		JNI_ThrowNewException( jEnv, nResult );
	}
}

} //extern "C"