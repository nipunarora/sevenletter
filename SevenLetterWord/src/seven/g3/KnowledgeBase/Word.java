package seven.g3.KnowledgeBase;

import java.util.Comparator;
import java.util.HashMap;

import seven.g3.ScrabbleValues;

public class Word implements Comparator<Word>{
	protected String word;
	protected HashMap<Character, Integer> letters = new HashMap<Character, Integer>();
	protected int score;
	
	public int getScore() {
		return this.score;
	}
	
	public String getWord() {
		return this.word;
	}
	
	public Word(String str)
	{
		this.word = str;
		this.score = ScrabbleValues.getWordScore(str);
		
        for(int loop=0;loop<word.length();loop++)
        {
        	Character currChar = word.charAt(loop);
        	if(letters.containsKey(currChar)) {
        		letters.put(currChar, (letters.get(currChar) + 1));
        	}
        	else {
        		letters.put(currChar, 1);
        	}
        }
        
        for(Character c : letters.keySet()) {
        	int v = ScrabbleValues.getLetterFrequency(c);
        	if(v < letters.get(c)) {
        		/* rem, fail */
        		score = -1;
        	}
        }
	}
	
	public int hasLetter(char c, int i)
	{
		if(letters.containsKey(c)) {
			int v = letters.get(c);
			return v - i;
		}
		else {
			return -i;
		}
	}
	
	public boolean matchLetters(HashMap<Character, Integer> match)
	{
		boolean rv = true;
		
		for(Character c : letters.keySet()) {
			if(match.containsKey(c)) {
				int v_them = match.get(c);
				int v_me   = letters.get(c);
				
				if(v_them < v_me) {
					rv = false;
					break;
				}
			}
			else {
				rv = false;
				break;
			}
		}
		
		return rv;
	}


	@Override
	public int compare(Word o1, Word o2) {
		// TODO Auto-generated method stub
		if(o1.score > o2.score){
			return -1;
		}
		else if(o1.score < o2.score){
			return 1;
		}
		else {
			int freq0 = 0;
			int freq1 = 0;
			
			for(Character c : o1.letters.keySet()) {
				freq0 += ScrabbleValues.getLetterFrequency(c);
			}
			
			for(Character c : o2.letters.keySet()) {
				freq1 += ScrabbleValues.getLetterFrequency(c);
			}
			
			if(freq0 < freq1) {
				return 1;
			}
			else if(freq0 > freq1) {
				return -1;
			}
			else {
				return 0;
			}
		}
	}
	
}
