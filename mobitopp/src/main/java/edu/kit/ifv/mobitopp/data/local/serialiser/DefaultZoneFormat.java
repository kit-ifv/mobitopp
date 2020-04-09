package edu.kit.ifv.mobitopp.data.local.serialiser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneProperties;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.dataimport.DefaultRegionType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.populationsynthesis.ColumnMapping;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public class DefaultZoneFormat implements SerialiserFormat<Zone> {

	private final ColumnMapping<Zone> columns;

	private final ChargingDataResolver charging;
	private final Map<Integer, Attractivities> attractivities;
	private final LocationParser locationParser;
	private final AreaTypeRepository areaTypeRepository;

	public DefaultZoneFormat(
			ChargingDataResolver charging, Map<Integer, Attractivities> attractivities,
			AreaTypeRepository areaTypeRepository) {
		super();
		this.charging = charging;
		this.attractivities = attractivities;
		this.areaTypeRepository = areaTypeRepository;
		locationParser = new LocationParser();
		columns = new ColumnMapping<>();
		columns.add("matrixColumn", zone -> zone.getId().getMatrixColumn());
		columns.add("id", zone -> zone.getId().getExternalId());
		columns.add("name", Zone::getName);
		columns.add("areaType", zone -> zone.getAreaType().getTypeAsInt());
		columns.add("regionType", zone -> zone.getRegionType().code());
		columns.add("classification", Zone::getClassification);
		columns.add("parkingPlaces", Zone::getNumberOfParkingPlaces);
		columns.add("centroidLocation", zone -> locationParser.serialise(zone.centroidLocation()));
		columns.add("isDestination", Zone::isDestination);
		columns.add("relief", Zone::getRelief);
	}

	@Override
	public List<String> header() {
		return columns.header();
	}

	@Override
	public List<String> prepare(Zone zone) {
		return columns.prepare(zone);
	}

	@Override
	public Optional<Zone> parse(List<String> data) {
		int oid = oidOf(data);
		String id = idOf(data);
		ZoneId zoneId = new ZoneId(id, oid);
		ZoneProperties zoneProperties = ZoneProperties
				.builder()
				.name(nameOf(data))
				.areaType(areaTypeOf(data))
				.regionType(regionTypeOf(data))
				.classification(classificationOf(data))
				.parkingPlaces(parkingPlacesOf(data))
				.centroidLocation(locationOf(data))
				.isDestination(isDestinationOf(data))
				.relief(reliefOf(data))
				.build();
		Attractivities attractivities = attractivitiesOf(data);
		ChargingDataForZone charging = chargingOf(data);
		Zone zone = new Zone(zoneId, zoneProperties, attractivities, charging);
		return Optional.of(zone);
	}

	private int oidOf(List<String> data) {
		return columns.get("matrixColumn", data).asInt();
	}

	private String idOf(List<String> data) {
		return columns.get("id", data).asString();
	}

	private String nameOf(List<String> data) {
		return columns.get("name", data).asString();
	}

	private AreaType areaTypeOf(List<String> data) {
		int areaType = columns.get("areaType", data).asInt();
		return areaTypeRepository.getTypeForCode(areaType);
	}

	private RegionType regionTypeOf(List<String> data) {
		int type = columns.get("regionType", data).asInt();
		return new DefaultRegionType(type);
	}

	private ZoneClassificationType classificationOf(List<String> data) {
		String classification = columns.get("classification", data).asString();
		return ZoneClassificationType.valueOf(classification);
	}

	private int parkingPlacesOf(List<String> data) {
		return columns.get("parkingPlaces", data).asInt();
	}

	private Location locationOf(List<String> data) {
		String location = columns.get("centroidLocation", data).asString();
		return locationParser.parse(location);
	}

	private boolean isDestinationOf(List<String> data) {
		return columns.hasColumn("isDestination") ? columns.get("isDestination", data).asBoolean()
				: true;
	}

	private double reliefOf(List<String> data) {
		return columns.hasColumn("relief") ? columns.get("relief", data).asDouble() : 0.0d;
	}

	private Attractivities attractivitiesOf(List<String> data) {
		int id = Integer.parseInt(idOf(data));
		return attractivities.get(id);
	}

	private ChargingDataForZone chargingOf(List<String> data) {
		return charging.chargingDataFor(oidOf(data));
	}

}
