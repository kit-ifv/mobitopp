package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class VisumPtLine 
	implements Serializable
{

	public final String name;

	public final VisumTransportSystem transportSystem;

	private List<VisumPtLineRoute> routes;

	public VisumPtLine (
		String name,
		VisumTransportSystem transportSystem
	) {
		this.name = name;
		this.transportSystem = transportSystem;

		this.routes = Collections.unmodifiableList(new ArrayList<VisumPtLineRoute>());
	}

	public void setLineRoutes(List<VisumPtLineRoute> routes) {
		this.routes = Collections.unmodifiableList(routes);
	}

	public List<VisumPtLineRoute> getRoutes() {
		return routes;
	}

	public String toString() {

		return "VisumPtLine("
						+ name + ","
						+ transportSystem.code + "-"
						+ transportSystem.name + "-"
						+ transportSystem.type 
						+ "#routes=" + routes.size()
						+ ")";
	}

}
