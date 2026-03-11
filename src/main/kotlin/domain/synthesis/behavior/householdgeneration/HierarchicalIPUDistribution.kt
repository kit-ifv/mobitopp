package domain.synthesis.behavior.householdgeneration

import domain.synthesis.Signature
import domain.synthesis.behavior.RawSurveyInfo

class HierarchicalIPUDistribution<AREA, H>(
    override val ruleProvider: HierarchicalRuleProviderDeprecated<AREA, H>,
    val config: NewAlgorithmConfig,
    val seedHouseholds: Collection<H>,
) : HierarchicalPopulationSynthesisDeprecated<AREA, H> {

    private val distributor: NewDistributor<RawSurveyInfo, AREA, H> = NewDistributor(
        ruleProvider = ruleProvider,
        config = config,
    )
    private val allRuleLogics = ruleProvider.getAllRuleLogics()
    private val householdMapping = initializeHouseholdMapping()

    private fun initializeHouseholdMapping(): Map<Signature, List<H>> {
        return seedHouseholds.groupBy {
            allRuleLogics.toSignatureNamed(it)
        }
    }

    override fun synthesize(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<H>> {
        val conflictAreas = targetAreas.filter { !ruleProvider.isFinal(it) }
            .filter { ruleProvider.getAllDescendants(it).any { it in targetAreas } }
        require(conflictAreas.isEmpty()) {
            "The target areas are dependent, this means that one of the targets is a descendant of another target." +
                "I don't know how to handle this case, because I use the target areas as an early exit condition " +
                "to stop unnecessary distribution once a target area is satisfied. Since the stop condition will " +
                "prevent any assignment to the descendants we cannot fulfill the request of generating a synthesis" +
                "for all targets. Thats why I decide to fail \n\n Broken Areas: \n $conflictAreas"
        }

        val initialSolution = initialSolution(highestArea)
        val output: MutableMap<AREA, List<H>> = mutableMapOf()
        val handledNonTargetNodes = mutableMapOf<AREA, List<SignatureAmount>>()
        if (highestArea !in targetAreas) {
            handledNonTargetNodes[highestArea] = initialSolution
        } else {
            output[highestArea] = finalize(initialSolution, householdMapping)
        }
        val rules = ruleProvider.getConflictFreeRules(highestArea)
        while (handledNonTargetNodes.isNotEmpty()) {
            val (area, signatureAmounts) = handledNonTargetNodes.entries.first()
            handledNonTargetNodes.remove(area)
            val distribution = distributeToChildren(rules, area, signatureAmounts)
            distribution.entries.forEach { (subArea, amounts) ->
                if (subArea !in targetAreas) {
                    handledNonTargetNodes[subArea] = amounts
                } else {
                    output[subArea] = finalize(amounts, householdMapping)
                }
            }
        }
        return output
    }

    private val collector = GenericCollector<SignatureAmount, H> {
        it.map { it.amount }
    }

    fun finalize(
        amounts: Collection<SignatureAmount>,
        signatures: Map<Signature, List<H>>,
    ): List<H> {
        val targetMap = amounts.associateWith {
            val targetHouseholds = signatures[it.signature] ?: run {
                error("No households for the signature ${it.signature}")
            }
            targetHouseholds
        }
        return collector.extract(targetMap).map { it }
    }

    /**
     * Generate a solution of the amount of households (read signatures for efficiency) which then can be distributed
     * According to whatever other strategy is present.
     */
    fun initialSolution(target: AREA): List<SignatureAmount> {
        val rules = ruleProvider.getConflictFreeRules(target)
        val oIpu = config.ipu.calculateSignature(seedHouseholds, rules)

        val integerIPUResult = standardRoundingStrategy.integerizeIPUOutput(oIpu)

        val sigs = integerIPUResult.map { (element, amount) ->
            SignatureAmount(element, amount)
        }

        return sigs
    }

    fun distributeToChildren(
        inheritedRules: List<Rule<H>>,
        target: AREA,
        calculatedAmounts: List<SignatureAmount>
    ): Map<AREA, List<SignatureAmount>> {
        // If no further subspecification can be done then we are donzo
        val subAreas = ruleProvider.getSubAreas(target)
        if (subAreas.isEmpty()) return mapOf(target to calculatedAmounts)
        if (subAreas.size == 1) return mapOf(subAreas.first() to calculatedAmounts)
        return distributor.distribute(
            inheritedRules,
//            ruleProvider.getConflictFreeRules(target),
            calculatedAmounts,
            subAreas
        )
    }
}
