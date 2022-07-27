package me.vp.vengine.util.world.terrain;

import me.vp.vengine.util.Vector;
import me.vp.vengine.util.world.chunk.Chunk;
import me.vp.vengine.util.world.noise.Noise;
import me.vp.vengine.util.world.noise.SimplexNoise;
import me.vp.vengine.util.world.voxel.voxels.Grass;

import java.awt.*;

public class NormalTerrain extends Terrain {
    public Noise noise;

    public NormalTerrain(long s) {
        super(s, 32);
        this.noise = new SimplexNoise(s);
    }

    private int getZ(double x, double y) {
        return Math.max(0, 24 + (int) ((
                this.noise.noise(x * 0.005, y * 0.005, 0) +
                        this.noise.noise(x * 0.004, y * 0.0004, 0) +
                        this.noise.noise(x * 0.01, y * 0.01, 0)
        ) / 3 * 16));
    }

    @Override
    public Chunk createChunk(Vector p) {
        Chunk chunk = new Chunk(p);

        for (double x = p.x; x < p.x + Chunk.size; x++) {
            for (double y = p.y; y < p.y + Chunk.size; y++) {
                for (double z = p.z; z < p.z + Chunk.size && z <= this.getZ(x, y); z++) {
                    try {
                        chunk.addVoxel(new Grass(new Vector(x, y, z), true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return chunk;
    }

}