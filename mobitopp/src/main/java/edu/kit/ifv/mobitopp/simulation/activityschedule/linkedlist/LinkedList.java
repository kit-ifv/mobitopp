package edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist;

public interface LinkedList {


	public LinkedListElement first();
	public LinkedListElement last();

	public LinkedListElement next(LinkedListElement elem);
	public LinkedListElement prev(LinkedListElement elem);

	public void addToBack(LinkedListElement elem);
	public void addToFront(LinkedListElement elem);
	public void insertAfter(LinkedListElement current, LinkedListElement elem);
	public void remove(LinkedListElement elem);


}
