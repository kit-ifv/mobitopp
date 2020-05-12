package edu.kit.ifv.mobitopp.dataimport;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import lombok.ToString;

@ToString
public class FreeFloatingBikeSharingCompany implements BikeSharingCompany {

	private final String companyName;
	private Map<ZoneId, LinkedList<BikeSharingBike>> availableBikes;

	public FreeFloatingBikeSharingCompany(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Sets all given {@link Bike}s as the companies {@link Bike}s. The {@link Bike}s are sorted by
	 * their start {@link Zone} according to {@link BikeSharingBike#startZone()} and their id.
	 * 
	 * @param bikes
	 *          list of {@link Bike}s to add to this company
	 */
	public void own(List<BikeSharingBike> bikes) {
		this.availableBikes = bikes
				.stream()
				.collect(groupingBy(BikeSharingBike::startZone, toCollection(LinkedList::new)));
	}

	@Override
	public String name() {
		return companyName;
	}

	@Override
	public boolean isBikeAvailableFor(Person person) {
		ZoneId currentZone = person.currentActivity().zone().getId();
		return person.isMobilityProviderCustomer(companyName) && availableBikes.containsKey(currentZone)
				&& !availableBikes.get(currentZone).isEmpty();
	}

	@Override
	public Bike bookBikeFor(Person person) {
		if (!isBikeAvailableFor(person)) {
			throw new IllegalStateException("There is no bike available for " + person);
		}
		ZoneId currentZone = person.currentActivity().zone().getId();
		return availableBikes.get(currentZone).removeFirst();
	}

	@Override
	public void returnBike(BikeSharingBike bike, ZoneId zone) {
		if (!this.equals(bike.owner())) {
			throw new IllegalArgumentException("Bike has another owner: " + bike.owner());
		}
		if (!availableBikes.containsKey(zone)) {
			availableBikes.put(zone, new LinkedList<>());
		}
		availableBikes.get(zone).addLast(bike);
	}

}
