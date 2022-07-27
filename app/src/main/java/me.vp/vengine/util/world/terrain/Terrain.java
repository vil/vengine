package me.vp.vengine.util.world.terrain;

import me.vp.vengine.util.Vector;
import me.vp.vengine.util.world.chunk.Chunk;

/*
 *
 * @Author Vp (https://github.com/herravp)
 *
 */
public abstract class Terrain {
    public long seed;
    public int maxZ;

    public Terrain(long s, int m) {
        this.seed = s;
        this.maxZ = m;
    }

    public abstract Chunk createChunk(Vector p);
}