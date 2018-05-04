package edu.kit.ifv.mobitopp.routing.util;

class PQElement<T extends Comparable<T>> implements Comparable<PQElement<T>> {

	public final Float priority;
	public final T data;

	public PQElement(T data, Float priority) {
		this.data = data;
		this.priority = priority;
	}

	public int compareTo(PQElement<T> o) {

		if (priority < o.priority) {
			return -1;
		}
		if (priority > o.priority) {
			return 1;
		}

		return data.compareTo(o.data);
	}

	public String toString() {
		return "(" + data + "," + priority + ")";
	}

}