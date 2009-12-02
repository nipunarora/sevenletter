package seven.g3.KnowledgeBase;

import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import seven.ui.CSVReader;
import seven.g3.KnowledgeBase.*;

public class KnowledgeBase {
	HashSet<Word> wordlist = new HashSet<Word>();

    public KnowledgeBase()
    {
        try{
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
            System.out.println("\n Could not load dictionary!");
        }
    }

    public PriorityQueue<Word> findMatchingWord(HashMap<Character, Integer> letters, int totalLetters)
    {
    	PriorityQueue<Word> rv = new PriorityQueue<Word>(10, new Word(""));

    	for(Word w : wordlist) {
    		if(w.matchLetters(letters)) {
    			rv.add(w);
    		}
    	}

    	return rv;
    }

    public PriorityQueue<Word> findMatchingWord(HashMap<Character, Integer> letters, int totalLetters, char potentialLetter)
    {
    	HashMap<Character, Integer> potentialSet = potentialLetterSet(letters, potentialLetter);

    	return findMatchingWord(potentialSet, totalLetters + 1);
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