package seven.g2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class WordListTrimmer {
	public static void main(String[] args) {
		try {
			Scanner s = new Scanner(new File("SOWPODS.txt"));
			FileWriter fw = new FileWriter("SmallWordlist.txt");
			HashSet<String> indices = new HashSet<String>();
			s.nextLine();
			while(s.hasNextLine())
			{
				String word = s.nextLine().split(",")[1];
				String index = WordList.indexValue(word);
				if(word.length() <= 7 && scrabblePossible(word) && !indices.contains(index))
				{
					fw.write(index + "," + word + "\n");
					indices.add(index);
				}
			}
			fw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static boolean scrabblePossible(String w)
	{
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		for(int i=0;i<w.length();i++)
		{
			String l = w.substring(i,i+1);
			if(counts.containsKey(l))
				counts.put(l, counts.get(l)+1);
			else
				counts.put(l, 1);
		}

		return (!counts.containsKey("A") || counts.get("A") <= 9) &&
		(!counts.containsKey("B") || counts.get("B") <= 2) &&
		(!counts.containsKey("C") || counts.get("C") <= 2) &&
		(!counts.containsKey("D") || counts.get("D") <= 4) &&
		(!counts.containsKey("E") || counts.get("E") <= 12) &&
		(!counts.containsKey("F") || counts.get("F") <= 2) &&
		(!counts.containsKey("G") || counts.get("G") <= 3) &&
		(!counts.containsKey("H") || counts.get("H") <= 2) &&
		(!counts.containsKey("I") || counts.get("I") <= 9) &&
		(!counts.containsKey("J") || counts.get("J") <= 1) &&
		(!counts.containsKey("K") || counts.get("K") <= 1) &&
		(!counts.containsKey("L") || counts.get("L") <= 4) &&
		(!counts.containsKey("M") || counts.get("M") <= 2) &&
		(!counts.containsKey("N") || counts.get("N") <= 6) &&
		(!counts.containsKey("O") || counts.get("O") <= 8) &&
		(!counts.containsKey("P") || counts.get("P") <= 2) &&
		(!counts.containsKey("Q") || counts.get("Q") <= 1) &&
		(!counts.containsKey("R") || counts.get("R") <= 6) &&
		(!counts.containsKey("S") || counts.get("S") <= 4) &&
		(!counts.containsKey("T") || counts.get("T") <= 6) &&
		(!counts.containsKey("U") || counts.get("U") <= 4) &&
		(!counts.containsKey("V") || counts.get("V") <= 2) &&
		(!counts.containsKey("W") || counts.get("W") <= 2) &&
		(!counts.containsKey("X") || counts.get("X") <= 1) &&
		(!counts.containsKey("Y") || counts.get("Y") <= 2) &&
		(!counts.containsKey("Z") || counts.get("Z") <= 1);
	}
}
