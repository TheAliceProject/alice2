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
public class StencilStateCapsule implements edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule {
	protected java.util.ArrayList existantElements = new java.util.ArrayList();
	protected java.util.ArrayList nonExistantElements = new java.util.ArrayList();
	protected java.util.HashMap propertyValues = new java.util.HashMap();

        protected java.util.HashMap elementPositions = new java.util.HashMap();

	/**
	 * elementKey should be the element's key, relative to the world;  i.e. element.getKey( world ).
	 */
	public void addExistantElement( String elementKey ) {
		existantElements.add( elementKey );
	}

	/**
	 * elementKey should be the element's key, relative to the world;  i.e. element.getKey( world ).
	 */
	public void removeExistantElement( String elementKey ) {
		existantElements.remove( elementKey );
	}

	public String[] getExistantElements() {
		return (String[])existantElements.toArray( new String[0] );
	}

	/**
	 * elementKey should be the element's key, relative to the world;  i.e. element.getKey( world ).
	 */
	public void addNonExistantElement( String elementKey ) {
		nonExistantElements.add( elementKey );
	}

	/**
	 * elementKey should be the element's key, relative to the world;  i.e. element.getKey( world ).
	 */
	public void removeNonExistantElement( String elementKey ) {
		nonExistantElements.remove( elementKey );
	}

	public String[] getNonExistantElements() {
		return (String[])nonExistantElements.toArray( new String[0] );
	}

	/**
	 * propertyKey should be of the form (element key relative to world).(property name).
	 * i.e. "Bunny.Head.color"
	 *
	 * valueRepr should be the String representation of the property's value as returned by
	 * AuthoringToolResources.getReprForValue( property.get(), true )
	 */
	public void putPropertyValue( String propertyKey, String valueRepr ) {
		propertyValues.put( propertyKey, valueRepr );
	}

	/**
	 * propertyKey should be of the form (element key relative to world).(property name).
	 * i.e. "Bunny.Head.color"
	 */
	public void removePropertyValue( String propertyKey ) {
		propertyValues.remove( propertyKey );
	}

        /**
	 * elementKey should be the element's key, relative to the world;  i.e. element.getKey( world ).
         *
         * position should be the element's index relative to its parent
	 */
	public void putElementPosition( String elementKey, int position ) {
                elementPositions.put( elementKey, new Integer(position));
	}

	/**
	 * elementKey should be the element's key, relative to the world;  i.e. element.getKey( world ).
	 */
	public void removeElementPosition( String elementKey ) {
		elementPositions.remove( elementKey );
	}

        public int getElementPosition( String elementKey ) {
          Integer value = (Integer)elementPositions.get( elementKey );
          if (value != null) {
            return value.intValue();
          } else {
            return -1;
          }
        }

	/**
	 * propertyKey should be of the form (element key relative to world).