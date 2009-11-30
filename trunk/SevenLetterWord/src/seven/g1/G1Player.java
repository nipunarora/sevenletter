package seven.g1;

import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import seven.g1.datamining.LetterMine;
import seven.g1.datamining.LetterMine.LetterSet;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

/**
 *
 * @author Nipun, Ben and Manuel
 */
public class G1Player implements Player{

	/**
	 * 
	 * @author Manuel
	 * Class to keep some information about other players (id, letters in possesion, and current score)
	 * canGet7LetterWord
	 */
	private class TrackedPlayer{
		int playerId;		
		CountMap<Character> letterRack;
		ArrayList<Letter> openLetters;
		int score;
		
		public TrackedPlayer(int id){
			playerId = id;
			score = 100;
			letterRack = new CountMap<Character>();
			openLetters = new ArrayList<Letter>();
		}
		
	}

	/**
	 * Constant string containing the 98 letters of a  US-English Scrabble set (no blanks)
	 */
	public static final String SCRABBLE_LETTERS_EN_US =
		"EEEEEEEEEEEEAAAAAAAAAIIIIIIIIIOOOOOOOONNNNNNRRRRRR" +
		"TTTTTTLLLLSSSSUUUUDDDDGGGBBCCMMPPFFHHVVWWYYKJXQZ";

	/*
	 * Shared precalculated information for all instances of our player
	 */
	static final LetterMine mine = new LetterMine("src/seven/g1/super-small-wordlist.txt");
	static final ArrayList<Word> wordlist = new ArrayList<Word>();
	static final ArrayList<Word> sevenletterlist = new ArrayList<Word>();
/*
	static {
		BasicConfigurator.configure();
		Logger.getLogger(G1Player.class).setLevel(org.apache.log4j.Level.TRACE);
		mine.buildIndex();
		mine.aPriori(0.000001);
		initDict();
	}*/

	/*
	 * Fields specific to individual players:
	 */

	CountMap<Character> letterBag;
	CountMap<Character> letterRack;

	ArrayList<Letter> openletters= new ArrayList<Letter>();

	SecretState refstate;
	Boolean first = true;
	ArrayList<PlayerBids> RefList= new ArrayList<PlayerBids>();

	int player_id = -1;
	int current_auction = 0;
	int total_auctions = 0;
	int score;
	ArrayList<TrackedPlayer> otherPlayers;

	private Logger l = Logger.getLogger(this.getClass());
	private boolean[] reachable = new boolean[wordlist.size()];

    /**
     * More or less empty constructor--all of our initialization is now done
     * in initialization statements or the static initializer block.
     */
    public G1Player() {
		super();
		l.trace("reachable has length " + reachable.length);
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
			score = secretstate.getScore();
			if(otherPlayers == null){
				otherPlayers = new ArrayList<TrackedPlayer>();
				for(int i = 0; i < PlayerList.size(); i++){
					otherPlayers.add(new TrackedPlayer(i));
				}
			}
			else{
				//If some players cannot continue the game because their score is 0
				//does PlayerList still include them? If not we have to do more...
				//It would be nice to know the results of a round :)
				for(int i = 0; i < PlayerList.size(); i++){
					TrackedPlayer adversary = otherPlayers.get(i); 
					adversary.letterRack = new CountMap<Character>();
					adversary.openLetters = new ArrayList<Letter>();
				}
			}
			for (Letter l : openletters) {
				won(l, 0);
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

    	char[] c= new char[openletters.size()];
    	for(int i=0; i<openletters.size();i++){
    		 c[i]= openletters.get(i).getAlphabet();
    	}

//    	int[] possCountKeep= new int[26];
    	String s = String.valueOf(c);
    	Word open = new Word(s);
    	//greater than 4 logic
    	boolean matchfound = false;
    	char bidChar = bidLetter.getAlphabet();
    	int alpha= Integer.valueOf(bidChar) - Integer.valueOf('A');
    	for(Word current : sevenletterlist ) {
    		if (current.issubsetof(open)) {
    			Word diff = current.subtract(open);
    			// if we could use this letter, and won't also need more of it than exist...
    			if (0 < diff.countKeep[alpha] && diff.countKeep[alpha] <= letterBag.count(bidChar)) {
    				// then go ahead and bid
    				l.debug("Bidding on " + bidChar + " for word " + current.word);
    				matchfound = true;
        			break;
    			}
    		}

    	}

    	if(matchfound)
    		return bidLetter.getValue();
    	if(canReach7LetterWord(openletters))
    		return 0;
    	else
    		return scoreIncrementIfAcquire(bidLetter);
    }

	private void won(Letter letterWon, int amount) {
		Character c = letterWon.getAlphabet();
		assert(0 <= letterBag.decrement(c));
		assert(0 <= letterRack.increment(c));
		score -= amount;
	}

	private void lost(PlayerBids bid) {
		Letter letterLost = bid.getTargetLetter();
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
		
		//Update information about the player who won the bid
		TrackedPlayer adversary = otherPlayers.get(bid.getWinnerID());
		adversary.score -= bid.getWinAmmount();
		adversary.letterRack.increment(c);
		adversary.openLetters.add(letterLost);
	}

	/**
	 * Given the current state of the bag, what is the probability that the next draw
	 * will be this character?
	 * @param c the letter to be drawn
	 * @return the probability of drawing this letter from the bag.
	 */
	public double drawProbability(char c) {
		double letterCount = letterBag.count(c);
		double bagSize = letterBag.countSum();
		return letterCount/bagSize;
	}
	/**
	 * Returns the probability of the word being formed from the current rack
	 * @param s
	 * @return
	 */
	public double wordProbability(Word openLetters, Word sevenLWord){
		
		double probability=50;
		
		Word diff= sevenLWord.subtract(openLetters);
		
		for(int i=0;i<26;i++){
			if(diff.countKeep[i]>0){
				char c= (char)(i+65);
				probability *= drawProbability(c);
			}
		}
		return probability;
		
	}

	/**
	 * @param bidList
	 */
	private void checkBidSuccess(ArrayList<PlayerBids> bidList) {
		if(!bidList.isEmpty()){
			PlayerBids LastBid= bidList.get(bidList.size()-1);
			Letter lastletter = LastBid.getTargetLetter();
			int amountBid = LastBid.getWinAmmount();
			if(player_id == LastBid.getWinnerID()) {
				won(lastletter, amountBid);
				l.debug("We acquired letter " + lastletter.getAlphabet()
						+ " for " + amountBid);
				openletters.add(LastBid.getTargetLetter());
			} else {
				lost(LastBid);
			}
    	}
	}

    public String returnWord() {
    	l.debug("checking bid for final round: " + RefList.size());
    	checkBidSuccess(RefList);

    	char[] c= new char[openletters.size()];
    	for(int i=0; i<openletters.size();i++){
    		 c[i]= openletters.get(i).getAlphabet();
    	}

    	String s = String.valueOf(c);
    	Word open= new Word(s);
    	l.info("Open Letters are: [" + s + "]");

    	int bestscore = 0;
    	String bestword = "";
    	for (Word candidate : wordlist) {
    		if(open.issubsetof(candidate)){
    			if (candidate.score > bestscore) {
    				bestscore = candidate.score;
    				bestword = candidate.getWord();
    				l.trace("New best word: " + bestword + " (" + bestscore + ")");
    			}
    		}
    	}

    	l.info(bestword);
    	score += bestscore;
        // tell "bid" that we are about to begin a new round
    	first = true;
    	return bestword;
    }

    public static void initDict()
    {
    	Logger l = Logger.getLogger(G1Player.class);
        try{
        	Iterator<String> words = mine.getWordIterator();
        	while (words.hasNext()) {
                String word = words.next();

                Word tempword= new Word(word);
                l.trace(word + ": " + tempword.score);
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

    public Set<Word> getViableWords(LetterSet lset) {
    	HashSet<Word> viable = new HashSet<Word>();
    	for (int wordID : lset.getTransactions()) {
    		if (reachable[wordID]) {
    			viable.add(wordlist.get(wordID));
    		}
    	}
    	return viable;
    }
    
    /**
     * @return true if we can get a 7 letter word with our letters and some of the ones left in letterBag
     */
    
    public boolean canReach7LetterWord(ArrayList<Letter> letterSet){
    	char[] c= new char[letterSet.size()];
    	for(int i=0; i<letterSet.size();i++){
    		 c[i]= letterSet.get(i).getAlphabet();
    	}

    	String s = String.valueOf(c);
    	Word open = new Word(s);
    	boolean matchfound;

    	for(Word current : sevenletterlist ) {
    			matchfound = true;
    			Word diff = current.subtract(open);
    			for (int i = 0; i < 26; i++) {
    				int code = Integer.valueOf('A') + i;
    				char letter = (char) code;
    				if(letterBag.count(letter) < diff.countKeep[i])
    					matchfound = false;
    			}
    			if(matchfound)
    				return true;
    	}
    	return false;
    }
    
    /**
     * 
     * @param letterSet
     * @return Return number of 7 letter words that can be formed with the letters in letterSet
     */
    
    public int reachable7LetterWords(ArrayList<Letter> letterSet){
    	char[] c= new char[letterSet.size()];
    	for(int i=0; i<letterSet.size();i++){
    		 c[i]= letterSet.get(i).getAlphabet();
    	}

    	String s = String.valueOf(c);
    	Word open = new Word(s);
    	boolean matchfound;
    	int count = 0;

    	for(Word current : sevenletterlist ) {
    			matchfound = true;
    			Word diff = current.subtract(open);
    			for (int i = 0; i < 26; i++) {
    				int code = Integer.valueOf('A') + i;
    				char letter = (char) code;
    				if(letterBag.count(letter) < diff.countKeep[i])
    					matchfound = false;
    			}
    			if(matchfound)
    				count++;
    	}
    	return count;
    }
    
    /**
     * 
     * @param l letter that we are considering
     * @return how much our score would be incremented if we got that letter
     */
    
    public int scoreIncrementIfAcquire(Letter l){
    	char[] c= new char[openletters.size()];
    	char[] c2= new char[openletters.size()+1];
    	int i;
    	for(i=0; i<openletters.size();i++){
    		 c[i]= openletters.get(i).getAlphabet();
    		 c2[i]= openletters.get(i).getAlphabet();
    	}
    	c2[i] = l.getAlphabet();

    	String s = String.valueOf(c);
    	Word open= new Word(s);
    	s = String.valueOf(c2);
    	Word open2 = new Word(s);

    	int bestscore1 = 0;
    	int bestscore2 = 0;
    	for (Word candidate : wordlist) {
    		if(open.issubsetof(candidate)){
    			if (candidate.score > bestscore1) {
    				bestscore1 = candidate.score;
    			}
    			if (candidate.score > bestscore2){
    				bestscore2 = candidate.score;
    			}
    		}
    		else if(open2.issubsetof(candidate)){
    			if (candidate.score > bestscore2){
    				bestscore2 = candidate.score;
    			}
    		}
    	}
    	
    	return bestscore2 - bestscore1;
    }

}
