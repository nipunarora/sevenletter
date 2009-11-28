package seven.g1;

import java.util.HashMap;
import java.util.Map;

public class CountMap<T> extends HashMap<T, Integer> {

	/**
	 * Maintains a running total of all counts.
	 */
	private int total = 0;

	public CountMap() {
		super();
	}

	public CountMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CountMap(int initialCapacity) {
		super(initialCapacity);
	}

	public CountMap(Map<? extends T, ? extends Integer> m) {
		super(m);
	}

	/**
	 * Increment the count for this key (starting from 0 if not present).
	 * @param key the key to increment the count for
	 * @return the new count for this key
	 */
	public int increment(T key) {
		int current = 0;
		if (containsKey(key)) {
			current = get(key);
		}
		total++;
		current++;
		put(key,current);
		return current;
	}

	/**
	 * Decrement the count for this key (starting from 0 if not present)
	 * @param key the key to increment the count for
	 * @return the new count for this key
	 */
	public int decrement(T key) {
		int current = 0;
		if (containsKey(key)) {
			current = get(key);
		}
		current--;
		total--;
		put(key,current);
		return current;
	}

	/**
	 * Retrieve the current count for this key
	 * @param key a valid key, not necessarily present
	 * @return the current count, defaulting to 0
	 */
	public int count(T key) {
		if (containsKey(key)) {
			return get(key);
		} else {
			return 0;
		}
	}

	/**
	 * Retrieve the sum of all increments and decrements.  This should be the sum of all counts
	 * in the hash, provided you have not circumvented the API in obnoxious ways.
	 * @return the sum of all counts in this map, if you're behaving well.
	 */
	public int countSum() {
		return total;
	}
}
