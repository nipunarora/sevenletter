package seven.g2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import seven.g2.util.Logger;
import seven.g2.util.ScrabbleUtility;
import seven.g2.util.ScrabbleWord;
import seven.g2.util.DBWordList;
import seven.g2.util.WordUtility;
import seven.g2.util.DBWordList.LetterGroup;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class OldPlayer1 implements Player {

	/** Config params etc **/
	private int maxRandomIncrementForBid = 1;
	private int minLettersToHaveToBidAggressively = 3;
	private int minTilesForAnalysis = 3;
	private HashMap<String, DBWordList.LetterGroup> cache = new HashMap<String, DBWordList.LetterGroup>();
	private Logger log = new Logger(Logger.LogLevel.NONE,this.getClass());
	
	/** Information relevant across rounds **/
	private int totalNoOfRounds;
	private int totalBidAmount;
	private DBWordList DBWordList;
	private boolean isNewRound;
	private int currRound;
	private int noOfPlayers;
	private Random rand = new Random();
	private int myID;

	/** Information relevant to particular round **/
	private String currString = "";
	private DBWordList.LetterGroup currLetterGroup = null;
	private ScrabbleBag scrabbleBag;
	private int noOfHiddenLetters = 0;
	private int noOfAuctionsLeft = 0;
	private int amountSpentInCurrentRound = 0;
	private LetterGroup currentBestWord = null;
	private boolean isSevenLetterWordPossible = true;


	/**
	 * 
	 */
	public OldPlayer1() {
		DBWordList = new DBWordList();
		totalBidAmount = 100;
		currString = "";

		currRound = 0;
		isNewRound = true;
	}

	public void Register() {

	}

	public String returnWord() {

		String retWord = currentBestWord.getWord();
		int retScore = currentBestWord.getScore();
		/** Round is complete. Perform cleanup **/
		cleanUp();

		log.debug("Returning word = " + retWord);
		log.debug("Old total bid amount = " + totalBidAmount);
		totalBidAmount += retScore;
		log.debug("New total bid amount = " + totalBidAmount);
		
		if(retWord == null){
			retWord = "";
		}
		return retWord;
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate,int playerID) {

		/** If it is new round initialize information **/
		if (isNewRound) {
			init(total_rounds, PlayerList, secretstate,playerID);
		} else {
			noOfAuctionsLeft--;
			
			/** Update self/others based on previous auction result **/
			updatePreviousBidResult(PlayerBidList);
		}


		int bidAmount = 0;

		/** Check if we are the only player left in the game **/
		if (noOfAuctionsLeft == 7 - currString.length() - 1) {
			log.debug("Bidding 0. I am the only bidder left.");
			bidAmount = 0;
		} else {

			/** Consider the new string **/
			String possNewString = WordUtility
					.insertCharacterInLexicographicPosition(currString,
							bidLetter.getAlphabet());


			log.debug("New string under consideration = " + possNewString);
			
			DBWordList.LetterGroup possNewLG = getLetterGroup(possNewString);

			if (isSevenLetterWordPossible) {
				log.debug("Currently a 7 letter word can be formed.");
				if (possNewLG.getOccurrences()[6] == 0) {
					log.debug("Bidding 0. If I win this letter, I cant form a 7 letter word.");
					bidAmount = 0;
				} else {
					int randIncr = ((currentBestWord.getScore()
							- amountSpentInCurrentRound) <= 0) ? 0 : rand
							.nextInt(currentBestWord.getScore()
									- amountSpentInCurrentRound);

					if (currString.length() > minLettersToHaveToBidAggressively) {
						bidAmount = bidLetter.getValue() + randIncr;
						log.debug("Bidding "+bidAmount+" . I want this letter much much.");
					} else {
						bidAmount = bidLetter.getValue() / 2 + randIncr;
						log.debug("Bidding "+ bidAmount+" . I want this letter little bittle.");
					}
				}
			} else {
				log.debug("Currently a 7 letter word cant be formed.");
				LetterGroup sw = calculateBestWordPossible(bidLetter.getAlphabet());
				if(sw.getScore() > currentBestWord.getScore()){
					int randIncr = ((currentBestWord.getScore()
							- amountSpentInCurrentRound) <= 0) ? 0 : rand
							.nextInt(currentBestWord.getScore()
									- amountSpentInCurrentRound);
					if (currString.length() > minLettersToHaveToBidAggressively) {
						bidAmount = bidLetter.getValue() + randIncr;
						log.debug("Bidding "+bidAmount+" . I want this letter much much.");
					} else {
						bidAmount = bidLetter.getValue() / 2 + randIncr;
						log.debug("Bidding "+ bidAmount+" . I want this letter little bittle.");
					}
				}else{
					bidAmount = 0;
					log.debug("Bidding 0. If I win this letter, I dont really improve my score.");
				}
			}
		}
		return bidAmount;
	}

	/**
	 * Round is beginning, perform initialization of per round data
	 */
	private void init(int totalNoOfRounds_, ArrayList<String> PlayerList,
			SecretState secretState,int playerID) {

		log.debug("First auction of this round... Initializing");
		
		/** Update info which holds across rounds **/
		totalNoOfRounds = totalNoOfRounds_;
		noOfPlayers = PlayerList.size();

		/** Update info which holds per round **/
		isNewRound = false;
		currRound++;
		currString = "";
		noOfHiddenLetters = secretState.getSecretLetters().size();
		noOfAuctionsLeft = noOfPlayers * (7 - noOfHiddenLetters) - 1;

		/** Extract hidden letter information and update current string */
		Character[] hiddenTiles = new Character[noOfHiddenLetters];
		int i = 0;
		for (Letter l : secretState.getSecretLetters()) {
			hiddenTiles[i] = l.getAlphabet();
			currString = WordUtility.insertCharacterInLexicographicPosition(
					currString, hiddenTiles[i]);
			i++;
		}
		
		log.debug("Hidden Tiles = " + currString);

		/** Init scrabble bag **/
		scrabbleBag = new ScrabbleBag(noOfPlayers, noOfHiddenLetters,
				hiddenTiles);

		amountSpentInCurrentRound = 0;

		/** How much are we def earning now **/
//		currentBestWord = null;
//		currentBestWord.setWord("");
//		currentBestWord.setScore(0);

		currentBestWord = DBWordList.new LetterGroup();
		currentBestWord.setScore(0);
		
		if (currString.length() > 0) {
			calculateBestCurrentWord();
			updateIsSevenLetterPossible();
		} else {
			isSevenLetterWordPossible = true;
		}
		
		myID = playerID;
	}

	/**
	 * Round is over, perform clean up of the per round data
	 */
	private void cleanUp() {
		isNewRound = true;
		currString = null;
		scrabbleBag = null;
		amountSpentInCurrentRound = 0;
		currentBestWord = null;

	}

	/**
	 * Updates player information with respect to previous bid
	 * 
	 * @param PlayerBidList
	 */
	private void updatePreviousBidResult(ArrayList<PlayerBids> PlayerBidList) {
		if (PlayerBidList.size() > 0) {
			PlayerBids pb = PlayerBidList.get(PlayerBidList.size() - 1);
			if (pb.getWinnerID() == myID) {
				log.debug("Who won the bid? ... meeeee :)");

				currString = WordUtility
						.insertCharacterInLexicographicPosition(currString, pb
								.getTargetLetter().getAlphabet());

				log.fatal("Current String = " + currString);

				calculateBestCurrentWord();

				log.debug("CurrentBestWord = " + currentBestWord.getWord()
						+ " Score = " + currentBestWord.getScore());

				totalBidAmount -= pb.getWinAmmount();
				amountSpentInCurrentRound += pb.getWinAmmount();

				updateIsSevenLetterPossible();
			}
		}
	}

	/**
	 * Calculates and updates best current word possible and its score Given
	 * that the new letter won is as specified
	 * 
	 * @param wonLetter
	 */
	private LetterGroup calculateBestWordPossible(Character wonLetter) {

		String newStr = WordUtility.insertCharacterInLexicographicPosition(
				currString, wonLetter);
		LetterGroup ret = DBWordList.getLetterGroup(newStr).getBestSubstring();
		log.debug("New BestWord = " + ret.getWord() + " Score = "
				+ ret.getScore());
		return ret;
	}

	/**
	 * Calculates and updates best current word possible and its score Given the
	 * current set of characters
	 * 
	 * @param wonLetter
	 */
	private void calculateBestCurrentWord() {
		if (currLetterGroup == null
				|| !currLetterGroup.getLetters().equals(currString)) {
			currLetterGroup = DBWordList.getLetterGroup(currString);
		}

		currentBestWord = currLetterGroup.getBestSubstring();

		log.debug("CurrentBestWord = " + currentBestWord.getWord()
				+ " Score = " + currentBestWord.getScore());

	}

	/**
	 * Fetches the letter group from cache. If there is a miss, it goes to
	 * database
	 * 
	 * @param s
	 * @return
	 */
	private DBWordList.LetterGroup getLetterGroup(String s) {
		/** Check if it is already in cache, if not query db **/
		DBWordList.LetterGroup lg = cache.get(s);
		if (lg == null) {
			lg = DBWordList.getLetterGroup(s);
			cache.put(s, lg);
		}

		return lg;
	}

	/**
	 * Updates the value of field isSevenLetterPossible
	 */
	private void updateIsSevenLetterPossible() {
		isSevenLetterWordPossible = getLetterGroup(currString).getOccurrences()[6] != 0;

		log.debug("isSevenLetterWordPossible = " + isSevenLetterWordPossible);

	}
}
