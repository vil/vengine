package me.vp.vengine.util.world.voxel.voxels;

import me.vp.vengine.util.Vector;
import me.vp.vengine.util.world.voxel.Voxel;

import java.awt.*;

/*
 *
 * @Author Vp (https://github.com/herravp)
 *
 */
public class Grass extends Voxel {
    public Grass(Vector vector, boolean solid) {
        super(vector, new Color(0x428B1E), solid);
    }

}