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
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.DefaultHouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;


public class DemandDataForZoneCalculatorStuttgart implements DemandDataForZoneCalculatorIfc {
	
  private static final AttributeType domCode = StandardAttribute.domCode;
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
	private final Map<HouseholdForSetup, HouseholdOfPanelDataId> householdToId;

  private int householdIds = 1;
  private final ActivityScheduleAssigner activityProgramAssigner;
  private final EconomicalStatusCalculator economicalStatusCalculator;

	public DemandDataForZoneCalculatorStuttgart(
		Results results,
		HouseholdSelectorIfc householdSelector,
		HouseholdWeightCalculatorIfc householdWeightCalculator,
		FixedDestinationSelector fixedDestinationSelector,
		CarOwnershipModel carOwnershipModel,
		HouseholdLocationSelector householdLocationSelector,
		ChargePrivatelySelector chargePrivatelySelector,
		PersonCreator personCreator,
		ActivityScheduleAssigner activityProgramAssigner,
		EconomicalStatusCalculator economicalStatusCalculator,		
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
		this.activityProgramAssigner = activityProgramAssigner;
		this.economicalStatusCalculator = economicalStatusCalculator;

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

		calculateDemandDataInternal(zone);
	}

	public void saveDemandData(DemandZone zone) {
		dataRepository.demandDataRepository().store(zone);
	}

	void calculateDemandDataInternal(DemandZone zone) {
		System.out.println("Nachfragedaten Zone "+zone.getId());

		if (getNominalHouseholdDistribution(zone).getTotalAmount() > 0) {
	
			recalculateHouseholdWeights(zone);
	
			List<HouseholdForSetup> households = selectAndInstantiateHouseholds_Var1(zone);
      assignActivityPrograms(households);
			instantiatePoleZones(zone, households);
			createAndAssignCars(households);
		}
	}

	private void recalculateHouseholdWeights(DemandZone zone) {

		RangeDistributionIfc hhDistribution = getNominalHouseholdDistribution(zone);

		EmploymentDistribution empDistribution = getNominalEmploymentDistribution(zone);
		RangeDistributionIfc maleAgeDistribution = getNominalMaleAgeDistribution(zone);
		RangeDistributionIfc femaleAgeDistribution = getNominalFemaleAgeDistribution(zone);


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


	private List<HouseholdForSetup> selectAndInstantiateHouseholds_Var1(DemandZone zone) {
		List<HouseholdForSetup> households = new ArrayList<>();

		for (RangeDistributionItem householdTypeItem : getNominalHouseholdDistribution(zone)
				.getItemsReverse()) {

			int amount = householdTypeItem.amount();
			int domCodeType = householdTypeItem.lowerBound();

			List<HouseholdOfPanelDataId> householdOfPanelDataIds = retrieveHouseholdIds(domCodeType);

			for (HouseholdOfPanelDataId householdOfPanelDataId: 
						this.householdSelector.selectHouseholds(householdOfPanelDataIds,amount)) {

				HouseholdForSetup household = instantiateHousehold(zone, 
																										this.households.get(householdOfPanelDataId));
	
				zone.getPopulation().addHousehold(household);

				households.add(household);
			}
		}

		return households;
	}

	private HouseholdForSetup instantiateHousehold(
			DemandZone zone, HouseholdOfPanelData householdOfPanelData) {
		assert zone != null;
		assert householdOfPanelData != null;

		HouseholdForSetup household = newHousehold(zone.zone(), householdOfPanelData);
		
		incrementHousehold(zone, householdOfPanelData);


		List<PersonOfPanelData> personsOfPanelData = this.personsOfHousehold.get(householdOfPanelData.id());

		assert householdOfPanelData.numberOfReportingPersons() == personsOfPanelData.size()
					: ("persons found: " + personsOfPanelData.size() + "\n" + householdOfPanelData);


		for (PersonOfPanelData aPersonOfPanelData: personsOfPanelData) {

			PersonBuilder person = this.personCreator.createPerson(aPersonOfPanelData, householdOfPanelData, household, zone.zone());
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
	
	private void assignActivityPrograms(List<HouseholdForSetup> households) {
	  for (HouseholdForSetup toHousehold : households) {
      activityProgramAssigner.assignActivitySchedule(toHousehold);
    }
	}

	private void createAndAssignCars(
		Collection<HouseholdForSetup> households
	) {

		for (HouseholdForSetup household : households) {
			Collection<PrivateCarForSetup> cars = this.carOwnershipModel.createCars(household);
			household.ownCars(cars);
		}
	}


	HouseholdForSetup newHousehold(
		Zone zone,
		HouseholdOfPanelData householdOfPanelData
	) {

		HouseholdOfPanelDataId panel_id = householdOfPanelData.id(); 

		int oid = householdIds++;
    short year = panel_id.getYear();
    long householdNumber = panel_id.getHouseholdNumber();
    HouseholdId id = new HouseholdId(oid, year, householdNumber);

		Location location = this.householdLocationSelector.selectLocation(zone);

		assert householdOfPanelData.size() == householdOfPanelData.numberOfReportingPersons()
																				+ householdOfPanelData.numberOfNotReportingChildren();
		boolean canChargePrivately = chargePrivatelySelector.canChargeAt(zone);

		EconomicalStatus economicalStatus = economicalStatusCalculator
				.calculateFor(householdOfPanelData.size(), householdOfPanelData.numberOfMinors(),
						householdOfPanelData.income());
		HouseholdForSetup household = new DefaultHouseholdForSetup(
																id,
																householdOfPanelData.size(),
																householdOfPanelData.domCode(),
																zone,
																location,
																householdOfPanelData.numberOfMinors(),
																householdOfPanelData.numberOfNotReportingChildren(),
																householdOfPanelData.numberOfCars(),
																householdOfPanelData.income(),
																householdOfPanelData.incomeClass(),
																economicalStatus,
																canChargePrivately
													);
		householdToId.put(household, panel_id);
		return household;
	}






	private void instantiatePoleZones(DemandZone zone, List<HouseholdForSetup> households) {
    Map<PersonBuilder, PersonOfPanelData> mappedPersons = new LinkedHashMap<>();

    Map<Integer, PersonOfPanelData> panelPersons = new LinkedHashMap<>();
    Map<Integer, PersonBuilder> demandPersons = new LinkedHashMap<>();

		for (HouseholdForSetup household : households) {
			HouseholdOfPanelDataId id = householdToId.get(household);
	
			List<PersonOfPanelData> persons = this.personsOfHousehold.get(id);
	
			TreeMap<Integer,PersonOfPanelData> sortedPersons = new TreeMap<Integer,PersonOfPanelData>();
	
	
			for (PersonOfPanelData p : persons) {
	
				int persnum = p.getId().getPersonNumber();
				sortedPersons.put(persnum, p);
			}
	
			for (PersonBuilder person : household.getPersons()) {
	
				PersonOfPanelData aPersonOfPanelData = sortedPersons.get(person.getId().getPersonNumber());

				mappedPersons.put(person, aPersonOfPanelData);
				panelPersons.put(person.getId().getOid(), aPersonOfPanelData);
				demandPersons.put(person.getId().getOid(), person);
	
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

  private void incrementHousehold(DemandZone zone, HouseholdOfPanelData householdOfPanelData) {
    zone.actualDemography().increment(domCode, householdOfPanelData.domCode());
  }

	private RangeDistributionIfc getNominalHouseholdDistribution(DemandZone zone) {
    return zone.nominalDemography().getDistribution(domCode);
	}

	private EmploymentDistribution getNominalEmploymentDistribution(DemandZone zone) {
		return zone.nominalDemography().employment();
	}

	private RangeDistributionIfc getNominalMaleAgeDistribution(DemandZone zone) {
		return zone.nominalDemography().maleAge();
	}

	private RangeDistributionIfc getNominalFemaleAgeDistribution(DemandZone zone) {
		return zone.nominalDemography().femaleAge();
	}

	private void log_instantiateHoushold_log1(HouseholdForSetup household) {
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
		message.append("OID : ").append(zone.getId().getMatrixColumn()).append(", ");
		message.append("ID : ").append(zone.getId().getExternalId());
		message.append(")");

		results().write(categories.demanddataCalculation, message.toString());
	}
}

