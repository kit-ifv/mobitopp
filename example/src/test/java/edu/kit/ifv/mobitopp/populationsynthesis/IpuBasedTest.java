package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.LocalFiles;
import edu.kit.ifv.mobitopp.simulation.SimulationExample;

public class IpuBasedTest {

  private static final String community = "community";

	public static Stream<ResultFile> logFileNames() {
    return Stream
        .of(singleZoneIpu("activity.csv"), singleZoneIpu("car.csv"), singleZoneIpu("fixedDestination.csv"),
            singleZoneIpu("household.csv"), singleZoneIpu("opportunity.csv"), singleZoneIpu("person.csv"),
            multiZoneIpu("activity.csv"), multiZoneIpu("car.csv"), multiZoneIpu("fixedDestination.csv"),
            multiZoneIpu("household.csv"), multiZoneIpu("opportunity.csv"), multiZoneIpu("person.csv"),
            singleZoneIpuResult("actualDemography-community.csv"),
            singleZoneIpuResult("actualDemography-zone.csv"),
            multiZoneIpuResult("actualDemography-community.csv"),
            multiZoneIpuResult("actualDemography-zone.csv"),
            simulationResult("demandsimulationResult.csv"),
            simulationResult("demandsimulationResultActivity.csv"),
            simulationResult("demandsimulationResultCar.csv"),
            simulationResult("demandsimulationResultCharging.csv"),
            simulationResult("demandsimulationResultChargingAgg.csv"));
  }

  private static ResultFile singleZoneIpu(String fileName) {
    File outputFolder = new File("output", "demand-data");
    return new ResultFile(outputFolder, fileName, "ipu");
  }
  
  private static ResultFile multiZoneIpu(String fileName) {
  	File outputFolder = new File(new File("output", "demand-data"), community);
  	return new ResultFile(outputFolder, fileName, "ipu");
  }
  
  private static ResultFile singleZoneIpuResult(String fileName) {
    File resultFolder = new File("results", "population-synthesis");
    return new ResultFile(resultFolder, fileName, "ipu");
  }
  
  private static ResultFile multiZoneIpuResult(String fileName) {
  	File resultFolder = new File(new File("results", "population-synthesis"), community);
  	return new ResultFile(resultFolder, fileName, "ipu");
  }
  
  private static ResultFile simulationResult(String fileName) {
    File resultFolder = new File("results", "simulation");
    return new ResultFile(resultFolder, fileName, "ipu");
  }

  @BeforeAll
  public static void executeMobiTopp() throws Exception {
    WrittenConfiguration configuration = createPopulationZoneBased();
    createPopulationCommunityBased(configuration);
    simulatePopulation();
  }

  private static WrittenConfiguration createPopulationZoneBased() throws Exception {
    return PopulationSynthesisIpuZoneBasedStarter
        .startSynthesis(new File("config/leopoldshafen/population-synthesis-ipu.yaml"));
  }
  
  private static void createPopulationCommunityBased(WrittenConfiguration configuration) throws Exception {
  	String resultFolder = changeTo(configuration.getResultFolder(), community);
  	WrittenConfiguration communityConfiguration = new WrittenConfiguration(configuration);
		communityConfiguration.setResultFolder(resultFolder);
  	LocalFiles dataSource = (LocalFiles) communityConfiguration.getDataSource();
  	String dataFolder = changeTo(dataSource.getDemandDataFolder(), community);
  	dataSource.setDemandDataFolder(dataFolder);
  	communityConfiguration.setDataSource(dataSource);
		PopulationSynthesisIpuCommunityBasedStarter.startSynthesis(communityConfiguration);
  }

	private static String changeTo(String folderAsString, String name) {
		File folder = Convert.asFile(folderAsString);
		File communityFolder = new File(folder, name);
  	return Convert.asString(communityFolder);
	}

  private static void simulatePopulation() throws IOException {
    SimulationExample.startSimulation(new File("config/leopoldshafen/simulation-ipu.yaml"));
  }

  @ParameterizedTest(name="{0}")
  @MethodSource("logFileNames")
  public void logFilesContainExpectedContent(ResultFile logFile) throws Exception {
    assertThat(logFile.actualFile(), logFile.createMatcher(this.getClass()));
  }

}
