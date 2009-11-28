package seven.g1;

import java.io.File;
import java.io.*;
import java.io.FileReader;

import seven.ui.CSVReader;

/**
 * A class to filter out impossible scrabble words
 * @author Nipun Arora
 *
 */
public class ScrabbleFilter {

	public static void main(String args[]){

		Word Scrabble= new Word(G1Player.SCRABBLE_LETTERS_EN_US);
		int counter =0;
		 try{

			 	File file = new File("FilteredWords.txt");
			 	BufferedWriter output = new BufferedWriter(new FileWriter(file));
	            CSVReader csvreader = new CSVReader(new FileReader("sowpods.txt"));
	            String[] nextLine;
	            csvreader.readNext(); // Waste the first line
	            while((nextLine = csvreader.readNext()) != null)
	            {
	                String word = nextLine[1];
	                Word tempword= new Word(word);
	                if(Scrabble.issubsetof(tempword)&&word.length()<=7){
	                	counter++;
	                	output.write(counter+ ", " + nextLine[0]+ ", " + nextLine[1]);
	                	output.newLine();
	                }
	            }
	            output.close();
	            System.out.println("The counter is: "+ counter);
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	            System.out.println("\n Could not load dictionary!");
	        }
	}
}
