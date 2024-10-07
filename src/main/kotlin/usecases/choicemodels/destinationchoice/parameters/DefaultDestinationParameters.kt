package usecases.choicemodels.destinationchoice.parameters

@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
object DefaultDestinationParameters {
    const val asc_fuss = 3.76685346228208 - 1
    const val asc_rad = 1.86423253977476
    const val asc_pkw = 1.22529200766728
    const val asc_mf = -0.280608946005487
    const val asc_oev = 0 + 1.0
    const val b_tt_fuss = -0.143588796056184 + 0.03
    const val b_tt_rad = -0.152026975881995
    const val b_tt_pkw = -0.0382017895663777 - 0.02
    const val b_tt_mf_taxi = -0.0528979328553255
    const val b_tt_oev = -0.0479879518424728

    // TODO either implement access egress or kill these.
//    const val b_zuab_pkw = -0.07635381474178
//    const val b_zuab_oev = -0.0264056325589302
    const val b_cost_pkw = -0.116630151395857
    const val b_cost_oev = -0.667070030829148
    const val b_arb_on_fuss = -0.611295790763783
    const val b_dienst_on_fuss = -2.08288702898297
    const val b_service_on_fuss = -0.690263124475946
    const val b_freizeit_on_fuss = 0.267490527971426
    const val b_arb_on_rad = 0.866143069057071
    const val b_dienst_on_rad = -0.918456612362237
    const val b_service_on_rad = -1.19958587723503
    const val b_freizeit_on_rad = 0.0
    const val b_arb_on_mf = -1.02713467302909
    const val b_dienst_on_mf = -1.71859251059072
    const val b_service_on_mf = -0.452457466806963
    const val b_home_on_mf = 1.34227424091334
    const val b_arb_on_pkw = 0.0
    const val b_dienst_on_pkw = -1.28284371126842
    const val b_service_on_pkw = -0.0891560297531824
    const val b_freizeit_on_pkw = -1.00158195290812
    const val b_arb_on_oev = 0.814627986341692
    const val b_dienst_on_oev = -0.41496303954142
    const val b_service_on_oev = -0.103220410687913
    const val b_freizeit_on_oev = 0.0999365596556629
    const val b_park_oev = 0.426245178308876
    const val elasticity_park_oev = 0.597562490122148
}
