package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class NamedAttribute implements Attribute {

	static final String nameSeparator = "-";

	protected final RegionalContext context;
	protected final AttributeType type;
	protected final int lowerBound;
	protected final int upperBound;

	@Override
	public final String name() {
		return context.name() + nameSeparator
				+ type.createInstanceName(lowerBound, upperBound);
	}
	
	@Override
	public AttributeType type() {
		return type;
	}
	
	@Override
	public RegionalContext context() {
		return context;
	}

	@Override
	public boolean matches(RangeDistributionItem item) {
		return lowerBound == item.lowerBound() && upperBound == item.upperBound();
	}

}