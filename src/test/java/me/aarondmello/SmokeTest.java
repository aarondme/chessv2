package me.aarondmello;

import me.aarondmello.commandlineinterface.BasicProgramFlow;
import me.aarondmello.commandlineinterface.CommandLineInterface;
import me.aarondmello.csv.CsvReader;
import me.aarondmello.csv.CsvWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class SmokeTest {
    String root = "src/test/data/";
    @Test
    public void basicSmoke() throws IOException {
        BasicProgramFlow programFlow = new BasicProgramFlow(new CommandLineInterface(new Scanner(new FileReader(root + "Smoke Tests/basicSmoke.txt"))));
        programFlow.start(new CsvReader(), new CsvWriter());


        Files.delete(Path.of("BasicSmokeTest_Round 1.csv"));
        Files.delete(Path.of( "BasicSmokeTest_Round 1_Pairing.csv"));

        BufferedReader reader = new BufferedReader(new FileReader("BasicSmokeTest_Round 2.csv"));
        for (int i = 0; i < 6; i++) {
            reader.readLine();
        }
        Assertions.assertTrue(reader.readLine().contains("Ww"));
        Assertions.assertTrue(reader.readLine().contains("Ww-1"));
        Assertions.assertTrue(reader.readLine().contains("Lb"));

        for (int i = 0; i < 3; i++) {
            reader.readLine();
        }

        Assertions.assertTrue(reader.readLine().contains("Wb"));
        Assertions.assertTrue(reader.readLine().contains("Ww-1"));
        Assertions.assertTrue(reader.readLine().contains("Lw"));

        for (int i = 0; i < 3; i++) {
            reader.readLine();
        }

        Assertions.assertTrue(reader.readLine().contains("D"));
        Assertions.assertTrue(reader.readLine().contains("D"));

        reader.close();
        Files.delete(Path.of( "BasicSmokeTest_Round 2.csv"));
    }
}
