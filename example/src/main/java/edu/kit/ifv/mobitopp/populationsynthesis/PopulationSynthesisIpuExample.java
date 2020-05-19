package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.AssignCars;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ElectricCarOwnershipBasedOnSociodemographic;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.GenericElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.LogitBasedCarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ProbabilityForElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.community.IsolatedZonesCommunityDemandCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.community.CommunityOdPairCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.community.CommunityRelationsParser;
import edu.kit.ifv.mobitopp.populationsynthesis.community.CommunityZoneMappingParser;
import edu.kit.ifv.mobitopp.populationsynthesis.community.DefaultCommunityRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.community.HouseholdBasedStep;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPairSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.community.PersonBasedStep;
import edu.kit.ifv.mobitopp.populationsynthesis.community.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.CommunityBasedZoneSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.CommunityDestinationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.HasWorkActivity;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.PanelDistanceSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations.ZoneSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.ZoneBasedHouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ProbabilityBasedSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.ZoneBasedOpportunitySelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPersonCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class PopulationSynthesisIpuExample extends PopulationSynthesis {

	public PopulationSynthesisIpuExample(SynthesisContext context) {
		super(context);
	}

	@Override
	protected DemandDataCalculator createCalculator(
			Map<ActivityType, FixedDistributionMatrix> commuterMatrices) {
		DefaultCommunityRepository communityRepository = loadCommunities();
		LinkedList<PopulationSynthesisStep> steps = new LinkedList<>();
		steps.add(activityScheduleAssignment());
		steps.add(workDestinationSelector(communityRepository));
		steps.add(educationDestinationSelector());
		steps.add(createCarOwnershipModel());
		steps.add(storeData());
		steps.add(cleanData());
		DemandDataForZoneCalculatorIfc zoneCalculator = createZoneCalculator();
		DemandDataForCommunityCalculator communityCalculator = new IsolatedZonesCommunityDemandCalculator(
				zoneCalculator);
		return new CommunityDemandCalculator(communityRepository, communityCalculator, steps,
				impedance());
	}

	private DefaultCommunityRepository loadCommunities() {
		File mappingFile = context().experimentalParameters().valueAsFile("communityToZone");
		Map<String, Community> communities = new CommunityZoneMappingParser(demandZoneRepository(),
				dataRepository().demographyRepository())
				.parse(mappingFile);
		File communityRelationsFile = context()
				.experimentalParameters()
				.valueAsFile("communityCommuters");
		return new CommunityRelationsParser(communities).parse(communityRelationsFile);
	}

	private PopulationSynthesisStep workDestinationSelector(DefaultCommunityRepository communities) {
		PanelDataRepository dataRepository = context().dataRepository().panelDataRepository();
		OdPairSelector communityPairCreator = CommunityOdPairCreator.forWork(communities);
		ImpedanceIfc impedance = impedance();
		int range = context().experimentalParameters().valueAsInteger("poleDistanceRange");
		OdPairSelector odPairSelector = new PanelDistanceSelector(dataRepository, communityPairCreator,
				impedance, range);
		ZoneSelector zoneSelector = new CommunityBasedZoneSelector(communities, newRandom());
		Predicate<PersonBuilder> personFilter = new HasWorkActivity();
		return new CommunityDestinationSelector(odPairSelector, zoneSelector, personFilter,
				newRandom());
	}

	private PopulationSynthesisStep educationDestinationSelector() {
		return new PersonBasedStep(new EducationInHomeZone(newRandom()));
	}

	private PopulationSynthesisStep activityScheduleAssignment() {
		return new HouseholdBasedStep(activityScheduleAssigner()::assignActivitySchedule);
	}

	private DemandDataForZoneCalculatorIfc createZoneCalculator() {
		AttributeType attributeType = StandardAttribute.householdSize;
		WeightedHouseholdSelector householdSelector = createHouseholdSelector();
		HouseholdCreator householdCreator = createHouseholdCreator();
		PersonCreator personCreator = createPersonCreator();
		Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter = z -> h -> true;
		return new DemandDataForZoneCalculatorIpu(results(), householdSelector, householdCreator,
				personCreator, dataRepository(), context(), attributeType, householdFilter);
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
