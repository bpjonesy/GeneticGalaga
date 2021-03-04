package edu.umsl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Ship extends GameObject {

    private int horizontalIncrement;
    private int hits;
    private ArrayList<Bullet> bullets = new ArrayList<>();

    public Ship() {
        setIcon(new ImageIcon("images/ship.png"));
        setAttribute("ship");
    }

    @Override
    public void update() {
        x += horizontalIncrement;
    }

    public void hit() {
        hits++;
        if (hits == 3) {
            kill();
        }
    }

    public void move(int increment) {
        this.horizontalIncrement = increment;
    }
    
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
    
    public int getHits() {
        return hits;
    }
    
    public void fire() {
        Bullet bullet = new Bullet();
        bullet.x = this.x + 20;
        bullet.y = this.y - 20;
        bullets.add(bullet);
    }

    @Override
    public void draw(Graphics g, Component c) {
        super.draw(g, c);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawString("Hits: " + hits, this.x + 5, this.y + this.height + 15);
    }

}
