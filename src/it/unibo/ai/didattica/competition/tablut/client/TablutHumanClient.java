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

					for (int i = 0; i < 4; i++) {
						System.out.println("Bianchi nel quadrante " + i + ": " + getPawnsOnQuadrant(i, "W"));
						System.out.println("Neri in protezione nel quadrante " + i + ": " + getNumberOnBlockPositions(i));
						System.out.println("Bianchi nella cross " + i + ": " + getPawnsOnCross(i, "W"));
					}
					System.out.println("Miglior quadrante: " + getMostOpenQuadrant("W"));

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

					for (int i = 0; i < 4; i++) {
						System.out.println("Bianchi nel quadrante " + i + ": " + getPawnsOnQuadrant(i, "W"));
						System.out.println("Neri in protezione nel quadrante " + i + ": " + getNumberOnBlockPositions(i));
						System.out.println("Bianchi nella cross " + i + ": " + getPawnsOnCross(i, "W"));
					}
					System.out.println("Miglior quadrante: " + getMostOpenQuadrant("W"));


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
		State.Pawn[][] board = getCurrentState().getBoard();

		for(int[] position: cross) {
			State.Pawn pawn = board[position[0]][position[1]];
			if(pawn.equalsPawn(target) || (target.equalsIgnoreCase("W") && pawn.equalsPawn("K"))) result++;
		}

		return result;
	}

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

	private final int[][][] blockPositionsByQuadrant = {
			{{0, 2}, {2, 0}},
			{{0, 6}, {2, 8}},
			{{6, 0}, {8, 2}},
			{{8, 6}, {6, 8}}
	};

	private int getNumberOnBlockPositions(int quadrant) {
		int num = 0;

		for(int[] pos: blockPositionsByQuadrant[quadrant]) {
			if(getCurrentState().getPawn(pos[0], pos[1]).equalsPawn(State.Pawn.BLACK.toString())) num++;
		}

		return num;
	}

}
