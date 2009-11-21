/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package seven.ui;

import java.util.ArrayList;

/**
 *
 * @author Satyajeet
 */
public class PlayerBids {

    ArrayList<Integer> bidvalues; // players from 0 to n-1
    Letter TargetLetter;
    String wonBy;
    
    int winAmmount;
    public PlayerBids(Letter targetletter) {

        bidvalues = new ArrayList<Integer>();
        TargetLetter = targetletter;
    }
    


}
