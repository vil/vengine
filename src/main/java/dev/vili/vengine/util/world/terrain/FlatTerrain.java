/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine.util.world.terrain;

import dev.vili.vengine.util.world.noise.Noise;
import dev.vili.vengine.util.world.voxel.voxels.Grass;
import dev.vili.vengine.Vengine;
import dev.vili.vengine.util.Vector;
import dev.vili.vengine.util.world.chunk.Chunk;
import dev.vili.vengine.util.world.noise.SimplexNoise;

public class FlatTerrain extends Terrain {
    public Noise noise;

    public FlatTerrain(long s) {
        super(s, 1);
        this.noise = new SimplexNoise(s);
    }

    @Override
    public Chunk createChunk(Vector p) {
        Chunk chunk = new Chunk(p);

        for (double x = p.x; x < p.x + Chunk.size; x++) {
            for (double y = p.y; y < p.y + Chunk.size; y++) {
                for (double z = p.z; z < p.z + Chunk.size; z++) {
                    try {
                        chunk.addVoxel(new Grass(new Vector(x, y, z), true));
                    } catch (Exception e) {
                        Vengine.LOGGER.warning(e.getMessage());
                    }
                }
            }
        }

        return chunk;
    }
}
