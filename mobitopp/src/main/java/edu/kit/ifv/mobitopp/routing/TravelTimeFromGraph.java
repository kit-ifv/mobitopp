package edu.kit.ifv.mobitopp.routing;

public class  TravelTimeFromGraph
	implements TravelTime 
{

	private Graph graph;

	public TravelTimeFromGraph(Graph graph) {
		this.graph = graph;
	}


	public float travelTime(Edge e, float currentTime) {
		return graph.cost(e);
	}

}
