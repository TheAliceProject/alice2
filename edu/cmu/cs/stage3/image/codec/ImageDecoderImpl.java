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

package edu.cmu.cs.stage3.image.codec;

/*
 * The contents of this file are subject to the  JAVA ADVANCED IMAGING
 * SAMPLE INPUT-OUTPUT CODECS AND WIDGET HANDLING SOURCE CODE  License
 * Version 1.0 (the "License"); You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/software/imaging/JAI/index.html
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is JAVA ADVANCED IMAGING SAMPLE INPUT-OUTPUT CODECS
 * AND WIDGET HANDLING SOURCE CODE.
 * The Initial Developer of the Original Code is: Sun Microsystems, Inc..
 * Portions created by: _______________________________________
 * are Copyright (C): _______________________________________
 * All Rights Reserved.
 * Contributor(s): _______________________________________
 */

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * A partial implementation of the <code>ImageDecoder</code> interface
 * useful for subclassing.
 *
 * <p><b> This class is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 */
public abstract class ImageDecoderImpl implements ImageDecoder {

    /**
     * The <code>SeekableStream</code> associcted with this
     * <code>ImageEncoder</code>.
     */
    protected SeekableStream input;

    /**
     * The <code>ImageDecodeParam</code> object associated with this
     * <code>ImageEncoder</code>.
     */
    protected ImageDecodeParam param;

    /**
     * Constructs an <code>ImageDecoderImpl</code> with a given
     * <code>SeekableStream</code> and <code>ImageDecodeParam</code>
     * instance.
     */
    public ImageDecoderImpl(SeekableStream input,
                            ImageDecodeParam param) {
        this.input = input;
        this.param = param;
    }

    /**
     * Constructs an <code>ImageDecoderImpl</code> with a given
     * <code>InputStream</code> and <code>ImageDecodeParam</code>
     * instance.  The <code>input</code> parameter will be used to
     * construct a <code>ForwardSeekableStream</code>; if the ability
     * to seek backwards is required, the caller should construct
     * an instance of <code>SeekableStream</code> and
     * make use of the other contructor.
     */
    public ImageDecoderImpl(InputStream input,
                            ImageDecodeParam param) {
        this.input = new ForwardSeekableStream(input);
        this.param = param;
    }

    /**
     * Returns the current parameters as an instance of the
     * <code>ImageDecodeParam</code> interface.  Concrete
     * implementations of this interface will return corresponding
     * concrete implementations of the <code>ImageDecodeParam</code>
     * interface.  For example, a <code>JPEGImageDecoder</code> will
     * return an instance of <code>JPEGDecodeP