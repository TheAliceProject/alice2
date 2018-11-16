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

#include "RenderTarget.hpp"
#include "OrthographicCamera.hpp"
#include "PerspectiveCamera.hpp"
#include "ProjectionCamera.hpp"
#include "SymmetricPerspectiveCamera.hpp"
#include "UtilJNI.hpp"


extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_createNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_createNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jRenderer )
{
	if( jSelf ) {
		if( jRenderer ) {
			Renderer* pRenderer = (Renderer*)JNI_GetNativeInstance( pEnv, jRenderer );
			if( pRenderer ) {
				RenderTarget* pSelf = new RenderTarget( pRenderer );
				JNI_SetNativeInstance( pEnv, jSelf, pSelf );
			}
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_releaseNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	if( jSelf ) {
		RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			int nResult = pSelf->Release();
			JNI_SetNativeInstance( pEnv, jSelf, NULL );
			delete pSelf;
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_reset
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_reset
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	if( jSelf ) {
		RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			int nResult = pSelf->Reset();
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_clear
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_clear
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera, jboolean jbAll )
{
	if( jSelf && jCamera ) {
		RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			Camera* pCamera = (Camera*)JNI_GetNativeInstance( pEnv, jCamera );
			int nCameraTypeID = JNI_GetIntFieldNamed( pEnv, jCamera, "m_nativeTypeID" );
			int nResult;
			switch( nCameraTypeID ) {
			case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
				nResult = pSelf->Clear( (OrthographicCamera*)pCamera, jbAll==JNI_TRUE );
				break;
			case PERSPECTIVE_CAMERA_TYPE_ID: 
				nResult = pSelf->Clear( (PerspectiveCamera*)pCamera, jbAll==JNI_TRUE );
				break;
			case PROJECTION_CAMERA_TYPE_ID: 
				nResult = pSelf->Clear( (ProjectionCamera*)pCamera, jbAll==JNI_TRUE );
				break;
			case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
				nResult = pSelf->Clear( (SymmetricPerspectiveCamera*)pCamera, jbAll==JNI_TRUE );
				break;
			default:
				nResult = JNIERR_TYPE_ID_NOT_FOUND;
			}
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_render
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_render
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera )
{
	if( jSelf && jCamera ) {
		RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			Camera* pCamera = (Camera*)JNI_GetNativeInstance( pEnv, jCamera );
			int nCameraTypeID = JNI_GetIntFieldNamed( pEnv, jCamera, "m_nativeTypeID" );
			int nResult;
			switch( nCameraTypeID ) {
			case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
				nResult = pSelf->Render( (OrthographicCamera*)pCamera );
				break;
			case PERSPECTIVE_CAMERA_TYPE_ID: 
				nResult = pSelf->Render( (PerspectiveCamera*)pCamera );
				break;
			case PROJECTION_CAMERA_TYPE_ID: 
				nResult = pSelf->Render( (ProjectionCamera*)pCamera );
				break;
			case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
				nResult = pSelf->Render( (SymmetricPerspectiveCamera*)pCamera );
				break;
			default:
				nResult = JNIERR_TYPE_ID_NOT_FOUND;
			}
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_pick
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_pick
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera, jint jX, jint jY, jboolean jIsSubElementRequired, jboolean jIsOnlyFrontMostRequired, jintArray jAtVisual, jbooleanArray jAtIsFrontFacingAppearance, jintArray jAtSubElement, jdoubleArray jAtZ )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		Camera* pCamera = (Camera*)JNI_GetNativeInstance( pEnv, jCamera );
		assert( pCamera );
		
		int nCameraTypeID = JNI_GetIntFieldNamed( pEnv, jCamera, "m_nativeTypeID" );

		int nResult;

		_Visual* pVisual;
		bool bIsFrontFacingAppearance;
		int nSubElement;
		double fZ;
		switch( nCameraTypeID ) {
		case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
			nResult = pSelf->Pick( (OrthographicCamera*)pCamera, jX, jY, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->Pick( (PerspectiveCamera*)pCamera, jX, jY, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case PROJECTION_CAMERA_TYPE_ID: 
			nResult = pSelf->Pick( (ProjectionCamera*)pCamera, jX, jY, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->Pick( (SymmetricPerspectiveCamera*)pCamera, jX, jY, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		default:
			nResult = JNIERR_TYPE_ID_NOT_FOUND;
		}

		jint* pnVisual = pEnv->GetIntArrayElements( jAtVisual, NULL );
		jboolean* pbIsFrontFacingAppearance = pEnv->GetBooleanArrayElements( jAtIsFrontFacingAppearance, NULL );
		jint* pnSubElement = pEnv->GetIntArrayElements( jAtSubElement, NULL );
		jdouble* pfZ = pEnv->GetDoubleArrayElements( jAtZ, NULL );

		*pnVisual = (jint)pVisual;
		if( bIsFrontFacingAppearance ) {
			*pbIsFrontFacingAppearance = JNI_TRUE;
		} else {
			*pbIsFrontFacingAppearance = JNI_FALSE;
		}
		*pnSubElement = nSubElement;
		*pfZ = fZ;

		pEnv->ReleaseIntArrayElements( jAtVisual, pnVisual, 0 );
		pEnv->ReleaseBooleanArrayElements( jAtIsFrontFacingAppearance, pbIsFrontFacingAppearance, 0 );
		pEnv->ReleaseIntArrayElements( jAtSubElement, pnSubElement, 0 );
		pEnv->ReleaseDoubleArrayElements( jAtZ, pfZ, 0 );

		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_blt
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_blt
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	if( jSelf ) {
		RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
			int nResult = pSelf->Blt( pTextureMap );
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_onDisplayDriverChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_onDisplayDriverChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jDisplayDriver )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		DisplayDriver* pDisplayDriver = (DisplayDriver*)JNI_GetNativeInstance( pEnv, jDisplayDriver );
		int nResult = pSelf->SetDisplayDriver( pDisplayDriver );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_onDisplayDeviceChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_onDisplayDeviceChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jDisplayDevice )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		DisplayDevice* pDisplayDevice = (DisplayDevice*)JNI_GetNativeInstance( pEnv, jDisplayDevice );
		int nResult = pSelf->SetDesiredDisplayDevice( pDisplayDevice );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT jboolean JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_isLetterboxedAsOpposedToDistorted
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_isLetterboxedAsOpposedToDistorted
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	bool bIsLetterboxedAsOpposedToDistorted = true;
	int nResult;
	if( pSelf ) {
		Camera* pCamera = (Camera*)JNI_GetNativeInstance( pEnv, jCamera );
		int nCameraTypeID = JNI_GetIntFieldNamed( pEnv, jCamera, "m_nativeTypeID" );
		int nResult;
		switch( nCameraTypeID ) {
		case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
			nResult = pSelf->GetIsLetterboxedAsOpposedToDistorted( (OrthographicCamera*)pCamera, bIsLetterboxedAsOpposedToDistorted );
			break;
		case PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->GetIsLetterboxedAsOpposedToDistorted( (PerspectiveCamera*)pCamera, bIsLetterboxedAsOpposedToDistorted );
			break;
		case PROJECTION_CAMERA_TYPE_ID: 
			nResult = pSelf->GetIsLetterboxedAsOpposedToDistorted( (ProjectionCamera*)pCamera, bIsLetterboxedAsOpposedToDistorted );
			break;
		case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->GetIsLetterboxedAsOpposedToDistorted( (SymmetricPerspectiveCamera*)pCamera, bIsLetterboxedAsOpposedToDistorted );
			break;
		default:
			nResult = JNIERR_TYPE_ID_NOT_FOUND;
		}
	} else {
		nResult = JNIERR_NULL_NATIVE_INSTANCE;
	}
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return bIsLetterboxedAsOpposedToDistorted==JNI_TRUE;
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_setIsLetterboxedAsOpposedToDistorted
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_setIsLetterboxedAsOpposedToDistorted
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera, jboolean jIsLetterboxedAsOpposedToDistorted )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	int nResult;
	if( pSelf ) {
		Camera* pCamera = (Camera*)JNI_GetNativeInstance( pEnv, jCamera );
		int nCameraTypeID = JNI_GetIntFieldNamed( pEnv, jCamera, "m_nativeTypeID" );
		bool bIsLetterboxedAsOpposedToDistorted = jIsLetterboxedAsOpposedToDistorted==JNI_TRUE;
		switch( nCameraTypeID ) {
		case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
			nResult = pSelf->SetIsLetterboxedAsOpposedToDistorted( (OrthographicCamera*)pCamera, bIsLetterboxedAsOpposedToDistorted );
			break;
		case PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->SetIsLetterboxedAsOpposedToDistorted( (PerspectiveCamera*)pCamera, bIsLetterboxedAsOpposedToDistorted );
			break;
		case PROJECTION_CAMERA_TYPE_ID: 
			nResult = pSelf->SetIsLetterboxedAsOpposedToDistorted( (ProjectionCamera*)pCamera, bIsLetterboxedAsOpposedToDistorted );
			break;
		case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->SetIsLetterboxedAsOpposedToDistorted( (SymmetricPerspectiveCamera*)pCamera, bIsLetterboxedAsOpposedToDistorted );
			break;
		default:
			nResult = JNIERR_TYPE_ID_NOT_FOUND;
		}
	} else {
		nResult = JNIERR_NULL_NATIVE_INSTANCE;
	}
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_onViewportChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_onViewportChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera, jint jX, jint jY, jint jWidth, jint jHeight )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		Camera* pCamera = (Camera*)JNI_GetNativeInstance( pEnv, jCamera );
		int nCameraTypeID = JNI_GetIntFieldNamed( pEnv, jCamera, "m_nativeTypeID" );
		int nResult;
		switch( nCameraTypeID ) {
		case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
			nResult = pSelf->OnViewportChange( (OrthographicCamera*)pCamera, jX, jY, jWidth, jHeight );
			break;
		case PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->OnViewportChange( (PerspectiveCamera*)pCamera, jX, jY, jWidth, jHeight );
			break;
		case PROJECTION_CAMERA_TYPE_ID: 
			nResult = pSelf->OnViewportChange( (ProjectionCamera*)pCamera, jX, jY, jWidth, jHeight );
			break;
		case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->OnViewportChange( (SymmetricPerspectiveCamera*)pCamera, jX, jY, jWidth, jHeight );
			break;
		default:
			nResult = JNIERR_TYPE_ID_NOT_FOUND;
		}
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT jdoubleArray JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getActualPlane__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_OrthographicCameraProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getActualPlane__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_OrthographicCameraProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jOrthographicCamera )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	int nResult;
	if( pSelf ) {
		jdoubleArray jPlane = pEnv->NewDoubleArray( 4 );
		if( jPlane ) {
			jdouble* vfPlane = pEnv->GetDoubleArrayElements( jPlane, NULL );
			OrthographicCamera* pOrthographicCamera = (OrthographicCamera*)JNI_GetNativeInstance( pEnv, jOrthographicCamera );
			nResult = pSelf->GetActualPlane( pOrthographicCamera, vfPlane );
			pEnv->ReleaseDoubleArrayElements( jPlane, vfPlane, 0 );
		}
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
			return NULL;
		} else {
			return jPlane;
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		return NULL;
	}
}

JNIEXPORT jdoubleArray JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getActualPlane__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_PerspectiveCameraProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getActualPlane__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_PerspectiveCameraProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jPerspectiveCamera )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	int nResult;
	if( pSelf ) {
		jdoubleArray jPlane = pEnv->NewDoubleArray( 4 );
		if( jPlane ) {
			jdouble* vfPlane = pEnv->GetDoubleArrayElements( jPlane, NULL );
			PerspectiveCamera* pPerspectiveCamera = (PerspectiveCamera*)JNI_GetNativeInstance( pEnv, jPerspectiveCamera );
			nResult = pSelf->GetActualPlane( pPerspectiveCamera, vfPlane );
			pEnv->ReleaseDoubleArrayElements( jPlane, vfPlane, 0 );
		}
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
			return NULL;
		} else {
			return jPlane;
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		return NULL;
	}
}

JNIEXPORT jdouble JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getActualHorizontalViewingAngle
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getActualHorizontalViewingAngle
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jSymmetricPerspectiveCamera )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	jdouble jActualHorizontalViewingAngle = 0;
	int nResult;
	if( pSelf ) {
		SymmetricPerspectiveCamera* pSymmetricPerspectiveCamera = (SymmetricPerspectiveCamera*)JNI_GetNativeInstance( pEnv, jSymmetricPerspectiveCamera );
		nResult = pSelf->GetActualHorizontalViewingAngle( pSymmetricPerspectiveCamera, jActualHorizontalViewingAngle );
	} else {
		nResult = JNIERR_NULL_NATIVE_INSTANCE;
	}
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return jActualHorizontalViewingAngle;
}

JNIEXPORT jdouble JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getActualVerticalViewingAngle
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getActualVerticalViewingAngle
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jSymmetricPerspectiveCamera )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	jdouble jActualVerticalViewingAngle = 0;
	int nResult;
	if( pSelf ) {
		SymmetricPerspectiveCamera* pSymmetricPerspectiveCamera = (SymmetricPerspectiveCamera*)JNI_GetNativeInstance( pEnv, jSymmetricPerspectiveCamera );
		nResult = pSelf->GetActualVerticalViewingAngle( pSymmetricPerspectiveCamera, jActualVerticalViewingAngle );
	} else {
		nResult = JNIERR_NULL_NATIVE_INSTANCE;
	}
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return jActualVerticalViewingAngle;
}

JNIEXPORT jobject JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getActualViewport
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getActualViewport
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	jint jX, jY, jWidth, jHeight = 0;
	int nResult;
	if( pSelf ) {
		Camera* pCamera = (Camera*)JNI_GetNativeInstance( pEnv, jCamera );
		int nCameraTypeID = JNI_GetIntFieldNamed( pEnv, jCamera, "m_nativeTypeID" );
		switch( nCameraTypeID ) {
		case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
			nResult = pSelf->GetActualViewport( (OrthographicCamera*)pCamera, jX, jY, jWidth, jHeight );
			break;
		case PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->GetActualViewport( (PerspectiveCamera*)pCamera, jX, jY, jWidth, jHeight );
			break;
		case PROJECTION_CAMERA_TYPE_ID: 
			nResult = pSelf->GetActualViewport( (ProjectionCamera*)pCamera, jX, jY, jWidth, jHeight );
			break;
		case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->GetActualViewport( (SymmetricPerspectiveCamera*)pCamera, jX, jY, jWidth, jHeight );
			break;
		default:
			nResult = JNIERR_TYPE_ID_NOT_FOUND;
		}
	} else {
		nResult = JNIERR_NULL_NATIVE_INSTANCE;
	}
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
		return NULL;
	} else {
		return JNI_NewObject( pEnv, "java/awt/Rectangle", "(IIII)V", jX, jY, jWidth, jHeight );
	}
}

JNIEXPORT jobject JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getProjectionMatrix
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getProjectionMatrix
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	jdouble rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 = 0;
	int nResult;
	if( pSelf ) {
		Camera* pCamera = (Camera*)JNI_GetNativeInstance( pEnv, jCamera );
		int nCameraTypeID = JNI_GetIntFieldNamed( pEnv, jCamera, "m_nativeTypeID" );
		switch( nCameraTypeID ) {
		case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
			nResult = pSelf->GetProjectionMatrix( (OrthographicCamera*)pCamera, rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 );;
			break;
		case PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->GetProjectionMatrix( (PerspectiveCamera*)pCamera, rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 );;
			break;
		case PROJECTION_CAMERA_TYPE_ID: 
			nResult = pSelf->GetProjectionMatrix( (ProjectionCamera*)pCamera, rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 );;
			break;
		case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->GetProjectionMatrix( (SymmetricPerspectiveCamera*)pCamera, rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 );;
			break;
		default:
			nResult = JNIERR_TYPE_ID_NOT_FOUND;
		}
	} else {
		nResult = JNIERR_NULL_NATIVE_INSTANCE;
	}
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
		return NULL;
	} else {
		return JNI_NewObject( pEnv, "edu/cmu/cs/stage3/math/Matrix44", "(DDDDDDDDDDDDDDDD)V", rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 );
	}
}

JNIEXPORT jboolean JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_rendersOnEdgeTrianglesAsLines
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_rendersOnEdgeTrianglesAsLines
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jOrthographicCamera )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	OrthographicCamera* pOrthographicCamera = (OrthographicCamera*)JNI_GetNativeInstance( pEnv, jOrthographicCamera );
	bool bRendersOnEdgeTrianglesAsLines;
	int nResult = pSelf->GetRendersOnEdgeTrianglesAsLines( pOrthographicCamera, bRendersOnEdgeTrianglesAsLines );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
		return JNI_FALSE;
	} else {
		if( bRendersOnEdgeTrianglesAsLines ) {
			return JNI_TRUE;
		} else {
			return JNI_FALSE;
		}
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_setRendersOnEdgeTrianglesAsLines
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_setRendersOnEdgeTrianglesAsLines
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jOrthographicCamera, jboolean jRendersOnEdgeTrianglesAsLines )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	OrthographicCamera* pOrthographicCamera = (OrthographicCamera*)JNI_GetNativeInstance( pEnv, jOrthographicCamera );
	int nResult = pSelf->SetRendersOnEdgeTrianglesAsLines( pOrthographicCamera, jRendersOnEdgeTrianglesAsLines==JNI_TRUE );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_setDesiredSize
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_setDesiredSize
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint jWidth, jint jHeight )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->SetDesiredSize( jWidth, jHeight );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getWidth__
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getWidth__
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nWidth = 0;
	if( pSelf ) {
		int nResult = pSelf->GetWidth( nWidth );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nWidth;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getHeight__
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getHeight__
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nHeight = 0;
	if( pSelf ) {
		int nResult = pSelf->GetHeight( nHeight );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nHeight;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getPitch__
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getPitch__
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nPitch = 0;
	if( pSelf ) {
		int nResult = pSelf->GetPitch( nPitch );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nPitch;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getBitCount__
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getBitCount__
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nBitCount = 0;
	if( pSelf ) {
		int nResult = pSelf->GetBitCount( nBitCount );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nBitCount;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getRedBitMask__
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getRedBitMask__
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nRedBitMask = 0;
	if( pSelf ) {
		int nResult = pSelf->GetRedBitMask( nRedBitMask );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nRedBitMask;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getGreenBitMask__
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getGreenBitMask__
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nGreenBitMask = 0;
	if( pSelf ) {
		int nResult = pSelf->GetGreenBitMask( nGreenBitMask );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nGreenBitMask;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getBlueBitMask__
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getBlueBitMask__
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nBlueBitMask = 0;
	if( pSelf ) {
		int nResult = pSelf->GetBlueBitMask( nBlueBitMask );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nBlueBitMask;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getAlphaBitMask__
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getAlphaBitMask__
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nAlphaBitMask = 0;
	if( pSelf ) {
		int nResult = pSelf->GetAlphaBitMask( nAlphaBitMask );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nAlphaBitMask;
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getPixels__IIIIIIIIII_3I
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getPixels__IIIIIIIIII_3I
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint jX, jint jY, jint jWidth, jint jHeight, jint jPitch, jint jBitCount, jint jRedBitMask, jint jGreenBitMask, jint jBlueBitMask, jint jAlphaBitMask, jintArray jPixels )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		if( jPixels ) {
			jint* vnPixels = pEnv->GetIntArrayElements( jPixels, NULL );
			int nResult = pSelf->GetPixels( jX, jY, jWidth, jHeight, jPitch, jBitCount, jRedBitMask, jGreenBitMask, jBlueBitMask, jAlphaBitMask, vnPixels );
			pEnv->ReleaseIntArrayElements( jPixels, vnPixels, 0 );
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			//todo
			//JNI_ThrowNewException( pEnv, JNIERR_NULL );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}


JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getWidth__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getWidth__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nWidth = 0;
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pSelf->GetWidth( pTextureMap, nWidth );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nWidth;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getHeight__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getHeight__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nHeight = 0;
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pSelf->GetHeight( pTextureMap, nHeight );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nHeight;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getPitch__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getPitch__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nPitch = 0;
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pSelf->GetPitch( pTextureMap, nPitch );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nPitch;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getBitCount__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getBitCount__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nBitCount = 0;
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pSelf->GetBitCount( pTextureMap, nBitCount );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nBitCount;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getRedBitMask__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getRedBitMask__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nRedBitMask = 0;
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pSelf->GetRedBitMask( pTextureMap, nRedBitMask );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nRedBitMask;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getGreenBitMask__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getGreenBitMask__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nGreenBitMask = 0;
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pSelf->GetGreenBitMask( pTextureMap, nGreenBitMask );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nGreenBitMask;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getBlueBitMask__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getBlueBitMask__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nBlueBitMask = 0;
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pSelf->GetBlueBitMask( pTextureMap, nBlueBitMask );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nBlueBitMask;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getAlphaBitMask__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getAlphaBitMask__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nAlphaBitMask = 0;
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		int nResult = pSelf->GetAlphaBitMask( pTextureMap, nAlphaBitMask );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nAlphaBitMask;
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getPixels__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2IIIIIIIIII_3I
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getPixels__Ledu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_TextureMapProxy_2IIIIIIIIII_3I
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jTextureMap, jint jX, jint jY, jint jWidth, jint jHeight, jint jPitch, jint jBitCount, jint jRedBitMask, jint jGreenBitMask, jint jBlueBitMask, jint jAlphaBitMask, jintArray jPixels )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		TextureMap* pTextureMap = (TextureMap*)JNI_GetNativeInstance( pEnv, jTextureMap );
		if( jPixels ) {
			jint* vnPixels = pEnv->GetIntArrayElements( jPixels, NULL );
			int nResult = pSelf->GetPixels( pTextureMap, jX, jY, jWidth, jHeight, jPitch, jBitCount, jRedBitMask, jGreenBitMask, jBlueBitMask, jAlphaBitMask, vnPixels );
			pEnv->ReleaseIntArrayElements( jPixels, vnPixels, 0 );
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			//todo
			//JNI_ThrowNewException( pEnv, JNIERR_NULL );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getZBufferPitch
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getZBufferPitch
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nZBufferPitch = 0;
	if( pSelf ) {
		int nResult = pSelf->GetZBufferPitch( nZBufferPitch );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nZBufferPitch;
}

JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getZBufferBitCount
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getZBufferBitCount
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	long nZBufferBitCount = 0;
	if( pSelf ) {
		int nResult = pSelf->GetZBufferBitCount( nZBufferBitCount );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return nZBufferBitCount;
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getZBufferPixels
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getZBufferPixels
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint jX, jint jY, jint jWidth, jint jHeight, jint jZBufferPitch, jint jZBufferBitCount, jintArray jZBufferPixels )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		if( jZBufferPixels ) {
			jint* vnZBufferPixels = pEnv->GetIntArrayElements( jZBufferPixels, NULL );
			int nResult = pSelf->GetZBufferPixels( jX, jY, jWidth, jHeight, jZBufferPitch, jZBufferBitCount, vnZBufferPixels );
			pEnv->ReleaseIntArrayElements( jZBufferPixels, vnZBufferPixels, 0 );
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			//todo
			//JNI_ThrowNewException( pEnv, JNIERR_NULL );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_setSilhouetteThickness
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_setSilhouetteThickness
#endif
  ( JNIEnv* pEnv, jobject jSelf, jdouble fSilhouetteThickness )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult = pSelf->SetSilhouetteThickness( fSilhouetteThickness );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

JNIEXPORT jdouble JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_getSilhouetteThickness
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_getSilhouetteThickness
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
	jdouble fSilhouetteThickness = 0;
	if( pSelf ) {
		int nResult = pSelf->GetSilhouetteThickness( fSilhouetteThickness );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
	return fSilhouetteThickness;
}

/*
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_RenderTargetAdapter_HACK_doorTrick
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_RenderTargetAdapter_HACK_doorTrick
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jCamera,  jobjectArray jDoorPositions, jobject jOutside, jobjectArray jVisuals )
{
	if( jSelf && jCamera && jOutside ) {
		RenderTarget* pSelf = (RenderTarget*)JNI_GetNativeInstance( pEnv, jSelf );
		if( pSelf ) {
			SymmetricPerspectiveCamera* pCamera = (SymmetricPerspectiveCamera*)JNI_GetNativeInstance( pEnv, jCamera );
			Scene* pOutside = (Scene*)JNI_GetNativeInstance( pEnv, jOutside );

			jint nVisualCount = 0;
			if( jVisuals ) {
				nVisualCount = pEnv->GetArrayLength( jVisuals );
			}

			_Visual** vpVisuals = NULL;;
			if( nVisualCount ) {
				vpVisuals = new _Visual*[ nVisualCount ];
				for( int i=0; i<nVisualCount; i++ ) {
					jobject jVisual = pEnv->GetObjectArrayElement( jVisuals, i );
					vpVisuals[ i ] = (_Visual*)JNI_GetNativeInstance( pEnv, jVisual );
				}
			}

			double vfDoor[ 4 ][ 3 ];
			for( int i=0; i<4; i++ ) {
				jobject jPosition = pEnv->GetObjectArrayElement( jDoorPositions, i );
				vfDoor[ i ][ 0 ] = JNI_GetDoubleFieldNamed( pEnv, jPosition, "x" );
				vfDoor[ i ][ 1 ] = JNI_GetDoubleFieldNamed( pEnv, jPosition, "y" );
				vfDoor[ i ][ 2 ] = JNI_GetDoubleFieldNamed( pEnv, jPosition, "z" );
			}
			int nResult = pSelf->HACK_DoorTrick( pCamera, vfDoor, pOutside, vpVisuals, nVisualCount );
			if( nResult<0 ) {
				JNI_ThrowNewException( pEnv, nResult );
			}
		} else {
			JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
		}
	}
}
*/

} //extern "C"
