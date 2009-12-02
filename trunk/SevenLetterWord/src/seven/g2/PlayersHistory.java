package seven.g2;


import java.util.ArrayList;
import seven.ui.PlayerBids;
import java.util.HashMap;
import seven.g2.util.*;
import seven.g2.util.ScrabbleWord;



class PlayerStatus
{
	String playerName;
	Character[] gotLetters;
	HashMap<Character,ArrayList<Integer>> bidValues = new HashMap<Character,ArrayList<Integer>>();
	ArrayList<ScrabbleWord> possibleWords;
	int noOfLetters;
}

public class PlayersHistory {

	//stores the list of words present with the players
	//HashMap<String,String> playersStatus=new HashMap<String,String>();
	
	ArrayList<PlayerStatus> playersStats =new ArrayList<PlayerStatus>();
	HashMap<String,HashMap<Character,ArrayList<Integer>>> playersBids = new HashMap<String,HashMap<Character,ArrayList<Integer>>>();
	HashMap<String,Double> letterBid = new HashMap<String,Double>();

	HashMap<Character,ArrayList<Integer>> bids = new HashMap<Character,ArrayList<Integer>>();
	
	//initializes the number of players
	
	PlayersHistory(ArrayList<String> players, int noOfUnknownLetters)
	{
		int size=players.size();
		for(int i=0;i<size;i++)
		{
			if(!players.get(i).equals("seven.G2.SuperPlayer2"))
			playersStats.get(i).playerName=players.get(i);
		}
		/*with the noOfUnknownLetters add tht to Character Set Contained 
		*with the help of Scrabble
		*/
	}
	
	//updates after every bid
	
	public void playerStatusUpdate(Character bidLetter, String wonBy, HashMap<String,Integer> bidValues)
	{
		int size=playersStats.size();
		WordList wl=new WordList();
		ArrayList<Integer> bidVals = new ArrayList<Integer>();
		for(int i =0;i<size;i++)
		{
			if(playersStats.get(i).playerName.equals(wonBy))
			{
				playersStats.get(i).gotLetters[playersStats.get(i).noOfLetters]=bidLetter;
				ArrayList<Integer> bidsForLetter=new ArrayList<Integer>();
				bidsForLetter=playersStats.get(i).bidValues.get(bidLetter);
				bidsForLetter.add(bidValues.get(wonBy));
				playersStats.get(i).bidValues.put(bidLetter,bidsForLetter);
				playersStats.get(i).noOfLetters++;
				
				/* updating the possible words that could be formed with the letters the players possess
				*also update the possible bid values for each alphabet for the players
				*/
				
				String letters=new String();
				letters="";
				for(int j=0;j<playersStats.get(i).gotLetters.length;j++)
				{
					letters+=playersStats.get(i).gotLetters[j].charValue();
				}
				playersStats.get(i).possibleWords=wl.getValidWords(letters);
				break;
			}
			else
			{
				//update bid values for that letter
			}
			
			// Updates Bids for all players to its particular character
			
			for(int j=0;j<playersStats.size();j++)
			{
				ArrayList<Integer> alreadyPresent = new ArrayList<Integer>();
				alreadyPresent = playersStats.get(j).bidValues.get(playersStats.get(j).playerName);
				alreadyPresent.add(bidValues.get(playersStats.get(j).playerName));			
				playersStats.get(j).bidValues.put(bidLetter,alreadyPresent );
			}	
			
		}
		
	}
	
	public ArrayList<PlayerStatus> getPlayersStatus()
	{
		
		return playersStats;
	}
	
	
	public ArrayList<Double> possibleBids(Character bidLetter)
	{
		/*compute what a player might bid for that Letter using previous bid values for that letter
		* and possible words a player might form with those letters
		*/
		ArrayList<Double> possibleBids = new ArrayList<Double>();
		int k =0;
		for(int i=0; i< playersStats.size();i++)
		{
			if(playersStats.get(i).bidValues.containsKey(bidLetter))
			{
				Double currBid =0.0 ;
				for(k=0; k< playersStats.get(i).bidValues.get(bidLetter).size(); k++)
				{
					currBid+= playersStats.get(i).bidValues.get(bidLetter).get(k);
				}
				possibleBids.add((currBid/k));
			}
		}
		
		return possibleBids;		
	}
	
	//public storeBidResults()
	 
}
