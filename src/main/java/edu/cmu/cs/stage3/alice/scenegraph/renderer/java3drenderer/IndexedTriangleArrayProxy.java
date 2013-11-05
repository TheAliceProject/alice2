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

class IndexedTriangleArrayProxy extends VertexGeometryProxy {
    private javax.media.j3d.IndexedTriangleArray m_j3dIndexedTriangleArray;
    protected javax.media.j3d.Geometry getJ3DGeometry() {
        return m_j3dIndexedTriangleArray;
    }
    protected void updateGeometry() {
        edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray sgITA = (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray)getSceneGraphElement();
        edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = sgITA.getVertices();
        int[] indices = sgITA.getIndices();
	    int vertexCount = 0;
	    if( vertices != null ) {
	        vertexCount = vertices.length;
	    }

	    int indexCount = 0;
	    if( indices != null ) {
	        indexCount = indices.length;
	    }

	    if( (vertexCount < 1) || (indexCount < 3) ) { // build a bogus piece of geometry
			m_j3dIndexedTriangleArray = new javax.media.j3d.IndexedTriangleArray( 1, javax.media.j3d.GeometryArray.COORDINATES | javax.media.j3d.GeometryArray.NORMALS | javax.media.j3d.GeometryArray.TEXTURE_COORDINATE_2, 3 );
			int[] _indices = {0, 0, 0};
			double[] coords = {0.0, 0.0, 0.0};
            float [] normals = {0.0f, 1.0f, 0.0f};
            float [] texturecoords = {0.0f, 0.0f};
            m_j3dIndexedTriangleArray.setCoordinates( 0, coords );
            m_j3dIndexedTriangleArray.setCoordinateIndices( 0, _indices );
	    } else {
            int[] reversedIndices = new int[ indexCount ];
            for( int i=0; i<indexCount; i+=3 ) {
                reversedIndices[ i ] = indices[ i+2 ];
                reversedIndices[ i+1 ] = indices[ i+1 ];
                reversedIndices[ i+2 ] = indices[ i ];
            }

            double[] coords = null;
            float[] normals = null;
            javax.vecmath.TexCoord2f[] texturecoords = null;
            float[] colors = null;
    	    int j3dVertexFormat = 0;
    	    int vertexFormat = vertices[0].getFormat();
			if( (vertexFormat & edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_POSITION) != 0 ) {
	            coords = new double[vertexCount*3];
	    	    j3dVertexFormat |= javax.media.j3d.GeometryArray.COORDINATES;
	    	}
			if( (vertexFormat & edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_NORMAL) != 0 ) {
                normals = new float[vertexCount*3];
	            j3dVertexFormat |= javax.media.j3d.GeometryArray.NORMALS;
	        }
			if( (vertexFormat & edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_TEXTURE_COORDINATE_0) != 0 ) {
                texturecoords = new javax.vecmath.TexCoord2f[vertexCount];
	            j3dVertexFormat |= javax.media.j3d.GeometryArray.TEXTURE_COORDINATE_2;
	        }
			if( (vertexFormat & edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_DIFFUSE_COLOR) != 0 ) {
                colors = new float[vertexCount*4];
	            j3dVertexFormat |= javax.media.j3d.GeometryArray.COLOR_4;
	        }

            int coordIndex = 0;
            int normalIndex = 0;
            int texturecoordIndex = 0;
            int colorIn