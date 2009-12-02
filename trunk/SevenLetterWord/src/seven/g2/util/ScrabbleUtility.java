package seven.g2.util;

/**
 * Scrabble Utility class which will maps letter to score, noOfTiles Exposes
 * methods to calculate word score
 * 
 */
public class ScrabbleUtility {

	/** Mapping of letter to scrabble score(value) **/
	// private static final HashMap<Character, Integer> letterToScoreMap = new
	// HashMap<Character, Integer>();
	private static final int[] letterToScore = new int[26];

	/** Mapping of letter to number of tiles available in Scrabble **/
	// private static final HashMap<Character, Integer> letterToTileCountMap =
	// new HashMap<Character, Integer>();
	public static final int[] letterToTileCount = new int[26];

	public static final int TOTAL_TILE_COUNT = 98;
	static {

		/** Populate letter score mappings **/
		/*
		 * letterToScoreMap.put('E', 1); letterToScoreMap.put('A', 1);
		 * letterToScoreMap.put('I', 1); letterToScoreMap.put('O', 1);
		 * letterToScoreMap.put('N', 1); letterToScoreMap.put('R', 1);
		 * letterToScoreMap.put('T', 1); letterToScoreMap.put('L', 1);
		 * letterToScoreMap.put('S', 1); letterToScoreMap.put('U', 1);
		 * 
		 * letterToScoreMap.put('D', 2); letterToScoreMap.put('G', 2);
		 * 
		 * letterToScoreMap.put('B', 3); letterToScoreMap.put('C', 3);
		 * letterToScoreMap.put('M', 3); letterToScoreMap.put('P', 3);
		 * 
		 * letterToScoreMap.put('F', 4); letterToScoreMap.put('H', 4);
		 * letterToScoreMap.put('V', 4); letterToScoreMap.put('W', 4);
		 * letterToScoreMap.put('Y', 4);
		 * 
		 * letterToScoreMap.put('K', 5); letterToScoreMap.put('J', 8);
		 * letterToScoreMap.put('X', 8); letterToScoreMap.put('Q', 10);
		 * letterToScoreMap.put('Z', 10);
		 */

		letterToScore['E' - 'A'] = 1;
		letterToScore['A' - 'A'] = 1;
		letterToScore['I' - 'A'] = 1;
		letterToScore['O' - 'A'] = 1;
		letterToScore['N' - 'A'] = 1;
		letterToScore['R' - 'A'] = 1;
		letterToScore['T' - 'A'] = 1;
		letterToScore['L' - 'A'] = 1;
		letterToScore['S' - 'A'] = 1;
		letterToScore['U' - 'A'] = 1;

		letterToScore['D' - 'A'] = 2;
		letterToScore['G' - 'A'] = 2;

		letterToScore['B' - 'A'] = 3;
		letterToScore['C' - 'A'] = 3;
		letterToScore['M' - 'A'] = 3;
		letterToScore['P' - 'A'] = 3;

		letterToScore['F' - 'A'] = 4;
		letterToScore['H' - 'A'] = 4;
		letterToScore['V' - 'A'] = 4;
		letterToScore['W' - 'A'] = 4;
		letterToScore['Y' - 'A'] = 4;

		letterToScore['K' - 'A'] = 5;
		letterToScore['J' - 'A'] = 8;
		letterToScore['X' - 'A'] = 8;
		letterToScore['Q' - 'A'] = 10;
		letterToScore['Z' - 'A'] = 10;

		/** Populate letter tile count mappings **/
		/*
		 * letterToTileCountMap.put('E', 12); letterToTileCountMap.put('A', 9);
		 * letterToTileCountMap.put('I', 9); letterToTileCountMap.put('O', 8);
		 * letterToTileCountMap.put('N', 6); letterToTileCountMap.put('R', 6);
		 * letterToTileCountMap.put('T', 6); letterToTileCountMap.put('L', 4);
		 * letterToTileCountMap.put('S', 4); letterToTileCountMap.put('U', 4);
		 * 
		 * letterToTileCountMap.put('D', 4); letterToTileCountMap.put('G', 3);
		 * 
		 * letterToTileCountMap.put('B', 2); letterToTileCountMap.put('C', 2);
		 * letterToTileCountMap.put('M', 2); letterToTileCountMap.put('P', 2);
		 * 
		 * letterToTileCountMap.put('F', 2); letterToTileCountMap.put('H', 2);
		 * letterToTileCountMap.put('V', 2); letterToTileCountMap.put('W', 2);
		 * letterToTileCountMap.put('Y', 2);
		 * 
		 * letterToTileCountMap.put('K', 1); letterToTileCountMap.put('J', 1);
		 * letterToTileCountMap.put('X', 1); letterToTileCountMap.put('Q', 1);
		 * letterToTileCountMap.put('Z', 1);
		 */

		letterToTileCount['E' - 'A'] = 12;
		letterToTileCount['A' - 'A'] = 9;
		letterToTileCount['I' - 'A'] = 9;
		letterToTileCount['O' - 'A'] = 8;
		letterToTileCount['N' - 'A'] = 6;
		letterToTileCount['R' - 'A'] = 6;
		letterToTileCount['T' - 'A'] = 6;
		letterToTileCount['L' - 'A'] = 4;
		letterToTileCount['S' - 'A'] = 4;
		letterToTileCount['U' - 'A'] = 4;

		letterToTileCount['D' - 'A'] = 4;
		letterToTileCount['G' - 'A'] = 3;

		letterToTileCount['B' - 'A'] = 2;
		letterToTileCount['C' - 'A'] = 2;
		letterToTileCount['M' - 'A'] = 2;
		letterToTileCount['P' - 'A'] = 2;

		letterToTileCount['F' - 'A'] = 2;
		letterToTileCount['H' - 'A'] = 2;
		letterToTileCount['V' - 'A'] = 2;
		letterToTileCount['W' - 'A'] = 2;
		letterToTileCount['Y' - 'A'] = 2;

		letterToTileCount['K' - 'A'] = 1;
		letterToTileCount['J' - 'A'] = 1;
		letterToTileCount['X' - 'A'] = 1;
		letterToTileCount['Q' - 'A'] = 1;
		letterToTileCount['Z' - 'A'] = 1;

	}

	/**
	 * Calculates the scrabble score for a word
	 * 
	 * @param s
	 * @return
	 */
	public static int getScrabbleWordScore(String s) {
		int score = 0;
		for (int i = 0; i < s.length(); i++) {
			score += getScrabbleLetterScore(s.charAt(i));
		}

		return score;
	}

	public static int getScrabbleWordScoreWithBonus(String s) {
		int score = getScrabbleWordScore(s);
		if(s.length() == 7)
			score += 50;
		return score;
	}

	/**
	 * Returns the number of tiles available in Scrabble for a letter
	 * 
	 * @param c
	 * @return
	 */
	public static int getNoOfScrabbleTiles(Character c) {
		return letterToTileCount[c - 'A'];
	}

	/**
	 * Returns the letters score in Scrabble for a letter
	 * 
	 * @param c
	 * @return
	 */
	public static int getScrabbleLetterScore(Character c) {
		return letterToScore[c - 'A'];
	}

	public static void main(String[] args) {

		// System.out.println(letterToScoreMap.size());
		// System.out.println(letterToTileCountMap.size());

		String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			count += letterToTileCount[c - 'A'];
			System.out.println("Letter: " + c + " Value: "
					+ letterToScore[c - 'A'] + " Count: "
					+ letterToTileCount[c - 'A']);

		}
		System.out.println("Total Tile Count: " + count);
	}

	/**
	 * Returns true if this word can be made using scrabble tiles
	 * @param s
	 * @return
	 */
	public static boolean isValidScrabbleWord(String s) {
		int[] letterOccurences = new int[26];
		for (int i = 0; i < letterOccurences.length; i++) {
			letterOccurences[i] = 0;
		}

		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			if (letterOccurences[c - 'A'] < letterToTileCount[c - 'A']) {
				letterOccurences[c - 'A'] = letterOccurences[c - 'A'] + 1;
			} else {
				return false;
			}
		}

		return true;
	}
}
