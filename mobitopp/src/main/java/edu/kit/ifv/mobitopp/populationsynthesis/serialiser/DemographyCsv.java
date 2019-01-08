package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.result.CsvBuilder;

public class DemographyCsv {

  private final DemandZoneRepository zoneRepository;

  public DemographyCsv(DemandZoneRepository zoneRepository) {
    super();
    this.zoneRepository = zoneRepository;
  }

  public List<String> createHeader() {
    DemandZone demandZone = zoneRepository.getZones().iterator().next();
    Demography demography = demandZone.actualDemography();
    return Stream
        .concat(Stream.concat(Stream.of(demandZone.getId()), householdsHeaderOf(demography)),
            Stream.concat(malesHeaderOf(demography), femalesHeaderOf(demography)))
        .map(String::valueOf)
        .collect(toList());
  }

  public void serialiseActual(Consumer<String> results) {
    CsvBuilder line = new CsvBuilder();
    for (DemandZone demandZone : zoneRepository.getZones()) {
      Demography demography = demandZone.actualDemography();
      line.append(demandZone.getId());
      write(demography, line);
    }
    results.accept(line.toString());
  }

  public void serialiseNominal(Consumer<String> toWriter) {
    CsvBuilder serialised = new CsvBuilder();
    for (DemandZone demandZone : zoneRepository.getZones()) {
      Demography demography = demandZone.nominalDemography();
      serialised.append(demandZone.getId());
      write(demography, serialised);
    }
    toWriter.accept(serialised.toString());
  }

  private void write(Demography demography, CsvBuilder line) {
    line.newLine(serialised(demography));
  }

  private String serialised(Demography demography) {
    return Stream
        .concat(Stream.concat(householdsOf(demography), malesOf(demography)), femalesOf(demography))
        .map(String::valueOf)
        .collect(joining(";"));
  }

  private Stream<Object> femalesOf(Demography demography) {
    return demography.femaleAge().items().map(ContinuousDistributionItem::amount);
  }

  private Stream<Object> malesOf(Demography demography) {
    return demography.maleAge().items().map(ContinuousDistributionItem::amount);
  }

  private Stream<Object> householdsOf(Demography demography) {
    return demography.household().items().map(HouseholdDistributionItem::amount);
  }

  private Stream<Object> femalesHeaderOf(Demography demography) {
    return demography.femaleAge().items().map(this::toFemaleBounds);
  }

  private String toFemaleBounds(ContinuousDistributionItem item) {
    return "Age:F:" + item.lowerBound() + "-" + item.upperBound();
  }

  private Stream<Object> malesHeaderOf(Demography demography) {
    return demography.maleAge().items().map(this::toMaleBounds);
  }

  private String toMaleBounds(ContinuousDistributionItem item) {
    return "Age:M:" + item.lowerBound() + "-" + item.upperBound();
  }

  private Stream<Object> householdsHeaderOf(Demography demography) {
    return demography.household().items().map(this::toHouseholdBounds);
  }

  private String toHouseholdBounds(HouseholdDistributionItem item) {
    return "HHTyp:" + item.type();
  }

}
