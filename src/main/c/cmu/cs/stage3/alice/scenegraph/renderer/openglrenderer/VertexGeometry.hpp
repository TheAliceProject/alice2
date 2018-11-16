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
	VertexGeometry() : Geometry() {
		m_nFormat = 0;
		m_nVertexCount = 0;
		m_nVertexSpace = 0;
		m_nVertexLowerBound = 0;
		m_nVertexUpperBound = -1;
		m_vfPositions = NULL;
		m_vfNormals = NULL;
		m_vfDiffuseColors = NULL;
		m_vfSpecularHighlightColors = NULL;
		m_vfTextureCoordinate0s = NULL;
	}
	int OnVerticesFormatAndLengthChange( int nFormat, int nLength ) {
		if( nFormat != m_nFormat || nLength > m_nVertexSpace ) {
			ReleaseMemory();
		}
		m_nFormat = nFormat;
		if( nLength > m_nVertexSpace )  {
			m_nVertexSpace = nLength;
			if( m_nFormat & VERTEX_FORMAT_POSITION ) {
				m_vfPositions = new GLdouble[ m_nVertexSpace * 3 ];
			}
			if( m_nFormat & VERTEX_FORMAT_NORMAL ) {
				m_vfNormals = new GLdouble[ m_nVertexSpace * 3 ];
			}
			if( m_nFormat & VERTEX_FORMAT_DIFFUSE_COLOR ) {
				m_vfDiffuseColors = new GLfloat[ m_nVertexSpace * 4 ];
			}
			if( m_nFormat & VERTEX_FORMAT_SPECULAR_HIGHLIGHT_COLOR ) {
				m_vfSpecularHighlightColors = new GLfloat[ m_nVertexSpace * 4 ];
			}
			if( m_nFormat & VERTEX_FORMAT_TEXTURE_COORDINATE_0 ) {
				m_vfTextureCoordinate0s = new GLfloat[ m_nVertexSpace * 2 ];
			}
		}
		m_nVertexCount = nLength;
		return S_OK;
	}

	int OnVerticesVertexPositionChange( int nIndex, double fX, double fY, double fZ ) {
		GLdouble* pfPosition = m_vfPositions + nIndex*3;
		*pfPosition++ = fX;
		*pfPosition++ = fY;
		*pfPosition++ = -fZ;
		return S_OK;
	}
	int OnVerticesVertexNormalChange( int nIndex, double fI, double fJ, double fK ) {
		GLdouble* pfNormal = m_vfNormals + nIndex*3;
		*pfNormal++ = fI;
		*pfNormal++ = fJ;
		*pfNormal++ = -fK;
		return S_OK;
	}
	int OnVerticesVertexDiffuseColorChange( int nIndex, float fRed, float fGreen, float fBlue, float fAlpha ) {
		GLfloat* pfDiffuseColor = m_vfDiffuseColors + nIndex*4;
		*pfDiffuseColor++ = fRed;
		*pfDiffuseColor++ = fGreen;
		*pfDiffuseColor++ = fBlue;
		*pfDiffuseColor++ = fAlpha;
		return S_OK;
	}
	int OnVerticesVertexSpecularHighlightColorChange( int nIndex, float fRed, float fGreen, float fBlue, float fAlpha ) {
		GLfloat* pfSpecularHighlightColor = m_vfSpecularHighlightColors + nIndex*4;
		*pfSpecularHighlightColor++ = fRed;
		*pfSpecularHighlightColor++ = fGreen;
		*pfSpecularHighlightColor++ = fBlue;
		*pfSpecularHighlightColor++ = fAlpha;
		return S_OK;
	}
	int OnVerticesVertexTextureCoordinate0Change( int nIndex, float fU, float fV ) {
		GLfloat* pfTextureCoordinate0 = m_vfTextureCoordinate0s + nIndex*2;
		*pfTextureCoordinate0++ = fU;
		*pfTextureCoordinate0++ = fV;
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
protected:
	virtual void InternalRelease() {
		Element::InternalRelease();
		ReleaseMemory();
		m_nVertexCount = 0;
	}

	int RenderPrimative( RenderTarget* pRenderTarget, void* pContext, GLenum mode ) {
		GLuint nID;
		bool bIsNew;
		CHECK_SUCCESS( pRenderTarget->AcquireDisplayList( this, nID, bIsNew ) );
		if( bIsNew ) {
			CHECK_GL( glNewList( nID, GL_COMPILE_AND_EXECUTE ) );
			CHECK_GL( glBegin( mode ) );
			for( int i=0; i<m_nVertexCount; i++ ) {
				int hResult = AddVertex( i );
				if( hResult < 0 ) {
					//todo
					break;
				}
			}
			CHECK_GL( glEnd() );
			CHECK_GL( glEndList() );
		} else {
			CHECK_GL( glCallList( nID ) );
		}
		return S_OK;
	}

	int AddVertex( int nIndex ) {
		if( m_vfTextureCoordinate0s ) {
			GLfloat* pfTextureCoordinate0 = m_vfTextureCoordinate0s + nIndex*2;
			glTexCoord2fv( pfTextureCoordinate0 );
		}
		if( m_vfNormals ) {
			GLdouble* pfNormal = m_vfNormals + nIndex*3;
			glNormal3dv( pfNormal );
		}
		if( m_vfPositions ) {
			GLdouble* pfPosition = m_vfPositions + nIndex*3;
			glVertex3dv( pfPosition );
		}
		return S_OK;
	}

private:
	void ReleaseMemory() {
		if( m_vfPositions ) {
			delete [] m_vfPositions;
			m_vfPositions = NULL;
		}
		if( m_vfNormals ) {
			delete [] m_vfNormals;
			m_vfNormals = NULL;
		}
		if( m_vfDiffuseColors ) {
			delete [] m_vfDiffuseColors;
			m_vfDiffuseColors = NULL;
		}
		if( m_vfSpecularHighlightColors ) {
			delete [] m_vfSpecularHighlightColors;
			m_vfSpecularHighlightColors = NULL;
		}
		if( m_vfTextureCoordinate0s ) {
			delete [] m_vfTextureCoordinate0s;
			m_vfTextureCoordinate0s = NULL;
		}
		m_nVertexSpace = 0;
	}

	GLdouble* m_vfPositions;
	GLdouble* m_vfNormals;
	GLfloat* m_vfDiffuseColors;
	GLfloat* m_vfSpecularHighlightColors;
	GLfloat* m_vfTextureCoordinate0s;

	int m_nFormat;
	int m_nVertexCount;
	int m_nVertexSpace;
	int m_nVertexLowerBound;
	int m_nVertexUpperBound;
};

#endif

