package me.vp.vengine.window;

import javax.swing.*;

/*
 *
 * @Author Vp (https://github.com/herravp)
 *
 */
public class Window extends JFrame {
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