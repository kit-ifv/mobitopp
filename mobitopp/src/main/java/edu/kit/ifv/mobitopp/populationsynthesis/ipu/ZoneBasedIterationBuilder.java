package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;

public class ZoneBasedIterationBuilder {

	private final PanelDataRepository panelDataRepository;
	private final List<AttributeType> types;

	public ZoneBasedIterationBuilder(
			final PanelDataRepository panelDataRepository, final List<AttributeType> types) {
		super();
		this.panelDataRepository = panelDataRepository;
		this.types = types;
	}

	public Iteration buildFor(final DemandZone zone) {
		return new IpuIteration(constraintsFor(zone));
	}

	List<Constraint> constraintsFor(final DemandZone zone) {
		return attributes(zone)
				.map(attribute -> attribute.createConstraint(zone.nominalDemography()))
				.collect(toList());
	}

	private Stream<Attribute> attributes(final DemandZone zone) {
		String context = "zone" + zone.getId().getExternalId();
		return types
				.stream()
				.flatMap(type -> type.createAttributes(zone.nominalDemography(), () -> context));
	}

	public AttributeResolver createAttributeResolverFor(final DemandZone forZone) {
		List<Attribute> attributes = attributes(forZone).collect(toList());
		return new DefaultAttributeResolver(attributes, panelDataRepository);
	}

}
