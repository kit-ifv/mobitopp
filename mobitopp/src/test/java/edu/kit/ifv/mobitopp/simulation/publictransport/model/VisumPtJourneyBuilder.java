package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumJourneySection;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLineRoute;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumTimeProfile;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;

public class VisumPtJourneyBuilder {

	private static final int defaultToProfileIndex = 0;
	private static final int defaultFromProfileIndex = 0;
	private static final int defaultDeparture = 0;
	private static final String defaultName = "default";
	private static final int defaultId = 0;
	private static final VisumPtLineRoute defaultRoute = visumLineRoute().build();
	private static final VisumPtTimeProfile defaultProfile = visumTimeProfile().build();
	private int id;
	private final String name;
	private int departure;
	private VisumPtLineRoute route;
	private VisumPtTimeProfile timeProfile;
	private int fromProfileIndex;
	private int toProfileIndex;
	private final List<VisumPtVehicleJourneySection> sections;

	public VisumPtJourneyBuilder() {
		super();
		id = defaultId;
		name = defaultName;
		departure = defaultDeparture;
		route = defaultRoute;
		timeProfile = defaultProfile;
		fromProfileIndex = defaultFromProfileIndex;
		toProfileIndex = defaultToProfileIndex;
		sections = new ArrayList<>();
	}

	public VisumPtVehicleJourney build() {
		return new VisumPtVehicleJourney(id, name, departure, route, timeProfile, fromProfileIndex,
				toProfileIndex, sections);
	}

	public VisumPtJourneyBuilder uses(VisumTransportSystem transportSystem) {
		route = visumLineRoute().uses(transportSystem).build();
		return this;
	}

	public VisumPtJourneyBuilder servedBy(VisumVehicleCombination combination) {
		sections.add(visumJourneySection().servedBy(combination).build());
		return this;
	}

	public VisumPtJourneyBuilder sections(List<VisumPtVehicleJourneySection> sections) {
		this.sections.clear();
		this.sections.addAll(sections);
		return this;
	}

	public VisumPtJourneyBuilder takes(VisumPtLineRoute route) {
		this.route = route;
		return this;
	}

	public VisumPtJourneyBuilder takes(VisumPtTimeProfile timeProfile) {
		this.timeProfile = timeProfile;
		return this;
	}

	public VisumPtJourneyBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumPtJourneyBuilder departsAt(int departure) {
		this.departure = departure;
		return this;
	}

	public VisumPtJourneyBuilder startsAt(int fromProfileIndex) {
		this.fromProfileIndex = fromProfileIndex;
		return this;
	}

	public VisumPtJourneyBuilder endsAt(int toProfileIndex) {
		this.toProfileIndex = toProfileIndex;
		return this;
	}

}
