package edu.kit.ifv.mobitopp.simulation;

interface ImpedanceFunctionIfc {

	public float calculateImpedance(
								Person person,
								float traveltime,
								float cost,
								float traveltime_coefficient,
								float cost_coefficient,
								float constant
							);

}
