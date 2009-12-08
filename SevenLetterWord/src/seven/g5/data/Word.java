package seven.g5.data;

import java.util.ArrayList;

import seven.ui.Letter;
import seven.ui.Scrabble;
import seven.g5.Logger;
import seven.g5.Logger.LogLevel;

public class Word implements Comparable<Word>{

	private Logger log = new Logger(LogLevel.DEBUG, this.getClass());
	private String string;
	/**
	 * the sum of integer values of all letters
	 */
	private int score;
	private double probability;
	private float weightedScore;

	public Word(String stringRepresentation) {
		this.setString(stringRepresentation);
		this.setScore(calculateScore(string));
	}
	
	public ArrayList<Letter> getLetters() {
		ArrayList<Letter> al = new ArrayList<Letter>();
		for (int c=0; c<string.length(); c++) {
			al.add( new Letter(string.charAt(c), ScrabbleParameters.getScore(string.charAt(c))));
		}
		return al;
	}

	private int calculateScore(String string2) {
		score = 0;
        for(int loop=0;loop<string2.length();loop++)
        {
            Character currChar = string2.charAt(loop);
            score += ScrabbleParameters.getScore(currChar);
//            log.debug(currChar+"="+ScrabbleParameters.getScore(currChar));
        }
        if( string2.length() == 7 ) {
        	score += 50;
        }
        //log.debug("score for " + string + ": " + score);
        return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(float weightedScore) {
		this.weightedScore = weightedScore;
	}

	/**
	 * @return the score
	 */
	public float getWeightedScore() {
		return weightedScore;
	}

	/**
	 * @param value the value to set
	 */
	public void setScore(int value) {
		this.score = value;
	}

	/**
	 * @return the value
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param string the string to set
	 */
	public void setString(String string) {
		this.string = string;
	}

	/**
	 * @return the string
	 */
	public String toString() {
		return string;
	}

	@Override
	public int compareTo(Word o) {
		return o.getScore() - this.getScore();
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double prob) {
		this.probability = prob;
	}

	public void setWeightedScore(float weightedScore) {
		this.weightedScore = weightedScore;
	}
}
