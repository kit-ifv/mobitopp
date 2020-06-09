package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.AssignCars;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ElectricCarOwnershipBasedOnSociodemographic;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.GenericElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.LogitBasedCarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ProbabilityForElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.DemandRegionBasedZoneSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.DemandRegionDestinationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.HasWorkActivity;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.ZoneSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.ZoneBasedHouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ProbabilityBasedSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.ZoneBasedOpportunitySelector;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionOdPairCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionOdPairSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionRelationsParser;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionRelationsRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.region.HouseholdBasedStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PanelDistanceSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PersonBasedStep;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPersonCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemographyOnDifferentLevelsExample extends PopulationSynthesis {

	public DemographyOnDifferentLevelsExample(SynthesisContext context) {
		super(context);
	}

	@Override
	protected DemandDataCalculator createCalculator(
			Map<ActivityType, FixedDistributionMatrix> commuterMatrices) {
		DemandRegionRelationsRepository relationsRepository = loadRelations();
		LinkedList<PopulationSynthesisStep> steps = new LinkedList<>();
		steps.add(activityScheduleAssignment());
		steps.add(workDestinationSelector(relationsRepository));
		steps.add(educationDestinationSelector());
		steps.add(createCarOwnershipModel());
		steps.add(storeData());
		steps.add(cleanData());
		DemandDataForDemandRegionCalculator regionCalculator = createZoneCalculator();
		return new DemandRegionDemandCalculator(regions(), regionCalculator, steps, impedance());
	}

	private List<DemandRegion> regions() {
		return context()
				.dataRepository()
				.demandRegionRepository()
				.getRegionsOf(RegionalLevel.community);
	}

	private DemandRegionRelationsRepository loadRelations() {
		DemandRegionRepository regions = context().dataRepository().demandRegionRepository();
		File communityRelationsFile = context()
				.experimentalParameters()
				.valueAsFile("communityCommuters");
		return new DemandRegionRelationsParser(RegionalLevel.community, regions)
				.parse(communityRelationsFile);
	}

	private PopulationSynthesisStep workDestinationSelector(
			final DemandRegionRelationsRepository relationsRepository) {
		PanelDataRepository dataRepository = context().dataRepository().panelDataRepository();
		DemandRegionOdPairSelector communityPairCreator = DemandRegionOdPairCreator
				.forWork(relationsRepository);
		ImpedanceIfc impedance = impedance();
		int range = context().experimentalParameters().valueAsInteger("poleDistanceRange");
		DemandRegionOdPairSelector odPairSelector = new PanelDistanceSelector(dataRepository,
				communityPairCreator, impedance, range);
		ZoneSelector zoneSelector = new DemandRegionBasedZoneSelector(relationsRepository, newRandom());
		Predicate<PersonBuilder> personFilter = new HasWorkActivity();
		return new DemandRegionDestinationSelector(odPairSelector, zoneSelector, personFilter,
				newRandom());
	}

	private PopulationSynthesisStep educationDestinationSelector() {
		return new PersonBasedStep(new EducationInHomeZone(newRandom()));
	}

	private PopulationSynthesisStep activityScheduleAssignment() {
		return new HouseholdBasedStep(activityScheduleAssigner()::assignActivitySchedule);
	}

	private DemandDataForDemandRegionCalculator createZoneCalculator() {
		AttributeType attributeType = StandardAttribute.householdSize;
		WeightedHouseholdSelector householdSelector = createHouseholdSelector();
		HouseholdCreator householdCreator = createHouseholdCreator();
		PersonCreator personCreator = createPersonCreator();
		Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter = z -> h -> true;
		return new DemandRegionBasedIpu(results(), householdSelector, householdCreator, personCreator, dataRepository(),
				context(), attributeType, householdFilter);
	}

	private ActivityScheduleAssigner activityScheduleAssigner() {
		PanelDataRepository panelDataRepository = context().dataRepository().panelDataRepository();
		ActivityScheduleCreator scheduleCreator = new DefaultActivityScheduleCreator();
		return new DefaultActivityAssigner(panelDataRepository, scheduleCreator);
	}

	private WeightedHouseholdSelector createHouseholdSelector() {
		return new ProbabilityBasedSelector(newRandom());
	}

	private DoubleSupplier newRandom() {
		return new Random(context().seed())::nextDouble;
	}

	private HouseholdBasedStep createCarOwnershipModel() {
		AssignCars assigner = new AssignCars(loadCarOwnership());
		return new HouseholdBasedStep(assigner::assignCars);
	}

  private CarOwnershipModel loadCarOwnership() {
  	IdSequence carIDs = new IdSequence();
    long seed = context().seed();
    File engineFile = context().carEngineFile();
    String ownershipFile = context().configuration().getCarOwnership().getOwnership();
    String segmentFile = context().configuration().getCarOwnership().getSegment();
    ImpedanceIfc impedance = context().impedance();
    CarSegmentModel segmentModel = new LogitBasedCarSegmentModel(impedance, seed, segmentFile);
    ProbabilityForElectricCarOwnershipModel calculator = new ElectricCarOwnershipBasedOnSociodemographic(
        impedance, engineFile.getAbsolutePath());
    return new GenericElectricCarOwnershipModel(carIDs, segmentModel, seed, calculator,
        ownershipFile);
  }

  private HouseholdCreator createHouseholdCreator() {
		HouseholdLocationSelector householdLocationSelector = createHouseholdLocationSelector();
		ChargePrivatelySelector chargePrivatelySelector = createChargePrivatelySelector();
		EconomicalStatusCalculator economicalStatusCalculator = createEconomicalStatusCalculator();
		return new DefaultHouseholdCreator(householdLocationSelector, economicalStatusCalculator,
				chargePrivatelySelector);
	}

	private EconomicalStatusCalculator createEconomicalStatusCalculator() {
		return EconomicalStatusCalculators.oecd2017();
	}

	private HouseholdLocationSelector createHouseholdLocationSelector() {
		return new ZoneBasedHouseholdLocationSelector(context());
	}

	private ChargePrivatelySelector createChargePrivatelySelector() {
		return new AllowChargingProbabilityBased(seed());
	}

	private PersonCreator createPersonCreator() {
		CommutationTicketModelIfc commuterTicketModel = commuterTickets();
		Map<String, MobilityProviderCustomerModel> carSharing = context().carSharing();
		return new EmobilityPersonCreator(commuterTicketModel, carSharing, seed());
	}

	private CommutationTicketModelIfc commuterTickets() {
    String commuterTicketFile = context().configuration().getCommuterTicket();
    return new CommutationTicketModelStuttgart(commuterTicketFile, context().seed());
	}

	@Override
	protected OpportunityLocationSelector createOpportunityLocationSelector() {
		return new ZoneBasedOpportunitySelector(context());
	}

}
