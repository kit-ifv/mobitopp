package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.demand.DemandModelDistributionItemIfc;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;

public class DemographyCsv {

  private final List<AttributeType> attributeTypes;
  private final Supplier<List<? extends DemandRegion>> demandSupplier;

	public DemographyCsv(
			final List<AttributeType> attributeTypes,
			final Supplier<List<? extends DemandRegion>> demandSupplier) {
    super();
    this.attributeTypes = attributeTypes;
    this.demandSupplier = demandSupplier;
  }

  public List<String> createHeader() {
    List<? extends DemandRegion> regions = demandSupplier.get();
    if (regions.isEmpty()) {
    	return List.of();
    }
		DemandRegion region = regions.iterator().next();
    Demography demography = region.actualDemography();
    return StreamUtils
        .concat(Stream.of("externalId"),
            attributes().flatMap(type -> headerOf(demography, type)))
        .map(String::valueOf)
        .collect(toList());
  }

  private Stream<AttributeType> attributes() {
    Predicate<AttributeType> isEmployment = type -> StandardAttribute.employment.equals(type);
    return attributeTypes.stream().filter(isEmployment.negate());
  }

  public void serialiseActual(Consumer<String> results) {
  	results.accept(serialised(DemandRegion::actualDemography));
  }

  public void serialiseNominal(Consumer<String> results) {
  	results.accept(serialised(DemandRegion::nominalDemography));
  }

	private String serialised(Function<DemandRegion, Demography> toDemography) {
		CsvBuilder line = new CsvBuilder();
    for (DemandRegion region : demandSupplier.get()) {
      Demography demography = toDemography.apply(region);
      line.append(region.getExternalId());
      write(demography, line);
    }
		return line.toString();
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

  private Stream<Integer> valuesOf(Demography demography, AttributeType type) {
    return demography.getDistribution(type).items().map(DemandModelDistributionItemIfc::amount);
  }

  private String toBounds(RangeDistributionItem item, AttributeType type) {
    return type.prefix() + item.lowerBound() + "-" + item.upperBound();
  }

  private Stream<String> headerOf(Demography demography, AttributeType type) {
    return demography.getDistribution(type).items().map(item -> toBounds(item, type));
  }

}
