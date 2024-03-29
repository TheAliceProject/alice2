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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import edu.cmu.cs.stage3.lang.Messages;

class WritableRasterJAI extends WritableRaster {

    protected WritableRasterJAI(SampleModel sampleModel,
                                DataBuffer dataBuffer,
                                Rectangle aRegion,
                                Point sampleModelTranslate,
                                WritableRaster parent){
        super(sampleModel, dataBuffer, aRegion,
              sampleModelTranslate, parent);
    }
}

/**
 * A convenience class for the construction of various types of
 * <code>WritableRaster</code> and <code>SampleModel</code> objects.
 *
 * <p> This class provides the capability of creating
 * <code>Raster</code>s with the enumerated data types in the
 * java.awt.image.DataBuffer.
 *
 * <p> In come cases, instances of
 * <code>ComponentSampleModelJAI</code>, a subclass of
 * <code>java.awt.image.ComponentSampleModel</code> are instantiated
 * instead of <code>java.awt.image.BandedSampleModel</code> in order
 * to work around bugs in the current release of the Java 2 SDK.
 */
public class RasterFactory {

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>PixelInterleavedSampleModel</code> with the specified
     * data type, width, height, and number of bands.
     *
     * <p> The upper left corner of the <code>WritableRaster</code> is
     * given by the <code>location</code> argument.  If
     * <code>location</code> is <code>null</code>, (0, 0) will be
     * used.  The <code>dataType</code> parameter should be one of the
     * enumerated values defined in the <code>DataBuffer</code> class.
     *
     * @param dataType The data type of the <code>SampleModel</code>,
     *        one of <code>DataBuffer.TYPE_BYTE</code>,
     *        <code>TYPE_USHORT</code>,
     *        <code>TYPE_SHORT</code>,
     *        <code>TYPE_INT</code>,
     *        <code>TYPE_FLOAT</code>, or
     *        <code>TYPE_DOUBLE</code>.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param numBands The desired number of bands.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException if <code>numbands</code> is
     *         <code><1</code>.
     */
    public static WritableRaster createInterleavedRaster(int dataType,
                                                         int width, int height,
                                                         int numBands,
                                                         Point location) {
        if (numBands < 1) {
            throw new IllegalArgumentException(Messages.getString("Number_of_bands_must_be_greater_than_0_"));
        }
        int[] bandOffsets = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            bandOffsets[i] = numBands - 1 - i;
        }
        return createInterleavedRaster(dataType, width, height,
                                       width*numBands, numBands,
                                       bandOffsets, location);
    }

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>PixelInterleavedSampleModel</code> with the specified
     * data type, width, height, scanline stride, pixel stride, and
     * band offsets.  The number of bands is inferred from
     * bandOffsets.length.
     *
     * <p> The upper left corner of the <code>WritableRaster</code> is
     * given by the <code>location</code> argument.  If
     * <code>location</code> is <code>null</code>, (0, 0) will be
     * used.  The <code>dataType</code> parameter should be one of the
     * enumerated values defined in the <code>DataBuffer</code> class.
     *
     * @param dataType The data type of the <code>WritableRaster</code>,
     *        one of the enumerated dataType values in
     *        java.awt.image.DataBuffer.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param scanlineStride The desired scanline stride.
     * @param pixelStride The desired pixel stride.
     * @param bandOffsets An array of <code>int</code>s indicating the
     *        relative offsets of the bands within a pixel.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException if <code>bandOffsets</code> is
     *         <code>null</code>, <code>dataType</code> is not one of
     *         the enumerated dataType value of java.awt.image.DataBuffer.
     *
     * @throws IllegalArgumentException if the number of array elements
     *         required by the returned <code>WritableRaster</code>
     *         would exceed <code>Integer.MAX_VALUE</code>.
     */
    public static WritableRaster createInterleavedRaster(int dataType,
                                                         int width, int height,
                                                         int scanlineStride,
                                                         int pixelStride,
                                                         int bandOffsets[],
                                                         Point location) {

        if (bandOffsets == null) {
            throw new IllegalArgumentException(Messages.getString("Band_offsets_array_is_null_"));
        }

	DataBuffer d;
        int bands = bandOffsets.length;

        int maxBandOff = bandOffsets[0];
        for (int i=1; i < bands; i++) {
            if (bandOffsets[i] > maxBandOff) {
                maxBandOff = bandOffsets[i];
            }
        }

        long lsize = maxBandOff +
            (long)scanlineStride*(height - 1) + (long)pixelStride*(width - 1) +
            1L;
        if (lsize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(Messages.getString("Size_of_array_must_be_smaller_than_Integer_MAX_VALUE_"));
        }
        int size = (int)lsize;

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            d = new DataBufferByte(size);
            break;

        case DataBuffer.TYPE_USHORT:
            d = new DataBufferUShort(size);
            break;

        case DataBuffer.TYPE_SHORT:
            d = new DataBufferShort(size);
            break;

        case DataBuffer.TYPE_INT:
            d = new DataBufferInt(size);
            break;

        case DataBuffer.TYPE_FLOAT:
            d = new DataBufferFloat(size);
            break;

        case DataBuffer.TYPE_DOUBLE:
            d = new DataBufferDouble(size);
            break;

        default:
            throw new IllegalArgumentException(Messages.getString("Unsupported_data_type_"));
        }

        return createInterleavedRaster(d, width, height, scanlineStride,
                                       pixelStride, bandOffsets, location);
    }

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>ComponentSampleModel</code> with the specified data type,
     * width, height, and number of bands.
     *
     * <p> Note that the <code>Raster</code>'s
     * <code>SampleModel</code> will be of type
     * <code>ComponentSampleModel</code>, not
     * <code>BandedSampleModel</code> as might be expected.
     *
     * <p> The upper left corner of the <code>WritableRaster</code> is
     * given by the <code>location</code> argument.  If
     * <code>location</code> is <code>null</code>, (0, 0) will be
     * used.  The <code>dataType</code> parameter should be one of the
     * enumerated values defined in the <code>DataBuffer</code> class.
     *
     * @param dataType The data type of the <code>WritableRaster</code>,
     *        one of the enumerated dataType values in
     *        java.awt.image.DataBuffer.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param bands The desired number of bands.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException if <code>bands</code> is
     *         <code><1</code>.
     */
    public static WritableRaster createBandedRaster(int dataType,
                                                    int width, int height,
                                                    int bands,
                                                    Point location) {
        if (bands < 1) {
            throw new IllegalArgumentException(Messages.getString("Number_of_bands_must_be_greater_than_0_"));
        }
        int[] bankIndices = new int[bands];
        int[] bandOffsets = new int[bands];
        for (int i = 0; i < bands; i++) {
            bankIndices[i] = i;
            bandOffsets[i] = 0;
        }

        return createBandedRaster(dataType, width, height, width,
                                  bankIndices, bandOffsets,
                                  location);
    }

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>ComponentSampleModel</code> with the specified data type,
     * width, height, scanline stride, bank indices and band offsets.
     * The number of bands is inferred from
     * <code>bankIndices.length</code> and
     * <code>bandOffsets.length</code>, which must be the same.
     *
     * <p> Note that the <code>Raster</code>'s
     * <code>SampleModel</code> will be of type
     * <code>ComponentSampleModel</code>, not
     * <code>BandedSampleModel</code> as might be expected.
     *
     * <p> The upper left corner of the <code>WritableRaster</code> is
     * given by the <code>location</code> argument.  The
     * <code>dataType</code> parameter should be one of the enumerated
     * values defined in the <code>DataBuffer</code> class.
     *
     * @param dataType The data type of the <code>WritableRaster</code>,
     *        one of the enumerated dataType values in
     *        java.awt.image.DataBuffer.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param scanlineStride The desired scanline stride.
     * @param bankIndices An array of <code>int</code>s indicating the
     *        bank index for each band.
     * @param bandOffsets An array of <code>int</code>s indicating the
     *        relative offsets of the bands within a pixel.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException if <code>bankIndices</code> is
     *         <code>null</code>, <code>bandOffsets</code> is
     *         <code>null</code>, if <code>bandOffsets.length</code>
     *         is <code>!=</code> <code>bankIndices.length</code>,
     *         if <code>dataType</code> is not one of the enumerated
     *         datatypes of java.awt.image.DataBuffer.
     */
    public static WritableRaster createBandedRaster(int dataType,
                                                    int width, int height,
                                                    int scanlineStride,
                                                    int bankIndices[],
                                                    int bandOffsets[],
                                                    Point location) {
	DataBuffer d;
        

        if (bankIndices == null) {
            throw new IllegalArgumentException(Messages.getString("Bank_indices_array_is_null_"));
        }
        if (bandOffsets == null) {
            throw new IllegalArgumentException(Messages.getString("Band_offsets_array_is_null_"));
        }
        
        if (bandOffsets.length != bankIndices.length) {
            throw new IllegalArgumentException(Messages.getString("bankIndices_length____bandOffsets_length"));
        }

        // Figure out the #banks and the largest band offset
        int maxBank = bankIndices[0];
        int maxBandOff = bandOffsets[0];
        for (int i = 1; i < bandOffsets.length; i++) {
            if (bankIndices[i] > maxBank) {
                maxBank = bankIndices[i];
            }
            if (bandOffsets[i] > maxBandOff) {
                maxBandOff = bandOffsets[i];
            }
        }

        int banks = maxBank + 1;
        long lsize = maxBandOff + (long)scanlineStride*(height - 1) +
            (width - 1) + 1L;
        if (lsize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(Messages.getString("Size_of_array_must_be_smaller_than_Integer_MAX_VALUE_"));
        }
        int size = (int)lsize;

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            d = new DataBufferByte(size, banks);
            break;

        case DataBuffer.TYPE_USHORT:
            d = new DataBufferUShort(size, banks);
            break;

        case DataBuffer.TYPE_SHORT:
            d = new DataBufferShort(size, banks);
            break;

        case DataBuffer.TYPE_INT:
            d = new DataBufferInt(size, banks);
            break;

        case DataBuffer.TYPE_FLOAT:
            d = new DataBufferFloat(size, banks);
            break;

        case DataBuffer.TYPE_DOUBLE:
            d = new DataBufferDouble(size, banks);
            break;

        default:
            throw new IllegalArgumentException(Messages.getString("Unsupported_data_type_"));
        }

        return createBandedRaster(d, width, height, scanlineStride,
                                  bankIndices, bandOffsets, location);
    }

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>SinglePixelPackedSampleModel</code> with the specified
     * data type, width, height, and band masks.  The number of bands
     * is inferred from <code>bandMasks.length</code>.
     *
     * <p> The upper left corner of the <code>WritableRaster</code> is
     * given by the <code>location</code> argument.  If
     * <code>location</code> is <code>null</code>, (0, 0) will be
     * used.  The <code>dataType</code> parameter should be one of the
     * enumerated values defined in the <code>DataBuffer</code> class.
     *
     * @param dataType The data type of the <code>WritableRaster</code>,
     *        one of <code>DataBuffer.TYPE_BYTE</code>,
     *        <code>TYPE_USHORT</code> or <code>TYPE_INT</code>.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException is thrown if
     *         the <code>dataType</code> is not of either TYPE_BYTE
     *         or TYPE_USHORT or TYPE_INT.
     */
    public static WritableRaster createPackedRaster(int dataType,
                                                    int width, int height,
                                                    int bandMasks[],
                                                    Point location) {
        return Raster.createPackedRaster(dataType,
                                         width, height, bandMasks, location);
    }

    /**
     * Creates a <code>WritableRaster</code> based on a packed
     * <code>SampleModel</code> with the specified data type, width,
     * height, number of bands, and bits per band.  If the number of
     * bands is one, the <code>SampleModel</code> will be a
     * <code>MultiPixelPackedSampleModel</code>.
     *
     * <p> If the number of bands is more than one, the
     * <code>SampleModel</code> will be a
     * <code>SinglePixelPackedSampleModel</code>, with each band
     * having <code>bitsPerBand</code> bits.  In either case, the
     * requirements on <code>dataType</code> and
     * <code>bitsPerBand</code> imposed by the corresponding
     * <code>SampleModel</code> must be met.
     *
     * <p> The upper left corner of the <code>WritableRaster</code> is
     * given by the <code>location</code> argument.  If
     * <code>location</code> is <code>null</code>, (0, 0) will be
     * used.  The <code>dataType</code> parameter should be one of the
     * enumerated values defined in the <code>DataBuffer</code> class.
     *
     * @param dataType The data type of the <code>WritableRaster</code>,
     *        one of <code>DataBuffer.TYPE_BYTE</code>,
     *        <code>TYPE_USHORT</code> or <code>TYPE_INT</code>.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param numBands The desired number of bands.
     * @param bitsPerBand The number of bits per band.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException is thrown if
     *         the <code>dataType</code> is not of either TYPE_BYTE
     *         or TYPE_USHORT or TYPE_INT.
     * @throws IllegalArgumentException is thrown if bitsPerBand
     *         is negative or zero.
     */
    public static WritableRaster createPackedRaster(int dataType,
                                                    int width, int height,
                                                    int numBands,
                                                    int bitsPerBand,
                                                    Point location) {
        if (bitsPerBand <= 0) {
            throw new IllegalArgumentException(Messages.getString("bitsPerBands_must_be_greater_than_0_"));
        }

        return Raster.createPackedRaster(dataType, width, height, numBands,
                                         bitsPerBand, location);
    }

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>PixelInterleavedSampleModel</code> with the specified
     * <code>DataBuffer</code>, width, height, scanline stride, pixel
     * stride, and band offsets.  The number of bands is inferred from
     * <code>bandOffsets.length</code>.  The upper left corner of the
     * <code>WritableRaster</code> is given by the
     * <code>location</code> argument.  If <code>location</code> is
     * <code>null</code>, (0, 0) will be used.
     *
     * @param dataBuffer The <code>DataBuffer</code> to be used.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param scanlineStride The desired scanline stride.
     * @param pixelStride The desired pixel stride.
     * @param bandOffsets An array of <code>int</code>s indicating the
     *        relative offsets of the bands within a pixel.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException if <code>bandOffsets</code> is
     *         <code>null</code>, if <code>pixelStride*width</code> is
     *         <code>></code> <code>scanlineStride</code>,
     *         if <code>dataType</code>of the DataBuffer is not one
     *         the enumerated dataType value of java.awt.image.DataBuffer.
     */
    public static WritableRaster createInterleavedRaster(DataBuffer dataBuffer,
                                                         int width, int height,
                                                         int scanlineStride,
                                                         int pixelStride,
                                                         int bandOffsets[],
                                                         Point location) {

        if (bandOffsets == null) {
            throw new IllegalArgumentException(Messages.getString("Band_offsets_array_is_null_"));
        }
        if (location == null) {
            location = new Point(0, 0);
        }
        int dataType = dataBuffer.getDataType();

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
        case DataBuffer.TYPE_USHORT:
            PixelInterleavedSampleModel csm =
                new PixelInterleavedSampleModel(dataType, width, height,
                                                pixelStride,
                                                scanlineStride,
                                                bandOffsets);
            return Raster.createWritableRaster(csm,dataBuffer,location);

        case DataBuffer.TYPE_INT:
        case DataBuffer.TYPE_SHORT:
        case DataBuffer.TYPE_FLOAT:
        case DataBuffer.TYPE_DOUBLE:
            int minBandOff=bandOffsets[0];
            int maxBandOff=bandOffsets[0];
            for (int i=1; i<bandOffsets.length; i++) {
                minBandOff = Math.min(minBandOff,bandOffsets[i]);
                maxBandOff = Math.max(maxBandOff,bandOffsets[i]);
            }
            maxBandOff -= minBandOff;
            if (maxBandOff > scanlineStride) {
                throw new IllegalArgumentException(
                                          Messages.getString("Offsets_between_bands_must_be_less_than_the_scanline_stride_"));

            }
            if (pixelStride*width > scanlineStride) {
                throw new IllegalArgumentException(
                                          Messages.getString("Pixel_stride_times_width_must_be_less_than_the_scanline_stride_"));
            }
            if (pixelStride < maxBandOff) {
                throw new IllegalArgumentException(
                                          Messages.getString("Pixel_stride_must_be_greater_than_or_equal_to_the_offset_between_bands_"));
            }

            SampleModel sm =
                 new ComponentSampleModelJAI(dataType,width,height,
                                             pixelStride,
                                             scanlineStride,
                                             bandOffsets);
            return Raster.createWritableRaster(sm, dataBuffer, location);

        default:
            throw new IllegalArgumentException(Messages.getString("Unsupported_data_type_"));
        }
    }

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>ComponentSampleModel</code> with the specified
     * <code>DataBuffer</code>, width, height, scanline stride, bank
     * indices, and band offsets.  The number of bands is inferred
     * from <code>bankIndices.length</code> and
     * <code>bandOffsets.length</code>, which must be the same.  The
     * upper left corner of the <code>WritableRaster</code> is given
     * by the <code>location</code> argument.  If
     * <code>location</code> is <code>null</code>, (0, 0) will be
     * used.
     *
     * <p> Note that the <code>Raster</code>'s
     * <code>SampleModel</code> will be of type
     * <code>ComponentSampleModel</code>, not
     * <code>BandedSampleModel</code> as might be expected.
     *
     * @param dataBuffer The <code>DataBuffer</code> to be used.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param scanlineStride The desired scanline stride.
     * @param bankIndices An array of <code>int</code>s indicating the
     *        bank index for each band.
     * @param bandOffsets An array of <code>int</code>s indicating the
     *        relative offsets of the bands within a pixel.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException if <code>bankIndices</code> is
     *         <code>null</code>, if <code>bandOffsets</code> is
     *         <code>null</code>, if <code>bandOffsets.length</code>
     *         is <code>!=</code> <code>bankIndices.length</code>,
     *         if <code>dataType</code> is not one of the enumerated
     *         datatypes of java.awt.image.DataBuffer.
     */
    public static WritableRaster createBandedRaster(DataBuffer dataBuffer,
                                                    int width, int height,
                                                    int scanlineStride,
                                                    int bankIndices[],
                                                    int bandOffsets[],
                                                    Point location) {
        if (location == null) {
           location = new Point(0,0);
        }
        int dataType = dataBuffer.getDataType();

        if (bankIndices == null) {
            throw new IllegalArgumentException(Messages.getString("Bank_indices_array_is_null_"));
        }
        if (bandOffsets == null) {
            throw new IllegalArgumentException(Messages.getString("Band_offsets_array_is_null_"));
        }

        int bands = bankIndices.length;
        if (bandOffsets.length != bands) {
            throw new IllegalArgumentException(Messages.getString("bankIndices_length____bandOffsets_length"));
        }

        SampleModel bsm =
            new ComponentSampleModelJAI(dataType, width, height,
                                        1, scanlineStride,
                                        bankIndices, bandOffsets);

        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
        case DataBuffer.TYPE_USHORT:
        case DataBuffer.TYPE_INT:
        case DataBuffer.TYPE_SHORT:
        case DataBuffer.TYPE_FLOAT:
        case DataBuffer.TYPE_DOUBLE:
           return Raster.createWritableRaster(bsm, dataBuffer, location);

        default:
            throw new IllegalArgumentException(Messages.getString("Unsupported_data_type_"));
        }
    }

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>SinglePixelPackedSampleModel</code> with the specified
     * <code>DataBuffer</code>, width, height, scanline stride, and
     * band masks.  The number of bands is inferred from
     * <code>bandMasks.length</code>.  The upper left corner of the
     * <code>WritableRaster</code> is given by the
     * <code>location</code> argument.  If <code>location</code> is
     * <code>null</code>, (0, 0) will be used.
     *
     * @param dataBuffer The <code>DataBuffer</code> to be used.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param scanlineStride The desired scanline stride.
     * @param bandMasks An array of <code>int</code>s indicating the
     *        bitmasks for each band within a pixel.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException is thrown if
     *         the <code>dataType</code> is not of either TYPE_BYTE
     *         or TYPE_USHORT or TYPE_INT.
     */
    public static WritableRaster createPackedRaster(DataBuffer dataBuffer,
                                                    int width,
                                                    int height,
                                                    int scanlineStride,
                                                    int bandMasks[],
                                                    Point location) {
        return Raster.createPackedRaster(dataBuffer, width, height,
                                         scanlineStride,
                                         bandMasks,
                                         location);
    }

    /**
     * Creates a <code>WritableRaster</code> based on a
     * <code>MultiPixelPackedSampleModel</code> with the specified
     * <code>DataBuffer</code>, width, height, and bits per pixel.
     * The upper left corner of the <code>WritableRaster</code> is
     * given by the <code>location</code> argument.  If
     * <code>location</code> is <code>null</code>, (0, 0) will be
     * used.
     *
     * @param dataBuffer The <code>DataBuffer</code> to be used.
     * @param width The desired width of the <code>WritableRaster</code>.
     * @param height The desired height of the <code>WritableRaster</code>.
     * @param bitsPerPixel The desired pixel depth.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     *
     * @throws IllegalArgumentException is thrown if
     *         the <code>dataType</code> of the <code>dataBuffer</code>
     *         is not of either TYPE_BYTE or TYPE_USHORT or TYPE_INT.
     */
    public static WritableRaster createPackedRaster(DataBuffer dataBuffer,
                                                    int width,
                                                    int height,
                                                    int bitsPerPixel,
                                                    Point location) {
        return Raster.createPackedRaster(dataBuffer, width,  height,
                                         bitsPerPixel, location);
    }

    /**
     *  Creates a <code>WritableRaster</code> with the specified
     *  <code>SampleModel</code> and <code>DataBuffer</code>.  The
     *  upper left corner of the <code>WritableRaster</code> is given
     *  by the <code>location</code> argument.  If
     *  <code>location</code> is <code>null</code>, (0, 0) will be
     *  used.
     *
     * @param sampleModel The <code>SampleModel</code> to be used.
     * @param dataBuffer The <code>DataBuffer</code> to be used.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     */
    public static Raster createRaster(SampleModel sampleModel,
                                      DataBuffer dataBuffer,
                                      Point location) {
        return Raster.createRaster(sampleModel, dataBuffer, location);
    }

    /**
     *  Creates a <code>WritableRaster</code> with the specified
     *  <code>SampleModel</code>.  The upper left corner of the
     *  <code>WritableRaster</code> is given by the
     *  <code>location</code> argument.  If <code>location</code> is
     *  <code>null</code>, (0, 0) will be used.
     *
     * @param sampleModel The <code>SampleModel</code> to use.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     */
    public static WritableRaster createWritableRaster(SampleModel sampleModel,
                                                      Point location) {
        if (location == null) {
           location = new Point(0,0);
        }

        return createWritableRaster(sampleModel,
                                    sampleModel.createDataBuffer(),
                                    location);
    }

    /**
     *  Creates a <code>WritableRaster</code> with the specified
     *  <code>SampleModel</code> and <code>DataBuffer</code>.  The
     *  upper left corner of the <code>WritableRaster</code> is given
     *  by the <code>location</code> argument.  If
     *  <code>location</code> is <code>null</code>, (0, 0) will be
     *  used.
     *
     * @param sampleModel The <code>SampleModel</code> to be used.
     * @param dataBuffer The <code>DataBuffer</code> to be used.
     * @param location A <code>Point</code> indicating the starting
     *        coordinates of the <code>WritableRaster</code>.
     */
    public static WritableRaster createWritableRaster(SampleModel sampleModel,
                                                      DataBuffer dataBuffer,
                                                      Point location) {
        return Raster.createWritableRaster(sampleModel, dataBuffer, location);
    }

    /**
     * Returns a new WritableRaster which shares all or part of the
     * supplied WritableRaster's DataBuffer.  The new WritableRaster will
     * possess a reference to the supplied WritableRaster, accessible
     * through its getParent() and getWritableParent() methods.
     *
     * <p> This method provides a workaround for a bug in the
     * implementation of WritableRaster.createWritableChild in
     * the initial relase of the Java2 platform.
     *
     * <p> The <code>parentX</code>, <code>parentY</code>,
     * <code>width</code> and <code>height</code> parameters form a
     * Rectangle in this WritableRaster's coordinate space, indicating
     * the area of pixels to be shared.  An error will be thrown if
     * this Rectangle is not contained with the bounds of the supplied
     * WritableRaster.
     *
     * <p> The new WritableRaster may additionally be translated to a
     * different coordinate system for the plane than that used by the supplied
     * WritableRaster.  The childMinX and childMinY parameters give
     * the new (x, y) coordinate of the upper-left pixel of the
     * returned WritableRaster; the coordinate (childMinX, childMinY)
     * in the new WritableRaster will map to the same pixel as the
     * coordinate (parentX, parentY) in the supplied WritableRaster.
     *
     * <p> The new WritableRaster may be defined to contain only a
     * subset of the bands of the supplied WritableRaster, possibly
     * reordered, by means of the bandList parameter.  If bandList is
     * null, it is taken to include all of the bands of the supplied
     * WritableRaster in their current order.
     *
     * <p> To create a new WritableRaster that contains a subregion of
     * the supplied WritableRaster, but shares its coordinate system
     * and bands, this method should be called with childMinX equal to
     * parentX, childMinY equal to parentY, and bandList equal to
     * null.
     *
     * @param raster     The parent WritableRaster.
     * @param parentX    X coordinate of the upper left corner of the shared
     *        rectangle in this WritableRaster's coordinates.
     * @param parentY    Y coordinate of the upper left corner of the shared
     *        rectangle in this WritableRaster's coordinates.
     * @param width      Width of the shared rectangle starting at
     *        (<code>parentX</code>, <code>parentY</code>).
     * @param height     Height of the shared rectangle starting at
     *        (<code>parentX</code>, <code>parentY</code>).
     * @param childMinX  X coordinate of the upper left corner of
     *        the returned WritableRaster.
     * @param childMinY  Y coordinate of the upper left corner of
     *        the returned WritableRaster.
     * @param bandList   Array of band indices, or null to use all bands.
     *
     * @throws RasterFormatException if the subregion is outside of the
     *         raster bounds.
     */
    public static WritableRaster createWritableChild(WritableRaster raster,
                                                     int parentX,
                                                     int parentY,
                                                     int width,
                                                     int height,
                                                     int childMinX,
                                                     int childMinY,
                                                     int bandList[]) {
        if (parentX < raster.getMinX()) {
            throw new RasterFormatException(Messages.getString("parentX_lies_outside_raster_"));
        }
        if (parentY < raster.getMinY()) {
            throw new
		RasterFormatException(Messages.getString("parentY_lies_outside_raster_"));
        }
        if (parentX + width > raster.getWidth() + raster.getMinX()) {
            throw new
		RasterFormatException(Messages.getString("_parentX___width__is_outside_raster_"));
        }
        if (parentY + height > raster.getHeight() + raster.getMinY()) {
            throw new
		RasterFormatException(Messages.getString("_parentY___height__is_outside_raster_"));
        }

        SampleModel sampleModel = raster.getSampleModel();
        DataBuffer dataBuffer = raster.getDataBuffer();
        int sampleModelTranslateX = raster.getSampleModelTranslateX();
        int sampleModelTranslateY = raster.getSampleModelTranslateY();

        SampleModel sm;

        if (bandList != null) {
            sm = sampleModel.createCompatibleSampleModel(
                                                      sampleModel.getWidth(),
                                                      sampleModel.getHeight());
            sm = sm.createSubsetSampleModel(bandList);
        }
        else {
            sm = sampleModel;
        }

        int deltaX = childMinX - parentX;
        int deltaY = childMinY - parentY;

        return new WritableRasterJAI(sm,
                                     dataBuffer,
                                     new Rectangle(childMinX, childMinY,
                                                   width, height),
                                     new Point(sampleModelTranslateX + deltaX,
                                               sampleModelTranslateY + deltaY),
                                     raster);
    }


}
