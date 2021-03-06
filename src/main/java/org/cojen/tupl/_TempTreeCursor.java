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

import java.io.IOException;

/**
 * _TreeCursor which prohibits redo durabilty.
 *
 * @author Generated by PageAccessTransformer from TempTreeCursor.java
 */
/*P*/
final class _TempTreeCursor extends _TreeCursor {
    _TempTreeCursor(_TempTree tree, Transaction txn) {
        super(tree, txn);
    }

    _TempTreeCursor(_TempTree tree) {
        super(tree);
    }

    @Override
    protected int storeMode() {
        // Never redo.
        return 2;
    }
}
