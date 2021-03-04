package edu.umsl;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EvolutionEngine {

    private static final double MUTATION_RATE = 0.15;

    public List<Alien> createNewAliens(int alienCount, Ship targetShip, int maxHorizontalBounds) {
        List<Chromosome> chromosomes = createChromosomes(alienCount, true);
        return createNewAliens(chromosomes, targetShip, maxHorizontalBounds);
    }
    private List<Alien> createNewAliens(List<Chromosome> alienChromosomes, Ship targetShip, int maxHorizontalBounds) {
        List<Alien> aliens = new LinkedList<>();
        for (int i = 0; i < alienChromosomes.size(); i++) {
            Alien alien = new Alien(alienChromosomes.get(i), targetShip, maxHorizontalBounds);
            aliens.add(alien);
        }
        return aliens;
    }
    public List<Alien> evolveAliens(List<Alien> lastGenAliens, Ship targetShip, int maxHorizontalBounds) {
        List<Chromosome> alienChromosomes = getAlienChromosomes(lastGenAliens);
        List<Chromosome> evolvedAlienChromosomes = evolve(alienChromosomes);
        return createNewAliens(evolvedAlienChromosomes, targetShip, maxHorizontalBounds);
    }
    private List<Chromosome> getAlienChromosomes(List<Alien> aliens) {
        List<Chromosome> alienChromosomes = new LinkedList<>();
        for (Alien alien : aliens) {
            alienChromosomes.add(alien.getChromosome());
        }
        return alienChromosomes;
    }

    private List<Chromosome> evolve(List<Chromosome> chromosomes) {

        List<Chromosome> newChromosomes = null;

        if (chromosomes.size() >= 2) {

            newChromosomes = createChromosomes(chromosomes.size(), false);

            List<Chromosome> sortedChromosomes = sortChromosomesByFitness(chromosomes);

            StringBuilder ranking = new StringBuilder();
            ranking.append("****** Rankings ******\n");
            sortedChromosomes.forEach((chromosome) -> {
                ranking.append("Chromosome: " + chromosome.hashCode() + ", ");
                ranking.append("Fitness: " + Integer.toString(chromosome.getFitness()) + "\n");
            });
            ranking.append("**********************\n");
            Logger.getLogger(EvolutionEngine.class.getName()).log(Level.FINE, "\n" + ranking.toString());

            Chromosome fittest = sortedChromosomes.get(0);
            newChromosomes.add(0, fittest);

            Chromosome secondFittest = sortedChromosomes.get(1);

            for (int i = 1; i < chromosomes.size(); i++) {
                Chromosome newChromosome = crossover(fittest, secondFittest);
                newChromosomes.add(i, newChromosome);
            }

            for (int i = 1; i < newChromosomes.size(); i++) {
                mutate(newChromosomes.get(i));
            }

        }

        return newChromosomes;
    }

    private Chromosome crossover(Chromosome chromosome1, Chromosome chromosome2) {
        Chromosome crossedChromosome = new Chromosome();

        int crossoverPoint = new Random().nextInt(crossedChromosome.size());

        for (int i = 0; i < crossoverPoint; i++) {
            crossedChromosome.setGene(i, chromosome1.getGene(i));
        }

        for (int i = crossoverPoint; i < crossedChromosome.size() - 1; i++) {
            crossedChromosome.setGene(i, chromosome1.getGene(i));
        }

        return crossedChromosome;
    }

    private void mutate(Chromosome chromosome) {

        int mutationPoint = new Random().nextInt(chromosome.size());

        for (int i = 0; i < chromosome.size(); i++) {
            if (Math.random() <= MUTATION_RATE) {
                if (chromosome.getGene(i) == 0) {
                    chromosome.setGene(i, (byte) 1);
                } else {
                    chromosome.setGene(i, (byte) 0);
                }
            }
        }
    }
    
    private List<Chromosome> createChromosomes(int populationSize, boolean initialize) {
        LinkedList<Chromosome> chromosomes = new LinkedList<>();
        if (initialize) {
            for (int i = 0; i < populationSize; i++) {
                Chromosome newChromosome = new Chromosome();
                newChromosome.generateChromosome();
                chromosomes.add(i, newChromosome);
            }
        }
        return chromosomes;
    }

    public Alien getFittestAlien(List<Alien> aliens) {
        Alien fittestAlien = aliens.get(0);
        for (int i = 0; i < aliens.size(); i++) {
            if (fittestAlien.getChromosome().getFitness() <= aliens.get(i).getChromosome().getFitness()) {
                fittestAlien = aliens.get(i);
            }
        }
        return fittestAlien;
    }

    private Chromosome getFittest(List<Chromosome> chromosomes) {
        Chromosome fittest = chromosomes.get(0);
        for (int i = 0; i < chromosomes.size(); i++) {
            if (fittest.getFitness() <= chromosomes.get(i).getFitness()) {
                fittest = chromosomes.get(i);
            }
        }
        return fittest;
    }

    private List<Chromosome> sortChromosomesByFitness(List<Chromosome> chromosomes) {
        return new LinkedList<>(chromosomes).stream()
                .sorted(Comparator.comparing(Chromosome::getFitness).reversed())
                .collect(Collectors.toList());
    }

}
