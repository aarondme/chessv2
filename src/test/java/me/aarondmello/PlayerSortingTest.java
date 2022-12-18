package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
//TODO fix sorting test
//public class PlayerSortingTest {
//    ArrayList<Player> players = new ArrayList<Player>();
//    Player player0 = new Player("a", "b");
//    Player player1 = new Player("a", "b");
//    Tiebreak tiebreakReturns1 = new TestTiebreak(1);
//    Tiebreak tiebreakReturns2 = new TestTiebreak(2);
//
//    public void init(){
//        players.add(player0);
//        players.add(player1);
//    }
//
//    @Test
//    public void twoPlayersDifferentScoreNoTiebreaks(){
//        init();
//        player0.setScore(1);
//        player1.setScore(3);
//
//        Collections.sort(players, Collections.reverseOrder());
//
//        assertEquals(player1, players.get(0));
//        assertEquals(player0, players.get(1));
//    }
//
//    @Test
//    public void twoPlayersSameScoreOneDifferentTiebreak(){
//        init();
//        player0.setScore(1);
//        player0.addTiebreak(tiebreakReturns2);
//        player1.setScore(1);
//        player1.addTiebreak(tiebreakReturns1);
//
//        Collections.sort(players, Collections.reverseOrder());
//
//        assertEquals(player0, players.get(0));
//        assertEquals(player1, players.get(1));
//    }
//
//    @Test
//    public void twoPlayersSameScoreOneMatchingTiebreakOneDifferentTiebreak(){
//        init();
//        player0.setScore(1);
//        player0.addTiebreak(tiebreakReturns2);
//        player0.addTiebreak(tiebreakReturns1);
//        player1.setScore(1);
//        player1.addTiebreak(tiebreakReturns2);
//        player1.addTiebreak(tiebreakReturns2);
//
//        Collections.sort(players, Collections.reverseOrder());
//
//        assertEquals(player1, players.get(0));
//        assertEquals(player0, players.get(1));
//    }
//}
