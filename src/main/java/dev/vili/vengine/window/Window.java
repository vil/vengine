/*
 * Copyright (c) 2023. Vili and contributors.
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 *  file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package dev.vili.vengine.window;

import javax.swing.*;
import java.io.Serial;

public class Window extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    public static double width = 1900;
    public static double height = 1080;

    public Window(String title, double width, double height) {
        super(title);
        Window.width = width;
        Window.height = height;

        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize((int) Window.width, (int) Window.height);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setVisible(true);
    }

}