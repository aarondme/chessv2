# Chess Tournament Manager
<p xmlns:cc="http://creativecommons.org/ns#" xmlns:dct="http://purl.org/dc/terms/"><span property="dct:title">Chess Tournament Manager</span> by <span property="cc:attributionName">Aaron D'Mello</span> is licensed under <a href="http://creativecommons.org/licenses/by-sa/4.0/?ref=chooser-v1" target="_blank" rel="license noopener noreferrer" style="display:inline-block;">CC BY-SA 4.0<img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/cc.svg?ref=chooser-v1"><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/by.svg?ref=chooser-v1"><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/sa.svg?ref=chooser-v1"></a></p>

## Summary

This program automatically determines the pairings and rankings for a swiss chess tournament
(i.e. fixed number of rounds, significantly fewer rounds than the number of players
in the tournament. No eliminations).
It pairs students based on the following criteria:


1. No two players can play each other more than once.
2. Each player does not sit out more than once.
3. As few players sit out as possible
4. Players play opponents with similar scores as them.
5. (Divisional tournaments only) Avoid pairing players of the same school.
6. Players play white about as many times as they play black

A player is assigned 2 points for a win, 1 point for a draw and 0 points for a loss.
At the end of the tournament, automatic tiebreaks are used. These include (but are not limited to):
1. Sum of scores of opponents played.
2. Sum of (score in the game against opponent) * (opponent's overall tournament score).
3. Number of games (or wins) played as black.

 
## Usage
### Basic Steps
Ensure that Java 16 or higher is installed, and you have admin permissions on the device 
used to run the software. This program does not need an internet connection to run. It runs locally, and creates save
files throughout the tournament to allow restoring in case the program is closed.

Running the program with the CSV Based interface:

1. Double-click the app
2. Follow the prompts given.

Note that the CSV Based interface currently does not allow the user to 
add/edit/remove players individually to the tournament, and only allows
adding files. It also does not allow marking a player as "sitting out",
in the case that someone quits the tournament. In order to use these features,
the command line interface must be used.

This interface creates additional spreadsheets showing the pairings for each round,
when these spreadsheets are filled with the results of the round and the
confirm button is pressed, the results will be loaded.

Running the program with the command line interface:

1. Open a terminal/command prompt.
2. Change directories into the directory that contains this application.
   If you are on Windows, and have the folder on your Desktop, this can be done by typing the following command in the terminal:
   ```cd Desktop\ChessApp```
   On a Mac, or machine running Linux, if the folder is on the Desktop, this can be done by typing:
   ```cd Desktop/ChessApp```
3. Run the program using ```java -jar chess_jar.jar -c```
4. Follow the prompts given by the program. When entering a file
   to load, then make sure to include the extension of the file. For example ```data.csv```
   instead of just ```data```
 
### Loading player lists from a csv file

If you wish to read data from a file, rather than enter players one-by-one,
ensure the file is in the following format:


|school1| |
|-------|--------|
|Name   |Division|
|a      |A       |
|b      |A       |
|c      |A       |
|d      |B       |
|e      |B       |
|f      |B       |
|g      |C       |
|h      |C       |

Here "school1" is the name of the school that all the players above belong to.
Each school requires a separate file. A csv file can be imported to and created from an Excel 
(or similar) software, using the "save as" functionality.

### Loading/Reading in progress tournament saves
While the tournament progresses, csv files will be created to allow restoring the tournament.
Part of the output will include table(s) that look similar to the following:

|ID |Name|Organization|Score|Game 1|WinCount|
|---|----|------------|-----|------|--------|
|0  |p1  |org         |2    |Ww1   |1       |
|1  |p2  |org         |0    |Lb0   |0       |

The ID column indicates the id of a player. Name and Organization
correspond to the player and school names. The score is the player's current score
(2 points per win, 1 point per draw, 0 points per loss). The next columns indicate
the player's game results. The codes are made of three parts. The first letter "W, D, L"
indicates a win, loss or draw. The second letter "w, b" indicates white or black.
The number indicates the id of the opponent. If a player sat out, this is notated as "Ww-1".
The remainder of the columns indicate the tiebreaks and their scores, sorted in order in which
they were applied.

Once a new round is started, it is not possible to edit the results of previous rounds
in the software. If changes need to be made, close the program, edit the "Game #"
columns appropriately, then restart the program choosing "resume existing tournament"
and loading the appropriate file. All tiebreaks will be recomputed appropriately.
DO NOT MAKE CHANGES WHILE A ROUND IS BEING PLAYED! The pairings are determined by previous results,
so if a round is in progress and results are being modified, when the program
is restarted with the new data, it will not necessarily output a pairing consistent with the
round in progress.
