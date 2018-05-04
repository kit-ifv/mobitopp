package edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist;

public interface LinkedListElement {

	public LinkedListElement next();
	public LinkedListElement prev();

	public void setNext(LinkedListElement next);
	public void setPrev(LinkedListElement prev);

}
