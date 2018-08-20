package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class StationBasedCarSharingOrganizationFormat
		implements SerialiserFormat<StationBasedCarSharingOrganization> {

	private static final int nameIndex = 0;

	@Override
	public List<String> header() {
		return asList("name");
	}

	@Override
	public List<String> prepare(StationBasedCarSharingOrganization company) {
		return asList(company.name());
	}

	@Override
	public Optional<StationBasedCarSharingOrganization> parse(List<String> data) {
		String name = data.get(nameIndex);
		StationBasedCarSharingOrganization organization = new StationBasedCarSharingOrganization(name);
		return Optional.of(organization);
	}

}
