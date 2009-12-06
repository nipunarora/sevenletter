package seven.g1;

import java.util.ArrayList;

/**
 * A Class to mantain/track history and calculate the budge required
 * @author Nipun Arora
 *
 */
public class History {
	
	ArrayList<Integer> ourBid= new ArrayList<Integer>();
	ArrayList<Integer> winningValue= new ArrayList<Integer>();
	ArrayList<Boolean> winStatus= new ArrayList<Boolean>();
	
	/**
	 * populate the list with the bids and bidStatus
	 * @param ourBid
	 * @param winningValue
	 * @param winstatus
	 */
	
	public void add(int ourBid, int winningValue, boolean winStatus){
		this.ourBid.add(ourBid);
		this.winningValue.add(winningValue);
		this.winStatus.add(winStatus);
	}
	/**
	 * returns the length of the current History
	 * @return
	 */
	public int length(){
		return ourBid.size();
	}
}
