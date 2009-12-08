/**
 * this class will get the most likely 7 letter word and just bid on those letters. 
 * May be combined with KickOff to throw a rare letter into the mix.
 */

package seven.g5.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import seven.g5.Utilities;
import seven.g5.data.OurLetter;
import seven.g5.data.ScrabbleParameters;
import seven.g5.data.Word;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.ui.Letter;

public class MostPossibleWordsStrategy extends Strategy {

	public MostPossibleWordsStrategy() {
		super();
		log.debug("Switching to MostPossibleWordsStrategy Mode");
	}

	@Override
	public int[] getBid(GameInfo gi, PlayerInfo pi) {
		
		ArrayList<OurLetter> targets = new ArrayList<OurLetter>();//.getLettersToTarget();
				
		int numLettersToReturn = gi.getPlayerList().size() + 2;
		targets = pi.getDictionaryHandler().getLettersWithMostFutureWords( pi, gi, numLettersToReturn );
//		for( OurLetter ltr: targets ) {
//			System.out.println(ltr.getAlphabet() + " has "+ltr.getNumWordsPossibleWithThisAdditionalLetter());
//		}
		
		Random r = new Random();
		
		//set answer to bid, and flag to continue strat
		int[] answer = { 0, 1 };
		
		if( targets.size() == 0 ) {
			//emergency mimicking of LessThanSevenLetterStrategy
			//sets continue strategy flag to no
			//finds best bid
			answer = Utilities.mimicLessThanSevenStrategy(pi,gi);
			}
		else {
			for( OurLetter ltr: targets) {
				if( gi.getCurrentBidLetter().getAlphabet().equals( ltr.getAlphabet() )) {
					int whichTurn = gi.getPlayerList().size() * 7 - gi.getNoOfTurnsRemaining();
					int scaledTurnScore = (6 * whichTurn) / (gi.getPlayerList().size() * 7);
					System.out.println("scaledTurnScore: "+scaledTurnScore);
					int whichIndex = targets.size() - targets.indexOf(ltr);
					int scaledIndexScore = (6 * whichIndex) / (targets.size());
					System.out.println("scaled index score: "+scaledIndexScore);
					answer[0] = scaledTurnScore + scaledIndexScore + r.nextInt(3);
					return answer;
				}
			}
		}
		
		//set bid to 0
		answer[0] = 0;
		return answer;
	}
}
