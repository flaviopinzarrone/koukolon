package it.unibo.ai.didattica.competition.tablut.koukolon.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of general heuristics and utilities
 *
 * @author Yuri Noviello
 * @author Francesco Olivo
 * @author Enrico Pallotta
 * @æuthor Flavio Pinzarrone
 *
 */
public abstract class Heuristics {

    protected State state;

    private final int[][][] quadrants = {
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

    private final int[][][] crosses = {
            {
                    {2, 4},
                    {3, 4}
            }, // vertical upper
            {
                    {4, 5}, {4, 6}
            }, // horizontal right
            {
                    {5, 4},
                    {6, 4}
            }, // vertical lower
            {
                    {4, 2}, {4, 3}
            } // horizontal left
    };

    private final int[][][] blockPositions = {
            {
                            {0, 2},
                    {2, 0}
            },
            {
                    {0, 6},
                            {2, 8}
            },
            {
                    {6, 0},
                            {8, 2}
            },
            {
                            {6, 8},
                    {8, 6}
            }
    };

    private final static int[][][] bestPositions = {
            {
                    {2, 3}
            },
            {
                    {3, 5}
            },
            {
                    {5, 3}
            },
            {
                    {6, 5}
            }
    };

    private final int[][][] narrowRhombus = {
            {
                        {2, 3},
                {3, 2}
            },
            {
                {2, 5},
                        {3, 6}
            },
            {
                {5, 2},
                        {6, 3}
            },
            {
                        {5, 6},
                {6, 5}
            }
    };

    private final int[][][] wideRhombus = {
            {
                            {1, 2},
                    {2, 1}
            },
            {
                    {1, 6},
                            {2, 7}
            },
            {
                    {6, 1},
                            {7, 2}
            },
            {
                            {6, 7},
                    {7, 6}
            }
    };

    private final int[][][] behindNarrowRhombus = {
            {
                    {0, 0}, {0, 1}, {0, 2},
                    {1, 0}, {1, 1}, {1, 2}, {1, 3},
                    {2, 0}, {2, 1}, {2, 2}, {2, 3},
                    {3, 1}, {3, 2}
            },
            {
                    {0, 6}, {0, 7}, {0, 8},
                    {1, 5}, {1, 6}, {1, 7}, {1, 8},
                    {2, 5}, {2, 6}, {2, 7}, {2, 8},
                    {3, 6}, {3, 7}
            },
            {
                    {5, 1}, {5, 2},
                    {6, 0}, {6, 1}, {6, 2}, {6, 3},
                    {7, 0}, {7, 1}, {7, 2}, {7, 3},
                    {8, 0}, {8, 1}, {8, 2}
            },
            {
                    {5, 6}, {5, 7},
                    {6, 5}, {6, 6}, {6, 7}, {6, 8},
                    {7, 5}, {7, 6}, {7, 7}, {7, 8},
                    {8, 6}, {8, 7}, {8, 8}
            }
    };

    private final int[][][] extremeDefenses = {
            {
                            {0, 1}, {0, 2},
                    {1, 0},
                    {2, 0}
            },
            {
                    {0, 6}, {0, 7},
                                    {1, 8},
                                    {2, 8}
            },
            {
                    {6, 0},
                    {7, 0},
                            {8, 1}, {8, 2}
            },
            {
                                    {6, 8},
                                    {7, 8},
                    {8, 6}, {8, 7}
            }
    };

    public int[][][] getQuadrants() {
        return quadrants;
    }

    public int[][][] getCrosses() {
        return crosses;
    }

    public int[][][] getBlockPositions() {
        return blockPositions;
    }

    public int[][][] getNarrowRhombus() {
        return narrowRhombus;
    }

    public int[][][] getWideRhombus() {
        return wideRhombus;
    }

    public int[][][] getBehindNarrowRhombus() {
        return behindNarrowRhombus;
    }

    public int[][][] getExtremeDefenses() {
        return extremeDefenses;
    }

    public static int[][][] getBestPositions() {
        return bestPositions;
    }

    public Heuristics(State state) {
        this.state = state;
    }

    public double evaluateState() {
        return 0;
    }

    /**
     * @return the position of the king
     */
    public int[] getKingPosition() {
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
    public boolean isKingInCastle() {
        return state.getPawn(4, 4).equalsPawn("K");
    }


    /**
     * @return the number of near pawns that are target(BLACK or WHITE)
     */
    public int checkNearPawns(int[] position, String target) {
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
        List<int[]> occupiedPosition = new ArrayList<>();
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

    public boolean isKingInPosition(int[][][] positions) {
        return getPawnsOnPosition("K", positions) > 0;
    }

    public int getPositionWithKing(int[][][] positions) {
        int result = -1;

        for(int i = 0; i < 4; i++) {
            if(getPawnsOnPosition("K", positions, i) > 0) return i;
        }

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
     * @param state
     * @return number of escapes which king can reach
     */
    public int countWinWays(State state) {
        int[] kingPosition = getKingPosition();
        int col = 0;
        int row = 0;
        if (!safePositionKing(state, kingPosition)) {
            if ((!(kingPosition[1] > 2 && kingPosition[1] < 6)) && (!(kingPosition[0] > 2 && kingPosition[0] < 6))) {
                // not safe row not safe col
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
            // System.out.println("ROW:"+row);
            // System.out.println("COL:"+col);

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

        // going right
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
        // lock column
        int row = position[0];
        int column = position[1];
        int[] currentPosition = new int[2];
        int freeWays = 0;
        int countUp = 0;
        int countDown = 0;
        // going down
        for (int i = row + 1; i <= 8; i++) {
            currentPosition[0] = i;
            currentPosition[1] = column;
            if (checkOccupiedPosition(state, currentPosition)) {
                countDown++;
            }
        }
        if (countDown == 0)
            freeWays++;
        // going up
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
     * @return number of positions needed to eat king in the current state
     */
    public int getNumEatingPositions() {

        int[] kingPosition = getKingPosition();

        if (kingPosition[0] == 4 && kingPosition[1] == 4) {
            return 4;
        } else if ((kingPosition[0] == 3 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 3)
                || (kingPosition[0] == 5 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 5)) {
            return 3;
        } else if ((kingPosition[0] == 1 && kingPosition[1] == 3) || (kingPosition[0] == 1 && kingPosition[1] == 5) || (kingPosition[0] == 3 && kingPosition[1] == 1) || (kingPosition[0] == 5 && kingPosition[1] == 1) ||
                (kingPosition[0] == 2 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 2) || (kingPosition[0] == 6 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 6) ||
                (kingPosition[0] == 7 && kingPosition[1] == 3) || (kingPosition[0] == 3 && kingPosition[1] == 7) || (kingPosition[0] == 7 && kingPosition[1] == 5) || (kingPosition[0] == 5 && kingPosition[1] == 7)) {

            // Checks in the king is near to a camp, where only a pawn is needed to eat

            return 1;
        } else {
            return 2;
        }

    }

    /**
     *
     * @param target: the color to count
     * @param positions: the set of positions to analyze
     * @return the number of pawns of target on positions
     */
    public int getPawnsOnPosition(String target, int[][][] positions) {
        int result = 0;

        for(int i = 0; i < 4; i++) {
            result += getPawnsOnPosition(target, positions, i);
        }

        return result;
    }

    /**
     *
     * @param target: the color to count
     * @param positions: the set of positions to analyze
     * @param quadrant: the quadrant to consider
     * @return the number of pawns of target on quadrant of positions
     */
    public int getPawnsOnPosition(String target, int[][][] positions, int quadrant) {
        int result = 0;
        State.Pawn[][] board = state.getBoard();

        for(int[] position: positions[quadrant])
            if(board[position[0]][position[1]].equalsPawn(target)
                || (target.equalsIgnoreCase("W") && board[position[0]][position[1]].equalsPawn("K"))) result++;

        return result;
    }

}
