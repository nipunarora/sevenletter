package seven.g5.gameHolders;

import java.util.ArrayList;

import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class GameInfo {
	
	private ArrayList<PlayerBids> playerBidList;
	private Letter currentBid;
	private int numRounds;
	private SecretState secretState;
	
	public GameInfo(){}
	
	public GameInfo(ArrayList<PlayerBids> playerBidList, Letter currentBid, int numRounds, SecretState st) {
		this.playerBidList = playerBidList;
		this.currentBid = currentBid;
		this.numRounds = numRounds;
		this.secretState = st;
	}
	public ArrayList<PlayerBids> getPlayerBidList() {
		return playerBidList;
	}
	public void setPlayerBidList(ArrayList<PlayerBids> playerBidList) {
		this.playerBidList = playerBidList;
	}
	public Letter getCurrentBid() {
		return currentBid;
	}
	public void setCurrentBid(Letter currentBid) {
		this.currentBid = currentBid;
	}
	public int getNumRounds() {
		return numRounds;
	}
	public void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public SecretState getSecretState() {
		return secretState;
	}

	public void setSecretState(SecretState secretState) {
		this.secretState = secretState;
	}
}
