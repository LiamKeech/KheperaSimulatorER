package Code;

import java.util.ArrayList;

public class FitnessFunction {
    public static final double gridWidth = 30.0; // 60 x 60 grid
    public static final double cellWidth = gridWidth / 3.0; // 3 x 3 cells

    private static final int[] clockwiseOrder = {0, 1, 2, 5, 8, 7, 6, 3, 0};

    private static final double centrePenalty = 25.0;
    private static final double outsidePenalty = 50.0;
    private static final double cellReward = 20.0;
    private static final double traversalReward = 300.0; // (9 x 20) + 300 = 480

    public static int getBlockID(double x, double y) {
        if (x < -gridWidth || x > gridWidth || y < -gridWidth || y > gridWidth) {
            return -1;
        }

        int col;
        if (x < -cellWidth) {
            col = 0;
        } else if (x < cellWidth) {
            col = 1;
        } else {
            col = 2;
        }

        int row;
        if (y > cellWidth) {
            row = 0;
        } else if (y < -cellWidth) {
            row = 2;
        } else {
            row = 1;
        }

        return (row * 3) + col;
    }

    public static double evaluate(ArrayList<KheperaState> states) {
        double fitness = 0.0;
        int nextCellIdx = 0;

        for (int i = 0; i < states.size(); i++) {
            KheperaState state = states.get(i);
            int cellID = getBlockID(state.position.sx, state.position.sy);

            if (cellID == -1) {
                fitness -= outsidePenalty;
            } else if (cellID == 4) {
                fitness -= centrePenalty;
            } else if (nextCellIdx < clockwiseOrder.length && cellID == clockwiseOrder[nextCellIdx]) {
                fitness += cellReward;
                nextCellIdx++;

                if (nextCellIdx == clockwiseOrder.length) {
                    fitness += traversalReward;


                    return fitness;
                }
            }
        }
        return fitness;
    }
}
