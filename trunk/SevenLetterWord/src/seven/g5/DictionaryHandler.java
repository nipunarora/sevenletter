package seven.g5;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import seven.g5.Logger.*;
//import seven.g5.Logger.LogLevel;
import seven.g5.apriori_ben.DataMine;
import seven.g5.apriori_ben.LetterMine;
import seven.g5.apriori_ben.DataMine.ItemSet;
import seven.g5.apriori_ben.LetterMine.LetterSet;
import seven.g5.data.ScrabbleParameters;
import seven.g5.data.Word;
import seven.g5.data.OurLetter;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.ui.CSVReader;
import seven.ui.Letter;

public class DictionaryHandler {

	//a priori stuff
	private static DataMine mine;
	private static ItemSet[] answer;
	private Logger log;
	
	//pastAnagram stuff
	private static HashSet<String> wordlist;

	public DictionaryHandler() {
		log = new Logger(LogLevel.DEBUG,this.getClass());
		this.binHeapOfWordsByValue = new PriorityQueue<Word>(1);
		if( DictionaryHandler.mine == null ) {
			DictionaryHandler.mine = new LetterMine("src/seven/g5/data/FilteredWords.txt");//src/seven/g5/super-small-wordlist.txt");
			DictionaryHandler.mine.buildIndex();
			DictionaryHandler.answer = mine.aPriori(0.000001);
			log.debug("Built index");
		}
		if( DictionaryHandler.wordlist == null ) { 
			DictionaryHandler.wordlist = new HashSet<String>();
			this.loadDictionary();
		}
	}

	PriorityQueue<Word> binHeapOfWordsByProbability = new PriorityQueue<Word>(1,
			new Comparator<Word>() {
				public int compare(Word a, Word b)
				{
					double scoreA = a.getProbability();
					double scoreB = b.getProbability();
					if (scoreB>scoreA)
						return 1;
					else if (scoreB<scoreA)
						return -1;
					else
						return 0;
				}
			}
		);
		
	PriorityQueue<OurLetter> binHeapOfOurLettersByNumPossibleWords = new PriorityQueue<OurLetter>(1,
			new Comparator<OurLetter>() {
				public int compare(OurLetter a, OurLetter b)
				{
					double scoreA = a.getNumWordsPossibleWithThisAdditionalLetter();
					double scoreB = b.getNumWordsPossibleWithThisAdditionalLetter();
					if (scoreB>scoreA)
						return 1;
					else if (scoreB<scoreA)
						return -1;
					else
						return 0;
				}
			}
		);
		
	public HashSet<String> getWordlist() {
		return wordlist;
	}

	public void setWordlist(HashSet<String> wordlist) {
		DictionaryHandler.wordlist = wordlist;
	}

	//futureAnagram stuff
	private PriorityQueue<Word> binHeapOfWordsByValue;

	
	public ArrayList<Word> futureAnagram(List<Letter> hand) {
		log.debug("Future!");
		//Utilities.printLetters(hand);
		
		if(hand.size() == 0) {
			return new ArrayList<Word>(0);
		} else {
			ArrayList<Letter> possibleHand = new ArrayList<Letter>(hand);  
			String[] stringLetters = new String[possibleHand.size()];
			for(int i = 0; i < possibleHand.size(); i++) {
				stringLetters[i] = possibleHand.get(i).getAlphabet().toString();
			}
			LetterSet i = (LetterSet) mine.getCachedItemSet(stringLetters);
			
			if(i == null) {
				return new ArrayList<Word>(0);
			}
			
			String[] words = i.getWords();
        	return convertStringsToWords(words);
		}
	}

	public ArrayList<Word> convertStringsToWords(String[] theList) {
		ArrayList<Word> finalList1 = new ArrayList<Word>();
		for (int i=0; i<theList.length; i++) {
			finalList1.add( new Word( theList[i] ));
		}
		return finalList1;
	}

	public Word getBestWordOfList(ArrayList<Word> listofWords2) {
		// TODO Auto-generated method stub
		binHeapOfWordsByValue.clear();
		for( Word w: listofWords2 ) binHeapOfWordsByValue.add(w);
		log.debug("best word: "+binHeapOfWordsByValue.peek());
		if( binHeapOfWordsByValue.peek() != null ) return binHeapOfWordsByValue.peek();
		else return null;
	}
	
	public Word getMostProbableWordOfList(ArrayList<Word> listofWords2) {
		binHeapOfWordsByProbability.clear();
		for( Word w: listofWords2 ) binHeapOfWordsByProbability.add(w);
		if( binHeapOfWordsByProbability.peek() != null ) return binHeapOfWordsByProbability.peek();
		else return null;
	}

	//setup for pastAnagrams
	public void loadDictionary() {
		try{
			CSVReader csvreader = new CSVReader(new FileReader("src/seven/g5/data/FilteredWords.txt"));
			String[] nextLine;
			//csvreader.readNext(); // Waste the first line
			while((nextLine = csvreader.readNext()) != null)
			{
				String word = nextLine[0];
				DictionaryHandler.wordlist.add(word);
				//log.debug(word);
			}

		} catch(Exception e)
		{
			e.printStackTrace();
			log.debug("\n Could not load dictionary!");
		}
	}
	
	public ArrayList<Word> pastAnagram(ArrayList<Letter> rack) {
		ArrayList<Word> goodAnagrams = new ArrayList<Word>();
		
		int[] freq = new int[26] ;
		int[] tp = new int[26] ;
		for(int i=0;i<26;++i) freq[i] = 0 ;
		for(int i=0;i<rack.size();++i) ++freq[rack.get(i).getAlphabet()-'A'] ;
		for(String str : DictionaryHandler.wordlist) if(DictionaryHandler.wordlist.contains(str)){

			for(int i=0;i<26;++i) tp[i] = 0 ;
			int i = 0 ;
			for(;i<str.length();++i)
			{
				char ch = str.charAt(i) ;
				++tp[ch-'A'] ;
				if(tp[ch-'A'] > freq[ch-'A']) break ;
			}
			if(i < str.length()) continue ;
			goodAnagrams.add(new Word(str)) ;
			log.debug("possible word: "+str);
		}
		return goodAnagrams;
	}

	public ArrayList<OurLetter> getLettersWithMostFutureWords(PlayerInfo pi, GameInfo gi, int i) {
		ArrayList<Word> allFutureWords = new ArrayList<Word>();
		ArrayList<Letter> hand = new ArrayList<Letter>();
		ArrayList<OurLetter> targets = new ArrayList<OurLetter>();
		for (Letter ltr: pi.getRack()) hand.add(ltr);
		binHeapOfOurLettersByNumPossibleWords.clear();
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		System.out.println("g5 looking for future words");
		for( int c = 0; c < 26; c++) {
			char l = (char)(c+'A');
			OurLetter Let = new OurLetter(l, ScrabbleParameters.getScore(l));
			hand.add(Let);
			allFutureWords = pi.getDictionaryHandler().futureAnagram(hand);
			Utilities.collectOnlySevenLetters( allFutureWords );
			//allFutureWords = pi.getDictionaryHandler().getLegitWordsFromRemainingLetters(gi.getNumberLettersRemaining(), allFutureWords);
//			System.out.print("'"+Let.getAlphabet()+"'-"+allFutureWords.size()+"|");

			//System.out.print("'"+Let.getAlphabet()+"'-"+allFutureWords.size()+"|");
			Let.setNumWordsPossibleWithThisAdditionalLetter( allFutureWords.size() );
			binHeapOfOurLettersByNumPossibleWords.add(Let);
			hand.remove( hand.size() - 1 );
		}
		for( int index=0; index<i; index++ ) {
			if( ((OurLetter)binHeapOfOurLettersByNumPossibleWords.peek()).getNumWordsPossibleWithThisAdditionalLetter() > 0 )
			targets.add( (OurLetter)binHeapOfOurLettersByNumPossibleWords.poll() );
			//System.out.println(targets.get(index).getAlphabet());
		}
		return targets;
	}
	
	public ArrayList<Word> getLegitWordsFromRemainingLetters(HashMap<Character, Integer> remainLetters, ArrayList<Word> possibleWords) {
		ArrayList<Word> goodWords = new ArrayList<Word>();
		
		for(Word w : possibleWords) {
			HashMap<Character, Integer> tempRemaining = new HashMap<Character, Integer>(remainLetters);
			
			for(Letter l : w.getLetters()) {
				tempRemaining.put(l.getAlphabet(), tempRemaining.get(l.getAlphabet()) - 1);
				if(tempRemaining.get(l.getAlphabet()) < 0){
					break;
				}
			}
			goodWords.add(w);
		}
		
		return goodWords;
	}
}
