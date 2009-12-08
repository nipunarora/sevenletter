package seven.g2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import seven.g2.miner.LetterMine.LetterSet;
import seven.g2.util.Logger;
import seven.g2.util.ScrabbleUtility;
import seven.g2.util.ScrabbleWord;
import seven.g2.util.WordGroup;
import seven.g2.util.WordList;
import seven.g2.util.WordUtility;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class OtherSuperPlayer2 implements Player {

	private int noOfWords = 54833;

	/** Config params etc **/
	private int minTilesForAnalysis = 3;
	private double limitThreshold1 = 15000;
	private double limitThreshold2 = 0.3;
	private double limitThreshold3 = 0.1;
	private double limitThreshold4 = 0.1;

	private Logger log = new Logger(Logger.LogLevel.DEBUG, this.getClass());

	/** Information relevant across rounds **/
	private int totalNoOfRounds;
	private int totalBidAmount;
	private WordList wordList;
	private boolean isNewRound;
	private boolean isFirstRound;
	private int currRound;
	private int noOfPlayers;
	private Random rand = new Random();
	private int myID;
	private PlayersHistory playersHistory = null;

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
	int cnt =0;
	boolean didIBid = false;
	private int scale = 1;
	private int minBidBase = 1;
	/**
	 * 
	 */
	
	private PlayersHistory playerHistory; 
	public OtherSuperPlayer2() {
		wordList = new WordList();
		totalBidAmount = 100;
		currString = "";
		currRound = 0;
		isNewRound = true;
		isFirstRound = true;
	}

	public void Register() {

	}

	public String returnWord() {

		calculateCurrentCapabilities();
		String retWord = "";
		int retScore = 0;
		if(currentBestWord!= null){
			retWord = currentBestWord.getWord();
			 retScore = currentBestWord.getScore();
		}
		//log.fatal("currString = " + currString);

		//log.fatal("currString = " + currLetterSet.getLetters());
		
		/** Round is complete. Perform cleanup **/
		cleanUp();

		log.debug("Returning word = " + retWord);
		log.debug("Old total bid amount = " + totalBidAmount);
		totalBidAmount += retScore;
		log.debug("New total bid amount = " + totalBidAmount);

		if (retWord == null) {
			retWord = "";
		}
		
		if(currRound == totalNoOfRounds){
			System.out.println(cnt);
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

			/** Update self/others based on previous auction result **/
			updatePreviousBidResult(PlayerBidList);
			
			noOfAuctionsLeft--;
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

						if (possNewWordGroup.getTotalOccurrences() > limitThreshold1) {
							/** First chance randomizer to avoid infighting **/
							bidAmount = bidLetter.getValue() + rand.nextInt(3)
							 + rand.nextInt((int)(possNewWordGroup.getTotalOccurrences() / limitThreshold1  * 3));
							
							logBid(bidAmount,
									"WIN = GOOD : At least >15000 words can still be made.");
					//added now to bid based on history
//							if(currRound != 0)
//							{
//								ArrayList<Double> allBids=playersHistory.possibleBids(bidLetter);
//							
//							if(allBids.size()!=0)
//							{
//								
//								double max=0;
//								for(int i=0;i<allBids.size();i++)
//								{
//									if(allBids.get(i) > max)
//										max = allBids.get(i);
//								}
//								
//								log.debug("max = " + max);
//								bidAmount = (int)max+rand.nextInt(3);
//							
//							}
//							}
							
							/** Check if we have seen x number of letters i.e x = no of players **/
							if (noOfAuctionsLeft < totalNoOfAuctions
									/ noOfPlayers * (noOfPlayers - 1)) {
								bidAmount+= getAuctionCountBasedIncr();
								
								logBid(bidAmount,
										"WIN = GOOD : Bid Incremented as I haven't yet picked first letter.");
							}
							
						} else {
							bidAmount = 0;
							logBid(bidAmount,
									"WIN = BAD : Only <15000 words can be made.");

						}

					} else {
												
						double totalOccurencesRatio = possNewWordGroup.getTotalOccurrences() * 1.0
						/ currWordGroup.getTotalOccurrences();
						
						double[] values = doAnalysis1();
						
						if (totalOccurencesRatio > limitThreshold2) {
							bidAmount = bidLetter.getValue();
							
							int bidValIncr = getBidValIncrement1(values,bidLetter,totalOccurencesRatio) 
												+ getAuctionCountBasedIncr();

							log.debug("Bid Components = " + bidAmount + "," + bidValIncr);
							bidAmount+= bidValIncr;
							logBid(bidAmount,
									"WIN = GOOD : Can make >"+ limitThreshold2*100+"% of words before this letter.");
						} else {
							bidAmount = 0;
							logBid(bidAmount,
									"WIN = BAD : Can make <"+ limitThreshold2*100+"% of words before this letter.");

						}

					}
				} else {
					if (isSevenLetterWordPossible) {
						log.debug("Currently a 7 letter word can be formed.");
						
						ScrabbleWord sw = calculateBestWordPossible(bidLetter
								.getAlphabet());
						
						if (possNewWordGroup.getOccurrences(7) == 0) {
							bidAmount = 0;
							logBid(bidAmount,
									"WIN = BAD : Cant form a 7 letter word.");
						} else {
							if (currString.length() == 6) {
								
								/** Calculate gain **/
								int gain = sw.getScore()
										- amountSpentInCurrentRound;
//								
//								int randIncr = rand
//										.nextInt(totalBidAmount / 10);
								
//								bidAmount = bidLetter.getValue() + rand.nextInt(gain/2) ;

								//With random probability p, perturb our bid to be in the range [bid,9]
								int amountLeftTo50 = (50-amountSpentInCurrentRound)/(7-currString.length());
								if(bidAmount > 0 && bidAmount < amountLeftTo50)
									bidAmount = bidAmount + rand.nextInt(amountLeftTo50-bidAmount);
	
								logBid(bidAmount,
										"WIN = GOOD : This makes me a 7 letter word.");

							} else{ 
									double sevenLetterOccurencesRatio = possNewWordGroup.getTotalOccurrences()
									* 1.0 / currWordGroup.getTotalOccurrences();
									double totalOccurencesRatio = possNewWordGroup.getTotalOccurrences() * 1.0
									/ currWordGroup.getTotalOccurrences();
									if (sevenLetterOccurencesRatio > limitThreshold3 && totalOccurencesRatio > limitThreshold4) {
	
										// doAnalysis2(possNewString);
		
										//bidAmount = bidLetter.getValue() + getCreditBasedRandIncr() + getAuctionCountBasedIncr();
										bidAmount = getBidValIncrement2(sevenLetterOccurencesRatio,totalOccurencesRatio,bidLetter);
										logBid(bidAmount,
												"WIN = GOOD : Can make >"+ limitThreshold4*100+"% of words and >"+ limitThreshold3*100+"% of 7 letter words.");
										//With random probability p, perturb our bid to be in the range [bid,9]
										int amountLeftTo50 = (50-amountSpentInCurrentRound)/(7-currString.length());
										if(bidAmount > 0 && bidAmount < amountLeftTo50)
											bidAmount = bidAmount + rand.nextInt(amountLeftTo50-bidAmount);
			
									} else {
									bidAmount = 0;
									logBid(bidAmount, "WIN = BAD : Limit myself.");
	
								}
							}
							
						}
					} else {
						log.debug("Currently a 7 letter word cant be formed.");
						ScrabbleWord sw = calculateBestWordPossible(bidLetter
								.getAlphabet());
						if (sw.getScore() > currentBestWord.getScore()) {
							
							/** Improvement in score **/
							int scoreImpr = sw.getScore()
									- currentBestWord.getScore();
														
							/** Calculate the random increment from the credit **/
							int randIncr = getCreditBasedRandIncr();
							
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
		
		if(bidAmount != 0){
			didIBid = true;
		}else{
			didIBid = false;
		}
		
		return bidAmount;
	}

	/**
	 * Round is beginning, perform initialization of per round data
	 */
	private void init(int totalNoOfRounds_, ArrayList<String> PlayerList,
			SecretState secretState, int playerID) {

		log.debug("First auction of this round... Initializing");
		
		if(isFirstRound){
			myID = playerID;
			/** Update info which holds across rounds **/
			totalNoOfRounds = totalNoOfRounds_;
			noOfPlayers = PlayerList.size();
			noOfHiddenLetters = secretState.getSecretLetters().size();
			//playersHistory = new PlayersHistory(noOfPlayers,noOfHiddenLetters,playerID);
			isFirstRound = false;
			
			if(noOfPlayers == 2){
				limitThreshold1 = 8000;
				limitThreshold2 = 0.2;
				minBidBase = 3;
				scale = 2;
				
			}
		}
		
		playersHistory = new PlayersHistory(noOfPlayers,noOfHiddenLetters,playerID);

		if(currRound % 10 == 0){
			//minBidBase++;
		}
		/** Update info which holds per round **/
		isNewRound = false;
		currRound++;
		
		currString = "";
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

		log.debug("Hidden Tiles = \"" + currString + "\"");

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
			calculateCurrentCapabilities();
			updateIsSevenLetterPossible();
			updateStrategy();
		} else {
			isSevenLetterWordPossible = true;
		}

	}

	/**
	 * Update strategy
	 */
	private void updateStrategy() {
		if(currString.length() < minTilesForAnalysis){
			int cnt = currWordGroup.getTotalOccurrences();
			double minExpectedCnt = limitThreshold1;
			if(currString.length() == 2){
				minExpectedCnt*= limitThreshold2;
			}
		
			if(cnt < minExpectedCnt){
				
			}
		}else{
			
		}
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
		currLetterSet = null;
		currWordGroup = null;
		
		limitThreshold2 = 0.3;
		limitThreshold3 = 0.1;

	}

	/**
	 * Updates player information with respect to previous bid
	 * 
	 * @param PlayerBidList
	 */
	private void updatePreviousBidResult(ArrayList<PlayerBids> PlayerBidList) {
		if (PlayerBidList.size() > 0) {
			playersHistory.playersHistoryUpdate(PlayerBidList);
			PlayerBids pb = PlayerBidList.get(PlayerBidList.size() - 1);

			if (pb.getWinnerID() == myID) {
				updatePreviousBidResult(pb.getTargetLetter().getAlphabet(), pb.getWinAmmount());
			} else {
				if(didIBid == true){
					cnt++;
				}
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
	
			log.debug("Current String = " + currString);
	
			calculateCurrentCapabilities();
	
			totalBidAmount -= winAmount;
			amountSpentInCurrentRound += winAmount;
	
			updateIsSevenLetterPossible();
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
	private void calculateCurrentCapabilities() {
		if (currLetterSet == null
				|| !currLetterSet.getLetters().equals(currString)
				&& wordList.getLetterGroup(currString) != null) {
			currLetterSet = wordList.getLetterGroup(currString);
		}


		currWordGroup = new WordGroup(currLetterSet);
		scrabbleBag.filterWordGroup(currWordGroup, currString);
		
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

	/**
	 * Logs the bid amount
	 * @param bidAmount
	 * @param logString
	 */
	private void logBid(int bidAmount, String logString) {
		log.debug("BidAmount = " + bidAmount + " " + logString);
	}
	
	/**
	 * Gets the randomized bid value increment for first three rounds 
	 * which purely depends on how good the successors are on basis of all words 
	 * count only
	 * 
	 * @param candidates
	 * @param goodCandidates
	 * @param noOfAuctionsLeft
	 * @param bidLetter
	 * @param totalOccurencesRatio_ 
	 * @return
	 */
	private int getBidValIncrement1(double[] values,Letter bidLetter, double totalOccurencesRatio_){
		int totalCnt = ScrabbleUtility.TOTAL_TILE_COUNT - scrabbleBag.totalSeenTiles;
				
		int bidIncr = (int) (4 * totalOccurencesRatio_/ scrabbleBag.lettersLeft[bidLetter.getAlphabet() - 'A']);
		//int bidIncr = (int) (4 * totalOccurencesRatio_/ values[2] / scrabbleBag.lettersLeft[bidLetter.getAlphabet() - 'A']);
		
		int randIncr1 = rand.nextInt((int)(1 + (1 -  values[1] * 1.0  /totalCnt) * 2 * bidLetter.getValue()));
		
		log.debug("Bid Increment for <= 3 letters = " + minBidBase + "," + bidIncr+ "," + randIncr1);
		return minBidBase + bidIncr + randIncr1;
	}
	
	/**
	 * 
	 * @param bidLetter_ 
	 * @param totalOccurencesRatio_ 
	 * @param sevenLetterOccurencesRatio_ 
	 * @return
	 */
	private int getBidValIncrement2(double sevenLetterOccurencesRatio_, double totalOccurencesRatio_, Letter bidLetter){
		int baseBid = 2 + bidLetter.getValue() + getCreditBasedRandIncr() + getAuctionCountBasedIncr();

		int bidIncr = (int) (7 * sevenLetterOccurencesRatio_/ scrabbleBag.lettersLeft[bidLetter.getAlphabet() - 'A']);
		
		return baseBid + bidIncr;
	}
	
	/**
	 * Returns an increment to bid based on how many auctions are left ..
	 * @return
	 */
	private int getAuctionCountBasedIncr(){
		double factor2 = ((7 - currString.length()) - ((noOfAuctionsLeft - 1) * 1.0/ noOfPlayers )) * scale;
		factor2 = factor2 > 0 ? Math.ceil(factor2 -1): 0;
		
		int randIncr2 = (int)(factor2/2) + rand.nextInt((int)(1 + factor2/2 ));
		
		log.debug("Bid Increment based on auctions = " + factor2 );
		
		return (int)randIncr2;
	}
	
	/**
	 * Gets a random increment for bid from the credit of this round
	 * @return
	 */
	private int getCreditBasedRandIncr(){
		
		if(currentBestWord == null){
			return 0;
		}
		
		/** Current credit **/
		int credit = currentBestWord.getScore()
				- amountSpentInCurrentRound;
		
		/** Calculate the random increment from the credit **/
		int randIncr = (credit <= 0) ? 0 : rand
				.nextInt(credit);
		
		return randIncr;
	}
	
	private double[] doAnalysis1() {
		HashMap<Character, WordGroup> allPossSuccessorsWG = scrabbleBag
				.filteredWordGroups(wordList.getAllSuccessors(currString),
						currString);

		HashSet<Character> goodSuccessors = new HashSet<Character>();
		HashMap<Character, Double> occCounts = new HashMap<Character, Double>();
		int totalCount = scrabbleBag.getSumTileCount(allPossSuccessorsWG.keySet());

		HashMap<Double, Character> occCounts1 = new HashMap<Double, Character>();
		
		for (Character c : allPossSuccessorsWG.keySet()) {
			double totalOccurencesRatio1 = allPossSuccessorsWG.get(c)
					.getTotalOccurrences()
					* 1.0 / currWordGroup.getTotalOccurrences();
			occCounts.put(c, totalOccurencesRatio1);
			occCounts1.put(totalOccurencesRatio1, c);

		}
		
		ArrayList<Double> ald2 = new ArrayList<Double>(occCounts.values());
		Collections.sort(ald2);
		Collections.reverse(ald2);
		
		
		double threshold = 0;
		int count = 0;
		for(Double d: ald2){
			threshold = d;
			count+= scrabbleBag.lettersLeft[occCounts1.get(d) - 'A'];
			
			if ( count * 1.0 / totalCount >= 0.4){
				break;
			}
		}
		

		limitThreshold2 = threshold;

		log.debug("Adjusting limitThreshold2 = " + limitThreshold2);
		
		for (Character c : allPossSuccessorsWG.keySet()) {			
			if (occCounts.get(c) > limitThreshold2) {
				goodSuccessors.add(c);
			}
		}
		
		log.debug("No of candidate successors = " + allPossSuccessorsWG.size());
		log.debug("No of good successors (> " + limitThreshold2 * 100 + "%) = "
				+ goodSuccessors.size() + " " + goodSuccessors);

		int counts[] =  new int[] {
				scrabbleBag.getSumTileCount(allPossSuccessorsWG.keySet()),
				scrabbleBag.getSumTileCount(goodSuccessors) };
		
		ArrayList<Double> ald = new ArrayList<Double>(occCounts1.keySet());
		Collections.sort(ald);
		Collections.reverse(ald);
		
		return new double[]{counts[0],counts[1],ald2.get(0),ald.get(0)};

	}

	private void doAnalysis2(String possNewString) {

		HashMap<Character, WordGroup> allPossSuccessorsWG = scrabbleBag
				.filteredWordGroups(wordList.getAllSuccessors(currString),
						currString);
		int noOfCandidates = allPossSuccessorsWG.size();
		int noOfGoodCandidates = 0;
		HashSet<Character> goodSuccessors = new HashSet<Character>();

		HashSet<Character> goodSuccessorsP = new HashSet<Character>();
		//System.out.print(System.currentTimeMillis());
		for (Character c : allPossSuccessorsWG.keySet()) {
			double totalOccurencesRatio1 = allPossSuccessorsWG.get(c)
					.getTotalOccurrences()
					* 1.0 / currWordGroup.getTotalOccurrences();

			double totalOccurencesRatio2 = allPossSuccessorsWG.get(c)
					.getOccurrences(7)
					* 1.0 / currWordGroup.getOccurrences(7);

			if (totalOccurencesRatio1 > limitThreshold3
					&& totalOccurencesRatio2 > limitThreshold3) {
				noOfGoodCandidates++;
				goodSuccessors.add(c);
				//System.out.println(c + ":" + totalOccurencesRatio1);
			}

			double sumExpectedScore = scrabbleBag.getSumProbability(
					allPossSuccessorsWG.get(c), possNewString);
			goodSuccessorsP.add(c);
			//System.out.println(c + ":" + sumExpectedScore);

		}
		//System.out.print(System.currentTimeMillis());
		log.debug("No of candidate successors = " + noOfCandidates);
		log.debug("No of good successors (> " + limitThreshold2 * 100 + "%) = "
				+ noOfGoodCandidates + " " + goodSuccessors);
		log.debug("No of good successors (> 15 expected score) = "
				+ goodSuccessorsP.size() + " " + goodSuccessorsP);

	}
}
