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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A subclass of <code>SeekableStream</code> that takes its input
 * from a <code>File</code> or <code>RandomAccessFile</code>.
 * Backwards seeking is supported.  The <code>mark()</code> and
 * <code>resest()</code> methods are supported.
 *
 * <p><b> This class is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 */
public class FileSeekableStream extends SeekableStream {

    private RandomAccessFile file;
    private long markPos = -1;

    // Base 2 logarithm of the cache page size
    private static final int PAGE_SHIFT = 9;

    // The page size, derived from PAGE_SHIFT
    private static final int PAGE_SIZE = 1 << PAGE_SHIFT;

    // Binary mask to find the offset of a pointer within a cache page
    private static final int PAGE_MASK = PAGE_SIZE - 1;

    // Number of pages to cache
    private static final int NUM_PAGES = 32;

    // Reads longer than this bypass the cache
    private static final int READ_CACHE_LIMIT = PAGE_SIZE;

    // The page cache
    private byte[][] pageBuf = new byte[PAGE_SIZE][NUM_PAGES];

    // The index of the file page held in a given cache entry,
    // -1 = invalid.
    private int[] currentPage = new int[NUM_PAGES];

    private long length = 0L;

    private long pointer = 0L;

    /**
     * Constructs a <code>FileSeekableStream</code> from a
     * <code>RandomAccessFile</code>.
     */
    public FileSeekableStream(RandomAccessFile file) throws IOException {
        this.file = file;
        file.seek(0L);
        this.length = file.length();

        // Allocate the cache pages and mark them as invalid
        for (int i = 0; i < NUM_PAGES; i++) {
            pageBuf[i] = new byte[PAGE_SIZE];
            currentPage[i] = -1;
        }
    }

    /**
     * Constructs a <code>FileSeekableStream</code> from a
     * <code>File</code>.
     */
    public FileSeekableStream(File file) throws IOException {
        this(new RandomAccessFile(file, "r"));
    }
