package me.aarondmello.swinguserinterface;

import javax.swing.JOptionPane;

public class WelcomePanel extends JOptionPane implements SwingPanel{

    @Override
    public void run(SwingUserInterface gui) {
        String prompt = "Chess Tournament Manager - Aaron D'Mello";
        String title = "Chess tournament Manager";
    
        JOptionPane.showMessageDialog(null, prompt, title, JOptionPane.INFORMATION_MESSAGE);
        
        gui.setCurrentPanel(new TournamentFolderPanel());
    }
}
