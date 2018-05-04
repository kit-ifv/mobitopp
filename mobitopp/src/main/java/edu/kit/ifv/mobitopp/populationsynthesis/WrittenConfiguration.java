package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collections;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataSource;

public class WrittenConfiguration {

	public static final int unlimited = Integer.MAX_VALUE;
	private static final String defaultResultFolder = "log";

	private long seed;
	private int numberOfZones;
	private DataSource dataSource;
	private ActivityScheduleCreatorType activityScheduleCreator;
	private String demographyData;
	private String panelData;
	private String visumFile;
	private CarOwnership carOwnership;
	private Map<String, String> carSharing;
	private String commuterTicket;
	private String resultFolder;
	private Map<String, String> experimental;

	public WrittenConfiguration() {
		super();
		numberOfZones = unlimited;
		activityScheduleCreator = ActivityScheduleCreatorType.standard;
		carSharing = Collections.emptyMap();
		resultFolder = defaultResultFolder;
		experimental = Collections.emptyMap();
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ActivityScheduleCreatorType getActivityScheduleCreator() {
		return activityScheduleCreator;
	}

	public void setActivityScheduleCreator(ActivityScheduleCreatorType activityScheduleCreator) {
		this.activityScheduleCreator = activityScheduleCreator;
	}

	public String getDemographyData() {
		return demographyData;
	}

	public void setDemographyData(String demographyData) {
		this.demographyData = demographyData;
	}

	public String getPanelData() {
		return panelData;
	}

	public void setPanelData(String panelData) {
		this.panelData = panelData;
	}

	public String getVisumFile() {
		return visumFile;
	}

	public void setVisumFile(String visumFile) {
		this.visumFile = visumFile;
	}

	public CarOwnership getCarOwnership() {
		return carOwnership;
	}

	public void setCarOwnership(CarOwnership carOwnership) {
		this.carOwnership = carOwnership;
	}

	public int getNumberOfZones() {
		return numberOfZones;
	}

	public void setNumberOfZones(int numberOfZones) {
		this.numberOfZones = numberOfZones;
	}

	public Map<String, String> getCarSharing() {
		return carSharing;
	}

	public void setCarSharing(Map<String, String> carSharing) {
		this.carSharing = carSharing;
	}

	public String getCommuterTicket() {
		return commuterTicket;
	}

	public void setCommuterTicket(String commuterTicket) {
		this.commuterTicket = commuterTicket;
	}

	public String getResultFolder() {
		return resultFolder;
	}

	public void setResultFolder(String resultFolder) {
		this.resultFolder = resultFolder;
	}

	public Map<String, String> getExperimental() {
		return experimental;
	}

	public void setExperimental(Map<String, String> experimental) {
		this.experimental = experimental;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [seed=" + seed + ", numberOfZones=" + numberOfZones
				+ ", dataSource=" + dataSource + ", visumFile=" + visumFile + ", carOwnership="
				+ carOwnership + ", activityScheduleCreator=" + activityScheduleCreator + ", carSharing="
				+ carSharing + ", commuterTicket=" + commuterTicket + ", resultFolder=" + resultFolder
				+ ", experimental=" + experimental + "]";
	}

}
