package me.vp.vengine.util.world;

import me.vp.vengine.Main;
import me.vp.vengine.util.Polygon;
import me.vp.vengine.util.Vector;
import me.vp.vengine.util.player.Player;
import me.vp.vengine.util.world.chunk.Chunk;
import me.vp.vengine.util.world.chunk.ChunkManager;
import me.vp.vengine.util.world.terrain.FlatTerrain;
import me.vp.vengine.util.world.terrain.Terrain;
import me.vp.vengine.util.world.voxel.Voxel;
import me.vp.vengine.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

/*
 *
 * @Author Vp (https://github.com/herravp)
 *
 */
public class World extends JPanel {

    private static final long serialVersionUID = 1L;
    public static Vector lightVector = new Vector(0, 0, 1);
    public long seed;
    public ChunkManager chunks;
    public int totalChunks = 0;
    public int totalVoxels = 0;
    public int totalPolygons = 0;
    public boolean renderFill = true;
    public boolean renderOutline = true;
    public boolean renderNormal = false;
    public ArrayList<Polygon> renderObjects = new ArrayList<>();
    public Player player;
    public Vector lastPlayerChunk;
    public double fps;
    public double frames;

    public ArrayList<Terrain> terrains = new ArrayList<>();

    public World(long s, Player p) {
        super();
        this.setSize((int) Window.width, (int) Window.height);
        this.setFocusable(true);
        this.hideMouse();

        // Init player
        this.player = p;
        this.player.setWorld(this);

        // user input
        this.addKeyListener(this.player.input);
        this.addMouseListener(this.player.input);
        this.addMouseMotionListener(this.player.input);
        this.addMouseWheelListener(this.player.input);

        // Init world
        this.seed = s;
        this.chunks = new ChunkManager(new FlatTerrain(s), 3); /* using flat terrain for debugging. */
        this.update();
    }

    public static void centerMouse() {
        try {
            new Robot().mouseMove((int) (Main.windowX / 2f), (int) (Main.windowY / 2f));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void hideMouse() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
        Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "InvisibleCursor");
        this.setCursor(invisibleCursor);
    }

    public void update() {
        if (player.input.mouseDown) if (System.currentTimeMillis() > player.input.mouseDownTime + 1000)
            this.player.processMouseClick();

        Vector pc = this.chunks.getChunkVector(this.player.viewFrom);
        if (!pc.equals(this.lastPlayerChunk)) {
            this.chunks.loadChunks(pc);
            this.lastPlayerChunk = pc;
        }
    }

    public void paint(Graphics g) {
        // Clear screen and draw background
        super.paint(g);
        g.setColor(new Color(140, 180, 180));
        g.fillRect(0, 0, (int) Window.width, (int) Window.height);

        // Update user view
        this.player.processMovement();
        this.player.setPredeterminedInfo();

        // Update render objects
        this.setRenderObjects();
        Collections.sort(this.renderObjects);
        this.setPolygonMouseOver();

        // Draw Polygons to screen
        for (Polygon p : this.renderObjects)
            p.drawPolygon(g, this, this.player);

        // Draw Player UI
        this.player.drawUI(g, this);

        // Update frames
        this.frames++;
    }

    public void setRenderObjects() {
        this.totalChunks = 0;
        this.totalVoxels = 0;
        this.totalPolygons = 0;
        this.renderObjects = new ArrayList<>();

        for (Chunk c : this.chunks.loaded) {
            c.setPrederterminedInfo(this.player);
            this.totalChunks++;

            for (Voxel v : c.getVoxelList()) {
                this.totalVoxels++;

                for (int k = 0; k < v.faces.length; k++) {
                    Polygon p = v.faces[k];
                    this.totalPolygons++;

                    if (v.neighbors[k] == null || v.isSolid() && !v.neighbors[k].isSolid())
                        if (c.visibleDirections.contains(p.normal))
                            if (p.update(this.player))
                                this.renderObjects.add(p);

                }
            }
        }
    }

    public void setPolygonMouseOver() {
        this.player.polygonMouseOver = null;

        for (int i = this.renderObjects.size() - 1; i >= 0; i--) {
            Polygon p = this.renderObjects.get(i);
            if (p.mouseOver()) {
                this.player.polygonMouseOver = p;
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
            if (lastFPSCheck >= 1000) {
                this.fps = this.frames;
                lastFPSCheck = 0;
                this.frames = 0;
            }

            if (delta < 1000.0 / maxFPS) {
                try {
                    Thread.sleep((long) (1000.0 / maxFPS - delta));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.repaint();
            this.update();
        }
    }

}