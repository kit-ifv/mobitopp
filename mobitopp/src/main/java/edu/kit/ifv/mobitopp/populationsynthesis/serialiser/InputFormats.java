package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultFixedDestinationFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultPrivateCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;

class InputFormats {

	private final ZoneRepository zoneRepository;

	InputFormats(ZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
	}

	public DefaultHouseholdFormat householdFormat() {
		return new DefaultHouseholdFormat(zoneRepository);
	}

	public DefaultPersonFormat personFormat() {
		return new DefaultPersonFormat();
	}

	public DefaultActivityFormat activityFormat() {
		return new DefaultActivityFormat();
	}

	public DefaultPrivateCarFormat privateCarFormat() {
		DefaultPrivateCarFormat carFormat = new DefaultPrivateCarFormat();
		carFormat.register(CarType.conventional, conventionalCarFormat());
		carFormat.register(CarType.bev, bevCarFormat());
		carFormat.register(CarType.erev, erevCarFormat());
		return carFormat;
	}

	private ConventionalCarFormat conventionalCarFormat() {
		return new ConventionalCarFormat(zoneRepository);
	}

	private BevCarFormat bevCarFormat() {
		return new BevCarFormat(abstractElectricCarFormat());
	}

	private AbstractElectricCarFormat abstractElectricCarFormat() {
		return new AbstractElectricCarFormat(conventionalCarFormat());
	}

	private ErevCarFormat erevCarFormat() {
		return new ErevCarFormat(abstractElectricCarFormat());
	}
	
	public DefaultFixedDestinationFormat fixedDestinationFormat() {
		return new DefaultFixedDestinationFormat(zoneRepository);
	}

	public DefaultOpportunityFormat opportunityFormat() {
		return new DefaultOpportunityFormat(zoneRepository);
	}
}
