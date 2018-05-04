package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumVehicleCombination;

import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;

public class VisumPtVehicleJourneySectionBuilder {

	private static final int defaultId = 0;
	private static final int defaultJourneyId = 0;
	private static final int defaultFrom = 0;
	private static final int defaultTo = 0;
	private static final int defaultDay = 0;
	private static final VisumVehicleCombination defaultCombination = visumVehicleCombination()
			.build();
	private final int id;
	private final int journeyId;
	private final int from;
	private final int to;
	private final int day;
	private VisumVehicleCombination combination;

	public VisumPtVehicleJourneySectionBuilder() {
		super();
		id = defaultId;
		journeyId = defaultJourneyId;
		from = defaultFrom;
		to = defaultTo;
		day = defaultDay;
		combination = defaultCombination;
	}

	public VisumPtVehicleJourneySectionBuilder servedBy(VisumVehicleCombination combination) {
		this.combination = combination;
		return this;
	}

	public VisumPtVehicleJourneySection build() {
		return new VisumPtVehicleJourneySection(id, journeyId, from, to, day, combination);
	}

}
