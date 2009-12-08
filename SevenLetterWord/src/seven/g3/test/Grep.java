package seven.g3.test;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

public class Grep {

	public static List<String> matchingWords = new ArrayList<String>();

	// Charset and decoder for ISO-8859-15
	private static Charset charset = Charset.forName("ISO-8859-15");
	private static CharsetDecoder decoder = charset.newDecoder();

	// Pattern used to parse lines
	private static Pattern linePattern = Pattern.compile(".*\r?\n");

	// The input pattern that we're looking for
	private static Pattern pattern;

	// Compile the pattern from the command line
	//
	private static void compile(String pat) {
		try {
			pattern = Pattern.compile(pat);
		} catch (PatternSyntaxException x) {
			System.err.println(x.getMessage());
			System.exit(1);
		}
	}

	// Use the linePattern to break the given CharBuffer into lines, applying
	// the input pattern to each line to see if we have a match
	//
	private static void grep(File f, CharBuffer cb) {
		Matcher lm = linePattern.matcher(cb); // Line matcher
		Matcher pm = null; // Pattern matcher
		int lines = 0;
		while (lm.find()) {
			lines++;
			CharSequence cs = lm.group(); // The current line
			// System.out.println(cs);
			if (pm == null)
				pm = pattern.matcher(cs);
			else
				pm.reset(cs);
			if (pm.find()) {
				//System.out.print(f + ":" + lines + ":" + cs);
				matchingWords.add(cs.toString());
				//System.out.println(cs);
			}
			if (lm.end() == cb.limit())
				break;
		}
	}

	// Search for occurrences of the input pattern in the given file
	//
	private static void grep(File f) throws IOException {

		// Open the file and then get a channel from the stream
		FileInputStream fis = new FileInputStream(f);
		FileChannel fc = fis.getChannel();

		// Get the file's size and then map it into memory
		int sz = (int) fc.size();
		MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

		// Decode the file into a char buffer
		CharBuffer cb = decoder.decode(bb);

		// Perform the search
		grep(f, cb);

		// Close the channel and the stream
		fc.close();
	}

	public static String get7LetterWordPattern() {
		return ".......";
	}

	public static String getStringContainingAll(String charString) {
		String pattern = "";
		for (int i = 0; i < 7; i++) {
			pattern += "[" + charString + "]";
		}
		return pattern;
	}

	public static boolean isRepeating(String word) {
		Map<Character, Integer> charIntMap = new HashMap<Character, Integer>();

		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			Integer integer = charIntMap.get(c);
			if (integer != null) {
				return true;
			}
			charIntMap.put(c, 1);
		}
		return false;
	}

	public static boolean isRepeatingAsPer(String word,
			Map<Character, Integer> exceptMap) {
		Map<Character, Integer> charIntMap = new HashMap<Character, Integer>();
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			Integer integer = charIntMap.get(c);
			if(integer == null){
				integer =0;
			}
			Integer numberOfRepetitionsAllowed = exceptMap.get(c);
			if(numberOfRepetitionsAllowed == null){
				continue;
			}
			//System.out.println(c + " - "+ integer + " - "+numberOfRepetitionsAllowed);
			if (++integer > numberOfRepetitionsAllowed) {
				return true;
			}
			charIntMap.put(c, integer);
		}
		return false;
	}

	public static void filterRepeating() {
		List<String> inputWords = new ArrayList<String>(matchingWords);
		matchingWords.clear();
		for (String s : inputWords) {
			if (!isRepeating(s)) {
				matchingWords.add(s);
			}
		}
	}

	public static void filterRepeating(Map<Character, Integer> exceptMap) {
		List<String> inputWords = new ArrayList<String>(matchingWords);
		matchingWords.clear();
		for (String s : inputWords) {
			if (!isRepeatingAsPer(s, exceptMap)) {
				matchingWords.add(s);
			}
		}
	}
	
	public static void findSevenLetterWords()
	{
		File f = new File(
		"C:\\Users\\shilpa\\workspace\\7Letters\\src\\seven\\g3\\KnowledgeBase\\smallwordlist.txt");
		compile(".......");
		try {
			grep(f);
		} catch (IOException x) {
			System.err.println(f + ": " + x);
		}
	}
	
	public static void findWordsWithEAIONRT()
	{
		File f = new File(
		"C:\\Users\\shilpa\\workspace\\7Letters\\src\\seven\\g3\\KnowledgeBase\\smallwordlist.txt");
		compile("[EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT]");
		try {
			grep(f);
		} catch (IOException x) {
			System.err.println(f + ": " + x);
		}
	}

	/**  E-12 A,I-9  O-8 N,R,T -6*/
	public static void findWordsWithEAIONRTOnly()
	{
		File f = new File(
		"C:\\Users\\shilpa\\workspace\\7Letters\\src\\seven\\g3\\KnowledgeBase\\smallwordlist.txt");
		compile("[EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT]");
		try {
			grep(f);
		} catch (IOException x) {
			System.err.println(f + ": " + x);
		}
		filterRepeating();
	}
	
	public static void findWordsWithEAIONRTWithExceptions()
	{
		File f = new File(
		"C:\\Users\\shilpa\\workspace\\7Letters\\src\\seven\\g3\\database\\EAIONRT.txt");
		compile("[EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT][EAIONRT]");
		try {
			grep(f);
		} catch (IOException x) {
			System.err.println(f + ": " + x);
		}
		Map<Character, Integer> exceptMap = new HashMap<Character, Integer>();
		exceptMap.put('E', 2);
		exceptMap.put('A', 1);
		exceptMap.put('I', 1);
		exceptMap.put('O', 1);
		exceptMap.put('N', 1);
		exceptMap.put('R', 1);
		exceptMap.put('T', 1);
		
		filterRepeating(exceptMap);
	}
	
	/* NRT - frequency 4 , DLSU - frequency - 2 */
	public static void findWordsWithNRTDLSUOnly()
	{
		File f = new File(
		"C:\\Users\\shilpa\\workspace\\7Letters\\src\\seven\\g3\\KnowledgeBase\\smallwordlist.txt");
		//compile("[JKQZXDLSUAE][JKQZXDLSUAE][JKQZXDLSUAE][JKQZXDLSUAE][JKQZXDLSUAE][JKQZXDLSUAE][JKQZXDLSUAE]"); // Ans - QUEZALS
		//compile("[DLSUAEI][DLSUAEI][DLSUAEI][DLSUAEI][DLSUAEI][DLSUAEI][DLSUAEI]"); // ans - AUDILES
		compile("[AOIBCFHMPVWY][AIOBCFHMPVWY][AIOBCFHMPVWY][AIOBCFHMPVWY][AIOBCFHMPVWY][AIOBCFHMPVWY][AIOBCFHMPVWY]"); // ans - OPACIFY
		try {
			grep(f);
		} catch (IOException x) {
			System.err.println(f + ": " + x);
		}
		filterRepeating();
	}
	
//	public static void findWordsWithNRTDLSUOnly()
//	{
//		File f = new File(
//		"C:\\Users\\shilpa\\workspace\\7Letters\\src\\seven\\g3\\KnowledgeBase\\smallwordlist.txt");
//		compile("[NRTDLSU][NRTDLSU][NRTDLSU][NRTDLSU][NRTDLSU][NRTDLSU][NRTDLSU]");
//		try {
//			grep(f);
//		} catch (IOException x) {
//			System.err.println(f + ": " + x);
//		}
//		//filterRepeating();
//	}
	
	
	public static void main(String[] args) {
	
	//	findWordsWithEAIONRTOnly(f);		
		//findWordsWithEAIONRTWithExceptions();
		findWordsWithNRTDLSUOnly();
		for (String s : matchingWords) {
			
			System.out.println(s);
		}
		System.out.println("total words="+matchingWords.size());
	}
}
