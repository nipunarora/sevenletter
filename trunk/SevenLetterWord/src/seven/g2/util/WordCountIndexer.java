package seven.g2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;


import seven.ui.Scrabble;

public class WordCountIndexer {
//	private HashMap<String, HashMap<Integer,Integer>> wordCountsByIndex = new HashMap<String, HashMap<Integer,Integer>>();
	private HashSet<String> prefixes = new HashSet<String>();
	private Scrabble s = new Scrabble();
	int st, e;
	public WordCountIndexer(int i, int j)
	{
		st=i;
		e=j;
	}
	
	private HashSet<String> shuffleString(String s, int l, HashSet<String> ret)
	{
		if(l == s.length())
		{
			ret.add(s);
			return ret;
		}
		else
		{
			char[] sa = s.toCharArray();
			for(int i=l;i<s.length();i++)
			{
				char c = sa[l];
				sa[l] = sa[i];
				sa[i] = c;
				s = String.valueOf(sa);
				ret = shuffleString(s, l+1, ret);
				c = sa[l];
				sa[l] = sa[i];
				sa[i] = c;
				s = String.valueOf(sa);
			}
		}
		return ret;
	}
	
	public HashSet<String> getSubStringIndices(String s)
	{
		HashSet<String> ret = new HashSet<String>();
		HashSet<String> candidates = new HashSet<String>();
		candidates = shuffleString(s, 0, candidates);
		for(String t : candidates)
		{
			ret.add(WordList.indexValue(t));
			for(int i =1;i<t.length();i++)
			{
				ret.add(WordList.indexValue(t.substring(0,i)));
			}
		}
		
		
		return ret;
	}
	public void init()
	{
		System.out.println("Begin loading");
		initDB();
		loadWords();
		disconnect();
		System.out.println("Done loading");
//		outputList();
		System.out.println("Done");
	}
//	private void outputList()
//	{
//		try {
//			FileWriter fw = new FileWriter("src/seven/g2/util/WordIndex.txt");
//			for(String ind : wordCountsByIndex.keySet())
//			{
//					fw.write(ind +",");
//					for(int i = 1;i<=7;i++)
//					{
//						if(wordCountsByIndex.get(ind).containsKey(i))
//							fw.write("" + wordCountsByIndex.get(ind).get(i));
//						else
//							fw.write("0");
//						if(i != 7)
//							fw.write(",");
//					}
//					fw.write("\n");
//					fw.flush();
//				
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	private void loadWords()
	{
		try {
			Scanner s = new Scanner(new File("src/seven/g2/util/SmallWordlist.txt"));
			int i = 0;
			while(s.hasNextLine())
			{
				String word = s.nextLine();
					word = word.split(",")[1];
					addLetterPermutations(word);
				if(i%10 ==0)
					System.out.println(word);
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Add the permutations of the letters to the wordlist
	 * @param word
	 */
	private void addLetterPermutations(String word) {
		int score = s.getWordScore(word);
		String sorted = WordList.indexValue(word);
		if(!prefixes.contains(sorted))
			for(String p : getSubStringIndices(word))
			{
				Statement s;
				try {
					s = conn.createStatement();
					if(!prefixes.contains(p))
						s.executeUpdate("INSERT INTO letter_groups (letters) values ('"+p+"');");
					int l = word.length();
					s.executeUpdate("UPDATE letter_groups SET oc_"+l+"=oc_"+l+" + 1 where letters='"+p+"';");
					prefixes.add(p);
					s.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	public void disconnect()
	{
		if (conn != null)
        {
            try
            {
                conn.close ();
                System.out.println ("Database connection terminated");
            }
            catch (Exception e) { /* ignore close errors */ }
        }
	}
	Connection conn = null;
	private void initDB()
	{
		

        try
        {
            String userName = "ppsf09";
            String password = "ppsf09";
            String url = "jdbc:mysql://projects.seas.columbia.edu/4444p4";
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");
        }
        catch (Exception e)
        {
            System.err.println ("Cannot connect to database server");
        }

	}
public static void main(String[] args) {
	WordCountIndexer i = new WordCountIndexer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
	i.init();
//	System.out.println(i.getSubStringIndices("JOZZ"));
}
}
