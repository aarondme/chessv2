import java.util.function.BiFunction;
import java.util.*;

class NoRatingTiebreak implements Comparator<Tiebreaks>{

    @Override
    public int compare(Tiebreaks o1, Tiebreaks o2) {
        List<BiFunction<Tiebreaks, Tiebreaks, Integer>> methodlist = 
            Arrays.asList(((Tiebreaks a, Tiebreaks b) -> {return b.getBuchholzCutOne() - a.getBuchholzCutOne();}),
                          ((Tiebreaks a, Tiebreaks b) -> {return b.getBuchholz() - a.getBuchholz();}),
                          ((Tiebreaks a, Tiebreaks b) -> {return b.getSonnebornBerger() - a.getSonnebornBerger();}),
                          ((Tiebreaks a, Tiebreaks b) -> {return b.getProgressiveScores() - a.getProgressiveScores();}),
                          ((Tiebreaks a, Tiebreaks b) -> {return b.getDirectEncounter() - a.getDirectEncounter();}),
                          ((Tiebreaks a, Tiebreaks b) -> {return b.getWinCount() - a.getWinCount();}),
                          ((Tiebreaks a, Tiebreaks b) -> {return b.getWinCountAsBlack() - a.getWinCountAsBlack();}));
        for(BiFunction<Tiebreaks, Tiebreaks, Integer> m : methodlist){
            int x = m.apply(o1, o2);
            if(x != 0) return x;
        }
        return 0;
    }
    
}