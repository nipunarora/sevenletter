package seven.g4;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import seven.ui.CSVReader;
import seven.ui.Letter;


public class Permutation {
	
	public static void getPermMap(HashMap<wordSegment,  ArrayList<wordSegment>> map, HashSet<wordSegment> words, ArrayList<Letter> list, Trie dic)
	{
		ArrayList<Letter> listBackup = new ArrayList<Letter>();
		listBackup.addAll(list);
		
		Letter[] print = new Letter[list.size()+1];   
		boolean[] bUsed = new boolean[list.size()];   

		int r = 0;  	
		while(r <= list.size())   
		{   
			permuteMap(map, words, list,  bUsed , print, 0, r, dic);   
			r++;   
		}
		
		list = listBackup;
		
	}
	
	public static void permuteMap(HashMap<wordSegment,ArrayList<wordSegment>> map, HashSet<wordSegment> words, ArrayList<Letter> arr,  boolean[] bUsed, Letter[] backup, int pos, int r, Trie dic)
	{
		int	i = 0;     		
		if ( pos == r )     
		{     
			String s = "";
			int score = 0;
			for ( i = 0; i < r; i++ )   
			{
				s += backup[i].getAlphabet();
				score += backup[i].getValue();
			}
			if(dic.countWords(s) > 0)
			{
				words.add(new wordSegment(s,score));
			}
				
			if(dic.countPrefixes(s) > 0 )
			{
				ArrayList<Letter> compliment = new ArrayList<Letter>();
				compliment.addAll(arr);
				for(i = 0; i < r; i++)
				{
					compliment.remove(backup[i]);
				}
				ArrayList<wordSegment> toMap = new ArrayList<wordSegment>();
				getAllPerm(toMap,compliment);
				
				Trie.Vertex v = dic.getNode(s);
				
				map.put(new wordSegment(s,score,v), toMap);
			}
			return;     
		}   
		for ( i=0; i < arr.size(); i++)   
			if (!bUsed[i])   
			{     
				bUsed[i] = true ;
				backup[pos] = arr.get(i);     
				permuteMap(map, words, arr, bUsed, backup, pos+1, r, dic);     
				bUsed[i] = false;     
			}   
		
	}
	
	
		
	public static void getAllPerm(ArrayList<wordSegment> ret, ArrayList<Letter> list, Trie dic)
	{
		Letter[] print = new Letter[list.size()+1]; 
		boolean[] bUsed = new boolean[list.size()];   

		int r = 1;  	
		while(r <= list.size())   
		{   
			permute(ret, list, bUsed , print, 0, r, dic);   
			r++;   
		}
	}
		
	
	
	
	
	public static void permute(ArrayList<wordSegment> ret, ArrayList<Letter> arr, boolean[] bUsed, Letter[] backup, int pos, int r, Trie dic)
	{   
		int	i = 0;     		
		if ( pos == r )     
		{     
			String s = "";
			int score = 0;
			for ( i = 0; i < r; i++ )   
			{
				s += backup[i].getAlphabet();
				score += backup[i].getValue();
			}
			System.out.println("try "+s);
			if(dic.countPrefixes(s) > 0 )
				ret.add(new wordSegment(s,score));
			return;     
		}   
		for ( i=0; i < arr.size(); i++)   
			if (!bUsed[i])   
			{     
				bUsed[i] = true ;
				backup[pos] = arr.get(i);     
				permute(ret, arr, bUsed, backup, pos+1, r, dic);     
				bUsed[i] = false;     
			}   
	}   

	
	public static void getAllPerm(ArrayList<wordSegment> ret, ArrayList<Letter> list)
	{
		Letter[] print = new Letter[list.size()+1]; 
		boolean[] bUsed = new boolean[list.size()];   

		int r = 1;  	
		while(r <= list.size())   
		{   
			permute(ret, list, bUsed , print, 0, r);   
			r++;   
		}
	}
		
	
	
	
	
	public static void permute(ArrayList<wordSegment> ret, ArrayList<Letter> arr, boolean[] bUsed, Letter[] backup, int pos, int r)
	{   
		int	i = 0;     		
		if ( pos == r )     
		{     
			String s = "";
			int score = 0;
			for ( i = 0; i < r; i++ )   
			{
				s += backup[i].getAlphabet();
				score += backup[i].getValue();
			}
			ret.add(new wordSegment(s,score));
			return;     
		}   
		for ( i=0; i < arr.size(); i++)   
			if (!bUsed[i])   
			{     
				bUsed[i] = true ;
				backup[pos] = arr.get(i);     
				permute(ret, arr, bUsed, backup, pos+1, r);     
				bUsed[i] = false;     
			}   
	}   


	
	
	
	public static void main(String[] args)   
	{   
		ArrayList<Letter> list = new ArrayList<Letter>();
		
		list.add(new Letter('A',1));
		//list.add(new Letter('A',1));
		//list.add(new Letter('A',1));
		list.add(new Letter('B',1));
		list.add(new Letter('B',1));
		//list.add(new Letter('B',1));
		list.add(new Letter('C',1));
		
	//	ArrayList<Letter> allElem = new ArrayList<Letter>(list);
		//System.out.println("All number of element: "+allElem.size());
		
		ArrayList<wordSegment> ret = new ArrayList<wordSegment>();
		
		HashMap<wordSegment, ArrayList<wordSegment>> map = new HashMap<wordSegment, ArrayList<wordSegment>>();
		
		Trie dic = new Trie();
		try{
            CSVReader csvreader = new CSVReader(new FileReader("sowpods.txt"));
            String[] nextLine;
            csvreader.readNext(); // Waste the first line
            while((nextLine = csvreader.readNext()) != null)
            {
                String word = nextLine[1];
                dic.addWord(word);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("\n Could not load dictionary!");
        }
        ArrayList<String> allwords = (ArrayList<String>) dic.listAllWords();
        System.out.println(allwords.size());
        
        HashSet<wordSegment> words = new HashSet<wordSegment>();
        
        
		getPermMap(map, words, list, dic);
		
		System.out.println(map.keySet().size());
		for(wordSegment w : map.keySet())
		{
		/*	if(w.s == ""){
//			System.out.println(w.s+'\t'+w.score+'\t'+map.get(w).size());
				for(wordSegment w1 : map.get(w))
					System.out.println(w1.s);
			System.out.println(map.get(w).size());
			}*/
/*
			System.out.println(toMap.size());
			for(int i = 0; i < toMap.size(); i++ )
				System.out.println('\t'+toMap.get(i).s+"\t"+toMap.get(i).score);
			System.out.println();
*/		}
		
		for(wordSegment w : words)
		{
			System.out.println(w.s+'\t'+w.score);
		}
	}   
}
