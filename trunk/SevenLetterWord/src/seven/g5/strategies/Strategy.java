package seven.g5.strategies;

import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.g5.*;
import seven.g5.Logger.*;
import seven.g5.apriori_ben.*;
import seven.g5.apriori_ben.DataMine.*;
import seven.g5.apriori_ben.LetterMine.*;
import seven.g5.data.*;

import seven.ui.*;

import java.util.*;

public abstract class Strategy {
	
	protected Logger log;
//	protected HashMap<Character, Integer> numberLettersRemaining = new HashMap<Character, Integer>();
//	protected int bidpoints = 100;
//	protected ArrayList<String> playerList;
	//a priori stuff
//	DataMine mine = null;
//	ItemSet[] answer;

//	PriorityQueue<Word> binHeapOfWordsByValue = new PriorityQueue<Word>(1,
//			new Comparator<Word>() {
//		public int compare(Word a, Word b)
//		{
//			float scoreA = a.getScore();
//			float scoreB = b.getScore();
//			if (scoreB>scoreA)
//				return 1;
//			else if (scoreB<scoreA)
//				return -1;
//			else
//				return 0;
//		}
//	}
//	);

	public Strategy( ) {
		log = new Logger(LogLevel.DEBUG,this.getClass());
	}

//	protected String[] useAPriori(ArrayList<Letter> hand, Letter bidLetter) {
//    	ArrayList<Letter> possibleHand = new ArrayList<Letter>();
//    	for (Letter ltr: hand) possibleHand.add(ltr);
//    	possibleHand.add(bidLetter);
//        LetterSet i = (LetterSet) mine.getCachedItemSet( (String[])(possibleHand.toArray()) );
//		String[] words = i.getWords();
//		//        for (String w : words) {
//		//            log.debug(w);
//		//        }
//        return words;
//	}

//	// 	EXAMPLE USING A PRIORI - NOTE THAT THE BIDDING STRATEGY IS TERRIBLE THOUGH
//	//        int returnBid;
//	//        //put some stuff to find ideal letters here
//	//        if (totalTurns > 42) {
//	//        	String[] wordPathsArray = useAPriori( hand, bidLetter );
//	//        	ArrayList<Word> wordPathsList = convertStringsToWords( wordPathsArray );
//	//        	bestFutureWord = getBestWordOfList( wordPathsList );
//	//        	returnBid = bestFutureWord.getScore();
//	//        }
//	//        return returnBid;
//
//
//	protected ArrayList<Word> convertStringsToWords( String[] theList ) {
//		ArrayList<Word> finalList1 = new ArrayList<Word>();
//		for (int i=0; i<theList.length; i++) {
//			finalList1.add( new Word( theList[i] ));
//		}
//		return finalList1;
//	}

//	private String makeKey(ArrayList<Letter> subkeys) {
//		if (1 == subkeys.size()) return Character.toString(subkeys.get(0).getAlphabet()); // common case short-circuit
//		String tmp[] = (String[])subkeys.toArray();
//		Arrays.sort(tmp);
//		StringBuffer b = new StringBuffer();
//		for (int i = 0; i < tmp.length; i++) {
//			if (0 < i) b.append(' ');
//			b.append(tmp[i]);
//		}
//		return b.toString();
//	}
//
//	protected Word getBestWordOfList( ArrayList<Word> listofWords2 ) {
//		// TODO Auto-generated method stub
//		binHeapOfWordsByValue.clear();
//		for( Word w: listofWords2 ) binHeapOfWordsByValue.add(w);
//		if( binHeapOfWordsByValue.peek() != null ) return binHeapOfWordsByValue.peek();
//		else return null;
//	}

//	protected void decrementLettersRemainingInBag(Letter letter2) {
//		int oldAmount = numberLettersRemaining.get(letter2.getAlphabet());
//		numberLettersRemaining.put(letter2.getAlphabet(), --oldAmount );
//	}
//	
//	private void initializeLettersRemaining() {
//		numberLettersRemaining.put('A', ScrabbleParameters.getCount('A'));
//		numberLettersRemaining.put('B', ScrabbleParameters.getCount('B'));
//		numberLettersRemaining.put('C', ScrabbleParameters.getCount('C'));
//		numberLettersRemaining.put('D', ScrabbleParameters.getCount('D'));
//		numberLettersRemaining.put('E', ScrabbleParameters.getCount('E'));
//		numberLettersRemaining.put('F', ScrabbleParameters.getCount('F'));
//		numberLettersRemaining.put('G', ScrabbleParameters.getCount('G'));
//		numberLettersRemaining.put('H', ScrabbleParameters.getCount('H'));
//		numberLettersRemaining.put('I', ScrabbleParameters.getCount('I'));
//		numberLettersRemaining.put('J', ScrabbleParameters.getCount('J'));
//		numberLettersRemaining.put('K', ScrabbleParameters.getCount('K'));
//		numberLettersRemaining.put('L', ScrabbleParameters.getCount('L'));
//		numberLettersRemaining.put('M', ScrabbleParameters.getCount('M'));
//		numberLettersRemaining.put('N', ScrabbleParameters.getCount('N'));
//		numberLettersRemaining.put('O', ScrabbleParameters.getCount('O'));
//		numberLettersRemaining.put('P', ScrabbleParameters.getCount('P'));
//		numberLettersRemaining.put('Q', ScrabbleParameters.getCount('Q'));
//		numberLettersRemaining.put('R', ScrabbleParameters.getCount('R'));
//		numberLettersRemaining.put('S', ScrabbleParameters.getCount('S'));
//		numberLettersRemaining.put('T', ScrabbleParameters.getCount('T'));
//		numberLettersRemaining.put('U', ScrabbleParameters.getCount('U'));
//		numberLettersRemaining.put('V', ScrabbleParameters.getCount('V'));
//		numberLettersRemaining.put('W', ScrabbleParameters.getCount('W'));
//		numberLettersRemaining.put('X', ScrabbleParameters.getCount('X'));
//		numberLettersRemaining.put('Y', ScrabbleParameters.getCount('Y'));
//		numberLettersRemaining.put('Z', ScrabbleParameters.getCount('Z'));
//	}

	public abstract int[] getBid(GameInfo gi, PlayerInfo pi);

	public String getFinalWord(GameInfo gi, PlayerInfo pi) {
		log.debug("All Words In End!");
		
		int i=0;
		for( Letter l: pi.getRack() ) 
			System.out.print( (i++==0?"":",")+l.getAlphabet());//pi.getDictionaryHandler().pastAnagram(pi.getRack())).toString();
		
		ArrayList<Word> endWordList = pi.getDictionaryHandler().pastAnagram(pi.getRack());
		
		if( endWordList.size() > 0 ) {
			if( endWordList.size() > 0 ) {
				for(Word w : endWordList) {
					log.debug(w.toString() + " - " + w.getScore());
				}
			}
			String endWord = pi.getDictionaryHandler().getBestWordOfList(endWordList).toString();
			if( endWord != null ) 
				return endWord;
		}
		return "(none)";
	}
}
