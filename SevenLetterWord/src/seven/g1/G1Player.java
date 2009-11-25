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


	ArrayList<Letter> openletters= new ArrayList<Letter>();
	
	
	ArrayList<Word> wordlist=new ArrayList<Word>();
	ArrayList<Word> sevenletterlist= new ArrayList<Word>();
	SecretState refstate;
	Boolean first =true;
	Boolean initialize= true;
	ArrayList<PlayerBids> RefList= new ArrayList<PlayerBids>();
    public void Register() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int totalRounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
    	
		if(initialize){
			initDict();
		}
		
		//initialize dictionary in first move
		if(first){
			for(int i=0;i<secretstate.getSecretLetters().size();i++){
	    		openletters.add(secretstate.getSecretLetters().get(i));
	    	}
			System.out.println("Seven letter size: " + sevenletterlist.size());
		}
		
		
		RefList=PlayerBidList;
    	refstate=secretstate;
    	
    	if(!first){
    	PlayerBids LastBid= RefList.get(RefList.size()-1);
		if(LastBid.getWonBy().equals("seven.g1.G1Player")){
		openletters.add(LastBid.getTargetLetter());
		}
    	}
    	
    	if(openletters.size()<3){
    		first =false;
    		initialize=false;
    		return 0;
    	}
    	
    	String s= new String();
    	char[] c= new char[openletters.size()];
    	for(int i=0; i<openletters.size();i++){
    		 c[i]= openletters.get(i).getAlphabet();
    	}
    	
    	int[] possCountKeep= new int[26];
    	s= String.valueOf(c);
    	Word open= new Word(s);
    	//greater than 4 logic
    	for(int i=0;i<sevenletterlist.size();i++){
    		Word current= sevenletterlist.get(i);
    		if(current.issubsetof(open)){
    			for(int j=0;j<26;j++){
    				if(current.countKeep[j]>open.countKeep[j]){
    					possCountKeep[j]++;
    				}
    			}
    		}
    	}
    	
    	int alpha= Integer.valueOf(bidLetter.getAlphabet())- Integer.valueOf('A');
    	
    	initialize=false;
    	first=false;
    	if(possCountKeep[alpha]>0)
    		return bidLetter.getValue();
        return 0;
    }

    public String returnWord() {
    	ArrayList<String> possiblities= new ArrayList<String>();
    	
    	
    	PlayerBids LastBid= RefList.get(RefList.size()-1);
		if(LastBid.getWonBy().equals("seven.g1.G1Player")){
		openletters.add(LastBid.getTargetLetter());
		}
		
    	String s= new String();
    	char[] c= new char[openletters.size()];
    	for(int i=0; i<openletters.size();i++){
    		 c[i]= openletters.get(i).getAlphabet();
    	}
    	
    	s= String.valueOf(c);
    	Word open= new Word(s);
    	System.out.println("Open Letters are:" + s);
    	
    	
    	for (int i=0;i<wordlist.size();i++){
    		if(open.issubsetof(wordlist.get(i))){
    			possiblities.add(wordlist.get(i).getWord());
    		}
    	}
    	int max=0;
    	int maxindex=0;
    	for(int i=0;i<possiblities.size();i++){
    		if(max<possiblities.get(i).length()){
    			max=possiblities.get(i).length();
    			maxindex=i;
    		}
    	}
    	
        //throw new UnsupportedOperationException("Not supported yet.");
    	if(possiblities.size()>0){
    		System.out.println(possiblities.get(maxindex));
    		openletters.clear();
    		RefList.clear();
    		first=true;
    		return possiblities.get(maxindex).trim();
    	}else
    		return " ";
    }

    public  void initDict()
    {
        try{
            CSVReader csvreader = new CSVReader(new FileReader("FilteredWords.txt"));
            System.out.println("reached 1");
            String[] nextLine;
            csvreader.readNext(); // Waste the first line
            while((nextLine = csvreader.readNext()) != null)
            {
            	
                String word = nextLine[2];
                System.out.println(word);
                Word tempword= new Word(word);
               // System.out.println("reached 2");
                if(tempword.length==7){
                	sevenletterlist.add(tempword);
                }
                wordlist.add(tempword);
               // System.out.println("addedword "+ word);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("\n Could not load dictionary!");
        }

    }


	
   

}
