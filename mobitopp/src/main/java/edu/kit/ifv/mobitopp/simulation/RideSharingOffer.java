package edu.kit.ifv.mobitopp.simulation;

import java.util.Comparator;

import edu.kit.ifv.mobitopp.time.Time;

public class RideSharingOffer {

	public final Person person;
	public final Car car;
	public final Trip trip;


	public RideSharingOffer(
		Person person,
		Car car,
		Trip trip
	) {
		this.person = person;
		this.car = car;
		this.trip = trip;
	}

	public static Comparator<RideSharingOffer> comparator(final Time date) {

		Comparator<RideSharingOffer> comp = new Comparator<RideSharingOffer>() {

			public boolean equals(Object obj) {
				return obj == this;
			}

			public int compare(RideSharingOffer o1, RideSharingOffer o2) {

				long diff1 = differenceInSeconds(date, o1);
				long diff2 = differenceInSeconds(date, o2);
			
				if (diff1 == diff2) {	
					return o1.person.getOid() < o2.person.getOid() ? -1 : 1;
				}

				return diff1 < diff2 ? -1 : 1;
			}

		};
		

		return comp;
	}

	public static Comparator<RideSharingOffer> comparator(
		final Time date, 
		final Household household
	) {

		Comparator<RideSharingOffer> comp = new Comparator<RideSharingOffer>() {

			public boolean equals(Object obj) {
				return obj == this;
			}

			public int compare(RideSharingOffer o1, RideSharingOffer o2) {

				long diff1 = differenceInSeconds(date, o1);
				long diff2 = differenceInSeconds(date, o2);

				if (household.getOid() == o1.person.household().getOid()
						&& household.getOid() != o2.person.household().getOid()) {
					return -1;
				}

				if (household.getOid() != o1.person.household().getOid()
						&& household.getOid() == o2.person.household().getOid()) {
					return 1;
				}
			
				if (diff1 == diff2) {	
					return o1.person.getOid() < o2.person.getOid() ? -1 : 1;
				}

				return diff1 < diff2 ? -1 : 1;
			}

		};

		return comp;
	}

	private static long differenceInSeconds(final Time date, RideSharingOffer offer) {
		return Math.abs(date.differenceTo(offer.trip.startDate()).seconds());
	}

} 
