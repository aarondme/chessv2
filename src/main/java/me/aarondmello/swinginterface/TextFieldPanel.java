package me.aarondmello.swinginterface;
import java.util.HashMap;
import javax.swing.*;

import me.aarondmello.driver.Tournament;
class NewTournamentPanel extends JPanel{
    HashMap<String, JTextField> fieldNameToEnteredText;
    final static int EXIT_BUTTON_PRESSED = 1;
    final static int BACK_BUTTON_PRESSED = 2;
    final static int VALID_INPUT_ENTERED = 0;

    public NewTournamentPanel(){
        fieldNameToEnteredText = new HashMap<>();
        
        JTextField tournamentName = new JTextField();
        this.addToTextFields("tournamentName", tournamentName);
        JTextField numberOfRounds = new JTextField();
        this.addToTextFields("numberOfRounds", numberOfRounds);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(new JLabel("Enter the tournament name"));
        this.add(tournamentName);
        this.add(new JLabel("Enter the number of Rounds"));
        this.add(numberOfRounds);
        fieldNameToEnteredText.put("tournamentName", tournamentName);
        fieldNameToEnteredText.put("numberOfRounds", numberOfRounds);
    }

    public int promptForValidNewTournamentData(){
        boolean isInputValid = true;
        do {
            int in = JOptionPane.showConfirmDialog(null, this, "New Tournament", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
            if (in == JOptionPane.CLOSED_OPTION) 
                return EXIT_BUTTON_PRESSED;
            if (in == JOptionPane.CANCEL_OPTION) 
                return BACK_BUTTON_PRESSED;
            
            isInputValid = validateNewTournamentData();
        } while (!isInputValid);
        return VALID_INPUT_ENTERED;
    }

    private boolean validateNewTournamentData(){
        boolean isNumberOfRoundsValid = validateNewTournamentNumberOfRounds();
        boolean isNameValid = validateNewTournamentName();
        boolean isTournamentDataValid = isNameValid && isNumberOfRoundsValid;
        //TODO: display prompt to show error message.
        return isTournamentDataValid;
    }
    private boolean validateNewTournamentName(){
        //TODO
        return true;
    }
    private boolean validateNewTournamentNumberOfRounds(){
        try {
            int rounds = Integer.parseInt(getText("numberOfRounds"));
            return rounds > 0;
        } catch (Exception e) {
            return false;
        }
    }
    public Tournament createNewTournament(){
        //TODO
        return null;
    }

    public void addToTextFields(String fieldName, JTextField textField){
        fieldNameToEnteredText.put(fieldName, textField);
    }

    public String getText(String fieldName){
        return fieldNameToEnteredText.get(fieldName).getText();
    }
}