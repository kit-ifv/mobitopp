package edu.kit.ifv.mobitopp.simulation.carsharing;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.CarSharingListener;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import java.io.IOException;
import java.lang.ClassNotFoundException;


public class CarSharingDataForZone implements Serializable {

	private static final long serialVersionUID = 1L;

	protected final static Random random = new Random(1234);
	static final float defaultDensity = 0.0f;

	protected final Zone zone;

	transient protected Map<String,StationBasedCarSharingOrganization> stationBasedCarSharing;
	transient protected Map<String,FreeFloatingCarSharingOrganization> freeFloatingCarSharing;

	protected List<String> stationBasedCarSharingCompanies = new ArrayList<String>();
	protected List<String> freeFloatingCarSharingCompanies = new ArrayList<String>();

	protected final Map<String,List<CarSharingStation>> carSharingStations;

	protected	final Map<String, Boolean> freeFloatingArea;
	protected final Map<String, Integer> freeFloatingCars;
	protected final Map<String, Float> carsharingCarDensities;



	public CarSharingDataForZone(
		Zone zone,
		List<StationBasedCarSharingOrganization> stationBasedCarSharingCompanies,
		Map<String,List<CarSharingStation>> carSharingStations,
		List<FreeFloatingCarSharingOrganization> freeFloatingCarSharingCompanies,
		Map<String, Boolean> freeFloatingArea,
		Map<String, Integer> freeFloatingCars,
		Map<String, Float> carsharingCarDensities
	) {

		assert stationBasedCarSharingCompanies != null;
		assert freeFloatingCarSharingCompanies != null;

		assert carSharingStations != null : ("carSharingStations == null");

		this.zone = zone;

		this.stationBasedCarSharing = new LinkedHashMap<String,StationBasedCarSharingOrganization>();
		this.freeFloatingCarSharing = new LinkedHashMap<String,FreeFloatingCarSharingOrganization>();

		this.carSharingStations = carSharingStations;

		this.freeFloatingArea = Collections.unmodifiableMap(freeFloatingArea);
		this.freeFloatingCars = Collections.unmodifiableMap(freeFloatingCars);
		this.carsharingCarDensities = Collections.unmodifiableMap(carsharingCarDensities);

		for (StationBasedCarSharingOrganization company: stationBasedCarSharingCompanies) {

			if (!this.stationBasedCarSharingCompanies.contains(company.name())) { 
				this.stationBasedCarSharingCompanies.add(company.name());
				this.stationBasedCarSharing.put(company.name(), company);
			}

			assert carSharingStations.get(company.name()) != null : ("carSharingStations(company) == null: " + company);
		}

		for (FreeFloatingCarSharingOrganization company: freeFloatingCarSharingCompanies) {

			if (!this.freeFloatingCarSharingCompanies.contains(company.name())) {
				this.freeFloatingCarSharingCompanies.add(company.name());
				this.freeFloatingCarSharing.put(company.name(), company);
			}
		}

		assert this.stationBasedCarSharing != null;
	}

	// used for unit test
	CarSharingDataForZone(
		Zone zone,
		HashMap<String, Boolean> freeFloatingArea
	) {
		this(
			zone,
			new ArrayList<StationBasedCarSharingOrganization>(),
			new HashMap<String,List<CarSharingStation>>(),
			new ArrayList<FreeFloatingCarSharingOrganization>(),
			freeFloatingArea,
			new HashMap<String, Integer>(),
			new HashMap<String, Float>()
		);
	}


	public boolean isStationBasedCarSharingCarAvailable(CarSharingPerson person) {
		assert this.stationBasedCarSharing != null;

		return !availableStationBasedCarSharingCompanies(person).isEmpty();
	}

	public boolean isFreeFloatingCarSharingCarAvailable(CarSharingPerson person) {
		assert this.freeFloatingCarSharing != null;

		return !availableFreeFloatingCarSharingCompanies(person).isEmpty();
	}

	public boolean isFreeFloatingZone(CarSharingCar car) {

			String company = car.owner().name();

		return this.freeFloatingArea.containsKey(company) && this.freeFloatingArea.get(company);
	}

	public List<CarSharingOrganization> availableStationBasedCarSharingCompanies(CarSharingPerson person) {
		assert this.stationBasedCarSharing != null;

		List<CarSharingOrganization> availableCompanies = new ArrayList<CarSharingOrganization>();

		for (String companyName: this.stationBasedCarSharing.keySet()) {

			CarSharingOrganization company = stationBasedCarSharing.get(companyName);

			if ( person.isCarSharingCustomer(companyName)
						&& company.isCarAvailable(this.zone) 
			) {
				availableCompanies.add(company);
			}
		}

		return availableCompanies;
	}

	public List<CarSharingOrganization> availableFreeFloatingCarSharingCompanies(CarSharingPerson person) {
		assert this.freeFloatingCarSharing != null;

		List<CarSharingOrganization> availableCompanies = new ArrayList<CarSharingOrganization>();

		for (String companyName: this.freeFloatingCarSharing.keySet()) {

			CarSharingOrganization company = freeFloatingCarSharing.get(companyName);

			if ( person.isCarSharingCustomer(companyName)
						&& isFreeFloatingZone(companyName)
						&& company.isCarAvailable(this.zone) 
			) {
				availableCompanies.add(company);
			}
		}

		return availableCompanies;
	}

	public boolean isFreeFloatingZone(String company) {

		if (!this.freeFloatingArea.containsKey(company)) {
			return false;
		}

		return this.freeFloatingArea.get(company);
	}


	public List<StationBasedCarSharingOrganization> stationBasedCarSharingCompanies() {

		assert this.stationBasedCarSharing != null;

		return new ArrayList<StationBasedCarSharingOrganization>(this.stationBasedCarSharing.values());
	}

	public List<FreeFloatingCarSharingOrganization> freeFloatingCarSharingCompanies() {
		assert this.freeFloatingCarSharing != null;

		return new ArrayList<FreeFloatingCarSharingOrganization>(this.freeFloatingCarSharing.values());
	}


	public List<CarSharingStation> carSharingStations(
		String company,
		Zone zone
	) {
		assert this.carSharingStations.containsKey(company);

		List<CarSharingStation> stations = new ArrayList<CarSharingStation>();

		for (CarSharingStation station : this.carSharingStations.get(company)) {

			if (station.zone == zone) {
				stations.add(station);
			}
		}

		return stations;
	}

	public CarSharingCar bookStationBasedCar(CarSharingPerson person) {
		assert isStationBasedCarSharingCarAvailable(person);
		List<CarSharingOrganization> companies = availableStationBasedCarSharingCompanies(person);
		CarSharingOrganization company = selectCompanyRandomly(companies);
		return company.bookCar(this.zone);
	}

	public CarSharingCar bookFreeFloatingCar(CarSharingPerson person) {
		assert isFreeFloatingCarSharingCarAvailable(person);
		
		List<CarSharingOrganization> companies = availableFreeFloatingCarSharingCompanies(person);
		CarSharingOrganization company = selectCompanyRandomly(companies);

		return company.bookCar(this.zone);
	}

	protected CarSharingOrganization selectCompanyRandomly(
		List<CarSharingOrganization> companies
	) {

		assert companies != null;
		assert companies.size() > 0;


		double rand = random.nextDouble();

		int index = (int) Math.floor(companies.size()*rand);

		return companies.get(index);
	}

	// Serialization
	private static Map<String,StationBasedCarSharingOrganization> serializedStationBasedCarSharing
		= new HashMap<String,StationBasedCarSharingOrganization>();
	private static Map<String,StationBasedCarSharingOrganization> deserializedStationBasedCarSharing
		= new HashMap<String,StationBasedCarSharingOrganization>();

	private static Map<String,FreeFloatingCarSharingOrganization> serializedFreeFloatingCarSharing
		= new HashMap<String,FreeFloatingCarSharingOrganization>();
	private static Map<String,FreeFloatingCarSharingOrganization> deserializedFreeFloatingCarSharing
		= new HashMap<String,FreeFloatingCarSharingOrganization>();


	private void writeObject(java.io.ObjectOutputStream out)
     throws IOException
	{
		out.defaultWriteObject();

		for (String companyName : this.freeFloatingCarSharingCompanies) {

			if (!serializedFreeFloatingCarSharing.containsKey(companyName)) {

				FreeFloatingCarSharingOrganization company = this.freeFloatingCarSharing.get(companyName);
				out.writeObject(company);

				serializedFreeFloatingCarSharing.put(companyName, company);
			}
		}

		for (String companyName : this.stationBasedCarSharingCompanies) {

			if (!serializedStationBasedCarSharing.containsKey(companyName)) {

				StationBasedCarSharingOrganization company = this.stationBasedCarSharing.get(companyName);
				out.writeObject(company);

				serializedStationBasedCarSharing.put(companyName, company);
			}
		}
	}

	private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		if (this.freeFloatingCarSharing == null) {
			this.freeFloatingCarSharing = new HashMap<String,FreeFloatingCarSharingOrganization>();
		}
		assert  this.freeFloatingCarSharing != null : ("freeFloatingCarSharing == null");	

		for (String companyName :  freeFloatingCarSharingCompanies) {

			if (!deserializedFreeFloatingCarSharing.containsKey(companyName)) {

System.out.println("reading ff '" + companyName + "'");

				FreeFloatingCarSharingOrganization company = (FreeFloatingCarSharingOrganization) in.readObject();

				assert company != null;

				deserializedFreeFloatingCarSharing.put(companyName, company);

System.out.println("ff companies: " + deserializedFreeFloatingCarSharing.size());
			}

			FreeFloatingCarSharingOrganization company = deserializedFreeFloatingCarSharing.get(companyName);

			assert company != null;

			this.freeFloatingCarSharing.put(companyName, company);
		}

		if (this.stationBasedCarSharing == null) {
			this.stationBasedCarSharing = new HashMap<String,StationBasedCarSharingOrganization>();
		}
		assert  this.stationBasedCarSharing != null : ("stationBasedCarSharing == null");	

		for (String companyName :  stationBasedCarSharingCompanies) {

			if (!deserializedStationBasedCarSharing.containsKey(companyName)) {

System.out.println("reading sb " + companyName);

				StationBasedCarSharingOrganization company = (StationBasedCarSharingOrganization) in.readObject();

				assert company != null;

				deserializedStationBasedCarSharing.put(companyName, company);
System.out.println("sb companies: " + deserializedStationBasedCarSharing.size());
			}

			StationBasedCarSharingOrganization company = deserializedStationBasedCarSharing.get(companyName);

			assert company != null;

			this.stationBasedCarSharing.put(companyName, company);
		}

		isReady();
	}

// END Serialization


	public boolean isReady() {

		assert stationBasedCarSharing != null;
		assert freeFloatingCarSharing != null;

		return stationBasedCarSharing != null && freeFloatingCarSharing != null;
	}

	public String forLogging() {

		String s = "Zone " + zone.getId().getExternalId() + "\n";

		s += "station based companies:\n";
		for (String company : this.stationBasedCarSharingCompanies) {
			s += company + "\n";
			StationBasedCarSharingOrganization org = this.stationBasedCarSharing.get(company);
			s += org.availableCars(zone) + " cars available\n";
		}
		s += "free floating companies:\n";
		for (String company : this.freeFloatingCarSharingCompanies) {
			s += company + "\n";
			FreeFloatingCarSharingOrganization org = this.freeFloatingCarSharing.get(company);
			s += org.availableCars(zone) + " cars available\n";
		}

		return s;
	}

	public int numberOfFreeFloatingCars(String company) {
		assert freeFloatingCarSharingCompanies.contains(company);

		if(!this.freeFloatingCars.containsKey(company)) {
			return 0;
		}

		return this.freeFloatingCars.get(company);
	}

	public float carsharingcarDensity(String company) {
		return carsharingCarDensities.getOrDefault(company, defaultDensity);
	}

	public void clearCars() {

		for(CarSharingOrganization company : this.stationBasedCarSharing.values()) {
			company.clearCars(this.zone);
		}
		for(CarSharingOrganization company : this.freeFloatingCarSharing.values()) {
			company.clearCars(this.zone);
		}
	}

	public void register(CarSharingListener listener) {
		for (CarSharingOrganization organization : freeFloatingCarSharingCompanies()) {
			organization.register(listener);
		}
		for (CarSharingOrganization organization : stationBasedCarSharingCompanies()) {
			organization.register(listener);
		}
	}

	public String toString() {
		String data = "zone=" + zone.getId()
								+ "\n" + freeFloatingCarSharingCompanies
								+ "\n" + freeFloatingCarSharing
								+ "\n" + freeFloatingArea
								+ "\n" + freeFloatingCars // das ist die Basisanzahl, nicht die aktuell vorhandenen
								+ "\n" + forLogging()
								;
		return data;
	}

}

