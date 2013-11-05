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

public class Quaternion implements Cloneable, Interpolable {
	public double x = 0;
	public double y = 0;
	public double z = 0;
	public double w = 1;

	public Quaternion() {
	}
	public Quaternion( double x, double y, double z, double w ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public Quaternion( double[] v ) {
		this( v[0], v[1], v[2], v[3] );
	}
	public Quaternion( Matrix33 m ) {
		setMatrix33( m );
	}
	public Quaternion( AxisAngle aa ) {
		setAxisAngle( aa );
	}
	public Quaternion( EulerAngles ea ) {
		setEulerAngles( ea );
	}
	
	public synchronized Object clone() {
		try {
			return super.clone();
		} catch( CloneNotSupportedException e ) {
			throw new InternalError();
		}
	}
	
	public boolean equals( Object o ) {
		if( o==this ) return true;
		if( o!=null && o instanceof Quaternion ) {
			Quaternion q = (Quaternion)o;
			return x==q.x && y==q.y && z==q.z && w==q.w;
		} else {
			return false;
		}
	}
	public double[] getArray() {
		double[] a = { x, y, z, w };
		return a;
	}
	public void setArray( double[] a ) {
		x = a[0];
		y = a[1];
		z = a[2];
		w = a[3];
	}
	public boolean equals( Quaternion q ) {
		return x==q.x && y==q.y && z==q.z && w==q.w;
	}
	public AxisAngle getAxisAngle() {
		return new AxisAngle( this );
	}
	public void setAxisAngle( AxisAngle aa ) {
		double halfAngle = aa.getAngle()*0.5;
		double cosHalfAngle = Math.cos( halfAngle );
		double sinHalfAngle = Math.sin( halfAngle );
		javax.vecmath.Vector3d normalizedAxis = Vector3.normalizeV( aa.getAxis() );
		w = cosHalfAngle;
		x = sinHalfAngle*aa.m_axis.x;
		y = sinHalfAngle*aa.m_axis.y;
		z = sinHalfAngle*aa.m_axis.z;
	}
	public EulerAngles getEulerAngles() {
		return new EulerAngles( this );
	}
	public void setEulerAngles( EulerAngles ea ) {
		Matrix33 m = new Matrix33();
		m.rotateX( ea.pitch );
		m.rotateY( ea.yaw );
		m.rotateZ( ea.roll );
		setMatrix33( m );
		/*
		//todo: why doesn't this work?
		double c1 = Math.cos( ea.pitch/2 );
		double s1 = Math.sin( ea.pitch/2 );
		double c2 = Math.cos( ea.yaw/2 );
		double s2 = Math.sin( ea.yaw/2 );
		double c3 = Math.cos( ea.roll/2 );
		double s3 = Math.sin( ea.roll/2 );

		x = c2*s1*c3 - s2*c1*s3;
		y = c2*s1*s3 + s2*c1*c3;
		z = c2*c1*s3 - s2*s1*c3;
		w = c2*c1*c3 + s2*s1*s3;
		*/
	}
	public Matrix33 getMatrix33() {
		Matrix33 m = new Matrix33();
		m.setQuaternion( this );
		return m;
	}
	public void setMatrix33( Matrix33 m ) {
		//based on
		//http://www.gamasutra.com/features/19980703/quaternions_01.htm
		//the rows and columns have been reversed
		double tr = m.m00 + m.m11 + m.m22;
		if (tr > 0.0) {
			double s = Math.sqrt( tr + 1.0 );
			w = s*0.5;
			s = 0.5 / s;
			x = (m.m21-m.m12)*s;
			y = (m.m02-m.m20)*s;
			z = (m.m10-m.m01)*s;
		} else {
			int[] nxt = {1, 2, 0};
			double[][] a = m.getMatrix();
			int i = 0;
			if( a[1][1]>a[0][0] )
				i = 1;
			if( a[2][2]>a[i][i] )
				i = 2;
			int j = nxt[i];
			int k = nxt[j];

			double s = Math.sqrt( (a[i][i] - (a[j][j] + a[k][k])) + 1.0);

			double[] q = new double[4];
			q[i] = s * 0.5;

			if (s != 0.0) s = 