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

package edu.cmu.cs.stage3.alice.authoringtool.importers;

import edu.cmu.cs.stage3.lang.Messages;

/**
 * @author Jason Pratt
 */
public class ImageImporter extends edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter {
	public java.util.Map getExtensionMap() {
		java.util.HashMap knownCodecPrettyNames = new java.util.HashMap();
		knownCodecPrettyNames.put( "BMP", Messages.getString("Windows_Bitmap") );  
		knownCodecPrettyNames.put( "GIF", Messages.getString("Graphic_Interchange_Format") );  
		knownCodecPrettyNames.put( "JPEG", Messages.getString("Joint_Photographic_Experts_Group_format") );  
		knownCodecPrettyNames.put( "PNG", Messages.getString("Portable_Network_Graphics_format") );  
		knownCodecPrettyNames.put( "TIFF", Messages.getString("Tagged_Image_File_Format") );  

		java.util.HashMap map = new java.util.HashMap();

		String[] codecNames = edu.cmu.cs.stage3.image.ImageIO.getCodecNames();
		for( int i = 0; i < codecNames.length; i++ ) {
			String prettyName = (String)knownCodecPrettyNames.get( codecNames[i].toUpperCase() );
			if( prettyName == null ) {
				prettyName = codecNames[i];
			}
			String[] extensions = edu.cmu.cs.stage3.image.ImageIO.getExtensionsForCodec( codecNames[i] );
			for( int j = 0; j < extensions.length; j++ ) {
				map.put( extensions[j].toUpperCase(), prettyName );
			}
		}

		return map;
	}

	protected edu.cmu.cs.stage3.alice.core.Element load( java.io.InputStream istream, String ext ) throws java.io.IOException {
		String codecName = edu.cmu.cs.stage3.image.ImageIO.mapExtensionToCodecName( ext );
		if( codecName == null ) {
			throw new IllegalArgumentException( Messages.getString("Unsupported_Extension__", ext) ); 
		}

		java.io.BufferedInputStream bis;
		if( istream instanceof java.io.BufferedInputStream ) {
			bis = (java.io.BufferedInputStream)istream;
		} else {
			bis = new java.io.BufferedInputStream( istream );
		}
		java.awt.Image image = edu.cmu.cs.stage3.image.ImageIO.load( codecName, bis );

		edu.cmu.cs.stage3.alice.core.TextureMap texture = new edu.cmu.cs.stage3.alice.core.TextureMap();

		if( image instanceof java.awt.image.BufferedImage ) {
			java.awt.image.BufferedImage bi = (java.awt.image.BufferedImage)image;
			if( bi.getColorModel().hasAlpha() ) {
				texture.format.set( new Integer( edu.cmu.cs.stage3.alice.scenegraph.TextureMap.RGBA ) );
			}
		}

		texture.name.set( plainName );
		texture.image.set( image );

		return texture;
	}
}
