package edu.kit.ifv.mobitopp.routing;

public interface Link 
	extends Edge
{

	Node from();
	Node to();

	float distance();

}
