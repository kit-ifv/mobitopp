package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

class HouseholdWeightCalculator 
	implements HouseholdWeightCalculatorIfc
{

	private Map<HouseholdOfPanelDataId,HouseholdOfPanelData> households;
	private Map<HouseholdOfPanelDataId,List<PersonOfPanelData>> persons;

	private Map<Integer,Set<HouseholdOfPanelDataId>> hh_male = new TreeMap<Integer,Set<HouseholdOfPanelDataId>>();
	private Map<Integer,Set<HouseholdOfPanelDataId>> hh_female = new TreeMap<Integer,Set<HouseholdOfPanelDataId>>();
	private Map<Integer,Set<HouseholdOfPanelDataId>> hh_emp = new TreeMap<Integer,Set<HouseholdOfPanelDataId>>();

	private boolean hh_lists_initialized = false;

	private Gender nextChildGender = Gender.MALE;



	public List<HouseholdOfPanelDataId> calculateWeights(
		RangeDistributionIfc hhDistribution,
		EmploymentDistribution empDistribution,
		RangeDistributionIfc maleAgeDistribution,
		RangeDistributionIfc femaleAgeDistribution,
		List<HouseholdOfPanelDataId> householdOfPanelDataIds,
		Map<HouseholdOfPanelDataId,HouseholdOfPanelData> households,
		Map<HouseholdOfPanelDataId,List<PersonOfPanelData>> persons
	) {

		this.households = households;
		this.persons = persons;

		SortedMap<Integer,Double> maleDist = normalizedDistribution(maleAgeDistribution);
		SortedMap<Integer,Double> femaleDist = normalizedDistribution(femaleAgeDistribution);
		SortedMap<Integer,Double> empDist = normalizedDistribution(empDistribution);
		SortedMap<Integer,Double> hhDist = normalizedDistribution(hhDistribution);


		if(!this.hh_lists_initialized) {
			initHHIdLists(maleDist, femaleDist, householdOfPanelDataIds);
			this.hh_lists_initialized = true;
		}

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {
			hhId.set_weight(1.0);
		}

		for (int i=0; i <20; i++) {

			Map<String,SortedMap<Integer,Double>> quotients 
				= calculateWeightedDistributions(
						maleDist,
						femaleDist,
						empDist,
						hhDist,
						householdOfPanelDataIds
					);

			adjustHouseholdWeights(
				quotients.get("MALE"),
				quotients.get("FEMALE"),
				quotients.get("EMP")
			);

			normalizeHHWeights(householdOfPanelDataIds);

		}


		return new ArrayList<HouseholdOfPanelDataId>();
	}

	private void adjustHouseholdWeights(
		SortedMap<Integer,Double> male,
		SortedMap<Integer,Double> female,
		SortedMap<Integer,Double> emp
	) {

		for (Integer key : male.keySet()) {
			double quotient = male.get(key);

			if (this.hh_male.get(key) != null) {
				for (HouseholdOfPanelDataId hhId : this.hh_male.get(key)) {
					double weight = hhId.get_weight();
					hhId.set_weight(weight*Math.sqrt(quotient));
				}
			}
		}

		for (Integer key : female.keySet()) {
			double quotient = female.get(key);

			if (this.hh_female.get(key) != null) {
				for (HouseholdOfPanelDataId hhId : this.hh_female.get(key)) {
					double weight = hhId.get_weight();
					hhId.set_weight(weight*Math.sqrt(quotient));
				}
			}
		}

		for (Integer key : emp.keySet()) {
			double quotient = emp.get(key);

			if (this.hh_emp.get(key) != null) {
				for (HouseholdOfPanelDataId hhId : this.hh_emp.get(key)) {
					double weight = hhId.get_weight();
					hhId.set_weight(weight*Math.sqrt(quotient));
				}
			}
		}

	}

	private void normalizeHHWeights(List<HouseholdOfPanelDataId> householdOfPanelDataIds) {

		double total = 0.0;

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {
			total += hhId.get_weight();
		}

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {
			double weight = hhId.get_weight() / total * householdOfPanelDataIds.size();
			hhId.set_weight(weight);
		}
	}

	protected Map<String,SortedMap<Integer,Double>> calculateWeightedDistributions(
		SortedMap<Integer,Double> maleDist,
		SortedMap<Integer,Double> femaleDist,
		SortedMap<Integer,Double> empDist,
		SortedMap<Integer,Double> hhDist,
		List<HouseholdOfPanelDataId> householdOfPanelDataIds
	) {

		SortedMap<Integer,Double> maleCurr = initDistribution(maleDist);
		SortedMap<Integer,Double> femaleCurr = initDistribution(femaleDist);
		SortedMap<Integer,Double> empCurr = initDistribution(empDist);

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {

			HouseholdOfPanelData hh = this.households.get(hhId);

			double hh_weight = hhId.get_weight();

			int domcode = hh.domCode();

			double domcode_weight = hhDist.get(domcode);

			double children = hh.numberOfNotReportingChildren();
			incrementDistribution(maleCurr, 5, 0.5*children*hh_weight*domcode_weight);
			incrementDistribution(femaleCurr, 5, 0.5*children*hh_weight*domcode_weight);
			incrementDistribution(empCurr, 8, children*hh_weight*domcode_weight);

			for (PersonOfPanelData pers : this.persons.get(hhId)) {

				int age = pers.age();
				int emp = empDistributionType(pers.getEmploymentTypeAsInt());
				int sex = pers.getGenderTypeAsInt();

				incrementDistribution(empCurr, emp, hh_weight*domcode_weight);

				if (sex == 1) {
					incrementDistribution(maleCurr, age, hh_weight*domcode_weight);
				} else if (sex ==2) {
					incrementDistribution(femaleCurr, age, hh_weight*domcode_weight);
				} else {
							throwIncorrectGenderFor(pers);
				}

			}
		}

		Map<String,SortedMap<Integer,Double>> result = new TreeMap<String,SortedMap<Integer,Double>>();

		result.put("MALE",  calcQuotient(maleDist, normalizedDistribution(maleCurr)));
		result.put("FEMALE", calcQuotient(femaleDist, normalizedDistribution(femaleCurr)));
		result.put("EMP", calcQuotient(empDist, normalizedDistribution(empCurr)));

		return result;
	}


	private SortedMap<Integer,Double> normalizedDistribution(RangeDistributionIfc distribution) {

		SortedMap<Integer,Double> data = new TreeMap<Integer,Double>();

		double total = distribution.getTotalAmount();

		for (RangeDistributionItem item:
					(Collection<RangeDistributionItem>) distribution.getItems()) {

			double new_val =  Math.max(item.amount()/total,0.001d);

			data.put(item.upperBound(),new_val);
		}

		return data;
	}

	private SortedMap<Integer,Double> normalizedDistribution(EmploymentDistribution distribution) {

		SortedMap<Integer,Double> data = new TreeMap<Integer,Double>();

		double total = distribution.getTotalAmount();

		for (EmploymentDistributionItem item:
					(Collection<EmploymentDistributionItem>) distribution.getItems()) {

			double new_val =  Math.max(item.amount()/total,0.001d);

			data.put(item.getTypeAsInt(),new_val);
		}

		return data;
	}

	private SortedMap<Integer,Double> normalizedDistribution(Map<Integer,Double> distribution) {

		SortedMap<Integer,Double> data = new TreeMap<Integer,Double>();

		double total = 0.0;

		for (Integer key : distribution.keySet()) {
			total += distribution.get(key);
		}


		for (Integer key : distribution.keySet()) {

			double val = distribution.get(key);

			if (total != 0.0) {
				data.put(key, val/total);
			} else {
				data.put(key, 0.0);
			}
			
		}

		return data;
	}

	private SortedMap<Integer,Double> initDistribution(Map<Integer,Double> dist) {

		SortedMap<Integer,Double> data = new TreeMap<Integer,Double>();

		for (Integer key : dist.keySet()) {

			data.put(key,0.0);
		}

		return data;
	}

	private void incrementDistribution(SortedMap<Integer,Double> distribution, Integer key, double value) {
		SortedMap<Integer,Double> tail = distribution.tailMap(key);
		Integer dist_key;

		if (!tail.isEmpty()) {
			dist_key = tail.firstKey();
		} else {
			dist_key = distribution.lastKey();
		}
		distribution.put(dist_key, distribution.get(dist_key)+value);
	}

	private int empDistributionType(int val) {

		switch(val) {
			case 1:
			case 2:
			case 40:
			case 41:
			case 42:
			case 5:
			case 7:
			case 8:
				return val;
			case 3:
			case 6:
				return 9;
			default:
				return 0;
		}

	}

	private SortedMap<Integer,Double> calcQuotient(Map<Integer,Double> dist1, Map<Integer,Double> dist2) {

		SortedMap<Integer,Double> quotient = new TreeMap<Integer,Double>();

		for (Integer key : dist1.keySet()) {
			double val1 = dist1.get(key);
			double val2 = dist2.get(key);


			quotient.put(key, val1/val2);
		}

		return quotient;
	}

	private void initHHIdLists(
		SortedMap<Integer,Double> maleAgeDistribution,
		SortedMap<Integer,Double> femaleAgeDistribution,
		List<HouseholdOfPanelDataId> householdOfPanelDataIds
	) {

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {

			for (PersonOfPanelData pers : this.persons.get(hhId)) {

				int age = pers.age();
				int emp = empDistributionType(pers.getEmploymentTypeAsInt());
				int sex = pers.getGenderTypeAsInt();

				if (!this.hh_emp.containsKey(emp)) {
					this.hh_emp.put(emp, new TreeSet<HouseholdOfPanelDataId>());
				}

				this.hh_emp.get(emp).add(hhId);

				if (sex == 1) {
					addToHHList(this.hh_male, age, hhId, maleAgeDistribution);
				} else if (sex ==2) {
					addToHHList(this.hh_female, age, hhId, femaleAgeDistribution);
				} else {
					throwIncorrectGenderFor(pers);
				}
			}

			int emp = Employment.INFANT.getTypeAsInt();

			if (!this.hh_emp.containsKey(emp)) {
				this.hh_emp.put(emp, new TreeSet<HouseholdOfPanelDataId>());
			}

			HouseholdOfPanelData hh = this.households.get(hhId);

			for (int i=0; i<= hh.numberOfNotReportingChildren(); i++) {
			
				this.hh_emp.get(emp).add(hhId);
			
				if (this.nextChildGender == Gender.MALE) {
					addToHHList(this.hh_male, 5, hhId, maleAgeDistribution);
					this.nextChildGender = Gender.FEMALE;
				} else {
					addToHHList(this.hh_female, 5, hhId, femaleAgeDistribution);
					this.nextChildGender = Gender.MALE;
				}
			}
		}
	}

	private void throwIncorrectGenderFor(PersonOfPanelData pers) {
		throw new IllegalArgumentException("Incorrect gender for person: " + pers);
	}

	private void addToHHList(
		Map<Integer,Set<HouseholdOfPanelDataId>> hh_list,
		Integer key,
		HouseholdOfPanelDataId hhId,
		SortedMap<Integer,Double> distribution
	) {

		SortedMap<Integer,Double> tail = distribution.tailMap(key);

		Integer dist_key;

		if (!tail.isEmpty()) {
			dist_key = tail.firstKey();
		} else {
			dist_key = distribution.lastKey();
		}

		if (!hh_list.containsKey(dist_key)) {
			hh_list.put(dist_key, new TreeSet<HouseholdOfPanelDataId>());
		}
		hh_list.get(dist_key).add(hhId);

	}


}
