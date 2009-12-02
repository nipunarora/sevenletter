package seven.g3.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import seven.g3.KnowledgeBase.KnowledgeBase;
import seven.g3.KnowledgeBase.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.g3.ScrabbleValues;
import seven.ui.SecretState;

public class NaiveStrategy extends Strategy {

	protected static final int NUM_LETTERS_BEFORE_STRATEGY = 2;

	public NaiveStrategy(KnowledgeBase kb, int totalRounds,
			ArrayList<String> playerList) {
		super(kb, totalRounds, playerList);
	}

	@Override
	public void update(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			SecretState secretstate, int numLetters,
			HashMap<Character, Integer> letters) {
		// TODO Auto-generated method stub

		// Find the best word we can make when we get a new letter.
		if (numLetters > totalLetters) {
			System.out.println("Got a new letter. Update best word.");
			System.out.println(totalLetters(letters) + " " + numLetters);
			totalLetters = numLetters;
			PriorityQueue<Word> possibleWords = kb.findMatchingWord(letters,
					totalLetters);

			Word newBest = possibleWords.peek();

			if (newBest != null && newBest.getScore() > bestWord.getScore()) {
				System.out.println(newBest.getWord() + ": "
						+ newBest.getScore()
						+ " is the best word we can currently make.");

				bestWord = newBest;
			}
		}

	}

	@Override
	public int calculateBidAmount(Letter bidLetter,
			HashMap<Character, Integer> letters) {

		// Just bid on letters based on value for the first few letters.
		if (totalLetters < NUM_LETTERS_BEFORE_STRATEGY)
			return ScrabbleValues.letterScore(bidLetter.getAlphabet()) + 2;

		// Else, try to see if the next letter will give us a better valid word.
		PriorityQueue<Word> best = kb.findMatchingWord(letters, totalLetters,
				bidLetter.getAlphabet());

		Word newBest = best.peek();

		if (newBest != null)
			System.out.println("Best word with " + bidLetter.getAlphabet()
					+ ": " + newBest.getWord() + ": " + newBest.getScore()
					+ " points");
		else
			System.out.println("No possible words yet");

		if (newBest != null && newBest.compare(newBest, bestWord) < 0) {
			// Can make a better word with this letter.
			// Bid the difference in points between our previous best word
			// And the new best word.
			System.out.println("We can gain "
					+ (newBest.getScore() - bestWord.getScore()) + " points");
			return newBest.getScore() - bestWord.getScore();

		} else {
			// letter is useless to us in short run. Bid 0 for now.
			return 0;
		}

	}

	@Override
	public int calculateOthersLetterWorth(Letter bidLetter, int playerID) {
		// Naive method. Always bid letter amount.
		return ScrabbleValues.letterScore(bidLetter.getAlphabet());
	}

	@Override
	public int calculatePersonalLetterWorth(Letter bidLetter) {
		// Naive method. Always bid letter amount.
		return ScrabbleValues.letterScore(bidLetter.getAlphabet());
	}

	@Override
	public PriorityQueue<Word> findPossibleWords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PriorityQueue<Word> findPossibleWords(Letter letter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PriorityQueue<Word> findPossibleWordsOther(int playerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PriorityQueue<Word> findPossibleWordsOther(int playerID,
			Letter letter) {
		// TODO Auto-generated method stub
		return null;
	}

	private int totalLetters(HashMap<Character, Integer> letters) {
		int s = 0;
		for (Character c : letters.keySet())
			s += letters.get(c);

		return s;
	}

	@Override
	public String returnWord(HashMap<Character, Integer> myLetters)  {
		String rv = "";

		System.out.println("===============");
		System.out.println("G3 has the following letters:");
		for (Character c : myLetters.keySet()) {
			System.out.println("\t" + c + "\t\t" + myLetters.get(c));
		}
		System.out.println("===============");

		PriorityQueue<Word> matchingWords = kb.findMatchingWord(myLetters,
				totalLetters);
		Word w = matchingWords.peek();

		if (w != null) {
			System.out.println("Word:  " + w.getWord() + ";  " + w.getScore());
			rv = w.getWord();
		} else {
			System.out.println("Major error somewhere");
		}

		return rv;
	}

}
