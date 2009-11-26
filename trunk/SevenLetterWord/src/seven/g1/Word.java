package seven.g1;

public class Word {

	public String word;
	public int length;
	//countkeep implementation: the value is the frequency of the letter in the word, and the index is the letter ex A is 0
	public int[] countKeep= new int[26];

	public Word(String s){
		word=s;
		length=s.length();
		int index;
		for(int i = 0; i<s.length();i++){
			index= Integer.valueOf(s.charAt(i));
			index=index-Integer.valueOf('A');
			countKeep[index]++;
		}
	}
	/**
	 * returns true if the word w can be formed from the letters contained in the word bag object we have currently
	 * @return
	 */
	public boolean issubsetof(Word w){

		for (int i=0;i<26;i++){
			if(this.countKeep[i]<w.countKeep[i])
				return false;
		}
		return true;

	}

	public String getWord(){
		return this.word;
	}
}
