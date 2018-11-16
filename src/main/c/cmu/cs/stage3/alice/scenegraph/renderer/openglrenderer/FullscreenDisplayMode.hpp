#ifndef FULLSCREEN_DISPLAY_MODE_INCLUDED
#define FULLSCREEN_DISPLAY_MODE_INCLUDED

#include "platform.hpp"
class DisplayDriver;

class FullscreenDisplayMode {
public:
	FullscreenDisplayMode( DisplayDriver* pDisplayDriver ) {
		m_pDisplayDriver = pDisplayDriver;
	}
	DisplayDriver* GetDisplayDriver() {
		return m_pDisplayDriver;
	}
	int GetWidth( long& nWidth ) {
		nWidth = 0;
		return S_OK;
	}
	int GetHeight( long& nHeight ) {
		nHeight = 0;
		return S_OK;
	}
	int GetDepth( long& nDepth ) {
		nDepth = 0;
		return S_OK;
	}
	int GetRefreshRate( long& nRefreshRate ) {
		nRefreshRate = 0;
		return S_OK;
	}
private:
	DisplayDriver* m_pDisplayDriver;
};

#endif
