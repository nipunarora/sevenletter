package seven.g3.KnowledgeBase;

import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

import seven.ui.CSVReader;
import seven.g3.ScrabbleValues;
import seven.g3.Util;
import seven.g3.KnowledgeBase.*;

public class KnowledgeBase {
	static HashSet<Word> wordlist;
	public Map<Character, Integer> scrabbleBagMap = new HashMap<Character, Integer>();
	
	public static final int RARE_FREQ = 2;
	
    public KnowledgeBase()
    {
    	if(wordlist == null || wordlist.size() < 10) {
	        try{
	        	wordlist = new HashSet<Word>();
	            CSVReader csvreader = new CSVReader(new FileReader("src/seven/g3/KnowledgeBase/smallwordlist.txt"));
	            String[] nextLine;
	            //csvreader.readNext(); // Waste the first line
	            while((nextLine = csvreader.readNext()) != null)
	            {
	                String word_str = nextLine[0];
	                Word word = new Word(word_str);
	                
	                if(word.score > 0) {
	                	wordlist.add(word);
	                }
	            }
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	            System.err.println("\n Could not load dictionary!");
	        }
    	}
    	
    	initScrabbleBag();
    }

	public void initScrabbleBag() 
	{
		scrabbleBagMap.clear();
		scrabbleBagMap.put('E', ScrabbleValues.getLetterFrequency('E'));
    	scrabbleBagMap.put('A', ScrabbleValues.getLetterFrequency('A'));
    	scrabbleBagMap.put('I', ScrabbleValues.getLetterFrequency('I'));
    	scrabbleBagMap.put('O', ScrabbleValues.getLetterFrequency('O'));
    	
    	scrabbleBagMap.put('N', ScrabbleValues.getLetterFrequency('N'));
    	scrabbleBagMap.put('R', ScrabbleValues.getLetterFrequency('R'));
    	scrabbleBagMap.put('T', ScrabbleValues.getLetterFrequency('T'));
    	
    	scrabbleBagMap.put('L', ScrabbleValues.getLetterFrequency('L'));
    	scrabbleBagMap.put('S', ScrabbleValues.getLetterFrequency('S'));
    	scrabbleBagMap.put('U', ScrabbleValues.getLetterFrequency('U'));
    	scrabbleBagMap.put('D', ScrabbleValues.getLetterFrequency('P'));
    	
    	scrabbleBagMap.put('G', ScrabbleValues.getLetterFrequency('G'));
    	
    	scrabbleBagMap.put('B', ScrabbleValues.getLetterFrequency('B'));
    	scrabbleBagMap.put('C', ScrabbleValues.getLetterFrequency('C'));
    	scrabbleBagMap.put('M', ScrabbleValues.getLetterFrequency('M'));
    	scrabbleBagMap.put('P', ScrabbleValues.getLetterFrequency('P'));
    	
    	scrabbleBagMap.put('F', ScrabbleValues.getLetterFrequency('F'));
    	scrabbleBagMap.put('H', ScrabbleValues.getLetterFrequency('H'));
    	scrabbleBagMap.put('V', ScrabbleValues.getLetterFrequency('V'));
    	scrabbleBagMap.put('W', ScrabbleValues.getLetterFrequency('W'));
    	scrabbleBagMap.put('Y', ScrabbleValues.getLetterFrequency('Y'));
    	
    	scrabbleBagMap.put('K', ScrabbleValues.getLetterFrequency('K'));
    	scrabbleBagMap.put('J', ScrabbleValues.getLetterFrequency('J'));
    	scrabbleBagMap.put('X', ScrabbleValues.getLetterFrequency('X'));
    	scrabbleBagMap.put('Q', ScrabbleValues.getLetterFrequency('Q'));
    	scrabbleBagMap.put('Z', ScrabbleValues.getLetterFrequency('Z'));
	}
    
    public void printScrabbleBag()
    {
    	for(Map.Entry<Character, Integer> entry: scrabbleBagMap.entrySet()) {
    		Util.print("("+ entry.getKey() + "-" +entry.getValue()+") ");
    	}
    }
    
	public void scrabbleBagRemove(Character a) {
		Util.println("Removing char " + a  + " from bag");
		Integer integer = scrabbleBagMap.get(a);
		--integer;
		scrabbleBagMap.put(a, integer);
	}
	
	public int getRemainingScrabbleCountOf(Character a)
	{
		return scrabbleBagMap.get(a);
	}
	
	public boolean isScrabbleBagContains(Character a)
	{
		return scrabbleBagMap.get(a) > 0;
	}
	
	/**
	 * Checks if a given set of letters is contained in the scrabble bag
	 * @param letters Maps a set of letters to their frequency
	 * @return True if the scrabble bag contains all letters. False otherwise.
	 */
	public boolean isScrabbleBagContains(HashMap<Character, Integer> letters)
	{
		for(Character c : letters.keySet())
		{
			int needed = letters.get(c);
			if(needed > getRemainingScrabbleCountOf(c))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a given tile is "rare" in the current frequency set.
	 * @param c Character
	 * @return Returns true if the character appears in the scrabble bag less than RARE_FREQ times,
	 * accounting for removed tiles.
	 */
	public boolean isRare(Character c)
	{
		return (getRemainingScrabbleCountOf(c) <= RARE_FREQ);
	}
	
	/**
	 * 
	 * @param c Character
	 * @return True if there is only one of "c" left in the bag.
	 */
	public boolean isLastInstance(Character c)
	{
		return (getRemainingScrabbleCountOf(c) == 1);
	}
	
	public static PriorityQueue<Word> findPotentialWords(HashMap<Character, Integer> letters)
    {
    	return findPotentialWords(letters, 6);
    }
	
	public static PriorityQueue<Word> findPotentialWords(HashMap<Character, Integer> letters, int minLength)
    {
    	PriorityQueue<Word> rv = new PriorityQueue<Word>(10, new Word(""));
    	
    	for(Word w : wordlist) {
    		if(w.getLength() >= minLength && w.matchPossible(letters)) {
    			rv.offer(w);
    		}
    	}
    	
    	return rv;
    }
    
    
    public static PriorityQueue<Word> findMatchingWord(HashMap<Character, Integer> letters, int totalLetters)
    {
    	PriorityQueue<Word> rv = new PriorityQueue<Word>(10, new Word(""));
    	
    	for(Word w : wordlist) {
    		if(w.matchLetters(letters)) {
    			rv.add(w);
    		}
    	}
    	
    	return rv;
    }
    
    public static PriorityQueue<Word> findMatchingWord(HashMap<Character, Integer> letters, int totalLetters, char potentialLetter)
    {
    	HashMap<Character, Integer> potentialSet = potentialLetterSet(letters, potentialLetter);
    	
    	return findMatchingWord(potentialSet, totalLetters + 1);
    }
    
    public static PriorityQueue<Word> tupleScan(char a, char b)
    {

    	PriorityQueue<Word> rv = new PriorityQueue<Word>(10, new Word(""));
    	
    	if(a == b) {    	
    		for(Word w : wordlist) {
    			if(w.word.length() == 7 && w.letters.containsKey(a)) {
    				if(w.letters.get(a) >= 2) {
    					rv.add(w);
    				}
    			}
    		}
    	}
    	else {
    		for(Word w : wordlist) {
    			if(w.word.length() == 7 && w.letters.containsKey(a) && w.letters.containsKey(b)) {
    				rv.add(w);
    			}
    		}
    	}
    	
    	
    	return rv;
    }
    
    /**
	 * Returns a copy of the given letter set, with the given letter added.
	 * Non-destructive: letters is not changed.
	 * @param letters A set of letters, mapped to their frequency.
	 * @param l A letter to add
	 * @return A copy of letters with l added.
	 */
	public static HashMap<Character, Integer> potentialLetterSet(HashMap<Character, Integer> letters, char l)
	{
		HashMap<Character, Integer> copy = new HashMap<Character, Integer>();
		
		//Make a deep copy of the letter set
		for(Character c : letters.keySet())
		{
			copy.put(c.charValue(), letters.get(c).intValue());
		}
		
		addLetter(copy, l);
		
		return copy;
	}
	
	public static void addLetter(HashMap<Character, Integer> letters, Character c)
	{
		if(!letters.containsKey(c)) {
			letters.put(c, 0);
		}
		
		letters.put(c, letters.get(c)+1);
		//System.out.println("... " + c + ":  " + letters.get(c));
	}
}