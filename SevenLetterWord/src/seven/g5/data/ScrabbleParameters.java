package seven.g5.data;

public final class ScrabbleParameters {

	//not currently used
	public enum Characters {
		A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
	}
	
	//satya's method in Scrabble doesn't return values we can use
	public static int getCount( Character whichLetter ) {
		if (whichLetter == 'A') return 9;
		if (whichLetter == 'B') return 2;
		if (whichLetter == 'C') return 2;
		if (whichLetter == 'D') return 4;
		if (whichLetter == 'E') return 12;
		if (whichLetter == 'F') return 2;
		if (whichLetter == 'G') return 3;
		if (whichLetter == 'H') return 2;
		if (whichLetter == 'I') return 9;
		if (whichLetter == 'J') return 1;
		if (whichLetter == 'K') return 1;
		if (whichLetter == 'L') return 4;
		if (whichLetter == 'M') return 2;
		if (whichLetter == 'N') return 6;
		if (whichLetter == 'O') return 8;
		if (whichLetter == 'P') return 2;
		if (whichLetter == 'Q') return 1;
		if (whichLetter == 'R') return 6;
		if (whichLetter == 'S') return 4;
		if (whichLetter == 'T') return 6;
		if (whichLetter == 'U') return 4;
		if (whichLetter == 'V') return 2;
		if (whichLetter == 'W') return 2;
		if (whichLetter == 'X') return 1;
		if (whichLetter == 'Y') return 2;
		if (whichLetter == 'Z') return 1;
		return 0;
	}
	
	public static int getScore( Character character ) {
		if (character == 'A') return 1;
		if (character == 'B') return 3;
		if (character == 'C') return 3;
		if (character == 'D') return 2;
		if (character == 'E') return 1;
		if (character == 'F') return 4;
		if (character == 'G') return 2;
		if (character == 'H') return 4;
		if (character == 'I') return 1;
		if (character == 'J') return 8;
		if (character == 'K') return 5;
		if (character == 'L') return 1;
		if (character == 'M') return 3;
		if (character == 'N') return 1;
		if (character == 'O') return 1;
		if (character == 'P') return 3;
		if (character == 'Q') return 10;
		if (character == 'R') return 1;
		if (character == 'S') return 1;
		if (character == 'T') return 1;
		if (character == 'U') return 1;
		if (character == 'V') return 4;
		if (character == 'W') return 4;
		if (character == 'X') return 8;
		if (character == 'Y') return 4;
		if (character == 'Z') return 10;
		return 0;
	}	
}
