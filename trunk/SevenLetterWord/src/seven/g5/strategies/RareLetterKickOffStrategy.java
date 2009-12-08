package seven.g5.strategies;

import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.g5.Utilities;

public class RareLetterKickOffStrategy extends Strategy {


	public RareLetterKickOffStrategy() {
		super();
		//System.out.println("Switching to RareLetter KickOff Strategy! Pow!");
	}

	@Override
	public int[] getBid(GameInfo gi, PlayerInfo pi) {
		int bid = gi.getCurrentBidLetter().getValue();
		int continueStrat = 1;
		int[] ans = { bid, continueStrat };
		if ( pi.getRack().size() == 0 /*|| pi.getRack().size() == 1*/ ) {
			////System.out.println(gi.getCurrentBidLetter()+" "+Utilities.getProbabilityOfLetter(gi.getCurrentBidLetter(),gi));
			if((Utilities.getProbabilityOfLetter(gi.getCurrentBidLetter(),gi) <= 
					(6.0f/98.0f)*(float)gi.getNoOfTurnsRemaining() &&
				(Utilities.getProbabilityOfLetter(gi.getCurrentBidLetter(),gi) >=
					(2.0f/98.0f)*(float)gi.getNoOfTurnsRemaining() ))) {
				return ans;
			}
		}
//		if ( pi.getRack().size() == 1 /*|| pi.getRack().size() == 1*/ ) {
//			////System.out.println(gi.getCurrentBidLetter()+" "+Utilities.getProbabilityOfLetter(gi.getCurrentBidLetter(),gi));
//			if (
//			gi.getCurrentBidLetter().getAlphabet() == 'E' ||
//			gi.getCurrentBidLetter().getAlphabet() == 'A' ||
//			gi.getCurrentBidLetter().getAlphabet() == 'I' ||
//			gi.getCurrentBidLetter().getAlphabet() == 'O' ||
//			gi.getCurrentBidLetter().getAlphabet() == 'N' ||
//			gi.getCurrentBidLetter().getAlphabet() == 'R' ||
//			gi.getCurrentBidLetter().getAlphabet() == 'T' )
//				return (20); //bid for common letters at first
//		}
		ans[0] = 0;
		return ans;
	}
}
