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

package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public abstract class UnaryNumberResultingInNumberQuestion extends NumberQuestion {
	public final NumberProperty a = new NumberProperty( this, "a", new Double( 0 ) ); 
	protected abstract double getValue( double a );
	
	public Object getValue() {
		double aValue = a.doubleValue( 0.0 );
/*		if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Abs){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("absolute_value_of_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Sqrt){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("square_root_of_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Floor){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("floor_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Ceil){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("ceiling_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Sin){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("sin_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Cos){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("cos_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Tan){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("tan_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ACos){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("arccos_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ASin){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("arcsin_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ATan){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("arctan_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Log){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("natural_log_of_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Exp){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("e_raised_to_the_")+aValue+" "+Messages.getString("power_is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Round){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= Messages.getString("round_")+aValue+" "+Messages.getString("is_");		  
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ToRadians){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= aValue + " " + Messages.getString("converted_from_radians_to_degrees_is_");		 
		} else if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.ToDegrees){
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= aValue + " " + Messages.getString("converted_from_degrees_to_radians_is_");		 
		} */
		if (this instanceof edu.cmu.cs.stage3.alice.core.question.math.Int)
			return (int) aValue;
			
		return new Double( getValue( aValue ) );
	}
}
