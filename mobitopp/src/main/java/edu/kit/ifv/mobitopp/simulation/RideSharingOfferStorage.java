package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.ZoneId;

public class RideSharingOfferStorage {


  protected Map<ZoneId, Map<ZoneId, Map<Trip, RideSharingOffer>>> offers = new HashMap<>();

	protected int size=0;


	public void put(Trip trip, RideSharingOffer offer) {

		ZoneId source = trip.origin().zone().getInternalId();
		ZoneId target = trip.destination().zone().getInternalId();

		assertOffersContainsSourceAndTarget(trip);

		offers.get(source).get(target).put(trip, offer);

		size++;
	}

	private void assertOffersContainsSourceAndTarget(Trip trip) {

		ZoneId source = trip.origin().zone().getInternalId();
		ZoneId target = trip.destination().zone().getInternalId();

		if (!offers.containsKey(source)) {
			offers.put(source, new HashMap<>());
		}
		if (!offers.get(source).containsKey(target)) {
			offers.get(source).put(target, new HashMap<>());
		}
	}

	public void remove(Trip trip) {

		ZoneId source = trip.origin().zone().getInternalId();
		ZoneId target = trip.destination().zone().getInternalId();

		assert offers.containsKey(source);
		assert offers.get(source).containsKey(target);

		offers.get(source).get(target).remove(trip);

		size--;
	}

	public RideSharingOffer get(Trip trip) {

		ZoneId source = trip.origin().zone().getInternalId();
		ZoneId target = trip.destination().zone().getInternalId();

		assert offers.containsKey(source);
		assert offers.get(source).containsKey(target);

		RideSharingOffer offer = offers.get(source).get(target).get(trip);

		return offer;
	}

	public int size() {
		return this.size;
	}

	public Collection<Trip> keySet(Trip trip) {

		ZoneId source = trip.origin().zone().getInternalId();
		ZoneId target = trip.destination().zone().getInternalId();

		if (!offers.containsKey(source) || !offers.get(source).containsKey(target)) {
			return new ArrayList<>();
		}

		assert offers.containsKey(source);
		assert offers.get(source).containsKey(target);

		return offers.get(source).get(target).keySet();
	}

}
