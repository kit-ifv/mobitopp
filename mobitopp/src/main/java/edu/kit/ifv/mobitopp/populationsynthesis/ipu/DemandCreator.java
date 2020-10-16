package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandCreator {

  private final HouseholdBuilder householdBuilder;
  private final PanelDataRepository panelData;
  private final Predicate<HouseholdOfPanelData> householdFilter;
  private final HouseholdReproducer householdMultiplier;
  private final DemandZoneRepository zoneRepository;

  public DemandCreator(
      final HouseholdBuilder householdBuilder, final PanelDataRepository panelDataRepository,
      final Predicate<HouseholdOfPanelData> householdFilter,
      final HouseholdReproducer householdMultiplier, final DemandZoneRepository zoneRepository) {
    super();
    this.householdBuilder = householdBuilder;
    this.panelData = panelDataRepository;
    this.householdFilter = householdFilter;
    this.householdMultiplier = householdMultiplier;
    this.zoneRepository = zoneRepository;
  }

  public List<HouseholdForSetup> demandFor(final List<WeightedHousehold> households) {
    return householdMultiplier
        .getHouseholdsToCreate(households)
        .flatMap(this::toHousehold)
        .collect(toList());
  }

  private Stream<HouseholdForSetup> toHousehold(WeightedHousehold household) {
    HouseholdOfPanelData panelHousehold = panelData.getHousehold(household.id());
    if (householdFilter.test(panelHousehold)) {
      DemandZone zone = zoneRepository.getRegionBy(household.context());
      return Stream.of(householdBuilder.householdFor(panelHousehold, zone));
    }
    return Stream.empty();

  }

}
