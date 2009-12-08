package seven.g2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import seven.g2.miner.LetterMine.LetterSet;
import seven.g2.util.Logger;
import seven.g2.util.ScrabbleUtility;
import seven.g2.util.ScrabbleWord;
import seven.g2.util.WordList;
import seven.g2.util.WordUtility;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class OldPlayer2 implements Player {

	private int noOfWords = 54833;

	/** Config params etc **/
	private int maxRandomIncrementForBid = 1;
	private int minLettersToHaveToBidAggressively = 3;
	private int minTilesForAnalysis = 3;
	private Logger log = new Logger(Logger.LogLevel.NONE, this.getClass());

	/** Information relevant across rounds **/
	private int totalNoOfRounds;
	private int totalBidAmount;
	private WordList wordList;
	private boolean isNewRound;
	private int currRound;
	private int noOfPlayers;
	private Random rand = new Random();
	private int myID;

	/** Information relevant to particular round **/
	private String currString = "";
	private LetterSet currLetterGroup;
	private ScrabbleBag scrabbleBag;
	private int totalNoOfAuctions = 0;
	private int noOfHiddenLetters = 0;
	private int noOfAuctionsLeft = 0;
	private int amountSpentInCurrentRound = 0;
	private ScrabbleWord currentBestWord = null;
	private boolean isSevenLetterWordPossible = true;

	private double limitThreshold = 0.3;

	/**
	 * 
	 */
	public OldPlayer2() {
		wordList = new WordList();
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

		log.fatal("currString = " + currString);
		/** Round is complete. Perform cleanup **/
		cleanUp();

		
		log.fatal("Returning word = " + retWord);
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
			SecretState secretstate, int playerID) {

		/** If it is new round initialize information **/
		if (isNewRound) {
			init(total_rounds, PlayerList, secretstate, playerID);
		} else {
			noOfAuctionsLeft--;
			
			/** Update self/others based on previous auction result **/
			updatePreviousBidResult(PlayerBidList);
		}

		int bidAmount = 0;

		/** Check if I am done **/
		if (currString.length() == 7) {
			return bidAmount;
		}
		/** Check if we are the only player left in the game **/
		if (noOfAuctionsLeft == 7 - currString.length() - 1) {
			log.debug("Bidding 0. I am the only bidder left.");
			bidAmount = 0;
		} else {

			/** Consider the new string **/
			String possNewString = WordUtility
					.insertCharacterInLexicographicPosition(currString,
							bidLetter.getAlphabet());

			LetterSet possNewLG = wordList
					.getLetterGroup(possNewString);

			log.debug("New string under consideration = " + possNewString);
			
			if(possNewLG == null)
			{
				//Definitely do not bid more than 0 if this is not the basis for a word
				bidAmount = 0;
			}
			else
			{
				if (possNewString.length() <= minTilesForAnalysis) {
	
					if (possNewString.length() == 1) {
	
						if (possNewLG.getTotalOccurrences() > 15000) {
							bidAmount = bidLetter.getValue() + 1;
							logBid(bidAmount,"WIN = GOOD : At least >15000 words can still be made.");
							
							if (noOfAuctionsLeft < totalNoOfAuctions / noOfPlayers
									* (noOfPlayers - 1)) {
								bidAmount++;
								logBid(bidAmount,"WIN = GOOD : Bid Incremented as I haven't yet picked first letter.");
							}
						} else {
							bidAmount = 0;
							logBid(bidAmount,"WIN = BAD : Only <15000 words can be made.");
							
						}
	
					} else {
						if (possNewLG.getTotalOccurrences() * 1.0
								/ currLetterGroup.getTotalOccurrences() > 0.3) {
							bidAmount = bidLetter.getValue() + 1;
							logBid(bidAmount,"WIN = GOOD : Can make >30% of words before this letter.");
						} else {
							bidAmount = 0;
							logBid(bidAmount,"WIN = BAD : Can make <30% of words before this letter.");
	
						}
	
					}
				} else {
					if (isSevenLetterWordPossible) {
						log.debug("Currently a 7 letter word can be formed.");
						if (possNewLG.getOccurrences(7) == 0) {
							bidAmount = 0;
							logBid(bidAmount,"WIN = BAD : Cant form a 7 letter word.");
						} else {
							if (currString.length() == 6) {
								int randIncr = rand.nextInt(totalBidAmount / 10);
								bidAmount = bidLetter.getValue() + randIncr;
	
								logBid(bidAmount,"WIN = GOOD : Can still make a 7 letter word.");
	
							} else if (possNewLG.getTotalOccurrences() * 1.0
									/ currLetterGroup.getTotalOccurrences() > 0.1
									&& possNewLG.getOccurrences(7) * 1.0
											/ currLetterGroup.getOccurrences(7) > 0.1) {
	
								int credit = currentBestWord.getScore()
										- amountSpentInCurrentRound;
								int randIncr = (credit <= 0) ? 0 : rand
										.nextInt(credit);
	
								bidAmount = bidLetter.getValue() + randIncr;
	
								logBid(bidAmount,"WIN = GOOD : Can make > 10% of words and > 10% of 7 letter words.");
							} else {
								bidAmount = 0;
								logBid(bidAmount,"WIN = BAD : Limit myself.");
	
							}
						}
					} else {
						log.debug("Currently a 7 letter word cant be formed.");
						ScrabbleWord sw = calculateBestWordPossible(bidLetter
								.getAlphabet());
						if (sw.getScore() > currentBestWord.getScore()) {
							int scoreImpr = sw.getScore()
									- currentBestWord.getScore();
							int credit = currentBestWord.getScore()
									- amountSpentInCurrentRound;
							int randIncr = (credit <= 0) ? 0 : rand.nextInt(credit);
							bidAmount = scoreImpr + randIncr;
							logBid(bidAmount,"WIN = GOOD : Score improves.");
						} else {
							bidAmount = 0;
							logBid(bidAmount,"WIN = BAD : Score doesnt improve.");
						}
					}
				}
			}
		}
		return bidAmount;
	}

	/**
	 * Round is beginning, perform initialization of per round data
	 */
	private void init(int totalNoOfRounds_, ArrayList<String> PlayerList,
			SecretState secretState, int playerID) {

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
		totalNoOfAuctions = noOfAuctionsLeft;

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
		// currentBestWord = null;
		// currentBestWord.setWord("");
		// currentBestWord.setScore(0);
		currentBestWord = new ScrabbleWord();
		currentBestWord.setScore(0);

		if (currString.length() > 0) {
			calculateBestCurrentWord();
			updateIsSevenLetterPossible();
		} else {
			isSevenLetterWordPossible = true;
		}

		myID = playerID;

		log.fatal("Player ID = " + myID);
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
	private ScrabbleWord calculateBestWordPossible(Character wonLetter) {

		String newStr = WordUtility.insertCharacterInLexicographicPosition(
				currString, wonLetter);
		ScrabbleWord ret = wordList.getBestSubWord(newStr);
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
				|| !currLetterGroup.getLetters().equals(currString) && wordList.getLetterGroup(currString) != null) {
			//System.out.println("Current string: " + currString);
			currLetterGroup = wordList.getLetterGroup(currString);
		}

		currentBestWord = wordList.getBestSubWord(currString);

		log.debug("CurrentBestWord = " + currentBestWord.getWord()
				+ " Score = " + currentBestWord.getScore());

	}

	/**
	 * Updates the value of field isSevenLetterPossible
	 */
	private void updateIsSevenLetterPossible() {
		isSevenLetterWordPossible = ( wordList.getLetterGroup(currString) != null && wordList.getLetterGroup(currString)
				.getOccurrences(7) != 0);

		log.debug("isSevenLetterWordPossible = " + isSevenLetterWordPossible);

	}

	private void logBid(int bidAmount, String logString) {
		log.debug("BidAmount = " + bidAmount + " " + logString);
	}
}
