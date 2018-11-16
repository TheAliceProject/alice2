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

#ifndef VERTEX_GEOMETRY_INCLUDED
#define VERTEX_GEOMETRY_INCLUDED

#include <vector>

#include "Geometry.hpp"

const int VERTEX_FORMAT_POSITION = 1;
const int VERTEX_FORMAT_NORMAL = 2;
const int VERTEX_FORMAT_DIFFUSE_COLOR = 4;
const int VERTEX_FORMAT_SPECULAR_HIGHLIGHT_COLOR = 8;
const int VERTEX_FORMAT_TEXTURE_COORDINATE_0 = 16;

class VertexGeometry : public Geometry {
public:
	VertexGeometry() {
		//Geometry::Geometry();
		m_vD3DVertices = NULL;
		m_vD3DLitVertices = NULL;
		m_nVertexCount = 0;
		m_nVertexSpace = 0;
		m_nVertexLowerBound = 0;
		m_nVertexUpperBound = -1;
		m_dwVertexTypeDesc = 0;
	}
	int OnVerticesFormatAndLengthChange( int nFormat, int nLength ) {
		//todo
		switch( nFormat ) {
		case VERTEX_FORMAT_POSITION | VERTEX_FORMAT_NORMAL | VERTEX_FORMAT_TEXTURE_COORDINATE_0:
			m_dwVertexTypeDesc = D3DFVF_VERTEX;
			break;
		case VERTEX_FORMAT_POSITION | VERTEX_FORMAT_DIFFUSE_COLOR:
			m_dwVertexTypeDesc = D3DFVF_LVERTEX;
			break;
		default:
			m_dwVertexTypeDesc = 0;
			CHECK_TRUTH( nLength==0 );
		}
		if( nLength > m_nVertexSpace ) {
			if( m_vD3DVertices ) {
				delete [] m_vD3DVertices;
			}
			if( m_vD3DLitVertices ) {
				delete [] m_vD3DLitVertices;
			}
			m_nVertexSpace = nLength;
			switch( m_dwVertexTypeDesc ) {
			case D3DFVF_VERTEX:
				m_vD3DVertices = new D3DVERTEX[ m_nVertexSpace ];
				m_vD3DLitVertices = NULL;
				break;
			case D3DFVF_LVERTEX:
				m_vD3DVertices = NULL;
				m_vD3DLitVertices = new D3DLVERTEX[ m_nVertexSpace ];
				break;
			default:
				CHECK_TRUTH( false );
			}
		}
		m_nVertexCount = nLength;
		return S_OK;
	}	
	int OnVerticesVertexPositionChange( int nIndex, double fX, double fY, double fZ ) {
		D3DVERTEX* pVertex;
		D3DLVERTEX* pLitVertex;
		switch( m_dwVertexTypeDesc ) {
		case D3DFVF_VERTEX:
			pVertex = m_vD3DVertices + nIndex;
			pVertex->dvX = (D3DVALUE)fX;
			pVertex->dvY = (D3DVALUE)fY;
			pVertex->dvZ = (D3DVALUE)fZ;
			break;
		case D3DFVF_LVERTEX:
			pLitVertex = m_vD3DLitVertices + nIndex;
			pLitVertex->dvX = (D3DVALUE)fX;
			pLitVertex->dvY = (D3DVALUE)fY;
			pLitVertex->dvZ = (D3DVALUE)fZ;
			break;
		default:
			CHECK_TRUTH( false );
		}
		return S_OK;
	}
	int OnVerticesVertexNormalChange( int nIndex, double fI, double fJ, double fK ) {
		D3DVERTEX* pVertex;
		switch( m_dwVertexTypeDesc ) {
		case D3DFVF_VERTEX:
			pVertex = m_vD3DVertices + nIndex;
			pVertex->dvNX = (D3DVALUE)fI;
			pVertex->dvNY = (D3DVALUE)fJ;
			pVertex->dvNZ = (D3DVALUE)fK;
			break;
		default:
			CHECK_TRUTH( false );
		}
		return S_OK;
	}
	int OnVerticesVertexDiffuseColorChange( int nIndex, float fRed, float fGreen, float fBlue, float fAlpha ) {
		D3DLVERTEX* pLitVertex;
		switch( m_dwVertexTypeDesc ) {
		case D3DFVF_LVERTEX:
			pLitVertex = m_vD3DLitVertices + nIndex;
			//todo? alpha
			pLitVertex->dcColor = D3DRGB( fRed, fGreen, fBlue );
			break;
		default:
			CHECK_TRUTH( false );
		}
		return S_OK;
	}
	int OnVerticesVertexSpecularHighlightColorChange( int nIndex, float fRed, float fGreen, float fBlue, float fAlpha ) {
		D3DLVERTEX* pLitVertex;
		switch( m_dwVertexTypeDesc ) {
		case D3DFVF_LVERTEX:
			pLitVertex = m_vD3DLitVertices + nIndex;
			//todo? alpha
			pLitVertex->dcSpecular = D3DRGB( fRed, fGreen, fBlue );
			break;
		default:
			CHECK_TRUTH( false );
		}
		return S_OK;
	}
	int OnVerticesVertexTextureCoordinate0Change( int nIndex, float fU, float fV ) {
		D3DVERTEX* pVertex;
		D3DLVERTEX* pLitVertex;
		switch( m_dwVertexTypeDesc ) {
		case D3DFVF_VERTEX:
			pVertex = m_vD3DVertices + nIndex;
			pVertex->dvTU = fU;
			pVertex->dvTV = fV;
			break;
		case D3DFVF_LVERTEX:
			pLitVertex = m_vD3DLitVertices + nIndex;
			pLitVertex->dvTU = fU;
			pLitVertex->dvTV = fV;
			break;
		default:
			CHECK_TRUTH( false );
		}
		return S_OK;
	}
	int OnVertexLowerBoundChange( int value ) {
		m_nVertexLowerBound = value;
		return S_OK;
	}
	int OnVertexUpperBoundChange( int value ) {
		m_nVertexUpperBound = value;
		return S_OK;
	}
	int OnVerticesBeginChange() {
		return S_OK;
	}
	int OnVerticesEndChange() {
		return S_OK;
	}
#if defined DX7_RENDERER
	virtual int ReverseLighting() {
		if( m_vD3DVertices ) {
			for( int i=0; i<m_nVertexCount; i++ ) {
				m_vD3DVertices[ i ].dvNX = -m_vD3DVertices[ i ].dvNX; 
				m_vD3DVertices[ i ].dvNY = -m_vD3DVertices[ i ].dvNY; 
				m_vD3DVertices[ i ].dvNZ = -m_vD3DVertices[ i ].dvNZ; 
			}
		}
		return S_OK;
	}
#elif defined OPENGL_RENDERER
#endif
protected:
	virtual void InternalRelease() {
		Element::InternalRelease();
		if( m_vD3DVertices ) {
			delete [] m_vD3DVertices;
			m_vD3DVertices = NULL;
		}
		if( m_vD3DLitVertices ) {
			delete [] m_vD3DLitVertices;
			m_vD3DLitVertices = NULL;
		}
		m_nVertexCount = 0;
		m_nVertexSpace = 0;
	}
	int RenderIndexedPrimative( LPDIRECT3DDEVICE7 pD3DDevice, D3DPRIMITIVETYPE dptPrimitiveType, WORD* vwIndices, int nIndexCount ) {
		if( m_nVertexCount ) {
			void* pVertices;
			switch( m_dwVertexTypeDesc ) {
			case D3DFVF_VERTEX:
				pVertices = m_vD3DVertices;
				break;
			case D3DFVF_LVERTEX:
				pVertices = m_vD3DLitVertices;
				break;
			default:
				CHECK_TRUTH( false );
			}
			CHECK_SUCCESS( pD3DDevice->DrawIndexedPrimitive( dptPrimitiveType, m_dwVertexTypeDesc, pVertices, m_nVertexCount, vwIndices, nIndexCount, 0 ) );
		}
		return S_OK; 
	} 

	int RenderPrimative( RenderTarget* pRenderTarget, void* pContext, D3DPRIMITIVETYPE dptPrimitiveType ) {

		LPDIRECT3DDEVICE7 pD3DDevice = (LPDIRECT3DDEVICE7)pContext;

		if( m_nVertexCount ) {
			int nVertexLowerBound;
			if( m_nVertexLowerBound>=0 ) {
				nVertexLowerBound = m_nVertexLowerBound;
			} else {
				nVertexLowerBound = m_nVertexCount + m_nVertexLowerBound;
			}
			int nVertexUpperBound;
			if( m_nVertexUpperBound>=0 ) {
				nVertexUpperBound = m_nVertexUpperBound;
			} else {
				nVertexUpperBound = m_nVertexCount + m_nVertexUpperBound;
			}
			void* pVertices;
			switch( m_dwVertexTypeDesc ) {
			case D3DFVF_VERTEX:
				pVertices = m_vD3DVertices+nVertexLowerBound;
				break;
			case D3DFVF_LVERTEX:
				pVertices = m_vD3DLitVertices+nVertexLowerBound;
				break;
			default:
				CHECK_TRUTH( false );
			}
			unsigned nCount = nVertexUpperBound-nVertexLowerBound + 1;
			CHECK_SUCCESS( pD3DDevice->DrawPrimitive( dptPrimitiveType, m_dwVertexTypeDesc, pVertices, nCount, NULL ) );
		}
		return S_OK; 
	} 
	int UpdateXYZ( D3DLVERTEX* pLitVertex, WORD wIndex ) {
		switch( m_dwVertexTypeDesc ) {
		case D3DFVF_VERTEX:
			pLitVertex->dvX = m_vD3DVertices[wIndex].dvX;
			pLitVertex->dvY = m_vD3DVertices[wIndex].dvY;
			pLitVertex->dvZ = m_vD3DVertices[wIndex].dvZ;
			break;
		case D3DFVF_LVERTEX:
			pLitVertex->dvX = m_vD3DLitVertices[wIndex].dvX;
			pLitVertex->dvY = m_vD3DLitVertices[wIndex].dvY;
			pLitVertex->dvZ = m_vD3DLitVertices[wIndex].dvZ;
			break;
		default:
			CHECK_TRUTH( false );
		}
		return S_OK; 
	}

private:
	D3DVERTEX* m_vD3DVertices;
	D3DLVERTEX* m_vD3DLitVertices;
	DWORD m_dwVertexTypeDesc;
	int m_nVertexCount;
	int m_nVertexSpace;
	int m_nVertexLowerBound;
	int m_nVertexUpperBound;
};

#endif

