package seven.g3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import seven.g3.KnowledgeBase.KnowledgeBase;
import seven.g3.KnowledgeBase.Word;
import seven.g3.Strategy.MetaStrategy;
import seven.g3.Strategy.NaiveStrategy;
import seven.g3.Strategy.HighFrequencyStrategy;
import seven.g3.Strategy.Strategy;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class SimplePlayer implements seven.ui.Player {

	KnowledgeBase kb;
	HashMap<Character, Integer> myLetters = new HashMap<Character, Integer>();
	protected int totalLetters = 0;
	int turn = 0;
	protected Strategy strat;
	int totalRounds;
	boolean switchStrategy = false;

	protected HashMap<Integer, PlayerModel> otherPlayers = new HashMap<Integer, PlayerModel>();
	int score = 100;

	static Set<Integer> myplayerList = new HashSet<Integer>();
	static int myplayerCount = 0;
	
	int paidThisRound = 0;


	@Override
	public void Register() {
		// TODO Auto-generated method stub
	}

	@Override
	public String returnWord() {
		String rv = "";

		Util.println("===============");
		Util.println("G3 has the following letters:");
		for (Character c : myLetters.keySet()) {
			Util.println("\t" + c + "\t\t" + myLetters.get(c));
		}
		Util.println("===============");

		PriorityQueue<Word> matchingWords = KnowledgeBase.findMatchingWord(myLetters,
				totalLetters());
		Word w = matchingWords.peek();

		if (w != null) {
			Util.println("Word:  " + w.getWord() + ";  " + w.getScore());
			rv = w.getWord();
		} else {
			Util.println("Major error somewhere");
		}

		/* assume that new round is about to start? */
		totalLetters = 0;
		myLetters.clear();
		turn = 0;
		paidThisRound = 0;

		if (false) {
			/* verify that we do know other players' score states */
			Util.print("::");
			for (PlayerModel pm : otherPlayers.values()) {
				Util.print("\t" + pm.score);
			}
			Util.println("");
		}

		return rv;
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int totalRounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
		
		if (!myplayerList.contains(PlayerID)) {
			myplayerList.add(PlayerID);
		}

		Util.print("Player= " + PlayerID + "=====>");
		for (int id : myplayerList) {
			Util.print(id + " ");
		}
		Util.println("totalrounds =" + totalRounds + " playerId=" + PlayerID);
		Util.println("" + PlayerBidList.size());
		int totalTurns = PlayerList.size() * 7 - 1;
		Util.println("turn " + turn + " of " + totalTurns);
		

		if (PlayerBidList != null && !(PlayerBidList.isEmpty())) {
			PlayerBids mostRecent = PlayerBidList.get(PlayerBidList.size() - 1);

			if (mostRecent != null && mostRecent.getWinnerID() == PlayerID) {
				addLetter(mostRecent.getTargetLetter().getAlphabet());
				this.score -= mostRecent.getWinAmmount();
				paidThisRound += mostRecent.getWinAmmount();
				totalLetters++;
			} else {
				if (!(otherPlayers.containsKey(mostRecent.getWinnerID()))) {
					otherPlayers.put(mostRecent.getWinnerID(), new PlayerModel(
							secretstate.getTotalLetters()));
				}
				PlayerModel winner = otherPlayers.get(mostRecent.getWinnerID());

				winner.wonBid(mostRecent.getTargetLetter().getAlphabet(),
						mostRecent.getWinAmmount());
			}
		} else {
			this.totalRounds = totalRounds;
			
			kb = new KnowledgeBase();
		}

		// Special case for when we are the last to get a letter, since Bid is
		// not called again after that round.
		if (totalLetters() == 6 && turn == PlayerList.size() * 7 - 1) {
			addLetter(bidLetter.getAlphabet());
			totalLetters++;
		}

		// Initialize on first turn of new round.
		if (turn == 0) {
			// Initialize strategy.
			ArrayList<Letter> secretLetters = secretstate.getSecretLetters();
//			getStrategy(totalRounds, PlayerList, secretLetters);
			kb.initScrabbleBag();
			strat = null;
			strat = new MetaStrategy(kb, totalRounds, PlayerList);
			// Initialize secret letters
			myLetters = new HashMap<Character, Integer>();
			for (Letter l : secretLetters) {
				addLetter(l.getAlphabet());
				kb.scrabbleBagRemove(l.getAlphabet());
			}
		}

		turn++;
		int calculateBidAmount;
		
		if(totalLetters() != 7) {
			// Update our knowledge base
			strat.update(bidLetter, PlayerBidList, secretstate, totalLetters(),
						myLetters);
	
			calculateBidAmount = strat.calculateBidAmount(bidLetter, myLetters, paidThisRound);
		}
		else {
			calculateBidAmount = 0;
		}

		// remove after calculate bid so "last instance" works correctly
		kb.scrabbleBagRemove(bidLetter.getAlphabet());
		kb.printScrabbleBag();

		return calculateBidAmount;
	}

	public static boolean hasRequiredLetters(String required, List<Letter> list) {
		String requiredWord = required;

		for (Letter s : list) {
			char c = s.getAlphabet();
			int indexOf = requiredWord.indexOf(c);
			if (indexOf == -1) {
				Util.println(s + " is not in the list of required words");
				return false;
			} else {
				requiredWord = requiredWord.replace("" + c, "");
			}
		}

		return true;
	}

	protected void addLetter(Character c) {
		if (!myLetters.containsKey(c)) {
			myLetters.put(c, 0);
		}

		myLetters.put(c, myLetters.get(c) + 1);
		Util.println("... " + c + ":  " + myLetters.get(c));
	}

	public static void addLetter(HashMap<Character, Integer> letters,
			Character c) {
		if (!letters.containsKey(c)) {
			letters.put(c, 0);
		}

		letters.put(c, letters.get(c) + 1);
		Util.println("... " + c + ":  " + letters.get(c));
	}

	/**
	 * Returns a copy of the given letter set, with the given letter added.
	 * Non-destructive: letters is not changed.
	 * 
	 * @param letters
	 *            A set of letters, mapped to their frequency.
	 * @param l
	 *            A letter to add
	 * @return A copy of letters with l added.
	 */
	public static HashMap<Character, Integer> potentialLetterSet(
			HashMap<Character, Integer> letters, char l) {
		HashMap<Character, Integer> copy = new HashMap<Character, Integer>();

		// Make a deep copy of the letter set
		for (Character c : letters.keySet()) {
			copy.put(c.charValue(), letters.get(c).intValue());
		}

		addLetter(copy, l);

		return copy;
	}
	
	public int totalLetters()
	{
		int sum = 0;
		
		for(Character c : myLetters.keySet())
		{
			sum+= myLetters.get(c); 
		}
		
		return sum;
	}

}
