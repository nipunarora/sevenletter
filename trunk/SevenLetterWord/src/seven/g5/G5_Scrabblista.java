package seven.g5;


import java.util.ArrayList;

import seven.g5.Logger.LogLevel;
import seven.g5.data.ScrabbleParameters;
import seven.g5.strategies.RiskyStrategy;
import seven.g5.strategies.SimpleStrategy;
import seven.g5.strategies.Strategy;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;

public class G5_Scrabblista implements Player {

	private Logger log;
	private Strategy strategy;
	
	//this is out rack of letters or our "hand"
	private ArrayList<Letter> myRack;
	
	//round information
	private int roundNum = 0;
	private int totalRounds;
	
	//bidding info
	private int totalPoints = 100;
	
	//dictionary handler
	private DictionaryHandler dh;
	
		
	public G5_Scrabblista() {
		log = new Logger(LogLevel.DEBUG, this.getClass());
		strategy = new SimpleStrategy();
		this.myRack = new ArrayList<Letter>();
		this.dh = new DictionaryHandler();
	}

	public void Register() {

		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public String returnWord() {
		//throw new UnsupportedOperationException("Not supported yet.");
		return strategy.getFinalWord();
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList, int totalRounds, ArrayList<String> PlayerList, SecretState secretstate, int PlayerID) {
		this.roundNum++;
		
		//get the letters we start with
		if (this.roundNum == 0) {
			this.totalRounds = PlayerList.size() * 7;
			for(Letter ltr : secretstate.getSecretLetters()) {
				this.myRack.add(new Letter(ltr.getAlphabet(),ScrabbleParameters.getScore(ltr.getAlphabet())));
			}
		}

		//get results from last round
		if(PlayerBidList != null && PlayerBidList.size() > 0 ) {
			PlayerBids currentPlayerBids = (PlayerBids)(PlayerBidList.get(PlayerBidList.size()-1));
			if( (currentPlayerBids.getWonBy().equals("seven.g5.G5_Scrabblista"))){
				this.totalPoints -= currentPlayerBids.getWinAmmount();
				this.myRack.add(currentPlayerBids.getTargetLetter());
			}       
		}
		
		//fill person info
		PlayerInfo pi = new PlayerInfo(this.myRack, PlayerID, this.dh);
		
		//fill gameInfo
		GameInfo gi = new GameInfo(PlayerBidList, bidLetter, this.totalRounds, secretstate);
		
		return strategy.getBid(gi, pi);
	}
}
