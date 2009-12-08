package seven.g5.data;

public class OurLetter extends seven.ui.Letter {

    //from parent class
	private Character alphabet;
    private int value;
	//added
    private int numberInGame;
    private int numWordsPossibleWithThisAdditionalLetter = 0;
	
	public int getNumWordsPossibleWithThisAdditionalLetter() {
		return numWordsPossibleWithThisAdditionalLetter;
	}

	public void setNumWordsPossibleWithThisAdditionalLetter(
			int numWordsPossibleWithThisAdditionalLetter) {
		this.numWordsPossibleWithThisAdditionalLetter = numWordsPossibleWithThisAdditionalLetter;
	}

	public int getNumberInGame() {
		return numberInGame;
	}

	public void setNumberInGame(int numberInGame) {
		this.numberInGame = numberInGame;
	}

	public OurLetter(Character c, int s) {
		super(c,s);
		numberInGame = ScrabbleParameters.getCount( c );
	}

}
