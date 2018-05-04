package edu.kit.ifv.mobitopp.visum;

import java.util.Collections;
import java.util.List;

public class VisumPtVehicleJourney {

	public final int id;
	public final String name;
	public final int departure;

	public final VisumPtLineRoute route;
	public final VisumPtTimeProfile timeProfile;
	public final int fromProfileIndex;
	public final int toProfileIndex;

	public final List<VisumPtVehicleJourneySection> sections;


	public VisumPtVehicleJourney(
		int id,
		String name,
		int departure,
		VisumPtLineRoute route,
		VisumPtTimeProfile timeProfile,
		int fromProfileIndex,
		int toProfileIndex,
		List<VisumPtVehicleJourneySection> sections
	) {
		this.id = id;
		this.name = name;
		this.departure = departure;
		this.route = route;
		this.timeProfile = timeProfile;
		this.fromProfileIndex = fromProfileIndex;
		this.toProfileIndex = toProfileIndex;

		this.sections = Collections.unmodifiableList(sections);
	}


	public String toString() {

		return "VisumPtVehicleJourney("
							+ id + ","
							+ name + ","
							+ departure + ","
							+ route + ","
							+ timeProfile + ","
							+ fromProfileIndex + ","
							+ toProfileIndex + ")";
	}

}
