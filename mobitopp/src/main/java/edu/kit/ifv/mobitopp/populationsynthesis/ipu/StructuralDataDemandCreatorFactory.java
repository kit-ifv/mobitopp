package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StructuralDataDemandCreatorFactory implements DemandCreatorFactory {

  private final HouseholdCreator householdCreator;
  private final PersonCreator personCreator;
  private final PanelDataRepository panelData;
  private final AttributeType householdFilterType;
  private final Function<DemandRegion, Predicate<HouseholdOfPanelData>> householdFilter;
  private final WeightedHouseholdSelector householdSelector;
  private final DemandZoneRepository zoneRepository;

  @Override
  public DemandCreator create(
      final DemandRegion region, final AttributeResolver attributeResolver) {
    HouseholdBuilder usingBuilder = new DefaultHouseholdBuilder(householdCreator, personCreator,
        panelData);
    List<Attribute> householdAttributes = attributeResolver.attributesOf(householdFilterType);
    Predicate<HouseholdOfPanelData> householdForZoneFilter = householdFilter.apply(region);
    StructuralDataHouseholdReproducer multiplier = new StructuralDataHouseholdReproducer(region,
        householdFilterType, householdSelector, householdAttributes, panelData);
    return new DemandCreator(usingBuilder, panelData, householdForZoneFilter, multiplier,
        zoneRepository);
  }

}
