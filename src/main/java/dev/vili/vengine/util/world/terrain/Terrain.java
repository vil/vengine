/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine.util.world.terrain;

import dev.vili.vengine.util.Vector;
import dev.vili.vengine.util.world.chunk.Chunk;

public abstract class Terrain {
    public long seed;
    public int maxZ;

    public Terrain(long s, int m) {
        this.seed = s;
        this.maxZ = m;
    }

    public abstract Chunk createChunk(Vector p);
}