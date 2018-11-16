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

#ifndef TRANSFORMABLE_INCLUDED
#define TRANSFORMABLE_INCLUDED

#include "ReferenceFrame.hpp"

class Transformable : public ReferenceFrame {
public:
	int OnLocalTransformationChange( double rc00, double rc01, double rc02, double rc03, double rc10, double rc11, double rc12, double rc13, double rc20, double rc21, double rc22, double rc23, double rc30, double rc31, double rc32, double rc33 ) {
		return 0;
	}
	int OnIsFirstClassChange( bool value ) {
		return 0;
	}
};

#endif