package Code;

import java.util.ArrayList;

public class Main {


    private static final int POPULATION_SIZE = 50;
    private static final int GENERATIONS = 200;
    private static final int ELITE_COUNT = 2;
    private static final int TOURNAMENT_SIZE = 5;
    private static final double MUTATION_RATE = 0.15;
    private static final double MUTATION_STRENGTH = 0.08;

    private static final State START_STATE = new State(-20, 20, 270);
    private static ArrayList<Point> obstacles = new ArrayList<>(); //empty

    public static void main(String[] args) {

        Chromosome[] population = new Chromosome[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = Chromosome.createRandChromosome();
        }

        Chromosome bestEver = population[0];
        //EA loop
        for (int gen = 0; gen < GENERATIONS; gen++) {
            for (Chromosome chromosome : population) {
                double fitness = getFitness(chromosome);
                chromosome.setFitness(fitness);
            }

            EvolutionaryAlgorithm.sortByFitness(population);
            Chromosome best = population[0];
            if (bestEver == null || best.getFitness() > bestEver.getFitness()) {
                bestEver = best;
            }

            System.out.printf("Generation %d, Best fitness: %.2f, All-time best: %.2f%n", gen, best.getFitness(), bestEver.getFitness());

            if (bestEver.getFitness() >= 280.0) {
                System.out.println("\nStopping early.");
                break;
            }

            population = nextGeneration(population);
        }

        System.out.println("\nBest chromosome found after " + GENERATIONS + " generations");
        System.out.println("Fitness: " + bestEver.getFitness());
        ArrayList<KheperaState> bestStates = runSimulation(bestEver);
        GridVisualiser.show(bestStates, bestEver.getFitness());

    }

    private static double getFitness(Chromosome chromosome) {
        return FitnessFunction.evaluate(runSimulation(chromosome));
    }

    private static ArrayList<KheperaState> runSimulation(Chromosome chromosome) {
        KheperaSimulator sim = new KheperaSimulator(obstacles, new State(START_STATE.sx, START_STATE.sy, START_STATE.sa));
        return sim.getKheperaState(chromosome.createCommands());
    }

    private static Chromosome[] nextGeneration(Chromosome[] current) {
        Chromosome[] next = new Chromosome[POPULATION_SIZE];

        // Elitism
        Chromosome[] elites = EvolutionaryAlgorithm.elitism(current, ELITE_COUNT);
        System.arraycopy(elites, 0, next, 0, ELITE_COUNT);

        for (int i = ELITE_COUNT; i < POPULATION_SIZE; i++) {
            //Selection
            Chromosome parent1 = EvolutionaryAlgorithm.tournamentSelect(current, TOURNAMENT_SIZE);
            Chromosome parent2 = EvolutionaryAlgorithm.tournamentSelect(current, TOURNAMENT_SIZE);
            //Crossover
            Chromosome offspring = EvolutionaryAlgorithm.crossover(parent1, parent2);
            //Mutation
            EvolutionaryAlgorithm.GaussianMutation(offspring, MUTATION_RATE, MUTATION_STRENGTH);
            next[i] = offspring;
        }

        return next;
    }
}