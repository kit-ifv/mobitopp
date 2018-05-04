package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.HashSet;
import java.util.Set;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;

class DefaultUsedJourneys implements UsedJourneys {

	private final Set<Journey> used;

	public DefaultUsedJourneys() {
		super();
		used = new HashSet<>();
	}

	@Override
	public boolean used(Journey journey) {
		return used.contains(journey);
	}

	@Override
	public void use(Journey journey) {
		used.add(journey);
	}
}