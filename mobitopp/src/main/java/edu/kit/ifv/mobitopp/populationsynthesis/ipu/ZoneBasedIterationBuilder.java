package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;

public class ZoneBasedIterationBuilder {

  private final PanelDataRepository panelDataRepository;
  private final List<AttributeType> types;

  public ZoneBasedIterationBuilder(PanelDataRepository panelDataRepository, List<AttributeType> types) {
    super();
    this.panelDataRepository = panelDataRepository;
    this.types = types;
  }

  public Iteration buildFor(DemandZone zone) {
    return new IpuIteration(constraintsFor(zone));
  }

  List<Constraint> constraintsFor(DemandZone zone) {
    return attributes(zone)
        .map(attribute -> attribute.createConstraint(zone.nominalDemography()))
        .collect(toList());
  }

  private Stream<Attribute> attributes(DemandZone zone) {
    return types.stream().flatMap(type -> type.createAttributes(zone.nominalDemography()));
  }

  public AttributeResolver createAttributeResolverFor(DemandZone forZone) {
    List<Attribute> attributes = attributes(forZone).collect(toList());
    return new DefaultAttributeResolver(attributes, panelDataRepository);
  }

}
