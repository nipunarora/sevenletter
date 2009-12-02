package seven.g5.apriori_ben;

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
                /* (non-Javadoc)
                 * @see seven.g1.datamining.DataMine.ItemSetInt#intersect(seven.g1.datamining.DataMine.ItemSet, boolean)
                 */
                @Override
                public ItemSet intersect(ItemSet other_in, boolean finalRound) {
                        if (this == other_in) {
                                logger.trace("In special intersection case");
                                String items[] = getItems();
                                int repeats = 1;
                                String repeated = items[items.length - 1];
                                char basechar = repeated.charAt(0);
                                for (String item : items) {
                                        if (item.equals(repeated)) repeats++;
                                }
                                if (logger.isTraceEnabled()) {
                                        logger.trace(String.format(
                                                "Subsetting %s: looking for %d copies of %s",
                                                new Object[]{ Arrays.deepToString(items), repeats, repeated }
                                        ));
                                }
                                ArrayList<Integer> intersected = new ArrayList<Integer>();
                                for (int wordID : transList) {
                                        String word = LetterMine.this.wordIndex[wordID];
                                        int count = 0;
                                        for (int i = 0; i < word.length(); i++) {
                                                if (word.charAt(i) == basechar) {
                                                        if (++count >= repeats) {
                                                                intersected.add(wordID);
                                                                break;
                                                        }
                                                }
                                        }
                                }
                                String[] newterms = intersectionTerms(other_in);
                                LetterSet ans = new LetterSet(newterms,intersected.toArray(new Integer[0]));
                                return ans;
                        } else {
                                ItemSetInt tmp = (ItemSetInt) super.intersect(other_in, finalRound);
                                if (null == tmp) return null;
                                Integer stupidcopy[] = new Integer[tmp.transList.length];
                                for (int i = 0; i < stupidcopy.length;i++) stupidcopy[i] = tmp.transList[i];
                                return new LetterSet(tmp.getItems(),stupidcopy);
                        }
                }
                public String[] getWords() {
                        String[] words = new String[this.transList.length];
                        for (int i = 0; i < transList.length; i++) {
                                words[i] = wordIndex[transList[i]];
                        }
                        return words;
                }

                public int[] getTransactions() {
                        return Arrays.copyOf(transList, transList.length);
                }

        }

        SortedMap<Character,ArrayList<Integer>> letterIndex = new TreeMap<Character,ArrayList<Integer>>();

        private String[] wordIndex;

        public LetterMine(String filename) {
                super(filename);
                for (char c = 'A'; c <= 'Z'; c++) {
                        letterIndex.put(c, new ArrayList<Integer>());
                }
        }

        public String getWord(int wordID) {
                return wordIndex[wordID];
        }

        public Iterator<String> getWordIterator() {
                return Arrays.asList(wordIndex).iterator();
        }

        @Override
        public void buildIndex() {
                File wordFile = new File(this.name);
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
                        logger.fatal("Error reading word list",e);
                        throw new RuntimeException(e);
                }
                wordIndex = wordArrayList.toArray(new String[0]);
                this.totalDocs = wordIndex.length;
                for (int wordID = 0; wordID < wordIndex.length; wordID++) {
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
                throw new UnsupportedOperationException("findCommon is not currently implemented");
        }

        @Override
        public double findSupport(String[] terms) {
                throw new UnsupportedOperationException("findSupport is not currently implemented");
        }

        @Override
        protected Iterator<String> getTerms() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("getTerms is not implemented");
        }

}
