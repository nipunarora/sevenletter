/**
 * 
 */
package seven.g4;

import java.awt.Container;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import seven.ui.CSVReader;
import seven.ui.GameEngine;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.OpenState;
import seven.ui.SecretState;


/**
 * @author yeyangever
 *
 */
public class g4Player implements Player {
	
	
	public g4Player()
	{
		//System.out.println("init an instance");
		instanceID = n_Instance++;
		if(instanceID == 0)
		{
			initDic();
			db = new SuffixTrie();
			CreateMemDB.Build(db);
		}
	}
	
	
	protected static int n_Instance = 0;
	
	protected static Trie dic;
	
	protected static SuffixTrie db;
	
	protected HashMap<Character, Integer> lettersLeft; 
	
	protected int instanceID;

	protected Logger log = new Logger(Logger.LogLevel.DEBUG, this.getClass());
	
	protected ArrayList<Letter> letters;
	
	protected HashSet<wordSegment> words;
	
	protected int myID;
		
	protected boolean beginNewRound = true;
	
	protected HashMap<wordSegment, ArrayList<wordSegment>> prefixMap;
		
	protected int run;
	
	protected int run_left;
	
	protected int n_SevenLetter = 26070;
	
	
	
	/*used to record letter of current round to next round*/
	protected Letter lastLetter;
	
	private String SQL_SELECT_COUNT = "select count(*) from seven where key = \'";
	
	private Connection m_connection = null;

	private Statement st = null;
	
	private boolean prepareDatabase() throws SQLException {
		if (m_connection == null || m_connection.isClosed()) {
			try {
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			m_connection = DriverManager
					.getConnection("jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ="
							+ "src/seven/g4/seven.mdb");
			m_connection.setReadOnly(false);
		}
		st = m_connection.createStatement();
		return true;
	}
	
	protected boolean IsThereAnyLeft(Character c)
	{
		int numberLeft = lettersLeft.get(c);
		if(numberLeft>0)
			return true;
		else
			return false;
	}
	
	protected int howManyDoWeHave(Character c)
	{
		int n = 0;
		for(Letter l : letters)
		{
			if(l.getAlphabet().equals(c))
			{
				n++;
			}
		}
		return n;
	}
	
	protected void initDic()
	{
		if(dic != null)
		{        //log.debug("Do no INIT DIC");

			return;
		}		
		dic = new Trie();
		try{
            CSVReader csvreader = new CSVReader(new FileReader("src/seven/g4/smallwordlist.txt"));
            String[] nextLine;
            csvreader.readNext(); // Waste the first line
            while((nextLine = csvreader.readNext()) != null)
            {
                dic.addWord(nextLine[0]);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //System.out.println("\n Could not load dictionary!");
        }	
        
        log.debug("Init Dic Finished");
	}
	
	protected void initLettersLeftArray()
	{	
		lettersLeft = new HashMap<Character, Integer>();
		lettersLeft.put('A', 9);
		lettersLeft.put('B', 2);
		lettersLeft.put('C', 2);
		lettersLeft.put('D', 4);
		lettersLeft.put('E', 12);
		lettersLeft.put('F', 2);
		lettersLeft.put('G', 3);
		lettersLeft.put('H', 2);
		lettersLeft.put('I', 9);
		lettersLeft.put('J', 1);
		lettersLeft.put('K', 1);
		lettersLeft.put('L', 4);
		lettersLeft.put('M', 2);
		lettersLeft.put('N', 6);
		lettersLeft.put('O', 8);
		lettersLeft.put('P', 2);
		lettersLeft.put('Q', 1);
		lettersLeft.put('R', 6);
		lettersLeft.put('S', 4);
		lettersLeft.put('T', 6);
		lettersLeft.put('U', 4);
		lettersLeft.put('V', 2);
		lettersLeft.put('W', 2);
		lettersLeft.put('X', 1);
		lettersLeft.put('Y', 2);
		lettersLeft.put('Z', 1);
	}
	
	protected void updateLettersLeft(Letter bidLetter)
	{
		Character c = bidLetter.getAlphabet();
	    Integer numOfLetterLeft = lettersLeft.get(c);
	    lettersLeft.put(c,numOfLetterLeft -1);
	}
	
	protected void init(SecretState secretstate, int playerID)
	{
		//System.out.println("Init secrete state");
		initLettersLeftArray();
		for(int i = 0; i<secretstate.getSecretLetters().size(); i++)
		{
			Character c = secretstate.getSecretLetters().get(i).getAlphabet();
			int letterCount = lettersLeft.get(c)-1;
			lettersLeft.put(c, letterCount);
		}
				
		run = 0;
		myID = playerID;

		letters = new ArrayList<Letter>();
		lastLetter = new Letter(null, 0);
		
		for(int i = 0; i<secretstate.getSecretLetters().size(); i++)
		{
			letters.add(secretstate.getSecretLetters().get(i));
		}
		
		prefixMap = new HashMap<wordSegment,  ArrayList<wordSegment>>();
		words = new HashSet<wordSegment>();
		Permutation.getPermMap(prefixMap, words, letters, dic);		
		beginNewRound = false;
	}
	
	
	

	public void Register() {
		// TODO Auto-generated method stub

	}

	public String returnWord() {
		// TODO Auto-generated method stub		
		beginNewRound = true;
		
		if(letters.size()==6)
			letters.add(lastLetter);
		prefixMap = new HashMap<wordSegment,  ArrayList<wordSegment>>();
		words = new HashSet<wordSegment>();
		Permutation.getPermMap(prefixMap, words, letters, dic);

		/*
		System.out.println("------------------------>choose a highest score word to return");
		System.out.println("---Number of letters: "+letters.size());
		for(Letter l : letters)
			System.out.print(l.alphabet+" ");
		System.out.println();
		for(wordSegment w : words)
			System.out.println("---------->"+w.s+'\t'+w.score);
		*/
		return getHighestScoreWord().s;
		
	}

	protected wordSegment getHighestScoreWord() {
		wordSegment ret = null;
		int score = 0;
		for(wordSegment w: words)
		{
			if (w.score > score)
			{
				score = w.score;
				ret = w;
			}
		}
		/*if(ret != null)
		System.out.println("string with highest score to return: "+ret.s);
		*/
		return ret;
	}
	
	protected boolean isWinnerInLastBid(ArrayList<PlayerBids> PlayerBidList)
	{
		
		if(PlayerBidList == null || PlayerBidList.size() == 0)
		{
			//System.out.println("------->BidList is null, Just begin");
			return false;
		}
		PlayerBids lastBids = PlayerBidList.get(PlayerBidList.size()-1);
		if(lastBids.getWinnerID() == myID)
			return true;
		return false;
	}

	@Override
	public  int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID)
	{
		return 0;
		
	}
	
	
    protected int profitGainBid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
				int total_rounds, ArrayList<String> PlayerList,
				SecretState secretstate, int PlayerID) 
    {
    	int oldScore = 0;
		int profit = 0;

		wordSegment oldHighestWord = getHighestScoreWord();
		if(oldHighestWord != null)
			 oldScore = oldHighestWord.score;		
		
		Trie.Vertex child;
		
		for(wordSegment w: prefixMap.keySet())
		{
		
			/* if append the letter to prefix*/
			String s = "";
			s += bidLetter.getAlphabet();
//			System.out.println("------------->Case 1: The prefix: "+w.s+'\t'+w.score+'\t');
			child = dic.getNode(w.vertex, s);
			if(child != null)
				if (child.words >0)
				{
					profit = Math.max(w.score + bidLetter.getValue() - oldScore, profit);
				}

			/* if insert the letter in the middle fix and suffix*/
			/* including when w.s == "" and w.vertex == root*/
			//get the node of the word segment w and that under the letter
			Trie.Vertex v = dic.getNode(w.vertex, bidLetter.getAlphabet());
			if(v!=null)
				for(wordSegment suffix : prefixMap.get(w))
				{
					child = dic.getNode(v, suffix.s);
					if(child != null)
						if(child.words > 0)
						{
							profit = Math.max(profit, w.score+suffix.score+bidLetter.getValue() - oldScore);
						}
				}			
		}
		return profit;
	}
	
	
	public static class g4BackWardPlayer extends g4Player
	{
		private PatternMine miner;		
		
	    public g4BackWardPlayer() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
				int total_rounds, ArrayList<String> PlayerList,
				SecretState secretstate, int PlayerID) {
			// TODO Auto-generated method stub
			SharedPartOfBid( bidLetter, PlayerBidList,
					 total_rounds,  PlayerList,
					 secretstate,  PlayerID);
			
			if(letters.size() == 0)
			{
				int[] n_combinations = new int[26];
				int bestCount = 0;
				for(int i = 0; i<26; i++)
				{
					Character newCh = (char)('A'+i);
					String candidate = "";
					candidate += newCh;
					int count = db.getSuffixCount(candidate);					
					
					/*If so such letter in the scrabble bag then, just count 0*/
					if(!IsThereAnyLeft(newCh))
						count = 0;
					
					if(count > bestCount)
						bestCount = count;
					n_combinations[i] = count;
				}						
				double ratio1;				
				if(bestCount != 0)
					ratio1 = (double)n_combinations[bidLetter.getAlphabet()-'A']/bestCount;
				else
					ratio1 = 0;
				int score = (int)(ratio1*7);
				
				/*need to update letters left*/
				updateLettersLeft(bidLetter);
				return score;
			}
			
			if(letters.size() == 6)
			{
				ArrayList<Character> arr = new ArrayList<Character>();
				
				/*Build old Sring*/
				for(Letter l: letters)
					arr.add(l.getAlphabet());
				Collections.sort(arr);
				String oldString = "";
				for(Character c : arr)
					oldString += c;
				
				/*Build new String*/
				arr.add(bidLetter.getAlphabet());
				Collections.sort(arr);
				String candidate = "";
				for(Character c: arr)
					candidate += c;
				
				updateLettersLeft(bidLetter);
				
				if(db.getSuffixCount(candidate)>0)
					return 20;
				else
//					return 0;
					if(db.getSuffixCount(oldString) == 0)
						return profitGainBid(bidLetter, PlayerBidList,
								 total_rounds,  PlayerList,
								 secretstate,  PlayerID);
			}

			miner = new PatternMine(db, letters);
			
			/*Return 1-letter String as key and its appearing times as value*/
			HashMap<String, Integer> target = miner.getFrequentPatterns();
			
			
			if(target == null)
			{
				//System.out.println("Fall into ProfitGain Bid");
				updateLettersLeft(bidLetter);
				return profitGainBid(bidLetter, PlayerBidList,
					 total_rounds,  PlayerList,
					 secretstate,  PlayerID);
			}
			
			int bestCount = 0;
			int currCount = 0;
			for(String s: target.keySet())
			{
				Character c = s.charAt(0);
				if(c == bidLetter.getAlphabet())
					currCount = target.get(s);
				
				/*If so such letter in the scrabble bag then, just count 0*/
				if(target.get(s) > bestCount)
				{
					bestCount = target.get(s);
				}
			}
			double value =(double)currCount/bestCount*7;
			if(value>7)
			{
				System.out.println("See what is wrong");
				System.out.println(currCount+"\t"+bestCount);
			}
			updateLettersLeft(bidLetter);
			
			return (int)value;			
		}
	}
	
	
	public static class g4ForWardPlayer extends g4Player
	{

		public g4ForWardPlayer() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
				int total_rounds, ArrayList<String> PlayerList,
				SecretState secretstate, int PlayerID) {
			// TODO Auto-generated method stub
			
			SharedPartOfBid( bidLetter, PlayerBidList,
					 total_rounds,  PlayerList,
					 secretstate,  PlayerID);

			/* Build old String*/
			ArrayList<Character> arr = new ArrayList<Character>();
			for(Letter l: letters)
				arr.add(l.getAlphabet());
			Collections.sort(arr);
			String oldString = "";
			for(Character ch: arr)
				oldString += ch;
			
			int[] n_combinations = new int[26];
			int bestCount = 0;
			
			for(int i = 0; i<26; i++)
			{
				/*Build new String*/
				Character newCh = (char)('A'+i);
				ArrayList<Character> arr_ = new ArrayList<Character>(arr);
				arr_.add(newCh);			
				Collections.sort(arr_);
				String candidate = "";
				for(Character ch : arr_)
					candidate += ch;
				
				/* if not letter left in the scrabble bag, return 0 */				
				int count = db.getSuffixCount(candidate);
				if (!IsThereAnyLeft(newCh))
					count = 0;
				
				if(count > bestCount)
					bestCount = count;
				n_combinations[i] = count;
			}

			/* Now we can update the letters left */
			updateLettersLeft(bidLetter);
			
			/* If it can form a 7 letter word, bit 20, else, see if 
			 * old string (6 letters) can form, if not, fall into profitGain()
			 */
			if(letters.size() == 6)
				if(n_combinations[bidLetter.getAlphabet()-'A'] > 0)
					return 20;
				else
				{
//					return 0;
					if(db.getSuffixCount(oldString) == 0)
						return profitGainBid(bidLetter, PlayerBidList,
								 total_rounds,  PlayerList,
								 secretstate,  PlayerID);
				}
					
			double ratio1;
			if(bestCount != 0)
				ratio1 = (double)n_combinations[bidLetter.getAlphabet()-'A']/bestCount;
			else
			{
				System.out.println("In forward Player, the best Count is 0, see current count: "+n_combinations[bidLetter.getAlphabet()-'A']);
//				ratio1 = 0;
				return profitGainBid(bidLetter, PlayerBidList,
						 total_rounds,  PlayerList,
						 secretstate,  PlayerID);
			}
			int score = (int)(ratio1*7);
			return score;
		}
	}

	void SharedPartOfBid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID)
	{
		if(beginNewRound)
			init(secretstate, PlayerID);
				
		if(isWinnerInLastBid(PlayerBidList))
		{
			if(lastLetter != null && lastLetter.getAlphabet()!=null)
			{
				letters.add(lastLetter);
				prefixMap = new HashMap<wordSegment,  ArrayList<wordSegment>>();
				words = new HashSet<wordSegment>();
				Permutation.getPermMap(prefixMap, words, letters, dic);
			}
		}
		
		lastLetter = new Letter(bidLetter.getAlphabet(), bidLetter.getValue());
		run ++;		
		run_left = PlayerList.size()*7 - run;
	}
		
	


    public static void main(String[] args)
    {
    	g4Player.g4BackWardPlayer p1 = new g4BackWardPlayer();
 System.out.println("aaaaaa");
    }
 

}
