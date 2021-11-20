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
    private final String BLACK_ON_WEAK_SIDE = "blackOnWeakSide";
    private final String WEAK_RHOMBUS_POSITIONS = "weakRhombusPositions";


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

    /*
    First index: quadrant
    Second index: point in quadrant
    Third index: coordinate in point
    */
    private final int[][][] rhombusByQuadrant = {
            {{1, 2}, {2, 1}},
            {{1, 6}, {2, 7}},
            {{6, 1}, {7, 2}},
            {{7, 6}, {6, 7}}
    };

    /*
    First index: quadrant
    Second index: point in quadrant
    Third index: coordinate in point
     */
    private final int[][][] blockPositionsByQuadrant = {
            {{0, 2}, {2, 0}},
            {{0, 6}, {2, 8}},
            {{6, 0}, {8, 2}},
            {{8, 6}, {6, 8}}
    };

    /*
    private final int[][] rhombusNarrow = {
            {2, 3}, {3, 2}, {2, 5}, {5, 2}, {3, 6}, {6, 3}, {6, 5}, {5, 6}
    };
    */
    private final int[][] blockPositions = {
            {0, 2}, {0, 6}, {2, 0}, {6, 0}, {8, 2}, {2, 8}, {8, 6}, {6, 8}
    };

    private final int NUMBER_BLOCK_FORK = 8;
    private final int BLOCKS_PER_QUADRANT = 2;

    private double numberOfBlack;
    private double numberOfWhiteEaten;

    public BlackHeuristics(State state) {

        super(state);
        //Initializing weights
        weights = new HashMap<String, Double>();
        weights.put(BLACK_ALIVE, 35.0);
        weights.put(WHITE_EATEN, 30.0);
        weights.put(BLACK_SURROUND_KING, 50.0);
        /*
            TODO: Aggiungere modifica del peso rispetto al numero di turni
         */
        weights.put(WEAK_RHOMBUS_POSITIONS, 15.0);
        weights.put(WIDE_RHOMBUS_POSITIONS, 10.0);
        //weights.put(NARROW_RHOMBUS_POSITIONS, 2.0);
        // weights.put(BLOCK_FORK_POSITIONS, 10.0);
        // weights.put(BLACK_ON_WEAK_SIDE, 25.0);

        /*
            TODO: EURISTICHE NERE
            TODO: Il nero prima deve coprire le block position dei lati deboli, poi le block position rimanenti
            TODO: se ti può mangiare nella block poisition passa al rombo
            TODO: successivamente deve stringere e passare da block position al rombo largo
            TODO: Nella fase di collocamento al nero NON SERVE mangiare, è molto più importante prendere posizione correttamente
            TODO: Una volta presa posizione il nero deve usare le pedine che non sono in difesa per attaccare (NON QUELLE SUI ROMBI)
            TODO: una strategia efficace è cercare di ridurre i gradi di libertà del re, una volta mangiati tutti gli altri bianchi
         */
        /*
            TODO: EURISTICHE BIANCHE
            TODO: Il bianco deve impedire di prendere le posizioni tattiche, andando a metteresi dietro le diagonali prima che siano chiuse
            TODO: Se vengono chiuse tutte le diagonali il bianco deve cambiare strategia e puntare in ogni modo al pareggio
         */
        /*
            TODO: AGGIUNGERE PAREGGIO
         */

        //Extraction of keys
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }

    /**
     * @return the evaluation of the states using a weighted sum
     */
    @Override
    public double evaluateState() {
        if(state.getTurn().equalsTurn("D")) return -1e6;

        double utilityValue = 0.0;

        //Atomic functions to combine to get utility value through the weighted sum
        numberOfBlack = (double) state.getNumberOf(State.Pawn.BLACK) / GameAshtonTablut.NUM_BLACK;
        numberOfWhiteEaten = (double) (GameAshtonTablut.NUM_WHITE - state.getNumberOf(State.Pawn.WHITE)) / GameAshtonTablut.NUM_WHITE;
        double pawnsNearKing = (double) checkNearPawns(state, kingPosition(state), State.Turn.BLACK.toString()) / getNumEatingPositions(state);
        double numberOfPawnsOnWeakRhombus = (double) getNumberOnRhombus(getMostOpenQuadrant("W")) / BLOCKS_PER_QUADRANT;
        double numberOfPawnsOnWideRhombus = (double) getNumberOnRhombus(rhombusWide) / NUM_TILES_ON_RHOMBUS;
        // double numberOfPawnsBlocking = (double) getNumberOnBlockPositions() / NUMBER_BLOCK_FORK;
        // double numberOfPawnsOnWeakSide = (double)  getNumberOnBlockPositions(getMostOpenQuadrant("W")) / BLOCKS_PER_QUADRANT;

        if (flag) {
            System.out.println("Number of wide rhombus: " + numberOfPawnsOnWeakRhombus);
            //System.out.println("Number of narrow rhombus: " + numberOfPawnsOnNarrowRhombus);
            //System.out.println("Number of blocking pawns: " + numberOfPawnsBlocking);
            //System.out.println("Number of blocking pawns on weak side: " + numberOfPawnsOnWeakSide);
            System.out.println("Number of pawns near to the king:" + pawnsNearKing);
            System.out.println("Number of white pawns eaten: " + numberOfWhiteEaten);
            System.out.println("Black pawns: " + numberOfBlack);
        }


        //Weighted sum of functions to get final utility value
        Map<String, Double> atomicUtilities = new HashMap<String, Double>();
        atomicUtilities.put(BLACK_ALIVE, numberOfBlack);
        atomicUtilities.put(WHITE_EATEN, numberOfWhiteEaten);
        atomicUtilities.put(BLACK_SURROUND_KING, pawnsNearKing);
        atomicUtilities.put(WEAK_RHOMBUS_POSITIONS, numberOfPawnsOnWeakRhombus);
        atomicUtilities.put(WIDE_RHOMBUS_POSITIONS, numberOfPawnsOnWideRhombus);
        // atomicUtilities.put(BLOCK_FORK_POSITIONS, numberOfPawnsBlocking);
        // atomicUtilities.put(BLACK_ON_WEAK_SIDE, numberOfPawnsOnWeakSide);

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
     * @param rhombus
     * @return number of black pawns on tiles if condition is true, 0 otherwise
     */
    public int getNumberOnRhombus(int[][] rhombus) {

        if (state.getNumberOf(State.Pawn.BLACK) >= THRESHOLD) {
            return getValuesOnSpecialCells(rhombus);
        } else {
            return 0;
        }
    }

    public int getNumberOnRhombus(int quadrant) {
        int num = 0;

        for(int[] pos: rhombusByQuadrant[quadrant]) {
            if(state.getPawn(pos[0], pos[1]).equalsPawn(State.Pawn.BLACK.toString())) num++;
        }

        return num;
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

    /**
     * @return number of white pawns on blocking positions
     */
    private int getNumberOnBlockPositions() {

        int num = 0;

        for (int[] pos : blockPositions) {
            if (state.getPawn(pos[0], pos[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                /*
                        Check also if the blocking pawn is in the same board half of the king
                        otherwise it's useless
                        Controlla anche se le righe/colonne sono già occupate, in tal caso non ci si mette
                     */
                if (kingPosition(state)[0] < 4 && pos[0] < 4) {
                    if (pos[0] == 0 && countFreeColumn(state, pos) == 0)
                        num++;
                    else if ((pos[1] == 0 || pos[1] == 8) && countFreeRow(state, pos) == 0)
                        num++;
                } else if (kingPosition(state)[0] > 4 && pos[0] > 4) {
                    if (pos[0] == 8 && countFreeColumn(state, pos) == 0)
                        num++;
                    else if ((pos[1] == 0 || pos[1] == 8)  && countFreeRow(state, pos) == 0)
                        num++;
                } else if (kingPosition(state)[1] < 4 && pos[1] < 4) {
                    if (pos[1] == 0 && countFreeRow(state, pos) == 0)
                        num++;
                    else if ((pos[0] == 0 || pos[0] == 8)  && countFreeColumn(state, pos) == 0)
                        num++;
                } else if (kingPosition(state)[1] > 4 && pos[1] > 4) {
                    if (pos[1] == 8 && countFreeRow(state, pos) == 0)
                        num++;
                    else if ((pos[0] == 0 || pos[0] == 8)  && countFreeColumn(state, pos) == 0)
                        num++;
                }
            }
        }

        return num;
    }

    private int getNumberOnBlockPositions(int quadrant) {
        int num = 0;

        for(int[] pos: blockPositionsByQuadrant[quadrant]) {
            if(state.getPawn(pos[0], pos[1]).equalsPawn(State.Pawn.BLACK.toString())) num++;
        }

        return num;
    }

}
