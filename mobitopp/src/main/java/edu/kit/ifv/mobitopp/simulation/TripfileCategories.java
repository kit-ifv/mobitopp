package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.result.Category;

public class TripfileCategories {

	public final Category result;
	public final Category activity;
	public final Category tour;
	public final Category subtour;
	public final Category route;
	public final Category car;

	public TripfileCategories() {
		this.result = result();
		this.activity = activity();
		this.tour = tour();
		this.subtour = subtour();
		this.route = route();
		this.car = car();
	}

	private Category result() {
		List<String> header = new ArrayList<>();
		header.add("W");
		header.add("personnumber");
		header.add("householdOid");
		header.add("personOid");
		header.add("tripBeginDay");
		header.add("activityNumber");
		header.add("tripBeginTime");
		header.add("activityType");
		header.add("modeType");
		header.add("vehicleId");
		header.add("tripEndDay");
		header.add("tripEndTime");
		header.add("distanceInKm");
		header.add("durationTrip");
		header.add("sourceZone");
		header.add("targetZone");
		header.add("employmentType");
		header.add("homeZone");
		header.add("activityDuration");
		header.add("locationFrom");
		header.add("locationTo");
		header.add("sex");
		header.add("tourNumber");
		header.add("isStartOfTour");
		header.add("tourPurpose");
		header.add("isMainActivity");
		header.add("carsharing owner");
		return new Category("demandsimulationResult", header);
	}

	private Category activity() {
		List<String> header = new ArrayList<>();
		header.add("A");
		header.add("personOid");
		header.add("startDay");
		header.add("startTime");
		header.add("numberOfWeek");
		header.add("type");
		header.add("duration");
		header.add("zoneId");
		header.add("location");
		return new Category("demandsimulationResultActivity", header);
	}

	private Category tour() {
		List<String> header = new ArrayList<>();
		header.add("T");
		header.add("personOid");
		header.add("forLogging");
		header.add("tourDestinationOid");
		header.add("tourDestinationId");
		header.add("tourMode");
		return new Category("DEMANDSIMULATION.tour", header);
	}

	private Category subtour() {
		List<String> header = new ArrayList<>();
		header.add("S");
		header.add("personOid");
		header.add("tourNumber");
		header.add("tourMode");
		header.add("subtourMode");
		header.add("forLogging");
		header.add("toString");
		return new Category("DEMANDSIMULATION.subtour", header);
	}

	private Category route() {
		List<String> header = new ArrayList<>();
		header.add("R");
		header.add("personOid");
		header.add("carId");
		header.add("sourceZone");
		header.add("targetZone");
		header.add("tripBeginDay");
		header.add("tripBeginTime");
		header.add("tripEndDay");
		header.add("tripEndTime");
		header.add("route");
		return new Category("demandsimulationRoute", header);
	}

	private Category car() {
		List<String> header = new ArrayList<>();
		header.add("C");
		header.add("cartype");
		header.add("carid");
		header.add("carSegment");
		header.add("currentMileage");
		header.add("fuelLevel");
		header.add("batteryLevel");
		header.add("electricCarMode");
		header.add("remainingRange");
		header.add("driveroid");
		header.add("passengers");
		header.add("tripBeginDay");
		header.add("tripBeginTime");
		header.add("activityType");
		header.add("tripEndDay");
		header.add("tripEndTime");
		header.add("distancekm");
		header.add("durationtrip");
		header.add("sourceZone");
		header.add("targetZone");
		header.add("locationCoordinates");
		header.add("locationRoadAccessEdgeId");
		header.add("locationRoadPosition");
		return new Category("demandsimulationResultCar", header);
	}
}