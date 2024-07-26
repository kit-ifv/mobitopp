package usecases.choicemodels.parameters

@Suppress("MagicNumber")
object LeisureParameters : DestinationParameters() {

    override val b_logsum_pt_active = 0.37913097643898 - 0.1
    override val b_logsum_drive = 0.101571780926651 - 0.05
    override val b_logsum_pt_active_fix = 0.0223919993684166

    override val b_logsum_drive_fix = 0.321408622664327
    override val b_attr = 0.164790334596064 - 0.1

    override val b_parken = -0.000805225283993312

    override val elasticity_parken = 2.03231417905929

    override val b_0_1 = 4.01726106476279
    override val b_1_2 = -1.2807770160566 + 0.4

    override val shift_b_0_1_on_attr = -0.391440604191497
    override val shift_b_1_2_on_attr = 0.165687668821311
    override val shift_age_2_on_logsum_pt_active = 0.0
    override val shift_age_2_on_logsum_pt_active_fix = 0.0
    override val shift_age_2_on_logsum_drive = 0.0
    override val shift_age_2_on_logsum_drive_fix = 0.0
    override val shift_age_2_on_attr = 0.0
    override val shift_age_3_on_logsum_pt_active = 0.0
    override val shift_age_3_on_logsum_pt_active_fix = 0.0
    override val shift_age_3_on_logsum_drive = 0.0
    override val shift_age_3_on_logsum_drive_fix = 0.0
    override val shift_age_3_on_attr = 0.0
    override val shift_age_4_on_logsum_drive = 0.0
    override val shift_age_4_on_logsum_drive_fix = 0.0
    override val shift_age_4_on_logsum_pt_active = 0.0
    override val shift_age_4_on_logsum_pt_active_fix = 0.0
    override val shift_age_4_on_attr = 0.0
    override val shift_age_56_on_logsum_pt_active = 0.0
    override val shift_age_56_on_logsum_pt_active_fix = 0.0
    override val shift_age_56_on_logsum_drive = 0.0
    override val shift_age_56_on_logsum_drive_fix = 0.0
    override val shift_age_56_on_attr = 0.0
    override val shift_age_78_on_logsum_pt_active = 0.0
    override val shift_age_78_on_logsum_pt_active_fix = 0.0
    override val shift_age_78_on_logsum_drive = 0.0
    override val shift_age_78_on_logsum_drive_fix = 0.0
    override val shift_age_78_on_attr = 0 + 0.1
    override val shift_arb_on_logsum_pt_active = 0.0
    override val shift_arb_on_logsum_pt_active_fix = 0.0
    override val shift_arb_on_logsum_drive = 0.0
    override val shift_arb_on_logsum_drive_fix = 0.0
    override val shift_arb_on_attr = 0 + 0.1
    override val shift_educ_on_logsum_pt_active = 0.0
    override val shift_educ_on_logsum_pt_active_fix = 0.0
    override val shift_educ_on_logsum_drive = 0.0
    override val shift_educ_on_logsum_drive_fix = 0.0
    override val shift_educ_on_attr = 0 - 0.05
    override val shift_high_inc_on_logsum_pt_active = 0.0
    override val shift_high_inc_on_logsum_pt_active_fix = 0.0
    override val shift_high_inc_on_logsum_drive = 0.0
    override val shift_high_inc_on_logsum_drive_fix = 0.0
    override val shift_high_inc_on_attr = 0.0
    override val shift_zk_on_logsum_pt_active = 0.0
    override val shift_zk_on_logsum_pt_active_fix = 0.0
    override val shift_zk_on_logsum_drive = 0.0
    override val shift_zk_on_logsum_drive_fix = 0.0
    override val shift_zk_on_attr = 0.0
    override val shift_carav_on_logsum_pt_active = 0.0
    override val shift_carav_on_logsum_pt_active_fix = 0.0
    override val shift_carav_on_logsum_drive = 0.0
    override val shift_carav_on_logsum_drive_fix = 0.0
    override val shift_carav_on_attr = 0.1
    override val shift_uml_on_logsum_pt_active = 0.0640383675977011 + 0.1
    override val shift_uml_on_logsum_pt_active_fix = 0.0416507340946399 + 0.1
    override val shift_uml_on_logsum_drive = 0.0462160663655077 + 0.1
    override val shift_uml_on_logsum_drive_fix = -0.0755006569431623 + 0.1
    override val shift_uml_on_attr = 0.610291409419243 - 0.2
    override val max_attractivity = 20000.0
}
