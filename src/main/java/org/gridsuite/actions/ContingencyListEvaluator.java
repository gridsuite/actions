/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.actions;

import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.contingency.list.ContingencyList;
import com.powsybl.iidm.network.Connectable;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Terminal;
import org.gridsuite.actions.dto.evaluation.ContingencyInfos;
import org.gridsuite.actions.dto.contingency.FilterBasedContingencyList;
import org.gridsuite.actions.dto.contingency.PersistentContingencyList;
import org.gridsuite.actions.utils.ContingencyListType;
import org.gridsuite.actions.utils.ContingencyListUtils;
import org.gridsuite.filter.identifierlistfilter.IdentifiableAttributes;
import org.gridsuite.filter.utils.FilterServiceUtils;
import org.gridsuite.filter.utils.FiltersWithEquipmentTypes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kevin Le Saulnier <kevin.le-saulnier at rte-france.com>
 */
public class ContingencyListEvaluator {

    private final FilterProvider filterProvider;

    public ContingencyListEvaluator(FilterProvider filterProvider) {
        this.filterProvider = filterProvider;
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
        return FilterServiceUtils.evaluateFiltersWithEquipmentTypes(filtersWithEquipmentTypes, network, new DefaultFilterLoader(filterProvider)).equipmentIds();
    }
}
