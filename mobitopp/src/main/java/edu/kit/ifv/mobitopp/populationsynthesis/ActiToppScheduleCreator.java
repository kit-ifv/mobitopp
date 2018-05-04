package edu.kit.ifv.mobitopp.populationsynthesis;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson;
import edu.kit.ifv.mobitopp.actitopp.Configuration;
import edu.kit.ifv.mobitopp.actitopp.HActivity;
import edu.kit.ifv.mobitopp.actitopp.InvalidPatternException;
import edu.kit.ifv.mobitopp.actitopp.ModelFileBase;
import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.actitopp.RNGHelper;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;


/**
 * 
 * Klasse zur Erzeugung der Aktivitätenpläne
 * Implementiert das Interface
 * 
 * @author Tim Hilgert
 *
 */
public class ActiToppScheduleCreator implements ActivityScheduleCreator
{
	
	private final ActiToppCategories categories;
	
	// Personenindex
	private int personIndex = 0;

  // Dateibasis zum Einlesen der Parameter
  private ModelFileBase fileBase;
  
  // Zufallszahlengenerator
  private RNGHelper randomgenerator;
    
  // DEBUG USE ONLY
  
  private static ArrayList<PatternActivityWeek> activities = new ArrayList<PatternActivityWeek>(); 
  public static HashMap<Number, ActitoppPerson> personmap = new HashMap<Number, ActitoppPerson>();
  public static HashMap<Number, List<HActivity>> schedulemap = new HashMap<Number, List<HActivity>>();
  private int globalretries = 0;

  /**
   * 
   * Konstruktor
   * 
   * @param seed
   */
  public ActiToppScheduleCreator(long seed)
  {
  	categories = new ActiToppCategories();
      fileBase = new ModelFileBase();
      randomgenerator = new RNGHelper(seed);
  }
		

  /**
   * 
   * Methode zur Erstellung des Wochen-Aktivitätenplans
   * Gibt den fertigen Plan mit Aktivitäten zurück
   * 
   */
  @Override
	public PatternActivityWeek createActivitySchedule(
			PersonOfPanelData aPersonOfPanelData,
			HouseholdOfPanelData householdOfPanelData,
			Household household
	)
	{
  	// Initialisierungen
    PatternActivityWeek patternActivityWeek= new PatternActivityWeek();
  	
    // Durchführung nur für x% der Bevölkerung
  	if ((personIndex % (100/Configuration.percent_bevsynthese)) == 0)
    { 	
	  	//Erzeuge neue Person
			ActitoppPerson personOfActiTopp = new ActitoppPerson(
					personIndex,
					householdOfPanelData.numberOfNotReportingChildren(),
					householdOfPanelData.numberOfMinors(),
					aPersonOfPanelData.age(),
					aPersonOfPanelData.getEmploymentTypeAsInt(),
					aPersonOfPanelData.getGenderTypeAsInt(),
					household.homeZone().getAreaType().getTypeAsInt(),
					householdOfPanelData.numberOfCars()  				
					); 		
			
			// DEBUG purpose
		  // System.out.println(personOfActiTopp);
			
	    // Erzeuge solange Schedules, bis kein InvalidPatternException mehr vorliegt
	    boolean scheduleOK = false;
	    while (!scheduleOK)
	    {
        try
        {
      		// Erzeuge Wochenaktivitätenplan
          personOfActiTopp.generateSchedule(fileBase, randomgenerator);
          
	        // Konvertiere alle Aktivitäten in mobiTopp-Datentypen
	        for (HActivity act : personOfActiTopp.getWeekPattern().getAllActivities())
	        {
	        	patternActivityWeek.addPatternActivity(convertActiToppTomobiToppActivityType(act));
	        }
      		
          scheduleOK = true;                
        }
        catch (InvalidPatternException e)
        {
          globalretries++;
          
          //  System.err.println(e.getReason());
          //  System.err.println("person involved: " + this.personIndex);
          //  System.err.println("RetryNumber : " + retryIndex);
        }
	    }
	    
	    // Füge die Act und die Person für den Export den Maps hinzu
	    schedulemap.put(personIndex, personOfActiTopp.getWeekPattern().getAllActivities());
	    personmap.put(personIndex, personOfActiTopp);
	    
	    
	    activities.add(patternActivityWeek);
    }
    personIndex++;
    
    // Statusinformationen und Log4J-Export
    if (personIndex % Configuration.anzexportds == 0)
    {
    	System.out.println("Doing person Nr... " + personIndex);
    	int prozent = (int) Math.round(100*((double) globalretries/(double) Configuration.anzexportds));
    	System.err.println("Anzahl Retries: " + globalretries + " (~ " + prozent + "% der Personen)");
    	globalretries=0;
    	
    	exportData();
    }
    
	  return patternActivityWeek;
	}

  /**
   * 
   * 
   * @param until
   */
  private void exportData()
  {

    // double rnd = Math.random();
    // Serial-Dateien
    //writeSerializeData("" + rnd);
    // CSV-Dateien Personen
    //writeCSVPersonData("Rohdaten_Personen", until);
    // CSV-Dateien Activities
    //writeCSVActData("Rohdaten_Wege", until);
    // Log4J-Dateien loggen
    writeLog4JData();

  	personmap.clear();
    schedulemap.clear();
  	activities.clear();
  }

  /**
   * 
   * Schreibe Serial-Daten für Debug
   * 
   * @param partition
   * @throws IOException
   */
  @SuppressWarnings("unused")
	private void writeSerializeData(String partition) throws IOException
  {
      File tmp = new File("log/populationsynthesis/serials/" + partition + "dataActiTopp.ser");
      tmp.createNewFile();
      FileOutputStream fileOut = new FileOutputStream(tmp);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(activities);
      out.close();
      fileOut.close();
  }
    
  /**
   * 
   * Schreibe CSV-Daten für Debug
   * 
   * @param partition
   * @param untilindex
   * @throws IOException
   */
	@SuppressWarnings("unused")
  private void writeCSVPersonData(String partition, int untilindex) throws IOException
  {
  	// Erstelle die Datei
  	FileWriter writer = new FileWriter("log/populationsynthesis/csv/" + partition + ".csv");
  	
  	// Header
  	writer.append("HHID;Jahr;PersNr;P0_10;kinder_u18;Alter;BERUF;SEX;Raumtyp_mobitopp;Pkwhh");
  	writer.append('\n');
  	writer.flush();

  	// Füge alle Personendaten der Datei hinzu
  	for (int i=(untilindex-Configuration.anzexportds); i<untilindex; i++)
  	{
  		ActitoppPerson person = personmap.get(i);
  		
  		//Output	ID, Jahr, PersNr, P0_10, kinder_u18, Alter, BERUF, SEX, Raumtyp, Pkwhh 
  		
  		
  		// ID
  		writer.append(i + ";");
  		// Jahr
  		writer.append("2016" + ";");
  		// PersNr
  		writer.append("1" + ";");
  		// P0_10
  		writer.append(person.getChildren0_10() + ";");
  		// kinder_u18
  		writer.append(person.getChildren_u18() + ";");
  		// Alter    		
  		writer.append(person.getAge() + ";");
  		// Beruf
  		writer.append(person.getEmployment() + ";");
  		// Sex
  		writer.append(person.getGender() + ";");
  		// Raumtyp 
  		writer.append(person.getAreatype() + ";");
  		// PKWHH
  		writer.append(person.getNumberofcarsinhousehold() + "");
  		
  		// Abschluss
  		writer.append('\n'); 		
  		
  		writer.flush();
  	}
  	writer.close();
  	String Uhrzeit = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date());  	
  	System.out.println("CSV Data Person geschrieben " + Uhrzeit);
  }
 
  /**
   * 
   * Schreibe CSV-Daten für Debug
   * 
   * @param partition
   * @param untilindex
   * @throws IOException
   */
	@SuppressWarnings("unused")
  private void writeCSVActData(String partition, int untilindex) throws IOException
  {
  	// Erstelle die Datei
  	FileWriter writer = new FileWriter("log/populationsynthesis/csv/" + partition + ".csv");
  	
  	// Header
  	writer.append("HHID;Jahr;PersNr;BERTAG;WOTAG;abzeit_woche;anzeit_woche;Dauer;zweck");
  	writer.append('\n');
  	writer.flush();

  	// Durlaufe alle ActivityLists
  	for (int i=(untilindex-Configuration.anzexportds); i<untilindex; i++)
  	{
  		List<HActivity> actschedule = schedulemap.get(i);
  		
  		// Füge alle Aktivitäten der Datei hinzu
  		for (HActivity act : actschedule)
  		{
  			if (act.getEstimatedTripTime()!=0)
  			{
    			// ID
        		writer.append(i + ";");
        		// Jahr
        		writer.append("2016" + ";");
        		// PersNr
        		writer.append("1" + ";");
        		// BERTAG
        		writer.append(act.getWeekDay() + ";");
        		// WOTAG
        		writer.append(act.getWeekDay() + ";");
        		// abzeit_woche
        		writer.append(act.getTripStartTimeWeekContext() + ";");  
        		// anzeit_woche
        		writer.append(act.getStartTimeWeekContext() + ";");      		
    			// Dauer
    			writer.append(act.getEstimatedTripTime() + ";");
    			// Zweck
    			writer.append(act.getMobiToppActType() + "");
    			
    			// Abschluss
        		writer.append('\n');
        		writer.flush();
  			}
  		}
  	}
  	writer.close();
  	String Uhrzeit = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date());  	
  	System.out.println("CSV Data Act geschrieben " + Uhrzeit);
  }
    
  /**
   * 
   * Schreibt die actiTopp Ergebnisse in die Log-Dateien
   * 
   */
  private void writeLog4JData()
  {
  	try(ResultWriter resultWriter = ResultWriter.createDefaultWriter()){
  		logElementsTo(resultWriter);
  	}
  	String Uhrzeit = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date());  	
  	System.out.println("Log4J Data geschrieben " + Uhrzeit);
  }


	private void logElementsTo(ResultWriter results) {
		for (Entry<Number, ActitoppPerson> e : personmap.entrySet())
  	{
  		ActitoppPerson person = personmap.get(e.getKey());
			List<HActivity> actschedule = schedulemap.get(e.getKey());

  		//////////////////
  		//	Personen
  		//////////////////
  		
  		//Output	ID, Jahr, PersNr, P0_10, kinder_u18, Alter, BERUF, SEX, Raumtyp, Pkwhh 
  		
  		CsvBuilder log_person = new CsvBuilder();
  		// PersID
  		log_person.append(person.getPersIndex());
  		// Jahr	    
  		log_person.append(new GregorianCalendar().get(Calendar.YEAR));
  		// PersNr
  		log_person.append("1");
  		// P0_10
  		log_person.append(person.getChildren0_10());
  		// kinder_u18
  		log_person.append(person.getChildren_u18());
  		// Alter    		
  		log_person.append(person.getAge());
  		// Beruf
  		log_person.append(person.getEmployment());
  		// Sex
  		log_person.append(person.getGender());
  		// Raumtyp 
  		log_person.append(person.getAreatype());
  		// PKWHH
  		log_person.append(person.getNumberofcarsinhousehold());
  		    		
  		
  		// Schreibe die log4j Datei
			results.write(categories.person, log_person.toString());
  		
  		//////////////////
  		//	Wege
  		//////////////////   		

  		for (HActivity act : actschedule)
  		{
  			if (act.getEstimatedTripTime()!=0)
  			{
  				CsvBuilder log_trip = new CsvBuilder();
  				
  				// PersID
      		log_trip.append(person.getPersIndex());
      		// Jahr
      		log_trip.append(new GregorianCalendar().get(Calendar.YEAR));
      		// PersNr
      		log_trip.append("1");
      		// BERTAG
      		log_trip.append(act.getWeekDay());
      		// WOTAG
      		log_trip.append(act.getWeekDay());
      		// abzeit_woche
      		log_trip.append(act.getTripStartTimeWeekContext());  
      		// anzeit_woche
      		log_trip.append(act.getStartTimeWeekContext());      		
      		// Dauer
      		log_trip.append(act.getEstimatedTripTime());
      		// Zweck
      		log_trip.append(act.getMobiToppActType());/*+ ";";
      		// Aktdauer
      		log_trip.append(act.getDuration() + "";
      		*/ 	
      		
      		// Schreibe die log4j Datei
     			results.write(categories.trip, log_trip.toString());
  			}
  		}
  	}
	}
    
    /**
  	 * 
  	 * Methode konvertiert die Aktivität in den mobiTopp Simulation Aktivitätentyp
  	 * 
  	 * @param stub
  	 * @return
  	 */
  	public PatternActivity convertActiToppTomobiToppActivityType(HActivity act)
  	{   	
  	    DayOfWeek wd = DayOfWeek.fromDay(act.getIndexDay());
  	    PatternActivity activity = new PatternActivity(ActivityType.getTypeFromInt(act.getMobiToppActType()), wd, act.getEstimatedTripTime(), act.getStartTime(), act.getDuration());
  	    return activity;
  	}

}
