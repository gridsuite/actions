package org.gridsuite.actions.internal.impl;

import com.powsybl.contingency.ContingencyElement;
import com.powsybl.contingency.list.IdentifierContingencyList;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.identifiers.IdBasedNetworkElementIdentifier;
import com.powsybl.iidm.network.identifiers.NetworkElementIdentifierContingencyList;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.gridsuite.actions.api.ContingencyListEvaluator;
import org.gridsuite.actions.api.dto.EquipmentTypesByFilter;
import org.gridsuite.actions.api.dto.FilterAttributes;
import org.gridsuite.actions.api.dto.contingency.FilterBasedContingencyList;
import org.gridsuite.actions.api.dto.contingency.IdBasedContingencyList;
import org.gridsuite.actions.api.dto.evaluation.ContingencyInfos;
import org.gridsuite.filter.api.FilterEvaluator;
import org.gridsuite.filter.api.dto.FiltersWithEquipmentTypes;
import org.gridsuite.filter.identifierlistfilter.FilteredIdentifiables;
import org.gridsuite.filter.identifierlistfilter.IdentifiableAttributes;
import org.gridsuite.filter.internal.impl.DefaultFilterEvaluator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gridsuite.filter.utils.EquipmentType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DefaultContingencyListEvaluatorTest {
    @Test
    void testEvaluateFilterBasedContingencyList() {
        // --- Set up --- //
        // filters used for filter-based contingency
        List<FilterAttributes> filtersAttributes = List.of(
            new FilterAttributes(UUID.randomUUID(), LINE, "Filter1"),
            new FilterAttributes(UUID.randomUUID(), SUBSTATION, "Filter2"),
            new FilterAttributes(UUID.randomUUID(), TWO_WINDINGS_TRANSFORMER, "Filter3")
        );
        List<EquipmentTypesByFilter> equipmentTypesByFilter = List.of(
            new EquipmentTypesByFilter(UUID.randomUUID(), Set.of(IdentifiableType.GENERATOR))
        );

        Network network = EurostagTutorialExample1Factory.createWithMoreGenerators();

        FilterEvaluator filterEvaluator = mock(FilterEvaluator.class);
        when(filterEvaluator.evaluateFilters(any(FiltersWithEquipmentTypes.class), eq(network)))
            .thenReturn(new FilteredIdentifiables(
                    List.of(
                            new IdentifiableAttributes("NHV1_NHV2_1", IdentifiableType.LINE, 0D),
                            new IdentifiableAttributes("GEN2", IdentifiableType.GENERATOR, 0D),
                            new IdentifiableAttributes("NGEN_NHV1", IdentifiableType.TWO_WINDINGS_TRANSFORMER, 0D),
                            new IdentifiableAttributes("TEST", IdentifiableType.TWO_WINDINGS_TRANSFORMER, 0D) // not in network -> should be ignored by evaluator
                    ),
                    List.of()
            ));

        FilterBasedContingencyList filterBasedContingencyList = new FilterBasedContingencyList(UUID.randomUUID(), Instant.now(), filtersAttributes, equipmentTypesByFilter);

        // --- Invoke evaluation to test --- //
        ContingencyListEvaluator contingencyListEvaluator = new DefaultContingencyListEvaluator(filterEvaluator);
        List<ContingencyInfos> contingencyInfos = contingencyListEvaluator.evaluateContingencyList(filterBasedContingencyList, network);

        // --- Check --- //
        verify(filterEvaluator, times(1)).evaluateFilters(any(FiltersWithEquipmentTypes.class), eq(network));

        assertEquals(3, contingencyInfos.size());
        assertThat(List.of("NHV1_NHV2_1", "GEN2", "NGEN_NHV1")).usingRecursiveComparison().isEqualTo(contingencyInfos.stream().map(ContingencyInfos::getId).toList());

    }

    @Test
    void testEvaluateIdBasedContingencyList() {
        // --- Set up --- //
        Network network = EurostagTutorialExample1Factory.createWithMoreGenerators();

        NetworkElementIdentifierContingencyList networkElementIdentifierContingencyList = new NetworkElementIdentifierContingencyList(List.of(
            new IdBasedNetworkElementIdentifier("NHV1_NHV2_1"),
            new IdBasedNetworkElementIdentifier("NHV1_NHV2_2"),
            new IdBasedNetworkElementIdentifier("TEST") // this element does not exist in the network and should be removed after evaluation
        ), "default");
        IdBasedContingencyList idBasedContingencyList = new IdBasedContingencyList(null, Instant.now(), new IdentifierContingencyList("defaultName", List.of(networkElementIdentifierContingencyList)));

        // --- Invoke evaluation to test --- //
        ContingencyListEvaluator contingencyListEvaluator = new DefaultContingencyListEvaluator(new DefaultFilterEvaluator(filtersUuids -> null));
        List<ContingencyInfos> contingencyInfos = contingencyListEvaluator.evaluateContingencyList(idBasedContingencyList, network);

        // --- Check --- //
        assertEquals(1, contingencyInfos.size());
        assertThat(List.of("NHV1_NHV2_1", "NHV1_NHV2_2")).usingRecursiveComparison().isEqualTo(contingencyInfos.stream().flatMap(c -> c.getContingency().getElements().stream().map(ContingencyElement::getId)).toList());
    }
}
