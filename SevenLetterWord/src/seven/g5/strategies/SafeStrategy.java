package seven.g5.strategies;

import java.util.ArrayList;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.g5.DictionaryHandler;
import seven.g5.data.Word;
import seven.ui.Letter;

public class SafeStrategy extends Strategy {

	private ArrayList<Letter> rack;
	private Word bestWord = null;
	
	//this just checks the new possible words and returns the score - 1 of 
	//the new letter if it increases the best word score
	@Override
	public int getBid(GameInfo gi, PlayerInfo pi) {
		this.rack = pi.getRack();
		
		//first update the best word for what you have
		Word currBestWord = pi.getDictionaryHandler().getBestWord(this.rack);
		if(currBestWord.getScore() > this.bestWord.getScore()) {
			this.bestWord = currBestWord;
		}
		
		//get the possible highest scoring word
		Word newWord = pi.getDictionaryHandler().getBestWordWithAddition(this.rack, gi.getCurrentBid());
		
		//get the difference in scores
		if((newWord.getScore() - this.bestWord.getScore()) > gi.getCurrentBid().getValue()) {
			return gi.getCurrentBid().getValue() - 1;
		} else {
			return 0;
		}
	}

	@Override
	public String getFinalWord() {
		// TODO Auto-generated method stub
		return this.bestWord.toString();
	}

}
