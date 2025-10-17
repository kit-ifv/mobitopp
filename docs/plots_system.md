# Plotting System: core.results.plots

This document explains the plotting subsystem centered around the `core.results.plots` package. It covers the concepts, the fluent DSL for building plots, the AddPlot step (`addPlot { }`), and shows end-to-end examples drawn from the REstatt project’s `Main.kt`.

## Table of Contents
- [Overview](#overview)
- [Core Concepts](#core-concepts)
  - [Entities, Grouping, Axes](#entities-grouping-axes)
  - [PlotData and Traces](#plotdata-and-traces)
  - [Aggregations and Summaries](#aggregations-and-summaries)
  - [Transformations (normalize, sort, fill)](#transformations-normalize-sort-fill)
  - [Rendering Backends and Layouts](#rendering-backends-and-layouts)
  - [Colors and Palettes](#colors-and-palettes)
- [The Plot DSL (PlotterBuilder.kt)](#the-plot-dsl-plotterbuilderkt)
  - [Start from data: forData(...)](#start-from-data-fordata)
  - [Group and pick attributes](#group-and-pick-attributes)
  - [Choose X and Y and aggregate](#choose-x-and-y-and-aggregate)
  - [Transform and compare](#transform-and-compare)
  - [Render as Histogram, Line, Box, Scatter](#render-as-histogram-line-box-scatter)
- [AddPlot Step: addPlot { }](#addplot-step-addplot--)
  - [Writing plots to the results directory](#writing-plots-to-the-results-directory)
  - [Using subdirectories](#using-subdirectories)
- [Comparison Data: How MIDComparison works](#comparison-data-how-midcomparison-works)
- [Examples from REstatt/Main.kt](#examples-from-restattmainkt)
  - [Distance distributions by mode (normalized)](#distance-distributions-by-mode-normalized)
  - [Relative distributions (not normalized)](#relative-distributions-not-normalized)
  - [Distance distributions by purpose](#distance-distributions-by-purpose)
  - [Time distributions](#time-distributions)
  - [Timelines as line plots](#timelines-as-line-plots)
- [Reference](#reference)
  - [Ordering](#ordering)
  - [Bins](#bins)
  - [Comparison against expected data (MID)](#comparison-against-expected-data-mid)

## Overview

The plotting system provides a typed, composable DSL to transform arbitrary domain data into publication-ready plots. It separates:
- Data selection and grouping
- Aggregation and transformation (normalize, sort, fill)
- Rendering (histogram, line, box, scatter)
- Output (file path and name)

The main entry points are found in `core.results.plots`:
- Plot building DSL: `forData`, `groupBy`, `plot*Of`, `over`, `count`, `aggregateBy`, transformations, and `as...Plot` renderers
- Layout and rendering: `core.results.plots.render.*`
- Data plumbing: `core.results.plots.data.*`

In application code, plots are scheduled and written via the `AddPlotStep` exposed as the extension function `addPlot { ... }`.

## Core Concepts

### Entities, Grouping, Axes
- Entity E: the base record you want to plot (e.g., a `PersonLeg` or a `MidLegRow`).
- Group G: category that forms separate traces/bars/lines (e.g., transport mode, trip purpose). If no grouping is desired, `noGrouping()` in applied implicitly by omitting the groupBy call in the plot definition.
- Axes X and Y: the plotted attributes. X is often a bucket/bin or time; Y is often a numeric value or a `Summary` for box plots.

The DSL begins with `forData { elements }` and then optionally `.groupBy { ... }`.

### PlotData and Traces
The type `PlotData<G, X, Y>` represents the prepared data to be plotted. Internally, it becomes a list of `Trace<G, X, Y>`, each containing a group key and a list of `(x,y)` points. Factories include:
- `AllValuesPlotData` for all values per (group, x)
- `AggregateValuesPlotData` for aggregated measures per (group, x)

### Aggregations and Summaries
You can select or compute Y via:
- `plot { y }` for raw values
- `plotMinOf`, `plotMaxOf`, `plotMedianOf`, `plotQuantileOf(q)`, `plotSumOf`, `plotMeanOf`
- `summarize { y }` to build a `Summary<Y>` with min/lower quartile/median/upper quartile/max (used for box plots)
- `count { x }` to count rows per X per group (Y = count)
- `aggregateBy(yAttr, Aggregation)` for custom aggregations

Then choose X with `.over { x }` when using `plot*`/`summarize`, or provide X directly in `count { x }`.

### Transformations (normalize, sort, fill)
Transform a `PlotDataTransformationBuilder` before rendering:
- `normalizeByGroup()` converts Y to shares within each group (sum of Y over X in a group becomes 1.0) and returns Y as Double.
- `normalizeByX()` converts Y to shares at each X across groups.
- `fillMissingXValues(default)` fills gaps for missing (group, X) with a default Y (e.g., 0 or 0.0).
- `sortGroups { Ordering }` and `sortX { Ordering }` sort groups and points.

### Rendering Backends and Layouts
Renderers consume the data and produce lets-plot/Kandy plots that are saved as PNGs:
- Histogram: `asHistogram { ... }`
  - Layout builder (`HistogramLayoutBuilder`) supports `name`, `xAxisLabel`, `stackAxisLabel`, `groupLabel`, `xLabel`, `coloring`.
- Line plot: `asLinePlot { ... }`
  - `LinePlotLayoutBuilder` supports `name`, `xAxisLabel`, `yAxisLabel`, `groupAxisLabel`, `groupLabel`, `coloring`.
- Box plot: `asBoxPlot { ... }` (requires Y = `Summary<Number>` via `summarize { ... }`).
- Scatter: `asScatterPlot { ... }` (X and Y numeric).

Each renderer constructs an internal DataFrame and writes a PNG with spaces in the name replaced by underscores. A project logo is overlaid in the top-right corner.

### Colors and Palettes
Found in `core.results.plots.ColorPallets.kt`:
- KIT color constants (e.g., `KIT_BLUE`, `KIT_GREEN`, ...)
- Utility mappers:
  - `modeStringColor(String)` maps common mode labels to distinct colors.
  - `randomColor()` picks from a seeded palette.
  - Hue/lightness/saturation scaling: `hueScale`, `lighterShades`, etc.
You can pass lambdas for `coloring = { g -> RGB }` in layout builders and build categorical color scales automatically.

## The Plot DSL (PlotterBuilder.kt)

The fluent DSL lives in `core.results.plots.PlotterBuilder.kt`. It stitches together data selection, grouping, attribute picking, aggregation, transformation, and rendering.

### Start from data: forData(...)
```
forData { elements }                 // lazy supplier evaluated at plot time
forData(iterable)                    // from Iterable
forData(resource: Resource<E>)       // from Resource
```

### Group and pick attributes
```
forData { elements }
  .groupBy { e: E -> groupKey }      // optional; default is noGrouping()
  .count { xAttr }                    // produce counts per X per group

// Or pick Y first, then X:
forData { elements }
  .groupBy { e: E -> groupKey }
  .plot { yAttr }                     // raw values
  .over { xAttr }
```

Available Y builders (instead of `plot { }`):
- `plotMinOf { y }`, `plotMaxOf { y }`, `plotMedianOf { y }`, `plotQuantileOf({ y }, q)`
- `plotSumOf { y: Number }`, `plotMeanOf { y: Number }`
- `summarize { y: Comparable }` to get `Summary<Y>`
- `aggregateBy(yAttr) { Aggregation }`

### Choose X and Y and aggregate
`over { x }` switches the axis to X and applies the aggregation (if any). For `count { x }`, X is provided right there and Y becomes the count.

### Transform and compare
After you have `PlotDataTransformationBuilder`, you can chain:
```
.fillMissingXValues(default)
.sortX { Ordering.Ascending() }
.sortGroups { Ordering.AscendingBy { key.hashCode() } }
.normalizeByGroup()    // sum_x y(group,x) = 1
.normalizeByX()        // sum_group y(group,x) = 1
```
To compare two datasets (original vs. expected), build a comparison via:
```
val base = ... // PlotDataTransformationBuilder<...>
val comp = ... // PlotDataTransformationBuilder<...>
base.compareTo { comp }     // returns PlotterBuilder with comparison data
```
In rendered plots, comparison series are styled differently (e.g., dashed lines, border stroke on bars) and key labels may be bracketed.

### Render as Histogram, Line, Box, Scatter
Finally, choose a renderer and configure layout:
```
.asHistogram {
  name = "travel distance by mode normalized"
  xAxisLabel = "travel distance [km]"
  stackAxisLabel = "mode"
  coloring = { g -> modeStringColor(g.toString().lowercase()) }
}

.asLinePlot {
  name = "timeline"
  xAxisLabel = "time"
  yAxisLabel = "trip count"
}

.asBoxPlot { name = "distribution by group" }
.asScatterPlot { name = "x vs y" }
```
Each `as...` function returns a `Plotter<G, X, Y>`, which `AddPlotStep` will execute and write.

## AddPlot Step: addPlot { }

The function `application.steps.results.addPlot(...)` integrates plotting into the step-based simulation pipeline.

Signature:
```kotlin
fun <C : Context> C.addPlot(
    subDir: String = "plots",
    scope: () -> Plotter<*, *, *>
)
```

Usage:
- Call `addPlot { ... }` within the `.steps { ... }` block of a `Simulation { ... }` pipeline.
- Inside the block, build and return a `Plotter` via the DSL and a renderer (e.g., `.asHistogram { ... }`).
- The step writes a PNG into `results/<subDir>/` (default `results/plots/`). The file name is derived from the layout `name` with spaces replaced by underscores. A mobiTopp logo is overlaid automatically.

### Writing plots to the results directory
Internally, `AddPlotStep` wraps the `Plotter` and calls `plot(resultDir)`. The renderer composes the DataFrame, creates the Kandy plot, saves it, and overlays the logo.

### Using subdirectories
You can route outputs to custom subdirectories by passing `subDir`:
```
addPlot("by_mode") { ... }
addPlot("by_purpose") { ... }
```
Results will be placed under `results/by_mode/` and `results/by_purpose/` respectively.

## Comparison Data: How MIDComparison works

Before looking at examples from REstatt/Main.kt, it is important to understand how comparison data is integrated in the plotting DSL. The repository contains helpers to compare simulated results with MID (Mobilität in Deutschland) aggregated reference data.

Key components live in `application.steps.results.MidComparisonData.kt`:
- `compareWithMid(csv)`: reads the MID CSV into a `Resource<Row>`.
- `MidLegRow`: a typed wrapper over a CSV `Row` exposing fields like `distance`, `duration`, `mode`, `purpose` and pre-parsed bins for distance/time/age. It also exposes weights absolute (`W_HOCH`) and relative (`W_GEW`).
- `AgentResultsContext.midComparisonPlotForLegs(...)`: entry point that builds a `MidComparisonLegPlotBuilder` tying simulation legs to MID data with the same grouping logic and binning.
- `MidComparisonLegPlotBuilder.overDistance()` / `overDuration()`: constructs aligned X bins for distance or duration and returns a `PlotterBuilder` with both the simulation data and the comparison (MID) data attached via `compareTo`. 

Concepts and flow:
1) Load and cache MID rows
   - `compareWithMid(midCsv).legs(purposes, modes)` maps CSV rows to `MidLegRow` using the project’s `ChoiceModelPurposes` and `ChoiceModelModes` to parse purpose/mode strings consistently with the simulation.
   - The helper caches parsed rows by absolute path, so repeated plots are fast.

2) Build simulation dataset
   - Start from `forData { context.personLegs }` and filter with `legFilter`.
   - Group by `legGroup(PersonLeg)` so groups match your legend (e.g., transport mode, simplified purpose, employment).

3) Build comparison dataset
   - Start from `forData { midLegs }` and group by `midGroup(MidLegRow)` so groups are comparable to the simulation’s grouping keys.

4) Choose the X axis and align bins
   - `overDistance()` forces evaluation of `MidLegRow.distanceBin` and collects the global `Bin<Double>` set derived from CSV labels (e.g., "0-1", "1-2", "5+"), then maps both datasets onto those same bins using `mapToBins`.
   - `overDuration()` analogous for duration using `Bin<Int>`.

5) Count/aggregate and normalize consistently
   - Simulation: `dataBuilder.count { xBin }` produces counts per (group, bin).
   - MID: `comparisonBuilder.plotSumOf { weight } .over { xBin }` sums weights per (group, bin).
   - If `normalize = true`: `normalizeByX()` is applied to both datasets, yielding stacked shares that sum to 1.0 per X bin. If `normalize = false`: `normalizeByGroup()` scales values per group across X.

6) Sort/fill and compare
   - `sortAndFill(default)` fills missing combinations with `0.0` and applies `Ordering.Ascending()` for X and a stable `Ordering.AscendingBy { ... }` for groups.
   - `compareTo { comp }` attaches the comparison dataset to the simulation dataset.

Rendering cues for comparisons:
- `HistogramRenderer`: MID series are outlined (border line color legend "is expected data").
- `LinePlotRenderer`: MID series use a dashed line (`LineType.DASHED`) with legend "is expected data".
- `DataFrameBuilder.combineCompAndXLabel` brackets keys of comparison series when combining labels, so legends differentiate clearly.

Convenience in REstatt/Main.kt:
- A helper extension `midPlotForLegs(...)` wires `midComparisonPlotForLegs(...)` with the right CSV path and ChoiceModel enums. It lets you specify:
  - `legFilter`: Predicate on `PersonLeg` to restrict simulation legs.
  - `legGroup`: How to group simulation legs.
  - `midGroup`: How to group MID rows (must match `legGroup`’s semantic).
  - `normalize`: `true`/`false` to switch normalization semantics.

Practical grouping examples:
- By mode: `legGroup = { it.leg.transportType }`, `midGroup = { it.mode }`
- By purpose: `legGroup = { it.purpose?.simplifyMID(restattChoiceModelPurposes) ?: REstattActivity.UNDEFINED }`, `midGroup = { it.activityType }`
- By socio-demographics: e.g., employment, car ownership, commuter ticket; adjust `legFilter` and `midGroup` accordingly if the MID CSV contains those fields.

With this background, the following section shows concrete snippets from REstatt/Main.kt.

## Examples from REstatt/Main.kt

Below are representative snippets adapted from `src/main/kotlin/Main.kt` in the REstatt project.

### Distance distributions by mode (normalized)
Counts simulated legs by distance bin and transport mode; compares against MID aggregated data; normalizes by X (distance) so bars stack to 1 per bin.
```kotlin
addPlot {
    midPlotForLegs(
        legFilter = { it.leg.transportType != MODEUNKOWN },
        legGroup = { it.leg.transportType },   // groups = modes of simulated legs
        midGroup = { it.mode },                // groups = modes of MID rows
    ).overDistance()                           // creates common distance bins
     .asHistogram {
        name = "travel distance by mode normalized"
        xAxisLabel = "travel distance [km]"
        stackAxisLabel = "mode"
        coloring = { modeStringColor(it.description) }
    }
}
```
Outputs: `results/plots/travel_distance_by_mode_normalized.png` and overlays MID comparison series.

### Relative distributions (not normalized)
Switch to `normalize = false` to display relative counts (shares by group across all X), useful for overall distributions.
```kotlin
addPlot {
    midPlotForLegs(
        legFilter = { it.leg.transportType != MODEUNKOWN },
        legGroup = { it.leg.transportType },
        midGroup = { it.mode },
        normalize = false
    ).overDistance().asHistogram {
        name = "relative travel distance distribution by mode"
        stackAxisLabel = "mode"
        xAxisLabel = "travel distance [km]"
        coloring = { modeStringColor(it.description) }
    }
}
```

### Distance distributions by purpose
Group by trip purpose (simulated vs. MID) and display normalized shares by distance bin:
```kotlin
addPlot {
    midPlotForLegs(
        legGroup = { it.purpose?.simplifyMID(restattChoiceModelPurposes) ?: REstattActivity.UNDEFINED },
        midGroup = { it.activityType }
    ).overDistance().asHistogram {
        name = "travel distance by purpose normalized"
        xAxisLabel = "travel distance [km]"
        stackAxisLabel = "trip purpose"
        coloring = { randomColor() }
    }
}
```

### Time distributions
Use `.overDuration()` to bin by travel time and render histograms:
```kotlin
addPlot {
    midPlotForLegs(
        legFilter = { it.leg.transportType != MODEUNKOWN },
        legGroup = { it.leg.transportType },
        midGroup = { it.mode },
    ).overDuration().asHistogram {
        name = "travel time by mode normalized"
        xAxisLabel = "duration [min]"
        stackAxisLabel = "mode"
        coloring = { modeStringColor(it.description) }
    }
}
```

### Timelines as line plots
Build a timeline of starts in 5-minute buckets, per mode and overall, then render as line plots:
```kotlin
addPlot {
    forData { personLegs }
        .groupBy { it.leg.transportType }
        .count { it.leg.startTime.roundToMultipleOf(5.minutes) }
        .sortX { Ordering.Ascending() }
        .asLinePlot {
            name = "timeline by mode"
            xAxisLabel = "time"
            yAxisLabel = "trip count"
            coloring = { modeStringColor(it.description) }
        }
}

addPlot {
    forData { personLegs }
        .count { it.leg.startTime.roundToMultipleOf(5.minutes) }
        .sortX { Ordering.Ascending() }
        .asLinePlot {
            name = "timeline"
            xAxisLabel = "time"
            yAxisLabel = "trip count"
        }
}
```

## Reference

### Ordering
`core.results.plots.data.Ordering` provides sort strategies for groups and X points, such as `Ascending()` and `AscendingBy { key -> ... }`.

### Bins
Duration and distance examples use generic `Bin` types (from `utils.collections`) created from MID category labels and reused to align simulated and comparison data.

### Comparison against expected data (MID)
`application.steps.results.MidComparisonData` provides helpers to compare simulation output against aggregated MID data. In REstatt, convenience functions like `midPlotForLegs { ... } .overDistance()` construct aligned bins, normalize consistently (by X or by group), and render with comparison styling (dashed/outlined) for the reference series.

---

Tips:
- Always set a descriptive `name` in layout builders; this defines the output filename and plot title.
- Use `coloring = { ... }` to map groups to readable colors. Prefer `modeStringColor` for transport modes, or build palettes with `hueScale`.
- When `normalize = true`, note the semantic: `.normalizeByX()` stacks to 1 per X across groups; `.normalizeByGroup()` scales per group across X.
