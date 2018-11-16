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

#ifndef ELEMENT_INCLUDED
#define ELEMENT_INCLUDED

#include "platform.hpp"

//todo: change to enum
const int ELEMENT_TYPE_ID = (1<<1);
const int COMPONENT_TYPE_ID = ELEMENT_TYPE_ID | (1<<2);
const int AFFECTOR_TYPE_ID = COMPONENT_TYPE_ID | (1<<3);
const int LIGHT_TYPE_ID = AFFECTOR_TYPE_ID | (1<<4);
const int POINT_LIGHT_TYPE_ID = LIGHT_TYPE_ID | (1<<5);
const int FOG_TYPE_ID = AFFECTOR_TYPE_ID | (1<<6);
const int CAMERA_TYPE_ID = COMPONENT_TYPE_ID | (1<<7);
const int CONTAINER_TYPE_ID = COMPONENT_TYPE_ID | (1<<8);
const int REFERENCE_FRAME_TYPE_ID = CONTAINER_TYPE_ID | (1<<9);
const int GEOMETRY_TYPE_ID = ELEMENT_TYPE_ID | (1<<10);
const int SHAPE_TYPE_ID = GEOMETRY_TYPE_ID | (1<<11);
const int VERTEX_GEOMETRY_TYPE_ID = GEOMETRY_TYPE_ID | (1<<12);

const int LINEAR_FOG_TYPE_ID = FOG_TYPE_ID | (1<<16);
const int EXPONENTIAL_FOG_TYPE_ID = FOG_TYPE_ID | (2<<16);
const int EXPONENTIAL_SQUARED_FOG_TYPE_ID = FOG_TYPE_ID | (3<<16);
const int AMBIENT_LIGHT_TYPE_ID = LIGHT_TYPE_ID | (4<<16);
const int DIRECTIONAL_LIGHT_TYPE_ID = LIGHT_TYPE_ID | (5<<16);
const int SPOT_LIGHT_TYPE_ID = POINT_LIGHT_TYPE_ID | (6<<16);
const int CLIPPING_PLANE_TYPE_ID = AFFECTOR_TYPE_ID | (7<<16);
const int ORTHOGRAPHIC_CAMERA_TYPE_ID = CAMERA_TYPE_ID | (8<<16);
const int PERSPECTIVE_CAMERA_TYPE_ID = CAMERA_TYPE_ID | (9<<16);
const int PROJECTION_CAMERA_TYPE_ID = CAMERA_TYPE_ID | (10<<16);
const int SYMMETRIC_PERSPECTIVE_CAMERA_TYPE_ID = CAMERA_TYPE_ID | (11<<16);
const int TRANSFORMABLE_TYPE_ID = REFERENCE_FRAME_TYPE_ID | (12<<16);
const int VISUAL_TYPE_ID = COMPONENT_TYPE_ID | (13<<16);
const int SCENE_TYPE_ID = REFERENCE_FRAME_TYPE_ID | (14<<16);
const int TEXT_TYPE_ID = GEOMETRY_TYPE_ID | (15<<16);
const int INDEXED_TRIANGLE_ARRAY_TYPE_ID = VERTEX_GEOMETRY_TYPE_ID | (16<<16);
const int LINE_ARRAY_TYPE_ID = VERTEX_GEOMETRY_TYPE_ID | (17<<16);
const int LINE_STRIP_TYPE_ID = VERTEX_GEOMETRY_TYPE_ID | (18<<16);
const int POINT_ARRAY_TYPE_ID = VERTEX_GEOMETRY_TYPE_ID | (19<<16);
const int TRIANGLE_ARRAY_TYPE_ID = VERTEX_GEOMETRY_TYPE_ID | (20<<16);
const int TRIANGLE_FAN_TYPE_ID = VERTEX_GEOMETRY_TYPE_ID | (21<<16);
const int TRIANGLE_STRIP_TYPE_ID = VERTEX_GEOMETRY_TYPE_ID | (22<<16);
const int SPRITE_TYPE_ID = GEOMETRY_TYPE_ID | (23<<16);
const int BOX_TYPE_ID = SHAPE_TYPE_ID | (24<<16);
const int CYLINDER_TYPE_ID = SHAPE_TYPE_ID | (25<<16);
const int SPHERE_TYPE_ID = SHAPE_TYPE_ID | (26<<16);
const int TORUS_TYPE_ID = SHAPE_TYPE_ID | (27<<16);
const int BACKGROUND_TYPE_ID = ELEMENT_TYPE_ID | (28<<16);
const int APPEARANCE_TYPE_ID = ELEMENT_TYPE_ID | (29<<16);
const int TEXTURE_MAP_TYPE_ID = ELEMENT_TYPE_ID | (30<<16);

class Element {
public:
	Element() {
		OnNameChange( "unnamed" );
	}
	virtual ~Element() {
		Release();
	}
	char* GetName() {
		return m_vcName;
	}
	int OnNameChange( const char* vcName ) {
		strncpy( m_vcName, vcName, 64 );
		return S_OK;
	}
	int GetTypeID() { 
		return m_nTypeID; 
	};
	void SetTypeID( int nTypeID ) { 
		m_nTypeID = nTypeID; 
	};
	int Release() {
		if( IsAlive() ) {
			InternalRelease();
			m_nTypeID = 0;
		}
		return S_OK;
	}
protected:
	virtual void InternalRelease() {
	}
private:
	bool IsAlive() {
		return m_nTypeID!=0;
	}
	int m_nTypeID;
	char m_vcName[64];
};

#endif