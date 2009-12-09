package seven.g1;

import java.util.*;

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
	 * Class to keep some information about other players (id, letters in possession, and current score)
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
	static int stupidCounter=0;
	String wordReturned= new String();
	static final History hist= new History();
	static final LetterMine mine = new LetterMine("src/seven/g1/super-small-wordlist.txt");
	static final ArrayList<Word> wordlist = new ArrayList<Word>();
	static final ArrayList<Word> sevenletterlist = new ArrayList<Word>();
	// initialized to correct size in static block, below
	static final boolean[] is_seven_letter;
	static final long base_probability_counters[];
	static final int TOTAL_WORDS;

	static {
		mine.buildIndex();
		mine.aPriori(0.000001);
		initDict();
		TOTAL_WORDS = wordlist.size();
		base_probability_counters = new long[TOTAL_WORDS];
		is_seven_letter = new boolean[TOTAL_WORDS];

		Word tmp = new Word(SCRABBLE_LETTERS_EN_US);
		int[] startbag = tmp.countKeep;
		for (int i = 0; i < TOTAL_WORDS; i++) {
			Word w = getWord(i);
			is_seven_letter[i] = (7 == w.length);
			base_probability_counters[i] = w.drawPossibilities(startbag);
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
	ArrayList<String> RefPlayerList= new ArrayList<String>();

	int player_id = -1;
	int current_auction = 0;
	int total_auctions = 0;
	int auctions_played = 0;
	int score;
	int cumulative_bid = 0;
	ArrayList<Integer> maxBids = new ArrayList<Integer>();
	double average_bid;
	double std_deviation;
	ArrayList<TrackedPlayer> otherPlayers;

	protected Logger l = Logger.getLogger(this.getClass());
	private boolean[] reachable = new boolean[TOTAL_WORDS];
	private long[] word_draw_possibilities = Arrays.copyOf(base_probability_counters, base_probability_counters.length);


    /**
     * More or less empty constructor--all of our initialization is now done
     * in initialization statements or the static initializer block.
     */
    public G1Player() {
		super();
		l.trace("reachable has length " + reachable.length);
	}

    /*
     * Static convenience methods
     */

	private static CountMap<Character> newBag() {
		CountMap<Character> bag = new CountMap<Character>();
		for (int i = 0; i < SCRABBLE_LETTERS_EN_US.length(); i++) {
			char c = SCRABBLE_LETTERS_EN_US.charAt(i);
			bag.increment(c);
		}
		return bag;
	}

	private static int[] arrayFromMap(CountMap<Character> m) {
		int[] a = new int[26]; // values initialized to 0
		for (Map.Entry<Character,Integer> e : m.entrySet()) {
			int idx = Integer.valueOf(e.getKey()) - OFFSET_OF_A;
			a[idx] = e.getValue();
		}
		return a;
	}

	/**
	 * @param terms
	 * @return
	 */
	private static LetterSet getLetterSet(String[] terms) {
		LetterSet lset = (LetterSet) mine.getCachedItemSet(terms);
		return lset;
	}

	private static LetterSet getLetterSet(char c) {
		return getLetterSet(new String[] {Character.toString(c)});
	}

	private static Word getWord(int word_id) {
		return wordlist.get(word_id);
	}

	private static int getCharIndex(char c) {
		return Integer.valueOf(c) - OFFSET_OF_A;
	}

	/* Interface required methods
	 * (non-Javadoc)
	 * @see seven.ui.Player#Register()
	 */

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
			cumulative_bid = 0;
			std_deviation = 5;
			stupidCounter = 0;


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
        	Arrays.fill(reachable, true);
			if (!openletters.isEmpty()) {
				String[] terms = new String[openletters.size()];
				for ( int i = 0; i < openletters.size(); i++) {
					Letter l = openletters.get(i);
					won(l, 0);
					terms[i] = l.getAlphabet().toString();
				}
				LetterSet reachableSet = getLetterSet(terms);
				int updateCount = updateDrawPossibilities(letterBag, letterRack, reachableSet);
				l.debug("Currently reachable words using all letters: " + updateCount);
			}


			l.debug("Seven letter size: " + sevenletterlist.size());
			l.info("Total bidding rounds: " +  total_auctions);
			first = false;
		} else {
			checkBidSuccess(PlayerBidList);
		}

		RefPlayerList= PlayerList;
		RefList=PlayerBidList;
    	refstate=secretstate;
    	++current_auction;
    	l.debug("Player " + player_id +" on bidding round " + current_auction + " of " + total_auctions);
    	int letters_needed = (7 - openletters.size());
       	char bidChar = bidLetter.getAlphabet();
    	/*if(openletters.isEmpty()){
    		String s= "ESAIRONLT";
    		if(s.contains(Character.toString(bidChar))) {
    			return (int)(bidLetter.getValue()*3)/2;
    		} else {
    			return (int)(bidLetter.getValue()*2)/3;
    		}
    	}*/

    	char[] c= new char[openletters.size()];
    	String[] terms = new String[openletters.size()];
    	for(int i=0; i<openletters.size();i++){
    		char letter = openletters.get(i).getAlphabet();
    		c[i]= letter;
    		terms[i] = Character.toString(letter);
    	}
    	String s = String.valueOf(c);
    	Word open = new Word(s);

    	LetterSet lset = getLetterSet(terms);
    	//Collection<Word> currenttargets = getReachableSevenLetterWords(lset);
    	Collection<Integer> currenttargets;
    	Collection<Integer> couldreachiflost;
    	int[] wouldlose = hypotheticallosses(bidChar);
    	// irritating special case for initial-letter hunting:
    	if (0 < terms.length) {
    		 currenttargets = getTargetIndices(lset, new int[0]);
    		 couldreachiflost = getTargetIndices(lset, wouldlose);
    	} else {
    		currenttargets = emptyRackTargets(null);
    		couldreachiflost = emptyRackTargets(wouldlose);
    	}
    	l.debug("From " + s + " we can reach " + currenttargets.size() + " seven-letter words");
    	l.debug("If we lose on " + bidChar + " we can reach " + couldreachiflost.size() + " seven-letter words");

    	String[] extended_terms = Arrays.copyOf(terms, terms.length + 1);
    	extended_terms[terms.length] = Character.toString(bidChar);
    	lset = getLetterSet(extended_terms);
    	Collection<Integer> couldreach = getTargetIndices(lset, new int[0]);
    	l.debug("Acquiring " + bidChar + " limits us to " + couldreach.size() + " seven-letter words");
    	long currsum = sumTargetPossibilities(currenttargets);
    	long potentialsum = sumTargetPossibilities(couldreach);
    	//long iflost = sumTargetPossibilities(couldreachiflost);
    	long penalty = lossPenalty(currenttargets,bidChar);
    	double kept_fraction = (double) potentialsum / (double) currsum;
    	l.debug("Current p-sum: " + currsum + "; kept fraction " + kept_fraction);
    	double lost_fraction = (double) penalty/(double) currsum;
    	l.debug("Fraction forgone if lost: " + lost_fraction);

    	double percentile = percentile(open,bidLetter.getAlphabet());
    	l.debug("current alphabet "+ bidLetter.getAlphabet()+ " percentile "+ percentile);

    	if(!couldreach.isEmpty()){  // there is a seven-letter word we can reach with this letter
			updateBiddingStatistics(PlayerBidList);

			int bid = makeBid(letters_needed, kept_fraction, lost_fraction, total_auctions - current_auction + 1, bidLetter);
    		return bid;
    	} else if (currenttargets.isEmpty()) { // there is no reachable 7-letter
    		int value = scoreIncrementIfAcquire(bidLetter);
    		l.debug("Cannot reach 7, bid "+value+" on "+bidChar);
    		return value;
    	} else {  		// there is a reachable 7-letter, but not using this letter
    		l.debug("Can still reach a 7-letter, bid 0 on "+bidChar+" and wait for a good letter");
    		return 0;
    	}
    }

	protected void updateBiddingStatistics(ArrayList<PlayerBids> bidList){
		if(!bidList.isEmpty()){
			PlayerBids LastBid= bidList.get(bidList.size()-1);

			int max = 0;
			for(int i : LastBid.getBidvalues()){
				if(i > max)
					max = i;
			}
			++auctions_played;
			maxBids.add(max);
			average_bid = average();
			std_deviation = stddev();
			l.debug("average: "+average_bid+" std_dev: "+std_deviation);
		}
	}

	protected int makeBid(int letters_needed, double kept_fraction, double lost_fraction, int auctions_left, Letter bidLetter) {
		double multiplier=1;
		if(stupidCounter>=3){
			//stupidCounter=0;
			multiplier=2;
		}
		return (int) ( multiplier*(50 * lost_fraction) * (1 + 1/(double) auctions_left));
	}

	double average(){
		double sum = 0;
		for(int i : maxBids){
			sum += i;
		}
		return sum / auctions_played;
	}

	double stddev(){
		double avg = average();
		double diff = 0;
		for(int i : maxBids){
			diff += Math.pow((avg-i), 2);
		}
		return Math.sqrt(diff / auctions_played);
	}

	/*
	 * Bookkeeping methods for bid tracking
	 */
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

			char c = LastBid.getTargetLetter().getAlphabet();
			LetterSet set = getLetterSet(c);
			// update words that contain this letter
			l.debug("Updating counters for " + set.getSupport() + " of all words");
			int changed = 0;
			changed = updateDrawPossibilities(letterBag, letterRack, set);
			l.debug("Updated counters for " +changed +  " words");

    	}
	}

	/**
	 * @param bagarray
	 * @param rackarray
	 * @param updated_words
	 * @return number of words with updated possibility count
	 */
	private int updateDrawPossibilities(int[] bagarray, int[] rackarray, LetterSet set) {
		int changed = 0;
		if (null == set) return 0;
		for (int idx : set.getTransactions()) {
			if (reachable[idx]) {
				long newscore =  getWord(idx).drawPossibilities(bagarray, rackarray);
				if (newscore != word_draw_possibilities[idx]) {
					changed++;
					word_draw_possibilities[idx] = newscore;
				}
			}
		}
		return changed;
	}


	private int updateDrawPossibilities(CountMap<Character> currentBag, CountMap<Character> currentRack, LetterSet reachableSet) {
		int [] bagarray = arrayFromMap(currentBag);
		int[] rackarray = arrayFromMap(currentRack);
		return updateDrawPossibilities(bagarray, rackarray, reachableSet);
	}

	private void won(Letter letterWon, int amount) {
		Character c = letterWon.getAlphabet();
		int bag_has = letterBag.decrement(c);
		assert(0 <= bag_has);
		int rack_has = letterRack.increment(c);
		assert(0 <= rack_has);
		score -= amount;
		cumulative_bid += amount;
		stupidCounter = 0;
	}

	private void lost(PlayerBids bid) {
		Letter letterLost = bid.getTargetLetter();
		Character c = letterLost.getAlphabet();
		int[] lostwords = hypotheticallosses(c);
		letterBag.decrement(c);

		l.debug(
				String.format("Marking %d words as unreachable (too few of '%c')",
						new Object[]{ lostwords.length, c})
		);
		for (int wordID : lostwords) {
			this.reachable[wordID] = false;
		}

		//Update information about the player who won the bid
		TrackedPlayer adversary = otherPlayers.get(bid.getWinnerID());
		adversary.score -= bid.getWinAmmount();
		adversary.letterRack.increment(c);
		adversary.openLetters.add(letterLost);
		stupidCounter++;
	}

	/* Interface required method #3
	 * (non-Javadoc)
	 * @see seven.ui.Player#returnWord()
	 */
    public String returnWord() {
    	l.debug("Player " + player_id  + " checking bid for final round: " + RefList.size());
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

    	/**
    	 * Adding to History
    	 */
    	l.debug("The size of the history is " + RefList.size());

    	if(RefList.size()== RefPlayerList.size()*7*10 ){
    		for(int i=0; i<RefPlayerList.size()*7*10;i++){
    			int winAmount= RefList.get(i).getWinAmmount();
    			int wonBy= RefList.get(i).getWinnerID();
    			boolean winnerStatus= player_id== RefList.get(i).getWinnerID()? true:false;
    			int ourBid= RefList.get(i).getBidvalues().get(player_id);
    			hist.add(ourBid, winAmount, winnerStatus);
    		}
    	}

    	return bestword;
    }
    /*
     * Bid value calculation methods.
     */
	/**
	 * @param currenttargets
	 * @param bidChar
	 * @return
	 */
	private long lossPenalty(Collection<Integer> currenttargets, char bidChar) {
		l.debug("Calculating lost probability for letter " + bidChar);
		long total = 0;
		LetterSet set = getLetterSet(bidChar);
		int[] bagarray = arrayFromMap(letterBag);
		int[] rackarray = arrayFromMap(letterRack);
		// alter bag array to assume that this letter is gone, but not in our rack
		int charIndex = getCharIndex(bidChar);
		bagarray[charIndex] -= 1;
		assert(bagarray[charIndex] >= 0);
		if (null != set) {
			int[] word_ids = set.getTransactions();
			// this cries out to be refactored
			// twice
			boolean[] affected_word = new boolean[TOTAL_WORDS];
			for (int wordID : word_ids) {
				affected_word[wordID] = true;
			}
			for (int wordID : currenttargets) {
				if (affected_word[wordID]) {
					Word w = getWord(wordID);
					long new_poss = w.drawPossibilities(bagarray, rackarray);
					long difference = word_draw_possibilities[wordID] - new_poss;
					assert(difference >= 0);
					l.trace("Difference for " + w.word + " is " + difference);
					total += difference;
				}
			}
		}
		return total;
	}

	/**
	 * @param target_indices
	 * @return
	 */
	private long sumTargetPossibilities(Collection<Integer> target_indices) {
		long currsum = 0;
    	for (int i : target_indices ) {
    		currsum += word_draw_possibilities[i];
    	}
		return currsum;
	}

	private int[] hypotheticallosses(char c) {
		int wordsfound[];
		int prevCount = letterBag.count(c) + letterRack.count(c);
		String[] terms = new String[prevCount];
		for (int i = 0; i < prevCount; i++) {
			terms[i] = Character.toString(c);
		}
		LetterSet set = getLetterSet(terms);
		if (null != set) {
			wordsfound = set.getTransactions();
		} else {
			wordsfound = new int[0];
		}
		return wordsfound;
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

	/*
	 * target-management functions
	 */
	private Collection<Word> getReachableSevenLetterWords(LetterSet lset, int[] wouldlose) {
		ArrayList<Word> found = new ArrayList<Word>();
		for (Integer i : getTargetIndices(lset, wouldlose)) {
			found.add(getWord(i));
		}
    	return found;
	}

	private List<Integer> emptyRackTargets(int[] wouldlose) {
		ArrayList<Integer> found = new ArrayList<Integer>();

		boolean temp_exclude[] = new boolean[TOTAL_WORDS];
		if (null != wouldlose) {
			for (int i : wouldlose) temp_exclude[i] = true;
		}

		for (int wordID = 0; wordID < TOTAL_WORDS; wordID++) {
			if (reachable[wordID] && !temp_exclude[wordID] && is_seven_letter[wordID]) {
				found.add(wordID);
			}
		}
		return found;
	}

	private List<Integer> getTargetIndices(LetterSet lset, int[] wouldlose) {
		ArrayList<Integer> found = new ArrayList<Integer>();

		boolean temp_exclude[] = new boolean[TOTAL_WORDS];
		if (null != wouldlose) {
			for (int i : wouldlose) temp_exclude[i] = true;
		}

    	if (null != lset) {
    		for (int wordID : lset.getTransactions()) {
    			if (reachable[wordID] && !temp_exclude[wordID] && is_seven_letter[wordID]) {
    				found.add(wordID);
    			}
    		}
    	}
		return found;
	}

    private Collection<Word> getReachableSevenLetterWords(LetterSet lset) {
    	return getReachableSevenLetterWords(lset, null);
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

    /*
	 * Static initialization (could be inlined in the up-top initializer block).
	 */

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

    public static class OriginalBidder extends G1Player {
    	protected int makeBid(int letters_needed, double kept_fraction, double lost_fraction, int auctions_left, Letter bidLetter) {
    		double cutoff = 0.4;
    		double bids_per_letter_remaining = letters_needed > 0 ? (double) auctions_left/letters_needed : 0;
    		if(6 == openletters.size() && 3 > bids_per_letter_remaining){
    			return 50;
    		}
    		// if we're low on options, take anything that doesn't hurt us
    		if (2 > bids_per_letter_remaining) {
    			l.debug("Using lower acceptability threshold: bid ratio is " + bids_per_letter_remaining);
    			cutoff = 0;
    		}
    		if(kept_fraction > cutoff) {
    			int tentative = (50+bidLetter.getValue()-cumulative_bid)/(7-openletters.size());
    			if(Math.abs(tentative - average_bid) > std_deviation)
    				tentative = (int)(tentative + average_bid)/2;
    			//return
    			return tentative;
    		} else {
    			return 0;
    		}
    	}
    }

    public static class LossBidder extends G1Player {

		/* (non-Javadoc)
		 * @see seven.g1.G1Player#makeBid(int, double, double, int, seven.ui.Letter)
		 */
		@Override
		protected int makeBid(int letters_needed, double kept_fraction, double lost_fraction, int auctions_left, Letter bidLetter) {
			return (int) ( (50 * lost_fraction) * (1 + 1/(double) auctions_left));
		}
    }
    public static class LossBidderMultiplier extends G1Player{
    	protected int makeBid(int letters_needed, double kept_fraction, double lost_fraction, int auctions_left, Letter bidLetter) {
    		double multiplier=1;
    		if(stupidCounter>=3){
    			//stupidCounter=0;
    			multiplier=3;
    		}
    		return (int) ( multiplier*(50 * lost_fraction) * (1 + 1/(double) auctions_left));
		}
    }
    public static class LossBidderMultiplier2 extends G1Player{
    	protected int makeBid(int letters_needed, double kept_fraction, double lost_fraction, int auctions_left, Letter bidLetter) {
    		double multiplier=1;
    		if(stupidCounter>=3){
    			//stupidCounter=0;
    			multiplier=2;
    		}
    		return (int) ( multiplier*(50 * lost_fraction) * (1 + 1/(double) auctions_left));
		}
    }
    public static class TimeLossBidder extends G1Player {

		/* (non-Javadoc)
		 * @see seven.g1.G1Player#makeBid(int, double, double, int, seven.ui.Letter)
		 */
		@Override
		protected int makeBid(int letters_needed, double kept_fraction, double lost_fraction, int auctions_left, Letter bidLetter) {
			double loss = 50.0 * lost_fraction;
			double time_left = (double) auctions_left / (double) this.total_auctions;
			double letters_left = (double) letters_needed / 7.0;
			double timefactor = 1.0;
			if (letters_left > time_left) {
				double ratio = letters_left / time_left;
				timefactor = 0.5 * Math.ceil(2 * ratio); // round up to nearest half
				l.debug(String.format("Letter fraction %.2f, time fraction %.2f: boosting by %.2f",
							new Object[]{letters_left, time_left, timefactor}
						)
				);
			}

			return (int) ( loss * timefactor);
		}
    }

}
