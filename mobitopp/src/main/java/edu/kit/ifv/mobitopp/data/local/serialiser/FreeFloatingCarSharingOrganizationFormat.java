package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;

public class FreeFloatingCarSharingOrganizationFormat
		implements SerialiserFormat<FreeFloatingCarSharingOrganization> {

	private static final int nameIndex = 0;

	@Override
	public List<String> header() {
		return asList("name");
	}

	@Override
	public List<String> prepare(FreeFloatingCarSharingOrganization company) {
		return asList(company.name());
	}

	@Override
	public Optional<FreeFloatingCarSharingOrganization> parse(List<String> data) {
		String name = data.get(nameIndex);
		FreeFloatingCarSharingOrganization organization = new FreeFloatingCarSharingOrganization(name);
		return Optional.of(organization);
	}

}
