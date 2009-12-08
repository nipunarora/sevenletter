package seven.g4;

import java.util.ArrayList;

import seven.g4.Trie.Vertex;

public class SuffixTrie {

	SuffixTrie()
	{
		root = new Vertex();
	}
	
	protected Vertex root;
	
    protected class Vertex
    {
    	protected String s;
    	private ArrayList<String> suffix;
        
        protected Vertex[] children;
        Vertex() {
            children = new Vertex[26];
            for (int i = 0; i < children.length; i++) {
                children[i] = null;
            }
            suffix = new ArrayList<String>();
        }
 
		public ArrayList<String> getSuffix() {
			return suffix;
		}
		
		public int getCount() {
			return suffix.size();
		}    	
    }
    
    
    
    protected void addWord(String word, String remainder) {		
		Vertex vertex = root;
		for(int i = 0; i < word.length(); i++)
		{
			char c = word.charAt(i);
			//c = Character.toLowerCase(c);
			int index = c - 'A';
			if (vertex.children[index] == null)  //if the edge does NOT exist
				vertex.children[index] = new Vertex();            
            vertex = vertex.children[index];
        }
		vertex.s = word;
		vertex.suffix.add(remainder);
    }

    
    
    private Vertex getNode(String s)
    {
    	return getNode(root, s);
    }
    
    
    private Vertex getNode(Vertex node, String s )
    {
    	if(s.equals("") || s==null)
    		return node;
    	
    	Vertex cur = node;
    	for(int i = 0; i<s.length(); i++)
    	{
    		int index = s.charAt(i) - 'A';
    		if(cur.children[index] == null)
    			return null;
    		else
    			cur = cur.children[index];
    	}
    	return cur;
    }
    
    
	protected int getSuffixCount(String word){
		Vertex v = getNode(word);
		if(v == null)
			return 0;
		return v.getCount();		
	}
	
	protected ArrayList<String> getSuffix(String word){
		Vertex v = getNode(word);
		if(v == null)
			return null;
		return v.getSuffix();
	}
    
    
}
