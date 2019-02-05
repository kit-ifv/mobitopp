package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.DemandModelDistributionItemIfc;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;

public class DemographyCsv {

  private final List<AttributeType> attributeTypes;
  private final DemandZoneRepository zoneRepository;

  public DemographyCsv(List<AttributeType> attributeTypes, DemandZoneRepository zoneRepository) {
    super();
    this.attributeTypes = attributeTypes;
    this.zoneRepository = zoneRepository;
  }

  public List<String> createHeader() {
    DemandZone demandZone = zoneRepository.getZones().iterator().next();
    Demography demography = demandZone.actualDemography();
    return StreamUtils
        .concat(Stream.of(demandZone.getId()), householdsHeaderOf(demography),
            attributeTypes.stream().flatMap(type -> headerOf(demography, type)))
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
        .concat(householdsOf(demography),
            attributeTypes.stream().flatMap(type -> valuesOf(demography, type)))
        .map(String::valueOf)
        .collect(joining(";"));
  }

  private Stream<Object> valuesOf(Demography demography, AttributeType type) {
    return demography.getDistribution(type).items().map(DemandModelDistributionItemIfc::amount);
  }

  private String toBounds(ContinuousDistributionItem item, AttributeType type) {
    return type.prefix() + item.lowerBound() + "-" + item.upperBound();
  }

  private Stream<Object> headerOf(Demography demography, AttributeType type) {
    return demography.getDistribution(type).items().map(item -> toBounds(item, type));
  }

  private Stream<Object> householdsOf(Demography demography) {
    return demography.household().items().map(DemandModelDistributionItemIfc::amount);
  }

  private Stream<Object> householdsHeaderOf(Demography demography) {
    return demography.household().items().map(this::toHouseholdBounds);
  }

  private String toHouseholdBounds(HouseholdDistributionItem item) {
    return StandardAttribute.householdSize.prefix() + item.type();
  }

}
