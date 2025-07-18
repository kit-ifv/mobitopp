package utils

import utils.report.CardStatus
import utils.report.ReportBuilder
import kotlin.io.path.Path

fun main() {
    val builder = ReportBuilder()
    builder.addWarningLog("Warning", "You overused the number of calls...")
    builder.addWarningLog(
        "Too many parsing faults",
        "It occured, that class XYZ had too many parsing faults during execution of the long-term-module."
    )
    builder.addSuccessLog(
        "Successfully completed the number of calls.",
        Art
    )
    builder.addErrorLog("Error", "oh noooo we failed")
    builder.addNormalLog("Nothing happened", "Not even here")
    builder.addNormalLog(
        "Very long test case. Even the Title is hilariously long.\n Multiple lines even",
        veryLongText
    )
    builder.addNormalLog("Nothing happened", "Not even here")
    builder.addNormalLog("Nothing happened", "Not even here")
    builder.addNormalLog("Nothing happened", "Not even here")
    builder.addNormalLog("Nothing happened", "Not even here")
    builder.addNormalLog("Nothing happened", "Not even here")
    builder.addOverviewItem("Load step", CardStatus.SUCCESS)
    builder.addOverviewItem("Person CSV Parsing", CardStatus.WARNING, "Some entries were erroneous")
    builder.addOverviewItem("Simulation step: 1 Week; Rastatt central; Summer;", CardStatus.FAILURE)
    var t = 15;
    while (t-- > 0) {
        builder.addOverviewItem("Load step: person.csv, car.csv, public_transport.csv, config.toml", CardStatus.SUCCESS)
    }
    builder.printReport(Path("src/test/resources/tempOutput/"))
}

@Suppress("TopLevelPropertyNaming")
private const val Art = "⡿⡿⠟⠓⠛⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
    "⣷⣶⣾⣿⣷⣶⣤⣬⣟⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿\n" +
    "⣿⣟⣱⡿⠿⠿⠿⣮⣻⣿⣿⣿⣿⣏⢿⣿⡿⢁⢀⣀⣀⣀⣬⣉⣙⠋⠛⠿⢿⣿\n" +
    "⣟⣛⣡⣤⣤⣁⣀⣄⣉⣻⣿⣿⣿⣿⠛⡿⠻⠛⠭⠿⡿⠯⣭⣟⡻⢿⣶⣦⣀⢙\n" +
    "⣿⣿⣿⣯⣝⣛⣛⣛⣭⣾⣿⣿⣿⢇⣨⢶⣿⣶⡾⢶⣶⡶⢤⣤⣤⣀⠟⢉⣛⣓\n" +
    "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⣿⠿⠻⠿⢿⣿⣷⣶⣮⣭⣭⣭⣴⣾⡻⣮⣵\n" +
    "⣿⣿⣿⣿⣿⠟⢋⣽⣿⣿⣿⣿⡿⢿⡿⠿⠿⣆⣉⠻⣿⣿⣿⣿⣿⣿⣿⣷⣽⣿\n" +
    "⣿⣿⣿⠟⢁⣶⣿⣿⣿⣿⣟⢋⣀⣒⣀⣐⣫⡍⠛⠿⠪⠻⣿⣿⣿⣿⣿⣿⣿⣿\n" +
    "⣿⣿⢏⠀⣿⣏⣠⣽⣍⣍⣡⣾⣿⣿⣟⣋⣋⣓⣓⣼⣥⣤⡈⠻⣿⣿⣿⡏⣿⣿\n" +
    "⣿⣿⡌⢦⢻⣮⡁⣼⣿⢭⣭⣉⣭⣭⣭⣉⣭⢛⡉⣛⢛⡛⠛⣠⡌⣿⣿⣧⣿⣿\n" +
    "⣙⢿⣿⡜⣿⣿⣷⡹⠿⠿⠿⠿⠿⠟⠿⠛⠿⠻⠿⠿⠿⢃⣴⣿⢣⣿⣿⣿⣿⣿\n" +
    "⣿⣿⣿⣿⡜⣿⣿⡗⠦⣀⣀⢀⠐⠒⠀⠀⠀⢀⡀⠀⣠⣾⣿⢏⣾⢏⢽⣻⣿⣿\n" +
    "⣿⣿⣿⠈⠙⣾⣏⢧⣼⣿⣟⣛⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣯⣼⡋⠿⣾⣿⣿⣾\n" +
    "⢿⣿⣿⠐⠄⠺⣿⣾⢟⣻⣿⣿⣛⡿⠿⢿⣾⣟⣭⡾⣿⣿⣿⡟⣿⣾⡻⣿⠿⠛\n" +
    "⠀⡹⣿⡆⠈⣠⣿⣷⣿⣿⣿⣿⣿⣿⣿⣷⣬⡛⣻⣿⣿⣿⣿⣹⢖⠝⠁⡳⣾⣾"

@Suppress("TopLevelPropertyNaming", "MaximumLineLength", "Indentation")
private const val veryLongText = "w: [ksp] Deferred SharingStation as it is not valid !!\n" +
    "w: [ksp] Round 1 - RETRY processor: MutableProcessor\n" +
    "w: [ksp] Deferred PrivateCar as it is not valid !!\n" +
    "w: [ksp] Deferred Person as it is not valid !!\n" +
    "w: [ksp] Deferred PlannedActivity as it is not valid !!\n" +
    "w: [ksp] Deferred SharingStation as it is not valid !!\n" +
    "w: [ksp] Round 1 - New files: [File: LazyList.kt, File: Binning.kt, File: CollectionUtils.kt, File: " +
    "EquivalenceClass.kt, File: ListExtensions.kt, File: MapExtensions.kt, File: ProgressBarFactory.kt, File: " +
    "StringUtils.kt, File: TreePrinter.kt, File: ClearableList.kt, File: Combinatorics.kt, File: " +
    "CsvColumnTypes.kt, File: CsvParser.kt, File: CsvReader.kt, File: MapParser.kt, File: " +
    "DiscreteRandomVariable.kt, File: RandomUtil.kt, File: Cache.kt, File: ConsoleCaptor.kt, File: " +
    "ErrorHandling.kt, File: Ids.kt, File: Patterns.kt, File: NestedSynchronization.kt, File: " +
    "FileDecompression.kt, File: FileValidation.kt, File: DurationExtensions.kt, File: GPSLegcayCoordinate.kt, " +
    "File: MeasureTime.kt, File: NumberUtils.kt, File: AbsoluteTime.kt, File: ReadParameterFile.kt, File: " +
    "BinaryConverter.kt, File: CSVConversion.kt, File: ExtensionFunctions.kt, File: ReportBuilder.kt, File: " +
    "LocalPaths.kt, File: Main.kt, File: PopulationSynthesis.kt, File: TestOneHousehold.kt, File: " +
    "TestOneHouseholdWithImpedance.kt, File: TestRunOnePerson.kt, File: ExampleProjectContext.kt, File: " +
    "AssignCarUser.kt, File: BuildAgentsStep.kt, File: HomeLocationStep.kt, File: LoadChoiceModelsStep.kt, " +
    "File: ScalePopulationStep.kt, File: SimulatePlannedActivities.kt, File: LoadBinarySteps.kt, File: " +
    "LoadImpedance.kt, File: LoadRoadNetworkStep.kt, File: LoadAttractivenessStep.kt, File: " +
    "LoadFixedDestinations.kt, File: LoadHouseholds.kt, File: LoadLegacyPrivateCars.kt, File: " +
    "LoadPersons.kt, File: LoadPlannedActivities.kt, File: LoadSharingStations.kt, File: LoadZoneCsv.kt, " +
    "File: WriteTripsToCsvStep.kt, File: ReadOnlyKDTree.kt, File: Matrix.kt, File: MatrixFormat.kt, File: " +
    "MultiMatrix.kt, File: YamlMultiMatrix.kt, File: EventQueue.kt, File: Events.kt, File: Simulator.kt, " +
    "File: Subscribable.kt, File: Context.kt, File: ModelSteps.kt, File: Repository.kt, File: Resource.kt, File: " +
    "Simulation.kt, File: ValidateCsvMetadata.kt, File: Warning.kt, File: LoadBinaryStep.kt, File: " +
    "WriteBinaryStep.kt, File: ColorPallets.kt, File: Plotter.kt, File: PlotterBuilder.kt, File: " +
    "AttractivityModel.kt, File: ChoiceModelModes.kt, File: ChoiceModelPurposes.kt, File: SynthesisContext.kt, " +
    "File: LocatableGraph.kt, File: BinaryMatrixFormat.kt, File: MatrixImpl.kt, File: YamlMatrixLookupMetrics.kt, " +
    "File: IVisumParser.kt, File: VisumMatrix.kt, File: VisumMatrixParser.kt, File: VisumParser.kt, File: " +
    "Action.kt, File: ActionBlock.kt, File: ActionVisitor.kt, File: Agenda.kt, File: LinkedAction.kt, File: " +
    "Schedule.kt, File: ScheduleModifier.kt, File: Trip.kt, File: ActionModel.kt, File: BlockModel.kt, File: " +
    "PlanModel.kt, File: PlanView.kt, File: TrackableModel.kt, File: ActivityType.kt, File: LegacyActivityType.kt, " +
    "File: LegacyMode.kt, File: Mode.kt, File: ZoneClassification.kt, File: LegacyRegionType.kt, File: " +
    "RegioStaR.kt, File: RegioStaRGem.kt, File: RegionType.kt, File: Location.kt, File: LocationKDTree.kt, " +
    "File: LocationMetric.kt, File: Metrics.kt, File: Zone.kt, File: BuildAgents.kt, File: CarAgent.kt, " +
    "File: HouseholdAgent.kt, File: PersonAgent.kt, File: SharingStationAgent.kt, File: ActivitiesToSchedule.kt, " +
    "File: DurationRandomizer.kt, File: FixedModesFilter.kt, File: LegacyDestinationChoice.kt, File: " +
    "LegacyModeChoice.kt, File: ModeAvailabilityFilter.kt, File: DemandSimContext.kt, File: EventScope.kt, File: " +
    "PersonEvents.kt, File: EvaluatePersonSchedule.kt, File: AssignCarUserModel.kt, File: " +
    "AssignHouseholdLocations.kt, File: DetermineEconomicStatus.kt, File: GenerateCars.kt, File: Synthesis.kt, " +
    "File: SynthesisHouseholds.kt, File: ActiToppNGGenerator.kt, File: ActitoppExtensions.kt, File: A" +
    "ctitoppGenerator.kt, File: GenerateActivitySchedule.kt, File: PreliminaryActivitySchedule.kt, File: " +
    "AssignStrategy.kt, File: CarEngine.kt, File: CarOwnership.kt, File: CarOwnershipKleinstadt.kt, File: " +
    "CarOwnershipLandraum.kt, File: CarOwnershipStadt.kt, File: CarOwnershipStadtraum.kt, File: CarSegment.kt, " +
    "File: TransitPassParameters.kt, File: SynthesisHousehold.kt, File: SynthesisPerson.kt, File: " +
    "AssignFixedDestinationBuilder.kt, File: BandwidthLocator.kt, File: LocationDetermination.kt, " +
    "File: UseClosestLocation.kt, File: CommunityBasedLocator.kt, File: CommunityNumber.kt, File: " +
    "CommuterDemandsMatrix.kt, File: Calculations.kt, File: HouseholdSynthesis.kt, File: IPU.kt, File: " +
    "Rule.kt, File: Car.kt, File: CarEngineStatistics.kt, File: Household.kt, File: Person.kt, File: " +
    "PlannedActivity.kt, File: SharingStations.kt, File: ActivityCsvParser.kt, File: ActivityLocation.kt, " +
    "File: ActivityStartShifter.kt, File: PersonCsvParser.kt, File: FixedDestinationConversion.kt, File: " +
    "HouseholdConversion.kt, File: LocationUtils.kt, File: PersonConversion.kt, File: PlannedActivityConversi" +
    "on.kt, File: PrivateCarConversion.kt, File: ZoneConversion.kt, File: WriteOutput.kt]\n" +
    "w: [ksp] Processing round: 2\n" +
    "w: [ksp] Round 2 - Found 0 symbols annotated by @Buildable: []\n" +
    "w: [ksp] Round 2 - Found 4 symbols annotated by @Mutable: [PrivateCar, Person, PlannedActivity, SharingStation]\n" +
    "w: [ksp] Generate mutable class domain.synthesis.data.MutablePrivateCar: PrivateCar (domain.synthesis.data.MutablePrivateCar)\n" +
    "w: [ksp] Generate (super)constructor params: [id: [typealias CarId], owner: MutableHousehold]\n" +
    "Hello: checking CarSegment\n" +
    "Hello: checking CarEngine\n" +
    "Hello: checking Int\n" +
    "w: [ksp] Processed PrivateCar, [@Mutable]\n" +
    "w: [ksp] Generate mutable class domain.synthesis.data.MutablePerson: Person (domain.synthesis.data.MutablePerson)\n" +
    "w: [ksp] Generate (super)constructor params: [id: [typealias PersonId], household: MutableHousehold, seed: Long]\n" +
    "Hello: checking Int"
