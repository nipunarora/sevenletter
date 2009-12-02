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
public class GameConfig {

   ArrayList<OpenState> openstateList;
   ArrayList<SecretState> secretstateList;
    ArrayList<PlayerBids> BidList;
    int number_of_secret_objects; // from 0 to 7
    ArrayList<String> PlayerList;
    ArrayList<Player> PObjectList;
    ArrayList<String> PlayerWords;
    ArrayList<Integer> lasPoints;
    Scrabble ScrabbleObject;
    
    int gameDelay = 500;
    int number_of_rounds;
    int current_round;
    ArrayList<String> wordbag;

    public GameConfig(ArrayList<String> plist, int secret_objs, IOController ioc, int numRounds) {

        // This does the basic shell memory allocations, actual data is handled by game controller.
        wordbag = new ArrayList<String>();
        ScrabbleObject = new Scrabble();
        BidList = new ArrayList<PlayerBids>();
        PlayerWords = new ArrayList<String>();
        PObjectList = new ArrayList<Player>();
        lasPoints = new ArrayList<Integer>();
        PlayerList = plist;
        number_of_secret_objects = secret_objs;
        current_round = 0;
        number_of_rounds = numRounds;
        for(int loop=0;loop<PlayerList.size();loop++)
        {
            Player ptemp = ioc.getPObject(PlayerList.get(loop));
            PObjectList.add(ptemp);
        }

        // Now you also need to init the player state
        openstateList = new ArrayList<OpenState>();
        secretstateList = new ArrayList<SecretState>();
        // Also put player number of states in it
        for(int loop=0;loop<PlayerList.size();loop++)
        {
            OpenState temp_os = new OpenState();
            openstateList.add(temp_os);

            SecretState temp_ss = new SecretState(100); // You start with 100 points
            secretstateList.add(temp_ss);
        }
        

        // Assign the first set of secret letters
        for(int loop=0;loop<PlayerList.size();loop++)
        {
            SecretState currSS = secretstateList.get(loop);
            for(int wordloop =0;wordloop<number_of_secret_objects;wordloop++)
            {
                Letter temp_letter = ScrabbleObject.getRandomFromBag();
                currSS.secretLetters.add(temp_letter);

            }

        }
     

    }

    public boolean isMoreBiddingLeft()
    {
        Boolean result = false;
        // This will see if any person with less than 7 letters exists
        for(int loop=0;loop<openstateList.size();loop++)
        {
            OpenState curr_os = openstateList.get(loop);
            int maxOpen = 7 - number_of_secret_objects;
            if(curr_os.openLetters.size() < maxOpen)
            {
                result = true;
                break;
            }

        }

        return result;
    }


}
