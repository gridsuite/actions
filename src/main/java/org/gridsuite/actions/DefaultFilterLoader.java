package org.gridsuite.actions;

import org.gridsuite.filter.AbstractFilter;
import org.gridsuite.filter.FilterLoader;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DefaultFilterLoader implements FilterLoader {

    private final FilterEvaluatorI filterEvaluator;

    public DefaultFilterLoader(FilterEvaluatorI filterEvaluator) {
        this.filterEvaluator = filterEvaluator;
    }

    @Override
    public List<AbstractFilter> getFilters(List<UUID> uuids) {
        return filterEvaluator.getFilters(uuids);
    }

    @Override
    public Optional<AbstractFilter> getFilter(UUID uuid) {
        return filterEvaluator.getFilters(List.of(uuid))
            .stream()
            .findFirst();
    }
}
