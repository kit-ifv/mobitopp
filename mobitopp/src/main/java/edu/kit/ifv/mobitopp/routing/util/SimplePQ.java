package edu.kit.ifv.mobitopp.routing.util;

import java.util.HashMap;

public class SimplePQ<T>
	implements PriorityQueue<T> {

	private HashMap<T, Float> data = new HashMap<T, Float>();


	public SimplePQ<T> createNew() {
		return new SimplePQ<T>();
	}


	public void clear() {

		data = new HashMap<T, Float>();
	}


	public void add(T object, Float priority) {

		data.put(object, priority);
	}

	public T minElement() {


		Float min_prio = Float.MAX_VALUE;
		T min = null;

		for (T tmp : data.keySet()) {

			Float prio = data.get(tmp);

			if (prio < min_prio) {

				min_prio = prio;
				min = tmp;
			}
		}

		return min;
	}
	
	public Float minPriority() {

		return data.get(minElement());
	}

	public void decreaseKey(T object, Float priority) {

		data.put(object, priority);
	}


	public T deleteMin() {

		T object = minElement();

		data.remove(object);

		return object;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public boolean contains(T object) {
		return data.containsKey(object);
	}

	public Float priority(T object) {
		return data.get(object);
	}

	public int size() {
		return data.size();
	}

}
