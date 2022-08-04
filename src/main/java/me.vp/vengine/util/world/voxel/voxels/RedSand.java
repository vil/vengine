package me.vp.vengine.util.world.voxel.voxels;

import me.vp.vengine.util.Vector;
import me.vp.vengine.util.world.voxel.Voxel;

import java.awt.*;

/*
 *
 * @Author Vp (https://github.com/herravp)
 */
public class RedSand extends Voxel {
    public RedSand(Vector vector, boolean solid) {
        super(vector, new Color(0xDDFF3684, true), solid);
    }
}

