package seven.g4;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    
    protected Vertex root;

    /**
     * @author yeyangever
     * words: number of *exact word* in this vertex, in dictionary case either 0 or 1
     * prefixes: number of words using this vertex as prefix  (equals to number of all words of children vertexes
     * children: child vertices
     */
    
    
    /**
     * @author yeyangever
     * Used to record suffix in the tree structure
     */
    
    protected class Vertex {
        protected int words;
        protected int prefixes;
        protected Vertex[] children;
        Vertex() {
            words = 0;
            prefixes = 0;
            children = new Vertex[26];
            for (int i = 0; i < children.length; i++) {
                children[i] = null;
            }
        }
    }
    
    public Trie () {
        root = new Vertex();
    }
    
    /** *//**
     * List all words in the Trie.
     * 
     * @return
     */
    public List<String> listAllWords() {
        
        List<String> words = new ArrayList<String>();
        
        Vertex[] children = root.children;
        
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                String word = "" + (char)('A' + i);
                depthFirstSearchWords(words, children[i], word);
            }
        }        
        return words;
    }
    
    public int countPrefixes(String prefix) {
        return countPrefixes(root, prefix);
    }
    
    protected int countPrefixes(Vertex vertex, String prefixSegment) {
        if (prefixSegment.length() == 0) { //reach the last character of the word
        	/*if(vertex == root)
        		return 1;*/
            return vertex.prefixes;
        }
        char c = prefixSegment.charAt(0);
        int index = c - 'A';
        if (vertex == null ||vertex.children[index] == null) { // the word does NOT exist
            return 0;
        } else {
            return countPrefixes(vertex.children[index], prefixSegment.substring(1));
        }        
    }
    
    public int countWords(String word) {
        return countWords(root, word);
    }    
    protected int countWords(Vertex vertex, String wordSegment){
    	/*if(vertex == root)
    		return 1;*/
        if (wordSegment.length() == 0) { //reach the last character of the word
           return vertex.words;
        }
        char c = wordSegment.charAt(0);
        int index = c - 'A';
        if (vertex.children[index] == null) { // the word does NOT exist
            return 0;
        } else {
            return countWords(vertex.children[index], wordSegment.substring(1));
        }        
    }
    

    /**
     * @param words  :  the words to be returned from search
     * @param vertex	:  from which vertex to search
     * @param prefix	:  the prefix to search
     */
    private  void depthFirstSearchWords(List<String> words, Vertex vertex, String prefix) {
        Vertex[] children = vertex.children;
        boolean hasChild = false;
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                hasChild = true;
                String newPrefix = prefix + (char)('A' + i);                
                depthFirstSearchWords(words, children[i], newPrefix);
            }            
        }
        if (!hasChild) {
            words.add(prefix);
        }
    }
    
    /** *//**
     * Add a word to the Trie.
     * 
     * @param word The word to be added.
     */
    protected void addWord(String word) {
        addWord(root, word);
    }
    
    /** *//**
     * Add the word from the specified vertex.
     * @param vertex The specified vertex.
     * @param word The word to be added.
     */
    private void addWord(Vertex vertex, String word) {
        if (word.length() == 0) { //if all characters of the word has been added
            vertex.words ++;
        } else {
            vertex.prefixes ++;
            char c = word.charAt(0);
            //c = Character.toLowerCase(c);
            int index = c - 'A';
            if (vertex.children[index] == null) { //if the edge does NOT exist
                vertex.children[index] = new Vertex();
            }
            addWord(vertex.children[index], word.substring(1)); //go the the next character
        }
    }
    
    public Vertex getNode(String s)
    {
    	return getNode(root, s);
    }
    
    
    public Vertex getNode(Character c)
    {
    	return getNode(root, c);
    }
    
    public Vertex getNode(Vertex node, String s )
    {
    	if(s.equals("") || s==null)
    		return node;
    	
    	Vertex cur = node;
    	for(int i = 0; i<s.length(); i++)
    	{
    		int index = s.charAt(i) - 'A';
    		//System.out.println("index"+index);
    		if(cur.children[index] == null)
    			return null;
    		else
    			cur = cur.children[index];
    	}
    	return cur;
    }
    
    public Vertex getNode(Vertex node, Character c )
    {    	
    	Vertex cur = node;
    	
    	int index = c - 'A';
    	return cur.children[index];
    }
    
    
    
}


    

