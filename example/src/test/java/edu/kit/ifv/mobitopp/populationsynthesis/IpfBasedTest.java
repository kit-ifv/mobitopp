package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.simulation.SimulationExample;

public class IpfBasedTest {

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
    File outputFolder = new File("output", "demand-data-local");
    return new ResultFile(outputFolder, fileName);
  }
  
  private static ResultFile resultFile(String fileName) {
    File resultFolder = new File("results", "simulation");
    return new ResultFile(resultFolder, fileName);
  }

  @BeforeAll
  public static void executeMobiTopp() throws Exception {
    System.getProperties().entrySet().stream().forEach(System.out::println);
    createPopulation();
    simulatePopulation();
  }

  private static void createPopulation() throws Exception {
    PopulationSynthesisIpf
        .startSynthesis(new File("config/leopoldshafen/population-synthesis.yaml"));
  }

  private static void simulatePopulation() throws IOException {
    SimulationExample.startSimulation(new File("config/leopoldshafen/simulation.yaml"));
  }

  @ParameterizedTest(name="{0}")
  @MethodSource("logFileNames")
  public void logFilesContainExpectedContent(ResultFile logFile) throws Exception {
    assertThat(logFile.actualFile(), logFile.createMatcher(this.getClass()));
  }

}
