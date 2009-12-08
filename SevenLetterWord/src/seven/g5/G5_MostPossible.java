package seven.g5;


import java.util.ArrayList;
import java.util.HashMap;

import seven.g5.Logger.LogLevel;
import seven.g5.data.ScrabbleParameters;
import seven.g5.strategies.EmpiricalFrameworkStrategy;
import seven.g5.strategies.LessThanSevenLetterStrategy;
import seven.g5.strategies.MostPossibleWordsStrategy;
import seven.g5.strategies.RareLetterKickOffStrategy;
import seven.g5.strategies.SingleSevenLetterStrategy;
import seven.g5.strategies.CommonLetterKickOffStrategy;
import seven.g5.strategies.Strategy;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;

public class G5_MostPossible implements Player {

	private Logger log;
	private Strategy strategy;
	private int continueStratFlag = 1;

	//this is out rack of letters or our "hand"
	private ArrayList<Letter> myRack;

	//round information
	private int turnNumber = 0;
	private int totalRounds;

	//bidding info
	private int totalPoints = 100;
	private HashMap<Character, Integer> numberLettersRemaining = new HashMap<Character, Integer>();
	private int totalLettersRemaining;
	private int numberTurnsRemaining;
	
	//dictionary handler
	private DictionaryHandler dh;
	
	//info holder
	PlayerInfo pi;
	GameInfo gi;
	
	
	public G5_MostPossible() {
		this.log = new Logger(LogLevel.DEBUG, this.getClass());
		this.myRack = new ArrayList<Letter>();
		this.dh = new DictionaryHandler();
	}

	public void Register() {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public String returnWord() {
		String finalWord = strategy.getFinalWord(this.gi, this.pi);
		this.turnNumber = 0;
		this.myRack.clear();
		this.pi.setRack(myRack);
		this.gi.setPlayerBidList(null);
		this.gi.setCurrentBidLetter(null);
		this.continueStratFlag = 1;
		initializeLettersRemaining();
		return finalWord;
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList, int totalRounds, ArrayList<String> PlayerList, SecretState secretstate, int PlayerID) {
				//get the letters we start with
		
		if(this.turnNumber == 0) {
			numberTurnsRemaining =  PlayerList.size() * 7;
			initializeLettersRemaining();
			for(Letter ltr : secretstate.getSecretLetters()) {
				if( ltr != null ) { 
					this.myRack.add(new Letter(ltr.getAlphabet(),ScrabbleParameters.getScore(ltr.getAlphabet())));
					decrementLettersRemainingInBag( ltr );
				}
			}
		}
		else --numberTurnsRemaining;

		if( bidLetter != null )
			decrementLettersRemainingInBag( bidLetter ); 

		//fill person info
		this.pi = new PlayerInfo(this.myRack, PlayerID, this.dh);
		
		//fill gameInfo
		this.gi = new GameInfo(PlayerBidList, bidLetter, totalRounds, secretstate, PlayerList, numberTurnsRemaining, numberLettersRemaining, totalLettersRemaining);
		if( continueStratFlag == 1 ) {
			if( PlayerList.size() > 1 ) {
	//			if( this.myRack.size() == 0 )
	//				this.strategy = new CommonLetterKickOffStrategy();
	//			else
					this.strategy = new MostPossibleWordsStrategy();
			}
			else {
				if( this.myRack.size() == 0 ) 
					this.strategy = new RareLetterKickOffStrategy();
				else if( this.myRack.size() == 1 )
					this.strategy = new CommonLetterKickOffStrategy();
				else this.strategy = new EmpiricalFrameworkStrategy();
//				else this.strategy = new LessThanSevenLetterStrategy();
			}
		}
		else //a strategy has indicated that no more seven letter words can be found
			this.strategy = new LessThanSevenLetterStrategy();


		//get results from last round
		if(PlayerBidList != null && PlayerBidList.size() > 0 ) {
			PlayerBids currentPlayerBids = (PlayerBids)(PlayerBidList.get(PlayerBidList.size()-1));
			if( currentPlayerBids.getWinnerID() == this.pi.getPlayerId()){
				this.totalPoints -= currentPlayerBids.getWinAmmount();
				this.myRack.add(currentPlayerBids.getTargetLetter());
			}			
		}
		
//		if(pi.getRack().size() >= 2) {
//			this.strategy = new EmpiricalFrameworkStrategy();
//		}
		
		turnNumber++;
		
		int[] answer = strategy.getBid(this.gi, this.pi);
		int bid = answer[0];
		if( continueStratFlag != 0 ) //once we're in less-than-seven-letter mode we stay there
			continueStratFlag = answer[1];		
		return bid;
	}
	
	//this is stuff regarding probability and tiles remaining
	private void decrementLettersRemainingInBag(Letter letter2) {
		int oldAmount = numberLettersRemaining.get(letter2.getAlphabet());
		numberLettersRemaining.put(letter2.getAlphabet(), --oldAmount );
		--totalLettersRemaining;
		//--noOfTurnsRemaining;
	}
	
	public void initializeLettersRemaining() {
		numberLettersRemaining.put('A', ScrabbleParameters.getCount('A'));
		numberLettersRemaining.put('B', ScrabbleParameters.getCount('B'));
		numberLettersRemaining.put('C', ScrabbleParameters.getCount('C'));
		numberLettersRemaining.put('D', ScrabbleParameters.getCount('D'));
		numberLettersRemaining.put('E', ScrabbleParameters.getCount('E'));
		numberLettersRemaining.put('F', ScrabbleParameters.getCount('F'));
		numberLettersRemaining.put('G', ScrabbleParameters.getCount('G'));
		numberLettersRemaining.put('H', ScrabbleParameters.getCount('H'));
		numberLettersRemaining.put('I', ScrabbleParameters.getCount('I'));
		numberLettersRemaining.put('J', ScrabbleParameters.getCount('J'));
		numberLettersRemaining.put('K', ScrabbleParameters.getCount('K'));
		numberLettersRemaining.put('L', ScrabbleParameters.getCount('L'));
		numberLettersRemaining.put('M', ScrabbleParameters.getCount('M'));
		numberLettersRemaining.put('N', ScrabbleParameters.getCount('N'));
		numberLettersRemaining.put('O', ScrabbleParameters.getCount('O'));
		numberLettersRemaining.put('P', ScrabbleParameters.getCount('P'));
		numberLettersRemaining.put('Q', ScrabbleParameters.getCount('Q'));
		numberLettersRemaining.put('R', ScrabbleParameters.getCount('R'));
		numberLettersRemaining.put('S', ScrabbleParameters.getCount('S'));
		numberLettersRemaining.put('T', ScrabbleParameters.getCount('T'));
		numberLettersRemaining.put('U', ScrabbleParameters.getCount('U'));
		numberLettersRemaining.put('V', ScrabbleParameters.getCount('V'));
		numberLettersRemaining.put('W', ScrabbleParameters.getCount('W'));
		numberLettersRemaining.put('X', ScrabbleParameters.getCount('X'));
		numberLettersRemaining.put('Y', ScrabbleParameters.getCount('Y'));
		numberLettersRemaining.put('Z', ScrabbleParameters.getCount('Z'));
	}
}
