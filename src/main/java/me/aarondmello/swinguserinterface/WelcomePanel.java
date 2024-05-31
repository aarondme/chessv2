package me.aarondmello.swinguserinterface;

import javax.swing.JOptionPane;

public class WelcomePanel extends JOptionPane{

    public void displayWelcomeMessage() {
        String prompt = """
                --- Chess Tournament Manager by Aaron D'Mello---
                Licensed under CC BY-SA 4.0
                http://creativecommons.org/licenses/by-sa/4.0/?ref=chooser-v1""";
        String title = "Chess Tournament Manager";
    
        JOptionPane.showMessageDialog(null, prompt, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
