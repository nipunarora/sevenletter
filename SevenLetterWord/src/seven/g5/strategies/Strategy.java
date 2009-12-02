package seven.g5.strategies;

import java.util.ArrayList;
import java.util.HashMap;

import seven.g5.Logger;
import seven.g5.Logger.LogLevel;
import seven.g5.data.ScrabbleParameters;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public abstract class Strategy {
	
	protected Logger log;
	protected HashMap<Character, Integer> numberLettersRemaining = new HashMap<Character, Integer>();
	protected int bidpoints = 100;
	protected ArrayList<String> playerList;

	public Strategy( ) {
		initializeLettersRemaining();
		log = new Logger(LogLevel.ERROR, this.getClass());
	}

	private void initializeLettersRemaining() {
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

	public abstract int getBid(GameInfo gi, PlayerInfo pi);

	public abstract String getFinalWord();
	
	
}
