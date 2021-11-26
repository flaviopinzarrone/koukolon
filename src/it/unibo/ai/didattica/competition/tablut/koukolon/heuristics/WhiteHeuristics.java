package it.unibo.ai.didattica.competition.tablut.koukolon.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the white heuristics
 *
 * @author Yuri Noviello
 * @author Francesco Olivo
 * @author Enrico Pallotta
 * @æuthor Flavio Pinzarrone
 *
 */
public class WhiteHeuristics extends Heuristics {

    private final String BEST_POSITIONS = "bestPositions";
    private final String BLACK_EATEN = "numberOfBlackEaten";
    private final String WHITE_ALIVE = "numberOfWhiteAlive";
    private final String NUM_ESCAPES_KING = "numberOfWinEscapesKing";
    private final String BLACK_SURROUND_KING = "blackSurroundKing";
    private final String PROTECTION_KING = "protectionKing";

    // unused strings
    // private final String BLOCK_CITIZENS = "blockingCitizens";
    // private final String ANGLE_POSITIONS = "anglePositions";

    // threshold used to decide whether to use best positions configuration
    private final static int THRESHOLD_BEST = 2;

    private final static int NUM_BEST_POSITION = 4;

    private Map<String,Double> weights;
    private Map<String,Double> values;
    private String[] keys;

    // flag to enable console print
    private boolean flag = false;

    public WhiteHeuristics(State state) {

        super(state);

        //Initializing weights
        weights = new HashMap<String,Double>();
        //Positions which are the best moves at the beginning of the game
        weights.put(BEST_POSITIONS, 2.0);
        weights.put(BLACK_EATEN, 20.0);
        weights.put(WHITE_ALIVE, 40.0);
        weights.put(NUM_ESCAPES_KING, 18.0);
        weights.put(BLACK_SURROUND_KING, 7.0);
        weights.put(PROTECTION_KING, 18.0);
        //weights.put(BLOCK_CITIZENS, 10.0);


        //Extraction of keys
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }
    /**
     *
     * @return the evaluation of the states using a weighted sum
     */
    @Override
    public double evaluateState() {
        double utilityValue = 0;
        if(this.state.getTurn().equalsTurn("D"))
            utilityValue += 100;

        //Atomic functions to combine to get utility value through the weighted sum
        double bestPositions = (double) getPawnsOnPosition("W", getBestPositions()) / NUM_BEST_POSITION;
        double numberOfWhiteAlive =  (double)(state.getNumberOf(State.Pawn.WHITE)) / GameAshtonTablut.NUM_WHITE;
        double numberOfBlackEaten = (double)(GameAshtonTablut.NUM_BLACK - state.getNumberOf(State.Pawn.BLACK))
                / GameAshtonTablut.NUM_BLACK;
        double blackSurroundKing = (double)(getNumEatingPositions() - checkNearPawns(getKingPosition(),
                State.Turn.BLACK.toString())) / getNumEatingPositions();
        double protectionKing = protectionKing();

        int numberWinWays = countWinWays(state);
        double numberOfWinEscapesKing = numberWinWays>1 ? (double)countWinWays(state)/4 : 0.0;

        if(flag){
            System.out.println("Number of white alive: " + numberOfWhiteAlive);
            System.out.println("Number of white pawns in best positions " + bestPositions);
            System.out.println("Number of escapes: " + numberOfWinEscapesKing);
            System.out.println("Number of black surrounding king: " + blackSurroundKing);
        }

        Map<String, Double> values = new HashMap<String, Double>();
        values.put(BEST_POSITIONS, bestPositions);
        values.put(WHITE_ALIVE, numberOfWhiteAlive);
        values.put(BLACK_EATEN, numberOfBlackEaten);
        values.put(NUM_ESCAPES_KING,numberOfWinEscapesKing);
        values.put(BLACK_SURROUND_KING,blackSurroundKing);
        values.put(PROTECTION_KING,protectionKing);
        // values.put(ANGLE_POSITIONS, anglePositions);

        for (int i=0; i < weights.size(); i++){
            utilityValue += weights.get(keys[i]) * values.get(keys[i]);
            if(flag){
                System.out.println(keys[i] + ":  "+ weights.get(keys[i]) + " * " + values.get(keys[i]) +
                        " = " + weights.get(keys[i]) * values.get(keys[i]));
            }
        }

        return utilityValue;
    }


    /**
     *
     * @return value according to the protection level of the king whether an enemy pawn is next to it
     */
    private double protectionKing(){

        // Vvlues whether there is only a white pawn near to the king
        final double VAL_NEAR = 0.6;
        final double VAL_TOT = 1.0;

        double result = 0.0;

        int[] kingPos = getKingPosition();
        // pawns near to the king
        ArrayList<int[]> pawnsPositions = (ArrayList<int[]>) positionNearPawns(state,kingPos,State.Pawn.BLACK.toString());

        // there is a black pawn that threatens the king and 2 pawns are enough to eat the king
        if (pawnsPositions.size() == 1 && getNumEatingPositions() == 2){
            int[] enemyPos = pawnsPositions.get(0);
            // used to store other position from where king could be eaten
            int[] targetPosition = new int[2];
            // enemy right to the king
            if(enemyPos[0] == kingPos[0] && enemyPos[1] == kingPos[1] + 1){
                // left to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0];
                targetPosition[1] = kingPos[1] - 1;
                if (state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            // enemy left to the king
            }else if(enemyPos[0] == kingPos[0] && enemyPos[1] == kingPos[1] -1){
                // right to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0];
                targetPosition[1] = kingPos[1] + 1;
                if(state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            // enemy up to the king
            }else if(enemyPos[1] == kingPos[1] && enemyPos[0] == kingPos[0] - 1){
                // down to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0] + 1;
                targetPosition[1] = kingPos[1];
                if(state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            // enemy down to the king
            }else{
                // up there is a white pawn and king is protected
                targetPosition[0] = kingPos[0] - 1;
                targetPosition[1] = kingPos[1];
                if(state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            }

            // considering whites to use as barriers for the target pawn
            double otherPoints = VAL_TOT - VAL_NEAR;
            double contributionPerN = 0.0;

            //whether it is better to keep free the position
            if (targetPosition[0] == 0 || targetPosition[0] == 8 || targetPosition[1] == 0 || targetPosition[1] == 8){
                if(state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.EMPTY.toString())){
                    result = 1.0;
                } else {
                    result = 0.0;
                }
            }else{
                // considering a reduced number of neighbours whether target is near to citadels or throne
                if (targetPosition[0] == 4 && targetPosition[1] == 2 || targetPosition[0] == 4 && targetPosition[1] == 6
                        || targetPosition[0] == 2 && targetPosition[1] == 4 || targetPosition[0] == 6 && targetPosition[1] == 4
                        || targetPosition[0] == 3 && targetPosition[1] == 4 || targetPosition[0] == 5 && targetPosition[1] == 4
                        || targetPosition[0] == 4 && targetPosition[1] == 3 || targetPosition[0] == 4 && targetPosition[1] == 5){
                    contributionPerN = otherPoints / 2;
                }else{
                    contributionPerN = otherPoints / 3;
                }

                result += contributionPerN * checkNearPawns(targetPosition,State.Pawn.WHITE.toString());
            }

        }
        return result;
    }

    /**
     *
     * @return the quadrant to block if exists, else -1
     */
    private int getQuadrantToBlock() {

        for(int i = 0; i < 4; i++) {
            if(getPawnsOnPosition("B", getNarrowRhombus()) == 1 && getPawnsOnPosition("W", getBehindNarrowRhombus()) == 0) {
                return i;
            }
        }

        return -1;
    }
}
