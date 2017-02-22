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

package edu.cmu.cs.stage3.alice.authoringtool.util;

public class PropertyUndoableRedoable implements ContextAssignableUndoableRedoable {
	protected edu.cmu.cs.stage3.alice.core.Property property;
	protected Object oldValue;
	protected Object newValue;
	protected Object context;

	/**
	 * @deprecated  please use other constructor
	 */
	public PropertyUndoableRedoable( edu.cmu.cs.stage3.alice.core.Property property, Object oldValue, Object newValue, Object context ) {
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public PropertyUndoableRedoable( edu.cmu.cs.stage3.alice.core.Property property, Object oldValue, Object newValue ) {
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public void setContext( Object context ) {
		this.context = context;
	}

	public void undo() {
		property.set( oldValue );
	}

	public void redo() {
		property.set( newValue );
	}

	public Object getAffectedObject() {
		return property.getOwner();
	}

	public Object getContext() {
		return context;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append( "edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable[ " );
		s.append( "property=" + property + "; " );
		s.append( "oldValue=" + oldValue + "; " );
		s.append( "newValue=" + newValue + "; " );
		if( (context != this) && !(context instanceof DefaultUndoRedoStack) ) {  // watch out for infinite loops
			s.append( "context=" + context + "; " );
		} else {
			s.append( "context=" + context.getClass() + "; " );
		}
		s.append( " ]" );
		return s.toString();
	}
	
	// Logging - this is picking up prop changes in methods
	public String getLogString() {
	  String logString = "TIME=<" + System.currentTimeMillis() + "> ";
	  String oldValueString = "null";
	  String newValueString = "null";
	  String ownerKey = "";

	  if (oldValue instanceof edu.cmu.cs.stage3.alice.core.Element) {
		oldValueString = ((edu.cmu.cs.stage3.alice.core.Element)oldValue).getKey();
	  } else {
		if (oldValue != null) oldValueString = oldValue.toString();
	  }

	  if (newValue instanceof edu.cmu.cs.stage3.alice.core.Element) {
		newValueString = ((edu.cmu.cs.stage3.alice.core.Element)newValue).getKey();
	  } else {
		if (newValue != null) newValueString = newValue.toString();
	  }

	  if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
		edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse cudResponse = (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) property.getOwner();
		String methodName = "";
		if (cudResponse.userDefinedResponse.get() instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) {
		  edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse udResponse = ((edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse) cudResponse.userDefinedResponse.get());
		  methodName = udResponse.getKey();
		}

		ownerKey = methodName + "(" + property.getOwner().getKey() + ")";
	  } else if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Response) {
		  String responseType = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(property.getOwner().getClass());
		  // get rid of the non-name parts of the string coming back
		  if (responseType.indexOf("set") != -1) {
			responseType = "set";
		  } else {
			if ( (responseType.indexOf("<<<") != -1) && (responseType.indexOf(">>>") != -1) ) {
			  responseType = responseType.substring(responseType.indexOf(">>>") + 3, responseType.length());
			}
			if ( (responseType.indexOf("<") != -1) && (responseType.indexOf(">") != -1) ) {
			  responseType = responseType.substring(0, responseType.indexOf("<"));
			}
		  }
		  ownerKey = responseType.trim() + "(" + property.getOwner().getKey() + ")";
	  } else {
		ownerKey = property.getOwner().getKey();
	  }

	  if ( ! (property.getName().equals("data")) ) {
		logString += "EVENT=<propertyChange> " + "PROPERTYNAME=<" + property.getName() + "> " + "PROPERTYOWNER=<" + ownerKey + "> "  + "OLDVALUE=<" + oldValueString + "> " + "NEWVALUE=<" + newValueString + ">";
		return logString;
	  }
	return null;


	}

}
