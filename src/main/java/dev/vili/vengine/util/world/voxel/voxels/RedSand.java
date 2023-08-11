/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine.util.world.voxel.voxels;

import dev.vili.vengine.util.world.voxel.Voxel;
import dev.vili.vengine.util.Vector;

import java.awt.*;

public class RedSand extends Voxel {
    public RedSand(Vector vector, boolean solid) {
        super(vector, new Color(0xDDFF3684, true), solid);
    }
}

