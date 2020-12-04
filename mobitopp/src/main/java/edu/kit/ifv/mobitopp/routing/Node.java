package edu.kit.ifv.mobitopp.routing;

public interface Node 
	extends Comparable<Node>
{

	Edge[] incomingEdges();
	Edge[] outgoingEdges();

	String id();

	boolean isSink();
	boolean isTarget();

}
