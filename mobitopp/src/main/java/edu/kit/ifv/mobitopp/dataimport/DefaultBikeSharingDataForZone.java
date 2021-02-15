package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.lang.String.valueOf;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultBikeSharingDataForZone implements BikeSharingDataForZone {

	private final ZoneId zoneId;
	private final Map<String, Boolean> serviceArea;
	private final Map<String, BikeSharingCompany> companies;

	public DefaultBikeSharingDataForZone(ZoneId zoneId, Map<String, Boolean> serviceArea,
			Map<String, BikeSharingCompany> companies) {
		this.zoneId = zoneId;
		this.serviceArea = serviceArea;
		this.companies = companies;
	}

	@Override
	public boolean isBikeAvailableFor(Person person) {
		return availableCompaniesFor(person).findAny().isPresent();
	}

	private Stream<BikeSharingCompany> availableCompaniesFor(Person person) {
		return companies.entrySet().stream()
				.filter(entry -> person.isMobilityProviderCustomer(entry.getKey()))
				.map(Entry::getValue).filter(company -> company.isBikeAvailableFor(person, zoneId));
	}

	@Override
	public Bike bookBike(Person person) {
		return availableCompaniesFor(person).findFirst()
				.map(company -> company.bookBikeFor(person, zoneId))
				.orElseThrow(() -> warn(new IllegalArgumentException(String.format(
						"There is no bike available in zone %s for person %s", zoneId, person)), log));
	}

	@Override
	public boolean isBikeSharingArea() {
		return serviceArea.values().stream().anyMatch(Boolean::booleanValue);
	}

	@Override
	public boolean isBikeSharingAreaFor(Bike bike) {
		String owner = bike.owner().name();
		return serviceArea.getOrDefault(owner,
				warn(owner, "'is bikesharing area for" + valueOf(bike) + "'", false, log));
	}

}
