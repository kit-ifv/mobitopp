# mobiTopp
[mobiTopp](http://mobitopp.ifv.kit.edu/) is an agent-based travel demand model developed at the [Institute for transport studies at the Karlsruhe Institute of Technology](http://www.ifv.kit.edu/english/index.php). Publications about mobiTopp can be found on the [project site](http://mobitopp.ifv.kit.edu/28.php).

## Execution
### Using gradle
The example project comes shipped with gradle tasks to run it directly from source. A population can be generated with the `runPopulationSynthesis` task and travel demand can be simulated using the `runSimulation` task. 

```
./gradlew runPopulationSynthesis runSimulation
```

### Using eclipse
The example project contains preconfigured launch configurations to create a population and simulate the travel demand. Eclipse automatically detects them and you can run them from the toolbar as every other launch configuration.

## Results
mobiTopp generates various output files during population synthesis and simulation. Most of the output data is self-explanatory. For those values which are not, the following tables map the codes to their values.

### ActivityType
Code | Description
--- | ---
1 | WORK
2 | BUSINESS
3 | EDUCATION
4 | SHOPPING
5 | LEISURE
6 | SERVICE
7 | HOME
8 | UNDEFINED
9 | OTHERHOME
11 | PRIVATE_BUSINESS
12 | PRIVATE_VISIT
21 | BUSINESS_OUT
22 | BUSINESS_TO_WORK
31 | EDUCATION_PRIMARY
32 | EDUCATION_SECONDARY
33 | EDUCATION_TERTIARY
34 | EDUCATION_OCCUP
41 | SHOPPING_DAILY
42 | SHOPPING_OTHER
51 | LEISURE_INDOOR
52 | LEISURE_OUTDOOR
53 | LEISURE_OTHER
71 | PICK_UP_PARCEL
77 | LEISURE_WALK

### ModeType
Code | Description
--- | ---
-2 | UNDEFINED
-1 | UNKNOWN
0 | BIKE
1 | CAR
2 | PASSENGER
3 | PEDESTRIAN
4 | PUBLICTRANSPORT
5 | TRUCK
6 | PARK_AND_RIDE
7 | TAXI
11 | CARSHARING_STATION
12 | CARSHARING_FREE
15 | E_SCOOTER
16 | PEDELEC
17 | BIKESHARING
21 | RIDE_POOLING
22 | RIDE_HAILING
23 | PREMIUM_RIDE_HAILING

### EmploymentType
Code | Description
--- | ---
-1 | UNKNOWN
1 | FULLTIME
2 | PARTTIME
22 | MARGINAL
3 | UNEMPLOYED
4 | STUDENT
40 | STUDENT_PRIMARY
41 | STUDENT_SECONDARY
42 | STUDENT_TERTIARY
5 | EDUCATION
6 | HOMEKEEPER
7 | RETIRED
8 | INFANT
9 | NONE

### Gender
Code | Description
--- | ---
1 | MALE
2 | FEMALE

### DomCode
Code | Size of household | Number of cars
--- | --- | --- 
1 | 1 | 0
2 | 1 | 1
3 | 1 | >=2
4 | 2 | 0
5 | 2 | 1
6 | 2 | >=2
7 | 3 | 0
8 | 3 | 1
9 | 3 | >=2
10 | >=4 | 0
11 | >=4 | 1
12 | >=4 | >=2
