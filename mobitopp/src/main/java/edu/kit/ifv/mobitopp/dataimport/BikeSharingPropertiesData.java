package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BikeSharingPropertiesData implements BikeSharingDataRepository {

	private static final String numberOfBikesAttribute = "number_of_bikes";
	private static final String operatingAreaAttribute = "operating_area";
	private final StructuralData properties;
	private final IdToOidMapper idMapper;
	private Map<String, BikeSharingCompany> companies;

	public BikeSharingPropertiesData(StructuralData properties, IdToOidMapper mapper) {
		this.properties = properties;
		this.idMapper = mapper;
	}

	@Override
	public BikeSharingDataForZone getData(ZoneId zone) {
		Map<String, Boolean> serviceArea = buildServiceAreaFor(zone);
		Map<String, BikeSharingCompany> companies = getCompanies();
		return new DefaultBikeSharingDataForZone(zone, serviceArea, companies);
	}

	Map<String, BikeSharingCompany> getCompanies() {
		if (null == this.companies) {
			this.companies = Collections.unmodifiableMap(createCompanies());
		}
		return this.companies;
	}

	private Map<String, BikeSharingCompany> createCompanies() {
		return properties
				.getAttributes()
				.stream()
				.filter(attribute -> attribute.startsWith(numberOfBikesAttribute))
				.map(this::companyName)
				.map(companyName -> createCompany(companyName))
				.collect(toMap(BikeSharingCompany::name, Function.identity()));
	}

	private String companyName(String attribute) {
		if (attribute.contains(":")) {
			return attribute.split(":")[1];
		}
		throw warn(new IllegalArgumentException(
				"Attribute or column name is missing company name: " + attribute), log);
	}

	private BikeSharingCompany createCompany(String companyName) {
		FreeFloatingBikeSharingCompany company = new FreeFloatingBikeSharingCompany(companyName);
		List<BikeSharingBike> bikes = new LinkedList<>();
		properties.resetRow();
		while (properties.hasNext()) {
			String currentRegion = String.valueOf(properties.currentRegion());
			ZoneId zoneId = this.idMapper.mapToZoneId(currentRegion);
			int numberOfBikes = properties
					.valueOrDefault(currentRegion, numberOfBikesAttribute + ":" + companyName);
			bikes
					.addAll(IntStream
							.range(0, numberOfBikes)
							.mapToObj(bikeId -> createBike(bikeId, zoneId, company))
							.collect(toList()));
			properties.next();
		}
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
