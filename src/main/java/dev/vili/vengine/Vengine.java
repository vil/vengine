/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine;

import dev.vili.vengine.util.Vector;
import dev.vili.vengine.util.player.Player;
import dev.vili.vengine.util.world.World;
import dev.vili.vengine.window.Window;

import java.awt.*;
import java.util.logging.Logger;

public class Vengine {
    public static Logger LOGGER = Logger.getLogger("Vengine");
    public static final String version = "0.01-beta";
    public static double windowX;
    public static double windowY;

    public static void main(String[] args) {
        LOGGER.info("Vengine v" + version + " is starting...");
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Vengine.windowX = dimension.getWidth();
        Vengine.windowY = dimension.getHeight();

        Player player = new Player(new Vector(0, 0, 70));
        World world = new World(0, player);
        dev.vili.vengine.window.Window window = new Window("Vengine " + version + " by Vili", 1900, 1080);
        LOGGER.info("Creating world...");
        window.add(world);
        world.run();
        LOGGER.info("World created..!");
    }

}
