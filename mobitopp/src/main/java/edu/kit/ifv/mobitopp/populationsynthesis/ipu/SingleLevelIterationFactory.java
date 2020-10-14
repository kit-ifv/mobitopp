package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;

public class SingleLevelIterationFactory extends BaseIterationFactory implements IterationFactory {

  public SingleLevelIterationFactory(SynthesisContext context) {
    super(context);
  }
	
	@Override
	protected Stream<Attribute> attributesFor(final DemandRegion region) {
		List<AttributeType> types = getContext().attributes(region.regionalLevel());
		Demography nominalDemography = region.nominalDemography();
		RegionalContext context = region.getRegionalContext();
		return types.stream().flatMap(type -> type.createAttributes(nominalDemography, context));
	}

	@Override
	public AttributeResolver createAttributeResolverFor(DemandRegion region) {
		List<Attribute> attributes = attributesFor(region).collect(toList());
		return new DefaultAttributeResolver(attributes);
	}

}
