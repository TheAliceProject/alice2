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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer;

class TextureMapProxy extends ElementProxy {
    private java.util.Vector m_appearances = new java.util.Vector();
    private javax.media.j3d.Texture m_j3dTexture = null;
    public javax.media.j3d.Texture getJ3DTexture() {
        return m_j3dTexture;
    }
	public void addAppearance( AppearanceProxy appearance ) {
		m_appearances.addElement( appearance );
	}
	public void removeAppearance( AppearanceProxy appearance ) {
		m_appearances.removeElement( appearance );
	}
    protected void updateAppearances() {
        for( int i=0; i<m_appearances.size(); i++ ) {
            AppearanceProxy appearance = (AppearanceProxy)m_appearances.elementAt( i );
            appearance.updateJ3DTexture( m_j3dTexture );
        }
    }
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.TextureMap.IMAGE_PROPERTY ) {
            m_j3dTexture = null;
            java.awt.image.BufferedImage image = (java.awt.image.BufferedImage)value;
            if( image != null ) {
				m_j3dTexture = new com.sun.j3d.utils.image.TextureLoader( image ).getTexture();
            }
            updateAppearances();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.TextureMap.FORMAT_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}
