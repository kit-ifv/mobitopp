package edu.kit.ifv.mobitopp.simulation;

class ImpedanceFunction
	implements ImpedanceFunctionIfc {

	public float calculateImpedance(
								Person person,
								float traveltime,
								float cost,
								float traveltime_coefficient,
								float cost_coefficient,
								float constant
							) {

		float income = person.getIncome();

		return Math.max(1.0f,
										traveltime_coefficient * traveltime
      							+ cost_coefficient * cost/income
						);
	}

}
