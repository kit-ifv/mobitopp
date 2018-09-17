package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPatternCreator;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferenceCreator;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.modeChoice.UncorrelatedModeChoicePreferenceCreator;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Employment;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class DefaultPersonCreator 
	implements PersonCreator 
{

	private final ActivityScheduleCreator scheduleCreator;
	private final CommutationTicketModelIfc commutationTicketModel;
	private final ModeChoicePreferenceCreator modeChoicePreferenceCreator;

	private static int personIdCounter = 1;
	
	private Map<PersonOfPanelDataId,TourBasedActivityPattern> cachedActivityPattern = new LinkedHashMap<>();

	public DefaultPersonCreator(
		ActivityScheduleCreator scheduleCreator,
		CommutationTicketModelIfc commutationTicketModel,
		ModeChoicePreferenceCreator modeChoicePreferenceCreator
	) {
		this.scheduleCreator = scheduleCreator;
		this.commutationTicketModel = commutationTicketModel;
		this.modeChoicePreferenceCreator = modeChoicePreferenceCreator;
	}
	
	public DefaultPersonCreator(
		ActivityScheduleCreator scheduleCreator,
		CommutationTicketModelIfc commutationTicketModel
		) {
		this(scheduleCreator,commutationTicketModel,new UncorrelatedModeChoicePreferenceCreator(0));
	}
	


	public Person createPerson(
		PersonOfPanelData panelPerson,
		HouseholdOfPanelData panelHousehold,
		Household household,
		Zone zone
	) {
			boolean hasCommuterTicket = this.commutationTicketModel.estimateCommutationTicket(
				panelPerson,
				panelHousehold,
				zone
				);
			
			
			PatternActivityWeek activityPattern = this.scheduleCreator.createActivitySchedule(
																																													panelPerson,
																																													panelHousehold,
																																													household
																																												);
			
			

			TourBasedActivityPattern activitySchedule;
			
			if(cachedActivityPattern.containsKey(panelPerson.getId())) {
				
				activitySchedule = cachedActivityPattern.get(panelPerson.getId());
			} else {
				
				activitySchedule = TourBasedActivityPatternCreator.fromPatternActivityWeek(activityPattern);
				cachedActivityPattern.put(panelPerson.getId(), activitySchedule);
			}
			
			Person person = newPerson(
    										personIdCounter++,
												panelPerson, 
												household,
												hasCommuterTicket,
												activitySchedule
											);
			


		return person;
	}

	protected Person newPerson(
			int oid,
			PersonOfPanelData personOfPanelData,
			Household household,
			boolean hasCommuterTicket,
			TourBasedActivityPattern activitySchedule
	) {

    PersonOfPanelDataId panel_id = personOfPanelData.getId(); 

    PersonId pid = 
      new PersonId(
        household.getId(), panel_id.getPersonNumber());

    int age 										= personOfPanelData.age();
		Employment employment 			= personOfPanelData.employment();
		Gender gender 					= personOfPanelData.gender();
		int income 							= personOfPanelData.getIncome();
		
		Map<Mode,Double> preferences = new LinkedHashMap<Mode,Double>();
		preferences.put(Mode.BIKE,(double) personOfPanelData.pref_cycling);
		preferences.put(Mode.CAR,(double) personOfPanelData.pref_cardriver);
		preferences.put(Mode.PASSENGER,(double) personOfPanelData.pref_carpassenger);
		preferences.put(Mode.PEDESTRIAN,(double) personOfPanelData.pref_walking);
		preferences.put(Mode.PUBLICTRANSPORT,(double) personOfPanelData.pref_publictransport);
		
		ModeChoicePreferences modePreferences = new ModeChoicePreferences(preferences);

    PersonForSetup person = new PersonForDemand(
													oid, 
													pid, 
													household,
													age,
													Employment.getTypeFromInt(employment.getTypeAsInt()),
													Gender.getTypeFromInt(gender.getTypeAsInt()),
													income,
													personOfPanelData.hasBicycle(),
													personOfPanelData.hasAccessToCar(),
													personOfPanelData.hasPersonalCar(),
													hasCommuterTicket,
													personOfPanelData.hasLicence(),
													activitySchedule,
													modePreferences,
													modeChoicePreferenceCreator.createPreferences()
												);


		return person;
	}

}
