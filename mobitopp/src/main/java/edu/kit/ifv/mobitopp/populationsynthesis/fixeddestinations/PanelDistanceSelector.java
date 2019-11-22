package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPair;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPairSelector;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class PanelDistanceSelector implements OdPairSelector {

	private static final int noRange = 0;
	private final PanelDataRepository panelDataRepository;
	private final ImpedanceIfc impedance;
	private final int range;
	private final OdPairSelector communityPairCreator;

	public PanelDistanceSelector(
			final PanelDataRepository panelDataRepository, final OdPairSelector communityPairCreator,
			final ImpedanceIfc impedance, final int range) {
		super();
		this.panelDataRepository = panelDataRepository;
		this.communityPairCreator = communityPairCreator;
		this.impedance = impedance;
		this.range = range;
	}

	public PanelDistanceSelector(
			final PanelDataRepository panelDataRepository, final OdPairSelector otherSelector,
			final ImpedanceIfc impedance) {
		this(panelDataRepository, otherSelector, impedance, noRange);
	}

	@Override
	public Collection<OdPair> select(PersonBuilder person) {
		TreeMap<Integer, List<OdPair>> zones = collectZonesByDistance(person);
		if (zones.isEmpty()) {
			throw new IllegalArgumentException(
					"No zones to commute to are available for person: " + person.toString());
		}
		Collection<OdPair> possibleDestinations = selectZonesInRange(zones);
		if (possibleDestinations.isEmpty()) {
			return zones.firstEntry().getValue();
		}
		return possibleDestinations;
	}

	private TreeMap<Integer, List<OdPair>> collectZonesByDistance(PersonBuilder person) {
		return communityPairCreator
				.select(person)
				.stream()
				.collect(groupingBy(d -> differenceToPoleDistance(person, d), TreeMap::new, toList()));
	}

	private int differenceToPoleDistance(PersonBuilder person, OdPair odPair) {
		float distance = impedance
				.getDistance(odPair.getHomeZone().getId(), odPair.getPossibleDestination().getId());
		float poleDistance = getPoleDistance(person);
		return (int) Math.abs(distance - poleDistance);
	}

	private Collection<OdPair> selectZonesInRange(TreeMap<Integer, List<OdPair>> zones) {
		return zones.headMap(range, true).values().stream().flatMap(List::stream).collect(toList());
	}

	private float getPoleDistance(PersonBuilder person) {
		PersonOfPanelData panelPerson = panelDataRepository
				.getPerson(PersonOfPanelDataId.fromPersonId(person.getId()));
		return panelPerson.getPoleDistance();
	}

	@Override
	public void scale(Community community, int numberOfCommuters) {
		communityPairCreator.scale(community, numberOfCommuters);
	}

}
