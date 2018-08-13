package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VisumPtVehicleJourneySection 
	implements Serializable
{

	public final int id;
	public final int journeyId;
	public final int fromElementIndex;
	public final int toElementIndex;
	public final int day;
	public final VisumVehicleCombination vehicle;



	public VisumPtVehicleJourneySection(
		 int id,
		 int journeyId,
		 int fromElementIndex,
		 int toElementIndex,
		 int day,
		 VisumVehicleCombination vehicle
	) {
		this.id = id;
		this.journeyId = journeyId;
		this.fromElementIndex = fromElementIndex;
		this.toElementIndex = toElementIndex;
		this.day = day;
		this.vehicle = vehicle;
	}


	@Override
	public String toString() {

		return "VisumPtVehicleJourneySection("
							+ id + ","
							+ journeyId + ","
							+ fromElementIndex + ","
							+ toElementIndex + ","
							+ day + ","
							+ vehicle + ")";

	}


}
