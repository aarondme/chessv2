package me.aarondmello.swinguserinterface;

import java.io.File;

import javax.swing.JFileChooser;

public class TournamentFolderPanel extends JFileChooser implements SwingPanel {

    @Override
    public void run(SwingUserInterface gui) {
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //TODO Fix this display.
        int option = this.showOpenDialog(null);

        if(option == JFileChooser.APPROVE_OPTION){
            File folder = this.getSelectedFile();
            gui.readFolder(folder);
        }
    }
    
}
