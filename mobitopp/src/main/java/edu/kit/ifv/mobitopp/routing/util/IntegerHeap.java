package edu.kit.ifv.mobitopp.routing.util;

import java.util.Arrays;
import java.util.Map;

/**
 * Heap takes arbitrary objects of type {@code T}, but works internally with {@link Integer}. A
 * continuous id is attachd to each object. Arrays and {@link Map}s are used to translate between
 * internal id and object.
 */
public class IntegerHeap
{


	protected static final class Entry {

		private final Integer value;
		private float priority; 


		private void setPriority(float newPriority) {
			this.priority = newPriority;
		}


		private Entry(Integer value, float priority) {

			this.value = value;
			this.priority = priority;
		}

		public String toString() {
			return "(" + value + "," + priority + ")";
		}


	}

	protected int size;

	protected Entry[] heap;
	protected Integer[] position = new Integer[32];


	public IntegerHeap() {
		clear();
	}

	public void clear() {
		this.size = 0;
		this.heap = new Entry[0];
		Arrays.fill(this.position, -1);
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public int size() {
		return this.size;
	}

	protected int left(int i) {
		return 2*i+1;
	}

	protected int right(int i) {
		return 2*i+2;
	}

	protected int parent(int i) {
		return (i-1)/2;
	}


	protected boolean isEmpty(int i) {

		return i >= size;
	}

	protected boolean isLeaf(int i) {

		return isEmpty(left(i)) && isEmpty(right(i));
	}


	protected boolean isHeap(int i) {

		if (isEmpty(i)) return true;
		if (isLeaf(i)) return true;

		return (isEmpty(left(i)) || (isHeap(left(i)) && (priority(left(i)) >= priority(i)) ))
				&& (isEmpty(right(i)) || (isHeap(right(i)) && priority(right(i)) >= priority(i)));
	}

	protected float priority(int i) {

		assert !isEmpty(i);

		return heap[i].priority;
	}

	protected void swap(int i, int j) {
		assert i < size;
		assert j < size;

		Entry tmp = heap[i];
		heap[i] = heap[j];
		heap[j] = tmp;

		position[heap[i].value] = i;
		position[heap[j].value] = j;
	}


	protected void decrease(int i, float newPrio) {

		assert newPrio < priority(i): (newPrio +  ", " + priority(i));
		assert isHeap(i);

		heap[i].setPriority(newPrio);

		while (i>0 && priority(i) < priority(parent(i))) {
			swap(i,parent(i));
			i = parent(i);
		}
	}

	
	protected void heapify(int i) {

		assert(isHeap(left(i)));
		assert(isHeap(right(i)));

		int min = i;

		if (!isEmpty(left(i)) && priority(left(i)) < priority(min)) {
			min = left(i);
		}
		if (!isEmpty(right(i)) && priority(right(i)) < priority(min)) {
			min = right(i);
		}
		if (min != i) {
			swap(i, min);
			heapify(min);
		}

	}


	public void add(Integer object, Float priority) {
		if (this.size == heap.length) {

			heap = Arrays.copyOf(heap, 2*heap.length+1);
		}

		if (object >= position.length) {

			position = Arrays.copyOf(position, Math.max(2*position.length,object+1));

			if (object >= position.length) {
				throw new AssertionError("object >= position.length: " + object + ", " + position.length);
			}
		}

		Entry entry = new Entry(object, Float.MAX_VALUE);

		heap[this.size] = entry;
		position[object] = this.size;

		this.size++;

		decrease(this.size-1, priority);
	}

	public Float minPriority() {

		Entry entry = heap[0];

		return entry.priority;
	}


	public Integer minElement() {

		Entry entry = heap[0];

		return entry.value;
	}

	public Integer deleteMin() {

		Entry first = heap[0];

		heap[0] = heap[this.size-1];
		heap[this.size-1] = null;

		this.size--;

		heapify(0);


		position[first.value] = -1;

		return first.value;
	}

	public void decreaseKey(Integer object, Float newPrio) {

		Integer pos = findPosition(object);

		decrease(pos, newPrio);
	}

	protected Integer findPosition(Integer object) {
		assert position.length > object;

		return position[object];
	}

	protected boolean contains(Integer object) {

		return findPosition(object) > -1;
	}


}

