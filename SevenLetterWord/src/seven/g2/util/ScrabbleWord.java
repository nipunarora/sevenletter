package seven.g2.util;

import java.util.ArrayList;

/**
 * Scrabble Word Class encapsulates word and score
 */
public class ScrabbleWord implements Comparable<ScrabbleWord> {
	/**
	 * 
	 */
	public ScrabbleWord() {
	}
	/**
	 * @param word_
	 */
	public ScrabbleWord(String word_) {
		setWord(word_);
	}

	String word;
	int score;
	
	/**
	 * Return the probability of forming this word
	 * @param is Array of all of the letters that we know exist to be grabbed
	 * @param string Array of all of the letters that we have
	 * @return
	 */
	public double getProbability(int[] is, String string)
	{
		double p = 1;
		double b = 1;
		ArrayList<Character> lettersToGet = new ArrayList<Character>();
		ArrayList<Character> freeLetters = new ArrayList<Character>();
		for(Character c : word.toCharArray())
			lettersToGet.add(c);
		for(int i =0;i<is.length;i++)
		{
			for(int j=0;j<is[i];j++)
				freeLetters.add((char) ('A' + i));
		}
		for(Character c : string.toCharArray())
			if(lettersToGet.contains(c))
				lettersToGet.remove(c);
		
		for(Character c : lettersToGet)
		{
			int n = 0;
			for(Character d : freeLetters)
			{
				if(c.equals(d))
					n++;
			}
			if(n==0)
				return -1;
			else
			{
				p *= n;
				b *= freeLetters.size();
			}
			freeLetters.remove(c);
		}
		return p/b;
	}
	
	@Override
	public int compareTo(ScrabbleWord o) {
		return score - o.score;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + word + "=" + score + "]";
	}
	
	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word_ the word to set
	 */
	public void setWord(String word_) {
		word = word_;
		if(word != null)
			score = ScrabbleUtility.getScrabbleWordScoreWithBonus(word);
		else
			score = 0;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score_ the score to set
	 */
	public void setScore(int score_) {
		score = score_;
	}
}
