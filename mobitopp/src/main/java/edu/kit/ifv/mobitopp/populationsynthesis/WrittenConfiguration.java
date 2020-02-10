package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collections;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataSource;
import edu.kit.ifv.mobitopp.simulation.VisumToMobitopp;

public class WrittenConfiguration {

  public static final int unlimited = Integer.MAX_VALUE;
  private static final String defaultResultFolder = "log";

  private long seed;
  private int maxIterations;

  private double maxGoodnessDelta;
  private int numberOfZones;
  private DataSource dataSource;
  private ActivityScheduleAssignerType activityScheduleAssigner;
  private Map<String, String> demographyData;
  private String panelData;
  private String visumFile;
  private CarOwnership carOwnership;
  private Map<String, String> carSharing;
  private String commuterTicket;
  private String resultFolder;
  private String synthesisZoneProperties;
  private VisumToMobitopp visumToMobitopp;
  private Map<String, String> experimental;

  public WrittenConfiguration() {
    super();
    numberOfZones = unlimited;
    activityScheduleAssigner = ActivityScheduleAssignerType.standard;
    carSharing = Collections.emptyMap();
    resultFolder = defaultResultFolder;
    visumToMobitopp = new VisumToMobitopp();
    experimental = Collections.emptyMap();
  }

  public WrittenConfiguration(WrittenConfiguration other) {
    super();
    this.activityScheduleAssigner = other.activityScheduleAssigner;
    this.carOwnership = other.carOwnership;
    this.carSharing = other.carSharing;
    this.commuterTicket = other.commuterTicket;
    this.dataSource = other.dataSource;
    this.demographyData = other.demographyData;
    this.experimental = other.experimental;
    this.maxGoodnessDelta = other.maxGoodnessDelta;
    this.maxIterations = other.maxIterations;
    this.numberOfZones = other.numberOfZones;
    this.panelData = other.panelData;
    this.resultFolder = other.resultFolder;
    this.seed = other.seed;
    this.visumFile = other.visumFile;
    this.visumToMobitopp = other.visumToMobitopp;
  }

  public long getSeed() {
    return seed;
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  public double getMaxGoodnessDelta() {
    return maxGoodnessDelta;
  }

  public void setMaxGoodnessDelta(double maxGoodnessDelta) {
    this.maxGoodnessDelta = maxGoodnessDelta;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public ActivityScheduleAssignerType getActivityScheduleAssigner() {
    return activityScheduleAssigner;
  }

  public void setActivityScheduleCreator(ActivityScheduleAssignerType activityScheduleAssigner) {
    this.activityScheduleAssigner = activityScheduleAssigner;
  }

  public Map<String, String> getDemographyData() {
    return demographyData;
  }

  public void setDemographyData(Map<String, String> demographyData) {
    this.demographyData = demographyData;
  }

  public String getPanelData() {
    return panelData;
  }

  public void setPanelData(String panelData) {
    this.panelData = panelData;
  }
  
	public String getSynthesisZoneProperties() {
		return synthesisZoneProperties;
	}
	
	public void setSynthesisZoneProperties(String synthesisZoneProperties) {
		this.synthesisZoneProperties = synthesisZoneProperties;
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

  public VisumToMobitopp getVisumToMobitopp() {
    return visumToMobitopp;
  }

  public void setVisumToMobitopp(VisumToMobitopp visumToMobitopp) {
    this.visumToMobitopp = visumToMobitopp;
  }

  public Map<String, String> getExperimental() {
    return experimental;
  }

  public void setExperimental(Map<String, String> experimental) {
    this.experimental = experimental;
  }

  @Override
  public String toString() {
    return "WrittenConfiguration [seed=" + seed + ", maxIterations=" + maxIterations
        + ", maxGoodnessDelta=" + maxGoodnessDelta + ", numberOfZones=" + numberOfZones
        + ", dataSource=" + dataSource + ", activityScheduleAssigner=" + activityScheduleAssigner
        + ", demographyData=" + demographyData + ", panelData=" + panelData + ", visumFile="
        + visumFile + ", carOwnership=" + carOwnership + ", carSharing=" + carSharing
        + ", commuterTicket=" + commuterTicket + ", resultFolder=" + resultFolder
        + ", visumToMobitopp=" + visumToMobitopp + ", experimental=" + experimental + "]";
  }

}
