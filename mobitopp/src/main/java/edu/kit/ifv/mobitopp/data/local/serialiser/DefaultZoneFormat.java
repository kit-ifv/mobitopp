package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.dataimport.DefaultRegionType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;


public class DefaultZoneFormat implements SerialiserFormat<Zone> {

	private static final int matrixColumnIndex = 0;
	private static final int idIndex = 1;
	private static final int nameIndex = 2;
	private static final int areaTypeIndex = 3;
	private static final int regionTypeIndex = 4;
	private static final int classificationIndex = 5;
	private static final int parkingPlaces = 6;
	private static final int locationIndex = 7;
	
	private final ChargingDataResolver charging;
	private final Map<Integer, Attractivities> attractivities;
	private final LocationParser locationParser;
	private final AreaTypeRepository areaTypeRepository;

	public DefaultZoneFormat(ChargingDataResolver charging, Map<Integer, Attractivities> attractivities, AreaTypeRepository areaTypeRepository) {
		super();
		this.charging = charging;
		this.attractivities = attractivities;
		this.areaTypeRepository = areaTypeRepository;
		locationParser = new LocationParser();
	}

	@Override
	public List<String> header() {
    return asList("matrixColumn", "id", "name", "areaType", "regionType", "classification", "parkingPlaces",
        "centroidLocation");
	}

	@Override
	public List<String> prepare(Zone zone) {
		return asList(valueOf(zone.getId().getMatrixColumn()), 
				zone.getId().getExternalId(),
				zone.getName(),
				valueOf(zone.getAreaType().getTypeAsInt()),
				valueOf(zone.getRegionType().code()),
				valueOf(zone.getClassification()),
				valueOf(zone.getNumberOfParkingPlaces()),
				valueOf(locationParser.serialise(zone.centroidLocation())));
	}

	@Override
	public Optional<Zone> parse(List<String> data) {
		int oid = oidOf(data);
		String id = idOf(data);
		ZoneId zoneId = new ZoneId(id, oid);
		String name = nameOf(data);
		AreaType areaType = areaTypeOf(data);
		RegionType regionType = regionTypeOf(data);
		ZoneClassificationType classification = classificationOf(data);
		int parkingPlaces = parkingPlacesOf(data);
		Location centroidLocation = locationOf(data);
		Attractivities attractivities = attractivitiesOf(data);
		ChargingDataForZone charging = chargingOf(data);
    Zone zone = new Zone(zoneId, name, areaType, regionType, classification, parkingPlaces,
        centroidLocation, attractivities, charging);
		return Optional.of(zone);
	}

  private int oidOf(List<String> data) {
		String oid = data.get(matrixColumnIndex);
		return Integer.parseInt(oid);
	}

	private String idOf(List<String> data) {
		return data.get(idIndex);
	}

	private String nameOf(List<String> data) {
		return data.get(nameIndex);
	}

	private AreaType areaTypeOf(List<String> data) {
		String areaType = data.get(areaTypeIndex);
		return areaTypeRepository.getTypeForCode(Integer.parseInt(areaType));
	}
	
	private RegionType regionTypeOf(List<String> data) {
	  String value = data.get(regionTypeIndex);
	  int type = Integer.valueOf(value);
	  return new DefaultRegionType(type);
	}

	private ZoneClassificationType classificationOf(List<String> data) {
		String classification = data.get(classificationIndex);
		return ZoneClassificationType.valueOf(classification);
	}
	
	private int parkingPlacesOf(List<String> data) {
	  return Integer.valueOf(data.get(parkingPlaces));
	}

	private Location locationOf(List<String> data) {
		String location = data.get(locationIndex);
		return locationParser.parse(location);
	}

	private Attractivities attractivitiesOf(List<String> data) {
		int id = Integer.parseInt(idOf(data));
		return attractivities.get(id);
	}

	private ChargingDataForZone chargingOf(List<String> data) {
		return charging.chargingDataFor(oidOf(data));
	}

}
