package seven.g1;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

import seven.ui.CSVReader;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Satyajeet
 */
public class G1Player implements Player{

	HashSet<Word> wordlist;
	
    public void Register() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList, int total_rounds,ArrayList<String> PlayerList, SecretState secretstate) {

        return 0;
    }

    public String returnWord() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return "";
    }

    public  void initDict()
    {
        try{
            CSVReader csvreader = new CSVReader(new FileReader("sowpods.txt"));
            String[] nextLine;
            csvreader.readNext(); // Waste the first line
            while((nextLine = csvreader.readNext()) != null)
            {
                String word = nextLine[1];
                Word tempword= new Word(word);
                wordlist.add(tempword);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("\n Could not load dictionary!");
        }

    }
   

}
