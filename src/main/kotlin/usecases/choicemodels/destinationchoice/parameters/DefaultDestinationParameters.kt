package usecases.choicemodels.destinationchoice.parameters

@Suppress("VariableNaming", "ComplexInterface") // I agree with detekt that these names are not good
interface IDefaultDestinationParameters {
    val asc_fuss: Double
    val asc_rad: Double
    val asc_pkw: Double
    val asc_mf: Double
    val asc_oev: Double
    val b_tt_fuss: Double
    val b_tt_rad: Double
    val b_tt_pkw: Double
    val b_tt_mf_taxi: Double
    val b_tt_oev: Double

    // TODO either implement access egress or kill these.
//    val b_zuab_pkw = -0.07635381474178
//    val b_zuab_oev = -0.0264056325589302
    val b_cost_pkw: Double
    val b_cost_oev: Double
    val b_arb_on_fuss: Double
    val b_dienst_on_fuss: Double
    val b_service_on_fuss: Double
    val b_freizeit_on_fuss: Double
    val b_arb_on_rad: Double
    val b_dienst_on_rad: Double
    val b_service_on_rad: Double
    val b_freizeit_on_rad: Double
    val b_arb_on_mf: Double
    val b_dienst_on_mf: Double
    val b_service_on_mf: Double
    val b_home_on_mf: Double
    val b_arb_on_pkw: Double
    val b_dienst_on_pkw: Double
    val b_service_on_pkw: Double
    val b_freizeit_on_pkw: Double
    val b_arb_on_oev: Double
    val b_dienst_on_oev: Double
    val b_service_on_oev: Double
    val b_freizeit_on_oev: Double
    val b_park_oev: Double
    val elasticity_park_oev: Double
}

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
internal object DefaultDestinationParameters : IDefaultDestinationParameters {
    override val asc_fuss = 3.76685346228208 - 1
    override val asc_rad = 1.86423253977476
    override val asc_pkw = 1.22529200766728
    override val asc_mf = -0.280608946005487
    override val asc_oev = 0 + 1.0
    override val b_tt_fuss = -0.143588796056184 + 0.03
    override val b_tt_rad = -0.152026975881995
    override val b_tt_pkw = -0.0382017895663777 - 0.02
    override val b_tt_mf_taxi = -0.0528979328553255
    override val b_tt_oev = -0.0479879518424728

    // TODO either implement access egress or kill these.
//    val b_zuab_pkw = -0.07635381474178
//    val b_zuab_oev = -0.0264056325589302
    override val b_cost_pkw = -0.116630151395857
    override val b_cost_oev = -0.667070030829148
    override val b_arb_on_fuss = -0.611295790763783
    override val b_dienst_on_fuss = -2.08288702898297
    override val b_service_on_fuss = -0.690263124475946
    override val b_freizeit_on_fuss = 0.267490527971426
    override val b_arb_on_rad = 0.866143069057071
    override val b_dienst_on_rad = -0.918456612362237
    override val b_service_on_rad = -1.19958587723503
    override val b_freizeit_on_rad = 0.0
    override val b_arb_on_mf = -1.02713467302909
    override val b_dienst_on_mf = -1.71859251059072
    override val b_service_on_mf = -0.452457466806963
    override val b_home_on_mf = 1.34227424091334
    override val b_arb_on_pkw = 0.0
    override val b_dienst_on_pkw = -1.28284371126842
    override val b_service_on_pkw = -0.0891560297531824
    override val b_freizeit_on_pkw = -1.00158195290812
    override val b_arb_on_oev = 0.814627986341692
    override val b_dienst_on_oev = -0.41496303954142
    override val b_service_on_oev = -0.103220410687913
    override val b_freizeit_on_oev = 0.0999365596556629
    override val b_park_oev = 0.426245178308876
    override val elasticity_park_oev = 0.597562490122148
}
