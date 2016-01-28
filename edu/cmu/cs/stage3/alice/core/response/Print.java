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

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;

public class Print extends edu.cmu.cs.stage3.alice.core.Response {
	public final StringProperty text = new StringProperty( this, "text", null ); 
	public final ObjectProperty object = new ObjectProperty( this, "object", null, Object.class ) { 
		
		protected boolean getValueOfExpression() {
			return true;
		}
	};
	public final BooleanProperty addNewLine = new BooleanProperty( this, "addNewLine", Boolean.TRUE ); 

	protected Number getDefaultDuration() {
		return new Double( 0 );
	}

/*	public String getPrefix() {
		String t = text.getStringValue();
		if( t != null ) {
			return null;
		} else {
			Object o = object.get();
			if( o != null ) {
				if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
					return Messages.getString("the_value_of_") + ((edu.cmu.cs.stage3.alice.core.Element)o).getTrimmedKey() + " " + Messages.getString("is_");  
				} else {
					return Messages.getString("the_value_of_") + o + " " + Messages.getString("is_");  
				}
			} else {
				return null;
			}
		}
	}*/

	//public static String outputtext = null;
	public class RuntimePrint extends RuntimeResponse {
		
		public void update( double t ) {
			super.update( t );
			//outputtext = null;
			String s = Print.this.text.getStringValue();
			Object o = Print.this.object.get();	
/*			// Time
			if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart)
				outputtext = Messages.getString("time_elapsed_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.Year)
				outputtext = Messages.getString("year_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear)
				outputtext = Messages.getString("month_of_year_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfYear)
				outputtext = Messages.getString("day_of_year_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth)
				outputtext = Messages.getString("day_of_month_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek)
				outputtext = Messages.getString("day_of_week_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth)
				outputtext = Messages.getString("day_of_week_in_month_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsAM)
				outputtext = Messages.getString("is_AM_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsPM)
				outputtext = Messages.getString("is_PM_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM)
				outputtext = Messages.getString("hour_of_AM_or_PM_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfDay)
				outputtext = Messages.getString("hour_of_day_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour)
				outputtext = Messages.getString("minutes_of_hour_is_"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute)
				outputtext = Messages.getString("seconds_of_minute_is_"); 
			// Mouse
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge)
				outputtext = Messages.getString("mouse_distance_from_left_edge_is_"); 
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge)
				outputtext = Messages.getString("mouse_distance_from_top_edge_is_"); 
			//Ask User
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber)
				outputtext = Messages.getString("ask_user_for_a_number_is_"); 
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo)
				outputtext = Messages.getString("ask_user_for_yes_or_no_is_"); 
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString)
				outputtext = Messages.getString("ask_user_for_a_string_is_"); 
			//Random
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.RandomNumber)
				outputtext = Messages.getString("random_number_is_"); */
			
			Object value = Print.this.object.getValue();
			if (value instanceof Double){
				java.text.NumberFormat formatter = new java.text.DecimalFormat("#.######"); 
				value = Double.valueOf(formatter.format( value ));
			}
			
			String valueText = Messages.getString("None"); 

			if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
				valueText = ((edu.cmu.cs.stage3.alice.core.Element)value).getTrimmedKey();
				if ( value instanceof edu.cmu.cs.stage3.alice.core.List ) {
					valueText = valueText.substring(0, valueText.indexOf("__")-1);
				}
			} else if ( value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color ) {
				double blue = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getBlue();
				double green = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getGreen();
				double red = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getRed();
				if ( blue == 1 && green == 1 && red == 1)
					valueText = Messages.getString("white"); 
				else if ( blue == 0 && green == 0 && red == 0)
					valueText = Messages.getString("black"); 
				else if ( blue == 0 && green == 0 && red == 1)
					valueText = Messages.getString("red"); 
				else if ( blue == 0 && green == 1 && red == 0)
					valueText = Messages.getString("green"); 
				else if ( blue == 1 && green == 0 && red == 0)
					valueText = Messages.getString("blue"); 
				else if ( blue == 0 && green == 1 && red == 1)
					valueText = Messages.getString("yellow"); 
				else if ( blue == 0.501960813999176 && green == 0 && red == 0.501960813999176)
					valueText = Messages.getString("purple"); 
				else if ( blue == 0 && green == 0.6470588445663452 && red == 1)
					valueText = Messages.getString("orange"); 
				else if ( blue == 0.686274528503418 && green == 0.686274528503418 && red == 1)
					valueText = Messages.getString("pink"); 
				else if ( blue == 0.16470588743686676 && green == 0.16470588743686676 && red == 0.6352941393852234)
					valueText = Messages.getString("brown"); 
				else if ( blue == 1 && green == 1 && red == 0)
					valueText = Messages.getString("cyan"); 
				else if ( blue == 1 && green == 0 && red == 1)
					valueText = Messages.getString("magenta"); 
				else if ( blue == 0.501960813999176 && green == 0.501960813999176 && red == 0.501960813999176)
					valueText = Messages.getString("gray"); 
				else if ( blue == 0.7529411911964417 && green == 0.7529411911964417 && red == 0.7529411911964417)
					valueText = Messages.getString("light_gray"); 
				else if ( blue == 0.250980406999588 && green == 0.250980406999588 && red == 0.250980406999588)
					valueText = Messages.getString("dark_gray"); 
				else {
					valueText = value.toString();
					valueText = valueText.substring(valueText.indexOf(Messages.getString("Color")) , valueText.length()); 
				}
			} else if( value != null ) {
				valueText = value.toString();
			}

			String output = "";
			if( s != null ) {
				if( o != null ) {
					output = s + valueText;
				} else {
					output = s;
				}
			} else {
				if( o != null ) {
					//output = Print.this.getPrefix();
					//if (outputtext != null && output.length()>1)
					//	output = output.substring(0,(output.indexOf("__")-1))+" "+outputtext+valueText;  
					//else
						output = output + valueText;
				} else {
					output = valueText;
				}
			}
			if( Print.this.addNewLine.booleanValue() ) {
				System.out.println( output );
			} else {
				System.out.print( output );
			}
		}
	}
}
