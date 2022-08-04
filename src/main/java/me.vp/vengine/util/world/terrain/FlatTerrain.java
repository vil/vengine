package me.vp.vengine.util.world.terrain;

import me.vp.vengine.Vengine;
import me.vp.vengine.util.Vector;
import me.vp.vengine.util.world.chunk.Chunk;
import me.vp.vengine.util.world.noise.Noise;
import me.vp.vengine.util.world.noise.SimplexNoise;
import me.vp.vengine.util.world.voxel.voxels.Grass;

/*
 *
 * @Author Vp (https://github.com/herravp)
 *
 */
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
