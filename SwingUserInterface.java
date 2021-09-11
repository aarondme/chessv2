import java.util.HashMap;

import javax.swing.*;
import java.awt.*;

class TextFieldPanel extends JPanel{
    HashMap<String, JTextField> fieldNameToEnteredText;
    public TextFieldPanel(){
        fieldNameToEnteredText = new HashMap<>();
    }

    public void addToTextFields(String fieldName, JTextField textField){
        fieldNameToEnteredText.put(fieldName, textField);
    }

    public String getText(String fieldName){
        return fieldNameToEnteredText.get(fieldName).getText();
    }
}

class SwingUserInterface implements GUI{
    boolean wasCancelPressed = false;
    @Override
    public void displayWelcomeScreen() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getIfNewTournament() {
        String[] options = { "New Tournament", "Resume Tournament" };
        String prompt = "Choose whether to create a new tournament or to resume an existing tournament";
        String title = "Chess tournament Manager";
    
        int input = JOptionPane.showOptionDialog(null, prompt, title, JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    
        if (input == -1) {
          wasCancelPressed = true;
          return false;
        }
    
        return (input == 0);
    }

    @Override
    public Tournament getNewTournament() {
        if (wasCancelPressed)
            return null;

        TextFieldPanel panel = createGetNewTournamentPanel();
        boolean isCreatingNewTournament = promptForValidNewTournamentData(panel);
        if(!isCreatingNewTournament) return null;
        Tournament tournament = createNewTournament(panel);
        return tournament;
    }
    private TextFieldPanel createGetNewTournamentPanel() {
        //TODO: add entry for filename, continue only if filename is valid and file does not exist
        //TODO: add entry to go back
        TextFieldPanel panel = new TextFieldPanel();
        JTextField tournamentName = new JTextField();
        panel.addToTextFields("tournamentName", tournamentName);
        JTextField numberOfRounds = new JTextField();
        panel.addToTextFields("numberOfRounds", numberOfRounds);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Enter the tournament name"));
        panel.add(tournamentName);
        panel.add(new JLabel("Enter the number of Rounds"));
        panel.add(numberOfRounds);
        return panel;
    }
    private boolean promptForValidNewTournamentData(TextFieldPanel panel){
        boolean isInputValid = true;
        do {
            int in = JOptionPane.showConfirmDialog(null, panel, "New Tournament", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
            if (in == JOptionPane.CLOSED_OPTION) {
                wasCancelPressed = true;
                return false;
            }
            if (in == JOptionPane.CANCEL_OPTION) {
                return false;
            }
            isInputValid = validateNewTournamentData(panel);
        } while (!isInputValid);
        return true;
    }
    private boolean validateNewTournamentData(TextFieldPanel panel){
        //TODO
        return false;
    }
    private Tournament createNewTournament(TextFieldPanel panel){
        //TODO
        return null;
    }

    @Override
    public Tournament getExistingTournament() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void getRoundResults(Round round) {
        //TODO  
    }

    @Override
    public void displayResults(Tournament tournament) {
        if (wasCancelPressed)
            return;
        JTextArea textArea = new JTextArea(25, 0);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        //TODO Fix for multiple divisions.
        /** 
        for (int i = 0; i < players.size(); i++) {
            Player q = players.get(i);
            textArea
                .append(String.format("%-7s" + " %-35s" + " %-4s" + "\n", (i + 1) + ".", q.getDisplayName(), q.getScore()));
        }

        JScrollPane scrollPlane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPlane,
            name + " - Results after " + (currentRound + 1) + " of " + numberOfRounds + " rounds",
            JOptionPane.PLAIN_MESSAGE);
        **/
    }
    public boolean getIfSavingResults(){
        //TODO
        return false;
    }
    @Override
    public boolean wasCancelPressed() {
        return wasCancelPressed;
    }

}