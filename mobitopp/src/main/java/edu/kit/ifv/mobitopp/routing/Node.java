package edu.kit.ifv.mobitopp.routing;

import java.lang.Comparable;

public interface Node 
	extends Comparable<Node>
{

	Edge[] incomingEdges();
	Edge[] outgoingEdges();

	String id();

	boolean isSink();
	boolean isTarget();

}
