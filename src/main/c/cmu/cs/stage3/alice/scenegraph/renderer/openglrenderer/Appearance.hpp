#ifndef APPEARANCE_INCLUDED
#define APPEARANCE_INCLUDED

#include <vector>
#include <algorithm>

#include "Element.hpp"
#include "TextureMap.hpp"
#include "RenderTarget.hpp"
class _Visual;

enum {
	FILLING_STYLE_POINTS = 1,
	FILLING_STYLE_WIREFRAME = 2,
	FILLING_STYLE_SOLID = 4
};
enum {
	SHADING_STYLE_NONE = 0,
	SHADING_STYLE_FLAT = 1,
	SHADING_STYLE_SMOOTH = 2,
};


class Appearance : public Element {
public:
	Appearance() : Element() {
		OnAmbientColorChange( 1,1,1,1 );
		OnDiffuseColorChange( 1,1,1,1 );
		//OnOpacityChange( 1 );
		m_vfDiffuse[ 3 ] = 1.0f;
		OnSpecularHighlightColorChange( 0,0,0,1 );
		OnEmissiveColorChange( 0,0,0,1 );
		OnSpecularHighlightExponentChange( 0 );
		OnFillingStyleChange( FILLING_STYLE_SOLID );
		OnShadingStyleChange( SHADING_STYLE_SMOOTH );
		OnDiffuseColorMapChange( NULL );
	}
	int AddVisual( _Visual* pVisual ) {
		m_visuals.push_back( pVisual );
		return S_OK;
	}
	int RemoveVisual( _Visual* pVisual ) {
		std::vector<_Visual*>::iterator iTarget;
		iTarget = std::find( m_visuals.begin(), m_visuals.end(), pVisual );
		CHECK_TRUTH( m_visuals.end() != iTarget );
		m_visuals.erase( iTarget );
		return S_OK;
	}

	int OnAmbientColorChange( double r, double g, double b, double a ) {
		m_vfAmbient[ 0 ] = (GLfloat)r;
		m_vfAmbient[ 1 ] = (GLfloat)g;
		m_vfAmbient[ 2 ] = (GLfloat)b;
		m_vfAmbient[ 3 ] = 1.0;
		m_bAmbientIsContrainedToDiffuse = IsNaN( r )!=0;
		return S_OK;
	}
	int OnDiffuseColorChange( double r, double g, double b, double a ) {
		m_vfDiffuse[ 0 ] = (GLfloat)r;
		m_vfDiffuse[ 1 ] = (GLfloat)g;
		m_vfDiffuse[ 2 ] = (GLfloat)b;
		return S_OK;
	}
	int OnOpacityChange( double value ) {
		GLfloat prev = m_vfDiffuse[ 3 ];
		m_vfDiffuse[ 3 ] = (GLfloat)value;
		if( prev < 1.0f ) {
			if( value >= 1.0f ) {
				CHECK_SUCCESS( NotifyVisualsOfOpaqueStateChange() );
			}
		} else {
			if( value < 1.0f ) {
				CHECK_SUCCESS( NotifyVisualsOfOpaqueStateChange() );
			}
		}
		return S_OK;
	}
	int OnFillingStyleChange( int value ) {
		switch( value ) {
		case FILLING_STYLE_SOLID:
			m_nPolygonMode = GL_FILL;
			break;
		case FILLING_STYLE_WIREFRAME:
			m_nPolygonMode = GL_LINE;
			break;
		case FILLING_STYLE_POINTS:
			m_nPolygonMode = GL_POINT;
			break;
		default:
			return -1;
		}
		return S_OK;
	}
	int OnShadingStyleChange( int value ) {
		switch( value ) {
		case SHADING_STYLE_SMOOTH:
			m_bIsShaded = true;
			break;
		case SHADING_STYLE_FLAT:
			m_bIsShaded = true;
			break;
		case SHADING_STYLE_NONE:
			m_bIsShaded = false;
			break;
		default:
			return -1;
		}
		return S_OK;
	}
	int OnSpecularHighlightColorChange( double r, double g, double b, double a ) {
		m_vfSpecular[ 0 ] = (GLfloat)r;
		m_vfSpecular[ 1 ] = (GLfloat)g;
		m_vfSpecular[ 2 ] = (GLfloat)b;
		m_vfSpecular[ 3 ] = 1.0f;
		return S_OK;
	}
	int OnSpecularHighlightExponentChange( double value ) {
		m_fShininess = (GLfloat)value;
		return S_OK;
	}
	int OnEmissiveColorChange( double r, double g, double b, double a ) {
		m_vfEmissive[ 0 ] = (GLfloat)r;
		m_vfEmissive[ 1 ] = (GLfloat)g;
		m_vfEmissive[ 2 ] = (GLfloat)b;
		m_vfEmissive[ 3 ] = 1.0f;
		return S_OK;
	}
	int OnDiffuseColorMapChange( TextureMap* value ) {
		m_pDiffuseColorMap = value;
		return S_OK;
	}
	int OnOpacityMapChange( TextureMap* value ) {
		TextureMap* prev = m_pOpacityMap;
		m_pOpacityMap = value;
		if( prev ) {
			if( !value ) {
				CHECK_SUCCESS( NotifyVisualsOfOpaqueStateChange() );
			}
		} else {
			if( value ) {
				CHECK_SUCCESS( NotifyVisualsOfOpaqueStateChange() );
			}
		}
		return S_OK;
	}
	int OnEmissiveColorMapChange( TextureMap* value ) {
		return S_OK;
	}
	int OnSpecularHighlightColorMapChange( TextureMap* value ) {
		return S_OK;
	}
	int OnBumpMapChange( TextureMap* value ) {
		return S_OK;
	}
	int OnDetailMapChange( TextureMap* value ) {
		return S_OK;
	}
	int SetPipelineState( RenderTarget* pRenderTarget, GLenum face ) {
		if( m_bIsShaded ) {
			CHECK_GL( glEnable( GL_LIGHTING ) );
			if( m_bAmbientIsContrainedToDiffuse ) {
				CHECK_GL( glMaterialfv( face, GL_AMBIENT_AND_DIFFUSE, m_vfDiffuse ) );
			} else {
				CHECK_GL( glMaterialfv( face, GL_AMBIENT, m_vfAmbient ) );
				CHECK_GL( glMaterialfv( face, GL_DIFFUSE, m_vfDiffuse ) );
			}
			CHECK_GL( glMaterialfv( face, GL_SPECULAR, m_vfSpecular ) );
			CHECK_GL( glMaterialfv( face, GL_EMISSION, m_vfEmissive ) );
			CHECK_GL( glMaterialf( face, GL_SHININESS, m_fShininess ) );
		} else {
			CHECK_GL( glDisable( GL_LIGHTING ) );
			//todo?
			//glColor( diffuse )
		}
		CHECK_GL( glPolygonMode( face, m_nPolygonMode ) );
		if( m_pDiffuseColorMap ) {
			if( pRenderTarget->HACK_NextTextureMap( m_pDiffuseColorMap ) ) {
				//pass
			} else {
				m_pDiffuseColorMap->SetPipelineState( pRenderTarget );
			}
			CHECK_GL( glEnable( GL_TEXTURE_2D ) );
		} else {
			CHECK_GL( glDisable( GL_TEXTURE_2D ) );
		}
		return S_OK;
	}

	bool IsOpaque() {
		return m_vfDiffuse[ 3 ]>=1.0f && m_pOpacityMap==NULL;
	}
private:
	int NotifyVisualsOfOpaqueStateChange();

	bool m_bIsShaded;
	bool m_bAmbientIsContrainedToDiffuse;
	GLenum m_nPolygonMode;
	GLfloat m_vfAmbient[4]; 
	GLfloat m_vfDiffuse[4]; 
	GLfloat m_vfSpecular[4]; 
	GLfloat m_vfEmissive[4]; 
	GLfloat m_fShininess; 
	TextureMap* m_pDiffuseColorMap;
	TextureMap* m_pOpacityMap;
	std::vector<_Visual*> m_visuals;
};

#endif