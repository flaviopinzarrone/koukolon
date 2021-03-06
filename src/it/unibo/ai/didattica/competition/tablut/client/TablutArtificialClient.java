package it.unibo.ai.didattica.competition.tablut.client;


import it.unibo.ai.didattica.competition.tablut.koukolon.minmax.MyIterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TablutArtificialClient extends TablutClient {

    private int game;
    private boolean debug;
    private boolean save;

    public TablutArtificialClient(String player, String name, int timeout, String ipAddress, int game, boolean save, boolean debug) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.game = game;
        this.save = save;
        this.debug = debug;
    }

    public static void main(String[] args) throws IOException {
        int gameType = 4;
        String role = "";
        String name = "koukolon";
        String ipAddress = "localhost";
        int timeout = 60;
        boolean save = false;
        boolean debug = false;

        final String USAGE = "USAGE: ./koukolon <black|white> <timeout-in-seconds> <server-ip> <save> <debug>";

        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.out.println(USAGE);
            System.exit(-1);
        } else {
            role = (args[0]);
        }
        if (args.length == 2) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                System.out.println("Timeout must be an integer representing seconds");
                System.out.println(USAGE);
                System.exit(-1);
            }
        }
        if (args.length == 3) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                System.out.println("Timeout must be an integer representing seconds");
                System.out.println(USAGE);
                System.exit(-1);
            }
            ipAddress = args[2];
        }

        if (args.length == 4) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                System.out.println("Timeout must be an integer representing seconds");
                System.out.println(USAGE);
                System.exit(-1);
            }
            ipAddress = args[2];
            if(args[3].equals("save")) {
                save = true;
            }
            else {
                System.out.println("The last argument can be only 'debug' and it allow to print logs during search");
                System.out.println(USAGE);
                System.exit(-1);
            }
        }

        if (args.length == 5) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                System.out.println("Timeout must be an integer representing seconds");
                System.out.println(USAGE);
                System.exit(-1);
            }
            ipAddress = args[2];
            if(args[3].equals("save")) {
                save = true;
            }
            if(args[4].equals("debug")) {
                debug = true;
            }
            else {
                System.out.println("The last argument can be only 'debug' and it allow to print logs during search");
                System.out.println(USAGE);
                System.exit(-1);
            }
        }

        TablutArtificialClient client = new TablutArtificialClient(role, name, timeout, ipAddress, gameType, save, debug);
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


        // attributes depends to parameters passed to main
        System.out.println("Team: Koukolon");
        System.out.println("Player: " + (this.getPlayer().equals(State.Turn.BLACK) ? "BLACK" : "WHITE" ));
        System.out.println("Timeout: " + this.timeout +" s");
        System.out.println("Server: " + this.serverIp);
        System.out.println("Save mode: " + this.save);
        System.out.println("Debug mode: " + this.debug+"\n");

        /* code to save game for replaying
        try {
            ArrayList<State> list = new ArrayList<State>();
            String home = System.getenv("HOME");
            String path = home + "/IdeaProjects/koukolon/Executables/games_savings/game.dat";
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(list);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        // still alive until you are playing

        String home = System.getenv("HOME");
        String path = "Executables/games_savings/game.dat";
        System.out.println("Path: " + path);
        while (true) {
            if(save) {
                try {
                    state.saveState(path);
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println(e);
                }
            }

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

                    if(save) {
                        try {
                            state.saveState(path);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                    System.exit(0);
                }
                // if I LOSE
                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU LOSE!");

                    if(save) {
                        try {
                            state.saveState(path);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println(e.getMessage());
                        }
                    }

                    System.exit(0);
                }
                // if DRAW
                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");

                    if(save) {
                        try {
                            state.saveState(path);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println(e.getMessage());
                        }
                    }

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

                }

                // if is turn of oppenent (WHITE)
                else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
                    System.out.println("Waiting for your opponent move...\n");
                }

                // if I LOSE
                else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU LOSE!");

                    if(save) {
                        try {
                            state.saveState(path);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println(e.getMessage());
                        }
                    }

                    System.exit(0);
                }

                // if I WIN
                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU WIN!");

                    if(save) {
                        try {
                            state.saveState(path);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println(e.getMessage());
                        }
                    }

                    System.exit(0);
                }

                // if DRAW
                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");

                    if(save) {
                        try {
                            state.saveState(path);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println(e.getMessage());
                        }
                    }

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


}