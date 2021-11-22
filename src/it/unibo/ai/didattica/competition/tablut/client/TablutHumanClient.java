package it.unibo.ai.didattica.competition.tablut.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

/**
 * 
 * @author A. Piretti, Andrea Galassi
 *
 */
public class TablutHumanClient extends TablutClient {

	public TablutHumanClient(String player) throws UnknownHostException, IOException {
		super(player, "humanInterface");
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

		if (args.length == 0) {
			System.out.println("You must specify which player you are (WHITE or BLACK)!");
			System.exit(-1);
		}
		System.out.println("Selected this: " + args[0]);

		TablutClient client = new TablutHumanClient(args[0]);

		client.run();

	}

	@Override
	public void run() {
		System.out.println("You are player " + this.getPlayer().toString() + "!");
		String actionStringFrom = "";
		String actionStringTo = "";
		Action action;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (this.getPlayer() == Turn.WHITE) {
			System.out.println("You are player " + this.getPlayer().toString() + "!");
			while (true) {
				try {
					this.read();


					System.out.println("Current state:");
					System.out.println(this.getCurrentState().toString());

					printStatus();

					if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
						System.out.println("Player " + this.getPlayer().toString() + ", do your move: ");
						System.out.println("From: ");
						actionStringFrom = in.readLine();
						System.out.println("To: ");
						actionStringTo = in.readLine();
						action = new Action(actionStringFrom, actionStringTo, this.getPlayer());
						this.write(action);

					} else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
						System.out.println("Waiting for your opponent move... ");
					} else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITEWIN)) {
						System.out.println("YOU WIN!");
						System.exit(0);
					} else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACKWIN)) {
						System.out.println("YOU LOSE!");
						System.exit(0);
					} else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.DRAW)) {
						System.out.println("DRAW!");
						System.exit(0);
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		} else {
			System.out.println("You are player " + this.getPlayer().toString() + "!");
			while (true) {
				try {
					this.read();
					System.out.println("Current state:");
					System.out.println(this.getCurrentState().toString());

					printStatus();

					if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
						System.out.println("Player " + this.getPlayer().toString() + ", do your move: ");
						System.out.println("From: ");
						actionStringFrom = in.readLine();
						System.out.println("To: ");
						actionStringTo = in.readLine();
						action = new Action(actionStringFrom, actionStringTo, this.getPlayer());
						this.write(action);
					} else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
						System.out.println("Waiting for your opponent move... ");
					} else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITEWIN)) {
						System.out.println("YOU LOSE!");
						System.exit(0);
					} else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACKWIN)) {
						System.out.println("YOU WIN!");
						System.exit(0);
					} else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.DRAW)) {
						System.out.println("DRAW!");
						System.exit(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	    	/*
        Funzioni fatte da Fra.
     */

	private int getPawnsOnQuadrant1(int quadrant, String target) {
		int count = 0;
		int row_start = (quadrant / 2) * 5;
		int column_start = (quadrant % 2) * 5;
		State.Pawn[][] board = getCurrentState().getBoard();

		for(int i = row_start; i < row_start + 3; i++) {
			for(int j = column_start; j < column_start + 3; j++) {
				State.Pawn pawn = board[i][j];
				if(pawn.equalsPawn(target) || (target.equalsIgnoreCase("W") && pawn.equalsPawn("K"))) count++;
			}
		}
		return count;
	}

//	private int quadrants[][][] = {
//			{ // Q0, upper left
//					{0, 0}, {0, 1}, {0, 2}, {0, 3},
//					{1, 0}, {1, 1}, {1, 2}, {1, 3},
//					{2, 0}, {2, 1}, {2, 2}, {2, 3},
//					{3, 0}, {3, 1}, {3, 2}, {3, 3},
//			},
//			{
//					// Q1, upper right
//					{0, 5}, {0, 6}, {0, 7}, {0, 8},
//					{1, 5}, {1, 6}, {1, 7}, {1, 8},
//					{2, 5}, {2, 6}, {2, 7}, {2, 8},
//					{3, 5}, {3, 6}, {3, 7}, {3, 8},
//			},
//			{ // Q2, lower left
//					{5, 0}, {5, 1}, {5, 2}, {5, 3},
//					{6, 0}, {6, 1}, {6, 2}, {6, 3},
//					{7, 0}, {7, 1}, {7, 2}, {7, 3},
//					{8, 0}, {8, 1}, {8, 2}, {8, 3},
//			},
//			{
//					// Q3, lower right
//					{5, 5}, {5, 6}, {5, 7}, {5, 8},
//					{6, 5}, {6, 6}, {6, 7}, {6, 8},
//					{7, 5}, {7, 6}, {7, 7}, {7, 8},
//					{8, 5}, {8, 6}, {8, 7}, {8, 8},
//			}
//	};
//
//	private int crosses[][][] = {
//			{{2, 4}, {3, 4}}, // vertical upper
//			{{4, 5}, {4, 6}}, // horizontal right
//			{{5, 4}, {6, 4}}, // vertical lower
//			{{4, 2}, {4, 3}} // horizontal left
//	};


	private int getPawnsOnQuadrant(int quadrantNumber, String target) {
		int result = 0;
		int quadrant[][] = quadrants[quadrantNumber];
		State.Pawn[][] board = getCurrentState().getBoard();

		for(int[] position: quadrant) {
			State.Pawn pawn = board[position[0]][position[1]];
			if(pawn.equalsPawn(target) || (target.equalsIgnoreCase("W") && pawn.equalsPawn("K"))) result++;
		}

		return result;
	}

	/**
	 *
	 * @return the most open quadrant, according to this logic:
	 */
	public int getMostOpenQuadrant() {
		int bestCross = -1;
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < 4; i++) {
			int pawnsOnCross = getPawnsOnPosition("W", getCrosses(), i);
			if(pawnsOnCross < min) {
				bestCross = i;
				min = pawnsOnCross;
			}
		}
		int q1 = bestCross;
		int q2 = (bestCross < 3) ? bestCross + 1 : 0;

		return(getPawnsOnPosition("W", getQuadrants(), q1) <= getPawnsOnPosition("W", getQuadrants(), q2) ? q1 : q2);
	}

	/**
	 *
	 * @param target: the color to count
	 * @param positions: the set of positions to analyze
	 * @return the number of pawns of target on positions
	 */
	public int getPawnsOnPosition(String target, int[][][] positions) {
		int result = 0;
		State.Pawn[][] board = getCurrentState().getBoard();

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
		State.Pawn[][] board = getCurrentState().getBoard();

		for(int[] position: positions[quadrant])
			if(board[position[0]][position[1]].equalsPawn(target)
					|| (target.equalsIgnoreCase("W") && board[position[0]][position[1]].equalsPawn("K"))) result++;

		return result;
	}


	/**
	 *
	 * @return the quadrant to block if exists, else -1
	 */
	private int getQuadrantToBlock() {

		for(int i = 0; i < 4; i++) {
			System.out.println("B on nr: " + getPawnsOnPosition("B", getNarrowRhombus(), i) + ", W bnr: " + (getPawnsOnPosition("W", getBehindNarrowRhombus(), i)));
			if((getPawnsOnPosition("B", getNarrowRhombus(), i) == 1) && (getPawnsOnPosition("W", getBehindNarrowRhombus(), i) == 0)) {
				System.out.println("Quadrant " + i + " must be blocked!");
				return i;
			}
		}

		return -1;
	}

	private int[][][] quadrants = {
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

	public void printStatus() {
		for(int i = 0; i < 4; i++) {
			System.out.println("QUADRANT " + i);
			System.out.println("=======================================================================================");
			System.out.println("Number of W pawns on quadrant " + i + ": " + getPawnsOnPosition("W", getQuadrants(), i));
			System.out.println("Number of W pawns on cross " + i + ": " + getPawnsOnPosition("W", getCrosses(), i));
			System.out.println("Number of B pawns on cross " + i + ": " + getPawnsOnPosition("B", getCrosses(), i));
			System.out.println("Number of W pawns on block positions " + i + ": " + getPawnsOnPosition("W", getBlockPositions(), i));
			System.out.println("Number of B pawns on narrow rhombus  " + i + ": " + getPawnsOnPosition("B", getNarrowRhombus(), i));
			System.out.println("Number of B pawns on wide rhombus " + i + ": " + getPawnsOnPosition("B", getWideRhombus(), i));
			System.out.println("Number of W pawns behind narrow rhombus " + i + ": " + getPawnsOnPosition("W", getBehindNarrowRhombus(), i));
			System.out.println("=======================================================================================" + System.lineSeparator());
		}
		System.out.println("OVERALL");
		System.out.println("=======================================================================================");
		System.out.println("Most open quadrant: " + getMostOpenQuadrant());
		System.out.println("Quadrant to block: " + getQuadrantToBlock());
		System.out.println("Total number of W pawns on quadrants: " + getPawnsOnPosition("W", getQuadrants()));
		System.out.println("Total number of W pawns on cross: " + getPawnsOnPosition("W", getCrosses()));
		System.out.println("Total number of B pawns on cross: " + getPawnsOnPosition("B", getCrosses()));
		System.out.println("Total number of B pawns on block positions: " + getPawnsOnPosition("B", getBlockPositions()));
		System.out.println("Total number of B pawns on narrow rhombus: " + getPawnsOnPosition("B", getNarrowRhombus()));
		System.out.println("Total number of B pawns on wide rhombus: " + getPawnsOnPosition("B", getWideRhombus()));
		System.out.println("Total number of W pawns behind narrow rhombus : " + getPawnsOnPosition("W", getBehindNarrowRhombus()));
		System.out.println("=======================================================================================");
	}
}
