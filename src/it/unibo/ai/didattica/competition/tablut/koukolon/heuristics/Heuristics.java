package it.unibo.ai.didattica.competition.tablut.koukolon.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

public abstract class Heuristics {

    protected State state;

    public Heuristics(State state) {
        this.state = state;
    }

    public double evaluateState() {
        return 0;
    }

    /**
     * @return the position of the king
     */
    public int[] kingPosition(State state) {
        //where I saved the int position of the king
        int[] king = new int[2];
        //obtain the board
        State.Pawn[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (state.getPawn(i, j).equalsPawn("K")) {
                    king[0] = i;
                    king[1] = j;
                }
            }
        }
        return king;
    }

    /**
     * @return true if king is on throne, false otherwise
     */
    public boolean isKingInCastle(State state) {
        if (state.getPawn(4, 4).equalsPawn("K"))
            return true;
        else
            return false;
    }

    /**
     * @return the number of near pawns that are target(BLACK or WHITE)
     */
    public int checkNearPawns(State state, int[] position, String target) {
        int count = 0;
        //GET TURN
        State.Pawn[][] board = state.getBoard();
        if (board[position[0] - 1][position[1]].equalsPawn(target))
            count++;
        if (board[position[0] + 1][position[1]].equalsPawn(target))
            count++;
        if (board[position[0]][position[1] - 1].equalsPawn(target))
            count++;
        if (board[position[0]][position[1] + 1].equalsPawn(target))
            count++;
        return count;
    }

    /**
     * @return the positions occupied near the pawn
     */
    protected List<int[]> positionNearPawns(State state, int[] position, String target) {
        List<int[]> occupiedPosition = new ArrayList<int[]>();
        int[] pos = new int[2];
        //GET TURN
        State.Pawn[][] board = state.getBoard();
        if (board[position[0] - 1][position[1]].equalsPawn(target)) {
            pos[0] = position[0] - 1;
            pos[1] = position[1];
            occupiedPosition.add(pos);
        }
        if (board[position[0] + 1][position[1]].equalsPawn(target)) {
            pos[0] = position[0] + 1;
            occupiedPosition.add(pos);
        }
        if (board[position[0]][position[1] - 1].equalsPawn(target)) {
            pos[0] = position[0];
            pos[1] = position[1] - 1;
            occupiedPosition.add(pos);
        }
        if (board[position[0]][position[1] + 1].equalsPawn(target)) {
            pos[0] = position[0];
            pos[1] = position[1] + 1;
            occupiedPosition.add(pos);
        }

        return occupiedPosition;
    }

    /**
     * @return true if king is near, false otherwise
     */
    protected boolean checkNearKing(State state, int[] position) {
        return checkNearPawns(state, position, "K") > 0;
    }

    /**
     * @return how many pawns are in the "strategic" block positions
     */
    protected int getNumberOfBlockedEscape() {
        int count = 0;
        int[][] blockedEscapes = {{1, 1}, {1, 2}, {1, 6}, {1, 7}, {2, 1}, {2, 7}, {6, 1}, {6, 7}, {7, 1}, {7, 2}, {7, 6}, {7, 7}};
        for (int[] position : blockedEscapes) {
            if (state.getPawn(position[0], position[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                count++;
            }
        }
        return count;

    }

    /**
     * @return true if king is on an escape tile, false otherwise
     */
    public boolean hasWhiteWon() {
        int[] posKing = kingPosition(state);
        boolean result;
        result = posKing[0] == 0 || posKing[0] == 8 || posKing[1] == 0 || posKing[1] == 8;
        return result;
    }


    /**
     * @return true if king is on an SAFE position false otherwise
     * SAFE POSITION = The square near the throne were it has no way to win
     */
    public boolean safePositionKing(State state, int[] kingPosition) {
        if (kingPosition[0] > 2 && kingPosition[0] < 6) {
            if (kingPosition[1] > 2 && kingPosition[1] < 6) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if king has some way to win and assign the value of ways to escape
     */
    public boolean kingGoesForWin(State state) {
        int[] kingPosition = this.kingPosition(state);
        int col = 0;
        int row = 0;
        if (!safePositionKing(state, kingPosition)) {
            if ((!(kingPosition[1] > 2 && kingPosition[1] < 6)) && (!(kingPosition[0] > 2 && kingPosition[0] < 6))) {
                //not safe row not safe col
                col = countFreeColumn(state, kingPosition);
                row = countFreeRow(state, kingPosition);
                //System.out.println(col);
            }
            if ((kingPosition[1] > 2 && kingPosition[1] < 6)) {
                // safe row not safe col
                row = countFreeRow(state, kingPosition);
            }
            if ((kingPosition[0] > 2 && kingPosition[0] < 6)) {
                // safe col not safe row
                col = countFreeColumn(state, kingPosition);
            }
            return (col + row > 0);
        }
        return (col + row > 0);
    }

    /**
     * @param state
     * @return number of escapes which king can reach
     */
    public int countWinWays(State state) {
        int[] kingPosition = this.kingPosition(state);
        int col = 0;
        int row = 0;
        if (!safePositionKing(state, kingPosition)) {
            if ((!(kingPosition[1] > 2 && kingPosition[1] < 6)) && (!(kingPosition[0] > 2 && kingPosition[0] < 6))) {
                //not safe row not safe col
                col = countFreeColumn(state, kingPosition);
                row = countFreeRow(state, kingPosition);
            }
            if ((kingPosition[1] > 2 && kingPosition[1] < 6)) {
                // safe row not safe col
                row = countFreeRow(state, kingPosition);
            }
            if ((kingPosition[0] > 2 && kingPosition[0] < 6)) {
                // safe col not safe row
                col = countFreeColumn(state, kingPosition);
            }
            //System.out.println("ROW:"+row);
            //System.out.println("COL:"+col);
            return (col + row);
        }

        return (col + row);

    }

    /**
     * @return number of free rows that a Pawn has
     */
    public int countFreeRow(State state, int[] position) {
        int row = position[0];
        int column = position[1];
        int[] currentPosition = new int[2];
        int freeWays = 0;
        int countRight = 0;
        int countLeft = 0;
        //going right
        for (int i = column + 1; i <= 8; i++) {
            currentPosition[0] = row;
            currentPosition[1] = i;
            if (checkOccupiedPosition(state, currentPosition)) {
                countRight++;
            }
        }
        if (countRight == 0)
            freeWays++;
        //going left
        for (int i = column - 1; i >= 0; i--) {
            currentPosition[0] = row;
            currentPosition[1] = i;
            if (checkOccupiedPosition(state, currentPosition)) {
                countLeft++;
            }
        }
        if (countLeft == 0)
            freeWays++;

        return freeWays;
    }

    /**
     * @return number of free columns
     */
    public int countFreeColumn(State state, int[] position) {
        //lock column
        int row = position[0];
        int column = position[1];
        int[] currentPosition = new int[2];
        int freeWays = 0;
        int countUp = 0;
        int countDown = 0;
        //going down
        for (int i = row + 1; i <= 8; i++) {
            currentPosition[0] = i;
            currentPosition[1] = column;
            if (checkOccupiedPosition(state, currentPosition)) {
                countDown++;
            }
        }
        if (countDown == 0)
            freeWays++;
        //going up
        for (int i = row - 1; i >= 0; i--) {
            currentPosition[0] = i;
            currentPosition[1] = column;
            if (checkOccupiedPosition(state, currentPosition)) {
                countUp++;
            }
        }
        if (countUp == 0)
            freeWays++;

        return freeWays;
    }

    /**
     * @return true if a position is occupied, false otherwise
     */
    public boolean checkOccupiedPosition(State state, int[] position) {
        return !state.getPawn(position[0], position[1]).equals(State.Pawn.EMPTY);
    }

    /**
     * @param state
     * @return number of positions needed to eat king in the current state
     */
    public int getNumEatingPositions(State state) {

        int[] kingPosition = kingPosition(state);

        if (kingPosition[0] == 4 && kingPosition[1] == 4) {
            return 4;
        } else if ((kingPosition[0] == 3 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 3)
                || (kingPosition[0] == 5 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 5)) {
            return 3;
        } else if ((kingPosition[0] == 1 && kingPosition[1] == 3) || (kingPosition[0] == 1 && kingPosition[1] == 5) || (kingPosition[0] == 3 && kingPosition[1] == 1) || (kingPosition[0] == 5 && kingPosition[1] == 1) ||
                (kingPosition[0] == 2 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 2) || (kingPosition[0] == 6 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 6) ||
                (kingPosition[0] == 7 && kingPosition[1] == 3) || (kingPosition[0] == 3 && kingPosition[1] == 7) || (kingPosition[0] == 7 && kingPosition[1] == 5) || (kingPosition[0] == 5 && kingPosition[1] == 7)) {
            /*
                Controlla se il re si trova in una posizione con una citadel alle spalle, quindi basta un pedone per mangiarlo.
             */
            return 1;
        } else {
            return 2;
        }

    }

    	/*
        Funzioni fatte da Fra.
     */

    private int getPawnsOnQuadrant1(int quadrant, String target) {
        int count = 0;
        int row_start = (quadrant / 2) * 5;
        int column_start = (quadrant % 2) * 5;
        State.Pawn[][] board = state.getBoard();

        for(int i = row_start; i < row_start + 3; i++) {
            for(int j = column_start; j < column_start + 3; j++) {
                State.Pawn pawn = board[i][j];
                if(pawn.equalsPawn(target) || (target.equalsIgnoreCase("W") && pawn.equalsPawn("K"))) count++;
            }
        }
        return count;
    }

    private int quadrants[][][] = {
            { // Q0, upper left
                    {0, 0}, {0, 1}, {0, 2}, {0, 3},
                    {1, 0}, {1, 1}, {1, 2}, {1, 3},
                    {2, 0}, {2, 1}, {2, 2}, {2, 3},
                    {3, 0}, {3, 1}, {3, 2}, {3, 3},
            },
            {
                    // Q1, upper right
                    {0, 5}, {0, 6}, {0, 7}, {0, 8},
                    {1, 5}, {1, 6}, {1, 7}, {1, 8},
                    {2, 5}, {2, 6}, {2, 7}, {2, 8},
                    {3, 5}, {3, 6}, {3, 7}, {3, 8},
            },
            { // Q2, lower left
                    {5, 0}, {5, 1}, {5, 2}, {5, 3},
                    {6, 0}, {6, 1}, {6, 2}, {6, 3},
                    {7, 0}, {7, 1}, {7, 2}, {7, 3},
                    {8, 0}, {8, 1}, {8, 2}, {8, 3},
            },
            {
                    // Q3, lower right
                    {5, 5}, {5, 6}, {5, 7}, {5, 8},
                    {6, 5}, {6, 6}, {6, 7}, {6, 8},
                    {7, 5}, {7, 6}, {7, 7}, {7, 8},
                    {8, 5}, {8, 6}, {8, 7}, {8, 8},
            }
    };

    private int crosses[][][] = {
            {{2, 4}, {3, 4}}, // vertical upper
            {{4, 5}, {4, 6}}, // horizontal right
            {{5, 4}, {6, 4}}, // vertical lower
            {{4, 2}, {4, 3}} // horizontal left
    };

    private int getPawnsOnCross(int crossNumber, String target) {
        int result = 0;
        int cross[][] = crosses[crossNumber];
        State.Pawn[][] board = state.getBoard();

        for(int[] position: cross) {
            State.Pawn pawn = board[position[0]][position[1]];
            if(pawn.equalsPawn(target) || (target.equalsIgnoreCase("W") && pawn.equalsPawn("K"))) result++;
        }

        return result;
    }

    private int getPawnsOnQuadrant(int quadrantNumber, String target) {
        int result = 0;
        int quadrant[][] = quadrants[quadrantNumber];
        State.Pawn[][] board = state.getBoard();

        for(int[] position: quadrant) {
            State.Pawn pawn = board[position[0]][position[1]];
            if(pawn.equalsPawn(target) || (target.equalsIgnoreCase("W") && pawn.equalsPawn("K"))) result++;
        }

        return result;
    }

    public int getMostOpenQuadrant(String target) {
        int bestCross = -1;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < 4; i++) {
            int pawnsOnCross = getPawnsOnCross(i, target);
            if(pawnsOnCross < min) {
                bestCross = i;
                min = pawnsOnCross;
            }
        }
        int q1 = bestCross;
        int q2 = (bestCross < 3) ? bestCross + 1 : 0;
        return (getPawnsOnQuadrant(q1, target) <= getPawnsOnQuadrant(q2, target)) ? q1 : q2;

    }
}
