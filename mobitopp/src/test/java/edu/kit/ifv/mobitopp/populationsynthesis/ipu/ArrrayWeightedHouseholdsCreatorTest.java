
package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

@ExtendWith(MockitoExtension.class)
public class ArrrayWeightedHouseholdsCreatorTest {

  @Mock
  public SynthesisContext context;
  @Mock
  public DemandRegion district;
  @Mock
  public DemandZone zone;
  @Mock
  public PanelDataRepository panelData;

  @Test
  void create() throws Exception {
    double weight = 1.0d;
    Map<String, Integer> attributes = Map.of();

    when(district.nominalDemography()).thenReturn(createDistrictDemography());
    when(district.regionalLevel()).thenReturn(RegionalLevel.district);
    when(zone.nominalDemography()).thenReturn(createZoneDemography());
    when(zone.regionalLevel()).thenReturn(RegionalLevel.zone);
    when(zone.getExternalId()).thenReturn("zone");
    when(district.parts()).thenReturn(List.of(zone));
    when(district.zones()).then(invocation -> Stream.of(zone));
    when(context.attributes(RegionalLevel.district))
        .thenReturn(List.of(StandardAttribute.householdSize));
    when(context.attributes(RegionalLevel.zone)).thenReturn(List.of(StandardAttribute.femaleAge));
    HouseholdOfPanelData panelHousehold = new HouseholdOfPanelDataBuilder().withSize(1).build();
    HouseholdOfPanelDataId id = panelHousehold.getId();
    when(panelData.getHouseholds()).thenReturn(List.of(panelHousehold));
    PersonOfPanelDataId personId = new PersonOfPanelDataId(id, 1);
    PersonOfPanelData panelPerson = new PersonOfPanelDataBuilder()
        .withId(personId)
        .withGender(Gender.FEMALE)
        .withAge(3)
        .build();
    when(panelData.getPersonsOfHousehold(id)).thenReturn(List.of(panelPerson));
    RegionalContext householdContext = new DefaultRegionalContext(RegionalLevel.zone, zone.getExternalId());

    ArrrayWeightedHouseholdsCreator creator = new ArrrayWeightedHouseholdsCreator(context, panelData);
    
    ArrayWeightedHouseholds weightedHouseholds = creator.createFor(district);
    List<WeightedHousehold> createdHouseholds = weightedHouseholds.toList();
    
    assertThat(createdHouseholds)
        .contains(new WeightedHousehold(id, weight, attributes, householdContext, panelHousehold));
  }

  protected Demography createDistrictDemography() {
    RangeDistribution distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(1, 1));
    return new Demography(EmploymentDistribution.createDefault(),
        Map.of(StandardAttribute.householdSize, distribution));
  }

  protected Demography createZoneDemography() {
    RangeDistribution distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(0, 5, 1));
    return new Demography(EmploymentDistribution.createDefault(),
        Map.of(StandardAttribute.femaleAge, distribution));
  }
}
