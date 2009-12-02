package seven.g3.test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author Satyajeet
 */
public class ScrabbleTest {

	Dictionary sowpods;
	ArrayList<Letter> wordbag;

	public ScrabbleTest() {

		// initDict();
		//initBag();
	}

	public class Letter {

		Character alphabet;
		int value;
		int occurance;

		public Letter(Character c, int s, int o) {
			alphabet = c;
			value = s;
			occurance = o;
		}

		/**
		 * @return the alphabet
		 */
		public Character getAlphabet() {
			return alphabet;
		}

		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}

		public int getOccurance() {
			// TODO Auto-generated method stub
			return occurance;
		}

	}

	public int getWordScore(String word) {
		int score = -1;

		if (sowpods.wordlist.get(word) == null) {
			score = -1;
		} else {
			if (sowpods.wordlist.get(word) == true) {
				// Lets compute the score.
				score = 0;
				for (int loop = 0; loop < word.length(); loop++) {
					Character currChar = word.charAt(loop);
					score += letterScore(currChar);
				}

			}
		}
		return score;
	}

	public int letterScore(Character letter) {
		int score = 0;
		switch (letter) {
		case 'E':
			score = 1;
			break;
		case 'A':
			score = 1;
			break;
		case 'I':
			score = 1;
			break;
		case 'O':
			score = 1;
			break;
		case 'N':
			score = 1;
			break;
		case 'R':
			score = 1;
			break;
		case 'T':
			score = 1;
			break;
		case 'L':
			score = 1;
			break;
		case 'S':
			score = 1;
			break;
		case 'U':
			score = 1;
			break;
		case 'D':
			score = 2;
			break;
		case 'G':
			score = 2;
			break;

		case 'B':
			score = 3;
			break;
		case 'C':
			score = 3;
			break;
		case 'M':
			score = 3;
			break;
		case 'P':
			score = 3;
			break;

		case 'F':
			score = 4;
			break;
		case 'H':
			score = 4;
			break;
		case 'V':
			score = 4;
			break;
		case 'W':
			score = 4;
			break;
		case 'Y':
			score = 4;
			break;

		case 'K':
			score = 5;
			break;
		case 'J':
			score = 8;
			break;
		case 'X':
			score = 8;
			break;
		case 'Q':
			score = 10;
			break;
		case 'Z':
			score = 10;
			break;
		case '*':
			score = 0;
			break;

		}

		return score;
	}

	public Letter getRandomFromBag() {
		Letter letter = null;

		Random rand = new Random();
		int index = rand.nextInt(wordbag.size()); // This accounts for zero
		// indexing

		letter = wordbag.remove(index);

		return letter;
	}

	public void initBag() {
		wordbag = new ArrayList<Letter>();
		// * = blank word
		// wordbag.add(new Letter('*', 0));
		// wordbag.add(new Letter('*', 0));

		for (int loop = 1; loop <= 12; loop++) {
			wordbag.add(new Letter('E', 1, 12));
		}
		for (int loop = 1; loop <= 9; loop++) {
			wordbag.add(new Letter('A', 1, 9));
		}
		for (int loop = 1; loop <= 9; loop++) {
			wordbag.add(new Letter('I', 1, 9));
		}
		for (int loop = 1; loop <= 8; loop++) {
			wordbag.add(new Letter('O', 1, 8));
		}
		for (int loop = 1; loop <= 6; loop++) {
			wordbag.add(new Letter('N', 1, 6));
		}
		for (int loop = 1; loop <= 6; loop++) {
			wordbag.add(new Letter('R', 1, 6));
		}
		for (int loop = 1; loop <= 6; loop++) {
			wordbag.add(new Letter('T', 1, 6));
		}
		for (int loop = 1; loop <= 4; loop++) {
			wordbag.add(new Letter('L', 1, 4));
		}
		for (int loop = 1; loop <= 4; loop++) {
			wordbag.add(new Letter('S', 1, 4));
		}
		for (int loop = 1; loop <= 4; loop++) {
			wordbag.add(new Letter('U', 1, 4));
		}

		for (int loop = 1; loop <= 4; loop++) {
			wordbag.add(new Letter('D', 2, 4));
		}
		for (int loop = 1; loop <= 3; loop++) {
			wordbag.add(new Letter('G', 2, 3));
		}

		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('B', 3, 2));
		}
		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('C', 3, 2));
		}
		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('M', 3, 2));
		}
		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('P', 3, 2));
		}

		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('F', 4, 2));
		}
		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('H', 4, 2));
		}
		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('V', 4, 2));
		}
		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('W', 4, 2));
		}
		for (int loop = 1; loop <= 2; loop++) {
			wordbag.add(new Letter('Y', 4, 2));
		}

		wordbag.add(new Letter('K', 5, 1));
		wordbag.add(new Letter('J', 8, 1));
		wordbag.add(new Letter('X', 8, 1));
		wordbag.add(new Letter('Q', 10, 1));
		wordbag.add(new Letter('Z', 10, 1));

	}

	// public void initDict()
	// {
	// sowpods = new Dictionary();
	// try{
	// CSVReader csvreader = new CSVReader(new FileReader("sowpods.txt"));
	// String[] nextLine;
	// csvreader.readNext(); // Waste the first line
	// while((nextLine = csvreader.readNext()) != null)
	// {
	// String word = nextLine[1];
	// sowpods.wordlist.put(word, Boolean.TRUE);
	// }
	//
	// }
	// catch(Exception e)
	// {
	// e.printStackTrace();
	// System.out.println("\n Could not load dictionary!");
	// }
	//
	// }

	public static Map<Character, Integer> map = new HashMap<Character, Integer>();

	public static void main(String args[]) {
		ScrabbleTest t = new ScrabbleTest();

		for (int round = 0; round < 1; round++) {
			t.initBag();
			for (int bid = 0; bid < 35; bid++) {
				Letter word = t.getRandomFromBag();
				Character alphabet = word.getAlphabet();
				Integer integer = map.get(alphabet);
				if (integer == null) {
					map.put(alphabet, 1);
				} else {
					map.put(alphabet, ++integer);
				}
				// System.out.println(alphabet + " - "+ word.getOccurance() +
				// " - "+
				// word.getValue());
			}
		}
		
		for(Map.Entry<Character, Integer> entry: map.entrySet()){
			System.out.println(entry.getKey() + " - "+entry.getValue());
		}

	}

}

class Dictionary {
	Hashtable<String, Boolean> wordlist;

	public Dictionary() {
		wordlist = new Hashtable<String, Boolean>();
	}

}
