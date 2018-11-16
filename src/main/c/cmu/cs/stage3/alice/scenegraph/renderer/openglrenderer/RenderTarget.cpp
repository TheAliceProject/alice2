#include "RenderTarget.hpp"
#include "RenderCanvas.hpp"

int RenderTarget::Prologue() {
	if( m_pRenderCanvas ) {
		return m_pRenderCanvas->Prologue();
	} else {
		return S_OK;
	}
}
int RenderTarget::Epilogue() {
	if( m_pRenderCanvas ) {
		return m_pRenderCanvas->Epilogue();
	} else {
		return S_OK;
	}
}
int RenderTarget::CommitIfNecessary() {
	if( m_pRenderCanvas ) {
		return m_pRenderCanvas->CommitIfNecessary();
	} else {
		return S_OK;
	}
}
