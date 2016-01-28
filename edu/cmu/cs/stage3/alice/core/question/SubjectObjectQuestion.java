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

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;

public abstract class SubjectObjectQuestion extends SubjectQuestion {
	public final TransformableProperty object = new TransformableProperty( this, "object", null ); 
	protected abstract Object getValue( Transformable subjectValue, Transformable objectValue );
	
	protected Object getValue( Transformable subjectValue ) {
		Transformable objectValue = object.getTransformableValue();
		if (objectValue == null) { 
			objectValue = subjectValue;	//Aik Min
		}
		// Proximity
/*		if ( objectValue == null || subjectValue == null)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext=""; 
		else if (this instanceof DistanceTo)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("distance_to_") + objectValue.name.getStringValue() + " " + Messages.getString("_is_");  
		else if (this instanceof DistanceToTheLeftOf)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("distance_to_the_left_of_") + objectValue.name.getStringValue() + " " + Messages.getString("_is_");  
		else if (this instanceof DistanceToTheRightOf)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("distance_to_the_right_of_") + objectValue.name.getStringValue() + " " + Messages.getString("_is_");  
		else if (this instanceof DistanceAbove)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("distance_above_") + objectValue.name.getStringValue() + " " + Messages.getString("_is_");  
		else if (this instanceof DistanceBelow)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("distance_below_") + objectValue.name.getStringValue() + " " + Messages.getString("_is_");  
		else if (this instanceof DistanceInFrontOf)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("distance_in_front_of_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof DistanceBehind)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("distance_behind_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		// Size
		else if (this instanceof IsSmallerThan)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
			subjectValue.name.getStringValue() + " " + Messages.getString("is_smaller_than_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsLargerThan)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
			subjectValue.name.getStringValue() + " " + Messages.getString("is_larger_than_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsNarrowerThan)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
			subjectValue.name.getStringValue() + " " + Messages.getString("is_narrower_than_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsWiderThan)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
			subjectValue.name.getStringValue() + " " + Messages.getString("is_wider_than_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsShorterThan)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
			subjectValue.name.getStringValue() + " " + Messages.getString("is_shorter_than_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsTallerThan)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
			subjectValue.name.getStringValue() + " " + Messages.getString("is_taller_than_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		// Spatial relation
		else if (this instanceof IsLeftOf)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("is_to_the_left_of_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsRightOf)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("is_to_the_right_of_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsAbove)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("is_above_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsBelow)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("is_below_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsInFrontOf)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("is_in_front_of_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else if (this instanceof IsBehind)
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue() + " " + Messages.getString("is_behind_") + objectValue.name.getStringValue() + " " + Messages.getString("is_");  
		else 
			edu.cmu.cs.stage3.alice.core.response.Print.outputtext= 
				subjectValue.name.getStringValue()+" __Unknown__ " + objectValue.name.getStringValue() + " " + Messages.getString("is_");  */
		return getValue( subjectValue, objectValue );
	}
}
