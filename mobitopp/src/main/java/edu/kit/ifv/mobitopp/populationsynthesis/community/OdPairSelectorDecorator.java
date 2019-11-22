package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public class OdPairSelectorDecorator implements OdPairSelector {

	private final OdPairSelector selector;

	public OdPairSelectorDecorator(OdPairSelector selector) {
		super();
		this.selector = selector;
	}

	@Override
	public Collection<OdPair> select(PersonBuilder person) {
		return selector.select(person);
	}

	@Override
	public void scale(Community community, int numberOfCommuters) {
		selector.scale(community, numberOfCommuters);
	}

}