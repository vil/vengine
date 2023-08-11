/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine.util.world;

import dev.vili.vengine.util.player.Player;
import dev.vili.vengine.util.world.terrain.Terrain;
import dev.vili.vengine.util.world.voxel.Voxel;
import dev.vili.vengine.Vengine;
import dev.vili.vengine.util.Polygon;
import dev.vili.vengine.util.Vector;
import dev.vili.vengine.util.world.chunk.Chunk;
import dev.vili.vengine.util.world.chunk.ChunkManager;
import dev.vili.vengine.util.world.terrain.FlatTerrain;
import dev.vili.vengine.util.world.terrain.MarsTerrain;
import dev.vili.vengine.util.world.terrain.NormalTerrain;
import dev.vili.vengine.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;

public class World extends JPanel {

    @Serial
    private static final long serialVersionUID = 1L;
    public static Vector lightVector = new Vector(0, 0, 1);
    public static long seed;
    public static ChunkManager chunks;
    public static Player player;
    public static Vector lastPlayerChunk;
    public static boolean isMars;
    public static boolean isDebug = false;
    public int totalChunks = 0;
    public int totalVoxels = 0;
    public int totalPolygons = 0;
    public boolean renderFill = true;
    public boolean renderOutline = true;
    public boolean renderNormal = false;
    public ArrayList<Polygon> renderObjects = new ArrayList<>();
    public double fps;
    public double frames;
    public ArrayList<Terrain> terrains = new ArrayList<>();

    public World(long s, Player p) {
        super();
        this.setSize((int) Window.width, (int) Window.height);
        this.setFocusable(true);
        this.hideMouse();

        // Init player
        player = p;
        player.setWorld(this);

        // user input
        this.addKeyListener(player.input);
        this.addMouseListener(player.input);
        this.addMouseMotionListener(player.input);
        this.addMouseWheelListener(player.input);

        // Init world
        seed = s;
        chunks = new ChunkManager(new NormalTerrain(s), 3);
        update();
    }

    public static void centerMouse() {
        try {
            new Robot().mouseMove((int) (Vengine.windowX / 2f), (int) (Vengine.windowY / 2f));
        } catch (AWTException e) {
            Vengine.LOGGER.warning(e.getMessage());
        }
    }

    public static void update() {
        if (player.input.mouseDown) if (System.currentTimeMillis() > player.input.mouseDownTime + 1000)
            player.processMouseClick();

        Vector pc = chunks.getChunkVector(player.viewFrom);
        if (!pc.equals(lastPlayerChunk)) {
            chunks.loadChunks(pc);
            lastPlayerChunk = pc;
        }
    }

    public static void changeTerrainToNormal() {
        isMars = false;
        chunks = new ChunkManager(new NormalTerrain(seed), 3);
        update();
    }

    public static void changeTerrainToMars() {
        isMars = true;
        chunks = new ChunkManager(new MarsTerrain(seed), 3);
        update();
    }

    public static void changeTerrainToDebug() {
        isMars = false;
        isDebug = true;
        chunks = new ChunkManager(new FlatTerrain(seed), 3);
        update();
    }

    public void hideMouse() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
        Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "InvisibleCursor");
        this.setCursor(invisibleCursor);
    }

    public void paint(Graphics g) {
        // Clear screen and draw background
        super.paint(g);
        if (isMars) g.setColor(new Color(0, 0, 0));
        else g.setColor(new Color(140, 180, 180));
        g.fillRect(0, 0, (int) Window.width, (int) Window.height);

        // Update user view
        player.processMovement();
        player.setPredeterminedInfo();

        // Update render objects
        this.setRenderObjects();
        Collections.sort(this.renderObjects);
        this.setPolygonMouseOver();

        // Draw Polygons to screen
        for (Polygon p : this.renderObjects)
            p.drawPolygon(g, this, player);

        // Draw Player UI
        player.drawUI(g, this);

        // Update frames
        this.frames++;
    }

    public void setRenderObjects() {
        this.totalChunks = 0;
        this.totalVoxels = 0;
        this.totalPolygons = 0;
        this.renderObjects = new ArrayList<>();

        for (Chunk c : chunks.loaded) {
            c.setPrederterminedInfo(player);
            this.totalChunks++;

            for (Voxel v : c.getVoxelList()) {
                this.totalVoxels++;

                for (int k = 0; k < v.faces.length; k++) {
                    Polygon p = v.faces[k];
                    this.totalPolygons++;

                    if (v.neighbors[k] == null || v.isSolid() && !v.neighbors[k].isSolid())
                        if (c.visibleDirections.contains(p.normal))
                            if (p.update(player)) this.renderObjects.add(p);
                }
            }
        }
    }

    public void setPolygonMouseOver() {
        player.polygonMouseOver = null;

        for (int i = this.renderObjects.size() - 1; i >= 0; i--) {
            Polygon p = this.renderObjects.get(i);
            if (p.mouseOver()) {
                player.polygonMouseOver = p;
                break;
            }
        }
    }

    public void run() {
        double maxFPS = 60;
        double lastFPSCheck = 0;
        double lastRefresh = 0;

        while (true) {
            long delta = (long) (System.currentTimeMillis() - lastRefresh);
            lastRefresh = System.currentTimeMillis();

            lastFPSCheck += delta;
            if (lastFPSCheck > 1000) {
                this.fps = this.frames / lastFPSCheck * 1000;
                lastFPSCheck = 0;
                this.frames = 0;
            }
            if (delta < 1000 / maxFPS) {
                try {
                    Thread.sleep((long) (1000 / maxFPS - delta));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.repaint();
            update();
        }
    }

}