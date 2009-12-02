package seven.g5.data;

public class OurLetter extends seven.ui.Letter {

    //from parent class
	private Character alphabet;
    private int value;
	//added
    private int numberInGame;
	
	public OurLetter(Character c, int s) {
		super(c,s);
		numberInGame = ScrabbleParameters.getCount( c );
	}
}
