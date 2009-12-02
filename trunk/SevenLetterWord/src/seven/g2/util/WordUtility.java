package seven.g2.util;

import java.util.HashSet;

/**
 * General Word Utility Class
 */
public class WordUtility {

	/**
	 * Inserts a character into correct position in an array of characters which
	 * are sorted in lexicographic increasing order.
	 * 
	 * @param currString
	 * @param newChar
	 * @return
	 */
	public static String insertCharacterInLexicographicPosition(
			String currString, Character newChar) {

		int currLength = currString.length();
		char[] newChars = new char[currLength + 1];

		int indexToInsert = newChars.length - 1;

		for (int i = currLength - 1; i >= 0; i--) {
			char c = currString.charAt(i);
			if (c - 'A' > newChar - 'A') {
				newChars[indexToInsert] = c;
				indexToInsert--;
			} else {
				break;
			}
		}

		newChars[indexToInsert] = newChar;

		for (int i = 0; i < indexToInsert; i++) {
			newChars[i] = currString.charAt(i);
		}

		return String.valueOf(newChars);
	}

	/**
	 * Generates a set of all possible subsequences for a string.
	 * Includes empty string.
	 * 
	 * If string is in lexicographic order then it will be unique set
	 * Else not necessarily unique subsequences	 
	 * 
	 * @param currString
	 * @return
	 */
	public static HashSet<String> getAllUniqueSubSequences(String currString) {

		HashSet<String> set = new HashSet<String>();
		int max = (int) Math.pow(2, currString.length());
		int len = currString.length();
		for (int i = 0; i < max; i++) {
			String currStr = "";
			int prev = i;
			int curr;
			for (int j = len - 1; j >= 0; j--) {
				curr = prev >> 1;
				if (prev == curr * 2 + 1) {
					currStr = currString.charAt(j) + currStr;
				}
				prev = curr;
			}

			set.add(currStr);

		}
		// System.out.println(set.size());
		return set;
	}

	public static void main(String[] args) {

		System.out.println(getAllUniqueSubSequences("aabb"));
	}
}
