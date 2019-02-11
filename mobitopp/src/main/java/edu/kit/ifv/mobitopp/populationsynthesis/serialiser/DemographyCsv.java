package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.DemandModelDistributionItemIfc;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
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
        .concat(Stream.of("ID"),
            attributes().flatMap(type -> headerOf(demography, type)))
        .map(String::valueOf)
        .collect(toList());
  }

  private Stream<AttributeType> attributes() {
    Predicate<AttributeType> isEmployment = type -> StandardAttribute.employment.equals(type);
    return attributeTypes.stream().filter(isEmployment.negate());
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
    return attributes()
        .flatMap(type -> valuesOf(demography, type))
        .map(String::valueOf)
        .collect(joining(";"));
  }

  private Stream<Object> valuesOf(Demography demography, AttributeType type) {
    return demography.getDistribution(type).items().map(DemandModelDistributionItemIfc::amount);
  }

  private String toBounds(RangeDistributionItem item, AttributeType type) {
    return type.prefix() + item.lowerBound() + "-" + item.upperBound();
  }

  private Stream<Object> headerOf(Demography demography, AttributeType type) {
    return demography.getDistribution(type).items().map(item -> toBounds(item, type));
  }

}
