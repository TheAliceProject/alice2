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

import edu.cmu.cs.stage3.alice.core.Messages;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class RandomBoolean extends Question {
	public final NumberProperty probabilityOfTrue = new NumberProperty( this, "probabilityOfTrue", new Double( 0.5 ) ); //$NON-NLS-1$
	
	public Object getValue() {
		edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("RandomBoolean.1")+(int)(probabilityOfTrue.doubleValue()*100)+Messages.getString("RandomBoolean.2"); //$NON-NLS-1$ //$NON-NLS-2$
		if( Math.random() < probabilityOfTrue.doubleValue() ) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public Class getValueClass() {
		return Boolean.class;
	}
}