package org.gridsuite.actions;

import org.gridsuite.filter.AbstractFilter;

import java.util.List;
import java.util.UUID;

public interface FilterProviderI {
    List<AbstractFilter> getFilters(List<UUID> filtersUuids);
}
