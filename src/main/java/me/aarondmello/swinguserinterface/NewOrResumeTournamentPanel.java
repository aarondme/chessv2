package me.aarondmello.swinguserinterface;

import javax.swing.*;

public class NewOrResumeTournamentPanel {

    public int getIfStartingNewTournament() {
        String[] options = { "New Tournament", "Resume Tournament" };
        String prompt = "Choose whether to create a new tournament or to resume an existing tournament";
        String title = "Chess tournament Manager";
    
        int input = JOptionPane.showOptionDialog(null, prompt, title, JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            
        return input; 
    }

}
