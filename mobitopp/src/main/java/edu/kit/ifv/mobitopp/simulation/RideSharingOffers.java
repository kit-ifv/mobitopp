package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.List;

public class RideSharingOffers {

	protected RideSharingOfferStorage offers = new RideSharingOfferStorage();


	public void add(Trip trip, Person person) {

		assert person.isCarDriver();

		offers.put(trip, new RideSharingOffer(person, person.whichCar(), trip));
	}

	public void remove(Trip trip, Person person) {

		assert offers.get(trip) != null;
		assert offers.get(trip).person == person;

		offers.remove(trip);
	}

	public List<RideSharingOffer> matchingTrips(
		Trip request, 
		int max_minutes_early,
		int max_minutes_late
	) {

		List<RideSharingOffer> matching = new ArrayList<RideSharingOffer>();

		for (Trip trip : offers.keySet(request)) {

			int tripToOffer = Math.toIntExact(trip.startDate().differenceTo(request.startDate()).toMinutes());
			if (trip.origin().zone().getId().equals(request.origin().zone().getId())
					&& trip.destination().zone().getId().equals(request.destination().zone().getId())
					&& tripToOffer <= max_minutes_late
					&& tripToOffer >= (-max_minutes_early)
				)
			{
				RideSharingOffer offer = offers.get(trip);

				assert offer.person.isCarDriver() : (offer.person +  " " + offer.person.getOid());

				if (offer.car.remainingCapacity() > 0) {

					matching.add(offer);
				}
			}
		}

		return matching;
	}

	public int size() {
		return this.offers.size();
	}

}
