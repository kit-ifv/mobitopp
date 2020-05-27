package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;

public interface IterationBuilder {

	Iteration buildFor(DemandRegion region);

	AttributeResolver createAttributeResolverFor(DemandRegion region);

	List<Constraint> constraintsFor(final DemandRegion region);

	Stream<Attribute> attributesFor(final DemandRegion region);

}