package seven.g4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import seven.ui.Letter;

public class PatternMine {

	double fixedThreshold;
	
	SuffixTrie db;					// 7 letter words possible from current letters
	
	ArrayList<String> originalSet;		// Words excluding current letters
	ArrayList<Letter> currLetters;
	
	public PatternMine(SuffixTrie db, ArrayList<Letter> curr) {
		
		this.db = db;
		currLetters = curr;
		originalSet = buildOriginalSet(); // Builds the original set (see below)
	}
	
	/**
	 * Returns an arraylist of size 1 patterns in order of frequency
	 * @return
	 */
	public HashMap<String, Integer> getFrequentPatterns() {
		if(originalSet == null)
			return null;
		ArrayList<String> nSet = originalSet;
		PatternSet n1Set = new PatternSet();
		
		// Iterate until n1Set is size 1
		for (int i = 1; i < 7 - currLetters.size(); i++)
		{	
			n1Set = new PatternSet();
			n1Set = buildPatternSet(n1Set, nSet);
			
			
			// Returns set that is above threshold
			n1Set.buildFreqSet();
			HashMap<String, Integer> map = n1Set.getFreqSet();
			
			//System.out.println("See content of the n-1 set");
			/*for(String s: map.keySet())
			{
				System.out.println(s+'\t'+map.get(s));
			}*/
			
			nSet = n1Set.getFreqArrayList();
		}

		System.out.println();
		return n1Set.getFreqSet();
	}
	

	/**
	 * Creates size n-1 pattern set from original set of size n
	 * @param pset
	 * @return pattern set
	 */
	private PatternSet buildPatternSet(PatternSet n1_set, ArrayList<String> n_set) {
		
		// iterate through n set
		for (String s : n_set) {
			// remove one character from each string and add to a new set
			for (int i = 0; i < s.length(); i++) {
				String str = removeCharAt(s, i);
				n1_set.add(str);
			}
		}
		return n1_set;
	}
	
	/**
	 * Extracts the current letters from possible words to get original set
	 * TODO -- Use SuffixTrie?
	 * @return 
	 */
	private ArrayList<String> buildOriginalSet() {
		
		HashSet<String> origSet = new HashSet<String>();
		
		String s = "";
		for(Letter l : currLetters)
			s+=l.getAlphabet();
		
		s = CreateMemDB.sortString(s);
		
		/*for (String str : db.getSuffix(s)) {	
			origSet.add(str);
		}*/
		ArrayList<String> list = db.getSuffix(s);
		//System.out.println("Original Set Size: "+ list.size());
		return list;
		
	}
	
	/**
	 * Remove a character from a string at given position
	 * @param s
	 * @param pos
	 * @return new string without character
	 */
	private String removeCharAt(String s, int pos) {
		return s.substring(0,pos)+s.substring(pos+1);
	}
	
	public HashMap<String, Integer> getFixedNumberPatterns() {
		return null;
	}
	
	public void setFixedNumberThreshold(int n) {
		fixedThreshold = n;
	}
	
	
	
	public static void main(String[] args)
	{
		SuffixTrie db = new SuffixTrie();
		CreateMemDB.Build(db);
		ArrayList<Letter> letters = new ArrayList<Letter>();
		letters.add(new Letter('A', 1));
		letters.add(new Letter('B', 1));
		letters.add(new Letter('C', 1));
		

		
		
		PatternMine mine = new PatternMine(db, letters);
		HashMap<String, Integer> target = mine.getFrequentPatterns();		
		
		for(String s : target.keySet())
			System.out.println(s+'\t'+target.get(s));
	}

}
