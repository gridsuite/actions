# actions

[![Actions Status](https://github.com/gridsuite/actions/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/gridsuite/actions/actions)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=org.gridsuite%3Aactions&metric=coverage)](https://sonarcloud.io/component_measures?id=org.gridsuite%3Aactions&metric=coverage)
[![MPL-2.0 License](https://img.shields.io/badge/license-MPL_2.0-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)

## Description

The **gridsuite-actions** library is the evaluation engine for contingency lists in the [GridSuite](https://github.com/gridsuite) platform.

It is a **framework-agnostic Java library** (no Spring dependency) that can be embedded in any service that needs to resolve contingency lists against a [PowSyBl](https://www.powsybl.org) network. It provides the following capabilities:

- **DTO model** for the two contingency list types (`IDENTIFIERS`, `FILTERS`), using Jackson polymorphic deserialization so both types are handled transparently by callers.
- **Evaluate contingency lists** against a PowSyBl `Network`, producing a unified `List<ContingencyInfos>` regardless of the list type.
- **Enrich evaluation results** beyond what raw PowSyBl provides: each `ContingencyInfos` carries `notFoundElements` (equipment IDs absent from the network) and `notConnectedElements` (equipment IDs present but currently disconnected).
- **`FilterProvider` interface**: a single-method SPI that decouples the library from any HTTP transport. Callers supply their own implementation to fetch filter definitions — typically a REST call to the [filter-server](https://github.com/gridsuite/filter-server).

---

## Contingency List Types

### Identifier-Based (`IDENTIFIERS`)

A named list of contingency groups where each group directly references a set of **equipment IDs** (strings). Evaluation delegates to PowSyBl's `IdentifierContingencyList`, which resolves those IDs against the network.

This is the most explicit type: the user enumerates exactly which equipment to trip in each contingency scenario.

### Filter-Based (`FILTERS`)

References one or more **filter UUIDs** from the [filter-server](https://github.com/gridsuite/filter-server). Each filter reference may optionally restrict which **equipment types** (e.g. `LINE`, `GENERATOR`, `BOUNDARY_LINE`) from the filter results are included.

This type is dynamic: the same contingency list yields different contingencies depending on the network being studied. Filter definitions are fetched at evaluation time via the `FilterProvider`.

---


## Useful Links

- [PowSyBl contingency API](https://github.com/powsybl/powsybl-core/tree/main/contingency/contingency-api)
