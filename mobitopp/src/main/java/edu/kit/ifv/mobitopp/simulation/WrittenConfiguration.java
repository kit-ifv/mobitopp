package edu.kit.ifv.mobitopp.simulation;

import java.util.Collections;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataSource;
import edu.kit.ifv.mobitopp.data.local.configuration.NoPublicTransport;

public class WrittenConfiguration {

	static final int defaultTimeStepLength = 60;
	public static final int unlimited = Integer.MAX_VALUE;
	private static final String defaultResultFolder = "log";

	private float fractionOfPopulation;
	private long seed;
	private int days;
	private int numberOfZones;
	private DataSource dataSource;
	private PublicTransport publicTransport;
	private String resultFolder;
	private String visumFile;
	private Map<String, String> destinationChoice;
	private Map<String, String> modeChoice;
	private int timeStepLength;
	private Map<String, String> experimental;

	public WrittenConfiguration() {
		super();
		numberOfZones = unlimited;
		publicTransport = new NoPublicTransport();
		resultFolder = defaultResultFolder;
		timeStepLength = defaultTimeStepLength;
		destinationChoice = Collections.emptyMap();
		modeChoice = Collections.emptyMap();
		experimental = Collections.emptyMap();
	}

	public WrittenConfiguration(WrittenConfiguration other) {
		this.fractionOfPopulation = other.fractionOfPopulation;
		this.seed = other.seed;
		this.days = other.days;
		this.numberOfZones = other.numberOfZones;
		this.dataSource = other.dataSource;
		this.publicTransport = other.publicTransport;
		this.resultFolder = other.resultFolder;
		this.visumFile = other.visumFile;
		this.destinationChoice = other.destinationChoice;
		this.modeChoice = other.modeChoice;
		this.timeStepLength = other.timeStepLength;
		this.experimental = other.experimental;
	}

	public float getFractionOfPopulation() {
		return fractionOfPopulation;
	}

	public void setFractionOfPopulation(float fractionOfPopulation) {
		this.fractionOfPopulation = fractionOfPopulation;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getNumberOfZones() {
		return numberOfZones;
	}

	public void setNumberOfZones(int numberOfZones) {
		this.numberOfZones = numberOfZones;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PublicTransport getPublicTransport() {
		return publicTransport;
	}

	public void setPublicTransport(PublicTransport publicTransport) {
		this.publicTransport = publicTransport;
	}

	public String getResultFolder() {
		return resultFolder;
	}

	public void setResultFolder(String resultFolder) {
		this.resultFolder = resultFolder;
	}

	public String getVisumFile() {
		return visumFile;
	}

	public void setVisumFile(String visumFile) {
		this.visumFile = visumFile;
	}

	public Map<String, String> getDestinationChoice() {
		return destinationChoice;
	}

	public void setDestinationChoice(Map<String, String> destinationChoice) {
		this.destinationChoice = destinationChoice;
	}
	
	public Map<String, String> getModeChoice() {
		return modeChoice;
	}
	
	public void setModeChoice(Map<String, String> modeChoice) {
		this.modeChoice = modeChoice;
	}

	public int getTimeStepLength() {
		return timeStepLength;
	}

	public void setTimeStepLength(int timeStepLength) {
		this.timeStepLength = timeStepLength;
	}

	public Map<String, String> getExperimental() {
		return experimental;
	}

	public void setExperimental(Map<String, String> experimental) {
		this.experimental = experimental;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [fractionOfPopulation=" + fractionOfPopulation + ", seed="
				+ seed + ", days=" + days + ", numberOfZones=" + numberOfZones + ", dataSource="
				+ dataSource + ", publicTransport=" + publicTransport + ", resultFolder=" + resultFolder
				+ ", visumFile=" + visumFile + ", destinationChoice=" + destinationChoice
				+ ", timeStepLength=" + timeStepLength + ", experimental=" + experimental + "]";
	}

}
