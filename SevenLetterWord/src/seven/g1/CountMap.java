package seven.g1;

import java.util.HashMap;
import java.util.Map;

public class CountMap<T> extends HashMap<T, Integer> {

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

	public int increment(T key) {
		int current = 0;
		if (containsKey(key)) {
			current = get(key);
		}
		current++;
		put(key,current);
		return current;
	}

	public int decrement(T key) {
		int current = 0;
		if (containsKey(key)) {
			current = get(key);
		}
		current--;
		put(key,current);
		return current;
	}

	public int count(T key) {
		if (containsKey(key)) {
			return get(key);
		} else {
			return 0;
		}
	}
}
