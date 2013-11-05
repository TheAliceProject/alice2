/*
 * Created on Mar 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.lang.Messages;

public class CloseUpAnimation extends AbstractPositionAnimation {
	public final Vector3Property position = new Vector3Property( this, "position", new javax.vecmath.Vector3d( 0,0,0 ) ); 
	public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty( this, "spatialRelation", edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF ); 
	public final NumberProperty amount = new NumberProperty( this, "amount", new Double( 1 ) ); 
	
	private edu.cmu.cs.stage3.math.Matrix33 m_cameraEndOrientation;
	private edu.cmu.cs.stage3.math.Matrix33 m_cameraBeginOrientation;

	public static CloseUpAnimation createCloseUpAnimation( Object subject, Object spatialRelation, Object amount, Object asSeenBy ) {
		CloseUpAnimation closeUpAnimation = new CloseUpAnimation();
		closeUpAnimation.subject.set( subject );
		closeUpAnimation.spatialRelation.set( spatialRelation );
		closeUpAnimation.amount.set( amount );
		closeUpAnimation.asSeenBy.set( asSeenBy );
		return closeUpAnimation;
	}
	
	
	public class RuntimeCloseUpAnimation extends RuntimeAbstractPositionAnimation {
		private edu.cmu.cs.stage3.math.Box m_subjectBoundingBox;
		private edu.cmu.cs.stage3.math.Box m_asSeenByBoundingBox;
		private double m_amount;
		private double m_cameraHeight;
		
		protected javax.vecmath.Vector3d getPositionBegin() {
			m_cameraBeginOrientation = m_subject.getOrientationAsAxes(m_asSeenBy);
			return m_subject.getPosition( edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
		
		protected javax.vecmath.Vector3d getPositionEnd() {
			if( m_subjectBoundingBox==null ) {
				m_subjectBoundingBox = m_subject.getBoundingBox();
			
				if (m_subjectBoundingBox.getMaximum() == null) {
					m_subjectBoundingBox = new edu.cmu.cs.stage3.math.Box(m_subject.getPosition( m_subject ), m_subject.getPosition( m_subject ));
				}
			}
			if( m_asSeenByBoundingBox ==null ) {
				m_asSeenByBoundingBox = m_asSeenBy.getBoundingBox();
			
				if (m_asSeenByBoundingBox.getMaximum() == null) {
					m_asSeenByBoundingBox = new edu.cmu.cs.stage3.math.Box(m_asSeenBy.getPosition( m_asSeenBy ), m_asSeenBy.getPosition( m_asSeenBy ));
				}
			}
			
			edu.cmu.cs.stage3.alice.core.SpatialRelation sv = CloseUpAnimation.this.spatialRelation.getSpatialRelationValue();
			edu.cmu.cs.stage3.math.Matrix33 cameraEndOrientation = m_asSeenBy.getOrientationAsAxes(m_asSeenBy);	

//			get the initial position and orientation for the camera
			 if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(1, 0, 0), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d(-1, 0, 0), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND_RIGHT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d( -0.7071068, 0, 0.7071068), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND_LEFT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d( 0.7071068, 0, 0.7071068), cameraEndOrientation.getRow(1));
			 } else if (sv.equals(edu.cmu.cs.stage3.alice.core.SpatialRelation.FRONT_RIGHT_OF)) {
				cameraEndOrientation.setForwardUpGuide(new javax.vecmath.Vector3d( -0.7071068, 0, -0.7071068), cameraEndOrientation.getRow(1));
			 } 