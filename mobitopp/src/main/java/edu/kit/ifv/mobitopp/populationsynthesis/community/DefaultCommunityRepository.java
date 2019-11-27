package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;

public class DefaultCommunityRepository implements CommunityRepository, CommunitySelector {

	private final Map<Community, Map<Community, Integer>> commutingRelations;

	public DefaultCommunityRepository(Map<Community, Map<Community, Integer>> commutingRelations) {
		super();
		this.commutingRelations = commutingRelations;
	}

	@Override
	public Collection<Community> getCommunities() {
		return Collections.unmodifiableCollection(commutingRelations.keySet());
	}

	@Override
	public Stream<Community> getCommutingCommunitiesFrom(ZoneId zoneId) {
		final Community community = get(zoneId);
		return commutingRelations.get(community).keySet().stream();
	}

	@Override
	public Community get(ZoneId id) {
		return commutingRelations
				.keySet()
				.stream()
				.filter(c -> c.contains(id))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No community found for zone: " + id));
	}

	@Override
	public void notifyAssignedRelation(Zone homeZone, Zone destination) {
		final Community homeCommunity = get(homeZone.getId());
		final Community destinationCommunity = get(destination.getId());
		updateRelation(homeCommunity, destinationCommunity);
	}

	private void updateRelation(Community origin, Community destination) {
		commutingRelations.get(origin).computeIfPresent(destination, (k, v) -> v - 1);
		cleanUpRelation(origin, destination);
	}

	private void cleanUpRelation(Community origin, Community destination) {
		if (!commutingRelations.containsKey(origin)) {
			return;
		}
		final Map<Community, Integer> relations = commutingRelations.get(origin);
		if (relations.containsKey(destination) && 0 == relations.get(destination)) {
			relations.remove(destination);
		}
	}

	@Override
	public void scale(final Community origin, final int numberOfCommuters) {
		final Map<Community, Integer> destinations = commutingRelations.get(origin);
		final int sum = destinations.values().stream().mapToInt(Integer::intValue).sum();
		final double factor = (double) numberOfCommuters / sum;
		double remainder = 0.0d;
		Entry<Community, Integer> last = null;
		for (Entry<Community, Integer> entry : destinations.entrySet()) {
			last = entry;
			final double scaled = entry.getValue() * factor;
			final double withRemainder = scaled + remainder;
			final double floored = Math.floor(withRemainder);
			remainder = withRemainder - floored;
			final int newValue = (int) floored;
			destinations.put(entry.getKey(), newValue);
		}
		if (0.0d != remainder && null != last) {
			destinations.put(last.getKey(), last.getValue() + 1);
		}
		destinations.keySet().forEach(d -> cleanUpRelation(origin, d));
	}
}
