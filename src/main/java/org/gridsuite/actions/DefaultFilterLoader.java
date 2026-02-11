package org.gridsuite.actions;

import org.gridsuite.filter.AbstractFilter;
import org.gridsuite.filter.FilterLoader;

import java.util.List;
import java.util.UUID;

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
