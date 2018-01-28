/*
 *  Copyright (C) 2018 Cojen.org
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

import java.util.ArrayList;
import java.util.Collections;

import java.util.concurrent.Executor;

/**
 * Parallel tree merging utility. All entries from the source trees (assumed to be temporary
 * trees) are merged into a new target tree, and all sources are deleted.
 *
 * @author Brian S O'Neill
 */
/*P*/
abstract class TreeMerger extends TreeSeparator {
    private final ArrayList<Target> mTargets;

    /**
     * @param executor used for parallel separation; pass null to use only the starting thread
     * @param workerCount maximum parallelism; must be at least 1
     */
    TreeMerger(LocalDatabase db, Tree[] sources, Executor executor, int workerCount) {
        super(db, sources, executor, workerCount);
        mTargets = new ArrayList<>();
    }

    @Override
    protected void separated(Tree tree, byte[] lowKey, byte[] highKey) {
        Target target = new Target(tree, lowKey);
        synchronized (mTargets) {
            mTargets.add(target);
        }
    }

    @Override
    protected void finished(Throwable exception) {
        final ArrayList<Target> targets = mTargets;

        synchronized (targets) {
            if (!targets.isEmpty()) {
                Collections.sort(targets);

                Tree merged = targets.get(0).mTree;

                int i = 1;
                final int size = targets.size();
                try {
                    for (; i<size; i++) {
                        merged = Tree.graftTempTree(merged, targets.get(i).mTree);
                    }
                } catch (Throwable e) {
                    failed(e);
                }

                merged(merged);

                // Pass along targets that didn't get merged.
                for (; i<size; i++) {
                    remainder(targets.get(i).mTree);
                }
            }

            for (Tree source : mSources) {
                if (isEmpty(source)) {
                    try {
                        mDatabase.quickDeleteTemporaryTree(source);
                        continue;
                    } catch (Throwable e) {
                        failed(e);
                    }
                }

                remainder(source);
            }
        }

        remainder(null);
    }

    /**
     * Receives the target tree; called at most once.
     */
    protected abstract void merged(Tree tree);

    /**
     * Receives any remaining source trees when merger is stopped. Is null when all finished.
     */
    protected abstract void remainder(Tree tree);

    private static boolean isEmpty(Tree tree) {
        Node root = tree.mRoot;
        root.acquireShared();
        boolean empty = root.isLeaf() && !root.hasKeys();
        root.releaseShared();

        if (!empty) {
            // Double check with a cursor. Tree might be composed of many empty leaf nodes.
            TreeCursor c = tree.newCursor(Transaction.BOGUS);
            try {
                c.mKeyOnly = true;
                c.first();
                empty = c.key() == null;
            } catch (Throwable e) {
                // Ignore and keep using the tree for now.
            } finally {
                c.reset();
            }
        }

        return empty;
    }

    private static class Target implements Comparable<Target> {
        final Tree mTree;
        final byte[] mLowKey;

        Target(Tree tree, byte[] lowKey) {
            mTree = tree;
            mLowKey = lowKey;
        }

        @Override
        public int compareTo(Target other) {
            byte[] key1 = mLowKey;
            byte[] key2 = other.mLowKey;

            if (key1 == null) {
                return key2 == null ? 0 : -1;
            } else if (key2 == null) {
                return 1;
            }

            return Utils.compareUnsigned(key1, key2);
        }
    }
}
