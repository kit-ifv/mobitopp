package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_OCCUP;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_PRIMARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_SECONDARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_TERTIARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.WORK;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.LocalDemandDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataFolder;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class LocalDataForPopulationSynthesis implements DataRepositoryForPopulationSynthesis {

	private static final EnumSet<ActivityType> usedTypes = EnumSet.of(WORK, EDUCATION_PRIMARY,
			EDUCATION_SECONDARY, EDUCATION_TERTIARY, EDUCATION_OCCUP, EDUCATION);

	private final Matrices matrices;
	private final DemandZoneRepository zoneRepository;
	private final ImpedanceIfc impedance;
	private final DemandDataRepository demandDataRepository;
	private final PanelDataRepository panelDataRepository;
	private final DemandDataFolder demandData;
	private final ResultWriter results;

	public LocalDataForPopulationSynthesis(
			Matrices matrices, DemandZoneRepository zoneRepository, PanelDataRepository panelDataRepository,
			ImpedanceIfc impedance, DemandDataFolder demandData, ResultWriter results) {
		super();
		this.matrices = matrices;
		this.zoneRepository = zoneRepository;
		this.panelDataRepository = panelDataRepository;
		this.impedance = impedance;
		this.demandData = demandData;
		this.results = results;
		demandDataRepository = new LocalDemandDataRepository();
	}

	@Override
	public Map<ActivityType, FixedDistributionMatrix> fixedDistributionMatrices() {
		HashMap<ActivityType, FixedDistributionMatrix> typeToMatrix = new HashMap<>();
		for (ActivityType activityType : usedTypes) {
			FixedDistributionMatrix matrix = matrices.fixedDistributionMatrixFor(activityType);
			typeToMatrix.put(activityType, matrix);
		}
		return typeToMatrix;
	}

	@Override
	public DemandDataRepository demandDataRepository() {
		return demandDataRepository;
	}

	@Override
	public DemandZoneRepository zoneRepository() {
		return zoneRepository;
	}

	@Override
	public ImpedanceIfc impedance() {
		return impedance;
	}

	@Override
	public PanelDataRepository panelDataRepository() {
		return panelDataRepository;
	}
	
	@Override
	public void finishExecution() throws IOException {
		try (DemandDataSerialiser serialiser = demandData.serialiseAsCsv()) {
			demandDataRepository.serialiseTo(serialiser);
		}
		results.close();
	}

}
