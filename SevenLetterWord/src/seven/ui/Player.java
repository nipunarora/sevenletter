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
public interface Player {

    

    public void Register();



    public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,int total_rounds, ArrayList<String> PlayerList, SecretState secretstate);
    
    public String returnWord();

}

