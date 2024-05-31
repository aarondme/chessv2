package me.aarondmello.swinguserinterface;

import static javax.swing.JOptionPane.*;

public class NewOrResumeTournamentPanel {

    public boolean getIfStartingNewTournament() {
        String[] options = { "New Tournament", "Resume Tournament" };
        String prompt = "Choose whether to create a new tournament or to resume an existing tournament";
        String title = "Chess Tournament Manager";

        return showOptionDialog(null, prompt, title, YES_NO_CANCEL_OPTION,
            PLAIN_MESSAGE, null, options, options[0]) == OK_OPTION;
    }

}
