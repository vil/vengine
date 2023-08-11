/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine.util;

import dev.vili.vengine.util.player.Player;
import dev.vili.vengine.util.world.World;
import dev.vili.vengine.util.world.voxel.Voxel;
import dev.vili.vengine.window.Window;

import java.awt.*;
import java.util.Objects;

public class Polygon extends java.awt.Polygon implements Comparable<Polygon> {

    public Voxel parent;

    public Vector[] vertexes;
    public Vector normal;
    public int[] projectX;
    public int[] projectY;
    public Vector projectC;
    public Vector projectN;

    public Color color;
    public double alpha = 255;
    public double lighting = 1;

    public double distance;

    public Polygon(Voxel p, double[] x, double[] y, double[] z, Color c) {
        this.parent = p;

        this.vertexes = new Vector[x.length];
        for (int i = 0; i < x.length; i++)
            this.vertexes[i] = new Vector(x[i], y[i], z[i]);
        this.normal = new Plane(this).normal.normalise();

        this.color = c;
    }

    public boolean update(Player player) {
        this.normal = new Plane(this).normal.normalise();

        // If the player and polygon face same direction
        if (this.normal.dot(this.vertexes[0].subtract(player.viewFrom)) >= 0)
            return false;


        // Calculate centre point
        Vector project = this.getCentre().project(player);
        if (project.z > 0.5)
            return false;
        this.projectC = new Vector(
                ((dev.vili.vengine.window.Window.width / 2 - player.viewFocus.x) + project.x * player.zoom),
                ((dev.vili.vengine.window.Window.height / 2 - player.viewFocus.y) + project.y * player.zoom),
                project.z
        );

        // Calculate points in projected space
        this.projectX = new int[this.vertexes.length];
        this.projectY = new int[this.vertexes.length];
        for (int i = 0; i < this.vertexes.length; i++) {
            project = this.vertexes[i].project(player);

            if (project.z < 0)
                return false;

            this.projectX[i] = (int) ((dev.vili.vengine.window.Window.width / 2 - player.viewFocus.x) + project.x * player.zoom);
            this.projectY[i] = (int) ((dev.vili.vengine.window.Window.height / 2 - player.viewFocus.y) + project.y * player.zoom);
        }

        // Calculate polygon lighting
        double angle = Math.acos(World.lightVector.multiply(this.normal).sum() / World.lightVector.length());
        this.lighting = 0.2 + 1 - Math.sqrt(Math.toDegrees(angle) / 180);
        this.lighting = Math.min(Math.max(this.lighting, 0), 1);

        // Calculate normal line
        project = this.getCentre().add(this.normal.scale(Voxel.length / 2)).project(player);
        this.projectN = new Vector(
                ((dev.vili.vengine.window.Window.width / 2 - player.viewFocus.x) + project.x * player.zoom),
                ((dev.vili.vengine.window.Window.height / 2 - player.viewFocus.y) + project.y * player.zoom),
                project.z
        );

        // Calculate distance to player
        double total = 0;
        for (Vector v : this.vertexes)
            total += player.viewFrom.distance(v);
        this.distance = total / this.vertexes.length;

        return true;
    }

    public Vector getCentre() {
        Vector m = new Vector(0, 0, 0);
        for (Vector v : this.vertexes)
            m = m.add(v);
        return m.scale((double) 1 / this.vertexes.length);
    }

    public void drawPolygon(Graphics g, World w, Player p) {
        if (w.renderFill && this.alpha > 0) {
            g.setColor(
                    new Color(
                            (int) (this.color.getRed() * lighting),
                            (int) (this.color.getGreen() * lighting),
                            (int) (this.color.getBlue() * lighting),
                            (int) (Math.min(this.color.getAlpha(), this.alpha))
                    )
            );
            g.fillPolygon(this.projectX, this.projectY, this.projectX.length);
        }

        if (Objects.equals(p.polygonMouseOver, this)) {
            g.setColor(new Color(255, 255, 255, 100));
            g.fillPolygon(this.projectX, this.projectY, this.projectX.length);
        }

        if (w.renderOutline) {
            g.setColor(new Color(0, 0, 0));
            g.drawPolygon(this.projectX, this.projectY, this.projectX.length);
        }

        if (w.renderNormal) {
            if (this.projectN.z >= 0) {
                g.setColor(new Color(0, 0, 0));
                g.drawLine((int) this.projectC.x, (int) this.projectC.y, (int) this.projectN.x, (int) this.projectN.y);
            }
        }
    }

    public boolean mouseOver() {
        double x = dev.vili.vengine.window.Window.width / 2;
        double y = Window.height / 2;
        int j = this.projectX.length - 1;
        boolean oddNodes = false;

        for (int i = 0; i < this.projectX.length; i++) {
            if (
                    (this.projectY[i] < y && this.projectY[j] >= y || this.projectY[j] < y && this.projectY[i] >= y) &&
                            (this.projectX[i] <= x || this.projectX[j] <= x)
            ) {
                oddNodes ^= (this.projectX[i] + (y - this.projectY[i]) / (this.projectY[j] - this.projectY[i]) * (this.projectX[j] - this.projectX[i]) < x);
            }
            j = i;
        }

        return oddNodes;
    }

    @Override
    public int compareTo(Polygon p) {
        if (this.distance < p.distance) return 1;
        else if (p.distance < this.distance) return -1;
        else return 0;
    }
}