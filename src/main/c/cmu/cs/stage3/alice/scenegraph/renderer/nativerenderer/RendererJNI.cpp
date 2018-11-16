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

#include "Renderer.hpp"
#include "Component.hpp"
#include "AmbientLight.hpp"
#include "ClippingPlane.hpp"
#include "DirectionalLight.hpp"
#include "ExponentialFog.hpp"
#include "LinearFog.hpp"
#include "OrthographicCamera.hpp"
#include "PerspectiveCamera.hpp"
#include "ProjectionCamera.hpp"
#include "SymmetricPerspectiveCamera.hpp"
#include "PointLight.hpp"
#include "SpotLight.hpp"
#include "Transformable.hpp"
#include "Visual.hpp"
#include "UtilJNI.hpp"

extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Renderer_createNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Renderer_createNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	Renderer* pRenderer = new Renderer();
	JNI_SetNativeInstance( pEnv, jSelf, pRenderer );
	int nResult = pRenderer->Enumerate();
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Renderer
 * Method:    releaseNativeInstance
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Renderer_releaseNativeInstance
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Renderer_releaseNativeInstance
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	Renderer* pRenderer = (Renderer*)JNI_GetNativeInstance( pEnv, jSelf );
	delete pRenderer;
	JNI_SetNativeInstance( pEnv, jSelf, NULL );
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Renderer
 * Method:    getDisplayDriverCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Renderer_getDisplayDriverCount
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Renderer_getDisplayDriverCount
#endif
  ( JNIEnv* pEnv, jobject jSelf )
{
	Renderer* pRenderer = (Renderer*)JNI_GetNativeInstance( pEnv, jSelf );
	jint nDisplayDriverCount;
	int nResult = pRenderer->GetDisplayDriverCount( nDisplayDriverCount );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return nDisplayDriverCount;
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Renderer
 * Method:    getDisplayDriverNativeInstanceAt
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Renderer_getDisplayDriverNativeInstanceAt
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Renderer_getDisplayDriverNativeInstanceAt
#endif
  ( JNIEnv* pEnv, jobject jSelf, jint index )
{
	Renderer* pRenderer = (Renderer*)JNI_GetNativeInstance( pEnv, jSelf );
	DisplayDriver* pDisplayDriver;
	int nResult = pRenderer->GetDisplayDriverAt( index, pDisplayDriver );
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
	return (jint)pDisplayDriver;
}
  
/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_Renderer
 * Method:    pick
 * Signature: (Ledu/cmu/cs/stage3/alice/scenegraph/renderer/nativerenderer/ComponentProxy;DDDDDDDDDZZ[I[Z[I[D)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Renderer_pick
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Renderer_pick
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jComponent, jdouble jVectorX, jdouble jVectorY, jdouble jVectorZ, jdouble jPlaneMinX, jdouble jPlaneMinY, jdouble jPlaneMaxX, jdouble jPlaneMaxY, jdouble jNear, jdouble jFar, jboolean jIsSubElementRequired, jboolean jIsOnlyFrontMostRequired, jintArray jAtVisual, jbooleanArray jAtIsFrontFacingAppearance, jintArray jAtSubElement, jdoubleArray jAtZ )
{
	Renderer* pSelf = (Renderer*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		void* pComponent = JNI_GetNativeInstance( pEnv, jComponent );

		_Visual* pVisual;
		bool bIsFrontFacingAppearance;
		int nSubElement;
		double fZ;

		int nComponentTypeID = JNI_GetIntFieldNamed( pEnv, jComponent, "m_cppTypeID" );

		int nResult;
		switch( nComponentTypeID ) {
		case AMBIENT_LIGHT_TYPE_ID: 
			nResult = pSelf->Pick( (AmbientLight*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case CLIPPING_PLANE_TYPE_ID: 
			nResult = pSelf->Pick( (ClippingPlane*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case DIRECTIONAL_LIGHT_TYPE_ID: 
			nResult = pSelf->Pick( (DirectionalLight*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case EXPONENTIAL_FOG_TYPE_ID: 
			nResult = pSelf->Pick( (ExponentialFog*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case LINEAR_FOG_TYPE_ID: 
			nResult = pSelf->Pick( (LinearFog*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case ORTHOGRAPHIC_CAMERA_TYPE_ID: 
			nResult = pSelf->Pick( (OrthographicCamera*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->Pick( (PerspectiveCamera*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case PROJECTION_CAMERA_TYPE_ID: 
			nResult = pSelf->Pick( (ProjectionCamera*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID: 
			nResult = pSelf->Pick( (SymmetricPerspectiveCamera*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case POINT_LIGHT_TYPE_ID: 
			nResult = pSelf->Pick( (PointLight*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case SPOT_LIGHT_TYPE_ID: 
			nResult = pSelf->Pick( (SpotLight*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case TRANSFORMABLE_TYPE_ID: 
			nResult = pSelf->Pick( (Transformable*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		case VISUAL_TYPE_ID: 
			nResult = pSelf->Pick( (_Visual*)pComponent, jVectorX, jVectorY, jVectorZ, jPlaneMinX, jPlaneMinY, jPlaneMaxX, jPlaneMaxY, jNear, jFar, jIsSubElementRequired==JNI_TRUE, jIsOnlyFrontMostRequired==JNI_TRUE, pVisual, bIsFrontFacingAppearance, nSubElement, fZ );
			break;
		default:
			nResult = JNIERR_TYPE_ID_NOT_FOUND;
		}
		
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		} else {
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
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Renderer
 * Method:    internalSetIsSoftwareEmulationForced
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_Renderer_internalSetIsSoftwareEmulationForced
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_Renderer_internalSetIsSoftwareEmulationForced
#endif
  ( JNIEnv* pEnv, jobject jSelf, jboolean jIsSoftwareEmulationForced )
{
	Renderer* pSelf = (Renderer*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		pSelf->SetIsSoftwareEmulationForced( jIsSoftwareEmulationForced==JNI_TRUE );
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

} //extern "C"