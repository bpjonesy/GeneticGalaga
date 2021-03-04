package edu.umsl;

import java.lang.annotation.Annotation;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Chromosome {
    
    private final byte[] genes;
    private int distanceMoved;
    private int closestDistance = 9999;
    private int furthestDistance;
    private int averageDistance;
    private int maxVelocity;
    private int maxDepth;
    private long timeAlive;
    private int shipHits;

    private final LinkedList<Method> instructions = new LinkedList<>();
    private final ArrayList<Integer> distanceSamples = new ArrayList<>();

    private static final Map<Integer, String> instructionMap = Map.of(
            0, "moveUp",
            1, "moveDown",
            2, "moveRight",
            3, "moveLeft",
            4, "moveDown",
            5, "moveRight",
            6, "moveLeft",
            7, "fire");

    public Chromosome() {
        genes = new byte[30];
    }

    public void generateChromosome() {
        for (int i = 0; i < size(); i++) {
            byte gene = (byte) Math.round(Math.random());
            genes[i] = gene;
        }
    }

    public byte getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, byte value) {
        genes[index] = value;
    }

    public int size() {
        return genes.length;
    }

    public int getFitness() {
        return calculateFitness();
    }

    public void setDistanceMoved(int distanceMoved) {
        this.distanceMoved = distanceMoved;
    }

    public void setClosestDistance(int closestDistance) {
        this.closestDistance = closestDistance;
    }

    public void setFurthestDistance(int furthestDistance) {
        this.furthestDistance = furthestDistance;
    }

    public void setAverageDistance(int averageDistance) {
        this.averageDistance = averageDistance;
    }

    public void setMaxVelocity(int maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public int getClosestDistance() {
        return closestDistance;
    }

    public int getFurthestDistance() {
        return furthestDistance;
    }

    public int getDistanceMoved() {
        return distanceMoved;
    }

    public int getAverageDistance() {
        return this.averageDistance;
    }

    public int getMaxVelocity() {
        return this.maxVelocity;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public long getTimeAlive() {
        return timeAlive;
    }

    public void setTimeAlive(long timeAlive) {
        this.timeAlive = timeAlive;
    }

    public void setShipHits(int shipHits) {
        this.shipHits = shipHits;
    }

    public int getShipHits() {
        return shipHits;
    }

    public ArrayList<Integer> getDistanceSamples() {
        return this.distanceSamples;
    }

    public void addDistanceSample(int distance) {
        this.distanceSamples.add(distance);
    }

    public int calculateFitness() {
        int fitness = 0;
        fitness += maxDepth;
        fitness += timeAlive / 10;
        fitness += shipHits * 500;
        fitness /= 10;
        return fitness;
    }

    public String getInstructionString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < instructions.size(); i++) {
            Method instruction = instructions.get(i);
            Instruction annotation = instruction.getDeclaredAnnotation(Instruction.class);
            sb.append(annotation.code());
            if (i < instructions.size() - 1) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    public Method getInstruction(int index) {
        if (instructions.isEmpty()) {
            generateInstructions();
        }
        return instructions.get(index);
    }

    public String getGenome() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += getGene(i);
        }
        return geneString;
    }

    private void generateInstructions() {
        List<Integer> indices = getInstructionIndices();
        indices.stream().map((Integer index) -> {
            Method instruction = null;
            try {
                instruction = Alien.class.getDeclaredMethod(instructionMap.get(index));
            } catch (NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(Chromosome.class.getName()).log(Level.SEVERE, null, ex);
            }
            return instruction;
        }).forEachOrdered((instruction) -> {
            instructions.add(instruction);
        });
    }

    public List getInstructionIndices() {
        String genome = getGenome();
        return IntStream.range(0, genome.length() / 3)
                .mapToObj(i -> genome.substring(i *= 3, Math.min(i + 3, genome.length())))
                .map(s -> Integer.parseInt(s, 2))
                .collect(Collectors.toList());
    }


}
