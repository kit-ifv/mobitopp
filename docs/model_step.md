# ModelStep Overview

## Introduction
A `ModelStep` represents an operation performed during model execution.
Each `ModelStep` must have a name and provides methods to validate and execute the step. 
It can operate in different execution modes, including validation and execution.

### Key Responsibilities
- **Execution**: Performs an action on a repository.
- **Validation**: Ensures that the step meets the required conditions.
- **Modification or Reading**: Some steps mutate data, while others only read it.


## Core `ModelStep` Interfaces
The main `ModelStep` interfaces are:

1. **`ModelStep`**: The base interface for all steps.
2. **`RepositoryDependentStep`**: A step that depends on other repositories.
3. **`MutatingStep<E, I>`**: A step that modifies a mutable repository. (> RepositoryDependentStep)
4. **`SameValidationBehavior`**: A step that uses the same behavior for validation and execution.


## **Important Related Steps Table**

|                 | E (for each element) | Collection<E> (for all elements) |
|----------------|----------------------|---------------------------------|
| **Modify (returns nothing)**  | `UpdateEachStep`  | `UpdateAllStep`  |
| **Replace (returns modified copy)** | `TransformEachStep` | `TransformAllStep` |
| **Read-Only Steps** | `ForEachStep` | `ForAllStep` |

This table visually differentiates `ModelStep` subclasses that apply lambdas to elements 
based on their scope (single element vs. collection) and behavior (modifying, replacing, or reading elements).


## Subclasses of `ModelStep`
Below is an explanation of key subclasses categorized by their behavior:

### **Initialization/Data Loading ModelStep**

#### **1. `AddResourceStep<E, I>`** (> MutatingStep)
- Adds a `Resource` of elements to a mutable repository.
- Can mock behavior for validation.

#### **2. `AddCsvStep<E, I>`** (> AddResourceStep)
- A subclass of `AddResourceStep` that loads elements from a CSV file.
- Uses a CSV parser to extract data.

#### **3. `LoadCsvStep<E, I>`** (concrete implementation of AddCsvStep)
- Loads data from a CSV file into a mutable repository.
- Supports validation by mocking data elements.


### **Mutating Steps** (Modify Data)
These steps change the data within a repository by updating the state of elements but do not replace elements.

#### **4. `UpdateEachStep<E, I>`** (> MutatingStep, SameValidationBehavior)
- Updates individual elements in a repository.
- Applies an update function to each element separately.

#### **5. `UpdateAllStep<E, I>`** (> MutatingStep, SameValidationBehavior)
- Updates all elements in a repository collectively.
- The update function operates on the entire collection at once.


### **Mutating Steps** (Replace Data)
These steps replace existing elements with transformed versions.

#### **6. `TransformEachStep<E, I>`** (> MutatingStep, SameValidationBehavior)
- Transforms each element individually.
- Can replace elements or remove them by returning `null`.

#### **7. `TransformAllStep<E, I>`** (> MutatingStep, SameValidationBehavior)
- Transforms the entire collection of elements at once.
- Computes new elements from all current elements in the repository.


### **Read-Only Steps** (> MutatingStep, SameValidationBehavior)
These steps perform operations on elements but do not modify them.

#### **8. `ForEachStep<E, I>`** (> MutatingStep, SameValidationBehavior)
- Applies an action to each element separately.
- Used for non-mutating operations like logging or analysis.

#### **9. `ForAllStep<E, I>`** (> MutatingStep, SameValidationBehavior)
- Processes all elements in a repository collectively.
- Used for non-mutating operations that analyze all elements together.



### **Additional ModelSteps**

#### **10. `FilterStep<E, I>`** (> MutatingStep, SameValidationBehavior)
- Filters elements in a mutable repository based on a predicate.
- Removes elements that do not match the condition.

#### **11. `FilterIdsStep<E, I>`** (> MutatingStep, SameValidationBehavior)
- Similar to `FilterStep` but filters elements based on their IDs.
- Removes elements where the ID does not match the predicate.

#### **12. `SealStep<E, I>`** (> MutatingStep)
- Seals a repository, preventing any further modifications.
- Ensures the repository remains unchanged after execution.
