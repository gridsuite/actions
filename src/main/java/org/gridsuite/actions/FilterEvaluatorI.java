package org.gridsuite.actions;

import org.gridsuite.filter.AbstractFilter;

import java.util.List;
import java.util.UUID;

public interface FilterEvaluatorI {
    List<AbstractFilter> getFilters(List<UUID> filtersUuids);
}
