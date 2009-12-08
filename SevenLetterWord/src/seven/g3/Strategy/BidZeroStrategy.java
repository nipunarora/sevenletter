package seven.g3.Strategy;

import java.util.ArrayList;
import java.util.HashMap;

import seven.g3.KnowledgeBase.KnowledgeBase;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class BidZeroStrategy extends Strategy  {

	public BidZeroStrategy(KnowledgeBase kb, int totalRounds,
			ArrayList<String> playerList) {
		super(kb, totalRounds, playerList);
	}

	@Override
	public int calculateBidAmount(Letter bidLetter,
			HashMap<Character, Integer> letters, int paidThisRound) {
		return 0;
	}

	@Override
	public String returnWord(HashMap<Character, Integer> myLetters) {
		return null;
	}

	@Override
	public void update(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			SecretState secretstate, int numLetters,
			HashMap<Character, Integer> letters) {
		
	}
	
	public boolean hasFailed()
	{
		return false;
	}

}
