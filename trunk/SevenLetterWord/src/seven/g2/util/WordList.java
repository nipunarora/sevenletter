package seven.g2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.sqlite.SQLiteJDBCLoader;

import seven.g2.miner.DataMine;
import seven.g2.miner.LetterMine;
import seven.g2.miner.Trie;
import seven.g2.miner.DataMine.ItemSet;
import seven.g2.miner.LetterMine.LetterSet;
import seven.ui.Scrabble;

/**
 *
 */
public class WordList {
//	public Scrabble s = new Scrabble();
	private Logger log = new Logger(Logger.LogLevel.NONE,this.getClass());
	private static DataMine mine;
	private static Trie trie;
	private String[] toStringArray(String w)
	{
		String[] r = new String[w.length()];
		for(int i=0;i<w.length();i++)
		{
			r[i] = w.substring(i,i+1);
		}
		return r;
	}

	public static void main(String[] args) {
		WordList l = new WordList();
//		LetterSet i = l.getLetterGroup("DINNO");
//		if (null != i) {
//			String[] words = i.getWords();
//			System.out.format(
//					"Itemset [%s] has %d associated words:\n",
//					new Object[]{i.getKey(), words.length}
//			);
//			for (String w : words) {
//				System.out.println(w);
//			}
//		} else {
//			System.out.format(
//					"No words contain the letters %s\n",
//					new Object[]{ Arrays.deepToString(args)}
//			);
//		}
//		System.out.println(i.getBestBuildableWord());
//		System.out.println(l.getBestSubWord("ILTUUUUZ"));
		
	}
	public ScrabbleWord getBestSubWord(String letters)
	{
		
		ScrabbleWord best = new ScrabbleWord();
		
		if(letters.length() == 1)
		{
			return best;
		}
		String r = trie.traverse(letters);
		best.setWord(r);
		for(int i=0;i<letters.length();i++)
		{
			ScrabbleWord w = getBestSubWord(letters.substring(0,i) + letters.substring(i+1));
			if(w.getScore() > best.getScore())
				best = w;
		}
		return best;
	}
	
	public WordList() {
		if(mine == null)
		{
			mine = new LetterMine("src/seven/g2/util/WordList.txt");
			mine.buildIndex();
			mine.aPriori(0.000001);
		}
		if(trie == null)
		{
			trie = Trie.loadWords("src/seven/g2/util/SmallWordlist.txt");
		}
	}

	public ArrayList<ScrabbleWord> getValidWords(String letters) {
		letters = letters.toUpperCase();
		HashSet<String> potentialWords = shuffleString(letters, 0,
				new HashSet<String>());
		ArrayList<ScrabbleWord> ret = new ArrayList<ScrabbleWord>();
		for (String l : potentialWords) {
			int points = ScrabbleUtility.getScrabbleWordScore(l);
			if (points > 0) {
				ScrabbleWord w = new ScrabbleWord();
				w.score = points;
				w.word = l;
				ret.add(w);
			}
		}
		Collections.sort(ret);
		return ret;
	}

	private HashSet<String> shuffleString(String s, int l, HashSet<String> ret) {
		if (l == s.length()) {
			ret.add(s);
			return ret;
		} else {
			char[] sa = s.toCharArray();
			for (int i = l; i < s.length(); i++) {
				char c = sa[l];
				sa[l] = sa[i];
				sa[i] = c;
				s = String.valueOf(sa);
				ret = shuffleString(s, l + 1, ret);
				c = sa[l];
				sa[l] = sa[i];
				sa[i] = c;
				s = String.valueOf(sa);
			}
		}
		return ret;
	}
	public static String indexValue(String word)
	{
		ArrayList<String> letters = new ArrayList<String>();
		for (int i = 0; i < word.length(); i++) {
			letters.add(word.substring(i, i + 1));
		}
		Collections.sort(letters);
		String sorted = "";
		for (int j = 0; j < word.length(); j++) {
			sorted += letters.get(j);
		}
		sorted = sorted.toUpperCase();
		return sorted;
	}
	public LetterSet getLetterGroup(String w)
	{
		LetterSet r = (LetterSet) mine.getCachedItemSet(toStringArray(w));
		return r;
	}

	public HashMap<Character,LetterSet> getAllSuccessors(String w){
		HashMap<Character,LetterSet> sets = new HashMap<Character,LetterSet>();
		for(int i=0;i<26;i++){
			String newstr = WordUtility.insertCharacterInLexicographicPosition(w, (char)('A'+i));
			LetterSet ls = getLetterGroup(newstr);
			if(ls != null){
				sets.put((char)('A'+i),ls);
			}
		}
		
		return sets;
	}


	public static final String[] letters = { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z" };
}
