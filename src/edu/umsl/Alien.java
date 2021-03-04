package edu.umsl;

import java.awt.Component;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class Alien extends GameObject {

    private final static int NUM_TURNS_PER_INSTRUCTION = 4;

    private final Random dice;
    private int cycleCount = 1;
    private int horizontalIncrement;
    private int verticalIncrement;
    private int randomValue = 0;
    private int shipHits;
    private final int horizontalMax;

    private Bullet bullet;

    private int repeatCount;

    private Chromosome chromosome;
    private final Ship ship;

    private int instructionIndex = 0;

    private final int initialX;
    private final int initialY;

    private long birthTime;

    public Alien(Chromosome chromosome, Ship ship, int horizontalMax) {
        super();

        this.chromosome = chromosome;
        this.ship = ship;
        this.horizontalMax = horizontalMax;

        dice = new Random();
        x = dice.nextInt(400);
        y = dice.nextInt(250);
        horizontalIncrement = dice.nextInt(20) + 10;
        verticalIncrement = dice.nextInt(20) + 10;
        setIcon(new ImageIcon("images/alien.png"));
        shipHits = 0;

        initialX = x;
        initialY = y;

        birthTime = System.currentTimeMillis();

        setAttribute("alien");

    }

    private void processInstructions() {
        try {
            Method instruction = chromosome.getInstruction(instructionIndex);
            instruction.invoke(this);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Alien.class.getName()).log(Level.SEVERE, null, ex);
        }
        resetIndexAtTurnsMax();

    }

    private void resetIndexAtTurnsMax() {
        if (repeatCount++ > NUM_TURNS_PER_INSTRUCTION) {
            repeatCount = 0;
            instructionIndex++;
            if (instructionIndex > 9) {
                instructionIndex -= 9;
            }
        }
    }

    public void setChromosome(Chromosome chromosome) {
        this.chromosome = chromosome;
    }

    public Chromosome getChromosome() {
        return this.chromosome;
    }

    public void updateStats() {
        updateDistanceMoved();

        int distanceToShip = (int) Math.sqrt(Math.pow((x - ship.x), 2) + Math.pow((y - ship.y), 2));

        updateClosestDistanceToShip(distanceToShip);
        updateFurthestDistanceFromShip(distanceToShip);
        updateAverageDistanceToShip(distanceToShip);
        updateMaximumDepth();

        chromosome.setTimeAlive(System.currentTimeMillis() - birthTime);
        chromosome.setShipHits(getShipHits());

    }

    private void updateMaximumDepth() {
        if (y > chromosome.getMaxDepth()) {
            chromosome.setMaxDepth(y);
        }
    }

    private void updateAverageDistanceToShip(int distanceToShip) {
        chromosome.addDistanceSample(distanceToShip);
        ArrayList<Integer> distanceSamples = chromosome.getDistanceSamples();
        IntSummaryStatistics stats = distanceSamples.stream().mapToInt((x) -> x).summaryStatistics();
        chromosome.setAverageDistance((int) stats.getAverage());
    }

    private void updateFurthestDistanceFromShip(int distanceToShip) {
        if (distanceToShip > chromosome.getFurthestDistance()) {
            chromosome.setFurthestDistance(distanceToShip);
        }
    }

    private void updateClosestDistanceToShip(int distanceToShip) {
        if (distanceToShip < chromosome.getClosestDistance()) {
            chromosome.setClosestDistance(distanceToShip);
        }
    }

    private void updateDistanceMoved() {
        int distanceMoved = (int) Math.sqrt(Math.pow((x - initialX), 2) + Math.pow((y - initialY), 2));
        if (distanceMoved > chromosome.getDistanceMoved()) {
            chromosome.setDistanceMoved(distanceMoved);
        }
    }

    public void setAlive(boolean alive) {
        super.setAlive(alive);
        birthTime = System.currentTimeMillis();
    }

    public void update() {

        if (!isAlive()) {
            return;
        }

        updateStats();

        processInstructions();

        if (x < 10) {
            x = horizontalMax - 45;
        }
        if (x > (horizontalMax - 45)) {
            x = 45;
        }
        if (y < 100) {
            y = 100;
        }
        if (y > ship.y) {
            y = ship.y - 100;
        }

    }

    public void draw(Graphics g, Component c) {
        super.draw(g, c);

    }

    protected int getShipHits() {
        return this.shipHits;
    }

    public int getFitness() {
        return chromosome.getFitness();
    }

    public String getGenome() {
        return chromosome.getGenome();
    }

    public String getInstructionString() {
        return chromosome.getInstructionString();
    }

    public Bullet getBullet() {
        return bullet;
    }

    public void bulletExpired() {
        bullet = null;
    }

    public void hitShip() {
        shipHits++;
        bullet = null;
    }

    @Instruction(code = "mvlt")
    public void moveLeft() {
        x -= horizontalIncrement;
    }

    @Instruction(code = "mvrt")
    public void moveRight() {
        x += horizontalIncrement;
    }

    @Instruction(code = "mvup")
    public void moveUp() {
        y -= verticalIncrement;
    }

    @Instruction(code = "mvdn")
    public void moveDown() {
        y += verticalIncrement;
    }

    @Instruction(code = "fire")
    public void fire() {
        if (bullet == null) {
            bullet = new Bullet(new ImageIcon("images/yellow-bullet.png"));
            bullet.x = x + width / 2;
            bullet.y = y + 5;
        }
    }

}
