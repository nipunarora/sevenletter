package seven.g2.miner;


import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
* @author ben
*
*/

public abstract class DataMine {

	protected String name;
	protected Logger logger;
	protected Set<ItemSet> singletonSets = new TreeSet<ItemSet>();
	protected ItemSet[] foundSets = null;
	protected double maxSupport = 1, minSupport = 0;
	int minDocs = -1;
	protected final int max_itemset_size = 7;
	protected int totalDocs;
	protected double minValue;
	/* internals for a priori. */
	/* this one might want to be private, actually */
	protected HashMap<String,ItemSet> itemCache = new HashMap<String,ItemSet>();
	private HashMap<String,Integer> indexLookup = new HashMap<String,Integer>();

	/**
	 * @return the dataset name for this Data Mine (generally "yahoo" or "20newsgroups").
	 */
	public String datasetName() {
		return name;
	}

	/**
	 * @return the maximum support of any term in the mine.  (Starts as 1, but will
	 * be reduced if findCommon has been called.)
	 */
	public double getMaxSupport() {
		return maxSupport;
	}

	/**
	 * @param datasetName the name of the dataset to be indexed (presumably "yahoo" or "20newsgroups").
	 */
	public  DataMine(String datasetName) {
		name = datasetName;
		logger = Logger.getLogger(this.getClass());
	}

	/**
	 * Build the index for this data mine.
	 * Exact details are entirely implementation-dependent, of course, but this should
	 * handle any and all query-independent data-structure construction.
	 */
	public abstract void buildIndex();
	/**
	 * @param terms the terms in an itemset which may or may not appear in this set of transactions.
	 * @return the support of this hypothetical itemset, as a number between 0 and 1 inclusive.
	 */
	public abstract double findSupport(String terms[]);
	protected abstract Iterator<String> getTerms();
	/**
	 * Find the most-common terms in the data set being mined,
	 * print them out, and remove them from the terms set.
	 * Optionally, use this step to start the a-priori itemset-building process
	 * (as a side-effect).
	 * @param i the number of terms to remove.
	 */
	public abstract void findCommon(int i);

	public ItemSet getCachedItemSet(String[] terms) {
		String lookup = makeKey(terms);
		return itemCache.get(lookup);
	}

	/**
	 * Run the <i>a priori</i> algorithm on the terms in this data mine.  <b>Must be called after buildIndex().</b>
	 *   Stores all qualified itemsets in an internal array, for convenience.
	 * @param minsupport the minimum support for an itemset to be considered "large".
	 * Any itemset that has a support strictly less than this number will be ignored.
	 * @return all itemsets of three or fewer items that have at least the given support.
	 */
	public ItemSet[] aPriori(double minsupport) {
		logger.debug("Entering a priori method");

		ArrayList<ArrayList<ItemSet>> roundLists = new ArrayList<ArrayList<ItemSet>>();
		if (null == this.singletonSets) {
			String msg ="aPriori called before index built, or index built improperly";
			logger.fatal(msg);
			throw new RuntimeException(msg);
		}
		this.minSupport = minsupport;
		if (minDocs < 0) {
			double tmpSupport = minSupport * totalDocs;
			minDocs = (int) tmpSupport;
			//	round fractions up--common case, but we wouldn't want to
			//  accidentally make the cutoff one too high that one time in a thousand
			if (minDocs < tmpSupport) {
				minDocs++;
			}
			logger.debug("MinDocs set to " + minDocs);
		}
		ArrayList<ItemSet> allRounds = new ArrayList<ItemSet>();
		// we don't actually need round 0 any more, but we keep it around
		// for tradition's sake
		for (int roundnum = 0; roundnum <= max_itemset_size; roundnum++) {
			logger.debug("Starting round " + roundnum + " of a priori tests");
			/* only slightly less naive implementation: */
			ArrayList<ItemSet> thisRound = runAPRound(roundLists, roundnum, minsupport);
			logger.debug("Found " + thisRound.size() + " itemsets");
			roundLists.add(thisRound);
			if (roundnum > 0) allRounds.addAll(thisRound);
		}


		ItemSet ans[] = allRounds.toArray(new ItemSet[0]);
		foundSets = ans;
		return ans;
	}

	private ArrayList<ItemSet> runAPRound(ArrayList<ArrayList<ItemSet>> roundLists, int roundnum, double minsupport) {
		ArrayList<ItemSet> thisRound = new ArrayList<ItemSet>();
		long skipCount=0;
		if ( 0 == roundnum) {
			/* round zero: empty set, 100% support */
			thisRound.add(new ItemSet(new String[0], 1));
			return thisRound;
		}

		if (1 == roundnum) {
			/* round 1: just filter the singleton set list */
			Iterator<ItemSet> i = singletonSets.iterator();
			ArrayList<String> singletonKeys = new ArrayList<String>();
			while (i.hasNext()) {
				ItemSet curr = i.next();
				if (curr.getSupport() >= minsupport) {
					String k = curr.getKey();
					singletonKeys.add(curr.getKey());
					itemCache.put(k, curr);
				}
				else i.remove();
			}
			Collections.sort(singletonKeys);
			for (int idx = 0; idx < singletonKeys.size(); idx++) {
				String k = singletonKeys.get(idx);
			//	indexLookup.put(k, idx);
				thisRound.add(itemCache.get(k));
			}
			indexLookup.put("", thisRound.size());
			return thisRound;
		}

		/* otherwise: round 2 or higher, so we will iterate over the cross-product
		 * as outlined in the a priori algorithm
		 */


		ItemSet prevRound[] = roundLists.get(roundnum - 1).toArray(new ItemSet[0]);
		/* now begin the heuristic hacks: */
		boolean isFinal = (max_itemset_size == roundnum);
		long out_of = (long) roundLists.get(1).size() * (long) prevRound.length;
		long debug_step = out_of / 100;
		logger.debug("Enumerating " + out_of + " ItemSet combinations");
		long this_set_number = 0;
		//for (ItemSet baseSet : roundLists.get(roundnum - 1)) {
		for (int j = 0; j < prevRound.length; j++) {
			ItemSet baseSet = prevRound[j];
			StringBuffer baseKey = new StringBuffer();;
			String oldterms [] = baseSet.getItems();
			for (int k = 0; k < oldterms.length - 1; k++) {
				if (k > 0) baseKey.append(' ');
				baseKey.append(oldterms[k]);
			}
			for (int k = j; k < indexLookup.get(baseKey.toString()); k++) {
				ItemSet singleton = prevRound[k]; // XXX no longer a singleton
				//String newTerm = singleton.getKey();
				String tmpItems[] = singleton.getItems();
				if (tmpItems.length <= roundnum - 2) {
					logger.fatal("About to blow up on " + singleton.getKey());
				}
				String newTerm = tmpItems[roundnum - 2];
				if (0 == (++this_set_number % debug_step)) {
					Object a[] = {
							this_set_number,
							out_of,
							Arrays.deepToString(oldterms),
							newTerm
					};
					logger.debug(String.format("On item %d of %d (%s + %s)",a));
					if (logger.isDebugEnabled()) {
						Runtime r = Runtime.getRuntime();
						long free = r.freeMemory();
						logger.debug("Currently using " +
								(r.totalMemory() - free) + " bytes(?) -- free " + free
						);
					}
				}

				boolean flag=false;
				String term;
				if (roundnum > 2) { // XXX this is right for 3, but not sufficient after that
					for (int i=1;i<oldterms.length;i++){
						term=oldterms[i]+" "+newTerm;
						if(!itemCache.containsKey(term)){
							flag= true;
							break;
						}
					}
					if(flag){
						skipCount++;
						continue;
					}

				}
				ItemSet combined = baseSet.intersect(singleton, isFinal);
				if (null != combined && combined.getSupport() >= minsupport) {
					itemCache.put(combined.getKey(), combined);
					if (roundnum != combined.getItems().length) {
						logger.error("Somehow got " + combined.getKey() + " from "  + baseSet.getKey() + " and " + singleton.getKey());
					}
					thisRound.add(combined);
				}
			}
			indexLookup.put(baseSet.getKey(), thisRound.size());
		}
		logger.debug("Count for short circuit 3 sets is "+ skipCount);
		logger.debug("Actually evaluated " + this_set_number + " itemsets");
		return thisRound;
	}

	/**
	 * @param item
	 * @return all the itemsets in foundSets that have the given string as one of their terms.
	 */
	public ItemSet[] getByMember(String item) {
		ArrayList<ItemSet> t = new ArrayList<ItemSet>();
		for (int i = 0; i < foundSets.length; i++) {
			String[] items = foundSets[i].getItems();
			for (int j = 0; j  < items.length; j++) {
				if (item.equals(items[j])) {
					t.add(foundSets[i]);
					break; // continues outer loop
				}
			}
		}
		return t.toArray(new ItemSet[0]);
	}

	/**
	 * Turn an unsorted array of strings into a canonical key.
	 * @param subkeys
	 * @return
	 */
	static protected String makeKey(String[] subkeys) {
		if (1 == subkeys.length) return subkeys[0]; // common case short-circuit
		String tmp[] = subkeys.clone();
		Arrays.sort(tmp);
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < tmp.length; i++) {
			if (0 < i) b.append(' ');
			b.append(tmp[i]);
		}
		return b.toString();
	}


	public ArrayList<Inference> calInference(ItemSet[] itemList, double minConf){
		ArrayList<Inference> inferenceList = new ArrayList<Inference>();

		for(int i = 0;i < itemList.length; i++){
			String list[]=itemList[i].getItems();
			if(list.length<=1){
				continue;
			}
			for(int j = 0; j< list.length;j++){
				Inference infer = new Inference();
				try{
					if(list[j]==null){
						continue;
					}
					infer.consequent = itemCache.get(list[j]);
				}
				catch(Exception e){
					e.printStackTrace();
					logger.debug("error in getItemSetObject");
					continue;
				}
				infer.support=itemList[i].support;

				String antecedent=null,total=null;

				for(int k = 0; k< list.length;k++){
					if(list[k]==null){
						continue;
					}
					if(!list[j].equalsIgnoreCase(list[k])){
						if(antecedent==null)
							antecedent= list[k];
						else
							antecedent+=" "+ list[k];
					}
					if(total==null)
						total=list[k];
					else
						total+=" "+list[k];

				}
				try{
					infer.antecedent = itemCache.get(antecedent);
					infer.total = itemCache.get(total);
				}
				catch(Exception e){
					e.printStackTrace();
					logger.debug("error in second section of getItemSetObject");
					continue;
				}

				if((infer.conf = infer.getConfidence())>=minConf){

					inferenceList.add(infer);
				}

			}
		}

		return inferenceList;
	}

	@SuppressWarnings("unchecked")
	public void printInferredRules(ArrayList<Inference> list){

		Collections.sort(list);
		for(Iterator<Inference> i = list.iterator();i.hasNext();){
			Inference infer = i.next();
			//logger.debug
			Object a[] = {
					Arrays.deepToString(infer.antecedent.getItems()),
					Arrays.deepToString(infer.consequent.getItems()),
					infer.conf * 100,
					infer.support * 100
			};
			System.out.println(String.format("%s => %s (Conf: %.2f%%, Supp: %.2f%%)", a));
//			System.out.println("["+infer.antecedent.getKey()+"]=>["+infer.consequent.getKey()+"] (Conf : "+infer.conf+", Supp : "+infer.support+")");
		}

	}

	public void dumpIndex(File outFile) throws FileNotFoundException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFile));

		try {
			out.write(this.name);
			out.write(' ');
			out.write(Integer.toString(this.totalDocs));
			out.write('\n');
			logger.debug("Preparing to dump " + this.singletonSets.size() + " itemSets");
			int c = 0;
			for (ItemSet i : this.singletonSets) {
				if (++c % 1000 == 0) logger.debug("On itemSet " + c + ": " + i.getKey());
				i.dumpTo(out);
			}
			logger.debug("Finished with " + c + " itemSets");
			out.close();
		} catch (IOException e) {
			logger.fatal("Caught IOException dumping out index", e);
			throw new RuntimeException(e);
		}


	}
	public class ItemSet implements Comparable {
		protected String[] items;
		private double support;
		private String concatenated = null; // may as well cache this

		public ItemSet(String[] items, double support) {
			super();
			Arrays.sort(items);
			this.items = items;
			this.support = support;
		}

		protected StringBuffer lineSerialize() {
			StringBuffer b = new StringBuffer();
			b.append(this.getKey());
			b.append(':');
			b.append(Double.toString(this.getSupport()));
			return b;
		}
		public void dumpTo(OutputStreamWriter out) throws IOException {
			out.write(this.lineSerialize().toString());
			out.write('\n');
		}

		public ItemSet intersect(ItemSet singleton, boolean finalRound) {
			String terms[] = this.intersectionTerms(singleton);
			double supp = DataMine.this.findSupport(terms);
			return new ItemSet(terms,supp);
		}

		public ItemSet(String word, double d) {
			super();
			String tmp[] = {word};
			this.items = tmp;
			this.support = d;
		}

		public double getSupport() { return support; }
		public String[] getItems() { return items; }
		public int getSize()       { return items.length; }
		public String getKey() {
			if (null == concatenated) {
				concatenated = makeKey(this.items);
			}
			return concatenated;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		/* in case we want to sort an array of these, for example */
		public int compareTo(Object o) {
			ItemSet other = (ItemSet) o;
			int firstcomp = Double.compare( other.support, support );
			if ( 0 != firstcomp ) return firstcomp;
			else return this.getKey().compareTo(other.getKey());
		}

		/**
		 * Add the final term of the other itemset to the end of this list
		 * for this itemset.  Assumes correct implementation of the a priori
		 * algorithm.
		 * @param other
		 * @return
		 */
		protected String[] intersectionTerms(ItemSet other) {
			String histerms[] = other.getItems();
			String terms[] = Arrays.copyOf(this.items, this.items.length + 1);
			terms[this.items.length] = histerms[histerms.length - 1];
			return terms;
		}


	}

	public class Inference implements Comparable{
		protected ItemSet antecedent, consequent, total;
		protected DataMine mine;
		public Double conf, support;

		public Inference() {
			mine = DataMine.this;
		}


		public double getConfidence() {
			return total.getSupport()/antecedent.getSupport();
		}

		public int compareTo(Object o) {
			Inference other = (Inference) o;
			int firstcomp = Double.compare(other.conf, this.conf);
			if ( 0 != firstcomp ) return firstcomp;
			else return other.support.compareTo(this.support);
		}

	}


	protected class ItemSetIterator implements Iterator<String> {
		private Iterator<ItemSet> setlist;

		public ItemSetIterator(ArrayList<ItemSet> items) {
			setlist = items.iterator();
		}
		public boolean hasNext() {
			return setlist.hasNext();
		}

		public String next() {
			ItemSet i = setlist.next();
			String [] tmp = i.getItems();
			return tmp[0];
		}

		public void remove() {
			setlist.remove();
		}

	}


	public void destroy() {
		// cleanup here if needed
	}
	protected void finalize() throws Throwable {
		this.destroy();
		super.finalize();
	}





	class ItemBitSet extends ItemSet {
		protected BitSet transactionList;

		/* (non-Javadoc)
		 * @see coms6111.astbbw.dataminer.DataMine.ItemSet#lineSerialize()
		 */
		@Override
		protected StringBuffer lineSerialize() {
			StringBuffer b = super.lineSerialize();
			b = appendDocIds(b);
			return b;
		}

		protected StringBuffer appendDocIds(StringBuffer in) {
			BitSet set = this.transactionList;
			for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i+1)) {
				in.append(' ');
				in.append( Integer.toString(i) );
			}
			return in;
		}

		public ItemBitSet(String[] items, double support) {
			super(items, support);
		}

		public ItemBitSet(String word, double d) {
			super(word, d);
		}

		public ItemBitSet(String[] items, Integer docids[]) {
			super(items, (float) docids.length / DataMine.this.totalDocs);
			transactionList = new BitSet();
			for (int doc : docids) transactionList.set(doc);
		}
		public ItemBitSet(String[] items, BitSet index) {
			super(items, (double) index.cardinality()/ DataMine.this.totalDocs);
			this.transactionList = index;
		}

		public ItemSet intersect(ItemSet other_in, boolean finalRound) {
			ItemBitSet other;
			if ( !(other_in instanceof ItemBitSet)) { // unnecessary type-safety?
				DataMine.this.logger.warn("Found that branch that Ben thought wouldn't happen");
				return super.intersect(other_in, finalRound);
			} else {
				other = (ItemBitSet) other_in;
			}
			String[] terms = intersectionTerms(other);
			String key = makeKey(terms);
			if (DataMine.this.itemCache.containsKey(key)) {
				return DataMine.this.itemCache.get(key);
			} else {
				double newSupport;
				BitSet newBits = (BitSet) this.transactionList.clone();
				newBits.and(other.transactionList);
				newSupport = (double) newBits.cardinality() / totalDocs ;
				if (newSupport < minSupport) return null;
				return finalRound
				? new ItemSet(terms, newSupport)
				: new ItemBitSet(terms, newBits);
			}
		}

	}


	class ItemSetInt extends ItemBitSet {
		protected int[] transList;


		/* (non-Javadoc)
		 * @see coms6111.astbbw.dataminer.GlimpseBitIndexed.ItemBitSet#appendDocIds(java.lang.StringBuffer)
		 */
		@Override
		protected StringBuffer appendDocIds(StringBuffer in) {
			for (int i : this.transList) {
				in.append(' ');
				in.append( Integer.toString(i) );
			}
			return in;
		}

		public ItemSetInt(String[] items, double support) {
			super(items, support);
			throw new UnsupportedOperationException("This constructor should not be called");
		}

		public ItemSetInt(String word, double d) {
			super(word, d);
			throw new UnsupportedOperationException("This constructor should not be called");
		}

		public ItemSetInt(String[] items, Integer docids[]) {
			super(items, (float) docids.length / DataMine.this.totalDocs);

			transList = new int[docids.length];
			for (int i=0 ;i< docids.length;i++) transList[i]= docids[i];
		}



		public ItemSet intersect(ItemSet other_in,  boolean finalRound) {
			ItemSetInt other;
			if ( !(other_in instanceof ItemSetInt)) { // unnecessary type-safety?
				DataMine.this.logger.warn("Found that branch that Ben thought wouldn't happen");
				return super.intersect(other_in, finalRound);
			} else {
				other = (ItemSetInt) other_in;
			}

			int temp[] = new int[transList.length];
			int i = this.transList.length - 1;
			int j = other.transList.length - 1;
			int found = 0;
			int needed = minDocs - 1;
			while( 0 <= i  && 0 <= j )
			{
				int my_doc = this.transList[i];
				int other_doc = other.transList[j];
				if(my_doc == other_doc){
					temp[found++] = my_doc;
					needed--;
					i--;
					j--;
				}
				else if(my_doc < other_doc) j--;
				else i--;
				if ( i < needed || j < needed) break; /* bail out! */
			}
			if (found < minDocs) return null;
			/* oh right, the reversal... */
			Integer fixed[] = new Integer[found];
			if (found > 0)  {
				for (int k = 0; k <= found/2; k++) {
					int swapidx = found - k -1;
					fixed[k] = temp[swapidx];
					fixed[swapidx] = temp[k];
				}
			}
			return new ItemSetInt(intersectionTerms(other), fixed);

		}

	}


}
