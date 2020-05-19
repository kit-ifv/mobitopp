package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

public class IterationBuilder {

	private final RegionalLevel regionalLevel;
	private final PanelDataRepository panelDataRepository;
	private final List<AttributeType> types;

	public IterationBuilder(
			final RegionalLevel regionalLevel, final PanelDataRepository panelDataRepository,
			final List<AttributeType> types) {
		super();
		this.regionalLevel = regionalLevel;
		this.panelDataRepository = panelDataRepository;
		this.types = types;
	}
	
	public static IterationBuilder forZone(
			final PanelDataRepository panelDataRepository, final List<AttributeType> types) {
		return new IterationBuilder(RegionalLevel.zone, panelDataRepository, types);
	}

	public Iteration buildFor(final DemandRegion region) {
		return new IpuIteration(constraintsFor(region));
	}

	List<Constraint> constraintsFor(final DemandRegion region) {
		return attributesFor(region)
				.map(attribute -> attribute.createConstraint(region.nominalDemography()))
				.collect(toList());
	}

	private Stream<Attribute> attributesFor(final DemandRegion region) {
		String context = regionalLevel.identifier() + region.getExternalId();
		return types
				.stream()
				.flatMap(type -> type.createAttributes(region.nominalDemography(), () -> context));
	}

	public AttributeResolver createAttributeResolverFor(final DemandRegion region) {
		List<Attribute> attributes = attributesFor(region).collect(toList());
		return new DefaultAttributeResolver(attributes, panelDataRepository);
	}

}
