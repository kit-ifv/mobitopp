package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.local.ZoneChargingFacility;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingPower;

public class ChargingFacilityFormat implements SerialiserFormat<ZoneChargingFacility> {

	private static final int zoneIdIndex = 0;
	private static final int idIndex = 1;
	private static final int stationIdIndex = 2;
	private static final int locationIndex = 3;
	private static final int typeIndex = 4;
	private static final int powerIndex = 5;
	
	private final LocationParser locationParser;
	
	public ChargingFacilityFormat() {
		super();
		this.locationParser = new LocationParser();
	}

	@Override
	public List<String> header() {
		return asList("zoneId", "id", "stationId", "location", "type", "power");
	}

	@Override
	public List<String> prepare(ZoneChargingFacility facility) {
		return asList(valueOf(facility.zoneId()),
				valueOf(facility.id()),
				valueOf(facility.stationId()),
				valueOf(locationParser.serialise(facility.location())),
				valueOf(facility.type()),
				valueOf(facility.power().inKw()));
	}

	@Override
	public Optional<ZoneChargingFacility> parse(List<String> data) {
		int zoneId = zoneIdOf(data);
		int id = idOf(data);
		int stationId = stationIdOf(data);
		Location location = locationOf(data);
		Type type = typeOf(data);
		ChargingPower power = powerOf(data);
		ChargingFacility facility = new ChargingFacility(id, stationId, location, type, power);
		return Optional.of(new ZoneChargingFacility(zoneId, facility));
	}

	private int zoneIdOf(List<String> data) {
		String zoneId = data.get(zoneIdIndex);
		return Integer.parseInt(zoneId);
	}

	private int idOf(List<String> data) {
		String id = data.get(idIndex);
		return Integer.parseInt(id);
	}

	private int stationIdOf(List<String> data) {
		String stationId = data.get(stationIdIndex);
		return Integer.parseInt(stationId);
	}

	private Location locationOf(List<String> data) {
		String location = data.get(locationIndex);
		return locationParser.parse(location);
	}

	private Type typeOf(List<String> data) {
		String type = data.get(typeIndex);
		return Type.valueOf(type);
	}

	private ChargingPower powerOf(List<String> data) {
		String power = data.get(powerIndex);
		double powerInKw = Double.parseDouble(power);
		return ChargingPower.fromKw(powerInKw);
	}

}
