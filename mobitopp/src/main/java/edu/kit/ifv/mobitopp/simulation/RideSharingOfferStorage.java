package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RideSharingOfferStorage {


	protected Map<Integer,Map<Integer,Map<TripIfc,RideSharingOffer>>> offers 
			= new HashMap<Integer,Map<Integer,Map<TripIfc,RideSharingOffer>>>();

	protected int size=0;


	public void put(TripIfc trip, RideSharingOffer offer) {

		Integer source = trip.origin().zone().getOid();
		Integer target = trip.destination().zone().getOid();

		assertOffersContainsSourceAndTarget(trip);

		offers.get(source).get(target).put(trip, offer);

		size++;
	}

	private void assertOffersContainsSourceAndTarget(TripIfc trip) {

		Integer source = trip.origin().zone().getOid();
		Integer target = trip.destination().zone().getOid();

		if (!offers.containsKey(source)) {
			offers.put(source, new HashMap<>());
		}
		if (!offers.get(source).containsKey(target)) {
			offers.get(source).put(target, new HashMap<>());
		}
	}

	public void remove(TripIfc trip) {

		Integer source = trip.origin().zone().getOid();
		Integer target = trip.destination().zone().getOid();

		assert offers.containsKey(source);
		assert offers.get(source).containsKey(target);

		offers.get(source).get(target).remove(trip);

		size--;
	}

	public RideSharingOffer get(TripIfc trip) {

		Integer source = trip.origin().zone().getOid();
		Integer target = trip.destination().zone().getOid();

		assert offers.containsKey(source);
		assert offers.get(source).containsKey(target);

		RideSharingOffer offer = offers.get(source).get(target).get(trip);

		return offer;
	}

	public int size() {
		return this.size;
	}

	public Collection<TripIfc> keySet(TripIfc trip) {

		Integer source = trip.origin().zone().getOid();
		Integer target = trip.destination().zone().getOid();

		if (!offers.containsKey(source) || !offers.get(source).containsKey(target)) {
			return new ArrayList<>();
		}

		assert offers.containsKey(source);
		assert offers.get(source).containsKey(target);

		return offers.get(source).get(target).keySet();
	}

}
