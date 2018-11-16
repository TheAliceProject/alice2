#ifndef TEXTURE_MAP_INCLUDED
#define TEXTURE_MAP_INCLUDED

#include "Element.hpp"
#include "RenderTarget.hpp"
class DisplayDevice;

#include <map>

class TextureMap : public Element {
public:
	TextureMap() : Element() {
		m_nWidth = 0;
		m_nHeight = 0;
		m_vnPixels = NULL;
		m_bIsDirty = true;
	}
	int OnImageChange( long* vnPixels, int nWidth, int nHeight ) {
		CHECK_TRUTH( nWidth>0 );
		CHECK_TRUTH( nHeight>0 );
		if( nWidth==m_nWidth && nHeight==m_nHeight ) {
			//update current surfaces
		} else {
			//purge old surfaces
			if( m_vnPixels ) {
				delete [] m_vnPixels;
			}
			m_nWidth = nWidth;
			m_nHeight = nHeight;
			m_vnPixels = new GLuint[ m_nWidth * m_nHeight ];
		}
		for( int nRow=0; nRow<nHeight; nRow++ ) {
#ifdef CHANGE_IMAGE_DATA
			char* vcDst = (char*)( m_vnPixels + nRow*nWidth );
			char* vcSrc = (char*)( vnPixels + (nHeight-1-nRow)*nWidth );
			for( int nCol=0; nCol<nWidth; nCol++ ) {
					vcDst[ 3 ] = vcSrc[ 0 ];
					vcDst[ 2 ] = vcSrc[ 1 ];
					vcDst[ 1 ] = vcSrc[ 2 ];
					vcDst[ 0 ] = vcSrc[ 3 ];
				vcSrc += 4;
				vcDst += 4;
			}
#else
			memcpy( m_vnPixels + nRow*nWidth, vnPixels + (nHeight-1-nRow)*nWidth, nWidth*4 );
#endif
		}
		m_bIsDirty = true;
		return S_OK;
	}
	int OnFormatChange( int value ) {
		return S_OK;
	}
	int SetPipelineState( RenderTarget* pRenderTarget ) {
		if( m_vnPixels ) {
			GLuint nID;
			bool bIsNew;
			CHECK_SUCCESS( pRenderTarget->AcquireTextureObject( this, nID, bIsNew ) );
			CHECK_GL( glBindTexture( GL_TEXTURE_2D, nID ) );
			if( bIsNew || m_bIsDirty ) {
				CHECK_GL( glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, m_nWidth, m_nHeight, 0, GL_BGRA_EXT, GL_UNSIGNED_BYTE, m_vnPixels ) );
				CHECK_GL( glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT ) );
				CHECK_GL( glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT ) );
				CHECK_GL( glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR ) );
				CHECK_GL( glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR ) );
				m_bIsDirty = false;
			}
		}
		return S_OK;
	}
private:
	int m_nWidth;
	int m_nHeight;
	GLuint* m_vnPixels;
	bool m_bIsDirty;
};

#endif