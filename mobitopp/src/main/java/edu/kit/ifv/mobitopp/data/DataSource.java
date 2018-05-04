package edu.kit.ifv.mobitopp.data;

import java.io.IOException;

import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ElectricChargingWriter;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public interface DataSource {

	DataRepositoryForPopulationSynthesis forPopulationSynthesis(
			VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, StructuralData demographyData,
			PanelDataRepository panelDataRepository, int numberOfZones, StartDateSpecification input,
			ResultWriter results) throws IOException;

	DataRepositoryForSimulation forSimulation(
			VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, int numberOfZones,
			InputSpecification input, PublicTransportData data, ResultWriter results,
			ElectricChargingWriter electricChargingWriter) throws IOException;

	void validate();


}
