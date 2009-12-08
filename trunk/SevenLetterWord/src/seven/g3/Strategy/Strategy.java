package seven.g3.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import seven.g3.Util;
import seven.g3.KnowledgeBase.*;
import seven.ui.*;


public abstract class Strategy 
{
	protected KnowledgeBase kb;
	protected ArrayList<String> playerList;
	protected int totalRounds;
	//protected HashMap<Character, Integer> letters;
	protected Word bestWord;
	protected int bestWordValue;
	protected int totalLetters;
	
	public Strategy(KnowledgeBase kb, int totalRounds, ArrayList<String> playerList)
	{
		this.kb = kb;
		this.playerList = playerList;
		this.totalRounds = totalRounds;
		//this.letters = letters;
		
		bestWord = new Word("");
		Util.println(bestWord.getWord()+ " " + bestWord.getScore());
	}
	
	/**
	 * Update our knowledge base with new information each turn.
	 * Such as updating letter frequencies, analyzing bid patterns and such.
	 * @param bidLetter	The letter being bid for.
	 * @param PlayerBidList What players bid for the last letter.
	 * @param secretstate Our current score, secret letters.
	 */
	abstract public void update(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList, SecretState secretstate, int numLetters, HashMap<Character, Integer> letters);
	
	/**
	 * How much should we bid for the letter?
	 * @param bidLetter The letter being bid on.
	 * @return The amount of our bid.
	 */
	abstract public int calculateBidAmount(Letter bidLetter, HashMap<Character, Integer> letters, int paidThisRound);
	
	/**
	 *  How much should we bid for the letter
	 *  without considering other players.
	 * @param bidLetter The letter being bid on.
	 * @return How much the letter is worth to us.
	 */
	//abstract public int calculatePersonalLetterWorth(Letter bidLetter);
	
	/**
	 *  How much we think a letter is worth to another player.
	 * @param bidLetter The letter being bid on.
	 * @param playerID The ID number of another player.
	 * @return How much we think that letter is worth to another player.
	 */
	//abstract public int calculateOthersLetterWorth(Letter bidLetter, int playerID);
	
	/**
	 * Finds a list of all words we can make with our current
	 * set of letters.
	 * @return A HashSet of Words.
	 */
	//abstract public PriorityQueue<Word> findPossibleWords();
	
	/**
	 *  Finds a list of all words we can make
	 *  with our current set of letters, plus an extra
	 *  specified letter.
	 * @param letter A letter.
	 * @return A list of all possible words we can make using our secret set, plus letter.
	 */
	//abstract public PriorityQueue<Word> findPossibleWords(Letter letter);
	
	/**
	 * Finds a list of all possible words another player
	 * can make.
	 * @param playerID Number identifying another player
	 * @return All words that player can make.
	 */
	//abstract public PriorityQueue<Word> findPossibleWordsOther(int playerID);
	
	/**
	 * Finds a list of all possible words another player
	 * can make if he wins the latest letter.
	 * @param playerID Number identifying another player
	 * @param letter A letter
	 * @return All words that player can make if he gets that letter.
	 */
	//abstract public PriorityQueue<Word> findPossibleWordsOther(int playerID, Letter letter);
	
	abstract public String returnWord(HashMap<Character, Integer> myLetters );
	
	abstract public boolean hasFailed();
}
