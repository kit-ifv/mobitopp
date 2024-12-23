package synthesis

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import synthesis.discreteChoice.TransitPassParameters

class TransitPassParametersTest {

    private val textdump = """asc_Ticket = -0.312173681653899
b_hhgro_2 = -0.0256945305058742 -0.3
b_hhgro_3 = 0.291424576249088  +0.3
b_hhgro_4 = 0.426000821405854 +  0.05 
b_hhgro_5 = 0.652434997336812  + 0.05
b_hhgro_6 = 1.1953115261173 +0.05
b_hhgro_7 = 2.24858460071818 +0.05
b_hhgro_8 = 3.23480976565317 +0.05
b_weibl = 0.0756731876254076 +0.2
b_alter1a = -3.25644379813947 
b_alter1b = -1.28889752460068 
b_alter3 = -0.506306055009702 -0.2
b_alter4 = -0.43068644042899 - 0.3
b_alter5 = -0.440916518808974 -0.3
b_alter6 = -0.412490615376118 -0.3
b_alter7 = -0.322763855797514 +0.2
b_alter8 = -0.170997032958524 +0.15
b_no_fspkw = 0.995492805381457 
b_pkwhh1 = -0.691970185373246
b_pkwhh2 = -1.27273744779308
b_pkwhh3 = -2.06584837619823
b_pkwhh4 = -1.96838830652269
b_schueler = 1.59413587911005 - 0.15
b_student = 2.19941421528667  - 0.3
b_home = -1.78872181887129 
b_parttime = -0.210495104059506
b_EINKO2 = 0.13386078964763
b_EINKO3 = 0.184411681953943
b_EINKO4 = 0.355024064024888 + 0.2
b_EINKO5 = 0.197233443164943 + 0.2
b_EINKO6 = 0.257279239343281 + 0.2
b_p05 = -0.291721944623581
b_p617 = -0.305570335406822"""
    @Test
    fun testLegacyParameterParse() {

        val parameters = TransitPassParameters.parse(textdump)
        println(parameters)
        assertEquals(parameters.age0to9, -3.25644379813947 )
    }
}