package seven.g1;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import seven.ui.Scrabble;

public class Word {

	private static final int LETTERS = 26;
	public String word;
	public int length;
	public int score = 0;
	private Logger l = Logger.getLogger(this.getClass());
	//countkeep implementation: the value is the frequency of the letter in the word, and the index is the letter ex A is 0
	public int[] countKeep= new int[LETTERS];

	public Word(String s){
		word=s;
		length=s.length();
		for(int i = 0; i<s.length();i++){
			char c = s.charAt(i);
			score += Scrabble.letterScore(c);
			int index= Integer.valueOf(c);
			index -= Integer.valueOf('A');
			countKeep[index]++;
		}
	}

	public Word(final int[] counts) {
		countKeep = counts;
		length = 0;
		StringBuffer b = new StringBuffer();
		int charOffset = Integer.valueOf('A');
		for (char c = 'A'; c <= 'Z'; c++) {
			int index= Integer.valueOf(c) - charOffset;
			for (int i = 0; i < countKeep[index]; i++) {
				score += Scrabble.letterScore(c);
				b.append(c);
				length++;
			}
		}
		word = b.toString();
		assert(length == word.length());
	}
	/**
	 * returns true if the word w can be formed from the letters contained in the word bag object we have currently
	 * @return
	 */
	public boolean issubsetof(Word w){

		for (int i=0;i<LETTERS;i++){
			if(this.countKeep[i]<w.countKeep[i])
				return false;
		}
		return true;

	}

	public Word subtract(Word w) {
		int diff[] = new int[LETTERS];
		for (int i = 0; i < LETTERS; i++) {
			diff[i] = countKeep[i] - w.countKeep[i];
			if (diff[i] < 0) {
				l.error("Negative value found subtracting " + w.word + " from " + word);
				// should be fatal, but this is not industrial-strength code
			}
		}
		return new Word(diff);

	}

	public String getWord(){
		return this.word;
	}


	public long drawPossibilities(int[] bag_counts) {
		return drawPossibilities(bag_counts, new int[LETTERS]);
	}

	public long drawPossibilities(int[] bag_counts, int[] rack_counts) {
		assert(LETTERS == bag_counts.length);
		assert(LETTERS == rack_counts.length);
		int have = 0;
		int needed = 0;
		long count = 1;
		for (int i = 0; i < LETTERS; i++) {
			int n = bag_counts[i];
			int r = countKeep[i] - rack_counts[i];
			have += rack_counts[i];
			needed += r;
			int nCr = nCr(n,r);
			count *= nCr;
		}
		if (7 <= have + needed) {
			long permute = factorial(needed);
			count *= permute; // oh, whoa...
		} else {
			count = 0;
		}
		return count;
	}

	private static int nCr(int n, int r) {
		int ans = 1;
		if (r > n) { // no way to choose 1 from 0
			ans = 0;
		} else { // formula: n!/(n-r)!r!
			// calculate n!/(n-r)!
			for (int i = n; i > n - r; --i) {
				ans *= i;
			}
			// divide by r! (start with 2, because 1 is a silly factor
			for (int i = 2; i <= r; i++) {
				ans /= i;
			}
		}
		return ans;
	}

	// it is far from clear that you didn't mess this one up, Benjamin
	private static BigInteger denominate(int bagsize, int draws, int lettersneeded) {
		BigInteger it = BigInteger.ONE;
		int d_minus_r = draws - lettersneeded;
		int n_minus_d = bagsize - draws;
		int factorial_limit = Math.max(d_minus_r, n_minus_d);
		for (int i = factorial_limit; i <= bagsize; i++) {
			it = it.multiply(BigInteger.valueOf(i));
		}
		for (long i = Math.min(d_minus_r,n_minus_d); i > 1; --i) {
			it.divide(BigInteger.valueOf(i));
		}

		return it;
	}

	private static int factorial(int n) {
		int f = 1;
		for (int i = 1; i <= n; i++) {
			 f *= i;
		}
		return f;
	}

	public static void main(String[] args) {
		//Word w = new Word("ABIOSES");
		//System.out.println(w.word);
		int startbag[] = new Word(G1Player.SCRABBLE_LETTERS_EN_US).countKeep;
		String inputs[] = {
				"Q",
				"KJQXZ",
				"KJQXZZ",
				"ABIOSES",
				"YYKJQXZ",
				"AA",
				"EEEEIOA",
				"EEEEIAA",
				"EEOIIAA",
				"EEEE",
				"EEEA",
		};
		for (String in : inputs) {
			Word tmp = new Word(in);
			System.out.format("Word %s:\t%d\n", new Object[]{in,tmp.drawPossibilities(startbag)});
		}
		System.out.println(denominate(98, 14, 7));
		System.out.println(denominate(98, 42, 7));
		System.out.println(denominate(98, 91, 7));
	}
}
