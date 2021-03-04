package edu.umsl;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class GeneticGalaga {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());

        EvolutionStatusPanel statusPanel = new EvolutionStatusPanel();
        GamePanel gamePanel = new GamePanel(statusPanel);
        gamePanel.addKeyListener(gamePanel);
        gamePanel.setFocusable(true);
        
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        frame.setTitle("Genetic Galaga");
        frame.setSize(600, 1050);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.setVisible(true);
        gamePanel.start();
        
    }

}
