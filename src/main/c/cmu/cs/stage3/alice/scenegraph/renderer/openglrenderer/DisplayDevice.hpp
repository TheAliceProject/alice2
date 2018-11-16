#ifndef DISPLAY_DEVICE_INCLUDED
#define DISPLAY_DEVICE_INCLUDED

#include "platform.hpp"
#include <algorithm>
#include <vector>

class DisplayDriver;

class DisplayDevice {
public:
	DisplayDevice( DisplayDriver* pDisplayDriver ) {
		m_pDisplayDriver = pDisplayDriver;
	}
	DisplayDriver* GetDisplayDriver() {
		return m_pDisplayDriver;
	}
	int GetDescription( char* vcDescription, long n ) {
		strncpy( vcDescription, "", n );
		return S_OK;
	}
	int GetName( char* vcName, long n ) {
		strncpy( vcName, "", n );
		return S_OK;
	}	
	bool IsHardwareAccelerated() {
		return false;
	}

	int Release() {
		return S_OK;
	}

private:
	DisplayDriver* m_pDisplayDriver;
};

#endif