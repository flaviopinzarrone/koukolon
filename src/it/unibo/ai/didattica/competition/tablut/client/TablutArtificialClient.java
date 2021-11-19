package it.unibo.ai.didattica.competition.tablut.client;


import it.unibo.ai.didattica.competition.tablut.koukolon.minmax.MyIterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutArtificialClient extends TablutClient {

    private int game;
    private boolean debug;

    public TablutArtificialClient(String player, String name, int timeout, String ipAddress, int game, boolean debug) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.game = game;
        this.debug = debug;
    }

    public static void main(String[] args) throws IOException {
        int gameType = 4;
        String role = "";
        String name = "koukolon";
        String ipAddress = "localhost";
        int timeout = 60;
        boolean debug = false;

        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.out.println("USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
            System.exit(-1);
        } else {
            role = (args[0]);
        }
        if (args.length == 2) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                System.out.println("Timeout must be an integer representing seconds");
                System.out.println("USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
                System.exit(-1);
            }
        }
        if (args.length == 3) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                System.out.println("Timeout must be an integer representing seconds");
                System.out.println("USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
                System.exit(-1);
            }
            ipAddress = args[2];
        }

        if (args.length == 4) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                System.out.println("Timeout must be an integer representing seconds");
                System.out.println("USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
                System.exit(-1);
            }
            ipAddress = args[2];
            if(args[3].equals("debug")) {
                debug = true;
            } else {
                System.out.println("The last argument can be only 'debug' and it allow to print logs during search");
                System.out.println("USAGE: ./runmyplayer <black|white> <timeout-in-seconds> <server-ip> <debug>");
                System.exit(-1);
            }
        }

        TablutArtificialClient client = new TablutArtificialClient(role, name, timeout, ipAddress, gameType, debug);
        client.run();
    }

    @Override
    public void run() {

        // send name of your group to the server saved in variable "name"
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set type of state and WHITE must do the first player
        State state = new StateTablut();
        state.setTurn(State.Turn.WHITE);

        // set type of game
        GameAshtonTablut tablutGame = new GameAshtonTablut(0, -1, "logs", "white_ai", "black_ai");;


        System.out.println("\n"+
                        "+--------------------------  Tablut competition 2021  ---------------------------------------------------+");
        System.out.println(
                        "|                  ██   ██  ██████  ██    ██ ██   ██  ██████  ██       ██████  ███    ██                 |\n" +
                        "|                  ██  ██  ██    ██ ██    ██ ██  ██  ██    ██ ██      ██    ██ ████   ██                 |\n" +
                        "|                  █████   ██    ██ ██    ██ █████   ██    ██ ██      ██    ██ ██ ██  ██                 |\n" +
                        "|                  ██  ██  ██    ██ ██    ██ ██  ██  ██    ██ ██      ██    ██ ██  ██ ██                 |\n" +
                        "|                  ██   ██  ██████   ██████  ██   ██  ██████  ███████  ██████  ██   ████                 |\n");

        System.out.println(
                        "+-------------  Made by Francesco Olivo, Enrico Pallotta, Yuri Noviello & Flavio Pinzarrone  ------------+\n");


        // attributes depends to parameters passed to main
        System.out.println("Player: " + (this.getPlayer().equals(State.Turn.BLACK) ? "BLACK" : "WHITE" ));
        System.out.println("Timeout: " + this.timeout +" s");
        System.out.println("Server: " + this.serverIp);
        System.out.println("Debug mode: " + this.debug+"\n");


        // still alive until you are playing
        while (true) {

            // update the current state from the server
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }

            // print current state
            System.out.println("Current state:");
            state = this.getCurrentState();
            System.out.println(state.toString());



            // if i'm WHITE
            if (this.getPlayer().equals(State.Turn.WHITE)) {

                // if is my turn (WHITE)
                if (state.getTurn().equals(StateTablut.Turn.WHITE)) {

                    System.out.println("\nSearching a suitable move... ");

                    // search the best move in search tree
                    Action a = findBestMove(tablutGame, state);

                    System.out.println("\nAction selected: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }

                }

                // if is turn of oppenent (BLACK)
                else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
                    System.out.println("Waiting for your opponent move...\n");
                }
                // if I WIN
                else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                // if I LOSE
                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                }
                // if DRAW
                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            }
            // if i'm BLACK
            else {

                // if is my turn (BLACK)
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {

                    System.out.println("\nSearching a suitable move... ");

                    // search the best move in search tree
                    Action a = findBestMove(tablutGame, state);

                    System.out.println("\nAction selected: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Pawns on Quad " + getPawnsOnQuadrant(0,"W"));
                    System.out.println("Pawns on Cross " + getPawnsOnCross(0,"W"));
                    System.out.println("Most open quad " + getMostOpenQuadrant("W"));
                }

                // if is turn of oppenent (WHITE)
                else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
                    System.out.println("Waiting for your opponent move...\n");
                }

                // if I LOSE
                else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                }

                // if I WIN
                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }

                // if DRAW
                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }
            }
        }
    }


    /**
     * Method that find a suitable moves searching in game tree
     * @param tablutGame Current game
     * @param state Current state
     * @return Action that is been evaluated as best
     */
    private Action findBestMove(GameAshtonTablut tablutGame, State state) {

        MyIterativeDeepeningAlphaBetaSearch search = new MyIterativeDeepeningAlphaBetaSearch(tablutGame, Double.MIN_VALUE, Double.MAX_VALUE, this.timeout - 2 );
        search.setLogEnabled(debug);
        return search.makeDecision(state);
    }

    /*
        Funzioni fatte da Fra.
     */

    private int getPawnsOnQuadrant(int quadrant, String target) {
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

    private int crosses[][][] = {
            {{2, 4}, {3, 4}}, // vertical upper
            {{4, 5}, {4, 6}}, // horizontal right
            {{5, 4}, {6, 4}}, // vertical lower
            {{4, 2}, {4, 3}} // horizontal left
    };

    private int getPawnsOnCross(int cross_number, String target) {
        int result = 0;
        int cross[][] = crosses[cross_number];
        State.Pawn[][] board = getCurrentState().getBoard();

        for(int[] row: cross) {
            State.Pawn pawn = board[row[0]][row[1]];
            if(pawn.equalsPawn(target) || (target.equalsIgnoreCase("W") && pawn.equalsPawn("K"))) result++;
        }

        return result;
    }

    public int getMostOpenQuadrant(String target) {
        int bestCross = -1;
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < 4; i++) {
            int pawnsOnCross = getPawnsOnCross(i, target);
            if(pawnsOnCross > max) {
                bestCross = i;
                max = pawnsOnCross;
            }
        }
        int bestQuadrant = -1;
        max = Integer.MIN_VALUE;
        for(int i = bestCross; i <= (bestCross+1)/4; i++) {
            int pawnsOnQuadrant = getPawnsOnQuadrant(i, target);
            if(pawnsOnQuadrant > max) {
                bestQuadrant = i;
                max = pawnsOnQuadrant;
            }
        }

        return bestQuadrant;
    }
}