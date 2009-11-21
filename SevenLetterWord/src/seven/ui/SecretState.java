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
public class SecretState {
    ArrayList<Letter> secretLetters;

    int score;
    int total_letters;
    public SecretState(int scoreL)
    {

        secretLetters = new ArrayList<Letter>();
        score = scoreL;
    }

}
