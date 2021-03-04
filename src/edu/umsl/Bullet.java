package edu.umsl;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class Bullet extends GameObject {

    private boolean up;

    public Bullet() {
        this(new ImageIcon("images/blue-bullet.png"));
        up = true;
    }

    public Bullet(ImageIcon bulletIcon) {
        setIcon(bulletIcon);
        width = 10;
        height = 10;
        x = 0;
        y = 0;
        setAttribute("bullet");
    }

    public Bullet(boolean up, ImageIcon bulletIcon) {
        this(bulletIcon);
        this.up = up;
    }

    @Override
    public void update() {
        if (up) {
            y -= 10;
        } else {
            y += 10;
        }
    }

    @Override
    public void draw(Graphics g, Component c) {
        g.drawImage(getIcon().getImage(), x, y, width, height, c);
    }
}
