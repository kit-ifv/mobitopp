package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;


public class DemandDataForZoneCalculatorStuttgart implements DemandDataForZoneCalculatorIfc {
	
	private final Results results;
	public final DemandCategories categories;

	private Map<HouseholdOfPanelDataId,HouseholdOfPanelData> households
					= new LinkedHashMap<HouseholdOfPanelDataId,HouseholdOfPanelData>();

	private Map<HouseholdOfPanelDataId,List<PersonOfPanelData>> personsOfHousehold 
					= new LinkedHashMap<HouseholdOfPanelDataId,List<PersonOfPanelData>>();

	private final Map<Integer, List<HouseholdOfPanelDataId>> householdCache;

	private FixedDestinationSelector fixedDestinationSelector;

	private HouseholdWeightCalculatorIfc householdWeightCalculator;
	private HouseholdSelectorIfc householdSelector;

	private Gender nextChildGender = Gender.MALE;


	private final CarOwnershipModel carOwnershipModel;

	private final HouseholdLocationSelector householdLocationSelector;
	private final ChargePrivatelySelector chargePrivatelySelector;

	private final PersonCreator personCreator;

	private final DataRepositoryForPopulationSynthesis dataRepository;
	private final Map<Household, HouseholdOfPanelDataId> householdToId;

	public DemandDataForZoneCalculatorStuttgart(
		Results results,
		HouseholdSelectorIfc householdSelector,
		HouseholdWeightCalculatorIfc householdWeightCalculator,
		FixedDestinationSelector fixedDestinationSelector,
		CarOwnershipModel carOwnershipModel,
		HouseholdLocationSelector householdLocationSelector,
		ChargePrivatelySelector chargePrivatelySelector,
		PersonCreator personCreator,
		DataRepositoryForPopulationSynthesis dataRepository
	) {

		this.results = results;
		categories = new DemandCategories();

		this.householdSelector = householdSelector;
		this.householdWeightCalculator = householdWeightCalculator;
		this.fixedDestinationSelector = fixedDestinationSelector;
		this.carOwnershipModel = carOwnershipModel;
		this.dataRepository = dataRepository;

		this.householdLocationSelector = householdLocationSelector;
		this.chargePrivatelySelector = chargePrivatelySelector;
		this.personCreator = personCreator;

		householdToId = new HashMap<>();
		householdCache = new LinkedHashMap<>();

		initCache();
	}

	private void initCache() {

		for (int domcode=1; domcode<=12; domcode++) {

			List<HouseholdOfPanelDataId> householdOfPanelDataIds = retrieveHouseholdIds(domcode);

			for (HouseholdOfPanelDataId id : householdOfPanelDataIds) {

				id.set_weight(1.0);

				this.households.put(id, panelDataRepository().getHousehold(id));
				this.personsOfHousehold.put(id, panelDataRepository().getPersonsOfHousehold(id));
			}
		}
	}

	private PanelDataRepository panelDataRepository() {
		return dataRepository.panelDataRepository();
	}

	private Results results() {
		return results;
	}

	@Override
	public void calculateDemandData(DemandZone zone, ImpedanceIfc impedance) {
		assert zone != null;

		log_calculateDemandData_log1(zone);

		DataForZone dataForZone = calculateDemandDataInternal(zone, impedance);
		saveDomainData(dataForZone);
	}

	private void saveDomainData(DataForZone dataForZone) {
		dataRepository.demandDataRepository().store(dataForZone);
	}

	DataForZone calculateDemandDataInternal(DemandZone zone, ImpedanceIfc impedance) {
		System.out.println("Nachfragedaten Zone "+zone.getId());

		if (getNominalHouseholdDistribution(zone).getTotalAmount() > 0) {
	
			recalculateHouseholdWeights(zone);
	
			List<Household> households = selectAndInstantiateHouseholds_Var1(zone);
			instantiatePoleZones(zone, households);
			createAndAssignCars(households);
			
			logResults(households, impedance);
		}
		return zone.zone().getDemandData();
	}

	private void recalculateHouseholdWeights(DemandZone zone) {

		HouseholdDistribution hhDistribution = getNominalHouseholdDistribution(zone);

		EmploymentDistribution empDistribution = getNominalEmploymentDistribution(zone);
		AgeDistributionIfc maleAgeDistribution = getNominalMaleAgeDistribution(zone);
		AgeDistributionIfc femaleAgeDistribution = getNominalFemaleAgeDistribution(zone);


		List<HouseholdOfPanelDataId> hhIds = new ArrayList<>(this.households.keySet());

		this.householdWeightCalculator.calculateWeights(
					hhDistribution,
					empDistribution,
					maleAgeDistribution,
					femaleAgeDistribution,
					hhIds,
					this.households,
					this.personsOfHousehold	
				);
	}


	private List<Household> selectAndInstantiateHouseholds_Var1(DemandZone zone) {
		List<Household> households = new ArrayList<Household>();

		for (HouseholdDistributionItem householdTypeItem : getNominalHouseholdDistribution(zone)
				.getItemsReverse()) {

			int amount = householdTypeItem.amount();
			int domCodeType = householdTypeItem.type();

			List<HouseholdOfPanelDataId> householdOfPanelDataIds = retrieveHouseholdIds(domCodeType);

			for (HouseholdOfPanelDataId householdOfPanelDataId: 
						this.householdSelector.selectHouseholds(householdOfPanelDataIds,amount)) {

				Household household = instantiateHousehold(zone, 
																										this.households.get(householdOfPanelDataId));
	
				zone.getDemandData().getPopulationData().addHousehold(household);

				households.add(household);
			}
		}

		return households;
	}

	private Household instantiateHousehold(
			DemandZone zone, HouseholdOfPanelData householdOfPanelData) {
		assert zone != null;
		assert householdOfPanelData != null;

		Household household = newHousehold(zone.zone(), householdOfPanelData);
		
		zone.actualDemography().incrementHousehold(householdOfPanelData.domCode());


		List<PersonOfPanelData> personsOfPanelData = this.personsOfHousehold.get(householdOfPanelData.id());

		assert householdOfPanelData.numberOfReportingPersons() == personsOfPanelData.size()
					: ("persons found: " + personsOfPanelData.size() + "\n" + householdOfPanelData);


		for (PersonOfPanelData aPersonOfPanelData: personsOfPanelData) {

			Person person = this.personCreator.createPerson(aPersonOfPanelData, householdOfPanelData, household, zone.zone());

			household.addPerson(person);
			
			zone.actualDemography().incrementAge(person.gender(), person.age());
			zone.actualDemography().incrementEmployment(person.employment());
		}

		for (int i=0; i< household.numberOfNotSimulatedChildren(); i++) {
			zone.actualDemography().incrementAge(this.nextChildGender, 0);
			zone.actualDemography().incrementEmployment(Employment.INFANT);

			this.nextChildGender = (this.nextChildGender == Gender.MALE)
														? Gender.FEMALE : Gender.MALE;
														
		}
		
		log_instantiateHoushold_log1(household);

		return household;
	}


	private void createAndAssignCars(
		Collection<Household> households
	) {

		for (Household household : households) {
			Collection<PrivateCar> cars = this.carOwnershipModel.createCars(household, household.getTotalNumberOfCars());
			household.ownCars(cars);
		}
	}


	Household newHousehold(
		Zone zone,
		HouseholdOfPanelData householdOfPanelData
	) {

		HouseholdOfPanelDataId panel_id = householdOfPanelData.id(); 

		HouseholdId id = new HouseholdId(panel_id.getYear(), panel_id.getHouseholdNumber());

		Location location = this.householdLocationSelector.selectLocation(zone);

		assert householdOfPanelData.size() == householdOfPanelData.numberOfReportingPersons()
																				+ householdOfPanelData.numberOfNotReportingChildren();
		boolean canChargePrivately = chargePrivatelySelector.canChargeAt(zone);

		Household household = new HouseholdForDemand(
																id,
																householdOfPanelData.size(),
																householdOfPanelData.domCode(),
																zone,
																location,
																householdOfPanelData.numberOfNotReportingChildren(),
																householdOfPanelData.numberOfCars(),
																householdOfPanelData.income(),
																canChargePrivately
													);
		householdToId.put(household, panel_id);
		return household;
	}






	private void instantiatePoleZones(DemandZone zone, List<Household> households) {
		Map<Person,PersonOfPanelData> mappedPersons 
			= new LinkedHashMap<Person,PersonOfPanelData>();

		Map<Integer,PersonOfPanelData> panelPersons 
			= new LinkedHashMap<Integer,PersonOfPanelData>();

		 Map<Integer,Person> demandPersons 
			 = new LinkedHashMap<Integer,Person>();

		for (Household household : households) {
			HouseholdOfPanelDataId id = householdToId.get(household);
	
			List<PersonOfPanelData> persons = this.personsOfHousehold.get(id);
	
			TreeMap<Integer,PersonOfPanelData> sortedPersons = new TreeMap<Integer,PersonOfPanelData>();
	
	
			for (PersonOfPanelData p : persons) {
	
				int persnum = p.getId().getPersonNumber();
				sortedPersons.put(persnum, p);
			}
	
			for (Person person : (Collection<Person>) household.getPersons()) {
	
				PersonOfPanelData aPersonOfPanelData = sortedPersons.get(person.getId().getPersonNumber());

				mappedPersons.put(person, aPersonOfPanelData);
				panelPersons.put(person.getOid(), aPersonOfPanelData);
				demandPersons.put(person.getOid(), person);
	
			}
		}
		System.out.println("mapped: " + mappedPersons.size());
		System.out.println("panel: " + panelPersons.size());
		System.out.println("demand: " + demandPersons.size());

		this.fixedDestinationSelector.setFixedDestinations(zone.zone(), demandPersons, panelPersons);
	}


	List<HouseholdOfPanelDataId> retrieveHouseholdIds(int domCodeType_) {
		if (!householdCache.containsKey(domCodeType_)) {
			List<HouseholdOfPanelDataId> householdIds = panelDataRepository().getHouseholdIds(domCodeType_);
			householdCache.put(domCodeType_, householdIds);
		}		
		return householdCache.get(domCodeType_);
	}

	private HouseholdDistribution getNominalHouseholdDistribution(DemandZone zone) {
		return zone.nominalDemography().household();
	}

	private EmploymentDistribution getNominalEmploymentDistribution(DemandZone zone) {
		return zone.nominalDemography().employment();
	}

	private AgeDistributionIfc getNominalMaleAgeDistribution(DemandZone zone) {
		return zone.nominalDemography().maleAge();
	}

	private AgeDistributionIfc getNominalFemaleAgeDistribution(DemandZone zone) {
		return zone.nominalDemography().femaleAge();
	}

	private void logResults(List<Household> households, ImpedanceIfc impedance) {
		for (Household household : households) {
			for (Person person : (Collection<Person>) household.getPersons()) {
				logResultsPerson(person, impedance);
			}
			logResultsHousehold(household);
			logResultsCars(household.whichCars());
		}
	}

	private void logResultsPerson(Person person, ImpedanceIfc impedance) {
		results().write(categories.demanddataResult, person.forLogging(impedance));
		results().write(categories.demanddataResultPerson, person.forLogging(impedance));
	}

	private void logResultsHousehold(Household household) {
		assert household instanceof HouseholdForDemand;
		results().write(categories.demanddataResult, ((HouseholdForDemand)household).forLogging());
		results().write(categories.demanddataResultHousehold, ((HouseholdForDemand)household).forLogging());
	}

	private void logResultsCars(Collection<PrivateCar> cars) {
		for (PrivateCar car : cars) {
			String msg = "C; " +  car.forLogging();

			results().write(categories.demanddataResult, msg);
			results().write(categories.demanddataResultCar, msg);
		}
	}

	private void log_instantiateHoushold_log1(Household household) {
		StringBuffer message = new StringBuffer();

		message.append("Neuer Haushalt ").append("( ");
		message.append("Jahr : ").append(household.getId().getYear());
		message.append("   , Nummer : ").append(household.getId().getHouseholdNumber()).append(")");
		message.append("  Groesse : ").append(household.getSize());

		results().write(categories.demanddataCalculationHouseholdSelection, message.toString());
	}

	void log_calculateDemandData_log1(DemandZone zone) {
		StringBuffer message = new StringBuffer();

		message.append("\n");
		message.append("Zone ").append("(");
		message.append("OID : ").append(zone.getOid()).append(", ");
		message.append("ID : ").append(zone.getId());
		message.append(")");

		results().write(categories.demanddataCalculation, message.toString());
	}
}

