package seven.g3.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import seven.g3.KnowledgeBase.KnowledgeBase;
import seven.g3.KnowledgeBase.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class SevenLetterStrategy extends Strategy {
	
	private static final String BEST_WORD = "OTARINE";
	int bid_amount = 0;
	final int max_bid_amount = 7;
	char previous_char;
	boolean isRequired = false;
	Strategy strat;
	boolean switchstrategy = false;
	
	List<Character> charsObtained = new ArrayList<Character>();
	List<Character> charsRequired = new ArrayList<Character>();

	public SevenLetterStrategy(KnowledgeBase kb, int totalRounds,
			ArrayList<String> playerList) {
		super(kb, totalRounds, playerList);
		strat = new NaiveStrategy(kb, totalRounds, playerList);
		
//		charsRequired.add('A');
//		charsRequired.add('A');
//		charsRequired.add('E');
//		charsRequired.add('E');
//		charsRequired.add('I');
//		charsRequired.add('I');
		for(int i = 0; i < BEST_WORD.length(); i++)
		{
			charsRequired.add(BEST_WORD.charAt(i));
		}
	}

	@Override
	public int calculateBidAmount(Letter bidLetter,
			HashMap<Character, Integer> letters) {
		if(switchstrategy){
			return strat.calculateBidAmount(bidLetter, letters);
		}
		return bid_amount;
	}

	@Override
	public int calculateOthersLetterWorth(Letter bidLetter, int playerID) {
		return 0;
	}

	@Override
	public int calculatePersonalLetterWorth(Letter bidLetter) {
		return 0;
	}

	@Override
	public PriorityQueue<Word> findPossibleWords() {
		return null;
	}

	@Override
	public PriorityQueue<Word> findPossibleWords(Letter letter) {
		return null;
	}

	@Override
	public PriorityQueue<Word> findPossibleWordsOther(int playerID) {
		return null;
	}

	@Override
	public PriorityQueue<Word> findPossibleWordsOther(int playerID,
			Letter letter) {
		return null;
	}

	@Override
	public void update(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			SecretState secretstate, int numLetters,
			HashMap<Character, Integer> letters) {
		if(switchstrategy){
			strat.update(bidLetter, PlayerBidList, secretstate, numLetters, letters);
			return;
		}
		System.out.println("--bid letter is "+ bidLetter.getAlphabet());
		bid_amount = 0;
		if(hasWonBid(numLetters) && !isRequired) {
			//switchStrategy();
			switchstrategy = true;
			strat.update(bidLetter, PlayerBidList, secretstate, numLetters, letters);
			System.out.println("***Switching the strategy as our required word cannot be formed by '"+ previous_char+"' **");
			return;
		}
		
		if(hasWonBid(numLetters)) {
			totalLetters = numLetters;
			charsObtained.add(previous_char);
			charsRequired.remove(charsRequired.indexOf(previous_char));
		}
		System.out.print("required chars :");
		for(Character c: charsRequired){
			System.out.print(c+ " ");
		}
		
		System.out.println("");
		
		System.out.print("obtained chars :");
		for(Character c: charsObtained){
			System.out.print(c+ " ");
		}
		System.out.println();
		System.out.println("Is it required :"+charsRequired.contains(bidLetter.getAlphabet()));
		if(charsRequired.contains(bidLetter.getAlphabet()) ){
			System.out.println("Hurray required word...");
			bid_amount = calBidAmount();
			isRequired = true;
		}else{
			bid_amount = 0;
			isRequired = false;
		}
		
		
		previous_char = bidLetter.getAlphabet();
	}
	
	private int calBidAmount()
	{
	
		return 50/charsRequired.size();
	}

	private boolean hasWonBid(int numLetters) {
		return numLetters > totalLetters;
	}

	@Override
	public String returnWord(HashMap<Character, Integer> myLetters) {
		
		return BEST_WORD;
	}
	
	static class Letter7Repository
	{
		static List<List<Character>> wordCharList = new ArrayList<List<Character>>();
		static List<String> wordsList = new ArrayList<String>();
		static{
			
			wordsList.add("ANTIAIR");
			wordsList.add("ARENITE");
			wordsList.add("ERINITE");
			wordsList.add("ETAERIO");
			wordsList.add("INERTIA");
			wordsList.add("ORATION");
			wordsList.add("OTARINE");
			wordsList.add("TAENIAE");
		}
		
		public void populate()
		{
			for(String word: wordsList)
			{
				List<Character> list = new ArrayList<Character>();
				for(int i =0; i< word.length(); i++){
					list.add(word.charAt(i));
				}
				wordCharList.add(list);
			}
		}
		
		public void isRequired(char c){
			
		}
		
		public void hasWon()
		{
			
		}
	}
}
