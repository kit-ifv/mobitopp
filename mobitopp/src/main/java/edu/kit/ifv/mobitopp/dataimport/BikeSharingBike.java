package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.bikesharing.Bike;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingCompany;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BikeSharingBike implements Bike {

	private final int bikeNumber;
	private final ZoneId startZone;
	private final BikeSharingCompany owner;
	private Person rider;

	public BikeSharingBike(int bikeNumber, ZoneId startZone, BikeSharingCompany owner) {
		this.bikeNumber = bikeNumber;
		this.startZone = startZone;
		this.owner = owner;
	}

	@Override
	public void returnBike(ZoneId zone) {
		owner.returnBike(this, zone);
	}

	@Override
	public String getId() {
		return bikeNumber + "-" + startZone.getExternalId() + "-" + owner.name();
	}

	@Override
	public void use(Person rider, Time time) {
		this.rider = rider;
	}

	@Override
	public void release(Person person, Time time) {
		if (!this.rider.equals(person)) {
			throw warn(new IllegalArgumentException(String
					.format(
							"Another person is not allowed to release the bike. Current user: %s person releasing the bike: %s",
							this.rider, person)), log);
		}
	}

	@Override
	public BikeSharingCompany owner() {
		return owner;
	}

	public int bikeNumber() {
		return bikeNumber;
	}

	public ZoneId startZone() {
		return startZone;
	}

}
