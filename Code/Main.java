package Code;

import java.util.ArrayList;

public class Main {

    private static final int POPULATION_SIZE = 150;
    private static final int GENERATIONS = 200;
    private static final int ELITE_COUNT = 2;
    private static final int TOURNAMENT_SIZE = 5;
    private static final double MUTATION_RATE = 0.15;
    private static final double MUTATION_STRENGTH = 0.08;

    private static final State START_STATE = new State(-20, 20, 270);
    private static final ArrayList<Point> obstacles = new ArrayList<>(); //empty

    public static void main(String[] args) {

        Chromosome[] population = new Chromosome[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = Chromosome.createRandChromosome();
        }

        Chromosome bestEver = population[0];
        //EA loop
        int gen;
        for (gen = 0; gen < GENERATIONS; gen++) {
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

            if (bestEver.getFitness() >= 480.0) {
                System.out.println("\nStopping early.");
                break;
            }

            population = nextGeneration(population);
        }

        System.out.println("\nBest chromosome found after " + gen + " generations");
        System.out.println("Fitness: " + bestEver.getFitness());
        ArrayList<KheperaState> bestStates = runSimulation(bestEver);

        ArrayList<State> posStates = new ArrayList<>();
        for (KheperaState kst : bestStates) {
            posStates.add(kst.position);
        }

        //Visualisation
        Point startPoint = new Point((int) START_STATE.sx, (int) START_STATE.sy);
        Point dummyTarget = new Point(999, 999);
        VisualFrame vis = new VisualFrame(50, 50, 800, 800, obstacles, 1.0, dummyTarget, startPoint, 1.0, 3.0);
        vis.setPath(posStates, "Final Fitness: " + bestEver.getFitness());
        Thread t = new Thread(vis);
        t.start();

    }

    private static double getFitness(Chromosome chromosome) {
        //return FlatFitnessFunction.evaluate(runSimulation(chromosome));
        //return DistanceScaledFitnessFunction.evaluate(runSimulation(chromosome));
        //return CheckpointFitnessFunction.evaluate(runSimulation(chromosome));
        return TimeTrialFitnessFunction.evaluate(runSimulation(chromosome));
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

    private static ArrayList<KheperaState> runSimulation(Chromosome chromosome) { //This will always work
        KheperaSimulator sim = new KheperaSimulator(obstacles, new State(START_STATE.sx, START_STATE.sy, START_STATE.sa));
        return sim.getKheperaState(chromosome.createCommands());
    }

//    private static ArrayList<KheperaState> runSimulation(Chromosome chromosome) { //This doesn't work, but it allows for early stopping
//        KheperaSimulator sim = new KheperaSimulator(obstacles, new State(START_STATE.sx, START_STATE.sy, START_STATE.sa));
//
//        ArrayList<Command> commands = chromosome.createCommands();
//        ArrayList<KheperaState> states = new ArrayList<>();
//
//        int nextCellIdx = 0;
//        int [] clockwiseOrder = {0, 1, 2, 5, 8, 7, 6, 3, 0};
//
//        for (int i = 0; i < commands.size(); i++) {
//
//            //Runs a single command at a time
//            //If final cell reached, remaining commands do nothing
//            ArrayList<Command> OneCommand = new ArrayList<>();
//            OneCommand.add(commands.get(i));
//            ArrayList<KheperaState> step = sim.getKheperaState(OneCommand);
//
//            KheperaState latestCommand = step.getLast();
//            states.add(latestCommand);
//
//            int cellID = FlatFitnessFunction.getBlockID(latestCommand.position.sx, latestCommand.position.sy);
//            if (nextCellIdx < clockwiseOrder.length && cellID == clockwiseOrder[nextCellIdx]) {
//                nextCellIdx++;
//                if (nextCellIdx == clockwiseOrder.length) {
//                    break;
//                }
//            }
//        }
//
//        return states;
//    }
}