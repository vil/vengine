/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine.util.world.voxel;

import dev.vili.vengine.util.Polygon;
import dev.vili.vengine.util.Vector;

import java.awt.*;

public abstract class Voxel {
    public static double length = 1;
    public static Vector[] directions = new Vector[]{
            new Vector(0, 0, -1),
            new Vector(0, 0, 1),
            new Vector(0, -1, 0),
            new Vector(0, 1, 0),
            new Vector(-1, 0, 0),
            new Vector(1, 0, 0)
    };

    public boolean isSolid;
    public Vector position;
    public Vector ps;
    public double rotation;
    public Color color;
    public Polygon[] faces;
    public Voxel[] neighbors;

    public Voxel(Vector p, Color c, boolean s) {
        this.position = p;
        this.ps = this.position.scale(Voxel.length);
        this.rotation = 0;
        this.color = c;
        this.isSolid = s;

        this.faces = new Polygon[6];
        this.faces[0] = new Polygon(this,
                new double[]{ps.x, ps.x, ps.x + length, ps.x + length},
                new double[]{ps.y, ps.y + length, ps.y + length, ps.y},
                new double[]{ps.z, ps.z, ps.z, ps.z},
                c);

        this.faces[1] = new Polygon(this,
                new double[]{ps.x, ps.x + length, ps.x + length, ps.x},
                new double[]{ps.y, ps.y, ps.y + length, ps.y + length},
                new double[]{ps.z + length, ps.z + length, ps.z + length, ps.z + length},
                c);

        this.faces[2] = new Polygon(this,
                new double[]{ps.x, ps.x + length, ps.x + length, ps.x},
                new double[]{ps.y, ps.y, ps.y, ps.y},
                new double[]{ps.z, ps.z, ps.z + length, ps.z + length}, c);

        this.faces[3] = new Polygon(this,
                new double[]{ps.x, ps.x, ps.x + length, ps.x + length},
                new double[]{ps.y + length, ps.y + length, ps.y + length, ps.y + length},
                new double[]{ps.z, ps.z + length, ps.z + length, ps.z},
                c);

        this.faces[4] = new Polygon(this,
                new double[]{ps.x, ps.x, ps.x, ps.x},
                new double[]{ps.y, ps.y, ps.y + length, ps.y + length},
                new double[]{ps.z, ps.z + length, ps.z + length, ps.z},
                c);

        this.faces[5] = new Polygon(this,
                new double[]{ps.x + length, ps.x + length, ps.x + length, ps.x + length},
                new double[]{ps.y, ps.y + length, ps.y + length, ps.y},
                new double[]{ps.z, ps.z, ps.z + length, ps.z + length}, c);

        this.neighbors = new Voxel[6];
    }

    public boolean isSolid() {
        return isSolid;
    }

}