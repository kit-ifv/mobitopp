package edu.kit.ifv.mobitopp.simulation;

class ImpedanceFunctionTravelTime
	implements ImpedanceFunctionIfc {

	public float calculateImpedance(
								Person person,
								float traveltime,
								float cost,
								float traveltime_coefficient,
								float cost_coefficient,
								float constant
							) {

		return 5.0f+Math.max(1.0f,traveltime_coefficient * traveltime);
	}

}
