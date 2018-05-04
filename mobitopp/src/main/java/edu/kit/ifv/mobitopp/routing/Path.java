package edu.kit.ifv.mobitopp.routing;

public interface Path {

	Node from();
	Node to();

	Link first();
	Link get(int idx);

	Link next(Link link);

	int size();
	Float length();

	boolean isEmpty();
	boolean isValid();

	float travelTime();

}
