package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_OCCUP;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_PRIMARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_SECONDARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.EDUCATION_TERTIARY;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.WORK;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.VehicleBehaviour;

public class LocalDataForSimulation implements DataRepositoryForSimulation {

	private static final EnumSet<ActivityType> usedTypes = EnumSet.of(WORK, EDUCATION_PRIMARY,
			EDUCATION_SECONDARY, EDUCATION_TERTIARY, EDUCATION_OCCUP, EDUCATION);

	private final Matrices matrices;
	private final ZoneRepository zoneRepository;
	private final ImpedanceIfc impedance;
	private final PersonLoader personLoader;
	private final VehicleBehaviour vehicleBehaviour;

	public LocalDataForSimulation(
			Matrices matrices, ZoneRepository zoneRepository, ImpedanceIfc impedance,
			PersonLoader personLoader, VehicleBehaviour vehicleBehaviour) {
		super();
		this.matrices = matrices;
		this.zoneRepository = zoneRepository;
		this.impedance = impedance;
		this.personLoader = personLoader;
		this.vehicleBehaviour = vehicleBehaviour;
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
	public PersonLoader personLoader() {
		return personLoader;
	}

	@Override
	public ZoneRepository zoneRepository() {
		return zoneRepository;
	}

	@Override
	public ImpedanceIfc impedance() {
		return impedance;
	}

	@Override
	public VehicleBehaviour vehicleBehaviour() {
		return vehicleBehaviour;
	}

}
