package me.aarondmello.swinguserinterface;

import javax.swing.JOptionPane;

public class WelcomePanel extends JOptionPane{

    public void displayWelcomeMessage() {
        String prompt = "Chess Tournament Manager - Aaron D'Mello";
        String title = "Chess tournament Manager";
    
        JOptionPane.showMessageDialog(null, prompt, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
