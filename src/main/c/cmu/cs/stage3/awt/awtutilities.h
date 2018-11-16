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

#ifndef AWTUTILITIES_H
#define AWTUTILITIES_H

/*bool*/int AWTUtilities_IsGetCursorLocationSupported();
/*bool*/int AWTUtilities_IsSetCursorLocationSupported();
/*bool*/int AWTUtilities_IsIsCursorShowingSupported();
/*bool*/int AWTUtilities_IsSetIsCursorShowingSupported();
/*bool*/int AWTUtilities_IsIsKeyPressedSupported();
/*bool*/int AWTUtilities_IsGetModifiersSupported();
/*bool*/int AWTUtilities_IsPumpMessageQueueSupported();
void AWTUtilities_PumpMessageQueue();

void AWTUtilities_GetCursorLocation( long* pnX, long* pnY );
void AWTUtilities_SetCursorLocation( long nX, long nY );

/*bool*/int AWTUtilities_IsCursorShowing();
void AWTUtilities_SetIsCursorShowing( /*bool*/int bIsCursorShowing );

/*bool*/int AWTUtilities_IsKeyPressed( long nKeyCode );

int AWTUtilities_GetModifiers();

#endif