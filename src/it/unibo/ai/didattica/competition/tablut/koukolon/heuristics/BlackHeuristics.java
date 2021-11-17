package it.unibo.ai.didattica.competition.tablut.koukolon.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.*;

public class BlackHeuristics extends Heuristics {

    private final String WIDE_RHOMBUS_POSITIONS = "wideRhombusPositions";
    private final String NARROW_RHOMBUS_POSITIONS = "narrowRhombusPositions";
    private final String BLOCK_FORK_POSITIONS = "blockForkPositions";
    private final String WHITE_EATEN = "numberOfWhiteEaten";
    private final String BLACK_ALIVE = "numberOfBlackAlive";
    private final String BLACK_SURROUND_KING = "blackSurroundKing";

    //Threshold used to decide whether to use rhombus configuration
    private final int THRESHOLD = 10;
    //Number of tiles on rhombus
    private final int NUM_TILES_ON_RHOMBUS = 8;

    private final Map<String, Double> weights;
    private String[] keys;

    //Flag to enable console print
    private boolean flag = false;

    //Matrix of favourite black positions in initial stages and to block the escape ways
    private final int[][] rhombusWide = {
            {1, 2}, {1, 6},
            {2, 1}, {2, 7},

            {6, 1}, {6, 7},
            {7, 2}, {7, 6}
    };

    private final int[][] rhombusNarrow = {
            {2, 3}, {3, 2}, {2, 5}, {5, 2}, {3, 6}, {6, 3}, {6, 5}, {5, 6}
    };

    private final int[][] blockFork = {
            {0, 2}, {0, 6}, {2, 0}, {6, 0}, {8, 2}, {2, 8}, {8, 6}, {6, 8}
    };

    private final int NUMBER_BLOCK_FORK = 8;

    private double numberOfBlack;
    private double numberOfWhiteEaten;

    public BlackHeuristics(State state) {

        super(state);
        //Initializing weights
        weights = new HashMap<String, Double>();
        weights.put(BLACK_ALIVE, 35.0);
        weights.put(WHITE_EATEN, 48.0);
        weights.put(BLACK_SURROUND_KING, 15.0);
        weights.put(WIDE_RHOMBUS_POSITIONS, 2.0);
        weights.put(NARROW_RHOMBUS_POSITIONS, 2.0);
        weights.put(BLOCK_FORK_POSITIONS, 2.0);

        //Extraction of keys
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }

    /**
     * @return the evaluation of the states using a weighted sum
     */
    @Override
    public double evaluateState() {

        double utilityValue = 0.0;

        //Atomic functions to combine to get utility value through the weighted sum
        numberOfBlack = (double) state.getNumberOf(State.Pawn.BLACK) / GameAshtonTablut.NUM_BLACK;
        numberOfWhiteEaten = (double) (GameAshtonTablut.NUM_WHITE - state.getNumberOf(State.Pawn.WHITE)) / GameAshtonTablut.NUM_WHITE;
        double pawnsNearKing = (double) checkNearPawns(state, kingPosition(state), State.Turn.BLACK.toString()) / getNumEatingPositions(state);
        double numberOfPawnsOnWideRhombus = (double) getNumberOnRhombus(rhombusWide) / NUM_TILES_ON_RHOMBUS;
        double numberOfPawnsOnNarrowRhombus = (double) getNumberOnRhombus(rhombusNarrow) / NUM_TILES_ON_RHOMBUS;
        double numberOfPawnsBlocking = (double) getValuesOnSpecialCells(blockFork) / NUMBER_BLOCK_FORK;
        if (flag) {
            System.out.println("Number of wide rhombus: " + numberOfPawnsOnWideRhombus);
            System.out.println("Number of narrow rhombus: " + numberOfPawnsOnNarrowRhombus);
            System.out.println("Number of blocking pawns: " + numberOfPawnsBlocking);
            System.out.println("Number of pawns near to the king:" + pawnsNearKing);
            System.out.println("Number of white pawns eaten: " + numberOfWhiteEaten);
            System.out.println("Black pawns: " + numberOfBlack);
        }


        //Weighted sum of functions to get final utility value
        Map<String, Double> atomicUtilities = new HashMap<String, Double>();
        atomicUtilities.put(BLACK_ALIVE, numberOfBlack);
        atomicUtilities.put(WHITE_EATEN, numberOfWhiteEaten);
        atomicUtilities.put(BLACK_SURROUND_KING, pawnsNearKing);
        atomicUtilities.put(WIDE_RHOMBUS_POSITIONS, numberOfPawnsOnWideRhombus);
        atomicUtilities.put(NARROW_RHOMBUS_POSITIONS, numberOfPawnsOnNarrowRhombus);
        atomicUtilities.put(BLOCK_FORK_POSITIONS, numberOfPawnsBlocking);

        for (int i = 0; i < weights.size(); i++) {
            utilityValue += weights.get(keys[i]) * atomicUtilities.get(keys[i]);
            if (flag) {
                System.out.println(keys[i] + ": " +
                        weights.get(keys[i]) + "*" +
                        atomicUtilities.get(keys[i]) +
                        "= " + weights.get(keys[i]) * atomicUtilities.get(keys[i]));
            }
        }

        return utilityValue;

    }


    /**
     * @return number of black pawns on tiles if condition is true, 0 otherwise
     * @param rhombus
     */
    public int getNumberOnRhombus(int[][] rhombus) {

        if (state.getNumberOf(State.Pawn.BLACK) >= THRESHOLD) {
            return getValuesOnSpecialCells(rhombus);
        } else {
            return 0;
        }
    }

    /**
     * @return number of black pawns on special cell configuration
     */
    public int getValuesOnSpecialCells(int[][] cells) {

        int count = 0;
        for (int[] position : cells) {
            if (state.getPawn(position[0], position[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                count++;
            }
        }
        return count;

    }

}
