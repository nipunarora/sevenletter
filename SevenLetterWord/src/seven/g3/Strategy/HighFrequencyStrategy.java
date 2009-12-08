package seven.g3.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import seven.g3.Util;
import seven.g3.KnowledgeBase.KnowledgeBase;
import seven.g3.KnowledgeBase.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class HighFrequencyStrategy extends Strategy {

	private static final String BEST_WORD = "OTARINE";
	String targetWord;
	int bid_amount = 0;
	final int max_bid_amount = 7;
	char previous_char;
	boolean isRequired = false;
	Strategy strat;
	boolean switchstrategy = false;
	int bidIncrementer = 0;

	List<Character> charsObtained = new ArrayList<Character>();
	List<Character> charsRequired = new ArrayList<Character>();
	
	
	/* 
	 * list of predefined FrequencyWords
	 * 
	 */
	static Map<Integer, String> predefined7Words = new HashMap<Integer, String>();
	static{
		initPredefinedWords();
	}
	
	static Random rand = new Random(0);
	public String get7letterWord() {
		
		int val = rand.nextInt(10);
		return predefined7Words.get(val);
	}

	public static void initPredefinedWords() {
		predefined7Words.put(0, "OTARINE");
		predefined7Words.put(1, "QUEZALS");
		predefined7Words.put(2, "AUDILES");
		predefined7Words.put(3, "OPACIFY");
		predefined7Words.put(4, "ANTIAIR");
		predefined7Words.put(5, "ARENITE");
		predefined7Words.put(6, "ERINITE");
		predefined7Words.put(7, "ETAERIO");
		predefined7Words.put(8, "INERTIA");
		predefined7Words.put(9, "TAENIAE");
	}

	
	public HighFrequencyStrategy(KnowledgeBase kb, int totalRounds,
			ArrayList<String> playerList) {
		this(kb, totalRounds, playerList, BEST_WORD);
	}


	public HighFrequencyStrategy(KnowledgeBase kb, int totalRounds,
			ArrayList<String> playerList, String word) {
		super(kb, totalRounds, playerList);
		
		if(word.isEmpty()) {
			String sevenletterWord = get7letterWord();
			//Util.println("using seven leter strategy" + sevenletterWord);
		}
		
		strat = new NaiveStrategy(kb, totalRounds, playerList);
		targetWord = word;

		// charsRequired.add('A');
		// charsRequired.add('A');
		// charsRequired.add('E');
		// charsRequired.add('E');
		// charsRequired.add('I');
		// charsRequired.add('I');
		for (int i = 0; i < targetWord.length(); i++) {
			charsRequired.add(targetWord.charAt(i));
		}
	}


	@Override
	public int calculateBidAmount(Letter bidLetter,
			HashMap<Character, Integer> letters, int paidThisRound) {
		if (switchstrategy) {
			return strat.calculateBidAmount(bidLetter, letters, paidThisRound);
		}
		
		return bid_amount;
	}


	@Override
	public void update(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			SecretState secretstate, int numLetters,
			HashMap<Character, Integer> letters) {
		if (switchstrategy) {
			strat.update(bidLetter, PlayerBidList, secretstate, numLetters,
					letters);
			return;
		}
		
		//Util.println("--bid letter is " + bidLetter.getAlphabet());
		bid_amount = 0;
		if (hasWonBid(numLetters) && !isRequired) {
			// switchStrategy();
			switchstrategy = true;
			strat.update(bidLetter, PlayerBidList, secretstate, numLetters,
					letters);
			//Util.println("***Switching the strategy as our required word cannot be formed by '"
		//							+ previous_char + "' **");
			return;
		}

		if (hasWonBid(numLetters)) {
			totalLetters = numLetters;
			charsObtained.add(previous_char);
			charsRequired.remove(charsRequired.indexOf(previous_char));
		}
		//Util.println("required chars :");
		for (Character c : charsRequired) {
			//Util.println(c + " ");
		}

		//Util.println("");

		//Util.println("obtained chars :");
		for (Character c : charsObtained) {
			//Util.println(c + " ");
		}
		//Util.println();
		//Util.println("Is it required :"
		//			+ charsRequired.contains(bidLetter.getAlphabet()));
		if (charsRequired.contains(bidLetter.getAlphabet())) {
			//Util.println("Hurray required word...");
			bidIncrementer++;
			bid_amount = bidLetter.getValue() + bidIncrementer;//calBidAmount();
			if(bid_amount > 6){
				bid_amount = 7;
			}
			isRequired = true;
		} else {
			bid_amount = 0;
			isRequired = false;
		}

		previous_char = bidLetter.getAlphabet();
	}

	private int calBidAmount() {

		return 50 / charsRequired.size();
	}

	private boolean hasWonBid(int numLetters) {
		return numLetters > totalLetters;
	}

	@Override
	public String returnWord(HashMap<Character, Integer> myLetters) {

		return targetWord;
	}
	
	public boolean hasFailed()
	{
		return switchstrategy;
	}

	static class Letter7Repository {
		static List<List<Character>> wordCharList = new ArrayList<List<Character>>();
		static List<String> wordsList = new ArrayList<String>();
		static {

			wordsList.add("ANTIAIR");
			wordsList.add("ARENITE");
			wordsList.add("ERINITE");
			wordsList.add("ETAERIO");
			wordsList.add("INERTIA");
			wordsList.add("ORATION");
			wordsList.add("OTARINE");
			wordsList.add("TAENIAE");
		}

		public void populate() {
			for (String word : wordsList) {
				List<Character> list = new ArrayList<Character>();
				for (int i = 0; i < word.length(); i++) {
					list.add(word.charAt(i));
				}
				wordCharList.add(list);
			}
		}

		public void isRequired(char c) {

		}

		public void hasWon() {

		}
	}
}
