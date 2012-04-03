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

	public String getPrefix() {
		String t = text.getStringValue();
		if( t != null ) {
			return null;
		} else {
			Object o = object.get();
			if( o != null ) {
				if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
					return Messages.getString("Print.3") + ((edu.cmu.cs.stage3.alice.core.Element)o).getTrimmedKey() + Messages.getString("Print.4");  
				} else {
					return Messages.getString("Print.5") + o + Messages.getString("Print.6");  
				}
			} else {
				return null;
			}
		}
	}

	public static String outputtext = null;
	public class RuntimePrint extends RuntimeResponse {
		
		public void update( double t ) {
			super.update( t );
			outputtext = null;
			String s = Print.this.text.getStringValue();
			Object o = Print.this.object.get();	
			// Time
			if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart)
				outputtext = Messages.getString("Print.7"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.Year)
				outputtext = Messages.getString("Print.8"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear)
				outputtext = Messages.getString("Print.9"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfYear)
				outputtext = Messages.getString("Print.10"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth)
				outputtext = Messages.getString("Print.11"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek)
				outputtext = Messages.getString("Print.12"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth)
				outputtext = Messages.getString("Print.13"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsAM)
				outputtext = Messages.getString("Print.14"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.IsPM)
				outputtext = Messages.getString("Print.15"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM)
				outputtext = Messages.getString("Print.16"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.HourOfDay)
				outputtext = Messages.getString("Print.17"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour)
				outputtext = Messages.getString("Print.18"); 
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute)
				outputtext = Messages.getString("Print.19"); 
			// Mouse
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge)
				outputtext = Messages.getString("Print.20"); 
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge)
				outputtext = Messages.getString("Print.21"); 
			//Ask User
			else if (o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber)
				outputtext = Messages.getString("Print.22"); 
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo)
				outputtext = Messages.getString("Print.23"); 
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString)
				outputtext = Messages.getString("Print.24"); 
			//Random
			else if	(o instanceof edu.cmu.cs.stage3.alice.core.question.RandomNumber)
				outputtext = Messages.getString("Print.25"); 
			
			Object value = Print.this.object.getValue();
			if (value instanceof Double){
				java.text.NumberFormat formatter = new java.text.DecimalFormat("#.######"); 
				value = Double.valueOf(formatter.format( value ));
			}
			
			String valueText = Messages.getString("Print.27"); 
			if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
				valueText = ((edu.cmu.cs.stage3.alice.core.Element)value).getTrimmedKey();
			} else if ( value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color ) {
				double blue = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getBlue();
				double green = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getGreen();
				double red = ((edu.cmu.cs.stage3.alice.scenegraph.Color) value).getRed();
				if ( blue == 1 && green == 1 && red == 1)
					valueText = Messages.getString("Print.28"); 
				else if ( blue == 0 && green == 0 && red == 0)
					valueText = Messages.getString("Print.29"); 
				else if ( blue == 0 && green == 0 && red == 1)
					valueText = Messages.getString("Print.30"); 
				else if ( blue == 0 && green == 1 && red == 0)
					valueText = Messages.getString("Print.31"); 
				else if ( blue == 1 && green == 0 && red == 0)
					valueText = Messages.getString("Print.32"); 
				else if ( blue == 0 && green == 1 && red == 1)
					valueText = Messages.getString("Print.33"); 
				else if ( blue == 0.501960813999176 && green == 0 && red == 0.501960813999176)
					valueText = Messages.getString("Print.34"); 
				else if ( blue == 0 && green == 0.6470588445663452 && red == 1)
					valueText = Messages.getString("Print.35"); 
				else if ( blue == 0.686274528503418 && green == 0.686274528503418 && red == 1)
					valueText = Messages.getString("Print.36"); 
				else if ( blue == 0.16470588743686676 && green == 0.16470588743686676 && red == 0.6352941393852234)
					valueText = Messages.getString("Print.37"); 
				else if ( blue == 1 && green == 1 && red == 0)
					valueText = Messages.getString("Print.38"); 
				else if ( blue == 1 && green == 0 && red == 1)
					valueText = Messages.getString("Print.39"); 
				else if ( blue == 0.501960813999176 && green == 0.501960813999176 && red == 0.501960813999176)
					valueText = Messages.getString("Print.40"); 
				else if ( blue == 0.7529411911964417 && green == 0.7529411911964417 && red == 0.7529411911964417)
					valueText = Messages.getString("Print.41"); 
				else if ( blue == 0.250980406999588 && green == 0.250980406999588 && red == 0.250980406999588)
					valueText = Messages.getString("Print.42"); 
				else {
					valueText = value.toString();
					valueText = valueText.substring(valueText.indexOf(Messages.getString("Print.43")) , valueText.length()); 
				}
			} else if( value != null ) {
				valueText = value.toString();
			}

			String output;
			if( s != null ) {
				if( o != null ) {
					output = s + valueText;
				} else {
					output = s;
				}
			} else {
				if( o != null ) {
					output = Print.this.getPrefix();
					if (outputtext != null)
						output = output.substring(0,(output.indexOf("__")-1))+" "+outputtext+valueText;  
					else
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
