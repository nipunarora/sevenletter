//package seven.g5.strategies;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.PriorityQueue;
//import java.util.Random;
//import java.io.FileReader;
//import java.util.*;
//
//import seven.ui.CSVReader;
//
//import seven.g5.Logger;
//import seven.g5.Logger.LogLevel;
//import seven.g5.data.OurLetter;
//import seven.g5.data.ScrabbleParameters;
//import seven.g5.data.Word;
//import seven.g5.gameHolders.GameInfo;
//import seven.g5.gameHolders.PlayerInfo;
//import seven.g5.apriori_ben.*;
//import seven.g5.apriori_ben.DataMine.ItemSet;
//import seven.g5.apriori_ben.LetterMine.LetterSet;
//import seven.ui.Letter;
//import seven.ui.PlayerBids;
//import seven.ui.SecretState;
//
//public class SimpleStrategy extends Strategy {
//
//	//monal
//	private PlayerBids cpbid = null ;
//	private Letter letter ;
//	private Word bestFoundWord;
//	//protected OurLetter currentLetter;
//	protected ArrayList<Letter> hand = new ArrayList<Letter>();
//	private int totalTurns;
//	private Word bestFutureWord;
//
//	int size = 0;
//	int futureSize = 0;
//	HashMap<Letter,Integer> currentLettersToBidFor = new HashMap<Letter,Integer>();
//	ArrayList<Word> ListofWords = new ArrayList<Word>();
//	char[] letters = new char[100] ;
//	char[] futureLetters = new char[100] ;
//	Dictionary sowpods = new Dictionary();
//	int currentTurn = 0;
//
//	public SimpleStrategy () {
//		super();
//		loadDictionary();
//	}
//
//	private void loadDictionary() {
//		try{
//			CSVReader csvreader = new CSVReader(new FileReader("src/seven/g5/data/FilteredWords.txt"));
//			String[] nextLine;
//			//csvreader.readNext(); // Waste the first line
//			while((nextLine = csvreader.readNext()) != null)
//			{
//				String word = nextLine[0];
//				sowpods.wordlist.put(word, Boolean.TRUE);
//			}
//
//		} catch(Exception e)
//		{
//			e.printStackTrace();
//			//System.out.println("\n Could not load dictionary!");
//		}
//	}
//
//	/**
//	 *
//	 * @return the value of the bid to place
//	 */
//
//	public int getBid(GameInfo gi, PlayerInfo pi) {
//
//		this.letter = gi.getCurrentBid();
//
//		//get the letters we start with
//		if (currentTurn++ == 0) {
//			this.totalTurns = gi.getPlayerBidList().size() * 7;
//			for(Letter ltr: gi.getSecretState().getSecretLetters()) {
//				this.hand.add(new Letter(ltr.getAlphabet(),ScrabbleParameters.getScore(ltr.getAlphabet())));
//			}
//			for (int i = 0; i<this.hand.size(); i++) {
//				decrementLettersRemainingInBag(hand.get(i));
//			}
//		}
//
//		//track how many of each letter remains (as far as we can tell)
//		if (letter != null) decrementLettersRemainingInBag( letter );
//
//		if( gi.getPlayerBidList() != null && gi.getPlayerBidList().size() > 0 ) {
//			PlayerBids currentPlayerBids = (PlayerBids)(gi.getPlayerBidList().get(gi.getPlayerBidList().size()-1));
//			if( currentPlayerBids.getWinnerID() == pi.getPlayerId()){
//				//log.debug("We won a "+this.letter);
//				this.bidpoints -= currentPlayerBids.getWinAmmount();
//				//getLetterList(currentPlayerBids.getTargetLetter());
//				hand.add(letter);
//			}       
//		}
//
//		if (hand.size() < 2 &&
//				letter.getAlphabet() == 'S' ||
//				letter.getAlphabet() == 'E' ||
//				letter.getAlphabet() == 'A' ||
//				letter.getAlphabet() == 'I' ||
//				letter.getAlphabet() == 'O' ||
//				letter.getAlphabet() == 'N' ||
//				letter.getAlphabet() == 'R' ||
//				letter.getAlphabet() == 'T' )
//			return 1; //bid for common letters at first
//
//		int returnBid;
//		currentLettersToBidFor = getBidworthyLetters();
//		log.debug("letters to bid for:");
//		for (Letter c: currentLettersToBidFor.keySet()) {
//			log.debug("bid for "+c.getAlphabet()+":"+currentLettersToBidFor.get(c)+" ");
//		}
//		returnBid = calculateBid(currentLettersToBidFor,gi.getCurrentBid());
//		return returnBid;
//
//	}
//
//	private int calculateBid(HashMap<Letter,Integer> currentLettersToBidFor2,
//			Letter bidLetter) {
//		for(Letter l: currentLettersToBidFor.keySet()) {
//			if(l.getAlphabet() == bidLetter.getAlphabet()) {
//				return currentLettersToBidFor.get(l);
//			}
//		}
//		return 0;
//	}
//
//	//    /**
//	//     * @param hand the arraylist of characters currently in our hand
//	//     * @return optimal word
//	//     */
//	//    protected String getOptimalWordFromHand( ArrayList<Letter> hand1 ) {
//	//        return getBestWordOfList(getListOfFoundWords( hand1 ));
//	//    }
//	//   
//	//    protected String getOptimalWordFromFuture( ArrayList<Letter> hand1 ) {
//	//        //here add every possible permutation of characters to fill out the rest of the hand
//	//        //then we'll see what words we can create with those
//	//        //then calculate the percentage chance of getting those letters (each Word's weightedScore)
//	//        return null;
//	//    }
//
//
//	//    protected ArrayList<Word> getListOfFoundWords( ArrayList<Letter> hand2 ) {
//	//        // TODO Auto-generated method stub
//	//        //this is what will call the anagram solver
//	//        return null;
//	//    }
//
//	public String getFinalWord() {
//		getListofPossibleWords();
//		//ArrayList<Word> finalList = convertStringsToWords(ListofWords);
//		bestFoundWord = getBestWordOfList( ListofWords );
//		//unloadDictionary();
//		if ( bestFoundWord != null ) return bestFoundWord.toString();
//		else return "";
//	}
//
//
//	private void unloadDictionary() {
//		sowpods.wordlist.clear();
//	}
//
//	public void getListofPossibleWords(){
//		//getWordlist();
//		solveCurrentlyHeld() ;
//		bestFoundWord = getBestWordOfList(ListofWords);
//		//solve(0);
//		//return ListofWords;
//	}
//
//	void solveCurrentlyHeld()
//	{
//		int[] freq = new int[26] ;
//		int[] tp = new int[26] ;
//		for(int i=0;i<26;++i) freq[i] = 0 ;
//		for(int i=0;i<hand.size();++i) ++freq[hand.get(i).getAlphabet()-'A'] ;
//		//for(int i=0;i<hand.size();++i) System.out.print(hand.get(i).getAlphabet() + " ") ;
//		////System.out.println("") ;
//		for(String str : sowpods.wordlist.keySet()) if(sowpods.wordlist.get(str)){
//
//			for(int i=0;i<26;++i) tp[i] = 0 ;
//			int i = 0 ;
//			for(;i<str.length();++i)
//			{
//				char ch = str.charAt(i) ;
//				++tp[ch-'A'] ;
//				if(tp[ch-'A'] > freq[ch-'A']) break ;
//			}
//			if(i < str.length()) continue ;
//			//int k = 0 ;
//			//for(;k<26;++k) if(tp[k] > freq[k]) continue ;
//			ListofWords.add(new Word(str)) ;
//		}
//	}
//
//
//	/**
//	 *
//	 * @return final word to return at the end of the round
//	 */
//	public HashMap<Letter,Integer> getBidworthyLetters() {
//		log.debug("getting good letters");
//		HashMap<Letter,Integer> goodLetters = new HashMap<Letter,Integer>();
//		HashMap<Letter,ArrayList<Word>> possibleWordsWithOneMoreLetter = new HashMap<Letter,ArrayList<Word>>();
//		possibleWordsWithOneMoreLetter = getListofPossibleWords(possibleWordsWithOneMoreLetter,hand);
//		solveCurrentlyHeld(); //get the ones we already found.
//		bestFoundWord = getBestWordOfList(ListofWords);
//		String oneOfTheFutureWords;
//		for(Letter ltr: possibleWordsWithOneMoreLetter.keySet()) {
//			for(Word lw: ListofWords) {
//				for(int i=0; i<possibleWordsWithOneMoreLetter.get(ltr).size(); i++) {
//					oneOfTheFutureWords = possibleWordsWithOneMoreLetter.get(ltr).get(i).toString();
//					if (lw.toString() == oneOfTheFutureWords) {
//						log.debug("removed "+oneOfTheFutureWords);
//						possibleWordsWithOneMoreLetter.get(ltr).remove(i);
//					}
//				}
//				if( possibleWordsWithOneMoreLetter.get(ltr).size() > 0 ) {
//					bestFutureWord = getBestWordOfList(possibleWordsWithOneMoreLetter.get(ltr));
//					goodLetters.put(ltr, (bestFutureWord.getScore() - bestFoundWord.getScore()));
//				}
//			}
//		}
//
//		//ListofWords.clear();
//		return goodLetters;
//	}
//
//	public HashMap<Letter,ArrayList<Word>> getListofPossibleWords( HashMap<Letter,ArrayList<Word>> possibleWordsByLetter, ArrayList<Letter> possibleHand ){
//		for (int i=0; i<26; i++) {
//			char c = (char)(i+'A');
//			possibleHand.add(new Letter(c,ScrabbleParameters.getScore(c)));
//			solveFuture(possibleHand,possibleWordsByLetter);
//			//this would be cool, but it's impossibly slow
//			//if( possibleHand.size() <= 7 ) possibleWordsByLetter = getListofPossibleWords(possibleWordsByLetter, possibleHand);
//			possibleHand.remove(possibleHand.size()-1);
//		}
//		return possibleWordsByLetter;
//	}
//
//	void solveFuture(ArrayList<Letter> hand1, HashMap<Letter, ArrayList<Word>> possibleWordsByLetter)
//	{
//		ArrayList<Word> wordsForASingleLetter = new ArrayList<Word>();
//		int[] freq = new int[26] ;
//		int[] tp = new int[26] ;
//		for(int i=0;i<26;++i) freq[i] = 0 ;
//		for(int i=0;i<hand1.size();++i) ++freq[hand1.get(i).getAlphabet()-'A'];
//		//for(int i=0;i<hand1.size();++i) System.out.print(hand1.get(i).getAlphabet()) ;
//		////System.out.println("") ;
//		for(String str : sowpods.wordlist.keySet()) if(sowpods.wordlist.get(str)){
//			for(int i=0;i<26;++i) tp[i] = 0 ;
//			int i = 0 ;
//			for(;i<str.length();++i)
//			{
//				char ch = str.charAt(i) ;
//				++tp[ch-'A'] ;
//				if(tp[ch-'A'] > freq[ch-'A']) break ;
//			}
//			if(i < str.length()) continue ;
//			log.debug("with a "+hand1.get(hand1.size()-1).getAlphabet()+" could make "+str);
//			wordsForASingleLetter.add(new Word(str));
//		}
//		possibleWordsByLetter.put(hand1.get(hand1.size()-1),wordsForASingleLetter);
//	}
//
//	public void getLetterList(Letter x){
//		char c = x.getAlphabet();
//		++size;
//		//there's a bug here
//		//colin removed
//		//letters[size-1] = c;
//		hand.add(x);
//		////System.out.println("Word list: "+words);
//	}
//
//	//        public void getFutureWordlist(Letter x){
//	//            char c = x.getAlphabet();
//	//            ++futureSize;
//	//            //there's a bug here
//	//            futureWords[futureSize-1] = c;
//	//            ////System.out.println("Word list: "+words);
//	//        }
//
//
//	//        public static void main( String[] args ) {
//	//                SimpleStrategy strat = new SimpleStrategy();
//	//                ArrayList<Letter> myHand = new ArrayList<Letter>();
//	//               
//	//                //ArrayList<String> myWordList = new ArrayList<String>();              
//	//                strat.binHeapOfCurrentWords.add(new Word("CAT"));
//	//                strat.binHeapOfCurrentWords.add(new Word("RAT"));
//	//                strat.binHeapOfCurrentWords.add(new Word("HAT"));
//	//               
//	//                while( strat.binHeapOfCurrentWords.size() > 0 ) {
//	//                       // //System.out.println("word "+strat.binHeapOfCurrentWords.peek()+" is "+((Word)strat.binHeapOfCurrentWords.poll()).getScore() );
//	//                }
//	//        }
//
//
//
//	public void getWordlist(){
//		char c = letter.getAlphabet();
//		++size;
//		//colin removed
//		//letters[size-1] = c;
//
//	}
//
//
//
//
//	class Dictionary
//	{
//		Hashtable<String,Boolean> wordlist;
//
//		public Dictionary()
//		{
//			wordlist = new Hashtable<String, Boolean>();
//		}
//	}
//
//	/**
//	 * @param numberOfTurnsLeft the number of characters that are still to be drawn, a function of the number of players * 7 - number of characters dispensed so far
//	 * @return the value of the letter times the chance of getting it.
//	 */
//	public float getAdjustedValueOfWord( int numberOfTurnsLeft, String targetWord ) {
//		//              TODO
//		//              float probability = (float)(numberInGame-numberSeen)/(float)numberOfTurnsLeft;
//		//              return (float)value * probability;
//		return 0;
//	}
//}