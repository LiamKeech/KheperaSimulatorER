package Code;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class CircleGenerator {

    private static final int[] CLOCKWISE_ORDER = {0, 1, 2, 5, 8, 7, 6, 3, 0};
    private static final State START_STATE = new State(-20, 20, 270);

    // Instead of math samples, we define the simulation parameters
    private static final int LEFT_SPEED = 14000;  // Outer wheel
    private static final int RIGHT_SPEED = 6500;  // Inner wheel
    private static final int TIME_STEP = 100;  // Milliseconds per command
    private static final int SIMULATION_STEPS = 150; // How many commands to send (enough to close the loop)

    /**
     * Generates a perfectly smooth circle using the robot's actual kinematics
     * by sending constant, unequal speeds to the left and right wheels.
     */
    public static ArrayList<State> generateKinematicCirclePath(int leftSpeed, int rightSpeed, int timeStep, int steps) {
        KheperaSimulator sim = new KheperaSimulator(START_STATE);
        ArrayList<Command> commands = new ArrayList<>();

        for(int i = 0; i < steps; i++) {
            commands.add(new Command(leftSpeed, rightSpeed, timeStep));
        }

        ArrayList<KheperaState> kStates = sim.getKheperaState(commands);

        String filename = "docs/circle_training_data.csv";
        ArrayList<State> path = new ArrayList<>();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("x,y,left_speed,right_speed,duration");

            for (KheperaState ks : kStates) {
                path.add(ks.position);
                State pos = ks.position;

                writer.printf(Locale.US, "%.4f,%.4f,%d,%d,%d\n",
                        pos.sx, pos.sy, LEFT_SPEED, RIGHT_SPEED, TIME_STEP);
            }

            System.out.println("Total coordinates generated: " + kStates.size());

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        return path;
    }

    public static int[] getVisitedCells(ArrayList<State> path) {
        ArrayList<Integer> visited = new ArrayList<>();
        int lastCell = -1;

        for (State s : path) {
            int cell = GridCellMap.getBlockID(s);
            if (cell != lastCell) {
                visited.add(cell);
                lastCell = cell;
            }
        }
        return visited.stream().mapToInt(i -> i).toArray();
    }

    public static void main(String[] args) {
        // Generate the simulated path
        ArrayList<State> path = generateKinematicCirclePath(LEFT_SPEED, RIGHT_SPEED, TIME_STEP, SIMULATION_STEPS);
        int[] actualOrder = getVisitedCells(path);

        String msg = String.format("Expected: %s\nActual:   %s\nMatch:    %b",
                Arrays.toString(CLOCKWISE_ORDER),
                Arrays.toString(actualOrder),
                Arrays.equals(actualOrder, CLOCKWISE_ORDER));
        System.out.println(msg);

        // One-line headless check
        if (args.length == 0 || !args[0].equalsIgnoreCase("--headless")) {
            VisualFrame vis = new VisualFrame(50, 50, 800, 800, new ArrayList<>(), 1.0,
                    new Point(999, 999),
                    new Point(START_STATE.sx, START_STATE.sy), 1.0, 3.0);
            vis.setPath(path, msg);
            new Thread(vis).start();
        }
    }
}