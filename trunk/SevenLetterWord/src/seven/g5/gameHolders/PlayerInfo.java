package seven.g5.gameHolders;

import java.util.ArrayList;

import seven.g5.DictionaryHandler;
import seven.ui.Letter;

public class PlayerInfo {

	private ArrayList<Letter> rack;
	private int playerId;
	private DictionaryHandler dictionaryHandler;
	
	public PlayerInfo() {}
	
	public PlayerInfo(ArrayList<Letter> rack, int playerId, DictionaryHandler dh) {
		this.rack = rack;
		this.playerId = playerId;
		this.dictionaryHandler = dh;
	}
	
	public ArrayList<Letter> getRack() {
		return rack;
	}
	public void setRack(ArrayList<Letter> rack) {
		this.rack = rack;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public DictionaryHandler getDictionaryHandler() {
		return dictionaryHandler;
	}

	public void setDictionaryHandler(DictionaryHandler dictionaryHandler) {
		this.dictionaryHandler = dictionaryHandler;
	}
}
