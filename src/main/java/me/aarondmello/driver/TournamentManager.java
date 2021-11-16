package me.aarondmello.driver;

import me.aarondmello.datatypes.Tournament;

public interface TournamentManager {
    /**
     * @return Tournament object if successful, null otherwise
     */
    public Tournament createTournament();
    public void startNextRound(Tournament tournament);
    public void save(Tournament tournament);
    public Tournament resumeTournament();
}
