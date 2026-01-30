package domain.synthesis.behavior.householdgeneration

import domain.synthesis.behavior.MinimalistHousehold

class NewDistributor<RULES, AREA, H>(
    val initialDistribution: InitialSignatureDistributor = GreedyAmountDistro(),
    val ruleProvider: HierarchicalRuleProvider<AREA, H>,
    val config: NewAlgorithmConfig,

) {

    fun distribute(
        parentRuleset: List<Rule<H>>,
        targetAmounts: Collection<SignatureAmount>,
        subregions: Collection<AREA>,
    ): Map<AREA, List<SignatureAmount>> {
        val signatures = targetAmounts.map { it.signature }
        // We need to get the rules for each subregion to construct the expected values.
        // I need the agglomerated rule set here, say HHSize is defined on level 1 and Age on level 0, then I need all
        // age rules for proper distribution.
        val subregionRules = subregions.associateWith { ruleProvider.getConflictFreeRules(it) }
//        val subregionRules = subregions.associateWith { ruleProvider.getAllRules(it) }

//        val logics = subregionRules.values.flatMap { it.map { it.logic } }.distinct()
        val signatureTracker = SignatureTracker(signatures, signatures.maxOf {
            it.keys.max()
        } + 1)

        val partitions = subregionRules.entries.map { (region, rules) ->
            val areaRuleset = parentRuleset.associate { it.logic to (0 to false) }.toMutableMap()
            rules.forEach {
                areaRuleset[it.logic] = it.target to true
            }
            val (targets, temp) = areaRuleset.toList().unzip()
            val (expected, mask) = temp.unzip()
            Partition(expected.toIntArray(), signatureTracker, mask.toBooleanArray())
        }

        initialDistribution.distribute(partitions, targetAmounts)
        config.refinement.refine(partitions)

        return subregions.zip(partitions).associate { (region, partition) ->
            region to partition.output()
        }
    }

//    private fun verify(targetAmounts: Collection<SignatureAmount>, partitions: List<Partition>): Boolean {
//        val fulfilledConditions = targetAmounts.map { (sig, amount) ->
//            partitions.sumOf { it.count(sig) } == amount }
//        return fulfilledConditions.all { it }
//    }

//    fun toIPUOutput(
//        partitions: List<Partition>,
//        subregions: Collection<AREA>,
//        logics: List<NamedCountRule<ISurveyHousehold<out RULES>>>,
//    ): List<IPUOutputLog> {
//        val zipper = partitions.zip(subregions)
//        return zipper.cartesianProduct(logics.withIndex().toList()).map { (l, p) ->
//            val (partition, subregion) = l
//            val (idx, rule) = p
//            IPUOutputLog("$subregion ${rule.ruleDescription}", partition.getExpected(idx), partition.getActual(idx))
//        }
//    }
//
//    private fun verify(groupedHouseholds: Map<Signature, List<SurveyHousehold<out RULES>>>, partitions: List<Partition>) {
//        val target = groupedHouseholds.values.withIndex().map { (sig, hhs) ->
//            val actual = partitions.sumOf { it.amount(SignatureIndex(sig)) }
//            val expected = hhs.size
//
//            actual == expected
//        }
//
//        require(target.all { it }) {
//            "There is a mismatch between assigned sigs and target households"
//        }
//    }
}
