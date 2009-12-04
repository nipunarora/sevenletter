package seven.g4;

public class wordSegment {

	String s;
	int score;
	Trie.Vertex vertex;
	
	
	wordSegment(String s, int score, Trie.Vertex v)
	{
		this.s = s;
		this.score = score;
		vertex = v;
	}
	
	wordSegment(String s, int score)
	{
		this.s = s;
		this.score = score;
		vertex = null;
	}
}
