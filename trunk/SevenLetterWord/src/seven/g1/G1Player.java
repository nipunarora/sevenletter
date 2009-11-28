package seven.g1;

import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import seven.g1.datamining.DataMine;
import seven.g1.datamining.LetterMine;
import seven.g1.datamining.LetterMine.LetterSet;
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

	public static final String SCRABBLE_LETTERS_EN_US =
		"EEEEEEEEEEEEAAAAAAAAAIIIIIIIIIOOOOOOOONNNNNNRRRRRR" +
		"TTTTTTLLLLSSSSUUUUDDDDGGGBBCCMMPPFFHHVVWWYYKJXQZ";

	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		Logger.getLogger(G1Player.class).setLevel(org.apache.log4j.Level.TRACE);
	}

	LetterMine mine = new LetterMine("src/seven/g1/super-small-wordlist.txt");
	DataMine.ItemSet[] allSets;

	CountMap<Character> letterBag;
	CountMap<Character> letterRack;

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
	private boolean[] reachable;

    public G1Player() {
		super();
		mine.buildIndex();
		allSets = mine.aPriori(0.000001);
		initDict();
    	reachable = new boolean[wordlist.size()];
	}

	private CountMap<Character> newBag() {
		CountMap<Character> bag = new CountMap<Character>();
		for (int i = 0; i < SCRABBLE_LETTERS_EN_US.length(); i++) {
			char c = SCRABBLE_LETTERS_EN_US.charAt(i);
			bag.increment(c);
		}
		return bag;
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
			letterBag = newBag();
			letterRack = new CountMap<Character>();
			openletters.addAll(secretstate.getSecretLetters());
			total_auctions = (7 - openletters.size()) * PlayerList.size();
			for (Letter l : openletters) {
				won(l);
			}
        	Arrays.fill(reachable, true);
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

	private void won(Letter letterWon) {
		Character c = letterWon.getAlphabet();
		assert(0 <= letterBag.decrement(c));
		assert(0 <= letterRack.increment(c));
	}

	private void lost(Letter letterLost) {
		Character c = letterLost.getAlphabet();
		int prevCount = letterBag.count(c) + letterRack.count(c);
		letterBag.decrement(c);
		String[] terms = new String[prevCount];
		for (int i = 0; i < prevCount; i++) {
			terms[i] = c.toString();
		}
		LetterSet set = (LetterSet) mine.getCachedItemSet(terms);
		if (null != set) {
			int wordList[] = set.getTransactions();
			l.debug(
					String.format("Marking %d words as unreachable (%d x '%c')",
						new Object[]{ wordList.length, prevCount , c})
			);
			for (int wordID : wordList) {
				this.reachable[wordID] = false;
			}
		}
	}

	/**
	 * @param bidList
	 */
	private void checkBidSuccess(ArrayList<PlayerBids> bidList) {
		if(!bidList.isEmpty()){
			PlayerBids LastBid= bidList.get(bidList.size()-1);
			Letter lastletter = LastBid.getTargetLetter();
			if(player_id == LastBid.getWinnerID()) {
				won(lastletter);
				l.debug("We acquired letter " + lastletter.getAlphabet()
						+ " for " + LastBid.getWinAmmount());
				openletters.add(LastBid.getTargetLetter());
			} else {
				lost(lastletter);
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
        	Iterator<String> words = mine.getWordIterator();
        	while (words.hasNext()) {
                String word = words.next();
                l.trace(word);
                Word tempword= new Word(word);
               // System.out.println("reached 2");
                if(tempword.length==7){
                	sevenletterlist.add(tempword);
                }
                wordlist.add(tempword);
            }

        }
        catch(Exception e)
        {
            l.fatal("Could not load dictionary!",e);
        }
    }





}
