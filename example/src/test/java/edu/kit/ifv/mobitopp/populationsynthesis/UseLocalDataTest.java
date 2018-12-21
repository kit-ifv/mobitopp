package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class UseLocalDataTest {

  public static Stream<ResultFile> logFileNames() {
    return Stream
        .of(resultFile("activity.csv"), resultFile("car.csv"), resultFile("fixedDestination.csv"),
            resultFile("household.csv"), resultFile("opportunity.csv"), resultFile("person.csv"));
  }

  private static ResultFile resultFile(String fileName) {
    File outputFolder = new File("output", "demand-data-local");
    return new ResultFile(outputFolder, fileName);
  }

  @BeforeAll
  public static void executeMobiTopp() throws Exception {
    System.getProperties().entrySet().stream().forEach(System.out::println);
    createPopulation();
  }

  private static void createPopulation() throws Exception {
    PopulationSynthesisExample
        .startSynthesis(new File("config/leopoldshafen/population-synthesis.yaml"));
  }

  @ParameterizedTest
  @MethodSource("logFileNames")
  public void logFilesContainExpectedContent(ResultFile logFile) throws Exception {
    assertThat(logFile.actualFile(), logFile.createMatcher(this.getClass()));
  }

}
