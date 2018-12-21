package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonFixedDestination;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;

public class DefaultFixedDestinationFormat implements SerialiserFormat<PersonFixedDestination> {

  private final ZoneRepository zoneRepository;
  private final LocationParser locationParser;
  private final ColumnMapping<PersonFixedDestination> columns;

  public DefaultFixedDestinationFormat(ZoneRepository zoneRepository) {
    super();
    this.zoneRepository = zoneRepository;
    locationParser = new LocationParser();
    columns = new ColumnMapping<>();
    columns.add("personOid", e -> e.person().getOid());
    columns.add("personNumber", e -> e.person().getPersonNumber());
    columns.add("householdOid", e -> e.person().getHouseholdId().getOid());
    columns.add("householdYear", e -> e.person().getHouseholdId().getYear());
    columns.add("householdNumber", e -> e.person().getHouseholdId().getHouseholdNumber());
    columns.add("activityType", e -> e.fixedDestination().activityType());
    columns.add("zoneId", e -> e.fixedDestination().zone().getOid());
    columns.add("location", e -> locationParser.serialise(e.fixedDestination().location()));
  }

  @Override
  public List<String> header() {
    return columns.header();
  }

  @Override
  public List<String> prepare(PersonFixedDestination element) {
    return columns.prepare(element);
  }

  @Override
  public Optional<PersonFixedDestination> parse(List<String> data) {
    return destinationOf(data).map(destination -> createFixedDestination(data, destination));
  }

  private PersonFixedDestination createFixedDestination(
      List<String> data, FixedDestination destination) {
    PersonId person = personOf(data);
    return new PersonFixedDestination(person, destination);
  }

  private PersonId personOf(List<String> fromData) {
    int householdOid = columns.get("householdOid", fromData).asInt();
    short year = columns.get("householdYear", fromData).asShort();
    long householdNumber = columns.get("householdNumber", fromData).asInt();
    HouseholdId householdId = new HouseholdId(householdOid, year, householdNumber);
    int oid = columns.get("personOid", fromData).asInt();
    int personNumber = columns.get("personNumber", fromData).asInt();
    return new PersonId(oid, householdId, personNumber);
  }

  private Optional<FixedDestination> destinationOf(List<String> data) {
    Optional<Zone> zone = zoneOF(data);
    return zone.map(z -> createFixedDestination(data, z));
  }

  private FixedDestination createFixedDestination(List<String> fromData, Zone zone) {
    String typeId = columns.get("activityType", fromData).asString();
    ActivityType activityType = ActivityType.valueOf(typeId);
    Location location = locationParser.parse(columns.get("location", fromData).asString());
    return new FixedDestination(activityType, zone, location);
  }

  private Optional<Zone> zoneOF(List<String> fromData) {
    int zoneOid = columns.get("zoneId", fromData).asInt();
    if (zoneRepository.hasZone(zoneOid)) {
      return Optional.of(zoneRepository.getZoneByOid(zoneOid));
    }
    return Optional.empty();
  }

}
