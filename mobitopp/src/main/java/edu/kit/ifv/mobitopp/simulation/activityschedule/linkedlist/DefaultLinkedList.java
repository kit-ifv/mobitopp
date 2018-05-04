package edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.simulation.TripIfc;

public class DefaultLinkedList 
	implements Serializable 
	{

	private static final long serialVersionUID = 2470575336632721989L;
	
	protected LinkedListElement head;
	protected LinkedListElement tail;

	public DefaultLinkedList() {
		head = new DefaultLinkedListElement();
		tail = new DefaultLinkedListElement();
		head.setNext(tail);
		tail.setPrev(head);
	}

	public LinkedListElement first() {
		return head.next() != tail ? head.next() : null;
	}

	public LinkedListElement last() {
		return tail.prev() != head ? tail.prev() : null;
	}

	public LinkedListElement next(LinkedListElement elem) {
		return elem.next() != tail ? elem.next() : null;
	}

	public LinkedListElement prev(LinkedListElement elem) {
		return elem.prev() != head ? elem.prev() : null;
	}

	public void addToBack(LinkedListElement elem) {
		LinkedListElement last = tail.prev();
		last.setNext(elem);
		elem.setPrev(last);
		elem.setNext(tail);
		tail.setPrev(elem);
	}

	public void addToFront(LinkedListElement elem) {
		LinkedListElement first = head.next();
		elem.setNext(first);
		elem.setPrev(head);
		first.setPrev(elem);
		head.setNext(elem);
	}

	public void insertAfter(LinkedListElement current, LinkedListElement elem) {
	
		assert current != elem;
	
		LinkedListElement next = current.next();
	
		assert current != next;
		assert elem != next;
	
		current.setNext(elem);
		next.setPrev(elem);
		elem.setPrev(current);
		elem.setNext(next);
	}

	public void remove(LinkedListElement elem) {
		LinkedListElement prev = elem.prev();
		LinkedListElement next = elem.next();
	
		prev.setNext(next);
		next.setPrev(prev);
		
		elem.setNext(null);
		elem.setPrev(null);
	}

	public boolean isEmpty() {
	
		return head.next() == tail;
	}

	protected LinkedListElement nextActivity(LinkedListElement activity) {
	
		LinkedListElement elem = activity.next();
	
		if (elem instanceof TripIfc) {
			elem = elem.next();
		}
	
		assert !(elem instanceof TripIfc);
	
		return elem;
	}

}