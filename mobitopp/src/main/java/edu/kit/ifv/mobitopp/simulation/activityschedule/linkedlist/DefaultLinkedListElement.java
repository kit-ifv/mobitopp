package edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist;

import java.io.Serializable;

class DefaultLinkedListElement
	implements LinkedListElement
	, Serializable
{

	private static final long serialVersionUID = 1L;

	private LinkedListElement next;
	private LinkedListElement prev;

	public LinkedListElement next() {	
		return next;
	}
	public LinkedListElement prev() {
		return prev;
	}

	public void setNext(LinkedListElement next) {
		assert next != this;
		this.next = next;
	}


	public void setPrev(LinkedListElement prev) {
		assert prev != this;
		this.prev = prev;
	}

}
