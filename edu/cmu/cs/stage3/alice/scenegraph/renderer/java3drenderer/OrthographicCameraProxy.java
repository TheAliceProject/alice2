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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer;

class OrthographicCameraProxy extends CameraProxy {
	protected int getProjectionPolicy() {
		return javax.media.j3d.View.PARALLEL_PROJECTION;
	}
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera.PLANE_PROPERTY ) {
			double[] plane = (double[])value;
			edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera sgCamera = (edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera)getSceneGraphElement();
			double left   = plane[ 0 ];
			double right  = plane[ 2 ];
			double top    = plane[ 1 ];
			double bottom = plane[ 3 ];
			double near   = sgCamera.getNearClippingPlaneDistance();
			double far    = sgCamera.getFarClippingPlaneDistance();
			if( Double.isNaN( left ) && Double.isNaN( right ) ) {
				double ratio = 4 / (double)3;
				left = ratio * top;
				right = ratio * bottom;
			} else if( Double.isNaN( top ) && Double.isNaN( bottom ) ) {
				double ratio = 3 / (double)4;
				top = ratio * left;
				bottom = ratio * right;
			} else {
				//todo: use screen dimensions
			}
			javax.media.j3d.Transform3D m = new javax.media.j3d.Transform3D();
			m.ortho( left, right, top, bottom, near, far );
			updateProjection( m );
		} else {
			super.changed( property, value );
		}
	}
}
