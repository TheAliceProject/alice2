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

#include "Visual.hpp"
#include "VertexGeometry.hpp"
#include "IndexedTriangleArray.hpp"
#include "Text.hpp"
#include "LineArray.hpp"
#include "LineStrip.hpp"
#include "PointArray.hpp"
#include "TriangleArray.hpp"
#include "TriangleFan.hpp"
#include "TriangleStrip.hpp"
#include "Sprite.hpp"
#include "Box.hpp"
#include "Cylinder.hpp"
#include "Sphere.hpp"
#include "Torus.hpp"
#include "AmbientLight.hpp"
#include "ClippingPlane.hpp"
#include "DirectionalLight.hpp"
#include "ExponentialFog.hpp"
#include "ExponentialSquaredFog.hpp"
#include "LinearFog.hpp"
#include "PointLight.hpp"
#include "SpotLight.hpp"
#include "UtilJNI.hpp"

extern "C" {

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_VisualProxy_onGeometryChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_VisualProxy_onGeometryChange
#endif
( JNIEnv* pEnv, jobject jSelf, jobject jGeometry ) 
{
	_Visual* pSelf = (_Visual*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		int nResult;
		if( jGeometry ) {
			void* pGeometry = JNI_GetNativeInstance( pEnv, jGeometry );
			int nGeometryTypeID = JNI_GetIntFieldNamed( pEnv, jGeometry, "m_nativeTypeID" );
			switch( nGeometryTypeID ) {
			case INDEXED_TRIANGLE_ARRAY_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (IndexedTriangleArray*)pGeometry );
				break;
			case LINE_ARRAY_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (LineArray*)pGeometry );
				break;
			case LINE_STRIP_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (LineStrip*)pGeometry );
				break;
			case POINT_ARRAY_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (PointArray*)pGeometry );
				break;
			case TRIANGLE_ARRAY_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (TriangleArray*)pGeometry );
				break;
			case TRIANGLE_FAN_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (TriangleFan*)pGeometry );
				break;
			case TRIANGLE_STRIP_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (TriangleStrip*)pGeometry );
				break;
			case TEXT_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (Text*)pGeometry );
				break;
			case SPRITE_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (Sprite*)pGeometry );
				break;
			case BOX_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (Box*)pGeometry );
				break;
			case CYLINDER_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (Cylinder*)pGeometry );
				break;
			case SPHERE_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (Sphere*)pGeometry );
				break;
			case TORUS_TYPE_ID: 
				nResult = pSelf->OnGeometryChange( (Torus*)pGeometry );
				break;
			default:
				nResult = JNIERR_TYPE_ID_NOT_FOUND;
			}
		} else {
			nResult = pSelf->OnGeometryChange( NULL );
		}
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

/*
 * Class:     edu_cmu_cs_stage3_alice_scenegraph_renderer_nativerenderer_VisualProxy
 * Method:    onDisabledAffectorsChange
 * Signature: ([Ledu/cmu/cs/stage3/alice/scenegraph/renderer/nativerenderer/AffectorProxy;)V
 */
JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_VisualProxy_onDisabledAffectorsChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_VisualProxy_onDisabledAffectorsChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobjectArray jDisabledAffectors ) 
{
	_Visual* pSelf = (_Visual*)JNI_GetNativeInstance( pEnv, jSelf );
	int nResult;
	if( jDisabledAffectors ) {
		jint nDisabledAffectorCount = pEnv->GetArrayLength( jDisabledAffectors );
		Affector** vpDisabledAffectors = new Affector*[ nDisabledAffectorCount ];
		for( int i=0; i<nDisabledAffectorCount; i++ ) {
			jobject jDisabledAffector = pEnv->GetObjectArrayElement( jDisabledAffectors, i );
			int nDisabledAffectorTypeID = JNI_GetIntFieldNamed( pEnv, jDisabledAffector, "m_nativeTypeID" );
			switch( nDisabledAffectorTypeID ) {
			case AMBIENT_LIGHT_TYPE_ID: 
				vpDisabledAffectors[i] = (AmbientLight*)JNI_GetNativeInstance( pEnv, jDisabledAffector );
				break;
			case CLIPPING_PLANE_TYPE_ID: 
				vpDisabledAffectors[i] = (ClippingPlane*)JNI_GetNativeInstance( pEnv, jDisabledAffector );
				break;
			case DIRECTIONAL_LIGHT_TYPE_ID: 
				vpDisabledAffectors[i] = (DirectionalLight*)JNI_GetNativeInstance( pEnv, jDisabledAffector );
				break;
			case EXPONENTIAL_FOG_TYPE_ID: 
				vpDisabledAffectors[i] = (ExponentialFog*)JNI_GetNativeInstance( pEnv, jDisabledAffector );
				break;
			case EXPONENTIAL_SQUARED_FOG_TYPE_ID: 
				vpDisabledAffectors[i] = (ExponentialSquaredFog*)JNI_GetNativeInstance( pEnv, jDisabledAffector );
				break;
			case LINEAR_FOG_TYPE_ID: 
				vpDisabledAffectors[i] = (LinearFog*)JNI_GetNativeInstance( pEnv, jDisabledAffector );
				break;
			case POINT_LIGHT_TYPE_ID: 
				vpDisabledAffectors[i] = (PointLight*)JNI_GetNativeInstance( pEnv, jDisabledAffector );
				break;
			case SPOT_LIGHT_TYPE_ID: 
				vpDisabledAffectors[i] = (SpotLight*)JNI_GetNativeInstance( pEnv, jDisabledAffector );
				break;
			default:
				JNI_ThrowNewException( pEnv, JNIERR_TYPE_ID_NOT_FOUND );
				return;
			}
		}
		nResult = pSelf->OnDisabledAffectorsChange( vpDisabledAffectors, nDisabledAffectorCount );
	} else {
		nResult = pSelf->OnDisabledAffectorsChange( NULL, 0 );
	}
	if( nResult<0 ) {
		JNI_ThrowNewException( pEnv, nResult );
	}
}

JNIEXPORT void JNICALL 
#if defined DX7_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_directx7renderer_VisualProxy_onScaleChange
#elif defined OPENGL_RENDERER
	Java_edu_cmu_cs_stage3_alice_scenegraph_renderer_openglrenderer_VisualProxy_onScaleChange
#endif
  ( JNIEnv* pEnv, jobject jSelf, jobject jScale ) 
{
	_Visual* pSelf = (_Visual*)JNI_GetNativeInstance( pEnv, jSelf );
	if( pSelf ) {
		double rc00 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m00" );
		double rc01 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m01" );
		double rc02 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m02" );
		double rc10 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m10" );
		double rc11 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m11" );
		double rc12 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m12" );
		double rc20 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m20" );
		double rc21 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m21" );
		double rc22 = JNI_GetDoubleFieldNamed( pEnv, jScale, "m22" );
		int nResult = pSelf->OnScaleChange( rc00, rc01, rc02, rc10, rc11, rc12, rc20, rc21, rc22 );
		if( nResult<0 ) {
			JNI_ThrowNewException( pEnv, nResult );
		}
	} else {
		JNI_ThrowNewException( pEnv, JNIERR_NULL_NATIVE_INSTANCE );
	}
}

} //extern "C"