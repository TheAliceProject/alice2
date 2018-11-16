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

#ifndef VIEWPORT_INCLUDED
#define VIEWPORT_INCLUDED

class Viewport {
public:
	Viewport() {
		set( 0, 0, -1, -1 );
	}
	Viewport( const Viewport& other ) {
		set( other.nX, other.nY, other.nWidth, other.nHeight );
	}
	Viewport( long nX, long nY, long nWidth, long nHeight ) {
		set( nX, nY, nWidth, nHeight );
	}
	void set( long nX, long nY, long nWidth, long nHeight ) {
		this->nX = nX;
		this->nY = nY;
		this->nWidth = nWidth;
		this->nHeight = nHeight;
	}
	long nX;
	long nY;
	long nWidth;
	long nHeight;
};

inline bool operator==( const Viewport& a, const Viewport& b ) {
	return a.nX==b.nX && a.nY==b.nY && a.nWidth==b.nWidth && a.nHeight==b.nHeight;
}
inline bool operator!=( const Viewport& a, const Viewport& b ) {
	return !(a==b);
}

#endif
