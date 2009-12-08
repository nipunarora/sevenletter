package seven.g5;

import java.util.ArrayList;
import java.util.List;

import seven.g5.data.Word;
import seven.g5.gameHolders.*;
import seven.ui.*;

public class Utilities {
	//effective java and headfirst java
	public static double getProbabilityOfLetter(Letter letter1, GameInfo gi)	
	{
		double p = 0;
		////System.out.println(gi.getNoOfTurnsRemaining());
		int turns = gi.getNoOfTurnsRemaining();
		for(int i=0;i<=turns;i++) {
			////System.out.println((double)gi.getNumberLettersRemaining().get(letter1.getAlphabet()));
			p+= (double)(gi.getNumberLettersRemaining().get(letter1.getAlphabet()))/(double)(98-i);
		}
		return p;
	}
	
	public static void printLetters(List<Letter> hand) {
		for(Letter l : hand) {
			System.out.print(l.getAlphabet() + ", ");
		}
		//System.out.println();
	}
	
	public static ArrayList<Word> calculateProbabilitiesOfWord(ArrayList<Word> allFutureWords,
			GameInfo gi) {
		double prob;
		for( Word w: allFutureWords ) {
			prob = 1;
			String s = w.toString();
			//chance of each of the letters coming up in one turn
			for ( int c = 0; c < s.length(); c++ ) {
				prob *= ((double)gi.getNumberLettersRemaining().get(s.charAt(c))) / (double)(gi.getTotalLettersRemaining());
			}
			//chance of all letters coming up by end of game
			prob *= gi.getNoOfTurnsRemaining();
			w.setProbability(prob);
		}
		return allFutureWords;
	}

	public static ArrayList<Word> collectOnlySevenLetters(ArrayList<Word> allFutureWords) {
		for( int i=0; i<allFutureWords.size(); i++ ) {
			if( allFutureWords.size() < 7 ) allFutureWords.remove(i);
		}
		return allFutureWords;
	}
	
	public static int[] mimicLessThanSevenStrategy( PlayerInfo pi, GameInfo gi ) {
		int[] answer = { 0, 0 };
		Word currentBestWord = null;
		ArrayList<Word> currentEndWordList = pi.getDictionaryHandler().pastAnagram(pi.getRack());
		if( currentEndWordList != null )
			currentBestWord = pi.getDictionaryHandler().getBestWordOfList(currentEndWordList);
		ArrayList<Letter> tempHand = new ArrayList<Letter>(pi.getRack());
		Word futureBestWord = null;
		tempHand.add( gi.getCurrentBidLetter() );//new Letter ((char)('A'+c),ScrabbleParameters.getScore((char)('A'+c))));
		ArrayList<Word> endWordList = pi.getDictionaryHandler().pastAnagram(tempHand);
		if( endWordList != null )
			futureBestWord = pi.getDictionaryHandler().getBestWordOfList(endWordList);
		if( currentBestWord != null ) {
			if( futureBestWord != null ) {
				if( futureBestWord.getScore() > currentBestWord.getScore() ) {
					answer[0] = futureBestWord.getScore() - currentBestWord.getScore();
				}
			}
		}
		else if( futureBestWord != null ) 
			answer[0] = futureBestWord.getScore();

		return answer;
	}
}
