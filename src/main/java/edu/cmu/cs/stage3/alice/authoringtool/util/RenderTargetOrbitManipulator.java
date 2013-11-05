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


package edu.cmu.cs.stage3.alice.authoringtool.util;

public class RenderTargetOrbitManipulator extends RenderTargetPickManipulator {
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable helper = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
	protected edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable eCameraTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgCameraTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Scene sgScene = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgIdentity = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
	protected javax.vecmath.Vector3d tempVec = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector4d tempVec4 = new javax.vecmath.Vector4d();
	protected edu.cmu.cs.stage3.math.Matrix44 oldTransformation;
	protected double orbitRotationFactor;
	protected double orbitZoomFactor;
	protected double sizeFactor;
	protected boolean clippingPlaneAdjustmentEnabled = false;

	protected UndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;

	private Configuration orbitConfig = Configuration.getLocalConfiguration( RenderTargetOrbitManipulator.class.getPackage() );

	public RenderTargetOrbitManipulator( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget, UndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler ) {
		super( renderTarget );
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
		helper.setName( "helper" );
		configInit();
	}

	public void setClippingPlaneAdjustmentEnabled( boolean enabled ) {
		clippingPlaneAdjustmentEnabled = enabled;
	}

	private void configInit() {
		if( orbitConfig.getValue( "renderTargetOrbitManipulator.orbitRotationFactor" ) == null ) {
			orbitConfig.setValue( "renderTargetOrbitManipulator.orbitRotationFactor", Double.toString( .02 ) );
		}
		if( orbitConfig.getValue( "renderTargetOrbitManipulator.orbitZoomFactor" ) == null ) {
			orbitConfig.setValue( "renderTargetOrbitManipulator.orbitZoomFactor", Double.toString( .05 ) );
		}
	}

	public void mousePressed( java.awt.event.MouseEvent ev ) {
		//DEBUG System.out.println( "mousePressed" );
		if( enabled ) {
			super.mousePressed( ev );

			orbitRotationFactor = Double.parseDouble( orbitConfig.getValue( "renderTargetOrbitManipulator.orbitRotationFactor" ) );
			orbitZoomFactor = Double.parseDouble( orbitConfig.getValue( "renderTargetOrbitManipulator.orbitZoomFactor" ) );

			if( (sgPickedTransformable == null) && (objectsOfInterest.size() == 1) ) {
				ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)objectsOfInterest.iterator().next();
				sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().setIsListening( false );
				mouseIsDown = true;
			}

			if( sgPickedTransformable != null ) {
				sizeFactor = Math.max( .1, ePickedTransformable.getBoundingSpher