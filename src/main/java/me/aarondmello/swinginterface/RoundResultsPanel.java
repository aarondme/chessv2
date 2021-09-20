package me.aarondmello.swinginterface;
import java.util.*;

import javax.swing.*;

import me.aarondmello.driver.Match;
import me.aarondmello.driver.Player;
import me.aarondmello.driver.Round;

import java.awt.*;

class RoundResultsPanel extends JPanel{
    JComboBox[] comboBoxes;

    RoundResultsPanel(Round round){
        LinkedList<Match> matches = round.getMatches();
        setPanelLayout(matches);
     
     
    }
    /** 
    public LinkedList<Integer> getRoundResults(){
        LinkedList<Integer> results = new LinkedList<>(); 
        boolean isInputValid = false;
        while (!isInputValid) {
          int input = JOptionPane.showConfirmDialog(null, this,
              t.getName() + " Round " + (t.getCurrentRound() + 1) + " of " + t.getNumberOfRounds(),
              JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
          if (input == -1) {
            t.isCancelPressed = true;
            return null;
          }
    
          isInputValid = true;
          for (int i = 0; i < matches.size(); i++) {
            String x = String.valueOf(options[i].getSelectedItem());
            if (x.equals((" ")))
              isInputValid = false;
            else
              results[i] = (x.equals("White Win")) ? 2 : (x.equals("Draw")) ? 1 : 0;
          }
        }
        return results;
    }
    **/
    private void setPanelLayout(LinkedList<Match> matches){
        int numberOfMatches = matches.size();
    
        this.setLayout(new GridLayout(numberOfMatches + 1, 4, 20, 5));
    
        this.add(new JLabel("Board #"));
        this.add(new JLabel("White Player"));
        this.add(new JLabel(" "));
        this.add(new JLabel("Black Player"));
        JComboBox[] options = new JComboBox[numberOfMatches];
        String[] choices = { " ", "White Win", "Draw", "Black Win" };
        
        
        int i = 1;
        for (Match match : matches) {
            this.add(new JLabel("" + i));
            Player white = match.getWhitePlayer();
            Player black = match.getBlackPlayer();

            if (white != null)
                this.add(new JLabel(white.getName()));
            else
                this.add(new JLabel("BYE"));
    
            options[i - 1] = new JComboBox(choices);
            this.add(options[i - 1]);
    
            if (black != null)
                this.add(new JLabel(black.getName()));
            else
                this.add(new JLabel("BYE"));
        
            i++;
        }
    }
}