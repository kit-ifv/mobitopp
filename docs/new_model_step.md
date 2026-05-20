# Core ModelStep Documentation

This document describes the core architecture and functionality of the `core.modelsteps` package, which provides the foundation for defining and executing simulations in a structured, verifiable, and reportable way.

## Abstract Idea
The core idea behind `ModelStep` is to decompose a complex simulation into discrete, sequential units of work. Each unit, or "step," is responsible for a specific task (e.g., loading data, transforming entities, or running a simulation module). 

Key features of this architecture:
- **Sequential Execution**: Steps are run one after another.
- **Two-Phase Operation**: Every simulation runs first in **Validation** mode and then in **Execution** mode.
- **Context-Aware**: Steps run within a `Context` that provides access to data, configuration, and reporting services.
- **Reporting**: Automatic collection of logs, successes, warnings, and errors for each step.

---

## The `Simulation` Class
The `Simulation` class is the entry point for running a sequence of model steps. It manages the lifecycle of the simulation, including the transition from validation to execution.

### Step Scope
The `steps` method in `Simulation` defines the scope where model steps are declared. It uses Kotlin's context receivers to provide both the simulation `Context` and the `Config`.

```kotlin
fun steps(lambda: context(CFG) C.() -> Unit)
```

- **C**: The Context type (must implement `Context` and `Cloneable`).
- **CFG**: The Configuration type (must implement `Config`).

---

## Two-Phase Execution
To ensure robustness, simulations follow a strict two-phase process:

1. **Validation Phase**:
   - The simulation runs through all steps in `Validation` mode.
   - Time-consuming logic is skipped.
   - "Obvious errors" (e.g., missing files, invalid parameters, unsealed repositories) are detected early.
   - If any **Error** is reported during this phase, the simulation terminates before the execution phase begins.

2. **Execution Phase**:
   - If validation passes, the simulation resets the context and runs through all steps in `Execution` mode.
   - The actual simulation logic is performed.
   - Results and performance metrics are collected.

---

## Error and Warning Report Mechanism
Reporting is integrated into the `Context`. Each step automatically logs its status to a `ReportBuilder`.

### Log Levels
- **Success**: Indicates the step completed as expected.
- **Normal**: Informational messages.
- **Warning**: Indicates potential issues that don't stop the simulation (e.g., performance concerns or non-critical data inconsistencies).
- **Error**: Critical failures. In validation, errors prevent execution. In execution, they indicate a failed run.

### Validation Utilities
Several utility functions in `core.modelsteps.validation` help in reporting:
- `validateCondition`: Checks a predicate and logs a message if it fails.
- `validateNoException`: Catches exceptions and logs them as errors or warnings.
- `validateFileReadAccess` / `validateFileWriteAccess`: Specifically for checking filesystem permissions.

---

## Core Model Step Functions
The basic building blocks for defining steps are:

### `modelStep`
The most fundamental function. It takes a name, a list of validation checks, and an execution block.
In validation mode, it executes the given set of validation checks. In execute mode, the block is executed.
Internally it takes care of detecting errors/warnings during validation and creates overview report cards.
Every custom/convenience model step function should internally wrap this function!
```kotlin
fun <C : Context> C.modelStep(name: String, validation: Validation<C>, execution: C.() -> Unit)
```

### `repositoryDependentStep`
A specialized version of `modelStep` that ensures specific repositories are "sealed" (immutable) before the step runs. This prevents accidental data corruption by ensuring dependencies are finalized.

---

## Convenience Model Step Wrappers
The `core.modelsteps.scopes` package provides several convenience wrappers that operate within a `MutableRepository` context. These wrappers simplify common tasks:

| Function            | Scope      | Description                                                                                             |
|---------------------|------------|---------------------------------------------------------------------------------------------------------|
| `mutatingStep`      | -          | Checks that a repository is not sealed.                                                                 |
| `addResourceStep`   | Collection | Adds entities from a `Resource` to the repository.                                                      |
| `addCsvResourceStep` | Collection | Specialized version of `addResourceStep` for `CsvResource`.                                             |
| `loadCsvStep`       | Collection | Specialized version of `addCsvResourceStep` building the `CsvResource` from path, parser and delimiter. |
| `updateEachStep`    | Element    | Applies an update function to every element in the repository.                                          |
| `updateBulkStep`    | Collection | Operates on the entire collection of elements at once.                                                  |
| `transformEachStep` | Element    | Replaces or removes (if null is returned) elements individually.                                        |
| `transformBulkStep` | Collection | Replaces elements based on the entire collection state.                                                 |
| `filterStep`        | Collection | Removes elements that do not match a predicate.                                                         |
| `forEachStep`       | Element    | Read-only iteration over elements.                                                                      |
| `forAllStep`        | Collection | Read-only operation on the entire collection.                                                           |

These wrappers automatically handle the `modelStep` boilerplate and integration with the repository context.


## Mutability Scopes
To simplify operations on specific repositories, the `mutableRepositoryScope` provides a dedicated context for a `MutableRepository`.

```kotlin
context(_: CFG)
fun <CTXT : Context, CFG, E : Identifiable<I>, I> CTXT.mutableRepositoryScope(
    getter: CTXT.() -> MutableRepository<E, I>,
    sealed: Boolean = false,
    scope: context(MutableRepository<E, I>, CFG) CTXT.() -> Unit
)
```
- **Sealing**: If `sealed = true`, the repository is automatically sealed after the scope finishes, ensuring no further modifications can be made.

---

## Example: Household Setup

This example demonstrates how to use mutability scopes and convenience wrappers to set up and manipulate household entities in a simulation.

```kotlin
val householdSetup = modelStep("Setup Households") {
    // Enter a mutability scope for households. 
    // 'sealed = true' ensures the repository is immutable after this block.
    households(sealed = true) {
        // Load households from a CSV file defined in the configuration.
        loadHouseholds(householdCsv())

        // Use filterStep to keep only households with a positive income.
        filterStep("Filter positive income") { h ->
            h.incomePerMonth > 0.euros
        }

        // Use updateEachStep to set a fixed income for all households.
        updateEachStep("Set base income") { h ->
            h.incomePerMonth = 1500.euros
        }

        // Use transformEachStep to apply a small bonus to every household's income.
        // It returns a new household instance replacing the previous respective repository entry.
        transformEachStep("Apply income bonus") { h ->
            h.copy(incomePerMonth = h.incomePerMonth + 1.euro)
        }

        // Use updateBulkStep to set all incomes to the mean income of the current population.
        updateBulkStep("Normalize incomes") { households ->
            val meanIncome = households.map { it.incomePerMonth.toDouble(CurrencyUnit.EUROS) }.average().euros
            households.forEach { it.incomePerMonth = meanIncome }
        }

        // Print the number of loaded households for verification.
        forAllStep("Log household count") { households ->
            println("Final count: ${households.size} households.")
        }
    }
}
```

In this example:
1.  `households(sealed = true) { ... }` opens a `MutableRepository` context for `MutableHousehold` entities.
2.  `loadHouseholds`, `filterStep`, `updateEachStep`, `transformEachStep`, and `updateBulkStep` are convenience wrappers that internally use `modelStep`.
3.  `transformEachStep` creates copies of the household objects from the repository, assuming `MutableHousehold` is a data class.
4.  The `incomePerMonth` is updated using both individual and bulk operations, demonstrating how mutability is handled within the scope.
5.  Once the `households` block completes, the household repository is "sealed," meaning any subsequent `repositoryDependentStep` that depends on it can safely assume the data will not change.

---