package edu.kit.ifv.mobitopp.routing.util;

import java.util.HashMap;

public class PriorityQueueBasedPQ<T extends Comparable<T>>
	implements PriorityQueue<T> {

	private HashMap<T, PQElement<T>> mapping = new HashMap<T, PQElement<T>>();
	private java.util.PriorityQueue<PQElement<T>> data = new java.util.PriorityQueue<PQElement<T>>();


	public void clear() {

		mapping = new HashMap<T, PQElement<T>>();
		data = new java.util.PriorityQueue<PQElement<T>>();
	}

	public PriorityQueueBasedPQ<T> createNew() {
		return new PriorityQueueBasedPQ<T>();
	}

	public void add(T object, Float priority) {

		if (mapping.containsKey(object)) {

			decreaseKey(object, priority);
		} else {

			PQElement<T> element = new PQElement<T>(object, priority);

			mapping.put(object, element);
			data.add(element);
		}
	}

	public T minElement() {

		PQElement<T> element = data.peek();

		return element.data;
	}
	
	public Float minPriority() {

		PQElement<T> element = data.peek();
		return element.priority;
	}

	public void decreaseKey(T object, Float priority) {

		PQElement<T> toBeAdded = new PQElement<T>(object, priority);
		PQElement<T> toBeRemoved = mapping.get(object);

		if (toBeRemoved != null) {
			mapping.remove(toBeRemoved.data);
			data.remove(toBeRemoved);
		} else {
			System.out.println("Warning: PriorityQueue does not contain element " + object);
		}

		mapping.put(object, toBeAdded);
		data.add(toBeAdded);
	}

	public T deleteMin() {

		PQElement<T> toBeRemoved = data.peek();

		assert toBeRemoved != null;

		mapping.remove(toBeRemoved.data);
		data.remove(toBeRemoved);

		return toBeRemoved.data;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public boolean contains(T object) {
		return mapping.containsKey(object);
	}

	public Float priority(T object) {

		PQElement<T> element = mapping.get(object);
		return element.priority;
	}

	public int size() {

		assert data.size() == mapping.size();

		return data.size();
	}

}
