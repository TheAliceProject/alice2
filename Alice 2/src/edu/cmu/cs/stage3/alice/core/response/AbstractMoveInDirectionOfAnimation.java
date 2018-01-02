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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.lang.Messages;

public abstract class AbstractMoveInDirectionOfAnimation extends TransformAnimation {
	public final ReferenceFrameProperty target = new ReferenceFrameProperty( this, "target", null ); 
	public final NumberProperty amount = new NumberProperty( this, "amount", new Double( 1 ) ); 
	
	public abstract class RuntimeAbstractMoveInDirectionOfAnimationAnimation extends RuntimeTransformAnimation implements edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener {
			
		private edu.cmu.cs.stage3.alice.core.ReferenceFrame m_target;
		private javax.vecmath.Vector3d m_positionBegin;
		private javax.vecmath.Vector3d m_positionEnd;
		
		protected abstract double getActualAmountValue();

		protected javax.vecmath.Vector3d getPositionEnd() {
			double amountValue = getActualAmountValue();
			if( !Double.isNaN( amountValue ) ) {
				m_positionEnd = m_asSeenBy.getPosition( edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
				javax.vecmath.Vector3d v = edu.cmu.cs.stage3.math.MathUtilities.subtract( m_positionEnd, m_positionBegin ) ;
                double length = edu.cmu.cs.stage3.math.MathUtilities.getLength( v );
                if( length>0 ) {
                    v.scale( amountValue/length );
                } else {
                    v.set( 0, 0, amountValue );
                }          
				return v;
			} 
			return new javax.vecmath.Vector3d();
		}
		
		public void absoluteTransformationChanged( edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent ) {
			m_positionEnd = null;
		}
		
		public void prologue( double t ) {
			super.prologue( t );
            m_target = AbstractMoveInDirectionOfAnimation.this.target.getReferenceFrameValue();
			if( m_target == null ) {
				throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( Messages.getString("target_must_not_be_null_"), getCurrentStack(), AbstractMoveInDirectionOfAnimation.this.target ); 
			}
			if( m_asSeenBy == null ) {
				m_asSeenBy = m_target;
			}
			m_positionBegin = m_subject.getPosition( edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
			m_positionEnd = null;
			m_asSeenBy.addAbsoluteTransformationListener( this );
		}
		
		public void update( double t ) {
			super.update( t );
			if( m_positionEnd==null ) {
				m_positionEnd = edu.cmu.cs.stage3.math.MathUtilities.add (getPositionEnd(), m_positionBegin);
			}
			m_subject.setPositionRightNow( edu.cmu.cs.stage3.math.MathUtilities.interpolate( m_positionBegin, m_positionEnd, getPortion( t ) ), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE );
		}

		public void epilogue( double t ) {
			super.epilogue( t );
			if (m_asSeenBy != null) m_asSeenBy.removeAbsoluteTransformationListener( this );
		}
	}
}
