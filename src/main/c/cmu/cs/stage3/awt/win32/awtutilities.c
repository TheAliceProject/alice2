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

#include <windows.h>
#include <assert.h>

#define SHIFT_MASK (1<<0)
#define CTRL_MASK (1<<1)
#define META_MASK (1<<2)
#define ALT_MASK (1<<3)
#define BUTTON1_MASK (1<<4)

/*bool*/int AWTUtilities_IsGetCursorLocationSupported() {
	return 1;
}
/*bool*/int AWTUtilities_IsSetCursorLocationSupported() {
	return 1;
}

/*bool*/int AWTUtilities_IsIsCursorShowingSupported() {
	return 1;
}
/*bool*/int AWTUtilities_IsSetIsCursorShowingSupported() {
	return 1;
}

/*bool*/int AWTUtilities_IsIsKeyPressedSupported() {
	return 1;
}

/*bool*/int AWTUtilities_IsGetModifiersSupported() {
	return 1;
}

/*bool*/int AWTUtilities_IsPumpMessageQueueSupported() {
	return 1;
}

void AWTUtilities_GetCursorLocation( long* pnX, long* pnY ) {
	POINT pt;
	GetCursorPos( &pt );
	*pnX = pt.x;
	*pnY = pt.y;
}

void AWTUtilities_SetCursorLocation( long nX, long nY ) {
	SetCursorPos( nX, nY );
}

/*bool*/int AWTUtilities_IsCursorShowing() {
	CURSORINFO sCursorInfo;
	sCursorInfo.cbSize = sizeof( CURSORINFO );
	GetCursorInfo( &sCursorInfo );
	return sCursorInfo.flags == CURSOR_SHOWING;
}
void AWTUtilities_SetIsCursorShowing( /*bool*/int bIsCursorShowing ) {
	int nDisplayCount = ShowCursor( bIsCursorShowing );
}

/*bool*/int AWTUtilities_IsKeyPressed( long nKeyCode ) {
	return GetAsyncKeyState( nKeyCode )<0;
}

int AWTUtilities_GetModifiers() {
	int nModifiers = 0;
	if( AWTUtilities_IsKeyPressed( VK_SHIFT ) ) {
		nModifiers |= SHIFT_MASK;
	}
	if( AWTUtilities_IsKeyPressed( VK_CONTROL ) ) {
		nModifiers |= CTRL_MASK;
	}
	//if( AWTUtilities_IsKeyPressed( VK_RBUTTON ) ) {
	//VK_CONTROL	nModifiers |= META_MASK;
	//}
	if( AWTUtilities_IsKeyPressed( VK_LBUTTON ) ) {
		nModifiers |= BUTTON1_MASK;
	}
	return nModifiers;
}
	
void AWTUtilities_PumpMessageQueue() {
    MSG sMsg;
	while( PeekMessage( &sMsg, NULL, 0, 0, PM_REMOVE ) ) {
		TranslateMessage(&sMsg);
		DispatchMessage(&sMsg);
	}
}
