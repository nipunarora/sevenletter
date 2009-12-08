package seven.g2.util;

import java.util.ArrayList;

import seven.g2.ScrabbleBag;
import seven.g2.miner.LetterMine.LetterSet;

public class WordGroup {

	private ArrayList<ScrabbleWord>[] wordsByLength;
	private LetterSet ls;

	/**
	 * @return the ls
	 */
	public LetterSet getLetterSet() {
		return ls;
	}

	/**
	 * @param ls_
	 *            the ls to set
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
	public ArrayList<ScrabbleWord>[] getWordsByLength() {
		return wordsByLength;
	}

	/**
	 * @param wordsByLength_
	 *            the wordsByLength to set
	 */
	public void setWordsByLength(ArrayList<ScrabbleWord>[] wordsByLength_) {
		wordsByLength = wordsByLength_;
	}

	public ScrabbleWord[] getWords() {
		if (wordsByLength == null) {
			if (ls != null) {
				return ls.getScrabbleWords();
			} else {
				return new ScrabbleWord[] {};
			}
		} else {
			ArrayList<ScrabbleWord> allWords = new ArrayList<ScrabbleWord>();
			for (int i = 0; i < wordsByLength.length; i++) {
					allWords.addAll(wordsByLength[i]);
			}
			return allWords.toArray(new ScrabbleWord[] {});
		}
	}
}
