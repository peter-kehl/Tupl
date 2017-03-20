/*
 *  Copyright 2017 Cojen.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.cojen.tupl;

import java.io.IOException;

/**
 * 
 *
 * @author Brian S O'Neill
 */
final class KeyOnlyView implements View {
    private final View mSource;

    KeyOnlyView(View source) {
        mSource = source;
    }

    static void valueCheck(byte[] value) throws ViewConstraintException {
        if (value != null) {
            throw new ViewConstraintException("Cannot store non-null value into key-only view");
        }
    }

    static byte[] valueScrub(byte[] value) {
        // Don't trust the source view to always return null or NOT_LOADED.
        return value == null ? null : Cursor.NOT_LOADED;
    }

    @Override
    public Ordering getOrdering() {
        return mSource.getOrdering();
    }

    @Override
    public Cursor newCursor(Transaction txn) {
        return new KeyOnlyCursor(mSource.newCursor(txn));
    }

    @Override
    public Transaction newTransaction(DurabilityMode durabilityMode) {
        return mSource.newTransaction(durabilityMode);
    }

    @Override
    public long count(byte[] lowKey, byte[] highKey) throws IOException {
        return mSource.count(lowKey, highKey);
    }

    @Override
    public byte[] load(Transaction txn, byte[] key) throws IOException {
        Cursor c = mSource.newCursor(txn);
        try {
            c.autoload(false);
            c.find(key);
            return valueScrub(c.value());
        } finally {
            c.reset();
        }
    }

    @Override
    public byte[] exchange(Transaction txn, byte[] key, byte[] value) throws IOException {
        valueCheck(value);

        Cursor c = mSource.newCursor(txn);
        try {
            c.autoload(false);
            c.find(key);
            byte[] old = valueScrub(c.value());
            c.store(value);
            return old;
        } finally {
            c.reset();
        }
    }

    @Override
    public void store(Transaction txn, byte[] key, byte[] value) throws IOException {
        valueCheck(value);
        mSource.store(txn, key, null);
    }

    @Override
    public boolean insert(Transaction txn, byte[] key, byte[] value) throws IOException {
        valueCheck(value);
        return mSource.insert(txn, key, null);
    }

    @Override
    public boolean replace(Transaction txn, byte[] key, byte[] value) throws IOException {
        valueCheck(value);
        return mSource.replace(txn, key, null);
    }

    @Override
    public boolean update(Transaction txn, byte[] key, byte[] oldValue, byte[] newValue)
        throws IOException
    {
        valueCheck(newValue);

        if (oldValue == null) {
            return mSource.update(txn, key, null, null);
        } else {
            Cursor c = mSource.newCursor(txn);
            try {
                c.autoload(false);
                c.find(key);
                if (valueScrub(c.value()) != oldValue) { // don't compare array contents
                    return false;
                }
                c.store(null);
                return true;
            } finally {
                c.reset();
            }
        }
    }

    @Override
    public boolean delete(Transaction txn, byte[] key) throws IOException {
        return mSource.delete(txn, key);
    }

    @Override
    public LockResult tryLockShared(Transaction txn, byte[] key, long nanosTimeout)
        throws DeadlockException, ViewConstraintException
    {
        return mSource.tryLockShared(txn, key, nanosTimeout);
    }

    @Override
    public LockResult lockShared(Transaction txn, byte[] key)
        throws LockFailureException, ViewConstraintException
    {
        return mSource.lockShared(txn, key);
    }

    @Override
    public LockResult tryLockUpgradable(Transaction txn, byte[] key, long nanosTimeout)
        throws DeadlockException, ViewConstraintException
    {
        return mSource.tryLockUpgradable(txn, key, nanosTimeout);
    }

    @Override
    public LockResult lockUpgradable(Transaction txn, byte[] key)
        throws LockFailureException, ViewConstraintException
    {
        return mSource.lockUpgradable(txn, key);
    }

    @Override
    public LockResult tryLockExclusive(Transaction txn, byte[] key, long nanosTimeout)
        throws DeadlockException, ViewConstraintException
    {
        return mSource.tryLockExclusive(txn, key, nanosTimeout);
    }

    @Override
    public LockResult lockExclusive(Transaction txn, byte[] key)
        throws LockFailureException, ViewConstraintException
    {
        return mSource.lockExclusive(txn, key);
    }

    @Override
    public LockResult lockCheck(Transaction txn, byte[] key) throws ViewConstraintException {
        return mSource.lockCheck(txn, key);
    }

    @Override
    public View viewKeys() {
        return this;
    }

    @Override
    public View viewReverse() {
        return new KeyOnlyView(mSource.viewReverse());
    }

    @Override
    public View viewUnmodifiable() {
        View source = mSource.viewUnmodifiable();
        return source == mSource ? this : UnmodifiableView.apply(this);
    }

    @Override
    public boolean isUnmodifiable() {
        return mSource.isUnmodifiable();
    }
}