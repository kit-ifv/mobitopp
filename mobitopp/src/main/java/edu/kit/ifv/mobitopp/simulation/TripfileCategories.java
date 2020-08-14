package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.simulation.person.Element;

public class TripfileCategories {

	public final Category result;
	public final Category activity;
	public final Category tour;
	public final Category subtour;
	public final Category route;
	public final Category car;
	public final Category stateChange;
	public final Category ptTimes;

	public TripfileCategories() {
		this.result = result();
		this.activity = activity();
		this.tour = tour();
		this.subtour = subtour();
		this.route = route();
		this.car = car();
		this.stateChange = stateChange();
		this.ptTimes = ptTimes();
	}

	private Category result() {
		List<String> header = new ArrayList<>();
		header.add("tripId");
		header.add("legId");
		header.add("personnumber");
		header.add("householdOid");
		header.add("personOid");
		header.add("tripBeginDay");
		header.add("activityNumber");
		header.add("tripBeginTime");
		header.add("previousActivityType");
		header.add("activityType");
		header.add("legMode");
		header.add("mainMode");
		header.add("tripMode");
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
		header.add("previousActivityZone");
		header.add("nextActivityZone");
    header.add("previousActivityStartTime");
    header.add("previousActivityStartDay");
    header.add("activityStartTime");
    header.add("activityStartDay");
		header.add("locationFrom");
		header.add("locationTo");
		header.add("sex");
		header.add("tourNumber");
		header.add("isStartOfTour");
		header.add("tourPurpose");
		header.add("isMainActivity");
		header.add("realEndDay");
		header.add("realEndTime");
		header.add("fromX");
		header.add("fromY");
		header.add("toX");
		header.add("toY");
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
		header.add("X");
		header.add("Y");
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

	private Category stateChange() {
		List<String> header = new ArrayList<>();
		header.add("personOid");
		header.add("day");
		header.add("time");
		header.add("previousState");
		header.add("nextState");
		return new Category("demandsimulationStateChange", header);
	}
	
	private Category ptTimes() {
		List<String> header = new ArrayList<>();
		header.add("personOid");
		Arrays.stream(Element.values()).map(Element::toString).forEach(header::add);
		return new Category("ptTimes", header);
	}
}