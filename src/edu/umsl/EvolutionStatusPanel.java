package edu.umsl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class EvolutionStatusPanel extends JPanel {

    JLabel currentWaveLabel = new JLabel();
    JLabel lastWaveHighestFitness = new JLabel();
    JLabel fittestGenomeLabel = new JLabel();
    JLabel fittestInstructionsLabel = new JLabel();

    public EvolutionStatusPanel() {
        currentWaveLabel = new JLabel();
        lastWaveHighestFitness = new JLabel();
        fittestGenomeLabel = new JLabel();
        fittestInstructionsLabel = new JLabel();

        Border raisedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        Border loweredBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        setBorder(BorderFactory.createCompoundBorder(raisedBorder, loweredBorder));

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.weightx = .5;

        constraints.insets = new Insets(20, 20, 10, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(new JLabel("Current Wave: "), constraints);

        constraints.insets = new Insets(20, 0, 10, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        add(currentWaveLabel, constraints);

        constraints.insets = new Insets(20, 20, 10, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 0;
        add(new JLabel("Last Wave Highest Fitness: "), constraints);

        constraints.insets = new Insets(20, 0, 10, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 3;
        constraints.gridy = 0;
        add(lastWaveHighestFitness, constraints);

        constraints.insets = new Insets(10, 20, 10, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        add(new JLabel("Fittest Genome: "), constraints);

        constraints.insets = new Insets(10, 0, 10, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        add(fittestGenomeLabel, constraints);

        constraints.insets = new Insets(10, 20, 20, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        add(new JLabel("Fittest Instructions: "), constraints);

        constraints.insets = new Insets(10, 0, 20, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        add(fittestInstructionsLabel, constraints);

    }

    public void setCurrentWave(int wave) {
        currentWaveLabel.setText(Integer.toString(wave));
    }

    public void setLastWaveHighestFitness(int highestFitness) {
        lastWaveHighestFitness.setText(Integer.toString(highestFitness));
    }

    public void clearLastWaveHighestFitness() {
        lastWaveHighestFitness.setText("");
    }

    public void setFittestGenome(String genome) {
        fittestGenomeLabel.setText(genome);
    }

    public void setFittestInstructions(String instructions) {
        fittestInstructionsLabel.setText(instructions);
    }

    public void clearFittestGenome() {
        fittestGenomeLabel.setText("");
    }

    public void clearFittestInstructions() {
        fittestInstructionsLabel.setText("");
    }

    public void updateGeneration(int generation) {
        setCurrentWave(generation);
        clearLastWaveHighestFitness();
        clearFittestGenome();
        clearFittestInstructions();
    }

    public void updateFittest(int generation, Alien fittestAlien) {
        setLastWaveHighestFitness(fittestAlien.getFitness());
        setFittestGenome(fittestAlien.getGenome());
        setFittestInstructions(fittestAlien.getInstructionString());
        setCurrentWave(generation);
    }

}
