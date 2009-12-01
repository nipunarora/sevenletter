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

	private static final Integer OFFSET_OF_A = Integer.valueOf('A');
	/**
	 * Constant string containing the 98 letters of a  US-English Scrabble set (no blanks)
	 */
	public static final String SCRABBLE_LETTERS_EN_US =
		"EEEEEEEEEEEEAAAAAAAAAIIIIIIIIIOOOOOOOONNNNNNRRRRRR" +
		"TTTTTTLLLLSSSSUUUUDDDDGGGBBCCMMPPFFHHVVWWYYKJXQZ";

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


	/*
	 * Shared precalculated information for all instances of our player
	 */
	static final LetterMine mine = new LetterMine("src/seven/g1/super-small-wordlist.txt");
	static final ArrayList<Word> wordlist = new ArrayList<Word>();
	static final ArrayList<Word> sevenletterlist = new ArrayList<Word>();
	static final long startscores[];

	static {
		BasicConfigurator.configure();
		Logger.getLogger(G1Player.class).setLevel(org.apache.log4j.Level.DEBUG);
		mine.buildIndex();
		mine.aPriori(0.000001);
		initDict();
		startscores = new long[wordlist.size()];
		Word tmp = new Word(SCRABBLE_LETTERS_EN_US);
		int[] startbag = tmp.countKeep;
		for (int i = 0; i < wordlist.size(); i++) {
			startscores[i] = wordlist.get(i).drawPossibilities(startbag);
		}
	}

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
	private long[] wordscore = Arrays.copyOf(startscores, startscores.length);


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
			// initialize
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

       	char bidChar = bidLetter.getAlphabet();
    	if(openletters.isEmpty()){
    		String s= "ESAIRONLT";
    		if(s.contains(Character.toString(bidChar))) {
    			return (int)(bidLetter.getValue()*3)/2;
    		} else {
    			return (int)(bidLetter.getValue()*2)/3;
    		}
    	}

    	char[] c= new char[openletters.size()];
    	String[] terms = new String[openletters.size()];
    	for(int i=0; i<openletters.size();i++){
    		char letter = openletters.get(i).getAlphabet();
    		c[i]= letter;
    		terms[i] = Character.toString(letter);
    	}
    	String s = String.valueOf(c);
    	Word open = new Word(s);

    	LetterSet lset = (LetterSet) mine.getCachedItemSet(terms);
    	Collection<Word> currenttargets = getReachableSevenLetterWords(lset);
    	l.debug("From " + s + " we can reach " + currenttargets.size() + " seven-letter words");

    	String[] extended_terms = Arrays.copyOf(terms, terms.length + 1);
    	extended_terms[terms.length] = Character.toString(bidChar);
    	lset = (LetterSet)mine.getCachedItemSet(extended_terms);
    	Collection<Word> couldreach = getReachableSevenLetterWords(lset);
    	l.debug("Acquiring " + bidChar + " limits us to " + couldreach.size() + " seven-letter words");


    	// does this letter help us?
    	boolean matchfound = !couldreach.isEmpty();

    	double percentile= percentile(open,bidLetter.getAlphabet());
    	l.debug("current alphabet "+ bidLetter.getAlphabet()+ " percentile "+ percentile);

    	if(matchfound){  // there is a seven-letter we can reach
    		if(percentile>0.75)
    		return bidLetter.getValue()*2;
    		else if(percentile>0&&percentile<=0.75)
    			return bidLetter.getValue();
    		else if(percentile ==0)
    			return 0;
    	}
    	l.debug(bidChar + " is not useful to us. Checking if we can reach a 7 letter word");

    	if(!currenttargets.isEmpty()){
    		l.debug("Can still reach a 7-letter, bid 0 on "+bidChar+" and wait for a good letter");
    		return 0;
    	}
    	else{
    		int value = scoreIncrementIfAcquire(bidLetter);
    		l.debug("Cannot reach 7, bid "+value+" on "+bidChar);
    		return value;
    	}
    }

	private HashMap<Character,Double> calcProb(Word o){
		HashMap<Character,Double> prob= new HashMap<Character,Double>();
		for(int i=0; i<26;i++){
		char bidChar= (char)(i+65);
		Word open= new Word(o.getWord().concat(String.valueOf(bidChar)));
		int alpha=i;
		if(letterBag.count(bidChar)>0){
		for(Word current : sevenletterlist ) {
    		if (current.issubsetof(open)) {
    			Word diff = current.subtract(open);
    			// if we could use this letter, and won't also need more of it than exist...
    			if (0 < diff.countKeep[alpha] && diff.countKeep[alpha] <= letterBag.count(bidChar)) {
    				// then go ahead and bid
    				double tempProb= wordProbability(open, current);
    				prob.put(Character.valueOf(bidChar), tempProb);
    				l.trace("bidChar "+ String.valueOf(bidChar)+ " Probability" + tempProb);
    			}
    		}
    	}

		}
		}
		return prob;
	}

	private Double percentile(Word o, char bidChar){
		HashMap<Character,Double> prob= calcProb(o);
		if(prob.size()==0)
			return 0.00;
		Collection tempC= prob.values();
		Iterator it= tempC.iterator();
		int lesscounter=0;
		if(prob.containsKey(bidChar)){
			while( it.hasNext()){
			if(prob.get(Character.valueOf(bidChar))>(Double)it.next())
				lesscounter++;
			}
		}
		if(prob.size()>0){
		return (double)lesscounter/prob.size();
		}
		else
			return 0.00;
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

		set = (LetterSet) mine.getCachedItemSet(new String[] {Character.toString(c)});
		int bagarray[] = arrayFromMap(letterBag);
		int rackarray[] = arrayFromMap(letterRack);
		// update words that contain this letter
		l.debug("Updating counters for " + set.getSupport() + " words");
		int ctr = 0;
		for (int idx : set.getTransactions()) {
			if (reachable[idx]) {
				ctr++;
				wordscore[idx] = wordlist.get(idx).drawPossibilities(bagarray, rackarray);
			}
		}
		l.debug("Updated counters for " + ctr + " words");

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
	private double drawProbability(char c) {
		double letterCount = letterBag.count(c);
		double bagSize = letterBag.countSum();
		return letterCount/bagSize;
	}
	/**
	 * Returns the bid *probability of the word being formed from the current rack
	 * @param s
	 * @return
	 */
	private double wordProbability(Word openLetters, Word sevenLWord){

		double probability=50;

		Word diff= sevenLWord.subtract(openLetters);
		l.trace("sevenLword: " +sevenLWord.getWord()+ " openLetters: "+ openLetters.getWord());
		for(int i=0;i<26;i++){
			if(diff.countKeep[i]>0){
				char c= (char)(i+65);
				//l.debug("draw probability of character " + String.valueOf(c) + "is "+ drawProbability(c));
				probability = probability * drawProbability(c);
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

    private static void initDict()
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


    private Collection<Word> getReachableSevenLetterWords(LetterSet lset) {
    	ArrayList<Word> found = new ArrayList<Word>();
    	if (null != lset) {
    		for (int wordID : lset.getTransactions()) {
    			if (reachable[wordID]) {
    				Word w = wordlist.get(wordID);
    				if (7 == w.length) {
    					found.add(w);
    				}
    			}
    		}
    	}
    	return found;
    }

    /**
     *
     * @param l letter that we are considering
     * @return how much our score would be incremented if we got that letter
     */

    private int scoreIncrementIfAcquire(Letter l){
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

    private static int[] arrayFromMap(CountMap<Character> m) {
    	int[] a = new int[26]; // values initialized to 0
    	for (Map.Entry<Character,Integer> e : m.entrySet()) {
    		int idx = Integer.valueOf(e.getKey()) - OFFSET_OF_A;
    		a[idx] = e.getValue();
    	}
    	return a;
    }

}
