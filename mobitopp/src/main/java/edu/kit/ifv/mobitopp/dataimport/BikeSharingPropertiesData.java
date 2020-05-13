package edu.kit.ifv.mobitopp.dataimport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;

public class BikeSharingPropertiesData implements BikeSharingDataRepository {

	private static final String numberOfBikesAttribute = "number_of_bikes";
	private static final String operatingAreaAttribute = "operating_area";
	private final StructuralData properties;
	private Map<String, BikeSharingCompany> companies;

	public BikeSharingPropertiesData(StructuralData properties) {
		this.properties = properties;
	}

	@Override
	public BikeSharingDataForZone getData(ZoneId zone) {
		Map<String, Boolean> serviceArea = buildServiceAreaFor(zone);
		Map<String, BikeSharingCompany> companies = buildCompanies(zone);
		return new DefaultBikeSharingDataForZone(zone, serviceArea, companies);
	}

	Map<String, BikeSharingCompany> buildCompanies(ZoneId zoneId) {
		if (null == this.companies) {
			this.companies = generateCompanies(zoneId);
		}
		return this.companies;
	}

	private Map<String, BikeSharingCompany> generateCompanies(ZoneId zoneId) {
		Map<String, BikeSharingCompany> companies = properties
				.getAttributes()
				.stream()
				.filter(attribute -> attribute.startsWith(numberOfBikesAttribute))
				.map(this::companyName)
				.map(companyName -> createCompany(zoneId, companyName))
				.collect(toMap(BikeSharingCompany::name, Function.identity()));
		return Collections.unmodifiableMap(companies);
	}

	private String companyName(String attribute) {
		if (attribute.contains(":")) {
			return attribute.split(":")[1];
		}
		throw new IllegalArgumentException(
				"Attribute or column name is missing company name: " + attribute);
	}

	private BikeSharingCompany createCompany(ZoneId zoneId, String companyName) {
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		int numberOfBikes = properties
				.valueOrDefault(zoneId.getExternalId(), numberOfBikesAttribute + ":" + companyName);
		List<BikeSharingBike> bikes = IntStream
				.range(0, numberOfBikes)
				.mapToObj(bikeId -> createBike(bikeId, zoneId, company))
				.collect(toList());
		company.own(bikes);
		return company;
	}

	private BikeSharingBike createBike(
			int bikeId, ZoneId zoneId, FreeFloatingBikeSharingCompany company) {
		return new BikeSharingBike(bikeId, zoneId, company);
	}

	Map<String, Boolean> buildServiceAreaFor(ZoneId zoneId) {
		return properties
				.getAttributes()
				.stream()
				.filter(attribute -> attribute.startsWith(operatingAreaAttribute))
				.map(this::companyName)
				.collect(toMap(Function.identity(), companyName -> isServiceArea(zoneId, companyName)));
	}

	private boolean isServiceArea(ZoneId zoneId, String companyName) {
		return properties
				.valueOrDefault(zoneId.getExternalId(), operatingAreaAttribute + ":" + companyName) == 1;
	}

}
