package seven.g3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import seven.g3.KnowledgeBase.KnowledgeBase;
import seven.g3.KnowledgeBase.Word;
import seven.g3.Strategy.NaiveStrategy;
import seven.g3.Strategy.SevenLetterStrategy;
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
		
	@Override
	public void Register() {
		// TODO Auto-generated method stub
	}

	@Override
	public String returnWord() {
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

		/* assume that new round is about to start? */
		myLetters.clear();
		turn = 0;
		
		if(false) {
		    /* verify that we do know other players' score states */
		    System.out.print("::");
		    for(PlayerModel pm : otherPlayers.values()) 
		    {
		        System.out.print("\t" + pm.score);
		    }
		    System.out.println("");
		}
		
		return rv;
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int totalRounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
		System.out.println("totalrounds ="+totalRounds+ " playerId="+PlayerID);
		System.out.println(PlayerBidList.size());
		System.out.println("turn " + turn + " of " + (PlayerList.size() * 7 - 1));
		
		if(PlayerBidList != null && !(PlayerBidList.isEmpty())) {
			PlayerBids mostRecent = PlayerBidList.get(PlayerBidList.size()-1);
			
			if(mostRecent != null && mostRecent.getWinnerID() == PlayerID) {
				addLetter(mostRecent.getTargetLetter().getAlphabet());
				this.score -= mostRecent.getWinAmmount();
				totalLetters++;
			}
			else {
			    if(!(otherPlayers.containsKey(mostRecent.getWinnerID()))) {
			        otherPlayers.put(mostRecent.getWinnerID(), new PlayerModel(secretstate.getTotalLetters()));
			    }
			    PlayerModel winner = otherPlayers.get(mostRecent.getWinnerID());
			    
			    winner.wonBid(mostRecent.getTargetLetter().getAlphabet(), mostRecent.getWinAmmount());
			}
		}
		else {
			this.totalRounds = totalRounds;
			kb = new KnowledgeBase();
		}
		
		//Special case for when we are the last to get a letter, since Bid is not called again after that round.
		if(totalLetters == 6 && turn == PlayerList.size() * 7 - 1)
		{
			addLetter(bidLetter.getAlphabet());
			totalLetters++;
		}
		
		
		// Initialize on first turn of new round.
		if(turn == 0)
		{
			//Initialize strategy.
			//strat = new NaiveStrategy(kb, totalRounds, PlayerList);
			strat = new SevenLetterStrategy(kb, totalRounds, PlayerList);
			
			//Initialize secret letters
			myLetters = new HashMap<Character, Integer>();
			for(Letter l : secretstate.getSecretLetters())
			{
				addLetter(l.getAlphabet());
			}
		}
		
		turn++;
		
		//Update our knowledge base
		strat.update(bidLetter, PlayerBidList, secretstate, totalLetters, myLetters);
		
		return strat.calculateBidAmount(bidLetter, myLetters);
	}
	
	protected void addLetter(Character c)
	{
		if(!myLetters.containsKey(c)) {
			myLetters.put(c, 0);
		}
		
		myLetters.put(c, myLetters.get(c)+1);
		System.out.println("... " + c + ":  " + myLetters.get(c));
	}
	
	public static void addLetter(HashMap<Character, Integer> letters, Character c)
	{
		if(!letters.containsKey(c)) {
			letters.put(c, 0);
		}
		
		letters.put(c, letters.get(c)+1);
		System.out.println("... " + c + ":  " + letters.get(c));
	}
	
	/**
	 * Returns a copy of the given letter set, with the given letter added.
	 * Non-destructive: letters is not changed.
	 * @param letters A set of letters, mapped to their frequency.
	 * @param l A letter to add
	 * @return A copy of letters with l added.
	 */
	public static HashMap<Character, Integer> potentialLetterSet(HashMap<Character, Integer> letters, char l)
	{
		HashMap<Character, Integer> copy = new HashMap<Character, Integer>();
		
		//Make a deep copy of the letter set
		for(Character c : letters.keySet())
		{
			copy.put(c.charValue(), letters.get(c).intValue());
		}
		
		addLetter(copy, l);
		
		return copy;
	}

}
