#ifndef RENDERER_INCLUDED
#define RENDERER_INCLUDED

#include "DisplayDriver.hpp"
#include "Viewport.hpp"
class _Component;
class _Visual;
class RenderTarget;

class Renderer {
public:
	Renderer() {
		m_bIsSoftwareEmulationForced = false;
	}
	int Enumerate() {
		return S_OK;
	}
	int GetDisplayDriverCount( long& displayDriverCount ) {
		displayDriverCount = (long)m_vDisplayDrivers.size();
		return S_OK;
	}
	int GetDisplayDriverAt( long index, DisplayDriver*& displayDriverID ) {
		displayDriverID = m_vDisplayDrivers[index];
		return S_OK;
	}
	bool IsSoftwareEmulationForced() {
		return m_bIsSoftwareEmulationForced;
	}
	void SetIsSoftwareEmulationForced( bool bIsSoftwareEmulationForced ) {
		m_bIsSoftwareEmulationForced = bIsSoftwareEmulationForced;
	}
	int PerformPick( _Component* pComponent, const GLdouble* vfProjection, const GLdouble* vfView, long nX, long nY, const Viewport& iViewport, bool isSubElementRequired, bool isOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ );
	int Pick( _Component* pComponent, double fVectorX, double fVectorY, double fVectorZ, double fMinX, double fMinY, double fMaxX, double fMaxY, double fNear, double fFar, bool bIsSubElementRequired, bool bIsOnlyFrontMostRequired, _Visual*& pVisual, bool& bIsFrontFacingAppearance, int& nSubElement, double& fZ );
	int Release() {
		return S_OK;
	}
private:
	std::vector< DisplayDriver* > m_vDisplayDrivers;
	bool m_bIsSoftwareEmulationForced;
};

#endif
