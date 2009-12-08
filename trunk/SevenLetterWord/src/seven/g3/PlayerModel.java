package seven.g3;

import java.util.HashMap;

public class PlayerModel {
    /* Generic Class that can be used to model the state of other players */
    public int hiddenLetters;
    public HashMap<Character, Integer> knownLetters;
    public int score = 100;
    
    public static final Boolean DEBUG = true;
    
    public PlayerModel(int hl)
    {
        knownLetters = new HashMap<Character, Integer>();
        score = 100;
        hiddenLetters = hl;
    }
    
    public void addLetter(Character c)
    {
        if(!knownLetters.containsKey(c)) {
            knownLetters.put(c, 0);
        }
        
        knownLetters.put(c, knownLetters.get(c)+1);
    }
    
    public void wonBid(Character c, int amount)
    {
        this.addLetter(c);
        this.score -= amount;
    }
}
