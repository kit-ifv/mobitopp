package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.ColumnMapping;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatus;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.DefaultHouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;

public class DefaultHouseholdFormat implements SerialiserFormat<HouseholdForSetup> {

	private final ZoneRepository zoneRepository;
	private final LocationParser locationParser;
	private final ColumnMapping<HouseholdForSetup> columns;

	public DefaultHouseholdFormat(ZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
		locationParser = new LocationParser();
		columns = new ColumnMapping<>();
		columns.add("householdId", e -> e.attributes().id.getOid());
		columns.add("year", e -> e.attributes().id.getYear());
		columns.add("householdNumber", e -> e.attributes().id.getHouseholdNumber());
		columns.add("nominalSize", e -> e.attributes().nominalSize);
		columns.add("domCode", e -> e.attributes().domCode);
		columns.add("homeZone", e -> e.attributes().homeZone.getId().getMatrixColumn());
		columns.add("homeLocation", e -> locationParser.serialise(e.attributes().homeLocation));
		columns.add("homeX", e -> e.attributes().homeLocation.coordinatesP().getX());
		columns.add("homeY", e -> e.attributes().homeLocation.coordinatesP().getY());
		columns.add("numberOfMinors", e -> e.attributes().numberOfMinors);
		columns.add("numberOfNotSimulatedChildren", e -> e.attributes().numberOfNotSimulatedChildren);
		columns.add("totalNumberOfCars", e -> e.attributes().totalNumberOfCars);
		columns.add("income", e -> e.attributes().monthlyIncomeEur);
		columns.add("incomeClass", e -> e.attributes().incomeClass);
		columns.add("economicalStatus", e -> e.attributes().economicalStatus.getCode());
		columns.add("canChargePrivately", e -> e.attributes().canChargePrivately);
	}

	@Override
	public List<String> header() {
		return columns.header();
	}

	@Override
	public List<String> prepare(HouseholdForSetup household) {
		return columns.prepare(household);
	}

	@Override
	public Optional<HouseholdForSetup> parse(List<String> data) {
		return zoneOf(data).map(zone -> createHousehold(data, zone));
	}

	private HouseholdForSetup createHousehold(List<String> data, Zone zone) {
		HouseholdId id = idOf(data);
		int nominalSize = nominalSizeOf(data);
		int domCode = domCodeOf(data);
		Location location = locationOf(data);
		int numberOfMinors = minorsOf(data);
		int numberOfNotSimulatedChildren = childrenOf(data);
		int totalNumberOfCars = carsOf(data);
		int income = incomeOf(data);
		int incomeClass = incomeClassOf(data);
		EconomicalStatus economicalStatus = economicalStatusOf(data);
		boolean canChargePrivately = chargePrivatelyOf(data);
		HouseholdForSetup household = new DefaultHouseholdForSetup(id, nominalSize, domCode, zone,
				location, numberOfMinors, numberOfNotSimulatedChildren, totalNumberOfCars, income,
				incomeClass, economicalStatus, canChargePrivately);
		assign(household, zone);
		return household;
	}


	private void assign(HouseholdForSetup household, Zone zone) {
		zone.getDemandData().getPopulationData().addHousehold(household.toHousehold());
	}

	private HouseholdId idOf(List<String> data) {
		int oid = columns.get("householdId", data).asInt();
		short year = columns.get("year", data).asShort();
		long householdNumber = columns.get("householdNumber", data).asLong();
		return new HouseholdId(oid, year, householdNumber);
	}

	private int nominalSizeOf(List<String> data) {
		return columns.get("nominalSize", data).asInt();
	}

	private int domCodeOf(List<String> data) {
		return columns.get("domCode", data).asInt();
	}

	private Optional<Zone> zoneOf(List<String> data) {
		int oid = columns.get("homeZone", data).asInt();
		if (zoneRepository.hasZone(oid)) {
			return Optional.of(zoneRepository.getZoneByOid(oid));
		}
		return Optional.empty();
	}

	private Location locationOf(List<String> data) {
		String location = columns.get("homeLocation", data).asString();
		return locationParser.parse(location);
	}

	private int minorsOf(List<String> data) {
		return columns.get("numberOfMinors", data).asInt();
	}

	private int childrenOf(List<String> data) {
		return columns.get("numberOfNotSimulatedChildren", data).asInt();
	}

	private int carsOf(List<String> data) {
		return columns.get("totalNumberOfCars", data).asInt();
	}

	private int incomeOf(List<String> data) {
		return columns.get("income", data).asInt();
	}

	private int incomeClassOf(List<String> data) {
		return columns.get("incomeClass", data).asInt();
	}

	private EconomicalStatus economicalStatusOf(List<String> data) {
		return EconomicalStatus.of(columns.get("economicalStatus", data).asInt());
	}
	
	private boolean chargePrivatelyOf(List<String> data) {
		return columns.get("canChargePrivately", data).asBoolean();
	}

}
