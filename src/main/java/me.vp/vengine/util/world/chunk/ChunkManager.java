package me.vp.vengine.util.world.chunk;

import me.vp.vengine.Vengine;
import me.vp.vengine.util.Vector;
import me.vp.vengine.util.world.terrain.Terrain;
import me.vp.vengine.util.world.voxel.Voxel;

import java.util.ArrayList;
import java.util.HashMap;

/*
 *
 * @Author Vp (https://github.com/herravp)
 *
 */
public class ChunkManager {
    public Terrain terrain;
    public int radius;

    public ArrayList<Chunk> loaded;
    public HashMap<Vector, Chunk> map;

    public ChunkManager(Terrain t, int r) {
        this.terrain = t;
        this.radius = r;

        this.loaded = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public Voxel getVoxel(Vector p) {
        Chunk c = this.map.get(this.getChunkVector(p));

        if (c == null)
            return null;
        try {
            return c.getVoxel(p);
        } catch (Exception e) {
            return null;
        }
    }

    public void addVoxel(Voxel v) throws Exception {
        Chunk c = this.map.get(this.getChunkVector(v.position));

        if (c == null)
            throw new Exception("Error: Cannot add voxel to position, chunk does not exist: " + v.position);
        c.addVoxel(v);
        this.updateVoxelNeighbors(v, c);
    }

    public void removeVoxel(Voxel v) throws Exception {
        Chunk c = this.map.get(this.getChunkVector(v.position));

        if (c == null)
            throw new Exception("Error: Cannot remove voxel from position, chunk does not exist: " + v.position);
        c.removeVoxel(v);
    }

    public void updateVoxelNeighbors(Voxel v, Chunk c) {
        for (int i = 0; i < Voxel.directions.length; i += 2) {
            if (v.neighbors[i] == null) {
                Voxel u;
                Vector r = v.position.add(Voxel.directions[i]);
                try {
                    u = c.getVoxel(r);
                } catch (Exception e) {
                    u = this.getVoxel(r);
                }

                if (u != null) {
                    v.neighbors[i] = u;
                    u.neighbors[i + 1] = v;
                }

                Voxel w;
                Vector t = v.position.add(Voxel.directions[i + 1]);
                try {
                    w = c.getVoxel(t);
                } catch (Exception e) {
                    w = this.getVoxel(t);
                }

                if (w != null) {
                    v.neighbors[i + 1] = w;
                    w.neighbors[i] = v;
                }
            }
        }
    }

    public Vector getChunkVector(Vector p) {
        return new Vector(
                Chunk.size * Math.floor(p.x / Chunk.size),
                Chunk.size * Math.floor(p.y / Chunk.size),
                Chunk.size * Math.floor(p.z / Chunk.size)
        );
    }

    public void loadChunks(Vector p) {
        Vector pc = this.getChunkVector(p);

        for (int x = -this.radius; x <= this.radius; x++) {
            for (int y = -this.radius; y <= this.radius; y++) {
                // Use modified manhatten distance rendering strategy
                if (Math.abs(x) + Math.abs(y) - 1 <= this.radius) {

                    for (int z = 0; z < this.terrain.maxZ; z++) {
                        Vector q = new Vector(
                                pc.x + (double) x * Chunk.size,
                                pc.y + (double) y * Chunk.size,
                                (double) z * Chunk.size
                        );

                        Chunk c = this.map.get(q);
                        if (c == null) {
                            c = this.terrain.createChunk(q);
                            for (Voxel v : c.getVoxelList()) {
                                this.updateVoxelNeighbors(v, c);
                            }
                            this.map.put(q, c);
                        }

                        if (!this.loaded.contains(c))
                            this.loaded.add(c);
                    }
                }

            }
        }

        this.loaded.removeIf(
                c -> (
                        Math.abs((pc.x / Chunk.size) - (c.position.x / Chunk.size)) - 1 +
                                Math.abs((pc.y / Chunk.size) - (c.position.y / Chunk.size)) > this.radius ||
                                Math.abs((pc.x / Chunk.size) - (c.position.x / Chunk.size)) > this.radius ||
                                Math.abs((pc.y / Chunk.size) - (c.position.y / Chunk.size)) > this.radius
                )
        );
    }

}