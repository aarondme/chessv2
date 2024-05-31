package me.aarondmello.swinguserinterface;

import java.io.File;

import javax.swing.JFileChooser;

public class TournamentFolderPanel extends JFileChooser{

    public File getFolder() {
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //TODO Fix this display.
        int option = this.showOpenDialog(null);

        if(option == JFileChooser.APPROVE_OPTION)
            return this.getSelectedFile();
        return null;
    }

    public File getFile(){
        this.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = this.showOpenDialog(null);

        if(option == JFileChooser.APPROVE_OPTION)
            return this.getSelectedFile();
        return null;
    }
    
}
