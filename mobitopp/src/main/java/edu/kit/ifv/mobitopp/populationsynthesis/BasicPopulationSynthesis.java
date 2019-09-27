package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.SingleZoneDemandCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.DefaultOpportunityLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.DefaultCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class BasicPopulationSynthesis extends PopulationSynthesis { 

	private final PersonCreator personCreator;
	private final HouseholdLocationSelector householdLocationSelector;
	private final CarOwnershipModel carOwnershipModel;
	private final ChargePrivatelySelector chargePrivatelySelector;
	private final DemandCategories categories;
  private final ActivityScheduleAssigner activityScheduleAssigner;
	private final EconomicalStatusCalculator economicalStatusCalculator;

	public BasicPopulationSynthesis(
			CarOwnershipModel carOwnershipModel,
			HouseholdLocationSelector householdLocationSelector,
			PersonCreator personCreator,
			ActivityScheduleAssigner activityScheduleAssigner,
			EconomicalStatusCalculator economicalStatusCalculator,
			SynthesisContext context
			) {
    this(carOwnershipModel, householdLocationSelector, chargeAlways(), personCreator,
        activityScheduleAssigner, economicalStatusCalculator, context);
	}
	
	private static ChargePrivatelySelector chargeAlways() {
		return (zone) -> true;
	}
	
	public BasicPopulationSynthesis(
		CarOwnershipModel carOwnershipModel,
		HouseholdLocationSelector householdLocationSelector,
		ChargePrivatelySelector chargePrivatelySelector,
		PersonCreator personCreator, 
		ActivityScheduleAssigner activityScheduleAssigner,
		EconomicalStatusCalculator economicalStatusCalculator,
		SynthesisContext context
	) {
		super(context);
		this.personCreator = personCreator;
		this.householdLocationSelector = householdLocationSelector;
		this.chargePrivatelySelector = chargePrivatelySelector;
		this.carOwnershipModel = carOwnershipModel;
		this.activityScheduleAssigner = activityScheduleAssigner;
		this.economicalStatusCalculator = economicalStatusCalculator;
		categories = new DemandCategories();
	}
	
	@Override
	protected DemandDataCalculator createCalculator(
			Map<ActivityType, FixedDistributionMatrix> commuterMatrices) {
		return new SingleZoneDemandCalculator(createZoneCalculator(commuterMatrices),
				demandZoneRepository(), impedance());
	}

	private DemandDataForZoneCalculatorIfc createZoneCalculator(
			Map<ActivityType, FixedDistributionMatrix> fdMatrices) {
		FixedDestinationSelector fixedDestinationSelector = new DefaultFixedDestinationSelector(
																											demandZoneRepository().zoneRepository(),
																											fdMatrices,
																											impedance(),
																											seed()
																									);

		HouseholdSelectorIfc householdSelector = new HouseholdSelector(seed());
		HouseholdWeightCalculatorIfc householdWeightCalculator = new HouseholdWeightCalculator();

		DemandDataForZoneCalculatorIfc calculator =  new DemandDataForZoneCalculatorStuttgart(
																												results(),
																												householdSelector,
																												householdWeightCalculator,
																												fixedDestinationSelector,
																												carOwnershipModel,
																												householdLocationSelector,
																												chargePrivatelySelector,
																												personCreator,
																												activityScheduleAssigner,
																												economicalStatusCalculator(),
																												dataRepository()
																												);
		return calculator;
	}

	protected EconomicalStatusCalculator economicalStatusCalculator() {
		return economicalStatusCalculator;
	}

	// BEGIN CarSharing

	protected void createCarSharingData(
		Zone zone,
		edu.kit.ifv.mobitopp.network.Zone networkZone,
		Random random,
		IdSequence carIds
	) {

		CarSharingDataForZone carSharing = zone.carSharing();
		carSharing.clearCars();

		List<StationBasedCarSharingCar> stationBasedCars = createStationBasedCars(zone, carIds);

		for (StationBasedCarSharingCar car : stationBasedCars) {

			StationBasedCarSharingOrganization company = car.station.carSharingCompany;

			company.ownCar(car, car.station.zone);

			results().write(categories.demanddataResult, car.forLogging());
		}


		for (FreeFloatingCarSharingOrganization company : carSharing.freeFloatingCarSharingCompanies()) {

			List<CarSharingCar> freeFloatingCars = createFreeFloatingCars(company, zone, networkZone, random, carIds);
			company.ownCars(freeFloatingCars, zone);

			for (CarSharingCar car : freeFloatingCars) {
				results().write(categories.demanddataResult, car.forLogging());
			}
		}

	}

	protected static List<CarSharingCar> createFreeFloatingCars(
		FreeFloatingCarSharingOrganization company,
		Zone zone,
		edu.kit.ifv.mobitopp.network.Zone networkZone,
		Random random,
		IdSequence carIds
	) {

		assert networkZone != null;

		CarSharingDataForZone carSharing = zone.carSharing();

		List<CarSharingCar> freeFloatingCars = new ArrayList<CarSharingCar>();

		int numCarsFreeFloating = carSharing.numberOfFreeFloatingCars(company.name());

		for (int i=0; i<numCarsFreeFloating; i++) {

			CarPosition position =	new CarPosition(zone, networkZone.randomLocation(random.nextInt()));

			CarSharingCar car = new DefaultCarSharingCar(
														new ConventionalCar(carIds, position, Car.Segment.MIDSIZE),
														company
													);

			freeFloatingCars.add(car);
		}

		return freeFloatingCars;
	}


	protected static List<StationBasedCarSharingCar> createStationBasedCars(
		Zone zone,
		IdSequence carIds
	) {

		CarSharingDataForZone carSharing = zone.carSharing();

		List<StationBasedCarSharingCar> cars = new ArrayList<StationBasedCarSharingCar>();

		for (CarSharingOrganization company :	carSharing.stationBasedCarSharingCompanies()) {

			for (CarSharingStation station : carSharing.carSharingStations(company.name(), zone)) {

				assert station != null;
				assert station.numberOfCars != null;

				for (int i=0; i<station.numberOfCars; i++) {

					CarPosition position =	new CarPosition(zone, station.location);

					StationBasedCarSharingCar car = new StationBasedCarSharingCar(
																new ConventionalCar(carIds, position, Car.Segment.MIDSIZE),
																company,
																station
															);
		
					cars.add(car);
				}
			}
		}

		return cars;
	}


	protected void createCarSharing(
		SimpleRoadNetwork network,
		Random random,
		IdSequence carIds
	) {


		List<Zone> zones = demandZoneRepository().zoneRepository().getZones();

		for (Zone zone : zones) {


System.out.println("zone " + zone.getId() + " is ready? " + zone.carSharing().isReady());

			Integer zoneid = Integer.parseInt(zone.getId().getExternalId());

			edu.kit.ifv.mobitopp.network.Zone networkZone = network.zones().get(zoneid);

			assert networkZone != null : (zoneid + " / " + zone.getId());

			createCarSharingData(zone, networkZone, random, carIds);
		}
	}
// End CarSharing

// BEGIN DEBUG LOG

	

// END DEBUG LOG
	
	@Override
	protected OpportunityLocationSelector createOpportunityLocationSelector() {
		return new DefaultOpportunityLocationSelector(context());
	}

}
