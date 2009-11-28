package seven.g1;

import org.apache.log4j.Logger;

public class Word {

	private static final int LETTERS = 26;
	public String word;
	public int length;
	private Logger l = Logger.getLogger(this.getClass());
	//countkeep implementation: the value is the frequency of the letter in the word, and the index is the letter ex A is 0
	public int[] countKeep= new int[LETTERS];

	public Word(String s){
		word=s;
		length=s.length();
		for(int i = 0; i<s.length();i++){
			int index= Integer.valueOf(s.charAt(i));
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

	public static void main(String[] args) {
		Word w = new Word("ABIOSES");
		System.out.println(w.word);
	}
}
