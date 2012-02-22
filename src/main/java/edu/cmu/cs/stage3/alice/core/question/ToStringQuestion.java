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

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Messages;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.response.Print;

/**
 * @author Ben Buchwald, Dennis Cosgrove
 */

public class ToStringQuestion extends Question {
    public final ObjectProperty what = new ObjectProperty( this, Messages.getString("ToStringQuestion.0"), new String( "" ),Object.class ) { //$NON-NLS-1$ //$NON-NLS-2$
		
		protected boolean getValueOfExpression() {
			return true;
		}
	};
    
	public Class getValueClass() {
        return String.class;
    }
    
	public Object getValue() {
    	Object value = what.getValue();
    	Object o = what.get();
		if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart)
			Print.outputtext = Messages.getString("ToStringQuestion.2"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.Year)
			Print.outputtext = Messages.getString("ToStringQuestion.3"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear)
			Print.outputtext = Messages.getString("ToStringQuestion.4"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfYear)
			Print.outputtext = Messages.getString("ToStringQuestion.5"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth)
			Print.outputtext = Messages.getString("ToStringQuestion.6"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek)
			Print.outputtext = Messages.getString("ToStringQuestion.7"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth)
			Print.outputtext = Messages.getString("ToStringQuestion.8"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsAM)
			Print.outputtext = Messages.getString("ToStringQuestion.9"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsPM)
			Print.outputtext = Messages.getString("ToStringQuestion.10"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM)
			Print.outputtext = Messages.getString("ToStringQuestion.11"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfDay)
			Print.outputtext = Messages.getString("ToStringQuestion.12"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour)
			Print.outputtext = Messages.getString("ToStringQuestion.13"); //$NON-NLS-1$
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute)
			Print.outputtext = Messages.getString("ToStringQuestion.14"); //$NON-NLS-1$
		// Mouse
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge)
			Print.outputtext = Messages.getString("ToStringQuestion.15"); //$NON-NLS-1$
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge)
			Print.outputtext = Messages.getString("ToStringQuestion.16"); //$NON-NLS-1$
		//Ask User
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber)
			Print.outputtext = Messages.getString("ToStringQuestion.17"); //$NON-NLS-1$
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo)
			Print.outputtext = Messages.getString("ToStringQuestion.18"); //$NON-NLS-1$
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString)
			Print.outputtext = Messages.getString("ToStringQuestion.19"); //$NON-NLS-1$
		//Random
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.RandomNumber)
			Print.outputtext = Messages.getString("ToStringQuestion.20"); //$NON-NLS-1$
		else if (Print.outputtext != null)
			Print.outputtext = Print.outputtext.substring(0, Print.outputtext.length()-4)+Messages.getString("ToStringQuestion.21"); //$NON-NLS-1$

		if( value instanceof Element ) {
			return ((Element)value).getTrimmedKey();
		} else if ( value != null ) {
			return value.toString();
		} else {
			return null;
    	}
    }
}