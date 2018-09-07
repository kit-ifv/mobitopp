package edu.kit.ifv.mobitopp.util.dataimport;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;
import edu.kit.ifv.mobitopp.dataimport.DemographyBuilder;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;

public class ConvertStructuralData {

	public static void main(String[] args) throws IOException {
		File basePath = new File(
				"F:/git/population-synthesis/data/Zielverteilungen_Zellenmodellierung/");
		Path householdFile = new File(basePath, "180706_1402_Haushalte.csv").toPath();
		Path personFile = new File(basePath, "180706_1409_Personen.csv").toPath();
		Path outputFile = new File(basePath, "structural-data.csv").toPath();
		convertCsvToStructuralData(householdFile, personFile, outputFile);
		printDemography(outputFile);
	}

	private static void convertCsvToStructuralData(
			Path householdFile, Path personFile, Path outputFile) throws IOException {
		try (CSVWriter writer = writerFor(outputFile)) {
			ZoneValue.writeHeaderTo(writer);
			readFromCsv(householdFile, personFile)
					.sorted(Comparator.comparing(ZoneValue::zoneId))
					.map(ZoneValue::toLine)
					.forEach(writer::writeNext);
		}
	}

	private static void printDemography(Path outputFile) {
		StructuralData structuralData = new StructuralData(new CsvFile(outputFile.toString()));
		List<Demography> total = new ArrayList<>();
		structuralData.resetIndex();
		while (structuralData.hasNext()) {
			total.add(new DemographyBuilder(structuralData).build());
			structuralData.next();
		}
		print(total);
	}

	private static void print(List<Demography> total) {
		List<MaleAgeDistribution> maleAge = total.stream().map(Demography::maleAge).collect(toList());
		printMaleAgeDistributions(maleAge);
		List<FemaleAgeDistribution> femaleAge = total.stream().map(Demography::femaleAge).collect(toList());
		printFemaleAgeDistributions(femaleAge);
	}

	private static void printMaleAgeDistributions(List<? extends AgeDistributionIfc> maleAge) {
		System.out.println("Male distribution");
		AgeDistributionIfc total = new MaleAgeDistribution();
		printAgeInformation(maleAge, total);
	}
	
	private static void printFemaleAgeDistributions(List<? extends AgeDistributionIfc> femaleAge) {
		System.out.println("Female distribution");
		AgeDistributionIfc total = new FemaleAgeDistribution();
		printAgeInformation(femaleAge, total);
	}

	private static void printAgeInformation(
			List<? extends AgeDistributionIfc> maleAge, AgeDistributionIfc total) {
		maleAge
				.get(0)
				.getItems()
				.stream()
				.map(AgeDistributionItem::createEmpty)
				.forEach(total::addItem);
		for (AgeDistributionIfc distribution : maleAge) {
			for (AgeDistributionItem item : distribution.getItems()) {
				for (int i = 0; i < item.amount(); i++) {
					total.getItem(item.age()).increment();
				}
			}
		}

		int grand_total = 0;

		System.out.println("\nSoll-Verteilung:");
		for (AgeDistributionItem item : total.getItems()) {
			System.out.println(item.age() + ": " + item.amount());
			grand_total += item.amount();
		}
		System.out.println("TOTAL\t" + grand_total + "\n");
		System.out.println("\n");
	}

	private static CSVWriter writerFor(Path outputFile) throws IOException {
		return new CSVWriter(Files.newBufferedWriter(outputFile), ';', CSVWriter.NO_QUOTE_CHARACTER);
	}

	private static Stream<ZoneValue> readFromCsv(Path householdFile, Path personFile)
			throws IOException {
		return Stream
				.concat(readHouseholds(householdFile), readPersons(personFile))
				.collect(groupingBy(Value::zoneId))
				.values()
				.stream()
				.map(ZoneValue::from);
	}

	private static Stream<HouseholdValue> readHouseholds(Path inputFile) throws IOException {
		try (CSVReader reader = readerFor(inputFile)) {
			List<String[]> readAll = reader.readAll();
			return readAll.stream().map(HouseholdValue::from);
		}
	}

	private static Stream<PersonValue> readPersons(Path inputFile) throws IOException {
		try (CSVReader reader = readerFor(inputFile)) {
			List<String[]> readAll = reader.readAll();
			return readAll.stream().map(PersonValue::from);
		}
	}

	private static CSVReader readerFor(Path inputFile) throws IOException {
		return new CSVReader(Files.newBufferedReader(inputFile), ';', '"', 1);
	}
}
