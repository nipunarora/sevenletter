package seven.g1.datamining;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class ScrabbleMiner {
	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(org.apache.log4j.Level.DEBUG);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger l = Logger.getLogger(ScrabbleMiner.class);
		DataMine mine = null;
		mine = new LetterMine("super-small-wordlist.txt");
		mine.buildIndex();
		DataMine.ItemSet[] answer = mine.aPriori(0.000001);
		System.out.println("alive and well: " + answer.length + " itemsets total");
	}
}
