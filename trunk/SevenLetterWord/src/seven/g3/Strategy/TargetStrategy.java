package seven.g3.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import seven.g3.Util;
import seven.g3.KnowledgeBase.KnowledgeBase;
import seven.g3.KnowledgeBase.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.Scrabble;
import seven.ui.SecretState;

public class TargetStrategy extends Strategy {
	
	ArrayList<Word> targets;
	HashMap<Word, HashMap<Character, Integer>> remainingLetters;
	int totalLetters;
	int totalLengths;
	char previousChar;
	
	public static final double BID_MODIFIER = 2.5;
	public static final double FREQ_CUTOFF = 0.1;
	public static final int MAX_LETTERS_BEFORE_CHANGE = 7;
	public static final double LAST_INSTANCE_MULTIPLIER = 1.25;
	public static final double RARE_LETTER_MULTIPLIER = 1.2;
	
	/**
	 * Targets a predefined list of words.
	 * @param kb Knowledge Base
	 * @param totalRounds Number of rounds in the game
	 * @param playerList Players in the game
	 * @param targets Which words to specifically target
	 */
	public TargetStrategy(KnowledgeBase kb, int totalRounds, ArrayList<String> playerList, ArrayList<Word> targets)
	{
		super(kb, totalRounds, playerList);
		initialize(kb, totalRounds, playerList, targets);
	}
	
	/**
	 * Targets the words which are possible based on a set of given letters
	 * @param kb Knowledge base
	 * @param totalRounds The total number of rounds
	 * @param playerList THe players competing
	 * @param letters A fixed set of letters that all targets must share
	 * @param minLength The smallest (inclusive) word length to target
	 */
	public TargetStrategy(KnowledgeBase kb, int totalRounds, ArrayList<String> playerList, HashMap<Character, Integer> letters, int minLength)
	{
		super(kb, totalRounds, playerList);
		
		//Find all possible words this letter set can make above a given length
		PriorityQueue<Word> possibleWords = KnowledgeBase.findPotentialWords(letters, minLength);
		ArrayList<Word> newTargets = new ArrayList<Word>();
		
		boolean stop = false;
		Word w;
		
		//Extract all possible words whose lengths are at least minLength
		while(possibleWords.size() > 0 && !stop)
		{
			w = possibleWords.poll();
			if(w.getLength() >= minLength)
				newTargets.add(w);
			//else
			//	stop = true;
		}
		
		initialize(kb, totalRounds, playerList, newTargets);
	}
	
	/**
	 * Targets the words which are possible based on a set of given letters
	 * @param kb Knowledge base
	 * @param totalRounds The total number of rounds
	 * @param playerList THe players competing
	 * @param letters A fixed set of letters that all targets must share
	 * @param minLength The smallest (inclusive) word length to target
	 * @param percent The (top) percent of all applicable words to target, based on word score
	 */
	public TargetStrategy(KnowledgeBase kb, int totalRounds, ArrayList<String> playerList, HashMap<Character, Integer> letters, int minLength, double percent)
	{
		super(kb, totalRounds, playerList);
		
		//Find all possible words this letter set can make above a given length
		PriorityQueue<Word> possibleWords = KnowledgeBase.findPotentialWords(letters, minLength);
		ArrayList<Word> newTargets = new ArrayList<Word>();
		
		boolean stop = false;
		Word w;
		
		int totalWords = possibleWords.size();
		int targetAmount = (int)(possibleWords.size() * (percent / 100.0));
		
		//Extract the top-scoring possible words whose lengths are at least minLength
		while(possibleWords.size() >= totalWords - targetAmount && !stop)
		{
			w = possibleWords.poll();
			if(w.getLength() >= minLength)
				newTargets.add(w);
			//else
			//	stop = true;
		}
		
		initialize(kb, totalRounds, playerList, newTargets);
	}
	
	/**
	 *  Initialize internal data members shared by the constructors.
	 * @param kb Knowledge base.
	 * @param totalRounds Total number of rounds
	 * @param playerList What players are competing
	 * @param targets What words are we targeting
	 */
	public void initialize(KnowledgeBase kb, int totalRounds, ArrayList<String> playerList, ArrayList<Word> targets)
	{
		this.targets = targets;
		
		remainingLetters = new HashMap<Word, HashMap<Character, Integer>>();
		
		totalLengths = 0;
		
		//Maps each target word to an array of what letters we have left to attain
		for(Word word : targets)
		{
			String s = word.getWord();
			HashMap<Character, Integer> charFreqs = new HashMap<Character, Integer>();
			for(int i = 0; i < s.length(); i++)
			{
				Character c = s.charAt(i);
				if(charFreqs.keySet().contains(c))
				{
					int freq = charFreqs.get(c);
					charFreqs.put(c, freq + 1);
				}
				else
				{
					charFreqs.put(c, 1);
				}
			}
			remainingLetters.put(word, charFreqs);
			
			totalLengths += s.length();
		}
		
		totalLetters = 0;
	}
	
	public int lettersRemaining(Word word)
	{
		if(remainingLetters.containsKey(word))
		{
			HashMap<Character, Integer> letters = remainingLetters.get(word);
			int rv = 0;
			
			for(Character c : letters.keySet())
			{
				rv+= letters.get(c);
			}
			return rv;
		}
			
		else
			return word.getWord().length();
	}
	
	public boolean wordNeedsLetter(Word word, Character letter)
	{
		if(remainingLetters.containsKey(word))
		{
			HashMap<Character, Integer> letters = remainingLetters.get(word);
			if(letters.containsKey(letter))
				return letters.get(letter) > 0;
			else
				return false;
		}
		else
			return false;
	}

	@Override
	public int calculateBidAmount(Letter bidLetter,
			HashMap<Character, Integer> letters, int paidThisRound) {
		
		/**
		 *  Return -1 when signal strategy change.
		 */
		if(totalLetters > MAX_LETTERS_BEFORE_CHANGE)
		{
			//Enough letters
			return -1;
		}
		
		if(numPossibleWords() <= 0)
		{
			return -1;
		}
		
		int sevenBid = 0;
		
		char letter = bidLetter.getAlphabet();
		double freq = 0;
		for(Word word : targets)
		{
			//ArrayList<Character> remain = remainingLetters.get(word);
			if(wordNeedsLetter(word, letter))
			{
				//Closer to making word, higher value
				freq+= word.getWord().length() - lettersRemaining(word);
				
				//Test if this letter will finish the word
				if(lettersRemaining(word) == 1)
				{
					int tempBid = word.getScore() - paidThisRound;
					if(tempBid > sevenBid)
						tempBid = sevenBid;
				}
					 
			}
			
		}
		
		int letterScore = Scrabble.letterScore(letter);
		
		//if it will finish a seven-letter word, go for it!
		if(sevenBid > 0)
			return sevenBid;
		
		//If it's our last letter, and it *won't* finish a seven letter word
		//don't bid on it.
		if(totalLetters == 6 && sevenBid != 0)
			return 0;
		
		//If it's not in any words, don't bid.
		if(freq == 0)
			return 0;
		
		//If it's in one word...
		//if(freq == 1)
		//	return letterScore;
		
		double proportion = freq / maxFreq();
		Util.println("Freq: " + freq + " Max: " + maxFreq());
		
		//Don't bid if it's not in enough words, proportionally
		if(proportion < FREQ_CUTOFF)
			return 0;
		
		Util.println("Freq proportion " + proportion);
		
		int initialBid = (int)((double)((letterScore + myLetterScore(letters) + 50)/7) * (proportion) * BID_MODIFIER);
		
		//Bid more for rare letters
		if(kb.isLastInstance(letter))
		{
			initialBid *= LAST_INSTANCE_MULTIPLIER;
		}
		else if(kb.isRare(letter))
		{
			initialBid *= RARE_LETTER_MULTIPLIER;
		}
		
		//Bid proportional to how many words in the list have that letter
		//Clamped to [letterScore, 3 * letterScore]
		return initialBid;
	}
	
	public int clamp(int value, int min, int max)
	{
		return Math.min(min, Math.max(value, max));
	}
	
	public int myLetterScore(HashMap<Character, Integer> letters)
	{
		int sum = 0;
		
		for(Character c : letters.keySet())
		{
			sum+= letters.get(c);
		}
		
		return sum;
	}

	@Override
	public String returnWord(HashMap<Character, Integer> myLetters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			SecretState secretstate, int numLetters,
			HashMap<Character, Integer> letters) {
		// TODO Auto-generated method stub
		
		if(totalLetters == 0)
		{
			//On first run, account for secret letters
			for(char c : letters.keySet())
			{
				updateLetterFrequencies(c);
			}
			totalLetters = numLetters;
			updatePossibleWords();
		}
		else if(numLetters > totalLetters)
		{
			//If win a bid, update the frequencies.
			totalLetters = numLetters;
			updateLetterFrequencies(previousChar);
			updatePossibleWords();
		}
		
		previousChar = bidLetter.getAlphabet();
		
		Util.println(targets.size() + " possible 7-letter words: " + printPossibleWords());

	}
	
	public void updateLetterFrequencies(Character c)
	{
		//Remove character from remaining letters of each word if applicable
		for(Word word : targets)
		{
			HashMap<Character,Integer> l = remainingLetters.get(word);
			if(l.keySet().contains(c))
			{
				l.put(c, l.get(c) - 1);
			}
		}
	}
	
	public String printPossibleWords()
	{
		String s = "";
		for(Word w : targets)
		{
			s+= w.getWord() + " ";
		}
		
		return s;
	}
	
	public void updatePossibleWords()
	{
		ArrayList<Word> wordsToRemove = new ArrayList<Word>();
		
		for(Word word: targets)
		{
			if(lettersRemaining(word) > 7 - totalLetters || !lettersRemainingInBag(word))
			{
				wordsToRemove.add(word);
			}
		}
		
		for(Word word: wordsToRemove)
		{
			if(targets.contains(word))
			{
				targets.remove(word);
				remainingLetters.remove(word);	
			}
		}
	}
	
	public boolean lettersRemainingInBag(Word w)
	{
		return kb.isScrabbleBagContains(remainingLetters.get(w));
	}
	
	public int numPossibleWords()
	{
		return targets.size();
	}
	
	public int maxFreq()
	{
		int max = 0;
		
		for(Word word : targets)
		{
				//Closer to making word, higher value
				max+= word.getWord().length() - lettersRemaining(word);
		}
		
		return max;
	}
	
	public static ArrayList<Word> commonWords()
	{
		ArrayList<Word> list = new ArrayList<Word>();
		
		list.add(new Word("ANTIAIR"));
		list.add(new Word("ARENITE"));
		list.add(new Word("ERINITE"));
		list.add(new Word("ETAERIO"));
		list.add(new Word("INERTIA"));
		list.add(new Word("ORATION"));
		list.add(new Word("OTARINE"));
		list.add(new Word("TAENIAE"));
		
		return list;
	}

	public boolean hasFailed()
	{
		System.out.println("Num possible" + numPossibleWords());
		// Fails when it has enough letters, or when all targets are unreachable.
		if(totalLetters > MAX_LETTERS_BEFORE_CHANGE || numPossibleWords() <= 0)
			return true;
		else
			return false;
	}
	
}
