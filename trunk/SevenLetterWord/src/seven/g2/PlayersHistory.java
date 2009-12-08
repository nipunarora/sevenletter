package seven.g2;
import seven.ui.Letter;

import java.util.ArrayList;
import seven.ui.PlayerBids;
import java.util.HashMap;
import seven.g2.util.*;



class PlayerStatus
{
	int playerID;
	Character[] gotLetters;
	HashMap<Character,ArrayList<Integer>> bidValues = new HashMap<Character,ArrayList<Integer>>();
	ArrayList<ScrabbleWord> possibleWords;
	int noOfLetters;
	
}

public class PlayersHistory {

	ArrayList<Integer> minBids = new ArrayList<Integer>();
	ArrayList<Integer> maxBids = new ArrayList<Integer>();
	
//	PlayersHistory playerHist = new PlayersHistory(PlayerList.size(),secretState.getSecretLetters().size(),playerID);
	ArrayList<PlayerStatus> playersStats;
	HashMap<String,HashMap<Character,ArrayList<Integer>>> playersBids = new HashMap<String,HashMap<Character,ArrayList<Integer>>>();
	HashMap<String,Double> letterBid = new HashMap<String,Double>();
	HashMap<Character,ArrayList<Integer>> bids = new HashMap<Character,ArrayList<Integer>>();
	int secretLettersCount;
	int ourID=-1;
	//initializes the number of players
	
	PlayersHistory(int noOfPlayers, int noOfUnknownLetters, int myID)
	{
		secretLettersCount=noOfUnknownLetters;
		playersStats =new ArrayList<PlayerStatus>(noOfPlayers-1);
		
		if(ourID==-1)
		{
			//round one 
			for(int i=0; i < noOfPlayers;i++)
			{
				minBids.add(i,1000);
				maxBids.add(i,-1);
				
			}
		}
		
		ourID=myID;
		
		
		for(int i=0;i<noOfPlayers;i++)
		{
			PlayerStatus ps = new PlayerStatus();
			ps.playerID=i;
			ps.noOfLetters=0;
			ps.gotLetters=new Character[7];
			// if(i!=myID)
				playersStats.add(ps);
				
		}
		
		for(int i=0;i<playersStats.size();i++)
				System.out.println(playersStats.get(i).playerID);
		System.out.println(" hello hello new round");
		
		/*with the noOfUnknownLetters add that to Character Set Contained 
		*with the help of Scrabble
		*/
	}
	
	//updates after every bid
	
	public void playersHistoryUpdate(ArrayList<PlayerBids> PlayerBidList)
	{
		playerStatusUpdate(PlayerBidList.get(PlayerBidList.size()-1).getTargetLetter().getAlphabet(),PlayerBidList.get(PlayerBidList.size()-1).getWinnerID(),PlayerBidList.get(PlayerBidList.size()-1).getBidvalues());
	}
	
	
	
	public void playerStatusUpdate(Character bidLetter, int winnerID, ArrayList<Integer> bidValues)
	{
		int size=playersStats.size();
		WordList wl=new WordList();
		ArrayList<Integer> bidVals = new ArrayList<Integer>();
		
		for(int i =0;i<size;i++)
		{
			if(winnerID != ourID )
			{
			if(playersStats.get(i).playerID==winnerID)
			{
				if(playersStats.get(i).noOfLetters != 0)
				{
				playersStats.get(i).gotLetters[playersStats.get(i).noOfLetters]=bidLetter;
				}
				else
					playersStats.get(i).gotLetters[0]=bidLetter;
				
				playersStats.get(i).noOfLetters++;
			 
				 
			}
			
			ArrayList<Integer> bidsForLetter=new ArrayList<Integer>();
				/* updating the possible words that could be formed with the letters the players possess
				*also update the possible bid values for each alphabet for the players
				*/
			System.out.println(playersStats.get(i).playerID+ " " + bidValues.size());
			if(!playersStats.get(i).bidValues.containsKey(bidLetter))
			{
				playersStats.get(i).bidValues.put(bidLetter, bidsForLetter);
			}
				
			bidsForLetter=playersStats.get(i).bidValues.get(bidLetter);
			if(bidsForLetter!=null)
				bidsForLetter.add(bidValues.get(i));
			else
				bidsForLetter.add(0,bidValues.get(i));
			
			
			
			playersStats.get(i).bidValues.put(bidLetter,bidsForLetter);

			}
			
			// Updates Bids for all players to its particular character
		}
		
		for(int i =0; i < bidValues.size() ; i++)
		{
			if(i == ourID)
				continue;
			if(bidValues.get(i)<minBids.get(i))
				minBids.add(i,bidValues.get(i));
			if(bidValues.get(i)>maxBids.get(i))
				maxBids.add(i,bidValues.get(i));
		}
		
		}
		
	
	
	public ArrayList<PlayerStatus> getPlayersStatus()
	{
		
		return playersStats;
	}
	
	
	
	
	
	public ArrayList<Double> possibleBids(Letter bidLetter)
	{
		/*compute what a player might bid for that Letter using previous bid values for that letter
		* and possible words a player might form with those letters
		*/
		ArrayList<Double> possibleBids = new ArrayList<Double>();
		int k =0;
		int gotIt=0;
		int notGotAllLetters=1;
		
		for(int i=0; i< playersStats.size();i++)
		{
			if(i == ourID)
			 continue;
			for(int j=0;  j<playersStats.get(i).noOfLetters;j++)
			{
				if(playersStats.get(i).gotLetters[j].equals(bidLetter))
				{
					gotIt=1;
				}
				if(playersStats.get(i).noOfLetters+secretLettersCount == 7)
				{
					notGotAllLetters=0;
				}
					
			}
			if(playersStats.get(i).bidValues.containsKey(bidLetter))
			{
					
				Double currBid =0.0 ;
				for(k=0; k< playersStats.get(i).bidValues.get(bidLetter).size(); k++)
				{
					currBid+= playersStats.get(i).bidValues.get(bidLetter).get(k);
				}
				//if the player has already got the letter, he will bid less when the letter appears again

				currBid-=gotIt*bidLetter.getValue();
				if(notGotAllLetters==1)
				{
					possibleBids.add((currBid/k));
				
				}
				notGotAllLetters=1;
				gotIt=0;
			}
		}
		
		return possibleBids;		
	}
	
	
	public ArrayList<Integer> getMinBids()
	{
		return minBids;
	}
	

	public int getAvgMinBids()
	{
		int sum=0;
		int avg=0;
		for(int i=0;i<minBids.size();i++)
		{
			sum+=minBids.get(i);
			
		}
		if(minBids.size()!=0)
			avg=sum/minBids.size();
		return avg;
	}
	
	public int getAvgMaxBids()
	{
		int sum=0;
		int avg=0;
		for(int i=0;i<maxBids.size();i++)
		{
			sum+=maxBids.get(i);
			
		}
		if(maxBids.size()!=0)
			avg=sum/maxBids.size();
		return avg;
	}
	
	
	public ArrayList<Integer> getMaxBids()
	{
		return maxBids;
	}
	//public storeBidResults()
	 
}
