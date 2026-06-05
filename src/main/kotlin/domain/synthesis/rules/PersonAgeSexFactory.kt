package domain.synthesis.rules

import domain.shared.enums.person.Sex
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.rules.measurements.MutablePersonAgeSexDefinition
import domain.synthesis.rules.measurements.PersonAgeSexDefinition
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.FullCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

/**
 * Base factory for creating age-and-sex based person rules from project-specific input data.
 *
 * Implementations provide [definitionDecoder], which translates an input value of type [Input] into
 * pairs of [PersonAgeSexDefinition] and target values. Each definition describes the age range and
 * sex a rule applies to, while the associated [Number] is used as the target value for the generated
 * rule.
 *
 * [buildRuleSet] performs additional normalization and validation before creating the resulting
 * [CoverageGroup]
 * - age intervals are checked for empty ranges,
 * - age intervals are checked for holes and overlaps within each sex,
 * - the lowest age interval for each sex is extended down to `0`,
 * - the highest age interval for each sex is extended up to [Int.MAX_VALUE].
 *
 * This ensures that generated age-and-sex rules cover the complete supported age range for each sex
 * represented in the decoded definitions.
 *
 * Use [buildDirect] when the decoded definitions should be converted directly into a [RuleSet]
 * without validation, interval consistency checks, or automatic range extension.
 *
 * @param Input the project-specific input type from which rule definitions are decoded.
 * @property definitionDecoder converts an [Input] value into age-and-sex measurement definitions
 * together with their target values.
 */
abstract class PersonAgeSexFactory<Input>(
    val definitionDecoder: (Input) -> List<Pair<PersonAgeSexDefinition, Number>>,
) {

    fun buildRuleSet(input: Input): CoverageGroup<ISurveyHousehold<*, *>> {
        val requestedRules = definitionDecoder(input).map { it.first.toMutableDefinition() to it.second }
        val ageIntervals = requestedRules.map { it.first }
        val maleAgeIntervals = ageIntervals.filter { it.acceptedSex == Sex.MALE }.map { it.acceptedAgeRange }
        require(maleAgeIntervals.none { it.isEmpty() }) {
            "Cannot operate on empty ranges but at least one range is empty: $maleAgeIntervals"
        }
        require(testIntervalConsistency(maleAgeIntervals)) {
            "There is an overlap in age interval creation: This is problematic because a person could now contribute " +
                "to multiple measurements"
        }

        makeValidDefinitions(ageIntervals, Sex.MALE)
        makeValidDefinitions(ageIntervals, Sex.FEMALE)

        return FullCoverageGroup(requestedRules.map { it.first.makeRule(it.second) }.toRuleSet())
    }

    /**
     * No protection, no guarantee that you get a coverage group, just a straight translation to a rule set
     * from the definitions as read bu definition decoder, no check of interval bounds.
     */
    fun buildDirect(input: Input): RuleSet<ISurveyHousehold<*, *>> = definitionDecoder(input).map {
        it.first.makeRule(it.second)
    }.toRuleSet()

    /**
     * We only demand the input to be converted to a proper PersonAgeSexDefinition, but we nowhere demand that
     * the ranges are contiguous and cover the range from 0 to infinity.
     * This has caused problems in the past, where the rule definitions ended at age 100 and a person that was aged
     * 101.
     *
     * So we make sure that A) The intervals dont have holes and overlaps. and B) that the lowest interval extrudes to
     * 0 and the highest interval extrudes to infinity.
     *
     *
     * Operates via side effect to change the intervals, so that the mapping between definition and target is maintained.
     * Separated by sex because these two are independent.
     */
    private fun makeValidDefinitions(intervals: Collection<MutablePersonAgeSexDefinition>, targetSex: Sex) {
        val ordered = intervals
            .filter { it.acceptedSex == targetSex }
            .sortedBy { it.acceptedAgeRange.start }

        if (ordered.isEmpty()) {
            return
        }
        val ranges: List<IntRange> = ordered.map { it.acceptedAgeRange }
        require(testIntervalConsistency(ranges)) {
            "There is an inconsistency in the ranges, either a hole or an overlap: $ranges"
        }

        ordered.first().acceptedAgeRange = ordered.first().acceptedAgeRange.extrude(0, null)
        ordered.last().acceptedAgeRange = ordered.last().acceptedAgeRange.extrude(null, Int.MAX_VALUE)
    }

    private fun IntRange.extrude(lowerbound: Int?, upperbound: Int?): IntRange {
        val extrudedStart = (lowerbound?.let { start.coerceAtMost(it) } ?: start)
        val extrudedEnd = (upperbound?.let { endInclusive.coerceAtLeast(it) } ?: endInclusive)
        return (extrudedStart..extrudedEnd)
    }

    private fun testIntervalConsistency(ordered: Collection<IntRange>): Boolean =
        ordered.zipWithNext().all { (first, second) ->
            first.endInclusive + 1 == second.start
        }
}
