package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.result.ResultWriter;

public interface SimulationContext {

	WrittenConfiguration configuration();

	DynamicParameters modeChoiceParameters();
	
	DynamicParameters experimentalParameters();

	long seed();

	float fractionOfPopulation();

	SimulationDays simulationDays();

	DataRepositoryForSimulation dataRepository();

	ZoneRepository zoneRepository();

	ImpedanceIfc impedance();

	VehicleBehaviour vehicleBehaviour();

	PersonLoader personLoader();

	ResultWriter results();

	PersonResults personResults();

	void beforeSimulation();

	void afterSimulation();

}
