package usecases.choicemodels

import domain.data.Person
import domain.enums.ActivityType
import domain.enums.StandardMode

interface Parameters<M, A> {

    operator fun get(m: M, a: A): Double
}

class ModeActivityParameters : Parameters<StandardMode, ActivityType> {

    val ass = 0.0
    override operator fun get(m: StandardMode, a: ActivityType): Double {
        return 0.0
    }
}

class ModePersonParameters : Parameters<StandardMode, Person> {
    override operator fun get(m: StandardMode, a: Person): Double {
        return 0.0
    }
}

// class Parameters {
//
//    val asc_fuss = "asc_fuss"
//    val asc_mf = "asc_mf"
//    val asc_moia = "asc_moia"
//    val asc_oev = "asc_oev"
//    val asc_pkw = "asc_pkw"
//    val asc_rad = "asc_rad"
//    val b_age_1_on_fuss = "b_age_1_on_fuss"
//    val b_age_1_on_mf = "b_age_1_on_mf"
//    val b_age_1_on_oev = "b_age_1_on_oev"
//    val b_age_1_on_rad = "b_age_1_on_rad"
//    val b_age_2_on_fuss = "b_age_2_on_fuss"
//    val b_age_2_on_mf = "b_age_2_on_mf"
//    val b_age_2_on_oev = "b_age_2_on_oev"
//    val b_age_2_on_rad = "b_age_2_on_rad"
//    val b_age_34_on_fuss = "b_age_34_on_fuss"
//    val b_age_34_on_moia = "b_age_34_on_moia"
//    val b_age_34_on_oev = "b_age_34_on_oev"
//    val b_age_34_on_rad = "b_age_34_on_rad"
//    val b_age_56_on_moia = "b_age_56_on_moia"
//    val b_age_78_on_moia = "b_age_78_on_moia"
//    val b_arb_on_moia = "b_arb_on_moia"
//    val b_arb_on_oev = "b_arb_on_oev"
//    val b_arb_on_rad = "b_arb_on_rad"
//    val b_arb_on_tt_fuss = "b_arb_on_tt_fuss"
//    val b_arb_on_tt_mf = "b_arb_on_tt_mf"
//    val b_arb_on_tt_oev = "b_arb_on_tt_oev"
//    val b_arb_on_tt_pkw = "b_arb_on_tt_pkw"
//    val b_arb_on_tt_rad = "b_arb_on_tt_rad"
//    val b_cost = "b_cost"
//    val b_cost_pkw = "b_cost_pkw"
//    val b_dienst_on_cost_moia = "b_dienst_on_cost_moia"
//    val b_dienst_on_moia = "b_dienst_on_moia"
//    val b_dienst_on_oev = "b_dienst_on_oev"
//    val b_dienst_on_rad = "b_dienst_on_rad"
//    val b_dienst_on_tt_fuss = "b_dienst_on_tt_fuss"
//    val b_dienst_on_tt_mf = "b_dienst_on_tt_mf"
//    val b_dienst_on_tt_moia = "b_dienst_on_tt_moia"
//    val b_dienst_on_tt_oev = "b_dienst_on_tt_oev"
//    val b_dienst_on_tt_rad = "b_dienst_on_tt_rad"
//    val b_frau_on_mf = "b_frau_on_mf"
//    val b_frau_on_oev = "b_frau_on_oev"
//    val b_frau_on_pkw = "b_frau_on_pkw"
//    val b_freizeit_on_fuss = "b_freizeit_on_fuss"
//    val b_freizeit_on_mf = "b_freizeit_on_mf"
//    val b_freizeit_on_moia = "b_freizeit_on_moia"
//    val b_freizeit_on_oev = "b_freizeit_on_oev"
//    val b_freizeit_on_rad = "b_freizeit_on_rad"
//    val b_freizeit_on_tt_fuss = "b_freizeit_on_tt_fuss"
//    val b_freizeit_on_tt_mf = "b_freizeit_on_tt_mf"
//    val b_freizeit_on_tt_pkw = "b_freizeit_on_tt_pkw"
//    val b_fs_on_mf = "b_fs_on_mf"
//    val b_fs_on_moia = "b_fs_on_moia"
//    val b_fs_on_oev = "b_fs_on_oev"
//    val b_konto_moia_on_moia = "b_konto_moia_on_moia"
//    val b_logsum_ab_oev = "b_logsum_ab_oev"
//    val b_logsum_zu_oev = "b_logsum_zu_oev"
//    val b_mode_before_oev = "b_mode_before_oev"
//    val b_mode_before_pkw = "b_mode_before_pkw"
//    val b_mode_before_rad = "b_mode_before_rad"
//    val b_nutzer_123_moia_on_cost_moia = "b_nutzer_123_moia_on_cost_moia"
//    val b_nutzer_123_moia_on_moia = "b_nutzer_123_moia_on_moia"
//    val b_oekstat5_on_cost = "b_oekstat5_on_cost"
//    val b_oekstat_on_moia = "b_oekstat_on_moia"
//    val b_oekstat_on_oev = "b_oekstat_on_oev"
//    val b_oekstat_on_pkw = "b_oekstat_on_pkw"
//    val b_parkdruck_on_oev = "b_parkdruck_on_oev"
//    val b_pkwimHH_on_fuss = "b_pkwimHH_on_fuss"
//    val b_pkwimHH_on_mf = "b_pkwimHH_on_mf"
//    val b_pkwimHH_on_moia = "b_pkwimHH_on_moia"
//    val b_pkwimHH_on_oev = "b_pkwimHH_on_oev"
//    val b_pkwimHH_on_rad = "b_pkwimHH_on_rad"
//    val b_service_on_fuss = "b_service_on_fuss"
//    val b_service_on_mf = "b_service_on_mf"
//    val b_service_on_moia = "b_service_on_moia"
//    val b_service_on_oev = "b_service_on_oev"
//    val b_service_on_rad = "b_service_on_rad"
//    val b_shopping_on_fuss = "b_shopping_on_fuss"
//    val b_shopping_on_oev = "b_shopping_on_oev"
//    val b_shopping_on_pkw = "b_shopping_on_pkw"
//    val b_shopping_on_rad = "b_shopping_on_rad"
//    val b_tt_fuss = "b_tt_fuss"
//    val b_tt_mf = "b_tt_mf"
//    val b_tt_moia = "b_tt_moia"
//    val b_tt_oev = "b_tt_oev"
//    val b_tt_pkw = "b_tt_pkw"
//    val b_tt_rad = "b_tt_rad"
//    val b_wt_moia = "b_wt_moia"
//    val b_zeitkarte_on_mf = "b_zeitkarte_on_mf"
//    val b_zeitkarte_on_oev = "b_zeitkarte_on_oev"
//    val b_zeitkarte_on_pkw = "b_zeitkarte_on_pkw"
//    val b_zeitkarte_on_rad = "b_zeitkarte_on_rad"
//    val b_zeitkarte_on_tt_oev = "b_zeitkarte_on_tt_oev"
//    val b_zuab_moia = "b_zuab_moia"
//    val b_zuab_pkw = "b_zuab_pkw"
//    val elast_parkdruck_on_oev = "elast_parkdruck_on_oev"
// }
