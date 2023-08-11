/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine.util.player;

import dev.vili.vengine.Vengine;
import dev.vili.vengine.util.Plane;
import dev.vili.vengine.util.Polygon;
import dev.vili.vengine.util.Vector;
import dev.vili.vengine.util.world.World;
import dev.vili.vengine.util.world.voxel.Voxel;
import dev.vili.vengine.window.Window;

import java.awt.*;
import java.lang.reflect.Constructor;

public class Player {
    private final double movementSpeed;
    private final double horizontalLookSpeed;
    private final double verticalLookSpeed;
    private final double minZoom;
    private final double maxZoom;
    public double zoom;
    public int aimSight;
    public Vector viewFrom;
    public Vector viewTo;
    public Vector viewVector;
    public Vector viewW1;
    public Vector viewW2;
    public Vector viewFocus;
    public Plane viewPlane;
    public PlayerListener input;
    public Polygon polygonMouseOver;
    public Constructor<Voxel> voxelConstructor;
    private double horizontalLook;
    private double verticalLook;

    public Player(Vector vf) {
        this.movementSpeed = 0.50;
        this.horizontalLookSpeed = 900;
        this.verticalLookSpeed = 2200;
        this.horizontalLook = 0;
        this.verticalLook = -0.2;
        this.zoom = 1000;
        this.minZoom = 500;
        this.maxZoom = 2500;
        this.aimSight = 4;
        this.viewTo = new Vector(0, 0, 0);
        this.viewFrom = vf;
        this.input = new PlayerListener(this);
        this.polygonMouseOver = null;
    }

    public void setWorld(World w) {
    }

    public void processMouse() {
        double difX = (this.input.mouseX - (int) (Window.width / 2f));
        double difY = (this.input.mouseY - (int) (Window.height / 2f));
        difY -= Math.min(15, (Vengine.windowY - Window.height) / 2);
        difY *= 6 - Math.abs(this.verticalLook) * 5;

        this.verticalLook -= difY / this.verticalLookSpeed;
        this.horizontalLook += difX / this.horizontalLookSpeed;
        this.verticalLook = Math.min(this.verticalLook, 0.999);
        this.verticalLook = Math.max(this.verticalLook, -0.999);

        this.processMouseClick();

        if (this.input.mouseScroll > 0)
            if (this.zoom > this.minZoom)
                this.zoom -= 25 * this.input.mouseScroll;
        if (this.input.mouseScroll < 0)
            if (this.zoom < this.maxZoom)
                this.zoom -= 25 * this.input.mouseScroll;
        this.input.mouseScroll = 0;

        // Update view vector
        double r = Math.sqrt(1 - (this.verticalLook * this.verticalLook));
        this.viewVector = new Vector(r * Math.cos(this.horizontalLook), this.verticalLook, r * Math.sin(this.horizontalLook));
        this.viewVector.normalise();

        this.viewTo = this.viewFrom.add(this.viewVector);
    }

    public void processMouseClick() {
        try {
            if (this.input.leftClick)
                if (this.polygonMouseOver != null)
                    World.chunks.removeVoxel(this.polygonMouseOver.parent);
        } catch (Exception e) {
            Vengine.LOGGER.warning("Error while processing mouse click..! : " + e.getMessage());
        }

        try {
            if (this.input.rightClick)
                if (this.polygonMouseOver != null)
                    World.chunks.addVoxel(this.voxelConstructor.newInstance(new Vector(this.polygonMouseOver.parent.position.add(this.polygonMouseOver.normal))));
        } catch (Exception e) {
            Vengine.LOGGER.warning("Error while processing mouse click..! : " + e.getMessage());
        }
    }

    public void processMovement() {
        if (this.input.exit) System.exit(0);

        this.viewVector = this.viewTo.subtract(this.viewFrom).normalise();
        Vector move = new Vector(0, 0, 0);
        double speed = this.movementSpeed;
        Vector vertVector = new Vector(0, 0, 1).normalise();
        Vector horzVector = this.viewVector.cross(vertVector);

        if (this.input.forward) move = move.add(this.viewVector);
        if (this.input.back) move = move.subtract(this.viewVector);
        if (this.input.left) move = move.add(horzVector);
        if (this.input.right) move = move.subtract(horzVector);
        if (this.input.up) move = move.add(vertVector);
        if (this.input.down) move = move.subtract(vertVector);
        if (this.input.sprint) speed = speed * 3;

        this.viewFrom = move.normalise().scale(speed).add(this.viewFrom);
        double r = Math.sqrt(1 - (this.verticalLook * this.verticalLook));
        this.viewTo = new Vector(
                viewFrom.x + r * Math.cos(this.horizontalLook),
                viewFrom.y + r * Math.sin(this.horizontalLook),
                viewFrom.z + this.verticalLook
        );
    }

    public void setPredeterminedInfo() {
        this.viewVector = this.viewTo.subtract(this.viewFrom).normalise();
        Vector DirectionVector = new Vector(1, 1, 1).normalise();
        Vector PlaneVector1 = this.viewVector.cross(DirectionVector);
        Vector PlaneVector2 = this.viewVector.cross(PlaneVector1);
        this.viewPlane = new Plane(this.viewTo, PlaneVector1, PlaneVector2);

        double dx = Math.abs(viewFrom.x - viewTo.x);
        double dy = Math.abs(viewFrom.y - viewTo.y);
        double xRot = dy / (dx + dy);
        double yRot = dx / (dx + dy);
        if (viewFrom.y > viewTo.y) xRot = -xRot;
        if (viewFrom.x < viewTo.x) yRot = -yRot;
        Vector RotationVector = new Vector(xRot, yRot, 0).normalise();
        this.viewW1 = this.viewVector.cross(RotationVector);
        this.viewW2 = this.viewVector.cross(this.viewW1);

        this.viewFocus = this.viewTo.project(this);
        this.viewFocus.x = this.zoom * this.viewFocus.x;
        this.viewFocus.y = this.zoom * this.viewFocus.y;
    }

    public void drawUI(Graphics graphics, World world) {
        if (World.isMars) graphics.setColor(Color.WHITE);
        else graphics.setColor(Color.black);
        graphics.drawLine((int) (Window.width / 2f) - this.aimSight, (int) (Window.height / 2f), (int) (Window.width / 2f) + this.aimSight, (int) (Window.height / 2f));
        graphics.drawLine((int) (Window.width / 2f), (int) (Window.height / 2f) - this.aimSight, (int) (Window.width / 2f), (int) (Window.height / 2f) + this.aimSight);


        if (World.isMars) graphics.setColor(Color.WHITE);
        else graphics.setColor(Color.black);
        graphics.drawString("Vengine " + Vengine.version + " by Vili", 10, 20);
        graphics.drawString("Current terrain: " + (World.isMars ? "Mars" : "Normal"), 200, 20);
        graphics.drawString("Press F9/F10/F11 to change terrain!", 200, 40);
        graphics.drawString("FPS: " + (int) world.fps, 10, 40);
        graphics.drawString("XYZ: " + this.viewFrom, 10, 60);
        graphics.drawString("Look: " + this.viewTo.subtract(this.viewFrom), 10, 80);
        graphics.drawString("Zoom: " + this.zoom, 10, 100);

        graphics.drawString("Objects loaded: ", 10, 160);
        graphics.drawString("Chunks: " + world.totalChunks / World.chunks.terrain.maxZ, 10, 180);
        graphics.drawString("Voxels: " + world.totalVoxels, 10, 200);
        graphics.drawString("Polygons: " + world.renderObjects.size() + "/" + world.totalPolygons, 10, 220);

    }
}