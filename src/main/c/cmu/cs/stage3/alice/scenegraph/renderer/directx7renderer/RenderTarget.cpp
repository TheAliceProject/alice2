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

#include "FullScreenDisplayMode.hpp"
#include "RenderTarget.hpp"
#include "RenderCanvas.hpp"
#include "SymmetricPerspectiveCamera.hpp"
#include <math.h>

int RenderTarget::CommitIfNecessary( DisplayDevice* pDisplayDevice ) {
	LPDIRECTDRAW7 pDD;
	CHECK_SUCCESS( m_pDisplayDriver->GetDD( pDD ) );
	
	CHECK_SUCCESS( AcquireBackBuffer( pDD ) );
	CHECK_NOT_NULL( m_pDDSurfaceBackBuffer );
	if( m_pDDSurfaceBackBuffer->IsLost() ) {
		CHECK_SUCCESS( m_pDDSurfaceBackBuffer->Restore() );
	}

	memset( &m_sDDSD, 0, sizeof( DDSURFACEDESC2 ) );
	m_sDDSD.dwSize = sizeof( DDSURFACEDESC2 );
	m_pDDSurfaceBackBuffer->GetSurfaceDesc( &m_sDDSD );

	if( m_pDDSurfaceZBuffer==NULL ) {
		// Set up ZBuffer description
		memset( &m_sZDDSD, 0, sizeof( DDSURFACEDESC2 ) );
		m_sZDDSD.dwSize = sizeof( DDSURFACEDESC2 );
		m_sZDDSD.dwFlags = DDSD_CAPS|DDSD_WIDTH|DDSD_HEIGHT|DDSD_PIXELFORMAT;
		m_sZDDSD.ddsCaps.dwCaps = DDSCAPS_ZBUFFER;
		m_sZDDSD.dwWidth = m_sDDSD.dwWidth; 
		m_sZDDSD.dwHeight = m_sDDSD.dwHeight;
		if( pDisplayDevice->IsHardwareAccelerated() ) {
			m_sZDDSD.ddsCaps.dwCaps |= DDSCAPS_VIDEOMEMORY;
		} else {
			m_sZDDSD.ddsCaps.dwCaps |= DDSCAPS_SYSTEMMEMORY;
		}

		const size_t BIT_DEPTH_COUNT = 3;
		int vnBitDepth[ BIT_DEPTH_COUNT ] = { 32, 16, 24 };
		for( int i=0; i<BIT_DEPTH_COUNT; i++ ) {
			if( pDisplayDevice->GetZBufferPixelFormat( vnBitDepth[ i ], m_sZDDSD.ddpfPixelFormat ) == S_OK ) {
				if( pDD->CreateSurface( &m_sZDDSD, &m_pDDSurfaceZBuffer, NULL ) == S_OK ) {
					break;
				}
			}
		}
		CHECK_NOT_NULL( m_pDDSurfaceZBuffer );

		memset( &m_sZDDSD, 0, sizeof( DDSURFACEDESC2 ) );
		m_sZDDSD.dwSize = sizeof( DDSURFACEDESC2 );
		CHECK_SUCCESS( m_pDDSurfaceZBuffer->GetSurfaceDesc( &m_sZDDSD ) );
		
		CHECK_SUCCESS( m_pDDSurfaceBackBuffer->AddAttachedSurface( m_pDDSurfaceZBuffer ) );
	}
	CHECK_NOT_NULL( m_pDDSurfaceZBuffer );
	if( m_pDDSurfaceZBuffer->IsLost() ) {
		CHECK_SUCCESS( m_pDDSurfaceZBuffer->Restore() );
	}
	return S_OK;
}

int RenderTarget::CommitIfNecessary() {
	if( m_pActualDisplayDevice ) {
		//pass
	} else {
		if( m_pDisplayDriver==NULL ) {
			CHECK_SUCCESS( m_pRenderer->GetDefaultDisplayDriver( m_pDisplayDriver ) );
		}
		CHECK_NOT_NULL( m_pDisplayDriver );
		if( m_pDesiredDisplayDevice==NULL ) {
			long nDisplayModeBitDepth = -1;
			CHECK_SUCCESS( m_pDisplayDriver->GetDisplayDepth( nDisplayModeBitDepth ) );
			CHECK_SUCCESS( m_pDisplayDriver->GetDefaultDisplayDevice( nDisplayModeBitDepth, m_pDesiredDisplayDevice ) );
		}
		CHECK_NOT_NULL( m_pDesiredDisplayDevice );
		CHECK_TRUTH( m_nDesiredWidth>0 && m_nDesiredHeight>0 );
		
		if( CommitIfNecessary( m_pDesiredDisplayDevice ) == S_OK ) {
			m_pActualDisplayDevice = m_pDesiredDisplayDevice;
		} else {
			static bool s_bIsFirstTime = true;
			if( s_bIsFirstTime ) {
				MessageBox( m_pRenderCanvas->GetHWnd(), "WARNING: cannot use desired hardware rendering.  The video card may not be up to snuff.  Attempting to resort to software rendering.", "Resorting to software rendering.", MB_OK );
				s_bIsFirstTime = false;
			}
			
			Release();

			DisplayDevice* pSoftwareDisplayDevice;
			m_pDisplayDriver->GetRGBDisplayDevice( pSoftwareDisplayDevice );
			CHECK_SUCCESS( CommitIfNecessary( pSoftwareDisplayDevice ) );
			m_pActualDisplayDevice = pSoftwareDisplayDevice;
		}
		CHECK_NOT_NULL( m_pActualDisplayDevice );
		CHECK_SUCCESS( m_pDisplayDriver->DisplayVideoMemoryDiagnosticInformation() );
	}
	return S_OK;
}

int RenderTarget::UpdateDeviceIfNecessary( LPDIRECT3DDEVICE7 pD3DDevice ) {
	if( m_pRenderCanvas ) {
		if( m_pRenderCanvas->IsExclusive() ) {
			return S_OK;
		}
	}
	CHECK_SUCCESS( pD3DDevice->SetRenderTarget( m_pDDSurfaceBackBuffer, 0 ) );
	return S_OK;
}
int RenderTarget::AcquireBackBuffer( LPDIRECTDRAW7 pDD ) {
	if( m_pDDSurfaceBackBuffer==NULL ) {
		if( m_pRenderCanvas ) {
			if( m_pRenderCanvas->IsExclusive() ) {
				return m_pDisplayDriver->AcquireExclusive( m_pRenderCanvas->GetFullscreenDisplayMode(), m_pDDSurfaceBackBuffer );
			}
		}
		CHECK_TRUTH( m_nDesiredWidth>0 && m_nDesiredHeight>0 );
		DDSURFACEDESC2 sDDSDBack;
		memset( &sDDSDBack, 0, sizeof( DDSURFACEDESC2 ) );
		sDDSDBack.dwSize = sizeof( DDSURFACEDESC2 );
		sDDSDBack.dwFlags = DDSD_CAPS | DDSD_WIDTH | DDSD_HEIGHT;
		sDDSDBack.ddsCaps.dwCaps = DDSCAPS_OFFSCREENPLAIN | DDSCAPS_3DDEVICE;
		sDDSDBack.dwWidth  = m_nDesiredWidth;
		sDDSDBack.dwHeight = m_nDesiredHeight;
		CHECK_SUCCESS( pDD->CreateSurface( &sDDSDBack, &m_pDDSurfaceBackBuffer, NULL ) );
	}
	return S_OK;
}

extern void ConvertPixels( DDSURFACEDESC2 sDDSD, DWORD* pPixelsB, bool bFromAToB );

//todo: handle nX, nY, nWidth, nHeight
int GetPixels( DisplayDevice* pDisplayDevice, LPDIRECTDRAWSURFACE7 pDDSurfaceOriginal, bool bBltSupported, bool bFlipY, long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long* vnPixels ) {
	RECT sRectSrc;
	sRectSrc.left = nX;
	sRectSrc.top = nY;
	sRectSrc.right = nX+nWidth;
 	sRectSrc.bottom = nY+nHeight;

	LPDIRECTDRAWSURFACE7 pDDSurfaceTemp = NULL;
	LPDIRECTDRAWSURFACE7 pDDSurfaceSrc;

	pDDSurfaceSrc = pDDSurfaceOriginal;
	switch( nBitCount ) {
	case 32:
	case 24:
		break;
	default:
		if( bBltSupported ) {
			LPDIRECTDRAW7 pDD;
			CHECK_SUCCESS( pDDSurfaceOriginal->GetDDInterface( (void**)&pDD ) );

			DDSURFACEDESC2 sDDSDTemp;
			memset( &sDDSDTemp, 0, sizeof( DDSURFACEDESC2 ) );
			sDDSDTemp.dwSize = sizeof( DDSURFACEDESC2 );
			sDDSDTemp.dwFlags = DDSD_CAPS | DDSD_WIDTH | DDSD_HEIGHT | DDSD_PIXELFORMAT;
			sDDSDTemp.ddsCaps.dwCaps = DDSCAPS_OFFSCREENPLAIN;
			sDDSDTemp.dwWidth = nWidth; 
			sDDSDTemp.dwHeight = nHeight;

			if( pDisplayDevice->GetTexturePixelFormat( 32, false, sDDSDTemp.ddpfPixelFormat ) == S_OK ) {
				CHECK_SUCCESS( pDD->CreateSurface( &sDDSDTemp, &pDDSurfaceTemp, NULL ) );
				CHECK_NOT_NULL( pDDSurfaceTemp );

				while( pDDSurfaceOriginal->GetBltStatus( DDGBS_CANBLT ) != DD_OK ) {
					//pass
				}
				if( pDDSurfaceTemp->Blt( NULL, pDDSurfaceOriginal, &sRectSrc, DDBLT_WAIT, NULL ) == S_OK ) {
					pDDSurfaceSrc = pDDSurfaceTemp;

					sRectSrc.left = 0;
					sRectSrc.top = 0;
					sRectSrc.right = nWidth;
 					sRectSrc.bottom = nHeight;
					nBitCount = sDDSDTemp.ddpfPixelFormat.dwRGBBitCount;
				}
			}
		}
	}

	DDSURFACEDESC2 sDDSDSrc;
	memset( &sDDSDSrc, 0, sizeof( DDSURFACEDESC2 ) );
	sDDSDSrc.dwSize = sizeof( DDSURFACEDESC2 );
	CHECK_SUCCESS( pDDSurfaceSrc->Lock( &sRectSrc, &sDDSDSrc, DDLOCK_READONLY | DDLOCK_NOSYSLOCK, NULL ) );

	int nPitchDst = nWidth*4;
	switch( nBitCount ) {
	case 32: {
		if( bFlipY ) {
			byte* pbPixels = (byte*)sDDSDSrc.lpSurface;
			for( int j=0; j<nHeight; j++ ) {
				byte* pbPixelsDst = ((byte*)vnPixels) + (nHeight-1-j)*nPitchDst;
				memcpy( pbPixelsDst, pbPixels, nPitchDst );
				pbPixels += sDDSDSrc.lPitch;
				pbPixelsDst += nPitchDst;
			}
		} else {
			if( nPitchDst == sDDSDSrc.lPitch ) {
				memcpy( vnPixels, sDDSDSrc.lpSurface, nHeight*nPitchDst );
			} else {
				byte* pbPixels = (byte*)sDDSDSrc.lpSurface;
				byte* pbPixelsDst = (byte*)vnPixels;
				for( int j=0; j<nHeight; j++ ) {
					memcpy( pbPixelsDst, pbPixels, nPitchDst );
					pbPixels += sDDSDSrc.lPitch;
					pbPixelsDst += nPitchDst;
				}
			}
		}
		break; }
	case 24: {
		byte* pbPixels = (byte*)sDDSDSrc.lpSurface;
		byte* pbPixelsDst = (byte*)vnPixels;
		for( int j=0; j<nHeight; j++ ) {
			if( bFlipY ) {
				pbPixelsDst = ((byte*)vnPixels) + (nHeight-1-j)*nPitchDst;
			}
			int nPitchLeft = sDDSDSrc.lPitch;
			for( int i=0; i<nWidth; i++ ) {
				*pbPixelsDst++ = *pbPixels++;
				*pbPixelsDst++ = *pbPixels++;
				*pbPixelsDst++ = *pbPixels++;
				*pbPixelsDst++ = 0xFF;
				nPitchLeft -= 3;
			}
			pbPixels += nPitchLeft;
		}
		break; }
	case 16: {
		//todo: handle bFlipY
		::ConvertPixels( sDDSDSrc, (DWORD*)vnPixels, false );
		break; }
	default:
		CHECK_SUCCESS( -1 );
	} 

	CHECK_SUCCESS( pDDSurfaceSrc->Unlock( &sRectSrc ) );

	SAFE_RELEASE( pDDSurfaceTemp );
	return S_OK;
}

int RenderTarget::GetPixels( long nX, long nY, long nWidth, long nHeight, long nPitch, long nBitCount, long nRedBitMask, long nGreenBitMask, long nBlueBitMask, long nAlphaBitMask, long* vnPixels ) {
	CHECK_SUCCESS( CommitIfNecessary() );

	CHECK_NOT_NULL( vnPixels );
	CHECK_TRUTH( nPitch == m_sDDSD.lPitch );
	CHECK_TRUTH( nBitCount == m_sDDSD.ddpfPixelFormat.dwRGBBitCount );
	CHECK_TRUTH( nRedBitMask == m_sDDSD.ddpfPixelFormat.dwRBitMask );
	CHECK_TRUTH( nGreenBitMask == m_sDDSD.ddpfPixelFormat.dwGBitMask );
	CHECK_TRUTH( nBlueBitMask == m_sDDSD.ddpfPixelFormat.dwBBitMask );
	CHECK_TRUTH( nAlphaBitMask == m_sDDSD.ddpfPixelFormat.dwRGBAlphaBitMask );

	CHECK_SUCCESS( ::GetPixels( m_pActualDisplayDevice, m_pDDSurfaceBackBuffer, true, false, nX, nY, nWidth, nHeight, nPitch, nBitCount, vnPixels ) );

	return S_OK;
}

int RenderTarget::GetZBufferPixels( long nX, long nY, long nWidth, long nHeight, long nZBufferPitch, long nZBufferBitCount, long* vnZBufferPixels ) {
	CHECK_SUCCESS( CommitIfNecessary() );

	CHECK_NOT_NULL( vnZBufferPixels );
	CHECK_TRUTH( nZBufferPitch == m_sZDDSD.lPitch );
	CHECK_TRUTH( nZBufferBitCount == m_sZDDSD.ddpfPixelFormat.dwZBufferBitDepth );

	CHECK_SUCCESS( ::GetPixels( m_pActualDisplayDevice, m_pDDSurfaceZBuffer, false, false, nX, nY, nWidth, nHeight, nZBufferPitch, nZBufferBitCount, vnZBufferPixels ) );
	return S_OK;
}

/*
int RenderTarget::HACK_DoorTrick( SymmetricPerspectiveCamera* pCamera, double vfDoor[ 4 ][ 3 ], Scene* pOutside, _Visual** vpVisuals, int nVisualCount ) {
	CHECK_SUCCESS( CommitIfNecessary() );
	LPDIRECT3DDEVICE7 pD3DDevice;
	CHECK_SUCCESS( m_pDisplayDevice->GetD3DDevice( m_pDDSurfaceBackBuffer, pD3DDevice ) );
	CHECK_SUCCESS( UpdateDeviceIfNecessary( pD3DDevice ) );
	
	Viewport iOuterViewport;
	Viewport iActualViewport;
	CHECK_SUCCESS( GetOuterViewport( pCamera, iOuterViewport ) );
	CHECK_SUCCESS( GetActualViewport( pCamera, iActualViewport.nX, iActualViewport.nY, iActualViewport.nWidth, iActualViewport.nHeight ) );

	DWORD flags = D3DCLEAR_ZBUFFER;
	RECT rc;
	rc.left = iActualViewport.nX;
	rc.top = iActualViewport.nY;
	rc.right = iActualViewport.nX + iActualViewport.nWidth;
	rc.bottom = iActualViewport.nY + iActualViewport.nHeight;
	CHECK_SUCCESS( pD3DDevice->Clear( 1, (LPD3DRECT)&rc, flags, 0, 1.f, 0 ) );

	D3DVIEWPORT7 vp = { 0, 0, 1, 1, 0.f, 1.f };
	CHECK_SUCCESS( pD3DDevice->SetViewport( &vp ) );


	D3DVERTEX vD3DVertices[ 4 ];
	for( int i=0; i<4; i++ ) {
		vD3DVertices[ i ].dvX = vfDoor[ i ][ 0 ];
		vD3DVertices[ i ].dvY = vfDoor[ i ][ 1 ];
		vD3DVertices[ i ].dvZ = vfDoor[ i ][ 2 ];

		vD3DVertices[ i ].dvNX = 0; 
		vD3DVertices[ i ].dvNY = 1;
		vD3DVertices[ i ].dvNZ = 0;

		vD3DVertices[ i ].dvTU = 0; 
		vD3DVertices[ i ].dvTV = 0; 
	}

	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_FILLMODE, D3DFILL_SOLID ) );
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_SHADEMODE, D3DSHADE_FLAT ) );
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_LIGHTING, TRUE ) );
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ALPHABLENDENABLE, TRUE ) );
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_SRCBLEND, D3DBLEND_SRCALPHA ) );
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_DESTBLEND, D3DBLEND_INVSRCALPHA ) );
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_CULLMODE, D3DCULL_NONE ) );

	D3DMATERIAL7 sD3DMaterial;
	ZeroMemory( &sD3DMaterial, sizeof(D3DMATERIAL7) );
	sD3DMaterial.dcvDiffuse.r = sD3DMaterial.dcvAmbient.r = 1;
	sD3DMaterial.dcvDiffuse.g = sD3DMaterial.dcvAmbient.g = 0;
	sD3DMaterial.dcvDiffuse.b = sD3DMaterial.dcvAmbient.b = 0;
	sD3DMaterial.dcvDiffuse.a = sD3DMaterial.dcvAmbient.a = 0;
	CHECK_SUCCESS( pD3DDevice->SetMaterial( &sD3DMaterial ) );
	CHECK_SUCCESS( pD3DDevice->SetTexture( 0, NULL ) );

	CHECK_SUCCESS( pCamera->Setup( this, pD3DDevice, iActualViewport ) );
	CHECK_SUCCESS( pD3DDevice->SetTransform( D3DTRANSFORMSTATE_WORLD, &g_sD3DMIdentity ) );
	CHECK_SUCCESS( pD3DDevice->DrawPrimitive( D3DPT_TRIANGLEFAN, D3DFVF_VERTEX, vD3DVertices, 4, NULL ) );
	
	CHECK_SUCCESS( pD3DDevice->SetRenderState( D3DRENDERSTATE_ALPHABLENDENABLE, FALSE ) );

	CHECK_SUCCESS( pD3DDevice->BeginScene() );
	CHECK_SUCCESS_WITH_CLEANUP( pOutside->Render( this, pD3DDevice, pCamera ), pD3DDevice->EndScene );

	Scene* pScene;
	CHECK_SUCCESS_WITH_CLEANUP( pCamera->GetScene( pScene ), pD3DDevice->EndScene );
	CHECK_SUCCESS_WITH_CLEANUP( pScene->HACK_Render( this, pD3DDevice, pCamera, vpVisuals, nVisualCount ), pD3DDevice->EndScene );

	CHECK_SUCCESS( pD3DDevice->EndScene() );

	return S_OK;
}
*/

//#define EDGE(A,B) (((A) & 0x1F)-((B) & 0x1F))*(((A) & 0x1F)-((B) & 0x1F))
//inline long EDGE( short a, short b ) {
//	long dr = (a & 0x1F)-(b & 0x1F);
//	return dr*dr;
//}

int RenderTarget::RenderSilhouette() {
	if( m_fSilhouetteThickness > 0.0 ) {
		RECT rect;
		rect.left=0;
		rect.top=0;
		rect.right=m_sDDSD.dwWidth-1;
 		rect.bottom=m_sDDSD.dwHeight-1;

		DDSURFACEDESC2 ddsdColor;
		DDSURFACEDESC2 ddsdZ;

		memset( &ddsdColor, 0, sizeof( DDSURFACEDESC2 ) );
		ddsdColor.dwSize = sizeof( DDSURFACEDESC2 );

		memset( &ddsdZ, 0, sizeof( DDSURFACEDESC2 ) );
		ddsdZ.dwSize = sizeof( DDSURFACEDESC2 );

		CHECK_SUCCESS( m_pDDSurfaceBackBuffer->Lock( &rect, &ddsdColor, DDLOCK_SURFACEMEMORYPTR, NULL ) );
		CHECK_NOT_NULL( ddsdColor.lpSurface );
		CHECK_SUCCESS( m_pDDSurfaceZBuffer->Lock( &rect, &ddsdZ, DDLOCK_SURFACEMEMORYPTR | DDLOCK_READONLY | DDLOCK_NOSYSLOCK, NULL ) );
		CHECK_NOT_NULL( ddsdZ.lpSurface );

		if( ddsdColor.ddpfPixelFormat.dwRGBBitCount != 16 ) {
		} else if ( ddsdColor.ddpfPixelFormat.dwZBufferBitDepth != 16 ) {
		} else {
			long nStrideColor = ddsdColor.lPitch/sizeof( short );
			long nStrideZ = ddsdZ.lPitch/sizeof( short );

			unsigned short* pnPrevColor = (unsigned short*)ddsdColor.lpSurface;
			unsigned short* pnCurrColor = pnPrevColor + nStrideColor;
			unsigned short* pnNextColor = pnCurrColor + nStrideColor;

			unsigned short* pnPrevZ = (unsigned short*)ddsdZ.lpSurface;
			unsigned short* pnCurrZ = pnPrevZ + nStrideZ;
			unsigned short* pnNextZ = pnCurrZ + nStrideZ;

			long z0;
			long zxn, zxp, zyn, zyp;
			long dx, dy;
			//long dz1, dz2;
			//long pdx, pdy;
			//long dx2, dy2;
			long zThreshold = 1000;
			//long colorThreshold = 100;
			for (int y = rect.top+1; y < rect.bottom-1; y++) {
				//pdx = pnCurrZ[ 2 ] - pnCurrZ[ 0 ];
				//pdy = 0;
				for (int x = rect.left+1; x < rect.right-1; x++) {

					z0 = pnCurrZ[x];
					zxp = pnCurrZ[x-1];
					zxn = pnCurrZ[x+1];
					zyn = pnNextZ[x];
					zyp = pnPrevZ[x];

					dx = zxn - z0;
					dy = zyn - z0;

					//dx2 = dx - pdx;
					//dy2 = dy - pdy;

					if( abs( dx ) > zThreshold ) { //|| abs( dx2 ) > threshold2 ) { 
						if( m_fSilhouetteThickness > 0.5 ) {
							pnCurrColor[x] = 0;
						}
						if( m_fSilhouetteThickness > 1.5 ) {
							pnCurrColor[x-1] = 0;
						}
						if( m_fSilhouetteThickness > 2.5 ) {
							pnCurrColor[x+1] = 0;
						}
					} else if( abs( dy ) > zThreshold ) { //|| abs( dy2 ) > threshold2  ) {
						if( m_fSilhouetteThickness > 0.5 ) {
							pnCurrColor[x] = 0;
						}
						if( m_fSilhouetteThickness > 1.5 ) {
							pnPrevColor[x] = 0;
						}
						if( m_fSilhouetteThickness > 2.5 ) {
							pnNextColor[x] = 0;
						}
					//} else if( EDGE( pnCurrColor[x], pnNextColor[x] ) > colorThreshold || EDGE( pnCurrColor[x], pnCurrColor[x+1] ) > colorThreshold ) {
					//	pnCurrColor[x] = 0;
					}

					//pdx = dx;
					//pdy = dy;

					/*
					dz1 = abs( z0-zxn );
					dz2 = abs( zxp-z0 );

					if( dz1 > ( threshold * dz2 ) || dz2 > ( threshold * dz1 ) ) {
						if( m_fSilhouetteThickness > 0.5 ) {
							pnCurrColor[x] = 0;
						}
						if( m_fSilhouetteThickness > 1.5 ) {
							pnCurrColor[x+1] = 0;
						}
						if( m_fSilhouetteThickness > 2.5 ) {
							pnCurrColor[x-1] = 0;
						}
					}

					dz1 = abs( z0-zyn );
					dz2 = abs( zyp-z0 );
			
					if( dz1 > ( threshold * dz2 ) || dz2 > ( threshold * dz1 ) ) {
						if( m_fSilhouetteThickness > 0.5 ) {
							pnCurrColor[x] = 0;
						}
						if( m_fSilhouetteThickness > 1.5 ) {
							pnNextColor[x] = 0;
						}
						if( m_fSilhouetteThickness > 2.5 ) {
							pnPrevColor[x] = 0;
						}
					}
					*/

				}
				pnPrevColor += nStrideColor;
				pnCurrColor += nStrideColor;
				pnNextColor += nStrideColor;
				pnPrevZ += nStrideZ;
				pnCurrZ += nStrideZ;
				pnNextZ += nStrideZ;
			}
		}

		CHECK_SUCCESS( m_pDDSurfaceBackBuffer->Unlock( NULL ) );
		CHECK_SUCCESS( m_pDDSurfaceZBuffer->Unlock( NULL ) );
		
	}

	return S_OK;
}


