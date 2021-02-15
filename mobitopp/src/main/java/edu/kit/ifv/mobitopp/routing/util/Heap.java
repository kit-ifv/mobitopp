package edu.kit.ifv.mobitopp.routing.util;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;


/**
 * Heap takes arbitrary objects of type {@code T}, but works internally with {@link Integer}. A
 * continuous id is attachd to each object. Arrays and {@link Map}s are used to translate between
 * internal id and object.
 */
@Slf4j
public class Heap<T> 
	implements PriorityQueue<T>
{

	protected IntegerHeap heap;


	protected Object[] objects;
	protected Map<T,Integer> ids;

	protected int nextId = 0;

	protected final int initialSize;

	public Heap(int size) {
		this.initialSize = size;
		this.objects = new Object[initialSize];
		this.ids = new HashMap<T,Integer>(initialSize);
		clear();
	}

	public Heap<T> createNew() {
		return new Heap<T>(this.initialSize);
	}

	public void clear() {
		heap = new IntegerHeap();
	}

	public boolean isEmpty() {
		return heap.isEmpty();
	}

	public int size() {
		return heap.size();
	}

	protected Integer getId(T object) {

		Integer id = ids.get(object);

		if (id == null) {

			if (nextId >= objects.length) {
				objects = Arrays.copyOf(objects, 2*objects.length);
			}

			id = nextId;
			nextId++;

			objects[id] = object;
			ids.put(object, id);
		}

		return id;
	}

	public void add(T object, Float priority) {
		Integer id = getId(object);

		heap.add(id, priority);
	}

	public Float minPriority() {

		return heap.minPriority();
	}

	@SuppressWarnings("unchecked")
	public T minElement() {
		Integer id = heap.minElement();
		return (T) objects[id];
	}

	@SuppressWarnings("unchecked")
	public T deleteMin() {
		Integer id = heap.deleteMin();
		return (T) objects[id];
	}

	public void decreaseKey(T object, Float newPrio) {
		verifyId(object);

		Integer id = getId(object);

		heap.decreaseKey(id, newPrio);
	}

	private void verifyId(T object) {
		if (ids.containsKey(object)) {
			return;
		}
		throw warn(new IllegalArgumentException(object + ", " + System.lineSeparator() + mappingAsString()), log);
	}

	private String mappingAsString() {
		StringBuilder buf = new StringBuilder();
		for(T key : ids.keySet()) {
			Integer val = ids.get(key);
			buf.append(key + ":" + val + ", ");
		}
		return buf.toString();
	}
	
	public boolean contains(T object) {

		return heap.contains(getId(object));
	}


}

