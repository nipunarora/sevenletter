package seven.g2.miner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Trie {
	
	public static void main(String[] args) {
		Trie t = loadWords("src/seven/g2/util/SmallWordlist.txt");
		System.out.println(t.traverse("ILTU"));
	}
	public static Trie loadWords(String f)
	{
		Trie t = new Trie();
		try {
			Scanner s = new Scanner(new File(f));
			while(s.hasNextLine())
			{
				String w[] =s.nextLine().split(","); //index 0 word 1
				t.addWord(w[0], w[1]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
	private HashMap<Character,Trie> entries;
	private String value=null;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + value  + ", " + entries.size() + "]";
	}
	public String traverse(String w)
	{
		if(w.length() == 0)
			return value;
		if(entries.containsKey(w.charAt(0)))
			return getEntry(w.charAt(0)).traverse(w.substring(1));
		else
			return null;
	}
	public Trie getEntry(Character c)
	{
		return entries.get(c);
	}
	private Trie()
	{
		entries = new HashMap<Character, Trie>();
	}

	public HashMap<Character, Trie> getEntries() {
		return entries;
	}
	public String getValue() {
		return value;
	}
	private void addWord(String w, String l)
	{
		if(w.length() == 0)
			value = l;
		else
		{
			Character c = w.charAt(0);
			if(!entries.containsKey(c))
				entries.put(c, new Trie());
			entries.get(c).addWord(w.substring(1), l);
		}
	}
}
