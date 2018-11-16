#include "Appearance.hpp"
#include "Visual.hpp"

int Appearance::NotifyVisualsOfOpaqueStateChange() {
	for( unsigned i=0; i<m_visuals.size(); i++ ) {
		CHECK_SUCCESS( m_visuals[i]->CheckForVisualStateChange() );
	}
	return S_OK;
}
