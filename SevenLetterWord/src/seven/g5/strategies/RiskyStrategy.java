package seven.g5.strategies;

import java.util.ArrayList;
import seven.g5.gameHolders.GameInfo;
import seven.g5.gameHolders.PlayerInfo;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class RiskyStrategy extends Strategy {

	@Override
	public String getFinalWord() {
		// TODO Auto-generated method stub
		return "hi";
	}

	@Override
	public int getBid(GameInfo gi, PlayerInfo pi) {
		return bidpoints;
		
	}

}
