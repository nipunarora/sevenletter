package seven.g2.util;

import java.util.ArrayList;

import seven.g2.ScrabbleBag;
import seven.g2.miner.LetterMine.LetterSet;

public class WordGroup {

	private ArrayList<String>[] wordsByLength;
	private LetterSet ls;
	
	/**
	 * @return the ls
	 */
	public LetterSet getLetterSet() {
		return ls;
	}

	/**
	 * @param ls_ the ls to set
	 */
	public void setLetterSet(LetterSet ls_) {
		ls = ls_;
	}

	/**
	 * @param ls_
	 */
	public WordGroup(LetterSet ls_) {
		super();
		ls = ls_;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public int getOccurrences(int i) {
		if (1 <= i && i <= 7) {
			return wordsByLength[i - 1].size();
		} else {
			return 0;
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getTotalOccurrences() {
		int total = 0;
		for (int i = 0; i < wordsByLength.length; i++) {
			total += wordsByLength[i].size();
		}

		return total;
	}

	/**
	 * @return the wordsByLength
	 */
	public ArrayList<String>[] getWordsByLength() {
		return wordsByLength;
	}

	/**
	 * @param wordsByLength_ the wordsByLength to set
	 */
	public void setWordsByLength(ArrayList<String>[] wordsByLength_) {
		wordsByLength = wordsByLength_;
	}
	
	public String[] getWords(){
		if(wordsByLength == null){
			return ls.getWords();
		}else{
			ArrayList<String> allWords = new ArrayList<String>();
			for (int i = 0; i < wordsByLength.length; i++) {
				allWords.addAll(wordsByLength[i]);
			}
			return allWords.toArray(new String[]{});
		}
	}
}
