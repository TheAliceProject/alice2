#include "Renderer.hpp"
#include "Scene.hpp"

GLdouble g_vfIdentity[16] = { 1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1 };
GLfloat g_vfBlack[4] = { 0,0,0,1 };
GLUquadricObj* g_pGLUQuadric = NULL;

int Renderer::PerformPick( _Component* pComponent, const GLdouble* vfProjection, const GLdouble* vfView, long nX, long nY, const Viewport& iViewport, bool isSubElementRequired, bool isOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ ) {

	GLenum vnSelect[ 256 ];
	CHECK_GL( glSelectBuffer( 256, vnSelect ) );
	CHECK_GL( glRenderMode( GL_SELECT ) );

	CHECK_GL( glViewport( iViewport.nX, iViewport.nY, iViewport.nWidth, iViewport.nHeight ) );
	
	GLint vnViewport[4];
	CHECK_GL( glGetIntegerv(GL_VIEWPORT, vnViewport) );

	CHECK_GL( glMatrixMode( GL_PROJECTION ) );
	CHECK_GL( glLoadIdentity() );
	CHECK_GL( gluPickMatrix( nX, nY, 1, 1, vnViewport ) );
	CHECK_GL( glMultMatrixd( vfProjection ) );

	CHECK_GL( glMatrixMode( GL_MODELVIEW ) );
	CHECK_GL( glLoadMatrixd( vfView ) );

	Scene* pScene;
	CHECK_SUCCESS( pComponent->GetScene( pScene ) );
	CHECK_SUCCESS( pScene->Pick( this, isSubElementRequired, isOnlyFrontMostRequired ) );
	CHECK_GL( glFlush() );

	int nHitCount = glRenderMode( GL_RENDER );
	if( nHitCount ) {
		const unsigned nZMax = (unsigned)-1;
		GLenum* pnSelect = vnSelect;
		for ( int nHitIndex=0; nHitIndex<nHitCount; nHitIndex++ ) {
			unsigned nNameCount = *pnSelect;
			pnSelect++;
			fZ = *pnSelect/(double)nZMax;
			pnSelect++;
			//double fZBack = *pnSelect/(double)nZMax;
			pnSelect++;
			CHECK_TRUTH( nNameCount==3 );
			unsigned nVisualID = *pnSelect++;
			bIsFrontFacingAppearance = (*pnSelect++) != 0;
			nSubElement = *pnSelect++;
			CHECK_SUCCESS( pScene->GetVisual( nVisualID, pVisual ) );
		}
	} else {
		pVisual = NULL;
		bIsFrontFacingAppearance = false;
		nSubElement = -1;
		fZ = 0;
	}

	return S_OK;
}

int Renderer::Pick( _Component* pComponent, double fVectorX, double fVectorY, double fVectorZ, double fMinX, double fMinY, double fMaxX, double fMaxY, double fNear, double fFar, bool bIsSubElementRequired, bool bIsOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ ) {
	//todo
	//Prologue/Epilogue?
	return S_OK;
}
