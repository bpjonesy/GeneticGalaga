package edu.umsl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener {

    private static final String BACKGROUND_IMAGE = "images/background.png";
    private static final String GAME_OVER_IMAGE = "images/game-over.png";
    private static final String LOGO_IMAGE = "images/logo.png";
    private static final String INTRO_SOUND = "intro.wav";
    private static final String EXPLOSION_SOUND = "explosion.wav";
    private static final String LASER_SOUND = "laser.wav";

    private Image backgroundImage;
    private Image logoImage;
    private Image gameOverImage;

    private List<Alien> aliens;
    private Ship ship;

    private long updateStartTime = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;

    private int wave;
    private final EvolutionEngine evolutionEngine;

    EvolutionStatusPanel statusPanel;

    public GamePanel(EvolutionStatusPanel statusPanel) {
        this.statusPanel = statusPanel;
        evolutionEngine = new EvolutionEngine();
        loadImages();
        resetGame();
    }

    public void start() {
        resetGame();
        playSound(INTRO_SOUND);
        startUpdateThread();
        updateStartTime = System.currentTimeMillis();
    }

    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(new File(BACKGROUND_IMAGE));
            logoImage = ImageIO.read(new File(LOGO_IMAGE));
            gameOverImage = ImageIO.read(new File(GAME_OVER_IMAGE));
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void resetGame() {
        gameOver = false;
        addShipToGame();
        aliens = evolutionEngine.createNewAliens(3, ship, this.getWidth());
        wave = 1;
        statusPanel.updateGeneration(wave);
    }

    protected void addShipToGame() {
        ship = new Ship();
        ship.x = 200;
        ship.y = 600;
    }

    @Override
    public void paintComponent(Graphics g) {
        clearScreen(g);
        if (gameStarted) {
            drawAllObjectsOnScreen(g);
        } else {
            drawIntro(g);
        }
    }

    private void drawIntro(Graphics g) {
        g.drawImage(logoImage, 100, 150, 400, 400, this);
    }

    private void drawAllObjectsOnScreen(Graphics g) {
        drawAliensAndBullets(g);
        drawShipAndBullets(g);
        drawGameOverOnShipDeath(g);
    }

    private void drawGameOverOnShipDeath(Graphics g) {
        if (!ship.isAlive()) {
            g.drawImage(gameOverImage, 200, 150, 200, 200, this);
            g.setColor(new Color(55, 220, 0));
            g.setFont(new Font("sansserif", Font.BOLD, 24));
            g.drawString("Press F1 for a new game", 150, 450);
            gameOver = true;
        }
    }

    private void drawShipAndBullets(Graphics g) {
        ship.draw(g, this);
        
        ship.getBullets().forEach((bullet) -> {
            bullet.draw(g, this);
        });
    }

    private void drawAliensAndBullets(Graphics g) {
        aliens.stream().map((Alien alien) -> {
            alien.draw(g, GamePanel.this);
            return alien;
        }).forEachOrdered((Alien alien) -> {
            if (alien.getBullet() != null) {
                alien.getBullet().draw(g, GamePanel.this);
            }
        });
    }

    private void clearScreen(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    public void update() {
        setGameStartedAfterIntro();
        if (!gameStarted) {
            repaint();
            return;
        }
        if (gameOver) {
            return;
        }
        confineShipToGameBounds();
        evolveWhenAllAliensDie();
        checkForAllBulletHits();
        updateAllActiveBullets();
        removeOutOfRangeBullets();  
        repaint();
    }

    private void checkForAllBulletHits() {
        for (Alien alien : aliens) {
            alien.update();
            for (Bullet bullet : ship.getBullets()) {
                if (bullet.intersects(alien)) {
                    alien.kill();
                    playSound(EXPLOSION_SOUND);
                    break;
                }
            }
            if (alien.intersects(ship) && !alien.getAttribute().equalsIgnoreCase("ship")) {
                ship.kill();
                playSound(EXPLOSION_SOUND);
            }
            if ((alien.getBullet() != null) && (alien.getBullet().intersects(ship))) {
                ship.hit();                
                alien.hitShip();
                if (!ship.isAlive()) {
                    playSound(EXPLOSION_SOUND);
                }
            }
        }
    }

    private void updateAllActiveBullets() {
        ship.getBullets().forEach((bullet) -> {
            bullet.update();
        });
        ship.update();

        aliens.stream().filter((alien) -> (alien.getBullet() != null)).forEachOrdered((alien) -> {
            alien.getBullet().update();
        });
    }

    private void evolveWhenAllAliensDie() {
        int deadAlienCount = 0;
        deadAlienCount = aliens.stream().filter((alien) -> (!alien.isAlive())).map((_item) -> 1).reduce(deadAlienCount, Integer::sum); 
        if (deadAlienCount == aliens.size()) {
            evolve();
        }
    }

    private void setGameStartedAfterIntro() {
        if ((System.currentTimeMillis() - updateStartTime) > 3000) {
            gameStarted = true;
        }
    }

    private void removeOutOfRangeBullets() {       
        ship.getBullets().removeIf(b -> b.y < 0);
        aliens.stream().filter((alien) -> (alien.getBullet() != null && alien.getBullet().y > ship.y + ship.height)).forEachOrdered((alien) -> {
            alien.bulletExpired();
        });       
    }

    private void evolve() {
        wave++;
        statusPanel.updateFittest(wave, evolutionEngine.getFittestAlien(aliens));
        aliens = evolutionEngine.evolveAliens(aliens, ship, this.getWidth());
    }

    public void confineShipToGameBounds() {
        int minX = 0;
        int maxX = this.getWidth() - ship.width;
        if (ship.x > maxX) {
            ship.x = maxX;
        }
        if (ship.x < 0) {
            ship.x = 0;
        }
    }

    @Override
    public void keyPressed(KeyEvent k) {
        char c = k.getKeyChar();
        if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
            ship.move(5);
        }
        if (k.getKeyCode() == KeyEvent.VK_LEFT) {
            ship.move(-5);
        }
        if (c == ' ') {
            ship.fire();
            playSound(LASER_SOUND);
        }
        if (k.getKeyCode() == KeyEvent.VK_F1) {
            if (gameOver) {
                resetGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent k) {
        if (k.getKeyCode() == KeyEvent.VK_LEFT || k.getKeyCode() == KeyEvent.VK_RIGHT) {
            ship.move(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent k) {
    }

    private void playSound(String soundFile) {
        SoundPlayer sound = new SoundPlayer(soundFile);
        sound.start();
    }

    private void startUpdateThread() {
        new Thread(() -> {
            while (true) {
                update();
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GamePanel.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

}
