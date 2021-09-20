package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumVehicleCombination;

import java.util.EnumSet;
import java.util.List;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;

public class VisumPtVehicleJourneySectionBuilder {

	private static final int defaultId = 0;
	private static final int defaultJourneyId = 0;
	private static final int defaultFrom = 0;
	private static final int defaultTo = 0;
	private static final int defaultDay = 0;
	private static final EnumSet<DayOfWeek> defaultValid = EnumSet.of(DayOfWeek.MONDAY);
	private static final VisumVehicleCombination defaultCombination = visumVehicleCombination()
		.build();
	private int id;
	private int journeyId;
	private int from;
	private int to;
	private final int day;
	private VisumVehicleCombination combination;
	private EnumSet<DayOfWeek> validDays;

	public VisumPtVehicleJourneySectionBuilder() {
		super();
		id = defaultId;
		journeyId = defaultJourneyId;
		from = defaultFrom;
		to = defaultTo;
		day = defaultDay;
		combination = defaultCombination;
		validDays = defaultValid;
	}

	public VisumPtVehicleJourneySectionBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumPtVehicleJourneySectionBuilder withJourney(int journeyId) {
		this.journeyId = journeyId;
		return this;
	}

	public VisumPtVehicleJourneySectionBuilder from(int from) {
		this.from = from;
		return this;
	}

	public VisumPtVehicleJourneySectionBuilder to(int to) {
		this.to = to;
		return this;
	}

	public VisumPtVehicleJourneySectionBuilder withValidDays(DayOfWeek...validDays) {
		return withValidDays(EnumSet.copyOf(List.of(validDays)));
	}

	public VisumPtVehicleJourneySectionBuilder withValidDays(EnumSet<DayOfWeek> validDays) {
		this.validDays = validDays;
		return this;
	}

	public VisumPtVehicleJourneySectionBuilder servedBy(VisumVehicleCombination combination) {
		this.combination = combination;
		return this;
	}

	public VisumPtVehicleJourneySection build() {
		return new VisumPtVehicleJourneySection(id, journeyId, from, to, day, validDays, combination);
	}

}
