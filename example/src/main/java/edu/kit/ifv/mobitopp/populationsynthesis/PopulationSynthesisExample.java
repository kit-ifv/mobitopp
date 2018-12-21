package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarSharingCustomerModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ElectricCarOwnershipBasedOnSociodemographic;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.GenericElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.LogitBasedCarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ProbabilityForElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.LanduseCLCwithRoadsHouseholdLocationSelector;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPersonCreator;

public class PopulationSynthesisExample extends BasicPopulationSynthesis {

  public PopulationSynthesisExample(
      CarOwnershipModel carOwnershipModel, HouseholdLocationSelector householdLocationSelector,
      ChargePrivatelySelector chargePrivatelySelector, PersonCreator personCreator,
      ActivityScheduleAssigner activityScheduleAssigner, SynthesisContext context) {

    super(carOwnershipModel, householdLocationSelector, chargePrivatelySelector, personCreator,
        activityScheduleAssigner, context);
  }

  public static void main(String... args) throws Exception {
    if (1 > args.length) {
      System.out.println("Usage: ... <configuration file>");
      System.exit(-1);
    }

    File configurationFile = new File(args[0]);
    LocalDateTime start = LocalDateTime.now();
    startSynthesis(configurationFile);
    LocalDateTime end = LocalDateTime.now();
    Duration runtime = Duration.between(start, end);
    System.out.println("Population synthesis took " + runtime);
  }

  static PopulationSynthesisExample startSynthesis(File configurationFile) throws Exception {
    SynthesisContext context = new ContextBuilder().buildFrom(configurationFile);
    return startSynthesis(context);
  }

  public static PopulationSynthesisExample startSynthesis(SynthesisContext context) {
    context.printStartupInformationOn(System.out);
    PopulationSynthesisExample synthesizer = populationSynthesis(context);
    synthesizer.createPopulation();
    return synthesizer;
  }

  private static HouseholdLocationSelector householdLocations(SynthesisContext context) {
    return new LanduseCLCwithRoadsHouseholdLocationSelector(context);
  }

  private static PopulationSynthesisExample populationSynthesis(SynthesisContext context) {
    HouseholdLocationSelector householdLocationSelector = householdLocations(context);
    CommutationTicketModelIfc commuterTicketModel = commuterTickets(context);
    CarOwnershipModel carOwnershipModel = carOwnership(context);
    ChargePrivatelySelector chargePrivatelySelector = chargePrivately(context);
    PersonCreator personCreator = personCreator(context, commuterTicketModel);
    PanelDataRepository panelDataRepository = context.dataRepository().panelDataRepository();
    ActivityScheduleCreator scheduleCreator = new DefaultActivityScheduleCreator();
    ActivityScheduleAssigner activityScheduleAssigner = new DefaultActivityAssigner(
        panelDataRepository, scheduleCreator);
    return populationSynthesis(householdLocationSelector, carOwnershipModel,
        chargePrivatelySelector, personCreator, activityScheduleAssigner, context);
  }

  private static EmobilityPersonCreator personCreator(
      SynthesisContext context, CommutationTicketModelIfc commuterTicketModel) {
    Map<String, CarSharingCustomerModel> carSharing = context.carSharing();
    return new EmobilityPersonCreator(commuterTicketModel, carSharing, context.seed());
  }

  private static CommutationTicketModelIfc commuterTickets(SynthesisContext context) {
    String commuterTicketFile = context.configuration().getCommuterTicket();
    return new CommutationTicketModelStuttgart(commuterTicketFile, context.seed());
  }

  private static CarOwnershipModel carOwnership(SynthesisContext context) {
    IdSequence carIDs = new IdSequence();
    long seed = context.seed();
    File engineFile = context.carEngineFile();
    String ownershipFile = context.configuration().getCarOwnership().getOwnership();
    String segmentFile = context.configuration().getCarOwnership().getSegment();
    ImpedanceIfc impedance = context.impedance();
    CarSegmentModel segmentModel = new LogitBasedCarSegmentModel(impedance, seed, segmentFile);
    ProbabilityForElectricCarOwnershipModel calculator = new ElectricCarOwnershipBasedOnSociodemographic(
        impedance, engineFile.getAbsolutePath());
    return new GenericElectricCarOwnershipModel(carIDs, segmentModel, seed, calculator,
        ownershipFile);
  }

  private static AllowChargingProbabilityBased chargePrivately(SynthesisContext context) {
    return new AllowChargingProbabilityBased(context.seed());
  }

  private static PopulationSynthesisExample populationSynthesis(
      HouseholdLocationSelector householdLocationSelector, CarOwnershipModel carOwnershipModel,
      ChargePrivatelySelector chargePrivatelySelector, PersonCreator personCreator,
      ActivityScheduleAssigner activityScheduleAssigner, SynthesisContext context) {
    return new PopulationSynthesisExample(carOwnershipModel, householdLocationSelector,
        chargePrivatelySelector, personCreator, activityScheduleAssigner, context);
  }

}
