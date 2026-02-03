package org.gridsuite.actions;

import org.gridsuite.filter.AbstractFilter;
import org.gridsuite.filter.FilterLoader;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DefaultFilterLoader implements FilterLoader {

    private final FilterProviderI filterProvider;

    public DefaultFilterLoader(FilterProviderI filterProvider) {
        this.filterProvider = filterProvider;
    }

    @Override
    public List<AbstractFilter> getFilters(List<UUID> uuids) {
        return filterProvider.getFilters(uuids);
    }

    @Override
    public Optional<AbstractFilter> getFilter(UUID uuid) {
        return filterProvider.getFilters(List.of(uuid))
            .stream()
            .findFirst();
    }
}
