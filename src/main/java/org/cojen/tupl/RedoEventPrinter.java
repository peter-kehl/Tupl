/*
 *  Copyright (C) 2011-2017 Cojen.org
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

package org.cojen.tupl;

import java.nio.charset.StandardCharsets;

/**
 * 
 *
 * @author Brian S O'Neill
 */
class RedoEventPrinter implements RedoVisitor {
    private static final int MAX_VALUE = 1000;

    private final EventListener mListener;
    private final EventType mType;

    RedoEventPrinter(EventListener listener, EventType type) {
        mListener = listener;
        mType = type;
    }

    @Override
    public boolean reset() {
        mListener.notify(mType, "Redo reset");
        return true;
    }

    @Override
    public boolean timestamp(long timestamp) {
        mListener.notify(mType, "Redo %1$s: %2$s", "timestamp", toDateTime(timestamp));
        return true;
    }

    @Override
    public boolean shutdown(long timestamp) {
        mListener.notify(mType, "Redo %1$s: %2$s", "shutdown", toDateTime(timestamp));
        return true;
    }

    @Override
    public boolean close(long timestamp) {
        mListener.notify(mType, "Redo %1$s: %2$s", "close", toDateTime(timestamp));
        return true;
    }

    @Override
    public boolean endFile(long timestamp) {
        mListener.notify(mType, "Redo %1$s: %2$s", "endFile", toDateTime(timestamp));
        return true;
    }

    @Override
    public boolean control(byte[] message) {
        mListener.notify(mType, "Redo %1$s: %2$s", "control", valueStr(message));
        return true;
    }

    @Override
    public boolean store(long indexId, byte[] key, byte[] value) {
        mListener.notify(mType, "Redo %1$s: indexId=%2$d, key=%3$s, value=%4$s",
                         "store", indexId, keyStr(key), valueStr(value));
        return true;
    }

    @Override
    public boolean storeNoLock(long indexId, byte[] key, byte[] value) {
        mListener.notify(mType, "Redo %1$s: indexId=%2$d, key=%3$s, value=%4$s",
                         "storeNoLock", indexId, keyStr(key), valueStr(value));
        return true;
    }

    @Override
    public boolean renameIndex(long txnId, long indexId, byte[] newName) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d, name=%4$s",
                         "renameIndex", txnId, indexId, keyStr(newName));
        return true;
    }

    @Override
    public boolean deleteIndex(long txnId, long indexId) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d",
                         "deleteIndex", txnId, indexId);
        return true;
    }

    @Override
    public boolean txnPrepare(long txnId) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d", "txnPrepare", txnId);
        return true;
    }

    @Override
    public boolean txnEnter(long txnId) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d", "txnEnter", txnId);
        return true;
    }

    @Override
    public boolean txnRollback(long txnId) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d", "txnRollback", txnId);
        return true;
    }

    @Override
    public boolean txnRollbackFinal(long txnId) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d", "txnRollbackFinal", txnId);
        return true;
    }

    @Override
    public boolean txnCommit(long txnId) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d", "txnCommit", txnId);
        return true;
    }

    @Override
    public boolean txnCommitFinal(long txnId) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d", "txnCommitFinal", txnId);
        return true;
    }

    @Override
    public boolean txnEnterStore(long txnId, long indexId, byte[] key, byte[] value) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d, key=%4$s, value=%5$s",
                         "txnEnterStore", txnId, indexId, keyStr(key), valueStr(value));
        return true;
    }

    @Override
    public boolean txnStore(long txnId, long indexId, byte[] key, byte[] value) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d, key=%4$s, value=%5$s",
                         "txnStore", txnId, indexId, keyStr(key), valueStr(value));
        return true;
    }

    @Override
    public boolean txnStoreCommit(long txnId, long indexId, byte[] key, byte[] value) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d, key=%4$s, value=%5$s",
                         "txnStoreCommit", txnId, indexId, keyStr(key), valueStr(value));
        return true;
    }

    @Override
    public boolean txnStoreCommitFinal(long txnId, long indexId, byte[] key, byte[] value) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d, key=%4$s, value=%5$s",
                         "txnStoreCommitFinal", txnId, indexId, keyStr(key), valueStr(value));
        return true;
    }

    @Override
    public boolean cursorRegister(long cursorId, long indexId) {
        mListener.notify(mType, "Redo %1$s: cursorId=%2$d, indexId=%3$d",
                         "txnCursorRegister", cursorId, indexId);
        return true;
    }

    @Override
    public boolean cursorUnregister(long cursorId) {
        mListener.notify(mType, "Redo %1$s: cursorId=%2$d", "cursorUnregister", cursorId);
        return true;
    }

    @Override
    public boolean cursorStore(long cursorId, long txnId, byte[] key, byte[] value) {
        mListener.notify(mType, "Redo %1$s: cursorId=%2$d, txnId=%3$d, key=%4$s, value=%5$s",
                         "cursorStore", cursorId, txnId, keyStr(key), valueStr(value));
        return true;
    }

    @Override
    public boolean cursorFind(long cursorId, long txnId, byte[] key) {
        mListener.notify(mType, "Redo %1$s: cursorId=%2$d, txnId=%3$d, key=%4$s",
                         "cursorFind", cursorId, txnId, keyStr(key));
        return true;
    }

    @Override
    public boolean cursorValueSetLength(long cursorId, long txnId, long length) {
        mListener.notify(mType, "Redo %1$s: cursorId=%2$d, txnId=%3$d, length=%4$d",
                         "cursorValueSetLength", cursorId, txnId, length);
        return true;
    }

    @Override
    public boolean cursorValueWrite(long cursorId, long txnId,
                                    long pos, byte[] buf, int off, int len)
    {
        mListener.notify(mType, "Redo %1$s: cursorId=%2$d, txnId=%3$d, pos=%4$d, value=%5$s",
                         "cursorValueWrite", cursorId, txnId, pos, valueStr(buf, off, len));
        return true;
    }

    @Override
    public boolean cursorValueClear(long cursorId, long txnId, long pos, long length) {
        mListener.notify(mType, "Redo %1$s: cursorId=%2$d, txnId=%3$d, pos=%4$d, length=%5$d",
                         "cursorValueClear", cursorId, txnId, pos, length);
        return true;
    }

    @Override
    public boolean txnLockShared(long txnId, long indexId, byte[] key) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d, key=%4$s",
                         "txnLockShared", txnId, indexId, keyStr(key));
        return true;
    }

    @Override
    public boolean txnLockUpgradable(long txnId, long indexId, byte[] key) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d, key=%4$s",
                         "txnLockUpgradable", txnId, indexId, keyStr(key));
        return true;
    }

    @Override
    public boolean txnLockExclusive(long txnId, long indexId, byte[] key) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, indexId=%3$d, key=%4$s",
                         "txnLockExclusive", txnId, indexId, keyStr(key));
        return true;
    }

    @Override
    public boolean txnCustom(long txnId, byte[] message) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, message=%3$s",
                         "txnCustom", txnId, valueStr(message));
        return true;
    }

    @Override
    public boolean txnCustomLock(long txnId, byte[] message, long indexId, byte[] key) {
        mListener.notify(mType, "Redo %1$s: txnId=%2$d, message=%3$s, key=%4$s",
                         "txnCustomLock", txnId, valueStr(message), keyStr(key));
        return true;
    }

    private static String keyStr(byte[] key) {
        char[] chars = new String(key, StandardCharsets.UTF_8).toCharArray();

        for (int i=0; i<chars.length; i++) {
            if (Character.isISOControl(chars[i])) {
                chars[i] = '\ufffd';
            }
        }

        return new StringBuilder().append("0x").append(Utils.toHex(key)).append(" (")
            .append(chars).append(')').toString();
    }

    private static String valueStr(byte[] value) {
        return valueStr(value, 0, value.length);
    }

    private static String valueStr(byte[] value, int offset, int length) {
        if (value == null) {
            return "null";
        } else if (length <= MAX_VALUE) {
            return "0x" + Utils.toHex(value, offset, length);
        } else {
            return "0x" + Utils.toHex(value, offset, MAX_VALUE) + "...";
        }
    }

    private static String toDateTime(long timestamp) {
        return java.time.Instant.ofEpochMilli(timestamp).toString();
    }
}
