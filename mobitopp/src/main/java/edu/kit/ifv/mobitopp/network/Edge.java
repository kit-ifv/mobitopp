package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Line2D;

public interface Edge {

	public int id();	

	public Node from();
	public Node to();

	public Edge twin();

	public Line2D line();
}
