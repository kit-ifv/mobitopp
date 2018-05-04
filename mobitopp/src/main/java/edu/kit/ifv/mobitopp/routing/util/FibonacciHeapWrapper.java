package edu.kit.ifv.mobitopp.routing.util;

import java.util.HashMap;


public class FibonacciHeapWrapper<T>
	implements PriorityQueue<T>
{

	private FibonacciHeap<T> heap;
	private HashMap<T,FibonacciHeap.Entry<T>> enqueued = new HashMap<T,FibonacciHeap.Entry<T>>();

	public FibonacciHeapWrapper() {
		clear();
	}

	public FibonacciHeapWrapper<T> createNew() {
		return new FibonacciHeapWrapper<T>();
	}

	public void clear() {
		heap = new FibonacciHeap<T>();
	}

	public void add(T value, Float priority) {  
    FibonacciHeap.Entry<T> e = heap.enqueue(value, priority.doubleValue());
		enqueued.put(value, e);	
	}

	public T minElement() {
		return heap.min().getValue();
	}

	public Float minPriority() {
		return (float) heap.min().getPriority();
	}


	public T deleteMin() {
		FibonacciHeap.Entry<T> e = heap.dequeueMin();
		enqueued.remove(e.getValue());
		return e.getValue();
	}


	public boolean isEmpty() {
		return heap.isEmpty();
	}

	public int size() {
		return heap.size();
	}

	public void decreaseKey(T value, Float priority) {
		FibonacciHeap.Entry<T> e =  enqueued.get(value);
	
		heap.decreaseKey(e, priority.doubleValue());
	}

	public boolean contains(T object) {
		return enqueued.containsKey(object);
	}

}
