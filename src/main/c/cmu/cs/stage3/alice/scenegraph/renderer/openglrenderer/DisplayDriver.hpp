#ifndef DISPLAY_DRIVER_INCLUDED
#define DISPLAY_DRIVER_INCLUDED

#include <vector>

#include "DisplayDevice.hpp"
#include "FullscreenDisplayMode.hpp"
class Renderer;

class DisplayDriver {
public:
	DisplayDriver( Renderer* pRenderer ) {
		m_pRenderer = pRenderer;
	}

	int GetDescription( char* vcDescription, long n ) {
		strncpy( vcDescription, "", n );
		return S_OK;
	}
	int GetName( char* vcName, long n ) {
		strncpy( vcName, "", n );
		return S_OK;
	}

	int GetFullscreenDisplayModeCount( long& nFullscreenDisplayModeCount ) {
		nFullscreenDisplayModeCount = (long)m_vFullscreenDisplayModes.size();
		return S_OK;
	}
	int GetFullscreenDisplayModeAt( long nIndex, FullscreenDisplayMode*& pFullscreenDisplayMode ) {
		pFullscreenDisplayMode = m_vFullscreenDisplayModes[ nIndex ];
		return S_OK;
	}
	int GetDisplayDeviceCount( long& nDisplayDeviceCount ) {
		nDisplayDeviceCount = (long)m_vDisplayDevices.size();
		return S_OK;
	}
	int GetDisplayDeviceAt( long nIndex, DisplayDevice*& pDisplayDevice ) {
		pDisplayDevice = m_vDisplayDevices[ nIndex ];
		return S_OK;
	}
	bool SupportsOverlapped() {
		return true;
	}


	int Release() {
		return S_OK;
	}

private:
	Renderer* m_pRenderer;
	std::vector< FullscreenDisplayMode* > m_vFullscreenDisplayModes;
	std::vector< DisplayDevice* > m_vDisplayDevices;
};

#endif
