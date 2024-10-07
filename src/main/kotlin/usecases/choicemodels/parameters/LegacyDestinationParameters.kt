package usecases.choicemodels.parameters

import usecases.choicemodels.IDestinationParameters

@Suppress("MagicNumber")
abstract class LegacyDestinationParameters : IDestinationParameters {
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

    override val b_zuab_pkw = -0.07635381474178
    override val b_zuab_oev = -0.0264056325589302

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

    // Apparently these parameters are not used
//    override val asc_bs = -2.74748353864304
//    override val asc_moia = -0.37705709382932
//    override val asc_cs_ff = -2.46414662819837
//    override val asc_cs_sb = -3.85787734173363
//    override val asc_escooter = -2.61071425609589
//    override val asc_taxi = -4.21831953675007
//    override val b_tt_bs = -0.127727726463221
//    override val b_tt_escooter = -0.124879896367696
//    override val b_tt_moia = -0.0739562799984561
//    override val b_zuab_cs_ff = -0.0998733766734251
//    override val b_zu_bs = -0.071827380024202
//    override val b_zu_es = -0.319085328690639
//    override val b_zuab_moia = -0.202978247661008
//    override val b_wt_moia = -0.144917240562579
//    override val b_cost_taxi = -0.108195743922512
//    override val b_cost_bs_escooter = -0.130093425537354
//    override val b_cost_cs = -0.163102933626618
//    override val b_cost_moia = -0.105365775670613
//    override val b_nutzer_moia = 1.04304109349849
//    override val b_home_on_fuss = 1.55831009663069
//    override val b_home_on_rad = 1.29825292653342
//    override val b_home_on_pkw = 1.00741039269339
//    override val b_home_on_oev = 1.50609681106275
//    override val b_arb_on_bs = 0.955115013067832
//    override val b_dienst_on_bs = -0.726993715307142
//    override val b_freizeit_on_bs = 0.183085627372588
//    override val b_arb_on_taxi = 0.595912615053922
//    override val b_dienst_on_taxi = 3.20705739471168
//    override val b_service_on_taxi = 1.84303354885933
//    override val b_freizeit_on_taxi = 1.15768641383172
//    override val b_home_on_taxi = 2.90369389142055
//    override val b_arb_on_cs_ff = -0.206950523845975
//    override val b_dienst_on_cs_ff = -1.03601319882931
//    override val b_service_on_cs_ff = -0.161620475279169
//    override val b_freizeit_on_cs_ff = -0.653204676851787
//    override val b_arb_on_escooter = 1.01755198945501
//    override val b_dienst_on_escooter = -0.0862734391017355
//    override val b_service_on_escooter = 0
//    override val b_freizeit_on_escooter = 0.506305350701758
}
