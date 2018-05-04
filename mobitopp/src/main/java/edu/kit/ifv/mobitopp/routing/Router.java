package edu.kit.ifv.mobitopp.routing;

interface Router {

	Path shortestPath(Graph g, Node source, Node target);


}
