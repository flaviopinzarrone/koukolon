package it.unibo.ai.didattica.competition.tablut;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.gui.Gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class replayGames {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        // percorso in cui salvare/caricare la partita
        String path = "/home/enrico/IdeaProjects/koukolon/Executables/games_savings/game.dat";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
        Gui gui = new Gui(4);
        ArrayList<State> states = (ArrayList<State>) in.readObject();
        in.close();
        for (State state : states){
            gui.update(state);
            TimeUnit.SECONDS.sleep(1);
        }

    }
}
