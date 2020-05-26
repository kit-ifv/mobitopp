package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collections;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataSource;
import edu.kit.ifv.mobitopp.simulation.VisumToMobitopp;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
  private Map<String, String> mobilityProviders;
  private String commuterTicket;
  private String resultFolder;
  private String synthesisZoneProperties;
  private VisumToMobitopp visumToMobitopp;
  private Map<String, String> experimental;

  public WrittenConfiguration() {
    super();
    numberOfZones = unlimited;
    activityScheduleAssigner = ActivityScheduleAssignerType.standard;
    mobilityProviders = Collections.emptyMap();
    resultFolder = defaultResultFolder;
    visumToMobitopp = new VisumToMobitopp();
    experimental = Collections.emptyMap();
  }

  public WrittenConfiguration(WrittenConfiguration other) {
    super();
    this.activityScheduleAssigner = other.activityScheduleAssigner;
    this.carOwnership = other.carOwnership;
    this.mobilityProviders = other.mobilityProviders;
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
    this.synthesisZoneProperties = other.synthesisZoneProperties;
    this.visumFile = other.visumFile;
    this.visumToMobitopp = other.visumToMobitopp;
  }

}
