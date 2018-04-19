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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import com.jogamp.opengl.GL2;

class IndexedTriangleArrayProxy extends VertexGeometryProxy {
    private int[] m_indices;
    
	public void render( RenderContext context ) {
        Integer id = context.getDisplayListID( this );
        if( id == null ) {
            id = context.generateDisplayListID( this );
		    setIsGeometryChanged( true );
        }
        if( isGeometryChanged() ) {
            context.gl2.glNewList( id.intValue(), GL2.GL_COMPILE_AND_EXECUTE );
            context.gl2.glBegin( GL2.GL_TRIANGLES );
            if ( m_indices != null)
            for( int i=0; i<m_indices.length; i+=3 ) {
                context.renderVertex( getVertexAt( m_indices[ i+2 ] ) );
                context.renderVertex( getVertexAt( m_indices[ i+1 ] ) );
                context.renderVertex( getVertexAt( m_indices[ i+0 ] ) );
            }
            context.gl2.glEnd();
            context.gl2.glEndList();
		    setIsGeometryChanged( false );
        } else {
            context.gl2.glCallList( id.intValue() );
        }
    }
	
	public void pick( PickContext context, boolean isSubElementRequired ) {
	    context.gl2.glPushName( -1 );
	    if( isSubElementRequired ) {
            context.gl2.glBegin( GL2.GL_TRIANGLES );
            for( int i=0; i<m_indices.length; i+=3 ) {
                context.renderPickVertex( getVertexAt( m_indices[ i+2 ] ) );
                context.renderPickVertex( getVertexAt( m_indices[ i+1 ] ) );
                context.renderPickVertex( getVertexAt( m_indices[ i+0 ] ) );
            }
            context.gl2.glEnd();
	    } else {
	        int id = 0;
	        if ( m_indices != null)
	        for( int i=0; i<m_indices.length; i+=3 ) {
				context.gl2.glLoadName( id++ );
	            context.gl2.glBegin( GL2.GL_TRIANGLES );
                context.renderPickVertex( getVertexAt( m_indices[ i+2 ] ) );
                context.renderPickVertex( getVertexAt( m_indices[ i+1 ] ) );
                context.renderPickVertex( getVertexAt( m_indices[ i+0 ] ) );
	            context.gl2.glEnd();
	        }
	    }
	    context.gl2.glPopName();
	}
	
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.INDICES_PROPERTY ) {
		    m_indices = (int[])value;
		    setIsGeometryChanged( true );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.INDEX_LOWER_BOUND_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.INDEX_UPPER_BOUND_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}
