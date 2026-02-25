/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.actions.api;

import org.gridsuite.filter.AbstractFilter;

import java.util.List;
import java.util.UUID;

/**
 * Provides access to filter definitions referenced by actions features.
 * <p>
 * This interface is primarily used to retrieve the full filter model (polymorphic {@link AbstractFilter})
 * so that downstream components (e.g. contingency list evaluation) can resolve filter-based selections.
 * Implementations typically fetch filters from an external service (such as a filter server) or a repository.
 * </p>
 * <p>
 * Returned filters are expected to be consistent with the given UUIDs; missing filters should either be omitted
 * from the returned list or handled according to the implementation's error strategy.
 * </p>
 *
 * @author Kevin Le Saulnier <kevin.le-saulnier at rte-france.com>
 */
public interface FilterProvider {

    /**
     * Loads filters for the provided identifiers.
     *
     * @param filtersUuids identifiers of the filters to load
     * @return a list of loaded {@link AbstractFilter} instances (possibly empty, never {@code null})
     * @throws NullPointerException if {@code filtersUuids} is {@code null}
     */
    List<AbstractFilter> getFilters(List<UUID> filtersUuids);
}
