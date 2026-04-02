package Code;

import java.util.ArrayList;
import java.util.Random;

public class Chromosome {

    public static final int numberOfCommands = 25;

    public static final int MIN_SPEED = 8000;
    public static final int MAX_SPEED = 17500;
    public static final int MIN_TIME = 300;
    public static final int MAX_TIME = 3100;

    private final double[] genes = new double[numberOfCommands * 3];
    private double fitness = Double.NEGATIVE_INFINITY;

    public static Chromosome createRandChromosome() {
        Chromosome chromosome = new Chromosome();
        Random rand = new Random();

        for (int i = 0; i < chromosome.genes.length; i++) {
            chromosome.genes[i] = rand.nextDouble();
        }

        return chromosome;
    }

    private int decodeGene(double gene, int min, int max) {
        double validGene = Math.max(0.0, Math.min(1.0, gene));
        return min + (int) (validGene * (max - min));
    }

    public ArrayList<Command> createCommands() {
        ArrayList<Command> commands = new ArrayList<>(numberOfCommands);

        for (int i = 0; i < genes.length; i += 3) {
            int leftSpeed = decodeGene(genes[i], MIN_SPEED, MAX_SPEED);
            int rightSpeeed = decodeGene(genes[i + 1], MIN_SPEED, MAX_SPEED);
            int duration = decodeGene(genes[i + 2], MIN_TIME, MAX_TIME);

            commands.add(new Command(leftSpeed, rightSpeeed, duration));
        }
        return commands;
    }

    public double[] getGenes() {
        return genes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}