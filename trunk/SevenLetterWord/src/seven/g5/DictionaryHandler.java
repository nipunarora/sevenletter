package seven.g5;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import seven.g5.data.Word;
import seven.ui.CSVReader;
import seven.ui.Letter;

public class DictionaryHandler {

	private Hashtable<String, Boolean> dictionary;
	
	public DictionaryHandler() {
		this.dictionary = new Hashtable<String, Boolean>();
		try{
			CSVReader csvreader = new CSVReader(new FileReader("src/seven/g5/data/FilteredWords.txt"));
			String[] nextLine;
			//csvreader.readNext(); // Waste the first line
			while((nextLine = csvreader.readNext()) != null)
			{
				String word = nextLine[0];
				dictionary.put(word, Boolean.TRUE);
			}

		} catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("\n Could not load dictionary!");
		}
	}
	
	//this method needs to take list of letters and return a list. it will be sorted in last line by score
	public ArrayList<Word> getPossibleWords(ArrayList<Letter> letters) {
		ArrayList<Word> possibleWords = null;
		
		Collections.sort(possibleWords);
		return possibleWords;
	}
	
	public Word getBestWord(ArrayList<Letter> rack) {
		ArrayList<Word> possibleWords = getPossibleWords(rack);
		
		return possibleWords.get(possibleWords.size() - 1);
	}
	
	public Word getBestWordWithAddition(ArrayList<Letter> rack, Letter newLetter) {
		ArrayList<Letter> tempRack = new ArrayList<Letter>(rack);
		tempRack.add(newLetter);
		
		return getBestWord(tempRack);
	}
}
