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

/**
 * @author Jason Pratt
 */
public class OneShotUndoableRedoable implements ContextAssignableUndoableRedoable {
	protected Object context;
	protected edu.cmu.cs.stage3.alice.core.Response redoResponse;
	protected edu.cmu.cs.stage3.alice.core.Response undoResponse;
	protected OneShotSimpleBehavior oneShotBehavior;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;

	/**
	 * use this constructor if you already have a OneShotSimpleBehavior that's being run.
	 * if undo or redo are called while the bevavior is still being scheduled, its response
	 * will be stopped before the undo or redo occurs.
	 */
	public OneShotUndoableRedoable( edu.cmu.cs.stage3.alice.core.Response redoResponse, edu.cmu.cs.stage3.alice.core.Response undoResponse, OneShotSimpleBehavior oneShotBehavior, edu.cmu.cs.stage3.alice.core.Scheduler scheduler ) {
		this.redoResponse = redoResponse;
		this.undoResponse = undoResponse;
		this.oneShotBehavior = oneShotBehavior;
		this.scheduler = scheduler;
	}

	/**
	 * this constructor is a convenience, in case you don't already have a OneShotSimpleBehavior built.
	 */
	public OneShotUndoableRedoable( edu.cmu.cs.stage3.alice.core.Response redoResponse, edu.cmu.cs.stage3.alice.core.Response undoResponse, edu.cmu.cs.stage3.alice.core.Property[] affectedProperties, edu.cmu.cs.stage3.alice.core.Scheduler scheduler ) {
		this.redoResponse = redoResponse;
		this.undoResponse = undoResponse;
		this.oneShotBehavior = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior();
		this.oneShotBehavior.setAffectedProperties( affectedProperties );
		this.scheduler = scheduler;
	}

	public void setContext( Object context ) {
		this.context = context;
	}

	public void undo() {
		oneShotBehavior.stopRunningResponse( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getCurrentTime() );
		oneShotBehavior.setResponse( undoResponse );
		oneShotBehavior.start( scheduler );
	}

	public void redo() {
		oneShotBehavior.stopRunningResponse( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getCurrentTime() );
		oneShotBehavior.setResponse( redoResponse );
		oneShotBehavior.start( scheduler );
	}

	public Object getAffectedObject() {
		return this;
	}

	public Object getContext() {
		return context;
	}

	public edu.cmu.cs.stage3.alice.core.Response getRedoResponse() {
		return redoResponse;
	}

	public edu.cmu.cs.stage3.alice.core.Response getUndoResponse() {
		return undoResponse;
	}

	public OneShotSimpleBehavior getOneShotBehavior() {
		return oneShotBehavior;
	}
	
	// Logging
	public String getLogString() {
	  String logString = "TIME=" + "<" +System.currentTimeMillis() + "> ";

	  String responseType = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(redoResponse.getClass());
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
	  responseType = "<" + responseType.trim() + ">";

	  String propNames[] = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getInitialVisibleProperties(redoResponse.getClass());
	  String propValues = "";
	  for (int i = 0; i < propNames.length; i++) {
		edu.cmu.cs.stage3.alice.core.Property prop = redoResponse.getPropertyNamed(propNames[i]);
		propValues += propNames[i].toUpperCase() + "=<" + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(prop.get()) + "> ";
	  }

	  logString += "EVENT=<oneshot> " + " RESPONSE=" + responseType + " " + propValues;

	  return logString;
	}

}
