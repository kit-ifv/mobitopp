package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.RideSharingOffers;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.ZoneBasedRouteChoice;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingPerson;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Passenger;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.time.Time;

public interface SimulationPerson 
	extends Person, Passenger, CarSharingPerson {

		
	public ActivityIfc currentActivity();
	public TripIfc currentTrip();
	public void currentTrip(TripIfc trip);

	public boolean hasNextActivity();
	public ActivityIfc nextActivity();
	public boolean nextActivityStartsAfterSimulationEnd();

	public boolean rideOfferAccepted();
	public void acceptRideOffer();

	public PersonState currentState();

	public SimulationOptions options();


	public void startTrip(
		ImpedanceIfc impedance,
		TripIfc trip,
		Time date
	);

	public void endTrip(
  	ImpedanceIfc impedance,
  	ReschedulingStrategy rescheduling,
  	Time currentDate
	);

	public void startActivity(
		TripIfc previousTrip,
  	ReschedulingStrategy rescheduling,
  	Time currentDate
	);

	public void endActivity();

	public void selectDestinationAndMode(
		DestinationChoiceModel targetSelector,
		TourBasedModeChoiceModel modeChoiceModel,
		ImpedanceIfc impedance,
		boolean passengerAsOption
	);
	
	public void offerRide(
		Time currentDate,
		SimulationOptions options
	);

	public void revokeRideOffer(
		RideSharingOffers rideOffers,
		TripIfc trip,
		Time currentTime
	);

	public boolean findAndAcceptBestMatchingRideOffer(
		RideSharingOffers rideOffers,
		TripIfc trip,
		int max_difference_minutes
	);


	public void selectRoute(
		ZoneBasedRouteChoice routeChoice,
		TripIfc trip,
		Time date
	);

	public  void allocateCar(
		ImpedanceIfc impedance,
		TripIfc trip,
		Time time
	);

	public void notify(
		EventQueue queue,
		DemandSimulationEventIfc event, 
		Time currentDate
	);
	
	void enterFirstStop(Time time);
	public boolean isPublicTransportVehicleAvailable(Time time);
	public boolean hasPlaceInPublicTransportVehicle();
	public void changeToNewTrip(Time time);
	public void boardPublicTransportVehicle(Time time);
	public void getOffPublicTransportVehicle(Time time);
	public boolean hasArrivedAtNextActivity();
	public void wait(Time currentTime);
	public boolean hasPublicTransportVehicleDeparted(Time time);

}
