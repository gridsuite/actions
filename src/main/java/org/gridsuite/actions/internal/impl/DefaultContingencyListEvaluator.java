package org.gridsuite.actions.internal.impl;

import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.contingency.list.ContingencyList;
import com.powsybl.iidm.network.Connectable;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Terminal;
import org.gridsuite.actions.api.ContingencyListEvaluator;
import org.gridsuite.actions.api.dto.ContingencyListType;
import org.gridsuite.actions.api.dto.contingency.FilterBasedContingencyList;
import org.gridsuite.actions.api.dto.contingency.PersistentContingencyList;
import org.gridsuite.actions.api.dto.evaluation.ContingencyInfos;
import org.gridsuite.actions.internal.utils.ContingencyListUtils;
import org.gridsuite.filter.api.FilterEvaluator;
import org.gridsuite.filter.api.dto.FiltersWithEquipmentTypes;
import org.gridsuite.filter.identifierlistfilter.IdentifiableAttributes;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultContingencyListEvaluator implements ContingencyListEvaluator {

    private final FilterEvaluator filterEvaluator;

    public DefaultContingencyListEvaluator(FilterEvaluator filterEvaluator) {
        this.filterEvaluator = filterEvaluator;
    }

    public List<ContingencyInfos> evaluateContingencyList(PersistentContingencyList persistentContingencyList, Network network) {
        List<Contingency> contingencies = getPowsyblContingencies(persistentContingencyList, network);
        Map<String, Set<String>> notFoundElements = persistentContingencyList.getNotFoundElements(network);

        // For a gridsuite contingency with all equipments not found the powsybl contingency is not created
        List<ContingencyInfos> contingencyInfos = new ArrayList<>();
        notFoundElements.entrySet().stream()
            .filter(stringSetEntry -> contingencies.stream().noneMatch(c -> c.getId().equals(stringSetEntry.getKey())))
            .map(stringSetEntry -> new ContingencyInfos(stringSetEntry.getKey(), null, stringSetEntry.getValue(), null))
            .forEach(contingencyInfos::add);

        contingencies.stream()
            .map(contingency -> new ContingencyInfos(contingency.getId(), contingency, notFoundElements.get(contingency.getId()), getDisconnectedElements(contingency, network)))
            .forEach(contingencyInfos::add);

        return contingencyInfos;
    }

    private List<Contingency> getPowsyblContingencies(PersistentContingencyList contingencyList, Network network) {
        ContingencyList powsyblContingencyList;
        if (Objects.requireNonNull(contingencyList.getMetadata().getType()) == ContingencyListType.FILTERS) {
            FilterBasedContingencyList filterBasedContingencyList = (FilterBasedContingencyList) contingencyList;
            FiltersWithEquipmentTypes filtersWithEquipmentTypes = filterBasedContingencyList.toFiltersWithEquipmentTypes();
            List<IdentifiableAttributes> identifiers = evaluateFiltersNetwork(filtersWithEquipmentTypes, network);
            powsyblContingencyList = ContingencyList.of(identifiers.stream()
                .map(id ->
                    new Contingency(id.getId(), List.of(ContingencyListUtils.toContingencyElement(id))))
                .toArray(Contingency[]::new)
            );
        } else {
            powsyblContingencyList = contingencyList.toPowsyblContingencyList(network);
        }
        return powsyblContingencyList == null ? Collections.emptyList() : powsyblContingencyList.getContingencies(network);
    }

    private Set<String> getDisconnectedElements(Contingency contingency, Network network) {
        return contingency.getElements().stream()
            .filter(contingencyElement -> {
                var connectable = network.getConnectable(contingencyElement.getId());
                return connectable != null && isDisconnected(connectable);
            })
            .map(ContingencyElement::getId)
            .collect(Collectors.toSet());
    }

    private boolean isDisconnected(Connectable<?> connectable) {
        List<? extends Terminal> terminals = connectable.getTerminals();
        // check if the connectable are connected with terminal.isConnected()
        boolean atleastOneIsConnected = false;
        for (Terminal terminal : terminals) {
            if (terminal != null && terminal.isConnected()) {
                atleastOneIsConnected = true;
                break;
            }
        }
        return !atleastOneIsConnected;
    }

    private List<IdentifiableAttributes> evaluateFiltersNetwork(FiltersWithEquipmentTypes filtersWithEquipmentTypes, Network network) {
        return filterEvaluator.evaluateFilters(filtersWithEquipmentTypes, network).equipmentIds();
    }
}
