package seven.g2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import seven.g2.miner.LetterMine.LetterSet;
import seven.g2.util.ScrabbleUtility;
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
	private int[] letterToTileCountSeen = new int[26];
	private int[] lettersLeft = new int[26];
	private double[] estimatedTilesLeft = new double[26];

	private int totalSeenTiles;

	private int totalUnknownTiles;

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

	/**
	 * Filters and returns list of words which are possible given the current
	 * state of scrabble bag
	 * 
	 * @param possStrings
	 * @param currString
	 * @return
	 */
	public ArrayList<String>[] filterWords(String[] possStrings, String currString) {
		HashMap<Character, Integer> charCounts = new HashMap<Character, Integer>();
		for (Character c : currString.toCharArray()) {
			Integer count = charCounts.get(c);
			if (count == null) {
				count = 0;
			}
			charCounts.put(c, count - 1);
		}

		ArrayList<String>[] filteredWords = new ArrayList[7];
		for(int i=0;i<7;i++){
			filteredWords[i] = new ArrayList<String>();
		}
		
		for (String s : possStrings) {
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
				filteredWords[s.length()-1].add(s);
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
		System.out.println("1");
		WordList wl = new WordList();

		System.out.println("2:"+ System.currentTimeMillis());
		ScrabbleBag sb = new ScrabbleBag(1, 0, new Character[] {});

		LetterSet ls1 = wl.getLetterGroup("B");
		LetterSet ls2 = wl.getLetterGroup("BB");
		
		System.out.println(ls1.getTotalOccurrences());
		System.out.println(ls2.getTotalOccurrences());


		System.out.println("3:"+System.currentTimeMillis());
		int total = 0;
		ArrayList<String>[] words = sb.filterWords(ls1.getWords(), "");
		for (int i = 0; i < 7; i++) {
			total+= words[i].size();
		}
		System.out.println(total);
		total = 0;
		words = sb.filterWords(ls2.getWords(), "");
		for (int i = 0; i < 7; i++) {
			total+= words[i].size();
		}

		System.out.println(total);
		
		System.out.println("4:"+System.currentTimeMillis());
		
		sb.updateSeenTileInformation('B');
		
		total = 0;
		words = sb.filterWords(ls1.getWords(), "");
		for (int i = 0; i < 7; i++) {
			total+= words[i].size();
		}
		System.out.println(total);
		total = 0;
		words = sb.filterWords(ls2.getWords(), "");
		for (int i = 0; i < 7; i++) {
			total+= words[i].size();
		}

		System.out.println(total);
		System.out.println("5:"+System.currentTimeMillis());
		//System.out.println(sb.getProbabilityOfAuction('A', 2));
	}
}
