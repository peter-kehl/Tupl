/*
 *  Copyright (C) 2011-2018 Cojen.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cojen.tupl.schemata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.cojen.tupl.Transaction;

/**
 * 
 *
 * @author Brian S O'Neill
 */
public class PolymorphicType extends Type {
    private static final long HASH_BASE = 1902668584782181472L;

    /* FIXME: Should be: Type nameType, byte[] name, Type[] allowedTypes
    private final long mIdentifier;
    private final Type mTargetType;
    */

    // FIXME: User defines the tag to type mappings. Tag is a simple byte[] constant which must
    // not match a prefix of any other tag. This eliminates decoding ambiguity.

    PolymorphicType(Schemata schemata, long typeId, short flags) {
        super(schemata, typeId, flags);
    }

    /* FIXME: remove
    public long getIdentifier() {
        return mIdentifier;
    }

    public Type getTargetType() {
        return mTargetType;
    }
    */

    @Override
    public boolean isFixedLength() {
        // FIXME
        throw null;
    }

    @Override
    public int printData(StringBuilder b, byte[] data, int offset) {
        // FIXME
        throw null;
    }

    @Override
    public int printKey(StringBuilder b, byte[] data, int offset) {
        // FIXME
        throw null;
    }

    @Override
    public int parseData(ByteArrayOutputStream out, String str, int offset) {
        // FIXME
        throw null;
    }

    @Override
    public int parseKey(ByteArrayOutputStream out, String str, int offset) {
        // FIXME
        throw null;
    }

    @Override
    void appendTo(StringBuilder b) {
        b.append("PolymorphicType");
        b.append(" {");
        appendCommon(b);
        b.append(", ");
        // FIXME
        b.append('}');
    }

    static PolymorphicType decode(Transaction txn, Schemata schemata, long typeId, byte[] value)
        throws IOException
    {
        if (value[0] != TYPE_PREFIX_POLYMORPH) {
            throw new IllegalArgumentException();
        }
        // FIXME
        throw null;
    }

    @Override
    long computeHash() {
        // FIXME
        throw null;
    }

    @Override
    byte[] encodeValue() {
        // FIXME: polymorph prefix, uint_16 flags, MapType<uvarint_64, uvarint_64>
        throw null;
    }

    @Override
    @SuppressWarnings("unchecked")
    <T extends Type> T equivalent(T type) {
        // FIXME
        throw null;
    }
}
