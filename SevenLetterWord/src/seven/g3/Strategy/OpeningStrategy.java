package seven.g3.Strategy;

import java.util.*;

import seven.g3.Util;
import seven.g3.KnowledgeBase.KnowledgeBase;
import seven.g3.KnowledgeBase.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.ScrabbleValues;
import seven.ui.SecretState;

public class OpeningStrategy extends Strategy {
	
	boolean failed;
	public static final int MIN_FREQ = 250;
	
	public OpeningStrategy(KnowledgeBase kb, int totalRounds,
			ArrayList<String> playerList) {
		super(kb, totalRounds, playerList);
		failed = false;
	}

	@Override
	public int calculateBidAmount(Letter bidLetter,
			HashMap<Character, Integer> letters, int paidThisRound) {
		if(letters.size() > 1) {
			/* this strategy should only be useful for first or second letter */
			return -1;
		}
		else {
			if(letters.size() == 0 || ScrabbleValues.getLetterFrequency(bidLetter.getAlphabet()) == 1) {
				/* either first letter or bidLetter is common; bid value is fine 
				 * TODO:  maybe bid higher for fewer players?
				 **/
				return bidLetter.getValue();
			}
			else {
				Set<Character> firstLetter = letters.keySet();
				char a = '0', b = bidLetter.getAlphabet().charValue();
				for(Character c : firstLetter) {
					/* firstLetter should have just one letter, but I don't know a better way to extract from Set */
					a = c.charValue();
				}
				
				if(ScrabbleValues.getLetterFrequency(a) <= 1) {
					return bidLetter.getValue();
				}
				else {
					PriorityQueue<Word> tupleWords = KnowledgeBase.tupleScan(a, b);
					Util.println("Tuple <" + a + b + "> frequency = " + tupleWords.size());
					if(tupleWords.size() > MIN_FREQ) {
						return bidLetter.getValue();
					}
					else {
						/* Too infrequent */
						Util.println(System.out.format("Tuple <%c, %c> too infrequent:   %d", a, b, tupleWords.size()).toString());
						return 0;
					}
				}
			}
		}
	}

	@Override
	public String returnWord(HashMap<Character, Integer> myLetters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			SecretState secretstate, int numLetters,
			HashMap<Character, Integer> letters) {
		// TODO Auto-generated method stub
		if(letters.size() > 1)
			failed = true;

	}
	
	public boolean hasFailed()
	{
		return failed;
	}

}
