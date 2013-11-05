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

public class Vector3 extends javax.vecmath.Vector3d implements Interpolable {
	public static final Vector3 ZERO = new Vector3( 0,0,0 );
	public static final Vector3 X_AXIS = new Vector3( 1,0,0 );
	public static final Vector3 X_AXIS_NEGATIVE = new Vector3( -1,0,0 );
	public static final Vector3 Y_AXIS = new Vector3( 0,1,0 );
	public static final Vector3 Y_AXIS_NEGATIVE = new Vector3( 0,-1,0 );
	public static final Vector3 Z_AXIS = new Vector3( 0,0,1 );
	public static final Vector3 Z_AXIS_NEGATIVE = new Vector3( 0,0,-1 );

	public Vector3() {
	}
	public Vector3( double x, double y, double z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3( double[] v ) {
		this( v[0], v[1], v[2] );
	}
	public Vector3( javax.vecmath.Tuple3d t ) {
		this( t.x, t.y, t.z );
	}
	public Vector3( javax.vecmath.Tuple4d t ) {
		this( t.x/t.w, t.y/t.w, t.z/t.w );
	}
	public double getItem( int i ) {
		switch( i ) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		}
		throw new IllegalArgumentException();
	}
	public void setItem( int i, double v ) {
		switch( i ) {
		case 0:
			x = v;
			return;
		case 1:
			y = v;
			return;
		case 2:
			z = v;
			return;
		}
		throw new IllegalArgumentException();
	}
	public double[] getArray() {
		double[] a = { x, y, z };
		return a;
	}
	public void setArray( double[] a ) {
		x = a[0];
		y = a[1];
		z = a[2];
	}
	public void add( Vector3 v ) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	public static Vector3 add( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b ) {
		return new Vector3( a.x+b.x, a.y+b.y, a.z+b.z );
	}
	public void subtract( Vector3 v ) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	public static Vector3 subtract( javax.vecmath.Vector3d a, javax.vecmath.Vector3d b ) {
		return new Vector3( a.x-b.x, a.y-b.y, a.z-b.z );
	}
	public static Vector3 negate( javax.vecmath.Vector3d v ) {
		return new Vector3( -v.x, -v.y, -v.z );
	}
	public void multiply( double scalar ) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}
	public void multiply( javax.vecmath.Vector3d scalar ) {
		x *= scalar.x;
		y *= scalar.y;
		z *= scalar.z;
	}
	public static Vector3 multiply( javax.vecmath.Vector3d v, double scalar ) {
		return new Vector3( v.x*scalar, v.y*scalar, v.z*scalar );
	}
	public static Vector3 multiply( javax.vecmath.Vector3d v, Vector3 scalar ) {
		return new Vector3( v.x*scalar.x, v.y*scalar.y, v.z*scalar.z );
	}
	public void divide( double divisor ) {
		multiply( 1/divisor );
	}
	public void divide( javax.vecmath.Vector3d divisor ) {
		x /= divisor.x;
		y /= divisor.y;
		z /= divisor.z;
	}
	public static Vector3 divide( javax.vecmath.Vector3d v, double divisor ) {
		return multiply( v, 1/divisor );
	}
	public static Vector3 divide( javax.vecmath.Vector3d numerator, javax.vecmath.Vector3d divisor ) {
		return new Vector3( numerator.x/divisor.x, numerator.y/divisor.y, numerator.z/divisor.z );
	}

	public void invert() {
		x = 1/x;
		y = 1/y;
		z = 1/z;
	}
	public static Vector3 invert( javax.vecmath.Vector3d v ) {
		return new Vector3( 1/v.x, 1/v.y, 1/v.z );
	}
	pu