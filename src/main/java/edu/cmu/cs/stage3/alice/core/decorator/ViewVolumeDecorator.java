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

package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;

public abstract class ViewVolumeDecorator extends Decorator {
	private edu.cmu.cs.stage3.alice.scenegraph.Visual m_sgVisualLines = null;
	private edu.cmu.cs.stage3.alice.scenegraph.LineArray m_sgLineArray = null;
	private edu.cmu.cs.stage3.alice.scenegraph.Visual m_sgVisualFaces = null;
	private edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray m_sgITA = null;

	protected ReferenceFrame getReferenceFrame() {
		return getCamera();
	}
	protected abstract Camera getCamera();

	public void internalRelease( int pass ) {
        switch( pass ) {
        case 1:
            if( m_sgVisualLines != null ) {
                m_sgVisualLines.setFrontFacingAppearance( null );
                m_sgVisualLines.setGeometry( null );
            }
            if( m_sgVisualFaces != null ) {
                m_sgVisualFaces.setFrontFacingAppearance( null );
                m_sgVisualFaces.setGeometry( null );
            }
            break;
        case 2:
            if( m_sgVisualLines != null ) {
                m_sgVisualLines.release();
                m_sgVisualLines = null;
            }
            if( m_sgLineArray != null ) {
                m_sgLineArray.release();
                m_sgLineArray = null;
            }
            if( m_sgVisualFaces != null ) {
                m_sgVisualFaces.release();
                m_sgVisualFaces = null;
            }
            if( m_sgITA != null ) {
                m_sgITA.release();
                m_sgITA = null;
            }
            break;
        }
        super.internalRelease( pass );
    }
	protected abstract double[] getXYNearAndXYFar( double zNear, double zFar );

	protected void update() {
		super.update();
        boolean requiresVerticesToBeUpdated = isDirty();
        if( m_sgLineArray==null ) {
            m_sgLineArray = new edu.cmu.cs.stage3.alice.scenegraph.LineArray();
            requiresVerticesToBeUpdated = true;
        }
        if( m_sgITA==null ) {
            int[] indices = {
                0,1,2, 2,3,0,
                5,6,2, 2,1,5,
                6,7,3, 3,2,6,
                7,4,0, 0,3,7,
                4,5,1, 1,0,4,
                7,6,5, 5,4,7,
                3,2,1, 1,0,3,
                1,2,6, 6,5,1,
                2,3,7, 7,6,2,
                3,0,4, 4,7,3,
                0,1,5, 5,4,0,
                4,5,6, 6,7,4
            };
            m_sgITA = new edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray();
            m_sgITA.setIndices( indices );
            requiresVerticesToBeUpdated = true;
        }
        //faces = [(0,1,2,3), (5,6,2,1), (6,7,3,2), (7,4,0,3), (4,5,1,0), (7,6,5,4), (3,2,1,0), (1,2,6,5), (2,3,7,6), (3,0,4,7), (0,1,5,4), (4,5,6,7)]
        if( m_sgVisualLines==null ) {
            m_sgVisualLines = new edu.cmu.cs.stage3.alice.scenegraph.Visual();
            //todo
            //m_sgVisualLines.setDiffuseColor( edu.cmu.cs.stage3.alice.scen