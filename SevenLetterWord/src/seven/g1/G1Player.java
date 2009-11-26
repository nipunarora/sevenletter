package seven.g1;

import java.io.FileReader;
import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import seven.g1.datamining.DataMine;
import seven.g1.datamining.LetterMine;
import seven.ui.CSVReader;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;
import seven.ui.Scrabble;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Satyajeet
 */
public class G1Player implements Player{

	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		Logger.getLogger(G1Player.class).setLevel(org.apache.log4j.Level.TRACE);
	}

	LetterMine mine = new LetterMine("src/seven/g1/super-small-wordlist.txt");
	DataMine.ItemSet[] allSets;


	ArrayList<Letter> openletters= new ArrayList<Letter>();


	ArrayList<Word> wordlist=new ArrayList<Word>();
	ArrayList<Word> sevenletterlist= new ArrayList<Word>();

	SecretState refstate;
	Boolean first =true;
	ArrayList<PlayerBids> RefList= new ArrayList<PlayerBids>();

	int player_id = -1;
	int current_auction = 0;
	int total_auctions = 0;

	private Logger l = Logger.getLogger(this.getClass());

    public G1Player() {
		super();
		mine.buildIndex();
		allSets = mine.aPriori(0.000001);
		initDict();
	}

	public void Register() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int totalRounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {

		//initialize dictionary in first move
		if(first){
			player_id = PlayerID;
			current_auction = 0;
			openletters.clear();
			openletters.addAll(secretstate.getSecretLetters());
			total_auctions = (7 - openletters.size()) * PlayerList.size();
			l.debug("Seven letter size: " + sevenletterlist.size());
			l.info("Total bidding rounds: " +  total_auctions);
			first = false;
		} else {
			checkBidSuccess(PlayerBidList);
		}


		RefList=PlayerBidList;
    	refstate=secretstate;
    	++current_auction;
    	l.debug("On bidding round " + current_auction + " of " + total_auctions);


    	if(openletters.size()<3){
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

    	if(possCountKeep[alpha]>0)
    		return bidLetter.getValue();
        return 0;
    }

	/**
	 * @param bidList
	 */
	private void checkBidSuccess(ArrayList<PlayerBids> bidList) {
		if(!bidList.isEmpty()){
			PlayerBids LastBid= bidList.get(bidList.size()-1);
			if(player_id == LastBid.getWinnerID()) {
				Letter lastletter = LastBid.getTargetLetter();
				l.debug("We acquired letter " + lastletter.getAlphabet()
						+ " for " + LastBid.getWinAmmount());
				openletters.add(LastBid.getTargetLetter());
			}
    	}
	}

    public String returnWord() {
    	ArrayList<String> possiblities= new ArrayList<String>();

    	l.debug("checking bid for final round: " + RefList.size());
    	checkBidSuccess(RefList);

    	String s= new String();
    	char[] c= new char[openletters.size()];
    	for(int i=0; i<openletters.size();i++){
    		 c[i]= openletters.get(i).getAlphabet();
    	}

    	s= String.valueOf(c);
    	Word open= new Word(s);
    	l.info("Open Letters are: [" + s + "]");


    	for (Word candidate : wordlist) {
    		if(open.issubsetof(candidate)){
    			possiblities.add(candidate.getWord());
    		}
    	}

    	int bestscore = 0;
    	String bestword = "";
    	for (String possible : possiblities) {
    		int score = Scrabble.getWordScore(possible);
    		if (score > bestscore) {
    			bestscore = score;
    			bestword = possible;
    		}
    	}


    	l.info(bestword);
        // tell "bid" that we are about to begin a new round
    	first = true;
    	return bestword;
    }

    public  void initDict()
    {
        try{
            CSVReader csvreader = new CSVReader(new FileReader("FilteredWords.txt"));
            l.trace("reached 1");
            String[] nextLine;

            while((nextLine = csvreader.readNext()) != null)
            {

                String word = nextLine[2].trim();
                l.trace(word);
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
            l.fatal("Could not load dictionary!",e);
        }
    }





}
