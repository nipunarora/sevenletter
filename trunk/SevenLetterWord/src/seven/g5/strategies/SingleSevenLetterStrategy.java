/**
 * this class will get the most likely 7 letter word and just bid on those letters. 
 * May be combined with KickOff to throw a rare letter into the mix.
 */

package seven.g5.strategies;

import java.util.ArrayList;

import seven.g5.Utilities;
import seven.g5.data.ScrabbleParameters;
import seven.g5.data.Word;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.ui.Letter;

public class SingleSevenLetterStrategy extends Strategy {

	public SingleSevenLetterStrategy() {
		super();
		log.debug("Switching to SingleSevenLetterStrategy Mode");
	}

	@Override
	public int[] getBid(GameInfo gi, PlayerInfo pi) {
		
		int[] answer = { (gi.getCurrentBidLetter().getValue() + 1), 1 };

		ArrayList<Letter> hand = new ArrayList<Letter>();
		for (Letter ltr: pi.getRack()) hand.add(ltr);
		ArrayList<Letter> targets = pi.getLettersToTarget();
		
		ArrayList<Word> allFutureWords = pi.getDictionaryHandler().futureAnagram(hand);

		if( allFutureWords != null ) {

			Utilities.collectOnlySevenLetters( allFutureWords );
			Utilities.calculateProbabilitiesOfWord( allFutureWords, gi );
			//log.debug("word probabilities ");
			//for( Word w: allFutureWords ) log.debug(w+": "+w.getProbability());
			log.debug("player "+pi.getPlayerId()+" mostlikelyword "+pi.getDictionaryHandler().getMostProbableWordOfList( allFutureWords ));
			Word mostLikelyWord = pi.getDictionaryHandler().getMostProbableWordOfList( allFutureWords );
		
			if ( mostLikelyWord != null ) {
				for ( int c = 0; c < mostLikelyWord.toString().length(); c++ ) {
					targets.add(
							new Letter( mostLikelyWord.toString().charAt(c), 
									ScrabbleParameters.getScore( mostLikelyWord.toString().charAt(c) ) ));
				}	
		
				for( int i=0; i < targets.size(); i++ ) {
					for ( int j=0; j<hand.size(); j++ ) {
						if (hand.get(j).getAlphabet() == targets.get(i).getAlphabet() ) {
							hand.remove(j);
							targets.remove(i);
							break;
						}
					}
				}
								
				for( Letter ltr: targets) 
					if( gi.getCurrentBidLetter().getAlphabet().equals( ltr.getAlphabet() ))
						return answer;
			}
		}
		answer[0] = 0;
		return answer;
	}
}
