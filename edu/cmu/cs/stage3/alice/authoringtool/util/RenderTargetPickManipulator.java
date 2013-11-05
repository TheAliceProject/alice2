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

public abstract class RenderTargetPickManipulator extends ScreenWrappingMouseListener {
	protected edu.cmu.cs.stage3.alice.core.Transformable ePickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable lastEPickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgPickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget = null;
	protected java.util.HashSet objectsOfInterest = new java.util.HashSet();
	protected java.util.HashSet listeners = new java.util.HashSet();
	protected java.awt.Cursor invisibleCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor( java.awt.Toolkit.getDefaultToolkit().getImage(""), new java.awt.Point( 0, 0 ), "invisible cursor" );
	protected java.awt.Cursor savedCursor = java.awt.Cursor.getDefaultCursor();
	protected java.awt.Point originalMousePoint;
	protected boolean hideCursorOnDrag = true;
	protected boolean popupEnabled = false;
	protected boolean enabled = true;
	protected boolean pickAllForOneObjectOfInterest = true;
	protected boolean ascendTreeEnabled = true;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo;

	public RenderTargetPickManipulator( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget ) {
		setRenderTarget( renderTarget );
	}

	public void setEnabled( boolean b ) {
		enabled = b;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setPickAllForOneObjectOfInterestEnabled( boolean b ) {
		pickAllForOneObjectOfInterest = b;
	}

	public boolean isPickAllForOneObjectOfInterestEnabled() {
		return pickAllForOneObjectOfInterest;
	}

	public void setRenderTarget( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget ) {
		if( this.renderTarget != null ) {
			this.renderTarget.getAWTComponent().removeMouseListener( this );
			if( popupEnabled ) {
				this.renderTarget.getAWTComponent().removeMouseListener( popupMouseListener );
			}
		}

		this.renderTarget = renderTarget;
		if( renderTarget != null ) {
			renderTarget.getAWTComponent().addMouseListener( this );
			if( popupEnabled ) {
				this.renderTarget.getAWTComponent().addMouseListener( popupMouseListener );
			}
		}
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public edu.cmu.cs.stage3.alice.core.Transformable getCorePickedTransformable() {
		return ePickedTransformable;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Transformable getSceneGraphPickedTransformable() {
		return sgPickedTransformable;
	}

	public void addRenderTargetPickManipulatorListener( edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener listener ) {
		if( listener != null ) {
			listeners.add( listener );
		}
	}

	public void removeRenderTargetPickManipulatorListener( edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener listener ) {
		if( listener != null ) {
			listeners.remove( listener );
		}
	}

	/**
	 * when objectsOfInterest is empty, all objects are of interest.
	 * when objectsOfInterest has one element, it will always be picked.
	 * when objectsOfInterest has more than one element, mouseDragged behavior will only occur if an objectOfInterest is picked
	 * only firstClass objects will be considered
	 */
	public boolean addObjectOfInterest( edu.cmu.cs.stage3.alice.core.Transformable trans ) {
		return objectsOfInterest.add( trans );
	}

	/**
	 * when objectsOfInterest is empty, all objects are of interest.
	 * when objectsOfInterest has one element, it will always be picked.
	 * when objectsOfInterest has more than one element, mouseDragged behavior will only occur if an objectOfInterest is picked
	 * only firstClass objects will be considered
	 */
	public boolean removeObjectOfInterest( edu.cmu.cs.stage3.alice.core.Transformable trans ) {
		return objectsOfInterest.remove( trans );
	}

	public void clearObjectsOfInterestList() {
		objectsOfInterest.clear();
	}

	public boolean getHideCursorOnDrag() {
		return hideCursorOnDrag;
	}

	public void setHideCursorOnDrag( boolean b ) {
		hideCursorOnDrag = b;
	}

	public void setAscendTreeEnabled( boolean b ) {
		ascendTreeEnabled = b;
	}

	public boolean isAscendTreeEnabled() {
		return ascendTreeEnabled;
	}

	public boolean isPopupEnabled() {
		return popupEnabled;
	}

	public synchronized void setPopupEnabled( boolean b ) {
		if( renderTarget != null ) {
			if( b && (! popupEnabled) ) {
				renderTarget.getAWTComponent().addMouseListener( popupMouseListener );
			} else if( (! b) && popupEnabled ) {
				renderTarget.getAWTComponent().removeMouseListener( popupMouseListener );
			}
		}
		popupEnabled = b;
	}

	public void mousePressed( java.awt.event.MouseEvent ev ) {
		if( enabled ) {
			super.mousePressed( ev );

			firePrePick();
			if( (objectsOfInterest.size() == 1) && pickAllForOneObjectOfInterest ) {
				ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)objectsOfInterest.iterator().next();
				sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
			} else {
				// implementors are responsible for pushing their own undos onto the stack
				if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() != null ) {
					if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack() != null ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().setIsListening( false );
					}
				}

				pickInfo = renderTarget.pick( ev.getX(), ev.getY(), edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget.SUB_ELEMENT_IS_NOT_REQUIRED, edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget.ONLY_FRONT_MOST_VISUAL_IS_REQUIRED );

				if( pickInfo != null ) {
					//DEBUG System.out.println( "info not null" );
					edu.cmu.cs.stage3.alice.scenegraph.Visual[] visuals = pickInfo.getVisuals();
					//DEBUG System.out.println( "visuals: " + visuals );
					//DEBUG if( visuals != null ) {
					//DEBUG 	System.out.println( "visuals.length: " + visuals.length );
					//DEBUG }
					if( (visuals != null) && (visuals.length >= 1) ) {

						ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)visuals[0].getBonus();
						if( ePickedTransformable == null ) {
							sgPickedTransformable = (edu.cmu.cs.stage3.alice.scenegraph.Transformable)visuals[0].getParent();
							ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)sgPickedTransformable.getBonus();
						} else {
							sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
						}
						if( ascendTreeEnabled ) {
							while( (ePickedTransformable != null) && (ePickedTransformable.getParent() instanceof edu.cmu.cs.stage3.alice.core.Transformable) && (! ePickedTransformable.doEventsStopAscending()) && (! objectsOfInterest.contains( ePickedTransformable )) ) {
								//DEBUG System.out.println( "moving up from: " + sgPickedTransformable );
								sgPickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)ePickedTransformable.getParent()).getSceneGraphTr