package seven.g5.strategies;

import java.util.ArrayList;

import seven.g5.data.Word;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.ui.Letter;

public class EmpiricalFrameworkStrategy extends Strategy {

	
	public EmpiricalFrameworkStrategy() {
		super();
		log.debug("Switching to Empirical Mode! Zoooom!");
	}

	@Override
	public int[] getBid(GameInfo gi, PlayerInfo pi) {
		//make new fake hand with new letter
		ArrayList<Letter> tempHand = new ArrayList<Letter>(pi.getRack());
		tempHand.add(gi.getCurrentBidLetter());
		
		log.debug("Hand! " + tempHand.toString());
		
		ArrayList<Word> tempAllWords = pi.getDictionaryHandler().futureAnagram(tempHand);
		ArrayList<Word> allFutureWords = pi.getDictionaryHandler().getLegitWordsFromRemainingLetters(gi.getNumberLettersRemaining(), tempAllWords);
		
		int bid = gi.getCurrentBidLetter().getValue() + 1;
		int continueStrat = 1;
		int[] ans = { bid, continueStrat };

		for(Word w : allFutureWords) {
			if(w.toString().length() == 7) {
				return ans;
			}
		}
		ans[0] = 0;
		return ans;
	}
}
