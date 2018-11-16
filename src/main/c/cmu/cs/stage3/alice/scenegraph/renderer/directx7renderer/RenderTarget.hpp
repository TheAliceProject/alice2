#ifndef RENDER_TARGET_INCLUDED
#define RENDER_TARGET_INCLUDED

#include "Renderer.hpp"
#include "TextureMap.hpp"
#include "Camera.hpp"
#include "OrthographicCamera.hpp"
#include "PerspectiveCamera.hpp"
#include "SymmetricPerspectiveCamera.hpp"
#include "Scene.hpp"
#include "Viewport.hpp"

#include <map>

typedef std::map< Camera*, Viewport > CameraToViewportMap;
typedef std::map< Camera*, bool > CameraToIsLetterboxedMap;
typedef std::map< OrthographicCamera*, bool > OrthographicCameraToRendersOnEdgeTrianglesAsLinesMap;

class Light;
class Fog;
class ClippingPlane;
class _Visual;
class RenderCanvas;

class RenderTarget {
public:
	RenderTarget( Renderer* pRenderer ) {
		m_pRenderer = pRenderer;
		m_pDDSurfaceBackBuffer = NULL;
		m_pDDSurfaceZBuffer = NULL;

		m_pDisplayDriver = NULL;
		m_pDesiredDisplayDevice = NULL;
		m_pActualDisplayDevice = NULL;

		m_pRenderCanvas = NULL;

		m_nDesiredWidth = 0;
		m_nDesiredHeight = 0;

		m_nNextLightID = 0;
		m_nNextClippingPlaneID = 0;
		m_dwClippingPlane = 0;

		m_fSilhouetteThickness = 0.0;
	}
	virtual ~RenderTarget() {
		Release();
	}
	virtual int Release() {
		//todo?
		//if( m_pDesiredDisplayDevice ) {
		//	CHECK_SUCCESS( m_pDesiredDisplayDevice->Release() );
		//	m_pDesiredDisplayDevice = NULL;
		//}
		//if( m_pActualDisplayDevice ) {
		//	CHECK_SUCCESS( m_pActualDisplayDevice->Release() );
		//	m_pActualDisplayDevice = NULL;
		//}
		m_pActualDisplayDevice = NULL;
		SAFE_RELEASE( m_pDDSurfaceZBuffer );
		SAFE_RELEASE( m_pDDSurfaceBackBuffer );
		return S_OK;
	}

	int Reset() {
		CHECK_SUCCESS( Release() );
		return S_OK;
	}
	int SetRenderCanvas( RenderCanvas* pRenderCanvas ) {
		m_pRenderCanvas = pRenderCanvas;
		return S_OK;
	}

	int GetBackBuffer( LPDIRECTDRAWSURFACE7& pBackBuffer ) {
		pBackBuffer = m_pDDSurfaceBackBuffer;
		return S_OK;
	}

	int GetRenderer( Renderer*& pRenderer ) {
		pRenderer = m_pRenderer;
		return S_OK;
	}
	
	int GetDisplayDriver( DisplayDriver*& pDisplayDriver ) {
		pDisplayDriver = m_pDisplayDriver;
		return S_OK;
	}
	int SetDisplayDriver( DisplayDriver* pDisplayDriver ) {
		if( m_pDisplayDriver != pDisplayDriver ) {
			CHECK_SUCCESS( Release() );
			m_pDisplayDriver = pDisplayDriver;
		}
		return S_OK;
	}
	
	int GetActualDisplayDevice( DisplayDevice*& pDisplayDevice ) {
		pDisplayDevice = m_pActualDisplayDevice;
		return S_OK;
	}
	int GetDesiredDisplayDevice( DisplayDevice*& pDisplayDevice ) {
		pDisplayDevice = m_pDesiredDisplayDevice;
		return S_OK;
	}
	int SetDesiredDisplayDevice( DisplayDevice* pDisplayDevice ) {
		if( m_pDesiredDisplayDevice != pDisplayDevice ) {
			CHECK_SUCCESS( Release() );
			m_pDesiredDisplayDevice = pDisplayDevice;
		}
		return S_OK;
	}
	
	int Clear( Camera* pCamera, bool bAll ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( UpdateDeviceIfNecessary( pD3DDevice ) );
		
		Viewport iOuterViewport;
		Viewport iActualViewport;
		if( bAll ) {
			iOuterViewport.nX = 0;
			iOuterViewport.nY = 0;
			GetWidth( iOuterViewport.nWidth );
			GetHeight( iOuterViewport.nHeight );
		} else {
			CHECK_SUCCESS( GetOuterViewport( pCamera, iOuterViewport ) );
		}
		CHECK_SUCCESS( GetActualViewport( pCamera, iActualViewport.nX, iActualViewport.nY, iActualViewport.nWidth, iActualViewport.nHeight ) );
		CHECK_SUCCESS( pCamera->Clear( this, pD3DDevice, iOuterViewport, iActualViewport ) );
		return S_OK;
	}

	int Render( Camera* pCamera ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( UpdateDeviceIfNecessary( pD3DDevice ) );
		
		Viewport iActualViewport;
		CHECK_SUCCESS( GetActualViewport( pCamera, iActualViewport.nX, iActualViewport.nY, iActualViewport.nWidth, iActualViewport.nHeight ) );

		DWORD dwFilter;
		DWORD dwSpecularEnable;
		//DWORD dwTexturePerspective;
		if( m_pActualDisplayDevice->IsHardwareAccelerated() ) {
			dwFilter = D3DTFG_LINEAR;
			dwSpecularEnable = TRUE;
			//dwTexturePerspective = TRUE;
		} else {
			dwFilter = D3DTFG_POINT;
			dwSpecularEnable = FALSE;
			//dwTexturePerspective = FALSE;
		}

		CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 0, D3DTSS_MINFILTER, dwFilter ) );
		CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 0, D3DTSS_MAGFILTER, dwFilter ) );
		CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 1, D3DTSS_MINFILTER, dwFilter ) );
		CHECK_SUCCESS( pD3DDevice->SetTextureStageState( 1, D3DTSS_MAGFILTER, dwFilter ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_SPECULARENABLE, dwSpecularEnable ) );
		//CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_TEXTUREPERSPECTIVE, dwTexturePerspective ) );

		CHECK_SUCCESS( ResetAffectors( pD3DDevice ) );

		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_LIGHTING, TRUE ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ZENABLE, TRUE ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CULLMODE, D3DCULL_CCW ) );

		
		Scene* pScene;
		CHECK_SUCCESS( pCamera->GetScene( pScene ) );
		if( pScene ) {
			CHECK_SUCCESS( pCamera->Setup( this, pD3DDevice, iActualViewport ) );
			CHECK_SUCCESS( pD3DDevice->BeginScene() );
			CHECK_SUCCESS_WITH_CLEANUP( pScene->Render( this, pD3DDevice, pCamera ), pD3DDevice->EndScene );
			CHECK_SUCCESS( pD3DDevice->EndScene() );
		}

		CHECK_SUCCESS( RenderSilhouette() );
		return S_OK;
	}
	int RenderSilhouette();

	int Pick( Camera* pCamera, long nX, long nY, bool isSubElementRequired, bool isOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		Viewport iActualViewport;
		CHECK_SUCCESS( GetActualViewport( pCamera, iActualViewport.nX, iActualViewport.nY, iActualViewport.nWidth, iActualViewport.nHeight ) );

		CHECK_NOT_NULL( m_pRenderer );

		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pRenderer->GetPickDevice( pD3DDevice ) );

		CHECK_SUCCESS( pCamera->Pick( m_pRenderer, nX, (m_sDDSD.dwHeight-1)-nY, iActualViewport, isSubElementRequired, isOnlyFrontMostRequired, pVisual, bIsFrontFacingAppearance, nSubElement, fZ ) );

		return S_OK;
	}

	int Blt( TextureMap* pTextureMap ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		//todo
		return S_OK;
	}

	int GetWidth( long& nWidth ) {
		if( CommitIfNecessary() == S_OK ) {
			nWidth = m_sDDSD.dwWidth;
		} else {
			nWidth = 0;
		}
		return S_OK;
	}
	int GetHeight( long& nHeight ) {
		if( CommitIfNecessary() == S_OK ) {
			nHeight = m_sDDSD.dwHeight;
		} else {
			nHeight = 0;
		}
		return S_OK;
	}
	int GetPitch( long& nPitch ) {
		if( CommitIfNecessary() == S_OK ) {
			nPitch = m_sDDSD.lPitch;
		} else {
			nPitch = 0;
		}
		return S_OK;
	}
	int GetBitCount( long& nBitCount ) {
		if( CommitIfNecessary() == S_OK ) {
			nBitCount = m_sDDSD.ddpfPixelFormat.dwRGBBitCount;
		} else {
			nBitCount = 0;
		}
		return S_OK;
	}
	int GetRedBitMask( long& nRedBitMask ) {
		if( CommitIfNecessary() == S_OK ) {
			nRedBitMask = m_sDDSD.ddpfPixelFormat.dwRBitMask;
		} else {
			nRedBitMask = 0;
		}
		return S_OK;
	}
	int GetGreenBitMask( long& nGreenBitMask ) {
		if( CommitIfNecessary() == S_OK ) {
			nGreenBitMask = m_sDDSD.ddpfPixelFormat.dwGBitMask;
		} else {
			nGreenBitMask = 0;
		}
		return S_OK;
	}
	int GetBlueBitMask( long& nBlueBitMask ) {
		if( CommitIfNecessary() == S_OK ) {
			nBlueBitMask = m_sDDSD.ddpfPixelFormat.dwBBitMask;
		} else {
			nBlueBitMask = 0;
		}
		return S_OK;
	}
	int GetAlphaBitMask( long& nAlphaBitMask ) {
		if( CommitIfNecessary() == S_OK ) {
			nAlphaBitMask = m_sDDSD.ddpfPixelFormat.dwRGBAlphaBitMask;
		} else {
			nAlphaBitMask = 0;
		}
		return S_OK;
	}

	int GetPixels( long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long nRedBitMask, long nGreenBitMask, long nBlueBitMask, long nAlphaBitMask, long* vnPixels );

	int GetWidth( TextureMap* pTextureMap, long& nWidth ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetWidth( pD3DDevice, nWidth ) );
		return S_OK;
	}
	int GetHeight( TextureMap* pTextureMap, long& nHeight ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetHeight( pD3DDevice, nHeight ) );
		return S_OK;
	}
	int GetPitch( TextureMap* pTextureMap, long& nPitch ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetPitch( pD3DDevice, nPitch ) );
		return S_OK;
	}
	int GetBitCount( TextureMap* pTextureMap, long& nBitCount ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetBitCount( pD3DDevice, nBitCount ) );
		return S_OK;
	}
	int GetRedBitMask( TextureMap* pTextureMap, long& nRedBitMask ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetRedBitMask( pD3DDevice, nRedBitMask ) );
		return S_OK;
	}
	int GetGreenBitMask( TextureMap* pTextureMap, long& nGreenBitMask ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetGreenBitMask( pD3DDevice, nGreenBitMask ) );
		return S_OK;
	}
	int GetBlueBitMask( TextureMap* pTextureMap, long& nBlueBitMask ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetBlueBitMask( pD3DDevice, nBlueBitMask ) );
		return S_OK;
	}
	int GetAlphaBitMask( TextureMap* pTextureMap, long& nAlphaBitMask ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetAlphaBitMask( pD3DDevice, nAlphaBitMask ) );
		return S_OK;
	}

	int GetPixels( TextureMap* pTextureMap, long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long nRedBitMask, long nGreenBitMask, long nBlueBitMask, long nAlphaBitMask, long* vnPixels ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		CHECK_SUCCESS( pTextureMap->GetPixels( m_pActualDisplayDevice, m_pDDSurfaceBackBuffer, nX, nY, nWidth, nHeight, nPitch, nBitCount, nRedBitMask, nGreenBitMask, nBlueBitMask, nAlphaBitMask, vnPixels ) );
		return S_OK;
	}

	int GetZBufferPitch( long& nZBufferPitch ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		nZBufferPitch = m_sZDDSD.lPitch;
		return S_OK;
	}
	int GetZBufferBitCount( long& nZBufferBitCount ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		nZBufferBitCount = m_sZDDSD.ddpfPixelFormat.dwZBufferBitDepth;
		return S_OK;
	}

	int GetZBufferPixels( long nX, long nY, long nWidth, long nHeight, long nZBufferPitch, long nZBufferBitCount, long* vnZBufferPixels );

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
		if( iter!=m_cameraToViewportMap.end() ) {
			iter->second.set( nViewportX, nViewportY, nViewportWidth, nViewportHeight );
		} else {
			Viewport viewport( nViewportX, nViewportY, nViewportWidth, nViewportHeight );
			m_cameraToViewportMap[ pCamera ] = viewport;
		}
		return S_OK;
	}

	int GetDDSurface( LPDIRECTDRAWSURFACE7& pDDSurface ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		pDDSurface = m_pDDSurfaceBackBuffer;
		return S_OK;
	}
	int GetDDSurface( TextureMap* pTextureMap, LPDIRECTDRAWSURFACE7& pDDSurface ) {
		CHECK_SUCCESS( CommitIfNecessary() );
		LPDIRECT3DDEVICE7 pD3DDevice;
		CHECK_SUCCESS( m_pActualDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
		CHECK_SUCCESS( pTextureMap->GetSurface( pD3DDevice, pDDSurface ) );
		return S_OK;
	}

	int GetNextLightID( LightID& nLightID ) {
		nLightID = m_nNextLightID;
		m_nNextLightID++;
		return S_OK;
	}
	int GetNextClippingPlaneID( ClippingPlaneID& nClippingPlaneID ) {
		nClippingPlaneID = m_nNextClippingPlaneID;
		m_nNextClippingPlaneID++;
		return S_OK;
	}

	int EnableClippingPlane( LPDIRECT3DDEVICE7 pD3DDevice, ClippingPlaneID nID ) {
		m_dwClippingPlane |= (1<<nID);
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CLIPPLANEENABLE, m_dwClippingPlane ) );
		return S_OK;
	}
	int DisableClippingPlane( LPDIRECT3DDEVICE7 pD3DDevice, ClippingPlaneID nID ) {
		m_dwClippingPlane &= (~(1<<nID));
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CLIPPLANEENABLE, m_dwClippingPlane ) );
		return S_OK;
	}

	int ResetAffectors( LPDIRECT3DDEVICE7 pD3DDevice ) {
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_AMBIENT, 0x0 ) );
		for( int i=0; i<m_nNextLightID; i++ ) {
			CHECK_SUCCESS( pD3DDevice->LightEnable( i, FALSE ) );
		}
		m_nNextLightID = 0;

		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGENABLE, FALSE ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGTABLEMODE, D3DFOG_NONE ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FOGVERTEXMODE,  D3DFOG_NONE ) );
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_RANGEFOGENABLE, FALSE ) );

		m_nNextClippingPlaneID = 0;
		m_dwClippingPlane = 0;
		CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CLIPPLANEENABLE, m_dwClippingPlane ) );
		return S_OK;
	}

	int SetDesiredSize( long nWidth, long nHeight ) {
		//fprintf( stderr, "SetDesiredSize( %d,  %d )\n", nWidth, nHeight ); 
		//fflush( stderr ); 
		if( m_nDesiredWidth!=nWidth || m_nDesiredHeight!=nHeight ) {
			m_nDesiredWidth = nWidth;
			m_nDesiredHeight = nHeight;
			CHECK_SUCCESS( Release() );
		}
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

	int CommitIfNecessary( DisplayDevice* pDisplayDevice );
	int CommitIfNecessary();

private:
	int UpdateDeviceIfNecessary( LPDIRECT3DDEVICE7 pD3DDevice );
	int AcquireBackBuffer( LPDIRECTDRAW7 pDD );

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
	Renderer* m_pRenderer;
	RenderCanvas* m_pRenderCanvas;
	LPDIRECTDRAWSURFACE7 m_pDDSurfaceBackBuffer;
	LPDIRECTDRAWSURFACE7 m_pDDSurfaceZBuffer;
	DDSURFACEDESC2 m_sDDSD;
	DDSURFACEDESC2 m_sZDDSD;
	DisplayDriver* m_pDisplayDriver;
	DisplayDevice* m_pDesiredDisplayDevice;
	DisplayDevice* m_pActualDisplayDevice;
	int m_nDesiredWidth;
	int m_nDesiredHeight;

	CameraToViewportMap m_cameraToViewportMap;
	CameraToIsLetterboxedMap m_cameraToIsLetterboxedMap;
	OrthographicCameraToRendersOnEdgeTrianglesAsLinesMap m_orthographicCameraToRendersOnEdgeTrianglesAsLinesMap;
	double m_fSilhouetteThickness;
	int m_nNextLightID;
	int m_nNextClippingPlaneID;
	DWORD m_dwClippingPlane;

	//todo: use GetSystemMetrics( SM_CXSCREEN ); ?
	//float m_fMetersPerPixelX;
};

#endif
