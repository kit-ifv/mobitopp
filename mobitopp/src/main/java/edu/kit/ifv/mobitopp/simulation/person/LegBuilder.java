package edu.kit.ifv.mobitopp.simulation.person;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;

public class LegBuilder {

	private final List<Connection> connections;
	
	public LegBuilder() {
		connections = new ArrayList<>();
	}
}
