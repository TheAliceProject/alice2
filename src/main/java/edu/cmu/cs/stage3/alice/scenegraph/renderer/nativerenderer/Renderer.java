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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class Renderer extends edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer {
	static {
		//java.awt.Frame f = new java.awt.Frame();
		//f.show();
		//f = null;
		try {
			System.loadLibrary( "jawt" );
		} catch( UnsatisfiedLinkError ule ) {
			//pass
		}
	}
	private int m_nativeInstance = 0;

	protected abstract void createNativeInstance();
	protected abstract void releaseNativeInstance();

	protected abstract void pick( ComponentProxy componentProxy, double x, double y, double z, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired, int[] atVisual, boolean[] atIsFrontFacing, int[] atSubElement, double[] atZ );

    protected abstract RenderTargetAdapter createRenderTargetAdapter( RenderTarget renderTarget );
    protected abstract RenderCanvas createRenderCanvas( OnscreenRenderTarget onscreenRenderTarget );

    
	protected abstract boolean requiresHierarchyAndAbsoluteTransformationListening();
    
	protected abstract boolean requiresBoundListening();

	public Renderer() {
        super();
		createNativeInstance();
	}

	protected abstract void internalSetIsSoftwareEmulationForced( boolean isSoftwareEmulationForced );
	
	public void setIsSoftwareEmulationForced( boolean isSoftwareEmulationForced ) {
		super.setIsSoftwareEmulationForced( isSoftwareEmulationForced );
		internalSetIsSoftwareEmulationForced( isSoftwareEmulationForced );
	}

    public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick( edu.cmu.cs.stage3.alice.scenegraph.Component sgComponent, javax.vecmath.Vector3d v, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired ) {
        commitAnyPendingChanges();
        ComponentProxy componentProxy = (ComponentProxy)getProxyFor( sgComponent );
        int[] atVisual = { 0 };
        boolean[] atIsFrontFacing = { true };
        int[] atSubElement = { -1 };
        double[] atZ = { Double.NaN };

        pick( componentProxy, v.x, v.y, v.z, planeMinX, planeMinY, planeMaxX, planeMaxY, nearClippingPlaneDistance, farClippingPlaneDistance, isSubElementRequired, isOnlyFrontMostRequired, atVisual, atIsFrontFacing, atSubElement, atZ );

        edu.cmu.cs.stage3.alice.scenegraph.Visual[] sgVisuals = null;
        edu.cmu.cs.stage3.alice.scenegraph.Geometry[] sgGeometries = null;
        int[] subElements = null;
        boolean[] isFrontFacings = null;
        VisualProxy visualProxy = VisualProxy.map( atVisual[ 0 ] );
        if( visualProxy!=null ) {
            sgVisuals = new edu.cmu.cs.stage3.alice.scenegraph.Visual[ 1 ];
            sgVisuals[0] = (edu.cmu.cs.stage3.alice.scenegraph.Visual)visualProxy.getSceneGraphElement();
            sgGeometries = new edu.cmu.cs.stage3.alice.scenegraph.Geometry[ 1 ];
            sgGeometries[0] = sgVisu