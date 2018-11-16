#ifndef RENDER_TARGET_INCLUDED
#define RENDER_TARGET_INCLUDED

#include "Renderer.hpp"
#include "Camera.hpp"
#include "OrthographicCamera.hpp"
#include "PerspectiveCamera.hpp"
#include "SymmetricPerspectiveCamera.hpp"
#include "Scene.hpp"

class Background;
class Geometry;
class TextureMap;

class _Visual;
class RenderCanvas;

#include <map>
typedef std::map< Camera*, Viewport > CameraToViewportMap;
typedef std::map< Camera*, bool > CameraToIsLetterboxedMap;
typedef std::map< OrthographicCamera*, bool > OrthographicCameraToRendersOnEdgeTrianglesAsLinesMap;

class RenderTarget {
public:
	RenderTarget( Renderer* pRenderer ) {
		m_pRenderer = pRenderer;
		m_nWidth = -1;
		m_nHeight = -1;
		m_nNextLightID = GL_LIGHT0;
		m_nNextClippingPlaneID = GL_CLIP_PLANE0;
		m_fSilhouetteThickness = 0.0;
	}
	virtual ~RenderTarget() {
		Release();
	}
	int Release() {
		return S_OK;
	}
	int Reset() {
		return Release();
	}
	int SetRenderCanvas( RenderCanvas* pRenderCanvas ) {
		m_pRenderCanvas = pRenderCanvas;
		return S_OK;
	}

	int GetRenderer( Renderer*& pRenderer ) {
		pRenderer = m_pRenderer;
		return S_OK;
	}
	int SetDisplayDriver( DisplayDriver* pDisplayDriver ) {
		return S_OK;
	}
	int GetActualDisplayDevice( DisplayDevice*& pDisplayDevice ) {
		return S_OK;
	}
	int GetDesiredDisplayDevice( DisplayDevice*& pDisplayDevice ) {
		return S_OK;
	}
	int SetDesiredDisplayDevice( DisplayDevice* pDisplayDevice ) {
		return S_OK;
	}
	int SetDesiredSize( long nWidth, long nHeight ) {
		if( m_nWidth!=nWidth || m_nHeight!=nHeight ) {
			m_nWidth = nWidth;
			m_nHeight = nHeight;
		}
		return S_OK;
	}
	int Clear( Camera* pCamera, bool bAll ) {
		CHECK_SUCCESS( Prologue() );

		//todo: handle bAll
		Viewport iOuterViewport;
		Viewport iActualViewport;

		CHECK_SUCCESS( GetOuterViewport( pCamera, iOuterViewport ) );
		CHECK_SUCCESS( GetActualViewport( pCamera, iActualViewport.nX, iActualViewport.nY, iActualViewport.nWidth, iActualViewport.nHeight ) );
		CHECK_SUCCESS( pCamera->Clear( this, NULL, iOuterViewport, iActualViewport ) );

		CHECK_SUCCESS( Epilogue() );
		return S_OK;
	}

	int Render( Camera* pCamera ) {
		CHECK_SUCCESS( Prologue() );

		Viewport iActualViewport;
		CHECK_SUCCESS( GetActualViewport( pCamera, iActualViewport.nX, iActualViewport.nY, iActualViewport.nWidth, iActualViewport.nHeight ) );

		CHECK_SUCCESS( ResetAffectors() );

		CHECK_GL( glEnable( GL_DEPTH_TEST ) );

		HACK_s_pCurrentTextureMap = NULL;
		CHECK_SUCCESS( pCamera->Setup( this, NULL, iActualViewport ) );

		Scene* pScene;
		CHECK_SUCCESS( pCamera->GetScene( pScene ) );
		CHECK_SUCCESS( pScene->Render( this, NULL, pCamera ) );

		CHECK_GL( glFlush() );

		CHECK_SUCCESS( Epilogue() );
		return S_OK;
	}

	int Pick( Camera* pCamera, long nX, long nY, bool isSubElementRequired, bool isOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ ) {
		CHECK_SUCCESS( Prologue() );

		Viewport iActualViewport;
		CHECK_SUCCESS( GetActualViewport( pCamera, iActualViewport.nX, iActualViewport.nY, iActualViewport.nWidth, iActualViewport.nHeight ) );

		CHECK_SUCCESS( pCamera->Pick( m_pRenderer, nX, (m_nHeight-1)-nY, iActualViewport, isSubElementRequired, isOnlyFrontMostRequired, pVisual, bIsFrontFacingAppearance, nSubElement, fZ ) );

		CHECK_SUCCESS( Epilogue() );
		return S_OK;
	}

	//todo remove
	bool HACK_NextTextureMap( TextureMap* pNextTextureMap ) {
		bool b = ( pNextTextureMap == HACK_s_pCurrentTextureMap );
		HACK_s_pCurrentTextureMap = pNextTextureMap;
		return b;
	}

	int GetNextLightID( GLenum& nLightID ) {
		nLightID = m_nNextLightID;
		m_nNextLightID++;
		return S_OK;
	}
	int GetNextClippingPlaneID( GLenum& nClippingPlaneID ) {
		nClippingPlaneID = m_nNextClippingPlaneID;
		m_nNextClippingPlaneID++;
		return S_OK;
	}

	int AcquireDisplayList( Geometry* pGeometry, GLuint& nDisplayListID, bool& bIsNew ) {
		bIsNew = m_mapDisplayLists.find( pGeometry ) == m_mapDisplayLists.end();
		if( bIsNew ) {
			nDisplayListID = glGenLists( 1 );
			m_mapDisplayLists[ pGeometry ] = nDisplayListID;
		} else {
			nDisplayListID = m_mapDisplayLists[ pGeometry ];
		}
		return S_OK;
	}
	int ReleaseDisplayList( Geometry* pGeometry, GLuint nDisplayListID ) {
		CHECK_GL( glDeleteLists( nDisplayListID, 1 ) );
		m_mapDisplayLists.erase( pGeometry );
		return S_OK;
	}

	int AcquireTextureObject( TextureMap* pTextureMap, GLuint& nTextureObjectID, bool& bIsNew ) {
		bIsNew = m_mapTextureObjects.find( pTextureMap ) == m_mapTextureObjects.end();
		if( bIsNew ) {
			CHECK_GL( glGenTextures( 1, &nTextureObjectID ) );
			m_mapTextureObjects[ pTextureMap ] = nTextureObjectID;
		} else {
			nTextureObjectID = m_mapTextureObjects[ pTextureMap ];
		}
		return S_OK;
	}
	int ReleaseTextureObject( TextureMap* pTextureMap, GLuint nTextureObjectID ) {
		CHECK_GL( glDeleteTextures( 1, &nTextureObjectID ) );
		m_mapTextureObjects.erase( pTextureMap );
		return S_OK;
	}

	int ResetAffectors() {
		int i;
		GLfloat vfDefaultPosition[] = { 0,0,1,0 };
		GLfloat vfDefaultSpotDirection[] = { 0,0,-1 };
		for( i=GL_LIGHT0; i<m_nNextLightID; i++ ) {
			CHECK_GL( glDisable( i ) );
			CHECK_GL( glLightfv( i, GL_AMBIENT, g_vfBlack ) );
			CHECK_GL( glLightfv( i, GL_DIFFUSE, g_vfBlack ) );
			CHECK_GL( glLightfv( i, GL_SPECULAR, g_vfBlack ) );
			CHECK_GL( glLightfv( i, GL_POSITION, vfDefaultPosition ) );
			CHECK_GL( glLightfv( i, GL_SPOT_DIRECTION, vfDefaultSpotDirection ) );
			CHECK_GL( glLightf( i, GL_CONSTANT_ATTENUATION, 1.0f ) );
			CHECK_GL( glLightf( i, GL_LINEAR_ATTENUATION, 0.0f ) );
			CHECK_GL( glLightf( i, GL_QUADRATIC_ATTENUATION, 0.0f ) );
			CHECK_GL( glLightf( i, GL_SPOT_EXPONENT, 0.0f ) );
			CHECK_GL( glLightf( i, GL_SPOT_CUTOFF, 180.0f ) );
		}
		m_nNextLightID = GL_LIGHT0;

		for( i=GL_CLIP_PLANE0; i<m_nNextClippingPlaneID; i++ ) {
			CHECK_GL( glDisable( i ) );
		}
		m_nNextClippingPlaneID = GL_CLIP_PLANE0;
		return S_OK;
	}


	int Blt( TextureMap* pTextureMap ) {
		return S_OK;
	}

	int GetWidth( long& nWidth ) {
		nWidth = m_nWidth;
		return S_OK;
	}
	int GetHeight( long& nHeight ) {
		nHeight = m_nHeight;
		return S_OK;
	}
	int GetPitch( long& nPitch ) {
		//todo
		nPitch = m_nWidth*4;
		return S_OK;
	}
	int GetBitCount( long& nBitCount ) {
		//todo
		nBitCount = 32;
		return S_OK;
	}
	int GetRedBitMask( long& nBitCount ) {
		//todo
		nBitCount = 0x00FF0000;
		return S_OK;
	}
	int GetGreenBitMask( long& nRedBitMask ) {
		//todo
		nRedBitMask = 0x0000FF00;
		return S_OK;
	}
	int GetBlueBitMask( long& nGreenBitMask ) {
		//todo
		nGreenBitMask = 0x000000FF;
		return S_OK;
	}
	int GetAlphaBitMask( long& nAlphaBitMask ) {
		//todo
		nAlphaBitMask = 0xFF000000;
		return S_OK;
	}
	int GetPixels( long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long nRedBitMask, long nGreenBitMask, long nBlueBitMask, long nAlphaBitMask, long* vnPixels ) {
		return S_OK;
	}

	int GetWidth( TextureMap* pTextureMap, long& nWidth ) {
		nWidth = -1;
		return S_OK;
	}
	int GetHeight( TextureMap* pTextureMap, long& nHeight ) {
		nHeight = -1;
		return S_OK;
	}
	int GetPitch( TextureMap* pTextureMap, long& nPitch ) {
		//todo
		nPitch = -1;
		return S_OK;
	}
	int GetBitCount( TextureMap* pTextureMap, long& nBitCount ) {
		//todo
		nBitCount = -1;
		return S_OK;
	}
	int GetRedBitMask( TextureMap* pTextureMap, long& nRedBitMask ) {
		//todo
		nRedBitMask = -1;
		return S_OK;
	}
	int GetGreenBitMask( TextureMap* pTextureMap, long& nGreenBitMask ) {
		//todo
		nGreenBitMask = -1;
		return S_OK;
	}
	int GetBlueBitMask( TextureMap* pTextureMap, long& nBlueBitMask ) {
		//todo
		nBlueBitMask = -1;
		return S_OK;
	}
	int GetAlphaBitMask( TextureMap* pTextureMap, long& nAlphaBitMask ) {
		//todo
		nAlphaBitMask = -1;
		return S_OK;
	}

	int GetPixels( TextureMap* pTextureMap, long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long nRedBitMask, long nGreenBitMask, long nBlueBitMask, long nAlphaBitMask, long* vnPixels ) {
		return S_OK;
	}

	int GetZBufferPitch( long& nZBufferPitch ) {
		nZBufferPitch = 0;
		//todo
		return S_OK;
	}
	int GetZBufferBitCount( long& nZBufferBitCount ) {
		nZBufferBitCount = 0;
		//todo
		return S_OK;
	}
	int GetZBufferPixels( long nX, long nY, long nWidth, long nHeight, long nZBufferPitch, long nZBufferBitCount, long* vnZBufferPixels ) {
		return S_OK;
	}

	int GetIsLetterboxedAsOpposedToDistorted( Camera* pCamera, bool& bIsLetterboxedAsOpposedToDistorted ) {
		CameraToIsLetterboxedMap::iterator iter = m_cameraToIsLetterboxedMap.find( pCamera );
		if( iter==m_cameraToIsLetterboxedMap.end() ) {
			bIsLetterboxedAsOpposedToDistorted = true;
		} else {
			bIsLetterboxedAsOpposedToDistorted = iter->second;
		}
		return S_OK;
	}
	int SetIsLetterboxedAsOpposedToDistorted( Camera* pCamera, bool bIsLetterboxedAsOpposedToDistorted ) {
		m_cameraToIsLetterboxedMap[ pCamera ] = bIsLetterboxedAsOpposedToDistorted;
		return S_OK;
	}
	
	int GetRendersOnEdgeTrianglesAsLines( OrthographicCamera* pOrthographicCamera, bool& bRendersOnEdgeTrianglesAsLines ) {
		OrthographicCameraToRendersOnEdgeTrianglesAsLinesMap::iterator iter = m_orthographicCameraToRendersOnEdgeTrianglesAsLinesMap.find( pOrthographicCamera );
		if( iter==m_orthographicCameraToRendersOnEdgeTrianglesAsLinesMap.end() ) {
			bRendersOnEdgeTrianglesAsLines = false;
		} else {
			bRendersOnEdgeTrianglesAsLines = iter->second;
		}
		return S_OK;
	}
	int SetRendersOnEdgeTrianglesAsLines( OrthographicCamera* pOrthographicCamera, bool bRendersOnEdgeTrianglesAsLines ) {
		m_orthographicCameraToRendersOnEdgeTrianglesAsLinesMap[ pOrthographicCamera ] = bRendersOnEdgeTrianglesAsLines;
		return S_OK;
	}


	int GetProjectionMatrix( Camera* pCamera, double& rc00, double& rc01, double& rc02, double& rc03, double& rc10, double& rc11, double& rc12, double& rc13, double& rc20, double& rc21, double& rc22, double& rc23, double& rc30, double& rc31, double& rc32, double& rc33 ) {
		CHECK_SUCCESS( pCamera->GetProjectionMatrix( rc00, rc01, rc02, rc03, rc10, rc11, rc12, rc13, rc20, rc21, rc22, rc23, rc30, rc31, rc32, rc33 ) );
		return S_OK;
	}
	int GetActualPlane( OrthographicCamera* pOrthographicCamera, double* vfPlane ) {
		//todo
		return S_OK;
	}
	int GetActualPlane( PerspectiveCamera* pPerspectiveCamera, double* vfPlane ) {
		//todo
		return S_OK;
	}
	int GetActualVerticalViewingAngle( SymmetricPerspectiveCamera* pSymmetricPerspectiveCamera, double& fVerticalViewingAngle ) {
		//todo
		return S_OK;
	}
	int GetActualHorizontalViewingAngle( SymmetricPerspectiveCamera* pSymmetricPerspectiveCamera, double& fVerticalViewingAngle ) {
		//todo
		return S_OK;
	}
	int GetActualViewport( Camera* pCamera, long& nViewportX, long& nViewportY, long& nViewportWidth, long& nViewportHeight ) {
		Viewport iViewport;
		CHECK_SUCCESS( GetOuterViewport( pCamera, iViewport ) );
		bool bIsLetterboxedAsOpposedToDistorted;
		CHECK_SUCCESS( GetIsLetterboxedAsOpposedToDistorted( pCamera, bIsLetterboxedAsOpposedToDistorted ) );
		if( bIsLetterboxedAsOpposedToDistorted ) {
			CHECK_SUCCESS( pCamera->LetterboxViewportIfNecessary( iViewport ) );
		}
		nViewportX = iViewport.nX;
		nViewportY = iViewport.nY;
		nViewportWidth = iViewport.nWidth;
		nViewportHeight = iViewport.nHeight;
		return S_OK;
	}
	int OnViewportChange( Camera* pCamera, long nViewportX, long nViewportY, long nViewportWidth, long nViewportHeight ) {
		CameraToViewportMap::iterator iter = m_cameraToViewportMap.find( pCamera );
		if( iter==m_cameraToViewportMap.end() ) {
			iter->second.set( nViewportX, nViewportY, nViewportWidth, nViewportHeight );
		} else {
			Viewport viewport( nViewportX, nViewportY, nViewportWidth, nViewportHeight );
			m_cameraToViewportMap[ pCamera ] = viewport;
		}
		return S_OK;
	}

	int OverlayBegin() {
		CHECK_SUCCESS( Prologue() );
		CHECK_GL( glMatrixMode( GL_PROJECTION ) );
		CHECK_GL( glLoadIdentity() );
		CHECK_GL( glOrtho( 0, m_nWidth-1, m_nHeight-1, 0, -1, 1 ) );
		CHECK_GL( glViewport( 0, 0, m_nWidth, m_nHeight ) );
		CHECK_GL( glMatrixMode( GL_MODELVIEW ) );
		CHECK_GL( glLoadIdentity() );

		CHECK_GL( glDisable( GL_DEPTH_TEST ) );
		CHECK_GL( glDisable( GL_LIGHTING ) );
		return S_OK;
	}
	int OverlayBegin( TextureMap* pTextureMap ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		return S_OK;
	}
	int OverlayEnd() {
		CHECK_SUCCESS( Prologue() );
		glFlush();
		CHECK_SUCCESS( Epilogue() );
		return S_OK;
	}


	int GetSilhouetteThickness( double& fSilhouetteThickness ) {
		fSilhouetteThickness = m_fSilhouetteThickness;
		return S_OK;
	}
	int SetSilhouetteThickness( double fSilhouetteThickness ) {
		m_fSilhouetteThickness = fSilhouetteThickness;
		return S_OK;
	}

	int HACK_DoorTrick( SymmetricPerspectiveCamera* pCamera, double vfDoor[ 4 ][ 3 ], Scene* pOutside, _Visual** vpVisuals, int nVisualCount ) {
		//never going to be implemented
		return S_OK;
	}

	int ClearDisplayLists() {
		m_mapDisplayLists.clear();
		return 0;
	}
	int ClearTextureObjects() {
		m_mapTextureObjects.clear();
		return 0;
	}


private:
	int Prologue();
	int Epilogue();
	int CommitIfNecessary();

	int GetOuterViewport( Camera* pCamera, Viewport& iViewport ) {
		CameraToViewportMap::iterator iter = m_cameraToViewportMap.find( pCamera );
		if( iter==m_cameraToViewportMap.end() ) {
			iViewport.nX = 0;
			iViewport.nY = 0;
			GetWidth( iViewport.nWidth );
			GetHeight( iViewport.nHeight );
		} else {
			iViewport = iter->second;
			if( iViewport.nWidth == -1 ) {
				GetWidth( iViewport.nWidth );
				iViewport.nWidth -= iViewport.nX;
			}
			if( iViewport.nHeight == -1 ) {
				GetHeight( iViewport.nHeight );
				iViewport.nHeight -= iViewport.nY;
			}
		}
		return S_OK;
	}

	CameraToViewportMap m_cameraToViewportMap;
	CameraToIsLetterboxedMap m_cameraToIsLetterboxedMap;
	OrthographicCameraToRendersOnEdgeTrianglesAsLinesMap m_orthographicCameraToRendersOnEdgeTrianglesAsLinesMap;

	Renderer* m_pRenderer;
	RenderCanvas* m_pRenderCanvas;
	int m_nWidth;
	int m_nHeight;

	TextureMap* HACK_s_pCurrentTextureMap;
	int m_nNextLightID;
	int m_nNextClippingPlaneID;

	std::map< Geometry*, GLuint > m_mapDisplayLists;
	std::map< TextureMap*, GLuint > m_mapTextureObjects;

	double m_fSilhouetteThickness;
};

#endif
