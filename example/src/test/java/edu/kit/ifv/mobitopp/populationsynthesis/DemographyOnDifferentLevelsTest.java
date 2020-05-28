package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class DemographyOnDifferentLevelsTest {

	public static Stream<ResultFile> logFileNames() {
		return Stream
				.of(demandData("activity.csv"), demandData("car.csv"), demandData("fixedDestination.csv"),
						demandData("household.csv"), demandData("opportunity.csv"), demandData("person.csv"),
						synthesisResult("actualDemography-community.csv"),
						synthesisResult("actualDemography-zone.csv"));
	}

	private static ResultFile demandData(String fileName) {
		File outputFolder = new File("output", "demand-data");
		return new ResultFile(outputFolder, fileName, "ipu_different_levels");
	}

	private static ResultFile synthesisResult(String fileName) {
		File resultFolder = new File("results", "population-synthesis");
		return new ResultFile(resultFolder, fileName, "ipu_different_levels");
	}

	@BeforeAll
	public static void executeMobiTopp() throws Exception {
		createPopulationZoneBased();
	}

	private static void createPopulationZoneBased() throws Exception {
		DemographyOnDifferentLevelsStarter
				.startSynthesis(new File("config/leopoldshafen/population-synthesis-ipu-different-levels.yaml"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("logFileNames")
	public void logFilesContainExpectedContent(ResultFile logFile) throws Exception {
		assertThat(logFile.actualFile(), logFile.createMatcher(this.getClass()));
	}

}
