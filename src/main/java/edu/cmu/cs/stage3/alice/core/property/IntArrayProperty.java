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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Messages;

public class IntArrayProperty extends ObjectProperty {
	public IntArrayProperty( Element owner, String name, int[] defaultValue ) {
		super( owner, name, defaultValue, int[].class );
	}
	public int[] getIntArrayValue() {
		return (int[])getValue();
	}
	public int size() {
		int[] value = getIntArrayValue();
		if( value!=null ) {
			return value.length;
		} else {
			return 0;
		}
	}
    
	protected void decodeObject( org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version ) throws java.io.IOException {
		m_associatedFileKey = null;
        String filename = getFilename( getNodeText( node ) );
        String extension = filename.substring( filename.lastIndexOf( '.' )+1 );
        int[] indicesValue;
        java.io.InputStream is = loader.readFile( filename );
        if( extension.equalsIgnoreCase( "vfb" ) ) { //$NON-NLS-1$
            indicesValue = edu.cmu.cs.stage3.alice.scenegraph.io.VFB.loadIndices( is );
        } else {
            indicesValue = edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.loadIndices( is );
        }
        loader.closeCurrentFile();
        set( indicesValue );
        try {
            m_associatedFileKey = loader.getKeepKey( filename );
        } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
            m_associatedFileKey = null;
        }
    }
    
	protected void encodeObject( org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator ) throws java.io.IOException {
		int[] indicesValue = getIntArrayValue();
        String filename = "indices.bin"; //$NON-NLS-1$
        Object associatedFileKey;
        try {
            associatedFileKey = storer.getKeepKey( filename );
        } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
            associatedFileKey = null;
        }
        if( m_associatedFileKey==null || !m_associatedFileKey.equals( associatedFileKey ) ) {
            m_associatedFileKey = null;
            java.io.OutputStream os = storer.createFile( filename, true );
            edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray.storeIndices( indicesValue, os );
            storer.closeCurrentFile();
            m_associatedFileKey = associatedFileKey;
        } else {
            if( storer.isKeepFileSupported() ) {
                try {
                    storer.keepFile( filename );
                } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
                    throw new Error( storer + Messages.getString("IntArrayProperty.2") + kfnse ); //$NON-NLS-1$
                } catch( edu.cmu.cs.stage3.io.KeepFileDoesNotExistException kfdne ) {
                    throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper( kfdne, filename );
                }
            }
        }
        node.appendChild( createNodeForString( document, "java.io.File["+filename+"]" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
	public void keepAnyAssociatedFiles( edu.cmu.cs.stage3.io.DirectoryTreeStorer storer ) throws edu.cmu.cs.stage3.io.KeepFileNotSupportedException, edu.cmu.cs.stage3.io.KeepFileDoesNotExistException {
        super.keepAnyAssociatedFiles( storer );
        String filename = "indices.bin"; //$NON-NLS-1$
        storer.keepFile( filename );
    }
}