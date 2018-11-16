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

#ifndef INDEXED_TRIANGLE_ARRAY_INCLUDED
#define INDEXED_TRIANGLE_ARRAY_INCLUDED

#include "VertexGeometry.hpp"
class IndexedTriangleArray : public VertexGeometry {
public:
	IndexedTriangleArray() : VertexGeometry() {
#if defined DX7_RENDERER
		m_vwIndices = NULL;
#elif defined OPENGL_RENDERER
		m_vnIndices = NULL;
#endif
		m_nIndexSpace = 0;
		m_nIndexCount = 0;
	}
	int OnIndicesChange( long* vnIndices, int nLength ) {
#if defined DX7_RENDERER
		if( nLength > m_nIndexSpace ) {
			if( m_vwIndices ) {
				delete [] m_vwIndices;
			}
			m_nIndexSpace = nLength;
			m_vwIndices = new WORD[ m_nIndexSpace ];
		}
		m_nIndexCount = nLength;
		for( int i=0; i<m_nIndexCount; i++ ) {
			m_vwIndices[i] = (WORD)vnIndices[i];
		}

#elif defined OPENGL_RENDERER
		
		if( nLength > m_nIndexSpace ) {
			if( m_vnIndices ) {
				delete [] m_vnIndices;
			}
			m_nIndexSpace = nLength;
			m_vnIndices = new long[ m_nIndexSpace ];
		}
		m_nIndexCount = nLength;
		memcpy( m_vnIndices, vnIndices, m_nIndexCount*sizeof( long ) );

#endif
		return S_OK;
	}
	int OnIndexLowerBoundChange( int value ) {
		m_nIndexLowerBound = value;
		return S_OK;
	}
	int OnIndexUpperBoundChange( int value ) {
		m_nIndexUpperBound = value;
		return S_OK;
	}

	int Render( RenderTarget* pRenderTarget, void* pContext ) {
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		CHECK_SUCCESS( RenderIndexedPrimative( pD3DDevice, D3DPT_TRIANGLELIST, m_vwIndices, m_nIndexCount ) );
#elif defined OPENGL_RENDERER
		GLuint nID;
		bool bIsNew;
		CHECK_SUCCESS( pRenderTarget->AcquireDisplayList( this, nID, bIsNew ) );
		if( bIsNew ) {
			CHECK_GL( glNewList( nID, GL_COMPILE_AND_EXECUTE ) );
			glBegin( GL_TRIANGLES );
			for( int iIndex=0; iIndex<m_nIndexCount; iIndex+=3 ) {
				for( int lcv=2; lcv>=0; lcv-- ) {
					CHECK_SUCCESS( AddVertex( m_vnIndices[ iIndex+lcv ] ) );
				}
			}
			CHECK_GL( glEnd() );
			CHECK_GL( glEndList() );
		} else {
			CHECK_GL( glCallList( nID ) );
		}
#endif
		return S_OK;
	}

	int Pick( void* pContext, bool isSubElementRequired, bool bIsFrontFacingAppearance ) {
#if defined DX7_RENDERER
		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;
		if( bIsFrontFacingAppearance ) {
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CULLMODE, D3DCULL_CCW  ) );
		} else {
			CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CULLMODE, D3DCULL_CW  ) );
		}
		D3DLVERTEX v[3];
		int tIndex = 0;
		for( int i=0; i<m_nIndexCount; i+=3 ) {
			for( int i2=0; i2<3; i2++ ) {
				UpdateXYZ( v+i2, m_vwIndices[i+i2] );
				v[i2].color = HACK_g_nVisualID | ( tIndex << 16);
				assert( !( HACK_g_nVisualID & ( 1<<15 ) ) );
				if( bIsFrontFacingAppearance ) {
					v[i2].color |= ( 1<<15 );
				}
				v[i2].dwReserved = 0;
			}
			CHECK_SUCCESS( pD3DDevice->DrawPrimitive( D3DPT_TRIANGLELIST, D3DFVF_LVERTEX, v, 3, D3DDP_WAIT ) );
			tIndex++;
		}
		//fprintf( stderr, "pick visual id: %d\n", HACK_g_nVisualID );
		//fflush( stderr );
#elif defined OPENGL_RENDERER
		CHECK_GL( glEnable( GL_CULL_FACE ) );
		if( bIsFrontFacingAppearance ) {
			CHECK_GL( glCullFace( GL_BACK ) );
			CHECK_GL( glPushName( 1 ) );
		} else {
			CHECK_GL( glCullFace( GL_FRONT ) );
			CHECK_GL( glPushName( 0 ) );
		}
		CHECK_GL( glPushName( (GLuint)-1 ) );
		if( isSubElementRequired ) {
			GLuint nID = 0;
			for( int iIndex=0; iIndex<m_nIndexCount; iIndex+=3 ) {
				glLoadName( nID++ );
				glBegin( GL_TRIANGLES );
				for( int lcv=2; lcv>=0; lcv-- ) {
					CHECK_SUCCESS( AddVertex( m_vnIndices[ iIndex+lcv ] ) );
				}
				CHECK_GL( glEnd() );
			}
		} else {
			glLoadName( (GLuint)-1 );
			glBegin( GL_TRIANGLES );
			for( int iIndex=0; iIndex<m_nIndexCount; iIndex+=3 ) {
				for( int lcv=2; lcv>=0; lcv-- ) {
					CHECK_SUCCESS( AddVertex( m_vnIndices[ iIndex+lcv ] ) );
				}
			}
			CHECK_GL( glEnd() );
		}
		CHECK_GL( glPopName() );
		CHECK_GL( glPopName() );
#endif
		return S_OK;
	}
protected:
	virtual void InternalRelease() {
		Element::InternalRelease();
#if defined DX7_RENDERER
		if( m_vwIndices ) {
			delete [] m_vwIndices;
			m_vwIndices = NULL;
		}
#elif defined OPENGL_RENDERER
		if( m_vnIndices ) {
			delete [] m_vnIndices;
			m_vnIndices = NULL;
		}
#endif
		m_nIndexCount = 0;
		m_nIndexSpace = 0;
	}

private:
#if defined DX7_RENDERER
	WORD* m_vwIndices;
#elif defined OPENGL_RENDERER
	long* m_vnIndices;
#endif
	int m_nIndexSpace;
	int m_nIndexCount;
	int m_nIndexLowerBound;
	int m_nIndexUpperBound;
};

#endif