package seven.g2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import seven.g2.miner.LetterMine.LetterSet;
import seven.g2.util.Logger;
import seven.g2.util.ScrabbleWord;
import seven.g2.util.WordGroup;
import seven.g2.util.WordList;
import seven.g2.util.WordUtility;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class OldPlayer3 implements Player {

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
	private LetterSet currLetterSet;
	private ScrabbleBag scrabbleBag;
	private int totalNoOfAuctions = 0;
	private int noOfHiddenLetters = 0;
	private int noOfAuctionsLeft = 0;
	private int amountSpentInCurrentRound = 0;
	private ScrabbleWord currentBestWord = null;
	private boolean isSevenLetterWordPossible = true;
	private WordGroup currWordGroup = null;
	private double limitThreshold = 0.3;
	private HashMap<Character,LetterSet> allPossNewSets = null;
	private int perRoundReserve = 0;
	int cnt =0;
	/**
	 * 
	 */
	public OldPlayer3() {
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

		log.fatal("currString = " + currLetterSet.getLetters());
		
		/** Round is complete. Perform cleanup **/
		cleanUp();

		log.debug("Returning word = " + retWord);
		log.debug("Old total bid amount = " + totalBidAmount);
		totalBidAmount += retScore;
		log.debug("New total bid amount = " + totalBidAmount);

		if (retWord == null) {
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


		scrabbleBag.updateSeenTileInformation(bidLetter.getAlphabet());

		int bidAmount = 0;

		/** Check if I am done **/
		if (currString.length() == 7) {
			return bidAmount;
		}
		/** Check if we are the only player left in the game **/
		if (noOfAuctionsLeft == 7 - currString.length() - 1) {
			log.debug("Bidding 0. I am the only bidder left.");
			bidAmount = 0;
			if(noOfAuctionsLeft == 0){
				updatePreviousBidResult(bidLetter.getAlphabet(),0);
			}
		} else {

			/** Consider the new string **/
			String possNewString = WordUtility
					.insertCharacterInLexicographicPosition(currString,
							bidLetter.getAlphabet());

			LetterSet possNewLG = wordList.getLetterGroup(possNewString);

			//DO THIS to calculate probability
			
//			if(possNewLG != null && possNewLG.getWords().length < 8000)
//				for(String s: possNewLG.getWords())
//				{
//					ScrabbleWord w = new ScrabbleWord();
//					w.setWord(s);
//					w.getProbability(scrabbleBag.lettersLeft, currLetterSet.getLetters());
//				}
			log.debug("New string under consideration = " + possNewString);

			if (possNewLG == null) {
				// Definitely do not bid more than 0 if this is not the basis
				// for a word
				bidAmount = 0;
			} else {
				WordGroup possNewWordGroup = new WordGroup(possNewLG);
				scrabbleBag.filterWordGroup(possNewWordGroup, possNewString);

				if(currWordGroup!= null){
				log.debug("Current: Total = "
						+ currWordGroup.getTotalOccurrences() + " 7W = "
						+ currWordGroup.getOccurrences(7));
				}
				
				log.debug("New: Total = "
						+ possNewWordGroup.getTotalOccurrences() + " 7W = "
						+ possNewWordGroup.getOccurrences(7));
				
				if (possNewString.length() <= minTilesForAnalysis) {

					if (possNewString.length() == 1) {

						if (possNewWordGroup.getTotalOccurrences() > 15000) {
							bidAmount = bidLetter.getValue() + 1;
							logBid(bidAmount,
									"WIN = GOOD : At least >15000 words can still be made.");

							if (noOfAuctionsLeft < totalNoOfAuctions
									/ noOfPlayers * (noOfPlayers - 1)) {
								bidAmount++;
								logBid(bidAmount,
										"WIN = GOOD : Bid Incremented as I haven't yet picked first letter.");
							}
						} else {
							bidAmount = 0;
							logBid(bidAmount,
									"WIN = BAD : Only <15000 words can be made.");

						}

					} else {
						
						HashMap<Character,LetterSet> allPossSuccessors = wordList.getAllSuccessors(currString);
						HashMap<Character,WordGroup> allPossSuccessorsWG = scrabbleBag.filteredWordGroups(allPossSuccessors, currString);
						int noOfCandidates = allPossSuccessorsWG.size();
						int noOfGoodCandidates = 0;
						
						for (Character c: allPossSuccessorsWG.keySet()) {
							double totalOccurencesRatio1 = allPossSuccessorsWG.get(c).getTotalOccurrences() * 1.0
							/ currWordGroup.getTotalOccurrences();
							if(totalOccurencesRatio1 > limitThreshold){
								noOfGoodCandidates++;
							}
						}
						
						log.debug("No of candidate successors = " + noOfCandidates);
						log.debug("No of good successors (> "+ limitThreshold*100+"%) = " + noOfGoodCandidates);
						
						double totalOccurencesRatio = possNewWordGroup.getTotalOccurrences() * 1.0
						/ currWordGroup.getTotalOccurrences();
						
						if (totalOccurencesRatio > limitThreshold) {
							bidAmount = bidLetter.getValue();
							
							int bidValIncr = 1;
							//int bidValIncr = getBidValIncrement(noOfGoodCandidates, noOfCandidates, noOfAuctionsLeft,bidLetter);
							
							bidAmount+= bidValIncr;
							logBid(bidAmount,
									"WIN = GOOD : Can make >"+ limitThreshold*100+"% of words before this letter.");
						} else {
							bidAmount = 0;
							logBid(bidAmount,
									"WIN = BAD : Can make <"+ limitThreshold*100+"% of words before this letter.");

						}

					}
				} else {
					if (isSevenLetterWordPossible) {
						log.debug("Currently a 7 letter word can be formed.");
						if (possNewWordGroup.getOccurrences(7) == 0) {
							bidAmount = 0;
							logBid(bidAmount,
									"WIN = BAD : Cant form a 7 letter word.");
						} else {
							if (currString.length() == 6) {
								int randIncr = rand
										.nextInt(totalBidAmount / 10);
								bidAmount = bidLetter.getValue() + randIncr;

								logBid(bidAmount,
										"WIN = GOOD : Can still make a 7 letter word.");

							} else if (possNewWordGroup.getTotalOccurrences()
									* 1.0 / currWordGroup.getTotalOccurrences() > 0.1
									&& possNewWordGroup.getOccurrences(7) * 1.0
											/ currWordGroup.getOccurrences(7) > 0.1) {

								int credit = currentBestWord.getScore()
										- amountSpentInCurrentRound;
								int randIncr = (credit <= 0) ? 0 : rand
										.nextInt(credit);

								bidAmount = bidLetter.getValue() + randIncr;

								logBid(bidAmount,
										"WIN = GOOD : Can make > 10% of words and > 10% of 7 letter words.");
							} else {
								bidAmount = 0;
								logBid(bidAmount, "WIN = BAD : Limit myself.");

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
							int randIncr = (credit <= 0) ? 0 : rand
									.nextInt(credit);
							bidAmount = scoreImpr + randIncr;
							logBid(bidAmount, "WIN = GOOD : Score improves.");
						} else {
							bidAmount = 0;
							logBid(bidAmount,
									"WIN = BAD : Score doesnt improve.");
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
		
		perRoundReserve = (int) (0.1 * totalBidAmount);
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
				updatePreviousBidResult(pb.getTargetLetter().getAlphabet(), pb.getWinAmmount());
			} else {
				if (currWordGroup != null) {
					/**
					 * A letter is lost ... this may mean some words are no
					 * longer possible
					 **/
					scrabbleBag.filterWordGroup(currWordGroup, currString);
				}
			}
		}
	}

	/**
	 * Updates player information with respect to previous bid
	 * 
	 * @param PlayerBidList
	 */
	private void updatePreviousBidResult(char letter,int winAmount) {
			cnt++;
			log.debug("Who won the bid? ... meeeee :)");
	
			currString = WordUtility
					.insertCharacterInLexicographicPosition(currString, letter);
	
			log.fatal("Current String = " + currString);
	
			calculateBestCurrentWord();
	
			totalBidAmount -= winAmount;
			amountSpentInCurrentRound += winAmount;
	
			updateIsSevenLetterPossible();
	
			currWordGroup = new WordGroup(currLetterSet);
			scrabbleBag.filterWordGroup(currWordGroup, currString);
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
		if (currLetterSet == null
				|| !currLetterSet.getLetters().equals(currString)
				&& wordList.getLetterGroup(currString) != null) {
			currLetterSet = wordList.getLetterGroup(currString);
			currWordGroup = new WordGroup(currLetterSet);
			scrabbleBag.filterWordGroup(currWordGroup, currString);
		}

		currentBestWord = wordList.getBestSubWord(currString);

		log.debug("CurrentBestWord = " + currentBestWord.getWord()
				+ " Score = " + currentBestWord.getScore());

	}

	/**
	 * Updates the value of field isSevenLetterPossible
	 */
	private void updateIsSevenLetterPossible() {
		isSevenLetterWordPossible = (wordList.getLetterGroup(currString) != null && wordList
				.getLetterGroup(currString).getOccurrences(7) != 0);

		log.debug("isSevenLetterWordPossible = " + isSevenLetterWordPossible);

	}

	private void logBid(int bidAmount, String logString) {
		log.debug("BidAmount = " + bidAmount + " " + logString);
	}
	
	private int getBidValIncrement(int noOfGoodCandidates, int noOfCandidates,int noOfAuctionsLeft,Letter bidLetter_){
		return rand.nextInt(1 + (1 -  noOfGoodCandidates /noOfCandidates) * bidLetter_.getValue() / 2);
	}
}
