package seven.g2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;

import seven.g2.miner.LetterMine.LetterSet;
import seven.g2.util.ScrabbleUtility;
import seven.g2.util.ScrabbleWord;
import seven.g2.util.WordGroup;
import seven.g2.util.WordList;

/**
 * Class to maintain current round's scrabble bag information. This includes
 * current tile counts for each letter This includes hidden letter counts,seen
 * letter counts and all such information.
 * 
 */

public class ScrabbleBag {

	/**
	 * For each letter maintain count of how many were seen This includes
	 * auctioned letters + personal hidden letters
	 */
	public int[] letterToTileCountSeen = new int[26];
	public int[] lettersLeft = new int[26];

	public int totalSeenTiles;

	public int totalUnknownTiles;

	/**
	 * Constructor Initializes counts
	 */
	public ScrabbleBag(int noOfPlayers, int noOfHiddenTiles,
			Character[] hiddenLetters_) {

		/** Init seen tiles counts **/
		for (int i = 0; i < letterToTileCountSeen.length; i++) {
			letterToTileCountSeen[i] = 0;
			lettersLeft[i] = ScrabbleUtility.letterToTileCount[i];
		}

		totalSeenTiles = 0;

		/** my hidden tiles are not counted **/
		totalUnknownTiles = (noOfPlayers - 1) * noOfHiddenTiles;

		/** Update my hidden tiles as seen tiles **/
		for (Character c : hiddenLetters_) {
			updateSeenTileInformation(c);
		}

	}

	/**
	 * Update seen tile information
	 * 
	 * @param c
	 */
	public void updateSeenTileInformation(Character c) {
		letterToTileCountSeen[c - 'A'] = letterToTileCountSeen[c - 'A'] + 1;
		lettersLeft[c - 'A']--;
		totalSeenTiles++;
	}

	/**
	 * Returns the probability of this letter being up for auction in the
	 * remaining auctions.
	 * 
	 * Calculated as 1 - probability of this letter not being picked in n
	 * pickings
	 * 
	 * @param c
	 * @param noOfRemainingAuctions
	 * @return
	 */
	public double getProbabilityOfAuction(Character c, int noOfRemainingAuctions) {

		int totalTilesLeft = ScrabbleUtility.TOTAL_TILE_COUNT - totalSeenTiles
				- totalUnknownTiles;

		double tilesLeftForThisLetter = (ScrabbleUtility
				.getNoOfScrabbleTiles(c) - letterToTileCountSeen[c - 'A'])
				* 1.0 * totalTilesLeft / (totalTilesLeft + totalUnknownTiles);

		double denominator = 1;
		for (int t = totalTilesLeft; t > totalTilesLeft - noOfRemainingAuctions; t--) {
			denominator = denominator * t;
		}

		double numerator = 1;
		for (double t = (totalTilesLeft - tilesLeftForThisLetter); t > totalTilesLeft
				- tilesLeftForThisLetter - noOfRemainingAuctions; t--) {
			numerator = numerator * t;
		}

		double probOfNotPickingThisLetter = numerator / denominator;

		return 1 - probOfNotPickingThisLetter;
	}

	/**
	 * Returns the probability of these letters being up for auction in the
	 * remaining auctions.
	 * 
	 * @param current
	 * @param required
	 * @param noOfRemainingAutions
	 * @return
	 */
	public double getProbabilityOfMakingThisWord(Character[] current,
			Character[] required, int noOfRemainingAuctions) {
		return 0;
	}

//	/**
//	 * Filters and returns list of words which are possible given the current
//	 * state of scrabble bag
//	 * 
//	 * @param possStrings
//	 * @param currString
//	 * @return
//	 */
//	public ArrayList<ScrabbleWord>[] filterWords(String[] possStrings, String currString) {
//		HashMap<Character, Integer> charCounts = new HashMap<Character, Integer>();
//		for (Character c : currString.toCharArray()) {
//			Integer count = charCounts.get(c);
//			if (count == null) {
//				count = 0;
//			}
//			charCounts.put(c, count - 1);
//		}
//
//		ArrayList<ScrabbleWord>[] filteredWords = new ArrayList[7];
//		for(int i=0;i<7;i++){
//			filteredWords[i] = new ArrayList<ScrabbleWord>();
//		}
//		
//		for (String s : possStrings) {
//			HashMap<Character, Integer> currCharCounts = (HashMap<Character, Integer>) charCounts
//					.clone();
//			for (Character c : s.toCharArray()) {
//				Integer count = currCharCounts.get(c);
//				if (count == null) {
//					count = 0;
//				}
//				currCharCounts.put(c, count + 1);
//			}
//
//			boolean isValid = true;
//			for (Character c : currCharCounts.keySet()) {
//				if (currCharCounts.get(c) > lettersLeft[c - 'A']) {
//					isValid = false;
//					break;
//				}
//			}
//
//			if (isValid) {
//				filteredWords[s.length()-1].add(new ScrabbleWord(s));
//			}
//		}
//
//		return filteredWords;
//	}
	
	/**
	 * Filters and returns list of words which are possible given the current
	 * state of scrabble bag
	 * 
	 * @param possStrings
	 * @param currString
	 * @return
	 */
	public ArrayList<ScrabbleWord>[] filterWords(ScrabbleWord[] possStrings, String currString) {
		HashMap<Character, Integer> charCounts = new HashMap<Character, Integer>();
		for (Character c : currString.toCharArray()) {
			Integer count = charCounts.get(c);
			if (count == null) {
				count = 0;
			}
			charCounts.put(c, count - 1);
		}

		ArrayList<ScrabbleWord>[] filteredWords = new ArrayList[7];
		for(int i=0;i<7;i++){
			filteredWords[i] = new ArrayList<ScrabbleWord>();
		}
		
		for (ScrabbleWord sw : possStrings) {
			String s = sw.getWord();
			HashMap<Character, Integer> currCharCounts = (HashMap<Character, Integer>) charCounts
					.clone();
			for (Character c : s.toCharArray()) {
				Integer count = currCharCounts.get(c);
				if (count == null) {
					count = 0;
				}
				currCharCounts.put(c, count + 1);
			}

			boolean isValid = true;
			for (Character c : currCharCounts.keySet()) {
				if (currCharCounts.get(c) > lettersLeft[c - 'A']) {
					isValid = false;
					break;
				}
			}

			if (isValid) {
				filteredWords[s.length()-1].add(new ScrabbleWord(s));
			}
		}

		return filteredWords;
	}
	
	/**
	 * 
	 * @param wg
	 */
	public void filterWordGroup(WordGroup wg,String currString){
		wg.setWordsByLength(filterWords(wg.getWords(), currString));
	}
		
	public int getSumTileCount(Set<Character> chars){
		int count = 0;
		for(Character c: chars){
			count+= lettersLeft[c - 'A'];
		}
		
		return count;
	}
	
	/**
	 * 
	 * @param wg
	 */
	public HashMap<Character,WordGroup> filteredWordGroups(HashMap<Character,LetterSet> sets,String currString){
		HashMap<Character,WordGroup> map = new HashMap<Character,WordGroup>();
		for (Character c: sets.keySet()) {
			LetterSet ls = sets.get(c);
			if(ls == null){
				sets.remove(c);
				continue;
			}
			WordGroup wg = new WordGroup(sets.get(c));
			filterWordGroup(wg, currString);
			if(wg.getTotalOccurrences() != 0){
				map.put(c, wg);
			}
		}
		return map;
	}

	public static void main(String[] args) {

	}
	
	/**
	 * Gets expected score for one word
	 * @param sw
	 * @param currString
	 * @return
	 */
	public double getExpectedScore(ScrabbleWord sw, String currString){
		return sw.getScore() * sw.getProbability(lettersLeft, currString);
	}
	
	/**
	 * Returns averaged of expected scores of all words
	 * @param words
	 * @param currString
	 * @return
	 */
	public double getExpectedScore(ScrabbleWord[] words, String currString){
		double sumExpectedScore = 0;
		for (int i = 0; i < words.length; i++) {
			sumExpectedScore += getExpectedScore(words[i], currString);
		}
		
		return sumExpectedScore;
	}
	
	/**
	 * Returns averaged of expected scores of all words
	 * @param words
	 * @param currString
	 * @return
	 */
	public double getExpectedScore(WordGroup wg, String currString){
		return getExpectedScore(wg.getWords(), currString);
	}
	
	/**
	 * Returns sum of probability of all words
	 * @param words
	 * @param currString
	 * @return
	 */
	public double getSumProbability(WordGroup wg, String currString){
		ScrabbleWord[] words = wg.getWords();
		double sumProb = 0;
		for (int i = 0; i < words.length; i++) {
			sumProb += words[i].getProbability(lettersLeft, currString);
		}
		
		return sumProb;
	}
}
