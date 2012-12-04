/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryConversions;
import org.apache.commons.imaging.common.ByteOrder;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.common.RationalNumberUtilities;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.util.Debug;

public class FieldTypeRational extends FieldType {
    public FieldTypeRational(final int type, final String name) {
        super(type, 8, name);
    }

    @Override
    public Object getSimpleValue(final TiffField entry) {
        if (entry.length == 1) {
            return BinaryConversions.toRational(entry.oversizeValue, entry.byteOrder);
        }

        return BinaryConversions.toRationals(getRawBytes(entry), 0, 8*entry.length,
                entry.byteOrder);
    }

    @Override
    public byte[] writeData(final Object o, final ByteOrder byteOrder) throws ImageWriteException {
        if (o instanceof RationalNumber) {
            return BinaryConversions.toBytes((RationalNumber) o, byteOrder);
        } else if (o instanceof RationalNumber[]) {
            return BinaryConversions.toBytes((RationalNumber[]) o,
                    byteOrder);
        } else if (o instanceof Number) {
            final Number number = (Number) o;
            final RationalNumber rationalNumber = RationalNumberUtilities
                    .getRationalNumber(number.doubleValue());
            return BinaryConversions.toBytes(rationalNumber, byteOrder);
        } else if (o instanceof Number[]) {
            final Number numbers[] = (Number[]) o;
            final RationalNumber rationalNumbers[] = new RationalNumber[numbers.length];
            for (int i = 0; i < numbers.length; i++) {
                final Number number = numbers[i];
                rationalNumbers[i] = RationalNumberUtilities
                        .getRationalNumber(number.doubleValue());
            }
            return BinaryConversions.toBytes(rationalNumbers, byteOrder);
        } else if (o instanceof double[]) {
            final double numbers[] = (double[]) o;
            final RationalNumber rationalNumbers[] = new RationalNumber[numbers.length];
            for (int i = 0; i < numbers.length; i++) {
                final double number = numbers[i];
                rationalNumbers[i] = RationalNumberUtilities
                        .getRationalNumber(number);
            }
            return BinaryConversions.toBytes(rationalNumbers, byteOrder);
        } else {
            throw new ImageWriteException("Invalid data: " + o + " ("
                    + Debug.getType(o) + ")");
        }
    }

    public byte[] writeData(final int numerator, final int denominator, final ByteOrder byteOrder)
            throws ImageWriteException {
        return writeData(new int[] { numerator }, new int[] { denominator },
                byteOrder);
    }

    public byte[] writeData(final int numerators[], final int denominators[], final ByteOrder byteOrder)
            throws ImageWriteException {
        return convertIntArrayToRationalArray(numerators, denominators,
                byteOrder);
    }
}
