package edu.kit.ifv.mobitopp.dataimport;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;

public class DefaultBikeSharingDataForZone implements BikeSharingDataForZone {

	private final Map<String, Boolean> serviceArea;
	private final Map<String, BikeSharingCompany> companies;

	public DefaultBikeSharingDataForZone(
			Map<String, Boolean> serviceArea, Map<String, BikeSharingCompany> companies) {
		this.serviceArea = serviceArea;
		this.companies = companies;
	}

	@Override
	public boolean isBikeAvailableFor(SimulationPerson person) {
		return availableCompaniesFor(person).findAny().isPresent();
	}

	private Stream<BikeSharingCompany> availableCompaniesFor(SimulationPerson person) {
		return companies
				.entrySet()
				.stream()
				.filter(entry -> person.isMobilityProviderCustomer(entry.getKey()))
				.map(Entry::getValue)
				.filter(company -> company.isBikeAvailableFor(person));
	}

	@Override
	public Bike bookBike(SimulationPerson person) {
		return availableCompaniesFor(person)
				.findFirst()
				.map(company -> company.bookBikeFor(person))
				.orElseThrow(
						() -> new IllegalArgumentException("There is no bike available for: " + person));
	}

	@Override
	public boolean isBikeSharingAreaFor(Bike bike) {
		String owner = bike.owner().name();
		return serviceArea.getOrDefault(owner, false);
	}

}
