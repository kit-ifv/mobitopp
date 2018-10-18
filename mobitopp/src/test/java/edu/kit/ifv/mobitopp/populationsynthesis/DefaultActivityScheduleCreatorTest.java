package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.data.local.ExamplePersonOfPanelData;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class DefaultActivityScheduleCreatorTest {

	private static final int tripDuration = 1;
	private static final int duration = 2;
	private static final int startTime = 3;
	private static final Time start = Time.start.plusMinutes(startTime);
	private HouseholdOfPanelData unusedPanelHousehold;
	private Household unusedHousehold;
	private ActivityOfPanelData work;
	private ActivityOfPanelData home;
	private DefaultActivityScheduleCreator creator;
	private PatternFixer fixer;

	@Before
	public void initialise() {
		unusedPanelHousehold = ExampleHouseholdOfPanelData.household;
		unusedHousehold = mock(Household.class);
		fixer = mock(PatternFixer.class);
		work = new ActivityOfPanelData(tripDuration, ActivityType.WORK, duration, startTime);
		home = new ActivityOfPanelData(tripDuration, ActivityType.HOME, duration, startTime);

		when(fixer.ensureIsTour(any())).then(invocation -> invocation.getArgument(0));

		creator = new DefaultActivityScheduleCreator(fixer);
	}

	@Test
	public void useCachedActivities() {
		String pattern = correctTour();

		PatternActivityWeek activities = createActivitiesFrom(pattern);
		PatternActivityWeek cachedActivities = createActivitiesFrom(pattern);

		PatternActivityWeek completeTour = createCompleteTour();
		assertThat(activities, is(equalTo(completeTour)));
		assertThat(activities, sameInstance(cachedActivities));
		verify(fixer).ensureIsTour(any());
	}

	private PatternActivityWeek createCompleteTour() {
		PatternActivityWeek week = new PatternActivityWeek();
		week.addPatternActivity(createActivityFor(ActivityType.HOME));
		week.addPatternActivity(createActivityFor(ActivityType.WORK));
		week.addPatternActivity(createActivityFor(ActivityType.HOME));
		return week;
	}

	private PatternActivity createActivityFor(ActivityType type) {
		return new PatternActivity(type, tripDuration, start, duration);
	}

	private String correctTour() {
		return home.asCSV() + ";" + work.asCSV() + ";" + home.asCSV();
	}

	private PatternActivityWeek createActivitiesFrom(String pattern) {
		PersonOfPanelData panelPerson = ExamplePersonOfPanelData.personWithPattern(pattern);
		return creator.createActivitySchedule(panelPerson, unusedPanelHousehold, unusedHousehold);
	}
	
	private static final int unknown = -1;
	private static final int otherTripDuration = 15;
	private static final int workDuration = 315;
	private static final int firstHomeDuration = 6260;
	private static final int secondHomeDuration = 35;
	private static final int workStart = firstHomeDuration + otherTripDuration;

	@Test
	public void createsActivitySchedule() {
		int secondHomeStart = firstHomeDuration + otherTripDuration + workDuration + otherTripDuration;
		PatternActivityWeek pattern = createHomeStartPattern(secondHomeStart);

		assertThat(pattern, is(equalTo(expectedPattern())));
	}

	@Test
	public void createsActivityScheduleForMissingStartTimes() {
		int secondHomeStart = unknown;
		PatternActivityWeek pattern = createHomeStartPattern(secondHomeStart);

		assertThat(pattern, is(equalTo(expectedPattern())));
	}

	@Test
	public void createsActivityScheduleForMissingTripTimes() {
		PatternActivityWeek pattern = createPatternWithUnknownTripDuration();

		assertThat(pattern, is(equalTo(expectedPattern(unknown))));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsToParsePatternsWithIncorrectLength() {
		String tooShortPattern = "1;2;3";
		PersonOfPanelData person = newPersonFor(tooShortPattern);
		createPatternFor(person);
	}
	
	@Test
	public void parsesEmptyPattern() {
		String emptyPattern = "";
		PersonOfPanelData person = newPersonFor(emptyPattern);
		PatternActivityWeek pattern = createPatternFor(person);
		
		assertThat(pattern, is(equalTo(PatternActivityWeek.WHOLE_WEEK_AT_HOME)));
	}

	private PatternActivityWeek createPatternWithUnknownTripDuration() {
		int secondHomeStart = unknown;
		int workTripDuration = unknown;
		int workStart = unknown;
		return createPatternFor(secondHomeStart, workTripDuration, workStart);
	}

	private PatternActivityWeek createHomeStartPattern(int secondHomeStart) {
		return createPatternFor(secondHomeStart, otherTripDuration, workStart);
	}

	private PatternActivityWeek createPatternFor(
			int secondHomeStart, int workTripDuration, int workStart) {
		int firstTrip = unknown;
		int firstStart = unknown;
		CsvBuilder pattern = new CsvBuilder(";");
		pattern.append(firstTrip);
		pattern.append(ActivityType.HOME.getTypeAsInt());
		pattern.append(firstHomeDuration);
		pattern.append(firstStart);
		pattern.append(workTripDuration);
		pattern.append(ActivityType.WORK.getTypeAsInt());
		pattern.append(workDuration);
		pattern.append(workStart);
		pattern.append(otherTripDuration);
		pattern.append(ActivityType.HOME.getTypeAsInt());
		pattern.append(secondHomeDuration);
		pattern.append(secondHomeStart);
		String activityPattern = pattern.toString();
		PersonOfPanelData panelPerson = newPersonFor(activityPattern);
		return createPatternFor(panelPerson);
	}

	private PatternActivityWeek createPatternFor(PersonOfPanelData panelPerson) {
		DefaultActivityScheduleCreator creator = new DefaultActivityScheduleCreator();
		return creator.createActivitySchedule(panelPerson, unusedPanelHousehold, mock(Household.class));
	}

	private PersonOfPanelData newPersonFor(String activityPattern) {
		return ExamplePersonOfPanelData
				.createPersonWith(ExamplePersonOfPanelData.anId, activityPattern);
	}

	private PatternActivityWeek expectedPattern() {
		return expectedPattern(otherTripDuration);
	}

	private PatternActivityWeek expectedPattern(int workTripDuration) {
		List<PatternActivity> activities = new ArrayList<>();
		Time firstActivityStart = Time.start;
		Time workStart = firstActivityStart.plusMinutes(firstHomeDuration).plusMinutes(otherTripDuration);
		Time secondHomeStart = workStart
				.plusMinutes(workDuration)
				.plusMinutes(otherTripDuration);
		PatternActivity firstHome = new PatternActivity(ActivityType.HOME, unknown, firstActivityStart,
				firstHomeDuration);
		PatternActivity work = new PatternActivity(ActivityType.WORK, workTripDuration, workStart,
				workDuration);
		PatternActivity secondHome = new PatternActivity(ActivityType.HOME, otherTripDuration,
				secondHomeStart, secondHomeDuration);
		activities.add(firstHome);
		activities.add(work);
		activities.add(secondHome);
		return new PatternActivityWeek(activities);
	}
}
