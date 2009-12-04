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

	private Logger log = new Logger(Logger.LogLevel.DEBUG, this.getClass());
	
	private ArrayList<Letter> letters;
	
	private HashSet<wordSegment> words;
	
	private int myID;
		
	boolean beginNewRound = true;
	
	private HashMap<wordSegment, ArrayList<wordSegment>> prefixMap;
	
	private static Trie dic;
	
	private int run;
	
	private int run_left;
	
	private int n_SevenLetter = 26070;
	
	private int last_count = 0;
	
	
	/*used to record letter of current round to next round*/
	private Letter lastLetter;
	
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
	
	
	
	
	
	private void initDic()
	{
		if(dic != null)
		{        log.debug("Do no INIT DIC");

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
            System.out.println("\n Could not load dictionary!");
        }	
        
        log.debug("Init Dic Finished");
	}
	
	private void init(SecretState secretstate, int playerID)
	{
		run = 0;
		myID = playerID;
		try {
			prepareDatabase();
			
			log.debug("Prepare Database finished");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("we need to init the settings");
		initDic();
		
		letters = new ArrayList<Letter>();
		lastLetter = new Letter(null, 0);
		
		for(int i = 0; i<secretstate.getSecretLetters().size(); i++)
		{
			letters.add(secretstate.getSecretLetters().get(i));
		}
		
		prefixMap = new HashMap<wordSegment,  ArrayList<wordSegment>>();
		words = new HashSet<wordSegment>();
		Permutation.getPermMap(prefixMap, words, letters, dic);
		

/*		System.out.println("------->Number of possible words: "+words.size());
		for(wordSegment w : words)
			System.out.println("--------------->"+w.s);
*//*		System.out.println("------->No of possible prefixes: "+prefixMap.size());
		for(wordSegment w : prefixMap.keySet())
			System.out.println("--------------->"+w.s);
*/			
		beginNewRound = false;
	}
	
	
	class g5NaivePlayer extends g4Player
	{
		public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList, int total_rounds, ArrayList<String> PlayerList) {
			return 0;
		}
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

	private wordSegment getHighestScoreWord() {
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
	
	private boolean isWinnerInLastBid(ArrayList<PlayerBids> PlayerBidList)
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
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
		// TODO Auto-generated method stub

		if(beginNewRound)
			init(secretstate, PlayerID);
				
/*		System.out.println("--------------------------> See wonBy history");
		for(PlayerBids bid: PlayerBidList)
		{
			System.out.println("---------------->"+bid.getWonBy());
		}
*/		

		
		System.out.println("======================================>Begin BID, see cont of Letters: ");
		for(Letter l : letters)
			System.out.print(l.getAlphabet()+" ");
		System.out.println();

		
		System.out.println("-->Bid for  "+bidLetter.getAlphabet());
		
		if(isWinnerInLastBid(PlayerBidList))
		{
			System.out.println("===========================> Is winner in last bid, update the infos");
			if(lastLetter != null && lastLetter.getAlphabet()!=null)
			{
/*
				System.out.print("------->Previous Letters: ");
				for(Letter l : letters)
					System.out.print(l.alphabet+" ");
				System.out.println();
				
				System.out.println("------------->New Letter: "+lastLetter.alphabet);
				
				*/
				letters.add(lastLetter);
				System.out.print("------->Current Letters: ");
				for(Letter l : letters)
					System.out.print(l.getAlphabet()+" ");
				System.out.println();
	
				prefixMap = new HashMap<wordSegment,  ArrayList<wordSegment>>();
				words = new HashSet<wordSegment>();
				Permutation.getPermMap(prefixMap, words, letters, dic);
				/*System.out.println("------->Number of possible words: "+words.size());
				for(wordSegment w : words)
					System.out.println("--------------->"+w.s);*/
/*				System.out.println("------->No of possible prefixes: "+prefixMap.size());
				for(wordSegment w : prefixMap.keySet())
					System.out.println("--------------->"+w.s);
*/				}
		}

		
		int oldScore = 0;
		int profit = 0;

		wordSegment oldHighestWord = getHighestScoreWord();
		if(oldHighestWord != null)
			 oldScore = oldHighestWord.score;
		
		
		
		System.out.println("------->Old score: "+oldScore);
		if(oldHighestWord != null)
			System.out.println("------->Old word: "+oldHighestWord.s);		
		else
			System.out.println("------->Old word is null because of begin");		
			
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
/*					System.out.println("-------------------> Profit from CASE 1: "+profit);
					System.out.println("-------------------> The old word: "+w.s);
					System.out.println("-------------------> The new word: "+w.s+s);
					System.out.println();
*/					
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

							/*System.out.println("-------------------> Profit from CASE 2: "+profit);
							System.out.println("-------------------> The old word: "+w.s);
							System.out.println("-------------------> The new word: "+w.s+bidLetter.alphabet+suffix.s);						
							System.out.println();*/
						}
				}			
		}

		
		/*Change Environment*/
		
		lastLetter = new Letter(bidLetter.getAlphabet(), bidLetter.getValue());
		//round ++;	
		
		
		run ++;		
		run_left = PlayerList.size()*7 - run;
		

		ArrayList<Character> arr = new ArrayList<Character>();
		for(Letter l: letters)
			arr.add(l.getAlphabet());
		
		int[] n_combinations = new int[26];
		int bestCount = 0;
		for(int i = 0; i<26; i++)
		{
			Character newCh = (char)('A'+i);
			ArrayList<Character> arr_ = new ArrayList<Character>(arr);
			arr_.add(newCh);
			
			Collections.sort(arr_);
			
			String candidate = "";
			for(Character ch : arr_)
				candidate += ch;
			
			//log.debug("=============>Candidate:" +candidate);
			ResultSet rs = null;
			try {
				String sql = SQL_SELECT_COUNT+candidate+'\'';
				//log.debug(sql);
				rs = st.executeQuery(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int count = 0 ;
			try {
				rs.next();
				count = rs.getInt(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(count > bestCount)
				bestCount = count;
			n_combinations[i] = count;
		}
		
		System.err.println("see content of the 26 possible additional letters:");
		for(int elem: n_combinations)
			System.out.print(elem+" ");
		System.out.println();
		
		
		if(letters.size() == 6 && n_combinations[bidLetter.getAlphabet()-'A'] > 0)
		{
			System.out.println("------->We can target 7 now!");
			return 20;
		}
		
		double ratio1;
		if(last_count!=0)
			ratio1 = n_combinations[bidLetter.getAlphabet()-'A'] / last_count;
		else
			ratio1 = 1;
		
		double ratio2;
		
		if(bestCount != 0)
			ratio2 = (double)n_combinations[bidLetter.getAlphabet()-'A']/bestCount;
		else
			ratio2 = 0;

		System.out.println("See Best Count: "+bestCount);
		System.out.println("See value of this Leter: "+n_combinations[bidLetter.getAlphabet()-'A']);
		System.out.println("See value of ratio2: "+ratio2);
		
		int score = (int)(ratio2*10);
		
		return score;
		
		
/*		
		ArrayList<Character> arr = new ArrayList<Character>();
		for(Letter l: letters)
			arr.add(l.getAlphabet());
		arr.add(bidLetter.getAlphabet());
		
		Collections.sort(arr);
		
		String candidate = "";
		for(Character ch : arr)
			candidate += ch;
		
		log.debug(candidate);
		ResultSet rs = null;
		try {
			rs = st.executeQuery(SQL_SELECT_COUNT+candidate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int count = 0 ;
		try {
			rs.first();
			count = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug(count);
*/		
		
		
		

		
		/*if(letters.size() == 0)
		{			
			System.out.println("Letters: "+ letters);
			return NotEnoughLettersStrategy(bidLetter, PlayerBidList,
					total_rounds, PlayerList, secretstate);
		}*/
		
		
		
			
		//return profit;
		
	}

	private int NotEnoughLettersStrategy(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate)
	{
		System.out.println("We have less than 3 letters, returning the value of Letter "+bidLetter.getAlphabet()+": "+bidLetter.getValue());
		
		return bidLetter.getValue();
	}


    public static void main(String[] args)
    {
    //	System.out.println()
    }
    
	

}
