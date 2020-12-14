package edu.kit.ifv.mobitopp.simulation;

import java.util.Collections;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataSource;
import edu.kit.ifv.mobitopp.data.local.configuration.NoPublicTransport;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WrittenConfiguration {

	static final int defaultTimeStepLength = 60;
	public static final int unlimited = Integer.MAX_VALUE;
	private static final String defaultResultFolder = "log";

	static final int defaulThreadCount = Math.max(1,
			Runtime.getRuntime().availableProcessors() / 2);
	private static final String defaultLogLevel = "INFO";

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
	private int threadCount;
	private String logLevel;
	private VisumToMobitopp visumToMobitopp;
	private Map<String, String> experimental;

	public WrittenConfiguration() {
		super();
		numberOfZones = unlimited;
		publicTransport = new NoPublicTransport();
		resultFolder = defaultResultFolder;
		timeStepLength = defaultTimeStepLength;
		threadCount = defaulThreadCount;
		logLevel = defaultLogLevel;
		destinationChoice = Collections.emptyMap();
		modeChoice = Collections.emptyMap();
		visumToMobitopp = new VisumToMobitopp();
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
		this.threadCount = other.threadCount;
		this.logLevel = other.logLevel;
		this.visumToMobitopp = other.visumToMobitopp;
		this.experimental = other.experimental;
	}

}
