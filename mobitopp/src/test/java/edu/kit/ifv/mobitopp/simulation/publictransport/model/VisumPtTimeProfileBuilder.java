package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLineRoute;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfileElement;

public class VisumPtTimeProfileBuilder {

	private static final String defaultProfileId = "default profile id";
  private static final String defaultName = "default time profile";
	private static final VisumPtLineRoute defaultRoute = visumLineRoute().build();

	private String profileId;
	private String name;
	private final VisumPtLineRoute route;
	private final Map<Integer, VisumPtTimeProfileElement> elements;

	public VisumPtTimeProfileBuilder() {
		super();
		profileId = defaultProfileId;
		name = defaultName;
		route = defaultRoute;
		elements = new HashMap<>();
	}

	public VisumPtTimeProfile build() {
		return new VisumPtTimeProfile(profileId, name, route, elements);
	}

	public VisumPtTimeProfileBuilder withName(String name) {
		this.name = name;
		return this;
	}

}
