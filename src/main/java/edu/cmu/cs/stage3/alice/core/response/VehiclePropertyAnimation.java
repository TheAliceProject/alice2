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

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;

public class VehiclePropertyAnimation extends PropertyAnimation {
	public VehiclePropertyAnimation() {
		super();
		//propertyName.set( AikMin.getName("vehicle") );
		propertyName.set( "vehicle" );
	}
	
	protected void propertyChanging( edu.cmu.cs.stage3.alice.core.Property property, Object value ) {
		if( property == propertyName ) {
			//if( ((String)value).equals( AikMin.getName("vehicle") ) ) {
			if( ((String)value).equals( "vehicle" ) ) {
				//pass
			} else {
                System.err.println( "propertyName: " + value );
				throw new RuntimeException( "VehiclePropertyAnimation should be animating \"vehicle\" property.  Attempted to change to the \"" + value + "\" property." );
			}
		} else {
			super.propertyChanging( property, value );
		}
	}
	public class RuntimeVehiclePropertyAnimation extends RuntimePropertyAnimation {
		
		protected void set( Object value ) {
			edu.cmu.cs.stage3.alice.core.property.VehicleProperty vehicleProperty = (edu.cmu.cs.stage3.alice.core.property.VehicleProperty)getProperty();
			if( vehicleProperty != null ) {
				vehicleProperty.set( value, true );
			} else {
				throw new RuntimeException();
			}
		}
	}
}
