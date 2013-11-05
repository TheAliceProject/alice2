/*
 * Created on Mar 1, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.lang.Messages;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LieDownAnimation extends BetterStandUpAnimation {
	public final edu.cmu.cs.stage3.alice.core.property.TransformableProperty target = new edu.cmu.cs.stage3.alice.core.property.TransformableProperty( this, "target", null ); 
	public final DirectionProperty feetFaceDirection = new DirectionProperty(this, "feetFacingDirection", edu.cmu.cs.stage3.alice.core.Direction.FORWARD); 
	
	public class RuntimeLieDownAnimation extends RuntimeBetterStandUpAnimation {
		edu.cmu.cs.stage3.alice.core.Transformable target = null;
		
		private javax.vecmath.Vector3d m_positionBegin;
		private javax.vecmath.Vector3d m_positionEnd;
		private edu.cmu.cs.stage3.alice.core.Direction m_direction;
		
		
		public void prologue( double t ) {	
			super.prologue( t );
			target = LieDownAnimation.this.target.getTransformableValue();
			
			if( target == null ) {
                throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( m_subject.name.getStringValue() + Messages.getString("_needs_something_or_someone_to_lie_down_on_"), null, LieDownAnimation.this.target ); 
            }
            if( m_subject == target ) {
                throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( m_subject.name.getStringValue() + Messages.getString("_can_t_lie_down_on_") + target.name.getStringValue() + ".", getCurrentStack(), LieDownAnimation.this.target );              
            }
            
            if (m_subject.isAncestorOf(target)) {
            	throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( m_subject.name.getStringValue() + Messages.getString("_can_t_lie_down_on_a_part_of_itself"), getCurrentStack(), LieDownAnimation.this.target );             
            }
			
			m_positionBegin = m_subject.getPosition(m_subject.getWorld());
			m_direction = feetFaceDirection.getDirectionValue();
		}
				
		
		public void update( double t ) {
			super.update( t );
			if( m_positionEnd==null ) {
				m_positionEnd = getPositionEnd();
			}
			m_subject.setPositionRightNow( edu.cmu.cs.stage3.math.MathUtilities.interpolate( m_positionBegin, m_positionEnd, getPortion( t ) ), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}
		
		
		public void epilogue( double t ) {
			super.epilogue(t);
			m_positionEnd = null;
		}
		
		
		
		
		protected javax.vecmath.Vector3d getPositionEnd() {
			if (target != null) {
				
				javax.vecmath.Vector3d endPos = null;
				
				if (target.name.getStringValue().equals("ground")){ 
					endPos = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace();
				} else {
					endPos = target.getBoundingBox(target.getWorld()).getCenterOfTopFace();
				}
				javax.vecmath.Vector3d[] forwardAndUp =  target.getOrientationAsForwardAndUpGuide(target.getWorld());
				javax.vecmath.Vector3d left = new javax.vecmath.Vector3d();
				left.cross(forwardAndUp[0], forwardAndUp[1]);
				
				
				double subjectHeight = m_subject.getHeight();
				
				double zOffset = java.lang.Math.abs(m_subject.getBoundingBox(m_subject).getCenterOfBackFace().z) - m_subject.getBoundingBox(m_subject).getDepth()*0.2;
				
				if (m_direction.equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD)) {
					endPos = new javax.vecmath.Vector3d(endPos.x - (forwardAndUp[0].x * subjectHeight/2.0), endPos.y + zOffset + (forwardAndUp[0].y * subjectHeight/2.0), endPos.z - (forwardAndUp[0].z * subjectHeight/2.0));
				} else if (m_direction.equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD)) {
					endPos = new javax.vecmath.Vector3d(endPos.x + (forwardAndUp[0].x * subjectHeight/2.0), endPos.y + zOffset + (forwardAndUp[