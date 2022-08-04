package me.vp.vengine.util.player;

import me.vp.vengine.util.Vector;
import me.vp.vengine.util.world.World;
import me.vp.vengine.util.world.voxel.voxels.Grass;

import java.awt.event.*;

public class PlayerListener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    public boolean left, right, forward, back, up, down, sprint, exit;
    public int mouseX, mouseY, mouseScroll;
    public boolean rightClick, leftClick, mouseDown;
    public long mouseDownTime;
    private final Player player;

    public PlayerListener(Player player) {
        this.player = player;
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_W -> forward = true;
            case KeyEvent.VK_D -> right = true;
            case KeyEvent.VK_S -> back = true;
            case KeyEvent.VK_A -> left = true;
            case KeyEvent.VK_SPACE -> up = true;
            case KeyEvent.VK_CONTROL -> down = true;
            case KeyEvent.VK_SHIFT -> sprint = true;
            case KeyEvent.VK_ESCAPE -> exit = true;
            case KeyEvent.VK_1 -> {
                try {
                    player.voxelConstructor = Grass.class.getDeclaredConstructor(Vector.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case KeyEvent.VK_F9 -> World.changeTerrainToNormal();
            case KeyEvent.VK_F10 -> World.changeTerrainToMars();
            case KeyEvent.VK_F11 -> World.changeTerrainToDebug();
            default -> throw new IllegalStateException("Unexpected value: " + key.getKeyCode());
        }
    }

    public void keyReleased(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_W -> forward = false;
            case KeyEvent.VK_D -> right = false;
            case KeyEvent.VK_S -> back = false;
            case KeyEvent.VK_A -> left = false;
            case KeyEvent.VK_SPACE -> up = false;
            case KeyEvent.VK_CONTROL -> down = false;
            case KeyEvent.VK_SHIFT -> sprint = false;
            case KeyEvent.VK_ESCAPE -> exit = false;
        }
    }

    public void keyTyped(KeyEvent arg0) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseScroll = e.getUnitsToScroll();
        this.player.processMouse();
    }

    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        World.centerMouse();
        this.player.processMouse();
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        World.centerMouse();
        this.player.processMouse();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        mouseDownTime = System.currentTimeMillis();
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                leftClick = true;
                break;
            case MouseEvent.BUTTON3:
                rightClick = true;
                break;
            default:
        }
        this.player.processMouse();
    }

    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                leftClick = false;
                break;
            case MouseEvent.BUTTON3:
                rightClick = false;
                break;
            default:
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}