package seven.g1.datamining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LetterMine extends DataMine {

	public class LetterSet extends ItemSetInt {
		public LetterSet(String[] items, Integer[] docids) {
			super(items, docids);
		}
		public LetterSet(char c, Integer[] docids) {
			super(new String[] { Character.toString(c)}, docids);
		}

	}

	//ArrayList<ArrayList<Integer>> letterIndex = new ArrayList<ArrayList<Integer>>(26);
	SortedMap<Character,ArrayList<Integer>> letterIndex = new TreeMap<Character,ArrayList<Integer>>();
	private String[] wordIndex;
	private String wordListFile;

	public LetterMine(String filename) {
		super(filename);
		wordListFile = filename;

		for (char c = 'A'; c <= 'Z'; c++) {
			letterIndex.put(c, new ArrayList<Integer>());
		}

		// TODO Auto-generated constructor stub
	}

	@Override
	public void buildIndex() {
		// TODO Auto-generated method stub
		File wordFile = new File(wordListFile);
		FileReader r = null;
		ArrayList<String> wordArrayList = new ArrayList<String>(55000);
		try {
			r = new FileReader(wordFile);
			BufferedReader br = new BufferedReader(r);
			for(;;) {
				String line = br.readLine();
				if (null == line) break;
				line = line.trim();
				wordArrayList.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordIndex = wordArrayList.toArray(new String[0]);
		this.totalDocs = wordIndex.length;
		for (int wordID = 0; wordID < wordIndex.length; wordID++) {
			//seven.g1.Word w = new seven.g1.Word(wordIndex[wordID]);
			String thisword = wordIndex[wordID];
			Set<Character> chars = new HashSet<Character>();
			for (int j = 0; j < thisword.length(); j++) {
				char c = thisword.charAt(j);
				chars.add(c);
			}
			for (char c : chars) {
				letterIndex.get(c).add(wordID);
			}
		}

		for (char c = 'A'; c <= 'Z'; c++) {
			this.singletonSets.add(
				new LetterSet(c,letterIndex.get(c).toArray(new Integer[0]))
			);
		}
	}

	@Override
	public void findCommon(int i) {
		throw new IllegalArgumentException("findCommon is not currently implemented");
	}

	@Override
	public double findSupport(String[] terms) {
		throw new IllegalArgumentException("findSupport is not currently implemented");
	}

	@Override
	protected Iterator<String> getTerms() {
		// TODO Auto-generated method stub
		return null;
	}

}
