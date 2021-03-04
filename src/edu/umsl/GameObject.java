package edu.umsl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class GameObject extends Rectangle {

    private ImageIcon icon;
    private ImageIcon explosionIcon;
    private boolean alive;
    private String attribute = "nothing";
    private int explosionTimeCounter = 0;

    public GameObject() {
        x = 200;
        y = 200;
        width = 50;
        height = 50;
        alive = true;
        explosionIcon = new ImageIcon("images/explosion.png");
    }

    public boolean isAlive() {
        return this.alive;
    }

    protected void setAlive(boolean alive) {
        this.alive = alive;
    }

    protected void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    protected String getAttribute() {
        return this.attribute;
    }

    public void setIcon(ImageIcon p) {
        icon = p;
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public void setExplosionIcon(ImageIcon p) {
        explosionIcon = p;
    }

    public void draw(Graphics g, Component c) {
        if (alive) {
            if (icon != null) {
                g.drawImage(icon.getImage(), x, y, width, height, c);
            } else {
                g.setColor(Color.BLUE);
                g.fillRect(x, y, width, height);
            }
        } else {
            if (explosionTimeCounter < 50) {
                if (explosionIcon != null) {
                    g.drawImage(explosionIcon.getImage(), x, y, width, height, c);
                } else {
                    g.setColor(Color.RED);
                    g.fillRect(x, y, width, height);
                }
                explosionTimeCounter++;
            } else {
                x = 0;
                y = 0;
                width = 0;
                height = 0;
                explosionTimeCounter = 0;
            }
        }
    }

    public void kill() {
        alive = false;
    }

    public void update() {

    }

}
