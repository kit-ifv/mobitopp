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

  public static Stream<ResultFile> logFileNames() {
    return Stream
        .of(demandFile("activity.csv"), demandFile("car.csv"), demandFile("fixedDestination.csv"),
            demandFile("household.csv"), demandFile("opportunity.csv"), demandFile("person.csv"),
            resultFile("demandsimulationResult.csv"),
            resultFile("demandsimulationResultActivity.csv"),
            resultFile("demandsimulationResultCar.csv"),
            resultFile("demandsimulationResultCharging.csv"),
            resultFile("demandsimulationResultChargingAgg.csv"));
  }

  private static ResultFile demandFile(String fileName) {
    File outputFolder = new File("output", "demand-data");
    return new ResultFile(outputFolder, fileName, "ipu");
  }
  
  private static ResultFile resultFile(String fileName) {
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
  	String name = "community";
		String resultFolder = changeTo(configuration.getResultFolder(), name);
  	WrittenConfiguration communityConfiguration = new WrittenConfiguration(configuration);
		communityConfiguration.setResultFolder(resultFolder);
  	LocalFiles dataSource = (LocalFiles) communityConfiguration.getDataSource();
  	String dataFolder = changeTo(dataSource.getDemandDataFolder(), name);
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
