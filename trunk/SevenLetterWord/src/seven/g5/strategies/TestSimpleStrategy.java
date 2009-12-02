package seven.g5.strategies;

import java.util.ArrayList;

import seven.g5.data.OurLetter;
import seven.g5.data.Word;

public class TestSimpleStrategy extends SimpleStrategy {

	public TestSimpleStrategy() {
		// TODO Auto-generated constructor stub
	}

	public static void main( String[] args ) {
		SimpleStrategy strat = new SimpleStrategy();
		ArrayList<OurLetter> myHand = new ArrayList<OurLetter>();

		//ArrayList<String> myWordList = new ArrayList<String>();		
		strat.binHeapOfCurrentWords.add(new Word("CAT"));
		strat.binHeapOfCurrentWords.add(new Word("RAT"));
		strat.binHeapOfCurrentWords.add(new Word("HAT"));
				
		while( strat.binHeapOfCurrentWords.size() > 0 ) {
			System.out.println("word "+strat.binHeapOfCurrentWords.peek()+" is "+((Word)strat.binHeapOfCurrentWords.poll()).getScore() );
		}
	}
	
}
