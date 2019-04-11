package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.dataimport.DefaultRegionType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;


public class DefaultZoneFormat implements SerialiserFormat<Zone> {

	private static final int oidIndex = 0;
	private static final int idIndex = 1;
	private static final int nameIndex = 2;
	private static final int areaTypeIndex = 3;
	private static final int regionTypeIndex = 4;
	private static final int classificationIndex = 5;
	private static final int locationIndex = 6;
	
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
		return asList("oid", "id", "name", "areaType", "regionType", "classification", "centroidLocation");
	}

	@Override
	public List<String> prepare(Zone zone) {
		return asList(valueOf(zone.getOid()), 
				zone.getId(),
				zone.getName(),
				valueOf(zone.getAreaType().getTypeAsInt()),
				valueOf(zone.getRegionType().code()),
				valueOf(zone.getClassification()),
				valueOf(locationParser.serialise(zone.centroidLocation())));
	}

	@Override
	public Optional<Zone> parse(List<String> data) {
		int oid = oidOf(data);
		String id = idOf(data);
		String name = nameOf(data);
		AreaType areaType = areaTypeOf(data);
		RegionType regionType = regionTypeOf(data);
		ZoneClassificationType classification = classificationOf(data);
		Location centroidLocation = locationOf(data);
		Attractivities attractivities = attractivitiesOf(data);
		ChargingDataForZone charging = chargingOf(data);
    Zone zone = new Zone(oid, id, name, areaType, regionType, classification, centroidLocation, attractivities, charging);
		return Optional.of(zone);
	}

  private int oidOf(List<String> data) {
		String oid = data.get(oidIndex);
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

	private Location locationOf(List<String> data) {
		String location = data.get(locationIndex);
		return locationParser.parse(location);
	}

	private Attractivities attractivitiesOf(List<String> data) {
		int id = Integer.parseInt(idOf(data).substring(1));
		return attractivities.get(id);
	}

	private ChargingDataForZone chargingOf(List<String> data) {
		return charging.chargingDataFor(oidOf(data));
	}

}
