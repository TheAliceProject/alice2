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

public class ChildChangeUndoableRedoable implements ContextAssignableUndoableRedoable {
	protected edu.cmu.cs.stage3.alice.core.Element parent;
	protected edu.cmu.cs.stage3.alice.core.Element child;
	protected int changeType;
	protected int oldIndex;
	protected int newIndex;
	protected Object context;

	public ChildChangeUndoableRedoable( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent ) {
		this.parent = (edu.cmu.cs.stage3.alice.core.Element)childrenEvent.getSource();
		this.child = childrenEvent.getChild();
		this.changeType = childrenEvent.getChangeType();
		this.oldIndex = childrenEvent.getOldIndex();
		this.newIndex = childrenEvent.getNewIndex();
	}

	/**
	 * @deprecated  use other constructor
	 */
	public ChildChangeUndoableRedoable( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childrenEvent, Object context ) {
		this.parent = (edu.cmu.cs.stage3.alice.core.Element)childrenEvent.getSource();
		this.child = childrenEvent.getChild();
		this.changeType = childrenEvent.getChangeType();
		this.oldIndex = childrenEvent.getOldIndex();
		this.newIndex = childrenEvent.getNewIndex();
	}

	public void setContext( Object context ) {
		this.context = context;
	}

	public void undo() {
		if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
			child.removeFromParent();
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
			parent.insertChildAt( child, oldIndex );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED ) {
			parent.insertChildAt( child, oldIndex );
		}
	}

	public void redo() {
		if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
			parent.insertChildAt( child, newIndex );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
			parent.removeChild( child );
		} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED ) {
			parent.insertChildAt( child, newIndex );
		}
	}

	public Object getAffectedObject() {
		return child;
	}

	public Object getContext() {
		return context;
	}
	
	public String getLogString() {
		  String logString = "TIME=<" + System.currentTimeMillis() + "> ";

		  if (child instanceof edu.cmu.cs.stage3.alice.core.Response) {

			// handle user defined responses
			if (child instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse) {
			  String responseType = child.getClass().getName();
			  edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse udResponse = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)child).userDefinedResponse.get();
			  String methodName = "methodCopied";
			  if (udResponse != null) {
			  	methodName = udResponse.getKey();
			  }

			  edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty eaProp = ((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)child).requiredActualParameters;
			  edu.cmu.cs.stage3.alice.core.Element[] elems = eaProp.getElementArrayValue();

			  String params = "";

			  for (int i = 0; i < elems.length; i++) {
				if (elems[i] instanceof edu.cmu.cs.stage3.alice.core.Variable) {
				  edu.cmu.cs.stage3.alice.core.Variable var = (edu.cmu.cs.stage3.alice.core.Variable) elems[i];
				  params += var.getKey(child).toUpperCase() + "=<" + var.getValue() + "> ";
				}
			  }


			  if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
				logString += "EVENT=<insertCallToUserResponse> " + "RESPONSETYPE=<" + responseType + "> " + "RESPONSENAME=<" + methodName + "> " + params + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "NEWINDEX=<" + newIndex + ">";
			  } else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
				logString += "EVENT=<deleteCallToUserResponse> " + "RESPONSETYPE=<" + responseType + "> " + "RESPONSENAME=<" + methodName + "> " + params + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + ">";
			  } else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED ) {
				logString += "EVENT=<shiftCallToUserResponse> " + "RESPONSETYPE=<" + responseType + "> " + "RESPONSENAME=<" + methodName + "> " + params + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + "> " + "NEWINDEX=<" + newIndex + ">";
			  }

			// handle other kinds of responses
			} else {
			  String responseType = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(child.getClass());
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

			  edu.cmu.cs.stage3.alice.core.Response resp = (edu.cmu.cs.stage3.alice.core.Response) child;

			  String propNames[] = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getInitialVisibleProperties(child.getClass());
			  String propValues = "";
			  for (int i = 0; i < propNames.length; i++) {
				edu.cmu.cs.stage3.alice.core.Property prop = child.getPropertyNamed(propNames[i]);
				propValues += propNames[i].toUpperCase() + "=<" + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(prop.get()) + "> ";
			  }

			  if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
				logString += "EVENT=<insertResponse> " + "RESPONSETYPE=<" + child.getClass().getName() + "> " + "RESPONSENAME=<" + responseType + "> " + propValues + " " + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "NEWINDEX=<" + newIndex + ">";
			  } else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
				logString += "EVENT=<deleteResponse> " + "RESPONSETYPE=<" + child.getClass().getName() + "> " + "RESPONSENAME=<" + responseType + "> " + propValues + " " + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + ">";
			  } else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED ) {
				logString += "EVENT=<shiftResponse> " + "RESPONSETYPE=<" + child.getClass().getName() + "> " + "RESPONSENAME=<" + responseType + "> " + propValues + " " + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + "> " + "NEWINDEX=<" + newIndex + ">";
			  }

			}

		  } else {
			if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
			  logString += "EVENT=<insertChild> " + "CHILDTYPE=<" + child.getClass().getName() + "> " + "CHILDKEY=<" + child.getKey() + "> " + "PARENTKEY=<" +  parent.getKey() + "> " + "NEWINDEX=<" + newIndex + ">";
			} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
			  logString += "EVENT=<deleteChild> " + "CHILDTYPE=<" + child.getClass().getName() + "> " + "CHILDKEY=<" + child.getKey() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + ">";
			} else if( changeType == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_SHIFTED ) {
			  logString += "EVENT=<shiftChild> " + "CHILDTYPE=<" + child.getClass().getName() + "> " + "CHILDKEY=<" + child.getKey() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + "> " + "NEWINDEX=<" + newIndex + ">";
			}
		  }

		  return logString;
		}

}
