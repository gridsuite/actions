/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.actions;

import org.gridsuite.filter.AbstractFilter;
import org.gridsuite.filter.FilterLoader;

import java.util.List;
import java.util.UUID;

/**
 * @author Kevin Le Saulnier <kevin.le-saulnier at rte-france.com>
 */
public class DefaultFilterLoader implements FilterLoader {

    private final FilterProvider filterProvider;

    public DefaultFilterLoader(FilterProvider filterProvider) {
        this.filterProvider = filterProvider;
    }

    @Override
    public List<AbstractFilter> getFilters(List<UUID> uuids) {
        return filterProvider.getFilters(uuids);
    }
}
