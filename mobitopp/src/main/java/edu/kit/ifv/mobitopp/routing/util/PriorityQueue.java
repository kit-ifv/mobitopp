package edu.kit.ifv.mobitopp.routing.util;

public interface PriorityQueue<T> {

	public void add(T object, Float priority);

	public T minElement();
	public Float minPriority();

	public void decreaseKey(T object, Float priority);

	public T deleteMin();

	public void clear();

	public boolean isEmpty();

	public int size();

	public PriorityQueue<T> createNew();

	public boolean contains(T object);

}
