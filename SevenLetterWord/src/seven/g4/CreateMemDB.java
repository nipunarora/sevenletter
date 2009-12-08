package seven.g4;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class CreateMemDB 
{	
	public static void Build(SuffixTrie trie)
	{
		
		if(trie == null)
			trie = new SuffixTrie();
		try {
			Scanner s = new Scanner(new File("src/seven/g4/7letterWords.txt"));
			while(s.hasNextLine())
			{
				String word = sortString(s.nextLine());
				addSubSet(trie, word);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void Build(SuffixTrie trie, String s)
	{
				addSubSet(trie, s);
	}
	
	
	private static void addSubSet(SuffixTrie trie, String word) {
		//if(!prefixes.contains(sorted))
			for(String p : getAllSubSet(word))
			{
				//System.out.println(p);
				//System.out.println(p.length());
				ArrayList<Character> suffix = new ArrayList<Character> ();
				for(int i = 0 ; i < word.length(); i++)
					suffix.add(word.charAt(i));
				for(int i = 0 ; i < p.length(); i++)
					suffix.remove(suffix.indexOf(p.charAt(i)));
				String suff = "";
				for(int i = 0 ; i < suffix.size(); i++)
					suff += suffix.get(i);
				
				trie.addWord(p, suff);								
			}
	}
	
	
	public static ArrayList<String>  getAllSubSet(String s)
	{
		ArrayList<String> ret = new ArrayList<String>();
		if(s.length() == 1)
			ret.add(s);
	
		else
		{
			ArrayList<String> part = getAllSubSet(s.substring(0,s.length()-1));
			ret.addAll(part);
			for(String prefix : part)
			{
				String whole = prefix+s.charAt(s.length()-1);
				ret.add(whole);
			}
			
			ret.add(s.substring(s.length()-1));
			
		}
		return ret;
	}
	
	public static String sortString(String word)
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
		return sorted;
	}	
	
	
	
	
	public static void main(String[] args)
	{
		SuffixTrie trie = new SuffixTrie();
		
		String s = sortString("AACD");

		//HashSet<String> set = new HashsSet<String>();
		ArrayList<String> set = getAllSubSet(s);
		
		for(String st: set)
			System.out.println(st);
		System.out.println(set.size());
		
		

		
		Build(trie,s);
//		/*Build(trie, s);
		
		
		LinkedList<SuffixTrie.Vertex> list = new LinkedList<SuffixTrie.Vertex>();
		
		list.add(trie.root);
		int n=0;
		while(!(list.size() == 0))
		{
			n++;
			SuffixTrie.Vertex v = list.poll();
			
			for(int i=0 ; i<26; i++)
			if(v.children[i]!=null)
				list.add(v.children[i]);
			if(v.getSuffix() != null)
			{
				System.out.print(v.s+"\n");
				for(String s1 : v.getSuffix())
					System.out.println("------>"+s1);
				System.out.println();
			}			
			
		}

		System.out.println(n);
		
		
	}
	
}
