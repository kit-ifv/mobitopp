package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.DataSource;
import edu.kit.ifv.mobitopp.data.InputSpecification;
import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.StartDateSpecification;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.TypeMapping;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ElectricChargingWriter;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class TestSource implements DataSource {

	/**
	 * SnakeYaml needs a single argument constructor for classes without attributes.
	 * 
	 * @param dummy
	 */
	public TestSource(String dummy) {
	}

	@Override
	public DataRepositoryForPopulationSynthesis forPopulationSynthesis(
			VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, DemographyData demographyData,
			PanelDataRepository panelDataRepository, int numberOfZones, StartDateSpecification input,
			ResultWriter results, AreaTypeRepository areaTypeRepository, TypeMapping typeMapping,
			PersonChanger perChanger, Function<ImpedanceIfc, ImpedanceIfc> wrapImpedance) throws IOException {
		throw new RuntimeException("dummy implementation");
	}

	@Override
	public DataRepositoryForSimulation forSimulation(
			Supplier<Network> network, int numberOfZones, InputSpecification input,
			PublicTransportData data, ResultWriter results, ElectricChargingWriter electricChargingWriter,
			AreaTypeRepository areaTypeRepository, TypeMapping typeMapping,
			Predicate<HouseholdForSetup> householdFilter, PersonChanger perChanger,
			Function<ImpedanceIfc, ImpedanceIfc> wrapImpedance) throws IOException {
		throw new RuntimeException("dummy implementation");
	}

	@Override
	public void validate() {
		throw new RuntimeException("dummy implementation");
	}

}
