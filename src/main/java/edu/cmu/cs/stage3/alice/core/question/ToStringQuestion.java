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
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;

/**
 * @author Ben Buchwald, Dennis Cosgrove
 */

public class ToStringQuestion extends Question {
    public final ObjectProperty what = new ObjectProperty( this, "what", new String( "" ),Object.class ) {  
		
		protected boolean getValueOfExpression() {
			return true;
		}
	};
    
	public Class getValueClass() {
        return String.class;
    }
    
	public Object getValue() {
    	Object value = what.getValue();
    	//Object o = what.get();
/*		if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart)
			Print.outputtext = Messages.getString("time_elapsed_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.Year)
			Print.outputtext = Messages.getString("year_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear)
			Print.outputtext = Messages.getString("month_of_year_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfYear)
			Print.outputtext = Messages.getString("day_of_year_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth)
			Print.outputtext = Messages.getString("day_of_month_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek)
			Print.outputtext = Messages.getString("day_of_week_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth)
			Print.outputtext = Messages.getString("day_of_week_in_month_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsAM)
			Print.outputtext = Messages.getString("is_AM_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsPM)
			Print.outputtext = Messages.getString("is_PM_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM)
			Print.outputtext = Messages.getString("hour_of_AM_or_PM_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfDay)
			Print.outputtext = Messages.getString("hour_of_day_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour)
			Print.outputtext = Messages.getString("minutes_of_hour_as_a_string_is_"); 
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute)
			Print.outputtext = Messages.getString("seconds_of_minute_as_a_string_is_"); 
		// Mouse
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge)
			Print.outputtext = Messages.getString("mouse_distance_from_left_edge_as_a_string_is_"); 
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge)
			Print.outputtext = Messages.getString("mouse_distance_from_top_edge_as_a_string_is_"); 
		//Ask User
		else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber)
			Print.outputtext = Messages.getString("ask_user_for_a_number_as_a_string_is_"); 
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo)
			Print.outputtext = Messages.getString("ask_user_for_yes_or_no_as_a_string_is_"); 
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString)
			Print.outputtext = Messages.getString("ask_user_for_a_string_as_a_string_is_"); 
		//Random
		else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.RandomNumber)
			Print.outputtext = Messages.getString("random_number_as_a_string_is_"); 
		else if (Print.outputtext != null)
			Print.outputtext = Print.outputtext.substring(0, Print.outputtext.length()-4)+" "+Messages.getString("as_a_string_is_"); */

		if( value instanceof Element ) {
			return ((Element)value).getTrimmedKey();
		} else if ( value != null ) {
			return value.toString();
		} else {
			return null;
    	}
    }
}
