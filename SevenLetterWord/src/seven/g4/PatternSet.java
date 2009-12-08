package seven.g4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class PatternSet {

	private HashMap<String, Integer> set;			// maps a string to its frequency
	private HashMap<String, Integer> freqSet;		// gets a limited set based on threshold
	
	public HashMap<String, Integer> getFreqSet() {
		return freqSet;
	}


	double freqSum;    // sum of frequencies
	int patternSum; // number of patterns found
	double ratioThreshold;
	
	public PatternSet() {
		set = new HashMap<String, Integer>();
	}
	
	/**
	 * Adds the string to the map and increases frequency
	 * @param s the string
	 */
	public void add(String s) {
		
		//No necessary, because it's already sorted.
		//String ps = CreateMemDB.sortString(s);
		
		if (patternExists(s)) {
			
			int freq = set.get(s);
			set.put(s, ++freq);
		} else {
			set.put(s, 1);
		}
		
	}
	
	/**
	 * Creates a list of patterns that meet threshold
	 * @param threshold
	 */
	public HashMap<String, Integer> buildFreqSet() {
		
		buildSums();
		freqSet = new HashMap<String, Integer>();
		
		for (String s : set.keySet()) {			
			int freqP = set.get(s);	// frequency value
			double freqSupport = freqP / freqSum;			

// freq of pattern over total amount of frequencies
			//double patternSupport = freqP / patternSum;		

// freq of pattern over total number of patterns found
			
			// pick those over threshold
			if (freqSupport >= ratioThreshold) {
				freqSet.put(s, freqP);
			}			
		}
		
		// sorts by highest frequency to lowest
		//freqSet = sortSetByFreq(freqSet);		
		return freqSet;
	}
	
	
	/**
	 * Returns arraylist of strings
	 * @return
	 */
	public ArrayList<String> getFreqArrayList() {
		ArrayList<String> array = new ArrayList<String>();
		
		// How many counts, add this string how many times
		for (String s : freqSet.keySet()) {
			for(int i = 0; i < freqSet.get(s); i++)
				array.add(s);
		}
		return array;
	}
	
	/**
	 * Returns hash set of strings
	 * @return
	 */
	public HashSet<String> getHashSet() {
		return (HashSet<String>) set.keySet();
	}

	/**
	 * Sorts the given set by frequency from high to low
	 */
	private HashMap<String, Integer> sortSetByFreq(HashMap<String, 

Integer> s) {

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		int freq;
		int maxFreq = 0;
		String maxStr = null;
		
		while (!s.isEmpty()) {
			for (String str : s.keySet()) {
				freq = s.get(str);
				if (freq > maxFreq) {
					maxFreq = freq;
					maxStr = str;
				}
			}
		
			map.put(maxStr, maxFreq);
			s.remove(maxStr);
		}
		
		return map;
		
	}
	
	/**
	 * Checks if pattern exists in the set already
	 * @param str string in question
	 * @return the pattern string if it is true. null otherwise.
	 */
	private boolean patternExists(String str) {
		
		//String str_alpha = getAlphaPattern(str);
		
		if(set.containsKey(str))
				return true;		
		return false;
	}
	
	
	/** 
	 * Sets the sum values
	 */
	private void buildSums() {
		
		
		freqSum = 0;
		
		for (int i : set.values()) {
			freqSum += i;
		}
		ratioThreshold = set.size()/freqSum * 0.001;
		//System.out.println("See ratio threshold: "+ratioThreshold);
		/*
		for(String s: set.keySet())
		{
			// 
			freqSum/=(s.length()+1);
			System.out.println("Frequent Sum: "+freqSum);
			break;
		}
		*/
		
		/*HashSet<String> patterns = (HashSet<String>) set.keySet();
		
		patternSum = patterns.size();
		*/
		
		
	}

	public HashMap<String, Integer> getSet() {
		return set;
	}
	
}












