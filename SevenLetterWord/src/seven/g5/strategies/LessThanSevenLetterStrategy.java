package seven.g5.strategies;

import java.util.ArrayList;

import seven.g5.data.ScrabbleParameters;
import seven.g5.data.Word;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.ui.Letter;

public class LessThanSevenLetterStrategy extends Strategy {

	public LessThanSevenLetterStrategy() {
		super();
		System.out.println();
		System.out.println("Switching to LessThanSevenLetterStrategy Mode");
		System.out.println();
	}

	@Override
	public int[] getBid(GameInfo gi, PlayerInfo pi) {
		
		Word currentBestWord = null;
		ArrayList<Word> currentEndWordList = pi.getDictionaryHandler().pastAnagram(pi.getRack());
		if( currentEndWordList != null )
			currentBestWord = pi.getDictionaryHandler().getBestWordOfList(currentEndWordList);
		
		ArrayList<Letter> tempHand = new ArrayList<Letter>(pi.getRack());
		Word futureBestWord = null;
//		for( int c=0; c<26; c++ ) {
			tempHand.add( gi.getCurrentBidLetter() );//new Letter ((char)('A'+c),ScrabbleParameters.getScore((char)('A'+c))));
			ArrayList<Word> endWordList = pi.getDictionaryHandler().pastAnagram(tempHand);
			if( endWordList != null )
				futureBestWord = pi.getDictionaryHandler().getBestWordOfList(endWordList);
//			tempHand.remove(tempHand.size() - 1);
//		}
		
		int[] answer = { 0, 0 };
		
		if( currentBestWord != null && futureBestWord != null )
			if( futureBestWord.getScore() > currentBestWord.getScore() )
				answer[0] = futureBestWord.getScore() - currentBestWord.getScore();

		return answer;
	}
}
