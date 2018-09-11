package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
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
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;

public class PopulationSynthesis { 

	private final PersonCreator personCreator;
	private final HouseholdLocationSelector householdLocationSelector;
	private final CarOwnershipModel carOwnershipModel;
	private final ChargePrivatelySelector chargePrivatelySelector;
	private final SynthesisContext context;
	private final DemandCategories categories;

	public PopulationSynthesis(
			CarOwnershipModel carOwnershipModel,
			HouseholdLocationSelector householdLocationSelector,
			PersonCreator personCreator, 
			SynthesisContext context
			) {
		this(carOwnershipModel, householdLocationSelector, chargeAlways(), personCreator, context);
	}
	
	private static ChargePrivatelySelector chargeAlways() {
		return (zone) -> true;
	}
	
	public PopulationSynthesis(
		CarOwnershipModel carOwnershipModel,
		HouseholdLocationSelector householdLocationSelector,
		ChargePrivatelySelector chargePrivatelySelector,
		PersonCreator personCreator, 
		SynthesisContext context
	) {
		this.personCreator = personCreator;
		this.householdLocationSelector = householdLocationSelector;
		this.chargePrivatelySelector = chargePrivatelySelector;
		this.carOwnershipModel = carOwnershipModel;
		this.context = context;
		categories = new DemandCategories();
	}

	private void doCreatePopulation(Map<ActivityType, FixedDistributionMatrix> fdMatrices) {

		FixedDestinationSelector fixedDestinationSelector = new DefaultFixedDestinationSelector(
																											demandZoneRepository().zoneRepository(),
																											fdMatrices,
																											impedance(),
																											seed()
																									);

		HouseholdSelectorIfc householdSelector = new HouseholdSelector(seed());
		HouseholdWeightCalculatorIfc householdWeightCalculator = new HouseholdWeightCalculator();

		DemandDataForZoneCalculatorIfc calculator = createDemandDataCalculator(fixedDestinationSelector,
				householdSelector, householdWeightCalculator, results(), this.carOwnershipModel,
				this.householdLocationSelector, this.chargePrivatelySelector, this.personCreator,
				dataRepository());

		printZoneInformation();
		createDemandForZonesWith(calculator);
	}

	private void createDemandForZonesWith(DemandDataForZoneCalculatorIfc calculator) {
		for (DemandZone zone : demandZoneRepository().getZones()) {
			System.out.println("PopulationSynthesis: Zone " + zone.getId());
			calculator.calculateDemandData(zone, impedance());
		}
	}

	private void printZoneInformation() {
		for (DemandZone zone : demandZoneRepository().getZones()) {
			System.out.println("PopulationSynthesis: Zone " + zone.getId() + ", oid=" + zone.getOid());
		}
	}

	protected DemandDataForZoneCalculatorIfc createDemandDataCalculator(
			FixedDestinationSelector fixedDestinationSelector, HouseholdSelectorIfc householdSelector,
			HouseholdWeightCalculatorIfc householdWeightCalculator, Results results,
			CarOwnershipModel carOwnershipModel, HouseholdLocationSelector householdLocationSelector,
			ChargePrivatelySelector chargePrivatelySelector, PersonCreator personCreator,
			DataRepositoryForPopulationSynthesis dataRepository) {
		return new DemandDataForZoneCalculatorStuttgart(
																											results,
																											householdSelector,
																											householdWeightCalculator,
																											fixedDestinationSelector,
																											carOwnershipModel,
																											householdLocationSelector,
																											chargePrivatelySelector,
																											personCreator,
																											dataRepository
																											);
	}

	SynthesisContext context() {
		return context;
	}
	private DataRepositoryForPopulationSynthesis dataRepository() {
		return context.dataRepository();
	}

	private Results results() {
		return context.resultWriter();
	}

	DemandZoneRepository demandZoneRepository() {
		return context.zoneRepository();
	}

	private long seed() {
		return context.seed();
	}

	private ImpedanceIfc impedance() {
		return context.impedance();
	}

	private List<DemandZone> getZones() {
		return demandZoneRepository().getZones();
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

			Integer zoneid = Integer.parseInt(zone.getId().substring(1));

			edu.kit.ifv.mobitopp.network.Zone networkZone = network.zones().get(zoneid);

			assert networkZone != null : (zoneid + " / " + zone.getId());

			createCarSharingData(zone, networkZone, random, carIds);
		}
	}
// End CarSharing

// BEGIN DEBUG LOG

	private static void printNominalDistributions(List<DemandZone> zones) {
		EmploymentDistribution total = new EmploymentDistribution();

		for (DemandZone zone : zones) {
			EmploymentDistribution dist = getNominalEmploymentDistribution(zone);

			for (EmploymentDistributionItem item:
				(Collection<EmploymentDistributionItem>) dist.getItems()) {

				for (int i=0;i<item.amount();i++) {
					total.getItem(item.type()).increment();
				}
			}
		}

		int grand_total = 0;

		System.out.println("\nSoll-Verteilung:");
		for (EmploymentDistributionItem item: (Collection<EmploymentDistributionItem>) total.getItems()) {

			System.out.println(item.type().getTypeAsString() + ": " + item.amount());
			grand_total += item.amount();
		}
		System.out.println("TOTAL\t" + grand_total + "\n");
		System.out.println("\n");
	}

	private static void printActualDistributions(List<DemandZone> zones) {
		EmploymentDistribution total = new EmploymentDistribution();

		for (DemandZone zone : zones) {
			EmploymentDistribution dist = getActualEmploymentDistribution(zone);

			for (EmploymentDistributionItem item:
				(Collection<EmploymentDistributionItem>) dist.getItems()) {

				for (int i=0;i<item.amount();i++) {
					total.getItem(item.type()).increment();
				}
			}
		}

		int grand_total = 0;

		System.out.println("\nIst-Verteilung:");

		for (EmploymentDistributionItem item: (Collection<EmploymentDistributionItem>) total.getItems()) {

			System.out.println(item.type().getTypeAsString() + ": " + item.amount());
			grand_total += item.amount();
		}
		System.out.println("TOTAL\t" + grand_total + "\n");
		System.out.println("\n");
	}

	private static EmploymentDistribution getNominalEmploymentDistribution(DemandZone zone) {
		return zone.nominalDemography().employment();
	}

	private static EmploymentDistribution getActualEmploymentDistribution(DemandZone zone) {
		return zone.actualDemography().employment();
	}

// END DEBUG LOG

	protected void createLocations(OpportunityLocationSelector opportunityLocationSelector) {
		List<Zone> zones = demandZoneRepository().zoneRepository().getZones();
		for (Zone zone : zones) {
			System.out.println(
					"zone " + zone.getId() + " is ready? " + zone.opportunities().locationsAvailable());
			createLocationsForZone(opportunityLocationSelector, zone);
		}
	}

	protected void createLocationsForZone(
			OpportunityLocationSelector opportunityLocationSelector, Zone zone) {
		OpportunityDataForZone opportunities = zone.opportunities();
		opportunities.createLocations(opportunityLocationSelector);

		results().write(categories.demanddataOpportunities, opportunities.forLogging());
	}

	public void createPopulation() {
		Map<ActivityType,FixedDistributionMatrix> fdMatrices = fixedDistributionMatrices();
		assertFixedDistributionMatrices(fdMatrices);
		
		printNominalDistributions(getZones());

		doCreatePopulation(fdMatrices);

		printNominalDistributions(getZones());
		printActualDistributions(getZones());
		
		finishExecution();
	}

	private void finishExecution() {
		try {
			dataRepository().finishExecution();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private Map<ActivityType, FixedDistributionMatrix> fixedDistributionMatrices() {
		System.out.println("Lade Matrizen....");
		Map<ActivityType, FixedDistributionMatrix> fixedDistributionMatrices = dataRepository()
				.fixedDistributionMatrices();
		System.out.println("...geladen!\n");
		return fixedDistributionMatrices;
	}

	private void assertFixedDistributionMatrices(Map<ActivityType, FixedDistributionMatrix> inMatrices) {
		verify(ActivityType.WORK, inMatrices);
		verify(ActivityType.EDUCATION, inMatrices);
	}

	private void verify(
			ActivityType activityType, Map<ActivityType, FixedDistributionMatrix> matrices) {
		if (!matrices.containsKey(activityType)) {
			throw new IllegalStateException("Fixed distribution matrix missing for " + activityType);
		}
	}

}
