package it.unibo.ai.didattica.competition.tablut.koukolon.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.*;

/**
 * Implementation of the black heuristics
 *
 * @author Yuri Noviello
 * @author Francesco Olivo
 * @author Enrico Pallotta
 * @æuthor Flavio Pinzarrone
 *
 */
public class BlackHeuristics extends Heuristics {


    private final String WHITE_EATEN = "numberOfWhiteEaten";
    private final String BLACK_ALIVE = "numberOfBlackAlive";
    private final String BLACK_SURROUND_KING = "blackSurroundKing";
    private final String WEAK_RHOMBUS_POSITIONS = "weakRhombusPositions";

    /* unused strings
    private final String WIDE_RHOMBUS_POSITIONS = "wideRhombusPositions";
    private final String NARROW_RHOMBUS_POSITIONS = "narrowRhombusPositions";
    private final String BLOCK_FORK_POSITIONS = "blockForkPositions";
    private final String BLACK_ON_WEAK_SIDE = "blackOnWeakSide";
     */

    private final int PAWNS_ON_SINGLE_DEFENSE = 2;
    private final int PAWNS_ON_SINGLE_EXTREME_DEFENSE = 4;
    private final int BLOCKS_PER_QUADRANT = 2;

    /* unused values
    private final int THRESHOLD = 10;
    private final int PAWNS_ON_TOTAL_DEFENSE = 8;
    private final int NUMBER_BLOCK_FORK = 8;
     */

    private final Map<String, Double> weights;
    private String[] keys;

    // flag to enable console print
    private boolean flag = false;


    public BlackHeuristics(State state) {

        super(state);

        // initializing weights
        weights = new HashMap<String, Double>();
        weights.put(BLACK_ALIVE, 32.0);
        weights.put(WHITE_EATEN, 35.0);
        weights.put(BLACK_SURROUND_KING, 30.0);
        weights.put(WEAK_RHOMBUS_POSITIONS, 3.0);

        // removed weights
        // weights.put(WIDE_RHOMBUS_POSITIONS, 10.0);
        // weights.put(NARROW_RHOMBUS_POSITIONS, 2.0);
        // weights.put(BLOCK_FORK_POSITIONS, 10.0);
        // weights.put(BLACK_ON_WEAK_SIDE, 25.0);

        // keys extraction
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }

    /**
     * @return the evaluation of the states using a weighted sum
     */
    @Override
    public double evaluateState() {
        if(this.state.getTurn().equalsTurn("D"))
            return -50;

        double utilityValue = 0.0;

        // atomic functions to combine to get utility value through the weighted sum
        double numberOfBlack = (double) state.getNumberOf(State.Pawn.BLACK) / GameAshtonTablut.NUM_BLACK;
        double numberOfWhiteEaten = (double) (GameAshtonTablut.NUM_WHITE - state.getNumberOf(State.Pawn.WHITE)) / GameAshtonTablut.NUM_WHITE;
        double pawnsNearKing = (double) checkNearPawns(getKingPosition(), State.Turn.BLACK.toString()) / getNumEatingPositions();
        double numberOfPawnsOnWeakRhombus = (double) getPawnsOnPosition("B", getWideRhombus(), getMostOpenQuadrant()) / BLOCKS_PER_QUADRANT;

        // unused states
        // double numberOfPawnsOnWideRhombus = (double) getPawnsOnPosition("B", getWideRhombus()) / PAWNS_ON_TOTAL_DEFENSE;
        // double numberOfPawnsOnWeakRhombus = (double) getNumberOnRhombus(getMostOpenQuadrant("W")) / BLOCKS_PER_QUADRANT;
        // double numberOfPawnsOnWideRhombus = (double) getNumberOnRhombus(rhombusWide) / NUM_TILES_ON_RHOMBUS;
        // double numberOfPawnsBlocking = (double) getNumberOnBlockPositions() / NUMBER_BLOCK_FORK;
        // double numberOfPawnsOnWeakSide = (double)  getNumberOnBlockPositions(getMostOpenQuadrant("W")) / BLOCKS_PER_QUADRANT;

        if (flag) {
            System.out.println("Number of pawns near to the king:" + pawnsNearKing);
            System.out.println("Number of white pawns eaten: " + numberOfWhiteEaten);
            System.out.println("Black pawns: " + numberOfBlack);

            // unused prints
            // System.out.println("Number of wide rhombus: " + numberOfPawnsOnWeakRhombus);
            // System.out.println("Number of narrow rhombus: " + numberOfPawnsOnNarrowRhombus);
            // System.out.println("Number of blocking pawns: " + numberOfPawnsBlocking);
            // System.out.println("Number of blocking pawns on weak side: " + numberOfPawnsOnWeakSide);
        }


        // weighted sum of functions to get final utility value
        Map<String, Double> atomicUtilities = new HashMap<String, Double>();
        atomicUtilities.put(BLACK_ALIVE, numberOfBlack);
        atomicUtilities.put(WHITE_EATEN, numberOfWhiteEaten);
        atomicUtilities.put(BLACK_SURROUND_KING, pawnsNearKing);
        atomicUtilities.put(WEAK_RHOMBUS_POSITIONS, numberOfPawnsOnWeakRhombus);

        // unused weights
        // atomicUtilities.put(WIDE_RHOMBUS_POSITIONS, numberOfPawnsOnWideRhombus);
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
     * @return the most open quadrant, according to this logic:
     */
    public int getMostOpenQuadrant() {
        int cross = -1;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < 4; i++) {
            int pawnsOnCross = getPawnsOnPosition("W", getCrosses(), i);
            if(pawnsOnCross < min) {
                cross = i;
                min = pawnsOnCross;
            }
        }
        int previous = cross;
        int next = (cross + 1) % 4;

        return(getPawnsOnPosition("W", getQuadrants(), previous) < getPawnsOnPosition("W", getQuadrants(), next) ? previous : next);
    }

    /**
     *
     * @return the quadrant to defend, according to this logic:
     * If the king is in a quadrant, that is the main quadrant to defend;
     * note that if the king is a quadrant but there is an open way in another quadrant this get closed automatically
     * in order not to lose.
     * If this is the case but the quadrant is already defended then the adjacent ones must be defended, considering
     * the one with fewer whites first.
     * If the king is on a cross then the quadrant to defend is the one with fewer whites among the two.
     * If this is the case but the quadrant is already defended then the second one gets defended, otherwise the
     * most open one among the remaining two.
     * If the king is in the castle the most open quadrant must be defended, and in case it is already defended then
     * the other ones in order of openness.
     */
    public int getQuadrantToDefend() {
        int result = -1;

        if(isKingInPosition(getQuadrants())) {
            int quadrant = getPositionWithKing(getQuadrants());
            if(!isQuadrantDefended(quadrant)) {
                return quadrant;
            } else if(isQuadrantDefended(quadrant)) {
                int previous = (quadrant + 3) % 4;
                int next = (quadrant + 1) % 4;
                if(!isQuadrantDefended(previous) && !isQuadrantDefended(next)) {
                    return (getPawnsOnPosition("B", getQuadrants(), previous) < getPawnsOnPosition("B", getQuadrants(), previous)) ?
                            previous : next;
                }
                else if(!isQuadrantDefended(previous) && isQuadrantDefended(next)) return previous;
                else if(isQuadrantDefended(previous) && !isQuadrantDefended(next)) return next;
                else {
                    return (quadrant + 2) % 4;
                }
            }
        } else if(isKingInPosition(getCrosses())) {
            int cross = getPositionWithKing(getCrosses());
            int previous = cross;
            int next = (cross + 1) % 4;
            if(!isQuadrantDefended(previous) && !isQuadrantDefended(next)) {
                return (getPawnsOnPosition("W", getQuadrants(), previous) < getPawnsOnPosition("W", getQuadrants(), previous)) ?
                        previous : next;
            }
            else if(!isQuadrantDefended(previous) && isQuadrantDefended(next)) return previous;
            else if(isQuadrantDefended(previous) && !isQuadrantDefended(next)) return next;
            else {
                return (getPawnsOnPosition("W", getQuadrants(), (cross + 2) % 4) < getPawnsOnPosition("W", getQuadrants(), (cross + 3) % 4)) ?
                        (cross + 2) % 4 : (cross + 3) % 4;
            }
        } else if(isKingInCastle()) {
            int quadrant = getMostOpenQuadrant();
            if(!isQuadrantDefended(quadrant)) return quadrant;
            else {
                int min = Integer.MAX_VALUE;
                for(int i = 0; i < 4; i++) {
                    if(i != quadrant && getPawnsOnPosition("W", getQuadrants(), i) < min) {
                        min = getPawnsOnPosition("W", getQuadrants(), i);
                        result = i;
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param quadrant: the quadrant to analyze
     * @return true if there is at least one COMPLETE defense, false otherwise
     */
    public boolean isQuadrantDefended(int quadrant) {
        return getPawnsOnPosition("B", getBlockPositions(), quadrant) == PAWNS_ON_SINGLE_DEFENSE
                || getPawnsOnPosition("B", getNarrowRhombus(), quadrant) == PAWNS_ON_SINGLE_DEFENSE
                || getPawnsOnPosition("B", getWideRhombus(), quadrant) == PAWNS_ON_SINGLE_DEFENSE
                || getPawnsOnPosition("B", getExtremeDefenses(), quadrant) == PAWNS_ON_SINGLE_EXTREME_DEFENSE;
    }

}
