package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.Persister;
import me.aarondmello.driver.ProgramRunner;
import me.aarondmello.maininterfaces.GUI;

public class CreateTournamentTest {
    GUI gui = Mockito.mock(GUI.class);
    Persister persister = Mockito.mock(Persister.class);
    ProgramRunner programRunner = new ProgramRunner(gui, persister);
    Tournament tournament;
    File file = new File("/");
    @Test
    public void nullReturnIfNullFolderProvided(){
        when(gui.getSaveLocation()).thenReturn(null);

        tournament = programRunner.createTournament();

        assertEquals(null, tournament);
    }
    @Test
    public void scansProvidedFolderIfNotNull(){
        when(gui.getSaveLocation()).thenReturn(file);

        programRunner.createTournament();

        verify(persister).scanFolder(file);
    }

    @Test
    public void confirmsTournamentDetailsIfConfigWasReadSuccessfullyAndTournamentDataValid(){
        tournament = Mockito.mock(Tournament.class);
        when(gui.getSaveLocation()).thenReturn(file);
        when(persister.scanFolder(file)).thenReturn(tournament);
        when(persister.wasFileReadSuccessfully("config.txt")).thenReturn(true);
        when(tournament.isDataValid()).thenReturn(true);

        programRunner.createTournament();

        verify(gui).confirmTournamentDetails(tournament, persister.getFilesReadIterator());
    }

    @Test
    public void getsTournamentDetailsIfConfigNotReadUnsuccessfully(){
        tournament = Mockito.mock(Tournament.class);
        when(gui.getSaveLocation()).thenReturn(file);
        when(persister.scanFolder(file)).thenReturn(tournament);
        when(persister.wasFileReadSuccessfully("config.txt")).thenReturn(false);

        programRunner.createTournament();

        verify(gui).getTournamentDetails(tournament);
    }

    @Test
    public void getsTournamentDetailsIfTournamentDataNotValid(){
        tournament = Mockito.mock(Tournament.class);
        when(gui.getSaveLocation()).thenReturn(file);
        when(persister.scanFolder(file)).thenReturn(tournament);
        when(persister.wasFileReadSuccessfully("config.txt")).thenReturn(true);
        when(tournament.isDataValid()).thenReturn(false);

        programRunner.createTournament();

        verify(gui).getTournamentDetails(tournament);
    }
}
