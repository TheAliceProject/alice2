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

package edu.cmu.cs.stage3.math;

public class Box implements Cloneable {
	protected javax.vecmath.Vector3d m_minimum = null;
	protected javax.vecmath.Vector3d m_maximum = null;

	public Box() {
	}
	public Box( javax.vecmath.Vector3d minimum, javax.vecmath.Vector3d maximum ) {
		setMinimum( minimum );
		setMaximum( maximum );
	}
	public Box( double minimumX, double minimumY, double minimumZ, double maximumX, double maximumY, double maximumZ ) {
		setMinimum( new javax.vecmath.Vector3d( minimumX, minimumY, minimumZ ) );
		setMaximum( new javax.vecmath.Vector3d( maximumX, maximumY, maximumZ ) );
	}
	
	public synchronized Object clone() {
		try {
			Box box = (Box)super.clone();
			box.setMinimum( m_minimum );
			box.setMaximum( m_maximum );
			return box;
		} catch( CloneNotSupportedException e ) {
			throw new InternalError();
		}
	}
	
	public boolean equals( Object o ) {
		if( o==this ) return true;
		if( o!=null && o instanceof Box ) {
			Box box = (Box)o;
			return m_minimum.equals( box.m_minimum ) &&  m_maximum.equals( box.m_maximum ) ;
		} else {
			return false;
		}
	}

	public javax.vecmath.Vector3d[] getCorners() {
		javax.vecmath.Vector3d[] corners = new javax.vecmath.Vector3d[8];
		corners[0] = new javax.vecmath.Vector3d( m_minimum.x, m_minimum.y, m_minimum.z );
		corners[1] = new javax.vecmath.Vector3d( m_minimum.x, m_minimum.y, m_maximum.z );
		corners[2] = new javax.vecmath.Vector3d( m_minimum.x, m_maximum.y, m_minimum.z );
		corners[3] = new javax.vecmath.Vector3d( m_minimum.x, m_maximum.y, m_maximum.z );
		corners[4] = new javax.vecmath.Vector3d( m_maximum.x, m_minimum.y, m_minimum.z );
		corners[5] = new javax.vecmath.Vector3d( m_maximum.x, m_minimum.y, m_maximum.z );
		corners[6] = new javax.vecmath.Vector3d( m_maximum.x, m_maximum.y, m_minimum.z );
		corners[7] = new javax.vecmath.Vector3d( m_maximum.x, m_maximum.y, m_maximum.z );
		return corners;
	}
	public javax.vecmath.Vector3d getMinimum() {
		if( m_minimum!=null ) {
			return new javax.vecmath.Vector3d( m_minimum );
		} else {
			return null;
		}
	}
	public void setMinimum( javax.vecmath.Vector3d minimum ) {
		if( minimum!=null ) {
			m_minimum = new javax.vecmath.Vector3d( minimum );
		} else {
			m_minimum = null;
		}
	}
	public javax.vecmath.Vector3d getMaximum() {
		if( m_maximum!=null ) {
			return new javax.vecmath.Vector3d( m_maximum );
		} else {
			return null;
		}
	}
	public void setMaximum( javax.vecmath.Vector3d maximum ) {
		if( maximum!=null ) {
			m_maximum = new javax.vecmath.Vector3d( maximum );
		} else {
			m_maximum = null;
		}
	}

	public javax.vecmath.Vector3d getCenter() {
		if( m_minimum!=null && m_maximum!=null ) {
			return new javax.vecmath.Vector3d( (m_minimum.x+m_maximum.x)/2, (m_minimum.y+m_maximum.y)/2, (m_minimum.z+m_maximum.z)/2 );
		} else {
			return null;
		}
	}

	public javax.vecmath.Vector3d getCenterOfFrontFace(){
		if( m_minimum!=null && m_maximum!=null ) {
			return new javax.vecmath.Vector3d( (m_minimum.x+m_maximum.x)/2, (m_minimum.y+m_maximum.y)/2, (m_maximum.z) );
		} else {
			return null;
		}
	}

	public javax.vecmath.Vec