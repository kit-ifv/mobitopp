package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.EnumSet;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@SuppressWarnings("serial")
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class VisumPtVehicleJourneySection implements Serializable {

	public final int id;
	public final int journeyId;
	public final int fromElementIndex;
	public final int toElementIndex;
	public final int day;
	public final EnumSet<DayOfWeek> validDays;
	public final VisumVehicleCombination vehicle;

}
