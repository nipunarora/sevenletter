package seven.g1;

public class Word {
	
	private String word;
	private int length;
	private int[] countKeep= new int[26];
	
	public Word(String s){
		word=s;
		length=s.length();
		int index;
		for(int i=0; i<s.length();i++){
			index= Integer.valueOf(s.charAt(i))-Integer.valueOf('A');
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
}
