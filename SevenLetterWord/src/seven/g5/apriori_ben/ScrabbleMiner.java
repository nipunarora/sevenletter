package seven.g5.apriori_ben;

import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import seven.g5.apriori_ben.DataMine.ItemSet;
import seven.g5.apriori_ben.LetterMine.LetterSet;

public class ScrabbleMiner {
        static {
                BasicConfigurator.configure();
                Logger.getRootLogger().setLevel(org.apache.log4j.Level.ERROR);
        }
        /**
         * Demo program for LetterMine modification of <i>a priori</i> algorithm.
         * Prints out the number of words containing the given letters, and the words
         * themselves.  Also a lot of logging information, if you don't turn it off.
         * @param args the letters (all caps) in the set to look at.
         */

        public static void main(String[] args) {
                Logger l = Logger.getLogger(ScrabbleMiner.class);
                DataMine mine = null;
                mine = new LetterMine("src/seven/g5/data/FilteredWords.txt");//src/seven/g5/super-small-wordlist.txt");
                mine.buildIndex();
                ItemSet[] answer = mine.aPriori(0.000001);
                LetterSet i = (LetterSet) mine.getCachedItemSet(args);
                //System.out.println("alive and well: " + answer.length + " itemsets total");
                if (null != i) {
                        String[] words = i.getWords();
                        System.out.format(
                                        "Itemset [%s] has %d associated words:\n",
                                        new Object[]{i.getKey(), words.length}
                        );
                        for (String w : words) {
                                //System.out.println(w);
                        }
                } else {
                        System.out.format(
                                        "No words contain the letters %s\n",
                                        new Object[]{ Arrays.deepToString(args)}
                        );
                }

        }
}


